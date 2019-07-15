package com.nttdata.de.ityx.cx.workflow.outgoing.o8_lettershop;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.document.CDocument;

public class PrepareTransfer extends AbstractWflBean {

	@Override
	public void execute(IFlowObject flowObject) throws Exception {
		CDocumentContainer con = (CDocumentContainer) flowObject.get(DocContainerUtils.DOC);
		if (con != null) {
			CDocument doc = con.getDocument(0);
			String doctag = TagMatchDefinitions.DOCUMENT_ID;
			String docid = (String) doc.getNote(doctag);
			flowObject.put(doctag, docid);
		}
	}
}
