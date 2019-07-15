package com.nttdata.de.ityx.cx.workflow.incoming.i6_index;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.document.CDocument;
import de.ityx.contex.interfaces.extag.TagMatch;

import java.util.ArrayList;

public class Postprocessing extends AbstractWflBean {

	@Override
	public void execute(IFlowObject flowObject) throws Exception {
		CDocumentContainer con = DocContainerUtils.getDocContainer(flowObject);
		CDocument document = DocContainerUtils.getDoc(flowObject);
		String docid=DocContainerUtils.getDocID(document);
		String customer = null;
		String contract = null;
		String smartcard = null;
		if (con.getTags() != null && !con.getTags().isEmpty()) {
			for (TagMatch tm : (ArrayList<TagMatch>) con.getTags()) {
				String key = tm.getIdentifier();
				switch (key) {
					case "ManualValidation":
						customer = tm.getTagValue("CustomerID");
						contract = tm.getTagValue("ContractNumber");
						smartcard = tm.getTagValue("SmartcardNumber");
						break;
					case "CustomerID":
					case "VerifiedCustomerNumber":
						customer = tm.getTagValue();
						break;
					case "ContractNumber":
					case "VerifiedContractNumber":
						contract = tm.getTagValue();
						break;
					case "SmartcardNumber":
					case "VerifiedSmartcardNumber":
						smartcard = tm.getTagValue();
						break;
				}
			}
		}
		String info = (docid + " Cust:" + customer + ", ContractNr:" + contract + " SC:" + smartcard + " formtype:" + document.getFormtype());
		setWflStepDetail(flowObject, info);
		SkyLogger.getWflLogger().info("670: " + info + " CSize" + con.size());
	}
}
