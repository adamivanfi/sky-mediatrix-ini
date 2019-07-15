/**
 * 
 */
package com.nttdata.de.ityx.utils;

import com.nttdata.de.lib.logging.SkyLogger;
import de.ityx.base.dbpooling.DBConnectionPoolFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author DHIFLM
 *
 */
public class CancellationUtils {

	private static CancellationUtils instance ;
	private Map<String,String> reasonCodeMap = new HashMap<>();
	private Map<String,String> mapReasonDetailMap = new HashMap<>();

	private static synchronized CancellationUtils getInstance() {
		if (instance==null) {
			instance = new CancellationUtils();
			Connection con  = DBConnectionPoolFactory.getPool().getCon();
			try {
				PreparedStatement stmt = con.prepareStatement("select reason,code,reasondetail from reasons where action='cancel' order by id");
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					instance.reasonCodeMap.put(rs.getString("reason").toUpperCase(),rs.getString("code"));
					instance.mapReasonDetailMap.put(rs.getString("reason").toUpperCase(),rs.getString("reasondetail"));

					SkyLogger.getMediatrixLogger().info("loadReasons:>"+rs.getString("reason").toUpperCase()+"< >"+rs.getString("code")+"<");

				}
			} catch (SQLException e) {
				SkyLogger.getMediatrixLogger().warn(e.getMessage(), e);
			}
			finally {
				DBConnectionPoolFactory.getPool().releaseCon(con);
			}
		}
		return instance;
	}
	
	public static String mapReasonCode(String reason) {
		if (reason==null||reason.isEmpty()) {
			SkyLogger.getMediatrixLogger().warn("mapReasonCode:NULL");
			return null;
		}
		String reasonCode = getInstance().reasonCodeMap.get(reason.toUpperCase());
		if (reasonCode==null||reasonCode.isEmpty()){
			if (reason.endsWith("_K")){
				reasonCode=reason;
			}else{
				SkyLogger.getMediatrixLogger().warn("mapReasonCode:NotFoundFor:"+reason);
			}
		}else{
			SkyLogger.getMediatrixLogger().debug("mapReasonCode:FoundCode:>"+reasonCode+"< for:>"+reason+"<");
		}
		return reasonCode;
	}

	public static String mapReasonDetail(String reason) {
		if (reason==null||reason.isEmpty()) {
			return null;
		}
		String reasonDetail = getInstance().mapReasonDetailMap.get(reason.toUpperCase());
		if (reasonDetail==null||reasonDetail.isEmpty()){
			if (reason.endsWith("_K")){
				reasonDetail=reason;
			}
		}
		return reasonDetail;
	}
}
