package com.nttdata.de.sky.connector;

public class CreateContactParameter {
	public String documentId;
	public ISiebel.Channel channel;
	public ISiebel.Direction direction;
	public String documentType;
	public String customerId;
	public String contractId;
	public String mandateNumber;
	public String signatureFlag;
	public String signatureDate;

	public CreateContactParameter(String documentId, ISiebel.Channel channel, ISiebel.Direction direction, String documentType, String customerId, String contractId, String mandateNumber, String signatureFlag, String signatureDate) {
		this.documentId = documentId;
		this.channel = channel;
		this.direction = direction;
		this.documentType = documentType;
		this.customerId = customerId;
		this.contractId = contractId;
		this.mandateNumber = mandateNumber;
		this.signatureFlag = signatureFlag;
		this.signatureDate = signatureDate;
	}
}