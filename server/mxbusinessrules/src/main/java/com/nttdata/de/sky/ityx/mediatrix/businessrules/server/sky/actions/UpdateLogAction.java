package com.nttdata.de.sky.ityx.mediatrix.businessrules.server.sky.actions;

import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.lib.utils.MitarbeiterlogWriter;
import com.nttdata.de.sky.ityx.common.Actions;

import java.sql.Connection;
import java.util.List;

/**
 * Created by meinusch on 13.04.15.
 */
public class UpdateLogAction extends AServerEventAction {
	@Override
	public List actionPerformed(Connection con, String actionname, List parameters) throws Exception {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ": ";
		Boolean ret = false;
		Integer operator = (Integer) parameters.get(0);
		Integer questionid = (Integer) parameters.get(1);
		Integer aktion = (Integer) parameters.get(2);
		Long time = (Long) parameters.get(3);
		String parameter = (String) parameters.get(4);
		SkyLogger.getMediatrixLogger().info(logPrefix + "updateLogDirectly for:" +
				" operator:"+operator +
				" questionid:" + questionid+
				" aktion:" + aktion+
				" long:" +time
		);

		parameters.clear();
		try {
			ret = MitarbeiterlogWriter.updateLogDirectly(con, operator, questionid, aktion, time, parameter);
		} catch (Exception e){
			SkyLogger.getMediatrixLogger().error(logPrefix +"Error updateLogDirectly for:"+
					" operator:"+operator +
					" questionid:" + questionid+
					" msg:"+e.getMessage(),e);
			throw e;
		}
		parameters.add(ret);
		SkyLogger.getMediatrixLogger().info(logPrefix +"updateLogDirectly done for:"+
				" operator:"+operator +
				" questionid:" + questionid+
				" parameter:" +parameter
		);
		return parameters;
	}

	@Override
	public String[] getActionNames() {
		return new String[]{Actions.ACTION_UPDATELOG.name()};
	}
}
