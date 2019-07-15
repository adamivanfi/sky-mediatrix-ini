package com.nttdata.de.ityx.cx.workflow.incoming.i4_customerindexing;

import com.nttdata.de.ityx.cx.workflow.incoming.DocpoolBean;
import de.ityx.contex.interfaces.designer.IFlowObject;

/**
 * Created by meinusch on 08.09.16.
 */
public class Wfl_480_SkyToSBS extends DocpoolBean {
	
	@Override
	public String getMaster() {
		return "sbs";
	}
	
	@Override
	public String getParameter(IFlowObject flow) {
		return "SBS_600_MXInjection";
	}
	
}
