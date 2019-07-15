package com.nttdata.de.ityx.cx.workflow.outbound;

import com.nttdata.de.ityx.cx.workflow.outgoing.mxpostprocessing.MxDbSingleton;
import com.nttdata.de.ityx.sharedservices.configuration.BeanConfig;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import com.nttdata.de.sky.outbound.MxOutboundIntegration;
import de.ityx.contex.interfaces.designer.IExflowState;
import de.ityx.contex.interfaces.designer.IFlowObject;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public abstract class CallbackPooler extends OutboundPooler {

	public void execute(IFlowObject flowObject, IExflowState exflowState) throws Exception {
		Connection con = null;
		try {
			con = MxDbSingleton.getMxConnection(flowObject);
			
			List<Map<String, Object>> docsMetaList = MxOutboundIntegration.getCxOutboundDocumentProcessesFromDB(con, flowObject, getProcessname(), getMaster(), BeanConfig.getInt(MxOutboundIntegration.MX2CX_MAXCOUNT ,200));

			if (docsMetaList != null && docsMetaList.size() > 0) {
				SkyLogger.getWflLogger().debug(">" + getProcessname() + "<: list" + docsMetaList.size());

				docsMetaList = prepareIterationProcessor(flowObject, docsMetaList);
				for (Map<String, Object> docMeta : docsMetaList) {
					String docId = "";
					long logID = 0L;
					try {
						docId = (String) docMeta.get(TagMatchDefinitions.DOCUMENT_ID);
						logID = (Long) docMeta.get(MxOutboundIntegration.MX2CX_ID);

						addReportingEntryForItem(flowObject, exflowState, docId, logID, 0);
						itemProcessor(flowObject, exflowState, docMeta, docId, logID);
						SkyLogger.getWflLogger().info(getProcessname() + ": " + docId + " l:" + logID + " markAsWaitingForCallback.");
						MxOutboundIntegration.updateDbStatusOfCxOutboundDocumentQueue(con, logID, MxOutboundIntegration.MXOUT_STATUS.WAITFORCB,null);

						addReportingEntryForItem(flowObject, exflowState, docId, logID, Integer.MAX_VALUE);
					} catch (Exception e) {
						if (logID > 0L) {
							MxOutboundIntegration.updateDbStatusOfCxOutboundDocumentQueue(con, logID, MxOutboundIntegration.MXOUT_STATUS.PROCERR,e.getMessage());
							SkyLogger.getWflLogger().error(getProcessname() + ": " + docId + " l:" + logID + " Unable to process document. MARKED as FAILED:" + e.getMessage(), e);
						} else {
							SkyLogger.getWflLogger().error(getProcessname() + ": " + docId + " l:" + logID + " Unable to process document. e:" + e.getMessage(), e);
						}
					}
					try{
						if(!con.getAutoCommit()){
							con.commit();
						}
					}catch (SQLException e){
						SkyLogger.getWflLogger().error(getProcessname() + ": " + docId + " l:" + logID + " Unable to commit changes:" + e.getMessage(), e);
					}
				}
				docsMetaList = postIterationProcessor(flowObject, docsMetaList);
				flowObject.put(DOCS_METALIST, docsMetaList);
				flowObject.put(DOCS_METALISTCOUNTER, docsMetaList.size());
			} else {
				//SkyLogger.getWflLogger().debug(">" + getProcessname() + "<: emptyList");
				flowObject.put(DOCS_METALIST, docsMetaList);
				flowObject.put(DOCS_METALISTCOUNTER, 0);
			}
		} finally {
			MxDbSingleton.closeConnection(con);
		}
	}
}
