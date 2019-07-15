package com.nttdata.de.sky.connector.data;

import java.util.Date;

public class RoutingCustomer {
	private String number;
	private String category;
	private String pricelist;
	private String campaignStamp;
	private String dunningLevel;
	private String srContractChange;
	private String srContractChangeDate;
	private String contractSkyGo;
	private String subscriptionStartDate;
	private String customerMigration;

	//202687 - Sky Relaunch Pricing and Packaging Zusammenfassung
	private String qFlag;


	// Stage Metatag
	private String stage;
	//private String rateCode;

	// Projekt "Box-Only":
	private String boxOnly;

	// berechneter Wert der in Abhängigkiet von subscriptionStartDate berechnet
	// wird
	private boolean subscriptionStartDateBeforeLimit;
	private String earmarkedCancelationDate;
	private Date possibleCancellationDate;
	private Date cancellationDate;
	private String SelectedContractNumber;

	private String CampaignTypes;
	private String WashMachineFlag;
	private String reception;
	private String operator;
	private String platform;
	private String contractType;
	private String rateCard;
	private String toids;
	private String packAndProductList;
	
	private int contactInterval3D;
	private int contactInterval7D;
	private int contactInterval14D;
	private int contactInterval21D;
	private int contactInterval28D;

	private int customerContractQuantity;
	private int activeContractQuantity;

	private String SMCNumber;

	private String rateCardFlg;
	private boolean isNewCustomer;
	private String ctrStartDate;

	private String mandateNumber;
	private String mandateStatus;
	private String signatureFlag;
	private String signatureDate;
	//private String customerIBAN;
	private String customerBIC;
	
	public String getSelectedContractNumber() {
		return SelectedContractNumber;
	}

	public void setSelectedContractNumber(String selectedContractNumber) {
		SelectedContractNumber = selectedContractNumber;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getToids() {
		return toids;
	}

	public void setToid(String pToid) {
		this.toids = pToid;
	}

	public String getSMCNumber() {
		return SMCNumber;
	}

	public void setSMCNumber(String pSMC) {
		this.SMCNumber = pSMC;
	}

	public int getContactInterval3D() {
		return contactInterval3D;
	}

	public void setContactInterval3D(int pcontactInterval) {
		this.contactInterval3D = pcontactInterval;
	}

	public int getContactInterval7D() {
		return contactInterval7D;
	}

	public void setContactInterval7D(int pcontactInterval) {
		this.contactInterval7D = pcontactInterval;
	}

	public int getContactInterval14D() {
		return contactInterval14D;
	}

	public void setContactInterval14D(int pcontactInterval) {
		this.contactInterval14D = pcontactInterval;
	}

	public int getContactInterval21D() {
		return contactInterval21D;
	}

	public void setContactInterval21D(int pcontactInterval) {
		this.contactInterval21D = pcontactInterval;
	}

	public int getContactInterval28D() {
		return contactInterval28D;
	}

	public void setContactInterval28D(int pcontactInterval) {
		this.contactInterval28D = pcontactInterval;
	}

	public void appendToid(String pToid) {
		final int MAXLEN = 250;
		if (toids == null || toids.length() < MAXLEN) {
			String newStr = ((toids == null || toids.length() == 0) ? pToid : toids + "," + pToid);
			if (newStr.length() < MAXLEN) {
				toids = newStr;
			} else {
				toids = newStr.substring(0, MAXLEN);
			}
		}
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getPricelist() {
		return pricelist;
	}

	public void setPricelist(String pricelist) {
		this.pricelist = pricelist;
	}

	public String getCampaignStamp() {
		return campaignStamp;
	}

	public void setCampaignStamp(String campaignStamp) {
		this.campaignStamp = campaignStamp;
	}

	public String getDunningLevel() {
		return dunningLevel;
	}

	public void setDunningLevel(String dunningLevel) {
		this.dunningLevel = dunningLevel;
	}

	public String getSrContractChange() {
		return srContractChange;
	}

	public void setSrContractChange(String srContractChange) {
		this.srContractChange = srContractChange;
	}

	public String getSrContractChangeDate() {
		return srContractChangeDate;
	}

	public void setSrContractChangeDate(String srContractChangeDate) {
		this.srContractChangeDate = srContractChangeDate;
	}

	public String getContractSkyGo() {
		return contractSkyGo;
	}

	public void setContractSkyGo(String contractSkyGo) {
		this.contractSkyGo = contractSkyGo;
	}

	public String getSubscriptionStartDate() {
		return subscriptionStartDate;
	}

	public void setSubscriptionStartDate(String subscriptionStartDate) {
		this.subscriptionStartDate = subscriptionStartDate;
	}

	public String getEarmarkedCancelationDate() {
		return earmarkedCancelationDate;
	}

	public void setEarmarkedCancelationDate(String earmarkedCancelationDate) {
		this.earmarkedCancelationDate = earmarkedCancelationDate;
	}

	public Date getPossibleCancellationDate() {
		return possibleCancellationDate;
	}

	public void setPossibleCancellationDate(Date possibleCancellationDate) {
		this.possibleCancellationDate = possibleCancellationDate;
	}

	public Date getCancellationDate() {
		return cancellationDate;
	}

	public void setCancellationDate(Date cancellationDate) {
		this.cancellationDate = cancellationDate;
	}

	public int getCustomerContractQuantity(){
		return customerContractQuantity;
	}
	public void setCustomerContractQuantity(int customerContractQuantity){
		this.customerContractQuantity = customerContractQuantity;

	}
	public int getActiveContractQuantity(){
		return activeContractQuantity;
	}
	public void setActiveContractQuantity(int activeContractQuantity){
		this.activeContractQuantity = activeContractQuantity;

	}
	public boolean isSubscriptionStartDateBeforeLimit() {
		return subscriptionStartDateBeforeLimit;
	}

	public void setSubscriptionStartDateBeforeLimit(boolean subscriptionStartDateLimitPassed) {
		this.subscriptionStartDateBeforeLimit = subscriptionStartDateLimitPassed;
	}

	public String getCampaignTypes() {
		return CampaignTypes;
	}

	public void setCampaignTypes(String campaignTypes) {
		CampaignTypes = campaignTypes;
	}

	public String getWashMachineFlag() {
		return WashMachineFlag;
	}

	public void setWashMachineFlag(String washMachineFlag) {
		WashMachineFlag = washMachineFlag;
	}

	public String getReception() {
		return this.reception;
	}

	public void setReception(final String pReception) {
		this.reception = pReception;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(final String pOperator) {
		this.operator = pOperator;
	}

	public String getPlatform() {
		return this.platform;
	}

	public void setPlatform(final String pPlatform) {
		this.platform = pPlatform;
	}

	public String getContractType() {
		return this.contractType;
	}

	public void setContractType(final String pContractType) {
		this.contractType = pContractType;
	}

	public String getRateCardFlg() {
		return this.rateCardFlg;
	}

	public void setRateCardFlg(final String pRateCardFlg) {
		this.rateCardFlg = pRateCardFlg;
	}

	public String getCtrStartDate() {
		return ctrStartDate;
	}

	public void setCtrStartDate(String pctrStartDate) {
		this.ctrStartDate = pctrStartDate;
	}

	public boolean isNewCustomer() {
		return isNewCustomer;
	}

	public void setNewCustomer(boolean isNewCustomer) {
		this.isNewCustomer = isNewCustomer;
	}

	public String getRateCard() {
		return rateCard;
	}

	public void setRateCard(String rateCard) {
		this.rateCard = rateCard;
	}

	public String getPackAndProductList() {
		return packAndProductList;
	}

	public void setPackAndProductList(String packAndProductList) {
		this.packAndProductList = packAndProductList;
	}

	public String getMandateNumber() {
		return mandateNumber;
	}

	public void setMandateNumber(String mandateNumber) {
		this.mandateNumber = mandateNumber;
	}

	public String getMandateStatus() {
		return mandateStatus;
	}

	public void setMandateStatus(String mandateStatus) {
		this.mandateStatus = mandateStatus;
	}

	public String getSignatureFlag() {
		return signatureFlag;
	}

	public void setSignatureFlag(String signatureFlag) {
		this.signatureFlag = signatureFlag;
	}

	public String getSignatureDate() {
		return signatureDate;
	}

	public void setSignatureDate(String signatureDate) {
		this.signatureDate = signatureDate;
	}

	/*public String getCustomerIBAN() {
		return customerIBAN;
	}

	public void setCustomerIBAN(String customerIBAN) {
		this.customerIBAN = customerIBAN;
	}*/

	public String getCustomerBIC() {
		return customerBIC;
	}

	public void setCustomerBIC(String customerBIC) {
		this.customerBIC = customerBIC;
	}
	
	public String getCustomerMigration(){
		return customerMigration;
	}
	public void setCustomerMigration(String pCustomerMigration){
		customerMigration=pCustomerMigration;
	}

	//202687 - Sky Relaunch Pricing and Packaging Zusammenfassung
	public String getQFlag(){
		return qFlag;
	}
	public void setQFlag(String pQFlag){ this.qFlag=pQFlag;	}
	public String getStage() {return stage; }
	public void setStage(String stage) {this.stage = stage; }
	// Projekt - Box-Only:
	public String getBoxOnly(){
		return boxOnly;
	}
	public void setBoxOnly(String pBoxOnly){ this.boxOnly=pBoxOnly;	}

	//public String getRateCode(){
	//	return rateCode;
	//}
	//public void setRateCode(String pRateCode){ 	this.rateCode=pRateCode;	}

	
	public String toString() {
		String toString = "RoutingCustomer:\n";
		toString += "\n<number=" + getNumber() + ">\n";
		toString += "<category=" + getCategory() + ">\n";
		toString += "<pricelist=" + getPricelist() + ">\n";
		toString += "<dunningLevel=" + getDunningLevel() + ">\n";
		toString += "<srContractChange=" + getSrContractChange() + ">\n";
		toString += "<srContractChangeDate=" + getSrContractChangeDate() + ">\n";
		toString += "<contractSkyGo=" + getContractSkyGo() + ">\n";
		toString += "<subscriptionStartDate=" + getSubscriptionStartDate() + ">\n";
		toString += "<subscriptionStartDateBeforeLimit=" + isSubscriptionStartDateBeforeLimit() + ">\n";
		toString += "<selectedContractNumber=" + getSelectedContractNumber() + ">\n";
		toString += "<campaign=" + getCampaignTypes() + ">\n";
		toString += "<WMflag=" + getWashMachineFlag() + ">\n";
		toString += "<Stamp=" + getCampaignStamp() + ">\n";
		toString += "<RateCardFlg=" + getRateCardFlg() + ">\n";
		toString += "<CustomerMigration=" + getCustomerMigration() + ">\n";
		toString += "<Operator=" + getOperator() + ">\n";
		toString += "<Platform=" + getPlatform() + ">\n";
		toString += "<Reception=" + getReception() + ">\n";
		toString += "<ContactInterval3=" + getContactInterval3D() + ">\n";
		toString += "<ContactInterval7=" + getContactInterval7D() + ">\n";
		toString += "<ContactInterval14=" + getContactInterval14D() + ">\n";
		toString += "<ContactInterval21=" + getContactInterval21D() + ">\n";
		toString += "<ContactInterval28=" + getContactInterval28D() + ">\n";
		toString += "<NewCustomer=" + isNewCustomer() + ">\n";
		toString += "<RateCard=" + getRateCard() + ">\n";
		toString += "<ContractType=" + getContractType() + ">\n";
		toString += "<MandateNumber=" + getMandateNumber() + ">\n";
		toString += "<MandateStatus=" + getMandateStatus() + ">\n";
		toString += "<SignatureFlag=" + getSignatureFlag() + ">\n";
		toString += "<SignatureDate=" + getSignatureDate() + ">\n";
		//toString += "<CustomerIBAN=" + getCustomerIBAN() + ">\n";
		toString += "<CustomerBIC=" + getCustomerBIC() + ">\n";
		toString += "<PackAndProductList=" + packAndProductList + ">\n";
		toString += "<ClientContractItems=" + getCustomerContractQuantity() + ">\n";
		toString += "<ActiveContractItems=" + getActiveContractQuantity() + ">\n";
		toString += "<PossibleCancellationDate=" + getPossibleCancellationDate() + ">\n";
		toString += "<CancellationDate=" + getCancellationDate() + ">\n";
		toString += "<QFlag=" + getQFlag() + ">\n";
		toString += "<boxOnlyFlag=" + getBoxOnly() + ">\n";
		toString += "<Stage=" + getStage() + ">\n";
		//toString += "<RateCode=" + getRateCode() + ">\n";
		return toString;
	}
}
