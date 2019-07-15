/**
 *
 */
package com.nttdata.de.sky.ityx.mediatrix.businessrules.server;

import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.mediatrix.businessrules.server.sky.SkyServerMonitor;
import de.ityx.mediatrix.api.interfaces.businessrules.server.IServerMonitor;
import de.ityx.mediatrix.data.Email;
import de.ityx.mediatrix.data.Operator;
import de.ityx.mediatrix.data.Subproject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.util.HashMap;

public class ServerMonitor implements IServerMonitor {

	private IServerMonitor skydel = new SkyServerMonitor();
	private IServerMonitor outbound_delegate = new de.ityx.sky.outbound.server.ServerMonitor();
	private IServerMonitor agenturdel=null;
	private final String aclazz = "de.ityx.agentursteuerung.ServerMonitorRule";

	public ServerMonitor() {
		String logPrefix =  "ServerMonitor # Constructor ";
		try {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " initalization");
			Class aclass=Class.forName(aclazz);
			if (aclass!=null){
				Constructor aconstr=aclass.getConstructor(null);
				if (aconstr!=null){
					agenturdel = (IServerMonitor) aconstr.newInstance(null);
				}
			}
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " initalized");

		} catch (NullPointerException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
			SkyLogger.getBRSLogger().warn(logPrefix + aclazz + " cannot be instantiated." + e.getMessage());
		}
	}

	@Override
	public HashMap postMonitorEnter(Connection arg0, Email arg1, Operator arg2, Subproject arg3) {
		String logPrefix = getClass().getName() + "#" + new Object() {}.getClass().getEnclosingMethod().getName() + (arg1!=null?" email:"+arg1.getEmailId()+" ":" ");
		SkyLogger.getBRSLogger().info(logPrefix + " start");
		HashMap hm = skydel.postMonitorEnter(arg0, arg1, arg2, arg3);
		HashMap hmo = outbound_delegate.postMonitorEnter(arg0, arg1, arg2, arg3);
		if(hmo!=null)
			{hm.putAll(hmo);
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.postMonitorEnter(arg0, arg1, arg2, arg3));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	@Override
	public HashMap postMonitorSend(Connection arg0, Email arg1, Operator arg2, Subproject arg3) {
		String logPrefix = getClass().getName() + "#" + new Object() {}.getClass().getEnclosingMethod().getName() + (arg1!=null?" email:"+arg1.getEmailId()+" ":" ");

		SkyLogger.getBRSLogger().info(logPrefix + " start :arg0"+arg0+":arg1:"+arg1+":arg2:"+arg2+"arg3:"+arg3);
		HashMap hm = skydel.postMonitorSend(arg0, arg1, arg2, arg3);
		SkyLogger.getBRSLogger().info(logPrefix + " finish");
		//hm.putAll(outbound_delegate.postMonitorSend(arg0, arg1, arg2, arg3));
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.postMonitorSend(arg0, arg1, arg2, arg3));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	@Override
	public HashMap preMonitorEnter(Connection arg0, Email arg1, Operator arg2, Subproject arg3) {
		String logPrefix = getClass().getName() + "#" + new Object() {}.getClass().getEnclosingMethod().getName() + (arg1!=null?" email:"+arg1.getEmailId()+" ":" ");
		SkyLogger.getBRSLogger().info(logPrefix + " start");
		HashMap hm = skydel.preMonitorEnter(arg0, arg1, arg2, arg3);
		HashMap hmod=outbound_delegate.preMonitorEnter(arg0, arg1, arg2, arg3);
		if (hmod!=null) {
			hm.putAll(hmod);
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.preMonitorEnter(arg0, arg1, arg2, arg3));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	@Override
	public HashMap preMonitorSend(Connection arg0, Email arg1, Operator arg2, Subproject arg3) {
		String logPrefix = getClass().getName() + "#" + new Object() {}.getClass().getEnclosingMethod().getName() + (arg1!=null?" email:"+arg1.getEmailId()+" ":" ");
		SkyLogger.getBRSLogger().info(logPrefix + " start");
		HashMap hm = skydel.preMonitorSend(arg0, arg1, arg2, arg3);
		HashMap hmod=outbound_delegate.preMonitorSend(arg0, arg1, arg2, arg3);
		if (hmod!=null) {
			hm.putAll(hmod);
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.preMonitorSend(arg0, arg1, arg2, arg3));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

}
