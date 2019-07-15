package de.ityx.sky.outbound.server;

import de.ityx.mediatrix.api.interfaces.businessrules.server.IServerMonitor;
import de.ityx.mediatrix.data.Email;
import de.ityx.mediatrix.data.Operator;
import de.ityx.mediatrix.data.Subproject;
import de.ityx.mediatrix.modules.tools.logger.Log;
import de.ityx.sky.outbound.common.ServerUtils;
import de.ityx.sky.outbound.server.base.ServerBaseMonitor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

public class ServerMonitor extends ServerBaseMonitor {

	@Override
	public HashMap preMonitorSend(Connection con, Email email,			Operator operator, Subproject subproject) {
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put(IServerMonitor.PARAM_MONITOR_ACTION, "true");
		try {
			if (email.getType() == Email.TYPE_LETTER || email.getType() == Email.TYPE_FAX) {
				ServerUtils.createPdf(con, email);
				ServerUtils.complete(con, email, true);
				ServerUtils.archive(con, email);
			}
		} catch (Exception e) {
			try {
				ServerUtils.requeue(con, email);
				hm.put(IServerMonitor.PARAM_MONITOR_ACTION, "false");
			}
			catch (SQLException e1) {
				Log.exception(e1);
			}
			Log.exception(e);
		}
		return hm;
	}

}
