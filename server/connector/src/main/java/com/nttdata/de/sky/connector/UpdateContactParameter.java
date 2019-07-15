package com.nttdata.de.sky.connector;

public class UpdateContactParameter {
	public Integer questionId;
	public String documentId;
	public String contactId;
	public String customerIdNew;
	public String contractIdNew;
	public String channel;
	public String doctype;
	public String mandateNumber;
	public String signatureFlag;
	public String signatureDate;
	public String direction;

	public UpdateContactParameter(Integer questionId, String documentId, String contactId, String customerIdNew, String contractIdNew, String channel, String doctype, String direction, String mandateNumber, String signatureFlag, String signatureDate) {
		this.questionId = questionId;
		this.documentId = documentId;
		this.contactId = contactId;
		this.customerIdNew = customerIdNew;
		this.contractIdNew = contractIdNew;
		this.channel = channel;
		this.doctype = doctype;
		this.mandateNumber = mandateNumber;
		this.signatureFlag = signatureFlag;
		this.signatureDate = signatureDate;
		this.direction=direction;
	}
}