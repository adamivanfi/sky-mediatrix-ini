package com.nttdata.de.sky.connector;

import com.nttdata.de.sky.connector.data.IndexingCustomer;
import com.nttdata.de.sky.connector.data.RoutingCustomer;

public interface IFAA {
	public RoutingCustomer queryFAACustomerData(IndexingCustomer indexingCustomer, String logId) throws Exception;

	public RoutingCustomer queryFAACustomerData(String documentid, String customerid, String serialnumber) throws Exception;
	
}
