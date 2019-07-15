package com.nttdata.de.sky.ityx.contex.connector.siebel;

import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.extag.TagMatch;
import de.ityx.contexdesigner.exflow.states.FlowObject;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ValidateExtractedMandateDataBeanTest {

	@Test
	public void testExecute() {
		ValidateExtractedMandateDataBean bean = new ValidateExtractedMandateDataBean();

		// [mandate_Tags = [ VerifiedMandateNumber = 42866474,
		// VerifiedCustomerNumber = 1347664559, VerifiedContractNumber =
		// 42866474]] java.util.ArrayList
		IFlowObject flowObject = new FlowObject();

		ArrayList<TagMatch> list = new ArrayList<TagMatch>();
		TagMatch mandate_Tags = new TagMatch("mandate_Tags");
		mandate_Tags.add(new TagMatch("VerifiedMandateNumber", "42866474"));
		mandate_Tags.add(new TagMatch("VerifiedCustomerNumber", "1347664559"));
		mandate_Tags.add(new TagMatch("VerifiedContractNumber", "42866474"));
		list.add(mandate_Tags);
		flowObject.put("customer", list);

		try {
			bean.execute(0, flowObject, null);
		} catch (Exception e) {
			fail("Exception: " + e.getMessage());
		}

		// assertNotNull(flowObject.get("CustomerName"));
		assertTrue((Boolean) flowObject.get("VerifiedDocument"));
	}
}
