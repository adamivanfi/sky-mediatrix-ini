package com.nttdata.de.ityx.cx.workflow.incoming.i3_classify;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.document.CDocument;

public class SetValidationFlaq extends AbstractWflBean {

	@Override
	public void execute(IFlowObject flowObject) throws Exception {
		CDocumentContainer cont = (CDocumentContainer)flowObject.get("doc");
		CDocument doc = cont.getDocument(0);
		Boolean doValidation = (Boolean) flowObject.get("doValidation");
		doc.setNote("doValidation",doValidation!=null?doValidation:new Boolean(true));
	}
}
