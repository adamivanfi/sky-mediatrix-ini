package com.nttdata.de.sky.ityx.contex.connector.siebel;

import com.nttdata.de.sky.connector.siebel.SiebelConnectorImpl;
import com.nttdata.de.sky.connector.siebel.SiebelServiceProvider;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contexdesigner.exflow.states.FlowObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class CreateSiebelAssociationBeanTest {

	private static SiebelServiceProvider sp;
	
	@BeforeClass
	public static void beforeClass() {
		System.setProperty(SiebelConnectorImpl.SysProperty_SiebelService_Endpoint, "http://localhost:13200/BusinessService/SiebelService?WSDL");
		
		sp = new SiebelServiceProvider();
		
		sp.start();
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			fail(e.getMessage());
		}
	}
	
	@AfterClass
	public static void afterClass() {
		if (sp != null) {
			sp.shutdown();
			sp = null;
		}	
	}
	
	@Test
	public void testExecute() {
		CreateSiebelAssociationBean bean = new CreateSiebelAssociationBean();
		
		IFlowObject flowObject = new FlowObject();
		flowObject.put(TagMatchDefinitions.DOCUMENT_ID, ""+(int)Math.floor(Math.random()*99999999)+1);
		flowObject.put(TagMatchDefinitions.CHANNEL, "EMAIL");
		flowObject.put(TagMatchDefinitions.CUSTOMER_ID, "4123456789");
		flowObject.put(TagMatchDefinitions.CONTACT_ID, "10123456789");
		
		try {
			bean.execute(0, flowObject, null);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
		assertEquals("OK", flowObject.get(CreateSiebelAssociationBean.class.getName()));
	}

}
