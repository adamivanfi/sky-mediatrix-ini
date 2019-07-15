/**
 *
 *//**
 *
 */
package com.nttdata.de.ityx.cx.sky.reporting;

//import com.nttdata.de.ityx.cx.sky.ContexAccess;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;
import com.nttdata.de.ityx.sharedservices.utils.ContexDbConnector;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.contex.dbo.designer.Designer_processinfo;
import de.ityx.contex.dbo.designer.Designer_stateinfo;
import de.ityx.contex.impl.designer.state.StateResult;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.interfaces.designer.IExflowState;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.designer.StateRepresentation;
import de.ityx.contex.interfaces.document.CDocument;
import de.ityx.contex.interfaces.extag.TagMatch;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.nttdata.de.ityx.cx.workflow.incoming.i4_customerindexing.CheckExtractedData.*;

//import de.ityx.contex.data.icat.Category;

/**
 * Writes contex reporting data.
 *
 * @author DHIFLM
 */
public class WorkflowReportingBean extends AbstractWflBean {

	private static final String PROCESSNAME_WITHOUT_VERSION = "(\\p{Alpha}+_)?(\\d*)_([\\w]*)[\\w\\d\\+_\\-]*";
	private static final String PROCESSNAME_WITH_VERSION = "(\\p{Alpha}+_?)?(\\d*)_([\\w]*)_[Vv]*([\\d\\.]*)?(_[\\w\\d\\+_\\-]*)*";
	private static final Pattern QUESTIONURIPATTERN = Pattern.compile("question:\\/\\/(\\d*)");
	public static final String DOCPOOLID = "docpoolid";
	public static final String INTERIM = "INTERIM";
	public static final String END = "END";
	public static final String START = "START";
	public static final String SUSPEND = "SUSPEND";
	public static final String RESUME = "RESUME";
	public static final String INTERIMTIMESTAMP = "interimtimestamp";
	public static final String STARTTIMESTAMP = "starttimestamp";
	public static final String LASTTIMESTAMP = "lasttimestamp";
	private static final String NEXTVAL_STMT = "select SEQ_COMMON_SERIALNUMBER.nextval from dual";
	private static final Pattern PATTERN_WITH_VERSION = Pattern.compile(PROCESSNAME_WITH_VERSION);
	private static final Pattern PATTERN_WITHOUT_VERSION = Pattern.compile(PROCESSNAME_WITHOUT_VERSION);

	private static final String INSERT_STMT = "insert into NTT_CX_REPORT (LOG_ID,CREATED,CURRENTDOCPOOL,PROCESSVERSION,STEP,STEPDETAIL,CTX_DOCPOOLID,CTX_PREVDOCPOOLID,CTX_DOCUMENTID,MTX_ORGMAILID,MTX_FRAGEID,MTX_VORGANGID,MTX_EMAILID,DOCTYPE,INCOMINGDATE,DOCUMENTSOURCE,DOCUMENTID,CUSTOMERID,FORMTYPE,MTX_SUBPROJECTID,MTX_SUBPROJECTDESCRIPTION,CRM_CONTACTID,DURATION,CLUSTERNODE,TENANT) values (?,LOCALTIMESTAMP,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	public static final String NEXTVAL_INBOUND = "select SEQNTT_CX_INBOUND.nextval from dual";
	/**
	 * Names the session key that can contain "false" if the reporting should be
	 * disabled By default the bean assumes that reporting is disabled.
	 */
	public static final String REPORTING_ENABLED = "reportingEnabled";

	public static final String MAP_KEY = "reporting_map";
	public static final String RPT_STEPDETAIL = "STEPDETAIL";
	public static final String REPORTING_STEP_COUNTER = "REPORTING_STEP_COUNTER";
	private Map<Long, String> parameterMap = new ConcurrentHashMap<>();


	public static String MYHOSTNAME;

	protected final String logPreafix = getClass().getName();

	@Override
	public StateResult execute(int arg0, IFlowObject flowObject, IExflowState arg2) throws Exception {
		try {
			String docid = DocContainerUtils.getDocID(flowObject);
			if (docid != null) {
				parameterMap.put(Thread.currentThread().getId(), docid);
				setWflChannel(flowObject);
			}
			execute(flowObject, arg2, isReadonly());
		} catch (Exception e) {
			Logger log = SkyLogger.getItyxLogger();
			log.error(logPreafix + ":ERROR: " + e.getMessage(), e);
			// return StateResult.exception(e.getMessage());
		}
		return StateResult.STATEOK;
	}

	protected boolean isReadonly() {
		return false;
	}

	public void execute(IFlowObject flow, IExflowState exflowstate, boolean readonly) throws SQLException {
		String logPrefix = getClass().getName() + "#" + (new Object() {
		}.getClass().getEnclosingMethod().getName()) + ":";

		// TODO: Methode auteilen auf eine die daten aus cdoc-Dokumenten aufsammelt und eine die mit einer parameter-map leben kann
		if (flow == null || flow.get(REPORTING_ENABLED) == null || !(Boolean) flow.get(REPORTING_ENABLED)) {
			SkyLogger.getItyxLogger().debug(logPreafix + ": Reporting Disabled or Configuration not loaded");
		} else {
			String step = "";
			String parameter = "";
			Map<String, Object> map = new HashMap<>();
			Object mobj = flow.get(MAP_KEY);
			if (mobj != null && !mobj.getClass().equals(String.class)) {
				map = (Map<String, Object>) mobj;
			} else {
				map = new HashMap<>();
			}

			final InsertReportingEntryParameter processLogData = new InsertReportingEntryParameter();
			long ctx_docpoolid = 0;

			if (exflowstate != null) {
				Designer_processinfo process = exflowstate.getProcessInfo();
				if (process != null) {
					String proctyp = process.getProcessTyp();
					SkyLogger.getItyxLogger().debug(logPreafix + " process: " + proctyp);

					if (process.getProjectName()!=null) {
						processLogData.setMaster(process.getProjectName().toUpperCase());
					}

					Matcher matcher = PATTERN_WITH_VERSION.matcher(proctyp);
					if (matcher.find()) {
						setProcessNameInfo(processLogData, matcher, map, true);

					} else {
						matcher = PATTERN_WITHOUT_VERSION.matcher(proctyp);
						if (matcher.find()) {
							setProcessNameInfo(processLogData, matcher, map, false);
						} else {
							SkyLogger.getItyxLogger().debug(logPreafix + " Problem during read ProcessnTypVariable of process: " + proctyp);
						}
					}
				}

				Designer_stateinfo stateinfo = exflowstate.getStateInfo();
				if (stateinfo != null) {
					step = stateinfo.getInfo();
				}
				StateRepresentation staterepr = exflowstate.getStateRepresentation();
				if (step == null && staterepr != null) {
					step = staterepr != null ? staterepr.getName() : null;
				}
			}
			parameter += "\n<CURRENTDOCPOOL," + processLogData.getCurrentdocpool() + ">";
			Integer stepReporting = (Integer) map.get(REPORTING_STEP_COUNTER);
			if (stepReporting == null) {
				stepReporting = 0;
			}

			map.put(REPORTING_STEP_COUNTER, stepReporting + 1);
			parameter += "\n<REPORTING_STEP_COUNTER," + stepReporting + ">";
			parameter += "\n<PROCESSVERSION," + processLogData.getProcessversion() + ">";
			step = getStep(stepReporting, processLogData.getCurrentdocpool());

			parameter += "\n<STEP," + step + ">";
			processLogData.setStep(step);

			long duration = fillDuration(DocContainerUtils.getDoc(flow), step, map, readonly);
			parameter += "\n<DURATION," + duration + ">";
			processLogData.setDuration(duration);

			String stepdetail = (String) map.get(RPT_STEPDETAIL);
			if (stepdetail == null) {
				stepdetail = getStepDetail(flow, processLogData.getCurrentdocpool(), step);
			}
			parameter += "\n<STEPDETAIL," + stepdetail + ">";
			processLogData.setStepdetail(stepdetail);

			long id = Thread.currentThread().getId();
			parameterMap.put(id, parameter);

			processLogData.setDocumentid((String) map.get("DOCUMENTKEY"));
			processLogData.setDocumentsource((String) map.get("DOCUMENTSOURCE"));
			java.util.Date incomingdate =null;
			//long log_id = getLogID();
			try {
				CDocument cdoc = DocContainerUtils.getDoc(flow);
				CDocumentContainer cont = DocContainerUtils.getDocContainer(flow);

				if (cdoc == null || cont == null) {
					SkyLogger.getItyxLogger().info(logPrefix + "Simple logging for Process without Document:" + flow.get(TagMatchDefinitions.DOCUMENT_ID));
				} else {
					//String ctx_documentid = DocContainerUtils.getOrGenerateDocID(flow, TagMatchDefinitions.DocumentDirection.INBOUND, DocContainerUtils.getChannelType(cdoc), new java.util.Date());
					try {
						Object docpoolidO = cont.getMetainformation(DOCPOOLID);
						if (docpoolidO != null && docpoolidO instanceof String) {
							String docpoolid = (String) docpoolidO;
							ctx_docpoolid = Long.parseLong(docpoolid);
							cont.setNote(DOCPOOLID, ctx_docpoolid);
						} else if (docpoolidO != null && docpoolidO instanceof Long) {
							ctx_docpoolid = (Long) docpoolidO;
						}

					} catch (Exception e) {
						e.printStackTrace();
						SkyLogger.getItyxLogger().error("Docpoolid can not be read from current document" + e.getMessage());
					}
					if (ctx_docpoolid < 1) {
						try {
							Object docpoolidO = cont.getNote(DOCPOOLID);
							if (docpoolidO != null && docpoolidO instanceof String) {
								String docpoolid = (String) cont.getNote(DOCPOOLID);
								ctx_docpoolid = Long.parseLong(docpoolid);
								cont.setNote(DOCPOOLID, ctx_docpoolid);
							} else if (docpoolidO != null && docpoolidO instanceof Long) {
								ctx_docpoolid = (Long) docpoolidO;
							}
						} catch (Exception e) {
							e.printStackTrace();
							SkyLogger.getItyxLogger().error("Docpoolid can not be read from current document" + e.getMessage(), e);
						}
					}
					parameter += "\n<CTX_DOCPOOLID," + ctx_docpoolid + ">";
					processLogData.setCtx_docpoolid(ctx_docpoolid);

					//String logpreafix = "WflReportingBean->Exec:" + ctx_docpoolid + ":";


					long ctx_prevdocpoolid = 0;
					if (step.equals(START)) {
						Object ctx_prevdocpoolidO = cont.getNote("CTX_PREVDOCPOOLID");
						if (ctx_prevdocpoolidO instanceof Long) {
							ctx_prevdocpoolid = (Long) ctx_prevdocpoolidO;
						} else if (ctx_prevdocpoolidO instanceof Integer) {
							ctx_prevdocpoolid = (Integer) ctx_prevdocpoolidO;
						}
						if (ctx_prevdocpoolid > 0) {
							flow.put("CTX_PREVDOCPOOLID", ctx_prevdocpoolid);
						}
						cont.setNote("CTX_PREVDOCPOOLID", ctx_docpoolid);
					} else if (flow.get("CTX_PREVDOCPOOLID") != null) {
						Object ctx_prevdocpoolidO = cont.getNote("CTX_PREVDOCPOOLID");
						if (ctx_prevdocpoolidO instanceof Long) {
							ctx_prevdocpoolid = (Long) ctx_prevdocpoolidO;
						} else if (ctx_prevdocpoolidO instanceof Integer) {
							ctx_prevdocpoolid = (Integer) ctx_prevdocpoolidO;
						}
					}
					parameter += "\n<CTX_PREVDOCPOOLID," + ctx_prevdocpoolid + ">";
					processLogData.setCtx_prevdocpoolid(ctx_prevdocpoolid);

					// String message = id + ": MAP:\n" + parameter;
					// SkyLogger.getItyxLogger().info(message);
					processLogData.setMtx_orgmailid(getLNoteByName(cont, "MTX-EmailId"));

					Object qidO = cont.getNote("uri");
					SkyLogger.getItyxLogger().debug(logPreafix + " Qid:" + qidO);

					if (qidO instanceof String && ((String) qidO).contains("question")) {
						Matcher matcher = QUESTIONURIPATTERN.matcher((String) qidO);
						if (matcher.find() && matcher.group(1) != null && !matcher.group(1).isEmpty()) {
							try {
								long qid = Integer.parseInt(matcher.group(1));
								processLogData.setMtx_frageid(qid);
							} catch (Exception e) {
								SkyLogger.getItyxLogger().warn(logPreafix + " unable to extract questionid " + e.getMessage());
							}
						}
					}
					if (processLogData.getDocumentid()  == null|| processLogData.getDocumentid().isEmpty()) {
						processLogData.setDocumentid((String) cont.getNote("DOCUMENTKEY"));
					}
					if (processLogData.getDocumentid()  == null || processLogData.getDocumentid().isEmpty()) {
						processLogData.setDocumentid((String) cont.getNote("uri"));
					}
					if (processLogData.getDocumentid()  == null || processLogData.getDocumentid().isEmpty()) {
						processLogData.setDocumentid((String) cdoc.getNote("URI"));
					}

					if (processLogData.getDocumentsource() == null) {
						processLogData.setDocumentsource((String)cont.getNote("DOCUMENTSOURCE"));
					}
					if (processLogData.getDocumentsource() == null) {
						processLogData.setDocumentsource(getSNoteByName(cdoc, TagMatchDefinitions.MX_DIRECTION));
					}
					incomingdate =  DocContainerUtils.getIncommingDate(cont);
					

					TagMatchDefinitions.Channel channel = DocContainerUtils.getChannelType(cdoc);
					processLogData.setChannel(channel);

					String crm_contactid = (String) cdoc.getNote(TagMatchDefinitions.CONTACT_ID);
					processLogData.setCrm_contactid(crm_contactid);

					String formtype = DocContainerUtils.getFormtype(cdoc);
					processLogData.setFormtype(formtype);


					long customerId = getLNoteByName(cdoc, TagMatchDefinitions.CUSTOMER_ID);
					if (customerId == 0L) {
						customerId = getLNoteByName(cdoc, TagMatchDefinitions.EVAL_CUSTOMER_NUMBER);
					}
					processLogData.setCustomerid(customerId);
					processLogData.setMtx_frageid(getLNoteByName(cdoc, "FrageID"));
					processLogData.setMtx_vorgangid(getLNoteByName(cdoc, "VorgangID"));
					processLogData.setMtx_mailid(getLNoteByName(cdoc, "EmailID"));
					processLogData.setMtx_TPName(getSNoteByName(cdoc, "TeilprojektName"));
				}
				if (incomingdate==null){
					incomingdate = new java.util.Date();
				}
				processLogData.setIncomingdate(incomingdate);
				
				insertReportingEntry(flow, logPrefix, processLogData, readonly);

			} catch (Exception e) {
				e.printStackTrace();
				String stacktrace = "";
				for (StackTraceElement est : e.getStackTrace()) {
					stacktrace += " => " + est.getClassName() + ">" + est.getMethodName() + ":" + est.getLineNumber() + "\r\n";
				}
				SkyLogger.getItyxLogger().error(e.getMessage() + " " + stacktrace, e);
			}
			if (!readonly) {
				map.remove("STEPDETAIL"); // preparation for the next step
				flow.put(MAP_KEY, map);
			}
		}
	}

	/**
	 * @param processLogData
	 * @param matcher
	 * @param map
	 * @param setVersion
	 */
	protected void setProcessNameInfo(final InsertReportingEntryParameter processLogData, Matcher matcher, Map<String, Object> map, boolean setVersion) {
		if (matcher.group(1) != null && !matcher.group(1).isEmpty() && (processLogData.getMaster()==null || processLogData.getMaster().isEmpty())) {
			String mastercandidate=matcher.group(1);
			if (mastercandidate!=null){
				mastercandidate=mastercandidate.substring(0,3).toUpperCase();
				if (mastercandidate.equals("FHV") || mastercandidate.equals("VOS")){
					mastercandidate="SKY";
				}
				processLogData.setMaster(mastercandidate);
			}
		}
		Integer currentdocpool = null;
		if (matcher.group(2) != null && !matcher.group(2).isEmpty()) {
			currentdocpool = Integer.parseInt(matcher.group(2));
		}
		if (currentdocpool == null || currentdocpool == 0) {
			currentdocpool = (Integer) map.get("CURRENTDOCPOOL");
		}
		processLogData.setCurrentdocpool(currentdocpool != null ? currentdocpool : 999);
		if (matcher.group(3) != null && !matcher.group(3).isEmpty()) {
			processLogData.setProcessname(matcher.group(3));
		}
		if (setVersion) {
			String processversion = "";
			if (matcher.group(4) != null && !matcher.group(4).isEmpty()) {
				processversion = matcher.group(4);
			}
			if (processversion.isEmpty()) {
				processversion = (String) map.get("PROCESSVERSION");
			}
			processLogData.setProcessversion(processversion != null ? processversion : "");
		}
	}

	private String getStepDetail(IFlowObject flow, int currdocpool, String step) {
		if (currdocpool == 300 && flow.get("bestcat") != null) {
			String caResult = "cat: " + flow.get("bestcat");
			Object catArrayO = flow.get("cattaray");
			if (catArrayO != null && !(catArrayO instanceof String)) {
				caResult += " ca:";
				if (catArrayO instanceof de.ityx.lingua.categorizer.Category[]) {
					for (de.ityx.lingua.categorizer.Category cat : (de.ityx.lingua.categorizer.Category[]) catArrayO) {
						caResult += cat.getName() + ":" + cat.getProbability() + " ";
					}
				} else if (catArrayO instanceof de.ityx.contex.data.icat.Category[]) {
					for (de.ityx.contex.data.icat.Category cat : (de.ityx.contex.data.icat.Category[]) catArrayO) {
						caResult += cat.getPath() + ":" + cat.getRelevance() + " ";
					}
				}


				caResult = caResult.substring(0, caResult.length() < 200 ? caResult.length() : 200);
			}
			return caResult;
		} else if (currdocpool >= 400 && currdocpool < 500) {
			String message = "";
			if (flow.get("IndexedDocument") != null) {
				message += ((Boolean) flow.get("IndexedDocument")) ? "OK" : "NK";
			}
			if (flow.get("customer") != null) {
				String vCust = (String) flow.get(VERIFIED_CUSTOMER_NUMBER);
				String vContr = (String) flow.get(VERIFIED_CONTRACT_NUMBER);
				String vSN = (String) flow.get(VERIFIED_SMARTCARD_NUMBER);
				String vMan = (String) flow.get(VERIFIED_MANDATE_NUMBER);
				message += (vCust != null && !vCust.isEmpty()) ? " vc:" + vCust : "";
				message += (vContr != null && !vContr.isEmpty()) ? " vv:" + vContr : "";
				message += (vSN != null && !vSN.isEmpty()) ? " vs:" + vSN : "";
				message += (vMan != null && !vMan.isEmpty()) ? " vm:" + vMan : "";

				for (TagMatch customer : (ArrayList<TagMatch>) flow.get("customer")) {
					if (customer != null && !DocContainerUtils.isEmpty(customer.getTagValue())) {
						message += " c:" + customer.getTagValue(VERIFIED_CUSTOMER_NUMBER) + ",";
					}
				}
			}
			return (!DocContainerUtils.isEmpty(message) && message.length() > 225) ? message.substring(0, 224) : message;
		} else if (currdocpool >= 220 && currdocpool < 230) {
			CDocument cdoc = DocContainerUtils.getDoc(flow);
			if (cdoc != null && cdoc.getPageCount() > 0) {
				return "s:" + cdoc.getPageCount();
			}
		} else if (currdocpool >= 800 && currdocpool < 840) {
			CDocument cdoc = DocContainerUtils.getDoc(flow);
			if (cdoc != null && cdoc.getNote("Direction") != null) {
				return "d:" + cdoc.getNote("Direction");
			}
		}
		return null;
	}

	private long getLogID() throws SQLException {
		Connection con = null;
		long log_id = 0;
		try {
			//ToDo
			//con = ContexAccess.getInstance().getConnection();
			con = ContexDbConnector.getAutoCommitConnection();

			con.setAutoCommit(true);
			log_id = getLogID(con);
		} finally {
			if (con != null) {
				con.close();
			}
		}
		return log_id;
	}

	private long getLogID(Connection con) throws SQLException {
		long log_id = 0;
		PreparedStatement idStmt = null;
		ResultSet rs = null;
		try {

			idStmt = con.prepareStatement(NEXTVAL_STMT);
			rs = idStmt.executeQuery();
			if (rs.next()) {
				log_id = rs.getLong(1);
			}
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (idStmt != null) {
				idStmt.close();
			}
		}
		return log_id;
	}

	public void insertReportingEntry(IFlowObject flow, String logPrefix, InsertReportingEntryParameter processLogData, boolean readonly) throws SQLException {

		Connection con = null;
		PreparedStatement idStmt = null;
		ResultSet rs = null;
		try {
			//con = ContexAccess.getInstance().getConnection();
			con = ContexDbConnector.getAutoCommitConnection();

			con.setAutoCommit(true);

			long log_id = getLogID(con);
			if (processLogData.getChannel()==null){
				processLogData.setChannel(TagMatchDefinitions.Channel.DOCUMENT);
			}
			String ctx_documentid = DocContainerUtils.getOrGenerateDocID(flow, TagMatchDefinitions.DocumentDirection.INBOUND, processLogData.getChannel(), new java.util.Date());
			String parameter = "<" + TagMatchDefinitions.DOCUMENT_ID + "," + ctx_documentid + ">";

			PreparedStatement insertStmt = con.prepareStatement(INSERT_STMT);
			insertStmt.setLong(1, log_id);
			insertStmt.setLong(2, processLogData.getCurrentdocpool());
			insertStmt.setString(3, processLogData.getCurrentdocpool() + "_" + processLogData.getProcessname() + "_v" + processLogData.getProcessversion());
			insertStmt.setString(4, processLogData.getStep());
			insertStmt.setString(5, processLogData.getStepdetail());
			insertStmt.setLong(6, processLogData.getCtx_docpoolid());
			insertStmt.setLong(7, processLogData.getCtx_prevdocpoolid());
			insertStmt.setString(8, ctx_documentid);
			insertStmt.setLong(9, processLogData.getMtx_orgmailid());
			insertStmt.setLong(10, processLogData.getMtx_frageid());
			insertStmt.setLong(11, processLogData.getMtx_vorgangid());
			insertStmt.setLong(12, processLogData.getMtx_mailid());
			insertStmt.setString(13, processLogData.getChannel().toString());
			insertStmt.setTimestamp(14, new Timestamp(processLogData.getIncomingdate().getTime()));
			insertStmt.setString(15, processLogData.getDocumentsource());
			if (processLogData.getDocumentid()!=null && !processLogData.getDocumentid().isEmpty()) {
				String documentLongID = processLogData.getDocumentid().substring(0, processLogData.getDocumentid().length() < 240 ? processLogData.getDocumentid().length() : 240);
				insertStmt.setString(16, documentLongID);
			}else{
				insertStmt.setString(16, "");
			}
			insertStmt.setLong(17, processLogData.getCustomerid());
			insertStmt.setString(18, processLogData.getFormtype());
			//MTX_SUBPROJECTID
			insertStmt.setLong(19, 0);
			insertStmt.setString(20, processLogData.getMtx_TPName());
			insertStmt.setString(21, processLogData.getCrm_contactid());
			insertStmt.setLong(22, processLogData.getDuration());
			insertStmt.setString(23, processLogData.getLHostname());
			insertStmt.setString(24, processLogData.getMaster());

			// SkyLogger.getItyxLogger().debug(logPrefix + ": executing"+
			// parameter);
			insertStmt.execute();

		} finally {
			if (rs != null) {
				rs.close();
			}
			if (idStmt != null) {
				idStmt.close();
			}
			if (con != null) {
				con.close();
			}
		}
	}

	private String getSNoteByName(CDocument cdoc, String noteName) {
		try {
			String idString = (String) cdoc.getNote(noteName);
			if (idString != null && !idString.isEmpty() && !idString.equals("0") && !idString.equals("SERVICE_OFF")) {
				return idString;
			}

		} catch (Exception e) {
			//e.printStackTrace();
			SkyLogger.getItyxLogger().debug("Problem accessing Note:" + noteName + " for Wfl-Reporting:" + e.getMessage());
		}
		return null;
	}

	private long getLNoteByName(CDocument cdoc, String noteName) {
		String idString = null;
		try {
			idString = (String) cdoc.getNote(noteName);
			if (idString != null && !idString.isEmpty() && !idString.equals("0") && !idString.equals("SERVICE_OFF")) {
				return Long.parseLong(idString);
			}

		} catch (Exception a) {
			try {
				return Long.parseLong(idString != null ? idString.replaceAll("\\D+", "") : "0");

			} catch (Exception e) {
				SkyLogger.getItyxLogger().debug("Problem accessing Note:" + noteName + " for Wfl-Reporting:" + e.getMessage());
				try {

					return (long) ((idString == null) ? 0 : Integer.parseInt(idString));
				} catch (Exception en) {
					SkyLogger.getItyxLogger().info("Problem accessing Note:" + noteName + " for Wfl-Reporting:" + en.getMessage());

				}
			}

		}
		return 0L;
	}


	private long getLNoteByName(CDocumentContainer ccont, String noteName) {
		String idString = null;
		try {
			if (ccont.getNote(noteName) instanceof String) {
				idString = (String) ccont.getNote(noteName);
				if (idString != null && !idString.isEmpty() && !idString.equals("0") && !idString.equals("SERVICE_OFF")) {
					return Long.parseLong(idString);
				}
			} else if (ccont.getNote(noteName) instanceof Long) {
				long idStringL = (Long) ccont.getNote(noteName);
				if (idStringL > 0) {
					return idStringL;
				}
			} else if (ccont.getNote(noteName) instanceof Integer) {
				int idStringI = (Integer) ccont.getNote(noteName);
				if (idStringI > 0) {
					return idStringI;
				}
			}

		} catch (Exception e) {
			SkyLogger.getItyxLogger().debug("Problem accessing Note:" + noteName + " for Wfl-Reporting:" + e.getMessage());
			try {
				return (long) ((idString == null) ? 0 : Integer.parseInt(idString));
			} catch (Exception en) {
				SkyLogger.getItyxLogger().info("Problem accessing Note:" + noteName + " for Wfl-Reporting:" + en.getMessage());

			}

		}
		return 0L;
	}

	public String getStep(Integer stepReporting, int currentdocpool) {
		if (stepReporting != null && stepReporting == Integer.MAX_VALUE) {
			return END;
		} else if (stepReporting == null || stepReporting == 0) {
			return START;
		} else if (currentdocpool == 401) {
			switch (stepReporting) {
				case 1:
					return "VertragNrL";
				case 2:
					return "KundenNrLVertrag";
				case 3:
					return "VertragNrKundenNr";
				case 4:
					return "SmcVertrag";
				case 5:
					return "SmcKundenNr";
				case 6:
					return "VertragNr_Name";
				case 7:
					return "VertragNr_Email";
				case 8:
					return "Smc_Email";
				case 9:
					return "Smc_Name";
				case 10:
					return "KundenNrL";
				case 11:
					return "KundenNr_Name";
				case 12:
					return "KundenNr_Email";
				case 13:
					return "ZIP_LastnameFirstname";
				case 14:
					return "ZIP_LastnameStreet";
				case 15:
					return "Bank_Lastname";
				case 16:
					return "Bank_Email";
			} // else @TODO: END Zustand setzen
		} else if (currentdocpool == 431) {
			switch (stepReporting) {
				case 1:
					return "MandateL_KundennummerVertrag";
				case 2:
					return "MandateKundennummerVertrag";
				case 3:
					return "VertragNrL";
				case 4:
					return "KundenNrLVertrag";
				case 5:
					return "VertragNrKundenNr";
				case 6:
					return "SmcVertrag";
				case 7:
					return "SmcKundenNr";
				case 8:
					return "VertragNr_Name";
				case 9:
					return "VertragNr_Email";
				case 10:
					return "Smc_Email";
				case 11:
					return "Smc_Name";
				case 12:
					return "KundenNrL";
				case 13:
					return "KundenNr_Name";
				case 14:
					return "KundenNr_Email";
				case 15:
					return "ZIP_LastnameFirstname";
				case 16:
					return "ZIP_LastnameStreet";
				case 17:
					return "Bank_Lastname";
				case 18:
					return "Bank_Email";
			} // else @TODO: END Zustand setzen
		}
		return INTERIM + stepReporting;
	}

	private long fillDuration(CDocument cdoc, String step, Map<String, Object> map, boolean readonly) {
		assert step != null : "Reporting step is not set!";

		long sysdate = System.currentTimeMillis();
		Double exactDuration = 0d;
		if (step.equalsIgnoreCase(START) || step.equalsIgnoreCase(RESUME)) {
			Long lasttimestamp = null;
			if (cdoc != null) {
				lasttimestamp = (Long) cdoc.getNote(LASTTIMESTAMP);
			}
			if (lasttimestamp != null) {
				exactDuration = (sysdate - lasttimestamp) / 1000d;
			}
			map.put(STARTTIMESTAMP, sysdate);
		} else if (step.equalsIgnoreCase(END) || step.equalsIgnoreCase(SUSPEND)) {
			Long starttimestamp = (Long) map.get(STARTTIMESTAMP);
			if (starttimestamp != null) {
				exactDuration = (sysdate - starttimestamp) / 1000d;
			}
		} else { //if (step.startsWith(INTERIM)) // muss auch für 410 Schritte funktionieren
			Long interimtimestamp = (Long) map.get(INTERIMTIMESTAMP);
			if (interimtimestamp != null) {
				exactDuration = (sysdate - interimtimestamp) / 1000d;
			}
		}

		if (!readonly) {
			map.put(INTERIMTIMESTAMP, sysdate);
			if (cdoc != null) {
				cdoc.setNote(LASTTIMESTAMP, sysdate);
			}
		}
		return Math.round(exactDuration);
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
		String logPrefix = getClass().getName() + "#" + name + ":";
		SkyLogger.getItyxLogger().error(logPrefix + " abortEXECUTE");

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
		String message = id + ": ROLLBACK:" + parameter + ":";
		SkyLogger.getItyxLogger().error(message);
	}

	@Override
	public void execute(IFlowObject flowObject) throws Exception {
		throw new Exception("Deprecated call");

	}
}
