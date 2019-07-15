package com.nttdata.de.sky.ityx.contex.importChannel;

import com.nttdata.de.lib.logging.SkyLogger;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contexdesigner.exflow.states.FlowObject;
import org.junit.Test;

import java.io.File;
import java.net.URL;

import static org.junit.Assert.*;

public class FaxMetadataBeanTest {

	@Test
	public void testExecutePositive() {

		FaxMetadataBean bean = new FaxMetadataBean();

		String baseDir = ".";
		URL base = this.getClass().getResource("/DMSftp");
		if (base != null) {
			baseDir = new File(base.getFile().replaceAll("%20", " ")).getAbsolutePath();
		} else {
			fail("No test data available");
		}

		// The fax file for this test case
		String faxFile = baseDir+File.separator+"Fax"+File.separator+"tmp"+File.separator+"2725668_0.tif";

		IFlowObject flowObject = new FlowObject();

		flowObject.put("doc.uri", faxFile);
		
		flowObject.put("importFax", baseDir + File.separator + "Fax");

		try {
			bean.execute(0, flowObject, null);
		} catch (Exception e) {
			fail("Exception: "+e.getMessage());
		}

		Boolean s = (Boolean)flowObject.get(FaxMetadataBean.KEY_VALIDFILE);
		assertNotNull(s);

		assertTrue(s.booleanValue());


		String origin = (String)flowObject.get(FaxMetadataBean.KEY_FAX_ORIGIN);
		String rcvtime = (String)flowObject.get(FaxMetadataBean.KEY_FAX_RCVTIME);

		assertNotNull(origin);
		assertTrue(!"".equals(origin));
		SkyLogger.getTestLogger().debug("ORIGIN: "+origin);
		assertTrue(!"".equals(rcvtime));
		SkyLogger.getTestLogger().debug("RCVTIME: "+rcvtime);
		assertTrue(true);
	}

	@Test
	public void testExecuteNegative() {

		FaxMetadataBean bean = new FaxMetadataBean();

		String faxFile = "C:\\mediatrix_data\\DMSftp\\Fax\\xxxxxxx_0.tif";

		IFlowObject flowObject = new FlowObject();

		flowObject.put("doc.uri", faxFile);

		try {
			bean.execute(0, flowObject, null);
		} catch (Exception e) {
			fail("Exception: "+e.getMessage());
		}

		SkyLogger.getTestLogger().debug(flowObject.get(FaxMetadataBean.KEY_FAX_ORIGIN));
		SkyLogger.getTestLogger().debug(flowObject.get(FaxMetadataBean.KEY_FAX_RCVTIME));

		Boolean s = (Boolean)flowObject.get(FaxMetadataBean.KEY_VALIDFILE);

		assertNotNull(s);
		assertFalse(s.booleanValue());
		assertTrue(true);
	}
}
