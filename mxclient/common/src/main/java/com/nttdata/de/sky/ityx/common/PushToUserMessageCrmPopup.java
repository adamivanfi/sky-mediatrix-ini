package com.nttdata.de.sky.ityx.common;

/**
 * Created by meinusch on 04.08.15.
 */
public class PushToUserMessageCrmPopup extends PushToUserMessage {

	public final static String MSG_TYPE="CRM_POPUP";
	private String customerid;
	private String sblcontactid;
	private String activityid;
	private String contractid;



	public PushToUserMessageCrmPopup(String user, String received, String customerid, String contractid, String activityid, String sblcontactid){
		super(user, received, MSG_TYPE);
		this.customerid=customerid;
		this.contractid=contractid;
		this.activityid=activityid;
		this.sblcontactid=sblcontactid;
	}

	public String getSblcontactid() {
		return sblcontactid;
	}

	public void setSblcontactid(String sblcontactid) {
		this.sblcontactid = sblcontactid;
	}

	public String getCustomerid() {
		return customerid;
	}

	public void setCustomerid(String customerid) {
		this.customerid = customerid;
	}

	public String getActivityid() {
		return activityid;
	}

	public void setActivityid(String activityid) {
		this.activityid = activityid;
	}


	public String getContractid() {
		return contractid;
	}

	public void setContractid(String contractid) {
		this.contractid = contractid;
	}
}
