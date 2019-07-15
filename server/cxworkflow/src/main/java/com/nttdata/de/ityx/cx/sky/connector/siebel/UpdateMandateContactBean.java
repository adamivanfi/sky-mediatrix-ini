package com.nttdata.de.ityx.cx.sky.connector.siebel;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.connector.ConnectorFactory;
import com.nttdata.de.sky.connector.UpdateContactParameter;
import com.nttdata.de.sky.connector.siebel.SiebelConnectorImpl;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.contex.impl.designer.exflow.maps.TextParameter;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.designer.IParameterMap;
import de.ityx.contex.interfaces.designer.Parameter;

import java.util.Iterator;

public class UpdateMandateContactBean extends AbstractWflBean {

    @Override
    public void execute(IFlowObject flowObject) throws Exception {
        IParameterMap inputMap = flowObject.getInputMap("modelMap");
        String docid = getInputParameterAsString(inputMap, TagMatchDefinitions.DOCUMENT_ID);

        SkyLogger.getConnectorLogger().debug("IF4.5S: " + docid + " enter");

        if (SkyLogger.getConnectorLogger().isDebugEnabled()) {
            String inputNameParamsDebug = "";
            for (Iterator it = inputMap.names(); it.hasNext(); ) {
                inputNameParamsDebug += ": " + it.next().toString();

            }
            SkyLogger.getConnectorLogger().debug("IF4.5S: " + docid + " :" + inputNameParamsDebug);
        }

        if (docid.length() > 0) {
            flowObject.put("documentid", docid);
            SkyLogger.getItyxLogger().info(TagMatchDefinitions.DOCUMENT_ID + ": " + docid);
        } else {
            throw new Exception("Input parameters do not contain a " + TagMatchDefinitions.DOCUMENT_ID);
        }

		/* VerifiedCustomerNumber */
        String verifiedCustomerNumber = inputMap.getParameter("VerifiedCustomerNumber").getAsString();
        SkyLogger.getConnectorLogger().debug("IF4.5S: " + docid + " VerifiedCustomerNumber: " + verifiedCustomerNumber);
        flowObject.put("VerifiedCustomerNumber", verifiedCustomerNumber);

        String verifiedContractNumber = inputMap.getParameter("VerifiedContractNumber").getAsString();
        if (verifiedContractNumber.equals("0")) {
            verifiedContractNumber = null;
        } else {
            SkyLogger.getConnectorLogger().debug("IF4.5S: " + docid + " VerifiedContractNumber: " + verifiedContractNumber);
            flowObject.put("VerifiedContractNumber", verifiedContractNumber);
        }

        String channel = inputMap.getParameter(TagMatchDefinitions.CHANNEL).getAsString();
        String doctype = inputMap.getParameter(TagMatchDefinitions.FORM_TYPE_CATEGORY).getAsString();
        String direction = inputMap.getParameter(TagMatchDefinitions.MX_DIRECTION).getAsString();

		/* MandateNumber */
        String mandateNumber = inputMap.getParameter(TagMatchDefinitions.SEPA_MANDATE_NUMBER).getAsString();
        SkyLogger.getConnectorLogger().debug("IF4.5S: " + docid + " MandateNumber: " + mandateNumber);
        flowObject.put("VerifiedMandateNumber", mandateNumber);

		/* Signature Flag */
        String signatureFlag = null;
        Parameter signatureFlagParameter = inputMap.getParameter(TagMatchDefinitions.SEPA_SIGNATURE_FLAG);
        String signatureFlagParameterString = signatureFlagParameter.getAsString();
        SkyLogger.getConnectorLogger().debug("IF4.5S: " + docid + " SignatureFlag: " + signatureFlagParameterString);
        if (signatureFlagParameter != null && signatureFlagParameterString.equals("1")) {
            signatureFlag = "signed";
        }

		/* Signature Date */
        String signatureDate = null;
        Parameter signatureDateParameter = inputMap.getParameter(TagMatchDefinitions.SEPA_SIGNATURE_DATE);
        SkyLogger.getConnectorLogger().info("IF4.5S: " + docid + " SignatureDate: " + signatureDate);
        if (signatureDateParameter != null) {
            signatureDate = signatureDateParameter.getAsString();
        }

        if (inputMap.getParameter(TagMatchDefinitions.INITIAL).getAsString().equals(TagMatchDefinitions.TRUE) && !inputMap.getParameter(TagMatchDefinitions.CONTACT_ID).getAsString().equals("0")) {
            SkyLogger.getConnectorLogger().info("IF4.5S: " + docid + " Initial Contact with ContactId - Doing nothing");
            return;
        }

		/* Find out which service we want to connect to */
        Boolean serviceActive = (Boolean) flowObject.get("Siebel_Enabled");
        if (serviceActive == null || !serviceActive) {
            SkyLogger.getItyxLogger().warn("IF4.5S: " + docid + " Service not used - Doing nothing");
            return;
        }

		/* WSDL Url for Siebel Service */
        String serviceUrl = (String) flowObject.get("Siebel_WSDL");
        if (serviceUrl == null || "".equals(serviceUrl)) {
            serviceUrl = "http://www.sky.de/integration/web/SiebelService?WSDL";
        }
        System.setProperty(SiebelConnectorImpl.SysProperty_SiebelService_Endpoint, serviceUrl);

        if (verifiedCustomerNumber != null && verifiedCustomerNumber.trim().length() > 0) {

            SkyLogger.getConnectorLogger().debug("IF4.5S: " + docid + " Using service: " + System.getProperty(SiebelConnectorImpl.SysProperty_SiebelService_Endpoint));

            String contactid = getInputParameterAsString(inputMap, TagMatchDefinitions.CONTACT_ID);
            if (contactid.length() > 0) {
                flowObject.put("contactid", contactid);
                SkyLogger.getConnectorLogger().info("IF4.5S: " + docid + " " + TagMatchDefinitions.CONTACT_ID + ": " + contactid);
            } else {
                contactid = "0";
                SkyLogger.getConnectorLogger().info("IF4.5S: " + docid + " " + TagMatchDefinitions.CONTACT_ID + ": " + contactid);
            }

            final long currentTimeMillis = System.currentTimeMillis();
            String newContactId = ConnectorFactory.getSiebelInstance().updateContactOfDocument(
                    new UpdateContactParameter(null,docid, contactid, verifiedCustomerNumber, verifiedContractNumber, channel, doctype, direction, mandateNumber, signatureFlag, signatureDate));
            final long reindexingTime = System.currentTimeMillis() - currentTimeMillis;
            if (reindexingTime > 2000) {
                SkyLogger.getConnectorLogger().info("IF4.5S: " + docid + "  CustomerReindexing time in ms: " + reindexingTime);
            }

            if (newContactId != null) {
                flowObject.getOutputMap("outputMap").replaceParameter(TagMatchDefinitions.CONTACT_ID, TagMatchDefinitions.CONTACT_ID, new TextParameter(newContactId));
            } else {
                SkyLogger.getConnectorLogger().error("IF4.5S: " + docid + " Could not update Siebel contact for document - emptyNewContactID");
                throw new Exception("Could not update Siebel contact for document " + docid);
            }
        } else {
            SkyLogger.getConnectorLogger().error("IF4.5S: " + docid + " VerifiedCustomerNumber is not properly set - not calling Siebel interface");
        }
    }

    /**
     * @param inputMap
     * @param parameterKey TODO
     * @return
     */
    protected String getInputParameterAsString(IParameterMap inputMap, String parameterKey) {
        String docid = "";
        final Parameter docidParameter = inputMap.getParameter(parameterKey);
        if (docidParameter != null) {
            docid = docidParameter.getAsString();
        }
        return docid;
    }

    /**
     *
     */
    private static final long serialVersionUID = 2323470949278884537L;


}
