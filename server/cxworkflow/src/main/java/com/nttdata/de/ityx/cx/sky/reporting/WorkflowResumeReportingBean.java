package com.nttdata.de.ityx.cx.sky.reporting;


public class WorkflowResumeReportingBean extends WorkflowReportingBean {
	@Override
	public String getStep(Integer stepReporting, int currentprocess) {
		return RESUME;
	}
	
}
