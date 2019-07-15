package com.nttdata.de.ityx.cx.workflow.outbound;

import com.nttdata.de.ityx.cx.sky.reporting.WorkflowReportingBean;
import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;
import com.nttdata.de.ityx.cx.workflow.outgoing.mxpostprocessing.MxDbSingleton;
import com.nttdata.de.ityx.sharedservices.configuration.BeanConfig;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import com.nttdata.de.sky.outbound.MxOutboundIntegration;
import de.ityx.contex.impl.designer.state.KeyConfiguration;
import de.ityx.contex.impl.designer.state.StateResult;
import de.ityx.contex.interfaces.designer.IExflowState;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.mediatrix.data.Question;

import java.sql.Connection;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class OutboundPooler extends AbstractWflBean {

	public static final String DOCS_METALIST = "docsMetaList";
	public static final String DOCS_METALISTCOUNTER = "docsMetaList";

	public abstract void itemProcessor(IFlowObject flowObject, IExflowState exflowState, Map<String, Object> docMeta, String docid, long logid) throws Exception;

	public abstract String getProcessname();

	public abstract String getMaster() ;

	public void execute(IFlowObject flowObject, IExflowState exflowState) throws Exception {
		Connection con = null;
		try {
			con = MxDbSingleton.getMxConnection(flowObject);			
			List<Map<String, Object>> docsMetaList = MxOutboundIntegration.getCxOutboundDocumentProcessesFromDB(con, flowObject, getProcessname(),getMaster(), BeanConfig.getInt(MxOutboundIntegration.MX2CX_MAXCOUNT, 200));
			if (docsMetaList != null && docsMetaList.size() > 0) {
				SkyLogger.getWflLogger().debug(">" + getProcessname() + "<: list" + docsMetaList.size());

				docsMetaList = prepareIterationProcessor(flowObject, docsMetaList);
				for (Map<String, Object> docMeta : docsMetaList) {
					String docId = "";
					long logID = 0L;
					try {
						logID = (Long) docMeta.get(MxOutboundIntegration.MX2CX_ID);
						if (docMeta.get(TagMatchDefinitions.DOCUMENT_ID)!=null) {
							docId = (String) docMeta.get(TagMatchDefinitions.DOCUMENT_ID);
						}else{
							throw new SQLDataException("Provided document with empty DocID logid:"+logID);
						}
						
						addReportingEntryForItem(flowObject, exflowState, docId, logID, 0);
						itemProcessor(flowObject, exflowState, docMeta, docId, logID);
						SkyLogger.getWflLogger().info(getProcessname() + ": " + docId + " l:" + logID + " markAsProcessed.");
						MxOutboundIntegration.updateDbStatusOfCxOutboundDocumentQueue(con, logID, MxOutboundIntegration.MXOUT_STATUS.PROCESSED, null);

						addReportingEntryForItem(flowObject, exflowState, docId, logID, Integer.MAX_VALUE);
					} catch (Exception e) {
						if (logID > 0L) {
							MxOutboundIntegration.updateDbStatusOfCxOutboundDocumentQueue(con, logID, MxOutboundIntegration.MXOUT_STATUS.PROCERR, e.getMessage());
							SkyLogger.getWflLogger().error(getProcessname() + ": " + docId + " l:" + logID + " Unable to process document. MARKED as FAILED:" + e.getMessage(), e);
						} else {
							SkyLogger.getWflLogger().error(getProcessname() + ": " + docId + " l:" + logID + " Unable to process document. e:" + e.getMessage(), e);
						}
					}
					try {
						if (!con.getAutoCommit()) {
							con.commit();
						}
					} catch (SQLException e) {
						SkyLogger.getWflLogger().error(getProcessname() + ": " + docId + " l:" + logID + " Unable to commit changes:" + e.getMessage(), e);
					}
				}
				docsMetaList = postIterationProcessor(flowObject, docsMetaList);
				flowObject.put(DOCS_METALIST, docsMetaList);
				flowObject.put(DOCS_METALISTCOUNTER, docsMetaList.size());
			} else {
				//SkyLogger.getWflLogger().debug(getProcessname() + ": emptyList ");
				flowObject.put(DOCS_METALIST, docsMetaList);
				flowObject.put(DOCS_METALISTCOUNTER, 0);
			}
		} finally {
			MxDbSingleton.closeConnection(con);
		}
	}




	public void addReportingEntryForItem(IFlowObject flowObject, IExflowState exflowState, String docId, long logID, int stepnr) throws SQLException {
		Map<String, Object> reportingmap;
		Object mobj = flowObject.get(WorkflowReportingBean.MAP_KEY);
		if (mobj != null && !mobj.getClass().equals(String.class)) {
			reportingmap = (Map<String, Object>) mobj;
		} else {
			reportingmap = new HashMap<>();
		}
		reportingmap.put(WorkflowReportingBean.REPORTING_STEP_COUNTER, stepnr);
		reportingmap.put(WorkflowReportingBean.RPT_STEPDETAIL, logID);
		flowObject.put(TagMatchDefinitions.DOCUMENT_ID, docId);
		getReportingBean().execute(flowObject, exflowState, false);
	}

	private WorkflowReportingBean reportingBean;

	public WorkflowReportingBean getReportingBean() {
		if (reportingBean == null) {
			reportingBean = new WorkflowReportingBean();
		}
		return reportingBean;
	}


	public List<Map<String, Object>> postIterationProcessor(IFlowObject flowObject, List<Map<String, Object>> docsMetaContainer) {
		return docsMetaContainer;
	}

	public List<Map<String, Object>> prepareIterationProcessor(IFlowObject flowObject, List<Map<String, Object>> docsMetaContainer) {
		return docsMetaContainer;
	}

	@Override
	public StateResult execute(int arg0, IFlowObject flowObject, IExflowState arg2) throws Exception {
		try {
			String docid = DocContainerUtils.getDocID(flowObject);
			if (docid != null) {
				parameterMap.put(Thread.currentThread().getId(), docid);
				setWflChannel(flowObject);
			}
			execute(flowObject, arg2);

		} catch (Exception e) {
			SkyLogger.getWflLogger().error("ARB ERROR: " + e.getMessage());
			e.printStackTrace();
		}
		return StateResult.STATEOK;
	}

	public boolean isSBS(Map<String, Object> docMeta,Question question){
		boolean isSbsProjectCX="sbs".equalsIgnoreCase((String) docMeta.get(TagMatchDefinitions.MX_MASTER));
		boolean isSbsProjectMX;
		if (question!=null) {
			isSbsProjectMX=TagMatchDefinitions.isSbsProject(question);
			if (isSbsProjectCX!=isSbsProjectMX){
				SkyLogger.getWflLogger().warn("810: Archive/CreateArchFiles: " + docid + " MasterConflict mx:q:" + question.getId()+":"+isSbsProjectMX  + " cx:" + isSbsProjectCX);

			}
		}else{
			isSbsProjectMX=false;
		}
		return isSbsProjectCX||isSbsProjectMX;
	}

	@Override
	public void execute(IFlowObject flowObject) throws Exception {
		throw new UnsupportedOperationException("OutboundPooler: unsupported operation. use extended execute.");
	}


	@Override
	public KeyConfiguration[] getKeys() {
		return new KeyConfiguration[]{new KeyConfiguration(DOCS_METALIST, List.class), new KeyConfiguration(DOCS_METALISTCOUNTER, Integer.class)};
	}
}
