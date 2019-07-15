package com.nttdata.de.sky.connector.contex;

import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.webservice.schema.Entry;
import com.nttdata.de.sky.webservice.service.ContexErrorMessage;
import com.nttdata.de.sky.webservice.service.ContexWs;
import com.nttdata.de.sky.webservice.service.ContexWsService;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import javax.xml.namespace.QName;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ContexServiceProviderTest {

	private static final QName SERVICE_NAME = new QName("http://webservice.contex.ityx.de/service", "ContexWsService");
	
	public static ContexServiceProvider p;
	
	@BeforeClass
	public static void beforeClass() {
		SkyLogger.getTestLogger().info("Starting up ContexServiceProvider");
		
		p = new ContexServiceProvider();
		p.start();
	}
	
	@AfterClass
	public static void afterClass() {
		SkyLogger.getTestLogger().info("Shutting down ContexServiceProvider");
		p.shutdown();
	}
	
	//@Test
	public void test() {

		ContexWsService ss;
		try {
			ss = new ContexWsService(new URL(ContexServiceProvider.address+"?WSDL"), SERVICE_NAME);

			ContexWs port = ss.getContexWsPort();  

			{
				SkyLogger.getTestLogger().debug("Invoking runProcess...");
				com.nttdata.de.sky.webservice.schema.ContexRequest _runProcess_request = new com.nttdata.de.sky.webservice.schema.ContexRequest();
				try {

					_runProcess_request.setMaster("sky");
					_runProcess_request.setProcessName("03_inbound_callback");

					Entry e = new Entry();
					e.setKey("documentid");
					e.setValue("44444444");
					_runProcess_request.getEntries().add(e);

					e = new Entry();
					e.setKey("contactid");
					e.setValue("1-SBL4444444444");
					_runProcess_request.getEntries().add(e);

					com.nttdata.de.sky.webservice.schema.ContexResponse _runProcess__return = port.runProcess(_runProcess_request);
					SkyLogger.getTestLogger().debug("runProcess.result=" + _runProcess__return);

					assertEquals(_runProcess__return.getReturn().get(0).getKey(), "resultFromDummy");
					assertEquals(_runProcess__return.getReturn().get(0).getValue(), "ok-2");
				} catch (ContexErrorMessage e) { 
					fail("Exception: "+e.getMessage());
				}
			}
		} catch (MalformedURLException e1) {
			fail("Exception: "+e1.getMessage());
		}
	}

}
