package com.nttdata.de.sky.ityx.common;

/**
 * Created by meinusch on 04.08.15.
 */
public class PushToUserMessageScm extends PushToUserMessage {

	public final static String MSG_TYPE = "CRM_SCM";
	private String customerid;
	private int questionid;
	public PushToUserMessageScm(String user, String received, String customerid, int questionid) {
		super(user, received, MSG_TYPE);
		this.customerid = customerid;
		this.questionid = questionid;
	}

	public String getCustomerid() {
		return customerid;
	}

	public void setCustomerid(String customerid) {
		this.customerid = customerid;
	}


	public int getQuestionid() {
		return questionid;
	}

	public void setQuestionid(int questionid) {
		this.questionid = questionid;
	}


}
