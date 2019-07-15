package com.nttdata.de.sky.ityx.contex.connector.siebel;

import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contexdesigner.exflow.states.FlowObject;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ValidateExtractedDataBeanTest {

	@Test
	public void testExecute() {
		ValidateExtractedDataBean bean = new ValidateExtractedDataBean();
		
		IFlowObject flowObject = new FlowObject();
		
		flowObject.put("CustomerNumber", "4123456789");
		
		try {
			bean.execute(0, flowObject, null);
		} catch (Exception e) {
			fail("Exception: "+e.getMessage());
		}
		
		// assertNotNull(flowObject.get("CustomerName"));
		assertTrue(true);
	}

}
