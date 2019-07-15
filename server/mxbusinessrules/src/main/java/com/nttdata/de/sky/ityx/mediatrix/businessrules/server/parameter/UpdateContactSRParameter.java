package com.nttdata.de.sky.ityx.mediatrix.businessrules.server.parameter;

import internal.qu;

public class UpdateContactSRParameter {
	public Integer  questionId;
	public String	customerNumber;
	public String	contractNumber;
	public String	channel;
	public String	doctype;
	public String	direction;
	public String	docId;
	public String	contactid;
	public boolean	isInitial;

	public UpdateContactSRParameter(Integer questionId, String customerNumber, String contractNumber, String channel, String doctype, String direction,  String docId, String contactid, boolean isInitial) {
		this.questionId = questionId;
		this.customerNumber = customerNumber;
		this.contractNumber = contractNumber;
		this.channel = channel;
		this.doctype = doctype;
		this.direction=direction;
		this.docId = docId;
		this.contactid = contactid;
		this.isInitial = isInitial;
	}
}