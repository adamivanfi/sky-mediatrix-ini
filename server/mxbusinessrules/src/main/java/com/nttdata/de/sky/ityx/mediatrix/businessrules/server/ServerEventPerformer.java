package com.nttdata.de.sky.ityx.mediatrix.businessrules.server;

import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.mediatrix.businessrules.server.sky.SkyServerEventPerformer;
import de.ityx.mediatrix.api.interfaces.businessrules.server.IServerEventPerformer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.util.List;

public class ServerEventPerformer implements IServerEventPerformer {

	private SkyServerEventPerformer skydel = new SkyServerEventPerformer();
	private IServerEventPerformer agenturdel = null;
	private final String aclazz = "de.ityx.agentursteuerung.ServerEventPerformer";

	public ServerEventPerformer() {
		String logPrefix =  "ServerEventPerformer # Constructor ";
		try {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " initalization");
			Class aclass=Class.forName(aclazz);
			if (aclass!=null){
				Constructor aconstr=aclass.getConstructor(null);
				if (aconstr!=null){
					agenturdel = (IServerEventPerformer) aconstr.newInstance(null);
				}
			}SkyLogger.getBRSLogger().info(logPrefix + aclazz + " initalized");

		} catch (NullPointerException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
			SkyLogger.getBRSLogger().warn(logPrefix + aclazz + " cannot be instantiated." + e.getMessage());
		}
	}

	@Override
	public void actionPerformed(Connection arg0, String arg1, List arg2) {
		String logPrefix = getClass().getName() + "#" + new Object() {}.getClass().getEnclosingMethod().getName() + " "+arg1+" ";
		SkyLogger.getBRSLogger().info(logPrefix + " start");
		boolean matched = skydel.actionPerformedB(arg0, arg1, arg2);
		if (!matched && agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			agenturdel.actionPerformed(arg0, arg1, arg2);
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finished call");
		}
	}
}