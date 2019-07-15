package com.nttdata.de.ityx.sharedservices.utils;

import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.DocIdGenerator;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.impl.document.text.email.EmailDocument;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.document.CDocument;
import de.ityx.contex.interfaces.extag.TagMatch;
import de.ityx.mediatrix.data.Question;
import de.ityx.mediatrix.data.metainformation.MetaInformationDocumentContainer;
import de.ityx.mediatrix.data.metainformation.MetaInformationInt;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by meinusch on 17.02.15.
 */
public class DocContainerUtils {
	public static final String DOC = "doc";
	
	public static CDocument getDoc(IFlowObject flow, String flowObjectname) {
		return getDoc(flow, getDocContainer(flow, flowObjectname));
	}
	
	public static CDocument getDoc(IFlowObject flow) {
		return getDoc(flow, getDocContainer(flow));
	}
	
	public static CDocument getDoc(IFlowObject flow, CDocumentContainer cont) {
		return getDoc(cont);
	}
	
	public static CDocument getDoc(CDocumentContainer cont) {
		if (cont != null) {
			for (int i = 0; i < cont.getDocuments().size(); i++) {
				if (cont.getDocument(i) != null) {
					return cont.getDocument(i);
				}
			}
		}
		return null;
	}
	
	public static CDocumentContainer<CDocument> getDocContainer(IFlowObject flow) {
		return getDocContainer(flow, DOC);
	}
	
	public static CDocumentContainer<CDocument> getDocContainer(IFlowObject flow, String flowObjectname) {
		CDocumentContainer result = null;
		Object object = flow.get(flowObjectname);
		if (object != null && object instanceof CDocumentContainer) {
			result = (CDocumentContainer) object;
		}
		return result;
	}
	
	public static String getDocID(IFlowObject flowObject) {
		String documentId = getDocID(getDoc(flowObject));
		
		if (documentId == null || documentId.isEmpty()) {
			documentId = (String) flowObject.get(TagMatchDefinitions.DOCUMENT_ID);
		}
		
		if (documentId == null || documentId.isEmpty()) {
			CDocumentContainer cont = getDocContainer(flowObject);
			if (cont != null) {
				documentId = cont.getExternalID();
				if (documentId == null || documentId.isEmpty()) {
					documentId = (String) cont.getNote(TagMatchDefinitions.DOCUMENT_ID);
				}
			}
		}
		return documentId;
	}
	
	public static String getDocID(CDocument cdoc) {
		String documentId = null;
		if (cdoc != null) {
			documentId = (String) cdoc.getNote(TagMatchDefinitions.DOCUMENT_ID);
		}
		
		return documentId;
	}
	
	public static void setDocID(IFlowObject flowObject, String ctx_documentid) {
		setDocID(getDocContainer(flowObject), getDoc(flowObject), ctx_documentid);
	}
	
	public static void setDocID(CDocumentContainer cont, String ctx_documentid) {
		CDocument cdoc = getDoc(cont);
		setDocID(cont, cdoc, ctx_documentid);
	}
	
	public static void setDocID(CDocumentContainer cont, CDocument cdoc, String ctx_documentid) {
		if (cdoc != null) {
			cdoc.setNote(TagMatchDefinitions.DOCUMENT_ID, ctx_documentid);
			if (cont != null) {
				cont.setExternalID(ctx_documentid);
				cont.setNote(TagMatchDefinitions.DOCUMENT_ID, ctx_documentid);
				SkyLogger.getWflLogger().info("SettingDocID " + ctx_documentid);
			}
		}
	}
	
	public static String getOrGenerateDocID(IFlowObject flowObject, TagMatchDefinitions.DocumentDirection direction, TagMatchDefinitions.Channel channel, java.util.Date incommingDate) {
		String ctx_documentid = getDocID(flowObject);
		if (ctx_documentid == null) {
			Connection con = null;
			try {
				con = ContexDbConnector.getAutoCommitConnection();
				ctx_documentid = DocIdGenerator.createUniqueDocumentId(con, direction, channel, incommingDate);
				setDocID(flowObject, ctx_documentid);
			} catch (SQLException e1) {
				e1.printStackTrace();
			} finally {
				if (con != null) {
					ContexDbConnector.releaseConnection(con);
				}
			}
		}
		
		CDocumentContainer cont = getDocContainer(flowObject);
		if (cont != null) {
			String documentId = cont.getExternalID();
			if (documentId == null || documentId.isEmpty() || !documentId.equalsIgnoreCase(ctx_documentid)) {
				if (ctx_documentid != null) {
					cont.setExternalID(ctx_documentid);
				}
			}
		}
		return ctx_documentid;
	}
	
	public static String getCTXDocpoolID(IFlowObject flowObject) {
		return getCTXDocpoolID(getDocContainer(flowObject));
	}
	
	public static String getCTXDocpoolID(CDocumentContainer doc) {
		return (String) doc.getMetainformation("docpoolid");
	}
	
	public static String getChannel(CDocument document) {
		return (String) document.getNote(TagMatchDefinitions.CHANNEL);
	}
	
	public static TagMatchDefinitions.Channel getChannelType(CDocument document) {
		String cnote = getChannel(document);
		
		if (cnote == null) {
			return TagMatchDefinitions.Channel.EMAIL;
		} else if (cnote.equalsIgnoreCase("BRIEF") || cnote.equalsIgnoreCase("LETTER")) {
			return TagMatchDefinitions.Channel.BRIEF;
		} else if (cnote.equalsIgnoreCase("FAX")) {
			return TagMatchDefinitions.Channel.FAX;
		} else {//if (cnote.equalsIgnoreCase("EMAIL") || cnote.equalsIgnoreCase("EMAIL")){
			return TagMatchDefinitions.Channel.EMAIL;
		}
	}
	
	public static String getFormtype(IFlowObject flowObject) {
		String formtype = getFormtype(getDoc(flowObject));
		
		if (isEmpty(formtype)) {
			formtype = (String) flowObject.get("bestcat");
		}

		/*
		if (isEmpty(formtype) && flowObject.get("cattaray") != null && !(flowObject.get("cattaray") instanceof String)) {
			String caResult = "";
			for (Category cat : (Category[]) flowObject.get("cattaray")) {
				if (isEmpty(formtype)){
					formtype=cat.getPath().replaceAll("\\/","").replaceAll("\\\\","");
				}
				caResult += cat.getPath() + ":" + cat.getRelevance() + " ";
			}
		}
		*/
		if (isEmpty(formtype)) {
			ArrayList<TagMatch> tml = (ArrayList<TagMatch>) flowObject.get("docTags");
			if (tml != null) {
				for (TagMatch tm : tml) {
					if (tm.getIdentifier().equals(TagMatchDefinitions.FORM_TYPE_CATEGORY)) {
						formtype = tm.getTagValue();
					}
				}
			}
		}
		if (isEmpty(formtype)) {
			formtype = "systemdefault";
		}
		return formtype;
	}
	
	public static String getFormtype(CDocumentContainer srcDocC) {
		return getFormtype(getDoc(srcDocC));
	}
	
	
	public static String getFormtype(CDocument srcDoc) {
		String formtype = null;
		if (srcDoc != null) {
			formtype = srcDoc.getFormtype(); // Standardfall
			//Fallbacks
			if (!isEmpty(formtype)) {
				return formtype;
			} else {
				formtype = (String) srcDoc.getNote(TagMatchDefinitions.FORM_TYPE_CATEGORY); //Formtype
			}
			if (isEmpty(formtype)) {
				formtype = (String) srcDoc.getNote(TagMatchDefinitions.DOCUMENT_TYPE); //DocumentType
			}
			if (isEmpty(formtype)) {
				formtype = (String) srcDoc.getNote(TagMatchDefinitions.EVAL_FORMTYPE); //EvalFormtypeAuto
			}
			if (!isEmpty(formtype)) {
				return formtype;
			} else {
				formtype = getDocTagValue(srcDoc, TagMatchDefinitions.FORM_TYPE_CATEGORY); //Formtype
			}
			if (isEmpty(formtype)) {
				formtype = getDocTagValue(srcDoc, TagMatchDefinitions.DOCUMENT_TYPE); //DocumentType
			}
			if (isEmpty(formtype)) {
				formtype = getDocTagValue(srcDoc, TagMatchDefinitions.EVAL_FORMTYPE); //EvalFormtypeAuto
			}
		}
		if (isEmpty(formtype)) {
			formtype = "systemdefault";
		}
		return formtype;
	}
	
	public static void setExtFormtype(IFlowObject flowObject, CDocumentContainer srcCont, CDocument srcDoc, String formtype) {
		flowObject.put(TagMatchDefinitions.FORM_TYPE_CATEGORY, formtype);
		flowObject.put(TagMatchDefinitions.EVAL_FORMTYPE, formtype);
		flowObject.put(TagMatchDefinitions.EVAL_FORMTYPE_BEFORE_VALIDATION, formtype);
		flowObject.put("bestcat", formtype);
		setExtFormtype(srcCont, srcDoc, formtype);
	}
	
	public static void setFormtype(IFlowObject flowObject, CDocumentContainer srcCont, CDocument srcDoc, String formtype) {
		flowObject.put(TagMatchDefinitions.FORM_TYPE_CATEGORY, formtype);
		flowObject.put("bestcat", formtype);
		setFormtype(srcCont, srcDoc, formtype);
	}
	
	public static void setExtFormtype(CDocumentContainer srcCont, CDocument srcDoc, String formtype) {
		List<TagMatch> contTml = srcCont.getTags();
		boolean replaced = false;
		
		for (TagMatch tm : contTml) {
			if (tm.getIdentifier().equals("formtype")) {
				contTml.remove(tm);
			}
		}
		
		for (TagMatch tm : contTml) {
			if (tm.getIdentifier().equals(TagMatchDefinitions.FORM_TYPE_CATEGORY)) {
				tm.setTagValue(formtype);
				replaced = true;
			}
		}
		if (!replaced) {
			contTml.add(new TagMatch(TagMatchDefinitions.FORM_TYPE_CATEGORY, formtype));
		}
		
		for (TagMatch tm : contTml) {
			if (tm.getIdentifier().equals(TagMatchDefinitions.EVAL_FORMTYPE)) {
				tm.setTagValue(formtype);
				replaced = true;
			}
		}
		if (!replaced) {
			contTml.add(new TagMatch(TagMatchDefinitions.EVAL_FORMTYPE, formtype));
		}
		for (TagMatch tm : contTml) {
			if (tm.getIdentifier().equals(TagMatchDefinitions.EVAL_FORMTYPE_BEFORE_VALIDATION)) {
				tm.setTagValue(formtype);
				replaced = true;
			}
		}
		if (!replaced) {
			contTml.add(new TagMatch(TagMatchDefinitions.EVAL_FORMTYPE_BEFORE_VALIDATION, formtype));
		}
		
		
		//srcCont.setTags(contTml);
		if (srcCont.getNotes() != null) {
			srcCont.getNotes().remove("formtype");
		}
		List<TagMatch> docTml = srcDoc.getTags();
		for (TagMatch tm : docTml) {
			if (tm.getIdentifier().equals("formtype")) {
				docTml.remove(tm);
			}
		}
		//srcDoc.setTags(docTml);
		srcDoc.getNotes().remove("formtype");
		
		srcDoc.setFormtype(formtype);
		srcDoc.setNote(TagMatchDefinitions.FORM_TYPE_CATEGORY, formtype);
		srcDoc.setNote(TagMatchDefinitions.EVAL_FORMTYPE, formtype);
		srcDoc.setNote(TagMatchDefinitions.DOCUMENT_TYPE, formtype);
	}
	
	public static void setFormtype(CDocumentContainer srcCont, CDocument srcDoc, String formtype) {
		List<TagMatch> contTml = srcCont.getTags();
		boolean replaced = false;
		
		for (TagMatch tm : contTml) {
			if (tm.getIdentifier().equals("formtype")) {
				contTml.remove(tm);
			}
		}
		
		for (TagMatch tm : contTml) {
			if (tm.getIdentifier().equals(TagMatchDefinitions.FORM_TYPE_CATEGORY)) {
				tm.setTagValue(formtype);
				replaced = true;
			}
		}
		if (!replaced) {
			contTml.add(new TagMatch(TagMatchDefinitions.FORM_TYPE_CATEGORY, formtype));
		}
		//srcCont.setTags(contTml);
		if (srcCont.getNotes() != null) {
			srcCont.getNotes().remove("formtype");
		}
		List<TagMatch> docTml = srcDoc.getTags();
		for (TagMatch tm : docTml) {
			if (tm.getIdentifier().equals("formtype")) {
				docTml.remove(tm);
			}
		}
		//srcDoc.setTags(docTml);
		srcDoc.getNotes().remove("formtype");
		
		srcDoc.setFormtype(formtype);
		srcDoc.setNote(TagMatchDefinitions.FORM_TYPE_CATEGORY, formtype);
		srcDoc.setNote(TagMatchDefinitions.EVAL_FORMTYPE, formtype);
		srcDoc.setNote(TagMatchDefinitions.DOCUMENT_TYPE, formtype);
	}
	
	public static String getDocTagValue(CDocument document, String tagIdentifier) {
		for (TagMatch tm : document.getTags()) {
			if (tm.getIdentifier().equals(tagIdentifier)) {
				return tm.getTagValue();
			}
		}
		return null;
	}
	
	public static CDocument getPageDocument(int splittedcount, CDocument cdoc) throws CloneNotSupportedException {
		List<Integer> pages = new ArrayList<>(1);
		pages.add(splittedcount);
		CDocument newdoc = cdoc.clone().cutDocument(new ArrayList<Integer>(1), pages);
		copyMetadata(cdoc, newdoc);
		return newdoc;
	}
	
	
	private static void copyMetadata(CDocument sourceDoc, CDocument destinationDoc) {
		if (sourceDoc.headers() != null) {
			List l = new LinkedList<>();
			Iterator i = sourceDoc.headers();
			while (i.hasNext()) {
				l.add(i.next());
			}
			destinationDoc.setHeaders(l);
		}
		if (sourceDoc.getTitle() != null) {
			destinationDoc.setTitle(sourceDoc.getTitle());
		} else if (sourceDoc.getClass().equals(EmailDocument.class)) {
			EmailDocument edoc = ((EmailDocument) sourceDoc);
			String subject = edoc.getSubject();
			
			if (subject != null) {
				destinationDoc.setTitle(subject);
			}
		}
		if (sourceDoc.getAnnotations() != null) {
			destinationDoc.setAnnotations(sourceDoc.getAnnotations());
		}
		if (sourceDoc.getTags() != null) {
			destinationDoc.getTags().clear();
			destinationDoc.setTags(sourceDoc.getTags());
		}
		
		for (Map.Entry<String, Object> note : sourceDoc.getNotes().entrySet()) {
			destinationDoc.setNote(note.getKey(), note.getValue());
		}
		if (sourceDoc.getFormtype() != null) {
			destinationDoc.setFormtype(sourceDoc.getFormtype());
		}
	}
	
	public static boolean isEmpty(String s) {
		return (s == null || s.isEmpty() || s.trim().equals("") || s.trim().equals("0") || s.trim().equals("unclassified") || s.trim().equals("systemdefault"));
	}
	
	public static void mergeTagsAfterVcat(IFlowObject flowObject, CDocument document) {
		String docid = getDocID(document);
		List<TagMatch> backupTags = (List<TagMatch>) flowObject.get(BACKUP_TAGS);
		List<TagMatch> cuttentDocTags = document.getTags();
		
		if (backupTags != null) {
			//document.setTags(backupTags);
			for (TagMatch ctm : cuttentDocTags) {
				String ctmkey = ctm.getIdentifier();
				boolean itemFoundInBackupList = false;
				
				for (TagMatch btm : backupTags) {
					if (btm.getIdentifier().equalsIgnoreCase(ctmkey)) {
						itemFoundInBackupList = true;
						if (ctm.getTagValue() != null && !ctm.getTagValue().isEmpty()) {
							//übernehme Werte aus neuemTag
							SkyLogger.getWflLogger().debug("MergingTags:" + docid + " replace:" + btm.getIdentifier() + ":" + btm.getTagValue() + "->" + ctm.getTagValue());
							btm.setTagValue(ctm.getTagValue());
						} else {
							//neuerTag empty -> nicht übernemhen
							SkyLogger.getWflLogger().debug("MergingTags:" + docid + " newTagWithEmptyValue:" + btm.getIdentifier() + ":" + btm.getTagValue() + "->" + ctm.getTagValue());
						}
					}
				}
				if (!itemFoundInBackupList) {
					//identifier from new Tag not Found in oldList, add Tag
					backupTags.add(ctm);
					SkyLogger.getWflLogger().debug("MergingTags:" + docid + " add:" + ctm.getIdentifier() + ":" + ctm.getTagValue());
				}
			}
			document.setTags(backupTags);
		} else {
			SkyLogger.getWflLogger().info("MergingTags:" + docid + " BackupTags are Empty");
		}
		
	}
	
	public static void saveTagsBeforeVcat(IFlowObject flowObject, CDocument document) {
		List<TagMatch> tags = document.getTags();
		if (tags != null) {
			flowObject.put(DocContainerUtils.BACKUP_TAGS, tags);
		}
	}
	
	public static final String BACKUP_TAGS = "backupTags";
	
	public static Map<String, Object> setIncommingDate(CDocumentContainer con, Date incommingdateD) {
		return setIncommingDate(con, getDoc(con), incommingdateD, incommingdateD);
	}
	
	public static Map<String, Object> setIncommingDate(CDocumentContainer con, CDocument cdoc, Date incommingdateD, Date createdateD) {
		if (createdateD == null) {
			createdateD = incommingdateD;
		}
		String incommingdate = (new SimpleDateFormat("yyyyMMddHHmmss")).format(incommingdateD);
		String createdate = (new SimpleDateFormat("yyyyMMddHHmmss")).format(createdateD);
		con.setNote(TagMatchDefinitions.CREATEDATE, createdate);
		con.setNote(TagMatchDefinitions.INCOMINGDATE, incommingdate);
		con.setNote(TagMatchDefinitions.INCOMINGTIMESTAMP, Long.toString(incommingdateD.getTime()));
		cdoc.setNote(TagMatchDefinitions.CREATEDATE, createdate);
		cdoc.setNote(TagMatchDefinitions.INCOMINGDATE, incommingdate);
		cdoc.setNote(TagMatchDefinitions.INCOMINGTIMESTAMP, Long.toString(incommingdateD.getTime()));
		
		Map<String, Object> map = new HashMap<>();
		map.put(TagMatchDefinitions.CREATEDATE, createdate);
		map.put(TagMatchDefinitions.INCOMINGDATE, incommingdate);
		return map;
	}
	
	public static Date getCreationDate(CDocumentContainer con) {
		String logPrefix = "DocContainerUtils.getCreationDateC #" + new Object() {
		}.getClass().getEnclosingMethod().getName();
		if (con == null) {
			SkyLogger.getCommonLogger().error(logPrefix + " Not able to access container.");
			return null;
		}
		Date creationDateD = null;
		String creationdate = (String) con.getNote(TagMatchDefinitions.CREATEDATE);
		if (creationdate != null && !creationdate.isEmpty()) {
			try {
				creationDateD = (new SimpleDateFormat("yyyyMMddHHmmss")).parse(creationdate);
			} catch (ParseException e) {
				SkyLogger.getCommonLogger().error(logPrefix + " Not able to parse:" + creationdate + " to Format: yyyyMMddHHmmss");
			}
		}
		if (creationDateD == null) {
			SkyLogger.getCommonLogger().info(logPrefix + " Not able to extract creationDate from container:" + getDocID(getDoc(con)));
			creationDateD = getCreationDate(getDoc(con));
		}
		return creationDateD;
	}
	
	
	public static Date getIncommingDate(CDocumentContainer con) {
		String logPrefix = "DocContainerUtils.getIncommingDateC #" + new Object() {
		}.getClass().getEnclosingMethod().getName();
		
		if (con == null) {
			SkyLogger.getCommonLogger().error(logPrefix + " Not able to access container.");
			return null;
		}
		
		Date incommingDateD = null;
		String incommingdate = (String) con.getNote(TagMatchDefinitions.INCOMINGDATE);
		if (incommingdate != null && !incommingdate.isEmpty()) {
			try {
				incommingDateD = (new SimpleDateFormat("yyyyMMddHHmmss")).parse(incommingdate);
			} catch (ParseException e) {
				SkyLogger.getCommonLogger().error(logPrefix + " Not able to parse:" + incommingdate + " to Format: yyyyMMddHHmmss");
			}
		}
		
		if (incommingDateD == null) {
			incommingdate = (String) con.getNote(TagMatchDefinitions.CREATEDATE);
			if (incommingdate != null && !incommingdate.isEmpty()) {
				try {
					incommingDateD = (new SimpleDateFormat("yyyyMMddHHmmss")).parse(incommingdate);
				} catch (ParseException e) {
					SkyLogger.getCommonLogger().error(logPrefix + " Not able to parse:" + incommingdate + " to Format: yyyyMMddHHmmss");
				}
			}
			
		}
		if (incommingDateD == null) {
			SkyLogger.getCommonLogger().info(logPrefix + " Not able to extract incommingdate from container:" + getDocID(getDoc(con)));
			incommingDateD = getIncommingDate(getDoc(con));
		}
		/*if (SkyLogger.getCommonLogger().isDebugEnabled()) {
			SkyLogger.getCommonLogger().debug(logPrefix + getDocID(getDoc(con)) + " extracted IncommingDate:" + (new SimpleDateFormat("yyyyMMddHHmmss")).format(incommingDateD));
		}*/
		return incommingDateD;
	}
	
	public static Date getCreationDate(Question question) {
		
		String createDateS = TagMatchDefinitions.extractXTMHeader(question.getHeaders(), TagMatchDefinitions.CREATEDATE);
		if (createDateS != null && !createDateS.isEmpty()) {
			try {
				DateFormat df = (new SimpleDateFormat("yyyyMMddHHmmss"));
				Date parsedDate;
				parsedDate = df.parse(createDateS);
				return new Date(parsedDate.getTime());
			} catch (ParseException ex) {
				SkyLogger.getCommonLogger().info("TAGMATCH:INCOMINGDATE not set correctly for question:" + question.getId() + " docid:" + question.getDocId(), ex);
			}
		}
		final MetaInformationInt metaDoc = question.getMetaInformation(MetaInformationDocumentContainer.TYPENAME);
		if (metaDoc != null) {
			Object content = metaDoc.getContent();
			CDocumentContainer<CDocument> cont = (CDocumentContainer<CDocument>) content;
			return getCreationDate(cont);
		}
		return null;
	}
	
	public static Date getIncommingDate(Question question) {
		
		String cancelDateS = TagMatchDefinitions.extractXTMHeader(question.getHeaders(), TagMatchDefinitions.INCOMINGDATE);
		if (cancelDateS != null && !cancelDateS.isEmpty()) {
			try {
				DateFormat df = (new SimpleDateFormat("yyyyMMddHHmmss"));
				Date parsedDate;
				parsedDate = df.parse(cancelDateS);
				return new java.sql.Date(parsedDate.getTime());
			} catch (ParseException ex) {
				SkyLogger.getCommonLogger().info("TAGMATCH:INCOMINGDATE not set correctly for question:" + question.getId() + " docid:" + question.getDocId(), ex);
			}
		}
		final MetaInformationInt metaDoc = question.getMetaInformation(MetaInformationDocumentContainer.TYPENAME);
		if (metaDoc != null) {
			Object content = metaDoc.getContent();
			CDocumentContainer<CDocument> cont = (CDocumentContainer<CDocument>) content;
			return getIncommingDate(cont);
		}
		return null;
	}
	
	public static Date getIncommingDate(CDocument doc) {
		String logPrefix = "DocContainerUtils.getIncommingDateD #" + new Object() {
		}.getClass().getEnclosingMethod().getName();
		
		if (doc == null) {
			SkyLogger.getCommonLogger().error(logPrefix + " Not able to access container.");
			return null;
		}
		
		Date incommingDateD = null;
		String incommingdate = (String) doc.getNote(TagMatchDefinitions.INCOMINGDATE);
		if (incommingdate != null && !incommingdate.isEmpty()) {
			try {
				incommingDateD = (new SimpleDateFormat("yyyyMMddHHmmss")).parse(incommingdate);
			} catch (ParseException e) {
				SkyLogger.getCommonLogger().error(logPrefix + " Not able to parse:" + incommingdate + " to Format: yyyyMMddHHmmss");
			}
		}
		
		if (incommingDateD == null) {
			incommingdate = (String) doc.getNote(TagMatchDefinitions.CREATEDATE);
			if (incommingdate != null && !incommingdate.isEmpty()) {
				try {
					incommingDateD = (new SimpleDateFormat("yyyyMMddHHmmss")).parse(incommingdate);
				} catch (ParseException e) {
					SkyLogger.getCommonLogger().error(logPrefix + " Not able to parse:" + incommingdate + " to Format: yyyyMMddHHmmss");
				}
			}
		}
		
		if (incommingDateD == null) {
			String docid = getDocID(doc);
			SkyLogger.getCommonLogger().info(logPrefix + " Not able to extract incommingdate from docNote, trying using DocID:" + docid);
			if (docid != null && !docid.isEmpty()) {
				String docidDate = docid.substring(4, 18);
				try {
					incommingDateD = new SimpleDateFormat("yyyyMMddHHmmss").parse(docidDate);
				} catch (Exception e) {
					SkyLogger.getCommonLogger().error(logPrefix + " Not able to extract incommingdate from doc");
				}
			}
		}
		
		if (incommingDateD == null) {
			return new Date();
		}
		return incommingDateD;
	}
	
	
	public static Date getCreationDate(CDocument doc) {
		String logPrefix = "DocContainerUtils.getCreationDate #" + new Object() {
		}.getClass().getEnclosingMethod().getName();
		
		if (doc == null) {
			SkyLogger.getCommonLogger().error(logPrefix + " Not able to access container.");
			return null;
		}
		
		Date creationDateD = null;
		String creationDate = (String) doc.getNote(TagMatchDefinitions.CREATEDATE);
		if (creationDate != null && !creationDate.isEmpty()) {
			try {
				creationDateD = (new SimpleDateFormat("yyyyMMddHHmmss")).parse(creationDate);
			} catch (ParseException e) {
				SkyLogger.getCommonLogger().error(logPrefix + " Not able to parse:" + creationDate + " to Format: yyyyMMddHHmmss");
			}
		}
		
		if (creationDateD == null) {
			return new Date();
		}
		return creationDateD;
	}

	public static long getMtxEmailId(CDocument doc) {
		String logPrefix = "DocContainerUtils.getMtxEmailId #" + new Object() {
		}.getClass().getEnclosingMethod().getName();
		if (doc == null) {
			SkyLogger.getCommonLogger().error(logPrefix + " Not able to access container.");
			return 0L;
		}
		long result = 0L;

		String strMtxEmailId ="";
		try {

			strMtxEmailId = "" + doc.getNote(TagMatchDefinitions.MTX_EMAIL_ID);
			SkyLogger.getCommonLogger().debug(logPrefix + " EmailId " + strMtxEmailId);

			//strMtxEmailId = (String) doc.getNote(TagMatchDefinitions.MTX_EMAIL_ID);
			if (strMtxEmailId != null && !strMtxEmailId.isEmpty() && !strMtxEmailId.equals("null")) {
				try {
					result = Long.parseLong(strMtxEmailId);
				} catch (NumberFormatException nfe) {
					SkyLogger.getCommonLogger().error(logPrefix + " Not able to parse "+TagMatchDefinitions.MTX_EMAIL_ID +": "+strMtxEmailId + " to Number Format",nfe);
				}
			}
		} catch (NumberFormatException nfe) {
			SkyLogger.getCommonLogger().error(logPrefix + " Not able to parse "+TagMatchDefinitions.MTX_EMAIL_ID +": "+strMtxEmailId + " to Number Format",nfe);
		}
		//String strMtxEmailId = (String) doc.getNote(TagMatchDefinitions.MTX_EMAIL_ID);
		//if (strMtxEmailId != null && !strMtxEmailId.isEmpty()) {
		//	try {
		//		result = Long.parseLong(strMtxEmailId);
		//	} catch (NumberFormatException nfe) {
		//		SkyLogger.getCommonLogger().error(logPrefix + " Not able to parse "+TagMatchDefinitions.MTX_EMAIL_ID +": "+strMtxEmailId + " to Number Format",nfe);
		//	}
		//}
		return result;
	}


	public static String getMessageId(EmailDocument edoc) {
		String logPrefix = "DocContainerUtils.getMessageId #" + new Object() {
		}.getClass().getEnclosingMethod().getName();
        String result = null;
		if (edoc == null) {
			SkyLogger.getCommonLogger().error(logPrefix + " Not able to access container.");
			return null;
		}
		result = TagMatchDefinitions.extractOrgHeader(edoc.getHeaders(), "Message-ID");
        if (result != null ){
            result = result.trim();
        }
		return result;
	}

}
