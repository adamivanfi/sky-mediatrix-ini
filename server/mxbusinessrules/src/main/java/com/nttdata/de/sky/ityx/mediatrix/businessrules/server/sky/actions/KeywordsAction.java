package com.nttdata.de.sky.ityx.mediatrix.businessrules.server.sky.actions;

import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.Actions;
import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.data.Keyword;

import java.sql.Connection;
import java.util.List;

/**
 * Created by meinusch on
 */
public class KeywordsAction extends AServerEventAction {

	@Override
	public List actionPerformed(Connection con, String action, List parameters) throws Exception {
		// Logging.
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName();
		SkyLogger.getMediatrixLogger().debug(logPrefix + " Starting : " + action);

		Integer questionId = (Integer) parameters.get(0);
		parameters.clear();

		try {
			List<Keyword> keywords = API.getServerAPI().getQuestionAPI().loadKeywords(con, questionId);
			parameters.add(keywords);
			SkyLogger.getMediatrixLogger().debug(logPrefix + " Found " + keywords.size() + " keywords for question " + questionId);
		} catch (Exception e) {
			SkyLogger.getMediatrixLogger().error(logPrefix + " " + e.getMessage(), e);
		}
		SkyLogger.getMediatrixLogger().debug(logPrefix + ": exit");
		return parameters;
	}

	@Override
	public String[] getActionNames() {
		return new String[]{Actions.ACTION_LOAD_KEYWORD.name()};
	}


}
