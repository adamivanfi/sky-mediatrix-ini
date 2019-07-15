package com.nttdata.de.sky.ityx.mediatrix.businessrules.server.sky.actions;

import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.Actions;
import de.ityx.base.Global;
import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.api.interfaces.textobjects.ITextObject;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class TextobjectAction extends AServerEventAction {

	// Contains the Id of the subproject that stores the personal signatures.
	private Integer signatureTP = Global.getIntProperty("personal.signature.tp", -1);

	@Override
	public String[] getActionNames() {
		return new String[]{Actions.ACTION_LOAD_TEXTOBJECT.name()};
	}

	@Override
	public List actionPerformed(Connection con, String actionname, List parameters) throws Exception {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName();
		SkyLogger.getMediatrixLogger().debug(logPrefix + " LoadTextObjects started ");
		int i = 0;
		for (Object par : parameters) {
			SkyLogger.getMediatrixLogger().debug(i + " par> " + par);
			i++;
		}

		String subprojectId = (String) parameters.get(0);
		String loginname = (String) parameters.get(1);
		String typ = (String) parameters.get(2);
		String country = (String) parameters.get(3);
		String preafixes = (String) parameters.get(4);
		parameters.clear();
		SkyLogger.getMediatrixLogger().debug(logPrefix + " LoadTextObjects parameters: s:" + subprojectId + " l:" + loginname + " t:" + typ + " c:" + country + " p:" + preafixes);

		try {
			List<ITextObject> filteredList = selectTemplates(con, preafixes, subprojectId, loginname, typ, country);
			filteredList.addAll(selectTemplates(con, preafixes, signatureTP + "", loginname, typ, country));
			parameters.add(forceReload(con, filteredList));
			SkyLogger.getMediatrixLogger().debug(logPrefix + " Found " + filteredList.size() + " TextObjects for subproject " + subprojectId);
		} catch (Exception e) {
			parameters.add("Problem during loading TextTemplates" + e.getMessage());
			SkyLogger.getMediatrixLogger().error(logPrefix + "Problem during loading TextTemplates for: s:" + subprojectId + " l:" + loginname + " t:" + typ + " c:" + country + " p:" + preafixes, e);
		}
		SkyLogger.getMediatrixLogger().debug(logPrefix + " LoadTextObjects finished ");
		return parameters;
	}

	public List<ITextObject> selectTemplates(Connection con, String preafixes, String subprojectId, String login, String typ, String countryString) throws SQLException {
		List<ITextObject> rawlist = API.getServerAPI().getTextObjectAPI().getTextObjects(con, Integer.parseInt(subprojectId));
		List<ITextObject> filteredList = new LinkedList<>();
		long currenttime = System.currentTimeMillis();
		for (ITextObject itemplate : rawlist) {
			for (String preafix : preafixes.split(",")) {
				if (itemplate.isEmailText() && currenttime >= itemplate.getValidFrom() && currenttime <= itemplate.getValidTo() && itemplate.getShortDescription().contains(preafix)) {
					String templatename = itemplate.getShortDescription().trim();
					//mit login
					if (notEmpty(login)) {
						if (notEmpty(subprojectId) && notEmpty(typ) && notEmpty(countryString) && templatename.equalsIgnoreCase(preafix + "_" + subprojectId + "_" + login + "_" + typ + "_" + countryString)) {
							filteredList.add(itemplate);
						} else if (notEmpty(subprojectId) && notEmpty(typ) && templatename.equalsIgnoreCase(preafix + "_" + subprojectId + "_" + login + "_" + typ)) {
							filteredList.add(itemplate);
						} else if (notEmpty(subprojectId) && notEmpty(countryString) && templatename.equalsIgnoreCase(preafix + "_" + subprojectId + "_" + login + "_" + countryString)) {
							filteredList.add(itemplate);
						} else if (notEmpty(subprojectId) && templatename.equalsIgnoreCase(preafix + "_" + subprojectId + "_" + login)) {
							filteredList.add(itemplate);
						}
						if (notEmpty(typ) && notEmpty(countryString) && templatename.equalsIgnoreCase(preafix + "_" + login + "_" + typ + "_" + countryString)) {
							filteredList.add(itemplate);
						} else if (notEmpty(typ) && templatename.equalsIgnoreCase(preafix + "_" + login + "_" + typ)) {
							filteredList.add(itemplate);
						} else if (notEmpty(countryString) && templatename.equalsIgnoreCase(preafix + "_" + login + "_" + countryString)) {
							filteredList.add(itemplate);
						} else if (templatename.equalsIgnoreCase(preafix + "_" + login)) {
							filteredList.add(itemplate);
						}
					}
					if (notEmpty(subprojectId) && notEmpty(typ) && notEmpty(countryString) && templatename.equalsIgnoreCase(preafix + "_" + subprojectId + "_" + typ + "_" + countryString)) {
						filteredList.add(itemplate);
					} else if (notEmpty(subprojectId) && notEmpty(typ) && templatename.equalsIgnoreCase(preafix + "_" + subprojectId + "_" + typ)) {
						filteredList.add(itemplate);
					} else if (notEmpty(subprojectId) && notEmpty(countryString) && templatename.equalsIgnoreCase(preafix + "_" + subprojectId + "_" + countryString)) {
						filteredList.add(itemplate);
					} else if (notEmpty(subprojectId) && templatename.equalsIgnoreCase(preafix + "_" + subprojectId)) {
						filteredList.add(itemplate);
					}

					if (notEmpty(typ) && notEmpty(countryString) && templatename.equalsIgnoreCase(preafix + "_" + typ + "_" + countryString)) {
						filteredList.add(itemplate);
					} else if (notEmpty(typ) && templatename.equalsIgnoreCase(preafix + "_" + typ)) {
						filteredList.add(itemplate);
					} else if (notEmpty(countryString) && templatename.equalsIgnoreCase(preafix + "_" + countryString)) {
						filteredList.add(itemplate);
					} else if (templatename.equalsIgnoreCase(preafix)) {
						filteredList.add(itemplate);
					}

				}
			}
		}
		return filteredList;
	}

	// Vorerst nur zur Debugzwecken, Bekannte Probleme mit Attachments der Textbausteine
	private List<ITextObject> forceReload(Connection con, List<ITextObject> input) throws SQLException {
		List<ITextObject> filteredList = new LinkedList<>();
		for (ITextObject toi : input) {
			//TextObject too=API.getServerAPI().getTextObjectAPI().load(con, toi.getId());
			if (toi != null) {
				filteredList.add(toi);
				SkyLogger.getMediatrixLogger().debug("Loaded Template: " + toi.getId());
				if (toi.getDocuments() != null && !toi.getDocuments().isEmpty()) {
					SkyLogger.getMediatrixLogger().debug(" Template: " + toi.getId() + " contains " + toi.getDocuments().size() + " att");
				}

			} else {
				SkyLogger.getMediatrixLogger().debug("Problem loading textemplate (empty): " + (toi != null ? toi.getId() : 0));
			}
		}
		return filteredList;
	}

	private boolean notEmpty(String s) {
		return (s != null && !s.trim().isEmpty() && !s.equals("0"));
	}
}
