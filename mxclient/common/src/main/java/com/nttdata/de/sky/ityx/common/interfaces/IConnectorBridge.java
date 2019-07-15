package com.nttdata.de.sky.ityx.common.interfaces;

import java.util.Map;

public interface IConnectorBridge {

	public abstract void fillCustomerData(String customerId, String contractNumber,
			Map<String, String> customerData) throws Exception;

}
