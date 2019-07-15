package com.nttdata.de.ityx.cx.sky.connector.siebel;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import de.ityx.contex.impl.designer.state.KeyConfiguration;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.extag.TagMatch;

import java.util.ArrayList;

/**
 * Implements search for customer based on information available via extraction.
 * 
 * The following information will be extracted by the extraction process:
 * 
 * 
 * 
 * @author VOGT41
 * 
 */
public class ValidateExtractedDataBean extends AbstractWflBean {

	public static final String VERIFIED_CONTRACT_NUMBER = "VerifiedContractNumber";
	public static final String VERIFIED_CUSTOMER_NUMBER = "VerifiedCustomerNumber";
        public static final String VERIFIED_SMARTCARD_NUMBER = "VerifiedSmartcardNumber";
        public static final String VERIFIED_MANDATE_NUMBER = "VerifiedMandateNumber";
	  
	@Override
	public void execute(IFlowObject flowObject) throws Exception {
		boolean different_customers=false;
		boolean different_contracts=false;
		boolean different_smartcards=false;
		boolean different_mandate=false;
                
		String customerNumber = null;
		String contractNumber = null;
		String smartcardNumber = null;
                String mandateNumber =null;
	
		String docid= DocContainerUtils.getDocID(flowObject);
		for (TagMatch customer :(ArrayList<TagMatch>) flowObject.get("customer") ){
			String currCustomerNumber = customer.getTagValue(VERIFIED_CUSTOMER_NUMBER);
			String currContractNumber = customer.getTagValue(VERIFIED_CONTRACT_NUMBER);
			String currSmartcardNumber = customer.getTagValue(VERIFIED_SMARTCARD_NUMBER);
			String currMandateNumber = customer.getTagValue(VERIFIED_MANDATE_NUMBER);
                        
			customerNumber=(!different_customers &&isEmptyNumber(customerNumber))?currCustomerNumber:customerNumber;
			contractNumber=(!different_contracts && isEmptyNumber(contractNumber))?currContractNumber:contractNumber;
			smartcardNumber=(!different_smartcards && isEmptyNumber(smartcardNumber))?currSmartcardNumber:smartcardNumber;
			mandateNumber=(!different_mandate && isEmptyNumber(mandateNumber))?currMandateNumber:mandateNumber;
                                
			if (customerNumber!=null && currCustomerNumber!=null && !customerNumber.equals(currCustomerNumber)){
				different_customers=true;
			}
			if (contractNumber!=null && currContractNumber!=null  && !contractNumber.equals(currContractNumber)){
				different_contracts=true;
			}
			if (smartcardNumber!=null && currSmartcardNumber!=null && !smartcardNumber.equals(currSmartcardNumber)){
				different_smartcards=true;
			}
                        if (mandateNumber!=null && currMandateNumber!=null && !mandateNumber.equals(currMandateNumber)){
				different_mandate=true;
			}
			SkyLogger.getItyxLogger().debug(docid + ": iterating: cust: " + currCustomerNumber+" contract:"+currContractNumber+" sn:"+currSmartcardNumber+" md:"+currMandateNumber);
		}
		
		boolean verifiedDocument=isVerifiedDocument(customerNumber,contractNumber, smartcardNumber, mandateNumber, different_customers, different_contracts, different_smartcards );
		
		if (verifiedDocument){
			if (isNotEmptyNumber(customerNumber) && !different_customers){
				flowObject.put(VERIFIED_CUSTOMER_NUMBER, customerNumber);
			}
			if (isNotEmptyNumber(contractNumber) && !different_contracts){
				flowObject.put(VERIFIED_CONTRACT_NUMBER, contractNumber);
			}
			if (isNotEmptyNumber(smartcardNumber) && !different_smartcards){
				flowObject.put(VERIFIED_SMARTCARD_NUMBER, smartcardNumber);
			}
                        if (isNotEmptyNumber(mandateNumber) && !different_mandate){
				flowObject.put(VERIFIED_MANDATE_NUMBER, mandateNumber);
			}
		}else{
			flowObject.put(VERIFIED_CUSTOMER_NUMBER, "");
			flowObject.put(VERIFIED_CONTRACT_NUMBER, "");
			flowObject.put(VERIFIED_SMARTCARD_NUMBER, "");
                        flowObject.put(VERIFIED_MANDATE_NUMBER, "");
		}
		
		SkyLogger.getItyxLogger().debug(docid + ": Extracted: v:"+verifiedDocument
			+" cust: " + customerNumber+" contract:"+contractNumber+" sn:"+smartcardNumber+" mand:"+mandateNumber
			+" diff:" + different_customers +":"+different_contracts+":"+different_smartcards+":"+different_mandate);
	
		//at least customer is filled
		flowObject.put("VerifiedDocument",verifiedDocument );
	}

	protected boolean isVerifiedDocument(String customerNumber, String contractNumber, String smartcardNumber, String mandateNumber, 
		boolean different_customers , boolean different_contracts, boolean different_smartcards ){ 
		return  (    isNotEmptyNumber(customerNumber)  && !different_customers  && !different_contracts   );
	}
	
	protected boolean isNotEmptyNumber(String value){
		return !isEmptyNumber(value);
	}
	protected boolean isEmptyNumber(String value){
		return (value == null || value.isEmpty() || "0".equals(value.trim()));
	}
	
	@Override
	public KeyConfiguration[] getKeys() {
		return new KeyConfiguration[] { new KeyConfiguration( "VerifiedDocument", Boolean.class) };
	}


}
