package com.nttdata.de.sky.connector.siebel;

import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.connector.ConnectorFactory;
import com.nttdata.de.sky.connector.CreateContactParameter;
import com.nttdata.de.sky.connector.ISiebel.Channel;
import com.nttdata.de.sky.connector.ISiebel.Direction;
import com.nttdata.de.sky.connector.UpdateContactParameter;
import com.nttdata.de.sky.connector.contex.ContexWsImpl;
import com.nttdata.de.sky.connector.contex.ContexWsListener;
import org.junit.*;

import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SiebelConnectorImplTest {

	public static class TestListener implements ContexWsListener {

		public boolean				allGood	= false;

		private String				retMaster;
		private String				retProcessName;
		private Map<String, String>	retOutput;

		@Override
		public void methodInvoked(String master, String processName, Map<String, String> input, Map<String, String> output) {
			SkyLogger.getTestLogger().debug("Received values from ContexWsListener: " + output.toString());
			this.retMaster = master;
			this.retProcessName = processName;
			this.retOutput = output;

			if (output.get("documentid").equals("DOCID_testCreateContactWithFaultResponse") || output.get("documentid").equals("DOCID_testCreateTrackingSRWithFaultResponse")) {
				SkyLogger.getTestLogger().warn("Expecting error for this callback--- " + output.get("errormessage"));
				allGood = allGood || PortTypeImpl.FaultLevelAsynchronousError.equals(output.get("errormessage"));
			}
		}

		public String getMaster() {
			return retMaster;
		}

		public String getProcessName() {
			return retProcessName;
		}

		public Map<String, String> getOutput() {
			return retOutput;
		}

	}

	private static SiebelServiceProvider	p;

	@BeforeClass
	public static void beforeClass() {
		SkyLogger.getTestLogger().info("Starting up SiebelServiceProvider");
		System.setProperty(SiebelConnectorImpl.SysProperty_SiebelService_Endpoint, "http://localhost:13200/BusinessService/SiebelService");

		p = new SiebelServiceProvider();
		p.start();

		TestListener listener = new TestListener();
		ContexWsImpl.setListener(listener);

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void afterClass() {

		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(((TestListener) ContexWsImpl.getListener()).allGood);

		SkyLogger.getTestLogger().info("Shuting down SiebelServiceProvider");
		p.shutdown();
	}

	@Before
	public void before() {
		SkyLogger.getTestLogger().info("Starting test");
	}

	@After
	public void after() {
		SkyLogger.getTestLogger().info("Finished test");
	}

	@Test
	public void testCreateContact() {
		try {
			ConnectorFactory.getSiebelInstance().createContact(new CreateContactParameter("DOCID_testCreateContact", Channel.EMAIL, Direction.INBOUND, "unclassified", "4123456789", "99123456789", null, null, null));
		} catch (Exception e) {
			fail("Exception: " + e.getMessage());
		}

		/*
		 * Nothing to test because the interface will always return
		 */
		assertTrue(true);
	}

	@Test
	public void testCreateContactWithException() {
		try {
			ConnectorFactory.getSiebelInstance().createContact(
					new CreateContactParameter("DOCID_testCreateContactWithException", Channel.EMAIL, Direction.INBOUND, PortTypeImpl.FaultLevelException, "4123456789", "99123456789", null, null, null));
			assertTrue(false);
		} catch (Exception e) {
			SkyLogger.getTestLogger().debug("Expected exception caught.");
			assertTrue(true);
		}

	}

	@Test
	public void testCreateContactWithFaultResponse() {
		try {
			TestListener listener = (TestListener) ContexWsImpl.getListener();
			ConnectorFactory.getSiebelInstance().createContact(
					new CreateContactParameter("DOCID_testCreateContactWithFaultResponse", Channel.EMAIL, Direction.INBOUND, PortTypeImpl.FaultLevelAsynchronousError, "4123456789", "99123456789", null, null, null));

			try {
				synchronized (listener) {
					SkyLogger.getTestLogger().debug("Waiting... ");
					listener.wait();
					SkyLogger.getTestLogger().debug("Listener returned!");
				}
			} catch (Exception ex) {
				SkyLogger.getTestLogger().error("Unable to wait on listener", ex);
			}

		} catch (Exception e) {
			fail("Exception: " + e.getMessage());
		}

		/*
		 * Nothing to test because the interface will always return
		 */
		assertTrue(true);
	}

	@Test
	public void testCreateTrackingSR() {
		try {
			ConnectorFactory.getSiebelInstance().associateDocumentIdWithoutActivityId("DOCID_testCreateTrackingSR", "123456789", null, "99123456789");
		} catch (Exception e) {
			fail("Exception: " + e.getMessage());
		}
	}

	@Test
	public void testCreateTrackingSRWithFaultResponse() {
		try {
			TestListener listener = (TestListener) ContexWsImpl.getListener();

			ConnectorFactory.getSiebelInstance().associateDocumentIdWithoutActivityId("DOCID_testCreateTrackingSRWithFaultResponse", "123456789", PortTypeImpl.FaultLevelAsynchronousError, "99123456789");

			try {
				synchronized (listener) {
					SkyLogger.getTestLogger().debug("Waiting... ");
					listener.wait();
					SkyLogger.getTestLogger().debug("Listener returned!");
				}
			} catch (Exception ex) {
				SkyLogger.getTestLogger().error("Unable to wait on listener", ex);
			}

		} catch (Exception e) {
			fail("Exception: " + e.getMessage());
		}
	}

	@Test
	public void testAssociateDocumentIdToActivity() {
		try {
			ConnectorFactory.getSiebelInstance().associateDocumentIdToActivity("DOCID_testAssociateDocumentIdToActivity", "1-4444", Channel.EMAIL, Direction.OUTBOUND);
		} catch (Exception e) {
			fail("Exception: " + e.getMessage());
		}

		/*
		 * Nothing to test because the interface will always return
		 */
		assertTrue(true);
	}

	@Test
	public void testAdjustSR() {
		try {
			ConnectorFactory.getSiebelInstance().updateContactOfDocument(new UpdateContactParameter("DOCID_testAdjustSR", "6123456789", "4123456788", "5123456789", "EMAIL", "systemdefault", null, null, null));
		} catch (Exception e) {
			fail("Exception: " + e.getMessage());
		}
	}

}
