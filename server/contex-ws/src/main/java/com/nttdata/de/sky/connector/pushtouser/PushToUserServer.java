package com.nttdata.de.sky.connector.pushtouser;

import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.PushToUserDecoder;
import com.nttdata.de.sky.ityx.common.PushToUserEncoder;
import com.nttdata.de.sky.ityx.common.PushToUserMessage;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

/**
 * Created by meinusch on 03.08.15.
 */
@ServerEndpoint(value="/pushToUser/{user}", encoders = PushToUserEncoder.class, decoders = PushToUserDecoder.class)
public class PushToUserServer {

	private MessageHandler messageHandler;

	public PushToUserServer() {
		//Nicht Löschen! Zwingend notwendig!
		SkyLogger.getCommonLogger().debug("PushToUserServer constructor");
	}

	@OnMessage
	public void pushToUser(Session session, PushToUserMessage msg) throws IOException, EncodeException, IllegalArgumentException {
		SkyLogger.getCommonLogger().info("PushToUserServer MsgReceived O:" + msg.getUser());
		try {
			boolean userfound = false;
			for (Session s : session.getOpenSessions()) {
				if (s.isOpen() && msg.getUser().toLowerCase().equals(s.getUserProperties().get("user"))) {
					s.getBasicRemote().sendObject(msg);
					userfound = true;
				}
			}
			if (!userfound) {
				SkyLogger.getCommonLogger().warn("onMessage User:" + msg.getUser() + " not online.");
				throw new EncodeException("PushToUser: User:" + msg.getUser() + " not online.", "User:" + msg.getUser() + " not online.");
			}
		}catch (EncodeException e){
			SkyLogger.getCommonLogger().info("onMessage failed:" + msg.getUser() + " " + e.getMessage(), e);
			throw e;
		}catch (IOException e) {
			try {
				session.close();
			} catch (IOException e1) {
				SkyLogger.getCommonLogger().info("onMessage failed io" +msg.getUser()+" "+ e.getMessage(), e);
			}
			SkyLogger.getCommonLogger().info("onMessage failed:" + msg.getUser() + " " + e.getMessage(), e);
			throw e;
		}
	}

	@OnOpen
	public void onOpen(final Session session, EndpointConfig endpointConfig, @PathParam("user") final String user) {
		for (Session s : session.getOpenSessions()) {
			if (!s.getId().equals(session.getId()) && user.toLowerCase().equals(s.getUserProperties().get("user"))) {
				try {
					s.close(new CloseReason(CloseReason.CloseCodes.GOING_AWAY,"Relogin with new Session"));
					session.getOpenSessions().remove(s);
					SkyLogger.getCommonLogger().info("closingSession on Relogin for user:" + user );
				} catch (IOException e) {
					SkyLogger.getCommonLogger().info("Warn on closingSession at Relogin for user:"+user + e.getMessage(), e);
				}
			}
		}
		session.getUserProperties().put("user", user.toLowerCase());
		SkyLogger.getCommonLogger().info("Session:"+session.getId()+" openend and bound for:"+user);
	}

	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		if (session!=null && session.getUserProperties()!=null && session.getUserProperties().get("user")!=null ) {
			String user = (String) session.getUserProperties().get("user");
			SkyLogger.getCommonLogger().info("Session:"+session.getId()+" closed for user:"+user);
			SkyLogger.getCommonLogger().info("closingSession for user:" + user);
		}else {
			SkyLogger.getCommonLogger().info("Session:"+((session!=null)?session.getId():0)+" closed");
		}
	}

	@OnError
	public void onError(Session session, Throwable thr) {
		SkyLogger.getCommonLogger().error("PushToUserServer Error:"+ session.getId()+" msg:" + thr.getMessage()  , thr);
	}
}
