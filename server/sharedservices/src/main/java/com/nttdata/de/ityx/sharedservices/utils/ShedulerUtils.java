package com.nttdata.de.ityx.sharedservices.utils;

import com.nttdata.de.ityx.sharedservices.configuration.BeanConfig;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.DocIdGenerator;
import de.ityx.base.Global;
import de.ityx.base.UnitDirectory;
import de.ityx.base.UnitInfo;
import de.ityx.contex.dao.docpool.DocumentPoolParameter;
import de.ityx.contex.dbo.designer.Designer_documentpool;
import de.ityx.contex.dbo.designer.STATUS;
import de.ityx.contex.execution.ExecutionContextHolder;
import de.ityx.contex.execution.ctx.Tenant;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.document.CDocument;
import de.ityx.contex.interfaces.document.service.DocumentPoolService;
import de.ityx.contex.security.system.SystemLoginToken;
import de.ityx.contex.security.util.ContexSecurityTool;
import de.ityx.contex.service.ContexServiceLocator;
import de.ityx.license.client.License;
import de.ityx.license.client.LicenseHolder;
import de.ityx.license.client.LicenseTrackerClient;
import de.ityx.licensing.api.LicenseInformations;
import de.ityx.licensing.api.LicenseToken;
import de.ityx.licensing.api.LicenseTrackerService;
import de.ityx.licensing.entities.RuntimeLicense;
import de.ityx.licensing.exception.LicensingException;
import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.api.client.ICQuestion;
import de.ityx.scheduler.processorder.api.ProcessOrder;
import de.ityx.scheduler.processorder.api.ProcessOrderService;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * All Calls of this class must be putted into try/finally block:
 * ContexSecurityTool.authenticateAsSystemUser();
 * <p/>
 * try {
 * ShedulerUtils.scheduleProcess(....
 * } finally {
 * ContexSecurityTool.clearAuthentication();
 * }
 */
public class ShedulerUtils {

	private static final String licTockenKey = BeanConfig.getString("LicenseToken", "ProdlizenzA");

	private static void scheduleProcess(String unit, String master, String processname, long documentId, Map<String, Object> parammap) throws Error {
		checkAuth();

		ProcessOrder order = new ProcessOrder();
		order.setContainerId(documentId);
		order.setUnit(unit);
		order.setMaster(master);
		order.setProcessName(processname);
		order.setParamterMap(parammap);
		order.setLicenseToken(licTockenKey);
		getProcessOrderService().createProcessOrder(order);
	}

	private static void storeDocAndScheduleProcess(String unit, String master, String processname, String parameter, CDocumentContainer cont, CDocument doc) throws Exception {
		checkAuth();
		DocumentPoolService dps = getDocumentPoolService();
		//CDocumentContainer<CDocument> container, int prio, int type, long masterID, String parameter, long serviceLevel, long delay, long maxCollectionWaitTime, boolean complete
		long sdoc = dps.storeDocumentContainer(cont, getPrioId(master, parameter), 0, getMasterId(master), parameter, 0, 2000, -1, true);
		Map<String, Object> parammap = new LinkedHashMap<>();
		//parammap.put("contactid",1);
		if (processname == null) {
			processname = getProcessForParameter(parameter);
		}
		scheduleProcess(unit, master, processname, sdoc, parammap);

	}

	public static void storeDocAndScheduleProcess(String unit, String master, String parameter, CDocumentContainer cont, CDocument doc) throws Exception {
		storeDocAndScheduleProcess(unit, master, getProcessForParameter(parameter), parameter, cont, doc);
	}

	public static final String SQL_GETPROCESSFORPARAMETER = "select processname from cxdsg_processdata where triggerparameter = ? order by id";

	private static String getProcessForParameter(String parameter) throws SQLException {
		String process = null;
		PreparedStatement idStmt = null;
		ResultSet rs = null;
		Connection con = null;
		try {
			con = ContexDbConnector.getAutoCommitConnection();
			idStmt = con.prepareStatement(SQL_GETPROCESSFORPARAMETER);
			idStmt.setString(1, parameter);
			rs = idStmt.executeQuery();
			if (rs.next()) {
				process = rs.getString(1);
			}
		} catch (SQLException e) {
			String logPrefix = DocIdGenerator.class.getName() + "#" + new Object() {
			}.getClass().getEnclosingMethod().getName();
			SkyLogger.getCommonLogger().error(logPrefix + " SHU GetProcessname for parameter:" + parameter + " failed" + e.getMessage(), e);
			throw e;

		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException e) {
				String logPrefix = DocIdGenerator.class.getName() + "#" + new Object() {
				}.getClass().getEnclosingMethod().getName();
				SkyLogger.getCommonLogger().error(logPrefix + " SHU Problem closing RS: GetProcessname for parameter:" + parameter + " failed" + e.getMessage(), e);
			}
			try {
				if (idStmt != null) {
					idStmt.close();
				}
			} catch (SQLException e) {
				String logPrefix = DocIdGenerator.class.getName() + "#" + new Object() {
				}.getClass().getEnclosingMethod().getName();
				SkyLogger.getCommonLogger().error(logPrefix + " SHU Problem closing statement: GetProcessname for parameter:" + parameter + " failed" + e.getMessage(), e);
			} finally {
				if (con != null) {
					ContexDbConnector.releaseConnection(con);
				}
			}
		}
		return process;
	}

	public static synchronized void quickCheckLicence(String msg) {
		if (!BeanConfig.getBoolean("LicenceRecovery", false)) {
			SkyLogger.getCommonLogger().info("SHU  LH-QuickCheck:DISABLED");
			return;
		}
		LicenseHolder lh = LicenseHolder.getInstance();
		try {
			String t = lh.getSelectedToken();
			SkyLogger.getCommonLogger().info("SHU  LH-QuickCheckOK:" + t + " msg:" + msg);
		} catch (Exception e) {
			SkyLogger.getCommonLogger().warn("SHU  LH-QuickCheck: NOK:restart: msg:" + msg + ": " + e.getMessage());
			try {
				for (String tok : LicenseHolder.getInstance().getAvailableTokens()) {
				  SkyLogger.getCommonLogger().warn("SHU  LH-QuickCheck: Token Found: " + tok);
				  LicenseToken licenseToken = LicenseHolder.getInstance().authenticateLicenseToken(new LicenseToken(tok));
			      ExecutionContextHolder.get().set("de.ityx.license.token", licenseToken);
				  //ExecutionContextHolder.get().set("de.ityx.license.token", new LicenseToken(tok));
			    }
				SkyLogger.getCommonLogger().warn("SHU  LH-QuickCheck: finished msg:" + msg + ": " + LicenseHolder.getInstance().getSelectedToken());
			} catch (Exception ee) {
				SkyLogger.getCommonLogger().warn("SHU  LH-QuickCheck: NOKK:something goes wrong:" + msg + ": " + ee.getMessage());
				if (BeanConfig.getBoolean("LicenceRecovery", false)) {
					SkyLogger.getCommonLogger().warn("SHU  LH-QuickCheck: NOKK: LICRELOAD ENABLED:INIT:" + msg + ": " + ee.getMessage());
					//LicenseTrackerClient.getClient().reload();

					final String cxUnitName = Global.DEFAULT_MANDANT_NAME;

					final UnitInfo unitInfo = UnitDirectory.getInstance().getUnitInfo();
					final String cxProjectName = unitInfo.getCxProjectName();

					final Tenant tenant = new Tenant();
					tenant.setUnit(cxUnitName);
					if (StringUtils.isNotBlank(cxProjectName)) {
						tenant.setMaster(cxProjectName);
					}

					final Tenant tenant2 = ExecutionContextHolder.get().getTenant();
					if (tenant2 == null || !StringUtils.equals(cxUnitName, tenant2.getUnit())) {
						ExecutionContextHolder.get().setTenant(tenant);
						License.initialize();
						SkyLogger.getCommonLogger().warn("SHU License initialized");
					}
					SkyLogger.getCommonLogger().warn("SHU SystemAuthentication checked");
					SkyLogger.getCommonLogger().warn("SHU  LH-QuickCheck: NOKK: LICRELOAD ENABLED:DONE:" + msg + ": " + ee.getMessage());
				} else {
					SkyLogger.getCommonLogger().warn("SHU  LH-QuickCheck: NOKK: LICRELOAD DISABLED:" + msg + ": " + ee.getMessage());
				}
				try {
					//checkRuntimeLicense("sub:" + msg);
					SkyLogger.getCommonLogger().warn("SHU  LH-QuickCheck kaputt:" + msg + ": ");
				} catch (Exception eee) {
					SkyLogger.getCommonLogger().warn("SHU  LH-QuickCheck: NOKKK:something goes wrong:" + msg + ": " + eee.getMessage(), e);
				}
			}
		}
	}

public static void checkRuntimeLicense(String log) {
	SkyLogger.getCommonLogger().warn("SHU  Calling QuickCheck instead of checkRuntime.");
	quickCheckLicence(log);
}

	// Inhaltlich kein Unterschied, wir nehmen einfach die QuickCheckLicense Methode für beide Zwecke

/* 	public static void checkRuntimeLicense(String log) {
		boolean license_initalized = true;

		if (token == null || !token.isAuthenticated()) {
			token = ContexSecurityTool.getSystemAuthentication();
		}

		LicenseHolder lh = LicenseHolder.getInstance();
		try {
			String t = lh.getSelectedToken();
			SkyLogger.getCommonLogger().info("SHU  LH-CheckLicenseOK:" + t + " msg:" + log);
		} catch (Exception eee) {
			try {
			for (String tok : LicenseHolder.getInstance().getAvailableTokens()) {
				  SkyLogger.getCommonLogger().warn("SHU  LH-CheckLicense to ExecutionContextHolder: Token Found: " + tok);
				  LicenseToken licenseToken = LicenseHolder.getInstance().authenticateLicenseToken(new LicenseToken(tok));
			      ExecutionContextHolder.get().set("de.ityx.license.token", licenseToken);
				  //ExecutionContextHolder.get().set("de.ityx.license.token", new LicenseToken(tok));
}
			}
			if (!BeanConfig.getBoolean("LicenceRecovery", false)) {
				SkyLogger.getCommonLogger().warn("SHU  LH-RCheck: NOKK: LICRELOAD DISABLED:" + log + " ee:" + eee.getMessage(), eee);
			} else {
				SkyLogger.getCommonLogger().info(log + "  L-TrackerCheck");
				synchronized (licTockenKey) {
					LicenseTrackerService licenseTracker = LicenseTrackerClient.getClient();

					if (licenseTracker.getRuntimeLicense(BeanConfig.getString("RLicenseToken", "ProdlizenzA")) == null) {
						SkyLogger.getCommonLogger().warn(log + "SHU  L-TrackerNOK: test:" + (licenseTracker.getRuntimeLicense(BeanConfig.getString("RLicenseToken", "ProdlizenzA")) != null) + " l:" + BeanConfig.getString("LicenseToken", "ProdlizenzA"));
						license_initalized = false;
					}

					if (lh.getRuntime(BeanConfig.getString("RLicenseToken", "ProdlizenzA")) == null) {
						SkyLogger.getCommonLogger().warn(log + "SHU  LH-TrackerNOK:test:" + (licenseTracker.getRuntimeLicense(BeanConfig.getString("RLicenseToken", "ProdlizenzA")) != null) + " l:" + BeanConfig.getString("LicenseToken", "ProdlizenzA"));
						license_initalized = false;
					}

					if (license_initalized == false) {
						try {
							if (token == null) {
								token = ContexSecurityTool.getSystemAuthentication();
							}
							if (BeanConfig.getBoolean("LicenceRecovery", false)) {
								SkyLogger.getCommonLogger().warn("SHU  LH-RCheck: NOKK: LICRELOAD ENABLED:INIT:" + log);
								//	licenseTracker.reload();
								//	lh.setTracker(licenseTracker);

								final String unitname = "DEFAULT"; //Default Wert = DEFAULT
								final String cxUnit = unitname;
								final String cxUnitName = Global.DEFAULT_MANDANT_NAME;
								final UnitInfo unitInfo = UnitDirectory.getInstance().getUnitInfo();
								final String cxProjectName = unitInfo.getCxProjectName();
								final Tenant tenant = new Tenant();
								tenant.setUnit(cxUnitName);
								if (StringUtils.isNotBlank(cxProjectName)) {
									tenant.setMaster(cxProjectName);
								}

								final Tenant tenant2 = ExecutionContextHolder.get().getTenant();
								if (tenant2 == null || !StringUtils.equals(cxUnitName, tenant2.getUnit())) {
									ExecutionContextHolder.get().setTenant(tenant);
									License.initialize();
									SkyLogger.getCommonLogger().warn("SHU License initialized");
								}

								SkyLogger.getCommonLogger().warn("SHU SystemAuthentication checked");

								SkyLogger.getCommonLogger().warn(" SHU LH-RCheck: NOKK: LICRELOAD ENABLED:DONE:" + log);
							} else {
								SkyLogger.getCommonLogger().warn(" SHU LH-RCheck: NOKK: LICRELOAD DISABLED:" + log);
							}
						} catch (Exception e) {
							SkyLogger.getCommonLogger().error(log + " SHU cannotReboot LicenseTracker:" + e.getMessage(), e);
						}
					}
					SkyLogger.getCommonLogger().info(log + " SHU Tokens:" + StringUtils.join(licenseTracker.getAvailableToken(), ":"));
				}
			}
		}
	} */

	private static SystemLoginToken token = null;

	public static synchronized void resetAuth(String docid) {

		if (!BeanConfig.getBoolean("LicenceRecovery", false)) {
			SkyLogger.getCommonLogger().warn("SHU  LH-RCheck: NOKK: LICRELOAD DISABLED:" + docid);
			return;
		}
		if (token == null || !token.isAuthenticated()) {
			token = ContexSecurityTool.getSystemAuthentication();
		}

		synchronized (licTockenKey) {
			try {
				if (BeanConfig.getBoolean("LicenceRecovery", false)) {
					// nicht thread-sicher in der 2.4 -> führt zur weiteren Problemen
					//ContexSecurityTool.clearAuthentication();
					SkyLogger.getCommonLogger().warn("SHU  LH-RCheck: NOKK: LICRELOAD ENABLED:INIT:" + docid);
					//LicenseTrackerClient.getClient().reload();

					final String cxUnitName = Global.DEFAULT_MANDANT_NAME;
					final UnitInfo unitInfo = UnitDirectory.getInstance().getUnitInfo();
					final String cxProjectName = unitInfo.getCxProjectName();

					final Tenant tenant = new Tenant();
					tenant.setUnit(cxUnitName);
					if (StringUtils.isNotBlank(cxProjectName)) {
						tenant.setMaster(cxProjectName);
					}

					final Tenant tenant2 = ExecutionContextHolder.get().getTenant();
					if (tenant2 == null || !StringUtils.equals(cxUnitName, tenant2.getUnit())) {
						ExecutionContextHolder.get().setTenant(tenant);
						License.initialize();
						SkyLogger.getCommonLogger().warn("SHU License initialized");
					}

					SkyLogger.getCommonLogger().warn("SHU SystemAuthentication checked");


					SkyLogger.getCommonLogger().warn("SHU  LH-RCheck: NOKK: LICRELOAD ENABLED:DONE:" + docid);
					//token = ContexSecurityTool.getSystemAuthentication();
					//ContexSecurityTool.authenticate(token);
					ShedulerUtils.checkRuntimeLicense("SHU ResetAuth:" + docid);

				} else {
					SkyLogger.getCommonLogger().warn("SHU LH-RCheck: NOKK: LICRELOAD DISABLED:" + docid);
				}
			} catch (Exception e) {
				SkyLogger.getCommonLogger().warn("SHU problems during LTC-Reboot:" + docid + " msg:" + e.getMessage(), e);
			}

			LicenseInformations cxLic = API.getServerAPI().getLicenceAPI().getContexLicenceInformation();
			String mxLic = de.ityx.mediatrix.api.API.getServerAPI().getLicenceAPI().getLicenseeInformation();
			if (cxLic != null) {
				String cxLicT = cxLic.getToken();
				SkyLogger.getCommonLogger().warn("SHU ResetAuth:finished:" + docid + " mx:" + mxLic + " cx:Lic:" + cxLicT);
			} else {
				SkyLogger.getCommonLogger().warn("SHU ResetAuth:finished:" + docid + " mx:" + mxLic + " cx:Lic:no CTX-LicToken found.");
			}

			LicenseTrackerService licenseTracker = LicenseTrackerClient.getClient();
			LicenseHolder lh = LicenseHolder.getInstance();

			//Map<String, RuntimeLicense> rlic=new LinkedHashMap<>();
			for (String ttoken : licenseTracker.getAvailableToken()) {
				//rlic.put(token,runtime);
				try {
					RuntimeLicense runtime = licenseTracker.getRuntimeLicense(ttoken);
					runtime.getLicenseType();
					lh.checkAvailibilityOfToken(ttoken);
				} catch (LicensingException le) {
					SkyLogger.getCommonLogger().warn("SHU ResetAuth: LToken:" + " not available in LicenceHolder! " + le.getMessage());
				}
			}


		}
	}

	public static void checkAuth() {
		if (token == null) {
			token = ContexSecurityTool.getSystemAuthentication();
		}
		synchronized (licTockenKey) {
			if (token != null && ContexSecurityTool.getCurrentAuthentication() == null) {
				ContexSecurityTool.authenticate(token);
			} else if (ContexSecurityTool.getCurrentAuthentication() != null) {
				SkyLogger.getCommonLogger().debug("SystemAuthentication ok");
			} else {
				SkyLogger.getCommonLogger().warn("Problem to get SystemAuthentication");
			}
		}
	}

	public static void wakeupDocumentAndSheduleWithNote(String unit, String master, String parameter, Designer_documentpool doc, Map<String, String> notes) throws Exception {
		checkAuth();
		String process = getProcessForParameter(parameter);
		Map<String, Object> parammap = new LinkedHashMap<>();
		DocumentPoolService dps = getDocumentPoolService();
		CDocumentContainer dc = dps.loadDocumentContainer(doc.getId());
		String comment = "";
		for (Map.Entry<String, String> note : notes.entrySet()) {
			parammap.put(note.getKey(), note.getValue());
			dc.setNote(note.getKey(), note.getValue());
			dc.getDocument(0).setNote(note.getKey(), note.getValue());
			comment += note.getKey() + ":" + note.getValue() + "; ";
		}
		dps.setComment(doc.getId(), comment);
		long sdoc = dps.storeDocumentContainer(dc, getPrioId(doc.getMasterID().toString(), parameter), doc.getDocPoolType(), doc.getMasterID(), parameter, doc.getServiceLevel(), 5, -1, true);
		/*try {
			dps.unlockDocumentContainer(sdoc);
		} catch (RemoteException e) {
			SkyLogger.getConnectorLogger().info("ShedulerUtils: not able to unlock document: " + (doc.getId()));
		}*/
		scheduleProcess(unit, master, process, sdoc, parammap);
		SkyLogger.getConnectorLogger().debug("IF_ContexWS SCHEDULED  externalID: " + dc.getExternalID() + "; parameter: " + parameter +
				"; process: " + process + "; unit: " + unit + "; master:" + master + "; parameter: " + getProcessForParameter(parameter) +
				"; docType: " + getChannelExt(doc.getDocPoolType()) + "; doc.id:" + doc.getId() + ";  masterId: " + doc.getMasterID() +
				"; doc.prio: "+doc.getPrio()+"; doc.status: " + doc.getStatus() + "; comment: " + comment);
		dps.setStatus(doc.getId(), STATUS.DELETED.value);
	}


	public static List<Designer_documentpool> getDocsByParameter(String master, String cdoc_docpool_parameter, String cdoc_extid) throws Exception {
		checkAuth();
		DocumentPoolParameter param_WAIT = new DocumentPoolParameter();
		param_WAIT.setStatus("WAIT");
		if (cdoc_docpool_parameter != null && !cdoc_docpool_parameter.isEmpty()) {
			param_WAIT.setParameter(cdoc_docpool_parameter);
		}
		if (cdoc_extid != null && !cdoc_extid.isEmpty()) {
			param_WAIT.setExternalId(cdoc_extid);
		}
		List<Designer_documentpool> ret = new ArrayList<>();
		ret.addAll(getDocsByParameter(master, param_WAIT));
		return ret;
	}

	public static List<Designer_documentpool> getDocsByParameter(String master, DocumentPoolParameter param) throws Exception {
		checkAuth();
		DocumentPoolService dps = getDocumentPoolService();
		return dps.getDocsByParameter(getMasterId(master), param);

	}

	/* Ausführung immer aus einem authentifizierten Block
	* */
	private static ProcessOrderService getProcessOrderService() {
		return ContexServiceLocator.getServiceBean(ProcessOrderService.class);
	}

	/* Ausführung immer aus einem authentifizierten Block
	* */
	private static DocumentPoolService getDocumentPoolService() {
		return ContexServiceLocator.getServiceBean(DocumentPoolService.class);
	}

	public static int getMasterId(String master) throws Exception {
		//gibt es ein Serivce wo mann es abfragen kann?
		int masterid = (master != null && ("sky".equals(master) || "1".equals(master))) ? 1 : 0;
		masterid = (masterid == 0 && ("sbs".equals(master)) || "2".equals(master)) ? 2 : masterid;
		if (masterid < 1) {
			throw new Exception("Unknown Master:" + master);
		}
		return masterid;
	}

	public static String getDefaultMaster() {
		return BeanConfig.getString("master", "sky");
	}

	public static String getDefaultUnit() {
		return BeanConfig.getString("unit", "DEFAULT");
	}

	private static String getChannelExt(int doctype) {
		if (doctype == 2) {
			return "EMAIL";
		} else {
			return "DOCUMENT";
		}
	}

	public static int getPrioId(String master, String parameter) throws Exception {
		int prioId = 3;
		if (master != null) {
			prioId = (("sky".equalsIgnoreCase(master) || "1".equals(master)) && parameter != null && parameter.startsWith("600_MXI")) ? 2 : prioId;
			prioId = ("sbs".equalsIgnoreCase(master) || "2".equals(master)) ? 1 : prioId;
		}
		SkyLogger.getCommonLogger().debug("getPrioId -> master: "+master+"; parameter: "+parameter+"; prioId: "+prioId);
		return prioId;
	}

	public static boolean isExistsDocsByParameter(String master, String cdoc_docpool_parameter, String cdoc_extid, long incommingDate){
        boolean result =false;
		checkAuth();
		DocumentPoolParameter param = new DocumentPoolParameter();
		param.setParameter(cdoc_docpool_parameter);
		param.setExternalId(cdoc_extid);

		param.setCreatetimeStart(incommingDate);
        try{
            List<Designer_documentpool>  docs = getDocsByParameter(master, param);
            for(Designer_documentpool doc: docs){
				SkyLogger.getCommonLogger().debug(" doc.id: "+doc.getId() + "; doc.status: "+doc.getStatus());
                if (!result){
                    switch ((int)doc.getStatus()) {
                        //case 0: case 1:
                         case 32: result = true;
                    }
                }
            }
            SkyLogger.getCommonLogger().debug(" master: "+master+"; param: "+cdoc_docpool_parameter+
                    "; docId: "+cdoc_extid+"; incommingDate: "+incommingDate+"; result: "+result);
        } catch (Exception e) {
            SkyLogger.getCommonLogger().error(" master:"+master+"; param: "+cdoc_docpool_parameter+
                    "; docId: "+cdoc_extid+"; incommingDate: "+incommingDate+"; errMsg: "+e.getMessage(),e);
        }
		return result;
	}

}
