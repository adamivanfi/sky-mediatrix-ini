package com.nttdata.de.ityx.cx.workflow.outgoing.mxpostprocessing;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import com.nttdata.de.sky.outbound.MxOutboundIntegration;
import de.ityx.contex.impl.designer.state.KeyConfiguration;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.document.CDocument;

import java.sql.Connection;
import java.util.List;

import static com.nttdata.de.ityx.cx.workflow.outgoing.mxpostprocessing.OutboundConfiguration.REQ_DOCS;

public class GetChild extends AbstractWflBean {

    @Override
    public void execute(IFlowObject flowObject) throws Exception {
        long mx2cxId = 0;
        try {
            List<CDocumentContainer> docs = (List<CDocumentContainer>) flowObject.get(REQ_DOCS);
            Integer docIndexKey = (Integer) flowObject.get("docIndexKey");
            
            if (docs.size() > 0) {
                CDocumentContainer<CDocument> outContainer = docs.get(docIndexKey);
                CDocument doc = DocContainerUtils.getDoc(flowObject, outContainer);
                String docId = (String) doc.getNote(TagMatchDefinitions.DOCUMENT_ID);
                String destProcess = (String) doc.getNote(MxOutboundIntegration.DOCPOOL_PARAMETER);
                mx2cxId = (Long) doc.getNote("mx2cxId");   

                SkyLogger.getWflLogger().debug("809GC: "+docId+" Size:" + docs.size()+" IndexKey:"+docIndexKey+" mx2cxID:"+mx2cxId+ " process:"+destProcess);
                Connection con=null;
                try{
                    con=MxDbSingleton.getMxConnection(flowObject);

                    MxOutboundIntegration.updateDbStatusOfCxOutboundDocumentQueue(con,mx2cxId,MxOutboundIntegration.MXOUT_STATUS.RUNNING, null);
                }finally {
                   MxDbSingleton.closeConnection(con);
                }
                flowObject.put(TagMatchDefinitions.DOCUMENT_ID, docId);
                flowObject.put(DocContainerUtils.DOC, outContainer);
                flowObject.put(MxOutboundIntegration.DOCPOOL_PARAMETER, destProcess);
             } else {
                SkyLogger.getWflLogger().error("809GC: Problems during processing of GetChild docSize:"+docIndexKey+"/"+docs.size());
            }

        } catch (Exception e) {
            if (mx2cxId >0) {
                Connection con=null;
                try{
                     con=MxDbSingleton.getMxConnection(flowObject);
                    MxOutboundIntegration.updateDbStatusOfCxOutboundDocumentQueue(con, mx2cxId, MxOutboundIntegration.MXOUT_STATUS.PROCERR,e.getMessage());
                }finally {
                   MxDbSingleton.closeConnection(con);
                }
              }
            SkyLogger.getWflLogger().error("809GC: Problems during processing of GetChild: "+e, e);
        }
    }

    @Override
    public KeyConfiguration[] getKeys() {
		return new KeyConfiguration[]{
				new KeyConfiguration(DocContainerUtils.DOC, CDocumentContainer.class),
		 		new KeyConfiguration(TagMatchDefinitions.DOCUMENT_ID, String.class),
				new KeyConfiguration(MxOutboundIntegration.DOCPOOL_PARAMETER,String.class)};
    }

}
