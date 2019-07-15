package com.nttdata.de.ityx.cx.sky.enrichment;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;
import com.nttdata.de.ityx.cx.workflow.base.FlowUtils;
import com.nttdata.de.ityx.sharedservices.utils.ContexDbConnector;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.connector.ConnectorFactory;
import com.nttdata.de.sky.connector.INewDB;
import com.nttdata.de.sky.connector.data.IndexingCustomer;
import com.nttdata.de.sky.connector.data.RoutingCustomer;
import com.nttdata.de.sky.connector.newdb.NewDBConnectorImpl;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.contex.impl.designer.exflow.maps.TextParameter;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.designer.IParameterMap;
import de.ityx.contex.interfaces.document.CDocument;
import de.ityx.contex.interfaces.extag.TagMatch;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
public class EnrichSEPAMandateBean extends AbstractWflBean {

	// added only for unit tests (ugly...)
	public boolean					simDB				= false;

	private transient Connection	con					= null;
	private String					tableCustomer		= "newdb_customer";
	private String					tableContract		= "newdb_contract";
	private String					tableAsset			= "newdb_asset";
	private String					tableCampaign		= "newdb_campaign";
	private String					tableCampaignConf	= "newdb_campaign_conf";

	private IndexingCustomer createEmptyCustomer(String defaultValue) {
		IndexingCustomer customer = new IndexingCustomer();
		customer.setCampaignStamp(defaultValue);
		customer.setCategory(defaultValue);
		customer.setCity(defaultValue);
		customer.setContractSkyGo(defaultValue);
		customer.setCountry(defaultValue);
		customer.setDunningLevel(defaultValue);
		customer.setEarmarkedCancelationDate(defaultValue);
		customer.setCustomerContractQuantity(0);
		customer.setActiveContractQuantity(0);
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
		customer.setMandateNumber(defaultValue);
		return customer;
	}

	private void writeCustomerToFlow(IFlowObject flowObject, IndexingCustomer customer) throws Exception {

		SkyLogger.getItyxLogger().debug("writeCustomerToFlow");
		List<TagMatch> list = new ArrayList<>();

		String docId = "noDocID";
		CDocumentContainer<CDocument> docContainer = (CDocumentContainer<CDocument>) flowObject.get(DocContainerUtils.DOC);

		final boolean noDoc = docContainer == null;
		if (!noDoc) {
			CDocument document = docContainer.getDocument(0);
			docId = (String) document.getNote(TagMatchDefinitions.DOCUMENT_ID);
		}

		String customerNumber = customer.getNumber();
		list.add(new TagMatch(TagMatchDefinitions.CUSTOMER_ID, customerNumber));
		String contractNumber = customer.getSelectedContractNumber();
		SkyLogger.getItyxLogger().debug(docId + " Contract Number: " + contractNumber);
		if (contractNumber != null) {
			list.add(new TagMatch(TagMatchDefinitions.TAGMATCH_CONTRACT_NUMBER, contractNumber));
		}
		String mandateNumber = customer.getMandateNumber();
		SkyLogger.getItyxLogger().debug(docId + " MandateRefId: " + mandateNumber);
		if (mandateNumber != null) {
			list.add(new TagMatch(TagMatchDefinitions.SEPA_MANDATE_NUMBER, mandateNumber));
		}

		list.add(new TagMatch(TagMatchDefinitions.SIEBEL_CUSTOMER_ID, customer.getRowId()));
		list.add(new TagMatch(TagMatchDefinitions.CUSTOMER_FIRST_NAME, customer.getFirstName()));
		list.add(new TagMatch(TagMatchDefinitions.CUSTOMER_LAST_NAME, customer.getLastName()));
		list.add(new TagMatch(TagMatchDefinitions.CUSTOMER_STREET, customer.getStreet()));
		list.add(new TagMatch(TagMatchDefinitions.CUSTOMER_ZIP_CODE, customer.getZipCode()));
		list.add(new TagMatch(TagMatchDefinitions.CUSTOMER_CITY, customer.getCity()));
		list.add(new TagMatch(TagMatchDefinitions.SEPA_STATUS, customer.getMandateStatus()));
		//list.add(new TagMatch(TagMatchDefinitions.IBAN, customer.getCustomerIBAN()));
		list.add(new TagMatch(TagMatchDefinitions.BIC, customer.getCustomerBIC()));

		String country = customer.getCountry();
		if (country != null && country.trim().length() > 0) {
			list.add(new TagMatch(TagMatchDefinitions.CUSTOMER_COUNTRY, country));
		}

		// set output values
		if (noDoc) {
			IParameterMap outputMap = flowObject.getOutputMap("outputMap");
			for (TagMatch tm : list) {
				String identifier = tm.getIdentifier();
				String value = tm.getTagValue();
				outputMap.replaceParameter(identifier, identifier, new TextParameter(value));
			}
		} else {
			docContainer.setTags(new ArrayList<TagMatch>());

			SkyLogger.getItyxLogger().debug(docId + " Enrichment of document");

			if (noDoc || docContainer.getDocument(0) == null) {
				throw new Exception("Trying to enrich Document, but the document cannot be found in FlowContext.");
			}
			CDocument document = docContainer.getDocument(0);

			// Checks if there is a valid formtype assigned to the document.
			String val = document.getFormtype();
			String manualDoctype = (String) document.getNote(TagMatchDefinitions.MANUAL_FORMTYPE);
			if (manualDoctype == null || manualDoctype.equals("")) {
				final List<TagMatch> tags = document.getTags();
				if (tags.size() > 0) {
					manualDoctype = tags.get(0).getTagValue(TagMatchDefinitions.MANUAL_FORMTYPE);
				}
			}
			list.add(new TagMatch(TagMatchDefinitions.MANUAL_FORMTYPE, manualDoctype));

			if ((manualDoctype != null && !manualDoctype.isEmpty()) && (!manualDoctype.equalsIgnoreCase(val) || val == null || val.isEmpty() || val.equals("systemdefault")|| val.equals("unclassified") || val.equals("null"))) {
				val = manualDoctype;
				DocContainerUtils.setFormtype(docContainer, document, manualDoctype);
				SkyLogger.getItyxLogger().debug(docId + " Setting manual Formtype <" + val + "> ");
			}
			
			String manualQID = (String) document.getNote(TagMatchDefinitions.DMS_MANUAL_INDEXING_QID);
			if (manualQID == null || manualQID.equals("")) {
				final List<TagMatch> tags = document.getTags();
				if (tags.size() > 0) {
					manualQID = tags.get(0).getTagValue(TagMatchDefinitions.DMS_MANUAL_INDEXING_QID);
				}
			}
			list.add(new TagMatch(TagMatchDefinitions.DMS_MANUAL_INDEXING_QID, manualQID));
			
			SkyLogger.getItyxLogger().debug(docId + " Setting document tag matches");
			document.setTags(list);

			SkyLogger.getItyxLogger().debug(docId + " Setting document notes [auto] (for evaluation/log entry)");

			document.setNote(TagMatchDefinitions.EVAL_FORMTYPE, val);
			SkyLogger.getItyxLogger().debug(docId + " " + TagMatchDefinitions.EVAL_FORMTYPE + ": " + val);

			val = customerNumber;
			document.setNote(TagMatchDefinitions.EVAL_CUSTOMER_NUMBER, val);
			SkyLogger.getItyxLogger().debug(docId + " " + TagMatchDefinitions.EVAL_CUSTOMER_NUMBER + ": " + val);

			val = contractNumber != null ? contractNumber : "";
			document.setNote(TagMatchDefinitions.EVAL_CONTRACT_NUMBER, val);
			SkyLogger.getItyxLogger().debug(docId + " " + TagMatchDefinitions.EVAL_CONTRACT_NUMBER + ": " + val);

			val = mandateNumber != null ? mandateNumber : "";
			document.setNote(TagMatchDefinitions.SEPA_MANDATE_NUMBER, val);
			SkyLogger.getItyxLogger().debug(docId + " " + TagMatchDefinitions.SEPA_MANDATE_NUMBER + ": " + val);

			SkyLogger.getItyxLogger().debug(docId + " Setting channel in flow");
			Object channel = document.getNote(TagMatchDefinitions.CHANNEL);
			flowObject.put(TagMatchDefinitions.CHANNEL, channel != null ? channel : "");

		}
	}

	public void enrichFromContractTable(IndexingCustomer customer) {
		try {
			String customerNumber = (customer.getNumber() == null) ? null : customer.getNumber().trim();
			String contractNumber = (customer.getSelectedContractNumber() == null) ? null : customer.getSelectedContractNumber().trim();
			String mandateNumber = (customer.getMandateNumber() == null) ? null : customer.getMandateNumber().trim();
			if (mandateNumber == null) {
				SkyLogger.getItyxLogger().warn("No MandateRefId provided for Mandate document.");
			}
			String sqlQuery = "select CONTRACT_ID,CUSTOMER_ID,HOUSE_NUMBER, STREET,ZIPCODE,CITY,COUNTRY,BANK_ACCOUNT_HOLDER,STATUS,CUSTOMER_BIC,MANDATE_STATUS,MANDATE_REF_ID " + "from " + tableContract
					+ " where customer_id = '" + customerNumber + "' and contract_id = '" + contractNumber + "' and mandate_ref_id = '" + mandateNumber + "'";
			if ((customerNumber == null || customerNumber.equals("") || customerNumber.equals("0")) && (contractNumber != null && !contractNumber.equals("") && !contractNumber.equals("0"))) {
				sqlQuery = "select CONTRACT_ID, CUSTOMER_ID,HOUSE_NUMBER,STREET,ZIPCODE,CITY,COUNTRY,BANK_ACCOUNT_HOLDER,STATUS,CUSTOMER_BIC,MANDATE_STATUS,MANDATE_REF_ID " + "from " + tableContract
						+ " where  contract_id = '" + contractNumber + "' and mandate_ref_id = '" + mandateNumber + "'";
			} else if ((customerNumber != null && !customerNumber.equals("") && !customerNumber.equals("0")) && (contractNumber == null || contractNumber.equals("") || contractNumber.equals("0"))) {
				sqlQuery = "select CONTRACT_ID,CUSTOMER_ID,HOUSE_NUMBER,STREET,ZIPCODE,CITY,COUNTRY,BANK_ACCOUNT_HOLDER,STATUS,CUSTOMER_BIC,MANDATE_STATUS,MANDATE_REF_ID  " + "from " + tableContract
						+ " where  customer_id = '" + customerNumber + "' and mandate_ref_id = '" + mandateNumber + "'" + "' order by OPERATION_DATE";
			} else if ((customerNumber == null || customerNumber.equals("") || customerNumber.equals("0")) && (contractNumber == null || contractNumber.equals("") || contractNumber.equals("0"))) {
				SkyLogger.getItyxLogger().info("Enrichment from Contract-Table not possible, neither customerid or contractid provided ");
				return;
			}
			SkyLogger.getItyxLogger().debug("Query: " + sqlQuery);
			PreparedStatement contractStatement = con.prepareStatement(sqlQuery);

			boolean primaryContractIdentified = false;
			ResultSet contract = contractStatement.executeQuery();
			while (contract.next()) {
				customer.setNumber(contract.getString("CUSTOMER_ID"));
				customer.setSelectedContractNumber(contract.getString("CONTRACT_ID"));
				customer.setCity(contract.getString("CITY"));
				customer.setStreet(contract.getString("STREET") + " " + contract.getString("HOUSE_NUMBER"));
				customer.setZipCode(contract.getString("ZIPCODE"));
				customer.setCountry(contract.getString("COUNTRY"));
				// Adds mandate archiving data.
				//customer.setCustomerIBAN(contract.getString("CUSTOMER_IBAN"));
				customer.setCustomerBIC(contract.getString("CUSTOMER_BIC"));
				customer.setMandateStatus(contract.getString("MANDATE_STATUS"));
				customer.setMandateNumber(contract.getString("MANDATE_REF_ID"));
				if (contractNumber != null && contractNumber.equals(contract.getString("CONTRACT_ID"))) {
					primaryContractIdentified = true;
					break;
				}
			}
		} catch (SQLException e) {
			SkyLogger.getItyxLogger().error("Exception in EnrichmentBean", e);
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

	private void enrichFromIF32NewDBWS(IndexingCustomer customer) throws Exception {
		/*
		 * Query NewDB
		 */
		INewDB newDb = ConnectorFactory.getNewDBInstance();

		String customerNumber = customer.getNumber();
		String contractNumber = customer.getSelectedContractNumber();
		// Adam:
		// System.out.println("EnrichDocumentBean.enrichFromIF32NewDBWS/customerNumber,contractNumber: "+customerNumber+","+contractNumber);
		SkyLogger.getItyxLogger().debug(customerNumber + " NewDB-Query: CustomerNumer" + customerNumber + ", ContractNumber:" + contractNumber);
		RoutingCustomer rCustomer = newDb.queryRoutingCustomer(customerNumber, contractNumber);

		if (rCustomer == null) {
			throw new Exception(customerNumber + " Customer returned by service is null - this happens when there are entries in FuzzyDB that are not in NewDB anymore.");
		} else {
			SkyLogger.getItyxLogger().debug(customerNumber + " NewDB-Query: " + rCustomer.toString());
			customer.setCampaignStamp(rCustomer.getCampaignStamp());
			if (contractNumber == null) {
				customer.setSelectedContractNumber(rCustomer.getSelectedContractNumber());
			}
			if (customer.getMandateNumber() == null) {
				customer.setMandateNumber(rCustomer.getMandateNumber());
				customer.setMandateStatus(rCustomer.getMandateStatus());
				customer.setCustomerBIC(rCustomer.getCustomerBIC());
				//customer.setCustomerIBAN(rCustomer.getCustomerIBAN());
			}
		}
	}

	public void execute(IFlowObject flowObject) throws Exception {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();

		// This bean expects one of the following attributes to be set:
		String verifiedCustomerNumber = FlowUtils.getOptionalString(flowObject, "VerifiedCustomerNumber", null);
		if ("".equals(verifiedCustomerNumber))
			verifiedCustomerNumber = null;
		String verifiedContractNumber = FlowUtils.getOptionalString(flowObject, "VerifiedContractNumber", null);
		if ("".equals(verifiedContractNumber))
			verifiedContractNumber = null;
		String verifiedMandateNumber = FlowUtils.getOptionalString(flowObject, "VerifiedMandateNumber", null);
		if ("".equals(verifiedMandateNumber))
			verifiedMandateNumber = null;

		String ctx_docid = "";
		Object object = flowObject.get(DocContainerUtils.DOC);
		if (object != null && object instanceof CDocumentContainer) {
			CDocumentContainer cont = (CDocumentContainer) object;
			if (cont.getDocuments().size() > 0 && cont.getDocument(cont.size() - 1) != null) {
				CDocument cdoc = cont.getDocument(cont.size() - 1);
				if (cdoc != null) {
					ctx_docid = (String) cdoc.getNote(TagMatchDefinitions.DOCUMENT_ID);
				}
			}
		}

		String logPrefix = clazz.getName() + "#" + name + " d:" + ctx_docid + " c:" + verifiedCustomerNumber + ":";
		SkyLogger.getItyxLogger().debug(logPrefix + ": enter");

		Boolean serviceActive = FlowUtils.getOptionalBoolean(flowObject, "DynamicCustomerData_Enabled", false);
		String serviceUrl = FlowUtils.getRequiredNonEmptyString(flowObject, "DynamicCustomerData_WSDL");

		SkyLogger.getItyxLogger().debug(verifiedCustomerNumber + " Enrich document " + ctx_docid + " for customer with customer number " + verifiedCustomerNumber + ", contract number " + verifiedContractNumber);

		if (!(verifiedMandateNumber != null || verifiedContractNumber != null) || "0".equals(verifiedCustomerNumber)) {
			SkyLogger.getItyxLogger().debug(logPrefix + verifiedCustomerNumber + " No number provided. Setting empty customer");
			writeCustomerToFlow(flowObject, createEmptyCustomer(""));
			return;
		}

		if (serviceActive) {
			try {
				System.setProperty(NewDBConnectorImpl.SysProperty_CustomerDataService_Endpoint, serviceUrl);
				SkyLogger.getItyxLogger().debug(logPrefix + verifiedCustomerNumber + " Using service at: " + serviceUrl);

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
				SkyLogger.getItyxLogger().debug(logPrefix + ": enrich using newdb_contract: customer:" + customer.getNumber() + " contract:" + customer.getSelectedContractNumber());// +
																																														// " dump:"
																																														// +
																																														// customer.toString());
				enrichFromContractTable(customer);
				SkyLogger.getItyxLogger().debug(logPrefix + ": enrich using newdb_customer: customer:" + customer.getNumber() + " contract:" + customer.getSelectedContractNumber());// +
																																														// " dump:"
																																														// +
																																														// customer.toString());
				enrichFromCustomerTable(customer);
				SkyLogger.getItyxLogger().debug(logPrefix + ": enrich using NewDB WS: customer:" + customer.getNumber() + " contract:" + customer.getSelectedContractNumber());// +
																																												// " dump:"
																																												// +
																																												// customer.toString());
				enrichFromIF32NewDBWS(customer);
				SkyLogger.getItyxLogger().debug(logPrefix + ": afterNewDB: customer:  customer:" + customer.getNumber() + " contract:" + customer.getSelectedContractNumber());// +
																																												// " dump:"
																																												// +
																																												// customer.toString());

				writeCustomerToFlow(flowObject, customer);

			} finally {
				if (con != null) {
					con.close();
				}
			}
		} else {
			writeCustomerToFlow(flowObject, createEmptyCustomer("SERVICE_OFF"));
		}
	}


}
