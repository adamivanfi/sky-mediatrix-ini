package com.nttdata.de.ityx.mediatrix.businessrules.interfaces;

import de.ityx.mediatrix.data.Email;

import java.util.Hashtable;
import java.util.Properties;

public interface IMXActions_Sky {

	String HD_Modell(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter);

	String Receiver_Modell(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter);

	String CI_Plus_Modell(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter);

	String Stiege(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter);

	String Etage(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter);

	String Wohnungsnummer(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter);

	String Telefon_Mobil(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter);

	String Vertragsnummer(Email email, String fullmatch, Hashtable<String, Object> session, Properties props);

	String Vertragsdatum(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter);

	String SMC_Seriennummer(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter);

	String HD_Seriennummer(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter);

	String Receiver_Seriennummer(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter);

	String CI_Plus_Seriennummer(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter);

	String Kundennummer(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter);

	String Anrede(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter);

	String Vorname(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter);

	String Nachname(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter);

	String Strasse(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter);

	String PLZ(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter);

	String Ort(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter);

	String Kontonummer(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter);

	String Bankleitzahl(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter);

	String Name_Kontoinhaber(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter);

	String Telefon_Kontakt(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter);

	String Abo_Beginn(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter);

	String Vorgemerktes_Kuendigungsdatum(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter);

	String Kuendigung_moeglich_zum(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter);

	String Mitarbeiter_Name(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter);

	String Aktueller_Saldo(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter);

	String Betrag_letztes_Mahnschreiben(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter);

	//String CustomerIBAN(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter);

	String CustomerBIC(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter);

	String MandateRefId(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter);

	String MandateStatus(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter);

	String SignatureDate(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter);

	String SignatureFlag(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter);

	String SbsCompany(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter);
}
