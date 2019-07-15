package com.nttdata.de.ityx.cx.workflow.incoming.i4_customerindexing;

import com.nttdata.de.ityx.cx.sky.enrichment.EnrichmentBean;
import com.nttdata.de.ityx.cx.workflow.WorkflowConstants;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.ityx.sharedservices.utils.ShedulerUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.document.CDocument;
import de.ityx.contex.interfaces.extag.TagMatch;

import java.util.ArrayList;


public class CancellationWebFormCustIndexer extends EnrichmentBean {

	@Override
	public void execute(IFlowObject flowObject) throws Exception {
		ShedulerUtils.checkAuth();
		String docid = DocContainerUtils.getDocID(flowObject);
		CDocumentContainer cont = DocContainerUtils.getDocContainer(flowObject);
		CDocument doc = DocContainerUtils.getDoc(flowObject);
		assert doc!=null;
		assert cont!=null;
		String parameter = "400_CustomerIndexing_EMAIL";
		try{
			checkExtractedData(flowObject, docid);
			enrichContainer(flowObject, cont, true);
			parameter = "500_CRMActivity_EMAIL";
			SkyLogger.getWflLogger().debug("402:" + DocContainerUtils.getDocID(doc) + " CustomerVerified.");
		} catch (Exception e) {
			SkyLogger.getWflLogger().warn("402: " + docid + " Exception: "+e.getClass()+" Cause: "+e.getCause() +" Message: "+e.getMessage());
			DocContainerUtils.setFormtype(flowObject, cont, doc, WorkflowConstants.FORMTYPE_KUENDIGUNG);
			doc.setNote(TagMatchDefinitions.EVAL_FORMTYPE, WorkflowConstants.FORMTYPE_KUENDIGUNG_AUTO);
			SkyLogger.getWflLogger().warn("402:" + docid + " CustomerNotVerified -> downgrade Formtype to kuendigung");
		}
		flowObject.put("parameter", parameter);
		SkyLogger.getWflLogger().debug("402: " + docid + " " + parameter);
	}

	private void checkExtractedData(IFlowObject flowObject, String docId) throws Exception{
		String customerNumber = null;
		String smartcardNumber = null;
		assert flowObject.get(WorkflowConstants.WEBFORM_CUSTOMER)!=null;
		for (TagMatch customer : (ArrayList<TagMatch>) flowObject.get(WorkflowConstants.WEBFORM_CUSTOMER)) {
			assert customer!=null;
			String currCustomerNumber = customer.getTagValue(WorkflowConstants.WEBFORM_CUSTOMER_NUMBER);
			if (customerNumber == null && currCustomerNumber != null) {
				customerNumber = currCustomerNumber;
				flowObject.put("VerifiedCustomerNumber", currCustomerNumber);
				SkyLogger.getWflLogger().debug("402: "+docId+" customerNumber = " + currCustomerNumber);
			}
			String currSmartcardNumber = customer.getTagValue(WorkflowConstants.WEBFORM_SMARTCARD_NUMBER);
			if (smartcardNumber == null && currSmartcardNumber != null) {
				smartcardNumber = currSmartcardNumber;
				flowObject.put("VerifiedSmartcardNumber", currSmartcardNumber);
				SkyLogger.getWflLogger().debug("402: "+docId+" smartcardNumber = " + currSmartcardNumber);
			}
		}
		if (customerNumber == null ){
			throw new Exception("Missing CustomerNumber");
		}else if(smartcardNumber == null){
			throw new Exception("Missing SmartcardNumber. Cancellation");
		}
	}
	
}
