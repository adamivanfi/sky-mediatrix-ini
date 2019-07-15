package com.nttdata.de.ityx.cx.workflow.incoming.i4_customerindexing;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflReportedBean;
import com.nttdata.de.ityx.cx.workflow.base.FlowUtils;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.document.CDocument;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReduceText extends AbstractWflReportedBean {

	@Override
	public void execute(IFlowObject flowObject) throws Exception {

		if (FlowUtils.getOptionalBoolean(flowObject, "ADDRS_FM_USEREDUCEDTEXT", true)) {
			//config
			String wordextractionpattern = FlowUtils.getOptionalString(flowObject, "ADDRS_FM_PATTERN", "[@\\w\\d:;\\-_,\\.#ÄäüÜöÖß\\?\\[\\]\\(\\)%!\\r\\n]{2,40}");
			int maxwords = FlowUtils.getOptionalInt(flowObject, "ADDRS_FM_MAXWORDS", 700);
			if (maxwords < 1) { maxwords = 700;}
			// end config
			
			//CDocument doc = getDoc(flowObject, "txtdoc");
			CDocument doc = DocContainerUtils.getDoc(flowObject, "doc");
			
                        if (doc==null){
			     doc = DocContainerUtils.getDoc(flowObject);
			}
			
			String docid = DocContainerUtils.getDocID(flowObject);
			String text = doc.getContentAsString();
			
			if (text != null) {
				String reducedtext = "";
				int count = 0;

				for (Matcher m = Pattern.compile(wordextractionpattern).matcher(text); m.find() && count < maxwords; count++){
					reducedtext += m.group(0) + " ";
				}
				if (reducedtext.length() < 2) {
					reducedtext = text.substring(0, Math.min(reducedtext.length(), maxwords * 5));
				}
				//SkyLogger.getWflLogger().debug("400 "+docid + " Red:" + reducedtext);

                                //flowObject.put("txtdoc", createSimplifiedTextDocument(flowObject, reducedtext));
                                flowObject.put("doc", createSimplifiedTextDocument(flowObject, reducedtext));
			}
		} else {
			SkyLogger.getWflLogger().debug("Usage of TextReduction disabled");
		}
	}
}
