package de.ityx.sky.outbound.server;

import de.ityx.mediatrix.api.interfaces.businessrules.server.IServerMultiChannel;
import de.ityx.mediatrix.data.Email;
import de.ityx.sky.outbound.common.ServerUtils;
import de.ityx.sky.outbound.server.base.ServerBaseMultiChannel;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

public class ServerMultichannel extends ServerBaseMultiChannel {

	@Override
	public HashMap deliverToChannel(Connection con, Email email)
			throws SQLException {
		HashMap<String, String> result = new HashMap<String, String>();
		result.put(IServerMultiChannel.PARAM_RESULT, "false");
		if (email.getType() == Email.TYPE_LETTER || email.getType() == Email.TYPE_FAX) {
			try {
				ServerUtils.complete(con, email, false);
				ServerUtils.archive(con, email);
				result.put(IServerMultiChannel.PARAM_RESULT, "true");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	@Override
	public boolean isValidateReceiver(Connection connection, String s) {
		return true;
	}

}
