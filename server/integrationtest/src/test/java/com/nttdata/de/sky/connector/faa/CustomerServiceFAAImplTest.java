package com.nttdata.de.sky.connector.faa;

import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.connector.ConnectorFactory;
import com.nttdata.de.sky.connector.data.IndexingCustomer;
import com.nttdata.de.sky.connector.data.RoutingCustomer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CustomerServiceFAAImplTest {

	private static CustomerServiceFAAProvider	p;

	@BeforeClass
	public static void beforeClass() {
		SkyLogger.getTestLogger().info("Starting up CustomerServiceFAAProvider");
		System.setProperty(FAAConnectorImpl.SysProperty_CustomerServiceFAA_Endpoint, "http://localhost:9300/BusinessService/CustomerServiceFAA");

		p = new CustomerServiceFAAProvider();

		p.start();

		try {
			Thread.sleep(2000);
			SkyLogger.getTestLogger().info("Sleeping for 2000ms.");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void afterClass() {
		SkyLogger.getTestLogger().info("Shuting down CustomerServiceFAAProvider");
		p.shutdown();
	}

	@Test
	public void testQueryCustomerAndSMC() {

		IndexingCustomer customer;
		try {
			customer = new IndexingCustomer();
			customer.setNumber("4123456789");
			customer.setSMCNumber("777123456789");

			RoutingCustomer rCustomer = ConnectorFactory.getFAAInstance().queryFAACustomerData(customer, "ITYX123Test");

			SkyLogger.getTestLogger().debug("Found test customer from 4123456789 and 777123456789.");

			assertTrue(rCustomer.getContactInterval7D()==1);

		} catch (Exception e) {
			SkyLogger.getTestLogger().error("Exception occured", e);
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testQueryCustomerOnly() {

		IndexingCustomer customer;
		try {
			customer = new IndexingCustomer();
			customer.setNumber("4123456789");
			RoutingCustomer rCustomer = ConnectorFactory.getFAAInstance().queryFAACustomerData(customer, "ITYX123-TEST");
			assertTrue(rCustomer.getContactInterval7D()==0);
		} catch (Exception e) {
			SkyLogger.getTestLogger().error("Exception occured", e);
			fail(e.getMessage());
		}
	}
}
