package com.nttdata.de.ityx.cx.workflow.incoming.i3_classify;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.contex.impl.designer.state.KeyConfiguration;
import de.ityx.contex.impl.document.text.email.EmailDocument;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.document.CDocument;

/**
 *
 * @author MEINUG
 */
@Deprecated // see ComplextermClassifier
public class SpamPrerouting extends AbstractWflBean {

    @Override
    public void execute(IFlowObject flowObject) throws Exception {
        CDocument doc = DocContainerUtils.getDoc(flowObject);
        if (doc != null && doc.getClass().equals(EmailDocument.class)) {
            EmailDocument edoc = ((EmailDocument) doc);
            String header = edoc.getHeaders();
            String subject = edoc.getSubject();
            if ((header != null && header.contains("X-SKYDE-EOP: This message appears to be spam.")) || (subject != null && subject.contains("SKYDE EOP: MOST LIKELY SPAM"))) {
                doc.setTitle("SPAM:" + doc.getTitle());

                DocContainerUtils.setFormtype(DocContainerUtils.getDocContainer(flowObject),edoc,"spam");
                doc.setNote(TagMatchDefinitions.EVAL_FORMTYPE, "spam");
                flowObject.put("spam", true);
                SkyLogger.getWflLogger().debug("300: "+ DocContainerUtils.getDocID(doc)+ " Categorized to spam");
            }
        }
    }
	@Override
	public KeyConfiguration[] getKeys() {
		return new KeyConfiguration[]{
				new KeyConfiguration("spam", Boolean.class)
		};
	}
  }
