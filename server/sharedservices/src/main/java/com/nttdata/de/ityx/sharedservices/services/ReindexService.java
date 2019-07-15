package com.nttdata.de.ityx.sharedservices.services;

import com.nttdata.de.ityx.sharedservices.configuration.BeanConfig;
import com.nttdata.de.ityx.sharedservices.enrichment.custattrcollectors.SkyCustomerAttrCollector;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.connector.ConnectorFactory;
import com.nttdata.de.sky.connector.ISiebel;
import com.nttdata.de.sky.connector.UpdateContactParameter;
import com.nttdata.de.sky.connector.siebel.SiebelConnectorImpl;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;

import java.util.Map;
import java.util.TreeMap;

public class ReindexService {

	private void config() {

		String endpoint = null;
		try {
			endpoint = BeanConfig.getReqString("Siebel_WSDL");
			System.setProperty(SiebelConnectorImpl.SysProperty_SiebelService_Endpoint, endpoint);
		} catch (Exception e) {
			SkyLogger.getConnectorLogger().info("IF4.X: SiebelInterfaceNotInitated please check the Value:Siebel_WSDL ");
		}
	}

	public Map<String, String> reindexFrage(Integer questionId, String documentid, String customerid, String contractid, String old_contactid, String channel, String doctype, String direction) throws Exception {
		return reindexFrage(questionId, documentid, customerid, contractid, old_contactid, channel, doctype, direction, null, null, null);
	}

	public Map<String, String>  associateFrage(String documentid, String customerid, String contractid, String mandateId, String contactid, String activityId, String channel, String direction) throws Exception {
		config();
		final long currentTimeMillis = System.currentTimeMillis();
		SkyLogger.getConnectorLogger().info("IF4.4: " + documentid + "  associateDocument");
		ConnectorFactory.getSiebelInstance().associateDocumentIdToActivity(documentid, activityId, ISiebel.Channel.valueOf(channel), ISiebel.Direction.valueOf(direction));
		SkyLogger.getConnectorLogger().info("IF4.4: " + documentid + "  associateDocument time in ms: " + (System.currentTimeMillis() - currentTimeMillis));
		return getCustomerAttributes(documentid, customerid, contractid, mandateId, contactid);
	}

	public Map<String, String> reindexFrage(Integer questionId, String documentid, String customerid, String contractid, String old_contactid, String channel, String doctype, String direction, String mandateId, String signatureDate, String signatureFlag) throws Exception {
		config();
		final long currentTimeMillis = System.currentTimeMillis();
		String newContactId = ConnectorFactory.getSiebelInstance().updateContactOfDocument(new UpdateContactParameter(questionId, documentid, old_contactid, customerid, contractid, channel, doctype, direction,mandateId, signatureFlag, signatureDate));

		final long reindexingTime = System.currentTimeMillis() - currentTimeMillis;
		SkyLogger.getConnectorLogger().info("IF4.5: " + documentid + "  CustomerReindexing time in ms: " + reindexingTime);
		return getCustomerAttributes(documentid, customerid, contractid, mandateId, newContactId);
	}

	private Map<String, String> getCustomerAttributes(String documentid, String customerid, String contractid, String mandateId, String newContactId) throws Exception {
		Map<String, String> attribs = SkyCustomerAttrCollector.collectCustomerAttributes(documentid, customerid, contractid, null, mandateId);

		if (customerid!=null) {
			attribs.put(TagMatchDefinitions.CUSTOMER_ID, customerid);
		}
		if (contractid!=null){
			attribs.put(TagMatchDefinitions.TAGMATCH_CONTRACT_NUMBER, contractid);

		}
		if (newContactId!=null) {
			attribs.put(TagMatchDefinitions.CONTACT_ID, newContactId);
		}

		Map<String, String> customerattribs = new TreeMap<>();

		for (String key : TagMatchDefinitions.CUSTOMER_DATA) {
			String value = attribs.get(key);
			customerattribs.put(key, value == null ? "" : value);
		}
		return customerattribs;
	}


}
