package de.ityx.sky.outbound.client;

import com.nttdata.de.lib.logging.SkyLogger;
import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.client.dialog.MediaTrixShortCutPanel;
import de.ityx.mediatrix.client.dialog.Start;
import de.ityx.mediatrix.client.dialog.StartAction;
import de.ityx.sky.outbound.client.base.ClientBaseLogin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientLogin extends ClientBaseLogin {

	//private CometSocketClient cep = null;

	@Override
	public void postLogin(String operator) {
		postLoginEvent(operator);
		changeMenuButtons();
	}

	private void changeMenuButtons() {
		StartAction a = new NewMailAction();
		StartAction dummy = new DummyAction();
		dummy.setEnabled(false);
		
		final Start instance = Start.getInstance();
		instance.getMainMenu().setNewMailAction(a);
		final MediaTrixShortCutPanel shortCuts = instance.getShortCuts();
		shortCuts.setNewMailAction(a);
		shortCuts.setTerminManagerAction(dummy);
	}


	public String getEventUrl() {
		String mxservlet = API.getServletUrl();
		Matcher matcher = Pattern.compile("(https?:\\/\\/[0-9a-zA-Z\\.\\-]*(:[0-9]*)?)*?\\/").matcher(mxservlet);
		if (matcher.find()) {
			if (API.getClientAPI().getConnectionAPI().getHttpSession() != null) {
				return matcher.group(1) + "/mediatrix/event;jsessionid=" + API.getClientAPI().getConnectionAPI().getHttpSession();
			} else {
				return matcher.group(1) + "/mediatrix/event";
			}
		}
		return null;
	}

	public void postLoginEvent(String arg0) {
		try {
			//   if (cep == null) {
	//		SkyLogger.getClientLogger().info("ClientEvent initialization:" + arg0);
	//		ClientEventExtension.logoutOperator(arg0);
	//		SkyLogger.getClientLogger().info("ClientEvent cleaning complete:" + arg0);
		/*	String eventUrl = getEventUrl();
			if (eventUrl != null) {
				SkyLogger.getClientLogger().info("ClientEvent:" + arg0 + " URL:" + eventUrl);
				CometSocketClient.setServiceURL(getEventUrl());
			}
		*/
			  //cep = new CometSocketClient();
	//		cep = ClientEventExtension.getEventClient();
	//		SkyLogger.getClientLogger().info("ClientEvent:" + arg0 + " eventClient");
	//		ClientEventExtension.register(cep.getSocketId());
	//		SkyLogger.getClientLogger().info("ClientEvent:" + arg0 + " registered: " + cep.getSocketId());
		} catch (Throwable t) {
			SkyLogger.getClientLogger().error("ClientEvent: PostLogin: Exception: " + arg0 +" "+ t.getMessage(), t);
			t.printStackTrace();
		}

	}

	@Override
	public void postLogout(String arg0) {
		SkyLogger.getClientLogger().info("ClientEvent clean cometSocket:" + arg0);
	//	ClientEventExtension.logoutOperator(arg0);
		SkyLogger.getClientLogger().info("ClientEvent cleaning cometSocket complete:" + arg0);

	}

	@Override
	public boolean preLogin(String arg0) {
		return true;
	}

	@Override
	public int preLogout(String arg0) {

		// s. logoutOperator in postLogout
		/*if (cep != null) {

			String ses=API.getClientAPI().getConnectionAPI().getHttpSession();
			if (ses==null) {
				SkyLogger.getClientLogger().error("ClientEvent: preLogout: nosession found");
			}
			API.getClientAPI().getConnectionAPI().exchange("evx_clientLoeschenInCometstrukturWgLogout", "evx_clientLoeschenInCometstrukturWgLogout", 0, ses==null?"":ses);
			ClientEventExtension.logoutOperator(arg0);

			cep = null;
		}*/
		return 0;
	}
}
