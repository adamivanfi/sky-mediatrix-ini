package com.nttdata.de.sky.ityx.common;

import com.nttdata.de.lib.logging.SkyLogger;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 * Created by meinusch on 04.08.15.
 */
public class PushToUserEncoder implements Encoder.Text<PushToUserMessage> {
	@Override
	public void init(final EndpointConfig config) {
	}

	@Override
	public void destroy() {
	}

	@Override
	public String encode(final PushToUserMessage pushMsg) throws EncodeException {
		String user = pushMsg.getUser();
		String type = pushMsg.getType();
		String recdate = pushMsg.getReceived();

		JsonObjectBuilder b = Json.createObjectBuilder();

		if (user != null && !user.isEmpty()) {
			b.add("user", user);
		} else {
			SkyLogger.getCommonLogger().info("PushToUserEncoder: user is empty");
		}
		if (type != null && !type.isEmpty()) {
			b.add("type", type);
		} else {
			SkyLogger.getCommonLogger().info("PushToUserEncoder: type is empty");
		}
		if (recdate != null) {
			b.add("received", recdate);
		} else {
			//SkyLogger.getCommonLogger().info("PushToUserEncoder: received is empty");
			b.add("received", System.currentTimeMillis() + "");
		}

		if (type == null || type.isEmpty()) {
			SkyLogger.getCommonLogger().info("PushToUserEncoder: empty messageType");
		}else if (type.equals(PushToUserMessageCrmPopup.MSG_TYPE)) {
			PushToUserMessageCrmPopup pushMsgCRM = (PushToUserMessageCrmPopup) pushMsg;
			String custid = pushMsgCRM.getCustomerid();
			String contractid = pushMsgCRM.getContractid();
			String sblcontractid = pushMsgCRM.getSblcontactid();
			String activity = pushMsgCRM.getActivityid();

			if (custid != null && !custid.isEmpty()) {
				b.add("customerid", custid);
			} else {
				SkyLogger.getCommonLogger().info("PushToUserEncoder: customerid is empty");
			}
			if (contractid != null && !contractid.isEmpty()) {
				b.add("contractid", contractid);
			}
			if (sblcontractid != null && !sblcontractid.isEmpty()) {
				b.add("sblcontactid", sblcontractid);
			} else {
				SkyLogger.getCommonLogger().info("PushToUserEncoder: sblcontractid is empty");
			}
			if (activity != null && !activity.isEmpty()) {
				b.add("activityid", activity);
			} else {
				SkyLogger.getCommonLogger().info("PushToUserEncoder: activityid is empty");
			}
		}else if (type.equals(PushToUserMessageScm.MSG_TYPE)) {
			PushToUserMessageScm pushMsgScm = (PushToUserMessageScm) pushMsg;
			String custid = pushMsgScm.getCustomerid();
			int questionid = pushMsgScm.getQuestionid();

			if (custid != null && !custid.isEmpty()) {
				b.add("customerid", custid);
			} else {
				SkyLogger.getCommonLogger().info("PushToUserEncoder: customerid is empty");
			}
			if (questionid >0) {
				b.add("questionid", questionid);
			}else {
				SkyLogger.getCommonLogger().info("PushToUserEncoder: questionid is empty");
			}
		}else {
			SkyLogger.getCommonLogger().info("PushToUserEncoder: unknown MessageType:"+type);
		}

			return b.build().toString();
	}
}
