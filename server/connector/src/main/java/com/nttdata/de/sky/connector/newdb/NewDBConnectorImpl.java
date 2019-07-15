package com.nttdata.de.sky.connector.newdb;

import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.connector.INewDB;
import com.nttdata.de.sky.connector.data.IndexingCustomer;
import com.nttdata.de.sky.connector.data.RoutingCustomer;
import com.nttdata.de.sky.connector.data.TextblockCustomer;
import de.ityx.base.dbpooling.DBConnectionPoolFactory;
import de.ityx.mediatrix.api.interfaces.IConnectionPool;
import de.ityx.mediatrix.modules.tools.logger.Log;
import de.sky.integration.web.customerdataservice._1.*;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NewDBConnectorImpl implements INewDB {

	public static final String SysProperty_CustomerDataService_Endpoint = "com.nttdata.de.sky.connector.newdb.customerdataservice.endpoint";
	private static final QName SERVICE_NAME = new QName("http://www.sky.de/integration/web/CustomerDataService/1.0", "CustomerDataService.serviceagent");
	private PortType port = null;

	public static String outputResponse(GetDynamicCustomerDataResponseType resp) {
		String result = "";

		result += "CustomerNumber: " + resp.getCustomerId();
		if (resp.getCustomer() != null) {
			result += "; MobileNumber: " + resp.getCustomer().getMobileNumber();
			result += "; ContractContent: " + resp.getCustomer().getContractContent();
			result += "; CustomerId: " + resp.getCustomer().getCustomerId();
			result += "; EmailAddress: " + resp.getCustomer().getEmailAddress();
			result += "; Salutation: " + resp.getCustomer().getSalutation();
			result += "; FirstName: " + resp.getCustomer().getFirstName();
			result += "; LastName: " + resp.getCustomer().getLastName();
			result += "; TelefphoneNumber: " + resp.getCustomer().getTelephoneNumber();
			result += "; CustomerCategory: " + resp.getCustomer().getCustomerCategory();
			result += "; CampaignStamp: " + resp.getCustomer().getStamp();
			for (CampaignType ct : resp.getCustomer().getCampaign()) {
				result += "; CampaignType: " + ct.getCampaignType();
			}
			for (ServiceRequestType sr : resp.getCustomer().getServiceRequest()) {
				result += "; SR_Category: " + sr.getContractChangeCategory();
				result += "; SR_Category: " + sr.getContractChangeDate();
			}
			for (ContractType ctr : resp.getCustomer().getContract()) {
				result += "; C HouseNumber: " + ctr.getHouseNumber();
				result += "; C Staircase: " + ctr.getStaircase();
				result += "; C Floor: " + ctr.getFloor();
				result += "; C FlatNumber: " + ctr.getFlatNumber();
				result += "; C UnexpectedReturnContract: " + ctr.getUnexpectedReturnContract();
				result += "; C ContractId: " + ctr.getContractId();
				result += "; C ContractDate: " + ctr.getContractDate();
				result += "; C Street: " + ctr.getStreet();
				result += "; C ZipCode: " + ctr.getZipCode();
				result += "; C City: " + ctr.getCity();
				result += "; C AccountNumber: " + ctr.getAccountNumber();
				result += "; C BankCode: " + ctr.getBankCode();
				result += "; C BankAccountHolder: " + ctr.getBankAccountHolder();
				result += "; C Pricelist: " + ctr.getPricelist();
				result += "; C SubsStartDate: " + ctr.getSubscriptionStartDate();
				result += "; C EarmCancelDate: " + ctr.getEarmarkedCancellationDate();
//  ROTHJA - 2018.07.03 - Requested from SKY to change from CancelDate to PossibleCancellationDate
				result += "; C PossCancelDate: " + ctr.getCancelDate();
// 				result += "; C PossCancelDate: " + ctr.getPossibleCancellationDate();
				result += "; C WashMachine: " + ctr.getWMFlag();
				MandateType mandate = ctr.getMandate();
				if (mandate != null) {
					result += "; C MandateRefID: " + mandate.getMandateRefId();
					result += "; C MandateStatus: " + mandate.getMandateStatus();
					result += "; C CustomerBIC: " + mandate.getCustomerBIC();
					//result += "; C CustomerIBAN: " + mandate.getCustomerIBAN();
					result += "; C SignatureFLag: " + mandate.getSignatureFlag();
					result += "; C SignatureDate: " + mandate.getSignatureDate();
				}
				for (AssetType asset : ctr.getAsset()) {
					result += "; C SMCSerialNumber: " + asset.getSMCSerialNumber();
					result += "; C HardDiskSerialNum: " + asset.getHardDiskSerialNum();
					result += "; C HardDiskType: " + asset.getHardDiskType();
					result += "; C ReceiverType: " + asset.getReceiverType();
					result += "; C ReceiverSerialNum: " + asset.getReceiverSerialNum();
					result += "; C CIPlusModuleType: " + asset.getCIPlusModuleType();
					result += "; C CIPlusModuleSerialNum: " + asset.getCIPlusModuleSerialNum();
				}
			}
			for (DunningType dunning : resp.getCustomer().getDunning()) {
				result += "; C DunningContract: " + dunning.getDunningContractId();
				result += "; C DunningLevel: " + dunning.getDunningLevel();
			}
		} else {
			result += "Customer - NULL";
		}

		return result;
	}

	private synchronized PortType getPort() {
		if (port == null) {
			String endpointUrl = System.getProperty(SysProperty_CustomerDataService_Endpoint);
			SkyLogger.getConnectorLogger().debug("IF3.2: Using service at: " + endpointUrl);
			URL wsdlURL = getClass().getClassLoader().getResource("wsdl/IF32_NewDB/CustomerDataService.wsdl");
			SkyLogger.getConnectorLogger().debug("IF3.2: Expecting CustomerDataService.wsdl at: " + wsdlURL);

			try {
				CustomerDataServiceServiceagent ss = new CustomerDataServiceServiceagent(wsdlURL, SERVICE_NAME);
				SkyLogger.getConnectorLogger().debug("IF3.2: Initialized CustomerDataService.wsdl with: " + SERVICE_NAME);

				port = ss.getCustomerDataServiceEndpoint();
				SkyLogger.getConnectorLogger().debug("IF3.2: PortOK");

				BindingProvider provider = (BindingProvider) port;
				provider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointUrl);

				SkyLogger.getConnectorLogger().debug("IF3.2: Endpoint" + endpointUrl);

				/*Client cl = ClientProxy.getClient(port);

				HTTPConduit http = (HTTPConduit) cl.getConduit();
				HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
				httpClientPolicy.setConnectionTimeout(ConnectorFactory.TIMEOUT);
				httpClientPolicy.setReceiveTimeout(ConnectorFactory.TIMEOUT);

				http.setClient(httpClientPolicy);*/
				SkyLogger.getConnectorLogger().debug("IF3.2: finished");
			} catch (Exception e) {
				SkyLogger.getConnectorLogger().error("IF3.2: Exception during init of CustomerDataServiceServiceagent" + e.getMessage(), e);
			} catch (Throwable t) {
				SkyLogger.getConnectorLogger().error("IF3.2: RuntimeException during init of CustomerDataServiceServiceagent" + t.getMessage(), t);
			}
		}
		// SkyLogger.getConnectorLogger().info("NewDBConnectorImpl.getPort. - return port");
		return port;
	}

	public RoutingCustomer queryRoutingCustomer(String customerNumber) throws Exception {
		return queryRoutingCustomer(customerNumber, null);
	}

	public TextblockCustomer queryTextblockCustomer(IndexingCustomer indexingCustomer) throws Exception {


		TextblockCustomer customer = new TextblockCustomer(indexingCustomer);
		GetDynamicCustomerDataRequestType req = new GetDynamicCustomerDataRequestType();

		SkyLogger.getConnectorLogger().debug("IF3.2: getDynamicCustomerData for customer:"+indexingCustomer.getNumber()+" " + indexingCustomer.getFirstName()+" "+indexingCustomer.getLastName()+" "+indexingCustomer.getRowId());
		req.setCustomerId(indexingCustomer.getNumber());
		req.setContractId(indexingCustomer.getSelectedContractNumber());
		req.setRequestType("TextModule");
		req.setRequestedFields("ContractContent;CustomerCategory;EmailAddress;CustomerId;FirstName;LastName;MobileNumber;Salutation;TelephoneNumber;AccountBalance;BankAccountHolder;BankCode;City;ContractDate;ContractId;EarmarkedCancellationDate;FlatNumber;Floor;HouseNumber;PossibleCancellationDate;CancelDate;Staircase;Street;SubscriptionStartDate;ZipCode;CIPlusModuleSerialNum;CIPlusModuleType;HardDiskSerialNum;HardDiskType;ReceiverSerialNum;ReceiverType;SMCSerialNumber;DunningContractId;LastDunningLetterAmount;AccountNumber;MandateRefId;MandateStatus;CustomerBIC;SignatureDate;SignatureFlag;");

		long acurrentTimeMillis=System.currentTimeMillis();
		GetDynamicCustomerDataResponseType resp = getPort().getDynamicCustomerData(req);
		long duration = System.currentTimeMillis() - acurrentTimeMillis;
		if (duration>200) {
			SkyLogger.getConnectorLogger().warn("IF3.2: getDynamicCustomerData from NewDB: duration: " + duration + " ms Customer:" + indexingCustomer.getNumber() + " Contract:" + indexingCustomer.getSelectedContractNumber());
		}else{
			SkyLogger.getConnectorLogger().info("IF3.2: getDynamicCustomerData from NewDB: duration: " + duration + " ms Customer:" + indexingCustomer.getNumber() + " Contract:" + indexingCustomer.getSelectedContractNumber());
		}
		SkyLogger.getConnectorLogger().debug("IF3.2: getDynamicCustomerData ReceivedResponse customer:"+indexingCustomer.getNumber());

		if (resp.getResult().equals(ResultType.KO)) {
			String errText = "IF3.2: getDynamicCustomerData -> custId:"+ req.getCustomerId() +
					"; contractId: "+ req.getContractId() +
					"; retCode:" +resp.getRetCode()+"; result: "+resp.getResult().value()+"; errDesc:" + resp.getErrorDesc();
			SkyLogger.getConnectorLogger().error(errText);
			throw new Exception(errText);
		}

		if (resp.getCustomer()==null) {
			SkyLogger.getConnectorLogger().error("IF3.2:  Service returned no Customer: " + indexingCustomer.getNumber());
			throw new Exception("IF3.2:  Service returned no Customer: " + indexingCustomer.getNumber());
		}
		final String notUnique = "NOT UNIQUE";

		SkyLogger.getConnectorLogger().debug("IF3.2: "+indexingCustomer.getNumber()+" Data: " + resp.getCustomer().getSalutation() + resp.getCustomer().getFirstName()+" "+resp.getCustomer().getLastName()+" "+resp.getCustomer().getCustomerId());
		customer.setSalutation(resp.getCustomer().getSalutation());
		customer.setFirstName(resp.getCustomer().getFirstName());
		customer.setLastName(resp.getCustomer().getLastName());
		customer.setMobileNumber(resp.getCustomer().getMobileNumber());
		customer.setTelephoneNumber(resp.getCustomer().getTelephoneNumber());
		customer.setContractSkyGo(resp.getCustomer().getContractContent());
		customer.setCategory(resp.getCustomer().getCustomerCategory());
		customer.setEmailAddress(resp.getCustomer().getEmailAddress());

		if (resp.getCustomer().getDunning().isEmpty()) {
			SkyLogger.getConnectorLogger().debug("IF3.2:  No dunning information found for this customer");
		} else {
			DunningType dunningType = resp.getCustomer().getDunning().get(0);
			SkyLogger.getConnectorLogger().debug("IF3.2:  Dunning information found (lastDunningLetterAmount): " + dunningType.getLastDunningLetterAmount());

			customer.setDunningLevel(dunningType.getDunningLevel());
			customer.setDunningAmount(dunningType.getLastDunningLetterAmount());
		}

		// Contract
		SkyLogger.getConnectorLogger().debug("IF3.2: There are " + resp.getCustomer().getContract().size() + " contract for this customer. Using first contract.");
		if (resp.getCustomer().getContract().size() > 0) {
			ContractType contract = resp.getCustomer().getContract().get(0);

			customer.setSelectedContractNumber(contract.getContractId());

			customer.setStreet(contract.getStreet() + " " + contract.getHouseNumber());
			customer.setStaircase(contract.getStaircase());
			customer.setFloor(contract.getFloor());
			customer.setFlatNumber(contract.getFlatNumber());
			customer.setZipCode(contract.getZipCode());
			customer.setCity(contract.getCity());

			customer.setPricelist(contract.getPricelist());

			customer.setAccountNumberShort(contract.getAccountNumber());
			customer.setBankCode(contract.getBankCode());
			customer.setBankAccountHolder(contract.getBankAccountHolder());
			customer.setAccountBalance(contract.getAccountBalance());

			customer.setContractDate(contract.getContractDate());
			customer.setSubscriptionStartDate(contract.getSubscriptionStartDate());
//  ROTHJA - 2018.07.03 - Requested from SKY to change from CancelDate to PossibleCancellationDate
//			customer.setPossibleCancelationDate(parseCustFormatedDate(contract.getCancelDate()));
//			customer.setCancelationDate(parseCustFormatedDate(contract.getCancelDate()));
			customer.setPossibleCancelationDate(parseCustFormatedDate(contract.getPossibleCancellationDate()));
			customer.setCancelationDate(parseCustFormatedDate(contract.getPossibleCancellationDate()));
//			customer.setEarmarkedCancelationDate(contract.getEarmarkedCancellationDate());



            SkyLogger.getConnectorLogger().debug("IF3.2: " + indexingCustomer.getNumber() + " PossibleCancellationDate is " + contract.getPossibleCancellationDate());

			if (contract.getAsset().size() == 1) {
				AssetType asset = contract.getAsset().get(0);
				SkyLogger.getConnectorLogger().debug("IF3.2: There is exactly one asset for the contract");
				customer.setSerialSmartcard(asset.getSMCSerialNumber());
				customer.setSerialHarddisk(asset.getHardDiskSerialNum());
				customer.setTypeOfHarddisk(asset.getHardDiskType());
				customer.setTypeOfReceiver(asset.getReceiverType());
				customer.setSerialReceiver(asset.getReceiverSerialNum());
				customer.setTypeOfCIPlus(asset.getCIPlusModuleType());
				customer.setSerialCIPlus(asset.getCIPlusModuleSerialNum());
			} else {
				String val = notUnique;
				if (contract.getAsset().size() > 1) {
					SkyLogger.getConnectorLogger().warn("IF3.2: There are multiple assets, setting " + notUnique + " for assets");
				} else {
					SkyLogger.getConnectorLogger().info("IF3.2: Contract has no assets assigned");
					val = "";
				}
				customer.setSerialSmartcard(val);
				customer.setSerialHarddisk(val);
				customer.setTypeOfHarddisk(val);
				customer.setTypeOfReceiver(val);
				customer.setSerialReceiver(val);
				customer.setTypeOfCIPlus(val);
				customer.setSerialCIPlus(val);
			}
		} else {
			String val = "";
			SkyLogger.getConnectorLogger().info("IF3.2:   Customer has no contracts assigned");

			customer.setSelectedContractNumber(val);

			customer.setStreet(val);
			customer.setStaircase(val);
			customer.setFloor(val);
			customer.setZipCode(val);
			customer.setCity(val);

			customer.setPricelist(val);

			customer.setAccountNumberShort(val);
			customer.setBankCode(val);
			customer.setBankAccountHolder(val);

			customer.setContractDate(val);
			customer.setSubscriptionStartDate(val);
			customer.setPossibleCancelationDate(null);
			customer.setCancelationDate(null);
			customer.setEarmarkedCancelationDate(val);

			customer.setSerialSmartcard(val);
			customer.setSerialHarddisk(val);
			customer.setTypeOfHarddisk(val);
			customer.setTypeOfReceiver(val);
			customer.setSerialReceiver(val);
			customer.setTypeOfCIPlus(val);
			customer.setSerialCIPlus(val);
		}

		return customer;
	}

	private Date parseNewDbDate(String newDbDate, Date parseErrorValue) {
		if (newDbDate == null || "".equals(newDbDate)) {
			return parseErrorValue;
		}
		try {
			//SkyLogger.getConnectorLogger().debug("Parsing date: " + newDbDate);
			return (new SimpleDateFormat("dd.MM.yyyy HH:mm:ss")).parse(newDbDate);
		} catch (Exception ex) {
			SkyLogger.getConnectorLogger().warn("Exception while processing: >" + newDbDate + "<", ex);
			return parseErrorValue;
		}
	}

	private String formatNewDbDate(Date date) {
		return (new SimpleDateFormat("dd.MM.yyyy HH:mm:ss")).format(date);
	}

	/**
	 * Checks if a date is before now + days. Use negative days to check now -
	 * days.
	 *
	 * @param date
	 * @param days
	 */
	private boolean isDateBeforeOffset(Date date, int days) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, days);
		SkyLogger.getConnectorLogger().debug("IF3.2: Date " + date + " - comparing it to " + c.getTime());

		return date.before(c.getTime());
	}

	public RoutingCustomer queryRoutingCustomer(String customerNumber, String contractNumber) throws Exception {

		if (customerNumber!=null){
			customerNumber=customerNumber.trim();
		}
		if (contractNumber!=null){
			contractNumber=contractNumber.trim();
		}
	        /*
		 * Invocation of web service
		 */
		SkyLogger.getConnectorLogger().info("IF3.2: " + customerNumber + " Invoking getDynamicCustomerData: customerNumber:>" + customerNumber + "< contractNumber:>" + contractNumber+"<");

		/*
		 * Build the request object
		 */
		de.sky.integration.web.customerdataservice._1.GetDynamicCustomerDataRequestType req = new GetDynamicCustomerDataRequestType();
		//SkyLogger.getConnectorLogger().debug("IF3.2: " + customerNumber + " Initalizing");

		req.setRequestType("Routing");
		req.setRequestedFields("DunningContractId;DunningLevel;ContractChangeCategory;ContractChangeDate;CustomerCategory;EarmarkedCancellationDate;Pricelist;Stamp;SubscriptionStartDate;PossibleCancellationDate;CancelDate;ContractContent;UnexpectedReturnContract;WMFlag;CampaignType;MandateRefId;MandateStatus;CustomerBIC;SignatureDate;SignatureFlag;");
		req.setCustomerId(customerNumber);
		if (contractNumber != null && !"".equals(contractNumber) && !"0".equals(contractNumber)) {
			req.setContractId(contractNumber);
		} else {
			req.setContractId("");
		}

		/*
		 * Invoke web service
		 */
		GetDynamicCustomerDataResponseType resp;
		//SkyLogger.getConnectorLogger().debug("IF3.2: " + customerNumber + " Invoking");
		try {
			resp = getPort().getDynamicCustomerData(req);
		} catch (Exception e) {
			SkyLogger.getConnectorLogger().error("IF3.2: " + customerNumber + " exception:" + e.getMessage(), e);
			throw e;
		}
		//GetDynamicCustomerDataResponseType resp = getPort().getDynamicCustomerData(req);
		SkyLogger.getConnectorLogger().debug("IF3.2: " + customerNumber + " getDynamicCustomerData.result=" + resp);
		//SkyLogger.getConnectorLogger().debug(outputResponse(resp));

		/*
		 * Create data object
		 */
		RoutingCustomer res = new RoutingCustomer();
		if (resp.getResult().equals(ResultType.KO)) {
			SkyLogger.getConnectorLogger().error("IF3.2: " + customerNumber + " returned KO: #" + resp.getErrorDesc() + "# during requesting WS-Data for customerNumber:" + customerNumber + " contractNumber:" + contractNumber + " err:" + outputResponse(resp));
			throw new Exception("IF3.2: " + customerNumber + " returned KO:  contrNr:" + contractNumber + " err:" + resp.getErrorDesc());
		}

		res.setNumber(resp.getCustomer().getCustomerId());
		res.setCategory(resp.getCustomer().getCustomerCategory());
		res.setContractSkyGo(resp.getCustomer().getContractContent());
		res.setCampaignStamp(resp.getCustomer().getStamp());

		String campaignTypes = "";
		for (CampaignType campaignType : resp.getCustomer().getCampaign()) {
			campaignTypes += campaignType.getCampaignType() + " , ";
		}
		int index = campaignTypes.lastIndexOf(" , ");
		if (index > -1) {
			campaignTypes = campaignTypes.substring(0, index);
		}
		res.setCampaignTypes(campaignTypes);

		if (!resp.getCustomer().getDunning().isEmpty()) {
			// TODO Multiple contracts and dunning levels
			res.setDunningLevel(resp.getCustomer().getDunning().get(0).getDunningLevel());
		} else {
			res.setDunningLevel("");
		}

		// TODO: For simplicity: Assume one service request

		// Set default values
		res.setSrContractChange("N");
		res.setSrContractChangeDate("");

		// Find most recent service request contract change
		Date mostRecentDate = null;
		final Date errorDate = new Date(0);
		if (resp.getCustomer().getServiceRequest() != null) {
			for (ServiceRequestType challenge : resp.getCustomer().getServiceRequest()) {
				if (mostRecentDate == null) {
					mostRecentDate = parseNewDbDate(challenge.getContractChangeDate(), null);
				} else {
					Date challengeDate = parseNewDbDate(challenge.getContractChangeDate(), errorDate);
					if (challengeDate.after(mostRecentDate)) {
						mostRecentDate = challengeDate;
					}
				}
			}

			if (mostRecentDate != null) {
				res.setSrContractChangeDate(formatNewDbDate(mostRecentDate));
				res.setSrContractChange(isDateBeforeOffset(mostRecentDate, -42) ? "N" : "Y");
				SkyLogger.getConnectorLogger().debug("IF3.2: " + customerNumber + "  Most recent service request contract change custNr:" + customerNumber + " contrNr:" + contractNumber + " date:" + mostRecentDate);
			} else {
				SkyLogger.getConnectorLogger().debug("IF3.2: " + customerNumber + "  No dates found in Customer.getServiceRequest() custNr:" + customerNumber + " contrNr:" + contractNumber + "");
			}
		} else {
			SkyLogger.getConnectorLogger().debug("IF3.2: " + customerNumber + "  No value for Customer.getServiceRequest() is empty. custNr:" + customerNumber + " contrNr:" + contractNumber);
		}

		String washMachineFlag = "N";

		final List<ContractType> contractList = resp.getCustomer().getContract();
		if (!contractList.isEmpty()) {

			// if there is a contract id find the contract with this particular
			// number
			SkyLogger.getConnectorLogger().debug("IF3.2: " + customerNumber + " At least one contract found");
			ContractType ctr = null;

			// if contractNumber was part of the query the result will contain
			// only
			// this specific contract
			if (contractNumber != null && !"".equals(contractNumber)) {
				ctr = contractList.get(0);
				final String washMachine = ctr.getWMFlag();
				if (washMachine != null && washMachine.equals("Y")) {
					washMachineFlag = washMachine;
				}
			}

			// Return the first contract in case it has not been idenfified
			// before
			if (ctr == null) {
				ctr = contractList.get(0);
				final String washMachine = ctr.getWMFlag();
				if (washMachine != null && washMachine.equals("Y")) {
					washMachineFlag = washMachine;
				}

				/*
				 * Still in discussion SimpleDateFormat formatter = new
				 * SimpleDateFormat("dd.MM.yyyy HH:mm:ss"); for (ContractType
				 * currentContract : resp.getCustomer().getContract()) { if (ctr
				 * == null) { ctr = currentContract;
				 * SkyLogger.getConnectorLogger().debug(logPrefix +
				 * ": Newset contract subscription start date: "
				 * +ctr.getSubscriptionStartDate()); } else { final String
				 * newSubscriptionStartDate = ctr.getSubscriptionStartDate(); if
				 * (newSubscriptionStartDate!= null &&
				 * newSubscriptionStartDate.matches
				 * ("\\d\\d.\\d\\d.\\d\\d\\d\\d\\s\\d\\d:\\d\\d:\\d\\d")) {
				 * final String currentSubscriptionStartDate =
				 * currentContract.getSubscriptionStartDate(); if
				 * (currentSubscriptionStartDate == null ||
				 * formatter.parse(ctr.getSubscriptionStartDate
				 * ()).before(formatter.parse(currentSubscriptionStartDate))) {
				 * ctr = currentContract;
				 * SkyLogger.getConnectorLogger().debug(logPrefix +
				 * ": Newset contract subscription start date: "
				 * +ctr.getSubscriptionStartDate()); } } else {
				 * SkyLogger.getConnectorLogger().debug(logPrefix +
				 * ": Unexpected format for timestamp: "
				 * +ctr.getSubscriptionStartDate()); throw new
				 * Exception("Unexpected format for timestamp: "
				 * +ctr.getSubscriptionStartDate()); } }
				 * SkyLogger.getConnectorLogger().debug(logPrefix +
				 * ": Current contract: "+ctr.getContractId()); }
				 * 
				 * if (resp.getCustomer().getContract().size() > 1) {
				 * SkyLogger.getConnectorLogger().debug(logPrefix +
				 * "Selected contract based on SubscriptionStartDate: "
				 * +ctr.getContractId()); }
				 */
			}

			SkyLogger.getConnectorLogger().debug("IF3.2: " + customerNumber + " SubscriptionStartDate is " + ctr.getSubscriptionStartDate() + " - comparing it to " + ctr.getSubscriptionStartDate() + " -42 days");
			res.setSubscriptionStartDateBeforeLimit(isDateBeforeOffset(parseNewDbDate(ctr.getSubscriptionStartDate(), new Date()), -42));
			SkyLogger.getConnectorLogger().debug("IF3.2: " + customerNumber + " SubscriptionStartDate is older than six weeks? " + (res.isSubscriptionStartDateBeforeLimit() ? "Y" : "N"));
			res.setSelectedContractNumber(ctr.getContractId());
			res.setEarmarkedCancelationDate(ctr.getEarmarkedCancellationDate());
//  ROTHJA - 2018.07.03 - Requested from SKY to change from CancelDate to PossibleCancellationDate using TextModule instead of Routing
//			SIT-18-07-004: "Anpassung von Feld ‚PossibleCancellationDate‘ in IF3.2" / Feasibility Check
//          The field is changed at the end of this methode.
//			res.setPossibleCancellationDate(parseCustFormatedDate(ctr.getCancelDate()));
			if (ctr.getCancelDate()!=null){
				res.setCancellationDate(parseCustFormatedDate(ctr.getCancelDate()));
			}
//			res.setPossibleCancellationDate(parseCustFormatedDate(ctr.getPossibleCancellationDate()));
//			res.setCancellationDate(parseCustFormatedDate(ctr.getPossibleCancellationDate()));
			if (ctr.getCancelDate()!=null){
				SkyLogger.getConnectorLogger().debug("IF3.2: " + customerNumber + " PossibleCancellationDate is " + ctr.getCancelDate());
			}else {
				SkyLogger.getConnectorLogger().debug("IF3.2: " + customerNumber + " PossibleCancellationDate is null");
			}
			res.setPricelist(ctr.getPricelist());
			res.setSubscriptionStartDate(ctr.getSubscriptionStartDate());

			if (!washMachineFlag.equals("Y")) {
				for (ContractType contract : contractList) {
					final String washMachine = contract.getWMFlag();
					if (washMachine != null && washMachine.equals("Y")) {
						washMachineFlag = "D";
						break;
					}

				}
			}

			// Sets Mandate data.
			MandateType mandate = ctr.getMandate();
			if (mandate != null) {
				res.setMandateNumber(mandate.getMandateRefId());
				res.setMandateStatus(mandate.getMandateStatus());
				res.setCustomerBIC(mandate.getCustomerBIC());
				//res.setCustomerIBAN(mandate.getCustomerIBAN());
			}
		} else {
			res.setEarmarkedCancelationDate("");
			res.setPricelist("");
			res.setSubscriptionStartDate("");
			res.setPossibleCancellationDate(null);
			res.setCustomerContractQuantity(0);
			res.setActiveContractQuantity(0);
		}


		//Set PossibleCancellationDate from RequestType as TextModule
		//SIT-18-07-004: "Anpassung von Feld ‚PossibleCancellationDate‘ in IF3.2" / Feasibility Check
		IndexingCustomer indexingCustomer = new IndexingCustomer();

		indexingCustomer.setNumber(customerNumber);
		indexingCustomer.setSelectedContractNumber(contractNumber);

		TextblockCustomer textblockCustomer = (TextblockCustomer) queryTextblockCustomer(indexingCustomer);
		res.setPossibleCancellationDate(textblockCustomer.getPossibleCancelationDate());
		res.setCancellationDate(textblockCustomer.getPossibleCancelationDate());

		res.setWashMachineFlag(washMachineFlag);
		return res;
	}

	protected static final String sblDateFormatPattern = "MM/dd/yyyy"; //05/08/2015
	private static synchronized String getSblFormattedDate(java.util.Date tsdate) {
		try {
			return (new SimpleDateFormat(sblDateFormatPattern)).format(tsdate);
		}catch (Exception e){
			return null;
		}
	}
	private static synchronized java.util.Date parseSblFormatedDate(String date){
		try {
			return	(new SimpleDateFormat(sblDateFormatPattern)).parse(date);
		} catch (ParseException e) {
			SkyLogger.getItyxLogger().error("Problem during Parsing ArchiveDate: " + date, e);
			return null;
		}
	}

	protected static final String custDateFormatPattern = "dd.MM.yyyy HH:mm:ss"; //30.04.2019 23:23:23
	private static synchronized String getCustFormattedDate(java.util.Date tsdate) {
		try {
			return (new SimpleDateFormat(custDateFormatPattern)).format(tsdate);
		}catch (Exception e){
			return null;
		}
	}
	private static synchronized java.util.Date parseCustFormatedDate(String date){
		try {
			return	(new SimpleDateFormat(custDateFormatPattern)).parse(date);
		} catch (ParseException e) {
			SkyLogger.getItyxLogger().error("Problem during Parsing ArchiveDate: " + date, e);
			return null;
		}
	}


	protected static final String mxDateFormatPattern = "yyyy.MM.dd"; //05/08/2015
	private static synchronized String getMxFormatedDate(java.util.Date tsdate) {
		return (new SimpleDateFormat(mxDateFormatPattern)).format(tsdate);
	}

	private static synchronized java.util.Date parseMXFormatedDate(String date){
		try {
			return	(new SimpleDateFormat(mxDateFormatPattern)).parse(date);
		} catch (ParseException e) {
			SkyLogger.getItyxLogger().error("Problem during Parsing ArchiveDate: " + date, e);
			return null;
		}
	}
}
