package com.nttdata.de.sky.ityx.mediatrix.businessrules.server.sky;

import com.nttdata.de.ityx.sharedservices.archiveMetadata.ArchiveMetaDataUtils;
import com.nttdata.de.ityx.sharedservices.services.ReindexService;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.lib.utils.MitarbeiterlogWriter;
import com.nttdata.de.sky.archive.AbstractArchiveMetaData;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import com.nttdata.de.sky.ityx.mediatrix.businessrules.BusinessRule;
import com.nttdata.de.sky.ityx.mediatrix.businessrules.server.BRProperties;
import com.nttdata.de.sky.ityx.mediatrix.businessrules.server.parameter.UpdateContactSRParameter;
import com.nttdata.de.sky.ityx.mediatrix.businessrules.server.parameter.UpdateMandateSRParameter;
import com.nttdata.de.sky.outbound.MxOutboundIntegration;
import de.ityx.contex.interfaces.designer.ProcessResult;
import de.ityx.contex.interfaces.extag.TagMatch;
import de.ityx.contex.service.designer.DesignerServiceClient;
import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.api.IServerAPI;
import de.ityx.mediatrix.api.server.ISCase;
import de.ityx.mediatrix.api.server.ISCustomer;
import de.ityx.mediatrix.data.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import org.apache.log4j.Logger;

//import de.ityx.rmicluster.ClusterAccess;

public abstract class SkyRule extends BusinessRule {

	public static final int SKY_PROJECT_ID = 110;
	public static final int SBS_PROJECT_ID = 120;
	public static final String AUTOARCHIVE_INITIALIZED="initialized";
	
	private static final String INSERT_INTO_MX_REPORTING_VALUES = "insert into MX_REPORTING (frageid, created, documentid, customerid, contractid, channel,contactid) values (?,SYSDATE,?,?,?,?,?)";
	public List<String> internal_mail = Collections.emptyList();
	protected Long fuzzy_master_id = 1L;
	protected String fuzzy_namespace = System.getProperty("contex.fuzzy.namespace", "fuzzy_newdb");
	protected String masterName = "sky";

	public SkyRule() {
		super();
		final BRProperties brProps = BRProperties.getInstance();
		if (brProps != null) {
			final String internalMail = brProps.getProperty("sky.internal");
			if (internalMail != null) {
				internal_mail = Arrays.asList(internalMail.replaceAll("\\s", "").split(","));
			} else {
				System.err.println("No sky.internal set in properties.");
			}
		} else {
			System.err.println("Could not load br.properties.");
		}
	}

	protected Logger getLogger(){
        return SkyLogger.getMediatrixLogger();
    }


	/**
	 * @return The id of the Contex master that contains the fuzzy matcher
	 * engine
	 */
	public Long getFuzzyMasterId() {
		return fuzzy_master_id;
	}

	/**
	 * @return The name of the default namespace.
	 */
	public String getFuzzyNamespace() {
		return fuzzy_namespace;
	}

	/**
	 * @param master_id The id of the Contex master that contains the fuzzy matcher
	 *                  engine
	 */
	public void setFuzzyMasterId(Long master_id) {
		fuzzy_master_id = master_id;
	}

	/**
	 * @param namespace The name of the default namespace.
	 */
	public void setFuzzyNamespace(String namespace) {
		fuzzy_namespace = namespace;
	}

	// Writes reporting data.
	// MX_REPORTING (
	// FRAGEID number (15),
	// CREATED TIMESTAMP,
	// DOCUMENTID number (15),
	// CUSTOMERID varchar2(15),
	// CONTRACTID varchar2(30),
	// CHANNEL varchar2(255),
	// CONTACTID varchar2(15));


	public void writeReportingEntry(Connection con, int questionId, String documentid, String customerNo, String contractid, String channel, String contactid) throws SQLException {
		long startTime = System.currentTimeMillis();
		if (questionId > 0) {
			//IConnectionPool cpool = DBConnectionPoolFactory.getPool();
			//Connection con = null;
			//Führt zur Deadlock bei externen Weiterleitung
			try {
				//con = cpool.getCon();

				if (con == null) {
					SkyLogger.getMediatrixLogger().error("Not possible to write MX_ReportingEntry - EmptyConnection:" + questionId);
					return;
				}
				PreparedStatement pst = con.prepareStatement(INSERT_INTO_MX_REPORTING_VALUES);
				pst.setInt(1, questionId);
				pst.setString(2, documentid);
				pst.setString(3, customerNo!=null?customerNo.trim():customerNo);
				pst.setString(4, contractid!=null?contractid.trim():contractid);
				pst.setString(5, channel);
				pst.setString(6, contactid!=null?contactid.trim():contactid);
				SkyLogger.getMediatrixLogger().debug("Start Execution of writeReportingEntry: " + questionId + " d:" + documentid +
						" cust:" + customerNo + " contract:" + contractid + " chann:" + channel + " contactid:" + contactid);
				/*if (SkyLogger.getMediatrixLogger().isDebugEnabled()) {
					String stacktrace = "";
					for (StackTraceElement e : Thread.currentThread().getStackTrace()) {
						stacktrace += " => " + e.getClassName() + ">" + e.getMethodName() + ":" + e.getLineNumber() + "\r\n";
					}
					SkyLogger.getMediatrixLogger().debug("Start Execution of writeReportingEntry:" + questionId + " threadid:" + Thread.currentThread().getId() + " stacktrace:" + stacktrace);
				}*/
				pst.execute();
				SkyLogger.getMediatrixLogger().debug("Finished Execution of writeReportingEntry: " + questionId + " took " + (System.currentTimeMillis() - startTime));
			}catch (SQLException e){
				SkyLogger.getMediatrixLogger().error("SQL-Problem during Execution of writeReportingEntry: " + questionId + " took " + (System.currentTimeMillis() - startTime)+e.getMessage(),e);
			}

			  /*} finally {
				if (con != null) {
					try {
						if (!con.getAutoCommit()) {
							con.commit();
						}
						SkyLogger.getMediatrixLogger().info("ExecutedReportingEntry: " + questionId);
					} catch (SQLException e) {
						SkyLogger.getMediatrixLogger().warn(" Unable to commit connection during writing mxlogentry for Question:" + questionId + e.getMessage(), e);
					}
					cpool.releaseCon(con);
				}
			}*/
		}else{
			SkyLogger.getMediatrixLogger().warn("Skipped mx_reporting entry for questionid: " + questionId + " and docid:"+documentid);
		}
	}

	/**
	 * @param con
	 * @param question
	 * @param noMail   TODO
	 */
	public void associateCustomer(Connection con, Question question, boolean noMail) {
		String logPrefix = new Object() {
		}.getClass().getEnclosingMethod().getName() + " (" + Thread.currentThread().getId() + ") q:" + question.getId() + " ";

		try {
			IServerAPI serverAPI = API.getServerAPI();
			ISCustomer customerAPI = serverAPI.getCustomerAPI();
			ISCase caseAPI = serverAPI.getCaseAPI();

			String questionExtId = TagMatchDefinitions.extractXTMHeader(question.getHeaders(), TagMatchDefinitions.CUSTOMER_ID);

			// Loads current customer associated to case.
			final Case questionCase = caseAPI.load(con, question.getCaseId());
			Customer custObj = null;
			String custObjExternalId = null;
			String custObjEmail = null;
			if (questionCase != null && questionCase.getCustomerId() > 0) {
				custObj = customerAPI.load(con, questionCase.getCustomerId());
				custObjExternalId = custObj.getExternalId();
				custObjEmail = custObj.getEmail();
				SkyLogger.getMediatrixLogger().debug(logPrefix + "check: c.id: " + custObj.getId() + ": c.extid:" + custObjExternalId + ": q.extid:" + questionExtId + " c.email:" + custObjEmail + " e.email:" + question.getFrom());
			}


			boolean freshCustomer = false;
			if (custObjExternalId == null || custObjExternalId.isEmpty() || !custObjExternalId.equals(questionExtId) || custObjExternalId.equals("0") || custObjExternalId.equals("-1")) {
				Customer newCustomer = null;
				try {
					newCustomer = customerAPI.loadExtID(con, questionExtId);
				} catch (Exception e) {
					if (custObj != null) {
						SkyLogger.getMediatrixLogger().warn(logPrefix + "customerLoad: c.id: " + custObj.getId() + ": c.extid:" + custObj.getExternalId() + ": q.extid:" + questionExtId + " msg:" + e.getMessage(), e);
					} else {
						SkyLogger.getMediatrixLogger().warn(logPrefix + "customerLoad: " + ": q.extid:" + questionExtId + " msg:" + e.getMessage(), e);
					}
				}
				// Creates new customer.
				if (newCustomer == null) {
					freshCustomer = true;
					
					custObj = new Customer(getProjId(question));
					//custObj.setId(ServerSequencer.GetId("kunde"));
					custObj.setExternalId(questionExtId);
					SkyLogger.getMediatrixLogger().debug(logPrefix + "newCust: c.id: " + custObj.getId() + ": c.extid:" + custObj.getExternalId() + ": q.extid:" + questionExtId);
				} else {
					custObj = newCustomer;
					SkyLogger.getMediatrixLogger().debug(logPrefix + "existingCust: c.id: " + custObj.getId() + ": c.extid:" + custObj.getExternalId() + ": q.extid:" + questionExtId);
				}
				if (noMail || (custObjEmail != null && (internal_mail.contains(custObjEmail.toLowerCase()) || custObjEmail.contains("@sky.") || custObjEmail.contains("noreply@") || custObjEmail.contains("no-reply@")))) {
					custObj.setEmail(null);
				} else {
					custObj.setEmail(custObjEmail);
				}
				// Associates case to correct customer if exists.
				//customer = newCustomer;

			}

			// Sets actual projectid. (Needed for optimized loading)
			custObj.setProjectId(getProjId(question));

			// Updates customermetadata with siebel values.
			custObj.setFirstname(TagMatchDefinitions.extractXTMHeader(question.getHeaders(), TagMatchDefinitions.getHeaderTagName(TagMatchDefinitions.CUSTOMER_FIRST_NAME)));
			custObj.setName(TagMatchDefinitions.extractXTMHeader(question.getHeaders(), TagMatchDefinitions.getHeaderTagName(TagMatchDefinitions.CUSTOMER_LAST_NAME)));

			question.setExtra3(questionExtId);

			if (	   !question.getStatus().equals(Question.S_CLASSIFICATION)
					&& !question.getStatus().equals(Question.S_CLASSIFICATION_REQUEUE)
					&& !question.getStatus().equals(Question.S_CLASSIFICATION_FORWARD)
					&& !question.getStatus().equals(Question.S_CLASSIFIED)
					&& !question.getStatus().equals(Question.S_EXTRACTION)
					&& !question.getStatus().equals(Question.S_EXTRACTION_REQUEUE)
					&& !question.getStatus().equals(Question.S_EXTRACTION_FORWARD)
					&& !question.getStatus().equals(Question.S_EXTRACTED)
					&& !question.getStatus().equals(Question.S_HOLD)
					&& !question.getStatus().equals(Question.S_HOLD_EXTRACTION)
					&& !question.getStatus().equals(Question.S_HOLD_CLASSIFICATION)
					&& !question.getStatus().equals(Question.S_HOLD_EXTRACTION_REQUEUE)
					&& !question.getStatus().equals(Question.S_HOLD_CLASSIFICATION_REQUEUE)
					) {
				writeReportingEntry(con, question.getId(), question.getDocId(), questionExtId, TagMatchDefinitions.extractXTMHeader(question.getHeaders(), TagMatchDefinitions.TAGMATCH_CONTRACT_NUMBER), TagMatchDefinitions.extractXTMHeader(question.getHeaders(), TagMatchDefinitions.getHeaderTagName(TagMatchDefinitions.CHANNEL)), TagMatchDefinitions.extractXTMHeader(question.getHeaders(), TagMatchDefinitions.CONTACT_ID));

			}

			/*
			if (question.getKeywords() == null) {
				SkyLogger.getMediatrixLogger().debug(logPrefix + "XXX:Keywords:" + question.getId() + ":areEmpty");
				//Geht nicht: NPE!
				question.setKeywords(new LinkedList<Keyword>());
			} else {

				if (SkyLogger.getMediatrixLogger().isDebugEnabled()) {
					for (Keyword keyword : question.getKeywords()) {
						SkyLogger.getMediatrixLogger().debug(logPrefix + "XXX:Keywords:" + question.getId() + ":" + keyword.getName() + ":" + keyword.getParameter());
					}
				}
			}*/
			// Stores the customer and question.


			//try {
			try {
				boolean custstoreok = customerAPI.store(con, custObj, true);
				SkyLogger.getMediatrixLogger().debug(logPrefix + " new customer created:" + custstoreok + ":" + custObj.getId());
			} catch (SQLException e) {
				SkyLogger.getMediatrixLogger().warn(logPrefix + " not possible to store Customer created:" + ":" + custObj.getId() + " mail:" + custObj.getEmail() + " name:" + custObj.getName() + " msg:" + e.getMessage(), e);
			}
			boolean caseok = false;
			if (questionCase != null) {
				questionCase.setCustomerId(custObj.getId());
				try {
					caseok = caseAPI.store(con, questionCase);
				} catch (Exception e) {
					SkyLogger.getMediatrixLogger().warn(logPrefix + " not possible to store case:" + e.getMessage(), e);
				}
				//SkyLogger.getMediatrixLogger().debug(logPrefix + "case:"+caseok);
			}
			String headers = question.getHeaders();
			headers = TagMatchDefinitions.addOrReplaceXTMHeader(headers, TagMatchDefinitions.CUSTOMER_ID, questionExtId);
			headers = TagMatchDefinitions.addOrReplaceXTMHeader(headers, TagMatchDefinitions.X_TAGMATCH_CUSTOMER_ID, questionExtId);
			question.setHeaders(headers);
			String checker = TagMatchDefinitions.extractXTMHeader(question.getHeaders(), TagMatchDefinitions.X_TAGMATCH_CUSTOMER_ID);
			SkyLogger.getMediatrixLogger().debug(logPrefix + " questionExtID:" + questionExtId + " 1:" + checker);
			SkyLogger.getMediatrixLogger().debug(logPrefix + " questionExtID:" + questionExtId + " 2a:" + TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.X_TAGMATCH_CUSTOMER_ID));
			SkyLogger.getMediatrixLogger().debug(logPrefix + " questionExtID:" + questionExtId + " 2b:" + TagMatchDefinitions.extractXTMHeader(question.getHeaders(), TagMatchDefinitions.X_TAGMATCH_CUSTOMER_ID));
			SkyLogger.getMediatrixLogger().debug(logPrefix + " questionExtID:" + questionExtId + " 3a:" + TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.CUSTOMER_ID));
			SkyLogger.getMediatrixLogger().debug(logPrefix + " questionExtID:" + questionExtId + " 3b:" + TagMatchDefinitions.extractXTMHeader(question.getHeaders(), TagMatchDefinitions.CUSTOMER_ID));
			//	boolean questionstoreok = serverAPI.getQuestionAPI().store(con, question);
			//	SkyLogger.getMediatrixLogger().debug(logPrefix+ " questionstore:" + questionstoreok+"  cid:"+questionExtId+" ext:"+TagMatchDefinitions.extractXTMHeader(question.getHeaders(), TagMatchDefinitions.X_TAGMATCH_CUSTOMER_ID));
				/*	} catch (SQLException ee) {
				SkyLogger.getMediatrixLogger().warn(logPrefix + "custstoreRESTART:" + ee.getMessage());
				ShedulerUtils.checkRuntimeLicense("complete qs:" + question.getId());
				try {
					if (freshCustomer) {
						boolean custstoreok = customerAPI.store(con, custObj, true);
						if (questionCase != null) {
							questionCase.setCustomerId(custObj.getId());
							//caseAPI.store(con, questionCase);
						}
					}
				//	question.setExtra3(questionExtId);
		//			boolean questionstoreok = serverAPI.getQuestionAPI().store(con, question);
					//String questionstoreok="unknown";
					SkyLogger.getMediatrixLogger().info(logPrefix + "eeHandling:Finish");
				} catch (SQLException eee) {
					SkyLogger.getMediatrixLogger().warn(logPrefix + " problems during storing the customer:" + eee.getMessage());
				}
			}*/
		} catch (SQLException e) {
			SkyLogger.getMediatrixLogger().error(logPrefix + e.getMessage() + "\n" + question.getId(), e);
		}
	}


	private int getProjId(Question question) {
		
		int ret;
		if (question.getProjectId() > 0) {
			ret = question.getProjectId();
		} else {
			ret = SKY_PROJECT_ID;
		}
		
		return ret;
	}

	public Map<String, String> updateSiebelSR(String logPrefix, UpdateContactSRParameter contactData, UpdateMandateSRParameter mandateData) throws Exception {

		final long currentTimeMillis = System.currentTimeMillis();
		Map<String, String> newTags;
		ReindexService rs = new ReindexService();
		if (mandateData != null && mandateData.sepa && mandateData.mandateId != null) {
			SkyLogger.getConnectorLogger().debug("IF4.5: pre: mandate: output:docid" + contactData.docId + " customer:" + contactData.customerNumber + " contractnr:" + contactData.contractNumber + " contactid:" + contactData.contactid + " channel:" + contactData.channel + " doctype:" + contactData.doctype + " direction:" + contactData.direction);
			newTags = rs.reindexFrage(contactData.questionId,contactData.docId, contactData.customerNumber, contactData.contractNumber, contactData.contactid, contactData.channel, contactData.doctype, contactData.direction, mandateData.mandateId, mandateData.signatureDate, mandateData.signatureFlag);
		} else {
			SkyLogger.getConnectorLogger().debug("IF4.5: pre: standard: output: docid" + contactData.docId + " customer:" + contactData.customerNumber + " contractnr:" + contactData.contractNumber + " contactid:" + contactData.contactid + " channel:" + contactData.channel + " doctype:" + contactData.doctype + " direction:" + contactData.direction);
			newTags = rs.reindexFrage(contactData.questionId, contactData.docId, contactData.customerNumber, contactData.contractNumber, contactData.contactid, contactData.channel, contactData.doctype, contactData.direction);
		}
		SkyLogger.getConnectorLogger().debug("IF4.5: done: output:" + contactData.docId + " : " + contactData.customerNumber + " " + contactData.docId);

		final long reindexingTime = System.currentTimeMillis() - currentTimeMillis;
		String stringOutput = "";
		for (String key : newTags.keySet()) {
			stringOutput += key + ":" + newTags.get(key) + ", ";
		}
		SkyLogger.getConnectorLogger().debug("post IF4.5: ms:" + reindexingTime + " output:" + contactData.docId + " : " + contactData.customerNumber + " " + contactData.docId + ": " + stringOutput);
		return newTags;
	}

	/**
	 * Executes the contex process that updates the Siebel contact and returns
	 * the new customer data.
	 *
	 * @param inputMap
	 * @param processName
	 * @return A map containing the new customer data
	 * @throws Exception
	 */
	@Deprecated
	protected Map<String, String> processContactUpdate(Hashtable<String, String> inputMap, String processName) throws Exception {

		// Calls contex process.
		ProcessResult re = (new DesignerServiceClient()).execute(processName, masterName, inputMap);
		// Collects the return values.
		Map<String, String> newTags = new TreeMap<>();
		Iterator it = re.getParameter().names();
		while (it.hasNext()) {
			String key = (String) it.next();
			newTags.put(key, re.getParameter().getParameter(key).getAsString());
		}

		return newTags;
	}

	public String updateHeadersAndTags(Map<String, String> newTags, Question question, Map<String, TagMatch> oldTags) {
		String headers = question.getHeaders();
		List<String> questionEssentialsTags = new LinkedList<>();
		questionEssentialsTags.addAll(TagMatchDefinitions.CUSTOMER_DATA);
		questionEssentialsTags.add(TagMatchDefinitions.DOCUMENT_ID);
		questionEssentialsTags.addAll(newTags.keySet());
		for (String key : questionEssentialsTags) {
			String value = newTags.get(key);
			value = value != null ? value : "";
			SkyLogger.getMediatrixLogger().debug("customerTag: <" + key + "," + value + ">");
			TagMatch tagMatch = oldTags.get(key);
			if (tagMatch != null) {
				tagMatch.setTagValue(value);
			} else {
				oldTags.put(key, new TagMatch(key, value));
			}
			headers = TagMatchDefinitions.addOrReplaceXTMHeader(headers, key, value);
		}
		question.setHeaders(headers);
		return headers;
	}

	/*
	 * Server implementation (non-Javadoc)
	 * 
	 * @see
	 * BusinessRule#loadKeywords
	 * (java.sql.Connection, de.ityx.mediatrix.data.Question)
	 */
	public List<Keyword> loadKeywords(Connection con, final Question question) {
		List<Keyword> keywords = null;
		try {
			
			if (API.getServerAPI() != null && API.getServerAPI().getQuestionAPI() != null) {
				if (con != null && question != null && (question.getId() > 0)) {
					keywords = API.getServerAPI().getQuestionAPI().loadKeywords(con, question.getId());
					SkyLogger.getMediatrixLogger().debug("Keywords loaded for Question:" + question.getId());
				} else {
					if (con == null) {
						SkyLogger.getMediatrixLogger().debug("Empty Connection, Keywords not loaded");
					} else {
						SkyLogger.getMediatrixLogger().debug("Empty QuestionID, Keywords not loaded");
					}
				}
			} else {
				SkyLogger.getMediatrixLogger().debug("API not available here, Keywords not loaded");
			}

		} catch (SQLException e) {
			SkyLogger.getMediatrixLogger().error("Keywords not loaded:" + e.getMessage(), e);
		}
		return keywords;
	}

	public boolean isEmpty(String s) {
		return (s == null || s.trim().equals("") || s.trim().equals("0") || s.trim().equals("unclassified") || s.trim().equals("systemdefault"));
	}


	protected String formatMap(Map<String, String> inputMap) {
		StringBuilder answer = new StringBuilder();
		for (Map.Entry<String, String> it : inputMap.entrySet()) {
			answer.append(it.getKey()).append(":").append(it.getValue()).append("; ");
		}
		return answer.toString();
	}
	public static void executeArchiving(Connection con, Map<String, String> metaMap, Email email, AbstractArchiveMetaData metaData) throws Exception {
		executeArchiving(con, metaMap,email,metaData,null);
	}

	public static void executeArchiving(Connection con, Map<String, String> metaMap, Email email, AbstractArchiveMetaData metaData, String status) throws Exception {
		String logPrefix = "SkyRule#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ": ";

		//createArchivingFiles(con, metaMap, email, metaData);
		if (email.getProjectId() == SBS_PROJECT_ID) {
			metaMap.put(MxOutboundIntegration.DOCPOOL_PARAMETER, "SBS_811_Archiv");
			MxOutboundIntegration.addCxOutboundDocumentProcessToDBQueue(con, metaMap, "sbs");
		} else {
			String archiveFlag = TagMatchDefinitions.extractXTMHeader(email.getHeaders(), TagMatchDefinitions.ARCHIVE_FLAG);
			if (archiveFlag !=null && (archiveFlag.equalsIgnoreCase(AUTOARCHIVE_INITIALIZED) || archiveFlag.equalsIgnoreCase(Question.S_MONITORED) )){
				SkyLogger.getMediatrixLogger().debug(logPrefix + metaMap.get(TagMatchDefinitions.DOCUMENT_ID) + " executeArchiving already initiated, skipping");
			}else {
				metaMap.put(MxOutboundIntegration.DOCPOOL_PARAMETER, "811_Archiv");
				MxOutboundIntegration.addCxOutboundDocumentProcessToDBQueue(con, metaMap, "sky");
				if (email instanceof Question) {
					Question question=(Question) email;
					MitarbeiterlogWriter.writeMitarbeiterlog(email.getOperatorId(), question.getId(), 0, OperatorLogRecord.ACTION_INFO, "Autoarchiving initiated:" + question.getDocId(), System.currentTimeMillis(), false);
					//MitarbeiterlogWriter.writeMitarbeiterlog(email.getOperatorId(), question.getId(), 0, 19, SkyServerObjectsControl.DWH + " TP-ID: " + question.getSubprojectId(), System.currentTimeMillis(), false);
				}
				if (status!=null && (status.equalsIgnoreCase(AUTOARCHIVE_INITIALIZED)|| status.equalsIgnoreCase(Question.S_MONITORED)) ) {
					email.setHeaders(TagMatchDefinitions.addOrReplaceXTMHeader(email.getHeaders(), TagMatchDefinitions.ARCHIVE_FLAG, status));
				}
			}
		}
		SkyLogger.getMediatrixLogger().debug(logPrefix + metaMap.get(TagMatchDefinitions.DOCUMENT_ID) + " executeArchiving finished");
	}

	public static void autoClose(Connection con, Question question, String status) {
		String logPrefix = "SkyRule#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + " q:" + question.getId() + " d:" + question.getDocId() + ":";

		SkyLogger.getMediatrixLogger().debug(logPrefix + "ServerEmailDemon.AutoArchiveClose start");
		AbstractArchiveMetaData archiveMetaData = ArchiveMetaDataUtils.getArchiveMetaData(con, question);
		Map<String, String> metaMap;
		try {
			String autoProcCancelFlag = TagMatchDefinitions.extractXTMHeader(question.getHeaders(), TagMatchDefinitions.AUTOMATICALLY_PROCESSED_CANCELLATION_FLAG);
			metaMap = archiveMetaData.collectMetadata(con, question);
			SkyLogger.getMediatrixLogger().debug(logPrefix + "ServerEmailDemon.AutoArchiveClose archiveExecuted");
			if (status.equals(Question.S_MONITORED)){
				executeArchiving(con, metaMap, question, archiveMetaData, Question.S_MONITORED);
			}else{
				executeArchiving(con, metaMap, question, archiveMetaData,AUTOARCHIVE_INITIALIZED );
			}
			SkyLogger.getMediatrixLogger().debug(logPrefix + "ServerEmailDemon.AutoArchiveClose storing with new status:" + status);
			if (!(autoProcCancelFlag!=null  &&
					((question.getStatus().equalsIgnoreCase(Question.S_COMPLETED) && autoProcCancelFlag.equalsIgnoreCase(Question.S_COMPLETED)) ||
							(question.getStatus().equalsIgnoreCase(Question.S_NEW) && autoProcCancelFlag.equalsIgnoreCase(Question.S_NEW))
					))
					){
				question.setStatus(status);
			};
			SkyLogger.getCommonLogger().debug("SkyRule.autoclose.QStore1b Generated docid:" + question.getDocId() + " frage:" + question.getId()+" status:"+question.getStatus());
			boolean questionstoreok = API.getServerAPI().getQuestionAPI().store(con, question);
			SkyLogger.getCommonLogger().debug("SkyRule.autoclose.QStore2b Generated docid:" + question.getDocId() + " frage:" + question.getId()+" sok:"+questionstoreok);
			SkyLogger.getMediatrixLogger().debug(logPrefix + "ServerEmailDemon.AutoArchiveClose storing complete");
			
			SkyLogger.getMediatrixLogger().debug(logPrefix + "ServerEmailDemon.AutoArchiveClose finished:" + question.getStatus());
		} catch (Exception ex) {
			question.setStatus(Question.S_NEW);
			MitarbeiterlogWriter.writeMitarbeiterlog(0, question.getId(), 0, OperatorLogRecord.ACTION_INFO,"Undefined Exception at ServerEmailDemon.AutoClose: " + ex.getMessage(), System.currentTimeMillis(), true);
			SkyLogger.getMediatrixLogger().error(logPrefix + "Undefined Exception at ServerEmailDemon.AutoClose: " + ex.getMessage(), ex);
		}
	}
}
