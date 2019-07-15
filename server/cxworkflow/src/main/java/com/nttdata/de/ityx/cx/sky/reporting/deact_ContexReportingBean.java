/**
 * 
 */
package com.nttdata.de.ityx.cx.sky.reporting;

import com.nttdata.de.ityx.sharedservices.utils.ContexDbConnector;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.contex.dbo.designer.Designer_processinfo;
import de.ityx.contex.dbo.designer.Designer_stateinfo;
import de.ityx.contex.impl.designer.state.KeyConfiguration;
import de.ityx.contex.impl.designer.state.StateResult;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.interfaces.designer.IBeanState;
import de.ityx.contex.interfaces.designer.IExflowState;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.designer.StateRepresentation;
import de.ityx.contex.interfaces.document.CDocument;

import java.sql.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Writes contex reporting data.
 * 
 * @author DHIFLM
 * 
 */
public class deact_ContexReportingBean implements IBeanState {

	public static final String	DOCPOOLID			= "docpoolid";

	public static final String	INTERIM				= "INTERIM";

	public static final String	END					= "END";

	public static final String	START				= "START";

	public static final String CTX_PREVDOCPOOLID="CTX_PREVDOCPOOLID";
	public static final String	INTERIMTIMESTAMP	= "interimtimestamp";

	public static final String	STARTTIMESTAMP		= "starttimestamp";

	public static final String	LASTTIMESTAMP		= "lasttimestamp";

	private static final String	NEXTVAL				= "select SEQ_COMMON_SERIALNUMBER.nextval from dual";

	private static final String	INSERT_STMT			= "insert into NTT_CX_REPORT (LOG_ID,CREATED,CURRENTDOCPOOL,PROCESSVERSION,STEP,STEPDETAIL,CTX_DOCPOOLID,CTX_PREVDOCPOOLID,CTX_DOCUMENTID,MTX_ORGMAILID,MTX_FRAGEID,MTX_VORGANGID,MTX_EMAILID,DOCTYPE,INCOMINGDATE,DOCUMENTSOURCE,DOCUMENTID,CUSTOMERID,FORMTYPE,MTX_SUBPROJECTID,MTX_SUBPROJECTDESCRIPTION,CRM_CONTACTID,DURATION) values (?,LOCALTIMESTAMP,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	/**
	 * Names the session key that can contain "false" if the reporting should be
	 * disabled By default the bean assumes that reporting is disabled.
	 */
	public static final String	REPORTING_ENABLED	= "reportingEnabled";

	private static final String	MAP_KEY				= "map";

	private Map<Long, String>	parameterMap		= new ConcurrentHashMap<>();

	private String				docid				= "ITYX_NOTSET";

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.ityx.contex.interfaces.designer.IBeanState#execute(int,
	 * de.ityx.contex.interfaces.designer.IFlowObject,
	 * de.ityx.contex.interfaces.designer.IExflowState)
	 */
	@Override
	public StateResult execute(int arg0, IFlowObject flow, IExflowState arg2) throws Exception {

		CDocumentContainer<CDocument> docContainer = (CDocumentContainer<CDocument>) flow.get(DocContainerUtils.DOC);
		docid = "ITYX_NOTSET";
		if (docContainer != null && docContainer.getDocument(0) != null) {
			docid = (String) docContainer.getDocument(0).getNote(TagMatchDefinitions.DOCUMENT_ID);
		}

		try {
			String logpreafix = "CtxReportingBean->Exec:" + docid + ":";
			if (arg2 != null) {
				Designer_processinfo process = arg2.getProcessInfo();
				if (process != null) {
					SkyLogger.getItyxLogger().debug(logpreafix + ": Processid:" + process.getProcessId() + " Processtyp:" + process.getProcessTyp() + " Projectname:" + process.getProjectName());
				}
				Designer_stateinfo stateinfo = arg2.getStateInfo();
				if (stateinfo != null) {
					SkyLogger.getItyxLogger().debug(logpreafix + ": Stateid:" + stateinfo.getId() + " Info:" + stateinfo.getInfo() + " :" + stateinfo.getStateid());
				}
				StateRepresentation staterepr = arg2.getStateRepresentation();
				if (staterepr != null) {
					SkyLogger.getItyxLogger().debug(logpreafix + ": Stateid:" + staterepr.getName() + " Info:" + staterepr.getComment() + " :" + staterepr.getProfile());
				}
			}
			execute(flow);
		} catch (Exception e) {
			e.printStackTrace();
			return StateResult.exception(e.getMessage());
		}
		return StateResult.STATEOK;
	}

	private void execute(IFlowObject flow) throws SQLException {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();

		Object object = flow.get(DocContainerUtils.DOC);
		CDocumentContainer doc = (CDocumentContainer) object;

		String logPrefix = clazz.getName() + "#" + name + ":" + docid + ":";
		SkyLogger.getItyxLogger().debug(logPrefix + ": enter");

		if (object != null && !object.getClass().equals(String.class)) {
			Connection con = ContexDbConnector.getAutoCommitConnection();
			PreparedStatement idStmt = con.prepareStatement(NEXTVAL);
			ResultSet rs = idStmt.executeQuery();

			Long log_id = null;
			if (rs.next()) {
				log_id = rs.getLong(1);
			}else{
				throw new SQLDataException("Unable to get next ID for Reporting:"+NEXTVAL);
			}
			rs.close();
			CDocument cdoc = doc.getDocument(0);
			String ctx_documentid = (String) cdoc.getNote(TagMatchDefinitions.DOCUMENT_ID);

			//if (ctx_documentid == null) {
			//	ctx_documentid = "ITYX" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date(System.currentTimeMillis())) + "-" + log_id;
			//	cdoc.setNote(TagMatchDefinitions.DOCUMENT_ID, ctx_documentid);
			//	doc.setExternalID(ctx_documentid);
			//}
			String parameter = "<" + TagMatchDefinitions.DOCUMENT_ID + "," + ctx_documentid + ">";

			if (!(Boolean) flow.get(REPORTING_ENABLED)) {
				SkyLogger.getItyxLogger().debug(logPrefix + ": not enabled");
			} else {
				Map<String, Object> map = (Map<String, Object>) flow.get(MAP_KEY);
				if (map != null) {
					try {

						Integer currentdocpool = (Integer) map.get("CURRENTDOCPOOL");
						parameter += "\n<CURRENTDOCPOOL," + currentdocpool + ">";

						String processversion = (String) map.get("PROCESSVERSION");
						parameter += "\n<PROCESSVERSION," + processversion + ">";

						String step = (String) map.get("STEP");
						parameter += "\n<STEP," + step + ">";

						Long duration = fillDuration(cdoc, step, map);
						parameter += "\n<DURATION," + duration + ">";

						String stepdetail = (String) map.get("STEPDETAIL");
						parameter += "\n<STEPDETAIL," + stepdetail + ">";

						Long ctx_docpoolid = null;
						Object ctx_docpoolidO=doc.getNote(DOCPOOLID);
						try {
							if (ctx_docpoolidO instanceof  Long){
								ctx_docpoolid = (Long) ctx_docpoolidO;
							}
							else if (ctx_docpoolidO instanceof String){
								String docpoolid = (String) ctx_docpoolidO;
								ctx_docpoolid = Long.parseLong(docpoolid);
							}
						} catch (Exception e) {
							e.printStackTrace();
							SkyLogger.getItyxLogger().error(e.getMessage());
						} finally {
							if (ctx_docpoolid == null) {
								ctx_docpoolid = 0L;
							}
						}
						parameter += "\n<CTX_DOCPOOLID," + ctx_docpoolid + ">";

						Integer ctx_prevdocpoolid = null;
						if (step.equals(START)) {
							ctx_prevdocpoolid = (Integer) doc.getNote("CTX_PREVDOCPOOLID");
							parameter += "\n<CTX_PREVDOCPOOLID," + ctx_prevdocpoolid + ">";
							if (ctx_prevdocpoolid != null) {
								flow.put("CTX_PREVDOCPOOLID", ctx_prevdocpoolid);
							}
							doc.setNote("CTX_PREVDOCPOOLID", ctx_docpoolid);
						} else {
							ctx_prevdocpoolid = (Integer) flow.get("CTX_PREVDOCPOOLID");
							parameter += "\n<CTX_PREVDOCPOOLID," + ctx_prevdocpoolid + ">";
						}

						long id = Thread.currentThread().getId();
						parameterMap.put(id, parameter);
						String message = id + ": MAP:\n" + parameter;
						SkyLogger.getItyxLogger().info(message);

						Integer mtx_orgmailid = (Integer) doc.getNote("MTX-EmailId");

						String documentsource = (String) doc.getNote("DOCUMENTSOURCE");

						String documentid = (String) doc.getNote("DOCUMENTID");

						Date incomingdate = (Date) doc.getNote("INCOMINGDATE");
						Timestamp incomingts = (Timestamp) doc.getNote("INCOMINGTIMESTAMP");

						String doctype = (String) cdoc.getNote(TagMatchDefinitions.CHANNEL);

						String crm_contactid = (String) cdoc.getNote(TagMatchDefinitions.CONTACT_ID);

						String formtype = cdoc.getFormtype();

						Long customerid = null;
						try {
							String idString = (String) cdoc.getNote(TagMatchDefinitions.EVAL_CUSTOMER_NUMBER);
							if (idString != null && idString.length() > 0 && !idString.equals("SERVICE_OFF")) {
								customerid = Long.parseLong(idString);
							}
						} catch (Exception e) {
							e.printStackTrace();
							SkyLogger.getItyxLogger().error(e.getMessage());
						} finally {
							if (customerid == null) {
								customerid = 0L;
							}
						}
						if (incomingdate == null) {
							incomingdate = (Date) map.get("INCOMINGDATE");
						}
						if (incomingts == null) {
							incomingts = (Timestamp) map.get("INCOMINGTIMESTAMP");
						}

						if (incomingts == null && incomingdate != null) {
							incomingts = new Timestamp(incomingdate.getTime());
						}

						if (documentid == null) {
							documentid = (String) map.get("DOCUMENTID");
						}
						if (documentsource == null) {
							documentsource = (String) map.get("DOCUMENTSOURCE");
						}

						if (ctx_prevdocpoolid == null) {
							ctx_prevdocpoolid = 0;
						}

						if (mtx_orgmailid == null) {
							mtx_orgmailid = 0;
						}

						PreparedStatement insertStmt = con.prepareStatement(INSERT_STMT);
						insertStmt.setLong(1, log_id);
						insertStmt.setLong(2, currentdocpool);
						insertStmt.setString(3, processversion);
						insertStmt.setString(4, step);
						insertStmt.setString(5, stepdetail);
						insertStmt.setLong(6, ctx_docpoolid);
						insertStmt.setInt(7, ctx_prevdocpoolid);
						insertStmt.setString(8, ctx_documentid);
						insertStmt.setInt(9, mtx_orgmailid);
						insertStmt.setInt(10, 0);
						insertStmt.setInt(11, 0);
						insertStmt.setInt(12, mtx_orgmailid);
						insertStmt.setString(13, doctype);
						insertStmt.setTimestamp(14, incomingts);
						insertStmt.setString(15, documentsource);
						insertStmt.setString(16, documentid);
						insertStmt.setLong(17, customerid);
						insertStmt.setString(18, formtype);
						insertStmt.setInt(19, 0);
						insertStmt.setInt(20, 0);
						insertStmt.setString(21, crm_contactid);
						insertStmt.setLong(22, duration);

						SkyLogger.getItyxLogger().debug(logPrefix + ": executing");
						insertStmt.execute();
					} catch (Exception e) {
						e.printStackTrace();
						SkyLogger.getItyxLogger().error(e.getMessage());
					}
				}
			}
			con.close();
		}
		SkyLogger.getItyxLogger().debug(logPrefix + ": exit");
	}

	private Long fillDuration(CDocument cdoc, String step, Map<String, Object> map) {
		Long sysdate = System.currentTimeMillis();
		Double exactDuration = -1d;
		if (step.equalsIgnoreCase(START)) {
			Long lasttimestamp = (Long) cdoc.getNote(LASTTIMESTAMP);
			if (lasttimestamp == null) {
				lasttimestamp = 0L;
			}
			exactDuration = (sysdate - lasttimestamp) / 1000d;
			map.put(STARTTIMESTAMP, sysdate);
		} else if (step.equalsIgnoreCase(END)) {
			Long starttimestamp = (Long) map.get(STARTTIMESTAMP);
			if (starttimestamp == null) {
				starttimestamp = sysdate + 1;
			}
			exactDuration = (sysdate - starttimestamp) / 1000d;
		} else if (step.equalsIgnoreCase(INTERIM)) {
			Long interimtimestamp = (Long) map.get(INTERIMTIMESTAMP);
			if (interimtimestamp == null) {
				interimtimestamp = (Long) map.get(STARTTIMESTAMP);
				if (interimtimestamp == null) {
					interimtimestamp = sysdate + 1;
				}
			}
			exactDuration = (sysdate - interimtimestamp) / 1000d;
			map.put(INTERIMTIMESTAMP, sysdate);
			cdoc.setNote(LASTTIMESTAMP, sysdate);
		}
		return Math.round(exactDuration);
	}



	@Override
	public KeyConfiguration[] getKeys() {
		return new KeyConfiguration[] {
				new KeyConfiguration(LASTTIMESTAMP, Long.class),
				new KeyConfiguration(STARTTIMESTAMP, Long.class),
				new KeyConfiguration(CTX_PREVDOCPOOLID, Integer.class)
		};
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.ityx.contex.interfaces.designer.IDesignerState#abortExecute()
	 */
	@Override
	public void abortExecute() {
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = getClass().getName() + "#" + name + ":" + docid + ":";
		SkyLogger.getItyxLogger().error(logPrefix + " abortEXECUTE");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.ityx.contex.interfaces.designer.IDesignerState#cleanState()
	 */
	@Override
	public void cleanState() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.ityx.contex.interfaces.designer.IDesignerState#prepareForCluster(java
	 * .lang.String)
	 */
	@Override
	public void prepareForCluster(String arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.ityx.contex.interfaces.designer.IDesignerState#prepareForResumeFromCluster
	 * ()
	 */
	@Override
	public void prepareForResumeFromCluster() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.ityx.contex.interfaces.designer.IDesignerState#rollbackExecute()
	 */
	@Override
	public void rollbackExecute() {
		long id = Thread.currentThread().getId();
		String parameter = parameterMap.get(id);
		String message = id + ": ROLLBACK:" + parameter + ":" + docid;
		SkyLogger.getItyxLogger().error(message);
	}
}
