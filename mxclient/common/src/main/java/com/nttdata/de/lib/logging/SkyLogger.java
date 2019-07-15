package com.nttdata.de.lib.logging;

import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.api.interfaces.ILogger;
import de.ityx.mediatrix.modules.tools.logger.Log;
import de.ityx.mediatrix.modules.tools.logger.LoggerConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.xml.DOMConfigurator;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;


public class SkyLogger implements LoggerConfigurator {
	// static data

	//private static final String xmlConfigName = "log4j_sky.xml";

	static {
		String log4jfilename = "log4j_sky.xml";

		String log4jconfig = System.getProperty("ityx.conf", "c:/mediatrix/conf") + "/" + log4jfilename;

		String log4jconffile = System.getProperty("sky_logging", log4jconfig);
		//String log4jconffile = System.getProperty("user.home", "c:/mediatrix/conf") +  "/"+log4jfilename;

		synchronized (Logger.getLogger(SkyLogger.class)) {
			File l4fj_pre = new File(System.getProperty("user.home") + "/" + log4jfilename);
			if (l4fj_pre.exists() && l4fj_pre.canRead()) {
				log4jconffile = System.getProperty("user.home") + "/" + log4jfilename;
				String msg = "SKY LOG4j: using File:" + System.getProperty("user.home") + "/" + log4jfilename + " ";
				System.out.println(msg);
				System.err.println(msg);
			} else {
				String msg = "SKY LOG4j: File:" + System.getProperty("user.home") + "/" + log4jfilename + " not found or not readable.";
				System.out.println(msg);
				System.err.println(msg);
			}
			if (System.getProperty("ityx.home")==null ||System.getProperty("ityx.home").isEmpty()){
				System.setProperty("ityx.home", System.getProperty("user.home")+"/mediatrix");
			}
			if (System.getProperty("de.ntt.sky.servicename")==null || System.getProperty("de.ntt.sky.servicename").isEmpty()){
				System.setProperty("de.ntt.sky.servicename", "MXClient"+System.getProperty("user.name"));
			}
			URL log4jURL = null;
			try {
				File l4fj = new File(log4jconffile);
				if (l4fj.exists() && l4fj.canRead()) {
					log4jURL = l4fj.toURI().toURL();
				}
			} catch (MalformedURLException e) {
				Logger.getLogger(SkyLogger.class).info(SkyLogger.class.getClass().getName() + ":SKYLOGGER: not possible to load Log-Configuration from AbsolutePath: " + log4jconffile);
				e.printStackTrace();
			}

			if (log4jURL == null) {
				log4jURL = SkyLogger.class.getClassLoader().getResource(log4jconffile);
				Logger.getLogger(SkyLogger.class).info(SkyLogger.class.getClass().getName() + ":SKYLOGGER: Tying using Classloader AbsolutePath: " + log4jconffile);
			}
			if (log4jURL == null) {
				log4jURL = SkyLogger.class.getClassLoader().getResource("/" + log4jfilename);
				Logger.getLogger(SkyLogger.class).info(SkyLogger.class.getClass().getName() + ":SKYLOGGER: Tying using Classloader RelativePath: " + log4jconffile);
			}

			if (log4jURL == null) {
				log4jURL = SkyLogger.class.getClassLoader().getResource(log4jfilename);
				Logger.getLogger(SkyLogger.class).warn(SkyLogger.class.getClass().getName() + ":SKYLOGGER: Tying using Classloader RelativePath2: " + log4jconffile);
			}

			try {
				RollingFileAppender ra = new RollingFileAppender(); // versuche damit zu erzwingen dass Tomcat auch den RollingFileAppender lädt und die Logs rollt.
				DOMConfigurator.configure(log4jURL);
				Logger.getLogger(SkyLogger.class).warn("URL used for log4j: " + log4jURL);

			} catch (Exception e) {
				Logger.getLogger(SkyLogger.class).error("Unable to find resource: " + log4jURL, e);
			}
			ILogger mxlogger = Log.getLogger();

			// scheint buggy zu sein, bzw das gegenteil bewirken von dem was es tuen soll
			//mxlogger.setDebug(false);

			// ToDo - woher die Ports nehmen?
			// abschalten des Loggers geht nicht -> schmeisst exceptions in Logs dass Services nicht erreichbart sind
			// Geht nicht am Client!
			try {
				if (!API.isClient()) {
					int minport = 17000;
					int maxport = 18000;
					mxlogger.setRemoteLogger(SmartLogger.getInstance((new Random()).nextInt((maxport - minport) + 1) + minport));
				}
			} catch (IOException e) {
				getMediatrixLogger().warn("RemoteLoggerProblem:" + e.getLocalizedMessage(), e);
				System.err.println("RemoteLoggerProblem:" + e.getLocalizedMessage());
				e.printStackTrace();
			}
			System.out.println("Configured custom logging from " + log4jURL);
			getMediatrixLogger().info("Configured custom logging from " + log4jURL + " MXLoggerLevel:" + getMediatrixLogger().getLevel().toString());

		}
		}

	public final static String NTT_ITYX_LOGGER = "NTTItyxLogger";
	public final static String NTT_WFL_LOGGER = "NTTWflLogger";
	public final static String NTT_CXIE_LOGGER = "NTTCxIeLogger";
	public final static String NTT_COMMON_LOGGER = "NTTCommonLogger";
	public final static String NTT_BRS_LOGGER = "NTTBRSLogger";
	public final static String NTT_MEDIATRIX_LOGGER = "NTTMediatrixLogger";
	public final static String NTT_CONNECTOR_LOGGER = "NTTConnectorLogger";
	public final static String NTT_CLIENT_LOGGER = "MediatrixClientLogger";
	public final static String NTT_TEST_LOGGER = "TestLogger";

	public static Logger getWflLogger() {
		return Logger.getLogger(NTT_WFL_LOGGER);
	}

	public static Logger getBRSLogger() {
		return Logger.getLogger(NTT_BRS_LOGGER);
	}

	public static Logger getCommonLogger() {
		return Logger.getLogger(NTT_COMMON_LOGGER);
	}

	public static Logger getItyxLogger() {
		return Logger.getLogger(NTT_ITYX_LOGGER);
	}

	public static Logger getMediatrixLogger() {
		return Logger.getLogger(NTT_MEDIATRIX_LOGGER);
	}

	public static Logger getCXIELogger() {
		return Logger.getLogger(NTT_CXIE_LOGGER);
	}

	/**
	 * Returns the connector logger.
	 *
	 * @return the connectors logger
	 */
	public static Logger getConnectorLogger() {
		return Logger.getLogger(NTT_CONNECTOR_LOGGER);
	}

	/**
	 * Returns the logger for client side.
	 *
	 * @return the client side logger
	 */
	public static Logger getClientLogger() {
		return Logger.getLogger(NTT_CLIENT_LOGGER);
	}

	/**
	 * Returns the test logger used for test cases and test implementations.
	 *
	 * @return the test logger
	 */
	public static Logger getTestLogger() {
		return Logger.getLogger(NTT_TEST_LOGGER);
	}

	/**
	 * Returns the connector exception logger.
	 *
	 * @return the connector exception logger
	 */
	/*
	 * public static Logger getConnectorExceptionLogger() { return
	 * Logger.getLogger(CONNECTOR_EXCEPTION_LOGGER); }
	 */

	// private
	private SkyLogger() {
	}

	public static void main(String[] args) {
		SkyLogger.getTestLogger().debug("TestLogger debug");
		SkyLogger.getTestLogger().info("TestLogger info");
		SkyLogger.getTestLogger().warn("TestLogger warn");
		SkyLogger.getTestLogger().error("TestLogger error");

		SkyLogger.getItyxLogger().debug("ItyxLogger debug");
		SkyLogger.getItyxLogger().info("ItyxLogger info");
		SkyLogger.getItyxLogger().warn("ItyxLogger warn");
		SkyLogger.getItyxLogger().error("ItyxLogger error");

		SkyLogger.getConnectorLogger().debug("ConnectorLogger debug");
		SkyLogger.getConnectorLogger().info("ConnectorLogger info");
		SkyLogger.getConnectorLogger().warn("ConnectorLogger warn");
		SkyLogger.getConnectorLogger().error("ConnectorLogger error");

		SkyLogger.getMediatrixLogger().debug("MediatrixLogger debug");
		SkyLogger.getMediatrixLogger().info("MediatrixLogger info");
		SkyLogger.getMediatrixLogger().warn("MediatrixLogger warn");
		SkyLogger.getMediatrixLogger().error("MediatrixLogger error");

		SkyLogger.getClientLogger().debug("ClientLogger debug");
		SkyLogger.getClientLogger().info("ClientLogger info");
		SkyLogger.getClientLogger().warn("ClientLogger warn");
		SkyLogger.getClientLogger().error("ClientLogger error");
	}

	@Override
	public String getLogDebugMSSQL() {
		return SkyLogger.getMediatrixLogger().getLevel().toString();
	}

	@Override
	public String getLogFileSize() {
		return "50MB";
	}

	@Override
	public int getLogKeeps() {
		return 3;
	}

	@Override
	public String getLogLevelArchiv() {
		return SkyLogger.getMediatrixLogger().getLevel().toString();
	}

	@Override
	public String getLogLevelClient() {
		return SkyLogger.getClientLogger().getLevel().toString();
	}

	@Override
	public String getLogLevelEmail() {
		return SkyLogger.getMediatrixLogger().getLevel().toString();
	}

	@Override
	public String getLogLevelEscalation() {
		return SkyLogger.getMediatrixLogger().getLevel().toString();
	}

	@Override
	public String getLogLevelOutbound() {
		return SkyLogger.getMediatrixLogger().getLevel().toString();
	}

	@Override
	public String getLogLevelPtme() {
		return SkyLogger.getMediatrixLogger().getLevel().toString();
	}

	@Override
	public String getLogLevelWebapp() {
		return SkyLogger.getMediatrixLogger().getLevel().toString();
	}

	@Override
	public boolean isLogDb() {
		return false;
	}

	@Override
	public boolean isLogDebug() {
		return false;
	}

	@Override
	public boolean isWindowsEventLog() {
		return false;
	}
}
