package com.nttdata.de.ityx.sharedservices.archiveMetadata;

import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.archive.AbstractArchiveMetaData;
import com.nttdata.de.sky.archive.ArchiveMetaDataFactory;
import com.nttdata.de.sky.ityx.common.Actions;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.data.Keyword;
import de.ityx.mediatrix.data.Question;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by meinusch on 20.07.15.
 */
public class ArchiveMetaDataUtils {
	public static String FORMTYPE_PREFIX = "[ROOT, FormType]";


	public static AbstractArchiveMetaData getArchiveMetaData(Connection con, Question question) {
		return ArchiveMetaDataUtils.getArchiveMetaData(getFormtype(con, question));

	}

	public static AbstractArchiveMetaData getArchiveMetaData(String formtype) {
		if (formtype != null && (TagMatchDefinitions.SEPA_MANDATE.equals(formtype))) {
			return ArchiveMetaDataFactory.getInstance("com.nttdata.de.sky.ityx.mediatrix.businessrules.archiving.MDocumentMandateArchiveMetaData");
		} else {
			return ArchiveMetaDataFactory.getInstance("com.nttdata.de.sky.ityx.mediatrix.businessrules.archiving.MDocumentArchiveMetaData");
		}
	}

	public static File createArchivingMetaFile(Map<String, String> metaMap, AbstractArchiveMetaData metaData, String path,boolean isSbsProject, int filesToArchive) throws Exception {
		String documentid = metaMap.get(TagMatchDefinitions.DOCUMENT_ID);
		String fileName = path + File.separator + documentid + "-0001.skyarc";
		//System.err.println("Writing archive file: " + fileName);
		SkyLogger.getMediatrixLogger().info(documentid + " executeArchiving create skyarc: " + fileName);
		return metaData.marshallArchiveF(metaData.buildMetaDataXML(metaMap, isSbsProject,filesToArchive), fileName);
	}

	/**
	 * Reads the formtype of the question from its keywords or headers.
	 *
	 * @param con
	 * @param question The question which contains the formtype
	 * @return The formtype of the question
	 */
	public static String getFormtype(Connection con, final Question question) {
		String formtype = TagMatchDefinitions.extractXTMHeader(question.getHeaders(), TagMatchDefinitions.X_TAGMATCH_FORM_TYPE);
		List<Keyword> keywords = null;
		if (formtype == null || formtype.isEmpty()) {
			keywords = question.getKeywords();
			formtype = ArchiveMetaDataUtils.getFormTypeKeyword(keywords);
		}
		if (formtype == null || formtype.isEmpty()) {
			keywords = ArchiveMetaDataUtils.loadKeywords(con, question);
			formtype = ArchiveMetaDataUtils.getFormTypeKeyword(keywords);
		}
		return formtype;
	}

	protected static String getFormTypeKeyword(List<Keyword> keywords) {
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

	protected static List<Keyword> loadKeywords(Connection con, final Question question) {
		List<Keyword> keywords=null;
		List<Object> parameter = new ArrayList<>();
		parameter.add((question.getProjectId() > 0) ? question.getProjectId() : 110);
		parameter.add(question.getId());
		if (API.isClient()) {
			List<Object> result = (List<Object>) API.getClientAPI().getConnectionAPI().sendServerEvent(Actions.ACTION_LOAD_KEYWORD.name(), parameter);
			if (result!=null && !result.isEmpty()) {
				keywords = (List<Keyword>) result.get(0);
			}
			SkyLogger.getClientLogger().info("keywords: " + keywords);
		} else {
			SkyLogger.getClientLogger().info("Not able to load Formtype-Keyword on Serverside for: " + question.getDocId() + " f:" + question.getId());
		}
		return keywords;
	}
}

