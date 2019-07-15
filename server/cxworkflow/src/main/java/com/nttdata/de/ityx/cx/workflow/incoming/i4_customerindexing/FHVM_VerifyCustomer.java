package com.nttdata.de.ityx.cx.workflow.incoming.i4_customerindexing;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;
import com.nttdata.de.ityx.cx.workflow.base.FlowUtils;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import de.ityx.contex.impl.designer.state.KeyConfiguration;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.extag.TagMatch;

import java.util.ArrayList;

public class FHVM_VerifyCustomer extends AbstractWflBean {

    public static final String VERIFIED_CONTRACT_NUMBER = "VerifiedContractNumber";
    public static final String VERIFIED_CUSTOMER_NUMBER = "VerifiedCustomerNumber";
    public static final String VERIFIED_MANDATE_NUMBER = "VerifiedMandateNumber";
    public static final String VERIFIED_SMARTCARD_NUMBER = "VerifiedSmartcardNumber";

    @Override
    public void execute(IFlowObject flowObject) throws Exception {

        String customerNumber = null;
        String contractNumber = null;
        String mandateNumber = null;
        String smcNumber = null;
        boolean mandateUnique = true;
        boolean smcUnique = true;
        boolean uniquecustomerFound = false;

        for (TagMatch nextCustomer : (ArrayList<TagMatch>) flowObject.get("customer")) {
            String itCustomerNumber = nextCustomer.getTagValue(VERIFIED_CUSTOMER_NUMBER);
            String itContractNumber = nextCustomer.getTagValue(VERIFIED_CONTRACT_NUMBER);
            String itSMCNumber = nextCustomer.getTagValue(VERIFIED_SMARTCARD_NUMBER);
            String itMandateNumber = nextCustomer.getTagValue(VERIFIED_MANDATE_NUMBER);

            if (DocContainerUtils.isEmpty(customerNumber) && !DocContainerUtils.isEmpty(itCustomerNumber)) {
                customerNumber = itCustomerNumber;
                uniquecustomerFound = true;
            } else if (!DocContainerUtils.isEmpty(customerNumber) && !DocContainerUtils.isEmpty(itCustomerNumber) && !customerNumber.equalsIgnoreCase(itCustomerNumber)) {
                SkyLogger.getWflLogger().info("400FHV: " + DocContainerUtils.getDocID(flowObject) + " Customer not unique:" + itCustomerNumber + "/" + customerNumber);
                uniquecustomerFound = false;
                customerNumber = null;
                break;
            }

            if (DocContainerUtils.isEmpty(contractNumber) && !DocContainerUtils.isEmpty(itContractNumber)) {
                contractNumber = itContractNumber;
                uniquecustomerFound = true;
            } else if (!DocContainerUtils.isEmpty(contractNumber) && !DocContainerUtils.isEmpty(itContractNumber) && !contractNumber.equalsIgnoreCase(itContractNumber)) {
                SkyLogger.getWflLogger().info("400FHV: " + DocContainerUtils.getDocID(flowObject) + " Contract not unique:" + itContractNumber + "/" + contractNumber);
                uniquecustomerFound = false;
                contractNumber = null;
                break;
            }

            if (DocContainerUtils.isEmpty(smcNumber) && !DocContainerUtils.isEmpty(itSMCNumber) && smcUnique) {
                smcNumber = itSMCNumber;
            } else if (!DocContainerUtils.isEmpty(smcNumber) && !DocContainerUtils.isEmpty(itSMCNumber) && !smcNumber.equalsIgnoreCase(itSMCNumber)) {
                SkyLogger.getWflLogger().info("400FHV: " + DocContainerUtils.getDocID(flowObject) + " SmarCard not unique:" + itSMCNumber + "/" + smcNumber);
                smcUnique = false;
                smcNumber = null;
                break;
            }

            if (DocContainerUtils.isEmpty(mandateNumber) && !DocContainerUtils.isEmpty(itMandateNumber) && mandateUnique) {
                mandateNumber = itMandateNumber;
            } else if (!DocContainerUtils.isEmpty(mandateNumber) && !DocContainerUtils.isEmpty(itMandateNumber) && !mandateNumber.equalsIgnoreCase(itMandateNumber)) {
                mandateUnique = false;
                SkyLogger.getWflLogger().debug("400FHV: " + DocContainerUtils.getDocID(flowObject) + " MandateID not unique:" + itMandateNumber + "/" + mandateNumber);
            }

        }

        if (uniquecustomerFound) {
            flowObject.put(VERIFIED_CUSTOMER_NUMBER, DocContainerUtils.isEmpty(customerNumber) ? "" : customerNumber);
            flowObject.put(VERIFIED_CONTRACT_NUMBER, DocContainerUtils.isEmpty(contractNumber) ? "" : contractNumber);
            flowObject.put(VERIFIED_SMARTCARD_NUMBER, DocContainerUtils.isEmpty(smcNumber) ? "" : smcNumber);
            flowObject.put(VERIFIED_MANDATE_NUMBER, DocContainerUtils.isEmpty(mandateNumber) ? "" : mandateNumber);
            SkyLogger.getWflLogger().debug("400FHV: " + DocContainerUtils.getDocID(flowObject) + " Extracted: verified:"+uniquecustomerFound
			+" cust: " + customerNumber+" contract:"+contractNumber+" sn:"+smcNumber
			+" mandate:" +mandateNumber );
            flowObject.put("VerifiedDocument", true);
        } else {
            SkyLogger.getWflLogger().debug("400FHV: " + DocContainerUtils.getDocID(flowObject) + " UniqueCustomerNotFound");
            flowObject.put(VERIFIED_CUSTOMER_NUMBER, "");
            flowObject.put(VERIFIED_CONTRACT_NUMBER, "");
            flowObject.put(VERIFIED_SMARTCARD_NUMBER, "");
            flowObject.put(VERIFIED_MANDATE_NUMBER, "");
            flowObject.put("VerifiedDocument", false);
        }
        setParameter(flowObject, uniquecustomerFound);

    }

    public void setParameter(IFlowObject flowObject, boolean uniquecustomerFound) throws Exception {
        if (uniquecustomerFound) {
            flowObject.put("parameter", FlowUtils.getRequiredString(flowObject, "FHV_docStateMandateSplit"));
        } else {
            flowObject.put("parameter", FlowUtils.getRequiredString(flowObject, "FHV_docStateManualValidation"));
        }
    }

    @Override
    public KeyConfiguration[] getKeys() {
        return new KeyConfiguration[]{
				new KeyConfiguration(VERIFIED_CONTRACT_NUMBER,String.class),
						new KeyConfiguration(VERIFIED_CONTRACT_NUMBER,String.class),
								new KeyConfiguration(VERIFIED_MANDATE_NUMBER,String.class),
										new KeyConfiguration(VERIFIED_SMARTCARD_NUMBER,String.class),
												new KeyConfiguration("VerifiedDocument",String.class)};
    }
}
