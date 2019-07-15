package com.nttdata.de.ityx.cx.sky.reporting;

import de.ityx.contex.interfaces.designer.IExflowState;
import de.ityx.contex.interfaces.designer.IFlowObject;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class WorkflowEndReportingBean extends WorkflowReportingBean {

	protected boolean           readonly = true;


@Override
	protected boolean isReadonly(){
		return true;
	}
	@Override
	public String getStep(Integer stepReporting, int currentprocess) {
		return END;
	}

	@Override
	public void execute(IFlowObject flow, IExflowState exflowstate, boolean readonly) throws SQLException {

		super.execute(flow, exflowstate,readonly);

		Map<String, Object> map = new HashMap<>();
		Object mobj = flow.get(MAP_KEY);
		if (mobj != null && !mobj.getClass().equals(String.class)) {
			map = (Map<String, Object>) mobj;
		} else {
			map = new HashMap<>();
		}
		map.put(REPORTING_STEP_COUNTER, 0);
		flow.put(MAP_KEY, map);
	}
}
