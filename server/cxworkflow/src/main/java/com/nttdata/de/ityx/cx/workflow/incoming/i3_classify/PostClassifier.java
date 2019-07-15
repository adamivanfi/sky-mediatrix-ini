package com.nttdata.de.ityx.cx.workflow.incoming.i3_classify;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflReportedBean;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.document.CDocument;

/**
 * Created by meinusch on 30.11.15.
 */
public class PostClassifier extends AbstractWflReportedBean {

	@Override
	public void execute(IFlowObject flowObject) throws Exception {
		CDocumentContainer cont = DocContainerUtils.getDocContainer(flowObject);
		CDocument doc = DocContainerUtils.getDoc(flowObject);

		Object catArrayO = flowObject.get("catarray");
		if (catArrayO != null && !(catArrayO instanceof String)) {
			String caResult = "";
			double firstRel = 0.0;

			if (catArrayO instanceof de.ityx.lingua.categorizer.Category[]) {
				for (de.ityx.lingua.categorizer.Category cat : (de.ityx.lingua.categorizer.Category[]) catArrayO) {
					caResult += cat.getName() + ":" + cat.getProbability() + " ";
					if (firstRel == 0.0) {
						firstRel = cat.getProbability();
					}
				}
			} else if (catArrayO instanceof de.ityx.contex.data.icat.Category[]) {
				for (de.ityx.contex.data.icat.Category cat : (de.ityx.contex.data.icat.Category[]) catArrayO) {
					caResult += cat.getPath() + ":" + cat.getRelevance() + " ";
					if (firstRel == 0.0) {
						firstRel = cat.getRelevance();
					}
				}
			}
			caResult = caResult.substring(0, caResult.length() < 200 ? caResult.length() : 200);
			flowObject.put("cattarayResult", caResult);
			doc.setNote(TagMatchDefinitions.FORMTYPERELIABILITY, ((int) (firstRel * 100)));
			cont.setNote(TagMatchDefinitions.FORMTYPERELIABILITY, ((int) (firstRel * 100)));
			SkyLogger.getWflLogger().info("300: " + DocContainerUtils.getDocID(doc) + " cattarayResult:" + caResult + " bestcat:" + flowObject.get("bestcat")+((int) (firstRel * 100)));
		} else {
			doc.setNote(TagMatchDefinitions.FORMTYPERELIABILITY, 100); //ComplexTermClassifier or Regex
			cont.setNote(TagMatchDefinitions.FORMTYPERELIABILITY, 100); //ComplexTermClassifier or Regex
			SkyLogger.getWflLogger().info("300: " + DocContainerUtils.getDocID(doc) + " catArray not Found:" + flowObject.get("bestcat") + " setting reliability for rexex to 100%");
		}
		flowObject.put("parameter", "400_CustomerIndexing");

	}
}
