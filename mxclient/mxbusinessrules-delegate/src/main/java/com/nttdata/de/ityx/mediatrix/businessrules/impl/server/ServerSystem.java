/**
 *
 */
package com.nttdata.de.ityx.mediatrix.businessrules.impl.server;

import de.ityx.mediatrix.api.interfaces.businessrules.server.IServerSystem;
import de.ityx.mediatrix.data.Email;

import javax.mail.internet.MimeMessage;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author DHIFLM
 */
public class ServerSystem implements IServerSystem {
	public void OnShutDown() {

	}

	@Override
	public void OnStartUp() {

	}

	/*
	 * Returns a list of additional configuration parameters to be editable in
	 * the ServiceCenter.
	 */
	@Override
	public List<de.ityx.mediatrix.api.interfaces.businessrules.server.IServerSystem.PropertyKey> addPropertyKeys() {
		return Collections.emptyList();
	}

	@Override
	public int checkFunction(Object p0, String p1, String p2, Object p3, boolean p4) {
		return 0;
	}

	@Override
	public byte[] getEmailRfc822(java.sql.Connection p0, Email p1) {
		return new byte[0];
	}

	@Override
	public Map postExecuteInquiry(java.sql.Connection p0, Object p1, Object p2, Map p3) {
		return Collections.emptyMap();
	}

	@Override
	public void postSendEmail(java.sql.Connection p0, MimeMessage p1, Email p2) {
	}

	@Override
	public void postSendEmailExceptionCheck(java.sql.Connection p0, javax.mail.MessagingException p1, MimeMessage p2, Email p3) {
	}

	@Override
	public Map preExecuteInquiry(java.sql.Connection p0, String p1, int p2, Object p3, Object p4, Object p5, Map p6) {
		return Collections.emptyMap();
	}

	@Override
	public boolean preSendEmail(java.sql.Connection p0, MimeMessage p1, Email p2) {
		return true;
	}


	@Override
	public Map<java.lang.Object, java.lang.Object> updateSingleMode(java.sql.Connection con, List<de.ityx.mediatrix.data.SingleMode> data, Map<java.lang.String, java.lang.Object> hm) {
		return Collections.emptyMap();
	}
}
