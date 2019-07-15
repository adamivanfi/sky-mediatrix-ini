package com.nttdata.de.sky.connector.contex;

import com.nttdata.de.lib.logging.SkyLogger;

import javax.xml.ws.Endpoint;


public class TestContexServiceProvider extends Thread {
	
	public static String address = "http://localhost:13300/BusinessServices/Contex";
	
	private Endpoint e;
	
	public static void main(String[] args) {
		if (args.length > 0)
			TestContexServiceProvider.address = args[0];

		new TestContexServiceProvider().start();
	}
	
	public void run() {
		SkyLogger.getTestLogger().info("Starting Provider at " + TestContexServiceProvider.address);
		Object implementor = new TestProviderContexWsImpl();
		e = Endpoint.publish(address, implementor);
	}
	
	public void shutdown() {
		if (e != null)
			e.stop();
	}
}
