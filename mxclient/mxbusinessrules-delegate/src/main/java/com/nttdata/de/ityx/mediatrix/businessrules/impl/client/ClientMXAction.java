/**
 * 
 */
package com.nttdata.de.ityx.mediatrix.businessrules.impl.client;

import com.nttdata.de.ityx.mediatrix.businessrules.interfaces.IMXActions_Sky;
import com.nttdata.de.lib.logging.SkyLogger;
import de.ityx.mediatrix.data.Email;

import javax.swing.*;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

/**
 *
 */
public class ClientMXAction implements IMXActions_Sky {

	private static final String	NOT_UNIQUE	= "NOT UNIQUE";

	/** @TODO: Why is this Method static?
	 * @param email
	 * @param session
	 * @return
	 */
	private static synchronized Map<String, String> getCurrentCustomer(Email email, Hashtable<String, Object> session) {
		Class clazz = ClientMXAction.class;
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;

		SkyLogger.getClientLogger().error(logPrefix + "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));
		return null;
	}



	@Override
	public String HD_Modell(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		
		SkyLogger.getClientLogger().error(logPrefix + "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));
		return null;	}

	/**
	 * @param tagTitle
	 *            Names the unknown tag.
	 * @return The value to be used for the unknown tag.
	 */
	public String getUnknownTagValue(String tagTitle) {
		return JOptionPane.showInputDialog("Bitte den Wert für <" + tagTitle + "> eingeben.");
	}

	@Override
	public String Receiver_Modell(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		
		SkyLogger.getClientLogger().error(logPrefix + "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));
		return null;	}

	@Override
	public String CI_Plus_Modell(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		
		SkyLogger.getClientLogger().error(logPrefix + "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));
		return null;}

	@Override
	public String Stiege(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		
		SkyLogger.getClientLogger().error(logPrefix + "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));
		return null;}

	@Override
	public String Etage(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		
		SkyLogger.getClientLogger().error(logPrefix + "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));
		return null;	}

	@Override
	public String Wohnungsnummer(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;

		SkyLogger.getClientLogger().error(logPrefix + "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));
		return null;
	}

	@Override
	public String Telefon_Mobil(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;

		SkyLogger.getClientLogger().error(logPrefix + "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));
		return null;
	}


	@Override
	public String Vertragsnummer(Email email, String fullmatch, Hashtable<String, Object> session, Properties props) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;

		SkyLogger.getClientLogger().error(logPrefix + "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));
		return null;
	}





	@Override
	public String Vertragsdatum(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;

		SkyLogger.getClientLogger().error(logPrefix + "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));
		return null;
	}

	@Override
	public String SMC_Seriennummer(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;

		SkyLogger.getClientLogger().error(logPrefix + "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));
		return null;
	}


	@Override
	public String HD_Seriennummer(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;

		SkyLogger.getClientLogger().error(logPrefix + "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));
		return null;
	}

	@Override
	public String Receiver_Seriennummer(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;

		SkyLogger.getClientLogger().error(logPrefix + "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));
		return null;
	}

	@Override
	public String CI_Plus_Seriennummer(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;

		SkyLogger.getClientLogger().error(logPrefix + "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));
		return null;
	}


	@Override
	public String Kundennummer(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;

		SkyLogger.getClientLogger().error(logPrefix + "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));
		return null;
	}

	@Override
	public String Anrede(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;

		SkyLogger.getClientLogger().error(logPrefix + "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));
		return null;
	}

	@Override
	public String Vorname(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;

		SkyLogger.getClientLogger().error(logPrefix + "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));
		return null;
	}


	@Override
	public String Nachname(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;

		SkyLogger.getClientLogger().error(logPrefix + "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));
		return null;
	}


	@Override
	public String Strasse(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;

		SkyLogger.getClientLogger().error(logPrefix + "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));
		return null;
	}


	@Override
	public String PLZ(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;

		SkyLogger.getClientLogger().error(logPrefix + "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));
		return null;
	}


	@Override
	public String Ort(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;

		SkyLogger.getClientLogger().error(logPrefix + "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));
		return null;
	}


	@Override
	public String Kontonummer(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;

		SkyLogger.getClientLogger().error(logPrefix + "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));
		return null;
	}


	@Override
	public String Bankleitzahl(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;

		SkyLogger.getClientLogger().error(logPrefix + "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));
		return null;
	}


	@Override
	public String Name_Kontoinhaber(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;

		SkyLogger.getClientLogger().error(logPrefix + "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));
		return null;
	}


	@Override
	public String Telefon_Kontakt(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;

		SkyLogger.getClientLogger().error(logPrefix + "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));
		return null;
	}


	@Override
	public String Abo_Beginn(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;

		SkyLogger.getClientLogger().error(logPrefix + "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));
		return null;
	}


	@Override
	public String Vorgemerktes_Kuendigungsdatum(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;

		SkyLogger.getClientLogger().error(logPrefix + "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));
		return null;
	}


	@Override
	public String Kuendigung_moeglich_zum(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;

		SkyLogger.getClientLogger().error(logPrefix + "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));
		return null;
	}


	@Override
	public String Mitarbeiter_Name(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;

		SkyLogger.getClientLogger().error(logPrefix + "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));
		return null;
	}


	@Override
	public String Aktueller_Saldo(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;

		SkyLogger.getClientLogger().error(logPrefix + "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));
		return null;
	}

	@Override
	public String Betrag_letztes_Mahnschreiben(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;

		SkyLogger.getClientLogger().error(logPrefix + "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));
		return null;
	}


	/*@Override
	public String CustomerIBAN(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;

		SkyLogger.getClientLogger().error(logPrefix + "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));
		return null;
	}*/


	@Override
	public String CustomerBIC(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;

		SkyLogger.getClientLogger().error(logPrefix + "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));
		return null;
	}


	@Override
	public String MandateRefId(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;

		SkyLogger.getClientLogger().error(logPrefix + "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));
		return null;
	}


	@Override
	public String SignatureDate(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;

		SkyLogger.getClientLogger().error(logPrefix + "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));
		return null;
	}

	@Override
	public String SignatureFlag(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;

		SkyLogger.getClientLogger().error(logPrefix + "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));
		return null;
	}


	@Override
	public String MandateStatus(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;

		SkyLogger.getClientLogger().error(logPrefix + "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));
		return null;
	}

	@Override
	public String SbsCompany(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;

		SkyLogger.getClientLogger().error(logPrefix + "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));
		return null;
	}

}
