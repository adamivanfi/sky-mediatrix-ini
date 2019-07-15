package com.nttdata.de.ityx.cx.workflow.incoming.i2_preprocessing;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.document.CDocument;

public class AfterOCR extends AbstractWflBean {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 2270338580022642316L;

	@Override
	public void execute(IFlowObject flowObject) throws Exception {
		CDocument cdoc = DocContainerUtils.getDoc(flowObject);
		String id = DocContainerUtils.getDocID(cdoc);

		CDocumentContainer ocrdoc = (CDocumentContainer)flowObject.get("ocrdoc");
		ocrdoc.setExternalID(id);
		CDocument img = ocrdoc.getDocument(0);
		img.setNote("DocumentID",id);
		flowObject.put("ocrdoc","");
		flowObject.put("doc",ocrdoc);		
	}

}
