package com.nttdata.de.ityx.cx.workflow.incoming.i1_import;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;

import de.ityx.contex.interfaces.designer.IFlowObject;

public class PrepareErrorBean extends AbstractWflBean{

	@Override
	public void execute(IFlowObject flowObject) throws Exception {
		flowObject.put("doc",flowObject.get("src"));
		
	}

}
