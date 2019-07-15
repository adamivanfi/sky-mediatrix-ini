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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.nttdata.de.ityx.cx.workflow.outgoing.mxpostprocessing.OutboundConfiguration.DOCUMENTS_COUNT;
import static com.nttdata.de.ityx.cx.workflow.outgoing.mxpostprocessing.OutboundConfiguration.REQ_DOCS;

public class GetMXOutboundBundle extends AbstractWflBean {

    @Override
    public void execute(IFlowObject flowObject) throws Exception {

        Connection con=null;
        try {
            con = MxDbSingleton.getMxConnection(flowObject);
            int queueSize = MxOutboundIntegration.getCxOutboundDocumentProcessesCountFromDB(con, flowObject, "%", tenant);
            if (queueSize == 0) {
                Thread.sleep(180000); // 3Min
                flowObject.put(REQ_DOCS, new LinkedList<CDocumentContainer>());
                flowObject.put(DOCUMENTS_COUNT, 0);
                return;
            } else if (queueSize < ((Integer) flowObject.get(MxOutboundIntegration.MX2CX_MAXCOUNT)) / 4) {
                Thread.sleep(30000); // 30 sec
                SkyLogger.getWflLogger().debug("809:1M: " + queueSize);
            } else {
                SkyLogger.getWflLogger().debug("809:I: " + queueSize);
            }
            List<CDocumentContainer> reqDocs = new LinkedList<>();
            List<Map<String, Object>> docsMetaContainer = MxOutboundIntegration.getCxOutboundDocumentProcessesFromDB(con, flowObject, "%",tenant, ((Integer) flowObject.get(MxOutboundIntegration.MX2CX_MAXCOUNT)));
            for (Map<String, Object> docMeta : docsMetaContainer) {
                String documentID = (String) docMeta.get(TagMatchDefinitions.DOCUMENT_ID);
                CDocumentContainer docC = createSimplifiedTextDocument(flowObject, documentID);
                CDocument doc = DocContainerUtils.getDoc(docC);
                for (String nextField : docMeta.keySet()) {
                    doc.setNote(nextField, docMeta.get(nextField));
                }
                //setDocID(docCon, doc, documentID);
                doc.setNote(TagMatchDefinitions.DOCUMENT_ID, documentID);
                //docCon.setExternalID(documentID); // das geht nicht
                docC.setNote(TagMatchDefinitions.DOCUMENT_ID, documentID);
                reqDocs.add(docC);
                SkyLogger.getCommonLogger().debug("Child loaded:" + documentID);
            }
            flowObject.put(REQ_DOCS, reqDocs);
            flowObject.put(DOCUMENTS_COUNT, docsMetaContainer.size());
        }finally {
           MxDbSingleton.closeConnection(con);
        }
    }


    @Override
    public KeyConfiguration[] getKeys() {
        return new KeyConfiguration[]{
				new KeyConfiguration(REQ_DOCS,List.class),
						new KeyConfiguration(DOCUMENTS_COUNT, Integer.class)
								};
    }
}
