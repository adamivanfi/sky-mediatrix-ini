package com.nttdata.de.ityx.cx.workflow.incoming.i1_import;

import com.nttdata.de.ityx.cx.workflow.incoming.DocpoolBean;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.ityx.sharedservices.utils.ShedulerUtils;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.document.CDocument;

import java.util.Date;
import java.util.Map;

/**
 * Created by meinusch on 05.02.16.
 */
public class SetVosMksFormtype extends DocpoolBean {
	
	@Override
	public void execute(IFlowObject flowObject) throws Exception {
		CDocumentContainer cont = DocContainerUtils.getDocContainer(flowObject);
		CDocument doc = DocContainerUtils.getDoc(flowObject);
		doc.setNote("Channel", "FAX");
		setFormtype(flowObject, cont, doc);
		Date incommingdate = new java.util.Date();
		cont.setNote("DOCUMENTSOURCE", doc.getUri());
		cont.setNote("DOCUMENTKEY", doc.getTitle());
		Map<String, Object> map = DocContainerUtils.setIncommingDate(cont, doc, incommingdate, incommingdate);
		flowObject.put("map", map);
		super.execute(flowObject);
	}
	
	public void setFormtype(IFlowObject flow, CDocumentContainer cont, CDocument doc) throws Exception {
		String formtypeToSet = getFormType();
		DocContainerUtils.setExtFormtype(flow, cont, doc, formtypeToSet);
	}
	
	public String getFormType() {
		return "vosbelege_mks";
	}
	
	@Override
	public String getMaster() {
		return ShedulerUtils.getDefaultMaster();
	}
	
	@Override
	public String getParameter(IFlowObject flow) {
		return "220_PreprocessingOCR";
	}
		
}