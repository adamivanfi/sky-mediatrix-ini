package com.nttdata.de.ityx.cx.workflow.incoming.i5_crm;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.document.CDocument;
import de.ityx.contex.interfaces.extag.TagMatch;

import java.util.List;

public class PrepareMandateContactCreation extends AbstractWflBean {

	@Override
	public void execute(IFlowObject flowObject) throws Exception {

		CDocumentContainer con = (CDocumentContainer) flowObject.get("doc");
		if (con != null && con.getDocument(0) != null) {
			CDocument document = con.getDocument(0);
			String docid = (String) document.getNote(TagMatchDefinitions.DOCUMENT_ID);
			if (docid != null) {
				flowObject.put(TagMatchDefinitions.DOCUMENT_ID, docid);
			} else {
				SkyLogger.getWflLogger().debug("!!! No DocID for Siebel call !!!");
			}

			String customerid = (String) document.getNote(TagMatchDefinitions.EVAL_CUSTOMER_NUMBER);
			if (customerid != null) {
				flowObject.put("VerifiedCustomerNumber", customerid);
			} else {
				SkyLogger.getWflLogger().debug("!!! No VerifiedCustomerNumber for DocumentID: " + docid);
			}

			String contractid = (String) document.getNote(TagMatchDefinitions.EVAL_CONTRACT_NUMBER);
			if (contractid != null) {
				flowObject.put("VerifiedContractNumber", contractid);
			} else {
				SkyLogger.getWflLogger().debug("!!! No VerifiedContractNumber for DocumentID: " + docid);
			}

			String channel = (String) document.getNote(TagMatchDefinitions.CHANNEL);
			if (channel != null) {
				flowObject.put(TagMatchDefinitions.CHANNEL, channel);
			}

			flowObject.put(TagMatchDefinitions.FORM_TYPE_CATEGORY, TagMatchDefinitions.SEPA_MANDATE);
			DocContainerUtils.setFormtype(DocContainerUtils.getDocContainer(flowObject), document, TagMatchDefinitions.SEPA_MANDATE);

			// Sets signature flag.
			List<TagMatch> tags = document.getTags();
                        String validity = null;
			if (tags != null) {
				for (TagMatch tm : tags) {
					if (tm.getIdentifier().equals(TagMatchDefinitions.SEPA_SIGNATURE_FLAG)) {
						validity = tm.getTagValue();
						
					}
				}
			}
			if (validity != null && validity.equals("signed")){
				validity="1";
				document.setTitle(SEPA_MANDAT_AUTOMAT_VERARBEITUNG + document.getTitle());                                                            
			}
			// Ticket 245267
			// We don´t have to check the Flag SEPA_SIGNATURE_FLAG
			//old code Title changed
			/*else {
				validity="0";
				document.setTitle(SEPA_MANDAT_MANUELE_VERARBEITUNG + document.getTitle());
			}*/
			else {
				validity="0";
				document.setTitle(SEPA_MANDAT_AUTOMAT_VERARBEITUNG + document.getTitle());
			}
			document.setNote(TagMatchDefinitions.SEPA_SIGNATURE_FLAG, validity);
			flowObject.put(TagMatchDefinitions.SEPA_SIGNATURE_FLAG, validity);
			SkyLogger.getWflLogger().debug("DocumentID=" + docid + " " + TagMatchDefinitions.SEPA_SIGNATURE_FLAG + "=" + validity);			
		}
	}
}
