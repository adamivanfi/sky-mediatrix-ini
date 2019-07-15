package com.nttdata.de.sky.ityx.mediatrix.businessrules.server.sky.actions;

import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.Actions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by meinusch on 13.04.15.
 */
public class ReasonsAction extends AServerEventAction {
	@Override
	public List actionPerformed(Connection con, String actionname, List parameters) throws Exception {
		// Logging.
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + " ";
		SkyLogger.getMediatrixLogger().debug(logPrefix + ": enter");
		SkyLogger.getMediatrixLogger().debug(logPrefix + "Starting: " + actionname);

		// Reads the parameters of the call.
		String actionString = (String) parameters.get(0);
		SkyLogger.getMediatrixLogger().debug(logPrefix + "Reading reasons for: " + actionString);
		parameters.clear();

		List<String> reasons = new ArrayList<>();
		try {
			PreparedStatement stmt = con.prepareStatement("select reason from reasons where action=? and invisible=0 order by id");
			stmt.setString(1, actionString);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				reasons.add(rs.getString("reason"));
			}
			rs.close();
		} catch (SQLException e) {
			SkyLogger.getMediatrixLogger().debug(logPrefix + " " + e.getMessage(), e);
		}

		parameters.add(reasons);
		return parameters;

	}

	@Override
	public String[] getActionNames() {

		return new String[]{Actions.ACTION_GET_REASONS.name()};
	}
}
