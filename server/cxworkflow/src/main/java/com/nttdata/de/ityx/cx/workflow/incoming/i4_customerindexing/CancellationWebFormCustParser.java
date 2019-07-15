package com.nttdata.de.ityx.cx.workflow.incoming.i4_customerindexing;

import com.nttdata.de.ityx.cx.workflow.WorkflowConstants;
import com.nttdata.de.ityx.cx.workflow.base.AbstractWflReportedBean;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.ityx.utils.CancellationUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.contex.impl.document.text.email.EmailDocument;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.extag.TagMatch;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CancellationWebFormCustParser extends AbstractWflReportedBean {
	
	private static Pattern CUSTOMER_PATTERN =  Pattern.compile("(?m)^\\s*Kundennummer:\\s*([\\d]{10})\\s*[\\n\\r<]");
	private static Pattern SMARTCARD_PATTERN = Pattern.compile("(?m)^\\s*Smartcard:\\s*([\\d\\w\\-\\..]+)\\s*[\\n\\r<]");
	private static Pattern EMAIL_PATTERN =     Pattern.compile("(?m)^\\s*E-Mail:\\s*([a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4})\\s*[\\n\\r<]");
	private static Pattern NAME_PATTERN =      Pattern.compile("(?m)^\\s*Name:\\s*([.\\w\\.\\-äöüÄÖÜß :;,_~\\+\\*/\\t]*)[\\n\\r<]");
	private static Pattern REASON_PATTERN =    Pattern.compile("(?m)^\\s*Kündigungsgrund:\\s*(.*)[\\n\\r<]");
	private static Pattern TEXT_PATTERN =      Pattern.compile("(?m)^\\s*Anderer Grund:\\s*([\\d\\w\\-\\..\\s]+)[\\n\\r]\\s*Vorname:\\s*");
	
	@Override
	public void execute(IFlowObject flowObject) throws Exception {
		EmailDocument doc = (EmailDocument) DocContainerUtils.getDoc(flowObject);
		String docid = DocContainerUtils.getDocID(doc);

		if (doc.getFormtype().equals(WorkflowConstants.FORMTYPE_KUENDIGUNG_AUTO)) {
			ArrayList<TagMatch> ret = new ArrayList<>();

			String body = doc.getBody();
			if (SkyLogger.getWflLogger().isDebugEnabled()){
				SkyLogger.getWflLogger().debug("CancellationWebFormCustParser:" + docid + " Body \n" + body);
			}

			Matcher customerMatcher = CUSTOMER_PATTERN.matcher(body);
			if (customerMatcher.find()) {
				String customerId = customerMatcher.group(1);
				if (customerId != null) {
					customerId=customerId.trim();
					doc.setNote(WorkflowConstants.WEBFORM_CUSTOMER_NUMBER, customerId);
					ret.add(new TagMatch(WorkflowConstants.WEBFORM_CUSTOMER_NUMBER, customerId));
				}
				SkyLogger.getWflLogger().debug("CancellationWebFormCustParser:" + docid + " customer =" + customerId);
			}else{
				SkyLogger.getWflLogger().warn("CancellationWebFormCustParser:" + docid + " customerNotFound " );
			}

			Matcher smartcardMatcher = SMARTCARD_PATTERN.matcher(body);
			if (smartcardMatcher.find()) {
				String smartcard = smartcardMatcher.group(1);
				if (smartcard != null) {
					smartcard=smartcard.trim();
					doc.setNote(WorkflowConstants.WEBFORM_SMARTCARD_NUMBER, smartcard);
					ret.add(new TagMatch(WorkflowConstants.WEBFORM_SMARTCARD_NUMBER, smartcard));
				}
				SkyLogger.getWflLogger().debug("CancellationWebFormCustParser:" + docid + " smartcard =" + smartcard);
			}

			Matcher emailMatcher = EMAIL_PATTERN.matcher(body);
			if (emailMatcher.find()) {
				String email = emailMatcher.group(1);
				if (email != null) {
					email=email.trim();
					doc.setNote(WorkflowConstants.WEBFORM_EMAIL, email);
					ret.add(new TagMatch(WorkflowConstants.WEBFORM_EMAIL, email));
				}
				SkyLogger.getWflLogger().debug("CancellationWebFormCustParser:" + docid + " email =" + email);
			}

			Matcher nameMatcher = NAME_PATTERN.matcher(body);
			if (nameMatcher.find()) {
				String name = nameMatcher.group(1);
				if (name != null) {
					name=name.trim();
					doc.setNote(WorkflowConstants.WEBFORM_NAME, name);
					ret.add(new TagMatch(WorkflowConstants.WEBFORM_NAME, name));
				}
				SkyLogger.getWflLogger().debug("CancellationWebFormCustParser:" + docid + " name =" + name);
			}

			Matcher reasonMatcher = REASON_PATTERN.matcher(body);

			String reasonCodeStr="OHNE GRUNDANGABE_K";

			if (reasonMatcher.find()) {
				reasonCodeStr = reasonMatcher.group(1);
				if (reasonCodeStr.isEmpty()) {
					reasonCodeStr="OHNE GRUNDANGABE_K";
				}else{
					reasonCodeStr=reasonCodeStr.trim();
				}
			}
			doc.setNote(TagMatchDefinitions.CANCELLATION_REASON, reasonCodeStr);
			ret.add(new TagMatch(TagMatchDefinitions.CANCELLATION_REASON, reasonCodeStr));

			String reasonCode = CancellationUtils.mapReasonCode(reasonCodeStr);
			if (reasonCode == null || reasonCode.isEmpty()) {
				reasonCode="OHNE GRUNDANGABE_K";
			}
			doc.setNote(TagMatchDefinitions.CANCELLATION_REASON_CODE, reasonCode);
			ret.add(new TagMatch(TagMatchDefinitions.CANCELLATION_REASON_CODE, reasonCode));

			SkyLogger.getWflLogger().debug("CancellationWebFormCustParser:" + docid + " reasonCode = " + reasonCode);

			SkyLogger.getWflLogger().debug("CancellationWebFormCustParser:" + docid + " reason = " + reasonCodeStr);

			doc.setNote(TagMatchDefinitions.WEBFORM_CANCELLATION, "true");

			Matcher reasonFreeTextMatcher = TEXT_PATTERN.matcher(body);
			if (reasonFreeTextMatcher.find()) {
				String reasonFreeText = reasonFreeTextMatcher.group(1).replace("\n", " ").replace("\r", "").trim();
				if (reasonFreeText!=null && !reasonFreeText.isEmpty()) {
					reasonFreeText=reasonFreeText.trim();
					doc.setNote(TagMatchDefinitions.CANCELLATION_REASON_FREE_TEXT, reasonFreeText);
					ret.add(new TagMatch(TagMatchDefinitions.CANCELLATION_REASON_FREE_TEXT, reasonFreeText));
				}
				SkyLogger.getWflLogger().debug("CancellationWebFormCustParser:" + docid + " reasonFreeText = " + reasonFreeText);
			}
			flowObject.put(WorkflowConstants.WEBFORM_CUSTOMER,ret);

			SkyLogger.getWflLogger().debug("CancellationWebFormCustParser:"+docid+" finished processing");
		} else {
			SkyLogger.getWflLogger().debug("CancellationWebFormCustParser:"+docid+"skipped formtype -> " + doc.getFormtype());
		}
	}
}
