package com.nttdata.de.ityx.mediatrix.businessrules.delegate.server;

import de.ityx.mediatrix.api.interfaces.businessrules.server.IServerSystem;
import de.ityx.mediatrix.data.Email;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

public class ServerSystem_delegate implements IServerSystem {

	private IServerSystem delegate = com.nttdata.de.ityx.mediatrix.businessrules.factories.NTTDataBusinessruleFactoryGenerator.createFactory(true).getServerSystem();

	@Override
	public void OnShutDown() {
		delegate.OnShutDown();
	}


	@Override
	public boolean preSendEmail(Connection connection, MimeMessage mimeMessage, Email email) {
		return delegate.preSendEmail(connection, mimeMessage, email);
	}

	@Override
	public void postSendEmail(Connection connection, MimeMessage mimeMessage, Email email) {
		delegate.postSendEmail(connection, mimeMessage, email);
	}

	@Override
	public void postSendEmailExceptionCheck(Connection connection, MessagingException e, MimeMessage mimeMessage, Email email) {
		delegate.postSendEmailExceptionCheck(connection, e, mimeMessage, email);
	}

	@Override
	public void OnStartUp() {
		delegate.OnStartUp();
	}

	/*
	 * Returns a list of additional configuration parameters to be editable in
	 * the ServiceCenter.
	 */
	@Override
	public List<de.ityx.mediatrix.api.interfaces.businessrules.server.IServerSystem.PropertyKey> addPropertyKeys() {
		return delegate.addPropertyKeys();
	}

	@Override
	public int checkFunction(Object p0, String p1, String p2, Object p3, boolean p4) {
		return delegate.checkFunction(p0, p1, p2, p3, p4);
	}

	@Override
	public byte[] getEmailRfc822(java.sql.Connection p0, Email p1) {
		return delegate.getEmailRfc822(p0, p1);
	}

	@Override
	public Map postExecuteInquiry(java.sql.Connection p0, Object p1, Object p2, Map p3) {
		return delegate.postExecuteInquiry(p0, p1, p2, p3);
	}

	@Override
	public Map preExecuteInquiry(java.sql.Connection p0, String p1, int p2, Object p3, Object p4, Object p5, Map p6) {
		return delegate.preExecuteInquiry(p0, p1, p2, p3, p4, p5, p6);
	}

	@Override
	public Map<java.lang.Object, java.lang.Object> updateSingleMode(java.sql.Connection con, List<de.ityx.mediatrix.data.SingleMode> data, Map<java.lang.String, java.lang.Object> hm) {
		return delegate.updateSingleMode(con, data, hm);
	}

}
