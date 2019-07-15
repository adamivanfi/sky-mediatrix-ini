package com.nttdata.de.sky.ityx.mediatrix.businessrules.server.sky.actions;

import com.nttdata.de.ityx.sharedservices.utils.ShedulerUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.Actions;
import de.ityx.contex.data.match.Request;
import de.ityx.contex.data.match.Response;
import de.ityx.contex.interfaces.match.IFuzzyMatcherEngine;
import de.ityx.contex.service.ContexServiceLocator;
import org.elasticsearch.ElasticsearchException;

import java.sql.Connection;
import java.util.List;

/**
 * Created by meinusch on 12.04.15.
 */
public class FuzzySearchAction extends AServerEventAction {

	protected String fuzzy_namespace = System.getProperty("contex.fuzzy.namespace", "fuzzy_newdb");
	protected String masterName = "sky";
	protected Long fuzzy_master_id = 1L;

	@Override
	public String[] getActionNames() {
		return new String[]{Actions.ACTION_FUZZY_SEARCH.name()};
	}

	@Override
	public List actionPerformed(Connection con, String actionname, List parameters) throws Exception {
		String logPrefix = getClass().getName() + "#" + new Object() {}.getClass().getEnclosingMethod().getName() + " ";
		if (!isResponsibleFor(actionname)) {
			SkyLogger.getMediatrixLogger().error(getClass().getName() + " is not reposible to serve: " + actionname);
			throw new Exception(getClass().getName() + " is not reposible to serve: " + actionname);
		}
		SkyLogger.getMediatrixLogger().debug(logPrefix + ": enter");
		Request request = (Request) parameters.get(0);
		request.setMaster(fuzzy_master_id);
		request.setNamespace(fuzzy_namespace);
		SkyLogger.getMediatrixLogger().debug(logPrefix + "Starting fuzzySearch: " + request);
		parameters.clear();
		Response search=fuzzySearch(request);
		parameters.add(search);
		SkyLogger.getMediatrixLogger().debug(logPrefix + "Finished fuzzySearch: " + request);
		return parameters;
	}

	private IFuzzyMatcherEngine fuzzyinst=null;


	// ToDo eine implementierung mit Thread-Sicheren fuzzy-Pool wäre hier besser
    // Risiko dass die search-Aktion nicht thread-sicher ist
	private Response fuzzySearch(Request request) throws Exception {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + " ";
		Response search;
		long acurrentTimeMillis=System.currentTimeMillis();
		try {
			search = getFuzzy(false).search(request.getMaster(), request.getNamespace(), request);
		}catch(ElasticsearchException e){
			SkyLogger.getMediatrixLogger().error(logPrefix + "fuzzyEngine Blocked?  Please Reload FuzzyIndex!!! " + request + " msg:" + e.getMessage(), e);
			search = getFuzzy(true).search(request.getMaster(), request.getNamespace(), request);
		}catch (Exception e){
			try {
				SkyLogger.getMediatrixLogger().info(logPrefix + "Reinit1 fuzzyEngine: " + request);
				search = getFuzzy(true).search(request.getMaster(), request.getNamespace(), request);
			}catch (Exception ee){
				SkyLogger.getMediatrixLogger().warn(logPrefix + "Reinit2 fuzzyEngine: " + request);
				ShedulerUtils.resetAuth(logPrefix);
				search = getFuzzy(true).search(request.getMaster(), request.getNamespace(), request);
			}
		}
		long duration = System.currentTimeMillis() - acurrentTimeMillis;
		SkyLogger.getMediatrixLogger().info(logPrefix + "fuzzySearch took: " + duration);
		return search;
	}

	private IFuzzyMatcherEngine getFuzzy(boolean reinit) throws Exception {
		ShedulerUtils.checkAuth();
		if (reinit || fuzzyinst==null) {
			//fuzzyinst  = API.getServerAPI().getContexAPI().getFuzzyMatcher(masterName);
			fuzzyinst= ContexServiceLocator.getInstance().getService(IFuzzyMatcherEngine.class);
			fuzzyinst.initialize(fuzzy_master_id, fuzzy_namespace, 1, false);
		}
		return fuzzyinst;
 	}
}
