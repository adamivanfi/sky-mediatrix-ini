package com.nttdata.de.sky.ityx.mediatrix.businessrules.server.sky;

import com.nttdata.de.ityx.sharedservices.utils.ShedulerUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.lib.utils.MitarbeiterlogWriter;
import com.nttdata.de.sky.ityx.common.DocIdGenerator;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import com.nttdata.de.sky.ityx.mediatrix.businessrules.BusinessRule;
import com.nttdata.de.sky.ityx.mediatrix.businessrules.server.parameter.UpdateContactSRParameter;
import de.ityx.contex.impl.document.*;
import de.ityx.contex.impl.document.text.email.EmailDocument;
import de.ityx.contex.interfaces.document.CDocument;
import de.ityx.contex.interfaces.extag.TagMatch;
import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.data.*;
import de.ityx.mediatrix.data.metainformation.MetaInformationDocumentContainer;
import de.ityx.mediatrix.data.metainformation.MetaInformationInt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.nttdata.de.lib.utils.MitarbeiterlogWriter.writeMitarbeiterlog;
import static com.nttdata.de.lib.utils.MitarbeiterlogWriter.writeMitarbeiterlogIfNeeded;

public class InboundRule extends SkyRule {

	private static final String CORRESPONDING_MANUAL_IDEXING_QUESTION = "Corresponding manual indexing question: ";

	public InboundRule() {
		super();
	}

	/**
	 * micolumn.3=Kundennummer micolumn.4=Dokumentenart/typ micolumn.5=SMC
	 * beiliegend micolumn.6=Empfaenger
	 *
	 * @param question
	 */
	public void initializeQuestion(Connection con, Question question, boolean refreshReferences) {

		String headers = question.getHeaders();

		String headerFormtype = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.X_TAGMATCH_FORM_TYPE);
		String contactid = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.getHeaderTagName(TagMatchDefinitions.CONTACT_ID));
		String direction = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.getHeaderTagName(TagMatchDefinitions.MX_DIRECTION));
		String docId = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.DOCUMENT_ID);

		int questionId = question.getId();
		String logPrefix = new Object() {
		}.getClass().getEnclosingMethod().getName() + " (" + Thread.currentThread().getId() + ") q:" + questionId + " d:" + docId + " ";
		SkyLogger.getMediatrixLogger().debug(logPrefix + "; q.caseId: " + question.getCaseId() +
				"; q.prjId: " + question.getProjectId() +"; q.subPrjId: " + question.getSubprojectId() +"; q.incSubPrjId: " + question.getIncomingSubproject()+
                "; q.subprjForwardId: " + question.getSubprojectForwardId() +"; q.service: " + question.isServicecenter() + "; q.html: " + question.isOriginalHTML()+
				"; q.status:"+ question.getStatus()+"; q.globalStatus:"+question.getGlobalStatus()+"; q.globalStatus:"+question.getGlobalStatus()+
				"; q.type: " + question.getType() + "; q.orig: " + question.getOrginal() 	);

		if (question.isServicecenter()) {
			return;
		}

		//String originalDocId = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.getHeaderTagName("originalDocId"));
		String uniqueDocId = null;
		String newContactid = "0";
		String subj = question.getSubject();

		boolean isMultiCopyNew = (subj.startsWith("{Multitopic-Copy}:") && (null == TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.getHeaderTagName("originalDocId"))));
		boolean isForwardedNew = (questionId == 0 && (question.getStatus().equalsIgnoreCase(Question.S_PART_ANSWERED) ||
                question.getStatus().equalsIgnoreCase(Question.S_EXTERNAL_FORWARDED) || question.getStatus().equalsIgnoreCase(Question.S_NEW)) && (null != docId && !docId.isEmpty()) && question.getOrginal() > 0);
		boolean isForwardedFirstRun = (questionId > 0 && question.getStatus().equalsIgnoreCase(Question.S_EXTERNAL_FORWARDED) && question.getOrginal() > 0 && question.getWorkingTime() == 0);
		// Execute settings for SBS Filter Process(Gregory Verbitsky)
		boolean isSBSFilterRun = headerFormtype != null && !headerFormtype.isEmpty() && question.getProjectId() == SBS_PROJECT_ID
                && question.getStatus().equalsIgnoreCase(Question.S_NEW) && question.getSubprojectForwardId() < 0;
		boolean isSBSSubprjectUpdate = headerFormtype != null && !headerFormtype.isEmpty() && question.getProjectId() == SBS_PROJECT_ID
                && question.getStatus().equalsIgnoreCase(Question.S_NEW) && question.getSubprojectForwardId() > 0
                && question.getSubprojectId() != question.getSubprojectForwardId();

		SkyLogger.getMediatrixLogger().debug(logPrefix + " contactid: " + contactid + "; subject: " + subj + "; multiNew: " + isMultiCopyNew + "; isForwardedNew: " + isForwardedNew + " isForwardedFirstRun: " + isForwardedFirstRun + ";  q.status:" + question.getStatus() + ";  q.org:" + question.getOrginal() + "; q.wt:" + question.getWorkingTime());

		if (isMultiCopyNew) {
			uniqueDocId = question.getDocId();
		}
		if (isForwardedNew) {
			TagMatchDefinitions.Channel channel = TagMatchDefinitions.getChannel(TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.CHANNEL));
			headers = TagMatchDefinitions.addOrReplaceXTMHeader(headers, TagMatchDefinitions.MX_DIRECTION, TagMatchDefinitions.Direction.OUTBOUND.toString());
			uniqueDocId = DocIdGenerator.createUniqueDocumentId(con, TagMatchDefinitions.DocumentDirection.FORWARDED, channel, docId);
			question.setDocId(uniqueDocId);
		}

		if (isMultiCopyNew || isForwardedNew) {
			SkyLogger.getMediatrixLogger().debug(logPrefix + ": Changing DocumentID: NewDoc=" + uniqueDocId + " isForwarded:" + isForwardedNew + " isMulticopy:" + isMultiCopyNew);
			headers = TagMatchDefinitions.addOrReplaceXTMHeader(headers, TagMatchDefinitions.DOCUMENT_ID, uniqueDocId);
			headers = TagMatchDefinitions.addOrReplaceXTMHeader(headers, "originalDocId", docId);
			docId = uniqueDocId;
			if (isMultiCopyNew && (contactid != null && !contactid.equalsIgnoreCase("0") && !contactid.equalsIgnoreCase(""))) {
				headers = TagMatchDefinitions.addOrReplaceXTMHeader(headers, TagMatchDefinitions.CONTACT_ID, newContactid);
			}
		}

		//Initialbefüllung der Tagmatches aus Contex-Meta Dokument
		CDocument doc = null;

		if (docId == null || docId.isEmpty() || docId.equals("0")) {
			try {
				final MetaInformationInt metaDoc = question.getMetaInformation(MetaInformationDocumentContainer.TYPENAME);

				if (metaDoc != null) {
					Object content = metaDoc.getContent();
					CDocumentContainer<CDocument> cont = (CDocumentContainer<CDocument>) content;
					List<TagMatch> ctags = cont.getPage0Tags();
					SkyLogger.getMediatrixLogger().debug(logPrefix + ": contentType=" + (content != null ? content.getClass().getName() : content) + ": tags=" + ctags);
					doc = cont.getDocument(0);

					List<TagMatch> dtags = cont.getPage0Tags();
					SkyLogger.getMediatrixLogger().debug(logPrefix + ": contentType=" + (content != null ? content.getClass().getName() : content) + ": tags=" + dtags);
					
					if (!doc.getClass().equals(EmailDocument.class)) {
						if (isMultiCopyNew || isForwardedNew) {
							SkyLogger.getMediatrixLogger().debug(logPrefix + "EMAIL: MailMultiCopyNew/Forwarded");
							doc.setNote("originalDocId", TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.getHeaderTagName("originalDocId")));
							doc.setNote(TagMatchDefinitions.DOCUMENT_ID, uniqueDocId);
						}
						if (isMultiCopyNew) {
							doc.setNote(TagMatchDefinitions.CONTACT_ID, newContactid);
						}

						List<TagMatch> tagList = doc.getTags();
						updateTagmatches(question, doc, metaDoc, cont, ctags, tagList);
					}
					String contractNumber = null;
					for (TagMatch tag : ctags) {
						String identifier = tag.getIdentifier();
						if (identifier.equals(TagMatchDefinitions.CUSTOMER_ID)) {
							String value = tag.getTagValue();
							headers = TagMatchDefinitions.addOrReplaceXTMHeader(headers, TagMatchDefinitions.CUSTOMER_ID, value);
						} else if (identifier.equals(TagMatchDefinitions.META_CUSTOMER_CATEGORY)) {
							String value = tag.getTagValue();
							headers = TagMatchDefinitions.addOrReplaceXTMHeader(headers, TagMatchDefinitions.META_CUSTOMER_CATEGORY, value);
						} else if ((isMultiCopyNew || isForwardedNew) && identifier.equals(TagMatchDefinitions.DOCUMENT_ID)) {
							SkyLogger.getMediatrixLogger().debug(logPrefix + "DocIDTagMatchFound " + uniqueDocId);
							tag.setTagValue(uniqueDocId);
						} else if ((isMultiCopyNew) && identifier.equals(TagMatchDefinitions.CONTACT_ID)) { // || isForwardedNew
							tag.setTagValue(newContactid);
						} else if (isMultiCopyNew && identifier.equals(TagMatchDefinitions.CONTACT_ID)) {
							contractNumber = tag.getTagValue();
						} else if (isForwardedNew && identifier.equals(TagMatchDefinitions.MX_DIRECTION)) {
							direction = TagMatchDefinitions.Direction.OUTBOUND.toString();
						}
					}
					String formtype = doc.getFormtype();
					if (formtype != null && !formtype.isEmpty()) {
						headerFormtype = formtype;
						headers = TagMatchDefinitions.addOrReplaceXTMHeader(headers, TagMatchDefinitions.X_TAGMATCH_FORM_TYPE, formtype);
					}
					if (isMultiCopyNew) { //|| isForwardedNew
						String customerNumber = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.X_TAGMATCH_CUSTOMER_ID);
						// SkyLogger.getMediatrixLogger().debug(logPrefix +
						// ": customer=" + customerNumber);
						String channel = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.getHeaderTagName(TagMatchDefinitions.CHANNEL));
						String doctype = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.X_TAGMATCH_FORM_TYPE);
						if (customerNumber != null && contactid != null && !contactid.isEmpty() && !contactid.equals("0")) {
							if (contractNumber == null) {
								contractNumber = "0";
							}
							SkyLogger.getMediatrixLogger().debug(logPrefix + "UpdatingSiebel SR start");
							Map<String, String> newTags = updateSiebelSR(logPrefix, new UpdateContactSRParameter(new Integer(question.getId()), customerNumber, contractNumber, channel, doctype, direction, uniqueDocId, newContactid, false), null);
							Map<String, TagMatch> oldTags = new TreeMap<>();
							for (TagMatch tm : ctags) {
								oldTags.put(tm.getIdentifier(), tm);
							}
							headers = updateHeadersAndTags(newTags, question, oldTags);
							cont.setTags(new ArrayList(oldTags.values()));
							contactid = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.getHeaderTagName(TagMatchDefinitions.CONTACT_ID));
							SkyLogger.getMediatrixLogger().debug(logPrefix + "UpdatingSiebel SR done:" + contactid);

						} else {
							cont.setTags(ctags);
						}
					}

				} else {
					SkyLogger.getMediatrixLogger().warn(logPrefix + ": MetaDoc not readable");
				}
			} catch (Exception e) {
				SkyLogger.getMediatrixLogger().error(logPrefix + ": " + e.getMessage() + e.getCause(), e);
			}
		}
		// Fix SC 462941
		boolean bodychanged = false;
		String body = question.getBody();
		if ((body == null || body.isEmpty()) && (question.getType() == Email.TYPE_DOCUMENT || question.getType() == Email.TYPE_LETTER || question.getType() == Email.TYPE_FAX)) {

			if (doc == null) {
				final MetaInformationInt metaDoc = question.getMetaInformation(MetaInformationDocumentContainer.TYPENAME);

				if (metaDoc != null) {
					CDocumentContainer<CDocument> cont = (CDocumentContainer<CDocument>) metaDoc.getContent();
					doc = cont.getDocument(0);
				}
			}
			if (doc != null) {
				String text = doc.getContentAsString();
				question.setBody(text);
				bodychanged = true;
				SkyLogger.getMediatrixLogger().debug(logPrefix + ": SetBody:" + (text != null ? text.length() : 0));
			} else {
				SkyLogger.getMediatrixLogger().warn(logPrefix + ": SetBody: could not load doc");
			}
		} else {
			SkyLogger.getMediatrixLogger().debug(logPrefix + ": qType:" + question.getType() + ":  " + (body == null || body.isEmpty()) + " doc?" + (doc != null));
		}
		if (question.getBody() == null) {
			bodychanged = true;
			question.setBody(" ");
		}
		// FIX SC:432989
		if (question.getFrom() == null || question.getFrom().isEmpty()) {
		    //Jardel Luis Roth - 20.03.2019 - Frage ID wurde für Briefe zur E-mail hinzugefügt.
			String emailBrief = question.getId() + "noReply@sky.de";
			//question.setFrom("noReply@sky.de");
			question.setFrom(emailBrief);
		}

		if (refreshReferences) {
			if (headerFormtype != null && !headerFormtype.isEmpty()) {
				setFormtypeKeyword(con, question, headerFormtype);
			} else {
				SkyLogger.getMediatrixLogger().warn(logPrefix + " emptyFORMTYPE!");
			}
			logValidationResult(con, question);
			associateCustomer(con, question, false);
			SkyLogger.getMediatrixLogger().debug(logPrefix + " refreshReferences");


		} else {
			SkyLogger.getMediatrixLogger().debug(logPrefix + " !refreshReferences:");
		}

		if (isMultiCopyNew || isForwardedFirstRun) {
			try {
				int questionid = question.getId();
				//if (questionid < 1) {
					//questionid=question.getOrginal();
				//}
				if (questionid > 1) {
					writeReportingEntry(con, questionid, docId, TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.X_TAGMATCH_CUSTOMER_ID), TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.TAGMATCH_CONTRACT_NUMBER), TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.getHeaderTagName(TagMatchDefinitions.CHANNEL)), contactid);
				}
			} catch (SQLException e) {
				SkyLogger.getMediatrixLogger().error(logPrefix + ": " + e.getMessage() + e.getCause(), e);
			}
			SkyLogger.getMediatrixLogger().debug(logPrefix + ": multicopy/forwarded:" + docId);
		}
		question.setHeaders(headers);
		setExtraColumns(question);

		if (isSBSFilterRun){
			findSubprojectIdByFilterRegEx(con, question);
			updateFilterMitarbeiterLog(con,question);
		}
		if (isSBSSubprjectUpdate){
			question.setSubprojectId(question.getSubprojectForwardId());
			question.setIncomingSubproject(question.getSubprojectForwardId());
		}

/*		if (refreshReferences) {
			try {
				SkyLogger.getCommonLogger().info("Inbound.QStore1 Generated docid:" + question.getDocId() + " frage:" + questionId+" status:"+question.getStatus());
				String statusbak=question.getStatus();
				question.setStatus(Question.S_BLOCKED);
				boolean questionstoreok = API.getServerAPI().getQuestionAPI().store(con, question);
				question.setStatus(statusbak);
				SkyLogger.getCommonLogger().info("Inbound.QStore2 Generated docid:" + question.getDocId() + " frage:" + questionId+" sok:"+questionstoreok);
			} catch (SQLException e) {
				SkyLogger.getCommonLogger().warn("Inbound.QStoreERR Generated docid:" + question.getDocId() + " frage:" + questionId + " err" + e.getMessage());
			}
			}
*/
		SkyLogger.getMediatrixLogger().debug(logPrefix + ": finished");
	}

	private void setExtraColumns(Question question) {
		String logPrefix = new Object() {
		}.getClass().getEnclosingMethod().getName() + " (" + Thread.currentThread().getId() + ") q:" + question.getId() + " d:" + question.getDocId() + " ";
		String headers = question.getHeaders();
		String headerFormtype = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.X_TAGMATCH_FORM_TYPE);
		String docId = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.DOCUMENT_ID);
		String customerNo = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.X_TAGMATCH_CUSTOMER_ID);
		String contactid = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.CONTACT_ID);
		String smcType = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.X_TAGMATCH_SMCTYPE);
		String category = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.X_TAGMATCH_CUSTOMER_CATEGORY);
		String country = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.getHeaderTagName(TagMatchDefinitions.CUSTOMER_COUNTRY));
		String toAddress = question.getTo();
		if (toAddress != null && toAddress.length() > 250) {
			toAddress = toAddress.substring(0, 250);
		}
		SkyLogger.getMediatrixLogger().debug(logPrefix + ": customerNo=" + customerNo + ": formtype =" + headerFormtype + ": barcode=" + smcType + ": category=" + category + ": country=" + country + ": toAddress=" + toAddress);

		if (customerNo != null && !customerNo.equals(question.getExtra3())) {
			question.setExtra3(customerNo);
		}
		if (headerFormtype != null && !headerFormtype.equals(question.getExtra4())) {
			question.setExtra4(headerFormtype);
		}
		if (smcType != null && !smcType.equals(question.getExtra5())) {
			question.setExtra5(smcType);
		}
		if (toAddress != null && !toAddress.equals(question.getExtra6())) {
			question.setExtra6(toAddress);
		}
		if (category != null && !category.equals(question.getExtra7())) {
			question.setExtra7(category);
		}
		if (contactid != null && !contactid.equals(question.getExtra8())) {
			question.setExtra8(contactid);
		}
		if (country != null && !country.equals(question.getExtra9())) {
			question.setExtra9(country);
		}
		if (docId != null && (question.getDocId() == null || question.getDocId().isEmpty() || !question.getDocId().equals(docId))) {
			question.setDocId(docId);
			SkyLogger.getMediatrixLogger().debug(logPrefix + ": setDocid:" + docId);
		}

		SkyLogger.getMediatrixLogger().debug(logPrefix + ": FINISH:" + question.getExtra3());
	}

	protected void logValidationResult(Connection con, Question question) {

		String logpreafix = new Object() {
		}.getClass().getEnclosingMethod().getName() + " qid:" + question.getId() + " ";

		final String headers = question.getHeaders();
/*
		String miFT = findTagMatchValueByName(question,TagMatchDefinitions.MANUAL_FORMTYPE);
        String miID = findTagMatchValueByName(question,TagMatchDefinitions.DMS_MANUAL_INDEXING_QID);
		SkyLogger.getMediatrixLogger().debug(logpreafix +TagMatchDefinitions.MANUAL_FORMTYPE+": "+miFT+"; "+TagMatchDefinitions.DMS_MANUAL_INDEXING_QID+": "+miID);

		if(miFT != null && !miFT.isEmpty() && (miID == null || miID.isEmpty() || miID.trim().equals("0"))) {
            miID = findManualIndexingQuestionId(con, question);
        }
*/
        String miID = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.getHeaderTagName(TagMatchDefinitions.DMS_MANUAL_INDEXING_QID));
        if(miID == null || miID.isEmpty() || miID.trim().equals("0")) {
            miID = findManualIndexingQuestionId(con, question);
        }
		if(miID != null && !miID.isEmpty() && !miID.trim().equals("0")) {
			try {
				MitarbeiterlogWriter.writeMitarbeiterlogIfNeeded(question,CORRESPONDING_MANUAL_IDEXING_QUESTION +miID, OperatorLogRecord.ACTION_CHECKLIST, 0);
			} catch (Exception e) {
				try{
					SkyLogger.getMediatrixLogger().warn("Retrying to write ManualIndexingQuestionTo Mitarbeiterlog:"+question.getId()+" docid:"+question.getDocId()+" e:" + e.getMessage() + e.getCause(), e);
					ShedulerUtils.resetAuth("IMLMI" + question.getId());
					writeMitarbeiterlog(question, CORRESPONDING_MANUAL_IDEXING_QUESTION +miID, OperatorLogRecord.ACTION_CHECKLIST, 0);
				}catch (Exception ee){
					SkyLogger.getMediatrixLogger().error(logpreafix +"Unable to write ManualIndexingQuestionTo Mitarbeiterlog:"+question.getId()+" docid:"+question.getDocId()+" e:" + e.getMessage() + e.getCause(), e);
				}
			}
		}
		SkyLogger.getMediatrixLogger().debug(logpreafix + CORRESPONDING_MANUAL_IDEXING_QUESTION + miID );
		
		String preFormtype = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.getHeaderTagName(TagMatchDefinitions.EVAL_FORMTYPE_BEFORE_VALIDATION));
		String postFormtype = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.getHeaderTagName(TagMatchDefinitions.EVAL_FORMTYPE));
		
		if (postFormtype==null || postFormtype.isEmpty()){
			//perf opt
			return;
		}
		//SkyLogger.getMediatrixLogger().debug("Formtype: Before Validation: " + preFormtype + ", Current: " + postFormtype);
		writeValidationLog(question, nullChecker(preFormtype), nullChecker(postFormtype), "Formtype");
		
		String postCustomerNumber = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.getHeaderTagName(TagMatchDefinitions.EVAL_CUSTOMER_NUMBER));
		String postContractNumber = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.getHeaderTagName(TagMatchDefinitions.EVAL_CONTRACT_NUMBER));
		String preCustomerNumber = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.getHeaderTagName(TagMatchDefinitions.EVAL_CUSTOMER_NUMBER_BEFORE_VALIDATION));
		String preContractNumber = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.getHeaderTagName(TagMatchDefinitions.EVAL_CONTRACT_NUMBER_BEFORE_VALIDATION));
		
		//SkyLogger.getMediatrixLogger().debug("CustomerNumber: Before Validation: " + preCustomerNumber + ", Current: " + postCustomerNumber);
		writeValidationLog(question, nullChecker(preCustomerNumber), nullChecker(postCustomerNumber), "CustomerNumber");

		//SkyLogger.getMediatrixLogger().debug("ContractNumber: Before Validation: " + preContractNumber + ", Current: " + postContractNumber);
		writeValidationLog(question, nullChecker(preContractNumber), nullChecker(postContractNumber), "ContractNumber");
	}

	private String nullChecker(String s) {
		return (s == null || s.isEmpty()) ? "0" : s;
	}

	/**
	 * Writes metadata values to the log table.
	 *
	 * @param question
	 * @param preValidation
	 * @param postValidation
	 * @param type
	 */
	protected void writeValidationLog(Question question, String preValidation, String postValidation, String type) {
		String parameter = type + ": " + preValidation + " -> " + postValidation;
		int aktion = OperatorLogRecord.ACTION_CHECKLIST;
		int operator = question.getOperatorId() > 0 ? question.getOperatorId() : 0;
		//ShedulerUtils.checkRuntimeLicense("Inbound:" + question.getDocId() + ":" + question.getId());
		try {
			writeMitarbeiterlogIfNeeded(question, parameter, aktion, operator);
		} catch (Exception e) {
			ShedulerUtils.resetAuth("IR" + question.getId());
			writeMitarbeiterlogIfNeeded(question, parameter, aktion, operator);
		}
	}

	/*public void setFormtypeKeyword(Connection con, Question question) {
		String headerFormtype = TagMatchDefinitions.extractXTMHeader(question.getHeaders(), TagMatchDefinitions.X_TAGMATCH_FORM_TYPE);
		setFormtypeKeyword(con, question, headerFormtype);

	}*/

	public void setFormtypeKeyword(Connection con, final Question question, String headerFormtype) {
		String logprefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + " q:" + question.getId() + " ";
		SkyLogger.getMediatrixLogger().debug(logprefix + "setting FORMTYPE: " + headerFormtype);
		int projectId = (question.getProjectId() > 0) ? question.getProjectId() : 110;

		if (headerFormtype == null || headerFormtype.isEmpty()) {
			SkyLogger.getMediatrixLogger().error(logprefix + " Cannot set empty Formtype:" + headerFormtype);
			return;
		}

		Keyword formtypeKeyword = null;
		try {
			formtypeKeyword = API.getServerAPI().getKeywordAPI().loadKeyword(con, BusinessRule.FORMTYPE_PREFIX + headerFormtype, projectId);
		} catch (Exception e) {
			SkyLogger.getMediatrixLogger().error(logprefix + " Cannot load Formtype-Keyword:" + headerFormtype + " e:" + e.getMessage() + e.getCause(), e);
			return;
		}

		if (formtypeKeyword == null) {
			SkyLogger.getMediatrixLogger().error(logprefix + " Cannot load Formtype-Keyword:" + headerFormtype + " - Need to be created in MX-Administration?");
			return; // wenn das neue Keyword nicht geladen werden kann, soll er nicht in die Frage gespeichert werden - sonnst npe beim speichern - viel später im code
		}

		boolean formtypeAlreadySet = false;
		List<Keyword> keywords = question.getKeywords();
		List<Keyword> newkeywords = new LinkedList<>();
		for (Keyword key : keywords) {
			if (key != null && key.getProjectId() == projectId && key.getName() != null && key.getName().startsWith(BusinessRule.FORMTYPE_PREFIX)) {
				if (key.getName().endsWith(headerFormtype)) {
					formtypeAlreadySet = true; //richtiges Keywort gesetzt
					newkeywords.add(key);
				} //else {
					//veraltete Formtypes müssen entfernt werden
					//keywords.remove(key);
			//	}
			} else {
				newkeywords.add(key);
			}
		}
		if (keywords != null && (newkeywords == null || keywords.size() != newkeywords.size())) {
			question.setKeywords(newkeywords);
		}

		SkyLogger.getMediatrixLogger().debug(logprefix + "FORMTYPEKEYWORD: " + formtypeKeyword);
		if (!formtypeAlreadySet && keywords != null) {
			keywords.add(formtypeKeyword);
			question.setKeywords(keywords);
		} else if (!formtypeAlreadySet && (keywords == null || keywords.isEmpty()) && formtypeKeyword != null) {
			if (keywords == null) {
				keywords = new LinkedList<>();
			}
			keywords.add(formtypeKeyword);
			question.setKeywords(keywords);
		} else if (formtypeAlreadySet) {
			SkyLogger.getMediatrixLogger().debug(logprefix + "Formtype: " + headerFormtype + " bereits gesetzt.");
		} else {
			// else - richtiges Keywort gesetzt oder nichts zu setzen
			SkyLogger.getMediatrixLogger().error(logprefix + "Formtype: " + headerFormtype + " need to be created in MX-Administration:" + formtypeAlreadySet + " :" + formtypeKeyword);
		}
		if (!headerFormtype.equalsIgnoreCase(TagMatchDefinitions.extractXTMHeader(question.getHeaders(), TagMatchDefinitions.X_TAGMATCH_FORM_TYPE))) {
			question.setHeaders(TagMatchDefinitions.addOrReplaceXTMHeader(question.getHeaders(), TagMatchDefinitions.X_TAGMATCH_FORM_TYPE, headerFormtype));
		}
	}

	public Question correctionOfSBSMail(Connection connection, Question question) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":  q:" + question.getId() + " q:" + question.getStatus() + " ";

		// Korrektur für Contex-Mail-Pooler used by SBS-Project
		String channel = TagMatchDefinitions.extractXTMHeader(question.getHeaders(), TagMatchDefinitions.CHANNEL);
		//if ((headers == null || headers.isEmpty() || (TagMatchDefinitions.Channel.EMAIL.toString().equals(channel) && !headers.contains("Received:")))) {
		if (TagMatchDefinitions.Channel.EMAIL.toString().equals(channel) && question.getType() != Email.TYPE_EMAIL) {
			question.setType(Email.TYPE_EMAIL);
		}
		SkyLogger.getMediatrixLogger().debug(logPrefix );

		//final MetaInformationInt metaDoc = question.getMetaInformation(MetaInformationDocumentContainer.TYPENAME);
		//if (metaDoc != null) {
		//	Object content = metaDoc.getContent();
		//	CDocumentContainer<CDocument> cont = (CDocumentContainer<CDocument>) content;

		CDocumentContainer cont = question.getDocumentContainer();

		if (cont != null) {
			SkyLogger.getMediatrixLogger().debug(logPrefix + question.getId() + " container accessible");
			for (Object doc : cont.getDocuments()) {
				SkyLogger.getMediatrixLogger().debug(logPrefix + question.getId() + " cdoc accessible");

				if (EmailDocument.class.isAssignableFrom(doc.getClass())) {
					SkyLogger.getMediatrixLogger().debug(logPrefix + question.getId() + " cdoc is Email");

					EmailDocument edoc = (EmailDocument) doc;
					question.setProjectId(SBS_PROJECT_ID );
					question.setFrom(edoc.getFrom());
					question.setTo(edoc.getTo());
					question.setCC(edoc.getCC());
					question.setSubject(edoc.getSubject());
					question.setHeaders(edoc.getHeaders());
					question.setBody(edoc.getBody());
					question.setContentType(edoc.getContenttype());
					question.setMessageId(edoc.getMessageId());
					question.setEmailDate(edoc.getCreated());

					SkyLogger.getMediatrixLogger().debug(logPrefix + "Updating attachments: " + question.getId());
					for (de.ityx.contex.impl.document.Attachment cAtt : (Vector<de.ityx.contex.impl.document.Attachment>) edoc.attachments) {
						de.ityx.mediatrix.data.Attachment att = new de.ityx.mediatrix.data.Attachment();
						att.setBuffer(cAtt.getBuffer());
						String filename = cAtt.getFilename();
						att.setFilename(filename);
						att.setClientFilename(filename);
						att.setContentDisposition(cAtt.getContentDisposition());
						att.setContentTransferEncoding(cAtt.getContentTransferEncoding());
						att.setContentType(cAtt.getContentType());
						int emailId = question.getEmailId();
						if (emailId > 0) {
							att.setEmailId(emailId);
						}
						question.addAttachment(att);
					}
				}
			}
		}
		SkyLogger.getMediatrixLogger().debug(logPrefix + " Headers: " + question.getHeaders());
		return question;

	}

	private boolean isNotEmpty(Enumeration allHeaders) {
		return allHeaders != null && allHeaders.hasMoreElements();
	}

	private boolean isNotEmpty(String[] tos) {
		return tos != null && tos.length > 0;
	}

	private boolean isNotEmpty(javax.mail.Address sender) {
		return sender != null && !sender.toString().isEmpty();
	}

	private boolean isNotEmpty(String subject) {
		return subject != null && !subject.isEmpty();
	}


	private boolean findSubprojectIdByFilterRegEx(Connection connection, Question question){
		String logPrefix = new Object() {
		}.getClass().getEnclosingMethod().getName() + " (" + Thread.currentThread().getId() + ") q.id:" + question.getId() + "; q.subproId:" + question.getSubprojectId();
		boolean found= false;
        SkyLogger.getMediatrixLogger().debug(logPrefix + " start with Subject: "+question.getSubject()+"; From:"+ question.getFrom()+"; To:"+question.getTo());
        try{
            //SkyLogger.getMediatrixLogger().debug(logPrefix + " question.headers: " + question.getHeaders());
            Project  project = API.getServerAPI().getProjectAPI().load(connection, SBS_PROJECT_ID);
            API.getServerAPI().getProjectAPI().loadFilter(connection,project);
            List<Filter> filters = project.getFilter();
            Iterator itrFilters = filters.iterator();
            while (itrFilters.hasNext() && !found){
                Filter filter = (Filter) itrFilters.next();
                SkyLogger.getMediatrixLogger().debug(logPrefix + " Rang: "+filter.getRank()+"; Prio: " + filter.getPriority()+
                        "; Name: "+filter.getName()+
                        "; Sender: "+filter.getSender()+
                        "; TargetAddress: "+filter.getTargetAddress()+
                        "; Header count: "+ (filter.getHeader() !=null ? filter.getHeader().size() : 0)+
                        "; Titel count: "+ (filter.getTitle() !=null ? filter.getTitle().size() : 0)
                        );


				if (filter.getSubprojectId()>0) {
					if (filter.getSender()!=null && filter.getSender().trim().length()>0) {
						found = question.getFrom().matches(filter.getSender());
						SkyLogger.getMediatrixLogger().debug(logPrefix + " From Term: " + filter.getSender()+"; matches: "+found);
						if (found) {
							setAndLogQuestionSubprojektId(question, filter, filter.getSender(), logPrefix);
						}
					}
					if (!found && filter.getTargetAddress()!=null && filter.getTargetAddress().trim().length()>0){
						found = question.getTo().matches(filter.getTargetAddress());
						SkyLogger.getMediatrixLogger().debug(logPrefix + " To Term: " + filter.getTargetAddress()+"; matches: "+found);
						if (found) {
							setAndLogQuestionSubprojektId(question, filter, filter.getTargetAddress(), logPrefix);
						}
					}

                    boolean foundHeader =  findByRegExList(question.getHeaders(), filter.getHeader(), filter.getHeaderConjunction());
                    boolean foundSubject = findByRegExList(question.getSubject(), filter.getTitle(), filter.getTitleConjunction());

                    boolean und = "und".equals(filter.getDetailConjunction());
                    if (und){
                        found =  foundHeader && foundSubject;
                    } else {
                        found =  foundHeader || foundSubject;
                    }
                    if (found) {
                        setAndLogQuestionSubprojektId(question, filter, filter.getTargetAddress(), logPrefix);
                    }
				}
            }
        }catch (Exception e){
            SkyLogger.getMediatrixLogger().error(logPrefix+" errMsg:" + e.getMessage(), e);
        }
        SkyLogger.getMediatrixLogger().debug(logPrefix + " finish with Subject: "+question.getSubject()+"; From:"+ question.getFrom());
		return found;
	}



	protected void updateFilterMitarbeiterLog(Connection con, Question question) {
		int subprojectid = question.getSubprojectId();
		String subproject = null;
		int operatorid = 2;
		try {
			subproject = API.getServerAPI().getSubprojectAPI().getSubprojectName(con, subprojectid);
			String parameter = "Filter: Subproject: " + subproject + " Actions: -Subproject (Direct):" + subproject + " - State: neu";
			int aktion = OperatorLogRecord.ACTION_FILTER;
			long time = System.currentTimeMillis();
			if (!updateLogDirectly(con, operatorid, question.getId(), aktion, time, parameter)){
				insertLogDirectly(con, operatorid, question.getId(),0,aktion,parameter,time);
			}
		} catch (SQLException e) {
			SkyLogger.getMediatrixLogger().error((question.getId()) + " Unable to load Subproject entry for Question! " + e.getMessage(), e);
		}
	}


	private boolean updateLogDirectly(Connection con, int operator, int questionid, int aktion, long time, String parameter)  {
		String logpreafix = new Object() {
		}.getClass().getEnclosingMethod().getName() + " q:" + questionid + " ";
		String strUpdateLogSQL = "update mitarbeiterlog set parameter=? where mitarbeiterid=? and frageid=? and aktion=? and zeit<=? and oper = 0";
		boolean ret = false;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = con.prepareStatement(strUpdateLogSQL);
			pst.setString(1, parameter);
			pst.setInt(2, operator);
			pst.setInt(3, questionid);
			pst.setInt(4, aktion);
			pst.setLong(5, time);
			rs = pst.executeQuery();
			if (!con.getAutoCommit()) {
				con.commit();
			}
			ret = true;
		} catch (Exception e) {
			SkyLogger.getMediatrixLogger().error(logpreafix + " Unable to update log entry via JDBC! " + e.getMessage(), e);
		} finally {
			try{
				if (rs != null) {
					rs.close();
				}
				if (pst != null) {
					pst.close();
				}
			} catch (Exception e) {
				SkyLogger.getMediatrixLogger().error(logpreafix + " Unable to close JDBC ResultSet or Statement!  " + e.getMessage(), e);
			}
		}
		return ret;
	}

	private boolean insertLogDirectly(Connection con, int operator, int questionid, int antwortid, int aktion, String parameter, long time){
		String logpreafix = new Object() {
		}.getClass().getEnclosingMethod().getName() + " q:" + questionid + " ";
		boolean ret = false;
		String strInsertLogSQL = "insert into MITARBEITERLOG(mitarbeiterid, frageid, antwortid, aktion, parameter, zeit, oper) values (?,?,?,?,?,?,?)";
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = con.prepareStatement(strInsertLogSQL);
			pst.setInt(1, operator);
			pst.setInt(2, questionid);
			pst.setInt(3, antwortid);
			pst.setInt(4, aktion);
			pst.setString(5, parameter);
			pst.setLong(6, time);
			pst.setInt(7, 0);
			rs = pst.executeQuery();
			if (!con.getAutoCommit()) {
				con.commit();
			}
			ret = true;
		} catch (Exception e) {
			SkyLogger.getMediatrixLogger().error(logpreafix + " Unable to insert log entry via JDBC! " + e.getMessage(), e);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pst != null) {
					pst.close();
				}
			} catch (Exception e) {
				SkyLogger.getMediatrixLogger().error(logpreafix + " Unable to close JDBC ResultSet or Statement!  " + e.getMessage(), e);
			}
		}
		return ret;
	}

	private void setAndLogQuestionSubprojektId(Question question, Filter filter, String term, String logPrefix){
		question.setSubprojectId(filter.getSubprojectId());
		question.setSubprojectForwardId(filter.getSubprojectId());
		question.setIncomingSubproject(filter.getSubprojectId());
		SkyLogger.getMediatrixLogger().debug(logPrefix + "; Filter Name: " + filter.getName() + " Term: " +
				term + "; matches: true; Set SubprojectId: " + question.getSubprojectId());
		question.setHeaders(TagMatchDefinitions.addOrReplaceXTMHeader(question.getHeaders(),
				TagMatchDefinitions.MX_TP_ID, Integer.toString(question.getSubprojectId())));

	}


    private boolean findByRegExList(String text, List<DetailsFilter> regList, String logicalOper) {
        if(regList != null && regList.size() != 0) {
            boolean und = "und".equals(logicalOper);
            Iterator itrReg = regList.iterator();

            boolean found = false;
            do {
                if(!itrReg.hasNext()) {
                    return und;
                }

                DetailsFilter reg = (DetailsFilter)itrReg.next();
                Pattern pattern = Pattern.compile(reg.getTerm());
                Matcher matcher = pattern.matcher(text);
                found = matcher.find();
                if(found && !und) {
                    return true;
                }
            } while(found || !und);

            return false;
        } else {
            return true;
        }
    }

    private String findManualIndexingQuestionId(Connection con, Question question){
        String logpreafix = new Object() {
        }.getClass().getEnclosingMethod().getName() + " q:" + question.getId() + " ";
        String result = null;
		PreparedStatement pst = null;
        Calendar calendar = Calendar.getInstance();
        if (question.getSubprojectId() == 1125 ||  question.getDocId() == null ||  question.getDocId().isEmpty() ){
            return result;
        }
		try {
            calendar.setTimeInMillis(question.getEmailDate());
            calendar.add(Calendar.DATE, -5);
			pst = con.prepareStatement("select id from frage where id != :1 and teilprojektid = 1125 and docid = :2  and EMAIL_DATE > :3 ");
			pst.setInt(1,question.getId());
			pst.setString(2,question.getDocId());
            pst.setLong(3,calendar.getTimeInMillis());
			ResultSet rs = null;
			try {
				rs = pst.executeQuery();
				while (rs.next()) {
					result = rs.getString("ID");
				}
				SkyLogger.getMediatrixLogger().debug(logpreafix+";  q.subprojectId: "+question.getSubprojectId()+"; q.docId: "+ question.getDocId()+
					"; "+TagMatchDefinitions.DMS_MANUAL_INDEXING_QID+": " + result );
			} finally {
				if (rs != null) {
					rs.close();
				}
			}
		} catch (SQLException e) {
			SkyLogger.getMediatrixLogger().error(logpreafix + " Unable to find manual indexing question ID! " + e.getMessage(), e);
		} finally {
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					SkyLogger.getMediatrixLogger().error(logpreafix + e.getMessage(), e);;
				}
			}
		}

        return result;
    }



}

