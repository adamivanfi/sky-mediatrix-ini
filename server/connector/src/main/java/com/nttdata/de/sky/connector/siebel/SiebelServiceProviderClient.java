package com.nttdata.de.sky.connector.siebel;

import com.nttdata.de.lib.logging.SkyLogger;
import de.sky.integration.web.siebelservice._1.CreateContactRequestType;
import de.sky.integration.web.siebelservice._1.MsgFault;
import de.sky.integration.web.siebelservice._1.PortType;
import de.sky.integration.web.siebelservice._1.SiebelService;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import java.net.URL;

public class SiebelServiceProviderClient {
	private static final QName SERVICE_NAME = new QName("http://www.sky.de/integration/web/SiebelService/1.0", "SiebelService");

	public static void main(String args[]) throws java.lang.Exception {
		URL wsdlURL = ClassLoader.getSystemResource("wsdl/IF4_Siebel/SiebelService.wsdl");
		String endpointUrl = args[0];

		SiebelService ss = new SiebelService(wsdlURL, SERVICE_NAME);
		PortType port = ss.getSiebelServiceEndpoint();

		BindingProvider provider = (BindingProvider) port;
		provider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointUrl);

		if (args.length > 1 && args[1] != null && !"".equals(args[1])) {
			if ("adjustSR".equalsIgnoreCase(args[1]) && args.length >= 6) {
				if (args.length < 5) {
					SkyLogger.getConnectorLogger().info("Expected arguments: <ContactId> <CustomerIdNew> <ContractIdNew> <DocId>");
					SkyLogger.getConnectorLogger().info("Example: 6123 4123 5123 123");
					System.exit(1);
				}
				SkyLogger.getConnectorLogger().info("Invoking adjustSR...");
				de.sky.integration.web.siebelservice._1.AdjustSRRequestType _adjustSR_adjustSRRequest = new de.sky.integration.web.siebelservice._1.AdjustSRRequestType();
				try {
					_adjustSR_adjustSRRequest.setContactId(args[2]);
					_adjustSR_adjustSRRequest.setCustomerIdNew(args[3]);
					_adjustSR_adjustSRRequest.setContractIdNew(args[4]);
					_adjustSR_adjustSRRequest.setDocumentId(args[5]);

					de.sky.integration.web.siebelservice._1.AdjustSRResponseType _adjustSR__return = port.adjustSR(_adjustSR_adjustSRRequest);
					SkyLogger.getConnectorLogger().info("adjustSR.result=" + _adjustSR__return);

				} catch (MsgFault e) {
					SkyLogger.getConnectorLogger().info("Expected exception: MsgFault has occurred.");
					SkyLogger.getConnectorLogger().info(e.toString());
				}
			} else if ("associateDocumentId".equalsIgnoreCase(args[1])) {
				if (args.length < 7) {
					SkyLogger.getConnectorLogger().info("Expected arguments: <ActivityId> EMAIL|FAX|BRIEF <CorrId> INBOUND|OUTBOUND <DocId> <UseCaseId>");
					SkyLogger.getConnectorLogger().info("Example: 1-A123 email 123 outbound 123 IF4.4");
					System.exit(1);
				}
				SkyLogger.getConnectorLogger().info("Invoking associateDocumentId...");
				de.sky.integration.web.siebelservice._1.AssociateDocumentIdRequestType _associateDocumentId_associateDocumentIdRequest = new de.sky.integration.web.siebelservice._1.AssociateDocumentIdRequestType();
				try {

					_associateDocumentId_associateDocumentIdRequest.setActivityId(args[2]);
					_associateDocumentId_associateDocumentIdRequest.setChannel(args[3]);
					_associateDocumentId_associateDocumentIdRequest.setCorrelationId(args[4]);
					_associateDocumentId_associateDocumentIdRequest.setDirection(args[5]);
					_associateDocumentId_associateDocumentIdRequest.setDocumentId(args[6]);
					_associateDocumentId_associateDocumentIdRequest.setUseCaseId(args[7]);


					de.sky.integration.web.siebelservice._1.AssociateDocumentIdResponseType _associateDocumentId__return = port.associateDocumentId(_associateDocumentId_associateDocumentIdRequest);
					SkyLogger.getConnectorLogger().info("associateDocumentId.result=" + _associateDocumentId__return);

				} catch (MsgFault e) {
					SkyLogger.getConnectorLogger().info("Expected exception: MsgFault has occurred.");
					SkyLogger.getConnectorLogger().info(e.toString());
				}
			} else if ("createContact".equalsIgnoreCase(args[1])) {
				if (args.length < 9) {
					SkyLogger.getConnectorLogger().info("Expected arguments: EMAIL|FAX|BRIEF INBOUND|OUTBOUND <ContractId> <DocId> <CustomerId> <DocType> <UseCaseId> <UseCaseId>");
					SkyLogger.getConnectorLogger().info("Example: email INBOUND 99123456789 123 4123456789 unclassified IF4.4");
					System.exit(1);
				}
				SkyLogger.getConnectorLogger().info("Invoking createContact...");

				de.sky.integration.web.siebelservice._1.CreateContactRequestType _createContact_createContactRequest = new CreateContactRequestType();

				_createContact_createContactRequest.setChannel(args[2]);
				_createContact_createContactRequest.setDirection(args[3]);
				_createContact_createContactRequest.setContractId(null);
				_createContact_createContactRequest.setDocumentId(args[5]);
				_createContact_createContactRequest.setCustomerId(args[6]);
				_createContact_createContactRequest.setDocumentType(args[7]);
				_createContact_createContactRequest.setUseCaseId(args[8]);
				_createContact_createContactRequest.setCorrelationId(_createContact_createContactRequest.getDocumentId());


				try {
					de.sky.integration.web.siebelservice._1.CreateContactResponseType _createContact__return = port.createContact(_createContact_createContactRequest);
					SkyLogger.getConnectorLogger().info("createContact.result=" + _createContact__return);

					SkyLogger.getConnectorLogger().info("Result : " + _createContact__return.getStatus());

				} catch (MsgFault e) {
					SkyLogger.getConnectorLogger().info("Expected exception: MsgFault has occurred.");
					SkyLogger.getConnectorLogger().info(e.toString());
				}
			} else {
				SkyLogger.getConnectorLogger().info("No method selected!");
			}
		} else {
			SkyLogger.getConnectorLogger().info("Add parameter method (adjustSR|associateDocumentId|createContact)");
		}
		System.exit(0);
	}
}
