package com.nttdata.de.ityx.cx.workflow.incoming;

import com.nttdata.de.lib.logging.SkyLogger;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.document.CDocument;

/**
 * Writes document to SBS docpool and schedules process with name in flow variable "parameter".
 */
public abstract class SBS_DocpoolWrite extends DocpoolBean {

	public static final String SBS_MASTER = "sbs";
	@Override
	public String getMaster() {
		return SBS_MASTER;
	}

	@Override
	public void execute(IFlowObject flow) throws Exception {
		Object object = flow.get("doc");



		if (object != null && !object.getClass().equals(String.class)) {
			CDocumentContainer doc = (CDocumentContainer) object;
			CDocument cdoc = doc.getDocument(0);
			doc.setNote("DOCUMENTSOURCE", cdoc.getNote("Channel"));
			//doc.setNote("DOCUMENTKEY", cdoc.getNote("DocumentID"));
			try {
				super.execute(flow);
			}catch (Exception e) {
				SkyLogger.getWflLogger().error("Error:"+e.getMessage(), e);
			}
		}else{
			SkyLogger.getWflLogger().warn("Unexpected doctype during processing");
		}
	}
}