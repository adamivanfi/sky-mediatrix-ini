package com.nttdata.de.ityx.cx.workflow.incoming.i3_classify;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.contex.data.icat.Category;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.impl.document.text.email.EmailDocument;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.document.CDocument;

public class AfterClassification extends AbstractWflBean {

    @Override
    public void execute(IFlowObject flowObject) throws Exception {
        CDocumentContainer cont = (CDocumentContainer) flowObject.get("doc");
        CDocument doc = cont.getDocument(0);
        String formtype = doc.getFormtype();
        String bestcat = (String) flowObject.get("bestcat");

        if (flowObject.get("cattaray") != null && !(flowObject.get("cattaray") instanceof String)) {
            String caResult = "";
            for (Category cat : (Category[]) flowObject.get("cattaray")) {
                caResult += cat.getPath() + ":" + cat.getRelevance() + " ";
            }
            caResult = caResult.substring(0, caResult.length() < 200 ? caResult.length() : 200);
            flowObject.put("caResult", caResult);
            SkyLogger.getWflLogger().info("300: " + DocContainerUtils.getDocID(doc) + " bestcat:" + bestcat + " catarray:" + caResult);
        } else {
            SkyLogger.getWflLogger().info("300: " + DocContainerUtils.getDocID(doc) + " bestcat:" + bestcat);
        }

        if (formtype.equals("unclassified")) {
         // wenn reliability von Classifikator benutzt wird
            DocContainerUtils.setFormtype(cont, doc, "systemdefault");
        }   
        //    flowObject.set("ErrorBean_GenerateError", new Boolean(true));
        //} else {
        //    flowObject.set("ErrorBean_GenerateError", new Boolean(false));
       // }

        CDocumentContainer xcont = (CDocumentContainer) flowObject.get("xcont");
        if (xcont != null) {
            CDocument xdoc = xcont.getDocument(0);
            
            Object catarrayD = doc.getNote("cats");
            if (catarrayD != null) {
                xdoc.setNote("cats", catarrayD);
                if (flowObject.get("cattaray") == null) {
                    flowObject.put("catarray", catarrayD);
                }
            }
            if (xdoc != null && xdoc.getClass().equals(EmailDocument.class)) {

                DocContainerUtils.setFormtype(xcont, xdoc, formtype);
                //xdoc.setNote(TagMatchDefinitions.CHANNEL,null);
                xcont.setDocument(0, xdoc);
                flowObject.put("doc", xcont);
            }
        }
        if (doc.getClass().equals(EmailDocument.class)) {
            doc.setNote(TagMatchDefinitions.CHANNEL, "EMAIL");
        }

    }

}
