package com.nttdata.de.ityx.cx.workflow.outgoing.o9_crm;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.sky.connector.siebel.SiebelConnectorImpl;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.document.CDocument;

public class PrepareSiebelAssociateBean extends AbstractWflBean {

	@Override
	public void execute(IFlowObject flow) throws Exception {

		String serviceUrl = (String) flow.get("Siebel_WSDL");
		if (serviceUrl == null || serviceUrl.isEmpty()) {
			serviceUrl = "http://10.96.53.10:13200/BusinessService/SiebelService"; // PROD
																					// Siebel
		}
		System.setProperty(SiebelConnectorImpl.SysProperty_SiebelService_Endpoint, serviceUrl);

		CDocumentContainer doc = (CDocumentContainer) flow.get(DocContainerUtils.DOC);
		if (doc != null) {
			CDocument document = doc.getDocument(0);
			copyNoteToFlow(TagMatchDefinitions.DOCUMENT_ID, document, flow);
			copyNoteToFlow(TagMatchDefinitions.CONTACT_ID, document, flow);
			copyNoteToFlow(TagMatchDefinitions.ACTIVITY_ID, document, flow);
			copyNoteToFlow(TagMatchDefinitions.CUSTOMER_ID, document, flow);
			copyNoteToFlow(TagMatchDefinitions.CHANNEL, document, flow);
                        copyNoteToFlow(TagMatchDefinitions.TAGMATCH_CONTRACT_NUMBER, document, flow);  
		}
	}

	private void copyNoteToFlow(String docTag, CDocument document, IFlowObject flow) {
		String note = (String) document.getNote(docTag);
		if (note != null && !note.isEmpty()) {
			flow.put(docTag, note);
		}
	}
}
