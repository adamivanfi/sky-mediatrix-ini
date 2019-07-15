package com.nttdata.de.ityx.cx.workflow.incoming.i5_crm;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.document.CDocument;

public class PrepareSiebelContactCreation extends AbstractWflBean {

	@Override
	public void execute(IFlowObject flowObject) throws Exception {

		CDocument document = DocContainerUtils.getDoc(flowObject);
		if (document != null) {
			String docid = DocContainerUtils.getDocID(document);
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
				SkyLogger.getWflLogger().info("No VerifiedCustomerNumber for DocumentID: " + docid);
			}

			String channel = (String) document.getNote(TagMatchDefinitions.CHANNEL);
			if (channel != null) {
				flowObject.put(TagMatchDefinitions.CHANNEL, channel);
			}
		}else{
			SkyLogger.getWflLogger().info("Problem with PrepareSiebelContactCreation: no Document provided");
		}

	}

}
