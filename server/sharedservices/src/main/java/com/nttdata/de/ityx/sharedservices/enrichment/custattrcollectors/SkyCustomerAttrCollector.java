package com.nttdata.de.ityx.sharedservices.enrichment.custattrcollectors;

import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class SkyCustomerAttrCollector {

	private static List<ICustomerAttributesCollector> cAttColl = null;
	private static Long initialized = 0L;

	private synchronized static List<ICustomerAttributesCollector> getCustAttrCollectors() {
		if (cAttColl == null || cAttColl.isEmpty()) {
			cAttColl = new LinkedList<>();

			ICustomerAttributesCollector if31 = new IF31_FuzzyDB_CustAttrCollector();
			if (if31.isEnabled()) {
				cAttColl.add(if31);
			}
			ICustomerAttributesCollector if32 = new IF32_NewDB_CustAttrCollector();
			if (if32.isEnabled()) {
				cAttColl.add(if32);
			}
			ICustomerAttributesCollector if33 = new IF33_FAA_CustAttrCollector();
			if (if33.isEnabled()) {
				cAttColl.add(if33);
			}
		}
		return cAttColl;
	}


	public static Map<String, String> collectCustomerAttributes(String documentid, String customerid, String contractid, String serial, String mandateid) throws Exception {
		Set<String> contractids = new HashSet<>();

		if (contractid != null && !contractid.isEmpty()) {
			contractids.add(contractid);
		}
		Set<String> serials = new HashSet<>();
		if (serial != null && !serial.isEmpty()) {
			serials.add(serial);
		}
		Set<String> mandates = new HashSet<>();
		if (mandateid != null && !mandateid.isEmpty()) {
			mandates.add(mandateid);
		}
		return collectCustomerAttributes(documentid, customerid, contractids, serials, mandates);

	}

	public static Map<String, String> collectCustomerAttributes(String documentid, String customerid, Set<String> contractids, Set<String> serials, Set<String> mandates) {
		Map<String, String> custAttributes = new HashMap<>();
		try {
			if (contractids==null){
				contractids=new LinkedHashSet<>();
			}

			if (serials==null){
				serials=new LinkedHashSet<>();
			}

			if (mandates==null){
				mandates=new LinkedHashSet<>();
			}

			if (customerid == null || customerid.trim().isEmpty() || customerid.trim().equalsIgnoreCase("null") || customerid.trim().equals("0")) {
				SkyLogger.getConnectorLogger().info("SkyCustomerAttrCollector:" + documentid + " Customerid is empty!");
				return new HashMap<>();
			}
			SkyLogger.getConnectorLogger().info("SkyCustomerAttrCollector:StartUsing:" + documentid + ":using Attribs:c:"+customerid+" v:"+StringUtils.join(contractids, ",")
					+" sn:"+StringUtils.join(serials, ",")+" m:"+StringUtils.join(mandates, ","));


			if (contractids.isEmpty() &&  !serials.isEmpty()) {
				for (ICustomerAttributesCollector c : getCustAttrCollectors()) {
					contractids.addAll(c.getContractsForCustomerSerialId(documentid, customerid, serials));
				}
				for (String id : contractids) {
					custAttributes.put(TagMatchDefinitions.TAGMATCH_CONTRACT_NUMBER, id);
				}
				SkyLogger.getConnectorLogger().info("SkyCustomerAttrCollector:" + documentid + " ContractIds for customerid:" + customerid + " sn:"+ StringUtils.join(serials, ",")+" not provided. " + (contractids.isEmpty() ? "Contractids not found" : ("Using best matches using SN:" + StringUtils.join(contractids, ","))));
			}
			if (contractids.isEmpty()) {
				for (ICustomerAttributesCollector c : getCustAttrCollectors()) {
					contractids.addAll(c.getContractsForCustomerId(documentid, customerid));
				}
				for (String id : contractids) {
					custAttributes.put(TagMatchDefinitions.TAGMATCH_CONTRACT_NUMBER, id);
				}
				SkyLogger.getConnectorLogger().info("SkyCustomerAttrCollector:" + documentid + " ContractIds for customerid:" + customerid + " not provided. " + (contractids.isEmpty() ? "Contractids not found" : ("Using best matches:" + StringUtils.join(contractids, ","))));
			}

			if ( !contractids.isEmpty() && serials.isEmpty()) {
				for (ICustomerAttributesCollector c : getCustAttrCollectors()) {
					serials.addAll(c.getSerialsForContractsId(documentid, customerid, contractids));
				}
				for (String id : serials) {
					custAttributes.put(TagMatchDefinitions.TAGMATCH_SMARTCARD_NUMBER, id);
				}
				SkyLogger.getConnectorLogger().info("SkyCustomerAttrCollector:" + documentid + " SNIds not provided. " + (serials.isEmpty() ? "SN not found" : ("Using best matches:" + StringUtils.join(serials, ","))));
			}

			// Change by Ivanfi, 17.01.2017 - I-232941:
			if (mandates != null && !mandates.isEmpty()) {
			//if (mandates.isEmpty()) {
				for (ICustomerAttributesCollector c : getCustAttrCollectors()) {
					mandates.addAll(c.getMandateIdForContractsId(documentid, customerid, contractids));
				}
				for (String id : mandates) {
					custAttributes.put(TagMatchDefinitions.SEPA_MANDATE_NUMBER, id);
				}
				// Change by Ivanfi, 16.01.2017 - I-232941:
				SkyLogger.getConnectorLogger().info("SkyCustomerAttrCollector:" + documentid + " MandateRefId not provided. " + (serials.isEmpty() ? "ID not found" : ("Using best matches:" + StringUtils.join(mandates, ","))));
				//SkyLogger.getConnectorLogger().info("SkyCustomerAttrCollector:" + documentid + " MandateRefId not provided. " + (mandates.isEmpty() ? "ID not found" : ("Using best matches:" + StringUtils.join(mandates, ","))));
			}

			for (ICustomerAttributesCollector c : getCustAttrCollectors()) {
				custAttributes.putAll(c.collectCustomerAttributes(documentid, customerid.trim(), contractids, serials, mandates));
			}
			return custAttributes;
		} catch (Exception e) {
			SkyLogger.getConnectorLogger().error("SkyCustomerAttrCollector:ERROR:" + e.getMessage(), e);
			return custAttributes;
		}
	}


	public static Map<String, String> collectCustomerAttributesWithContractConsistencyCheck(String documentid, String customerid, String contractid, String serial, String mandateid) throws Exception {
		Set<String> contractids = new HashSet<>();

		if (contractid != null && !contractid.isEmpty()) {
			contractids.add(contractid);
		}
		Set<String> serials = new HashSet<>();
		if (serial != null && !serial.isEmpty()) {
			serials.add(serial);
		}
		Set<String> mandates = new HashSet<>();
		if (mandateid != null && !mandateid.isEmpty()) {
			mandates.add(mandateid);
		}

		SkyLogger.getConnectorLogger().info("cCAWContractConsistencyCheck:StartUsing:" + documentid + ":using Attribs:c:"+customerid+" v:"+StringUtils.join(contractids, ",")
				+" sn:"+StringUtils.join(serials, ",")+" m:"+StringUtils.join(mandates, ","));

		for (ICustomerAttributesCollector c : getCustAttrCollectors()) {
			c.consistencyCheck(documentid, customerid, contractids, serials, mandates); // throws exception if needed
		}

		Map<String, String> custAttributes = collectCustomerAttributes(documentid, customerid, contractids, serials, mandates);

		if (contractids.isEmpty()) {
			throw new Exception(documentid + " Contract not found");
		}
		if (contractids.size() > 1) {
			String msg = documentid + " Contract not unique: " + (contractids.isEmpty() ? "ID not found" : ("Candidates:" + StringUtils.join(contractids, ",")));
			SkyLogger.getConnectorLogger().info("collectCustomerAttributesWithContractConsistencyCheck:" + msg);
			throw new Exception(msg);
		}
		return custAttributes;
	}
}
