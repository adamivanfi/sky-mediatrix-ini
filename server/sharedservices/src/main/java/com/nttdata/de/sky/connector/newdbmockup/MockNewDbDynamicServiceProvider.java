package com.nttdata.de.sky.connector.newdbmockup;

import com.nttdata.de.lib.logging.SkyLogger;
import de.sky.integration.web.customerdataservice._1.PortType;

import javax.xml.ws.Endpoint;

/**
 * This thread starts the implementation of the DynamicCustomerData web service
 * as a thread that is used for unit tests.
 *
 * @author vogtda
 */
public class MockNewDbDynamicServiceProvider extends Thread {

	public static String address = "http://localhost:13100/BusinessService/CustomerDataService";
	public static String setupScript = null;
	public static String dataScript = null;
	public static String cleanupScript = null;

	private PortType implementor;
	private Endpoint e;

	public static void main(String args[]) throws java.lang.Exception {
		if (args.length > 0)
			MockNewDbDynamicServiceProvider.address = args[0];
		if (args.length > 1)
			MockNewDbDynamicServiceProvider.setupScript = args[1];
		if (args.length > 2)
			MockNewDbDynamicServiceProvider.dataScript = args[2];

		new MockNewDbDynamicServiceProvider().start();
	}

	public void run() {
		SkyLogger.getMediatrixLogger().info("Starting Provider at " + address);
		implementor = new MockNewDbDynamicPortTypeImpl(setupScript, dataScript);
		e = Endpoint.publish(address, implementor);
	}

	public void shutdown() {
		//ToDo
		//implementor.cleanupDatabase(cleanupScript);
		if (e != null)
			e.stop();
	}
}
