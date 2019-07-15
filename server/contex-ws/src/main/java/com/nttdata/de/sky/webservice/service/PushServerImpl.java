package com.nttdata.de.sky.webservice.service;

import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.PushToUserClient;
import com.nttdata.de.sky.ityx.common.PushToUserMessageCrmPopup;
import de.ityx.sky.ws.ClientEventService;
import de.ityx.sky.ws.MediatrixRequest;
import de.ityx.sky.ws.MediatrixResponse;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * Created by meinusch on 03.08.15.
 */

@WebService(targetNamespace = "http://ws.sky.ityx.de",
		endpointInterface = "de.ityx.sky.ws.ClientEventService",
		portName = "ClientEventServiceImplPort",
		wsdlLocation = "WEB-INF/wsdl/pushClient.wsdl",
		serviceName = "clientEvent")

public class PushServerImpl implements ClientEventService {

	public static final String SUCCESS = "SUCCESS";
	public static final String ERROR = "ERROR";
	public static final String KEY_STATUS="STATUS";

	private PushToUserClient puc;

	public synchronized PushToUserClient getPushClient(){
		if (puc==null || !puc.isReady()){
			puc=new PushToUserClient();
		}
		return puc;
	}

	@WebMethod
	@Override
	public MediatrixResponse pushClient(@WebParam MediatrixRequest mxReq) {
		SkyLogger.getConnectorLogger().info("IF_WSCE: START WS-Call for user: "+mxReq.getUser());
		String master = mxReq.getMaster();
		String user = mxReq.getUser();
		String customerid = "";
		String contractid = "";
		String activityid = "";
		String contactid = "";
		for (MediatrixRequest.Entries.Entry entry : mxReq.getEntries().getEntry()) {
			switch (entry.getKey()) {
				case "customerid":
					customerid = entry.getValue();
					break;
				case "activityid":
					activityid = entry.getValue();
					break;
				case "contactid":
					contactid = entry.getValue();
					break;
				case "contractid":
					contractid = entry.getValue();
					break;
			}
		}
		SkyLogger.getConnectorLogger().info("IF_WSCE: START WS-Call for params: m:"+master +" u:"+user+" c:"+customerid+" a:"+activityid+" s:"+contactid);
		MediatrixResponse result = new MediatrixResponse();
		if (result.getEntries()==null) {
			result.setEntries(new MediatrixResponse.Entries());
		}
		try{
			getPushClient().sendMessage(new PushToUserMessageCrmPopup(user, System.currentTimeMillis()+"",customerid, contractid, activityid, contactid) );
		}catch (Exception e){
			SkyLogger.getConnectorLogger().error("IF_WSCE: ErrorS during WS-Call for params: m:" + master + " u:" + user + " c:" + customerid + " a:" + activityid + " s:" + contactid + " e:" + e.getMessage(), e);
			MediatrixResponse.Entries.Entry en=new MediatrixResponse.Entries.Entry();
			en.setKey(KEY_STATUS);
			en.setValue(ERROR);
			result.getEntries().getEntry().add(en);
			MediatrixResponse.Entries.Entry ev=new MediatrixResponse.Entries.Entry();
			ev.setKey("MSG");
			ev.setValue(e.getMessage() + ":" + e.getCause().getMessage());
			result.getEntries().getEntry().add(ev);
			SkyLogger.getConnectorLogger().error("IF_WSCE: ErrorF during WS-Call for params: m:" + master + " u:" + user + " c:" + customerid + " a:" + activityid + " s:" + contactid + " e:" + e.getMessage(), e);
			return result;
		}
		MediatrixResponse.Entries.Entry en=new MediatrixResponse.Entries.Entry();
		en.setKey(KEY_STATUS);
		en.setValue(SUCCESS);
		result.getEntries().getEntry().add(en);
		SkyLogger.getConnectorLogger().info("IF_WSCE: Finish WS-Call for params: m:" + master + " u:" + user + " c:" + customerid + " a:" + activityid + " s:" + contactid);
		return result;
	}
}
