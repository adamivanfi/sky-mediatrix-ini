package com.nttdata.de.ityx.cx.sky;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;
import com.nttdata.de.ityx.cx.workflow.base.FlowUtils;
import de.ityx.contex.impl.designer.state.StateResult;
import de.ityx.contex.interfaces.designer.IExflowState;
import de.ityx.contex.interfaces.designer.IFlowObject;

public class ErrorBean extends AbstractWflBean {


	@Override
	public StateResult execute(int arg0, IFlowObject arg1, IExflowState arg2)
			throws Exception {
		boolean doError = FlowUtils.getOptionalBoolean(arg1, "ErrorBean_GenerateError", false);
		if (doError)
			return StateResult.exception("ErrorBean");
		else
			return StateResult.STATEOK; 
	}

	@Override
	public void execute(IFlowObject flowObject) throws Exception {
		// shouldn't occurs since the method above overwrite the main entry point
		throw new UnsupportedOperationException("Problems in Exception Bean");
	}

}
