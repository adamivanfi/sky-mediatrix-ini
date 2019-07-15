package com.nttdata.de.sky.ityx.mediatrix.businessrules.server.sky.actions;

import com.nttdata.de.ityx.sharedservices.archiveMetadata.ArchiveMetaDataUtils;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.ityx.utils.CancellationUtils;
import com.nttdata.de.lib.exception.ExtendedMXException;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.lib.utils.MitarbeiterlogWriter;
import com.nttdata.de.sky.archive.AbstractArchiveMetaData;
import com.nttdata.de.sky.connector.ConnectorFactory;
import com.nttdata.de.sky.connector.INewDB;
import com.nttdata.de.sky.connector.data.IndexingCustomer;
import com.nttdata.de.sky.connector.data.TextblockCustomer;
import com.nttdata.de.sky.ityx.common.Actions;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import com.nttdata.de.sky.ityx.mediatrix.businessrules.server.sky.SkyRule;
import com.nttdata.de.sky.outbound.MxOutboundIntegration;
import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.data.Email;
import de.ityx.mediatrix.data.Question;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Action for SR-Call
 */
public class QuickSRAction extends AServerEventAction {

	private static final String MXOUTBOUND_CANCELPARAMS_INSERT = "insert into NTT_MX_OUTBOUND_CANCELPAR " + "( OQ_ID, DOCUMENT_ID,CANCELLATION_DATE, CANCELLATION_REASONCODE, CANCELLATION_REASON_FREETEXT, CANCELLATION_POSSIBLE_DATE ) " + " values (?,?,?,?,?,?)";
	private static final String MXOUTBOUND_CANCELPARAMS_UPDATE = "update NTT_MX_OUTBOUND_CANCELPAR set DOCUMENT_ID=?, CANCELLATION_DATE =?, CANCELLATION_REASONCODE=?, CANCELLATION_REASON_FREETEXT=?, CANCELLATION_POSSIBLE_DATE=? where OQ_ID=?";
	private static final String MXOUTBOUND_CANCELPARAMS_CHECK = "select OQ_ID from NTT_MX_OUTBOUND_CANCELPAR where OQ_ID = ?";
	private static final String MXOUTBOUND_CHECKOPENACTION = "select max(id) from NTT_MX_OUTBOUND_QUEUE where process like '840_Cancellation' and status<>'PROCESSED' and question_ID=?";
	private static final String PARAMETER = "processed cancellation";

	@Override
	public String[] getActionNames() {
		return new String[]{Actions.ACTION_CANCELLATION.name(), Actions.ACTION_OPENCANCELLATIONACTION.name()};
	}

	@Override
	public List actionPerformed(Connection con, String actionname, List parameters) throws Exception {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName();
		SkyLogger.getMediatrixLogger().debug(logPrefix + " QuickSRAction: " + actionname);
		Actions t = null;
		try {
			t = Actions.valueOf(actionname);
		} catch (IllegalArgumentException e) {
			SkyLogger.getMediatrixLogger().error(logPrefix + " QuickSRAction: actionname:" + actionname + " not mapped!!!");
			throw new ExtendedMXException(e.getMessage(), e);
		}

		Boolean success = false;
		String reasonCode = null;
		switch (t) {
		case ACTION_OPENCANCELLATIONACTION:
			Integer chQuestionId = (Integer) parameters.get(0);
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			boolean notClosedAction = false;
			try {
				pstmt = con.prepareStatement(MXOUTBOUND_CHECKOPENACTION);
				pstmt.setLong(1, chQuestionId);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					notClosedAction = true;
				}
			} finally {
				try {
					if (pstmt != null) {
						pstmt.close();
					}
				} catch (SQLException e) {
					SkyLogger.getCommonLogger().warn(logPrefix + ": Problems during release SQL-Res:" + e.getMessage(), e);
				}
			}
			parameters.clear();
			parameters.add(notClosedAction);
			break;
		case ACTION_CANCELLATION:
			Integer questionId = (Integer) parameters.get(0);
			Question question = API.getServerAPI().getQuestionAPI().load(con, questionId, true);
			if (question == null) {
				SkyLogger.getMediatrixLogger().warn(" Could not load question:" + questionId);
				parameters.clear();
				parameters.add(false);
				parameters.add("Frage:"+questionId+" konnte nicht geladen werden");
				return parameters;
			}

			String cancelReason = (String) parameters.get(1);
			// CR04 requirement 7
			Object object = parameters.get(2);
			String loginname = "default";
			if(object != null && object instanceof String) {
				loginname = (String) object;
			}
			parameters.clear();

			try {
				reasonCode = CancellationUtils.mapReasonCode(cancelReason);
				SkyLogger.getMediatrixLogger().debug(Actions.ACTION_CANCELLATION.name() + " : " + reasonCode + " q:" + question.getId());

				if (reasonCode == null) {
					SkyLogger.getMediatrixLogger().warn("TAGMATCH:CANCELLATION_REASON not set for question:" + questionId + " docid:" + question.getDocId());
					parameters.clear();
					parameters.add(false);
					parameters.add("Frage:"+questionId+" konnte nicht geladen werden");
					return parameters;
					//throw new Exception("TAGMATCH:CANCELLATION_REASON not set for question:" + questionId + " docid:" + question.getDocId());
				}
				String headers = question.getHeaders();
				headers = TagMatchDefinitions.addOrReplaceXTMHeader(headers, TagMatchDefinitions.CANCELLATION_REASON_CODE, reasonCode);
				String reasonDetail = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.CANCELLATION_REASON_FREE_TEXT);
				if (reasonDetail != null && !reasonDetail.isEmpty()) {
					reasonDetail = CancellationUtils.mapReasonDetail(cancelReason);
					if (reasonDetail != null && !reasonDetail.isEmpty()) {
						headers = TagMatchDefinitions.addOrReplaceXTMHeader(headers, TagMatchDefinitions.CANCELLATION_REASON_FREE_TEXT, reasonDetail);
					}
				}
				question.setHeaders(headers);
				success = triggerCancellationProcess(con, question, true, loginname);
				parameters.clear();
				parameters.add(success);
				parameters.add(reasonCode);
				return parameters;

			} catch (Exception e) {
				SkyLogger.getMediatrixLogger().error(logPrefix + "Problem during processing of Action:" + t.name() + " q:" + questionId + " docid:" + question.getDocId() + " msg:" + e.getMessage(), e);
				parameters.clear();
				parameters.add(false);
				parameters.add(e.getMessage());
			}
			break;
		default:
			parameters.clear();
			parameters.add(false);
			parameters.add(null);
			break;
		}
		return parameters;
	}

	public boolean triggerCancellationProcess(Connection con, Question question, boolean isManualProcess, String loginname) throws Exception {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + " q:" + question.getId() + " d:" + question.getDocId();
		int questionId = question.getId();
		String headers = question.getHeaders();
		String documentid = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.DOCUMENT_ID);

		AbstractArchiveMetaData archiveMetaData = ArchiveMetaDataUtils.getArchiveMetaData(con, question);
		Map<String, String> metaMap = archiveMetaData.collectMetadata(con, question);
		metaMap.put(MxOutboundIntegration.DOCPOOL_PARAMETER, "840_Cancellation");

		if (!archiveMetaData.shouldBeArchived(metaMap)) {
			SkyLogger.getMediatrixLogger().warn(logPrefix + " BusinessTest failed: Question without CustomerData can not start CancellationProcess: " + question.getId() + " docId:" + documentid);
			throw new Exception(" BusinessTest failed: Question without CustomerData can not start CancellationProcess: " + question.getId() + " docId:" + documentid + " Please Reindex the Question.");
		} else {
			if (archiveMetaData.isMetadataComplete(metaMap, questionId)) {
				metaMap.put(MxOutboundIntegration.PROCSTATUS, MxOutboundIntegration.MXOUT_STATUS.PREPARATION.name());
				SkyLogger.getMediatrixLogger().debug(logPrefix + " ##quickActionCancellation## docid:" + documentid + " Preparation ");

				Date cancelDate = DocContainerUtils.getCreationDate(question);
				if (cancelDate == null) {
					SkyLogger.getMediatrixLogger().warn(logPrefix + "TAGMATCH:CREATIONDATE not set for question:" + questionId);
					cancelDate = DocContainerUtils.getIncommingDate(question);
				}

				if (cancelDate == null) {
					SkyLogger.getMediatrixLogger().warn(logPrefix + "TAGMATCH:INCOMINGDATE not set for question:" + questionId);
					throw new Exception("Das Pflichtattribut INCOMINGDATE ist nicht gesetzt für die Frage:" + questionId + " docId:" + documentid);
				}

				// CR04
				Date possibleCancellationDate = null;
				String possibleCancellationDateS = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.META_POSSIBLE_CANCELATION_DATE);
				if(possibleCancellationDateS==null) {

//                  SIT-18-07-004: "Anpassung von Feld ‚PossibleCancellationDate‘ in IF3.2" / Feasibility Check
//                  ROTHJA - 20180704
					String customerId = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.CUSTOMER_ID);
					String contractNumber = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.TAGMATCH_CONTRACT_NUMBER);

					if (customerId!=null && contractNumber != null) {
						INewDB newDb = ConnectorFactory.getNewDBInstance();
						IndexingCustomer indexingCustomer = new IndexingCustomer();

						indexingCustomer.setNumber(customerId);
						if (contractNumber != null) {
							indexingCustomer.setSelectedContractNumber(contractNumber);
						}
						SkyLogger.getMediatrixLogger().debug(logPrefix + "TAGMATCH.CustomerID: " + customerId + " and TAGMATCH.ContractID: " + contractNumber + " for question:" + questionId);
						TextblockCustomer textblockCustomer = (TextblockCustomer) newDb
								.queryTextblockCustomer(indexingCustomer);
						possibleCancellationDate = textblockCustomer.getPossibleCancelationDate();

 					}

 					if (possibleCancellationDate == null) {
						SkyLogger.getMediatrixLogger().error(logPrefix + " ##quickActionCancellation## FAILED to read PossibleCancellationDate from question " + questionId + " docId:" + documentid);
						throw new Exception("Das Pflichtattribut POSSIBLE_CANCELATION_DATE ist nicht gesetzt für Frage:" + questionId + " docId:" + documentid+"\n Automatisiertes Starten des Kündigungsprozesses nicht möglich.");
						//return false;
					}
				}
				else {
					possibleCancellationDate = parseMXFormatedDate(possibleCancellationDateS);
				}

				String reasonCode = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.CANCELLATION_REASON_CODE);
				if (reasonCode == null) {
					// Anforderung von Dorina 3.12.2015
					reasonCode = "OHNE GRUNDANGABE_K";
					headers = TagMatchDefinitions.addOrReplaceXTMHeader(headers, TagMatchDefinitions.CANCELLATION_REASON_CODE, reasonCode);

					//SkyLogger.getMediatrixLogger().warn(logPrefix + "TAGMATCH:CANCELLATION_REASON_CODE : String not set for question:" + questionId);
					//throw new Exception("Mandatory Attribute CANCELLATION_REASON_CODE is not set for question:" + questionId + " docId:" + documentid);
				}

				// CR04 requirement 7
				String reasonText = "MXauto";
				if(isManualProcess) {
					reasonText = "MXsemi_"+loginname;
				}
				else {
						String isWebform = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.WEBFORM_CANCELLATION);
						if (question.getType() == Email.TYPE_EMAIL && isWebform != null && isWebform.equals("true")) {
							reasonText = "MXautoweb_" + TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.CANCELLATION_REASON_FREE_TEXT);
						}
				}
//				SkyLogger.getMediatrixLogger().info(logPrefix + " ##quickActionCancellation## reasonText: " + reasonText);

				question.setHeaders(headers);
				long mx2cxid = MxOutboundIntegration.addCxOutboundDocumentProcessToDBQueue(con, metaMap, "sky");

//				SkyLogger.getMediatrixLogger().debug(logPrefix + " ##quickActionCancellation## docid:" + documentid + " PreDBInsert: " + mx2cxid +
//						" d:" + documentid + " cdate:" + cancelDate + " rcode:" + reasonCode + " rtext:" + reasonText + " pdate:"+possibleCancellationDate);
				if (mx2cxid < 1) {
					SkyLogger.getMediatrixLogger().error(logPrefix + " ##quickActionCancellation## FAILED to fill ntt_mx_outbound_queue! docid:" + documentid + " PreDBInsert: " + mx2cxid +
							" d:" + documentid + " cdate:" + cancelDate + " rcode:" + reasonCode + " rtext:" + reasonText + " pDate:"+possibleCancellationDate);

					return false;
				}
				PreparedStatement pstmt = null;
				ResultSet rs=null;
				try {
					pstmt = con.prepareStatement(MXOUTBOUND_CANCELPARAMS_CHECK);
					pstmt.setLong(1, mx2cxid);
					rs = pstmt.executeQuery();
					if (rs.next()) {
						//update
						rs.close();
						pstmt.close();
						pstmt = con.prepareStatement(MXOUTBOUND_CANCELPARAMS_UPDATE);
						pstmt.setString(1, documentid);
						pstmt.setDate(2, new java.sql.Date(cancelDate.getTime()));
						pstmt.setString(3, reasonCode);
						pstmt.setString(4, reasonText != null ? (reasonText.length() > 3900 ? reasonText.substring(0, 3899) : reasonText) : "");
						pstmt.setDate(5, new java.sql.Date(possibleCancellationDate.getTime()));
						pstmt.setLong(6, mx2cxid);
						pstmt.execute();
						if (!con.getAutoCommit()) {
							con.commit();
						}
					}else{
						//insert
						rs.close();
						pstmt.close();

						pstmt = con.prepareStatement(MXOUTBOUND_CANCELPARAMS_INSERT);
						pstmt.setLong(1, mx2cxid);
						pstmt.setString(2, documentid);
						pstmt.setDate(3, new java.sql.Date(cancelDate.getTime()));
						pstmt.setString(4, reasonCode);
						pstmt.setString(5, reasonText != null ? (reasonText.length() > 3900 ? reasonText.substring(0, 3899) : reasonText) : "");
						pstmt.setDate(6, new java.sql.Date(possibleCancellationDate.getTime()));
						pstmt.execute();
						if (!con.getAutoCommit()) {
							con.commit();
						}
					}
				} finally {
					try {
						if (rs != null) {
							rs.close();
						}
						if (pstmt != null) {
							pstmt.close();
						}
					} catch (SQLException e) {
						SkyLogger.getCommonLogger().error(logPrefix + ": Problems during release SQL-Res:" + e.getMessage(), e);
					}
				}

				if (!MitarbeiterlogWriter.writeMitarbeiterlog(0, question.getId(), 0, 19, (isManualProcess ? "Semi-Automatically " : "Automatically ") + PARAMETER + " initiated.", System.currentTimeMillis(), false)) {
					SkyLogger.getCommonLogger().error(logPrefix + " Unable to write log entry for Question");
				}
				// erst nach den einfügen der zusatzinformationen - freigabe für den CTX-Pooler
				MxOutboundIntegration.updateDbStatusOfCxOutboundDocumentQueue(con, mx2cxid, MxOutboundIntegration.MXOUT_STATUS.WAIT, null);

				SkyLogger.getMediatrixLogger().info(logPrefix + " ##quickActionCancellation## docid:" + documentid + " POSTDBInsert: " + mx2cxid +
						" d:" + documentid + " cdate:" + cancelDate + " rcode:" + reasonCode + " rtext:" + reasonText +" pdate:"+possibleCancellationDate);
				SkyRule.autoClose(con, question, Question.S_MONITORED);

				return true;
			} else {
				metaMap.put(MxOutboundIntegration.PROCSTATUS, MxOutboundIntegration.MXOUT_STATUS.METAERR.name());
				SkyLogger.getMediatrixLogger().error(logPrefix + " ##quickActionCancellation## e.id:" + " Metadata are not complete:" + question.getId() + " documentid:" + documentid + " pInput:" + formatMap(metaMap));
				MxOutboundIntegration.addCxOutboundDocumentProcessToDBQueue(con, metaMap, "sky");
				throw new Exception("Metadata are not complete. Question:" + questionId + " docId:" + documentid);
			}
		}
	}

	protected static final String mxDateFormatPattern = "yyyy.MM.dd"; //05/08/2015
	private static synchronized String getMxFormatedDate(java.util.Date tsdate) {
		return (new SimpleDateFormat(mxDateFormatPattern)).format(tsdate);
	}

	private static synchronized java.util.Date parseMXFormatedDate(String date){
		try {
			return	(new SimpleDateFormat(mxDateFormatPattern)).parse(date);
		} catch (ParseException e) {
			SkyLogger.getItyxLogger().error("Problem during Parsing ArchiveDate: " + date, e);
			return null;
		}
	}

	private String formatMap(Map<String, String> inputMap) {
		StringBuilder answer = new StringBuilder();
		for (Map.Entry<String, String> it : inputMap.entrySet()) {
			answer.append(it.getKey()).append(":").append(it.getValue()).append("; ");
		}
		return answer.toString();
	}
}
