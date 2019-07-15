package com.nttdata.de.ityx.cx.workflow.incoming.i4_customerindexing;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;
import com.nttdata.de.ityx.cx.workflow.base.FlowUtils;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import de.ityx.contex.impl.designer.state.KeyConfiguration;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.document.CDocument;

public class FHVM_AfterDoneExtraction extends AbstractWflBean {

	@Override
	public void execute(IFlowObject flowObject) throws Exception {

		CDocumentContainer<CDocument> cont = (CDocumentContainer) flowObject.get("xdoc");
		if (cont != null) {
			flowObject.put("doc", cont);
		}
		Boolean verifiedDocument = (Boolean) flowObject.get("VerifiedDocument");
		String formtype = DocContainerUtils.getFormtype(flowObject);
		if (formtype == null || formtype.contains("systemdefault")) {
			verifiedDocument = false;
			flowObject.put("VerifiedDocument", false);
		}
		if (verifiedDocument) {
			flowObject.put("parameter", FlowUtils.getRequiredString(flowObject, "FHV_docStateMandateSplit"));
		} else {
			flowObject.put("parameter", FlowUtils.getRequiredString(flowObject, "FHV_docStateManualValidation"));
		}
	}
        
    @Override
    public KeyConfiguration[] getKeys() {
        return new KeyConfiguration[]{
				new KeyConfiguration("parameter", String.class),
				new KeyConfiguration("VerifiedDocument", Boolean.class)
		};
    }
}
