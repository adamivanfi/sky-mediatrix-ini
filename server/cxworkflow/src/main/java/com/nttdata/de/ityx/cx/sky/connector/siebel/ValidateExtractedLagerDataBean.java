package com.nttdata.de.ityx.cx.sky.connector.siebel;

/**
 * Checks if customer and contract could be extracted from the Lager3000
 * document.
 *
 * @author DHIF01
 *
 */
public class ValidateExtractedLagerDataBean extends ValidateExtractedDataBean {

	@Override
	protected boolean isVerifiedDocument(String customerNumber, String contractNumber, String smartcardNumber,String mandateNumber,
		boolean different_customers, boolean different_contracts, boolean different_smartcards) {

		Integer tags = 0;
		if (isNotEmptyNumber(customerNumber) && !different_customers) {
			tags++;
		}
		if (isNotEmptyNumber(contractNumber) && !different_contracts) {
			tags++;
		}
		if (isNotEmptyNumber(smartcardNumber)&& !different_smartcards) {
			tags++;
		}
		return (tags >= 2);
	}
}
