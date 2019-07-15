package com.nttdata.de.sky.connector.data;

public class IndexingCustomer extends RoutingCustomer {
	private String rowId;
	private String firstName;
	private String lastName;
	private String additionalAddress;
	private String street;
	private String zipCode;
	private String city;
	private String country;
	private String contractDate;
	
	public IndexingCustomer() {
		super();
	}

	public IndexingCustomer(RoutingCustomer r) {
		if (r == null)
			return;

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

	public String getRowId() {
		return rowId;
	}

	public void setRowId(String rowId) {
		this.rowId = rowId;
	}

	public String getFirstName() { return firstName; }

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getAdditionalAddress() { return additionalAddress; } // SIT-17-05-099

	public void setAdditionalAddress(String additionalAddress) { this.additionalAddress = additionalAddress; } // SIT-17-05-099

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getContractDate() {
		return contractDate;
	}

	public void setContractDate(String contractDate) {
		this.contractDate = contractDate;
	}
	
	public String toString() {
		
		String toString = super.toString() + "IndexingCustomer:\n";
		toString += "\n<rowId=" + rowId + ">\n";
		toString += "<firstName=" + firstName + ">\n";
		toString += "<lastName=" + lastName + ">\n";
		toString += "<street=" + street + ">\n";
		toString += "<zipCode=" + zipCode + ">\n";
		toString += "<city=" + city + ">\n";
		toString += "<country=" + country + ">\n";
		toString += "<contractDate=" + contractDate + ">\n";
		return toString;
	}
}