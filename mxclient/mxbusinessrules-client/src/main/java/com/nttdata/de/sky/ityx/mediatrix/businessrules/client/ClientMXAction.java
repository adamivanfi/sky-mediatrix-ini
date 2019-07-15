/**
 *
 */
package com.nttdata.de.sky.ityx.mediatrix.businessrules.client;

import com.nttdata.de.ityx.mediatrix.businessrules.interfaces.IMXActions_Sky;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.Actions;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.client.util.Repository;
import de.ityx.mediatrix.data.Email;
import de.ityx.mediatrix.data.Question;

import javax.swing.*;
import java.util.*;

/**
 *
 */
public class ClientMXAction implements IMXActions_Sky {

	private static final String	NOT_UNIQUE	= "NOT UNIQUE";

	@Override
	public String HD_Modell(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getClientLogger().info(logPrefix + ": enter");

		Map<String, String> customer = getCurrentCustomer(email, session);

		String ret = customer.get("TypeOfHarddisk");
		SkyLogger.getClientLogger().debug(logPrefix + ": HD_Modell = " + ret);

		SkyLogger.getClientLogger().info(logPrefix + ": exit");
		return ret != null && ret.trim().length() > 0 && !ret.equals(NOT_UNIQUE) ? ret : getUnknownTagValue("HD_Modell");
	}

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
		SkyLogger.getClientLogger().info(logPrefix + ": enter");

		Map<String, String> customer = getCurrentCustomer(email, session);

		String ret = customer.get("TypeOfReceiver");
		SkyLogger.getClientLogger().debug(logPrefix + ": Receiver_Modell = " + ret);

		SkyLogger.getClientLogger().info(logPrefix + ": exit");
		return ret != null && ret.trim().length() > 0 && !ret.equals(NOT_UNIQUE) ? ret : getUnknownTagValue("Receiver_Modell");
	}

	@Override
	public String CI_Plus_Modell(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getClientLogger().info(logPrefix + ": enter");

		Map<String, String> customer = getCurrentCustomer(email, session);

		String ret = customer.get("TypeOfCIPlus");
		SkyLogger.getClientLogger().debug(logPrefix + ": CI_Plus_Modell = " + ret);

		SkyLogger.getClientLogger().info(logPrefix + ": exit");
		return ret != null && ret.trim().length() > 0 && !ret.equals(NOT_UNIQUE) ? ret : getUnknownTagValue("CI_Plus_Modell");
	}

	@Override
	public String Stiege(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getClientLogger().info(logPrefix + ": enter");

		Map<String, String> customer = getCurrentCustomer(email, session);

		String ret = customer.get("Staircase");
		SkyLogger.getClientLogger().debug(logPrefix + ": Stiege = " + ret);

		SkyLogger.getClientLogger().info(logPrefix + ": exit");
		return ret != null && ret.trim().length() > 0 && !ret.equals(NOT_UNIQUE) ? ret : getUnknownTagValue("Stiege");
	}

	@Override
	public String Etage(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getClientLogger().info(logPrefix + ": enter");

		Map<String, String> customer = getCurrentCustomer(email, session);

		String ret = customer.get("Floor");
		SkyLogger.getClientLogger().debug(logPrefix + ": Etage = " + ret);

		SkyLogger.getClientLogger().info(logPrefix + ": exit");
		return ret != null && ret.trim().length() > 0 && !ret.equals(NOT_UNIQUE) ? ret : getUnknownTagValue("Etage");
	}

	@Override
	public String Wohnungsnummer(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getClientLogger().info(logPrefix + ": enter");

		Map<String, String> customer = getCurrentCustomer(email, session);

		String ret = customer.get("FlatNumber");
		SkyLogger.getClientLogger().debug(logPrefix + ": Wohnungsnummer = " + ret);

		SkyLogger.getClientLogger().info(logPrefix + ": exit");
		return ret != null && ret.trim().length() > 0 && !ret.equals(NOT_UNIQUE) ? ret : getUnknownTagValue("Wohnungsnummer");
	}

	@Override
	public String Telefon_Mobil(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getClientLogger().info(logPrefix + ": enter");

		Map<String, String> customer = getCurrentCustomer(email, session);

		String ret = customer.get("MobileNumber");
		SkyLogger.getClientLogger().debug(logPrefix + ": Telefon_Mobil = " + ret);

		SkyLogger.getClientLogger().info(logPrefix + ": exit");
		return ret != null && ret.trim().length() > 0 && !ret.equals(NOT_UNIQUE) ? ret : getUnknownTagValue("Telefon_Mobil");
	}

	@Override
	public String Vertragsnummer(Email email, String fullmatch, Hashtable<String, Object> session, Properties props) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getClientLogger().info(logPrefix + ": enter");

		Map<String, String> customer = getCurrentCustomer(email, session);

		String ret = customer.get("SelectedContractNumber");
		SkyLogger.getClientLogger().debug(logPrefix + ": Vertragsnummer = " + ret);

		SkyLogger.getClientLogger().info(logPrefix + ": exit");
		return ret != null && ret.trim().length() > 0 && !ret.equals(NOT_UNIQUE) ? ret : getUnknownTagValue("Vertragsnummer");
	}

	/**
	 * @param email
	 * @param session
	 * @return
	 */
	private static synchronized Map<String, String> getCurrentCustomer(Email email, Hashtable<String, Object> session) {
		Class clazz = ClientMXAction.class;
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getClientLogger().info(logPrefix + ": enter");
		Map<String, String> customer = (Map<String, String>) session.get("customer");
		Long lastTimestamp = (Long) session.get("timestamp");
		boolean expired = lastTimestamp == null || System.currentTimeMillis() - lastTimestamp > 60000;
		if (customer == null || expired) {
			SkyLogger.getClientLogger().info(logPrefix + ": load from server");
			List<Object> parameter = new ArrayList<Object>();
			final Question question = (Question) Repository.getObject(Repository.THEQUESTION);
			parameter.add((question.getProjectId()>0)?question.getProjectId():110);
			parameter.add(question.getId());
			parameter.add(API.getClientAPI().getConnectionAPI().getCurrentOperatorShort().getId());
			Boolean opMode = API.getClientAPI().getProcessingAPI().isOperatorModeStart();
			parameter.add(opMode);
			List<Object> result = (List<Object>) API.getClientAPI().getConnectionAPI().sendServerEvent(Actions.ACTION_MX_RESOLVE.name(), parameter);
			customer = (Map<String, String>) result.get(0);
			session.put("customer", customer);
			session.put("timestamp", new Long(System.currentTimeMillis()));
		}
		SkyLogger.getClientLogger().info(logPrefix + ": exit");
		return customer;
	}

	@Override
	public String Vertragsdatum(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getClientLogger().info(logPrefix + ": enter");

		Map<String, String> customer = getCurrentCustomer(email, session);

		String ret = customer.get(TagMatchDefinitions.META_CONTRACT_DATE);
		SkyLogger.getClientLogger().debug(logPrefix + ": Vertragsdatum = " + ret);
		
		SkyLogger.getClientLogger().info(logPrefix + ": exit");
		return ret != null && ret.trim().length() > 0 && !ret.equals(NOT_UNIQUE) ? ret : getUnknownTagValue("Vertragsdatum");
	}

	@Override
	public String SMC_Seriennummer(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getClientLogger().info(logPrefix + ": enter");

		Map<String, String> customer = getCurrentCustomer(email, session);

		String ret = customer.get("SerialSmartcard");
		SkyLogger.getClientLogger().debug(logPrefix + ": SMC_Seriennummer = " + ret);

		SkyLogger.getClientLogger().info(logPrefix + ": exit");
		return ret != null && ret.trim().length() > 0 && !ret.equals(NOT_UNIQUE) ? ret : getUnknownTagValue("SMC_Seriennummer");
	}

	@Override
	public String HD_Seriennummer(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getClientLogger().info(logPrefix + ": enter");

		Map<String, String> customer = getCurrentCustomer(email, session);

		String ret = customer.get("SerialHarddisk");
		SkyLogger.getClientLogger().debug(logPrefix + ": HD_Seriennummer = " + ret);

		SkyLogger.getClientLogger().info(logPrefix + ": exit");
		return ret != null && ret.trim().length() > 0 && !ret.equals(NOT_UNIQUE) ? ret : getUnknownTagValue("HD_Seriennummer");
	}

	@Override
	public String Receiver_Seriennummer(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getClientLogger().info(logPrefix + ": enter");

		Map<String, String> customer = getCurrentCustomer(email, session);

		String ret = customer.get("SerialReceiver");
		SkyLogger.getClientLogger().debug(logPrefix + ": Receiver_Seriennummer = " + ret);

		SkyLogger.getClientLogger().info(logPrefix + ": exit");
		return ret != null && ret.trim().length() > 0 && !ret.equals(NOT_UNIQUE) ? ret : getUnknownTagValue("Receiver_Seriennummer");
	}

	@Override
	public String CI_Plus_Seriennummer(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getClientLogger().info(logPrefix + ": enter");

		Map<String, String> customer = getCurrentCustomer(email, session);

		String ret = customer.get("SerialCIPlus");
		SkyLogger.getClientLogger().debug(logPrefix + ": CI_Plus_Seriennummer = " + ret);

		SkyLogger.getClientLogger().info(logPrefix + ": exit");
		return ret != null && ret.trim().length() > 0 && !ret.equals(NOT_UNIQUE) ? ret : getUnknownTagValue("CI_Plus_Seriennummer");
	}

	@Override
	public String Kundennummer(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getClientLogger().info(logPrefix + ": enter");

		Map<String, String> customer = getCurrentCustomer(email, session);

		String ret = customer.get("Number");
		SkyLogger.getClientLogger().debug(logPrefix + ": Kundennummer = " + ret);

		SkyLogger.getClientLogger().info(logPrefix + ": exit");
		return ret != null && ret.trim().length() > 0 && !ret.equals(NOT_UNIQUE) ? ret : getUnknownTagValue("Kundennummer");
	}

	@Override
	public String Anrede(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getClientLogger().info(logPrefix + ": enter");

		Map<String, String> customer = getCurrentCustomer(email, session);

		String ret = customer.get("Salutation");
		SkyLogger.getClientLogger().debug(logPrefix + ": Anrede = " + ret);

		SkyLogger.getClientLogger().info(logPrefix + ": exit");
		return ret != null && ret.trim().length() > 0 && !ret.equals(NOT_UNIQUE) ? ret : getUnknownTagValue("Anrede");
	}

	@Override
	public String Vorname(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getClientLogger().info(logPrefix + ": enter");

		Map<String, String> customer = getCurrentCustomer(email, session);

		String ret = customer.get("FirstName");
		SkyLogger.getClientLogger().debug(logPrefix + ": Vorname = " + ret);

		SkyLogger.getClientLogger().info(logPrefix + ": exit");
		return ret != null && ret.trim().length() > 0 && !ret.equals(NOT_UNIQUE) ? ret : getUnknownTagValue("Vorname");
	}

	@Override
	public String Nachname(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getClientLogger().info(logPrefix + ": enter");

		Map<String, String> customer = getCurrentCustomer(email, session);

		String ret = customer.get("LastName");
		SkyLogger.getClientLogger().debug(logPrefix + ": Nachname = " + ret);

		SkyLogger.getClientLogger().info(logPrefix + ": exit");
		return ret != null && ret.trim().length() > 0 && !ret.equals(NOT_UNIQUE) ? ret : getUnknownTagValue("Nachname");
	}

	@Override
	public String Strasse(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getClientLogger().info(logPrefix + ": enter");

		Map<String, String> customer = getCurrentCustomer(email, session);

		String ret = customer.get("Street");
		SkyLogger.getClientLogger().debug(logPrefix + ": Straße = " + ret);

		SkyLogger.getClientLogger().info(logPrefix + ": exit");
		return ret != null && ret.trim().length() > 0 && !ret.equals(NOT_UNIQUE) ? ret : getUnknownTagValue("Strasse");
	}

	@Override
	public String PLZ(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getClientLogger().info(logPrefix + ": enter");

		Map<String, String> customer = getCurrentCustomer(email, session);

		String ret = customer.get("ZipCode");
		SkyLogger.getClientLogger().debug(logPrefix + ": PLZ = " + ret);

		SkyLogger.getClientLogger().info(logPrefix + ": exit");
		return ret != null && ret.trim().length() > 0 && !ret.equals(NOT_UNIQUE) ? ret : getUnknownTagValue("PLZ");
	}

	@Override
	public String Ort(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getClientLogger().info(logPrefix + ": enter");

		Map<String, String> customer = getCurrentCustomer(email, session);

		String ret = customer.get("City");
		SkyLogger.getClientLogger().debug(logPrefix + ": Ort = " + ret);

		SkyLogger.getClientLogger().info(logPrefix + ": exit");
		return ret != null && ret.trim().length() > 0 && !ret.equals(NOT_UNIQUE) ? ret : getUnknownTagValue("Ort");
	}

	@Override
	public String Kontonummer(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getClientLogger().info(logPrefix + ": enter");

		Map<String, String> customer = getCurrentCustomer(email, session);

		String ret = customer.get("AccountNumberShort");
		SkyLogger.getClientLogger().debug(logPrefix + ": Kontonummer = " + ret);

		SkyLogger.getClientLogger().info(logPrefix + ": exit");
		return ret != null && ret.trim().length() > 0 && !ret.equals(NOT_UNIQUE) ? ret : getUnknownTagValue("Kontonummer");
	}

	@Override
	public String Bankleitzahl(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getClientLogger().info(logPrefix + ": enter");

		Map<String, String> customer = getCurrentCustomer(email, session);

		String ret = customer.get("BankCode");
		SkyLogger.getClientLogger().debug(logPrefix + ": Bankleitzahl = " + ret);

		SkyLogger.getClientLogger().info(logPrefix + ": exit");
		return ret != null && ret.trim().length() > 0 && !ret.equals(NOT_UNIQUE) ? ret : getUnknownTagValue("Bankleitzahl");
	}

	@Override
	public String Name_Kontoinhaber(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getClientLogger().info(logPrefix + ": enter");

		Map<String, String> customer = getCurrentCustomer(email, session);

		String ret = customer.get("BankAccountHolder");
		SkyLogger.getClientLogger().debug(logPrefix + ": Name_Kontoinhaber = " + ret);

		SkyLogger.getClientLogger().info(logPrefix + ": exit");
		return ret != null && ret.trim().length() > 0 && !ret.equals(NOT_UNIQUE) ? ret : getUnknownTagValue("Name_Kontoinhaber");
	}

	@Override
	public String Telefon_Kontakt(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getClientLogger().info(logPrefix + ": enter");

		Map<String, String> customer = getCurrentCustomer(email, session);

		String ret = customer.get("TelephoneNumber");
		SkyLogger.getClientLogger().debug(logPrefix + ": Telefon_Kontakt = " + ret);

		SkyLogger.getClientLogger().info(logPrefix + ": exit");
		return ret != null && ret.trim().length() > 0 && !ret.equals(NOT_UNIQUE) ? ret : getUnknownTagValue("Telefon_Kontakt");
	}

	@Override
	public String Abo_Beginn(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getClientLogger().info(logPrefix + ": enter");

		Map<String, String> customer = getCurrentCustomer(email, session);

		String ret = customer.get("SubscriptionStartDate");
		SkyLogger.getClientLogger().debug(logPrefix + ": Abo_Beginn = " + ret);

		SkyLogger.getClientLogger().info(logPrefix + ": exit");
		return ret != null && ret.trim().length() > 0 && !ret.equals(NOT_UNIQUE) ? ret : getUnknownTagValue("Abo_Beginn");
	}

	@Override
	public String Vorgemerktes_Kuendigungsdatum(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getClientLogger().info(logPrefix + ": enter");

		Map<String, String> customer = getCurrentCustomer(email, session);

		String ret = customer.get("EarmarkedCancelationDate");
		if (ret!=null){
			ret = ret.replaceAll("\\d\\d:\\d\\d:\\d\\d", "");
		} //sc:455412
		SkyLogger.getClientLogger().debug(logPrefix + ": Vorgemerktes_Kuendigungsdatum = " + ret);

		SkyLogger.getClientLogger().info(logPrefix + ": exit");
		return ret != null && ret.trim().length() > 0 && !ret.equals(NOT_UNIQUE) ? ret : getUnknownTagValue("Vorgemerktes_Kuendigungsdatum");
	}

	@Override
	public String Kuendigung_moeglich_zum(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getClientLogger().info(logPrefix + ": enter");

		Map<String, String> customer = getCurrentCustomer(email, session);

		String ret = customer.get("PossibleCancelationDate");
		SkyLogger.getClientLogger().debug(logPrefix + ": Kuendigung_moeglich_zum = " + ret);

		SkyLogger.getClientLogger().info(logPrefix + ": exit");
		return ret != null && ret.trim().length() > 0 && !ret.equals(NOT_UNIQUE) ? ret : getUnknownTagValue("Kuendigung_moeglich_zum");
	}

	@Override
	public String Mitarbeiter_Name(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getClientLogger().info(logPrefix + ": enter");

		String ret = API.getClientAPI().getConnectionAPI().getCurrentOperatorShort().getName();
		SkyLogger.getClientLogger().debug(logPrefix + ": Mitarbeiter_Name = " + ret);

		SkyLogger.getClientLogger().info(logPrefix + ": exit");
		return ret != null && ret.trim().length() > 0 && !ret.equals(NOT_UNIQUE) ? ret : getUnknownTagValue("Mitarbeiter_Name");
	}

	@Override
	public String Aktueller_Saldo(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getClientLogger().info(logPrefix + ": enter");

		Map<String, String> customer = getCurrentCustomer(email, session);

		String ret = customer.get("AccountBalance");
		SkyLogger.getClientLogger().debug(logPrefix + ": Aktueller_Saldo = " + ret);

		SkyLogger.getClientLogger().info(logPrefix + ": exit");
		return ret != null && ret.trim().length() > 0 && !ret.equals(NOT_UNIQUE) ? ret : getUnknownTagValue("Aktueller_Saldo");
	}

	@Override
	public String Betrag_letztes_Mahnschreiben(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getClientLogger().info(logPrefix + ": enter");

		Map<String, String> customer = getCurrentCustomer(email, session);

		String ret = customer.get("DunningAmount");
		SkyLogger.getClientLogger().debug(logPrefix + ": Betrag_letztes_Mahnschreiben = " + ret);

		SkyLogger.getClientLogger().info(logPrefix + ": exit");
		return ret != null && ret.trim().length() > 0 && !ret.equals(NOT_UNIQUE) ? ret : getUnknownTagValue("Betrag_letztes_Mahnschreiben");
	}

	/*@Override
	public String CustomerIBAN(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getClientLogger().info(logPrefix + ": enter");

		Map<String, String> customer = getCurrentCustomer(email, session);

		String ret = customer.get("CustomerIBAN");
		SkyLogger.getClientLogger().debug(logPrefix + ": CustomerIBAN = " + ret);

		SkyLogger.getClientLogger().info(logPrefix + ": exit");
		return ret != null && ret.trim().length() > 0 && !ret.equals(NOT_UNIQUE) ? ret : getUnknownTagValue("CustomerIBAN");
	}*/

	@Override
	public String CustomerBIC(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getClientLogger().info(logPrefix + ": enter");

		Map<String, String> customer = getCurrentCustomer(email, session);

		String ret = customer.get("CustomerBIC");
		SkyLogger.getClientLogger().debug(logPrefix + ": CustomerBIC = " + ret);

		SkyLogger.getClientLogger().info(logPrefix + ": exit");
		return ret != null && ret.trim().length() > 0 && !ret.equals(NOT_UNIQUE) ? ret : getUnknownTagValue("CustomerBIC");
	}

	@Override
	public String MandateRefId(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getClientLogger().info(logPrefix + ": enter");

		Map<String, String> customer = getCurrentCustomer(email, session);

		String ret = customer.get("MandateRefId");
		SkyLogger.getClientLogger().debug(logPrefix + ": MandateRefId = " + ret);

		SkyLogger.getClientLogger().info(logPrefix + ": exit");
		return ret != null && ret.trim().length() > 0 && !ret.equals(NOT_UNIQUE) ? ret : getUnknownTagValue("MandateRefId");
	}

	@Override
	public String SignatureDate(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getClientLogger().info(logPrefix + ": enter");

		Map<String, String> customer = getCurrentCustomer(email, session);

		String ret = customer.get("SignatureDate");
		SkyLogger.getClientLogger().debug(logPrefix + ": SignatureDate = " + ret);

		SkyLogger.getClientLogger().info(logPrefix + ": exit");
		return ret != null && ret.trim().length() > 0 && !ret.equals(NOT_UNIQUE) ? ret : getUnknownTagValue("SignatureDate");
	}

	@Override
	public String SignatureFlag(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getClientLogger().info(logPrefix + ": enter");

		Map<String, String> customer = getCurrentCustomer(email, session);

		String ret = customer.get("SignatureFlag");
		SkyLogger.getClientLogger().debug(logPrefix + ": SignatureFlag = " + ret);

		SkyLogger.getClientLogger().info(logPrefix + ": exit");
		return ret != null && ret.trim().length() > 0 && !ret.equals(NOT_UNIQUE) ? ret : getUnknownTagValue("SignatureFlag");
	}

	@Override
	public String MandateStatus(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getClientLogger().info(logPrefix + ": enter");

		Map<String, String> customer = getCurrentCustomer(email, session);

		String ret = customer.get("MandateStatus");
		SkyLogger.getClientLogger().debug(logPrefix + ": MandateStatus = " + ret);

		SkyLogger.getClientLogger().info(logPrefix + ": exit");
		return ret != null && ret.trim().length() > 0 && !ret.equals(NOT_UNIQUE) ? ret : getUnknownTagValue("MandateStatus");
	}

	@Override
	public String SbsCompany(Email email, String fullmatch, Hashtable<String, Object> session, Properties parameter) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getClientLogger().info(logPrefix + ": enter");

		Map<String, String> customer = getCurrentCustomer(email, session);

		String ret = customer.get("SbsCompany");
		SkyLogger.getClientLogger().debug(logPrefix + ": SbsCompany = " + ret);

		SkyLogger.getClientLogger().info(logPrefix + ": exit");
		return ret != null && ret.trim().length() > 0 && !ret.equals(NOT_UNIQUE) ? ret : getUnknownTagValue("SbsCompany");
	}
}