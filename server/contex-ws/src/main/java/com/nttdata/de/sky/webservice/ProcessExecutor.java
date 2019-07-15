package com.nttdata.de.sky.webservice;

import com.nttdata.de.ityx.cx.workflow.outgoing.mxpostprocessing.MxDbSingleton;
import com.nttdata.de.ityx.sharedservices.configuration.BeanConfig;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.ityx.sharedservices.utils.ShedulerUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.lib.utils.MitarbeiterlogWriter;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import com.nttdata.de.sky.outbound.MxOutboundIntegration;
import com.nttdata.de.sky.outbound.MxOutboundIntegration.MXOUT_DIRECTION;
import de.ityx.base.Global;
import de.ityx.contex.dbo.designer.Designer_documentpool;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.interfaces.document.CDocument;
import de.ityx.contex.webservice.schema.ContexError;
import de.ityx.contex.webservice.schema.ContexResponse;
import de.ityx.contex.webservice.schema.Entry;
import de.ityx.contex.webservice.service.ContexErrorMessage;
import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.data.OperatorLogRecord;
import de.ityx.mediatrix.data.Question;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


public class ProcessExecutor {

	private static ProcessExecutor pe;
	private static Map<String, Calendar> processingSet = Collections.synchronizedMap(new HashMap<String, Calendar>());
	private static final String AUTOMATICALLY_PROCESSED_CANCELLATION = "Automatically processed cancellation";
	private static final String AUTOMATICALLY_PROCESSED_SUBSEQUENTCANCELLATION = "Automatically processed subsequent cancellation";

	private static final String MXOUTBOUND_GETSINGLECB = "SELECT ID, DOCUMENT_ID, QUESTION_ID, ANSWER_ID, EMAIL_ID, " + "ACTIVITY_ID, CONTACT_ID, CUSTOMER_ID, CONTRACT_NUMBER, CHANNEL, DIRECTION, TP_NAME, " + "PROCESS, MASTER, FORMTYPE from NTT_MX_OUTBOUND_QUEUE where id in (select max(id) from NTT_MX_OUTBOUND_QUEUE where STATUS in ('WAITFORCB','CBERROR', 'PROCERR', 'METAERR')" + " and process = ? and QUESTION_ID>0  AND DOCUMENT_ID=?)";

	public static ProcessExecutor getInstance() {
		if (pe == null) {
			pe = new ProcessExecutor();
		}
		return pe;
	}

	public ContexResponse runProcess(String master, String processname, List<Entry> entries) throws ContexErrorMessage {

		CxWsParamReader reader = new CxWsParamReader(entries);
		boolean isError = false;
		// Also Ignore ErrorCode:6; ErrorMessage: Vertrag hat bereits eine eingetragene Kündigung
		if (!(reader.getErrorCode() == null || reader.getErrorCode().trim().equals("0") || reader.getErrorCode().trim().equals("6") || reader.getErrorCode().trim().equals("9") || reader.getErrorCode().trim().isEmpty())) {
			isError = true;
			SkyLogger.getConnectorLogger().error("IF_ContexWS:" + reader.getDocId() + " PARAM_ERROR:" + " CorrelationId: " + reader.getCorrelationId() + " Contact:" + reader.getContactid() + " DocId: " + reader.getDocId() + "  usecaseId-Parameter: " + reader.getUsecaseId() + "  errorCode-Parameter: " + reader.getErrorCode() + " errorMessage-Parameter: " + reader.getErrorMessage());
		}

		if (reader.getDocId() == null || reader.getDocId().isEmpty()) {
			if (reader.getCorrelationId() != null && !reader.getCorrelationId().isEmpty() && reader.getCorrelationId().startsWith("ITYX20")) {
				reader.setDocId(reader.getCorrelationId());
			} else {
				SkyLogger.getConnectorLogger().warn("IF_ContexWS: Emtpy DocID:" + reader.getDocId() + " useCase:" + reader.getUsecaseId() + " !!Skipping!! cid:" + reader.getCorrelationId() + " contact:" + reader.getContactid());
				ContexError e = new ContexError();
				String fault = "IF_ContexWS:Emtpy DocID:" + reader.getDocId() + " useCase:" + reader.getUsecaseId() + " !!Skipping!! cid:" + reader.getCorrelationId() + " contact:" + reader.getContactid();
				e.setMessage(fault);
				throw new ContexErrorMessage(fault, e);
			}
		}

		synchronized (AUTOMATICALLY_PROCESSED_CANCELLATION) {
			if (processingSet.containsKey(reader.getDocId())) {
				Calendar lastProcessingDate = processingSet.get(reader.getDocId());
				Calendar tenminago = Calendar.getInstance();
				tenminago.add(Calendar.MINUTE, -10);

				if (lastProcessingDate.before(tenminago)) {
					//last processing 10 min ago, update Timestamp
					processingSet.put(reader.getDocId(), Calendar.getInstance());
				} else {
					SkyLogger.getConnectorLogger().warn("IF_ContexWS: Double callback for DocID:" + reader.getDocId() + " useCase:" + reader.getUsecaseId() + " !!Skipping!!");
					ContexError e = new ContexError();
					e.setMessage("IF_ContexWS: Double callback for DocID:" + reader.getDocId() + " useCase:" + reader.getUsecaseId() + " within 10 min!");
					throw new ContexErrorMessage("IF_ContexWS: Double callback for DocID:" + reader.getDocId() + " useCase:" + reader.getUsecaseId() + " !!Skipping!!", e);
				}
			} else {
				processingSet.put(reader.getDocId(), Calendar.getInstance());
			}
		}

		try {
			SkyLogger.getConnectorLogger().info("IF_ContexWS: START " + reader.getDocId() + " useCase:" + reader.getUsecaseId() + " process:" + processname);
			if (reader.getUsecaseId() != null) {
				switch (reader.getUsecaseId().toUpperCase()) {
					case "DMS IF 4.1":
						ShedulerUtils.checkAuth();
						scheduleProcess(master, reader, reader.getUsecaseId());
						break;
					case "DMS IF 4.4":
						SkyLogger.getConnectorLogger().info("IF_ContexWS: IF4.4" + reader.getDocId() + ": ContactID-Parameter: " + reader.getContactid() + "  reader.getUsecaseId()-Parameter: " + reader.getUsecaseId() + "  errorCode-Parameter: " + reader.getErrorCode() + " errorMessage-Parameter: " + reader.getErrorMessage() + " cor-Parameter: " + reader.getCorrelationId());
						// no functionality
						break;
					case "DMS IF 4.2":
						SkyLogger.getConnectorLogger().info("IF_ContexWS: IF4.2" + reader.getDocId() + ": ContactID-Parameter: " + reader.getContactid() + "  usecaseId-Parameter: " + reader.getUsecaseId() + "  errorCode-Parameter: " + reader.getErrorCode() + " errorMessage-Parameter: " + reader.getErrorMessage() + " cor-Parameter: " + reader.getCorrelationId());

						//long documentId = documentPool.createAndStoreDocumentContainer(master, payload, uri,	contentType, prio, parameter, serviceLevel, delay, maxCollectionWaitTime, true);
						break;
					case "DMS IF 4.6":
						SkyLogger.getConnectorLogger().info("IF_ContexWS: IF4.6 " + reader.getDocId() + " ContactID-Parameter: " + reader.getContactid() + "  usecaseId-Parameter: " + reader.getUsecaseId() + "  errorCode-Parameter: " + reader.getErrorCode() + " errorMessage-Parameter: " + reader.getErrorMessage() + " cor-Parameter: " + reader.getCorrelationId());
						ShedulerUtils.checkAuth();
						ShedulerUtils.checkRuntimeLicense("IF4.6" + reader.getDocId());
						Connection con = null;
						try {
							con = MxDbSingleton.getMxConnection();
							Map<String, Object> docMeta = getCallbackOutboundDocumentProcessesFromDB(con, "840_Cancellation", reader.getCorrelationId());
							long logID = 0L;
							String docId = null;
							Question question = null;
							try {
								if (docMeta != null && docMeta.size() > 0) {
									docId = (String) docMeta.get(TagMatchDefinitions.DOCUMENT_ID);
									logID = (Long) docMeta.get(MxOutboundIntegration.MX2CX_ID);
									String questionID = (String) docMeta.get(TagMatchDefinitions.MX_QUESTIONID);
									SkyLogger.getConnectorLogger().info("IF_ContexWS: IF4.6 questionID: " + questionID);
									if (questionID != null) {
										question = API.getServerAPI().getQuestionAPI().load(con, Integer.parseInt(questionID), false);
										SkyLogger.getConnectorLogger().info("IF_ContexWS: IF4.6 logging: " + question.getId());

										if (Global.getOperatorLogLength() < 20) {
											synchronized (AUTOMATICALLY_PROCESSED_CANCELLATION) {
												Global.initializeConfigurationForUnitIfNotDoneYet("DEFAULT");
												Global.setOperatorLogLength(2040);
											}
										}
										if (isError) {
											SkyLogger.getConnectorLogger().error("IF_ContexWS: IF4.6: " + docId + " l:" + logID + "  FAILED:" + reader.getErrorCode() + ":" + reader.getErrorMessage());
											throw new Exception("IF4.6: " + docId + " l:" + logID + " FAILED: Tibco/SBLError:" + reader.getErrorCode() + ":" + reader.getErrorMessage());
										} else {
											MitarbeiterlogWriter.writeMitarbeiterlog( 0, question.getId(), 0, OperatorLogRecord.ACTION_INFO, AUTOMATICALLY_PROCESSED_CANCELLATION + " finished.", System.currentTimeMillis(), true);
											SkyLogger.getConnectorLogger().debug("IF_ContexWS: IF4.6: " + docId + " l:" + logID + " markAsProcessed.");
											MxOutboundIntegration.updateContexProcessedAttribute(con, logID, MxOutboundIntegration.MXOUT_STATUS.PROCESSED);
											if (!question.getStatus().equalsIgnoreCase(Question.S_COMPLETED)) {
												question.setStatus(Question.S_COMPLETED);
												question.setHeaders(TagMatchDefinitions.addOrReplaceXTMHeader(question.getHeaders(), TagMatchDefinitions.AUTOMATICALLY_PROCESSED_CANCELLATION_FLAG, Question.S_COMPLETED));
												de.ityx.mediatrix.api.API.getServerAPI().getQuestionAPI().store(con, question);
												SkyLogger.getConnectorLogger().debug("IF_ContexWS: IF4.6: " + docId + " l:" + logID + " QuestionStatus set to Completed (IF 4.6 Callback received).");
												MitarbeiterlogWriter.writeMitarbeiterlog( 0, question.getId(), 0, OperatorLogRecord.ACTION_INFO, " Question status set to completed (IF 4.6 Callback received).", System.currentTimeMillis(), true);
											}
											//Error Code 9 / Subsequent cancellation has been triggered instead of Regular
											// Save in log -> ErrorCode:9;
											if (reader.getErrorCode() != null && reader.getErrorCode().trim().equals("9")){
												MitarbeiterlogWriter.writeMitarbeiterlog( 0, question.getId(), 0, OperatorLogRecord.ACTION_INFO, AUTOMATICALLY_PROCESSED_SUBSEQUENTCANCELLATION + " finished.", System.currentTimeMillis(), true);
												MxOutboundIntegration.updateContexProcessedAttribute(con, logID, MxOutboundIntegration.MXOUT_STATUS.PROCESSED,reader.getErrorMessage());
											}
											// Save in log -> ErrorCode:6; ErrorMessage: Vertrag hat bereits eine eingetragene Kündigung
											else if (reader.getErrorCode() != null && reader.getErrorCode().trim().equals("6")){
												MitarbeiterlogWriter.writeMitarbeiterlog( 0, question.getId(), 0, OperatorLogRecord.ACTION_INFO, "IF4.6: " + docId + "; l:" + logID + "; Tibco/SBLError:" + reader.getErrorCode() + "; " + reader.getErrorMessage(), System.currentTimeMillis(), true);
												/* Ticket #262431  ->ErrorCode will be writed on the NTT_OUTBOUND_QUEQUE but the Status is PROCESSED  */
												MxOutboundIntegration.updateContexProcessedAttribute(con, logID, MxOutboundIntegration.MXOUT_STATUS.PROCESSED,reader.getErrorMessage());
											}
											//BUG: 202210 - Cancellation optimization CO-91 positiv fall soll nicht geloggt werden.
											//if (reader.getErrorCode() != null && (reader.getErrorCode().trim().equals("0") ||  reader.getErrorCode().trim().equals("1"))){
											//	MitarbeiterlogWriter.writeMitarbeiterlog( 0, question.getId(), 0, OperatorLogRecord.ACTION_INFO, "IF4.6: " + docId + "; l:" + logID + "; Tibco/SBLError:" + reader.getErrorCode() + "; " + reader.getErrorMessage(), System.currentTimeMillis(), true);
											//}
											if (!con.getAutoCommit()) {
												con.commit();
											}
										}
									} else {
										throw new Exception("IF4.6: " + docId + " l:" + logID + " Unable to process document. Callback incomplete: QuestionID missing");
									}
								} else {
									throw new Exception("IF4.6: "  + " l:" + logID + " Unable to process document. Document not found");
								}
							} catch (Exception e) {
								if (logID > 0L) {
									MxOutboundIntegration.updateContexProcessedAttribute(con, logID, MxOutboundIntegration.MXOUT_STATUS.CBERROR, e.getMessage());
									SkyLogger.getConnectorLogger().error("IF_ContexWS: IF4.6: " + docId + " l:" + logID + " Unable to process document. MARKED as FAILED in ntt_cx_outbound_queue:" + e.getMessage(), e);
								} else {
									SkyLogger.getConnectorLogger().error("IF_ContexWS: IF4.6: " + docId + " l:" + logID + " Unable to process document. errMsg: " + e.getMessage(), e);
								}
								if (question != null) {
									MitarbeiterlogWriter.writeMitarbeiterlog(0, question.getId(), 0, OperatorLogRecord.ACTION_INFO, e.getMessage(), System.currentTimeMillis(), true);
									if (!question.getStatus().equalsIgnoreCase(Question.S_NEW)) {
										try {
                                            SkyLogger.getConnectorLogger().debug("IF_ContexWS: IF4.6: 1 q.status: " + question.getStatus()+"; q.subprojectId: "+question.getSubprojectId());
											question.setStatus(Question.S_NEW);
											question.setHeaders(TagMatchDefinitions.addOrReplaceXTMHeader(question.getHeaders(), TagMatchDefinitions.AUTOMATICALLY_PROCESSED_CANCELLATION_FLAG, Question.S_NEW));
											int subPrgId = BeanConfig.getInt("CancellationReopenTeilprojekt", 941);
											if ( question.getSubprojectId() != subPrgId ) {
												question.setSubprojectId(subPrgId);
												question.setSubprojectForwardId(subPrgId);
											}
                                            SkyLogger.getConnectorLogger().debug("IF_ContexWS: IF4.6: 2 q.status: " + question.getStatus()+"; q.subprojectId: "+question.getSubprojectId());
											de.ityx.mediatrix.api.API.getServerAPI().getQuestionAPI().store(con, question);
											if (!con.getAutoCommit()) {
												con.commit();
											}
										} catch (Exception ex) {
											MitarbeiterlogWriter.writeMitarbeiterlog(0, question.getId(), 0, OperatorLogRecord.ACTION_INFO,"Question update errMsg: " + ex.getMessage(), System.currentTimeMillis(), true);
                                            SkyLogger.getConnectorLogger().error("IF_ContexWS: IF4.6: " + docId + " l:" + logID + " Question update errMsg: " + ex.getMessage(), ex);
										}
									} else {
										MitarbeiterlogWriter.writeMitarbeiterlog(0, question.getId(), 0, OperatorLogRecord.ACTION_INFO,"Question has new status!", System.currentTimeMillis(), true);
                                        SkyLogger.getConnectorLogger().debug("IF_ContexWS: IF4.6: 3 q.status: " + question.getStatus()+"; q.subprojectId: "+question.getSubprojectId());
									}
                                    SkyLogger.getConnectorLogger().debug("IF_ContexWS: IF4.6: 4 q.status: " + question.getStatus()+"; q.subprojectId: "+question.getSubprojectId());
								}
								if (!isError) {
									// 202210 cancellation optimization - CR003 WebService Callback with Error-Messages in payload should not be answered
									// to tibco with error - in order to prevent multiple reopening of questions in Mediatrix (answer with error cause new rescheduling
									// of tibco callback)
									throw e;
								}
							}
							try {
								if (!con.getAutoCommit()) {
									con.commit();
								}
							} catch (SQLException e) {
								SkyLogger.getWflLogger().error("IF_ContexWS: IF4.6: " + docId + " l:" + logID + " Unable to commit changes:" + e.getMessage(), e);
							}

						} finally {
							MxDbSingleton.closeConnection(con);
						} break;
					default:
						ContexError e = new ContexError();
						e.setMessage("Unknown UseCaseID:" + reader.getUsecaseId());
						SkyLogger.getConnectorLogger().error("IF_ContexWS:" + reader.getDocId() + " ERROR_UseCaseID_Unknown: ContactID-Parameter: " + reader.getContactid() + "  usecaseId-Parameter: " + reader.getUsecaseId() + "  errorCode-Parameter: " + reader.getErrorCode() + " errorMessage-Parameter: " + reader.getErrorMessage() + " cor-Parameter: " + reader.getCorrelationId());
						throw new ContexErrorMessage("IF_ContexWS:" + reader.getDocId() + " ERROR_UseCaseID_Unknown: ContactID-Parameter: " + reader.getContactid() + "  usecaseId-Parameter: " + reader.getUsecaseId() + "  errorCode-Parameter: " + reader.getErrorCode() + " errorMessage-Parameter: " + reader.getErrorMessage() + " cor-Parameter: " + reader.getCorrelationId(), e);
				}
			} else {
				ContexError e = new ContexError();
				e.setMessage("No UseCaseID given");
				SkyLogger.getConnectorLogger().error("IF_ContexWS:" + reader.getDocId() + " ERROR_UseCaseID_Null: ContactID-Parameter: " + reader.getContactid() + "  errorCode-Parameter: " + reader.getErrorCode() + " errorMessage-Parameter: " + reader.getErrorMessage() + " cor-Parameter: " + reader.getCorrelationId());
				throw new ContexErrorMessage("IF_ContexWS:" + reader.getDocId() + " ERROR_UseCaseID_Null: ContactID-Parameter: " + reader.getContactid() + "  errorCode-Parameter: " + reader.getErrorCode() + " errorMessage-Parameter: " + reader.getErrorMessage() + " cor-Parameter: " + reader.getCorrelationId(), e);

			}
		} catch (Exception e) {
			ContexError ee = new ContexError();
			ee.setMessage("Scheduling of process for docid:" + reader.getDocId() + " not successfull. " + e.getMessage());
			SkyLogger.getConnectorLogger().error("IF_ContexWS:" + reader.getDocId() + " ERROR_IF" + reader.getUsecaseId() + ": SendingBackErrorToTibco ContactID-Parameter: " + reader.getContactid() + "  usecaseId-Parameter: " + reader.getUsecaseId() + "  errorCode-Parameter: " + reader.getErrorCode() + " errorMessage-Parameter: " + reader.getErrorMessage() + " cor-Parameter: " + reader.getCorrelationId() + " msg:" + e.getMessage(), e);
			throw new ContexErrorMessage("IF_ContexWS:" + reader.getDocId() + " ERROR_ScheduleProcess" + e.getMessage(), ee);

		} finally {
			//if (forceauth)
			//ContexSecurityTool.clearAuthentication();
			try {
				synchronized (AUTOMATICALLY_PROCESSED_CANCELLATION) {
					processingSet.remove(reader.getDocId());
				}
			} catch (Exception e) {
				SkyLogger.getConnectorLogger().error("IF_ContexWS:" + reader.getDocId() + " ERROR_Cleaning ressources:" + e.getMessage(), e);
			}
		} return new ContexResponse();
	}

	private void scheduleProcess(String master, CxWsParamReader reader, String usecaseId) throws Exception {
		boolean started = false;
		for (Designer_documentpool doc : ShedulerUtils.getDocsByParameter("sky", "550_CRM_Callback_" + reader.getDocId(), reader.getDocId())) {

			if (started) {
				// Duplicate - Check!!
				SkyLogger.getConnectorLogger().warn("IF_ContexWS: InternalProcProblem for DocID:" + reader.getDocId() + " useCase:" + reader.getUsecaseId() + " ");
			} else {
				String parameter = "600_MXInjection";
				try {
					String formtype = null;
					de.ityx.contex.dbo.designer.Designer_documentpooldata data = doc.getData();
					if (data != null) {
						String xml = data.getDocXML();
						byte[] bin = data.getBinaryObject();
						if (bin != null) {
							ObjectInputStream bis = new ObjectInputStream(new ByteArrayInputStream(bin));
							CDocumentContainer<CDocument> docContainer = (CDocumentContainer<CDocument>) bis.readObject();
							formtype = DocContainerUtils.getFormtype(docContainer);
						}
					}
					//if (formtype != null && !formtype.isEmpty() && (formtype.equals("fh_vertrag") || formtype.equals(TagMatchDefinitions.SEPA_MANDATE))) { //
						//	parameter = "FHV_602_MXInjection";
					//}
				} catch (Exception e) {
					SkyLogger.getConnectorLogger().error("IF_ContexWS:" + reader.getDocId() + " problem getting formtype" + e.getMessage(), e);
				}

				SkyLogger.getConnectorLogger().info("IF_ContexWS:" + reader.getDocId() + " Scheduling:start:" + " " + " parameter:" + parameter + " for: " + reader.getDocId() + "< ContactID-Parameter: " + reader.getContactid() + "  usecaseId-Parameter: " + reader.getUsecaseId() + "  errorCode-Parameter: " + reader.getErrorCode() + " errorMessage-Parameter: " + reader.getErrorMessage() + " cor-Parameter: " + reader.getCorrelationId());

				Map<String, String> notes = new LinkedHashMap<>();

				if (!(reader.getErrorCode() == null || reader.getErrorCode().equals("0") || reader.getErrorCode().isEmpty())) {
					SkyLogger.getConnectorLogger().error("IF_ContexWS:" + reader.getDocId() + " PARAM_ERROR: ContactID-Parameter: " + reader.getDocId() + "  usecaseId-Parameter: " + reader.getUsecaseId() + "  errorCode-Parameter: " + reader.getErrorCode() + " errorMessage-Parameter: " + reader.getErrorMessage() + " cor-Parameter: " + reader.getCorrelationId());
					notes.put("CXWS_Exception", "CXWSERR:" + reader.getErrorCode() + ":" + reader.getErrorMessage());
				}
				notes.put(TagMatchDefinitions.CONTACT_ID, reader.getContactid());
				ShedulerUtils.wakeupDocumentAndSheduleWithNote("DEFAULT", master, parameter, doc, notes);
				SkyLogger.getConnectorLogger().info("IF_ContexWS:" + reader.getDocId() + " Scheduling:done:" + " " + " parameter:" + parameter + " for: " + reader.getDocId() + "< ContactID-Parameter: " + reader.getContactid() + "  usecaseId-Parameter: " + reader.getUsecaseId() + "  errorCode-Parameter: " + reader.getErrorCode() + " errorMessage-Parameter: " + reader.getErrorMessage() + " cor-Parameter: " + reader.getCorrelationId());

				started = true;
			}
		}
		if (!started) {
			SkyLogger.getConnectorLogger().error("IF_ContexWS:" + reader.getDocId() + " ERROR_DocNotFound: >" + "550_CRM_Callback_" + reader.getDocId() + "< ContactID-Parameter: " + reader.getContactid() + "  usecaseId-Parameter: " + reader.getUsecaseId() + "  errorCode-Parameter: " + reader.getErrorCode() + " errorMessage-Parameter: " + reader.getErrorMessage() + " cor-Parameter: " + reader.getCorrelationId());
		}
	}


	private class CxWsParamReader {
		String docId;
		String contactid;
		String errorCode;
		String errorMessage;
		String correlationId;
		String usecaseId;

		public String getDocId() {
			return docId;
		}

		public void setDocId(String idocid) {
			docId = idocid;
		}

		public String getContactid() {
			return contactid;
		}

		public String getErrorCode() {
			return errorCode;
		}

		public String getErrorMessage() {
			return errorMessage;
		}

		public String getCorrelationId() {
			return correlationId;
		}

		public String getUsecaseId() {
			return usecaseId;
		}

		protected CxWsParamReader(List<Entry> entries) throws ContexErrorMessage {
			for (Entry entry : entries) {
				String key = entry.getKey();

				if (key.equalsIgnoreCase("documentid")) {
					docId = entry.getValue();
				} else if (key.equalsIgnoreCase("contactid")) {
					contactid = entry.getValue();
				} else if (key.equalsIgnoreCase("errorcode")) {
					errorCode = entry.getValue();
				} else if (key.equalsIgnoreCase("errormessage")) {
					errorMessage = entry.getValue();
				} else if (key.equalsIgnoreCase("correlationid")) {
					correlationId = entry.getValue();
				} else if (key.equalsIgnoreCase("usecaseid")) {
					usecaseId = entry.getValue();
				} else {
					SkyLogger.getConnectorLogger().error("Unrecognized Parameter:" + key + ":" + entry.getValue());
					ContexError ee = new ContexError();
					ee.setMessage("Unrecognized Parameter:" + key + ":" + entry.getValue());
					throw new ContexErrorMessage("Unrecognized Parameter:" + key + ":" + entry.getValue(), ee);
				}
			}
		}
	}
	
	private Map<String, Object> getCallbackOutboundDocumentProcessesFromDB(Connection con, String processname, String docID) throws Exception {
		String logPrefix = MxOutboundIntegration.class.getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";

		SkyLogger.getCommonLogger().debug(logPrefix + " started...");
		Map<String, Object> docMeta = new HashMap<>();
		PreparedStatement getStmt = null;
		ResultSet rs = null;
		try {
			if (con != null) { //&& con.isValid(120)
				getStmt = con.prepareStatement(MXOUTBOUND_GETSINGLECB);
				getStmt.setString(1, processname);
				getStmt.setString(2, docID);
				rs = getStmt.executeQuery();
				SkyLogger.getCommonLogger().debug(logPrefix + " query: " + getStmt.toString());
				if (rs.next()) {
					docMeta.put(MxOutboundIntegration.MX2CX_ID, rs.getLong(1));
					docMeta.put(TagMatchDefinitions.DOCUMENT_ID, "" + rs.getString(2));
					docMeta.put(TagMatchDefinitions.MX_QUESTIONID, "" + rs.getLong(3));
					docMeta.put(TagMatchDefinitions.MX_ANSWERID, "" + rs.getLong(4));
					docMeta.put(TagMatchDefinitions.MX_EMAILID, "" + rs.getLong(5));
					docMeta.put(TagMatchDefinitions.ACTIVITY_ID, rs.getString(6));
					docMeta.put(TagMatchDefinitions.CONTACT_ID, rs.getString(7));
					docMeta.put(TagMatchDefinitions.CUSTOMER_ID, rs.getString(8));
					docMeta.put(TagMatchDefinitions.TAGMATCH_CONTRACT_NUMBER, "" + rs.getLong(9));
					docMeta.put(TagMatchDefinitions.CHANNEL, rs.getString(10));
					// DIRECTION,
					String intDir = rs.getString(11);

					if (intDir.equalsIgnoreCase(MXOUT_DIRECTION.INBOUND.name())) {
						docMeta.put(TagMatchDefinitions.MX_DIRECTION, MXOUT_DIRECTION.INBOUND.name());
					} else if (intDir.equalsIgnoreCase(MXOUT_DIRECTION.OUTBOUND.name())) {
						docMeta.put(TagMatchDefinitions.MX_DIRECTION, MXOUT_DIRECTION.OUTBOUND.name());
					}
					docMeta.put(TagMatchDefinitions.MX_TP_NAME, rs.getString(12));
					docMeta.put(MxOutboundIntegration.DOCPOOL_PARAMETER, rs.getString(13));
					docMeta.put(TagMatchDefinitions.MX_MASTER, rs.getString(14));
					docMeta.put(TagMatchDefinitions.FORM_TYPE_CATEGORY, rs.getString(15));
					SkyLogger.getCommonLogger().info("documentId: " + rs.getString(2) + " mxcx_queue_id:" + rs.getLong(1));

				}
			} else {

				SkyLogger.getCommonLogger().error("OuboundCX DB-Connection not available:");
				throw new SQLException("OuboundCX  DB-Connection not available.");

			}
		} catch (SQLException e) {
			SkyLogger.getCommonLogger().error(" " + MXOUTBOUND_GETSINGLECB + " :: " + e, e);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (getStmt != null) {
					getStmt.close();
				}
			} catch (SQLException e) {
				SkyLogger.getCommonLogger().error(" SQL; " + e, e);
			}
		}
		return docMeta;
	}
}
