package com.nttdata.de.sky.ityx.mediatrix.businessrules.server.parameter;

public class UpdateMandateSRParameter {
	public String	mandateId;
	public String	signatureDate;
	public String	signatureFlag;
	public boolean	sepa;

	public UpdateMandateSRParameter(String mandateId, String signatureDate, String signatureFlag, boolean sepa) {
		this.mandateId = mandateId;
		this.signatureDate = signatureDate;
		this.signatureFlag = signatureFlag;
		this.sepa= sepa;
	}
}