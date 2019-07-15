package com.nttdata.de.sky.ityx.mediatrix.businessrules.server.sky;

import com.nttdata.de.ityx.sharedservices.utils.ShedulerUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.mediatrix.businessrules.server.sky.actions.*;
import de.ityx.mediatrix.api.interfaces.businessrules.server.IServerEventPerformer;
import de.ityx.mediatrix.modules.tools.logger.Log;
import org.omg.CORBA.UserException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SkyServerEventPerformer implements IServerEventPerformer {

	private Map<String, IServerEventAction> eventactionperformers;
	private IServerEventAction[] actions = new IServerEventAction[]{new FuzzySearchAction(), new KeywordsAction(), new ReindexAction(), new ArchiveAction(), new CustomerLoadAction(), new DocIDGeneratorAction(), new MXResolveAction(), new ReasonsAction(), new TextobjectAction(), new QuickSRAction(), new SBSindexAction(), new UpdateLogAction()};

	public synchronized Map<String, IServerEventAction> getEventActionPerformers() {
		if (eventactionperformers == null) {
			ShedulerUtils.checkAuth();
			eventactionperformers = new TreeMap<>();
				for (IServerEventAction e : actions) {
					for (String actionname : e.getActionNames()) {
						eventactionperformers.put(actionname, e);
					}
				}
		}
		return eventactionperformers;
	}

	@Override
	public void actionPerformed(Connection con, String action, List list) {
		actionPerformedB(con, action, list);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public boolean actionPerformedB(Connection con, String actionname, List parameters) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName();
		SkyLogger.getMediatrixLogger().debug(logPrefix + " event: >" + actionname + "<");
		long acurrentTimeMillis=System.currentTimeMillis();
		Map<String, IServerEventAction> actions = getEventActionPerformers();
		IServerEventAction execaction = actions.get(actionname);
		if (execaction != null) {
			try {
				//ContexSecurityTool.authenticateAsSystemUser();
				execaction.actionPerformed(con, actionname, parameters);
				SkyLogger.getMediatrixLogger().info(logPrefix+" event: >" + actionname + "< took: " + (System.currentTimeMillis() - acurrentTimeMillis) );
				return true;

			} catch (Exception e) {
				markAsFailed(e, parameters);
				SkyLogger.getMediatrixLogger().error(logPrefix + " Error occured during executing event : >" + actionname + "< after: "+(System.currentTimeMillis() - acurrentTimeMillis)+ " msg:"+ e.getMessage(), e);
				return false;
			}
		}
		return false;
	}

	private void markAsFailed(Exception ex, List<Object> list) {
		list.clear();
		list.add(Boolean.FALSE);
		if (ex instanceof UserException) {
			list.add(ex.getMessage() != null ? ex.getMessage() : getExceptionString(ex));
		} else {
			Log.exception(ex);
			list.add(getExceptionString(ex));
		}
	}

	private String getExceptionString(Exception ex) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);
		pw.flush();
		return sw.toString().length() > 1024 ? sw.toString().substring(0, 1024) : sw.toString();
	}

	/**
	 * @return the masterName
	 */
	/*public String getMasterName() {
		return masterName;
	}*/

	/**
	 * @param masterName the masterName to set
	 */
	/*public void setMasterName(String masterName) {
		this.masterName = masterName;
	}*/
}
