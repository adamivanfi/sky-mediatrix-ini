/**
 *
 */
package com.nttdata.de.ityx.mediatrix.businessrules.delegate.server;

import de.ityx.mediatrix.api.interfaces.businessrules.server.IServerMultiChannel;
import de.ityx.mediatrix.data.Account;
import de.ityx.mediatrix.data.Customer;
import de.ityx.mediatrix.data.Email;
import de.ityx.mediatrix.data.Project;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;


public class ServerMultiChannel_delegate implements IServerMultiChannel {

	private IServerMultiChannel delegate = com.nttdata.de.ityx.mediatrix.businessrules.factories.NTTDataBusinessruleFactoryGenerator.createFactory(true).getServerMultiChannel();
	

	@Override
	public void assignLanguage(Connection arg0, Email arg1) throws SQLException {
		delegate.assignLanguage(arg0, arg1);
	}

	@Override
	public boolean isValidateReceiver(Connection connection, String s) {
		return delegate.isValidateReceiver(connection, s);
	}

	@Override
	public HashMap deliverToChannel(Connection arg0, Email arg1) throws SQLException {
		return delegate.deliverToChannel(arg0, arg1);
	}

	@Override
	public Customer findCustomerForChannel(Connection arg0, Customer arg1, Email arg2, Project arg3) throws SQLException {
		return delegate.findCustomerForChannel(arg0, arg1, arg2, arg3);
	}

	@Override
	public HashMap prepareForFilter(Connection arg0, Email arg1, Account arg2, Project arg3) throws SQLException {
		return delegate.prepareForFilter(arg0, arg1, arg2, arg3);
	}

}
