/**
 *
 *
 */
package com.nttdata.de.sky.ityx.common;

import com.nttdata.de.lib.logging.SkyLogger;
import de.ityx.contex.interfaces.extag.TagMatch;
import de.ityx.mediatrix.data.Email;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Defines tagmatch names for manual validation.
 *
 * @author DHIFLM
 */
public class TagMatchDefinitions {

	public static final int SBS_PROJECT_ID = 120;
	
	public static boolean isNotSbsProject(Email email) {
		return !isSbsProject(email);
	}

	public static boolean isSbsProject(Email email) {
		return email.getProjectId() == SBS_PROJECT_ID;
	}

	public static enum Channel {
		BRIEF, FAX, EMAIL, SOCIALMEDIA, DOCUMENT, SOCIALMEDIACARINGCOMMUNITY, SOCIALMEDIAFACEBOOK, SOCIALMEDIATWITTER, SOCIALMEDIAGOOGLE
	}


	public static enum Direction {
		INBOUND, OUTBOUND
	}

	public static enum DocumentDirection {INBOUND, OUTBOUND, MULTICASE, INDIVIDUALCORRESPONDENCE, SPLITTED, FORWARDED, DEFAULT}


	// Some constants
	public static final String MTX_EMAIL_ID ="MTX-EmailId";
	public static final String INITIAL = "initial";
	public static final String TRUE = "true";
	public static final String FALSE = "false";
	public static final String FORM_TYPE_CATEGORY = "FormType";
	public static final String FORM_TYPE = "Formtype";
	public static final String CONTACT_ID = "ContactID";
	public static final String ACTIVITY_ID = "ActivityID";
	public static final String DEFAULT_FORMTYPE = "unclassified";
	public static final String DMS_ARCHIVE_META = "DMSArchiveMeta";
	public static final String DMSB_2_ARCHIVE_META = "DMSB2ArchiveMeta";
	public static final String DMS_MANUAL_INDEXING_QID = "ManualIndexingQID";

	// Document notes
	public static final String EVAL_CUSTOMER_NUMBER = "EvalCustomerNumberAuto";
	public static final String EVAL_CUSTOMER_NUMBER_BEFORE_VALIDATION = "EvalPreCustomerNumber";
	public static final String EVAL_CONTRACT_NUMBER = "EvalContractNumberAuto";
	public static final String EVAL_CONTRACT_NUMBER_BEFORE_VALIDATION = "EvalPreContractNumber";
	public static final String EVAL_FORMTYPE = "EvalFormtypeAuto";
	public static final String EVAL_FORMTYPE_BEFORE_VALIDATION = "EvalPreFormtype";
	public static final String EVAL_CONTRACT_STATUS = "EvalContractStatus";
	public static final String CANCELLATION_REASON = "CancellationReason";
	public static final String CANCELLATION_REASON_CODE = "CancellationReasonCode";
	public static final String FORMTYPERELIABILITY ="FormtypeReliability";
	public static final String CANCELLATION_REASON_FREE_TEXT = "CancellationReasonFreeText";

	// Archiving tags
	public static final String CUSTOMER_COUNTRY = "CustomerCountry";
	public static final String CUSTOMER_CITY = "CustomerCity";
	public static final String CUSTOMER_ZIP_CODE = "CustomerZipCode";
	public static final String CUSTOMER_STREET = "CustomerStreet";
	public static final String CUSTOMER_ADDITIONAL_ADDRESS = "CustomerAdditionalAddress"; // SIT-17-05-099
	public static final String CUSTOMER_LAST_NAME = "CustomerLastName";
	public static final String CUSTOMER_FIRST_NAME = "CustomerFirstName";
	public static final String CHANNEL = "Channel";
	public static final String DOCUMENT_TYPE = "DocumentType";
	public static final String CUSTOMER_ID = "CustomerID";
	public static final String DOCUMENT_ID = "DocumentID";
	public static final String SIEBEL_CUSTOMER_ID = "SiebelCustomerID";

	// Validation tags
	public static final String TAGMATCH_BANK = "BankAccountData";
	public static final String TAGMATCH_PHONE_NUMBER = "PhoneNumber";
	public static final String TAGMATCH_SMARTCARD_NUMBER = "SmartcardNumber";
	public static final String TAGMATCH_CONTRACT_NUMBER = "ContractNumber";
	public static final String TAGMATCH_EMAIL = "CustomerEmailAddress";
	public static final String MANUAL_FORMTYPE = "ManualFormtype";

	// Enrichment tags
	public static final String META_EARMARKED_CANCELATION_DATE = "MetaEarmarkedCancelationDate";
	public static final String META_POSSIBLE_CANCELATION_DATE = "MetaCancellationDeadlineDate";
	public static final String META_SUBSCRIPTION_START_DATE = "MetaSubscriptionStartDate";
	public static final String META_CANCELATION_DATE = "MetaCancellationDate";
	public static final String META_SUBSCRIPTION_START_DATE_BEFORE_LIMIT = "MetaSubscriptionStartDateBeforeLimit";
	public static final String META_SKY_GO = "MetaSkyGo";
	public static final String META_SR_CONTRACT_CHANGE_DATE = "MetaSRContractChangeDate";
	public static final String META_SR_CONTRACT_CHANGE = "MetaSRContractChange";
	public static final String META_DUNNING_LEVEL = "MetaDunningLevel";
	public static final String META_CAMPAIGN_STAMP = "MetaCampaignStamp";
	public static final String META_PRICELIST = "MetaPricelist";
	public static final String META_CUSTOMER_CATEGORY = "MetaCustomerCategory";
	public static final String META_WASHING_MACHINE = "MetaWashingMachine";
	public static final String META_CAMPAIGN_TYPE = "MetaCampaignType";
	public static final String META_RATECARDFLG = "MetaRateCardFlg";
	public static final String META_SUBSCRIPTION_DATE = "SubscriptionStartDate";
	public static final String META_SUBSCRIPTON_DAYS = "StartSubscriptionDays";
	public static final String META_CONTRACT_DATE = "StartOfContractDay";
	public static final String META_CUSTOMER_MIGRATION="CustomerMigration";
	public static final String META_Q_FLAG="QFlag";
	public static final String META_BOX_ONLY="BoxOnly";
	public static final String META_STAGE="Stage";
	public static final String META_OPERATOR = "MetaOperator";
	public static final String META_PLATFORM = "MetaPlatform";
	public static final String META_RECEPTION = "MetaReception";
	public static final String META_TOIDS = "MetaTOIDS";
	public static final String META_CONTRACT_TYPE = "MetaContractType";
	public static final String CONTACT_INTERVAL_7D = "Contact_Interval_7D";
	public static final String CONTACT_INTERVAL_14D = "Contact_Interval_14D";
	public static final String CONTACT_INTERVAL_21D = "Contact_Interval_21D";
	public static final String CONTACT_INTERVAL_28D = "Contact_Interval_28D";
	public static final String CUSTOMER_CONTRACT_QUANTITY="CustomerContractQuantity";
	public static final String ACTIVE_CONTRACT_QUANTITY="ActiveContractQuantity";
	public static final String ATTACHMENTS_QUANTITY="AttachmentsQuantity";
	public static final String WEBFORM_CANCELLATION="WebformCancellation";

	public static final String PACKANDPRODUCT_LIST ="PackAndProductList";

	// SEPA tags
	public static final String SEPA_MANDATE = "SEPAMandate";
	public static final String FH_VERTRAG = "fh_vertrag";
	public static final String SEPA_MANDATE_NUMBER = "MandateRefID";
	//public static final String IBAN = "CustomerIBAN";
	public static final String BIC = "CustomerBIC";
	public static final String SEPA_SIGNATURE_FLAG = "SignatureFlag";
	public static final String SEPA_SIGNATURE_DATE = "SignatureDate";
	public static final String SEPA_STATUS = "MandateStatus";

	// SBS tags
	public static final String SBS_COMPANY = "SbsCompany";
	public static final String SBS_FORMTYPE_PREFIX = "sbs_";
	public static final String SBS_FORMTYPE_DEFAULT = "sbs_default";

	// Header prefixes
	public static final String X_TAGMATCHFIELD_SMCTYPE = "BarcodeTyp";
	public static final String X_TAGMATCH_SMCTYPE = "X-Tagmatch:" + X_TAGMATCHFIELD_SMCTYPE + "=";
	public static final String X_TAGMATCH_FORM_TYPE = "X-Tagmatch:" + FORM_TYPE_CATEGORY + "=";
	public static final String X_TAGMATCH_CUSTOMER_ID = "X-Tagmatch:" + CUSTOMER_ID + "=";
	public static final String X_TAGMATCH_DOCUMENT_ID = "X-Tagmatch:" + DOCUMENT_ID + "=";
	public static final String X_TAGMATCH_CUSTOMER_CATEGORY = "X-Tagmatch:" + META_CUSTOMER_CATEGORY + "=";
	public static final String X_TAGMATCH_CUSTOMER_FIRST_NAME = "X-Tagmatch:" + CUSTOMER_FIRST_NAME + "=";

	public static final String CREATEDATE = "CreateDate";
	public static final String INCOMINGDATE = "IncomingDate";
	public static final String INCOMINGTIMESTAMP = "INCOMINGTIMESTAMP";

	public static final String MX_QUESTIONID = "FrageID";
	public static final String MX_EMAILID = "EmailID";
	public static final String MX_ANSWERID = "AnswerID";
	public static final String MX_ANSWERDOCUMENTID = "AntwortDocumentID";

	public static final String MX_DIRECTION = "Direction";
	public static final String X_TAGMATCH_MX_DIRECTION = "X-Tagmatch:" + MX_DIRECTION + "=";
	public static final String X_TAGMATCH = "X-Tagmatch:";

	public static final String MX_TP_NAME = "TeilprojektName";
	public static final String MX_TP_ID = "TeilprojektID";
	public static final String MX_LANGUAGE = "Language";
	public static final String MX_QUESTIONDOCUMENTID = "FrageDocumentID";
	public static final String MX_MASTER = "FrageDocumentID";
	// Customer data
	public static final List<String> CUSTOMER_DATA = Arrays.asList(PACKANDPRODUCT_LIST, META_RATECARDFLG, CUSTOMER_COUNTRY, CUSTOMER_CITY, CUSTOMER_ZIP_CODE,
			CUSTOMER_STREET, CUSTOMER_ADDITIONAL_ADDRESS, CUSTOMER_LAST_NAME, CUSTOMER_FIRST_NAME, CUSTOMER_ID, TAGMATCH_BANK, TAGMATCH_PHONE_NUMBER,
			TAGMATCH_SMARTCARD_NUMBER, TAGMATCH_CONTRACT_NUMBER, TAGMATCH_EMAIL, META_EARMARKED_CANCELATION_DATE, CUSTOMER_CONTRACT_QUANTITY,META_CANCELATION_DATE,META_POSSIBLE_CANCELATION_DATE, META_SUBSCRIPTION_START_DATE,
			META_SUBSCRIPTION_START_DATE_BEFORE_LIMIT, META_SKY_GO, META_SR_CONTRACT_CHANGE_DATE, META_SR_CONTRACT_CHANGE, META_DUNNING_LEVEL, META_CAMPAIGN_STAMP,
			META_PRICELIST, META_CUSTOMER_CATEGORY, META_WASHING_MACHINE, META_CAMPAIGN_TYPE, META_SUBSCRIPTION_DATE, META_SUBSCRIPTON_DAYS, META_OPERATOR,
			META_PLATFORM, META_RECEPTION, META_TOIDS, SIEBEL_CUSTOMER_ID, CONTACT_ID, META_CONTRACT_TYPE, CONTACT_INTERVAL_7D, CONTACT_INTERVAL_14D,
			CONTACT_INTERVAL_21D, CONTACT_INTERVAL_28D, SEPA_MANDATE_NUMBER, SEPA_SIGNATURE_FLAG, SEPA_SIGNATURE_DATE, SEPA_STATUS, BIC, META_CONTRACT_DATE,
			SBS_COMPANY, META_CUSTOMER_MIGRATION, META_POSSIBLE_CANCELATION_DATE, META_Q_FLAG, META_BOX_ONLY,ACTIVE_CONTRACT_QUANTITY, META_STAGE);

	private static final ResourceBundle captions = ResourceBundle.getBundle("com.nttdata.de.sky.ityx.common.Captions", new Locale(System.getProperty("br.locale", "de")));

	/**
	 * Construtcs the corresponding headername of this tagname.
	 *
	 * @param tagname The tagname
	 * @return The headername
	 */
	public static String getHeaderTagName(String tagname) {
		return "X-Tagmatch:" + tagname + "=";
	}

	/**
	 * Returns a tag value. Caveat: This method is not threadsafe.
	 *
	 * @param tagName Names the tag.
	 * @param tagList Contains the tag.
	 * @return The tag value or <code>null</code> if none exists.
	 */
	public static String getTagValue(String tagName, List<TagMatch> tagList) {
		assert tagName != null && tagName.trim().length() > 0 && tagList != null;
		for (TagMatch tagMatch : tagList) {
			String value = tagMatch.getTagValue(tagName);
			if (value != null && !value.isEmpty()) {
				return value;
			}
		}
		return null;
	}

	/**
	 * Extracts a header value from the headers.
	 *
	 * @param headers    Contains the headers.
	 * @param headerName The header prefix
	 * @return The header value
	 */
	public static String extractXTMHeader(String headers, String headerName) {
		String headerKey = headerName.trim();
		if (!headerKey.startsWith("X-Tagmatch:")) {
			headerKey = "X-Tagmatch:" + headerKey;
		}
		if (!headerKey.endsWith("=")) {
			headerKey = headerKey + "=";
		}
		return extractOrgHeader(headers, headerKey);
	}


	public static String extractOrgHeader(String headers, String headerName) {
		String headerKey = headerName.trim();
		if (!headerKey.endsWith("=") && !headerKey.endsWith(":")) {
			headerKey = headerKey + ":";
		}
		if (headers == null) {
			return null;
		}

		String headerValue = null;
		int startIdx=0;
		int beginHeader=0;
		while ((beginHeader = headers.indexOf(headerKey, startIdx)) >= 0) {
			final int beginIndex = beginHeader + headerKey.length();
			String headerString = headers.substring(beginIndex);
			final int endHeader = headerString.indexOf("\n");

			if (startIdx>0) { // Fix for double TagMatches in Header
				if (headerValue==null|| headerValue.isEmpty()){
					if (endHeader > -1) {
						headerValue = headerString.substring(0, endHeader);
					} else {
						headerValue = headerString;
					}
				}else {
					//remove doubleTagmatch
					headers = (new StringBuffer(headers)).replace(beginHeader, beginIndex + headerValue.length(), "").toString();
					SkyLogger.getCommonLogger().debug("RemoveDoubleTagmatchFromHeader:" + headerKey+ " # " + headerValue+" # "+((endHeader > -1)?headerString.substring(0, endHeader):headerString));
				}
			}else{
				if (endHeader > -1) {
					headerValue = headerString.substring(0, endHeader);
				} else {
					headerValue = headerString;
				}
			}
			startIdx=beginIndex + endHeader;
		}
		if (headerValue == null || headerValue.isEmpty()) {
			return null;
		}
		return headerValue;
	}

	/**
	 * Replaces a header value in the headers.
	 *
	 * @param headers
	 * @param headerName
	 * @param replaceValue
	 * @return The new headers
	 */
	@Deprecated
	private static String replaceHeader(String headers, String headerName, String replaceValue) {
		final int beginHeader = headers.lastIndexOf(headerName);
		if (beginHeader > 0) {
			final int beginIndex = beginHeader + headerName.length();
			String headerString = headers.substring(beginIndex);
			String headerValue = headerString.substring(0, headerString.indexOf("\n"));
			StringBuffer buf = new StringBuffer(headers);
			headers = buf.replace(beginIndex, beginIndex + headerValue.length(), replaceValue).toString();
		}
		return headers;
	}

	/**
	 * Adds a header tag at the end of the headers.
	 *
	 * @param headers
	 * @param headerName
	 * @param headerValue
	 * @return The new headers
	 */
	@Deprecated
	private static String addHeader(String headers, String headerName, String headerValue) {
		StringBuffer buf = new StringBuffer(headers);
		headers = buf.append(headerName + headerValue + "\n").toString();
		return headers;
	}

	public static String addOrReplaceOrgHeader(String headers, String headerName, String replaceValue) {
		if (headers==null){
			headers="";
		}
		String headerKey = headerName.trim();
		if (!headerKey.endsWith("=") && !headerKey.endsWith(":")) {
			headerKey = headerKey + ":";
		}
		if (headers.contains(headerKey)) {
			//headers = TagMatchDefinitions.replaceHeader(headers, TagMatchDefinitions.getHeaderTagName(headerKey), headerValue);

			int startIdx=0;
			int beginHeader=0;
			while ((beginHeader = headers.indexOf(headerKey, startIdx)) >= 0) {
				final int beginIndex = beginHeader + headerKey.length();
				String headerString = headers.substring(beginIndex);
				String headerValue = headerString.substring(0, headerString.indexOf("\n"));

				if (startIdx==0) {
					headers = (new StringBuffer(headers)).replace(beginIndex, beginIndex + headerValue.length(), replaceValue==null?"":replaceValue).toString();
				}else{
					//remove doubleTagmatch
					headers = (new StringBuffer(headers)).replace(beginHeader, beginIndex + headerValue.length(), "").toString();
				}
				startIdx=beginIndex + headerValue.length();
			}
		} else {
			//headers = TagMatchDefinitions.addHeader(headers, TagMatchDefinitions.getHeaderTagName(headerKey), replaceValue);
			if (!headers.endsWith("\n")) {
				headers += "\n";
			}
			headers += headerKey + replaceValue + "\n";
		}
		return headers;
	}

	public static String addOrReplaceXTMHeader(String headers, String headerName, String replaceValue) {
		String headerKey = headerName.trim();

		if (!headerKey.startsWith("X-Tagmatch:")) {
			headerKey = "X-Tagmatch:" + headerKey;
		}
		if (!headerKey.endsWith("=")) {
			headerKey = headerKey + "=";
		}
		return addOrReplaceOrgHeader(headers, headerKey, replaceValue);
	}

	public static String getCaption(String identifier) {
		if (identifier != null) {
			final String captionKey = "CAPTION." + identifier;
			if (captions.containsKey(captionKey)) {
				return captions.getString(captionKey);
			}
		}
		return identifier;
	}

	public static final String ARCHIVE_FLAG = "AutomaticallyArchived";
	public static final String Z_EZM_NICHT_WEITERLEITEN = "Z_EZM_nicht_weiterleiten";
	public static final String LAGER3000 = "Lager3000";
	public static final String STATUS_AKTIV = "AKTIV";
	public static final String AUTOPROCESSING_FLAG = "Autoprocessing";
	public static final String AUTOMATICALLY_PROCESSED_CANCELLATION_FLAG = "AutomaticallyProcessedCancellation";


	public static Channel getChannel(String value) {
		return getChannel(value, Channel.EMAIL);
	}

	public static Channel getChannel(String value, Channel defchannel) {
		if (value == null) {
			return defchannel;
		} else if (value.equalsIgnoreCase("email") || value.equalsIgnoreCase("mail")) {
			return Channel.EMAIL;
		} else if (value.equalsIgnoreCase("brief") || value.equalsIgnoreCase("letter")) {
			return Channel.BRIEF;
		} else if (value.equalsIgnoreCase("fax")) {
			return Channel.FAX;
		} else if (value.equalsIgnoreCase("SOMCC") || value.equalsIgnoreCase("SocialMediaCaringCommunity") || value.equalsIgnoreCase("CaringCommunity")) {
			return Channel.SOCIALMEDIACARINGCOMMUNITY;
		} else if (value.equalsIgnoreCase("SOMF") || value.equalsIgnoreCase("SocialMediaFacebook") || value.equalsIgnoreCase("Facebook")) {
			return Channel.SOCIALMEDIAFACEBOOK;
		} else if (value.equalsIgnoreCase("SOMTW") || value.equalsIgnoreCase("SocialMediaTwitter") || value.equalsIgnoreCase("Twitter")) {
			return Channel.SOCIALMEDIATWITTER;
		} else if (value.equalsIgnoreCase("SOMGP") || value.equalsIgnoreCase("SocialMediaGooglePlus") || value.equalsIgnoreCase("GooglePlus") || value.equalsIgnoreCase("Google")) {
			return Channel.SOCIALMEDIAGOOGLE;
		} else if (value.equalsIgnoreCase("socialmedia") || value.equalsIgnoreCase("sm") || value.equalsIgnoreCase("twitter") || value.equalsIgnoreCase("facebook")) {
			return Channel.SOCIALMEDIA;
		} else {
			return Channel.DOCUMENT;
		}
	}

	public static DocumentDirection getDocumentDirection(String value) {
		if (value.equalsIgnoreCase("INBOUND")) {
			return DocumentDirection.INBOUND;
		} else if (value.equalsIgnoreCase("OUTBOUND")) {
			return DocumentDirection.OUTBOUND;
		} else if (value.equalsIgnoreCase("MULTICASE")) {
			return DocumentDirection.MULTICASE;
		} else if (value.equalsIgnoreCase("INDIVIDUALCORR") || value.equalsIgnoreCase("INDIVIDUALCORRESPONDENCE")) {
			return DocumentDirection.INDIVIDUALCORRESPONDENCE;
		} else if (value.equalsIgnoreCase("SPLITTED")) {
			return DocumentDirection.SPLITTED;
		} else if (value.equalsIgnoreCase("FORWARDED")) {
			return DocumentDirection.FORWARDED;
		} else {
			return DocumentDirection.DEFAULT;
		}
	}
}
