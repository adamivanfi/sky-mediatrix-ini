/**
 * 
 */
package com.nttdata.de.ityx.cx.sky.connector.newdb;

import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.connector.ConnectorFactory;
import com.nttdata.de.sky.connector.INewDB;
import com.nttdata.de.sky.connector.data.IndexingCustomer;
import com.nttdata.de.sky.connector.data.TextblockCustomer;
import com.nttdata.de.sky.ityx.common.interfaces.IConnectorBridge;

import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * Proxies calls of the NewDB interface that occur within Mediatrix.
 * 
 * @author DHIFLM
 * 
 */
public class NewDBProxy implements IConnectorBridge {

	/**
	 * Tries to load a customer via the NewDB interface and fills the Map with
	 * customer data.
	 * 
	 * @param customerId
	 *            The id of the customer to load
	 * @param contractNumber
	 *            The contract of the customer to load (optional)
	 * @param customerData
	 *            The map to be filled with the customer data
	 * @throws Exception
	 */
	@Override
	public void fillCustomerData(String customerId,
			String contractNumber, Map<String, String> customerData)
			throws Exception {
		INewDB newDb = ConnectorFactory.getNewDBInstance();
		IndexingCustomer indexingCustomer = new IndexingCustomer();
		indexingCustomer.setNumber(customerId);
		if (contractNumber != null) {
			indexingCustomer.setSelectedContractNumber(contractNumber);
		}
		TextblockCustomer textblockCustomer = (TextblockCustomer) newDb
				.queryTextblockCustomer(indexingCustomer);

		if (textblockCustomer.toString()!=null){
			SkyLogger.getMediatrixLogger().debug("Customer from textblock query: "+textblockCustomer.toString());
		}

		// ROTHJA - 20180709
		//Tags Auflösung können kein NULL zurück schicken.
		customerData.put("EmailAddress",textblockCustomer.getEmailAddress());
		customerData.put("AccountBalance", textblockCustomer.getAccountBalance());
		customerData.put("AccountNumberShort", textblockCustomer.getAccountNumberShort());
		customerData.put("BankAccountHolder", textblockCustomer.getBankAccountHolder());
		customerData.put("BankCode", textblockCustomer.getBankCode());
		customerData.put("CampaignStamp", textblockCustomer.getCampaignStamp());
		customerData.put("Category", textblockCustomer.getCategory());
		customerData.put("City", textblockCustomer.getCity());
		customerData.put("ContractDate", textblockCustomer.getContractDate());
		customerData.put("Country", textblockCustomer.getCountry());
		customerData.put("DunningAmount", textblockCustomer.getDunningAmount());
		customerData.put("DunningLevel", textblockCustomer.getDunningLevel());
		customerData.put("EarmarkedCancelationDate", textblockCustomer.getEarmarkedCancelationDate());
		customerData.put("FirstName", textblockCustomer.getFirstName());
		customerData.put("FlatNumber", textblockCustomer.getFlatNumber());
		customerData.put("Floor", textblockCustomer.getFloor());
		customerData.put("LastName", textblockCustomer.getLastName());
		customerData.put("Number", textblockCustomer.getNumber());
//		customerData.put("PossibleCancelationDate", textblockCustomer.getPossibleCancelationDate() != null ? getMxFormatedDate(textblockCustomer.getPossibleCancelationDate()) : " ");
		customerData.put("PossibleCancelationDate", getMxFormatedDate(textblockCustomer.getPossibleCancelationDate()));
		//customerData.put("PossibleCancelationDate", getMxFormatedDate(textblockCustomer.getPossibleCancelationDate()));
		customerData.put("RowId", textblockCustomer.getRowId());
		customerData.put("Salutation", textblockCustomer.getSalutation());
		customerData.put("SelectedContractNumber", textblockCustomer.getSelectedContractNumber());
		customerData.put("SerialCIPlus", textblockCustomer.getSerialCIPlus());
		customerData.put("SerialHarddisk", textblockCustomer.getSerialHarddisk());
		customerData.put("SerialReceiver", textblockCustomer.getSerialReceiver());
		customerData.put("SerialSmartcard", textblockCustomer.getSerialSmartcard());
		customerData.put("SrContractChange", textblockCustomer.getSrContractChange());
		customerData.put("SrContractChangeDate", textblockCustomer.getSrContractChangeDate());
		customerData.put("Staircase", textblockCustomer.getStaircase());
		customerData.put("Street", textblockCustomer.getStreet());
		customerData.put("SubscriptionStartDate", textblockCustomer.getSubscriptionStartDate());
		customerData.put("TelephoneNumber", textblockCustomer.getTelephoneNumber());
		customerData.put("MobileNumber", textblockCustomer.getMobileNumber());
		customerData.put("TypeOfCIPlus", textblockCustomer.getTypeOfCIPlus());
		customerData.put("TypeOfHarddisk", textblockCustomer.getTypeOfHarddisk());
		customerData.put("TypeOfReceiver", textblockCustomer.getTypeOfReceiver());
		customerData.put("ZipCode", textblockCustomer.getZipCode());
		customerData.put("AdditionalAddress", textblockCustomer.getAdditionalAddress());

		// March 2013  Kampagnentyp
		customerData.put("CampaignStamp", textblockCustomer.getCampaignStamp());
		
		// SEPA
		customerData.put("CustomerBIC", textblockCustomer.getCustomerBIC());
		//customerData.put("CustomerIBAN", textblockCustomer.getCustomerIBAN());
		customerData.put("MandateRefId", textblockCustomer.getMandateNumber());
		customerData.put("MandateStatus", textblockCustomer.getMandateStatus());
		customerData.put("SignatureDate", textblockCustomer.getSignatureDate());
		customerData.put("SignatureFlag", textblockCustomer.getSignatureFlag());
	}
	protected static final String mxDateFormatPattern = "yyyy.MM.dd"; //05/08/2015
	private static synchronized String getMxFormatedDate(java.util.Date tsdate) {
		return (new SimpleDateFormat(mxDateFormatPattern)).format(tsdate);
	}

}
