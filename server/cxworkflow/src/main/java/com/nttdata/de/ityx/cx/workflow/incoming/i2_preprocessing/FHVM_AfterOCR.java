package com.nttdata.de.ityx.cx.workflow.incoming.i2_preprocessing;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.ityx.sharedservices.utils.ShedulerUtils;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.document.CDocument;

public class FHVM_AfterOCR extends AbstractWflBean {
	@Override
	public void execute(IFlowObject flowObject) throws Exception {
		CDocument cdoc = DocContainerUtils.getDoc(flowObject);
		CDocumentContainer ocrdoc = (CDocumentContainer) flowObject.get("ocrdoc");
		CDocument img = ocrdoc.getDocument(0);
		DocContainerUtils.setDocID(ocrdoc, img, DocContainerUtils.getDocID(cdoc));

		String formtype = "fh_vertrag";
		DocContainerUtils.setFormtype(ocrdoc, img, formtype);

		img.setNote(TagMatchDefinitions.EVAL_FORMTYPE, formtype);
		flowObject.put("ocrdoc", "");
		flowObject.put("doc", ocrdoc);
		ShedulerUtils.storeDocAndScheduleProcess(ShedulerUtils.getDefaultUnit(), ShedulerUtils.getDefaultMaster(), "FHV_400_CustomerIndexing", ocrdoc, img);
	}
}
