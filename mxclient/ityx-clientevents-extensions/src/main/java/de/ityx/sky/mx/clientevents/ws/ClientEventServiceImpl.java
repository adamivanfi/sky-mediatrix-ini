package de.ityx.sky.mx.clientevents.ws;

import de.ityx.base.Global;
import de.ityx.base.dbpooling.DBConnectionPoolFactory;
import de.ityx.clientevents.mx.ClientProxy;
import de.ityx.clientevents.mx.ServerEventManager;
import de.ityx.clientevents.mx.event.EventResult;
import de.ityx.clientevents.mx.event.IMXClientEvent;
import de.ityx.clientevents.mx.extension.ServerEventExtension;
import de.ityx.mediatrix.api.interfaces.IConnectionPool;
import de.ityx.mediatrix.client.util.Repository;
import de.ityx.mediatrix.modules.tools.logger.Log;
import de.ityx.sky.mx.clientevents.extensions.OpenInBoxMailEvent;
import de.ityx.sky.mx.clientevents.extensions.OpenOpModusMailEvent;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.Iterator;

@WebService(targetNamespace = "http://ws.sky.ityx.de", endpointInterface = "de.ityx.sky.mx.clientevents.ws.ClientEventService", serviceName = "clientEvent")
public class ClientEventServiceImpl implements ClientEventService {

	public static final String SKY_CONTACTID_EXTRA = "sky.contactid.extra";

	private static final long CLIENT_EVENT_TIMEOUT = 30000L;
	;
	
	@Resource
	WebServiceContext context;

	@WebMethod(operationName = "pushClient")
	@Override
	public MediatrixResponse pushClient(MediatrixRequest request) {
		// Log.loginfo("IF4.3: pushClient: START");
		DateFormat formatter = DateFormat.getTimeInstance(DateFormat.MEDIUM);
		MediatrixResponse result = new MediatrixResponse();
		result.setEntries(new HashMap<String, String>());

		String user = request.getUser();
		String contactid = "";
		String activityid = "";
		String customerid = "";
		for (Iterator iterator = request.getEntries().keySet().iterator(); iterator.hasNext(); ) {
			String key = (String) iterator.next();
			Log.loginfo(key + ":" + request.getEntries().get(key));
			if (key.equalsIgnoreCase("contactid")) {
				contactid = request.getEntries().get(key);
			} else if (key.equalsIgnoreCase("activityid")) {
				activityid = request.getEntries().get(key);
			} else if (key.equalsIgnoreCase("customerid")) {
				customerid = request.getEntries().get(key);
			}
		}

		 Log.loginfo("IF4.3: pushClient: User:" + user + " Master:" + request.getMaster() + " contactid:" + contactid + " activityid:" + activityid + " customerid:" + customerid);
		//CometSocketClient cometClient = ClientEventExtension.getEventClient();
		// Log.loginfo("IF4.3: pushClient: CometClient: r:" + cometClient.isRunning() );

		//long sid = cometClient.getSocketId();
		// Log.loginfo("IF4.3: pushClient: SocketId: " + sid + " user:" + user);
		//		Benutzer nicht verf�gbar
		final ServerEventManager eventManager = ServerEventExtension.getEventManager();
		 Log.loginfo("IF4.3: pushClient: ServerEventManager: " + eventManager);
		//ClientProxy proxy = eventManager.getBySocketId(sid);
		ClientProxy proxy = eventManager.getByLogin(user.toLowerCase());

		 Log.loginfo("IF4.3: pushClient: ClientProxy: " + proxy + " sid:" + user);
		if(	proxy == null ) {
			result.getEntries().put(MediatrixResponse.KEY_STATUS, MediatrixResponse.MXC_NOT_ONLINE);
			 Log.loginfo("IF4.3: pushClient: ClientNotOnline :" + user);
			return result;
		}
		try {
			IMXClientEvent mEvent = null;
			int contactColumn = Global.getIntProperty(SKY_CONTACTID_EXTRA, 8);
			int frageId = getQuestionIdFromContacId(contactid, contactColumn);
			if (frageId == 0) {
				mEvent = new ExtendedNewMailEvent();
			} else {
				// TODO not used anymore?
				boolean clientIsinOpModus = getClientModus();
				if (clientIsinOpModus) {
					mEvent = new OpenOpModusMailEvent(frageId);
				} else {
					mEvent = new OpenInBoxMailEvent(frageId);
				}
				 Log.loginfo("IF4.3: clientIsinOpModus:" + clientIsinOpModus);
				//mEvent = new NewMailEvent(frageId);
				// TODO mEvent.setToken(token);
			}
			final String requestData = "contactid=" + contactid + ";" + "activityid=" + activityid + ";" + "customerid=" + customerid;
			setValueInExtraColumn(mEvent, contactColumn, requestData);
			 Log.loginfo("IF4.3: " + requestData);
			// Zum Testen
			// setValueInExtraColumn(mEvent, 1, "1");
			// setValueInExtraColumn(mEvent, 2, "2");
			// setValueInExtraColumn(mEvent, 3, "3");

			 Log.loginfo("IF4.3: Time before pushClient: " + formatter.format(System.currentTimeMillis()));
			EventResult er = EventResult.ERROR("");
			er = proxy.sendEvent(mEvent, CLIENT_EVENT_TIMEOUT);

			result.getEntries().put(MediatrixResponse.KEY_STATUS, MediatrixResponse.SUCCESS);
		} catch (Exception e) {
			Log.loginfo( "IF4.3: Exception pushClient: " + e.getMessage());
			Log.logerror(e); //"IF4.3: Exception pushClient: " + e.getMessage());

			result.getEntries().put(MediatrixResponse.KEY_STATUS, MediatrixResponse.ERROR);
		}

		 Log.loginfo("IF4.3: Time after pushClient: " + formatter.format(System.currentTimeMillis()));
		return result;
	}

	private boolean getClientModus() {
		if (Repository.getObject(Repository.OPERATORMODE) != null) {
			return true;
		}
		return false;
	}

	private void setValueInExtraColumn(IMXClientEvent event, int column, String value) {

		switch (column) {
			case 1:
				if (event instanceof OpenInBoxMailEvent) {
					((OpenInBoxMailEvent) event).setExtra1(value);
				}

				if (event instanceof OpenOpModusMailEvent) {
					((OpenOpModusMailEvent) event).setExtra1(value);
				}
				if (event instanceof ExtendedNewMailEvent) {
					((ExtendedNewMailEvent) event).setExtra1(value);
				}

				break;

			case 2:
				if (event instanceof OpenInBoxMailEvent) {
					((OpenInBoxMailEvent) event).setExtra2(value);
				}

				if (event instanceof OpenOpModusMailEvent) {
					((OpenOpModusMailEvent) event).setExtra2(value);
				}
				if (event instanceof ExtendedNewMailEvent) {
					((ExtendedNewMailEvent) event).setExtra2(value);
				}
				break;

			case 3:
				if (event instanceof OpenInBoxMailEvent) {
					((OpenInBoxMailEvent) event).setExtra3(value);
				}

				if (event instanceof OpenOpModusMailEvent) {
					((OpenOpModusMailEvent) event).setExtra3(value);
				}
				if (event instanceof ExtendedNewMailEvent) {
					((ExtendedNewMailEvent) event).setExtra3(value);
				}
				break;

			case 4:
				if (event instanceof OpenInBoxMailEvent) {
					((OpenInBoxMailEvent) event).setExtra4(value);
				}

				if (event instanceof OpenOpModusMailEvent) {
					((OpenOpModusMailEvent) event).setExtra4(value);
				}
				if (event instanceof ExtendedNewMailEvent) {
					((ExtendedNewMailEvent) event).setExtra4(value);
				}

				break;

			case 5:
				if (event instanceof OpenInBoxMailEvent) {
					((OpenInBoxMailEvent) event).setExtra5(value);
				}

				if (event instanceof OpenOpModusMailEvent) {
					((OpenOpModusMailEvent) event).setExtra5(value);
				}
				if (event instanceof ExtendedNewMailEvent) {
					((ExtendedNewMailEvent) event).setExtra5(value);
				}

				break;

			case 6:
				if (event instanceof OpenInBoxMailEvent) {
					((OpenInBoxMailEvent) event).setExtra6(value);
				}

				if (event instanceof OpenOpModusMailEvent) {
					((OpenOpModusMailEvent) event).setExtra6(value);
				}
				if (event instanceof ExtendedNewMailEvent) {
					((ExtendedNewMailEvent) event).setExtra6(value);
				}
				break;

			case 7:
				if (event instanceof OpenInBoxMailEvent) {
					((OpenInBoxMailEvent) event).setExtra7(value);
				}

				if (event instanceof OpenOpModusMailEvent) {
					((OpenOpModusMailEvent) event).setExtra7(value);
				}
				if (event instanceof ExtendedNewMailEvent) {
					((ExtendedNewMailEvent) event).setExtra7(value);
				}
				break;

			case 8:
				if (event instanceof OpenInBoxMailEvent) {
					((OpenInBoxMailEvent) event).setExtra8(value);
				}

				if (event instanceof OpenOpModusMailEvent) {
					((OpenOpModusMailEvent) event).setExtra8(value);
				}
				if (event instanceof ExtendedNewMailEvent) {
					((ExtendedNewMailEvent) event).setExtra8(value);
				}

				break;

			case 9:
				if (event instanceof OpenInBoxMailEvent) {
					((OpenInBoxMailEvent) event).setExtra9(value);
				}

				if (event instanceof OpenOpModusMailEvent) {
					((OpenOpModusMailEvent) event).setExtra9(value);
				}
				if (event instanceof ExtendedNewMailEvent) {
					((ExtendedNewMailEvent) event).setExtra9(value);
				}
				break;

			case 10:
				if (event instanceof OpenInBoxMailEvent) {
					((OpenInBoxMailEvent) event).setExtra10(value);
				}

				if (event instanceof OpenOpModusMailEvent) {
					((OpenOpModusMailEvent) event).setExtra10(value);
				}
				if (event instanceof ExtendedNewMailEvent) {
					((ExtendedNewMailEvent) event).setExtra10(value);
				}

				break;

			case 11:
				if (event instanceof OpenInBoxMailEvent) {
					((OpenInBoxMailEvent) event).setExtra11(value);
				}

				if (event instanceof OpenOpModusMailEvent) {
					((OpenOpModusMailEvent) event).setExtra11(value);
				}

				if (event instanceof ExtendedNewMailEvent) {
					((ExtendedNewMailEvent) event).setExtra11(value);
				}

				break;

			default:
				break;
		}

	}

	public int getQuestionIdFromContacId(String value, int column) {

		int result = 0;
		IConnectionPool pool = DBConnectionPoolFactory.getPool();
		Connection con = pool.getCon();
		try {
			PreparedStatement selectSt = con.prepareStatement("SELECT frage.id AS id FROM frage, email WHERE email.id = emailid AND extra" + column + "= ? and frage.geloeschtam = 0");
			selectSt.setString(1, value);
			ResultSet rs = selectSt.executeQuery();
			if (rs.next()) {
				result = rs.getInt("id");
			}
			rs.close();
			selectSt.close();
		} catch (SQLException e) {
			Log.loginfo( "IF4.3: Exception getQuestionIdFromContacId: " + e.getMessage());
			Log.logerror(e); //"IF4.3: Exception pushClient: " + e.getMessage());

			return 0;
		} finally {
			pool.releaseCon(con);
		}
		return result;

	}
}
