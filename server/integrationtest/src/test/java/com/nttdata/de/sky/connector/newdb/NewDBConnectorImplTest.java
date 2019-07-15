package com.nttdata.de.sky.connector.newdb;

import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.connector.ConnectorFactory;
import com.nttdata.de.sky.connector.INewDB;
import com.nttdata.de.sky.connector.data.RoutingCustomer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class NewDBConnectorImplTest {

	private static NewDBServiceProvider p;

	@BeforeClass
	public static void beforeClass() {
		SkyLogger.getTestLogger().info("Starting up CustomerDataServiceProvider");
		System.setProperty(
				NewDBConnectorImpl.SysProperty_CustomerDataService_Endpoint,
				"http://localhost:13100/BusinessService/CustomerDataService");

		p = new NewDBServiceProvider();

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
		SkyLogger.getTestLogger().info("Shuting down CustomerDataServiceProvider");
		p.shutdown();
	}

	@Test
	public void testQueryCustomer() {

		RoutingCustomer customer;
		try {
			customer = ConnectorFactory.getNewDBInstance()
					.queryRoutingCustomer("4123456789");

			SkyLogger.getTestLogger().debug(
					"Found customer (contract change date): "
							+ customer.getSrContractChangeDate());

//			assertTrue(customer.getCategory().equals("EHRENLOGE"));
//			assertTrue(customer.getSrContractChangeDate().equals("19.10.2011 12:34:56"));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testQueryCustomerAndContract() {

		RoutingCustomer customer;
		try {
			customer = ConnectorFactory.getNewDBInstance()
					.queryRoutingCustomer("5159444220", "2299475071");

			SkyLogger.getTestLogger().debug(
					"Found test customer from 5159444220 and 2299475071: "
							+ customer.getCategory());

//			assertTrue(customer.getCategory().equals("STANDARD"));
//			assertTrue(customer.getSubscriptionStartDate().equals("22.10.2011 00:00:00"));
			assertTrue(customer.getSrContractChangeDate().equals(""));

			customer = ConnectorFactory.getNewDBInstance()
					.queryRoutingCustomer("5159444220", "4556837323");

			SkyLogger.getTestLogger().debug(
					"Found test customer from 5159444220 and 4556837323: "
							+ customer.getCategory());

//			assertTrue(customer.getCategory().equals("STANDARD"));
//			assertTrue(customer.getSubscriptionStartDate().equals("19.10.2011 00:00:00"));
			assertTrue(customer.getSrContractChangeDate().equals(""));

		} catch (Exception e) {
			SkyLogger.getTestLogger().error("Exception occured", e);
			fail(e.getMessage());
		}
	}

	@Test
	public void testQueryWashMachineAndCampaign() {
		RoutingCustomer customer;
		try {
			final INewDB newDBInstance = ConnectorFactory.getNewDBInstance();
			checkPrimaryWM(newDBInstance);
//			checkSecondaryWM(newDBInstance);
//			checkMultipleCampaign(newDBInstance);
		} catch (Exception e) {
			SkyLogger.getTestLogger().error("Exception occured", e);
			fail(e.getMessage());
		}
	}

	/**
	 * @param newDBInstance
	 * @throws Exception
	 */
	protected void checkMultipleCampaign(final INewDB newDBInstance)
			throws Exception {
		RoutingCustomer customer;
		customer = newDBInstance.queryRoutingCustomer("3333333333");
		System.err.println(customer.toString());
		assertTrue(customer.getCampaignStamp().equals("Y"));
		assertTrue(customer.getWashMachineFlag().equals("N"));
		assertTrue(customer.getCampaignTypes().equals("TimeMachine,SkyMachine"));
	}

	/**
	 * @param newDBInstance
	 * @return
	 * @throws Exception
	 */
	protected void checkSecondaryWM(final INewDB newDBInstance) throws Exception {
		RoutingCustomer customer;
		customer = newDBInstance.queryRoutingCustomer("2222222222");
		System.err.println(customer.toString());
		assertTrue(customer.getCampaignStamp().equals("Y"));
		assertTrue(customer.getWashMachineFlag().equals("D"));
		assertTrue(customer.getCampaignTypes().equals("WashMachine"));
	}

	/**
	 * @param newDBInstance
	 * @return
	 * @throws Exception
	 */
	protected void checkPrimaryWM(final INewDB newDBInstance) throws Exception {
		RoutingCustomer customer;
		customer = newDBInstance.queryRoutingCustomer("1111111111",
				"1111111111");
		System.err.println(customer.toString());
//		assertTrue(customer.getCampaignStamp().equals("Y"));
		assertTrue(customer.getWashMachineFlag().equals("Y"));
//		assertTrue(customer.getCampaignTypes().equals("WashMachine"));
	}
}
