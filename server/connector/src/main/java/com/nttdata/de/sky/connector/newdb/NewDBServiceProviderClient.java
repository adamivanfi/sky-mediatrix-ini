package com.nttdata.de.sky.connector.newdb;

import com.nttdata.de.lib.logging.SkyLogger;
import de.sky.integration.web.customerdataservice._1.CustomerDataServiceServiceagent;
import de.sky.integration.web.customerdataservice._1.GetDynamicCustomerDataRequestType;
import de.sky.integration.web.customerdataservice._1.PortType;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import java.net.URL;

public class NewDBServiceProviderClient {

	private static final QName SERVICE_NAME = new QName("http://www.sky.de/integration/web/CustomerDataService/1.0", "CustomerDataService");

	public static void main(String args[]) throws java.lang.Exception {
		if (args.length < 3) {
			System.err.println("Expected parameters <Endpoint> [CustomerNumber]");
		}

		URL wsdlURL = ClassLoader.getSystemResource("wsdl/IF32_NewDB/CustomerDataService.wsdl");
		String endpointUrl = args[0];

		SkyLogger.getConnectorLogger().info("Expect WSDL at: " + wsdlURL);
		SkyLogger.getConnectorLogger().info("Connect to service at " + endpointUrl);

		String customerId = "4123456789";
		if (args.length > 1 && args[1] != null && !"".equals(args[1])) {
			customerId = args[1];
		}

		String contractId = null;
		if (args.length > 2 && args[2] != null && !"".equals(args[2])) {
			contractId = args[2];
		}

		SkyLogger.getConnectorLogger().info("Query for customer: " + customerId);

		CustomerDataServiceServiceagent ss = new CustomerDataServiceServiceagent(wsdlURL, SERVICE_NAME);
		PortType port = ss.getCustomerDataServiceEndpoint();

		if (endpointUrl != null) {
			SkyLogger.getConnectorLogger().info("Using endpoint: " + endpointUrl);
			BindingProvider provider = (BindingProvider) port;
			provider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointUrl);
		}
/*
		Client cl = ClientProxy.getClient(port);

		HTTPConduit http = (HTTPConduit) cl.getConduit();
		HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
		httpClientPolicy.setConnectionTimeout(ConnectorFactory.TIMEOUT);
		httpClientPolicy.setReceiveTimeout(ConnectorFactory.TIMEOUT);

		http.setClient(httpClientPolicy);
*/
		SkyLogger.getConnectorLogger().info("Invoking getDynamicCustomerData...");

		de.sky.integration.web.customerdataservice._1.GetDynamicCustomerDataRequestType req = new GetDynamicCustomerDataRequestType();
		req.setRequestType("Routing");
		req.setRequestedFields("DunningContractId;DunningLevel;ContractChangeCategory;ContractChangeDate;CustomerCategory;EarmarkedCancellationDate;Pricelist;Stamp;SubscriptionStartDate;UnexpectedReturnContract;CampaignTypes;WashMachine");
		req.setCustomerId(customerId);
		if (contractId != null) {
			req.setCustomerId(contractId);
		}

		de.sky.integration.web.customerdataservice._1.GetDynamicCustomerDataResponseType _getDynamicCustomerData__return = port.getDynamicCustomerData(req);
		SkyLogger.getConnectorLogger().info("getDynamicCustomerData.result=" + _getDynamicCustomerData__return);
		if (_getDynamicCustomerData__return != null) {
			SkyLogger.getConnectorLogger().info(NewDBConnectorImpl.outputResponse(_getDynamicCustomerData__return));
		}

		System.exit(0);
	}
}
