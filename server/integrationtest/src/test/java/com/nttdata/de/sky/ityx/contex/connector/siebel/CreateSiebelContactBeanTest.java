package com.nttdata.de.sky.ityx.contex.connector.siebel;

import com.nttdata.de.sky.connector.siebel.SiebelConnectorImpl;
import com.nttdata.de.sky.connector.siebel.SiebelServiceProvider;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.impl.document.text.string.StringDocument;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.document.CDocument;
import de.ityx.contexdesigner.exflow.states.FlowObject;
import org.junit.*;

import static org.junit.Assert.fail;

public class CreateSiebelContactBeanTest {

	private static SiebelServiceProvider sp;
	private IFlowObject						flowObject;

	@BeforeClass
	public static void beforeClass() {
		System.setProperty(SiebelConnectorImpl.SysProperty_SiebelService_Endpoint, "http://localhost:13200/BusinessService/SiebelService?WSDL");

		sp = new SiebelServiceProvider();

		sp.start();
	}

	@AfterClass
	public static void afterClass() {
		if (sp != null) {
			sp.shutdown();
			sp = null;
		}
	}

	@Before
	public void setUp() {
		CDocument document = StringDocument.getInstance();
		document.setNote(TagMatchDefinitions.DOCUMENT_ID, "TEST");
		document.setNote(TagMatchDefinitions.EVAL_CUSTOMER_NUMBER, "TEST");
		document.setNote(TagMatchDefinitions.EVAL_CONTRACT_NUMBER, "TEST");
		document.setNote(TagMatchDefinitions.DOCUMENT_TYPE, "TEST");

		flowObject = new FlowObject();
		flowObject.put("doc", new CDocumentContainer<CDocument>(document));
		flowObject.put("Siebel_WSDL", System.getProperty(SiebelConnectorImpl.SysProperty_SiebelService_Endpoint));
		flowObject.put("Siebel_Enabled", new Boolean(true));
	}

	@After
	public void tearDown() {
		flowObject = null;
	}

	@Test
	public void testExecute() {
		CreateSiebelContactBean bean = new CreateSiebelContactBean();
		try {
			bean.execute(0, flowObject, null);
		} catch (Exception e) {
			fail("Exception: " + e.getMessage());
		}

	}
}
