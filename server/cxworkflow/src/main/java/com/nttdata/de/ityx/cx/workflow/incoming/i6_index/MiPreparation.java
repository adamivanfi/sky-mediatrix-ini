package com.nttdata.de.ityx.cx.workflow.incoming.i6_index;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.document.CDocument;
import de.ityx.contex.interfaces.extag.TagMatch;

import java.util.List;

public class MiPreparation extends AbstractWflBean {

	public void execute(IFlowObject flowObject) throws Exception {

		CDocumentContainer con = (CDocumentContainer) flowObject.get("doc");
		CDocument document = con.getDocument(0);
		List<TagMatch> tags = con.getTags();

		String customer = "";
		String contract = "";

		if (con.getTags() != null && !con.getTags().isEmpty()) {
			TagMatch tm = (TagMatch) con.getTags().get(0);
			customer = tm.getTagValue("CustomerID");
			contract = tm.getTagValue("ContractNumber");
		}

		tags.add(new TagMatch(getMITag(), "true"));
		con.setTags(tags);

		String val = document.getFormtype();
		document.setNote(TagMatchDefinitions.EVAL_FORMTYPE_BEFORE_VALIDATION, val);

		customer = customer != null ? customer : "";
		document.setNote(TagMatchDefinitions.EVAL_CUSTOMER_NUMBER_BEFORE_VALIDATION, customer);

		contract = contract != null ? contract : "";
		document.setNote(TagMatchDefinitions.EVAL_CONTRACT_NUMBER_BEFORE_VALIDATION, contract);
		document.setNote("EvalPreTimestamp", "" + System.currentTimeMillis());
		flowObject.put("docid", document.getNote(TagMatchDefinitions.DOCUMENT_ID));

	}

	public String getMITag(){
		return "validation";
	}
}
