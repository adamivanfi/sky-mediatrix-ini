package com.nttdata.de.sky.connector.contex;

import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.connector.siebel.SiebelConnectorImpl;
import com.nttdata.de.sky.connector.siebel.SiebelServiceProvider;
import org.junit.BeforeClass;
import org.junit.Test;

public class AsyncTest {

	@BeforeClass
	public static void beforeClass() {
		SkyLogger.getTestLogger().info("Starting up SiebelServiceProvider");
		System.setProperty(SiebelConnectorImpl.SysProperty_SiebelService_Endpoint, "http://localhost:13200/BusinessService/SiebelService");
		
		SiebelServiceProvider siebel = new SiebelServiceProvider();
		siebel.start();
				
		SkyLogger.getTestLogger().info("Starting up ContexServiceProvider");
		
		ContexServiceProvider contex = new ContexServiceProvider();
		contex.start();
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}	
	
	@Test
	public void test() {
		
	}

}
