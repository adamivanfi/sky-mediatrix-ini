package com.nttdata.de.sky.ityx.mediatrix.businessrules.server.sky.actions;

import com.nttdata.de.ityx.sharedservices.services.ReindexService;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.lib.utils.MitarbeiterlogWriter;
import com.nttdata.de.sky.ityx.common.Actions;
import com.nttdata.de.sky.ityx.common.DocIdGenerator;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import com.nttdata.de.sky.ityx.mediatrix.businessrules.server.parameter.UpdateContactSRParameter;
import com.nttdata.de.sky.ityx.mediatrix.businessrules.server.parameter.UpdateMandateSRParameter;
import com.nttdata.de.sky.ityx.mediatrix.businessrules.server.sky.OutboundRule;
import com.nttdata.de.sky.ityx.mediatrix.businessrules.server.sky.SkyRule;
import de.ityx.base.Global;
import de.ityx.contex.data.match.Request;
import de.ityx.contex.data.match.Response;
import de.ityx.contex.data.match.Row;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.interfaces.document.CDocument;
import de.ityx.contex.interfaces.extag.TagMatch;
import de.ityx.contex.interfaces.match.IFuzzyMatcherEngine;
import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.data.Keyword;
import de.ityx.mediatrix.data.Question;
import de.ityx.mediatrix.data.metainformation.MetaInformationDocumentContainer;
import de.ityx.mediatrix.data.metainformation.MetaInformationInt;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by meinusch on 13.04.15.
 */
public class ReindexAction extends AServerEventAction {

	private final SkyRule outboundRule = new OutboundRule();


	@Override
	public List actionPerformed(Connection con, String actionname, List parameters) throws Exception {
		// Logging.
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + " ";

		String params = "";
		for (Object o : parameters) {
			params += o + "/";
		}
		// Reads the parameters of the call.
		Integer questionId = (Integer) parameters.get(0);
		SkyLogger.getMediatrixLogger().info(logPrefix + "Starting Reindex:fid:" + questionId + " " + actionname + ": Parameter " + params);
		logPrefix += "qid: " + questionId + " ";
		String customerNumber = (String) parameters.get(1);
		String contractNumber = (String) parameters.get(2);
		Integer operatorId = (Integer) parameters.get(3);
		Boolean isOperatorMode = (Boolean) parameters.get(4);
		Boolean isInitial = (Boolean) parameters.get(5);
		String channel = (String) parameters.get(6);
		String doctype = (String) parameters.get(7);
		String direction = (String) parameters.get(8);
		String docId = null;
		String mandateId = null;
		String signatureDate = null;
		String signatureFlag = null;
		boolean sepa = false;
		if (doctype.equals(TagMatchDefinitions.SEPA_MANDATE) && parameters.size() >= 11) {
			SkyLogger.getMediatrixLogger().debug(logPrefix + " Reindex:fid:" + questionId + " Reading SEPA parameters");
			Object object = parameters.get(9);
			if (object != null && object.getClass().equals(String.class)) {
				mandateId = (String) object;
				object = parameters.get(10);
				if (object != null && object.getClass().equals(String.class)) {
					String signatureDateParameter = (String) object;
					try {
						Date date = new SimpleDateFormat("dd.MM.yyyy").parse(signatureDateParameter);
						if (date != null) {
							signatureDate = signatureDateParameter;
						} else {
							signatureDate = new SimpleDateFormat("dd.MM.yyyy").format(new Date());
						}
					} catch (Exception e) {
						SkyLogger.getMediatrixLogger().error(logPrefix + "Reindex: " + questionId + " " + e.getMessage());
						signatureDate = new SimpleDateFormat("dd.MM.yyyy").format(new Date());
					}
					object = parameters.get(11);
					if (object != null && object.getClass().equals(Boolean.class)) {
						Boolean signatureFlagParameter = (Boolean) object;
						signatureFlag = signatureFlagParameter ? "1" : "0";
						sepa = true;
					}
				}
			}
		}
		String activityId = null;
		if (direction != null && direction.equals("OUTBOUND") && (doctype == null || !doctype.equals(TagMatchDefinitions.SEPA_MANDATE))) {
			if (parameters.size() >= 10) {
				docId = (String) parameters.get(9);

			} else {
				SkyLogger.getMediatrixLogger().warn(logPrefix + " Reindex:fid:" + questionId + " Problem getting DocID for Outbound:" + params);
			}
			if (parameters.size() >= 11) {
				activityId = (String) parameters.get(10);

			} else {
				SkyLogger.getMediatrixLogger().warn(logPrefix + " Reindex:fid:" + questionId + " Problem getting ActivityID for Outbound:" + params);
			}
		}
		SkyLogger.getMediatrixLogger().info(logPrefix + "Starting Reindex:fid:" + questionId + " " + actionname + ": Parameter " + params);

		parameters.clear();
		boolean doIndex = false;
		SkyLogger.getMediatrixLogger().info(logPrefix + " Reindex:fid:" + questionId + " Parameter loaded: cust:" + customerNumber + " isInitial:" + isInitial);

		if (isInitial || customerNumber.equals("0")) {
			doIndex = true;
		} else {
			// Checks if customer and (optional) contract exist in the fuzzy
			// index.
			Request request = new Request();
			if (customerNumber.length() > 0) {
				request.addColumn("CUSTOMER_ID", customerNumber, true, 0, IFuzzyMatcherEngine.CANDIDATES_EXACT);
			}
			if (contractNumber != null && contractNumber.length() > 0) {
				request.addColumn("CONTRACT_ID", contractNumber, true, 0, IFuzzyMatcherEngine.CANDIDATES_EXACT);
			}
			//if (sepa && mandateId != null && mandateId.length() > 0) {
			//	request.addColumn("MANDATE_NUM", mandateId, true, 0, IFuzzyMatcherEngine.CANDIDATES_EXACT);
			//}
			List<Object> parameter = new ArrayList<>();
			parameter.add(request);
			try {
				(new FuzzySearchAction()).actionPerformed(con, Actions.ACTION_FUZZY_SEARCH.name(), parameter);
				final Object result = parameter.get(0);
				if (result instanceof Response && ((Response) result).getRowCount() > 0) {
					doIndex = true;
					final Row response = ((Response) result).getRow(0);
					customerNumber = response.getField(2);
					contractNumber = response.getField(3);
				}
			} catch (Exception e) {
				SkyLogger.getMediatrixLogger().error(logPrefix + " Reindex:fid:" + questionId + " FuzzySearch error:" + e.getMessage(), e);
			}
		}


		if (doIndex) { // Updates Siebel contact with new customer and contract.
			parameters.add(doIndex);
			Boolean success = false;
			Question question = null;
			try {
				// Gets parameters.
				SkyLogger.getMediatrixLogger().info("Starting Reindex:fid:" + questionId);
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
						// für Emails die ohne Contex ins Mediatrix reingegangen sind und ins Default gelandet sind, ohne dass Tagmatches gefülht wurden
						docId = DocIdGenerator.createUniqueDocumentId(con, TagMatchDefinitions.DocumentDirection.INBOUND, TagMatchDefinitions.Channel.EMAIL, new java.util.Date());
						SkyLogger.getMediatrixLogger().info(" Reindex:fid:" + questionId + " new DocID generated:" + docId + " SReindexAction with parameters: fid:" + questionId + " cid:" + customerNumber + " vid:" + contractNumber + " op:" + operatorId + " ch:" + channel + " formtype" + doctype + " mandate:" + mandateId + " sDate:" + signatureDate + " sFlaq:" + signatureFlag);

						headers = TagMatchDefinitions.addOrReplaceXTMHeader(headers, TagMatchDefinitions.DOCUMENT_ID, docId);

						question.setDocId(docId);
						question.setHeaders(headers);
						//throw new IllegalArgumentException("Reindex fid:"+questionId+" is not possible for documents without DocumentID");
					}
					String oldContactid = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.getHeaderTagName(TagMatchDefinitions.CONTACT_ID));
					if (oldContactid == null) {
						oldContactid = "";
					}
					String oldCustomerId = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.getHeaderTagName(TagMatchDefinitions.CUSTOMER_ID));
					if (oldCustomerId == null) {
						oldCustomerId = "0";
					}
					SkyLogger.getMediatrixLogger().debug(" Reindex:fid:" + questionId + " START docid:" + docId + " custid:" + customerNumber + " contractid:" + contractNumber + " op:" + operatorId + " ch:" + channel + " formtype:" + doctype + " oldCustomer:" + oldCustomerId + " oldContactid:" + oldContactid + " mandate:" + mandateId + " sDate:" + signatureDate + " sFlaq:" + signatureFlag);

					question.setExtra3(customerNumber);
					Map<String, String> newTags;

					if (activityId != null && !activityId.isEmpty()) {
						SkyLogger.getMediatrixLogger().info(logPrefix + " AssociateToActivity:fid:" + questionId + " direction:" + direction + " activity:" + activityId + " channel:" + channel + " direction:" + direction);
						ReindexService rs = new ReindexService();
						newTags = rs.associateFrage(docId, customerNumber, contractNumber, mandateId, oldContactid, activityId, channel, direction);
					} else {
						newTags = outboundRule.updateSiebelSR(logPrefix, new UpdateContactSRParameter(new Integer(question.getId()), customerNumber, contractNumber, channel, doctype, direction, docId, oldContactid, isInitial), new UpdateMandateSRParameter(mandateId, signatureDate, signatureFlag, sepa));
						if (newTags.size() <= 1) {
							SkyLogger.getCommonLogger().info("RA.QStore1 Generated docid:" + question.getDocId() + " frage:" + question.getId());
							API.getServerAPI().getQuestionAPI().store(con, question);
							SkyLogger.getCommonLogger().info("RA.QStore2 Generated docid:" + question.getDocId() + " frage:" + question.getId());

							SkyLogger.getMediatrixLogger().error(logPrefix + " Reindex:fid:" + questionId + " Problem occured with parameters: customerNumber=<" + customerNumber + ">, contractNumber=<" + contractNumber + ">, docId=<" + docId + ">, contactid=<" + oldContactid + ">");
							throw new Exception("Reindex:fid:" + questionId + " Problem occured with parameters: customerNumber=<" + customerNumber + ">, contractNumber=<" + contractNumber + ">, docId=<" + docId + ">, contactid=<" + oldContactid + ">");
						}
					}
					SkyLogger.getMediatrixLogger().info(" ReindexComplete:fid:" + questionId + " START docid:" + docId + " custid:" + customerNumber + " contractid:" + contractNumber + " op:" + operatorId + " ch:" + channel + " formtype:" + doctype + " oldCustomer:" + oldCustomerId + " oldContactid:" + oldContactid + " mandate:" + mandateId + " sDate:" + signatureDate + " sFlaq:" + signatureFlag);

					// contex process returns at least one value

					if (sepa && mandateId != null && mandateId.length() > 0) {
						newTags.put(TagMatchDefinitions.SEPA_MANDATE_NUMBER, mandateId);
						newTags.put(TagMatchDefinitions.SEPA_SIGNATURE_FLAG, signatureFlag);
						newTags.put(TagMatchDefinitions.SEPA_SIGNATURE_DATE, signatureDate);
					}
					//newTags.put(TagMatchDefinitions.X_TAGMATCH_DOCUMENT_ID, docId);
					newTags.put(TagMatchDefinitions.DOCUMENT_ID, docId);
					String rparams = "";
					for (String key : newTags.keySet()) {
						rparams += key + "\t" + newTags.get(key);
					}
					SkyLogger.getMediatrixLogger().debug(logPrefix + " Reindex:fid:" + questionId + " return values" + rparams);
					updateQuestionMetaData(con, question, operatorId, isOperatorMode, newTags, isInitial, sepa, oldCustomerId, customerNumber, mandateId);
					success = true;
				}else {
					success = false;
					SkyLogger.getMediatrixLogger().error(logPrefix + " Reindex:fid:" + questionId + " error = QUESTION:"+ questionId+"can not be loaded");
				}
			} catch (Exception e) {
				SkyLogger.getMediatrixLogger().error(logPrefix + " Reindex:fid:" + questionId + " error = " + e.getMessage(), e);
				success = false;
			}
			parameters.add(success);
			SkyLogger.getMediatrixLogger().debug(logPrefix + " Reindex:fid:" + questionId + ": success = " + success);
			if (success && question != null) {
				try {
					Question newQuestion = API.getServerAPI().getQuestionAPI().load(con, questionId, true);
					parameters.add(newQuestion);
				} catch (Exception e) {
					SkyLogger.getMediatrixLogger().error(logPrefix + " Reindex:fid:" + questionId + " error = " + e.getMessage(), e);

				}
			} else {
				SkyLogger.getMediatrixLogger().error(logPrefix + " Reindex:fid:" + questionId + " not sucessfull s:" + success + "  fn:" + (question != null));
			}
		} else {
			SkyLogger.getMediatrixLogger().warn(logPrefix + " NoReindexPossible:fid:" + questionId + " direction:" + direction + " activity:" + activityId + " channel:" + channel + " direction:" + direction);
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
						oldTags.put(tm.getIdentifier(), tm);
					}
				}
				metaDoc = meta;
				break;
			}
		}

		String headers = outboundRule.updateHeadersAndTags(newTags, question, oldTags);
		question.setHeaders(headers);
		if (!isInitial) {
			String action = sepa ? "Mandate" : "Reindex";
			// Marks question as reindexed.
			Keyword reindexKeyword = API.getServerAPI().getKeywordAPI().loadKeyword(con, "[ROOT, Sky]" + action, (question.getProjectId() > 0) ? question.getProjectId() : 110);
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
				SkyLogger.getMediatrixLogger().error(logPrefix + "REINDEX: Could not add Reindex action to log:"+question.getId()+" d:"+question.getDocId());
			}
			// Associates to new customer and stores metadata.
			outboundRule.associateCustomer(con, question, true);
			question.setExtra3(customerNumber);
			SkyLogger.getCommonLogger().info("RA.QStore1a Generated docid:" + question.getDocId() + " frage:" + question.getId());
			API.getServerAPI().getQuestionAPI().store(con, question);
			SkyLogger.getCommonLogger().info("RA.QStore2a Generated docid:" + question.getDocId() + " frage:" + question.getId());

		} else {
			question.setExtra3(customerNumber);
			SkyLogger.getCommonLogger().info("RA.QStore1b Generated docid:" + question.getDocId() + " frage:" + question.getId());
			API.getServerAPI().getQuestionAPI().store(con, question);
			SkyLogger.getCommonLogger().info("RA.QStore1b Generated docid:" + question.getDocId() + " frage:" + question.getId());

		}

		if (question.getCaseId() < 1 || API.getServerAPI().getCaseAPI().load(con, question.getCaseId()).getCustomerId() < 1) {
			outboundRule.associateCustomer(con, question, true);
			question.setExtra3(customerNumber);
			SkyLogger.getCommonLogger().info("RA.QStore1c Generated docid:" + question.getDocId() + " frage:" + question.getId());
			API.getServerAPI().getQuestionAPI().store(con, question);
			SkyLogger.getCommonLogger().info("RA.QStore2c Generated docid:" + question.getDocId() + " frage:" + question.getId());

		}

		if (doc != null) {
			doc.setTags(new ArrayList(oldTags.values()));
			API.getServerAPI().getMetaInformationAPI().store(con, metaDoc);
		}
	}


	@Override
	public String[] getActionNames() {
		return new String[]{Actions.ACTION_REINDEX.name()};
	}
}
