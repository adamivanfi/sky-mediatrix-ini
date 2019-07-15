/**
 *
 */
package com.nttdata.de.sky.ityx.mediatrix.businessrules.server;

import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.mediatrix.businessrules.server.sky.SkyServerSystem;

import de.ityx.mediatrix.api.interfaces.businessrules.server.IServerSystem;
import de.ityx.mediatrix.data.Email;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public class ServerSystem implements IServerSystem {
	
	private IServerSystem agenturdel = null;
	private final String aclazz = "de.ityx.agentursteuerung.ServerSystemRule";
	private IServerSystem skydel = new SkyServerSystem();
	public ServerSystem() {
		String logPrefix = "ServerSystem # Constructor ";
		try {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " initalization");
			Class aclass = Class.forName(aclazz);
			if (aclass != null) {
				Constructor aconstr = aclass.getConstructor(null);
				if (aconstr != null) {
					agenturdel = (IServerSystem) aconstr.newInstance(null);
				}
			}
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " initalized");
			
		} catch (NullPointerException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
			SkyLogger.getBRSLogger().warn(logPrefix + aclazz + " cannot be instantiated." + e.getMessage());
		}
	}
	
	public void OnShutDown() {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + " ";
		SkyLogger.getBRSLogger().info(logPrefix + " start");
		skydel.OnShutDown();
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			agenturdel.OnShutDown();
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finished call");
		}
	}
	
	public void OnStartUp() {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + " ";
		SkyLogger.getBRSLogger().info(logPrefix + " start");
		skydel.OnStartUp();
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			agenturdel.OnStartUp();
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finished call");
		}
	}
	
	
	public List<PropertyKey> addPropertyKeys() {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + " ";
		SkyLogger.getBRSLogger().info(logPrefix + " start");
		List<PropertyKey> result = skydel.addPropertyKeys();
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			result.addAll(agenturdel.addPropertyKeys());
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finished call");
		}
		return result;
	}
	
	public int checkFunction(Object p0, String p1, String p2, Object p3, boolean p4) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + " ";
		SkyLogger.getBRSLogger().info(logPrefix + " start");
		skydel.checkFunction(p0, p1, p2, p3, p4);
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			int res = agenturdel.checkFunction(p0, p1, p2, p3, p4);
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finished call");
			return res;
		}
		return 0;
	}
	
	public byte[] getEmailRfc822(java.sql.Connection con, Email email) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + " " + (email != null ? " email:" + email.getEmailId() + " " : " ");
		SkyLogger.getBRSLogger().info(logPrefix + " start");
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			byte[] res = agenturdel.getEmailRfc822(con, email);
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finished call");
			return res;
		}
		return skydel.getEmailRfc822(con, email);
	}
	
	public Map postExecuteInquiry(java.sql.Connection p0, Object p1, Object p2, Map p3) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + " ";
		SkyLogger.getBRSLogger().info(logPrefix + " start");
		Map result = skydel.postExecuteInquiry(p0, p1, p2, p3);
		
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			Map res = agenturdel.postExecuteInquiry(p0, p1, p2, result);
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finished call");
			return res;
		}
		return result;
	}
	
	public void postSendEmail(java.sql.Connection con, MimeMessage p1, Email email) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + " " + (email != null ? " email:" + email.getEmailId() + " " : " ");
		SkyLogger.getBRSLogger().info(logPrefix + " start");
		skydel.postSendEmail(con, p1, email);
		SkyLogger.getBRSLogger().info(logPrefix + " finish");
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			agenturdel.postSendEmail(con, p1, email);
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finished call");
		}
	}
	
	public void postSendEmailExceptionCheck(java.sql.Connection p0, javax.mail.MessagingException p1, MimeMessage p2, Email email) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + " " + (email != null ? " email:" + email.getEmailId() + " " : " ");
		SkyLogger.getBRSLogger().info(logPrefix + " start :p0"+p0+":p1:"+p1+":p2:"+p2+"email:"+email);
		skydel.postSendEmailExceptionCheck(p0, p1, p2, email);
		SkyLogger.getBRSLogger().info(logPrefix + " finish");
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			agenturdel.postSendEmailExceptionCheck(p0, p1, p2, email);
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finished call");
		}
	}
	
	public Map preExecuteInquiry(java.sql.Connection p0, String p1, int p2, Object p3, Object p4, Object p5, Map p6) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + " " + p1 + " ";
		SkyLogger.getBRSLogger().info(logPrefix + " start");
		p6 = skydel.preExecuteInquiry(p0, p1, p2, p3, p4, p5, p6);
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			Map res = agenturdel.preExecuteInquiry(p0, p1, p2, p3, p4, p5, p6);
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finished call");
			return res;
		}
		return p6;
	}
	
	public boolean preSendEmail(java.sql.Connection con, MimeMessage p1, Email email) {
		String to = null;
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + " " + (email != null ? " email:" + email.getEmailId() + " " : " ");
		if (email != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " etype:" + email.getType());
			to = email.getTo();
			if (to == null || to.isEmpty())
				email.setTo("noreply@sky.de");
			if (email.getType() == Email.TYPE_FAX || email.getType() == Email.TYPE_LETTER || email.getType() == Email.TYPE_DOCUMENT) {
				//alle nicht emails auf noreply@sky.de umschreiben
				SkyLogger.getBRSLogger().info(logPrefix + " remaping mail-address caused by sending out letter");
				
				email.setTo("noreply@sky.de");
				try {
					if (p1 != null) {
						p1.setRecipient(Message.RecipientType.TO, new InternetAddress("noreply@sky.de"));
					} else {
						email.setTo("noreply@sky.de");
					}
				} catch (MessagingException e) {
					e.printStackTrace();
				}
			} else if (email.getType() == Email.TYPE_EMAIL) {
				String ityx_environment_type = System.getProperty("ityx_environment_type");
				if (ityx_environment_type != null
						&& ityx_environment_type.equalsIgnoreCase("integration")
						&& email.getTo() != null
						&& !(email.getTo().contains("@sky.de") || email.getTo().contains("@sky.at") || email.getTo().contains("@nttdata.com"))) {
					
					SkyLogger.getBRSLogger().warn(logPrefix + " sending out emails on INT to external adresses other than @sky.de and @nttdata.com disabled. Rempapping to noreply@sky.de");
					
					//email.setTo("noreply@sky.de");
					try {
						p1.setRecipient(Message.RecipientType.TO, new InternetAddress("noreply@sky.de"));
					} catch (MessagingException e) {
						e.printStackTrace();
					}
				}
			}
		}else{
			SkyLogger.getBRSLogger().warn(logPrefix + aclazz + " empty Email!:" );
		}
	
	boolean result = skydel.preSendEmail(con, p1, email);
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			result = result && agenturdel.preSendEmail(con, p1, email);
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finished call");
		}
		return result;
	}
	
	
	public Map<java.lang.Object, java.lang.Object> updateSingleMode(java.sql.Connection con, List<de.ityx.mediatrix.data.SingleMode> data, Map<java.lang.String, java.lang.Object> hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + " ";
		SkyLogger.getBRSLogger().info(logPrefix + " start");
		Map<java.lang.Object, java.lang.Object> result = skydel.updateSingleMode(con, data, hm);
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			result.putAll(agenturdel.updateSingleMode(con, data, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finished call");
		}
		return result;
	}
}
