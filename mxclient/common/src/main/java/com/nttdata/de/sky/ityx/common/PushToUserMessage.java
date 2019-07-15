package com.nttdata.de.sky.ityx.common;

/**
 * Created by meinusch on 04.08.15.
 */
public class PushToUserMessage {

	private String user;
	private String type;
	private String received;

	public PushToUserMessage(String user, String received, String type) {
		this.user = user;
		this.received=received;
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
	public String getReceived() {
		return received;
	}

	public void setReceived(String received) {
		this.received = received;
	}

}
