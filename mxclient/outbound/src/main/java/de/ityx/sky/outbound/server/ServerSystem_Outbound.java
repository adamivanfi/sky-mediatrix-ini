package de.ityx.sky.outbound.server;

import de.ityx.mediatrix.api.interfaces.businessrules.server.IServerSystem;
import de.ityx.mediatrix.data.Email;
import de.ityx.mediatrix.data.SingleMode;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.sql.Connection;
import java.util.List;
import java.util.Map;


public class ServerSystem_Outbound implements IServerSystem {


	@Override
	public void OnStartUp() {

	}

	@Override
	public void OnShutDown() {

	}

	@Override
	public boolean preSendEmail(Connection connection, MimeMessage mimeMessage, Email email) {
		return true;
	}

	@Override
	public void postSendEmail(Connection connection, MimeMessage mimeMessage, Email email) {

	}

	@Override
	public void postSendEmailExceptionCheck(Connection connection, MessagingException e, MimeMessage mimeMessage, Email email) {

	}

	@Override
	public byte[] getEmailRfc822(Connection connection, Email email) {
		return new byte[0];
	}

	@Override
	public Map<Object, Object> updateSingleMode(Connection connection, List<SingleMode> list, Map<String, Object> map) {
		return null;
	}

	@Override
	public Map preExecuteInquiry(Connection connection, String s, int i, Object o, Object o1, Object o2, Map map) {
		return null;
	}

	@Override
	public Map postExecuteInquiry(Connection connection, Object o, Object o1, Map map) {
		return null;
	}

	@Override
	public int checkFunction(Object o, String s, String s1, Object o1, boolean b) {
		return 0;
	}

	@Override
	public List<PropertyKey> addPropertyKeys() {
		return null;
	}
}