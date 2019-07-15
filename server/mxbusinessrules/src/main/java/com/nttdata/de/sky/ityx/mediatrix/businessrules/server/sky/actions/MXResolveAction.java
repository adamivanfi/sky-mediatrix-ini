package com.nttdata.de.sky.ityx.mediatrix.businessrules.server.sky.actions;

import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.Actions;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import com.nttdata.de.sky.ityx.common.interfaces.IConnectorBridge;
import de.ityx.contex.interfaces.extag.TagMatch;
import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.api.IServerAPI;
import de.ityx.mediatrix.data.Question;

import java.sql.Connection;
import java.util.*;

/**
 * Created by meinusch on 13.04.15.
 */
public class MXResolveAction extends AServerEventAction {
	@Override
	public List actionPerformed(Connection con, String actionname, List parameters) throws Exception {
		// Logging.
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + " ";
		SkyLogger.getMediatrixLogger().debug(logPrefix + "Starting resolve: " + actionname);

		// Reads the parameters of the call.
		Integer questionId = (Integer) parameters.get(0);
		SkyLogger.getMediatrixLogger().debug(logPrefix + "Resolving question: " + questionId);
		parameters.clear();
		try {
			final IServerAPI serverAPI = API.getServerAPI();
			Question question = serverAPI.getQuestionAPI().load(con, questionId, true);

			String customerId = null;
			String contractNumber = null;
			Map<String, String> customerData = new HashMap<>();

			// Identifies the customer.
			TagMatch match = question.getTagMatches();
			if (match != null) {
				for (Iterator<TagMatch> chit = match.children(); chit.hasNext(); ) {
					TagMatch tm = chit.next();
					String tagName = tm.getIdentifier();
					String tagValue = tm.getTagValue();
					SkyLogger.getMediatrixLogger().debug(logPrefix + "Tag: <" + tagName + ", " + tagValue + ">");
					customerData.put(tagName, tagValue);
					switch (tagName) {
						case TagMatchDefinitions.CUSTOMER_ID:
							customerId = tagValue;
							break;
						case TagMatchDefinitions.X_TAGMATCH_CUSTOMER_ID:
							customerId = tagValue;
							break;
						case TagMatchDefinitions.TAGMATCH_CONTRACT_NUMBER:
							contractNumber = tagValue;
							break;
					}
				}
			}
			if (customerId == null) {
				customerId = TagMatchDefinitions.extractXTMHeader(question.getHeaders(), TagMatchDefinitions.X_TAGMATCH_CUSTOMER_ID);
			}
			SkyLogger.getMediatrixLogger().debug(logPrefix + " customerId "+customerId);
			if (customerId != null && !customerId.isEmpty() && !customerId.equals("0")) {
				SkyLogger.getMediatrixLogger().debug(logPrefix + "Resolving question <" + questionId + "> with customer <" + customerId + ">");

				// Fills the data.
				if(TagMatchDefinitions.isNotSbsProject(question)) {
					IConnectorBridge newDBproxy = (IConnectorBridge) Class.forName("com.nttdata.de.ityx.cx.sky.connector.newdb.NewDBProxy").newInstance();
					newDBproxy.fillCustomerData(customerId, contractNumber, customerData);
				}
				else {
					SkyLogger.getMediatrixLogger().debug(logPrefix + " SBS resolve");
					String headers = question.getHeaders();
					for(String header : TagMatchDefinitions.CUSTOMER_DATA) {
						String value = TagMatchDefinitions.extractXTMHeader(headers, header);
						SkyLogger.getMediatrixLogger().debug(logPrefix + "<"+header+","+value+">");
						if(value!=null) {
							customerData.put(mapTagToMx(header), value);
						}
					}
				}
			} else {
				SkyLogger.getMediatrixLogger().debug(logPrefix + "Can not resolve question: " + questionId);
			}

			// Returns the customer.
			parameters.add(customerData);

		} catch (Exception e) {
			SkyLogger.getMediatrixLogger().error(logPrefix + "Exception: " + e.getMessage(), e);
			parameters.add(new TreeMap<String, String>());
		}
		return parameters;
	}

	private String mapTagToMx(String header) {
		String ret;
		switch(header) {
			case TagMatchDefinitions.CUSTOMER_FIRST_NAME:
				ret = "FirstName";
				break;
			case TagMatchDefinitions.CUSTOMER_LAST_NAME:
				ret = "LastName";
				break;
			case TagMatchDefinitions.CUSTOMER_CITY:
				ret = "City";
				break;
			case TagMatchDefinitions.CUSTOMER_STREET:
				ret = "Street";
				break;
			case TagMatchDefinitions.CUSTOMER_ZIP_CODE:
				ret = "ZipCode";
				break;
			case TagMatchDefinitions.CUSTOMER_COUNTRY:
				ret = "Country";
				break;
			case TagMatchDefinitions.SBS_COMPANY:
				ret = "SbsCompany";
				break;
			default:
				ret = header;
		}
		return ret;
	}

	@Override
	public String[] getActionNames() {
		return new String[]{Actions.ACTION_MX_RESOLVE.name()};
	}
}
