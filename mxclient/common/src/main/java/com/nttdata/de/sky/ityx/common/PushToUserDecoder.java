package com.nttdata.de.sky.ityx.common;

import com.nttdata.de.lib.logging.SkyLogger;

import javax.json.Json;
import javax.json.JsonObject;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import java.io.StringReader;
/**
 * Created by meinusch on 04.08.15.
 */
public class PushToUserDecoder implements Decoder.Text<PushToUserMessage> {
	@Override
	public void init(final EndpointConfig config) {
	}

	@Override
	public void destroy() {
	}

	@Override
	public PushToUserMessage decode(final String textMessage) throws DecodeException {
		JsonObject obj = Json.createReader(new StringReader(textMessage)).readObject();
		String user=obj.getString("user","");
		String type=obj.getString("type","");
		String received=obj.getString("received", System.currentTimeMillis()+"");

		if (type==null || type.isEmpty()){
			SkyLogger.getCommonLogger().info("PushToUserEncoder: emtpy MsgType not allowed!");
			throw new DecodeException("PushToUserEncoder: emtpy MsgType not allowed!", "type not set");

		}else if (type.equals(PushToUserMessageCrmPopup.MSG_TYPE)){
			String sblcontactid=obj.getString("sblcontactid","");
			String activityid=obj.getString("activityid","");
			String customerid=obj.getString("customerid","");
			String contractid=obj.getString("contractid","");
			return new PushToUserMessageCrmPopup(user, received,  customerid, contractid, activityid,  sblcontactid);

		}else if (type.equals(PushToUserMessageScm.MSG_TYPE)){
			String customerid=obj.getString("customerid","");
			int questionid=obj.getInt("questionid", 0);
			return new PushToUserMessageScm(user, received,  customerid, questionid);
		}else{
			SkyLogger.getCommonLogger().warn("PushToUserEncoder: ignoring unknown MsgType:"+type);
			throw new DecodeException("PushToUserEncoder: unknown MsgType:"+type, "unknown Type:"+type);
		}
	}

	@Override
	public boolean willDecode(final String s) {
		return true;
	}
}
