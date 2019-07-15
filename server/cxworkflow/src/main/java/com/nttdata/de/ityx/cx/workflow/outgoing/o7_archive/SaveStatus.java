package com.nttdata.de.ityx.cx.workflow.outgoing.o7_archive;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.contex.impl.designer.state.KeyConfiguration;
import de.ityx.contex.impl.designer.state.StateResult;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.interfaces.designer.IExflowState;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.document.CDocument;
import oracle.jdbc.pool.OracleConnectionPoolDataSource;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SaveStatus extends AbstractWflBean {

	public void execute(IFlowObject flowObject) {
		SkyLogger.getItyxLogger().debug(
				"Mx2CxSaveStatusBean.execute() started...");
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name + ":";
		SkyLogger.getItyxLogger().debug(logPrefix + "enter");
		Map<String, String> mx2cxStatuses = (Map<String, String>) flowObject
				.get("mx2cxStatuses");
		String currentStatusId = (String) flowObject.get("mx2cxId");
		String errorStatus = (String) flowObject.get(MX2CX_ERROR);
		int intErrorStatus = STATUS_OK;
		if (errorStatus.equals("1")) {
			intErrorStatus = STATUS_ERROR;
		}
		Connection conn = null;
		try {
			conn = getConnection(getDBUrlString(flowObject));
			saveStatus2Mx(conn, currentStatusId, intErrorStatus);
			conn.commit();
		} catch (Exception e) {
			SkyLogger.getItyxLogger().error("MX2CXSaveStatuses SQL; " + e, e);
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				SkyLogger.getItyxLogger().error(
						"Problem during closing connection; " + e, e);
			}
		}
	}

	protected final String logPreafix = getClass().getName();
	// protected Logger log=SkyLogger.getItyxLogger();
	private Map<Long, String> parameterMap = new ConcurrentHashMap<>();
	private final String MX_DBURL = "mx_dburl";
	private final String MX_DBHOST = "mx_dbhost";
	private final String MX_DBPORT = "mx_dbport";
	private final String MX_DBNAME = "mx_dbname";
	private final String MX_DBUSER = "mx_dbuser";
	private final String MX_DBPASS = "mx_dbpass";

	protected static final int STATUS_NEW = 10;
	protected static final int STATUS_WORK = 20;
	protected static final int STATUS_ERROR = 50;
	protected static final int STATUS_OK = 30;
	protected static final int DIR_IN = 10;
	protected static final int DIR_OUT = 20;

	private final String MX2CX_MAXCOUNT = "mx2cx_maxcount";
	protected final String MODEL_MAP = "modelMap";
	protected final String REQ_DOCS = "reqDocs";
	protected final String DOCUMENTS_COUNT = "DocumentCount";
	protected final String OUTPUT = "output";
	protected final String MX2CX_ERROR = "Error";

	public String getDBUrlString(IFlowObject flowObject) {
		String dburl = (String) flowObject.get(MX_DBURL);
		if (dburl != null && dburl.isEmpty()) {
			return dburl;
		}
		String dbhost = (String) flowObject.get(MX_DBHOST);
		String dbport = "" + flowObject.get(MX_DBPORT);
		String dbname = (String) flowObject.get(MX_DBNAME);
		String dbuser = (String) flowObject.get(MX_DBUSER);
		String dbpass = (String) flowObject.get(MX_DBPASS);
		// jdbc:oracle:thin:[USER/PASSWORD]@[HOST][:PORT]:SID
		// jdbc:oracle:thin:[USER/PASSWORD]@//[HOST][:PORT]/SERVICE
		return "jdbc:oracle:thin:" + dbuser + "/" + dbpass + "@" + dbhost + ":"
				+ dbport + "/" + dbname;

	}

	public Connection getConnection(String urlString) {
		Connection con = null;
		SkyLogger.getItyxLogger().debug("try to connect to MX DB...");
		OracleConnectionPoolDataSource ds = null;
		try {
			ds = new OracleConnectionPoolDataSource();
			ds.setURL(urlString);
			SkyLogger.getItyxLogger().debug(
					"try to connect with DB: " + urlString);
			con = ds.getConnection();
		} catch (SQLException e) {
			SkyLogger.getItyxLogger().error("MxDbSingleton.connect(): " + e, e);
		}
		return con;
	}

	protected int mx2cxMaxCount(IFlowObject flowObject) {
		return (Integer) flowObject.get(MX2CX_MAXCOUNT);
	}

	protected void saveStatus2Mx(Connection con, String id, int status)
			throws Exception {
		SkyLogger.getItyxLogger().debug("saveStatus2MX() started...");
		String updateMX2CXSQL = "UPDATE NTT_MX_OUTBOUND set STATUS = '"
				+ status + "', PROCESSED = SYSDATE where ID = " + id;
		SkyLogger.getItyxLogger().debug("SQL: " + updateMX2CXSQL);
		Statement statement = null;

		try {
			statement = con.createStatement();
			statement.execute(updateMX2CXSQL);
			SkyLogger.getItyxLogger().debug(
					"status changed: " + id + ", " + status);
		} catch (SQLException e) {
			SkyLogger.getItyxLogger().error(e.getMessage());
			throw e;
		} finally {
			if (statement != null) {
				statement.close();
			}
		}

	}

	/**
	 * Base class Methods
	 */

	public StateResult pexecute(int arg0, IFlowObject flowObject,
			IExflowState arg2) throws Exception {
		String docid = getDocID(flowObject);
		String processname = getProcessname(arg2);
		try {
			if (docid != null) {
				parameterMap.put(Thread.currentThread().getId(), processname
						+ ": " + docid);
			}
			execute(flowObject);
		} catch (Exception e) {
			e.printStackTrace();
			SkyLogger.getItyxLogger().error(
					logPreafix + ":ERROR: " + processname + ": " + docid
							+ " Message:" + e.getMessage(), e);
			return StateResult.exception(e.getMessage());
		}
		return StateResult.STATEOK;
	}

	@Override
	public void abortExecute() {
		long tid = Thread.currentThread().getId();
		String docid = parameterMap.get(tid);
		Logger log = SkyLogger.getItyxLogger();
		log.error(logPreafix + " ABORT EXECUTE for:" + docid + " threadID:"
				+ tid);
	}

	@Override
	public void rollbackExecute() {
		long tid = Thread.currentThread().getId();
		String docid = parameterMap.get(tid);
		Logger log = SkyLogger.getItyxLogger();
		log.error(logPreafix + " ROLLBACK EXECUTE for:" + docid + " threadID:"
				+ tid);

	}

	@Override
	public void cleanState() {
		throw new UnsupportedOperationException("Not supported yet."); // To
																		// change
																		// body
																		// of
																		// generated
																		// methods,
																		// choose
																		// Tools
																		// |
																		// Templates.
	}

	@Override
	public void prepareForCluster(String string) {
		throw new UnsupportedOperationException("Not supported yet."); // To
																		// change
																		// body
																		// of
																		// generated
																		// methods,
																		// choose
																		// Tools
																		// |
																		// Templates.
	}

	@Override
	public void prepareForResumeFromCluster() {
		throw new UnsupportedOperationException("Not supported yet."); // To
																		// change
																		// body
																		// of
																		// generated
																		// methods,
																		// choose
																		// Tools
																		// |
																		// Templates.
	}

	public String getDocID(IFlowObject flowObject) {
		String ctx_documentid = "DocID not found";
		CDocument cdoc = getDoc(flowObject);
		if (cdoc != null) {
			ctx_documentid = (String) cdoc
					.getNote(TagMatchDefinitions.DOCUMENT_ID);
		}
		return ctx_documentid;
	}

	public CDocument getDoc(IFlowObject flow, String flowObjectname) {
		return getDoc(flow, getDocContainer(flow, flowObjectname));
	}

	public CDocument getDoc(IFlowObject flow) {
		return getDoc(flow, getDocContainer(flow));
	}

	public CDocument getDoc(IFlowObject flow, CDocumentContainer cont) {
		if (cont != null) {
			for (int i = 0; i < cont.getDocuments().size(); i++) {
				if (cont.getDocument(i) != null) {
					return cont.getDocument(i);
				}
			}
		}
		return null;
	}

	public CDocumentContainer getDocContainer(IFlowObject flow) {
		return getDocContainer(flow, DocContainerUtils.DOC);
	}

	public CDocumentContainer getDocContainer(IFlowObject flow,
			String flowObjectname) {
		CDocumentContainer result = null;
		Object object = flow.get(flowObjectname);
		if (object != null && object instanceof CDocumentContainer) {
			result = (CDocumentContainer) object;
		}
		return result;
	}

	public String getProcessname(IExflowState exflowstate) {
		if (exflowstate != null && exflowstate.getProcessInfo() != null
				&& exflowstate.getProcessInfo().getProcessTyp() != null) {
			return exflowstate.getProcessInfo().getProcessTyp();
		}
		return null;
	}

	public KeyConfiguration[] getKeys() {
		return null;
	}
	
}
