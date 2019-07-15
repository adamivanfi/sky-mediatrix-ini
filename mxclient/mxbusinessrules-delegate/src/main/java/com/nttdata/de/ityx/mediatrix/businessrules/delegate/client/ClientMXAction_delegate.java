package com.nttdata.de.ityx.mediatrix.businessrules.delegate.client;

import com.nttdata.de.ityx.mediatrix.businessrules.interfaces.IMXActions_Sky;
import de.ityx.mediatrix.api.interfaces.businessrules.client.IClientMXAction;
import de.ityx.mediatrix.data.Email;

import java.util.Hashtable;
import java.util.Properties;
/**
 * Created by meinusch on 06.03.15.
 */
public class ClientMXAction_delegate implements IClientMXAction {

	IMXActions_Sky delegate	=  com.nttdata.de.ityx.mediatrix.businessrules.factories.NTTDataBusinessruleFactoryGenerator.createFactory(false).getClientMXAction();

	@Override
	public String hello(Email arg0, String arg1, Hashtable<String, Object> arg2, Properties arg3, String arg4) {
		return "";
	}

	public String HD_Modell(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter, String arg4) {
		return delegate.HD_Modell(email, fullmatch, session, parameter);
	}

	public String Receiver_Modell(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter, String arg4) {
		return delegate.Receiver_Modell(email, fullmatch, session, parameter);
	}

	public String CI_Plus_Modell(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter, String arg4) {
		return delegate.CI_Plus_Modell(email, fullmatch, session, parameter);
	}

	public String Stiege(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter, String arg4) {
		return delegate.Stiege(email, fullmatch, session, parameter);
	}

	public String Etage(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter, String arg4) {
		return delegate.Etage(email, fullmatch, session, parameter);
	}

	public String Wohnungsnummer(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter, String arg4) {
		return delegate.Wohnungsnummer(email, fullmatch, session, parameter);
	}

	public String Telefon_Mobil(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter, String arg4) {
		return delegate.Telefon_Mobil(email, fullmatch, session, parameter);
	}

	public String Vertragsnummer(Email email, String fullmatch, Hashtable<String, Object> session, Properties props, String arg4) {
		return delegate.Vertragsnummer(email, fullmatch, session, props);
	}

	public String Vertragsdatum(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter, String arg4) {
		return delegate.Vertragsdatum(email, fullmatch, session, parameter);
	}

	public String SMC_Seriennummer(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter, String arg4) {
		return delegate.SMC_Seriennummer(email, fullmatch, session, parameter);
	}

	public String HD_Seriennummer(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter, String arg4) {
		return delegate.HD_Seriennummer(email, fullmatch, session, parameter);
	}

	public String Receiver_Seriennummer(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter, String arg4) {
		return delegate.Receiver_Seriennummer(email, fullmatch, session, parameter);
	}

	public String CI_Plus_Seriennummer(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter, String arg4) {
		return delegate.CI_Plus_Seriennummer(email, fullmatch, session, parameter);
	}

	public String Kundennummer(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter, String arg4) {
		return delegate.Kundennummer(email, fullmatch, session, parameter);
	}

	public String Anrede(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter, String arg4) {
		return delegate.Anrede(email, fullmatch, session, parameter);
	}

	public String Vorname(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter, String arg4) {
		return delegate.Vorname(email, fullmatch, session, parameter);
	}

	public String Nachname(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter, String arg4) {
		return delegate.Nachname(email, fullmatch, session, parameter);
	}

	public String Strasse(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter, String arg4) {
		return delegate.Strasse(email, fullmatch, session, parameter);
	}

	public String PLZ(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter, String arg4) {
		return delegate.PLZ(email, fullmatch, session, parameter);
	}

	public String Ort(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter, String arg4) {
		return delegate.Ort(email, fullmatch, session, parameter);
	}

	public String Kontonummer(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter, String arg4) {
		return delegate.Kontonummer(email, fullmatch, session, parameter);
	}

	public String Bankleitzahl(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter, String arg4) {
		return delegate.Bankleitzahl(email, fullmatch, session, parameter);
	}

	public String Name_Kontoinhaber(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter, String arg4) {
		return delegate.Name_Kontoinhaber(email, fullmatch, session, parameter);
	}

	public String Telefon_Kontakt(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter, String arg4) {
		return delegate.Telefon_Kontakt(email, fullmatch, session, parameter);
	}

	public String Abo_Beginn(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter, String arg4) {
		return delegate.Abo_Beginn(email, fullmatch, session, parameter);
	}

	public String Vorgemerktes_Kuendigungsdatum(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter, String arg4) {
		return delegate.Vorgemerktes_Kuendigungsdatum(email, fullmatch, session, parameter);
	}

	public String Kuendigung_moeglich_zum(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter, String arg4) {
		return delegate.Kuendigung_moeglich_zum(email, fullmatch, session, parameter);
	}

	public String Mitarbeiter_Name(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter, String arg4) {
		return delegate.Mitarbeiter_Name(email, fullmatch, session, parameter);
	}

	public String Aktueller_Saldo(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter, String arg4) {
		return delegate.Aktueller_Saldo(email, fullmatch, session, parameter);
	}

	public String Betrag_letztes_Mahnschreiben(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter, String arg4) {
		return delegate.Betrag_letztes_Mahnschreiben(email, fullmatch, session, parameter);
	}

	public String Abo_Beginn(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		return delegate.Abo_Beginn(email, fullmatch, session, parameter);
	}

	public String Aktueller_Saldo(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		return delegate.Aktueller_Saldo(email, fullmatch, session, parameter);
	}

	public String Anrede(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		return delegate.Anrede(email, fullmatch, session, parameter);
	}

	public String Bankleitzahl(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		return delegate.Bankleitzahl(email, fullmatch, session, parameter);
	}

	public String Betrag_letztes_Mahnschreiben(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		return delegate.Betrag_letztes_Mahnschreiben(email, fullmatch, session, parameter);
	}

	public String CI_Plus_Modell(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		return delegate.CI_Plus_Modell(email, fullmatch, session, parameter);
	}

	public String CI_Plus_Seriennummer(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		return delegate.CI_Plus_Seriennummer(email, fullmatch, session, parameter);
	}

	public String Etage(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		return delegate.Etage(email, fullmatch, session, parameter);
	}

	public String HD_Modell(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		return delegate.HD_Modell(email, fullmatch, session, parameter);
	}

	public String HD_Seriennummer(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		return delegate.HD_Seriennummer(email, fullmatch, session, parameter);
	}

	public String Kontonummer(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		return delegate.Kontonummer(email, fullmatch, session, parameter);
	}

	public String Kuendigung_moeglich_zum(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		return delegate.Kuendigung_moeglich_zum(email, fullmatch, session, parameter);
	}

	public String Kundennummer(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		return delegate.Kundennummer(email, fullmatch, session, parameter);
	}

	public String Mitarbeiter_Name(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		return delegate.Mitarbeiter_Name(email, fullmatch, session, parameter);
	}

	public String Nachname(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		return delegate.Nachname(email, fullmatch, session, parameter);
	}

	public String Name_Kontoinhaber(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		return delegate.Name_Kontoinhaber(email, fullmatch, session, parameter);
	}

	public String Ort(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		return delegate.Ort(email, fullmatch, session, parameter);
	}

	public String PLZ(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		return delegate.PLZ(email, fullmatch, session, parameter);
	}

	public String Receiver_Modell(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		return delegate.Receiver_Modell(email, fullmatch, session, parameter);
	}

	public String Receiver_Seriennummer(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		return delegate.Receiver_Seriennummer(email, fullmatch, session, parameter);
	}

	public String SMC_Seriennummer(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		return delegate.SMC_Seriennummer(email, fullmatch, session, parameter);
	}

	public String Stiege(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		return delegate.Stiege(email, fullmatch, session, parameter);
	}

	public String Strasse(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		return delegate.Strasse(email, fullmatch, session, parameter);
	}

	public String Telefon_Kontakt(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		return delegate.Telefon_Kontakt(email, fullmatch, session, parameter);
	}

	public String Telefon_Mobil(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		return delegate.Telefon_Mobil(email, fullmatch, session, parameter);
	}

	public String Vertragsdatum(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		return delegate.Vertragsdatum(email, fullmatch, session, parameter);
	}

	public String Vertragsnummer(Email email, String fullmatch, Hashtable<String, Object> session, Properties props) {
		return delegate.Vertragsnummer(email, fullmatch, session, props);
	}

	public String Vorgemerktes_Kuendigungsdatum(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		return delegate.Vorgemerktes_Kuendigungsdatum(email, fullmatch, session, parameter);
	}

	public String Vorname(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		return delegate.Vorname(email, fullmatch, session, parameter);
	}

	public String Wohnungsnummer(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		return delegate.Wohnungsnummer(email, fullmatch, session, parameter);
	}

	/*public String CustomerIBAN(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		return delegate.CustomerIBAN(email, fullmatch, session, parameter);
	}*/

	public String CustomerBIC(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		return delegate.CustomerBIC(email, fullmatch, session, parameter);
	}

	public String MandateRefId(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		return delegate.MandateRefId(email, fullmatch, session, parameter);
	}

	public String MandateStatus(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		return delegate.MandateStatus(email, fullmatch, session, parameter);
	}

	public String SignatureDate(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		return delegate.SignatureDate(email, fullmatch, session, parameter);
	}

	public String SignatureFlag(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		return delegate.SignatureFlag(email, fullmatch, session, parameter);
	}

	String SbsCompany(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		return delegate.SbsCompany(email, fullmatch, session, parameter);
	}
}
