package com.nttdata.de.ityx.cx.workflow.outgoing.mxpostprocessing;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.outbound.MxOutboundIntegration;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.document.CDocument;

import java.sql.Connection;

public class SaveStatus extends AbstractWflBean {

	@Override
	public void execute(IFlowObject flowObject) throws Exception {
		CDocument doc = DocContainerUtils.getDoc(flowObject);
		String docId = DocContainerUtils.getDocID(doc);
		long logID = (Long) doc.getNote("mx2cxId");
		SkyLogger.getWflLogger().debug("809SS: " + docId + " " + logID + " markAsProcessed");
		Connection con = null;
		try {
			con = MxDbSingleton.getMxConnection(flowObject);
			MxOutboundIntegration.updateDbStatusOfCxOutboundDocumentQueue(con, logID, MxOutboundIntegration.MXOUT_STATUS.PROCESSED, null);
		} finally {
			MxDbSingleton.closeConnection(con);
		}

		SkyLogger.getWflLogger().debug("809SS: " + docId + " " + logID + " finished");
	}
}
