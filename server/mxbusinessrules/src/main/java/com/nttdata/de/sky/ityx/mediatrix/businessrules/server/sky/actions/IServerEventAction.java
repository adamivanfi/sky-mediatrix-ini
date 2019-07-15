package com.nttdata.de.sky.ityx.mediatrix.businessrules.server.sky.actions;

import java.sql.Connection;
import java.util.List;

public interface IServerEventAction {

	/*
	return parameters
	 */
	public List actionPerformed(Connection con, String actionname, List parameters) throws Exception;

	public String[] getActionNames();
}
