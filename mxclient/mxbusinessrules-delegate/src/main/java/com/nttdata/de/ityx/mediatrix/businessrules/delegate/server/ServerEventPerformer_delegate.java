package com.nttdata.de.ityx.mediatrix.businessrules.delegate.server;

import de.ityx.mediatrix.api.interfaces.businessrules.server.IServerEventPerformer;

import java.sql.Connection;
import java.util.List;

public class ServerEventPerformer_delegate implements IServerEventPerformer {

	IServerEventPerformer delegate = com.nttdata.de.ityx.mediatrix.businessrules.factories.NTTDataBusinessruleFactoryGenerator.createFactory(true).getServerEventPerformer();

	@Override
	public void actionPerformed(Connection arg0, String arg1, List arg2) {
		delegate.actionPerformed(arg0, arg1, arg2);
	}

}
