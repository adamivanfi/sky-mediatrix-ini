package com.nttdata.de.sky.ityx.contex.enrichment;

import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.connector.faa.CustomerServiceFAAProvider;
import com.nttdata.de.sky.connector.faa.FAAConnectorImpl;
import com.nttdata.de.sky.connector.newdb.NewDBConnectorImpl;
import com.nttdata.de.sky.connector.newdb.NewDBServiceProvider;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import com.nttdata.ityx.contex.workflow.base.AbstractWflBean;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.impl.document.text.string.StringDocument;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.document.CDocument;
import de.ityx.contex.interfaces.extag.TagMatch;
import de.ityx.contexdesigner.exflow.states.FlowObject;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class EnrichDocumentBeanTest {

	private static final String SERVICE_OFF = "SERVICE_OFF";

	@BeforeClass
	public static void beforeClass() {
		startCustomerDataService();
		startCustomerFAAService();
	}

	/**
	 * NewDB
	 */
	public static void startCustomerDataService() {
		System.setProperty(NewDBConnectorImpl.SysProperty_CustomerDataService_Endpoint, "http://127.0.0.1:13100/BusinessService/CustomerDataService");

		NewDBServiceProvider.address = System
				.getProperty(NewDBConnectorImpl.SysProperty_CustomerDataService_Endpoint);
		NewDBServiceProvider sp = new NewDBServiceProvider();
		sp.start();

		try {
			SkyLogger.getTestLogger().info("Sleeping for 1000ms");
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * FAA 
	 */
	public static void startCustomerFAAService() {
		System.setProperty(FAAConnectorImpl.SysProperty_CustomerServiceFAA_Endpoint, "http://localhost:9300/BusinessService/CustomerServiceFAA");

		CustomerServiceFAAProvider.address = System.getProperty(FAAConnectorImpl.SysProperty_CustomerServiceFAA_Endpoint);
		CustomerServiceFAAProvider sp = new CustomerServiceFAAProvider(false);
		sp.start();

		try {
			SkyLogger.getTestLogger().info("Sleeping for 1000ms");
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// @Test
	public void testExecuteWithCustomerWithTwoContracts() {

		EnrichDocumentBean bean = new EnrichDocumentBean();
		bean.simDB = true;

		IFlowObject flowObject = new FlowObject();

		flowObject.put("DynamicCustomerData_Enabled", Boolean.TRUE);
		flowObject
				.put("DynamicCustomerData_WSDL",
						System.getProperty(NewDBConnectorImpl.SysProperty_CustomerDataService_Endpoint));

		// flowObject.put("VerifiedSmartcardNumber", "777123456789");
		// flowObject.put("VerifiedContractNumber", "99123456789");
		flowObject.put("VerifiedCustomerNumber", "5159444220");
		// expected contract number is 99123456789

		CDocumentContainer<CDocument> docContainer = new CDocumentContainer<CDocument>(StringDocument.getInstance());
		flowObject.put(AbstractWflBean.DOC, docContainer);

		try {
			bean.execute(0, flowObject, null);
		} catch (Exception e) {
			fail("Exception: " + e.getMessage());
		}

		List<TagMatch> tags = docContainer.getDocument(0).getTags();
		Iterator<TagMatch> iter = tags.iterator();
		while (iter.hasNext()) {
			TagMatch tm = iter.next();
			String identifier = tm.getIdentifier();
			String value = tm.getTagValue();
			SkyLogger.getTestLogger().debug(
					"TagMatch: " + tm.getCaption() + "=" + value);
			if (identifier.equals(TagMatchDefinitions.CUSTOMER_LAST_NAME)) {
				assertTrue(value.equals("Daecher"));
			}
			if (identifier.equals(TagMatchDefinitions.CUSTOMER_STREET)) {
				assertTrue(value.equals("Friedrichstrasse 40"));
			}
			if (identifier.equals(TagMatchDefinitions.CUSTOMER_FIRST_NAME)) {
				assertTrue(value.equals("Patrick"));
			}
			if (identifier
					.equals(TagMatchDefinitions.META_SUBSCRIPTION_START_DATE)) {
				assertTrue(value.equals("22.10.2011 00:00:00"));
			}
		}
	}

	@Test
	public void testExecuteWithCustomer() {

		EnrichDocumentBean bean = new EnrichDocumentBean();
		bean.simDB = true;

		IFlowObject flowObject = new FlowObject();

		flowObject.put("DynamicCustomerData_Enabled", Boolean.TRUE);
		flowObject
				.put("DynamicCustomerData_WSDL",
						System.getProperty(NewDBConnectorImpl.SysProperty_CustomerDataService_Endpoint));

		// flowObject.put("VerifiedSmartcardNumber", "777123456789");
		// flowObject.put("VerifiedContractNumber", "99123456789");
		flowObject.put("VerifiedCustomerNumber", "6100424268");
		// expected contract number is 99123456789

		CDocumentContainer<CDocument> docContainer = new CDocumentContainer<CDocument>(StringDocument.getInstance());
		flowObject.put(AbstractWflBean.DOC, docContainer);

		try {
			bean.execute(0, flowObject, null);
		} catch (Exception e) {
			fail("Exception: " + e.getMessage());
		}

		List<TagMatch> tags = docContainer.getDocument(0).getTags();
		Iterator<TagMatch> iter = tags.iterator();
		while (iter.hasNext()) {
			TagMatch tm = iter.next();
			String identifier = tm.getIdentifier();
			String value = tm.getTagValue();
			SkyLogger.getTestLogger().debug(
					"TagMatch: " + tm.getCaption() + "=" + value);
			if (identifier.equals(TagMatchDefinitions.CUSTOMER_LAST_NAME)) {
				assertTrue(value.equals("Mustermann"));
			}
			if (identifier.equals(TagMatchDefinitions.CUSTOMER_STREET)) {
				assertTrue(value.equals("Musterstrasse 123"));
			}
			if (identifier.equals(TagMatchDefinitions.CUSTOMER_FIRST_NAME)) {
				assertTrue(value.equals("Manfred"));
			}
			if (identifier.equals(TagMatchDefinitions.CUSTOMER_STREET)) {
				assertTrue(value.equals("Musterstrasse 123"));
			}
			if (identifier.equals(TagMatchDefinitions.META_SUBSCRIPTON_DAYS)) {
				assertTrue(value.equals("BK"));
			}
//			if (identifier.equals(TagMatchDefinitions.META_OPERATIONAL_DATE)) {
//				assertTrue(value.equals("2011-01-01"));
//			}
			// if
			// (identifier.equals(TagMatchDefinitions.META_OPERATIONAL_CUSTOMER))
			// {
			// assertTrue(value.equals("BK"));
			// }
			// if (identifier.equals(TagMatchDefinitions.META_OPERATIONAL_DATE))
			// {
			// assertTrue(value.equals("2011-01-01"));
			// }
			// if
			// (identifier.equals(TagMatchDefinitions.META_SUBSCRIPTION_START_DATE))
			// {
			// assertTrue(value.equals("19.10.2011 00:00:00"));
			// }
			// if
			// (identifier.equals(TagMatchDefinitions.META_SUBSCRIPTION_START_DATE_BEFORE_LIMIT))
			// {
			// assertTrue(value.equals("Y"));
			// }
			// if (identifier.equals(TagMatchDefinitions.META_CAMPAIGN_STAMP)) {
			// assertTrue(value.equals("Y"));
			// }
			if (identifier.equals(TagMatchDefinitions.META_WASHING_MACHINE)) {
				assertTrue(value.equals("Y"));
			}
			// if (identifier.equals(TagMatchDefinitions.META_CAMPAIGN_TYPE)) {
			// assertTrue(value.contains("WashMachine"));
			// }
			if (identifier.equals(TagMatchDefinitions.META_RECEPTION)) {
				assertTrue(value.equals("DVB_C"));
			}
			if (identifier.equals(TagMatchDefinitions.META_OPERATOR)) {
				assertTrue(value.equals("SKY"));
			}
			if (identifier.equals(TagMatchDefinitions.META_PLATFORM)) {
				assertTrue(value.equals("SKY_Go"));
			}
		}
	}

	@Test
	public void testExecuteWithContract() {

		EnrichDocumentBean bean = new EnrichDocumentBean();
		bean.simDB = true;
		IFlowObject flowObject = new FlowObject();

		flowObject.put("DynamicCustomerData_Enabled", Boolean.TRUE);
		flowObject
				.put("DynamicCustomerData_WSDL",
						System.getProperty(NewDBConnectorImpl.SysProperty_CustomerDataService_Endpoint));

		// flowObject.put("VerifiedSmartcardNumber", "");
		flowObject.put("VerifiedContractNumber", "88123456789");
		flowObject.put("VerifiedCustomerNumber", "6100424268");

		CDocumentContainer<CDocument> docContainer = new CDocumentContainer<CDocument>(StringDocument.getInstance());
		flowObject.put(AbstractWflBean.DOC, docContainer);

		try {
			bean.execute(0, flowObject, null);
		} catch (Exception e) {
			fail("Exception: " + e.getMessage());
		}

		List<TagMatch> tags = docContainer.getDocument(0).getTags();
		Iterator<TagMatch> iter = tags.iterator();
		while (iter.hasNext()) {
			TagMatch tm = iter.next();
			String identifier = tm.getIdentifier();
			String value = tm.getTagValue();
			SkyLogger.getTestLogger().debug(
					"TagMatch: " + tm.getCaption() + "=" + value);
			if (identifier.equals(TagMatchDefinitions.CUSTOMER_LAST_NAME)) {
				assertTrue(value.equals("Mustermann"));
			}
			if (identifier.equals(TagMatchDefinitions.CUSTOMER_FIRST_NAME)) {
				assertTrue(value.equals("Manfred"));
			}
			if (identifier.equals(TagMatchDefinitions.CUSTOMER_STREET)) {
				assertTrue(value.equals("Musterstrasse 123"));
			}
			if (identifier.equals(TagMatchDefinitions.META_SUBSCRIPTON_DAYS)) {
				assertTrue(value.equals("BK"));
			}
//			if (identifier.equals(TagMatchDefinitions.META_OPERATIONAL_DATE)) {
//				assertTrue(value.equals("2011-01-01"));
//			}
			// if (identifier.equals(TagMatchDefinitions.META_CAMPAIGN_STAMP)) {
			// assertTrue(value.equals("Y"));
			// }
			if (identifier.equals(TagMatchDefinitions.META_WASHING_MACHINE)) {
				assertTrue(value.equals("Y"));
			}
			// if (identifier.equals(TagMatchDefinitions.META_CAMPAIGN_TYPE)) {
			// assertTrue(value.contains("WashMachine"));
			// }

			if (identifier.equals(TagMatchDefinitions.META_RECEPTION)) {
				assertTrue(value.equals("DVB_C"));
			}
			if (identifier.equals(TagMatchDefinitions.META_OPERATOR)) {
				assertTrue(value.equals("SKY"));
			}
			if (identifier.equals(TagMatchDefinitions.META_PLATFORM)) {
				assertTrue(value.equals("SKY_Go"));
			}
		}
	}

	@Test
	public void testExecuteWithContract2() {

		EnrichDocumentBean bean = new EnrichDocumentBean();
		bean.simDB = true;
		IFlowObject flowObject = new FlowObject();

		flowObject.put("DynamicCustomerData_Enabled", Boolean.TRUE);
		flowObject
				.put("DynamicCustomerData_WSDL",
						System.getProperty(NewDBConnectorImpl.SysProperty_CustomerDataService_Endpoint));

		// flowObject.put("VerifiedSmartcardNumber", "");
		flowObject.put("VerifiedContractNumber", "4556837323");
		flowObject.put("VerifiedCustomerNumber", "5159444220");

		CDocumentContainer<CDocument> docContainer = new CDocumentContainer<CDocument>(StringDocument.getInstance());
		flowObject.put(AbstractWflBean.DOC, docContainer);

		try {
			bean.execute(0, flowObject, null);
		} catch (Exception e) {
			fail("Exception: " + e.getMessage());
		}

		List<TagMatch> tags = docContainer.getDocument(0).getTags();
		Iterator<TagMatch> iter = tags.iterator();
		while (iter.hasNext()) {
			TagMatch tm = iter.next();
			String identifier = tm.getIdentifier();
			String value = tm.getTagValue();
			SkyLogger.getTestLogger().debug(
					"TagMatch: " + tm.getCaption() + "=" + value);
			if (identifier.equals(TagMatchDefinitions.CUSTOMER_LAST_NAME)) {
				assertTrue(value.equals("Daecher"));
			}
			if (identifier.equals(TagMatchDefinitions.CUSTOMER_FIRST_NAME)) {
				assertTrue(value.equals("Patrick"));
			}
			if (identifier.equals(TagMatchDefinitions.CUSTOMER_STREET)) {
				assertTrue(value.equals("Fasanenstrasse 95"));
			}
		}
	}

	@Test
	public void testExecuteWithContractOnly() {

		EnrichDocumentBean bean = new EnrichDocumentBean();
		bean.simDB = true;
		IFlowObject flowObject = new FlowObject();

		flowObject.put("DynamicCustomerData_Enabled", Boolean.TRUE);
		flowObject
				.put("DynamicCustomerData_WSDL",
						System.getProperty(NewDBConnectorImpl.SysProperty_CustomerDataService_Endpoint));

		// flowObject.put("VerifiedSmartcardNumber", "");
		flowObject.put("VerifiedContractNumber", "2299475071");
		// flowObject.put("VerifiedCustomerNumber", "5159444220");

		CDocumentContainer<CDocument> docContainer = new CDocumentContainer<CDocument>(StringDocument.getInstance());
		flowObject.put(AbstractWflBean.DOC, docContainer);

		try {
			bean.execute(0, flowObject, null);
		} catch (Exception e) {
			fail("Exception: " + e.getMessage());
		}

		List<TagMatch> tags = docContainer.getDocument(0).getTags();
		Iterator<TagMatch> iter = tags.iterator();
		while (iter.hasNext()) {
			TagMatch tm = iter.next();
			String identifier = tm.getIdentifier();
			String value = tm.getTagValue();
			SkyLogger.getTestLogger().debug(
					"TagMatch: " + tm.getCaption() + "=" + value);
			if (identifier.equals(TagMatchDefinitions.CUSTOMER_LAST_NAME)) {
				assertTrue(value.equals("Daecher"));
			}
			if (identifier.equals(TagMatchDefinitions.CUSTOMER_FIRST_NAME)) {
				assertTrue(value.equals("Patrick"));
			}
			if (identifier.equals(TagMatchDefinitions.CUSTOMER_STREET)) {
				assertTrue(value.equals("Friedrichstrasse 40"));
			}
		}
	}

	public void testExecuteSIT13_06_015() {

		EnrichDocumentBean bean = new EnrichDocumentBean();
		bean.simDB = true;
		IFlowObject flowObject = new FlowObject();

		flowObject.put("DynamicCustomerData_Enabled", Boolean.TRUE);
		flowObject
				.put("DynamicCustomerData_WSDL",
						System.getProperty(NewDBConnectorImpl.SysProperty_CustomerDataService_Endpoint));

		// flowObject.put("VerifiedSmartcardNumber", "");
		flowObject.put("VerifiedContractNumber", "1111111111");
		// flowObject.put("VerifiedCustomerNumber", "5159444220");

		CDocumentContainer<CDocument> docContainer = new CDocumentContainer<CDocument>(StringDocument.getInstance());
		flowObject.put(AbstractWflBean.DOC, docContainer);

		try {
			bean.execute(0, flowObject, null);
		} catch (Exception e) {
			fail("Exception: " + e.getMessage());
		}

		List<TagMatch> tags = docContainer.getDocument(0).getTags();
		Iterator<TagMatch> iter = tags.iterator();
		while (iter.hasNext()) {
			TagMatch tm = iter.next();
			String identifier = tm.getIdentifier();
			String value = tm.getTagValue();
			SkyLogger.getTestLogger().debug(
					"TagMatch: " + tm.getCaption() + "=" + value);
			if (identifier.equals(TagMatchDefinitions.META_CONTRACT_TYPE)) {
				assertTrue(value.equals("test"));
			}
			if (identifier.equals(TagMatchDefinitions.CONTACT_INTERVAL_7D)) {
				assertTrue(value.equals("1"));
			}
			if (identifier.equals(TagMatchDefinitions.CONTACT_INTERVAL_14D)) {
				assertTrue(value.equals("2"));
			}
			if (identifier.equals(TagMatchDefinitions.CONTACT_INTERVAL_21D)) {
				assertTrue(value.equals("3"));
			}
			if (identifier.equals(TagMatchDefinitions.CONTACT_INTERVAL_28D)) {
				assertTrue(value.equals("4"));
			}
		}
	}

	@Test
	public void testExecuteWithCustomerSMCFAA() {

		EnrichDocumentBean bean = new EnrichDocumentBean();
		bean.simDB = true;
		IFlowObject flowObject = new FlowObject();

		flowObject.put("DynamicCustomerData_Enabled", Boolean.TRUE);
		flowObject.put("CustomerServiceFAA_Enabled", Boolean.TRUE);
		flowObject.put("DynamicCustomerData_WSDL", System.getProperty(NewDBConnectorImpl.SysProperty_CustomerDataService_Endpoint));
		flowObject.put("CustomerServiceFAA_WSDL", System.getProperty(FAAConnectorImpl.SysProperty_CustomerServiceFAA_Endpoint));

		flowObject.put("VerifiedSmartcardNumber", "777123456789");
		flowObject.put("VerifiedCustomerNumber", "4123456789");

		CDocumentContainer<CDocument> docContainer = new CDocumentContainer<CDocument>(StringDocument.getInstance());
		flowObject.put(AbstractWflBean.DOC, docContainer);

		try {
			bean.execute(0, flowObject, null);
		} catch (Exception e) {
			fail("Exception: " + e.getMessage());
		}

		List<TagMatch> tags = docContainer.getDocument(0).getTags();
		Iterator<TagMatch> iter = tags.iterator();
		while (iter.hasNext()) {
			TagMatch tm = iter.next();
			String identifier = tm.getIdentifier();
			String value = tm.getTagValue();
			SkyLogger.getTestLogger().debug("TagMatch: " + tm.getCaption() + "=" + value);
			if (identifier.equals(TagMatchDefinitions.CONTACT_INTERVAL_7D)) {
				assertTrue(value.equals("1"));
			}
		}
	}

	@Test
	public void testExecuteServiceOff() {

		EnrichDocumentBean bean = new EnrichDocumentBean();
		bean.simDB = true;
		IFlowObject flowObject = new FlowObject();

		flowObject.put("DynamicCustomerData_Enabled", Boolean.FALSE);
		flowObject
				.put("DynamicCustomerData_WSDL",
						System.getProperty(NewDBConnectorImpl.SysProperty_CustomerDataService_Endpoint));
		flowObject.put("CustomerServiceFAA_WSDL", System.getProperty(FAAConnectorImpl.SysProperty_CustomerServiceFAA_Endpoint));

		flowObject.put("VerifiedSmartcardNumber", "4123456789");
		flowObject.put("VerifiedContractNumber", "4123456789");
		flowObject.put("VerifiedCustomerNumber", "4123456789");

		CDocumentContainer<CDocument> docContainer = new CDocumentContainer<CDocument>(StringDocument.getInstance());
		flowObject.put(AbstractWflBean.DOC, docContainer);

		try {
			bean.execute(0, flowObject, null);
		} catch (Exception e) {
			fail("Exception: " + e.getMessage());
		}

		List<TagMatch> tags = docContainer.getDocument(0).getTags();
		Iterator<TagMatch> iter = tags.iterator();
		while (iter.hasNext()) {
			TagMatch tm = iter.next();
			String identifier = tm.getIdentifier();
			String value = tm.getTagValue();
			SkyLogger.getTestLogger().debug(
					"TagMatch: " + tm.getCaption() + "=" + value);
			if (identifier.equals(TagMatchDefinitions.CUSTOMER_LAST_NAME)) {
				assertTrue(value.equals(SERVICE_OFF));
			}
			if (identifier.equals(TagMatchDefinitions.CUSTOMER_STREET)) {
				assertTrue(value.equals(SERVICE_OFF));
			}
			if (identifier.equals(TagMatchDefinitions.CUSTOMER_FIRST_NAME)) {
				assertTrue(value.equals(SERVICE_OFF));
			}
			if (identifier.equals(TagMatchDefinitions.CUSTOMER_STREET)) {
				assertTrue(value.equals(SERVICE_OFF));
			}
			if (identifier
					.equals(TagMatchDefinitions.META_SR_CONTRACT_CHANGE_DATE)) {
				assertTrue(value.equals(SERVICE_OFF));
			}
		}
	}

}
