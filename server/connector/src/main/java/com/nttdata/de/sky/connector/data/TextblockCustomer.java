package com.nttdata.de.sky.connector.data;

import java.util.Date;

public class TextblockCustomer extends IndexingCustomer {

	private String mobileNumber;
	private String telephoneNumber;
	private String emailAddress;
	private String typeOfHarddisk;
	private String typeOfReceiver;
	private String typeOfCIPlus;
	private String staircase;
	private String floor;
	private String flatNumber;
	private String accountBalance;
	private String contractDate;
	private String serialSmartcard;
	private String serialHarddisk;
	private String serialReceiver;
	private String serialCIPlus;
	private String salutation;
	private String accountNumberShort;
	private String bankCode;
	private String bankAccountHolder;
	private String dunningAmount;
	private Date possibleCancellationDate;
	private Date cancellationDate;
//Test
	public TextblockCustomer(IndexingCustomer r) {
		super(r);
		// Routing Customer
		setFirstName(r.getFirstName());
		setLastName(r.getLastName());
		setAdditionalAddress(r.getAdditionalAddress()); // SIT-17-05-099
		setStreet(r.getStreet());
		setZipCode(r.getZipCode());
		setCity(r.getCity());
		setCountry(r.getCountry());
		
		// Indexing Customer
		setNumber(r.getNumber());
		setCampaignStamp(r.getCampaignStamp());
		setCategory(r.getCategory());
		setContractSkyGo(r.getContractSkyGo());
		setDunningLevel(r.getDunningLevel());
		setEarmarkedCancelationDate(r.getEarmarkedCancelationDate());
		setCustomerContractQuantity(r.getCustomerContractQuantity());
		setActiveContractQuantity(r.getActiveContractQuantity());
		setPricelist(r.getPricelist());
		setSrContractChange(r.getSrContractChange());
		setSrContractChangeDate(r.getSrContractChangeDate());
		setSubscriptionStartDate(r.getSubscriptionStartDate());
	}

	public String getMobileNumber() {
		return mobileNumber;
	}
	
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	
	public String getTelephoneNumber() {
		return telephoneNumber;
	}
	
	public void setTelephoneNumber(String telephoneNumber) {
		this.telephoneNumber = telephoneNumber;
	}
	
	public String getEmailAddress() {
		return emailAddress;
	}
	
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	
	public String getTypeOfHarddisk() {
		return typeOfHarddisk;
	}
	
	public void setTypeOfHarddisk(String typeOfHarddisk) {
		this.typeOfHarddisk = typeOfHarddisk;
	}
	
	public String getTypeOfReceiver() {
		return typeOfReceiver;
	}

	public void setTypeOfReceiver(String typeOfReceiver) {
		this.typeOfReceiver = typeOfReceiver;
	}

	public String getTypeOfCIPlus() {
		return typeOfCIPlus;
	}

	public void setTypeOfCIPlus(String typeOfCIPlus) {
		this.typeOfCIPlus = typeOfCIPlus;
	}

	public String getStaircase() {
		return staircase;
	}

	public void setStaircase(String staircase) {
		this.staircase = staircase;
	}

	public String getFloor() {
		return floor;
	}

	public void setFloor(String floor) {
		this.floor = floor;
	}
	
	public String getFlatNumber() {
		return flatNumber;
	}
	
	public void setFlatNumber(String flatNumber) {
		this.flatNumber = flatNumber;
	}

	public String getAccountBalance() {
		return accountBalance;
	}

	public void setAccountBalance(String accountBalance) {
		this.accountBalance = accountBalance;
	}

	public String getContractDate() {
		return contractDate;
	}

	public void setContractDate(String contractDate) {
		this.contractDate = contractDate;
	}

	public String getSerialSmartcard() {
		return serialSmartcard;
	}

	public void setSerialSmartcard(String serialSmartcard) {
		this.serialSmartcard = serialSmartcard;
	}

	public String getSerialHarddisk() {
		return serialHarddisk;
	}

	public void setSerialHarddisk(String serialHarddisk) {
		this.serialHarddisk = serialHarddisk;
	}

	public String getSerialReceiver() {
		return serialReceiver;
	}

	public void setSerialReceiver(String serialReceiver) {
		this.serialReceiver = serialReceiver;
	}

	public String getSerialCIPlus() {
		return serialCIPlus;
	}

	public void setSerialCIPlus(String serialCIPlus) {
		this.serialCIPlus = serialCIPlus;
	}

	public String getSalutation() {
		return salutation;
	}

	public void setSalutation(String salutation) {
		this.salutation = salutation;
	}

	public String getAccountNumberShort() {
		return accountNumberShort;
	}

	public void setAccountNumberShort(String accountNumberShort) {
		this.accountNumberShort = accountNumberShort;
	}
	
	public String getBankCode() {
		return bankCode;
	}
	
	public void setBankCode(String bankNumber) {
		this.bankCode = bankNumber;
	}

	public String getDunningAmount() {
		return dunningAmount;
	}
	
	public void setDunningAmount(String dunningAmount) {
		this.dunningAmount = dunningAmount;
	}
	
	public String getBankAccountHolder() {
		return bankAccountHolder;
	}

	public void setBankAccountHolder(String bankAccountHolder) {
		this.bankAccountHolder = bankAccountHolder;
	}

	public Date getPossibleCancelationDate() {
		return possibleCancellationDate;
	}

	public void setPossibleCancelationDate(Date possibleCancelationDate) {
		this.possibleCancellationDate = possibleCancelationDate;
	}

	public Date getCancelationDate() {
		return possibleCancellationDate;
	}

	public void setCancelationDate(Date cancelationDate) {
		this.cancellationDate = cancelationDate;
	}
	
	public String toString() {
		
		String toString = super.toString() + "TextblockCustomer:\n";
		toString += "\n<typeOfReceiver=" + typeOfReceiver + ">\n";
		toString += "<typeOfCIPlus=" + typeOfCIPlus + ">\n";
		toString += "<typeOfHarddisk=" + typeOfHarddisk + ">\n";
		toString += "<mobilenumber=" + mobileNumber + ">\n";
		toString += "<telephonenumber=" + telephoneNumber + ">\n";
		toString += "<emailaddress=" + emailAddress + ">\n";
		toString += "<emailaddress=" + emailAddress + ">\n";
		toString += "<staircase=" + staircase + ">\n";
		toString += "<floor=" + floor + ">\n";
		toString += "<flatNumber=" + flatNumber + ">\n";
		toString += "<accountBalance=" + accountBalance + ">\n";
		toString += "\n<contractDate=" + contractDate + ">\n";
		toString += "<serialSmartcard=" + serialSmartcard + ">\n";
		toString += "<serialHarddisk=" + serialHarddisk + ">\n";
		toString += "<serialReceiver=" + serialReceiver + ">\n";
		toString += "<serialCIPlus=" + serialCIPlus + ">\n";
		toString += "<salutation=" + salutation + ">\n";
		toString += "<accountNumberShort=" + accountNumberShort + ">\n";
		toString += "<bankCode=" + bankCode + ">\n";
		toString += "<bankAccountHolder=" + bankAccountHolder + ">\n";
		toString += "<dunningAmount=" + dunningAmount + ">\n";
		toString += "<possibleCancellationDate=" + getPossibleCancellationDate() + ">\n";
		toString += "<cancellationDate=" + getCancellationDate() + ">\n";
		
		return toString;
	}
}