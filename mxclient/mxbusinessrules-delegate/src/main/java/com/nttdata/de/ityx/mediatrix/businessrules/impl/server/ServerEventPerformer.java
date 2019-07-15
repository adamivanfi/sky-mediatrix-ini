package com.nttdata.de.ityx.mediatrix.businessrules.impl.server;

import de.ityx.mediatrix.api.interfaces.businessrules.server.IServerEventPerformer;

import java.sql.Connection;
import java.util.List;

public class ServerEventPerformer implements IServerEventPerformer {

	@Override
	public void actionPerformed(Connection arg0, String arg1, List arg2) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);

	}

}
