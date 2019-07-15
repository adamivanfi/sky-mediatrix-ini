/**
 * 
 */
package com.nttdata.de.ityx.mediatrix.businessrules.impl.server;

import de.ityx.mediatrix.api.interfaces.businessrules.server.IServerMonitor;
import de.ityx.mediatrix.data.Email;
import de.ityx.mediatrix.data.Operator;
import de.ityx.mediatrix.data.Subproject;

import java.sql.Connection;
import java.util.HashMap;

/**
 * @author DHIFLM
 * 
 */
public class ServerMonitor implements IServerMonitor {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.ityx.mediatrix.api.interfaces.businessrules.server.IServerMonitor#
	 * postMonitorEnter(java.sql.Connection, de.ityx.mediatrix.data.Email,
	 * de.ityx.mediatrix.data.Operator, de.ityx.mediatrix.data.Subproject)
	 */
	@Override
	public HashMap postMonitorEnter(Connection arg0, Email arg1, Operator arg2,
			Subproject arg3) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return null;
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
	public HashMap postMonitorSend(Connection arg0, Email arg1, Operator arg2,
			Subproject arg3) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return null;
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
	public HashMap preMonitorEnter(Connection arg0, Email arg1, Operator arg2,
			Subproject arg3) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return null;
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
	public HashMap preMonitorSend(Connection arg0, Email arg1, Operator arg2,
			Subproject arg3) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return null;
	}

}
