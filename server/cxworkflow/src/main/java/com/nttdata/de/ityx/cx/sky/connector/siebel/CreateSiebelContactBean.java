package com.nttdata.de.ityx.cx.sky.connector.siebel;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.ityx.sharedservices.utils.ShedulerUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.connector.ConnectorFactory;
import com.nttdata.de.sky.connector.CreateContactParameter;
import com.nttdata.de.sky.connector.ISiebel;
import com.nttdata.de.sky.connector.siebel.SiebelConnectorImpl;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.document.CDocument;
import de.ityx.contex.interfaces.extag.TagMatch;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CreateSiebelContactBean extends AbstractWflBean {

    private static final long serialVersionUID = 2223470949278884534L;

    @Override
    public void execute(IFlowObject flowObject) throws Exception {
        String simException = (String) flowObject.get("CreateSiebelContactBean.simException");

        /* Find out which service we want to connect to */
        Boolean serviceActive = (Boolean) flowObject.get("Siebel_Enabled");
        if (serviceActive == null || !serviceActive) {
            SkyLogger.getConnectorLogger().warn("IF4.X SiebelService is not active");
            return;
        }

        /* WSDL Url for Siebel Service */
        String serviceUrl = (String) flowObject.get("Siebel_WSDL");
        if (serviceUrl == null || "".equals(serviceUrl)) {
            serviceUrl = "http://www.sky.de/integration/web/SiebelService?WSDL";
        }
        System.setProperty(SiebelConnectorImpl.SysProperty_SiebelService_Endpoint, serviceUrl);

        if (simException != null && simException.equalsIgnoreCase(TagMatchDefinitions.TRUE)) {
            SkyLogger.getConnectorLogger().warn("IF4.1 SimulatedException");
            throw new Exception("Simulated Exception");
        }

        CDocument document = DocContainerUtils.getDoc(flowObject);
        DocContainerUtils.mergeTagsAfterVcat(flowObject, document);
        CreateContactParameter cparam = getContactParameter(document,DocContainerUtils.getDocContainer(flowObject));
        if (cparam != null) {
            ConnectorFactory.getSiebelInstance().createContact(cparam);
        }

    }

    protected CreateContactParameter getContactParameter(CDocument document, CDocumentContainer cont) throws Exception {
        ISiebel.Channel channel = getSChannel(document);
        ISiebel.Direction direction = ISiebel.Direction.INBOUND;
        String documentType = DocContainerUtils.getFormtype(document);
        /* UniqueDocumentId */
        String docid = DocContainerUtils.getDocID(document);
        if (docid == null || "".equals(docid)) {
            SkyLogger.getConnectorLogger().error("IF4.1: CreateSiebelContactBean: DocID is necessary for SiebelService");
            throw new Exception("IF4.1: CreateSiebelContactBean: Flow object does not carray a " + TagMatchDefinitions.DOCUMENT_ID);
        }
        /* VerifiedCustomerNumber */
        String verifiedCustomerNumber = (String) document.getNote(TagMatchDefinitions.CUSTOMER_ID);

        /* VerifiedContractNumber */
        String verifiedContractNumber = (String) document.getNote(TagMatchDefinitions.TAGMATCH_CONTRACT_NUMBER);

        SkyLogger.getConnectorLogger().debug("IF4.1: CreateSiebelContactBean: DocID: " + docid + " CustId:" + verifiedCustomerNumber + " contr:" + verifiedContractNumber + " channel:" + channel.toString() + " " + TagMatchDefinitions.DOCUMENT_TYPE + ": " + documentType);
        if (DocContainerUtils.isEmpty(verifiedCustomerNumber)) {
            SkyLogger.getConnectorLogger().error("IF4.1: CreateSiebelContactBean VerifiedCustomerNumber is not properly set - not calling Siebel interface: DocID: " + docid + " CustId:" + verifiedCustomerNumber + " contr:" + verifiedContractNumber + " channel:" + channel.toString() + " " + TagMatchDefinitions.DOCUMENT_TYPE + ": " + documentType);
            ShedulerUtils.storeDocAndScheduleProcess(ShedulerUtils.getDefaultUnit(), ShedulerUtils.getDefaultMaster(), "600_MXInjection", cont, document);
        }
        /* MandateNumber */
        String mandateNumber = (String) document.getNote(TagMatchDefinitions.SEPA_MANDATE_NUMBER);
        if (DocContainerUtils.isEmpty(mandateNumber)) {
            mandateNumber = null;
        }
        SkyLogger.getConnectorLogger().info("IF4.1: " + docid + " MandateNumber: " + mandateNumber);
        String signatureFlag = null;
        String signatureDate = null;
        if (mandateNumber != null) {

            /* SignatureFlag */
            signatureFlag = (String) document.getNote(TagMatchDefinitions.SEPA_SIGNATURE_FLAG);
            SkyLogger.getConnectorLogger().info("IF4.1: " + docid + " SignatureFlag: " + signatureFlag);

            /* SignatureDate */
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            Date docDate = DocContainerUtils.getCreationDate(document);
            List<TagMatch> tags = document.getTags();
            if (tags != null) {
                for (TagMatch tm : tags) {
                    if (tm.getIdentifier().equals("DateTag")) {
                        String date = tm.getTagValue(TagMatchDefinitions.SEPA_SIGNATURE_DATE);
                        if (date != null) {
                            try {
                                Date sigDate = dateFormat.parse(date);
                                if (sigDate != null && (docDate==null || sigDate.before(docDate))) {
                                    signatureDate = date;
                                }
                            } catch (ParseException e) {
                                SkyLogger.getConnectorLogger().debug("IF4.1: " + docid + " Wrong date format: " + e.getMessage());
                            }
                        }
                    }
                }
            }
            if (signatureDate == null) {
                signatureDate = dateFormat.format(docDate);
            }
            document.setNote(TagMatchDefinitions.SEPA_SIGNATURE_DATE, signatureDate);
            SkyLogger.getItyxLogger().debug("SignatureDate: " + signatureDate);
        }
        SkyLogger.getConnectorLogger().info("IF4.1: " + docid + " CustId:" + verifiedCustomerNumber + " contr:" + verifiedContractNumber + " channel:" + channel.toString() + " " + TagMatchDefinitions.DOCUMENT_TYPE + ": " + documentType
                + " MandateRefID:" + mandateNumber + " SigFlag:" + signatureFlag + " SigDate" + signatureDate);
        if (mandateNumber != null && documentType != null && documentType.equalsIgnoreCase(TagMatchDefinitions.SEPA_MANDATE)) {
            SkyLogger.getConnectorLogger().info("IF4.1: mit Mandate Nummer");
            return new CreateContactParameter(docid, channel, direction, documentType, verifiedCustomerNumber, verifiedContractNumber, mandateNumber, signatureFlag, signatureDate);
        } else {
            SkyLogger.getConnectorLogger().info("IF4.1: ohne Mandate Nummer");
            return new CreateContactParameter(docid, channel, direction, documentType, verifiedCustomerNumber, verifiedContractNumber, null, null, null);
        }
    }

    protected ISiebel.Channel getSChannel(CDocument document) {
        ISiebel.Channel schannel = ISiebel.Channel.EMAIL;
        String channelString = DocContainerUtils.getChannel(document);
        if (channelString != null && channelString.trim().length() > 0) {
            schannel = ISiebel.Channel.valueOf(channelString);
        } else {
            SkyLogger.getConnectorLogger().warn("IF4.1: Channel not set - defaulting to EMAIL");
        }
        return schannel;
    }
}
