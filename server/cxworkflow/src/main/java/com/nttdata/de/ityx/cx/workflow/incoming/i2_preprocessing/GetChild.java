package com.nttdata.de.ityx.cx.workflow.incoming.i2_preprocessing;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;
import com.nttdata.de.lib.logging.SkyLogger;
import de.ityx.contex.impl.designer.state.KeyConfiguration;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.interfaces.designer.IFlowObject;

import java.util.List;

public class GetChild extends AbstractWflBean {

	private static final String SPLITTEDDOC = "splitteddoc";

	@Override
	public void execute(IFlowObject flowObject) throws Exception {

		List<CDocumentContainer> splitteddocs = (List<CDocumentContainer>) flowObject.get(SPLITTEDDOC);
		final Integer count = (Integer) flowObject.get("count");
		SkyLogger.getWflLogger().info("220: Splitted:" + count);
		if (splitteddocs != null) {
			CDocumentContainer splitteddoc = splitteddocs.get(count);
			flowObject.put(SPLITTEDDOC, splitteddoc);
		}else{
			SkyLogger.getWflLogger().info("220: Warn: no access to spitteddocs:" + count);

		}
	}

	public KeyConfiguration[] getKeys() {
		return new KeyConfiguration[]{new KeyConfiguration(SPLITTEDDOC, CDocumentContainer.class)};
	}
	
	
}
