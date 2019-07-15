package com.nttdata.de.ityx.cx.workflow.incoming.i5_crm;


import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.document.CDocument;
import de.ityx.contex.interfaces.extag.TagMatch;

import java.util.LinkedList;
import java.util.List;

public class FHVM_PrepareMandateContactCreation extends PrepareSiebelContactCreation {

	@Override
	public void execute(IFlowObject flowObject) throws Exception {
                super.execute(flowObject);                
		CDocument document = DocContainerUtils.getDoc(flowObject);
		if (document != null) {
			String docid = DocContainerUtils.getDocID(document);
			String mandatenumber = (String) document.getNote(TagMatchDefinitions.SEPA_MANDATE_NUMBER);
			if(mandatenumber==null || mandatenumber.replaceAll("\\s", "").isEmpty()) {
				for(TagMatch tag:document.getTags()) {
					if (tag.getIdentifier().equals(TagMatchDefinitions.SEPA_MANDATE_NUMBER)) {
						String tagValue = tag.getTagValue();
						if(tagValue!=null && !(tagValue.replaceAll("\\s", "").isEmpty())) {
							mandatenumber=tagValue;
							document.setNote(TagMatchDefinitions.SEPA_MANDATE_NUMBER, mandatenumber);
							break;
						}
					}
				}
			}
			
			if (mandatenumber != null) {
				flowObject.put("VerifiedMandatenNumber", mandatenumber);
			} else {
				SkyLogger.getWflLogger().debug("500_PrepMandate: "+docid+" No VerifiedMandateNumber for DocumentID: " );
			}
		}
		
		flowObject.put(TagMatchDefinitions.FORM_TYPE_CATEGORY, TagMatchDefinitions.SEPA_MANDATE);
		DocContainerUtils.setFormtype(DocContainerUtils.getDocContainer(flowObject), document, TagMatchDefinitions.SEPA_MANDATE);

		// Sets signature flag.
		List<TagMatch> tags = document != null ? document.getTags() : null;
        TagMatch sFlag = new TagMatch(TagMatchDefinitions.SEPA_SIGNATURE_FLAG,"signed");
        boolean changed=false;
		if (tags != null) {
			for (TagMatch tm : tags) {
				if (tm.getIdentifier().equals(TagMatchDefinitions.SEPA_SIGNATURE_FLAG)) {
					tm.setTagValue("signed");
					changed=true;
				}
			}
		}
		if(!changed) {
			if (tags==null){
				tags= new LinkedList<>();
			}
			tags.add(sFlag);
			if (document != null) {
				document.setTags(tags);
			}
		}
		document.setTitle(SEPA_MANDAT_AUTOMAT_VERARBEITUNG + document.getTitle());                                                            
		String validity = "1";
		document.setNote(TagMatchDefinitions.SEPA_SIGNATURE_FLAG, validity);
		flowObject.put(TagMatchDefinitions.SEPA_SIGNATURE_FLAG, validity);
		SkyLogger.getWflLogger().debug("DocumentID=" + DocContainerUtils.getDocID(document) + " " + TagMatchDefinitions.SEPA_SIGNATURE_FLAG + "=" + validity);			
	}
}
