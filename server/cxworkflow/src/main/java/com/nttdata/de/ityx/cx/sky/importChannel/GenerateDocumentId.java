/**
 * 
 */
package com.nttdata.de.ityx.cx.sky.importChannel;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.contex.impl.designer.state.KeyConfiguration;
import de.ityx.contex.interfaces.designer.IFlowObject;

import java.util.Date;

/**
 * @author DHIFLM
 * 
 */
public class GenerateDocumentId extends AbstractWflBean {
	@Override
	public void execute(IFlowObject flow) throws Exception {
            String docid = DocContainerUtils.getOrGenerateDocID(flow, TagMatchDefinitions.DocumentDirection.INBOUND, DocContainerUtils.getChannelType(DocContainerUtils.getDoc(flow)), new Date());
            flow.put(TagMatchDefinitions.DOCUMENT_ID, docid);
         }
	@Override
	public KeyConfiguration[] getKeys() {
		return new KeyConfiguration[] {new  KeyConfiguration (TagMatchDefinitions.DOCUMENT_ID, String.class) };
	}
}
