package com.nttdata.de.ityx.sharedservices.enrichment.custattrcollectors;

import com.nttdata.de.ityx.sharedservices.configuration.BeanConfig;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.connector.ConnectorFactory;
import com.nttdata.de.sky.connector.INewDB;
import com.nttdata.de.sky.connector.data.RoutingCustomer;
import com.nttdata.de.sky.connector.newdb.NewDBConnectorImpl;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class IF32_NewDB_CustAttrCollector extends AbstractCustomerAttributesCollector {

	@Override
	public Map<String, String> collectCustomerAttributes(String documentid, String customerid, Set<String> contractids, Set<String> serialNr, Set<String> mandates) throws Exception {

		Map<String, String> result = new TreeMap<>();
		if(!isEnabled()){
			SkyLogger.getConnectorLogger().warn("IF32_NewDB_CustAttrCollector:DISABLED:" + documentid + " Query: CustomerNumer:" + customerid + ", ContractNumber:" + StringUtils.join(contractids, ",")+ ", SerialNumber:" + StringUtils.join(serialNr, ",") );
			return result;
		}

		SkyLogger.getConnectorLogger().info("IF32_NewDB_CustAttrCollector:" + documentid + " Query: CustomerNumer" + customerid + ", ContractNumber:" + StringUtils.join(contractids, ","));
		INewDB newDb = ConnectorFactory.getNewDBInstance();
		boolean custfound = false;

		for (String contract : contractids) {
			if (!custfound) {
				RoutingCustomer rCustomer = newDb.queryRoutingCustomer(customerid, contract);
				if (rCustomer != null) {
					custfound = true;
					putIfNotEmpty(result, TagMatchDefinitions.CONTACT_ID, rCustomer.getSelectedContractNumber());
					putIfNotEmpty(result, TagMatchDefinitions.META_CUSTOMER_CATEGORY, rCustomer.getCategory());
					putIfNotEmpty(result, TagMatchDefinitions.META_SKY_GO, rCustomer.getContractSkyGo());
					putIfNotEmpty(result, TagMatchDefinitions.META_DUNNING_LEVEL, rCustomer.getDunningLevel());
					putIfNotEmpty(result, TagMatchDefinitions.META_EARMARKED_CANCELATION_DATE, rCustomer.getEarmarkedCancelationDate());
					putIfNotEmpty(result, TagMatchDefinitions.META_PRICELIST, rCustomer.getPricelist());
					putIfNotEmpty(result, TagMatchDefinitions.META_SR_CONTRACT_CHANGE, rCustomer.getSrContractChange());
					putIfNotEmpty(result, TagMatchDefinitions.META_SR_CONTRACT_CHANGE_DATE, rCustomer.getSrContractChangeDate());
					putIfNotEmpty(result, TagMatchDefinitions.META_SUBSCRIPTION_START_DATE, rCustomer.getSubscriptionStartDate());
					putIfNotEmpty(result, TagMatchDefinitions.META_SUBSCRIPTION_START_DATE_BEFORE_LIMIT, rCustomer.isSubscriptionStartDateBeforeLimit() ? "Y" : "N");
					putIfNotEmpty(result, TagMatchDefinitions.TAGMATCH_SMARTCARD_NUMBER, rCustomer.getSMCNumber());
					putIfNotEmpty(result, TagMatchDefinitions.META_CAMPAIGN_STAMP, rCustomer.getCampaignStamp());
					putIfNotEmpty(result, TagMatchDefinitions.META_CAMPAIGN_TYPE, rCustomer.getCampaignTypes());
					putIfNotEmpty(result, TagMatchDefinitions.META_WASHING_MACHINE, rCustomer.getWashMachineFlag());
					putIfNotEmpty(result, TagMatchDefinitions.SEPA_STATUS, rCustomer.getMandateStatus());
					putIfNotEmpty(result, TagMatchDefinitions.SEPA_MANDATE_NUMBER, rCustomer.getMandateNumber());
					putIfNotEmpty(result, TagMatchDefinitions.META_POSSIBLE_CANCELATION_DATE, getMxFormatedDate(rCustomer.getPossibleCancellationDate()));
					putIfNotEmpty(result, TagMatchDefinitions.META_CANCELATION_DATE, getMxFormatedDate(rCustomer.getCancellationDate()));
				}
			}
		}

		if (!custfound) {
			SkyLogger.getConnectorLogger().error("IF32_NewDB_CustAttrCollector: customer not found:" + documentid + " Query: CustomerNumer" + customerid + ", ContractNumber:" + StringUtils.join(contractids, ","));
			throw new Exception(customerid + " Customer returned by service is null - this happens when there are entries in FuzzyDB that are not in NewDB anymore.");
		}
		return result;
	}

	private boolean active = true;

	@Override
	public void configure() {
		String endpoint=null;
		active = BeanConfig.getBoolean("DynamicCustomerData_Enabled", true);
		try {
			endpoint = BeanConfig.getReqString("DynamicCustomerData_WSDL");
		} catch (Exception e) {
			SkyLogger.getConnectorLogger().error("IF32_NEWDB_ENDPOINT: is not set, the IF33 would be disabled. Please check the value of:CustomerServiceFAA_WSDL");
			active=false;
		}
		if (!active) {
			SkyLogger.getConnectorLogger().warn("IF32_NEWDB_DISABLED");
		}
		if (endpoint!=null && isNotEmpty(endpoint)) {
			System.setProperty(NewDBConnectorImpl.SysProperty_CustomerDataService_Endpoint, endpoint);
			SkyLogger.getConnectorLogger().info("IF32_NEWDB_ENDPOINT: " + endpoint);
		} else {
			SkyLogger.getConnectorLogger().warn("IF32_NEWDB_ENDPOINT NOT SET: DISABLED ");
			active=false;
		}
	}


	protected static final String mxDateFormatPattern = "yyyy.MM.dd"; //05/08/2015
	private static synchronized String getMxFormatedDate(java.util.Date tsdate) {
		return (new SimpleDateFormat(mxDateFormatPattern)).format(tsdate);
	}

	private static synchronized java.util.Date parseMXFormatedDate(String date){
		try {
			return	(new SimpleDateFormat(mxDateFormatPattern)).parse(date);
		} catch (ParseException e) {
			SkyLogger.getItyxLogger().error("Problem during Parsing ArchiveDate: " + date, e);
			return null;
		}
	}

	@Override
	public boolean isEnabled() {
		return active;
	}
}
