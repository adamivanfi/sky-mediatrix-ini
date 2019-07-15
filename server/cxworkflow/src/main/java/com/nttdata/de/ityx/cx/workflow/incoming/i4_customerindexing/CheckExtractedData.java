package com.nttdata.de.ityx.cx.workflow.incoming.i4_customerindexing;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflReportedBean;
import com.nttdata.de.ityx.sharedservices.utils.ContexDbConnector;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.impl.document.text.email.EmailDocument;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.document.CDocument;
import de.ityx.contex.interfaces.extag.TagMatch;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Map;

public class CheckExtractedData extends AbstractWflReportedBean {

    public static final String VERIFIED_CONTRACT_NUMBER = "VerifiedContractNumber";
    public static final String VERIFIED_CUSTOMER_NUMBER = "VerifiedCustomerNumber";
    public static final String VERIFIED_SMARTCARD_NUMBER = "VerifiedSmartcardNumber";
    public static final String VERIFIED_MANDATE_NUMBER = "VerifiedMandateNumber";
    public static final String EMAILFUZZY = "email";


    @Override
    public void execute(IFlowObject flowObject) throws Exception {

		CDocumentContainer<CDocument> orgCont = DocContainerUtils.getDocContainer(flowObject, Preparation.CURR_DOC);
        CDocument orgDoc = DocContainerUtils.getDoc(orgCont);
		String docid = DocContainerUtils.getDocID(orgDoc);

        // Validation - if customerNotFound; if doVal=true: goToMxMI else goToMxTP
        Boolean doValidation = true;
        if (orgDoc != null) {
            doValidation = (Boolean) orgDoc.getNote("doValidation");
            doValidation = doValidation == null ? true : doValidation;
        }
        flowObject.put("doValidation", doValidation);

        //Verification - if unique Customer has been found
        //Boolean verifiedDocument = (Boolean) flowObject.get("VerifiedDocument");
        boolean verifiedDocument = verifyDocument(flowObject);
        String formtype = DocContainerUtils.getFormtype(orgDoc);
        if (formtype == null || formtype.contains("systemdefault")) {
            verifiedDocument = Boolean.FALSE;
            flowObject.put("VerifiedDocument", verifiedDocument);
        }

        String parameter = getParameter(flowObject, verifiedDocument, doValidation);
        flowObject.put("parameter", parameter);
        SkyLogger.getWflLogger().debug("400: " + docid + " " + parameter + " ver=" + verifiedDocument + " val:" + doValidation + " AfterExtr par:" + parameter);
		//if (!verifiedDocument) {
		//	logall(flowObject,docid,formtype, orgDoc);
		//}
    }

    // can be overwrited
    public boolean isVerifiedDocument(String customerNumber, String contractNumber, String smartcardNumber, String mandateNumber,
            boolean different_customers, boolean different_contracts, boolean different_smartcards) {
        return (isNotEmptyNumber(customerNumber) && !different_customers);
    }

    public String getReportingStep(IFlowObject flow) {
        Object mobj = flow.get("reporting_map");
        if (mobj != null && !mobj.getClass().equals(String.class)) {
            Map<String, Object> map = (Map<String, Object>) mobj;
            Integer stepReporting = (Integer) map.get("REPORTING_STEP_COUNTER");
            SkyLogger.getWflLogger().debug("StepReporting: " + stepReporting);
            switch (stepReporting) {
                case 1:
                    return "VertragNrL";
                case 2:
                    return "KundenNrLVertrag";
                case 3:
                    return "VertragNrKundenNr";
                case 4:
                    return "SmcVertrag";
                case 5:
                    return "SmcKundenNr";
                case 6:
                    return "KundenNrL";
                case 7:
                    return "KundenNr_Name";
                case 8:
                    return "KundenNr_Email";
                case 9:
                    return "VertragNr_Name";
                case 10:
                    return "VertragNr_Email";
                case 11:
                    return "Smc_Email";
                case 12:
                    return "Smc_Name";
                case 13:
                    return "Address_LastnameStreet";
                case 14:
                    return "Bank_Lastname";
                case 15:
                    return "Bank_Email";
                case 16:
                    return "Email";
                default:
                    return stepReporting + "";
            }
        }
        return "";
    }

    // can be overwirted
    public String getParameter(IFlowObject flowObject, boolean verifiedDocument, boolean doValidation) throws Exception {
        String parameter;
        if (verifiedDocument) {
            parameter = "500_CRMActivity";//+ "_" + FlowUtils.getRequiredString(flowObject, "WFLDTS");;//FlowUtils.getRequiredString(flowObject, "docStateWaitForContactId") + "_" + FlowUtils.getRequiredString(flowObject, "WFLDTS");
        } else if (!doValidation) {
            parameter = "600_MXInjection"; //+"_" + FlowUtils.getRequiredString(flowObject, "WFLDTS");;//FlowUtils.getRequiredString(flowObject, "docStateMediatrixWrite") + "_" + FlowUtils.getRequiredString(flowObject, "WFLDTS");
        } else {
            parameter = "460_ManualIndexing";//FlowUtils.getRequiredString(flowObject, "docStateManualValidation");
        }
        return parameter;
    }

    public boolean verifyDocument(IFlowObject flowObject) throws Exception {
        boolean different_customers = false;
        boolean different_contracts = false;
        boolean different_smartcards = false;
        boolean different_mandate = false;

        String customerNumber = null;
        String contractNumber = null;
        String smartcardNumber = null;
        String mandateNumber = null;

        String candidates = "";

        String docid = DocContainerUtils.getDocID(flowObject);
        for (TagMatch customer : (ArrayList<TagMatch>) flowObject.get("customer")) {
            String currCustomerNumber = customer.getTagValue(VERIFIED_CUSTOMER_NUMBER);
            String currContractNumber = customer.getTagValue(VERIFIED_CONTRACT_NUMBER);
            String currSmartcardNumber = customer.getTagValue(VERIFIED_SMARTCARD_NUMBER);
            String currMandateNumber = customer.getTagValue(VERIFIED_MANDATE_NUMBER);
            String currEmailFuzzy = customer.getTagValue(EMAILFUZZY);

            SkyLogger.getWflLogger().debug("Email from Customer: " + currEmailFuzzy);

            customerNumber = (!different_customers && isEmptyNumber(customerNumber)) ? currCustomerNumber : customerNumber;
            contractNumber = (!different_contracts && isEmptyNumber(contractNumber)) ? currContractNumber : contractNumber;
            smartcardNumber = (!different_smartcards && isEmptyNumber(smartcardNumber)) ? currSmartcardNumber : smartcardNumber;
            mandateNumber = (!different_mandate && isEmptyNumber(mandateNumber)) ? currMandateNumber : mandateNumber;

            if (customerNumber != null && currCustomerNumber != null && !customerNumber.equals(currCustomerNumber)) {
                different_customers = true;
            }
            if (contractNumber != null && currContractNumber != null && !contractNumber.equals(currContractNumber)) {
                different_contracts = true;
            }
            if (smartcardNumber != null && currSmartcardNumber != null && !smartcardNumber.equals(currSmartcardNumber)) {
                different_smartcards = true;
            }
            if (mandateNumber != null && currMandateNumber != null && !mandateNumber.equals(currMandateNumber)) {
                different_mandate = true;
            }
            candidates += " c:" + currCustomerNumber + "/v:" + currContractNumber + "/sn:" + currSmartcardNumber + "/md:" + currMandateNumber;
        }

        boolean isVerified = isVerifiedDocument(customerNumber, contractNumber, smartcardNumber, mandateNumber, different_customers, different_contracts, different_smartcards);
        //E-Mail Indizierung - Jardel Luis Roth
        if (!isVerified && "Bank_Lastname".equals(getReportingStep(flowObject))){
            CDocumentContainer<CDocument> orgCont = DocContainerUtils.getDocContainer(flowObject, Preparation.CURR_DOC);
            CDocument orgDoc = DocContainerUtils.getDoc(orgCont);

            if (orgDoc != null && orgDoc.getClass().equals(EmailDocument.class)) {
                EmailDocument edoc = ((EmailDocument) orgDoc);
                String from = edoc.getFrom();
                SkyLogger.getWflLogger().debug("400: " + docid + " Email: " + from);
                if (from != null){
                    flowObject.put(EMAILFUZZY, from);
                    customerNumber = getCustomer(from);
                    isVerified = isVerifiedDocument(customerNumber, contractNumber, smartcardNumber, mandateNumber, different_customers, different_contracts, different_smartcards);
                    SkyLogger.getWflLogger().debug("400: " + docid + " Get Customer: " + customerNumber + " from Email: " + from);
                }
            }
        }
        // Solution prototype for Incident INCTASK0028782 / INC0274637 - MX: Fehlerhafte automatische Indizierung
        //   wegen stoerendem Betrag im Text:
        if (isVerified && "".equals(getReportingStep(flowObject))){
            String statusContract = getStatusfromContract(contractNumber);
            switch (statusContract.toUpperCase()) {
                case "BEENDET":
                    SkyLogger.getWflLogger().debug("400: " + docid + " NOK: Vertrag ist ungültig "
                            + " c: " + customerNumber + "/v:" + contractNumber + "/sn:" + smartcardNumber + "/md:" + mandateNumber);
                    customerNumber = "";
                    contractNumber = "";
                    isVerified = false;
                    break;
            }
        }

        if (isVerified) {
            if (isNotEmptyNumber(customerNumber) && !different_customers) {
                flowObject.put(VERIFIED_CUSTOMER_NUMBER, customerNumber);
            }
            if (isNotEmptyNumber(contractNumber) && !different_contracts) {
                flowObject.put(VERIFIED_CONTRACT_NUMBER, contractNumber);
            }
            if (isNotEmptyNumber(smartcardNumber) && !different_smartcards) {
                flowObject.put(VERIFIED_SMARTCARD_NUMBER, smartcardNumber);
            }
            if (isNotEmptyNumber(mandateNumber) && !different_mandate) {
                flowObject.put(VERIFIED_MANDATE_NUMBER, mandateNumber);
            }
        } else {
            flowObject.put(VERIFIED_CUSTOMER_NUMBER, "");
            flowObject.put(VERIFIED_CONTRACT_NUMBER, "");
            flowObject.put(VERIFIED_SMARTCARD_NUMBER, "");
            flowObject.put(VERIFIED_MANDATE_NUMBER, "");
        }

        if (isVerified) {
            SkyLogger.getWflLogger().debug("400: " + docid + " OK:" + getReportingStep(flowObject) 
                    + " c: " + customerNumber + "/v:" + contractNumber + "/sn:" + smartcardNumber + "/md:" + mandateNumber);
        } else if (different_customers || different_contracts || different_smartcards || different_mandate || !DocContainerUtils.isEmpty(customerNumber)) {
            SkyLogger.getWflLogger().debug("400: " + docid + " NK:" + getReportingStep(flowObject) 
                    + " cd:" + different_customers + " vd:" + different_contracts + " sc:" + different_smartcards + " md:" + different_mandate
                    + " c: " + customerNumber + "/v:" + contractNumber + "/sn:" + smartcardNumber + "/md:" + mandateNumber
                    + candidates
            );
        } else {
            SkyLogger.getWflLogger().debug("400: " + docid + " NK:" + getReportingStep(flowObject) + "");
        }

        //at least customer is filled
        flowObject.put("IndexedDocument", isVerified);
        flowObject.put("VerifiedDocument", isVerified);
        return isVerified;
    }

    private static String getCustomer(String email_address) {
        String customerid =null;
        Connection con = null;
        PreparedStatement selectSt = null;
        ResultSet rs = null;

        try {
            con = ContexDbConnector.getConnection();
            selectSt = con.prepareStatement("SELECT CUSTOMER_ID FROM NEWDB_CUSTOMER WHERE EMAIL_ADDRESS = UPPER('" + email_address + "')");
            rs = selectSt.executeQuery();

            if (rs.next()) {
                customerid = rs.getString("CUSTOMER_ID");
            }

            if (customerid == null){
                selectSt = con.prepareStatement("SELECT CUSTOMER_ID FROM NEWDB_CUSTOMER WHERE EMAIL_ADDRESS = UPPER(substr('" + email_address + "', instr('" + email_address + "','<')+1, instr('" + email_address + "','>') - instr('" + email_address + "','<') -1))");
                rs = selectSt.executeQuery();
                if (rs.next()) {
                    customerid = rs.getString("CUSTOMER_ID");
                }
            }
            rs.close();
            selectSt.close();
        } catch (SQLException e) {
            SkyLogger.getWflLogger().error("SQL Exception CustomerId Email " + email_address +"  " +  e.getMessage());
            return null;
        } finally {
            ContexDbConnector.releaseConnection(con);
        }
        return customerid;
    }
    private static String getStatusfromContract(String contract_id) {
        String status =null;
        Connection con = null;
        PreparedStatement selectSt = null;
        ResultSet rs = null;

        try {
            con = ContexDbConnector.getConnection();
            selectSt = con.prepareStatement("SELECT STATUS FROM NEWDB_CONTRACT WHERE CONTRACT_ID = '" + contract_id + "'");
            rs = selectSt.executeQuery();

            if (rs.next()) {
                status = rs.getString("STATUS");
            }

            rs.close();
            selectSt.close();
        } catch (SQLException e) {
            SkyLogger.getWflLogger().error("SQL Exception ContractID " + contract_id +"  " +  e.getMessage());
            return null;
        } finally {
            ContexDbConnector.releaseConnection(con);
        }
        return status;
    }

    protected boolean isNotEmptyNumber(String value) {
        return !isEmptyNumber(value);
    }

    protected boolean isEmptyNumber(String value) {
        return (value == null || value.isEmpty() || "0".equals(value.trim()));
    }

	@Deprecated
	public void logall(IFlowObject flowObject, String docid, String formtype, CDocument srcDoc ){
		int i=0;
		SkyLogger.getWflLogger().debug("400: " + docid + " LOG f1:"+ srcDoc.getFormtype());


		SkyLogger.getWflLogger().debug("400: " + docid + " LOG f1:"+ srcDoc.getNote(TagMatchDefinitions.FORM_TYPE_CATEGORY)); //Formtype
		SkyLogger.getWflLogger().debug("400: " + docid + " LOG f2:"+ srcDoc.getNote(TagMatchDefinitions.DOCUMENT_TYPE)); //DocumentType
		SkyLogger.getWflLogger().debug("400: " + docid + " LOG f3:"+ srcDoc.getNote(TagMatchDefinitions.EVAL_FORMTYPE)); //EvalFormtypeAuto
		SkyLogger.getWflLogger().debug("400: " + docid + " LOG f4:" + DocContainerUtils.getDocTagValue(srcDoc, TagMatchDefinitions.FORM_TYPE_CATEGORY)); //Formtype
		SkyLogger.getWflLogger().debug("400: " + docid + " LOG f5:" + DocContainerUtils.getDocTagValue(srcDoc, TagMatchDefinitions.DOCUMENT_TYPE)); //DocumentType
		SkyLogger.getWflLogger().debug("400: " + docid + " LOG f6:"+  DocContainerUtils.getDocTagValue(srcDoc, TagMatchDefinitions.EVAL_FORMTYPE)); //EvalFormtypeAuto


		for (TagMatch customer : (ArrayList<TagMatch>) flowObject.get("customer")) {
			String currCustomerNumber = customer.getTagValue(VERIFIED_CUSTOMER_NUMBER);
			String currContractNumber = customer.getTagValue(VERIFIED_CONTRACT_NUMBER);
			String currSmartcardNumber = customer.getTagValue(VERIFIED_SMARTCARD_NUMBER);
			String currMandateNumber = customer.getTagValue(VERIFIED_MANDATE_NUMBER);
			SkyLogger.getWflLogger().debug("400: " + docid + " LOG: cust:" +currCustomerNumber+
					" v:"+currContractNumber+
					" smc:"+currSmartcardNumber+
					" md:"+currMandateNumber + " ft:"+formtype);

			for (TagMatch cc :customer.getChildren().values()) {
				i++;
				SkyLogger.getWflLogger().debug("400: " + docid + " CCLOG: "+(cc.getIdentifier()+":"+cc.getTagValue()));
			}
		}
	}
}
