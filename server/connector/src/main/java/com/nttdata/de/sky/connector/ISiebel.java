package com.nttdata.de.sky.connector;


import java.util.Date;

public interface ISiebel {
	/**
	 * Call this method to for each incoming document. It will trigger a
	 * callback via the contex web service that will carry a a contactid that is
	 * necessary for other siebel methods.
	 *
	 * @param parameter TODO
	 * @throws Exception
	 */
	// public void createContact(String documentId, Channel channel, Direction
	// direction, String documentType, String customerId, String contractId)
	// throws Exception;
	public void createContact(CreateContactParameter parameter) throws Exception;

	/**
	 * Call this method for each outbound document sent, when the trigger for
	 * the document (Web Service for Siebel) has triggered the response. The web
	 * service will provide a parameter activityid.
	 *
	 * @param documentId
	 * @param siebelActivityId
	 * @param channel
	 * @param direction
	 * @throws Exception
	 */
	public void associateDocumentIdToActivity(String documentId, String siebelActivityId, Channel channel, Direction direction) throws Exception;

	/**
	 * Call this method for each outbound document where an acitivityid is not
	 * available. The customerid and contactid the document refers to (which in
	 * turn is determined by the corresponding inbound document) need to be
	 * provided.
	 *
	 * @param documentId
	 * @param customerId
	 * @param contactId
	 * @throws Exception
	 */
	public void associateDocumentIdWithoutActivityId(String documentId, String customerId, String contractId, String contactId) throws Exception;

	/**
	 * Call this method when a customer association is changed in mediatrix. The
	 * contactid necessary is provided as part of the callback triggered by the
	 * createContact method in this interface.
	 *
	 * @param parameterObject TODO
	 * @return
	 * @throws Exception
	 */
	public String updateContactOfDocument(UpdateContactParameter parameterObject) throws Exception;

	/**
	 * Call this method when a QuickAction:Cancellations should be triggered in Siebel
	 */
	public void triggerSRQuickAction_Cancellation(String documentId, String customerId, String contractId,   String srContactId,String srId, Date cancellationDate, Date possibleCancellationDate, String cancellationReasonCode, String cancellationReasonFreeText) throws Exception ;

	public enum Channel {
		BRIEF, FAX, EMAIL
	}

	public enum Direction {
		INBOUND, OUTBOUND
	}

}
