package com.nttdata.de.ityx.mediatrix.businessrules.factories;

import com.nttdata.de.lib.logging.SkyLogger;
import de.ityx.base.Global;

import java.io.FileInputStream;
import java.util.Properties;

/**
 * Generates factories that produce business rule implementations for usage with
 * the delegating rules in /src/main/resources/rules.
 *
 * @author DHIFLM
 */
public class NTTDataBusinessruleFactoryGenerator {

	public static Boolean isServer=true;
	
	/**
	 * Returns the factory.
	 *
	 * @param server Read the concrete factory name from the server config?
	 * @return A factory for business rules
	 */
	public static INttDataBusinessruleFactory createFactory(Boolean server) {
		isServer = server;
		INttDataBusinessruleFactory factory = null;
		String concreteBRFactory = loadConcreteFactoryName(server);
		try {
			factory = (INttDataBusinessruleFactory) Class.forName(concreteBRFactory).getConstructor(null).newInstance(null);
		} catch (Exception e) {
			if(isServer) {
				SkyLogger.getMediatrixLogger().error("Problems during initalization of ServerBusinessClassFactory: " + e.getMessage(), e);
			}
			else {
				SkyLogger.getClientLogger().error("Problems during initalization of ClientBusinessClassFactory: " + e.getMessage(), e);
			}
		}
		return returnFactory(factory);
	}

	private static INttDataBusinessruleFactory returnFactory(INttDataBusinessruleFactory factory) {
		if (factory == null) {
			if(isServer) {
				SkyLogger.getMediatrixLogger().error("!!!!!!!! Fallback to AbstractBusinessClassFactory !!!!!!!! ");
			}
			else {
				SkyLogger.getClientLogger().error("!!!!!!!! Fallback to AbstractBusinessClassFactory !!!!!!!! ");
			}
			factory = new AbstractNttDataBusinessruleFactory() {
			};
		}
		return factory;
	}

	private static String loadConcreteFactoryName(Boolean server) {
		String concreteBRFactory = null;
		if (server != null && server) {
			String propFile = Global.getProperty("br.props", Global.getProperty("ityx.conf",System.getProperty("ityx.conf", "c:/mediatrix/conf")) +"/br.properties");
			System.err.println("getDefaultFactory() propFile: " + propFile);
			SkyLogger.getMediatrixLogger().info("getDefaultFactory() propFile: " + propFile);
			if (propFile != null) {
				Properties p = new Properties();
				try {
					p.load(new FileInputStream(propFile));
					concreteBRFactory = p.getProperty("br.factory");
					System.err.println("getDefaultFactory() classname: " + concreteBRFactory);
					if (isServer) {
						SkyLogger.getMediatrixLogger().info("Prop: getDefaultFactory() classname: " + concreteBRFactory);
					}
					else {
						SkyLogger.getClientLogger().info("Prop: getDefaultFactory() classname: " + concreteBRFactory);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			concreteBRFactory = System.getProperty("br.factory");
			if (isServer) {
				SkyLogger.getMediatrixLogger().info("Sys:getDefaultFactory() classname: " + concreteBRFactory);
			}
			else {
				SkyLogger.getClientLogger().info("Sys:getDefaultFactory() classname: " + concreteBRFactory);
			}
		}
		return concreteBRFactory;
	}
}
