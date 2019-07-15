package com.nttdata.de.ityx.cx.workflow.outgoing.o7_archive;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.contex.impl.designer.state.KeyConfiguration;
import de.ityx.contex.impl.designer.state.StateResult;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.impl.document.text.string.StringDocument;
import de.ityx.contex.interfaces.designer.IExflowState;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.designer.IParameterMap;
import de.ityx.contex.interfaces.document.CDocument;
import oracle.jdbc.pool.OracleConnectionPoolDataSource;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Wfl_809_Preparation extends AbstractWflBean {

	protected final String		logPreafix							= getClass().getName();
	// protected Logger log=SkyLogger.getItyxLogger();
	private Map<Long, String>	parameterMap						= new ConcurrentHashMap<>();
        private final String MX_DBURL = "mx_dburl";
        private final String MX_DBHOST = "mx_dbhost";
	private final String MX_DBPORT = "mx_dbport";
	private final String MX_DBNAME = "mx_dbname";
	private final String MX_DBUSER = "mx_dbuser";
	private final String MX_DBPASS = "mx_dbpass";
	private final String MX2CX_MAXCOUNT = "mx2cx_maxcount";
	protected final String MODEL_MAP = "modelMap";
	protected final String REQ_DOCS = "reqDocs";
	protected final String DOCUMENTS_COUNT = "DocumentCount";
	protected final String OUTPUT = "output";
	protected final String MX2CX_ERROR = "Error";
	protected static final int STATUS_NEW = 10;
	protected static final int STATUS_WORK = 20;
	protected static final int STATUS_ERROR = 50;
	protected static final int STATUS_OK = 30;
	protected static final int DIR_IN = 10;
	protected static final int DIR_OUT = 20;
	protected String mxDbhost;
	protected String mxDbport;
	protected String mxDbname;
	protected String mxDbuser;
	protected String mxDbpass;


	protected static final String ACTIVITY_ID = "ActivityID";
	protected static final String ANSWER_ID = "ANSWERID";
	protected static final String CHANNEL = "Channel";
	protected static final String CONTACT_ID = "ContactID";
	protected static final String CONTRACT_NUMBER = "ContractNumber";
	protected static final String CUSTOMER_ID = "CustomerID";
	protected static final String DIRECTION = "Direction";
	protected static final String DOCUMENT_ID = "DocumentID";
	protected static final String EMAIL_ID = "EMAILID";
	protected static final String QUESTION_ID = "QUESTIONID";
	protected static final String TP_NAME = "TP_NAME";
	protected static final String MASTER_NAME = "MASTER_NAME";


	public void execute(IFlowObject flowObject) {
        
            Connection conn = null;
		Class classe = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = classe.getName() + "#" + name + ":";
		// Reads process input.
		IParameterMap inputMap = flowObject.getInputMap(MODEL_MAP);
		String inputValues = "";
		for (Iterator it = inputMap.names(); it.hasNext();) {
			inputValues += it.next().toString() + "; ";
		}
		SkyLogger.getItyxLogger().debug(logPrefix + ": " + inputValues);
		
		try {
			//conn = MxDbSingleton.getInstance(mxDbhost, mxDbport, mxDbname, mxDbuser, mxDbpass).getConnection();
			conn=getConnection(getDBUrlString(flowObject));
                        List<CDocumentContainer> reqDocs = new LinkedList<>();
			SkyLogger.getItyxLogger().debug("try to get the MX requests");

			List<Map<String, String>> docs = getMX2CXRequest(conn, getMaxCount(flowObject));
			for (Map<String, String> n : docs) {
				String metadataoutput = "";
				CDocument doc = StringDocument.getInstance(n.get(
						TagMatchDefinitions.DOCUMENT_ID));
				for (String nextField : n.keySet()) {
					doc.setNote(nextField, n.get(nextField));
					metadataoutput += nextField + ": " + n.get(nextField) + "; ";
				}
				SkyLogger.getItyxLogger().debug(
						"next document: " + metadataoutput);
				CDocumentContainer<CDocument> docCon = new CDocumentContainer<>(doc);
				reqDocs.add(docCon);
			}
			flowObject.put(REQ_DOCS, reqDocs);
			flowObject.put(DOCUMENTS_COUNT, docs.size());
			SkyLogger.getItyxLogger().debug("DocumentCount: " + docs.size());

		} catch (Exception e) {
			SkyLogger.getItyxLogger().error("getMX2CXRequest SQL; " + e, e);
		}finally{
                    try{
                        if (conn!=null){
                            conn.close();
                        }
                    }catch(SQLException e){
                        SkyLogger.getItyxLogger().error("Problem during closing connection; " + e, e);
                    }
                }
        }
	
	public KeyConfiguration[] getKeys() {
        return new KeyConfiguration[]{
        		new KeyConfiguration(DOCUMENTS_COUNT,Integer.class),
				new KeyConfiguration(REQ_DOCS,List.class)};
    }
    
	private static final String SELECT_FROM_MX2CX_REQ = "SELECT ID, DOCUMENT_ID, QUESTION_ID, ANSWER_ID, EMAIL_ID, "
			+ "ACTIVITY_ID, CONTACT_ID, CUSTOMER_ID, CONTRACT_NUMBER, CHANNEL, DIRECTION, TP_NAME, "
			+ "PROCESS, MASTER from NTT_MX_OUTBOUND where STATUS = "
			+ STATUS_NEW;

	private List<Map<String, String>> getMX2CXRequest(Connection con, Integer maxCount)
			throws Exception {
		SkyLogger.getItyxLogger().debug(
				"MxDbRequestBean.getMX2CXRequest() started...");
		SkyLogger.getItyxLogger().debug("SQL: " + SELECT_FROM_MX2CX_REQ);
		List<Map<String, String>> liste = new LinkedList<>();
		PreparedStatement getStmt = null;
		ResultSet rs = null;
		try {
			if (con != null && con.isValid(120)) {
				getStmt = con.prepareStatement(SELECT_FROM_MX2CX_REQ);
				rs = getStmt.executeQuery();
				int i = 0;
				while (rs.next() && i < maxCount) {
					
                                        Map<String, String> row = new HashMap<>();
					// ID,
					row.put("mx2cxId", "" + rs.getLong(1));
					// DOCUMENT_ID,
					row.put(DOCUMENT_ID, "" + rs.getString(2));
					// QUESTION_ID,
					row.put(QUESTION_ID, "" + rs.getLong(3));
					// ANSWER_ID,
					row.put(ANSWER_ID, "" + rs.getLong(4));
					// EMAIL_ID,
					row.put(EMAIL_ID, "" + rs.getLong(5));
					// ACTIVITY_ID,
					row.put(ACTIVITY_ID, rs.getString(6));
					// CONTACT_ID,
					row.put(CONTACT_ID, rs.getString(7));
					// CUSTOMER_ID,
					row.put(CUSTOMER_ID, rs.getString(8));
					// CONTRACT_NUMER,
					row.put(CONTRACT_NUMBER, "" + rs.getLong(9));
					// CHANNEL,
					row.put(CHANNEL, rs.getString(10));
					// DIRECTION,
					int intDir = rs.getInt(11);
					if (intDir == DIR_IN) {
						row.put(DIRECTION, "INBOUND");
					} else if (intDir == DIR_OUT) {
						row.put(DIRECTION, "OUTBOUND");
					}
					// TP_NAME,
					row.put(TP_NAME, rs.getString(12));
					// PROCESS,
					row.put(OUTPUT, rs.getString(13));
					// MASTER
					row.put(MASTER_NAME, rs.getString(14));
					SkyLogger.getItyxLogger().info(
							"documentId: " + rs.getLong(1));
					liste.add(row);
					i++;
				}
			}
		} catch (SQLException e) {
			SkyLogger.getItyxLogger().error("getMX2CXRequest SQL; " + e, e);
		} finally {
			try {
				if (rs != null && !rs.isClosed()) {
					rs.close();
				}
				if (getStmt != null && !getStmt.isClosed()) {
					getStmt.close();
				}
			} catch (SQLException e) {
				SkyLogger.getItyxLogger().error("getMX2CXRequest SQL; " + e, e);
			}
		}
		return liste;
	}

        
          public String getDBUrlString(IFlowObject flowObject){
                String  dburl = (String) flowObject.get(MX_DBURL);
		if (dburl!=null &&  dburl.isEmpty()){
                     return dburl;
                }
                String  dbhost = (String) flowObject.get(MX_DBHOST);
		String dbport = "" + flowObject.get(MX_DBPORT);
		String dbname = (String) flowObject.get(MX_DBNAME);
		String dbuser = (String) flowObject.get(MX_DBUSER);
		String dbpass = (String) flowObject.get(MX_DBPASS);
              // jdbc:oracle:thin:[USER/PASSWORD]@[HOST][:PORT]:SID
              // jdbc:oracle:thin:[USER/PASSWORD]@//[HOST][:PORT]/SERVICE
		return "jdbc:oracle:thin:"+dbuser+"/"+dbpass+"@" + dbhost + ":" + dbport + ":" + dbname;
	
    }
    
          
    public  Connection getConnection(String urlString) {
        Connection con=null;
        SkyLogger.getItyxLogger().debug("try to connect to MX DB...");
                OracleConnectionPoolDataSource ds = null;
                try {
                    ds = new OracleConnectionPoolDataSource();
                    ds.setURL(urlString);
                    SkyLogger.getItyxLogger().debug("try to connect with DB: " + urlString);
                    con = ds.getConnection();
                } catch (SQLException e) {
                    SkyLogger.getItyxLogger().error("MxDbSingleton.connect(): " + e, e);
                }            
        return con;
    }
          
    
    
    
    
        
        protected int getMaxCount(IFlowObject flowObject) {
		return (Integer) flowObject.get(MX2CX_MAXCOUNT);
	}

	protected void saveStatus2Mx(Connection con, String id, int status)
			throws Exception {
		SkyLogger.getItyxLogger().debug("saveStatus2MX() started...");
		String updateMX2CXSQL = "UPDATE NTT_MX_OUTBOUND set STATUS = '" + status
				+ "', PROCESSED = SYSDATE where ID = " + id;
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
        
        
        
	public StateResult pexecute(int arg0, IFlowObject flowObject, IExflowState arg2) throws Exception {
		String docid = getDocID(flowObject);
		String processname = getProcessname(arg2);
		try {
			if (docid != null) {
				parameterMap.put(Thread.currentThread().getId(), processname + ": " + docid);
			}
			execute(flowObject);
		} catch (Exception e) {
			e.printStackTrace();
			SkyLogger.getItyxLogger().error(logPreafix + ":ERROR: " + processname + ": " + docid + " Message:" + e.getMessage(), e);
			return StateResult.exception(e.getMessage());
		}
		return StateResult.STATEOK;
	}

        
        
	@Override
	public void abortExecute() {
		long tid = Thread.currentThread().getId();
		String docid = parameterMap.get(tid);
		Logger log = SkyLogger.getItyxLogger();
		log.error(logPreafix + " ABORT EXECUTE for:" + docid + " threadID:" + tid);
	}

	@Override
	public void rollbackExecute() {
		long tid = Thread.currentThread().getId();
		String docid = parameterMap.get(tid);
		Logger log = SkyLogger.getItyxLogger();
		log.error(logPreafix + " ROLLBACK EXECUTE for:" + docid + " threadID:" + tid);

	}

    
    @Override
    public void cleanState() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void prepareForCluster(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void prepareForResumeFromCluster() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public String getDocID(IFlowObject flowObject) {
		String ctx_documentid = "DocID not found";
		CDocument cdoc = getDoc(flowObject);
		if (cdoc != null) {
			ctx_documentid = (String) cdoc.getNote(TagMatchDefinitions.DOCUMENT_ID);
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

	public CDocumentContainer getDocContainer(IFlowObject flow, String flowObjectname) {
		CDocumentContainer result = null;
		Object object = flow.get(flowObjectname);
		if (object != null && object instanceof CDocumentContainer) {
			result = (CDocumentContainer) object;
		}
		return result;
	}

	public String getProcessname(IExflowState exflowstate) {
		if (exflowstate != null && exflowstate.getProcessInfo() != null && exflowstate.getProcessInfo().getProcessTyp() != null) {
			return exflowstate.getProcessInfo().getProcessTyp();
		}
		return null;
	}

}
