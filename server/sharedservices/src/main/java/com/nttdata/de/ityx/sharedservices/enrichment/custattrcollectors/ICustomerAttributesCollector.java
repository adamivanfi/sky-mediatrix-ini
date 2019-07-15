package com.nttdata.de.ityx.sharedservices.enrichment.custattrcollectors;

import java.util.Map;
import java.util.Set;

/**
 * Created by meinusch on 30.03.15.
 */
public interface ICustomerAttributesCollector {
	public Map<String, String> collectCustomerAttributes(String documentid, String customerid, Set<String> contractids, Set<String> serialNr, Set<String> mandates) throws Exception;

	public boolean isEnabled();

	public void configure();

	public Set<String> getContractsForCustomerId(String documentid, String customerid) throws Exception;

	public Set<String> getSerialsForContractsId(String documentid, String customerid, Set<String> contractids) throws Exception;

	public Set<String> getMandateIdForContractsId(String documentid, String customerid, Set<String> contractids) throws Exception;

	public void consistencyCheck(String documentid, String customerid, Set<String> contractids, Set<String> serials, Set<String> mandates) throws Exception;

	public Set<String> getContractsForCustomerSerialId(String documentid, String customerid, Set<String> serials)  throws Exception;
}
