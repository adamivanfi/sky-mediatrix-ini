package com.nttdata.de.sky.ityx.mediatrix.businessrules.server;

import com.nttdata.de.sky.ityx.mediatrix.businessrules.server.sky.SkyServerMultiChannel;
import de.ityx.mediatrix.api.interfaces.businessrules.server.IServerMultiChannel;
import de.ityx.mediatrix.data.Account;
import de.ityx.mediatrix.data.Customer;
import de.ityx.mediatrix.data.Email;
import de.ityx.mediatrix.data.Project;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

public class ServerMultiChannel implements IServerMultiChannel {

	private IServerMultiChannel skdel = new SkyServerMultiChannel();

	@Override
	public void assignLanguage(Connection arg0, Email arg1) throws SQLException {
		skdel.assignLanguage(arg0, arg1);
	}

	@Override
	public boolean isValidateReceiver(Connection connection, String s) {
		return skdel.isValidateReceiver(connection, s);
	}

	@Override
	public HashMap deliverToChannel(Connection arg0, Email arg1) throws SQLException {
		//String logPrefix =  getClass().getName() + "#" + new Object() {}.getClass().getEnclosingMethod().getName();
		//SkyLogger.getMediatrixLogger().info(logPrefix);
		return skdel.deliverToChannel(arg0, arg1);
	}

	@Override
	public Customer findCustomerForChannel(Connection arg0, Customer arg1, Email arg2, Project arg3) throws SQLException {
		return skdel.findCustomerForChannel(arg0, arg1, arg2, arg3);
	}

	@Override
	public HashMap prepareForFilter(Connection arg0, Email arg1, Account arg2, Project arg3) throws SQLException {
		return skdel.prepareForFilter(arg0, arg1, arg2, arg3);
	}
}
