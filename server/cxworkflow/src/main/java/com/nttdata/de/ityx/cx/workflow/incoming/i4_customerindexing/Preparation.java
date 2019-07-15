package com.nttdata.de.ityx.cx.workflow.incoming.i4_customerindexing;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflReportedBean;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.contex.impl.designer.state.KeyConfiguration;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.impl.document.image.ocr.OCRDocument;
import de.ityx.contex.impl.document.text.email.EmailDocument;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.document.CDocument;

public class Preparation extends AbstractWflReportedBean {

    public final static String ORG_DOC = "orgdoc";
    public final static String TXT_DOC = "txtdoc";
    public final static String CURR_DOC = "doc";

    @Override
    public void execute(IFlowObject flowObject) throws Exception {

        flowObject.put(ORG_DOC, DocContainerUtils.getDocContainer(flowObject));
        CDocument doc = DocContainerUtils.getDoc(flowObject);
        String text = "";
        String docid = DocContainerUtils.getDocID(doc);
        if (doc.getClass().equals(EmailDocument.class)) {
            EmailDocument edoc = (EmailDocument) doc;
            //text = doc.getNote("text") .ggetContentAsString();
            String header = "";
            if (edoc.getFrom() != null) {
                header += " " + edoc.getFrom() + " \n";
            }
            String replyto= TagMatchDefinitions.extractOrgHeader(edoc.getHeaders(), "Reply-To");
            if (replyto!= null) {
                header += " " + replyto + " \n";
            }            
            /* - für die Kundenindizierung sind To und cc nicht relevant führen aber möglicherweise zur falschindizierung   
            if (edoc.getTo() != null) {
                header += " " + edoc.getTo() + " \n";
            }
            if (edoc.getCC() != null) {
                header += " " + edoc.getCC() + " \n";
            }
           */ 
           if (edoc.getSubject() != null) {
                header += " " + edoc.getSubject() + " \n";
            }
            SkyLogger.getWflLogger().debug("400: " + docid + " Prep: Email:" ); // + text
            String body=edoc.getBody();
            if (body!=null && !body.isEmpty() && body.trim().length()>10){
                text = header + " \n " + body;
            }else{
                body=edoc.getContentAsString();
                if (body!=null && !body.isEmpty() && body.trim().length()>10){
                    text = header + " \n " + body;
                }else{
                      text = header + " \n " + edoc.getAlternativeBody();
                }
            }
            
            //SkyLogger.getWflLogger().debug("400: " + docid + " Prep: EmailText:" ); // + text
        } else if (doc.getClass().equals(OCRDocument.class)) {
            text = doc.getContentAsString();
            //SkyLogger.getWflLogger().debug("400: " + docid + " Prep: OCR-Content " ); //+ text
        } else {
            text = doc.getContentAsString();
            SkyLogger.getWflLogger().debug("400: " + docid + " Prep: class:" + doc.getClass() + " content " ); //+ text
        }
        CDocumentContainer txtdoc = createSimplifiedTextDocument(flowObject, text);
        flowObject.put(TXT_DOC, txtdoc);
        flowObject.put(CURR_DOC, txtdoc);

    }
	@Override
	public KeyConfiguration[] getKeys() {
		return new KeyConfiguration[]{
				new KeyConfiguration(TXT_DOC, CDocumentContainer.class),
				new KeyConfiguration(ORG_DOC, CDocumentContainer.class),
				new KeyConfiguration(CURR_DOC, CDocumentContainer.class)
		};
	}
	}
