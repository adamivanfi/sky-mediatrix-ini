package com.nttdata.de.ityx.cx.workflow.incoming.i4_customerindexing;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflReportedBean;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.impl.document.text.email.EmailDocument;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.document.CDocument;

public class AfterTextExtraction extends AbstractWflReportedBean {

    @Override
    public void execute(IFlowObject flowObject) throws Exception {
        if (DocContainerUtils.getDocContainer(flowObject, Preparation.ORG_DOC) != null) {

            flowObject.put(Preparation.CURR_DOC, DocContainerUtils.getDocContainer(flowObject, Preparation.ORG_DOC));
            if (!((Boolean) flowObject.get("IndexedDocument"))) {
                CDocumentContainer txtcont = (CDocumentContainer) flowObject.get(Preparation.TXT_DOC);
                SkyLogger.getWflLogger().debug("400 " + DocContainerUtils.getDocID(flowObject) + " ATE NotIndexed"); //: TXT (body):" + txtcont.getContentAsString());

                CDocumentContainer docC = DocContainerUtils.getDocContainer(flowObject, Preparation.ORG_DOC);
                CDocument doc = docC.getDocument(0);
                if (doc.getClass().equals(EmailDocument.class)) {
                    EmailDocument edoc = (EmailDocument) doc;
                    //SkyLogger.getWflLogger().debug("400 " + getDocID(flowObject) + " ATE NotIndexed: TXT:ABody:" + edoc.getAlternativeBody());
                    //SkyLogger.getWflLogger().debug("400 " + getDocID(flowObject) + " ATE NotIndexed: TXT:ContentAS:" + edoc.getContentAsString());
                    //SkyLogger.getWflLogger().debug("400 " + getDocID(flowObject) + " ATE NotIndexed: TXT:ContentCType:" + edoc.getContenttype());

                }

            }
        } else {
            SkyLogger.getWflLogger().warn("400 " + DocContainerUtils.getDocID(flowObject) + " ATE ORGDocNotFound");
            SkyLogger.getWflLogger().debug("400 " + DocContainerUtils.getDocID(flowObject) + " ATE TXT_DOC " + DocContainerUtils.getDocContainer(flowObject, Preparation.TXT_DOC));
            SkyLogger.getWflLogger().debug("400 " + DocContainerUtils.getDocID(flowObject) + " ATE CURR_DOC " + DocContainerUtils.getDocContainer(flowObject, Preparation.CURR_DOC));
            SkyLogger.getWflLogger().debug("400 " + DocContainerUtils.getDocID(flowObject) + " ATE ORG_DOC " + DocContainerUtils.getDocContainer(flowObject, Preparation.ORG_DOC));
        }
    }
}
