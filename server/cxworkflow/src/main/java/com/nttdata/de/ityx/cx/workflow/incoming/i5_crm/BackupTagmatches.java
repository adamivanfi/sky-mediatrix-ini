package com.nttdata.de.ityx.cx.workflow.incoming.i5_crm;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.document.CDocument;

public class BackupTagmatches extends AbstractWflBean {

	private static final long	serialVersionUID	= -1L;
	@Override
	public void execute(IFlowObject flowObject) throws Exception {

		CDocument document=DocContainerUtils.getDoc(flowObject);
		DocContainerUtils.saveTagsBeforeVcat(flowObject, document);
	}
}