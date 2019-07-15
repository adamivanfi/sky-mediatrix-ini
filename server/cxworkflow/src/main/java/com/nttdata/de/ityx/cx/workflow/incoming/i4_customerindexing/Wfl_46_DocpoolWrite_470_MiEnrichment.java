package com.nttdata.de.ityx.cx.workflow.incoming.i4_customerindexing;

import com.nttdata.de.ityx.cx.sky.reporting.WorkflowReportingBean;
import com.nttdata.de.ityx.cx.workflow.incoming.DocpoolWrite;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import de.ityx.contex.impl.designer.state.StateResult;
import de.ityx.contex.interfaces.designer.IExflowState;
import de.ityx.contex.interfaces.designer.IFlowObject;


public class Wfl_46_DocpoolWrite_470_MiEnrichment extends DocpoolWrite {

	@Override
	public void execute(IFlowObject flow) throws Exception {
		flow.set("parameter", "470_MiEnrichment");
		super.execute(flow);
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
					return RESUME;
				}
			}).execute(flowObject, arg2, false);
			execute(flowObject);
			(new WorkflowReportingBean() {
				@Override
				public String getStep(Integer stepReporting, int currentprocess) {
					return END;
				}
			}).execute(flowObject, arg2, false);

		} catch (Exception e) {
			SkyLogger.getWflLogger().error("ARB ERROR: " + e.getMessage());
			e.printStackTrace();
		}
		return StateResult.STATEOK;
	}
}
