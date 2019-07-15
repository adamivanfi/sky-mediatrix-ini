package com.nttdata.de.sky.connector.siebel;

import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.lib.utils.MitarbeiterlogWriter;
import com.nttdata.de.sky.connector.CreateContactParameter;
import com.nttdata.de.sky.connector.ISiebel;
import com.nttdata.de.sky.connector.UpdateContactParameter;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.sky.integration.web.siebelservice._1.*;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SiebelConnectorImpl implements ISiebel {

	public static final String USECASE_IF41 = "DMS IF 4.1";
	public static final String USECASE_IF42 = "DMS IF 4.2";
	public static final String USECASE_IF44 = "DMS IF 4.4";
	public static final String USECASE_IF46 = "DMS IF 4.6";
	public static final String SysProperty_SiebelService_Endpoint = "com.nttdata.de.sky.connector.siebelservice.endpoint";
	private static final QName SERVICE_NAME = new QName("http://www.sky.de/integration/web/SiebelService/1.0", "SiebelService");
	private PortType port = null;

	public static String outputCreateContactRequest(de.sky.integration.web.siebelservice._1.CreateContactRequestType req) {
		String result = "";

		result += " docId: " + req.getDocumentId();
		result += "; customerId: " + req.getCustomerId();
		result += "; contractId: " + req.getContractId();
		result += "; channel: " + req.getChannel();
		result += "; mandateRefId: " + req.getMandateRefId();
		result += "; signatureFlag: " + req.getSignatureFlag();
		result += "; signatureDate: " + req.getSignatureDate();

		return result;
	}

	public static String outputMsgFault(MsgFault e){
		return ((e.getFaultInfo() != null)?"; errorCode: " + e.getFaultInfo().getErrorCode() + "; errorDesc: " + e.getFaultInfo().getErrorDesc():"; faultInfo: null") +
				"; errorMessage: " + e.getMessage();
	}

	/**
	 * @param channel
	 * @param validitiy
	 * @return
	 */
	public static boolean checkSignedMandateLetter(String channel, String validitiy) {
		//		return Channel.valueOf(channel).equals(Channel.BRIEF) && validitiy != null && validitiy.equals("signed");
		return validitiy != null && (validitiy.equals("signed") || validitiy.equals("1"));
	}

	private synchronized PortType getPort() throws Exception {
		SiebelService ss;
		if (port == null) {
			String endpointUrl = System.getProperty(SysProperty_SiebelService_Endpoint);

			SkyLogger.getConnectorLogger().debug("IF4.X Using endpoint: " + endpointUrl);
			URL wsdlURL = getClass().getClassLoader().getResource("wsdl/IF4_Siebel/SiebelService.wsdl");

			ss = new SiebelService(wsdlURL, SERVICE_NAME);
			port = ss.getSiebelServiceEndpoint();

			BindingProvider provider = (BindingProvider) port;
			provider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointUrl);
/*
			Client cl = ClientProxy.getClient(port);

			HTTPConduit http = (HTTPConduit) cl.getConduit();
			HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
			httpClientPolicy.setConnectionTimeout(ConnectorFactory.TIMEOUT);
			httpClientPolicy.setReceiveTimeout(ConnectorFactory.TIMEOUT);

			http.setClient(httpClientPolicy);
			*/
		}
		return port;
	}

	@Override
	public void createContact(CreateContactParameter parameter) throws Exception {
		SkyLogger.getConnectorLogger().debug("IF4.1: " + parameter.documentId + " createContact:" + parameter.customerId +
				"/" + parameter.contractId + "/" + parameter.direction + "/" + parameter.documentType + "/" + parameter.mandateNumber);
		de.sky.integration.web.siebelservice._1.CreateContactRequestType req = new CreateContactRequestType();

		req.setChannel(parameter.channel.toString());
		req.setDirection(parameter.direction.toString());
		req.setContractId(parameter.contractId);
		req.setDocumentId(parameter.documentId);
		req.setCustomerId(parameter.customerId);
		req.setDocumentType(parameter.documentType);
		req.setMandateRefId(parameter.mandateNumber);
		if (parameter.mandateNumber != null && parameter.documentType != null && parameter.documentType.equalsIgnoreCase(TagMatchDefinitions.SEPA_MANDATE)) {
			//old code Ticket 251448 the signatureflag will be always 'Y'
			//req.setSignatureFlag(checkSignedMandateLetter(parameter.channel.toString(), parameter.signatureFlag) ? FlagType.Y : FlagType.N);
			req.setSignatureFlag(FlagType.Y);
			req.setSignatureDate(parameter.signatureDate);
		}
		req.setUseCaseId(USECASE_IF41);
		req.setCorrelationId(req.getDocumentId());

		String strObjInfo = outputCreateContactRequest(req);
		SkyLogger.getConnectorLogger().debug("IF4.1: " + parameter.documentId + "; createContact req -> " + strObjInfo);

		try {
			de.sky.integration.web.siebelservice._1.CreateContactResponseType resp = getPort().createContact(req);
			SkyLogger.getConnectorLogger().debug("IF4.1: " + parameter.documentId + "createContact resp -> " + resp.toString());

		} catch (MsgFault e) {
			SkyLogger.getConnectorLogger().error("IF4.1: " + parameter.documentId + "; createContact MsgFault -> " + strObjInfo + outputMsgFault(e), e);
			throw new Exception("Exception: WSMsgFault. IF 4.1: createContact " + parameter.documentId + "/" + parameter.customerId + "/" + parameter.contractId + "/" + parameter.direction.toString(), e);
		}
	}

	@Override
	public void associateDocumentIdToActivity(String documentId, String siebelActivityId, Channel channel, Direction direction) throws Exception {

		SkyLogger.getConnectorLogger().debug("IF4.4: " + documentId + " associateDocumentId: " + siebelActivityId + "/" + channel + "/" + direction);
		de.sky.integration.web.siebelservice._1.AssociateDocumentIdRequestType req = new de.sky.integration.web.siebelservice._1.AssociateDocumentIdRequestType();

		req.setDocumentId(documentId);
		req.setActivityId(siebelActivityId);
		req.setChannel(channel.toString());
		req.setDirection(direction.toString());

		req.setUseCaseId(USECASE_IF44);
		req.setCorrelationId(req.getDocumentId());

		try {
			de.sky.integration.web.siebelservice._1.AssociateDocumentIdResponseType resp = getPort().associateDocumentId(req);
			SkyLogger.getConnectorLogger().debug("IF4.4: " + documentId + " associateDocumentId " + siebelActivityId + " result=" + resp);
		} catch (MsgFault e) {
			SkyLogger.getConnectorLogger().error("IF4.4: " + documentId + " associateDocumentID Web Service Response MsgFault has occurred." + documentId + "/" + siebelActivityId + " errorcode:>" + e.getFaultInfo().getErrorCode() + "< errorDesc:>" + e.getFaultInfo().getErrorDesc() + "<", e);
			throw new Exception("IF4.4: " + documentId + " associateDocumentID Web Service Response MsgFault has occurred." + "/" + siebelActivityId, e);
		}
	}

	@Override
	public void associateDocumentIdWithoutActivityId(String documentId, String customerId, String contractId, String contactId) throws Exception {
		SkyLogger.getConnectorLogger().debug("IF4.2: " + documentId + " associateDocumentIdWithoutActivityId");

		de.sky.integration.web.siebelservice._1.CreateTrackingSRRequestType req = new de.sky.integration.web.siebelservice._1.CreateTrackingSRRequestType();

		req.setDocumentId(documentId);
		req.setCustomerId(customerId);
		req.setContractId(contractId);
		req.setContactId(contactId);
		req.setCorrelationId(documentId);
		req.setUseCaseId(USECASE_IF42);
		SkyLogger.getConnectorLogger().debug("IF4.2: " + documentId + " associateDocumentIdWithoutActivityId  custid: " + customerId + ": contrid: " + contactId + ";");

		try {
			de.sky.integration.web.siebelservice._1.CreateTrackingSRResponseType resp = getPort().createTrackingSR(req);
			SkyLogger.getConnectorLogger().debug("IF4.2: " + documentId + " associateDocumentIdWithoutActivityId. OK custid: " + customerId + "; result: " + resp);
		} catch (MsgFault e) {
			SkyLogger.getConnectorLogger().error("IF4.2: " + documentId + " associateDocumentIdWithoutActivityId. exception: MsgFault has occurred custid: " + customerId + "; contrid: " + contactId + outputMsgFault(e), e);
			throw new Exception(e.getMessage(), e);
		}
	}

	@Override
	public String updateContactOfDocument(UpdateContactParameter parameter) throws Exception {
		de.sky.integration.web.siebelservice._1.AdjustSRRequestType req = new de.sky.integration.web.siebelservice._1.AdjustSRRequestType();

		req.setDocumentId(parameter.documentId);

		String customerIdNewP = (parameter.customerIdNew == null || parameter.customerIdNew.isEmpty()) ? "0" : parameter.customerIdNew;
		req.setCustomerIdNew(customerIdNewP);

		String contractIdNewP = (parameter.contractIdNew == null || parameter.contractIdNew.isEmpty()) ? "0" : parameter.contractIdNew;
		req.setContractIdNew(contractIdNewP);

		String channel = (parameter.channel == null || parameter.channel.isEmpty()) ? TagMatchDefinitions.Channel.EMAIL.toString() : parameter.channel;
		req.setChannel(channel);

		String doctype = (parameter.doctype == null || parameter.doctype.isEmpty()) ? "unclassified" : parameter.doctype;
		req.setDocumentType(doctype);

		String contactIdP = (parameter.contactId == null || parameter.contactId.isEmpty()) ? "0" : parameter.contactId;
		req.setContactId(contactIdP);

		String direction = (parameter.direction == null || parameter.direction.isEmpty()) ? "INBOUND" : parameter.direction;
		req.setDirection(direction);

		String mandateNumber = (parameter.mandateNumber == null || parameter.mandateNumber.isEmpty()) ? "0" : parameter.mandateNumber;
		if (!"0".equals(mandateNumber)) {
			req.setMandateRefId(mandateNumber);
			if (mandateNumber != null) {
				//old code Ticket 251448 the signatureflag will be always 'Y'
				//req.setSignatureFlag(checkSignedMandateLetter(channel, parameter.signatureFlag) ? FlagType.Y : FlagType.N);
				req.setSignatureFlag(FlagType.Y);
				req.setSignatureDate(parameter.signatureDate);
			}
		}

		SkyLogger.getConnectorLogger().debug("IF4.5: adjustSR reg -> docId: " + req.getDocumentId() + "; customerIdNew: " + req.getCustomerIdNew() +
				"; contractIdNew: " + req.getContractIdNew() + "; contactId: " + req.getContactId() + "; channel: " + req.getChannel() +
				"; docType: " + req.getDocumentType() + "; direction: " + req.getDirection() + "; mandateRefId: " + req.getMandateRefId() +
				"; signatureFlag: " + req.getSignatureFlag() + "; signatureDate: " + req.getSignatureDate());

		de.sky.integration.web.siebelservice._1.AdjustSRResponseType resp = null;
		try {
			resp = getPort().adjustSR(req);
			SkyLogger.getConnectorLogger().debug("IF4.5: "+req.getDocumentId()+ "; adjustSR resp -> contactid: " + resp.getContactIdNew() + "; status: " + resp.getStatus().toString());
			return resp.getContactIdNew();
		} catch (MsgFault e) {
            String errText = "IF4.5: " + req.getDocumentId() + "; adjustSR MsgFault -> customerIdNew: " +req.getCustomerIdNew() +
                    "; contactid: " + req.getContactId() + "; contractid: " + req.getContractIdNew()+ "; channel: " + req.getChannel() + outputMsgFault(e);
            if (parameter.questionId != null && parameter.questionId.intValue()>0){
                MitarbeiterlogWriter.writeMitarbeiterlog( 0, parameter.questionId.intValue(), 0, 19, errText, System.currentTimeMillis(), true);
            }
			SkyLogger.getConnectorLogger().error(errText, e);
			throw new Exception(errText, e);
		}
	}



	@Override
	public void triggerSRQuickAction_Cancellation(String documentId, String customerId, String contractId,
												  String srContactId,String srId, Date cancellationDate, Date possibleCancellationDate,
												  String cancellationReasonCode,
												  String cancellationReasonFreeText) throws Exception {
		TriggerSRQuickActionRequestType req = new TriggerSRQuickActionRequestType();

		assert customerId!=null && !customerId.isEmpty() && !customerId.trim().equals("0");
		assert srContactId!=null&& !srContactId.isEmpty() && !srContactId.trim().equals("0");
		assert documentId!=null&& !documentId.isEmpty() && !documentId.trim().equals("0");
		assert contractId!=null&& !contractId.isEmpty() && !contractId.trim().equals("0");
		assert cancellationDate!=null;
		assert cancellationReasonCode!=null&& !cancellationReasonCode.isEmpty() && !cancellationReasonCode.trim().equals("null");

		if(srId==null || srId.isEmpty()) {
			srId = "0";
		}
		req.setCustomerId(customerId);
		req.setContractId(contractId);
		req.setContactId(srContactId);
		req.setCorrelationId(documentId);
		req.setServiceRequestId(srId);
		req.setProcess("CANCELLATION");
		req.setUseCaseId(USECASE_IF46);

		ProcessAttributesType processAttributesType = new ProcessAttributesType();
		req.setProcessAttributes(processAttributesType);
		List<AttributeType> attribute = processAttributesType.getAttribute();

		AttributeType pa_cancellationDate=new AttributeType();
		pa_cancellationDate.setKey("CancellationDate");
		String cadate=getFormattedDate(cancellationDate);
		pa_cancellationDate.setValue(cadate);

		attribute.add(pa_cancellationDate);

		if (possibleCancellationDate!=null) {
			AttributeType pa_lastCancellationDate = new AttributeType();
			pa_lastCancellationDate.setKey("PossibleCancellationDate");
			String pcadate = getFormattedDate(possibleCancellationDate);
			pa_lastCancellationDate.setValue(pcadate);
			attribute.add(pa_lastCancellationDate);
		}

		AttributeType pa_cancellationReasonCode=new AttributeType();

		pa_cancellationReasonCode.setKey("CancellationReasonCode");
		pa_cancellationReasonCode.setValue(cancellationReasonCode);
		attribute.add(pa_cancellationReasonCode);

		AttributeType pa_cancellationReasonFreeText=new AttributeType();
		pa_cancellationReasonFreeText.setKey("CancellationReasonFreeText");
		pa_cancellationReasonFreeText.setValue(cancellationReasonFreeText);
		attribute.add(pa_cancellationReasonFreeText);

		SkyLogger.getConnectorLogger().debug("IF4.6: " + documentId + " pretriggerSRQuickAction_Cancellation: starting: " +
				"cust:" + customerId + " contract:" + contractId + " contact:" + srContactId + " SRID:" + srId + " cancelationDate:" + cadate + " possibleCancellationDate:" + possibleCancellationDate + " cancellationReasonCode:" + cancellationReasonCode + " pa_cancellationReasonFreeText:" + cancellationReasonFreeText);


		TriggerSRQuickActionResponseType resp=null;
		try {
			resp = getPort().triggerSRQuickAction(req);
			SkyLogger.getConnectorLogger().debug("IF4.6: " + documentId + " post triggerSRQuickAction_Cancellation");
		} catch (MsgFault e) {
			if (resp != null) {
				SkyLogger.getConnectorLogger().error("IF4.6: " + documentId + " triggerSRQuickAction_Cancellation exception: MsgFault Status:" + resp.getStatus() + outputMsgFault(e), e);
			} else {
				SkyLogger.getConnectorLogger().error("IF4.6: " + documentId + " triggerSRQuickAction_Cancellation exception: MsgFault has occurred:  customerid: " + req.getCustomerId() + "; contactid: " + req.getContactId() + "; contractid: " + req.getContractId() + outputMsgFault(e), e);
			}
			throw new Exception("IF4.6: " + documentId + " triggerSRQuickAction_Cancellation Web Service Response: MsgFault  " +
					" customerid: " + req.getCustomerId() + ": contactid: " + req.getContactId() + "; contractid:" + req.getContractId() + outputMsgFault(e), e);
		}
	}

	protected static final String dateFormatPattern = "MM/dd/yyyy"; //05/08/2015

	private static synchronized String getFormattedDate(Date tsdate) {
		return (new SimpleDateFormat(dateFormatPattern)).format(tsdate);
	}

}
