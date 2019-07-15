package com.nttdata.de.ityx.cx.sky.enrichment;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;
import com.nttdata.de.ityx.cx.workflow.base.FlowUtils;
import com.nttdata.de.ityx.sharedservices.utils.ContexDbConnector;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.ityx.sharedservices.utils.ShedulerUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.connector.ConnectorFactory;
import com.nttdata.de.sky.connector.IFAA;
import com.nttdata.de.sky.connector.INewDB;
import com.nttdata.de.sky.connector.data.IndexingCustomer;
import com.nttdata.de.sky.connector.data.RoutingCustomer;
import com.nttdata.de.sky.connector.faa.FAAConnectorImpl;
import com.nttdata.de.sky.connector.newdb.NewDBConnectorImpl;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;

import de.ityx.contex.impl.designer.exflow.maps.TextParameter;
import de.ityx.contex.impl.designer.state.KeyConfiguration;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.designer.IParameterMap;
import de.ityx.contex.interfaces.document.CDocument;
import de.ityx.contex.interfaces.extag.TagMatch;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * Implements search for customer based on information available via extraction.
 * 
 * The following information will be extracted by the extraction process:
 * 
 * 
 * 
 * @author VOGT41
 * 
 */
public class EnrichContractBean extends AbstractWflBean {

	private static final long		TIME_1D				= 1000 * 60 * 60 * 24;
	public static final Long		TIME_7D				= 7 * TIME_1D;
	public static final Long		TIME_14D			= 2 * TIME_7D;
	public static final Long		TIME_21D			= 3 * TIME_7D;
	public static final Long		TIME_28D			= 4 * TIME_7D;
	//public static final String		DE					= "DEUTSCHLAND";

	// added only for unit tests (ugly...)
	public boolean					simDB				= false;

	private transient Connection	con					= null;
	private String					tableCustomer		= "newdb_customer";
	private String					tableContract		= "newdb_contract";
	private String					tableAsset			= "newdb_asset";
	private String					tableCampaign		= "newdb_campaign";
	private String					tableCampaignConf	= "newdb_campaign_conf";

	/**
	 * 
	 */
	private static final long		serialVersionUID	= -5939629539069842182L;

	@Override
	public void abortExecute() {

	}

	@Override
	public void cleanState() {

	}

	@Override
	public void prepareForCluster(String arg0) {

	}

	@Override
	public void prepareForResumeFromCluster() {

	}

	@Override
	public void rollbackExecute() {

	}

	@Override
	public KeyConfiguration[] getKeys() {
		return null;
	}

	// private String tableContact = "newdb_contact";

	// @Override
	// public void execute(IFlowObject flowObject) throws Exception {
	//
	// // This bean expects one of the following attributes to be set:
	// String verifiedCustomerNumber = getOptionalString(flowObject,
	// "VerifiedCustomerNumber", null);
	// if ("".equals(verifiedCustomerNumber)) verifiedCustomerNumber = null;
	// String verifiedSmartcardNumber = getOptionalString(flowObject,
	// "VerifiedSmartcardNumber", null);
	// if ("".equals(verifiedSmartcardNumber)) verifiedSmartcardNumber = null;
	// String verifiedContractNumber = getOptionalString(flowObject,
	// "VerifiedContractNumber", null);
	// if ("".equals(verifiedContractNumber)) verifiedContractNumber = null;
	//
	// Boolean verifiedDocument = getOptionalBoolean(flowObject,
	// "VerifiedDocument", false);
	//
	// String ctx_docid = getDocID(flowObject);
	// String logPrefix = getClass().getName() + "#" + (new Object()
	// {}.getClass().getEnclosingMethod().getName()) + " d:" + ctx_docid + " c:"
	// + verifiedCustomerNumber + ":";
	// SkyLogger.getItyxLogger().debug(logPrefix + ": enter");
	//
	// Boolean serviceActive = getOptionalBoolean(flowObject,
	// "DynamicCustomerData_Enabled", false);
	// String serviceUrl = getRequiredNonEmptyString(flowObject,
	// "DynamicCustomerData_WSDL");
	//
	// SkyLogger.getItyxLogger().debug(
	// verifiedCustomerNumber + " Enrich document " + ctx_docid +
	// " for customer with customer number " + verifiedCustomerNumber +
	// ", contract number " + verifiedContractNumber + " and smartcard number "
	// + verifiedSmartcardNumber+" verified:"+verifiedDocument);
	//
	// if (!(verifiedCustomerNumber != null || verifiedContractNumber != null ||
	// verifiedSmartcardNumber != null) || "0".equals(verifiedCustomerNumber)) {
	// SkyLogger.getItyxLogger().debug(logPrefix + verifiedCustomerNumber +
	// " No number provided. Setting empty customer");
	// writeCustomerToFlow(flowObject, createEmptyCustomer(""), null, "");
	// return;
	// }
	//
	// if (serviceActive) {
	// try {
	// System.setProperty(NewDBConnectorImpl.SysProperty_CustomerDataService_Endpoint,
	// serviceUrl);
	// SkyLogger.getItyxLogger().debug(logPrefix + verifiedCustomerNumber +
	// " Using service at: " + serviceUrl);
	//
	// /*
	// * Determine the database connection for enrichment
	// */
	// ContexAccess contexDBAccess;
	// if (simDB) {
	// // Wenn das ein Unit-Test ist, wurde die Datenbank bereits
	// // durch
	// // den NewDBServiceProvider geladen
	// con = DriverManager.getConnection("jdbc:hsqldb:mem:.", "SA", "");
	// } else {
	// contexDBAccess = ContexAccess.getInstance();
	// if (contexDBAccess != null) {
	// try {
	// con = contexDBAccess.getConnection();
	// } catch (Exception e) {
	// throw new Exception(verifiedCustomerNumber +
	// " Unable to access contex database", e);
	// }
	// } else {
	// throw new Exception(verifiedCustomerNumber +
	// " Unable to access contex database");
	// }
	// }
	//
	// /*
	// * Lookup customer data
	// */
	// IndexingCustomer customer = new IndexingCustomer();
	// customer.setNumber(verifiedCustomerNumber);
	// customer.setSelectedContractNumber(verifiedContractNumber);
	//
	// SkyLogger.getItyxLogger().debug(logPrefix +
	// ": Enrich using smartcard start: " + verifiedSmartcardNumber +
	// " customer:" + customer.getNumber() + " contract:" +
	// customer.getSelectedContractNumber());
	// getContractIDFromAsset(customer, verifiedSmartcardNumber);
	//
	// SkyLogger.getItyxLogger().debug(logPrefix +
	// ": enrich using newdb_contract: customer:" + customer.getNumber() +
	// " contract:" + customer.getSelectedContractNumber() + " dump:" +
	// customer.toString());
	// String isActive = enrichFromContractTable(customer);
	// SkyLogger.getItyxLogger().debug(logPrefix +
	// ": enrich using newdb_customer: customer:" + customer.getNumber() +
	// " contract:" + customer.getSelectedContractNumber() + " dump:" +
	// customer.toString());
	// enrichFromCustomerTable(customer);
	// SkyLogger.getItyxLogger().debug(logPrefix +
	// ": enrich using newdb_campaign: customer:" + customer.getNumber() +
	// " contract:" + customer.getSelectedContractNumber() + " dump:" +
	// customer.toString());
	// enrichFromCampaignTable(customer);
	// // SkyLogger.getItyxLogger().debug(logPrefix +
	// ": enrich using newdb_contact: customer:" + customer.getNumber() +
	// " dump:" + customer.toString());
	// // Map<String, Integer> intervalCounts =
	// enrichFromContactTable(customer);
	// SkyLogger.getItyxLogger().debug(logPrefix +
	// ": enrich using NewDB WS: customer:" + customer.getNumber() +
	// " contract:" + customer.getSelectedContractNumber() + " dump:" +
	// customer.toString());
	// Map<String, Integer> intervalCounts = enrichFromIF32NewDBWS(customer);
	// SkyLogger.getItyxLogger().debug(logPrefix +
	// ": afterNewDB: customer:  customer:" + customer.getNumber() +
	// " contract:" + customer.getSelectedContractNumber() + " dump:" +
	// customer.toString());
	//
	// writeCustomerToFlow(flowObject, customer, intervalCounts, isActive);
	//
	// } finally {
	// if (con != null) {
	// con.close();
	// }
	// }
	// } else {
	// writeCustomerToFlow(flowObject, createEmptyCustomer("SERVICE_OFF"), null,
	// "");
	// }
	// }

	private IndexingCustomer createEmptyCustomer(String defaultValue) {
		IndexingCustomer customer = new IndexingCustomer();
		customer.setCampaignStamp(defaultValue);
		customer.setCategory(defaultValue);
		customer.setCity(defaultValue);
		customer.setContractSkyGo(defaultValue);
		customer.setCountry(defaultValue);
		customer.setDunningLevel(defaultValue);
		customer.setEarmarkedCancelationDate(defaultValue);
		customer.setFirstName(defaultValue);
		customer.setLastName(defaultValue);
		customer.setNumber(defaultValue);
		customer.setPricelist(defaultValue);
		customer.setSrContractChange(defaultValue);
		customer.setSrContractChangeDate(defaultValue);
		customer.setStreet(defaultValue);
		customer.setSubscriptionStartDate(defaultValue);
		customer.setZipCode(defaultValue);
		customer.setNumber(defaultValue);
		customer.setRowId(defaultValue);
		customer.setCampaignTypes(defaultValue);
		customer.setWashMachineFlag(defaultValue);
		customer.setReception(defaultValue);
		customer.setOperator(defaultValue);
		customer.setPlatform(defaultValue);
		customer.setContractType(defaultValue);
		customer.setRateCardFlg(defaultValue);
		//customer.setRateCode(defaultValue);
		customer.setQFlag(defaultValue);
		customer.setBoxOnly(defaultValue);
		customer.setStage(defaultValue);
		return customer;
	}

	private void writeCustomerToFlow(IFlowObject flowObject, IndexingCustomer customer, String isActive) {

		String docId = DocContainerUtils.getDocID(flowObject);
		CDocumentContainer<CDocument> docContainer = DocContainerUtils.getDocContainer(flowObject);
		CDocument document=DocContainerUtils.getDoc(docContainer);
		SkyLogger.getItyxLogger().debug(docId + " writeCustomerToFlow");
		List<TagMatch> list = new ArrayList<>();

		//final boolean noDoc = docContainer == null;


		addToListIfNotNull(list, TagMatchDefinitions.CUSTOMER_ID, customer.getNumber());
		String contractNumber = customer.getSelectedContractNumber();
		SkyLogger.getItyxLogger().debug(docId + " Contract Number: " + contractNumber);
		if (contractNumber != null) {
			addToListIfNotNull(list, TagMatchDefinitions.TAGMATCH_CONTRACT_NUMBER, contractNumber);
		}
		String mandateNumber = customer.getMandateNumber();
		SkyLogger.getItyxLogger().debug(docId + " MandateRefId: " + mandateNumber);
		if (mandateNumber != null && !mandateNumber.isEmpty()) {
			addToListIfNotNull(list, TagMatchDefinitions.SEPA_MANDATE_NUMBER, mandateNumber);
			if (document!=null) {
				document.setNote(TagMatchDefinitions.SEPA_MANDATE_NUMBER, mandateNumber);
			}
		}

		addToListIfNotNull(list, TagMatchDefinitions.SIEBEL_CUSTOMER_ID, customer.getRowId());
		addToListIfNotNull(list, TagMatchDefinitions.META_CUSTOMER_CATEGORY, customer.getCategory());
		addToListIfNotNull(list, TagMatchDefinitions.META_PRICELIST, customer.getPricelist());
		addToListIfNotNull(list, TagMatchDefinitions.META_CAMPAIGN_STAMP, customer.getCampaignStamp());
		addToListIfNotNull(list, TagMatchDefinitions.META_DUNNING_LEVEL, customer.getDunningLevel());
		addToListIfNotNull(list, TagMatchDefinitions.META_SR_CONTRACT_CHANGE, customer.getSrContractChange());
		addToListIfNotNull(list, TagMatchDefinitions.META_SR_CONTRACT_CHANGE_DATE, customer.getSrContractChangeDate());
		addToListIfNotNull(list, TagMatchDefinitions.META_SKY_GO, customer.getContractSkyGo());
		addToListIfNotNull(list, TagMatchDefinitions.META_SUBSCRIPTION_START_DATE, customer.getSubscriptionStartDate());
		addToListIfNotNull(list, TagMatchDefinitions.META_SUBSCRIPTION_START_DATE_BEFORE_LIMIT, customer.isSubscriptionStartDateBeforeLimit() ? "Y" : "N");
		addToListIfNotNull(list, TagMatchDefinitions.META_EARMARKED_CANCELATION_DATE, customer.getEarmarkedCancelationDate());
		addToListIfNotNull(list, TagMatchDefinitions.META_POSSIBLE_CANCELATION_DATE, getMxFormatedDate(customer.getPossibleCancellationDate()));
		addToListIfNotNull(list, TagMatchDefinitions.META_CANCELATION_DATE, getMxFormatedDate(customer.getCancellationDate()));
		addToListIfNotNull(list, TagMatchDefinitions.CUSTOMER_CONTRACT_QUANTITY, customer.getCustomerContractQuantity()+"");
		addToListIfNotNull(list, TagMatchDefinitions.ACTIVE_CONTRACT_QUANTITY, customer.getActiveContractQuantity()+"");
		addToListIfNotNull(list, TagMatchDefinitions.CUSTOMER_FIRST_NAME, customer.getFirstName());
		addToListIfNotNull(list, TagMatchDefinitions.CUSTOMER_LAST_NAME, customer.getLastName());
		addToListIfNotNull(list, TagMatchDefinitions.CUSTOMER_STREET, customer.getStreet());
		addToListIfNotNull(list, TagMatchDefinitions.CUSTOMER_ZIP_CODE, customer.getZipCode());
		addToListIfNotNull(list, TagMatchDefinitions.CUSTOMER_CITY, customer.getCity());
		addToListIfNotNull(list, TagMatchDefinitions.META_CAMPAIGN_TYPE, customer.getCampaignTypes());
		addToListIfNotNull(list, TagMatchDefinitions.META_WASHING_MACHINE, customer.getWashMachineFlag());
		addToListIfNotNull(list, TagMatchDefinitions.META_SUBSCRIPTION_DATE, customer.getSubscriptionStartDate());
		addToListIfNotNull(list, TagMatchDefinitions.META_SUBSCRIPTON_DAYS, customer.isNewCustomer() ? "NK" : "BK");
		addToListIfNotNull(list, TagMatchDefinitions.META_RATECARDFLG, customer.getRateCardFlg());
		addToListIfNotNull(list, TagMatchDefinitions.META_OPERATOR, customer.getOperator());
		addToListIfNotNull(list, TagMatchDefinitions.META_PLATFORM, customer.getPlatform());
		addToListIfNotNull(list, TagMatchDefinitions.META_RECEPTION, customer.getReception());
		addToListIfNotNull(list, TagMatchDefinitions.META_TOIDS, customer.getToids());
		addToListIfNotNull(list, TagMatchDefinitions.META_CONTRACT_TYPE, customer.getContractType());
		addToListIfNotNull(list, TagMatchDefinitions.META_CONTRACT_DATE, customer.getContractDate());
		addToListIfNotNull(list, TagMatchDefinitions.META_CUSTOMER_MIGRATION, customer.getCustomerMigration());
		addToListIfNotNull(list, TagMatchDefinitions.META_Q_FLAG, customer.getQFlag());
		addToListIfNotNull(list, TagMatchDefinitions.META_STAGE, customer.getStage());
		SkyLogger.getItyxLogger().debug(" Enrichment of document QFLAG: "+ customer.getQFlag());
		//addToListIfNotNull(list, TagMatchDefinitions.META_RATE_CODE, customer.getRateCode());
		addToListIfNotNull(list, TagMatchDefinitions.META_BOX_ONLY, customer.getBoxOnly());

		// addToListIfNotNull(list, TagMatchDefinitions.CONTACT_INTERVAL_3D,
		// customer.getContractType())); -- not requested
		addToListIfNotNull(list, TagMatchDefinitions.CONTACT_INTERVAL_7D, customer.getContactInterval7D() + "");
		addToListIfNotNull(list, TagMatchDefinitions.CONTACT_INTERVAL_14D, customer.getContactInterval14D() + "");
		addToListIfNotNull(list, TagMatchDefinitions.CONTACT_INTERVAL_21D, customer.getContactInterval21D() + "");
		addToListIfNotNull(list, TagMatchDefinitions.CONTACT_INTERVAL_28D, customer.getContactInterval28D() + "");

		addToListIfNotNull(list, TagMatchDefinitions.SEPA_STATUS, customer.getMandateStatus());
		//addToListIfNotNull(list, TagMatchDefinitions.IBAN, customer.getCustomerIBAN());
		addToListIfNotNull(list, TagMatchDefinitions.BIC, customer.getCustomerBIC());
		
		String country = customer.getCountry();
		if (country != null && country.trim().length() > 0) {
			addToListIfNotNull(list, TagMatchDefinitions.CUSTOMER_COUNTRY, country);
		}

		// set output values
		if (docContainer == null) {
			IParameterMap outputMap = flowObject.getOutputMap("outputMap");
			for (TagMatch tm : list) {
				String identifier = tm.getIdentifier();
				String value = tm.getTagValue();
				outputMap.replaceParameter(identifier, identifier, new TextParameter(value));
			}
		} else {
			docContainer.setTags(new ArrayList<TagMatch>());

			SkyLogger.getItyxLogger().debug(docId + " Enrichment of document");

			if (document == null) {
				SkyLogger.getItyxLogger().error("Trying to enrich Document, but the document cannot be found in FlowContext.");
				return;
			}
			// Checks if there is a valid formtype assigned to the document.
			String val = document.getFormtype();
			String manualDoctype = (String) document.getNote(TagMatchDefinitions.MANUAL_FORMTYPE);
			if (manualDoctype == null || manualDoctype.equals("")) {
				final List<TagMatch> tags = document.getTags();
				if (tags.size() > 0) {
					manualDoctype = tags.get(0).getTagValue(TagMatchDefinitions.MANUAL_FORMTYPE);
				}
			}
			addToListIfNotNull(list, TagMatchDefinitions.MANUAL_FORMTYPE, manualDoctype);
			
			String manualQID = (String) document.getNote(TagMatchDefinitions.DMS_MANUAL_INDEXING_QID);
			if (manualQID == null || manualQID.equals("")) {
				final List<TagMatch> tags = document.getTags();
				if (tags.size() > 0) {
					manualQID = tags.get(0).getTagValue(TagMatchDefinitions.DMS_MANUAL_INDEXING_QID);
				}
			}
			addToListIfNotNull(list, TagMatchDefinitions.DMS_MANUAL_INDEXING_QID, manualQID);
			
			
			if ((manualDoctype != null && !manualDoctype.isEmpty()) && (!manualDoctype.equalsIgnoreCase(val) || val == null || val.isEmpty() || val.equals("systemdefault") || val.equals("unclassified") || val.equals("null"))) {
				val = manualDoctype;
				DocContainerUtils.setFormtype(docContainer, document, manualDoctype);
				SkyLogger.getItyxLogger().debug(docId + " Setting manual Formtype <" + val + "> ");
			}

			SkyLogger.getItyxLogger().debug(docId + " Setting document tag matches");
			document.setTags(list);

			SkyLogger.getItyxLogger().debug(docId + " Setting document notes [auto] (for evaluation/log entry)");

			document.setNote(TagMatchDefinitions.EVAL_FORMTYPE, val);
			SkyLogger.getItyxLogger().debug(docId + " " + TagMatchDefinitions.EVAL_FORMTYPE + ": " + val);

			val = customer.getNumber();
			document.setNote(TagMatchDefinitions.EVAL_CUSTOMER_NUMBER, val);
			SkyLogger.getItyxLogger().debug(docId + " " + TagMatchDefinitions.EVAL_CUSTOMER_NUMBER + ": " + val);

			val = contractNumber != null ? contractNumber : "";
			document.setNote(TagMatchDefinitions.EVAL_CONTRACT_NUMBER, val);
			SkyLogger.getItyxLogger().debug(docId + " " + TagMatchDefinitions.EVAL_CONTRACT_NUMBER + ": " + val);

			document.setNote(SetDocumentMetadata.EVAL_CONTRACT_STATUS, isActive);
			SkyLogger.getItyxLogger().debug(docId + " " + SetDocumentMetadata.EVAL_CONTRACT_STATUS + ": " + val);

			val = mandateNumber != null ? mandateNumber : "";
			document.setNote(TagMatchDefinitions.SEPA_MANDATE_NUMBER, val);
			SkyLogger.getItyxLogger().debug(docId + " " + TagMatchDefinitions.SEPA_MANDATE_NUMBER + ": " + val);
			
			SkyLogger.getItyxLogger().debug(docId + " Setting channel in flow");
			Object channel = document.getNote(TagMatchDefinitions.CHANNEL);
			flowObject.put(TagMatchDefinitions.CHANNEL, channel != null ? channel : "");

			if (document.getNote(TagMatchDefinitions.ATTACHMENTS_QUANTITY) != null) {
				flowObject.put(TagMatchDefinitions.ATTACHMENTS_QUANTITY, (String) document.getNote(TagMatchDefinitions.ATTACHMENTS_QUANTITY));
			}
			
			if (document.getNote(TagMatchDefinitions.CUSTOMER_CONTRACT_QUANTITY) != null) {
				flowObject.put(TagMatchDefinitions.CUSTOMER_CONTRACT_QUANTITY, (String) document.getNote(TagMatchDefinitions.CUSTOMER_CONTRACT_QUANTITY));
			}
			if (document.getNote(TagMatchDefinitions.META_STAGE) != null) {
				flowObject.put(TagMatchDefinitions.META_STAGE, (String) document.getNote(TagMatchDefinitions.META_STAGE));
			}
			if (document.getNote(TagMatchDefinitions.ACTIVE_CONTRACT_QUANTITY) != null) {
				flowObject.put(TagMatchDefinitions.ACTIVE_CONTRACT_QUANTITY, (String) document.getNote(TagMatchDefinitions.ACTIVE_CONTRACT_QUANTITY));
			}

		}
	}
	
	private void addToListIfNotNull(List<TagMatch> list, String key, String value){
		if (list==null || !list.isEmpty()){
			list = new ArrayList<TagMatch>();
		}
		if (key!=null && value!=null){
			list.add(new TagMatch(key, value));
		}
	}
	private String enrichFromContractTable(IndexingCustomer customer) throws Exception {
		String status = "";
		try {
			String customerNumber = (customer.getNumber() == null) ? null : customer.getNumber().trim();
			String contractNumber = (customer.getSelectedContractNumber() == null) ? null : customer.getSelectedContractNumber().trim();
			String mandateNumber = (customer.getMandateNumber() == null) ? null : customer.getMandateNumber().trim();
			String sqlQuery = "select CONTRACT_ID, CUSTOMER_ID, HOUSE_NUMBER,  STREET, ZIPCODE, CITY,COUNTRY,ACCOUNT_NUMBER,BANK_CODE, BANK_ACCOUNT_HOLDER, STATUS, OPERATOR, PLATFORM, RECEPTION, OPERATION_DATE, WM_FLG, CONTRACT_TYPE, RATECARD_FLG, CNTRSTARTDATE,BANK_ACCOUNT_HOLDER,STATUS,CUSTOMER_BIC,MANDATE_STATUS,MANDATE_REF_ID  "
					+ "from " + tableContract + " where customer_id = '" + customerNumber + "' and contract_id = '" + contractNumber + "' and mandate_ref_id = '" + mandateNumber + "'";
			if ((customerNumber == null || customerNumber.equals("") || customerNumber.equals("0")) && (contractNumber != null && !contractNumber.equals("") && !contractNumber.equals("0"))
					&& (mandateNumber != null && !mandateNumber.equals(""))) {
				sqlQuery = "select CONTRACT_ID, CUSTOMER_ID, HOUSE_NUMBER,  STREET, ZIPCODE, CITY,COUNTRY,ACCOUNT_NUMBER,BANK_CODE, BANK_ACCOUNT_HOLDER, STATUS, OPERATOR, PLATFORM, RECEPTION, OPERATION_DATE, WM_FLG, CONTRACT_TYPE, RATECARD_FLG, CNTRSTARTDATE,BANK_ACCOUNT_HOLDER,STATUS,CUSTOMER_BIC,MANDATE_STATUS,MANDATE_REF_ID  "
						+ "from " + tableContract + " where  contract_id = '" + contractNumber + "' and mandate_ref_id = '" + mandateNumber + "'";
			} else if ((customerNumber != null && !customerNumber.equals("") && !customerNumber.equals("0")) && (contractNumber == null || contractNumber.equals("") || contractNumber.equals("0"))) {
				sqlQuery = "select CONTRACT_ID, CUSTOMER_ID, HOUSE_NUMBER,  STREET, ZIPCODE, CITY,COUNTRY,ACCOUNT_NUMBER,BANK_CODE, BANK_ACCOUNT_HOLDER, STATUS, OPERATOR, PLATFORM, RECEPTION, OPERATION_DATE, WM_FLG, CONTRACT_TYPE, RATECARD_FLG, CNTRSTARTDATE,BANK_ACCOUNT_HOLDER,STATUS,CUSTOMER_BIC,MANDATE_STATUS,MANDATE_REF_ID  "
						+ "from " + tableContract + " where  customer_id = '" + customerNumber + "'  order by OPERATION_DATE ";
			} else if ((customerNumber == null || customerNumber.equals("") || customerNumber.equals("0")) && (contractNumber == null || contractNumber.equals("") || contractNumber.equals("0"))) {
				SkyLogger.getItyxLogger().info("Enrichment from Contract-Table not possible, neither customerid or contractid provided ");
				return status;
			}
			SkyLogger.getItyxLogger().debug("Query: " + sqlQuery);
			PreparedStatement contractStatement = con.prepareStatement(sqlQuery);
			PreparedStatement wmStatement = con.prepareStatement("select CONTRACT_ID, CUSTOMER_ID, WM_FLG from " + tableContract + " where customer_id= ? and WM_FLG = 'Y'  ");

			int processedcontracts = 0;
			int activecotnracts = 0;

			ResultSet contract = contractStatement.executeQuery();
			while (contract.next()) {
				boolean currentIsActive = false;
				if (contract.getString("STATUS") != null && !contract.getString("STATUS").trim().isEmpty() && contract.getString("STATUS").trim().equalsIgnoreCase("AKTIV")) {
					currentIsActive = true;
					activecotnracts++;
				}
				// first at all (ordered - newest) or first active cotnract
				if (processedcontracts == 0 || (activecotnracts == 1 && currentIsActive)) {
					if (contract.getString("CUSTOMER_ID") != null && !contract.getString("CUSTOMER_ID").isEmpty() && !contract.getString("CUSTOMER_ID").equals("0")) {
						customer.setNumber(contract.getString("CUSTOMER_ID"));
						customerNumber = contract.getString("CUSTOMER_ID");
					}
					customer.setSelectedContractNumber(contract.getString("CONTRACT_ID"));
					customer.setCity(contract.getString("CITY"));
					customer.setStreet(contract.getString("STREET") + " " + contract.getString("HOUSE_NUMBER"));
					customer.setZipCode(contract.getString("ZIPCODE"));
					customer.setCountry(contract.getString("COUNTRY"));
					customer.setOperator(contract.getString("OPERATOR"));
					customer.setPlatform(contract.getString("PLATFORM"));
					customer.setReception(contract.getString("RECEPTION"));
					customer.setContractType(contract.getString("CONTRACT_TYPE"));
					customer.setRateCardFlg(contract.getString("RATECARD_FLG"));

					// SIT-13-09-060, Vertragsdatum
					Date contractDate = contract.getDate("CNTRSTARTDATE");
					if (contractDate != null) {
						customer.setContractDate(contractDate.toString());
						if (contractDate.after(new java.sql.Date(System.currentTimeMillis() - (TIME_1D * 30)))) {
							customer.setNewCustomer(true);
						} else {
							customer.setNewCustomer(false);
						}
					}

					String wmFlag = contract.getString("WM_FLG");
					if (wmFlag == null || wmFlag.trim().isEmpty() || wmFlag.trim().equalsIgnoreCase("N")) {
						wmFlag = "N";
						// checking for derived flaq
						wmStatement.setString(1, customerNumber);
						ResultSet wmQ = wmStatement.executeQuery();
						while (wmQ.next()) {
							wmFlag = "D";
						}
					}
					status = contract.getString("STATUS");
					customer.setWashMachineFlag(wmFlag);
					
					// Adds mandate archiving data.
					//customer.setCustomerIBAN(contract.getString("CUSTOMER_IBAN"));
					customer.setCustomerBIC(contract.getString("CUSTOMER_BIC"));
					customer.setMandateStatus(contract.getString("MANDATE_STATUS"));
					customer.setMandateNumber(contract.getString("MANDATE_REF_ID"));
				}
				processedcontracts++;
			}
		} catch (SQLException e) {
			SkyLogger.getItyxLogger().error("Exception in EnrichmentBean", e);
		}
		return status;
	}

	private void enrichFromAssetTable(IndexingCustomer customer) {
		final String customer_id = customer.getNumber();
		String smartcardNumber = customer.getSMCNumber();
		String contractNumber = customer.getSelectedContractNumber();
		final boolean missingCustomer = customer_id == null || customer_id.equals("") || customer_id.equals("0");
		final boolean missingSmartCard = smartcardNumber == null || smartcardNumber.trim().equals("") || smartcardNumber.equals("0");
		final boolean missingContract = contractNumber == null || contractNumber.trim().equals("") || contractNumber.equals("0");
		try {
			// Tries to enrich contract.
			String sqlQuery = "SELECT nc.CUSTOMER_ID, nc.CONTRACT_ID, nc.OPERATOR, na.SERIAL_NUMBER, na.STATUS, na.OPERATION_DATE FROM " + tableAsset + " na, " + tableContract + " nc " + "WHERE na.CUSTOMER_ID = '" + customer_id
					+ "' AND na.CONTRACT_ID = nc.CONTRACT_ID AND na.CUSTOMER_ID = nc.CUSTOMER_ID AND na.SERIAL_NUMBER = '" + smartcardNumber + "' ORDER BY na.STATUS, na.OPERATION_DATE DESC";
			// Tries to enrich contract.
			if (!missingSmartCard && missingCustomer) {
				sqlQuery = "SELECT nc.CUSTOMER_ID, nc.CONTRACT_ID, nc.OPERATOR, na.SERIAL_NUMBER, na.STATUS, na.OPERATION_DATE FROM " + tableAsset + " na, " + tableContract + " nc "
						+ "WHERE na.CONTRACT_ID = nc.CONTRACT_ID AND na.CUSTOMER_ID = nc.CUSTOMER_ID AND na.SERIAL_NUMBER = '" + smartcardNumber + "' ORDER BY nc.CUSTOMER_ID, nc.CONTRACT_ID, na.STATUS, na.OPERATION_DATE DESC";
				// Tries to enrich contract and SMC.
			} else if (!missingCustomer && missingSmartCard) {
				sqlQuery = "SELECT nc.CUSTOMER_ID, nc.CONTRACT_ID, nc.OPERATOR, na.SERIAL_NUMBER, na.STATUS, na.OPERATION_DATE FROM " + tableAsset + " na, " + tableContract + " nc " + "WHERE na.CUSTOMER_ID = '" + customer_id
						+ "' AND na.CONTRACT_ID = nc.CONTRACT_ID AND na.CUSTOMER_ID = nc.CUSTOMER_ID ORDER BY na.STATUS, na.OPERATION_DATE DESC";
				// Tries to enrich SMC.
			} else if (missingCustomer && missingSmartCard && !missingContract) {
				sqlQuery = "SELECT nc.CUSTOMER_ID, nc.CONTRACT_ID, nc.OPERATOR, na.SERIAL_NUMBER, na.STATUS, na.OPERATION_DATE FROM " + tableAsset + " na, " + tableContract + " nc " + "WHERE na.CONTRACT_ID = '" + contractNumber
						+ "' AND na.CONTRACT_ID = nc.CONTRACT_ID AND na.CUSTOMER_ID = nc.CUSTOMER_ID ORDER BY na.STATUS, na.OPERATION_DATE DESC";
			}

			PreparedStatement contractStatement = con.prepareStatement(sqlQuery);
			ResultSet asset = contractStatement.executeQuery();
			int i = 0;
			while (asset.next()) {
				if (i++ == 0 && (customer.getSelectedContractNumber() == null || customer.getSelectedContractNumber().trim().equals(""))) {
					customer.setSelectedContractNumber(asset.getString("CONTRACT_ID"));
				}
				if (asset.getString("OPERATOR") != null && asset.getString("OPERATOR").equalsIgnoreCase("telekom")) {
					customer.appendToid(asset.getString("SERIAL_NUMBER"));
				}
				if (smartcardNumber == null || smartcardNumber.isEmpty()) {
					customer.setSMCNumber(asset.getString("SERIAL_NUMBER"));
				}
			}
		} catch (SQLException e) {
			SkyLogger.getItyxLogger().error(customer_id + " Exception in EnrichmentBean", e);
		}
	}

	private void enrichFromCampaignTable(IndexingCustomer customer) {
		String campaignString = "";
		try {

			String sqlQuery = "select campaign_type from newdb_campaign_conf ccf where end_date > sysdate and start_date < sysdate and status='AKTIV' and exists ("
					+ "select customer_id from newdb_campaign ca where ca.campaign_id=ccf.campaign_id and ca.customer_id='" + customer.getNumber() + "' ) group by campaign_type ";
			ResultSet campaign = null;
			try {
				PreparedStatement contractStatement = con.prepareStatement(sqlQuery);
				campaign = contractStatement.executeQuery();
				while (campaign.next()) {
					final String campaignColumn = campaign.getString("campaign_type");
					if (!campaign.wasNull() && campaignColumn != null && !campaignColumn.equalsIgnoreCase("null") && !campaignColumn.trim().isEmpty() ) {
						campaignString += ((campaignString.length() > 1) ? " , " : "") + campaignColumn;
					}
				}
			} finally {
				if (campaign != null) {
					campaign.close();
				}
			}
		} catch (SQLException e) {
			SkyLogger.getItyxLogger().error(customer.getNumber() + " Exception in EnrichmentBean", e);
		}
		if (!campaignString.isEmpty()) {
			customer.setCampaignTypes(campaignString);
		}
	}

	private void enrichFromCustomerTable(IndexingCustomer customer) throws Exception {
		// search for customer
		ResultSet rs = null;
		try {
			String sqlQuery = "select ROW_ID, CUSTOMER_ID, MOBILE_NUMBER, EMAIL_ADDRESS,  FIRST_NAME, LAST_NAME, TELEPHONE_NUMBER, STATUS, OPERATION_DATE from " + tableCustomer + " where customer_id = '" + customer.getNumber()
					+ "' ORDER BY OPERATION_DATE DESC";
			PreparedStatement ps = con.prepareStatement(sqlQuery);

			SkyLogger.getItyxLogger().debug("Query: " + sqlQuery);
			rs = ps.executeQuery();
			if (rs.next()) {
				customer.setNumber(rs.getString("CUSTOMER_ID"));
				customer.setFirstName(rs.getString("FIRST_NAME"));
				customer.setLastName(rs.getString("LAST_NAME"));
				customer.setRowId(rs.getString("ROW_ID"));
			}
		} catch (SQLException e) {
			throw new Exception(customer.getNumber() + " Exception in EnrichmentBean", e);
		} finally {
			if (rs != null) {
				rs.close();
			}
		}
	}

	private void enrichFromIF33FAAWS(IndexingCustomer customer, String itemid) throws Exception {
		IFAA faa = ConnectorFactory.getFAAInstance();
		RoutingCustomer rCustomer = faa.queryFAACustomerData(customer, itemid);
		if (rCustomer!=null){
			customer.setContactInterval3D(rCustomer.getContactInterval3D());
			customer.setContactInterval7D(rCustomer.getContactInterval7D());
			customer.setContactInterval14D(rCustomer.getContactInterval14D());
			customer.setContactInterval21D(rCustomer.getContactInterval21D());
			customer.setContactInterval28D(rCustomer.getContactInterval28D());
		} else {
			SkyLogger.getItyxLogger().info("IF3.3 FAA: SkippingQuerry: Customernumber or SMC not provided"+ itemid);
		}
	}


	private Map<String, Integer> enrichFromIF32NewDBWS(IndexingCustomer customer) throws Exception {
		/*
		 * Query NewDB
		 */
		INewDB newDb = ConnectorFactory.getNewDBInstance();

		Map<String, Integer> intervalCounts = new TreeMap<>();
		intervalCounts.put(TagMatchDefinitions.CONTACT_INTERVAL_7D, 0);
		intervalCounts.put(TagMatchDefinitions.CONTACT_INTERVAL_14D, 0);
		intervalCounts.put(TagMatchDefinitions.CONTACT_INTERVAL_21D, 0);
		intervalCounts.put(TagMatchDefinitions.CONTACT_INTERVAL_28D, 0);

		String customerNumber = customer.getNumber();
		String contractNumber = customer.getSelectedContractNumber();
		// Adam:
		// System.out.println("EnrichDocumentBean.enrichFromIF32NewDBWS/customerNumber,contractNumber: "+customerNumber+","+contractNumber);
		SkyLogger.getItyxLogger().debug(customerNumber + " NewDB-Query: CustomerNumer" + customerNumber + ", ContractNumber:" + contractNumber);
		RoutingCustomer rCustomer = newDb.queryRoutingCustomer(customerNumber, contractNumber);

		if (rCustomer == null) {
			throw new Exception(customerNumber + " Customer returned by service is null - this happens when there are entries in FuzzyDB that are not in NewDB anymore.");
		} else {
			//SkyLogger.getItyxLogger().debug(customerNumber + " NewDB-Query: " + rCustomer.toString());
			customer.setCampaignStamp(rCustomer.getCampaignStamp());
			if (contractNumber == null)
				customer.setSelectedContractNumber(rCustomer.getSelectedContractNumber());
			customer.setCategory(rCustomer.getCategory());
			customer.setContractSkyGo(rCustomer.getContractSkyGo());
			customer.setDunningLevel(rCustomer.getDunningLevel());
			customer.setEarmarkedCancelationDate(rCustomer.getEarmarkedCancelationDate());
			customer.setPricelist(rCustomer.getPricelist());
			customer.setSrContractChange(rCustomer.getSrContractChange());
			customer.setSrContractChangeDate(rCustomer.getSrContractChangeDate());
			customer.setSubscriptionStartDate(rCustomer.getSubscriptionStartDate());

			// former implementation of SIT-13-09-060
			// String subscribtionStartDateStr =
			// rCustomer.getSubscriptionStartDate();
			//
			// if (subscribtionStartDateStr != null &&
			// subscribtionStartDateStr.matches("\\d{4}\\-\\d{2}\\-\\d{2}")) {
			// java.util.Date subscribtionStartDate = null;
			// if (subscribtionStartDateStr.matches("\\d{4}\\-\\d{2}\\-\\d{2}"))
			// {
			// subscribtionStartDate = new
			// SimpleDateFormat("yyyy-MM-dd").parse(subscribtionStartDateStr);
			// } else {
			// SkyLogger.getItyxLogger().warn("EnrichDocumentBean:" +
			// customerNumber + " CannotParseDate:" + subscribtionStartDateStr);
			// }
			// if (subscribtionStartDate != null &&
			// subscribtionStartDate.after(new
			// java.util.Date(System.currentTimeMillis() - (TIME_1D * 7 * 8))))
			// {
			// customer.setNewCustomer(true);
			// } else {
			// customer.setNewCustomer(false);
			// }
			// }

			customer.setSubscriptionStartDateBeforeLimit(rCustomer.isSubscriptionStartDateBeforeLimit());

			if (rCustomer.getSMCNumber() != null && !rCustomer.getSMCNumber().isEmpty()) {
				customer.setSMCNumber(rCustomer.getSMCNumber());
			}
			String wscampaigntypes = rCustomer.getCampaignTypes();
			if (wscampaigntypes != null && !wscampaigntypes.isEmpty()) {
				customer.setCampaignTypes(rCustomer.getCampaignTypes());
			}
			String wsflag = rCustomer.getWashMachineFlag();
			String currFlag = customer.getWashMachineFlag();

			if (wsflag != null && !wsflag.isEmpty() && (currFlag == null || !currFlag.equals("D"))) {
				customer.setWashMachineFlag(wsflag);
			}
			return intervalCounts;
		}
	}

	public void execute(IFlowObject flowObject) throws Exception {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		ShedulerUtils.checkAuth();
		
		String verifiedCustomerNumber = FlowUtils.getOptionalString(flowObject, "VerifiedCustomerNumber", null);
		if ("".equals(verifiedCustomerNumber))
			verifiedCustomerNumber = null;
                String verifiedSmartcardNumber = FlowUtils.getOptionalString(flowObject, "VerifiedSmartcardNumber", null);
		if ("".equals(verifiedSmartcardNumber))
			verifiedSmartcardNumber = null;
		String verifiedContractNumber = FlowUtils.getOptionalString(flowObject, "VerifiedContractNumber", null);
		if ("".equals(verifiedContractNumber))
			verifiedContractNumber = null;
		String verifiedMandateNumber = FlowUtils.getOptionalString(flowObject, "VerifiedMandateNumber", null);
		if ("".equals(verifiedMandateNumber))
			verifiedMandateNumber = null;

		String ctx_docid = DocContainerUtils.getDocID(flowObject);
		
		String logPrefix = clazz.getName() + "#" + name + " d:" + ctx_docid + " c:" + verifiedCustomerNumber + ":";
		SkyLogger.getItyxLogger().debug(logPrefix + ": enter");

		Boolean serviceActive = FlowUtils.getOptionalBoolean(flowObject, "DynamicCustomerData_Enabled", false);
		Boolean faaActive = FlowUtils.getOptionalBoolean(flowObject, "CustomerServiceFAA_Enabled", false);
		String serviceUrl = FlowUtils.getRequiredNonEmptyString(flowObject, "DynamicCustomerData_WSDL");
		String customerServiceUrl = FlowUtils.getRequiredNonEmptyString(flowObject, "CustomerServiceFAA_WSDL");

		SkyLogger.getItyxLogger().debug(
				verifiedCustomerNumber + " Enrich document " + ctx_docid + " for customer with customer number " + verifiedCustomerNumber + ", contract number " + verifiedContractNumber + " and mandate number " + verifiedMandateNumber);

		if ((verifiedCustomerNumber == null && verifiedContractNumber == null ) || "0".equals(verifiedCustomerNumber)) {
			SkyLogger.getItyxLogger().debug(logPrefix + verifiedCustomerNumber + " No number provided. Setting empty customer");
			writeCustomerToFlow(flowObject, createEmptyCustomer(""), "");
			return;
		}

		if (serviceActive) {
			try {
				System.setProperty(NewDBConnectorImpl.SysProperty_CustomerDataService_Endpoint, serviceUrl);
				SkyLogger.getItyxLogger().debug(logPrefix + verifiedCustomerNumber + " Using CustomerDataService at: " + serviceUrl);

				/*
				 * Determine the database connection for enrichment
				 */
				if (simDB) {
					// Wenn das ein Unit-Test ist, wurde die Datenbank bereits
					// durch
					// den NewDBServiceProvider geladen
					con = DriverManager.getConnection("jdbc:hsqldb:mem:.", "SA", "");
				} else {
					con = ContexDbConnector.getAutoCommitConnection();
				}

				/*
				 * Lookup customer data
				 */
				IndexingCustomer customer = new IndexingCustomer();
				customer.setNumber(verifiedCustomerNumber);
				customer.setSelectedContractNumber(verifiedContractNumber);
				customer.setMandateNumber(verifiedMandateNumber);
                                customer.setSMCNumber(verifiedSmartcardNumber);

                                SkyLogger.getItyxLogger().debug(logPrefix + ": Enrich using smartcard start: " + verifiedSmartcardNumber + " customer:" + customer.getNumber() + " contract:" + customer.getSelectedContractNumber());
				enrichFromAssetTable(customer);

				SkyLogger.getItyxLogger().debug(logPrefix + ": enrich using newdb_contract: customer:" + customer.getNumber() + " contract:" + customer.getSelectedContractNumber());// + " dump:" + customer.toString());
				String isActive = enrichFromContractTable(customer);
				SkyLogger.getItyxLogger().debug(logPrefix + ": enrich using newdb_customer: customer:" + customer.getNumber() + " contract:" + customer.getSelectedContractNumber());// + " dump:" + customer.toString());
				enrichFromCustomerTable(customer);
				SkyLogger.getItyxLogger().debug(logPrefix + ": enrich using newdb_campaign: customer:" + customer.getNumber() + " contract:" + customer.getSelectedContractNumber());// + " dump:" + customer.toString());
				enrichFromCampaignTable(customer);
				SkyLogger.getItyxLogger().debug(logPrefix + ": enrich using newdb_contact: customer:" + customer.getNumber() + " dump:" + customer.toString());
				enrichFromIF32NewDBWS(customer);
				SkyLogger.getItyxLogger().debug(logPrefix + ": afterNewDB: customer:  customer:" + customer.getNumber() + " contract:" + customer.getSelectedContractNumber());// + " dump:" + customer.toString());

				if (FlowUtils.getOptionalBoolean(flowObject, "CustomerServiceFAA_Enabled", true)) {
					System.setProperty(FAAConnectorImpl.SysProperty_CustomerServiceFAA_Endpoint, customerServiceUrl);
					System.setProperty(FAAConnectorImpl.SysProperty_CustomerServiceFAA_Timeout, (FlowUtils.getOptionalInt(flowObject, "CustomerServiceFAA_Timeout", 25) * 1000) + "");
					if (faaActive) {
						SkyLogger.getItyxLogger().debug(logPrefix + verifiedCustomerNumber + " Using CustomerServiceFAA at: " + customerServiceUrl);
						enrichFromIF33FAAWS(customer,ctx_docid);
						SkyLogger.getItyxLogger().debug(logPrefix + ": afterFAA: customer:  customer:" + customer.getNumber() + " contract:" + customer.getSelectedContractNumber() );//+ " dump:" + customer.toString());
					}
				} else {
					SkyLogger.getItyxLogger().warn("Enrichment: IF 3.3 FAA disabled. Skiping IF 3.3 query for custnr:" + customer.getNumber() + " smc:" + customer.getSMCNumber());
				}

				writeCustomerToFlow(flowObject, customer, isActive);

			} finally {
				if (con != null) {
					con.close();
				}
			}
		} else {
			writeCustomerToFlow(flowObject, createEmptyCustomer("SERVICE_OFF"), "");
		}
	}

	protected static final String sblDateFormatPattern = "MM/dd/yyyy"; //05/08/2015
	private static synchronized String getSblFormattedDate(java.util.Date tsdate) {
		if (tsdate==null){
			return null;
		}
		try {
			return (new SimpleDateFormat(sblDateFormatPattern)).format(tsdate);
		}catch (Exception e){
			return null;
		}
	}
	private static synchronized java.util.Date parseSblFormatedDate(String date){
		if (date==null){
			return null;
		}
		try {
			return	(new SimpleDateFormat(sblDateFormatPattern)).parse(date);
		} catch (ParseException e) {
			SkyLogger.getItyxLogger().error("Problem during Parsing ArchiveDate: " + date, e);
			return null;
		}
	}


	protected static final String mxDateFormatPattern = "yyyy.MM.dd"; //05/08/2015
	private static synchronized String getMxFormatedDate(java.util.Date tsdate) {
		if (tsdate==null){
			return null;
		}
		return (new SimpleDateFormat(mxDateFormatPattern)).format(tsdate);
	}

	private static synchronized java.util.Date parseMXFormatedDate(String date){
		if (date==null){
			return null;
		}
		try {
			return	(new SimpleDateFormat(mxDateFormatPattern)).parse(date);
		} catch (ParseException e) {
			SkyLogger.getItyxLogger().error("Problem during Parsing ArchiveDate: " + date, e);
			return null;
		}
	}
}
