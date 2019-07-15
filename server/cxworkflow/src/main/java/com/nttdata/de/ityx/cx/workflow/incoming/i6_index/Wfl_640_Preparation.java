package com.nttdata.de.ityx.cx.workflow.incoming.i6_index;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.document.CDocument;
import de.ityx.contex.interfaces.extag.TagMatch;

import java.util.Map;

public class Wfl_640_Preparation extends AbstractWflBean {

	@Override
	public void execute(IFlowObject flowObject) throws Exception {

		CDocumentContainer con = (CDocumentContainer) flowObject.get(getDocname());
		//String parameter = FlowUtils.getRequiredString(flowObject, "docStateMediatrixWrite");

		CDocument document = con.getDocument(con.size() - 1);
		String mandate = null;
		String customer = null;
		String contract = null;
		if (con.getTags() != null && !con.getTags().isEmpty()) {
			TagMatch tm = (TagMatch) con.getTags().get(0);
			trim(tm.getTagMatch("CustomerID"));
			trim(tm.getTagMatch("ContractNumber"));
			customer = tm.getTagValue("CustomerID");
			contract = tm.getTagValue("ContractNumber");
			mandate = tm.getTagValue(TagMatchDefinitions.SEPA_MANDATE_NUMBER);
			if (mandate==null || mandate.isEmpty()) {
				mandate = (String) document.getNote(TagMatchDefinitions.SEPA_MANDATE_NUMBER);
			}
		}

		document.setNote("EvalTimestamp", "" + System.currentTimeMillis());
		if (mandate != null) {
			flowObject.put("VerifiedMandateNumber", mandate);
		} else {
			SkyLogger.getWflLogger().info("Warning: No mandate specified");
			flowObject.put("VerifiedMandateNumber", "");
		}
		if (customer != null && !customer.trim().equals("0")) {
			flowObject.put("VerifiedCustomerNumber", customer);
			if (contract != null) {
				flowObject.put("VerifiedContractNumber", contract);
				flowObject.put("VerifiedDocument", true);
				//ToDo: Logik nicht nachvollziehbar
				//parameter = FlowUtils.getRequiredString(flowObject, "docStateWaitForContactId_SEPA");
			} else {
				SkyLogger.getWflLogger().info("Warning: No contract specified");
				flowObject.put("VerifiedContractNumber", "");
			}
		} else {
			SkyLogger.getWflLogger().info("Warning: No customer specified");
			flowObject.put("VerifiedCustomerNumber", "");
		}

		String docid = "";
		if (document.getNotes() != null && document.getNotes().get("DocumentID") != null) {
			docid = (String) document.getNotes().get("DocumentID");
		}

		String info = (docid + " Cust:" + customer + ", ContractNr:" + contract);

		Object mobj = flowObject.get("reporting_map");
		if (mobj != null && !mobj.getClass().equals(String.class)) {
			Map<String, Object> map = (Map<String, Object>) mobj;
			map.put("STEPDETAIL", info);
		}

		SkyLogger.getWflLogger().info(info + " ConSize" + con.size());

		flowObject.put("parameter", "600_MXInjection");
	}

	protected String getDocname() {
		return "doc";
	}

	protected void trim(TagMatch tm) {
		if (tm != null) {
			String textValue = tm.getTagValue();
			if (textValue != null && textValue.length() > 0) {
				String trim = textValue.trim();
				if (!trim.equals(textValue)) {
					tm.setTagValue(trim);
				}
			}
		}

	}

}
