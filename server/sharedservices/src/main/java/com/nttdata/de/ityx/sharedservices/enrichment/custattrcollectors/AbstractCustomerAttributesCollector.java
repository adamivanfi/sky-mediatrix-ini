package com.nttdata.de.ityx.sharedservices.enrichment.custattrcollectors;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public abstract class AbstractCustomerAttributesCollector implements ICustomerAttributesCollector {

	public AbstractCustomerAttributesCollector() {
		configure();
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public void configure() {
	}

	@Override
	public Set<String> getContractsForCustomerId(String documentid, String customerid) throws Exception {
		return new TreeSet<>();
	}

	@Override
	public Set<String> getSerialsForContractsId(String documentid, String customerid, Set<String> contractids) throws Exception {
		return new TreeSet<>();
	}

	@Override
	public Set<String> getMandateIdForContractsId(String documentid, String customerid, Set<String> contractids) throws Exception {
		return new TreeSet<>();
	}

	@Override
	public void consistencyCheck(String documentid, String customerid, Set<String> contractids, Set<String> serials, Set<String> mandates) throws Exception {
	}

	@Override
	public Set<String> getContractsForCustomerSerialId(String documentid, String customerid, Set<String> serials) throws Exception {
		return new HashSet<>();
	}


	protected Map<String, String> putIfNotEmpty(Map<String, String> result, String key, String value) {
		if (isNotEmpty(value)) {
			result.put(key, value);
		}
		return result;
	}

	protected Map<String, String> putIntIntoMap(Map<String, String> result, String key, int value) {
		if (value >= 0) {
			result.put(key, String.valueOf(value));
		} else {
			result.put(key, "0");
		}
		return result;
	}

	protected boolean isNotEmpty(String value) {
		return value != null && !value.trim().isEmpty() && !value.equals("0") && !value.equalsIgnoreCase("null");
	}


}
