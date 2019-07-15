/**
 * 
 */
package com.nttdata.de.sky.ityx.mediatrix.businessrules;

import com.nttdata.de.sky.ityx.common.Actions;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.interfaces.document.CDocument;
import de.ityx.contex.interfaces.extag.TagMatch;
import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.data.Keyword;
import de.ityx.mediatrix.data.Question;
import de.ityx.mediatrix.data.metainformation.MetaInformationDocumentContainer;
import de.ityx.mediatrix.data.metainformation.MetaInformationInt;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.util.*;

/**
 * This class serves as superclass of business rules that use the same utility
 * methods.
 * 
 * @author DHIFLM
 * 
 */
public abstract class BusinessRule {

	public static String		FORMTYPE_PREFIX	= "[ROOT, FormType]";
	public static final String	AT				= "ÖSTERREICH";
	public static final String	DE				= "DEUTSCHLAND";


	protected abstract Logger getLogger();

	/**
	 * Reads the formtype of the question from its keywords or headers.
	 * 
	 * @param con
	 * 
	 * @param question
	 *            The question which contains the formtype
	 * @return The formtype of the question
	 */
	public String getFormtype(Connection con, final Question question) {
		String formtype = TagMatchDefinitions.extractXTMHeader(question.getHeaders(), TagMatchDefinitions.X_TAGMATCH_FORM_TYPE);
		List<Keyword> keywords = null;
                if (formtype == null || formtype.isEmpty()) {
                    keywords = question.getKeywords();
                    formtype = getFormTypeKeyword(keywords);
		}
                if (formtype == null || formtype.isEmpty()) {
			keywords = loadKeywords(con, question);
			formtype = getFormTypeKeyword(keywords);
		}
                return formtype;
	}

	protected String getFormTypeKeyword(List<Keyword> keywords) {
		String formtype = null;
		if (keywords != null) {
			for (Keyword keyword : keywords) {
				String key = keyword.getName();
				if (key.startsWith(FORMTYPE_PREFIX)) {
					formtype = key.substring(FORMTYPE_PREFIX.length(), key.length());
					break;
				}
			}
		}
		return formtype;
	}

	/**
	 * Client implementation
	 * 
	 * @param con
	 *            not used in client implementation
	 * @param question
	 * @return
	 */
	protected List<Keyword> loadKeywords(Connection con, final Question question) {
		List<Keyword> keywords;
		List<Object> parameter = new ArrayList<Object>();
		parameter.add((question.getProjectId()>0)?question.getProjectId():110);
		parameter.add(question.getId());

		List<Object> result = (List<Object>) API.getClientAPI().getConnectionAPI().sendServerEvent(Actions.ACTION_LOAD_KEYWORD.name(), parameter);
		if (result!=null && !result.isEmpty()) {
			keywords = (List<Keyword>) result.get(0);
		}else{
			return null;
		}
		getLogger().info("keywords: " + keywords);
		return keywords;
	}

	protected void updateTagmatches(Question question, CDocument doc, final MetaInformationInt metaDoc, CDocumentContainer<CDocument> cont, List<TagMatch> tags, List<TagMatch> tagList) {
		if (tagList.size() > 0) {
			// System.err.println(logPrefix+
			// ": Moving Tags from Document to Container");
			getLogger().debug(": Moving Tags from Document to Container");
			Map<String, TagMatch> page0TagMap = new HashMap<String, TagMatch>();
			for (TagMatch tm : tags) {
				page0TagMap.put(tm.getIdentifier(), tm);
			}
			for (TagMatch tm : tagList) {
				final String identifier = tm.getIdentifier();
				if (page0TagMap.containsKey(identifier)) {
					page0TagMap.get(identifier).setTagValue(tm.getTagValue());
				} else {
					tags.add(tm);
				}
			}
			doc.setTags(new ArrayList<TagMatch>());
			cont.setTags(tags);
			metaDoc.setContent(cont);
			question.setMetaInformation(metaDoc);
		}
	}



	protected  String findTagMatchValueByName(Question question, String tagMatchName){
        String result = null;
		String logpreafix = new Object() {
		}.getClass().getEnclosingMethod().getName() + " qid:" + question.getId() + " ";
		List<TagMatch> tagMatchResultList = new ArrayList<TagMatch>();
        List<TagMatch> tagMatchList = new ArrayList<TagMatch>();
		try {
			final MetaInformationInt metaDoc = question.getMetaInformation(MetaInformationDocumentContainer.TYPENAME);
			if (metaDoc != null) {
				CDocumentContainer<CDocument> cont = (CDocumentContainer<CDocument>) metaDoc.getContent();
				CDocument doc = cont.getDocument(0);
				//List<TagMatch> tags = cont.getPage0Tags();
                List<TagMatch> tags = cont.getTags();
				for (TagMatch tagMatch : tags) {
                    tagMatchList.clear();
					tagMatchList = findAllTagMatchByName(tagMatchName, tagMatch, 1);
                    if (tagMatchList != null && !tagMatchList.isEmpty()) {
                        tagMatchResultList.addAll(tagMatchList);
                    }
				}
			} else {
				getLogger().debug(logpreafix + " metaDoc is null");
			}
		} catch (Exception e){
			getLogger().error(logpreafix +"Unable to read Document!!! errMsg:"+e.getMessage(),e);
		}
		for(TagMatch tagMatch: tagMatchResultList){
			getLogger().debug(logpreafix + "Identifier: " + tagMatch.getIdentifier() + "; Value: " + tagMatch.getTagValue() + ";");
            if (tagMatch.getTagValue()!= null && !tagMatch.getTagValue().isEmpty()){
                result = tagMatch.getTagValue();
            }
		}
		return  result;
	}


	protected List<TagMatch> findAllTagMatchByName(String ident, TagMatch tagMatch, int level) {
        List<TagMatch>  result = new ArrayList<TagMatch>();
		getLogger().debug("findAllTagMatchByName -> level: "+level+"; Identifier: " + tagMatch.getIdentifier() + "; Value: " + tagMatch.getTagValue() + ";");
		if(tagMatch.getIdentifier()!=null && tagMatch.getIdentifier().trim().equalsIgnoreCase(ident)) {
			result.add(tagMatch);
		}

		if(tagMatch.getChildren() == null || tagMatch.getChildren().size() == 0) {
			return result;
		} else {
			Iterator tagMatchIter = tagMatch.getChildren().values().iterator();

			while(tagMatchIter.hasNext()) {
				TagMatch m = (TagMatch)tagMatchIter.next();
				result.addAll(findAllTagMatchByName(ident, m, level++));
			}

			return result;
		}
	}

}
