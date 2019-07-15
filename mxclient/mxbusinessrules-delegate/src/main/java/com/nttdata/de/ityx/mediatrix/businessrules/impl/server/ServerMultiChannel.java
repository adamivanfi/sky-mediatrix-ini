/**
 *
 */
package com.nttdata.de.ityx.mediatrix.businessrules.impl.server;

import com.nttdata.de.lib.logging.SkyLogger;
import de.ityx.mediatrix.api.interfaces.businessrules.server.IServerMultiChannel;
import de.ityx.mediatrix.data.Account;
import de.ityx.mediatrix.data.Customer;
import de.ityx.mediatrix.data.Email;
import de.ityx.mediatrix.data.Project;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

public class ServerMultiChannel implements IServerMultiChannel {

	@Override
	public void assignLanguage(Connection arg0, Email arg1) throws SQLException {
		SkyLogger.getBRSLogger().debug(getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ": call");
	}

	@Override
	public boolean isValidateReceiver(Connection connection, String s) {
		return true;
	}

	@Override
	public HashMap deliverToChannel(Connection arg0, Email arg1) throws SQLException {
		SkyLogger.getBRSLogger().debug(getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ": call");
		return null;
	}

	@Override
	public Customer findCustomerForChannel(Connection arg0, Customer arg1, Email arg2, Project arg3) throws SQLException {
		SkyLogger.getBRSLogger().debug(getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ": call");
		return null;
	}

	@Override
	public HashMap prepareForFilter(Connection arg0, Email arg1, Account arg2, Project arg3) throws SQLException {
		SkyLogger.getBRSLogger().debug(getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ": call");
		return new HashMap();
	}

}
