package com.nttdata.de.sky.ityx.contex.enrichment;

import com.nttdata.de.lib.logging.SkyLogger;
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

public class EnrichSEPAMandateBeanTest {

	private static final String	SERVICE_OFF	= "SERVICE_OFF";

	@BeforeClass
	public static void beforeClass() {
		System.setProperty(NewDBConnectorImpl.SysProperty_CustomerDataService_Endpoint, "http://127.0.0.1:13100/BusinessService/CustomerDataService");

		NewDBServiceProvider.address = System.getProperty(NewDBConnectorImpl.SysProperty_CustomerDataService_Endpoint);
		NewDBServiceProvider sp = new NewDBServiceProvider();
		sp.start();

		try {
			SkyLogger.getTestLogger().info("Sleeping for 2000ms");
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testExecuteWithCustomerOnly() {

		EnrichSEPAMandateBean bean = new EnrichSEPAMandateBean();
		bean.simDB = true;

		IFlowObject flowObject = new FlowObject();

		flowObject.put("DynamicCustomerData_Enabled", Boolean.TRUE);
		flowObject.put("DynamicCustomerData_WSDL", System.getProperty(NewDBConnectorImpl.SysProperty_CustomerDataService_Endpoint));

		flowObject.put("VerifiedCustomerNumber", "3333333333");

		CDocumentContainer<CDocument> docContainer = new CDocumentContainer<CDocument>( StringDocument.getInstance(""));
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
			if (identifier.equals(TagMatchDefinitions.CUSTOMER_ID)) {
				assertTrue(value.equals(""));
			}
			if (identifier.equals(TagMatchDefinitions.CUSTOMER_FIRST_NAME)) {
				assertTrue(value.equals(""));
			}

		}
	}

	@Test
	public void testExecuteWithCompleteData() {

		EnrichSEPAMandateBean bean = new EnrichSEPAMandateBean();
		bean.simDB = true;
		IFlowObject flowObject = new FlowObject();

		flowObject.put("DynamicCustomerData_Enabled", Boolean.TRUE);
		flowObject.put("DynamicCustomerData_WSDL", System.getProperty(NewDBConnectorImpl.SysProperty_CustomerDataService_Endpoint));

		flowObject.put("VerifiedContractNumber", "1111111111");
		flowObject.put("VerifiedCustomerNumber", "1111111111");
		flowObject.put("VerifiedMandateNumber", "1111111111");

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
			if (identifier.equals(TagMatchDefinitions.CUSTOMER_LAST_NAME)) {
				assertTrue(value.equals("Mustermann"));
			}
			if (identifier.equals(TagMatchDefinitions.CUSTOMER_FIRST_NAME)) {
				assertTrue(value.equals("Manfred"));
			}
			if (identifier.equals(TagMatchDefinitions.CUSTOMER_STREET)) {
				assertTrue(value.equals("Musterstrasse 123"));
			}
			if (identifier.equals(TagMatchDefinitions.SEPA_MANDATE_NUMBER)) {
				assertTrue(value.equals("1111111111"));
			}
			if (identifier.equals(TagMatchDefinitions.SEPA_STATUS)) {
				assertTrue(value.equals("PENDING"));
			}
			// }
			if (identifier.equals(TagMatchDefinitions.SEPA_SIGNATURE_FLAG)) {
				assertTrue(value.equals("Y"));
			}
			if (identifier.equals(TagMatchDefinitions.SEPA_SIGNATURE_DATE)) {
				assertTrue(value.equals("20130901000000"));
			}
		}
	}

	@Test
	public void testExecuteWithCustomerContractMandate() {

		EnrichSEPAMandateBean bean = new EnrichSEPAMandateBean();
		bean.simDB = true;
		IFlowObject flowObject = new FlowObject();

		flowObject.put("DynamicCustomerData_Enabled", Boolean.TRUE);
		flowObject.put("DynamicCustomerData_WSDL", System.getProperty(NewDBConnectorImpl.SysProperty_CustomerDataService_Endpoint));

		flowObject.put("VerifiedContractNumber", "4444444444");
		flowObject.put("VerifiedMandateNumber", "1111111111");
		flowObject.put("VerifiedCustomerNumber", "2222222222");

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
			if (identifier.equals(TagMatchDefinitions.CUSTOMER_LAST_NAME)) {
				assertTrue(value.equals("Mustermann"));
			}
			if (identifier.equals(TagMatchDefinitions.CUSTOMER_FIRST_NAME)) {
				assertTrue(value.equals("Manfred"));
			}
			if (identifier.equals(TagMatchDefinitions.CUSTOMER_STREET)) {
				assertTrue(value.equals("Musterstrasse 123"));
			}
			if (identifier.equals(TagMatchDefinitions.SEPA_MANDATE_NUMBER)) {
				assertTrue(value.equals("1111111111"));
			}
			if (identifier.equals(TagMatchDefinitions.SEPA_STATUS)) {
				assertTrue(value.equals("PENDING"));
			}
			if (identifier.equals(TagMatchDefinitions.SEPA_SIGNATURE_FLAG)) {
				assertTrue(value.equals("N"));
			}
		}
	}

	@Test
	public void testExecuteWithContractMandate() {

		EnrichSEPAMandateBean bean = new EnrichSEPAMandateBean();
		bean.simDB = true;
		IFlowObject flowObject = new FlowObject();

		flowObject.put("DynamicCustomerData_Enabled", Boolean.TRUE);
		flowObject.put("DynamicCustomerData_WSDL", System.getProperty(NewDBConnectorImpl.SysProperty_CustomerDataService_Endpoint));

		flowObject.put("VerifiedContractNumber", "5555555555");
		flowObject.put("VerifiedMandateNumber", "1111111111");

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
			if (identifier.equals(TagMatchDefinitions.CUSTOMER_LAST_NAME)) {
				assertTrue(value.equals("Mustermann"));
			}
			if (identifier.equals(TagMatchDefinitions.CUSTOMER_FIRST_NAME)) {
				assertTrue(value.equals("Manfred"));
			}
			if (identifier.equals(TagMatchDefinitions.CUSTOMER_STREET)) {
				assertTrue(value.equals("Musterstrasse 123"));
			}
			if (identifier.equals(TagMatchDefinitions.SEPA_MANDATE_NUMBER)) {
				assertTrue(value.equals("1111111111"));
			}
			if (identifier.equals(TagMatchDefinitions.SEPA_STATUS)) {
				assertTrue(value.equals("PENDING"));
			}
			// }
			if (identifier.equals(TagMatchDefinitions.SEPA_SIGNATURE_FLAG)) {
				assertTrue(value.equals("N"));
			}
		}
	}

	@Test
	public void testExecuteServiceOff() {

		EnrichSEPAMandateBean bean = new EnrichSEPAMandateBean();
		bean.simDB = true;
		IFlowObject flowObject = new FlowObject();

		flowObject.put("DynamicCustomerData_Enabled", Boolean.FALSE);
		flowObject.put("DynamicCustomerData_WSDL", System.getProperty(NewDBConnectorImpl.SysProperty_CustomerDataService_Endpoint));

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
			SkyLogger.getTestLogger().debug("TagMatch: " + tm.getCaption() + "=" + value);
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
			if (identifier.equals(TagMatchDefinitions.META_SR_CONTRACT_CHANGE_DATE)) {
				assertTrue(value.equals(SERVICE_OFF));
			}
		}
	}

}
