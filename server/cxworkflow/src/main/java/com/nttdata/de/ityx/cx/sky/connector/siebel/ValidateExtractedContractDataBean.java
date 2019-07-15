package com.nttdata.de.ityx.cx.sky.connector.siebel;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;
import de.ityx.contex.impl.designer.state.KeyConfiguration;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.extag.TagMatch;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Checks if customer and contract could be extracted from the mandate form.
 * 
 * @author DHIF01
 * 
 */
public class ValidateExtractedContractDataBean extends AbstractWflBean  {

	public static final String	VERIFIED_CONTRACT_NUMBER	= "VerifiedContractNumber";
	public static final String	VERIFIED_CUSTOMER_NUMBER	= "VerifiedCustomerNumber";
	public static final String	VERIFIED_MANDATE_NUMBER		= "VerifiedMandateNumber";

        @Override
	public void execute(IFlowObject flowObject) {
		Boolean verified = Boolean.FALSE;
		ArrayList<TagMatch> customerData = (ArrayList<TagMatch>) flowObject.get("customer");
		if (customerData != null && customerData.size() > 0) {
			Iterator<TagMatch> tmIterator = customerData.iterator();
			final TagMatch customer = tmIterator.next();
			String customerNumber = customer.getTagValue(VERIFIED_CUSTOMER_NUMBER);
			Boolean foundCustomer = true;
			String contractNumber = customer.getTagValue(VERIFIED_CONTRACT_NUMBER);
			Boolean foundContract = true;
			String mandateNumber = customer.getTagValue(VERIFIED_MANDATE_NUMBER);
			Boolean foundMandate = true;

			// Checks if the same customer or contract or mandate has been
			// extracted multiple times.
			while (tmIterator.hasNext() && (foundContract && foundCustomer && foundMandate)) {
				final TagMatch nextCustomer = tmIterator.next();
				if (customerNumber == null || !customerNumber.equals(nextCustomer.getTagValue(VERIFIED_CUSTOMER_NUMBER))) {
					customerNumber = null;
					foundCustomer = false;
				}
				if (contractNumber == null || !contractNumber.equals(nextCustomer.getTagValue(VERIFIED_CONTRACT_NUMBER))) {
					contractNumber = null;
					foundContract = false;
				}
				if (mandateNumber == null || !mandateNumber.equals(nextCustomer.getTagValue(VERIFIED_MANDATE_NUMBER))) {
					mandateNumber = null;
					foundMandate = false;
				}
			}

			// Sets unique customer data in the flow object, if customer and
			// contract and mandate have been extracted and if the mandate has
			// been signed.
			verified = customerNumber != null && customerNumber.trim().length() > 0 && !"0".equals(customerNumber.trim());
			verified = verified && (contractNumber != null && contractNumber.trim().length() > 0 && !"0".equals(contractNumber.trim()));
			if (verified) {
				flowObject.put(VERIFIED_CUSTOMER_NUMBER, customerNumber);
				flowObject.put(VERIFIED_CONTRACT_NUMBER, contractNumber);
				if (foundMandate) {
					flowObject.put(VERIFIED_MANDATE_NUMBER, mandateNumber);
				}
			}
		}
		flowObject.put("VerifiedDocument", verified);
	}

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -5939629539069842182L;

	
	@Override
	public KeyConfiguration[] getKeys() {
		return new KeyConfiguration[] { new KeyConfiguration( "VerifiedDocument", Boolean.class) };
	}

}
