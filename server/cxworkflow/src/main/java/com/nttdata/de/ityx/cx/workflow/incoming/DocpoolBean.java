package com.nttdata.de.ityx.cx.workflow.incoming;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflReportedBean;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.ityx.sharedservices.utils.ShedulerUtils;
import de.ityx.contex.interfaces.designer.IFlowObject;

/**
 * Created by DHIFLM on 03.03.2016.
 * Writes document to the docpool defined by the abstract method getMaster() and schedules the process with the name that is stored in the flow variable "parameter".
 */
public abstract class DocpoolBean extends AbstractWflReportedBean {

	@Override
	public void execute(IFlowObject flow) throws Exception {
		ShedulerUtils.storeDocAndScheduleProcess(ShedulerUtils.getDefaultUnit(), getMaster(), getParameter(flow),  DocContainerUtils.getDocContainer(flow) , DocContainerUtils.getDoc(flow));
	}

	public abstract String getMaster();
	public abstract String getParameter(IFlowObject flow);
}

