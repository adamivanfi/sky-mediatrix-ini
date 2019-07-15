package com.nttdata.de.ityx.sharedservices.enrichment.custattrcollectors;

import com.nttdata.de.ityx.sharedservices.configuration.BeanConfig;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.connector.ConnectorFactory;
import com.nttdata.de.sky.connector.IFAA;
import com.nttdata.de.sky.connector.data.RoutingCustomer;
import com.nttdata.de.sky.connector.faa.FAAConnectorImpl;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class IF33_FAA_CustAttrCollector extends AbstractCustomerAttributesCollector {

	@Override
	public Map<String, String> collectCustomerAttributes(String documentid, String customerid, Set<String> contractids, Set<String> serialNrs, Set<String> mandates) throws Exception {
		Map<String, String> result = new TreeMap<>();
		if(!isEnabled()){
			SkyLogger.getConnectorLogger().warn("IF33_FAA_CustAttrCollector:DISABLED:" + documentid + " Query: CustomerNumer:" + customerid + ", ContractNumber:" + StringUtils.join(contractids, ",")+ ", SerialNumber:" + StringUtils.join(serialNrs, ",") );
			return result;
		}

		SkyLogger.getConnectorLogger().debug("IF33_FAA_CustAttrCollector:" + documentid + " Query: CustomerNumer:" + customerid + ", ContractNumber:" + StringUtils.join(contractids, ",")+ ", SerialNumber:" + StringUtils.join(serialNrs, ",") );

		IFAA faa = ConnectorFactory.getFAAInstance();
		if (serialNrs==null || serialNrs.isEmpty()){
			SkyLogger.getConnectorLogger().warn("IF33_FAA_CustAttrCollector:" + documentid + " Query: CustomerNumer:" + customerid + ", ContractNumber:" + StringUtils.join(contractids, ",")+ " SN not provided! Skipping Query" );
			return new TreeMap<>();
		}
		boolean custfound = false;

		for (String serial : serialNrs) {
			if (!custfound){
				RoutingCustomer rCustomer = faa.queryFAACustomerData(documentid, customerid, serial);
				if (rCustomer != null) {
					custfound = true;
					//putIntIntoMap(result, TagMatchDefinitions.CONTACT_INTERVAL_3D, rCustomer.getContactInterval3D());
					putIntIntoMap(result, TagMatchDefinitions.CONTACT_INTERVAL_7D, rCustomer.getContactInterval7D());
					putIntIntoMap(result, TagMatchDefinitions.CONTACT_INTERVAL_14D, rCustomer.getContactInterval14D());
					putIntIntoMap(result, TagMatchDefinitions.CONTACT_INTERVAL_21D, rCustomer.getContactInterval21D());
					putIntIntoMap(result, TagMatchDefinitions.CONTACT_INTERVAL_28D, rCustomer.getContactInterval28D());
					result.put(TagMatchDefinitions.META_RATECARDFLG, rCustomer.getRateCardFlg());
					putIfNotEmpty(result, TagMatchDefinitions.META_CUSTOMER_MIGRATION, rCustomer.getCustomerMigration());
					result.put(TagMatchDefinitions.META_Q_FLAG, rCustomer.getQFlag());
					SkyLogger.getConnectorLogger().debug("Enrichment of document QFLAG: "+ rCustomer.getQFlag());
					result.put(TagMatchDefinitions.META_BOX_ONLY, rCustomer.getBoxOnly());
					result.put(TagMatchDefinitions.PACKANDPRODUCT_LIST,rCustomer.getPackAndProductList());
					SkyLogger.getConnectorLogger().info("IF33_FAA_CustAttrCollector:" + documentid + " Query: CustomerNumer:" + customerid + ", SN:" + serial + " Intervals:" + rCustomer.getContactInterval7D() + ":" + rCustomer.getContactInterval14D() + ":" + rCustomer.getContactInterval21D() + ":" + rCustomer.getContactInterval28D()+"." );
				}
			}
		}
		if (!custfound) {
			SkyLogger.getConnectorLogger().warn("IF33_FAA_CustAttrCollector:" + documentid + " Query: CustomerNumer:" + customerid + " Customer returned by service is null - this happens when there are entries in FuzzyDB that are not in NewDB anymore.");
			//throw new Exception(customerid + " Customer returned by service is null - this happens when there are entries in FuzzyDB that are not in NewDB anymore.");
		}
		return result;
	}

	private boolean active = true;

	@Override
	public void configure() {
		active = BeanConfig.getBoolean("CustomerServiceFAA_Enabled", true);
		String endpoint = null;
		try {
			endpoint = BeanConfig.getReqString("CustomerServiceFAA_WSDL");
		} catch (Exception e) {
			SkyLogger.getConnectorLogger().error("IF33_FAA_ENDPOINT: is not set, the IF33 would be disabled. Please check the value of:CustomerServiceFAA_WSDL");
		}
		if (!active) {
			SkyLogger.getConnectorLogger().warn("IF33_FAA_DISABLED");
		}
		if (isNotEmpty(endpoint)) {
			System.setProperty(FAAConnectorImpl.SysProperty_CustomerServiceFAA_Endpoint, endpoint);
			SkyLogger.getConnectorLogger().info("IF33_FAA_ENDPOINT: " + endpoint);
		} else {
			SkyLogger.getConnectorLogger().warn("IF33_FAA_ENDPOINT NOT SET: DISABLED ");
			active=false;
		}
		int faaTimeout = BeanConfig.getInt("CustomerServiceFAA_Timeout", 31);
		System.setProperty(FAAConnectorImpl.SysProperty_CustomerServiceFAA_Timeout, (faaTimeout * 1000) + "");

	}


	@Override
	public boolean isEnabled() {
		return active;
	}
}
