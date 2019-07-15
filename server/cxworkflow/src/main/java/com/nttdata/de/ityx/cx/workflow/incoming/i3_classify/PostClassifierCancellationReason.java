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
public class PostClassifierCancellationReason extends AbstractWflReportedBean {

	@Override
	public void execute(IFlowObject flowObject) throws Exception {

		CDocumentContainer cont = DocContainerUtils.getDocContainer(flowObject);
		CDocument doc = DocContainerUtils.getDoc(flowObject);

		String cancelReason = (String) flowObject.get("cancelReason");
		if (cancelReason != null && !cancelReason.isEmpty()) {
			String cancelReasonCode = getCRCode(cancelReason);
			doc.setNote(TagMatchDefinitions.CANCELLATION_REASON, cancelReason);
			doc.setNote(TagMatchDefinitions.CANCELLATION_REASON_CODE, cancelReasonCode);

			cont.setNote(TagMatchDefinitions.CANCELLATION_REASON, cancelReason);
			cont.setNote(TagMatchDefinitions.CANCELLATION_REASON_CODE, cancelReasonCode);
			SkyLogger.getWflLogger().info("300: " + DocContainerUtils.getDocID(doc) + " CancelReason:" + cancelReason + " code:" + cancelReasonCode);

			Object cancelReasonsArrayO = flowObject.get("cancelReasonsArray");
			if (cancelReasonsArrayO != null && !(cancelReasonsArrayO instanceof String)) {
				String caResult = "";
				double firstRel = 0.0;

				if (cancelReasonsArrayO instanceof de.ityx.lingua.categorizer.Category[]) {
					for (de.ityx.lingua.categorizer.Category cat : (de.ityx.lingua.categorizer.Category[]) cancelReasonsArrayO) {
						caResult += cat.getName() + ":" + cat.getProbability() + " ";
						if (firstRel == 0.0) {
							firstRel = cat.getProbability();
						}
					}

				} else if (cancelReasonsArrayO instanceof de.ityx.contex.data.icat.Category[]) {
					for (de.ityx.contex.data.icat.Category cat : (de.ityx.contex.data.icat.Category[]) cancelReasonsArrayO) {
						caResult += cat.getPath() + ":" + cat.getRelevance() + " ";
						if (firstRel == 0.0) {
							firstRel = cat.getRelevance();
						}
					}
				}

				caResult = caResult.substring(0, caResult.length() < 200 ? caResult.length() : 200);
				flowObject.put("cancelationReasonResult", caResult);
				doc.setNote("cancelationReasonResult", cancelReason);
				SkyLogger.getWflLogger().info("300: " + DocContainerUtils.getDocID(doc) + " cancelReason:" + cancelReason + " cancelationReasonResult:" + caResult + " ftype" + flowObject.get("bestcat"));
			} else {
				SkyLogger.getWflLogger().info("300: " + DocContainerUtils.getDocID(doc) + " cancelReason:" + cancelReason + " formtype:" + flowObject.get("bestcat"));
			}

		} else {
			SkyLogger.getWflLogger().info("300: " + DocContainerUtils.getDocID(doc) + " CancelReason not Found:" + flowObject.get("bestcat"));
		}
		//Recovery Formtype
		String formtype = (String) flowObject.get(PreClassifierCancellationReason.TMP_Formtype);
		DocContainerUtils.setFormtype(flowObject, cont, doc, formtype);

		Object cats=flowObject.get(PreClassifierCancellationReason.TMP_CATS);
		if (cats!=null) {
			flowObject.set("cats", cats);
		}

		Object catA=flowObject.get(PreClassifierCancellationReason.TMP_CATARRAY);
		if (catA!=null) {
			flowObject.set("catarray", catA);
		}
	}

	private String getCRCode(String cancelReason) {
		switch (cancelReason) {
			case "zu teuer_k":
				return "ZU TEUER_K";
			default:
				return cancelReason.toUpperCase();
		}
	}

}
