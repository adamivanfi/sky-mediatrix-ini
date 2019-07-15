package com.nttdata.de.ityx.cx.workflow.incoming.i3_classify;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.impl.document.text.email.EmailDocument;
import de.ityx.contex.impl.document.text.string.StringDocument;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.document.CDocument;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PrepareClassification extends AbstractWflBean {

	private static final long	serialVersionUID	= 722380631831369242L;

	@Override
	public void execute(IFlowObject flowObject) throws Exception {

		flowObject.put("CreateSiebelContactBean.simException", TagMatchDefinitions.FALSE);

		CDocumentContainer cont = (CDocumentContainer) flowObject.get("doc");
		flowObject.put("xcont", cont);

		CDocument doc = cont.getDocument(0);

		if (doc.getClass().equals(EmailDocument.class)) {
			StringDocument sdoc = (StringDocument) doc.getNote("text");

			//if (doc.getUri() != null)
			//	sdoc.setUri(doc.getUri());
			//if (doc.getWhiteKey() != null)
			//	sdoc.setWhiteKey(doc.getWhiteKey());
			if (doc.headers() != null) {
				List l = new LinkedList<>();
				Iterator i = doc.headers();
				while (i.hasNext()) {
					l.add(i.next());
				}
				sdoc.setHeaders(l);
			}
			if (doc.getTitle() != null)
				sdoc.setTitle(doc.getTitle());
			if (doc.getAnnotations() != null)
				sdoc.setAnnotations(doc.getAnnotations());
			if (doc.getTags() != null)
				sdoc.setTags(doc.getTags());

			for (Map.Entry<String, Object> note : doc.getNotes().entrySet()) {
				sdoc.setNote(note.getKey(), note.getValue());
			}

			CDocumentContainer out = new CDocumentContainer(sdoc);
			out.setTags(cont.getTags());

			if (cont.getNotes()!=null) {
				for (java.util.Map.Entry<String, Object> note : cont.getNotes().entrySet()) {
					out.setNote(note.getKey(), note.getValue());
				}
			}
			flowObject.put("doc", out);
		}
	}

}
