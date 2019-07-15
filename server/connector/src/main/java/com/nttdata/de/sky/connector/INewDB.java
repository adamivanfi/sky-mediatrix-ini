package com.nttdata.de.sky.connector;

import com.nttdata.de.sky.connector.data.IndexingCustomer;
import com.nttdata.de.sky.connector.data.RoutingCustomer;

public interface INewDB {
	public RoutingCustomer queryRoutingCustomer(String customerNumber) throws Exception;

	public RoutingCustomer queryRoutingCustomer(String customerNumber, String contractNumber) throws Exception;
	
	public RoutingCustomer queryTextblockCustomer(IndexingCustomer indexingCustomer) throws Exception;
}
