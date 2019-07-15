package com.nttdata.de.sky.outbound;

import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.contex.interfaces.designer.IFlowObject;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MxOutboundIntegration {

	public static final String PROCSTATUS = "PROCSTATUS";
	public static final String DOCPOOL_PARAMETER = "output";
	public static final String MX2CX_MAXCOUNT = "mx2cx_maxcount";
	public static final String MX2CX_ID = "mx2cxId";
	public static final String OUTBOUND_PROCESS = "820_Outbound";
	public static final String SBS_OUTBOUND_PROCESS = "SBS_820_Outbound";
	public static final String SCS_CANCELLATION_PROCESS = "840_Cancellation";

	private static final String MXOUTBOUND_INSERT = "insert into NTT_MX_OUTBOUND_QUEUE " + "( ID, CREATED, STATUS, DOCUMENT_ID, QUESTION_ID, ANSWER_ID, EMAIL_ID, ACTIVITY_ID, CONTACT_ID, CUSTOMER_ID, CONTRACT_NUMBER, CHANNEL, DIRECTION, TP_NAME, PROCESS, MASTER, FORMTYPE) " + " values (?,SYSDATE,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	private static final String CXOUTBOUND_INSERT = "insert into NTT_MX_OUTBOUND_QUEUE " + "( ID, CREATED, STATUS, DOCUMENT_ID, QUESTION_ID, ANSWER_ID, EMAIL_ID, ACTIVITY_ID, CONTACT_ID, CUSTOMER_ID, CONTRACT_NUMBER, CHANNEL, DIRECTION, TP_NAME, PROCESS, MASTER, FORMTYPE, PROCESSED) " + " values (?,SYSDATE,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,SYSDATE)";

	private static final String MXOUTBOUND_UPDATE = "update NTT_MX_OUTBOUND_QUEUE set CREATED=sysdate, STATUS=?, DOCUMENT_ID=?, QUESTION_ID=?, ANSWER_ID=?, EMAIL_ID=?, ACTIVITY_ID=?, CONTACT_ID=?, CUSTOMER_ID=?, CONTRACT_NUMBER=?, CHANNEL=?, DIRECTION=?, TP_NAME=?, PROCESS=?, MASTER=?, FORMTYPE=? " +
			" where id =?";

	private static final String CXOUTBOUND_UPDATE = "update NTT_MX_OUTBOUND_QUEUE set CREATED=sysdate, STATUS=?, DOCUMENT_ID=?, QUESTION_ID=?, ANSWER_ID=?, EMAIL_ID=?, ACTIVITY_ID=?, CONTACT_ID=?, CUSTOMER_ID=?, CONTRACT_NUMBER=?, CHANNEL=?, DIRECTION=?, TP_NAME=?, PROCESS=?, MASTER=?, FORMTYPE=? " +
			" PROCESSED=sysdate "+
			" where id =?";

	// Der Workaround mit der Subquery ist noetig, weil in v2.1.49 gibt es einen Bug der in 809-prozess sichtbar wurde
	// Wenn man in einem Prozesslauf in der der Schleife ein Docpool-Dokument rausschreibt welches zwei indentische CollectionID's hat
	// wird das zweite Dokument ohne Vorwarnung "gefressen"
	private static final String MXOUTBOUND_GETBUNDLE =
			"SELECT ID,DOCUMENT_ID,QUESTION_ID,ANSWER_ID,EMAIL_ID,ACTIVITY_ID,CONTACT_ID,CUSTOMER_ID,CONTRACT_NUMBER,CHANNEL,DIRECTION,TP_NAME,PROCESS,MASTER,FORMTYPE "+
			" from NTT_MX_OUTBOUND_QUEUE " +
			" where ID in ("+
				"SELECT MIN(ID) "+
					" FROM (select ID, DOCUMENT_ID, CREATED from NTT_MX_OUTBOUND_QUEUE where STATUS = ? and process = ? and master = ? and 0 < QUESTION_ID and created < sysdate - interval '2' minute ORDER BY CREATED ASC)"+
					" WHERE ROWNUM <= ?"+
					" GROUP BY DOCUMENT_ID"+
			") order by CREATED asc";


	private static final String MXOUTBOUND_GETQUEUECOUNT = "SELECT count(*) from NTT_MX_OUTBOUND_QUEUE where STATUS = 'WAIT' and process = ? and master=? ";

	private static final String MXOUTBOUND_CHECKDOUBLES = "SELECT count(*), max(id) from NTT_MX_OUTBOUND_QUEUE where created>sysdate - interval '3' day and  DOCUMENT_ID=? and QUESTION_ID=? and ANSWER_ID=? and EMAIL_ID=? and DIRECTION=? and PROCESS=? and master=? and customer_id=? and contract_number=?";

	private static final String MXOUTBOUND_UPDATE_STATUS = "UPDATE NTT_MX_OUTBOUND_QUEUE set  PROCESSED = SYSDATE,  STATUS = ?, CLUSTERNODE = ?, ERRORMSG=? where ID = ?";

	private static final String MXOUTBOUND_UPDATE_CXPROCESSED = "UPDATE NTT_MX_OUTBOUND_QUEUE set CXFINISHED = SYSDATE, CLUSTERNODE = ?, PROCESSED = SYSDATE,  STATUS = ? , ERRORMSG=? where ID = ? ";

	private static final String NEXTVAL_MX2CX_STMT = "select SEQNTT_MX2CX_REQ.nextval from dual";
	private static String LHostname;

	public enum MXOUT_DIRECTION {

		INBOUND, OUTBOUND
	}

	public enum MXOUT_STATUS {

		PREPARATION, WAIT, RUNNING, PROCESSED, PROCERR, METAERR, CXPROCESSED, WAITFORCB, CBERROR
	}

	public static long addCxOutboundDocumentProcessToDBQueue(Connection con, Map<String, String> inputMap, final String masterName) throws RemoteException {
		long acurrentTimeMillis = System.currentTimeMillis();

		String documentId = inputMap.get(TagMatchDefinitions.DOCUMENT_ID);
		Long questionId = getLongMapMember(inputMap, TagMatchDefinitions.MX_QUESTIONID);
		Long answerId = getLongMapMember(inputMap, TagMatchDefinitions.MX_ANSWERID);
		Long emailId = getLongMapMember(inputMap, TagMatchDefinitions.MX_EMAILID);
		String dir = getStringMapMember(inputMap, TagMatchDefinitions.MX_DIRECTION);
		String istatus = getStringMapMember(inputMap, PROCSTATUS);
		String formtype = getStringMapMember(inputMap, TagMatchDefinitions.FORM_TYPE_CATEGORY);
		MXOUT_STATUS status = MXOUT_STATUS.WAIT;
		if (istatus == null || istatus.isEmpty()) {
			status = MXOUT_STATUS.WAIT;
		} else if (istatus.equalsIgnoreCase(MXOUT_STATUS.WAIT.name())) {
			status = MXOUT_STATUS.WAIT;
		} else if (istatus.equalsIgnoreCase(MXOUT_STATUS.RUNNING.name())) {
			status = MXOUT_STATUS.RUNNING;
		} else if (istatus.equalsIgnoreCase(MXOUT_STATUS.PROCESSED.name())) {
			status = MXOUT_STATUS.PROCESSED;
		} else if (istatus.equalsIgnoreCase(MXOUT_STATUS.CXPROCESSED.name())) {
			status = MXOUT_STATUS.CXPROCESSED;
		} else if (istatus.equalsIgnoreCase(MXOUT_STATUS.PROCERR.name())) {
			status = MXOUT_STATUS.PROCERR;
		} else if (istatus.equalsIgnoreCase(MXOUT_STATUS.METAERR.name())) {
			status = MXOUT_STATUS.METAERR;
		}
		MXOUT_DIRECTION direction = MXOUT_DIRECTION.INBOUND;
		if (dir == null || dir.isEmpty()) {
			direction = MXOUT_DIRECTION.INBOUND;
		} else if (dir.equalsIgnoreCase(MXOUT_DIRECTION.INBOUND.name())) {
			direction = MXOUT_DIRECTION.INBOUND;
		} else if (dir.equalsIgnoreCase(MXOUT_DIRECTION.OUTBOUND.name())) {
			direction = MXOUT_DIRECTION.OUTBOUND;
		}
		String process = getStringMapMember(inputMap, DOCPOOL_PARAMETER);
		SkyLogger.getCommonLogger().debug("OuboundCX preparedAttrib docId: " + documentId + " process:" + process + " q:" + questionId + " a:" + answerId + " e:" + emailId);
		ResultSet rs = null;
		PreparedStatement pst = null;
		long mxcxid = 0;
		long counter = 0;
		try {
			if (con != null) { //&& con.isValid(120)
				
				pst = con.prepareStatement(MXOUTBOUND_CHECKDOUBLES);
				pst.setString(1, documentId);
				pst.setLong(2, questionId);
				pst.setLong(3, answerId);
				pst.setLong(4, emailId);
				pst.setString(5, direction.name());
				pst.setString(6, process);
				pst.setString(7, masterName);
				pst.setString(8, getStringMapMember(inputMap, TagMatchDefinitions.CUSTOMER_ID));
				pst.setLong(9, getLongMapMember(inputMap, TagMatchDefinitions.TAGMATCH_CONTRACT_NUMBER));
				
				rs = pst.executeQuery();
				if (rs.next()) {
					counter = rs.getInt(1);
					mxcxid = rs.getLong(2);
				}
				rs.close();
				pst.close();
				
				if(counter > 0 && (//process.equalsIgnoreCase(SCS_CANCELLATION_PROCESS) ||
						process.equalsIgnoreCase(OUTBOUND_PROCESS) ||process.equalsIgnoreCase( SBS_OUTBOUND_PROCESS))){
					SkyLogger.getCommonLogger().warn("checkDoublesOuboundCX skipDouble docId: " + documentId + " process:" + process + " q:" + questionId + " a:" + answerId + " e:" + emailId + " id:" + mxcxid);
				}else if (counter > 0) {
					SkyLogger.getCommonLogger().warn("checkDoublesOuboundCX skipDouble docId: " + documentId + " process:" + process + " q:" + questionId + " a:" + answerId + " e:" + emailId + " id:" + mxcxid);

					if (status == MXOUT_STATUS.CXPROCESSED) {
						pst = con.prepareStatement(CXOUTBOUND_UPDATE);
					} else {
						pst = con.prepareStatement(MXOUTBOUND_UPDATE);
					}
					pst.setString(1, status.name());
					pst.setString(2, documentId);
					pst.setLong(3, questionId);
					pst.setLong(4, answerId);
					pst.setLong(5, emailId);
					pst.setString(6, getStringMapMember(inputMap, TagMatchDefinitions.ACTIVITY_ID));
					pst.setString(7, getStringMapMember(inputMap, TagMatchDefinitions.CONTACT_ID));
					pst.setString(8, getStringMapMember(inputMap, TagMatchDefinitions.CUSTOMER_ID));
					pst.setLong(9, getLongMapMember(inputMap, TagMatchDefinitions.TAGMATCH_CONTRACT_NUMBER));
					pst.setString(10, getStringMapMember(inputMap, TagMatchDefinitions.CHANNEL));
					pst.setString(11, direction.name());
					pst.setString(12, getStringMapMember(inputMap, TagMatchDefinitions.MX_TP_NAME));
					pst.setString(13, process);
					pst.setString(14, masterName);
					pst.setString(15, formtype);
					pst.setLong(16, mxcxid);
					pst.execute();
				} else {
					pst = con.prepareStatement(NEXTVAL_MX2CX_STMT);
					rs = pst.executeQuery();
					if (rs.next()) {
						mxcxid = rs.getLong(1);
					}

					if (status == MXOUT_STATUS.CXPROCESSED) {
						pst = con.prepareStatement(CXOUTBOUND_INSERT);
					} else {
						pst = con.prepareStatement(MXOUTBOUND_INSERT);
					}
					pst.setLong(1, mxcxid);
					pst.setString(2, status.name());
					pst.setString(3, documentId);
					pst.setLong(4, questionId);
					pst.setLong(5, answerId);
					pst.setLong(6, emailId);
					pst.setString(7, getStringMapMember(inputMap, TagMatchDefinitions.ACTIVITY_ID));
					pst.setString(8, getStringMapMember(inputMap, TagMatchDefinitions.CONTACT_ID));
					pst.setString(9, getStringMapMember(inputMap, TagMatchDefinitions.CUSTOMER_ID));
					pst.setLong(10, getLongMapMember(inputMap, TagMatchDefinitions.TAGMATCH_CONTRACT_NUMBER));
					pst.setString(11, getStringMapMember(inputMap, TagMatchDefinitions.CHANNEL));
					pst.setString(12, direction.name());
					pst.setString(13, getStringMapMember(inputMap, TagMatchDefinitions.MX_TP_NAME));
					pst.setString(14, process);
					pst.setString(15, masterName);
					pst.setString(16, formtype);
					pst.execute();
				}
				if (!con.getAutoCommit()) {
					con.commit();
				}
			} else {
				SkyLogger.getCommonLogger().error("OuboundCX DB-Connection not available DocId: " + documentId);
				throw new SQLException("OuboundCX " + documentId + " DB-Connection not available.");
			}
		} catch (SQLException e) {
			SkyLogger.getCommonLogger().error("OuboundCX Outbound/Archiving not possible. DocId: " + documentId + " Problem:" + e.getMessage());
			throw new RemoteException("OuboundCX Outbound/Archiving not possible. DocId: " + documentId + " Problem:" + e.getMessage());
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pst != null) {
					pst.close();
				}
			} catch (SQLException e) {
				SkyLogger.getCommonLogger().warn("OuboundCX: Problems during release SQL-Res:" + e.getMessage(), e);
			}
		}
		SkyLogger.getMediatrixLogger().info("MXOutboundIntegration: Funktion addCxOutboundDocumentProcessToDBQueue took " + (System.currentTimeMillis() - acurrentTimeMillis) + " " + documentId + " " + questionId);

		return mxcxid;
	}

	public static int getCxOutboundDocumentProcessesCountFromDB(Connection con, IFlowObject flowObject, String processname, String master) throws Exception {
		int result = 0;

		PreparedStatement getStmt = null;
		ResultSet rs = null;
		try {
			if (con != null) { //&& con.isValid(120)
				getStmt = con.prepareStatement(MXOUTBOUND_GETQUEUECOUNT);
				getStmt.setString(1, processname);
				getStmt.setString(2, master);

				rs = getStmt.executeQuery();
				while (rs.next()) {
					result = rs.getInt(1);
				}
			} else {
				SkyLogger.getCommonLogger().error("OuboundCX DB-Connection not available:" + processname);
				throw new SQLException("OuboundCX  DB-Connection not available." + processname);
			}
		} catch (SQLException e) {
			SkyLogger.getCommonLogger().error(" " + MXOUTBOUND_GETQUEUECOUNT + " p:" + processname + " :: " + e, e);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (getStmt != null) {
					getStmt.close();
				}
			} catch (SQLException e) {
				SkyLogger.getCommonLogger().error("OuboundCX SQL; " + " p:" + processname + e, e);
			}
		}
		return result;
	}

	public static List<Map<String, Object>> getCxOutboundDocumentProcessesFromDB(Connection con, IFlowObject flowObject, String processname, String master, int maxitems) throws Exception {
		// String logPrefix = getClass().getName() + "#" + new Object() {}.getClass().getEnclosingMethod().getName() + ":";

		//SkyLogger.getCommonLogger().debug(logPrefix+" started...");
		
		
		List<Map<String, Object>> docsMetaContainer = new LinkedList<Map<String, Object>>();
		PreparedStatement getStmt = null;
		ResultSet rs = null;
		try {
			if (con != null) { //&& con.isValid(120)
				getStmt = con.prepareStatement(MXOUTBOUND_GETBUNDLE);
				getStmt.setString(1, MXOUT_STATUS.WAIT.name());
				getStmt.setString(2, processname);
				getStmt.setString(3, master);
				getStmt.setInt(4, maxitems);

				rs = getStmt.executeQuery();

				while (rs.next()) {
					Map<String, Object> docMeta = new HashMap<String, Object>();
					docMeta.put(MX2CX_ID, rs.getLong(1));
					docMeta.put(TagMatchDefinitions.DOCUMENT_ID,              "" + rs.getString(2));
					docMeta.put(TagMatchDefinitions.MX_QUESTIONID,            "" + rs.getLong(3));
					docMeta.put(TagMatchDefinitions.MX_ANSWERID,              "" + rs.getLong(4));
					docMeta.put(TagMatchDefinitions.MX_EMAILID,               "" + rs.getLong(5));
					docMeta.put(TagMatchDefinitions.ACTIVITY_ID,                   rs.getString(6));
					docMeta.put(TagMatchDefinitions.CONTACT_ID,                    rs.getString(7));
					docMeta.put(TagMatchDefinitions.CUSTOMER_ID,                   rs.getString(8));
					docMeta.put(TagMatchDefinitions.TAGMATCH_CONTRACT_NUMBER, "" + rs.getLong(9));
					docMeta.put(TagMatchDefinitions.CHANNEL,                       rs.getString(10));
					// DIRECTION,
					String intDir = rs.getString(11);

					if (intDir.equalsIgnoreCase(MXOUT_DIRECTION.INBOUND.name())) {
						docMeta.put(TagMatchDefinitions.MX_DIRECTION, MXOUT_DIRECTION.INBOUND.name());
					} else if (intDir.equalsIgnoreCase(MXOUT_DIRECTION.OUTBOUND.name())) {
						docMeta.put(TagMatchDefinitions.MX_DIRECTION, MXOUT_DIRECTION.OUTBOUND.name());
					}
					docMeta.put(TagMatchDefinitions.MX_TP_NAME, rs.getString(12));
					docMeta.put(DOCPOOL_PARAMETER, rs.getString(13));
					docMeta.put(TagMatchDefinitions.MX_MASTER, rs.getString(14));
					docMeta.put(TagMatchDefinitions.FORM_TYPE_CATEGORY, rs.getString(15));
					SkyLogger.getCommonLogger().info("documentId: " + rs.getString(2) + " mxcx_queue_id:" + rs.getLong(1));
					docsMetaContainer.add(docMeta);

				}
			} else {

				SkyLogger.getCommonLogger().error("OuboundCX DB-Connection not available:");
				throw new SQLException("OuboundCX  DB-Connection not available.");

			}
		} catch (SQLException e) {
			SkyLogger.getCommonLogger().error(" " + MXOUTBOUND_GETBUNDLE + " :: " + e, e);
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
		return docsMetaContainer;
	}

	public static void updateDbStatusOfCxOutboundDocumentQueue(Connection con, long logId, MXOUT_STATUS status, String errormsg) throws Exception {

		PreparedStatement statement = null;
		try {
			if (con != null) { //&& con.isValid(120)
				statement = con.prepareStatement(MXOUTBOUND_UPDATE_STATUS);
				statement.setString(1, status.name());
				statement.setString(2, MxOutboundIntegration.getClusternode());
				statement.setString(3, (errormsg == null || errormsg.isEmpty()) ? null : (errormsg.length() > 250 ? errormsg.substring(0, 249) : errormsg));
				statement.setLong(4, logId);
				statement.execute();
				if (!con.getAutoCommit()) {
					con.commit();
				}
				SkyLogger.getCommonLogger().debug("status changed: " + logId + ", " + status);
			} else {
				SkyLogger.getCommonLogger().error("OuboundCX DB-Connection not available:" + logId);
				throw new SQLException("OuboundCX " + logId + " DB-Connection not available.");
			}
		} catch (SQLException e) {
			SkyLogger.getCommonLogger().error(e.getMessage());
			throw e;
		} finally {
			if (statement != null) {
				statement.close();
			}
		}
	}

	public static synchronized String getClusternode() {
		if (LHostname == null) {
			try {
				LHostname = InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException e) {
				LHostname = "localhost";
			}
		}
		return LHostname;
	}

	public static void updateContexProcessedAttribute(Connection con, long logId, MXOUT_STATUS status) throws Exception {
		updateContexProcessedAttribute(con, logId, status, null);
	}

	public static void updateContexProcessedAttribute(Connection con, long logId, MXOUT_STATUS status, String errormsg) throws Exception {

		PreparedStatement statement = null;
		try {
			if (con != null) { //&& con.isValid(120)
				statement = con.prepareStatement(MXOUTBOUND_UPDATE_CXPROCESSED);
				statement.setString(1, MxOutboundIntegration.getClusternode());
				statement.setString(2, status.name());
				statement.setString(3, (errormsg == null || errormsg.isEmpty()) ? null : (errormsg.length() > 250 ? errormsg.substring(0, 249) : errormsg));
				statement.setLong(4, logId);
				statement.execute();
				if (!con.getAutoCommit()) {
					con.commit();
				}
				SkyLogger.getCommonLogger().debug("updateContexProcessedAttribute: " + logId);
			} else {
				SkyLogger.getCommonLogger().error("OuboundCX DB-Connection not available:" + logId);
				throw new SQLException("OuboundCX " + logId + " DB-Connection not available.");
			}
		} catch (SQLException e) {
			SkyLogger.getCommonLogger().error(e.getMessage());
			throw e;
		} finally {
			if (statement != null) {
				statement.close();
			}
		}
	}


	private static String getStringMapMember(Map<String, String> inputMap, String key) {
		String val = null;
		if (inputMap.containsKey(key)) {
			val = inputMap.get(key);
		}
		return val;
	}

	private static long getLongMapMember(Map<String, String> inputMap, String key) {
		long val = 0;
		if (inputMap.containsKey(key)) {
			try {
				String parsev = inputMap.get(key);
				if (parsev == null || parsev.isEmpty()) {
					val = 0;
				} else {
					val = Long.parseLong(parsev);
				}
			} catch (NumberFormatException e) {
				SkyLogger.getCommonLogger().error("Problem during parsing toLong: " + key + ":" + inputMap.get(key) + " e:" + e.getMessage());
			}
		}
		return val;
	}

	private static int getIntMapMember(Map<String, String> inputMap, String key) {
		int val = 0;
		if (inputMap.containsKey(key)) {
			try {
				val = Integer.parseInt(inputMap.get(key));
			} catch (NumberFormatException e) {
				SkyLogger.getCommonLogger().error("Problem during parsing toLong: " + key + ":" + inputMap.get(key) + " e:" + e.getMessage());
			}
		}
		return val;
	}
}
