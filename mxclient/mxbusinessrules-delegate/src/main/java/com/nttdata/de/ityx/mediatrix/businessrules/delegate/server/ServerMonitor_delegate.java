/**
 *
 */
package com.nttdata.de.ityx.mediatrix.businessrules.delegate.server;

import de.ityx.mediatrix.api.interfaces.businessrules.server.IServerMonitor;
import de.ityx.mediatrix.data.Email;
import de.ityx.mediatrix.data.Operator;
import de.ityx.mediatrix.data.Subproject;

import java.sql.Connection;
import java.util.HashMap;

/**
 * @author DHIFLM
 */
public class ServerMonitor_delegate implements IServerMonitor {

	IServerMonitor delegate = com.nttdata.de.ityx.mediatrix.businessrules.factories.NTTDataBusinessruleFactoryGenerator.createFactory(true).getServerMonitor();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.ityx.mediatrix.api.interfaces.businessrules.server.IServerMonitor#
	 * postMonitorEnter(java.sql.Connection, de.ityx.mediatrix.data.Email,
	 * de.ityx.mediatrix.data.Operator, de.ityx.mediatrix.data.Subproject)
	 */
	@Override
	public HashMap postMonitorEnter(Connection arg0, Email arg1, Operator arg2, Subproject arg3) {
		return delegate.postMonitorEnter(arg0, arg1, arg2, arg3);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.ityx.mediatrix.api.interfaces.businessrules.server.IServerMonitor#
	 * postMonitorSend(java.sql.Connection, de.ityx.mediatrix.data.Email,
	 * de.ityx.mediatrix.data.Operator, de.ityx.mediatrix.data.Subproject)
	 */
	@Override
	public HashMap postMonitorSend(Connection arg0, Email arg1, Operator arg2, Subproject arg3) {
		return delegate.postMonitorSend(arg0, arg1, arg2, arg3);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.ityx.mediatrix.api.interfaces.businessrules.server.IServerMonitor#
	 * preMonitorEnter(java.sql.Connection, de.ityx.mediatrix.data.Email,
	 * de.ityx.mediatrix.data.Operator, de.ityx.mediatrix.data.Subproject)
	 */
	@Override
	public HashMap preMonitorEnter(Connection arg0, Email arg1, Operator arg2, Subproject arg3) {
		return delegate.preMonitorEnter(arg0, arg1, arg2, arg3);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.ityx.mediatrix.api.interfaces.businessrules.server.IServerMonitor#
	 * preMonitorSend(java.sql.Connection, de.ityx.mediatrix.data.Email,
	 * de.ityx.mediatrix.data.Operator, de.ityx.mediatrix.data.Subproject)
	 */
	@Override
	public HashMap preMonitorSend(Connection arg0, Email arg1, Operator arg2, Subproject arg3) {
		return delegate.postMonitorSend(arg0, arg1, arg2, arg3);
	}

}
