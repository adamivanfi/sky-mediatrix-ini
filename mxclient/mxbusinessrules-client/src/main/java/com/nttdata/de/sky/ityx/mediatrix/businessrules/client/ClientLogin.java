package com.nttdata.de.sky.ityx.mediatrix.businessrules.client;

import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.PushToUserClient;
import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.api.interfaces.businessrules.client.IClientLogin;
import org.apache.log4j.Appender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.RollingFileAppender;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;

public class ClientLogin implements IClientLogin {

	IClientLogin outbound_delegate = new de.ityx.sky.outbound.client.ClientLogin();
	private IClientLogin agenturdel = null;
	private final String aclazz = "de.ityx.sky.outbound.client.ClientLogin_Agentur";

	public ClientLogin() {
		try {
			Class aclass=Class.forName(aclazz);
			if (aclass!=null){
				Constructor aconstr=aclass.getConstructor(null);
				if (aconstr!=null){
					agenturdel = (IClientLogin) aconstr.newInstance(null);
				}
			}
		} catch (NullPointerException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
			String logPrefix = "ClientLogin #Constructor:";
			//SkyLogger.getBRSLogger().warn(logPrefix + aclazz + " cannot be instantiated." + e.getMessage());
			System.out.print(logPrefix+ aclazz + " cannot be instantiated." + e.getMessage());
		}
	}

	@Override
	public boolean preLogin(String loginname) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + " " + loginname;
		SkyLogger.getBRSLogger().debug(logPrefix + " start");

		exchangeLoggingAppender();
		boolean ret = true;
		SkyLogger.getBRSLogger().debug(logPrefix + "preAttachmentView:");
		ret = ret && outbound_delegate.preLogin(loginname);
		
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret = ret && agenturdel.preLogin(loginname);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return ret;
		
	}

	public class CleanupMXtmp extends Thread {

		public void run() {
			// Removes temporary files from last run.
			File dir = new File(System.getProperty("user.home") + File.separator + "Mediatrix");
			File[] files = dir.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".ser");
				}
			});
			for (File f : files) {
				if (f.delete()) {
					SkyLogger.getClientLogger().debug("Deleted temporary file: " + f.getAbsolutePath());
				} else {
					SkyLogger.getClientLogger().debug("Could not delete temporary file: " + f.getAbsolutePath());
				}
			}
			File logFile = new File(dir, "mediatrix.log");
			try {
				FileWriter writer = new FileWriter(logFile);
				writer.write("unused, see appender configuration ");
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.print("Could not write " + dir + File.separator + "mediatrix.log: " + e.getMessage());
				SkyLogger.getClientLogger().error("Could not write " + dir + File.separator + "mediatrix.log: " + e.getMessage());
			}

		}


	}
	private void exchangeLoggingAppender() {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + " ";

		if (API.isClient()) {
			SkyLogger.getBRSLogger().debug(logPrefix + " start client");

			(new CleanupMXtmp()).start();

			// Clears default logfile and uses the Sky-Appender for logging.
			Enumeration appenders = SkyLogger.getClientLogger().getAllAppenders();
			RollingFileAppender skyClientAppender = (RollingFileAppender) appenders.nextElement();
			String skyAppenderName = skyClientAppender.getName();

			Logger logger = LogManager.getLogger(de.ityx.mediatrix.modules.tools.logger.MediatrixLog.class.getCanonicalName());
			appenders = logger.getAllAppenders();
			while (appenders.hasMoreElements()) {
				Appender appender = (Appender) appenders.nextElement();
				String appenderName = appender.getName();
				SkyLogger.getClientLogger().debug("mxAppender name: " + appenderName);
				if (appender.getName() == null || !appenderName.equals(skyAppenderName)) {
					appender.close();
				}
			}
			logger.removeAllAppenders();
			logger.addAppender(skyClientAppender);

			SkyLogger.getBRSLogger().debug("Calling Client.exchangeLoggingAppender for Client");
			if (SkyLogger.getBRSLogger().isDebugEnabled()) {
				String stacktrace = "";
				for (StackTraceElement e : Thread.currentThread().getStackTrace()) {
					stacktrace += " => " + e.getClassName() + ">" + e.getMethodName() + ":" + e.getLineNumber() + "\r\n";
				}
				SkyLogger.getBRSLogger().debug("ArchiveMetadata Problem durign collection of params: stacktrace:" + stacktrace);
			}
		} else {
			SkyLogger.getBRSLogger().debug(logPrefix + " start Server");

			SkyLogger.getBRSLogger().warn("Calling Client.exchangeLoggingAppender for Service");
			System.out.print("Calling Client.exchangeLoggingAppender for Service");
			if (SkyLogger.getBRSLogger().isDebugEnabled()) {
				String stacktrace = "";
				for (StackTraceElement e : Thread.currentThread().getStackTrace()) {
					stacktrace += " => " + e.getClassName() + ">" + e.getMethodName() + ":" + e.getLineNumber() + "\r\n";
				}
				SkyLogger.getBRSLogger().debug("ArchiveMetadata called by stacktrace:" + stacktrace);
			}
		}
	}

	@Override
	public int preLogout(String operator) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + " ";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");

		int ret = 0;
		ret += outbound_delegate.preLogout(operator);
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret += agenturdel.preLogout(operator);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return ret;
	}

	@Override
	public void postLogout(String operator) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + " ";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		outbound_delegate.postLogout(operator);
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			agenturdel.postLogout(operator);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
	}

	PushToUserClient cl = null;
	@Override
	public void postLogin(String operator) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + " ";
		SkyLogger.getBRSLogger().debug(logPrefix + " start " + operator);

		outbound_delegate.postLogin(operator);
		cl = new PushToUserClient(operator);
		cl.registerForNewMessage();

		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			agenturdel.postLogin(operator);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
	}
}
