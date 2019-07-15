package com.nttdata.de.ityx.cx.workflow.incoming.i3_classify;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflReportedBean;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.ityx.sharedservices.utils.ShedulerUtils;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.document.CDocument;

/**
 * Created by meinusch on 12.07.16.
 */
public class PostSBSClassifier extends AbstractWflReportedBean {

	private String getMaster(){
		return "sbs";
	}
	private String getRouteToProcess(){
		return "SBS_600_MXInjection";
	}


	@Override
	public void execute(IFlowObject flowObject) throws Exception {
		CDocumentContainer cont = DocContainerUtils.getDocContainer(flowObject);
		CDocument doc = DocContainerUtils.getDoc(flowObject);
		String formtype=DocContainerUtils.getFormtype(doc);
		if (formtype.equals("unclassified")) {
			// wenn reliability von Classifikator benutzt wird
			DocContainerUtils.setFormtype(cont, doc, "systemdefault");
		}
		ShedulerUtils.storeDocAndScheduleProcess(ShedulerUtils.getDefaultUnit(), getMaster(), getRouteToProcess(), cont, doc);

	}
}
