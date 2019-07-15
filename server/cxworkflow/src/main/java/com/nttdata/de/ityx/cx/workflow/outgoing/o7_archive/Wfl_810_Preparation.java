package com.nttdata.de.ityx.cx.workflow.outgoing.o7_archive;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.document.CDocument;

public class Wfl_810_Preparation extends AbstractWflBean {

	@Override
	public void execute(IFlowObject flowObject) throws Exception {
		CDocument doc= DocContainerUtils.getDoc(flowObject);
                if (doc!=null){
                    flowObject.put(TagMatchDefinitions.DOCUMENT_ID, DocContainerUtils.getDocID(doc));
                    flowObject.put("MoveFileToArchive_Direction", doc.getNote("Direction"));
                }
	}
    
}
