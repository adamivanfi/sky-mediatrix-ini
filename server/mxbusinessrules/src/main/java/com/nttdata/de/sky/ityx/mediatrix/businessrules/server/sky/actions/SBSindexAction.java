package com.nttdata.de.sky.ityx.mediatrix.businessrules.server.sky.actions;

import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.lib.utils.MitarbeiterlogWriter;
import com.nttdata.de.sky.ityx.common.Actions;
import com.nttdata.de.sky.ityx.common.DocIdGenerator;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import com.nttdata.de.sky.ityx.mediatrix.businessrules.server.sky.OutboundRule;
import com.nttdata.de.sky.ityx.mediatrix.businessrules.server.sky.SkyRule;
import de.ityx.base.Global;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.interfaces.document.CDocument;
import de.ityx.contex.interfaces.extag.TagMatch;
import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.data.Keyword;
import de.ityx.mediatrix.data.Question;
import de.ityx.mediatrix.data.metainformation.MetaInformationDocumentContainer;
import de.ityx.mediatrix.data.metainformation.MetaInformationInt;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * See @ReindexAction
 */
public class SBSindexAction extends AServerEventAction {

	private final SkyRule outboundRule = new OutboundRule();


	@Override
	public List actionPerformed(Connection con, String actionname, List parameters) throws Exception {
		// Logging.
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + " ";
		SkyLogger.getMediatrixLogger().debug(logPrefix + " enter: >" + actionname + "<");
		String params = "";
		for (Object o : parameters) {
			params += o + "/";
		}

		Question question = null;

		// Reads the parameters of the call.
		Integer questionId = (Integer) parameters.get(0);
		SkyLogger.getMediatrixLogger().info(logPrefix + "Starting SbsIndex:fid:" + questionId + " " + actionname + ": Parameter " + params);
		logPrefix += "qid: " + questionId + " ";
		String customerNumber = (String) parameters.get(1);
		Integer operatorId = (Integer) parameters.get(2);
		Boolean isInitial = (Boolean) parameters.get(3);
		String channel = (String) parameters.get(4);
		String doctype = (String) parameters.get(5);
		String direction = (String) parameters.get(6);
		Boolean isManual = (Boolean) parameters.get(7);
		parameters.clear();
		String docId = null;

		SkyLogger.getMediatrixLogger().info(logPrefix + "Starting SbsIndex:fid:" + questionId + " " + actionname + ": Parameter " + params);
		question = API.getServerAPI().getQuestionAPI().load(con, questionId, false);
		if (question != null) {
			String headers = question.getHeaders();
			if (docId == null)
				docId = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.X_TAGMATCH_DOCUMENT_ID);
			if (docId == null || docId.isEmpty()) {
				docId = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.DOCUMENT_ID);
			}
			if (docId == null || docId.isEmpty()) {
				docId = question.getDocId();
			}
			if (docId == null || docId.isEmpty()) {
				// missing docid? should never be executed
				docId = DocIdGenerator.createUniqueDocumentId(con, TagMatchDefinitions.DocumentDirection.INBOUND, TagMatchDefinitions.Channel.EMAIL, new java.util.Date());
				SkyLogger.getMediatrixLogger().warn(" SbsIndex:fid:" + questionId + " new DocID generated:" + docId + " SbsIndexAction with parameters: fid:" + questionId + " cid:" + customerNumber + " op:" + operatorId + " ch:" + channel + " formtype" + doctype);

				headers = TagMatchDefinitions.addOrReplaceXTMHeader(headers, TagMatchDefinitions.DOCUMENT_ID, docId);

				question.setDocId(docId);
				question.setHeaders(headers);
			}
			question.setExtra3(customerNumber);
			question.setExtra7("");
			question.setExtra9("");
			Map<String, String> newTags = new HashMap<>();

			newTags.put(TagMatchDefinitions.DOCUMENT_ID, docId);
			newTags.put(TagMatchDefinitions.CUSTOMER_ID, customerNumber);
			if(isManual) {
				newTags.put(TagMatchDefinitions.CUSTOMER_FIRST_NAME, TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.CUSTOMER_FIRST_NAME));
				newTags.put(TagMatchDefinitions.CUSTOMER_LAST_NAME, TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.CUSTOMER_LAST_NAME));
				newTags.put(TagMatchDefinitions.SBS_COMPANY, TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.SBS_COMPANY));
				newTags.put(TagMatchDefinitions.CUSTOMER_STREET, TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.CUSTOMER_STREET));
				newTags.put(TagMatchDefinitions.CUSTOMER_ZIP_CODE, TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.CUSTOMER_ZIP_CODE));
				newTags.put(TagMatchDefinitions.CUSTOMER_CITY, TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.CUSTOMER_CITY));
				newTags.put(TagMatchDefinitions.CUSTOMER_COUNTRY, TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.CUSTOMER_COUNTRY));
			}
			String rparams = "";
			for (String key : newTags.keySet()) {
				rparams += key + "\t" + newTags.get(key);
			}
			SkyLogger.getMediatrixLogger().debug(logPrefix + " SbsIndex:fid:" + questionId + " return values" + rparams);
			updateQuestionMetaData(con, question, operatorId, false, newTags, isInitial, false, "0", customerNumber, null);


			SkyLogger.getMediatrixLogger().debug(logPrefix + " SbsIndex:fid:" + questionId);
			Question newQuestion = API.getServerAPI().getQuestionAPI().load(con, questionId, true);
			parameters.add(newQuestion);
		}
		return parameters;
	}

	/**
	 * Updates the question with the new customer data.
	 *
	 * @param con
	 * @param questionId
	 * @param newTags
	 * @param isInitial
	 * @param sepa
	 * @throws SQLException
	 */
	private void updateQuestionMetaData(Connection con, Integer questionId, int operatorid, boolean isOperatorMode, Map<String, String> newTags, Boolean isInitial, boolean sepa, String oldCustomerId, String customerNumber, String mandateId) throws SQLException {
		Question question = API.getServerAPI().getQuestionAPI().load(con, questionId, true);
		updateQuestionMetaData(con, question, operatorid, isOperatorMode, newTags, isInitial, sepa, oldCustomerId, customerNumber, mandateId);
	}


	/**
	 * Updates the question with the new customer data.
	 *
	 * @param con
	 * @param newTags
	 * @param isInitial
	 * @param sepa
	 * @throws SQLException
	 */
	private void updateQuestionMetaData(Connection con, Question question, int operatorid, boolean isOperatorMode, Map<String, String> newTags, Boolean isInitial, boolean sepa, String oldCustomerId, String customerNumber, String mandateId) throws SQLException {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + " ";
		SkyLogger.getMediatrixLogger().debug(logPrefix + " updateQuestion: " + question.getId());

		// Loads question and document.

		List<MetaInformationInt> list = API.getServerAPI().getMetaInformationAPI().loadByEmailId(con, question.getEmailId());
		CDocumentContainer<CDocument> doc = null;
		Map<String, TagMatch> oldTags = new TreeMap<>();
		MetaInformationInt metaDoc = null;
		for (MetaInformationInt meta : list) {
			if (meta.getType().equals(MetaInformationDocumentContainer.TYPENAME)) {
				doc = (CDocumentContainer<CDocument>) meta.getContent();
				if (doc != null) {
					for (TagMatch tm : doc.getTags()) {
						String identifier = tm.getIdentifier();
						if(newTags.keySet().contains(identifier) || !TagMatchDefinitions.CUSTOMER_DATA.contains(identifier)) {
							oldTags.put(identifier, tm);
						}
					}
				}
				metaDoc = meta;
				break;
			}
		}
		if (doc != null) {
			doc.setTags(new ArrayList(oldTags.values()));
			API.getServerAPI().getMetaInformationAPI().store(con, metaDoc);
		}
		String headers = outboundRule.updateHeadersAndTags(newTags, question, oldTags);
		question.setHeaders(headers);
		if (!isInitial) {
			String action = sepa ? "SbsMandate" : "SbsIndex";
			// Marks question as reindexed.
			Keyword reindexKeyword = API.getServerAPI().getKeywordAPI().loadKeyword(con, "[ROOT, Sky]" + action, (question.getProjectId() > 0) ? question.getProjectId() : 120);
			List<Keyword> keywords = question.getKeywords();
			if (keywords == null){
				keywords=new LinkedList<>();
			}
			if (keywords.isEmpty() || !keywords.contains(reindexKeyword)) {
				keywords.add(reindexKeyword);
			}
			String msg = action + (sepa ? " custId:" + customerNumber + (mandateId != null ? " mandateRef:" + mandateId : "") : " from:" + oldCustomerId + " to:" + customerNumber);

			if (Global.getOperatorLogLength() < 20) {
				synchronized (outboundRule) {
					Global.initializeConfigurationForUnitIfNotDoneYet("DEFAULT");
					Global.setOperatorLogLength(2040);
				}
			}
			if (!MitarbeiterlogWriter.writeMitarbeiterlog( operatorid, question.getId(), -1, 9, msg, isOperatorMode)) {
				SkyLogger.getMediatrixLogger().error(logPrefix + "SBSINDEX: Could not add SbsIndex action to log:"+question.getId()+" d:"+question.getDocId());
			}
			// Associates to new customer and stores metadata.
			outboundRule.associateCustomer(con, question, true);
			question.setExtra3(customerNumber);
			SkyLogger.getCommonLogger().info("RA.QStore1a Generated docid:" + question.getDocId() + " frage:" + question.getId());
			API.getServerAPI().getQuestionAPI().store(con, question, true);
			SkyLogger.getCommonLogger().info("RA.QStore2b Generated docid:" + question.getDocId() + " frage:" + question.getId());

		} else {
			question.setExtra3(customerNumber);
			SkyLogger.getCommonLogger().info("RA.QStore1b Generated docid:" + question.getDocId() + " frage:" + question.getId());
			API.getServerAPI().getQuestionAPI().store(con, question);
			SkyLogger.getCommonLogger().info("RA.QStore2b Generated docid:" + question.getDocId() + " frage:" + question.getId());

		}

		if (question.getCaseId() < 1 || API.getServerAPI().getCaseAPI().load(con, question.getCaseId()).getCustomerId() < 1) {
			outboundRule.associateCustomer(con, question, true);
			if (question.getExtra3()!=null && !question.getExtra3().equals(customerNumber)){
				SkyLogger.getCommonLogger().warn("RA.QStore Setting CustomerID docid:" + question.getDocId() + " frage:" + question.getId());
				question.setExtra3(customerNumber);
			}
			SkyLogger.getCommonLogger().info("RA.QStore1c Generated docid:" + question.getDocId() + " frage:" + question.getId());
			API.getServerAPI().getQuestionAPI().store(con, question);
			SkyLogger.getCommonLogger().info("RA.QStore2c Generated docid:" + question.getDocId() + " frage:" + question.getId());
		}
	}


	@Override
	public String[] getActionNames() {
		return new String[]{Actions.ACTION_SBSINDEX.name()};
	}
}
