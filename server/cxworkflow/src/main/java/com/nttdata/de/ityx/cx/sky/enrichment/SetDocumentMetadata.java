/**
 *
 */
package com.nttdata.de.ityx.cx.sky.enrichment;

import com.nttdata.de.ityx.cx.sky.routing.SetInternalRoutingBean;
import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;
import com.nttdata.de.ityx.cx.workflow.utils.WorkflowTextExtractionUtils;
import com.nttdata.de.ityx.sharedservices.utils.ContexDbConnector;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.connector.ISiebel;
import com.nttdata.de.sky.ityx.common.SMCTypes;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.base.Global;
import de.ityx.contex.data.icat.Category;
import de.ityx.contex.impl.designer.state.KeyConfiguration;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.impl.document.text.email.EmailDocument;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.document.CDocument;
import de.ityx.contex.interfaces.extag.TagMatch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Pattern;

//import de.ityx.contex.data.icat.Category;

/**
 * This bean sets some document metadata for the transition to mediatrix.
 *
 * @author DHIFLM
 */
public class SetDocumentMetadata extends AbstractWflBean {

	private static final long serialVersionUID = -3884386288038054529L;

	public static final String FAX = "FAX";
	public static final String RECEIVING_FAX = "ReceivingFax";
	public static final String EVAL_CONTRACT_STATUS = "EvalContractStatus";
	public static final String SMC_REGEX = "\\d{13}(\\d{2})\\d";
	private static final Pattern SMC_PATTERN = Pattern.compile(SMC_REGEX);
	private static final String DOCUMENT = "document";
	private static final String MXDOCTYPE = "mxdoctype";
	private static final String DOC_TAGS = "docTags";
	public static final String BARCODE_EZM = "Einzugsermaechtigung Sky for SEPA";
	public static final Pattern BARCODE_EZM_Pattern = Pattern.compile(BARCODE_EZM);
	public static final String BARCODE_LAGER = "0000000080001080";
	private static final List<String> DDA_DATA = Arrays.asList(TagMatchDefinitions.DOCUMENT_ID, TagMatchDefinitions.EVAL_CUSTOMER_NUMBER, TagMatchDefinitions.EVAL_CONTRACT_NUMBER, TagMatchDefinitions.CONTACT_ID, EVAL_CONTRACT_STATUS);
	private static final List<Pattern> ARCHIVE_BARCODES = Arrays.asList(BARCODE_EZM_Pattern);
	private static final List<String> DIRECTARCHIVE = Arrays.asList("fh_vertraege", "fh_vertrag", "directdebitauth");
	private static final List<String> SMCARCHIVE = Arrays.asList("geraeteversand", "geraeteabwicklung");
	private static final String INSERT_EZM_STMT = "insert into NTT_PRESEPA_DDA (INCOMING_TIMESTAMP,DOCUMENTID,CUSTOMERID,CONTRACTID,DOCTYPE,SIEBEL_CONTACTID) values (?,?,?,?,?,?)";
	private static final String INSERT_LAGER_STMT = "insert into NTT_LAGER3000_DDA (INCOMING_TIMESTAMP,DOCUMENTID,CUSTOMERID,CONTRACTID,STATUS_RESULT,SIEBEL_CONTACTID) values (?,?,?,?,?,?)";

	public void execute(IFlowObject flow) throws Exception {
		// if doc.doctype is 3 (fax) set doctype for mxstate to "fax" otherwise
		// use "document"
		// emails are handled in another tree of this process.
		// flowObject.put("mxdoctype",
		// ((Integer)flowObject.get("doc.doctype")).intValue() == 3 ? "fax" :
		// "document");
		// do not use fax channel in mediatrix
		CDocumentContainer<CDocument> container = DocContainerUtils.getDocContainer(flow);
		CDocument document = DocContainerUtils.getDoc(container);

		if (Global.getOperatorLogLength() < 20) {
			Global.setOperatorLogLength(2040);
		}

		if ((Integer) flow.get(DocContainerUtils.DOC + ".doctype") == 2) {
			flow.put(MXDOCTYPE, "email");
			/*if (EmailDocument.class.isAssignableFrom(document.getClass())) {
				EmailDocument edoc = (EmailDocument) document;
				SkyLogger.getWflLogger().debug("SetDocumentMetadata.headers" + edoc.getHeaders());
				SkyLogger.getWflLogger().debug("SetDocumentMetadata.body" +edoc.getBody());
				SkyLogger.getWflLogger().debug("SetDocumentMetadata.abody" +edoc.getAlternativeBody());
				//flow.put("emailbody",document.getContentAsString());
			}*/
		} else {
			flow.put(MXDOCTYPE, DOCUMENT);
		}
		String documentId = DocContainerUtils.getDocID(flow);

		String logPrefix = new Object() {
		}.getClass().getEnclosingMethod().getName() + " docid:" + documentId + " ";
		SkyLogger.getWflLogger().info(logPrefix + "SetDocumentMetadata.execute()");

		List<TagMatch> inputTags = container.getTags();
		Map<String, String> tagMap = convertToTagMap(inputTags);

		//Timestamps
		Date incommingDate = DocContainerUtils.getIncommingDate(container);

		tagMap = copyNoteAsStringToTagMap(TagMatchDefinitions.EVAL_FORMTYPE_BEFORE_VALIDATION, tagMap, document);
		tagMap = copyNoteAsStringToTagMap(TagMatchDefinitions.EVAL_FORMTYPE, tagMap, document);
		tagMap = copyNoteAsStringToTagMap(TagMatchDefinitions.FORMTYPERELIABILITY, tagMap, document);
		tagMap = copyNoteAsStringToTagMap(TagMatchDefinitions.EVAL_CUSTOMER_NUMBER_BEFORE_VALIDATION, tagMap, document);
		tagMap = copyNoteAsStringToTagMap(TagMatchDefinitions.EVAL_CUSTOMER_NUMBER, tagMap, document);
		tagMap = copyNoteAsStringToTagMap(TagMatchDefinitions.EVAL_CONTRACT_NUMBER_BEFORE_VALIDATION, tagMap, document);
		tagMap = copyNoteAsStringToTagMap(TagMatchDefinitions.EVAL_CONTRACT_NUMBER, tagMap, document);
		tagMap = copyNoteAsStringToTagMap(TagMatchDefinitions.CONTACT_ID, tagMap, document);
		tagMap = copyNoteAsStringToTagMap(SetInternalRoutingBean.CONTEX_INTERNAL_ROUTING, tagMap, document);
		tagMap = copyNoteAsStringToTagMap(SetInternalRoutingBean.ROUTING_SPEZIELL, tagMap, document);
		tagMap = copyNoteAsStringToTagMap(TagMatchDefinitions.DOCUMENT_ID, tagMap, document);
		tagMap = copyNoteAsStringToTagMap(TagMatchDefinitions.DMS_MANUAL_INDEXING_QID, tagMap, document);
		tagMap.put(TagMatchDefinitions.DOCUMENT_ID, documentId);
		tagMap = copyNoteAsStringToTagMap("docpoolid", "ctx_docpoolid", tagMap, document);
		tagMap = copyNoteAsStringToTagMap(EVAL_CONTRACT_STATUS, tagMap, document);
		tagMap = copyNoteAsStringToTagMap(TagMatchDefinitions.INCOMINGDATE, tagMap, document);
		tagMap = copyNoteAsStringToTagMap(TagMatchDefinitions.CREATEDATE, tagMap, document);

		SMCTypes smcType = null;
		for (String smcCandidate : WorkflowTextExtractionUtils.getBarcodesMatchesPattern(document, SMC_PATTERN)) {
			if (smcCandidate != null && !smcCandidate.isEmpty()) {
				try {
					int smcInt = Integer.parseInt(smcCandidate);

					if (smcInt <= SMCTypes.max()) {
						smcType = SMCTypes.values()[smcInt];
						SkyLogger.getWflLogger().debug(logPrefix + "SMCBarcode smctype:" + smcType + ":" + smcType.name() + " parsedvalue:" + smcInt);
					} else {
						SkyLogger.getWflLogger().debug(logPrefix + "SMCBarcode not valid parsedvalue:" + smcInt);
					}
				} catch (Exception e) {
					SkyLogger.getWflLogger().debug(logPrefix + "SMCBarcode is not SMC:" + smcCandidate);
				}
			}
		}
		tagMap.put(TagMatchDefinitions.X_TAGMATCHFIELD_SMCTYPE, smcType != null ? smcType.toString() : "Unbekannt");

		String formtype = DocContainerUtils.getFormtype(document);
		tagMap.put(TagMatchDefinitions.FORM_TYPE_CATEGORY, formtype);

		String catTag = "cats";
		if (document.getNote(catTag) != null && !(document.getNote(catTag) instanceof String)) {
			try {
				Category[] cats = (Category[]) document.getNote(catTag);

				if (cats != null) {
					String catString = "";
					for (Category cat : cats) {
						catString += cat.getPath() + ":" + cat.getRelevance() + ",";
					}
					tagMap.put("Categories", catString);
				}
			} catch (Exception e) {
				try {
					de.ityx.lingua.categorizer.Category[] cats = (de.ityx.lingua.categorizer.Category[]) document.getNote(catTag);
					if (cats != null) {
						String catString = "";
						for (de.ityx.lingua.categorizer.Category cat : cats) {
							catString += cat.getName() + ":" + cat.getProbability() + ",";
						}
						tagMap.put("Categories", catString);
					}
				} catch (Exception ee) {
					SkyLogger.getWflLogger().warn(logPrefix + "not able to read of categories 1 " + e.getMessage(), e);
					SkyLogger.getWflLogger().warn(logPrefix + "not able to read of categories 2 " + ee.getMessage(), ee);
				}
			}
		}
		String channel = DocContainerUtils.getChannel(document);
		if (channel != null) {
			tagMap.put(TagMatchDefinitions.CHANNEL, channel);
			if (channel.equals(FAX)) {
				tagMap = copyNoteAsStringToTagMap(RECEIVING_FAX, tagMap, document);
			}
		} else if (document.getClass().equals(EmailDocument.class)) {
			tagMap.put(TagMatchDefinitions.CHANNEL, ISiebel.Channel.EMAIL.toString());
			document.setNote(TagMatchDefinitions.CHANNEL, ISiebel.Channel.EMAIL.toString());
		} else {
			tagMap.put(TagMatchDefinitions.CHANNEL, "notRecognized");
			document.setNote(TagMatchDefinitions.CHANNEL, "notRecognized");
		}

		// Triggers automatic archiving where feasible.
		Boolean archiveFlag = Boolean.FALSE;
		if (formtype != null && !formtype.isEmpty()) {
			String lc_formtype = formtype.toLowerCase();
			String customerId = tagMap.get(TagMatchDefinitions.EVAL_CUSTOMER_NUMBER);
			if ((DIRECTARCHIVE.contains(lc_formtype) && !(customerId == null || customerId.trim().isEmpty() || customerId.equals("0"))) || (SMCARCHIVE.contains(lc_formtype) && SMCTypes.SMCTYPE_01_MIT_SMARTCARD.equals(smcType))) {
				archiveFlag = Boolean.TRUE;
			} else if (formtype.equals(TagMatchDefinitions.SEPA_MANDATE)) {
				String validitiy = (String) document.getNote(TagMatchDefinitions.SEPA_SIGNATURE_FLAG);
				String mandate = (String) document.getNote(TagMatchDefinitions.SEPA_MANDATE_NUMBER);
				// Ticket 245267
				// We don´t have to check the Flag SEPA_SIGNATURE_FLAG
				//old code
				//if (mandate != null && SiebelConnectorImpl.checkSignedMandateLetter(channel, validitiy)) {
				SkyLogger.getWflLogger().info(logPrefix + " validitiy_SEPA_OHNE_FLAG_SIGNATURE: " + archiveFlag.toString());
				if (mandate != null) {
					archiveFlag = Boolean.TRUE;
					String signatureDate = (String) document.getNote(TagMatchDefinitions.SEPA_SIGNATURE_DATE);
					if (signatureDate != null) {
						tagMap.put(TagMatchDefinitions.SEPA_SIGNATURE_DATE, signatureDate);
					}
				}
				//old code
				//tagMap.put(TagMatchDefinitions.SEPA_SIGNATURE_FLAG, archiveFlag ? "1" : "0");
			} else if (formtype.equals("fh vertrag")) {
				Boolean localArchiveFlag = (Boolean) container.getNote(TagMatchDefinitions.ARCHIVE_FLAG);
				if (localArchiveFlag != null) {//DI 19.05.2014
					archiveFlag = (Boolean) container.getNote(TagMatchDefinitions.ARCHIVE_FLAG);
				}
			} else if (formtype != null && formtype.toLowerCase().contains("kuendigung")) {
				if (document.getNote(TagMatchDefinitions.CANCELLATION_REASON_CODE) != null) {
					tagMap.put(TagMatchDefinitions.CANCELLATION_REASON_CODE, (String) document.getNote(TagMatchDefinitions.CANCELLATION_REASON_CODE));
				}
				if (document.getNote(TagMatchDefinitions.CANCELLATION_REASON_FREE_TEXT) != null) {
					tagMap.put(TagMatchDefinitions.CANCELLATION_REASON_FREE_TEXT, (String) document.getNote(TagMatchDefinitions.CANCELLATION_REASON_FREE_TEXT));
				}
				if (document.getNote(TagMatchDefinitions.META_POSSIBLE_CANCELATION_DATE) != null) {
					tagMap.put(TagMatchDefinitions.META_POSSIBLE_CANCELATION_DATE, (String) document.getNote(TagMatchDefinitions.META_POSSIBLE_CANCELATION_DATE));
				}
				if (document.getNote(TagMatchDefinitions.META_CANCELATION_DATE) != null) {
					tagMap.put(TagMatchDefinitions.META_CANCELATION_DATE, (String) document.getNote(TagMatchDefinitions.META_CANCELATION_DATE));
				}
				if (document.getNote(TagMatchDefinitions.CUSTOMER_CONTRACT_QUANTITY) != null) {
					tagMap.put(TagMatchDefinitions.CUSTOMER_CONTRACT_QUANTITY, (String) document.getNote(TagMatchDefinitions.CUSTOMER_CONTRACT_QUANTITY));
				} else {
					SkyLogger.getWflLogger().warn(logPrefix + " CUSTOMER_CONTRACT_QUANTITY not available");
				}
				if (document.getNote(TagMatchDefinitions.META_STAGE) != null) {
					tagMap.put(TagMatchDefinitions.META_STAGE, (String) document.getNote(TagMatchDefinitions.META_STAGE));
				} else {
					SkyLogger.getWflLogger().warn(logPrefix + " CUSTOMER_STAGE not available");
				}
				if (document.getNote(TagMatchDefinitions.ACTIVE_CONTRACT_QUANTITY) != null) {
					tagMap.put(TagMatchDefinitions.ACTIVE_CONTRACT_QUANTITY, (String) document.getNote(TagMatchDefinitions.ACTIVE_CONTRACT_QUANTITY));
				}
				else {
					SkyLogger.getWflLogger().warn(logPrefix + " ACTIVE_CONTRACT_QUANTITY not available");
				}
			}
		}
		if (!archiveFlag) {
			long ts = incommingDate == null ? System.currentTimeMillis() : incommingDate.getTime();
			archiveFlag = checkBarcodePages(document, tagMap, new Timestamp(ts));
		}
		if (document.getNote(TagMatchDefinitions.ATTACHMENTS_QUANTITY) != null) {
			tagMap.put(TagMatchDefinitions.ATTACHMENTS_QUANTITY, (String) document.getNote(TagMatchDefinitions.ATTACHMENTS_QUANTITY));
		}
		else {
			SkyLogger.getWflLogger().warn("TagMatchDefinitions.ATTACHMENTS_QUANTITY not available");
		}
		if (document.getNote(TagMatchDefinitions.WEBFORM_CANCELLATION) != null) {
			tagMap.put(TagMatchDefinitions.WEBFORM_CANCELLATION, (String) document.getNote(TagMatchDefinitions.WEBFORM_CANCELLATION));
		}
		SkyLogger.getWflLogger().info(logPrefix + " ARCHIVE_FLAG: " + archiveFlag.toString());
		tagMap.put(TagMatchDefinitions.ARCHIVE_FLAG, archiveFlag.toString());
		document.setNote(TagMatchDefinitions.ARCHIVE_FLAG, archiveFlag);
		flow.put(TagMatchDefinitions.ARCHIVE_FLAG, archiveFlag);

		List<TagMatch> resultTagList = convertToTagList(tagMap);
		document.setTags(resultTagList);
		flow.put(DOC_TAGS, resultTagList);


	}

	@Override
	public KeyConfiguration[] getKeys() {
		return new KeyConfiguration[]{new KeyConfiguration(MXDOCTYPE, String.class), new KeyConfiguration(DOC_TAGS, List.class), new KeyConfiguration("emailbody", String.class)};
	}


	protected boolean checkBarcodePages(CDocument document, Map<String, String> tagList, Timestamp incomingTimestamp) throws Exception {
		boolean hasBarcode = false;
		for (Pattern p : ARCHIVE_BARCODES) {
			for (String bc : WorkflowTextExtractionUtils.getBarcodesMatchesPattern(document, p)) {
				hasBarcode = true;
				insertEZMAuthEntry(document, tagList, incomingTimestamp, bc);
				tagList.put("archive_barcode", bc);
				break;
			}
		}
		return hasBarcode;
	}

	// Creates table entry for SEPA conversion of EZM.
	protected void insertEZMAuthEntry(CDocument doc, Map<String, String> tagList, Timestamp incomingTimestamp, String bc) throws Exception {
		Connection con = null;
		PreparedStatement insertStmt = null;
		try {
			final Map<String, String> directDebitAuthdata = checkTags(tagList);
			con = ContexDbConnector.getAutoCommitConnection();
			if (BARCODE_EZM.equals(bc) && directDebitAuthdata != null && directDebitAuthdata.size() == 5) {
				insertStmt = con.prepareStatement(INSERT_EZM_STMT);
				insertStmt.setTimestamp(1, incomingTimestamp);
				insertStmt.setString(2, directDebitAuthdata.get(TagMatchDefinitions.DOCUMENT_ID));
				insertStmt.setString(3, directDebitAuthdata.get(TagMatchDefinitions.EVAL_CUSTOMER_NUMBER));
				insertStmt.setString(4, directDebitAuthdata.get(TagMatchDefinitions.EVAL_CONTRACT_NUMBER));
				insertStmt.setString(5, doc.getFormtype());
				insertStmt.setString(6, directDebitAuthdata.get(TagMatchDefinitions.CONTACT_ID));
				insertStmt.execute();
			} else {
				SkyLogger.getWflLogger().info("No DDA barcode? : " + bc + " for DOCID: " + (directDebitAuthdata != null ? directDebitAuthdata.get(TagMatchDefinitions.DOCUMENT_ID) : null));
			}
		} catch (SQLException e) {
			SkyLogger.getWflLogger().warn("ERROR WHILE ADDING DIRECT DEBIT AUTHORISATION FOR DOCUMENT ID: " + doc.getNote(TagMatchDefinitions.DOCUMENT_ID) + "\n Cause: " + e.getMessage());
			throw new Exception("ERROR WHILE ADDING DIRECT DEBIT AUTHORISATION FOR DOCUMENT ID: " + doc.getNote(TagMatchDefinitions.DOCUMENT_ID) + "\n Cause: " + e.getMessage(), e);
		} finally {
			try {
				if (insertStmt != null) {
					insertStmt.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				SkyLogger.getWflLogger().warn("Problem closing rs for Sequence " + doc.getNote(TagMatchDefinitions.DOCUMENT_ID) + "\n Cause: " + e.getMessage(), e);
			}
		}
	}


	private Map<String, String> checkTags(Map<String, String> tagList) {
		Map<String, String> directDebitAuthdata = new TreeMap<>();
		for (Map.Entry<String, String> e : tagList.entrySet()) {
			String key = e.getKey();
			if (DDA_DATA.contains(key)) {
				directDebitAuthdata.put(key, e.getValue());
			}
		}
		return directDebitAuthdata;
	}

	// Creates table entry for SEPA conversion of Lager3000.
	protected boolean insertLagerAuthEntry(CDocument doc, Map<String, String> tagList, Timestamp incomingTimestamp) throws Exception {
		Connection con = null;
		PreparedStatement insertStmt = null;
		boolean isActive = false;
		try {
			final Map<String, String> directDebitAuthdata = checkTags(tagList);
			con = ContexDbConnector.getAutoCommitConnection();

			insertStmt = con.prepareStatement(INSERT_LAGER_STMT);
			Integer status = 1;
			String customerId = directDebitAuthdata.get(TagMatchDefinitions.EVAL_CUSTOMER_NUMBER);
			String contractId = directDebitAuthdata.get(TagMatchDefinitions.EVAL_CONTRACT_NUMBER);
			String contractStatus = directDebitAuthdata.get(EVAL_CONTRACT_STATUS);
			if (customerId != null && customerId.length() > 0) {
				status = 2;
				if (contractId != null && contractId.length() > 0) {
					isActive = contractStatus.equals("AKTIV");
					status = isActive ? 0 : 3;
				} else {
					contractId = "";
				}
			} else {
				customerId = "";
			}
			insertStmt.setString(3, customerId);
			insertStmt.setString(4, contractId);
			insertStmt.setString(5, status.toString());
			insertStmt.setTimestamp(1, incomingTimestamp);
			insertStmt.setString(2, directDebitAuthdata.get(TagMatchDefinitions.DOCUMENT_ID));
			insertStmt.setString(6, directDebitAuthdata.get(TagMatchDefinitions.CONTACT_ID));
			insertStmt.execute();

		} catch (SQLException e) {
			SkyLogger.getWflLogger().warn("ERROR WHILE ADDING DIRECT DEBIT AUTHORISATION FOR DOCUMENT ID: " + doc.getNote(TagMatchDefinitions.DOCUMENT_ID) + "\n Cause: " + e.getMessage());
			throw new Exception("ERROR WHILE ADDING DIRECT DEBIT AUTHORISATION FOR DOCUMENT ID: " + doc.getNote(TagMatchDefinitions.DOCUMENT_ID) + "\n Cause: " + e.getMessage(), e);
		} finally {
			try {
				if (insertStmt != null) {
					insertStmt.close();
				}
			} catch (SQLException e) {
				SkyLogger.getWflLogger().warn("Problem closing rs for Sequence " + doc.getNote(TagMatchDefinitions.DOCUMENT_ID) + "\n Cause: " + e.getMessage(), e);
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				SkyLogger.getWflLogger().warn("Problem closing stmt for Sequence " + doc.getNote(TagMatchDefinitions.DOCUMENT_ID) + "\n Cause: " + e.getMessage(), e);
			}
		}

		return isActive;
	}

	private Map<String, String> copyNoteAsStringToTagMap(String identifier, Map<String, String> tagMap, CDocument document) {
		return copyNoteAsStringToTagMap(identifier, identifier, tagMap, document);
	}

	private Map<String, String> copyNoteAsStringToTagMap(String identifier, String tagmatchid, Map<String, String> tagMap, CDocument document) {
		if (tagMap == null) {
			SkyLogger.getWflLogger().warn("TagList is null");
			tagMap = new HashMap<>();
		}

		Object obj = document.getNote(identifier);
		String val;
		if (obj == null) {
			val = "";
		} else if (obj instanceof String) {
			val = (String) obj;
		} else {
			val = obj.toString();
		}
		//final TagMatch tagMatch = new TagMatch(tagmatchid, val);
		// tagMatch.setCaption(TagMatchDefinitions.getCaption(identifier));
		tagMap.put(tagmatchid, val);
		return tagMap;
	}


	private List<TagMatch> convertToTagList(Map<String, String> tagmap) {
		List<TagMatch> result = new LinkedList<>();
		for (Map.Entry<String, String> e : tagmap.entrySet()) {
			result.add(new TagMatch(e.getKey(), e.getValue()));
		}
		return result;
	}

	private static final List<String> tagBlacklist = Arrays.asList("validation", "mandateValidation");

	private Map<String, String> convertToTagMap(List<TagMatch> tagmap) {
		Map<String, String> result = new HashMap<>();
		for (TagMatch e : tagmap) {
			if (!tagBlacklist.contains(e.getIdentifier())) {
				result.put(e.getIdentifier(), e.getTagValue());
			}
		}
		return result;
	}
}
