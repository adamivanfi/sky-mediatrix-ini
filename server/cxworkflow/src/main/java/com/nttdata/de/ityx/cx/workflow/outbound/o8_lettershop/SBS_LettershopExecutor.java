package com.nttdata.de.ityx.cx.workflow.outbound.o8_lettershop;

import com.nttdata.de.ityx.sharedservices.configuration.BeanConfig;

/**
 * Created by meinusch on 16.04.15.
 */
public class SBS_LettershopExecutor extends LettershopExecutor {

	@Override
	public String getProcessname() {
		return "SBS_820_Outbound";
	}
	@Override
	public String getMaster() { return "sbs"; }

	public String getTargetPath() throws Exception {
		return BeanConfig.getReqString("MoveFileToLettershopSBS_DstDir");
	}
}
