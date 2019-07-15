package com.nttdata.de.ityx.cx.sky.enrichment;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;
import com.nttdata.de.ityx.cx.workflow.base.FlowUtils;
import com.nttdata.de.ityx.sharedservices.enrichment.custattrcollectors.SkyCustomerAttrCollector;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.ityx.sharedservices.utils.ShedulerUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.document.CDocument;
import de.ityx.contex.interfaces.extag.TagMatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class EnrichmentBean extends AbstractWflBean {


	@Override
	public void execute(IFlowObject flowObject) throws Exception {
		ShedulerUtils.checkAuth();
		CDocumentContainer cont = DocContainerUtils.getDocContainer(flowObject);
		enrichContainer(flowObject, cont, false);
	}


	public static void enrichContainer(IFlowObject flowObject, CDocumentContainer cont, boolean mustBeSafe) throws Exception {
		CDocument doc = DocContainerUtils.getDoc(cont);
		String docid = DocContainerUtils.getDocID(doc);

		String customerNumber = FlowUtils.getOptionalString(flowObject, "VerifiedCustomerNumber", null);
		String contractNumber = FlowUtils.getOptionalString(flowObject, "VerifiedContractNumber", null);
		String smartcardNumber = FlowUtils.getOptionalString(flowObject, "VerifiedSmartcardNumber", null);
		String mandateNumber = FlowUtils.getOptionalString(flowObject, "VerifiedMandateNumber", null);

		String documentid = DocContainerUtils.getDocID(flowObject);

		Map<String, String> attribs = new TreeMap<>();

		try {
			if (customerNumber == null || customerNumber.isEmpty()) {
				SkyLogger.getWflLogger().info("Enrichment:" + docid + ":SKIP:NoCustomerProvided");
			} else {
				SkyLogger.getWflLogger().info("Enrichment:" + docid + ":using Attribs:c:"+customerNumber+" v:"+contractNumber+" sn:"+smartcardNumber+" m::"+mandateNumber);
				if (mustBeSafe) {
					attribs = SkyCustomerAttrCollector.collectCustomerAttributesWithContractConsistencyCheck(documentid, customerNumber, contractNumber, smartcardNumber, mandateNumber);
				}else{
					attribs = SkyCustomerAttrCollector.collectCustomerAttributes(documentid, customerNumber, contractNumber, smartcardNumber, mandateNumber);
				}
			}
		}catch (Exception e){
			if (mustBeSafe) {
				flowObject.set("VerifiedCustomerNumber", null);
				flowObject.set("VerifiedContractNumber", null);
				flowObject.set("VerifiedSmartcardNumber", null);
				flowObject.set("VerifiedMandateNumber", null);
				SkyLogger.getWflLogger().error("Enrichment:" + docid + ":ERROR:ConsistenceCheck of Verified-Attributes failed:"+e.getMessage());
				throw e;
			}else {
				SkyLogger.getWflLogger().warn("Enrichment:" + docid + ":ERROR:ConsistenceCheck of Verified-Attributes failed:" + e.getMessage());
			}
		}
		List<TagMatch> list = new ArrayList<>();
		for (String key : TagMatchDefinitions.CUSTOMER_DATA) {
			String value = (attribs != null) ? attribs.get(key) : "";
			list.add(new TagMatch(key, value == null ? "" : value));
			SkyLogger.getWflLogger().debug("Enrichment:" + docid + ": Cadd:" + key + ":" + value);
		}

		if (attribs!=null && (contractNumber == null || contractNumber.isEmpty())){
			contractNumber = attribs.get(TagMatchDefinitions.TAGMATCH_CONTRACT_NUMBER);
		}
		if (attribs!=null &&(smartcardNumber == null || smartcardNumber.isEmpty())) {
			smartcardNumber = attribs.get(TagMatchDefinitions.TAGMATCH_SMARTCARD_NUMBER);
		}
		if (attribs!=null &&(mandateNumber == null || mandateNumber.isEmpty()) ){
			mandateNumber = attribs.get(TagMatchDefinitions.SEPA_MANDATE_NUMBER);
		}

		list.add(new TagMatch(TagMatchDefinitions.CUSTOMER_ID, prepareString(customerNumber)));
		list.add(new TagMatch(TagMatchDefinitions.TAGMATCH_CONTRACT_NUMBER, prepareString(contractNumber)));
		list.add(new TagMatch(TagMatchDefinitions.TAGMATCH_SMARTCARD_NUMBER, prepareString(smartcardNumber)));
		list.add(new TagMatch(TagMatchDefinitions.EVAL_CUSTOMER_NUMBER, prepareString(customerNumber)));
		list.add(new TagMatch(TagMatchDefinitions.EVAL_CONTRACT_NUMBER, prepareString(contractNumber)));
		list.add(new TagMatch(TagMatchDefinitions.SEPA_MANDATE_NUMBER, prepareString(mandateNumber)));


		for (TagMatch tag : doc.getTags()) {
			if (tag.getIdentifier() != null  && !tag.getIdentifier().equalsIgnoreCase("customer")) {
				boolean tagmatchfound=false;
				for (TagMatch ltm:list){
					if (ltm.getIdentifier().equals(tag.getIdentifier())){
						if ((ltm.getTagValue()==null || ltm.getTagValue().isEmpty()) && tag.getTagValue()!=null){
							ltm.setTagValue(tag.getTagValue());
						}
						tagmatchfound=true;
						if (!ltm.getTagValue().equals(tag.getTagValue())){
							SkyLogger.getWflLogger().warn("Enrichment:TM mit unterschiedlichen Werten!:" + docid + ": Oadd:" + tag.getIdentifier() + ":" + tag.getTagValue()+ " Aadd:" + ltm.getIdentifier() + ":" + ltm.getTagValue());
						}
					}
				}
				if (!tagmatchfound){
					list.add(tag);
				}

				SkyLogger.getWflLogger().debug("Enrichment:" + docid + ": Oadd:" + tag.getIdentifier() + ":" + tag.getTagValue());
			}
		}

		doc.setTags(list);

		//Notes
		doc.setNote(TagMatchDefinitions.CUSTOMER_ID, prepareString(customerNumber));
		doc.setNote(TagMatchDefinitions.TAGMATCH_CONTRACT_NUMBER, prepareString(contractNumber));
		doc.setNote(TagMatchDefinitions.TAGMATCH_SMARTCARD_NUMBER, prepareString(smartcardNumber));
		doc.setNote(TagMatchDefinitions.EVAL_CUSTOMER_NUMBER, prepareString(customerNumber));
		doc.setNote(TagMatchDefinitions.EVAL_CONTRACT_NUMBER, prepareString(contractNumber));
		doc.setNote(TagMatchDefinitions.SEPA_MANDATE_NUMBER, prepareString(mandateNumber));

		if (mandateNumber != null && !mandateNumber.isEmpty()) {
			flowObject.put("VerifiedMandateNumber", prepareString(mandateNumber));
		}
		//String manualDoctype = (String) doc.getNote(TagMatchDefinitions.MANUAL_FORMTYPE);

	}

	private static String prepareString(String s) {
		if (s == null || s.trim().isEmpty() || s.equals("")) {
			return "";
		} else {
			return s;
		}
	}
}
