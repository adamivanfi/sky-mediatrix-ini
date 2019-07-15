package com.nttdata.de.sky.connector.siebel;

import com.nttdata.de.lib.logging.SkyLogger;

import javax.xml.ws.Endpoint;


/**
 * @author VOGTDA
 */
public class SiebelServiceProvider extends Thread {
	
	public static String address = "http://localhost:13200/BusinessService/SiebelService";
	public static String contexAddress = null;

	private Endpoint e;
	private Object impl;
	
	public static void main(String args[]) throws java.lang.Exception {
		if (args.length > 0)
			SiebelServiceProvider.address = args[0];
		if (args.length > 1)
			SiebelServiceProvider.contexAddress = args[1];


		new SiebelServiceProvider().start();
	}
	
	public void run() {
		SkyLogger.getConnectorLogger().info("Starting Provider at " + address);
		impl = new MockupSblDynamicPortTypeImpl(contexAddress);
		e = Endpoint.publish(address, impl);
	}
	
	public void shutdown() {
		if (e != null) {
			e.stop();
			((MockupSblDynamicPortTypeImpl) impl).shutdown();
		}
	}
}
