/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nttdata.de.sky.connector.faa;

import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.connector.IFAA;
import com.nttdata.de.sky.connector.data.IndexingCustomer;
import com.nttdata.de.sky.connector.data.TextblockCustomer;
import customer.de.sky.faa.schemas.CustomerDataRequestType;
import customer.de.sky.faa.schemas.CustomerDataResponseType;
import customer.de.sky.faa.schemas.FaultType;
import customer.de.sky.faa.schemas.MultipleContactListType;
import customer.de.sky.faa.schemas.PackageListType;
import de.ityx.mediatrix.modules.tools.logger.Log;
import org.apache.commons.lang3.StringUtils;
import services.efbus.customerservicefaa.CustomerServiceFAA;
import services.efbus.customerservicefaa.MsgFault;
import services.efbus.customerservicefaa.PortType;
import de.ityx.mediatrix.api.interfaces.IConnectionPool;
import de.ityx.base.dbpooling.DBConnectionPoolFactory;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import java.math.BigInteger;
import java.net.URL;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * @author MEINUG
 */
public class FAAConnectorImpl implements IFAA {
	public static final String SysProperty_CustomerServiceFAA_Endpoint = "com.nttdata.de.sky.connector.faa.customerservicefaa.endpoint";
	public static final String SysProperty_CustomerServiceFAA_Timeout = "com.nttdata.de.sky.connector.faa.customerservicefaa.timeout";
	private static final QName SERVICE_NAME = new QName("http://efbus.services/CustomerServiceFAA", "CustomerServiceFAA");
	private PortType port = null;

	private PortType getPort() {
		if (port == null) {
			/*
			 * Properties
			 * 
			 * SendTimeout ReceiveTimeout URL
			 */

			String endpointUrl = System.getProperty(SysProperty_CustomerServiceFAA_Endpoint);
			SkyLogger.getConnectorLogger().info("IF3.3: Using service at: " + endpointUrl);
			URL wsdlURL = getClass().getClassLoader().getResource("wsdl/IF33_FAA/CustomerServiceFAA.wsdl");
			SkyLogger.getConnectorLogger().debug("IF3.3: Expecting CustomerServiceFAA.wsdl at: " + wsdlURL);

			CustomerServiceFAA csfa = new CustomerServiceFAA(wsdlURL, SERVICE_NAME);
			//CustomerServiceFAA csfa = new CustomerServiceFAA();
			port = csfa.getCustomerServiceFAA();

			BindingProvider provider = (BindingProvider) port;
			provider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointUrl);
/*
			Client cl = ClientProxy.getClient(port);

			HTTPConduit http = (HTTPConduit) cl.getConduit();
			HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
			
			int timeout = ConnectorFactory.TIMEOUT;
			
			if (System.getProperty(SysProperty_CustomerServiceFAA_Timeout) != null && !System.getProperty(SysProperty_CustomerServiceFAA_Timeout).isEmpty()) {
				try {
					timeout = Integer.parseInt(System.getProperty(SysProperty_CustomerServiceFAA_Timeout));
				} catch (NumberFormatException e) {
					SkyLogger.getConnectorLogger().warn("IF3.3: Wrong Configuration for Parameter SysProperty_CustomerServiceFAA_Timeout:" + System.getProperty(SysProperty_CustomerServiceFAA_Timeout));
				}

			}
			if (timeout < 10000) {
				SkyLogger.getConnectorLogger().warn("IF3.3: Wrong Configuration for Parameter SysProperty_CustomerServiceFAA_Timeout:" + timeout + " value cannot be set <10s");
				timeout = 15000;
			}
			
			httpClientPolicy.setConnectionTimeout(timeout);
			httpClientPolicy.setReceiveTimeout(timeout);
			http.setClient(httpClientPolicy);
*/
		}
		return port;
	}

	public IndexingCustomer queryFAACustomerData(IndexingCustomer indexingCustomer, String itemID) throws Exception {
		if (indexingCustomer == null || indexingCustomer.getSMCNumber() == null || indexingCustomer.getSMCNumber().isEmpty() || indexingCustomer.getNumber() == null || indexingCustomer.getNumber().isEmpty()) {
			if (indexingCustomer == null) {
				SkyLogger.getConnectorLogger().info("IF3.3: SkippingQuerry: CustomerNotGiven. " + itemID);
			} else {
				SkyLogger.getConnectorLogger().debug("IF3.3: " + itemID + " CustNr:" + indexingCustomer.getNumber() + " FAA: SkippingQuerry:IncompleteQuestionParameter " + " smcid:" + indexingCustomer.getSMCNumber());
			}
			return null;
		}
		SkyLogger.getConnectorLogger().debug("IF3.3: " + itemID + " CustNr:" + indexingCustomer.getNumber() + " FAA: Start:IncompleteQuestionParameter " + " smcid:" + indexingCustomer.getSMCNumber());

		IndexingCustomer rcustomer = queryFAACustomerData(itemID, indexingCustomer.getNumber(), indexingCustomer.getSMCNumber());

		TextblockCustomer customer = new TextblockCustomer(indexingCustomer);
		customer.setContactInterval7D(rcustomer.getContactInterval7D());
		customer.setContactInterval14D(rcustomer.getContactInterval14D());
		customer.setContactInterval21D(rcustomer.getContactInterval21D());
		customer.setContactInterval28D(rcustomer.getContactInterval28D());
		customer.setQFlag(rcustomer.getQFlag());
		customer.setBoxOnly(rcustomer.getBoxOnly());
		customer.setPackAndProductList(rcustomer.getPackAndProductList());

		SkyLogger.getConnectorLogger().debug("IF3.3: " + itemID + " CustNr:" + indexingCustomer.getNumber() + " FAA: finished:IncompleteQuestionParameter " + " smcid:" + indexingCustomer.getSMCNumber());

		return customer;
	}


	public IndexingCustomer queryFAACustomerData(String documentid, String customerid, String serialnumber) throws Exception {
		IndexingCustomer customer = new IndexingCustomer();
		CustomerDataRequestType req = new CustomerDataRequestType();
		String inMsg = 	"IF3.3 -> docId: " + documentid + "; custNr:" + customerid + "; smcNr: " + serialnumber;
		SkyLogger.getConnectorLogger().debug(inMsg + " <- START");

		if (customerid!=null){
			customerid=customerid.trim();
		}
		if (serialnumber!=null){
			serialnumber=serialnumber.trim();
		}

		req.setContactChannel("DMS");
		req.setCustomerNumber(customerid);
		req.setSMCNumber(serialnumber);
		
		long elapsedTime = -System.currentTimeMillis();

		try {
			CustomerDataResponseType resp = getPort().getCustomerData(req);
			String retCode = resp.getRetCode();
			if (retCode.equals("0")) {
				customer.setRateCardFlg(resp.getRateCardFlag().value());
				customer.setCustomerMigration(resp.getCustomerMigration());

				//202687 - Sky Relaunch Pricing and Packaging Zusammenfassung
				boolean skyQFlag = resp.isSkyQFlag();
				if (skyQFlag) {
					customer.setQFlag("1");
				}else {
					customer.setQFlag("0");
				}
				SkyLogger.getConnectorLogger().debug(inMsg+ ", QFlag: " + customer.getQFlag());
				customer.setBoxOnly(resp.getBoxOnlySubscriptionFlag().toString());


				MultipleContactListType mclt = resp.getMultipleContactList();
				if (mclt != null) {
					BigInteger contactInterval3D = mclt.getContactInterval3D();
					if (contactInterval3D != null) {
						customer.setContactInterval3D(contactInterval3D.intValue());
					}
					BigInteger contactInterval7D = mclt.getContactInterval7D();
					if (contactInterval7D != null) {
						customer.setContactInterval7D(contactInterval7D.intValue());
					}
					BigInteger contactInterval14D = mclt.getContactInterval14D();
					if (contactInterval14D != null) {
						customer.setContactInterval14D(contactInterval14D.intValue());
					}
					BigInteger contactInterval21D = mclt.getContactInterval21D();
					if (contactInterval21D != null) {
						customer.setContactInterval21D(contactInterval21D.intValue());
					}
					BigInteger contactInterval28D = mclt.getContactInterval28D();
					if (contactInterval28D != null) {
						customer.setContactInterval28D(contactInterval28D.intValue());
					}
					
					if (resp.getPackAndProductList()!=null && resp.getPackAndProductList().getPackAndProduct()!=null ){
						List<String> packAndProduct = resp.getPackAndProductList().getPackAndProduct();
						String packAndProductCSL= StringUtils.join(packAndProduct, ",");
						packAndProductCSL = packAndProductCSL.length() > 1000 ? packAndProductCSL.substring(0, 999) : packAndProductCSL;
						customer.setPackAndProductList(packAndProductCSL);
					}

					SkyLogger.getConnectorLogger().debug(inMsg+
							"; contactIntervals: {3d: " + contactInterval3D + "; 7d: " + contactInterval7D +"; 14d: " + contactInterval14D + "; 21d: " + contactInterval21D + "; 28d: " + contactInterval28D+";}; "+
							"; custMigration: "+resp.getCustomerMigration());

				} else {
					SkyLogger.getConnectorLogger().warn(inMsg +"; custMigration: "+resp.getCustomerMigration()+ "; WARN: does not contain MultipleContactList. retCode: " + retCode + ": errDesc: " + resp.getErrorDesc());
				}
			} else {
				SkyLogger.getConnectorLogger().warn(inMsg +"; custMigration: "+resp.getCustomerMigration()+ "; WARN: retCode: " + retCode + "; errDesc: " + resp.getErrorDesc());
			}
		} catch (MsgFault ex) {
			if (ex.getFaultInfo() != null && ex.getFaultInfo().getErrorCode().equals("80")) {
				SkyLogger.getConnectorLogger().error(inMsg+outputMsgFault(ex));
				throw new Exception(inMsg+outputMsgFault(ex));
			}else{
				SkyLogger.getConnectorLogger().info(inMsg+outputMsgFault(ex));
			}
		} catch (Exception ex) {
			SkyLogger.getConnectorLogger().error(inMsg + "; errMsg: " + ex.getMessage(), ex);
			throw ex;
		}
		if (SkyLogger.getConnectorLogger().isInfoEnabled()) {
			long elapsedtimeDiff = (elapsedTime + System.currentTimeMillis()) / 1000;
			if (elapsedtimeDiff > 5) {
				SkyLogger.getConnectorLogger().info(inMsg + "; took: " + elapsedtimeDiff + "s");
			}
		}
		return customer;
	}
	private static String outputMsgFault(MsgFault e){
		return ((e.getFaultInfo() != null)?"; errCode: " + e.getFaultInfo().getErrorCode() + "; errDesc: " + e.getFaultInfo().getErrorDesc():"; faultInfo: null") +
				"; errMsg: " + e.getMessage();
	}
}
