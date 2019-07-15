package com.nttdata.de.ityx.cx.workflow.incoming;

import com.nttdata.de.ityx.cx.workflow.base.FlowUtils;
import com.nttdata.de.ityx.sharedservices.utils.ShedulerUtils;
import de.ityx.contex.interfaces.designer.IFlowObject;

/**
 * Created by meinusch on 07.10.15.
 * Writes document to default docpool and schedules process with name in flow variable "parameter".
 */
public class DocpoolWrite extends DocpoolBean {

	@Override
	public String getMaster() {
		return ShedulerUtils.getDefaultMaster();
	}

	@Override
	public String getParameter(IFlowObject flow) {
		String ret = "default";
		try {
			ret = FlowUtils.getRequiredNonEmptyString(flow, "parameter");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
}
