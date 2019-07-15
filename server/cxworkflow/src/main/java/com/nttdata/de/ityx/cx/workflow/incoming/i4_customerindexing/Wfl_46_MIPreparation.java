package com.nttdata.de.ityx.cx.workflow.incoming.i4_customerindexing;

import com.nttdata.de.ityx.cx.sky.reporting.WorkflowReportingBean;
import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.contex.impl.designer.state.StateResult;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.interfaces.designer.IExflowState;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.document.CDocument;
import de.ityx.contex.interfaces.extag.TagMatch;

import java.util.List;

/**
 * Created by meinusch on 12.10.15.
 */
public class Wfl_46_MIPreparation extends AbstractWflBean {

	@Override
	public void execute(IFlowObject flowObject) throws Exception {

		CDocumentContainer con =  DocContainerUtils.getDocContainer(flowObject);
		CDocument document = DocContainerUtils.getDoc(con);
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

	@Override
	public StateResult execute(int arg0, IFlowObject flowObject, IExflowState arg2) throws Exception {
		try {
			String docid = DocContainerUtils.getDocID(flowObject);
			if (docid != null) {
				parameterMap.put(Thread.currentThread().getId(), docid);
				setWflChannel(flowObject);
			}
			(new WorkflowReportingBean() {
				@Override
				public String getStep(Integer stepReporting, int currentprocess) {
					return START;
				}
			}).execute(flowObject, arg2, false);
			execute(flowObject);
			(new WorkflowReportingBean() {
				@Override
				public String getStep(Integer stepReporting, int currentprocess) {
					return SUSPEND;
				}
			}).execute(flowObject, arg2, false);

		} catch (Exception e) {
			SkyLogger.getWflLogger().error("ARB ERROR: " + e.getMessage());
			e.printStackTrace();
		}
		return StateResult.STATEOK;
	}

}
