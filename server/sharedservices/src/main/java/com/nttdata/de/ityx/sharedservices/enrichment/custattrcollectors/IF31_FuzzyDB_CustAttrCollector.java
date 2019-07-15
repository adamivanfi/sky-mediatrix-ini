package com.nttdata.de.ityx.sharedservices.enrichment.custattrcollectors;

import com.nttdata.de.ityx.sharedservices.utils.ContexDbConnector;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.base.dbpooling.DBConnectionPoolFactory;
import de.ityx.mediatrix.api.interfaces.IConnectionPool;
import de.ityx.mediatrix.modules.tools.logger.Log;
import org.apache.commons.lang3.StringUtils;

import java.sql.*;
import java.sql.Date;
import java.util.*;

public class IF31_FuzzyDB_CustAttrCollector extends AbstractCustomerAttributesCollector {

	//private Connection connection=null;
	
	public IF31_FuzzyDB_CustAttrCollector() {

	}
	
	@Override
	public Map<String, String> collectCustomerAttributes(String documentid, String customerid, Set<String> contractids, Set<String> serialNr, Set<String> mandates) throws Exception {
		Map<String, String> rs = null;
		if (!isEnabled()) {
			SkyLogger.getConnectorLogger().warn("IF31_NewDB_CustAttrCollector:DISABLED:" + documentid + " Query: CustomerNumer:" + customerid + ", ContractNumber:" + StringUtils.join(contractids, ",") + ", SerialNumber:" + StringUtils.join(serialNr, ","));
			return null;
		}

		Connection connection = null;
		try {
			connection = ContexDbConnector.getAutoCommitConnection();
			if (connection == null || connection.isClosed()) {
				SkyLogger.getConnectorLogger().warn("Not valid connection from Connectionpool");
				connection = ContexDbConnector.getAutoCommitConnection();
			}
			rs = enrichFromCustomerTable(connection, documentid, customerid);
			SkyLogger.getItyxLogger().debug("IF3.1: # enrichFromCustomerTable done.");
			rs.putAll(enrichFromContractTable(connection, documentid, customerid, contractids));
			SkyLogger.getItyxLogger().debug("IF3.1: # enrichFromContractTable done.");
			rs.putAll(enrichFromAssetTable(connection, documentid, customerid, contractids, serialNr));
			SkyLogger.getItyxLogger().debug("IF3.1: # enrichFromAssetTable done.");
			rs.putAll(enrichFromCampaignTable(connection, documentid, customerid));
			SkyLogger.getItyxLogger().debug("IF3.1: # enrichFromCampaignTable done.");
			putIfNotEmpty(rs, TagMatchDefinitions.CUSTOMER_CONTRACT_QUANTITY, getCustomerContractItems(connection, customerid)+"");
			SkyLogger.getItyxLogger().debug("IF3.1: " + documentid + " Query: CustomerNumer:" + customerid + "# Contracts = " + rs.get(TagMatchDefinitions.CUSTOMER_CONTRACT_QUANTITY));
			putIfNotEmpty(rs, TagMatchDefinitions.ACTIVE_CONTRACT_QUANTITY, getActiveContractItems(connection, customerid)+"");
			SkyLogger.getItyxLogger().debug("IF3.1: " + documentid + " Query: CustomerNumer:" + customerid + "# ActiveContracts = " + rs.get(TagMatchDefinitions.ACTIVE_CONTRACT_QUANTITY));
			String stageCust = getStageFromDb(customerid);
			putIfNotEmpty(rs, TagMatchDefinitions.META_STAGE, stageCust);
			SkyLogger.getItyxLogger().debug("IF3.1: " + documentid + " Query: CustomerNumer:" + customerid + "# Stage = " + stageCust);


		} catch (SQLException e) {
			SkyLogger.getItyxLogger().error("DB-Problem during Enrichment using IF3.1:" + e.getMessage(), e);
		} finally {
			if (connection != null) {
				ContexDbConnector.releaseConnection(connection);
			}
		}
		return rs;
	}

	private static final String SQL_GETCUSTOMERCONTRACTS = "select count(distinct CONTRACT_ID) customercontractitems from newdb_contract where customer_id = ? ";
	private static final String SQL_GETACTIVECONTRACTS = "select count(distinct CONTRACT_ID) customercontractitems from newdb_contract where customer_id = ? and Status in ('AKTIV','ZU AKTIVIEREN', 'UNTERZEICHNET', 'UNTERSCHRIEBEN', 'GESPERRT', 'INTERESSENT', 'IN DER SCHWEBE')";

	private int getCustomerContractItems(Connection con, String customerNumber) throws Exception {
		ResultSet rs = null;
		try {
			PreparedStatement ps = con.prepareStatement(SQL_GETCUSTOMERCONTRACTS);
			ps.setString(1, customerNumber);
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("customercontractitems");
			}
		} finally {
			if (rs != null) {
				rs.close();
			}
		}
		return 0;
	}
	private static String getStageFromDb(String customerid) {
		String stage ="A";
		IConnectionPool pool = DBConnectionPoolFactory.getPool();
		Connection con = pool.getCon();
		try {
			PreparedStatement selectSt = con.prepareStatement("SELECT coalesce(STAGE, 'A') STAGE FROM GDPR_CUSTOMER_STAGE_MX WHERE CUSTOMER_ID = " + customerid);

			ResultSet rs = selectSt.executeQuery();
			if (rs.next()) {
				stage = rs.getString("STAGE");
			}
			rs.close();
			selectSt.close();
		} catch (SQLException e) {
			Log.loginfo( "SQL Exception Stage CustomerId= " + customerid +"  " +  e.getMessage());
			Log.logerror(e);

			return "A";
		} finally {
			pool.releaseCon(con);
		}
		return stage;
	}
	private int getActiveContractItems(Connection con, String customerNumber) throws Exception {
		ResultSet rs = null;
		try {
			PreparedStatement ps = con.prepareStatement(SQL_GETACTIVECONTRACTS);
			ps.setString(1, customerNumber);
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("customercontractitems");
			}
		} finally {
			if (rs != null) {
				rs.close();
			}
		}
		return 0;
	}

	private static final String SQL_ENRICH_USING_CUSTOMERTABLE = "select ROW_ID, CUSTOMER_ID, MOBILE_NUMBER, EMAIL_ADDRESS,  FIRST_NAME, LAST_NAME, TELEPHONE_NUMBER, STATUS, OPERATION_DATE from newdb_customer where customer_id = ? ORDER BY OPERATION_DATE DESC";


	private Map<String, String> enrichFromCustomerTable(Connection con, String documentid, String customerNumber) throws Exception {
		Map<String, String> resultm = new TreeMap<>();
		ResultSet rs = null;
		try {
			PreparedStatement ps = con.prepareStatement(SQL_ENRICH_USING_CUSTOMERTABLE);
			ps.setString(1, customerNumber);
			rs = ps.executeQuery();
			if (rs.next()) {
				putIfNotEmpty(resultm, TagMatchDefinitions.CUSTOMER_ID, rs.getString("CUSTOMER_ID"));
				putIfNotEmpty(resultm, TagMatchDefinitions.CUSTOMER_FIRST_NAME, rs.getString("FIRST_NAME"));
				putIfNotEmpty(resultm, TagMatchDefinitions.CUSTOMER_LAST_NAME, rs.getString("LAST_NAME"));
				putIfNotEmpty(resultm, TagMatchDefinitions.SIEBEL_CUSTOMER_ID, rs.getString("ROW_ID"));
				putIfNotEmpty(resultm, TagMatchDefinitions.TAGMATCH_EMAIL, rs.getString("EMAIL_ADDRESS"));
			}
		} finally {
			if (rs != null) {
				rs.close();
			}
		}
		return resultm;
	}

	private static final String SQL_ENRICH_USING_CONTRACTTABLE = "select CONTRACT_ID, CUSTOMER_ID, HOUSE_NUMBER,  STREET, ZIPCODE, CITY,COUNTRY,ACCOUNT_NUMBER,BANK_CODE, BANK_ACCOUNT_HOLDER, STATUS, OPERATOR, PLATFORM, RECEPTION, OPERATION_DATE, WM_FLG, CONTRACT_TYPE, RATECARD_FLG, CNTRSTARTDATE,BANK_ACCOUNT_HOLDER,CUSTOMER_BIC,MANDATE_STATUS,MANDATE_REF_ID from newdb_contract where customer_id = ?  %s order by status, cntrstartdate desc";
	private static final String SQL_ENRICH_USING_CONTRACTTABLE_NOCONTRACTID = "select CONTRACT_ID, CUSTOMER_ID, HOUSE_NUMBER,  STREET, ZIPCODE, CITY,COUNTRY,ACCOUNT_NUMBER,BANK_CODE, BANK_ACCOUNT_HOLDER, STATUS, OPERATOR, PLATFORM, RECEPTION, OPERATION_DATE, WM_FLG, CONTRACT_TYPE, RATECARD_FLG, CNTRSTARTDATE,BANK_ACCOUNT_HOLDER,CUSTOMER_BIC,MANDATE_STATUS,MANDATE_REF_ID from newdb_contract where customer_id = ? order by status, cntrstartdate desc";
	private static final String SQL_ENRICH_USING_CONTRACTTABLE_WM = "select CONTRACT_ID, CUSTOMER_ID, WM_FLG from newdb_contract where customer_id= ? and WM_FLG = 'Y'";

	private Map<String, String> enrichFromContractTable(Connection con, String documentid, String customerNumber, Set<String> contractids) throws Exception {
		Map<String, String> resultm = new TreeMap<>();
		ResultSet rs = null;
		try {
			if (contractids.isEmpty()) {
				PreparedStatement contractStatement = con.prepareStatement(SQL_ENRICH_USING_CONTRACTTABLE_NOCONTRACTID);
				contractStatement.setString(1, customerNumber);
				rs = contractStatement.executeQuery();
				resultm = mapRsToContractTable(rs);
			} else {
				String esql = String.format(SQL_ENRICH_USING_CONTRACTTABLE, " and contract_id in('" + StringUtils.join(contractids, "','") + "') ");
				PreparedStatement contractStatement = con.prepareStatement(esql);
				contractStatement.setString(1, customerNumber);
				rs = contractStatement.executeQuery();
				resultm = mapRsToContractTable(rs);
			}
			//WM-Machine
			PreparedStatement wmStatement = con.prepareStatement(SQL_ENRICH_USING_CONTRACTTABLE_WM);
			wmStatement.setString(1, customerNumber);
			String wmflag = "N";
			rs = wmStatement.executeQuery();
			while (rs.next()) {
				String contractid = rs.getString("CONTRACT_ID");
				if (contractids.contains(contractid)) {
					wmflag = "Y";
				} else if (wmflag.equalsIgnoreCase("N")) {
					wmflag = "D";
				}
			}
			putIfNotEmpty(resultm, TagMatchDefinitions.META_WASHING_MACHINE, wmflag);
		} finally {
			if (rs != null)
				rs.close();
		}
		return resultm;
	}

	private Map<String, String> mapRsToContractTable(ResultSet rs) throws Exception {
		Map<String, String> result = new TreeMap<>();
		if (rs.next()) {
			putIfNotEmpty(result, TagMatchDefinitions.CUSTOMER_ID, rs.getString("CUSTOMER_ID"));
			putIfNotEmpty(result, TagMatchDefinitions.TAGMATCH_CONTRACT_NUMBER, rs.getString("CONTRACT_ID"));
			putIfNotEmpty(result, TagMatchDefinitions.CUSTOMER_CITY, rs.getString("CITY"));
			putIfNotEmpty(result, TagMatchDefinitions.CUSTOMER_STREET, rs.getString("STREET") + " " + rs.getString("HOUSE_NUMBER"));
			putIfNotEmpty(result, TagMatchDefinitions.CUSTOMER_ZIP_CODE, rs.getString("ZIPCODE"));
			putIfNotEmpty(result, TagMatchDefinitions.CUSTOMER_COUNTRY, rs.getString("COUNTRY"));
			putIfNotEmpty(result, TagMatchDefinitions.META_OPERATOR, rs.getString("OPERATOR"));
			putIfNotEmpty(result, TagMatchDefinitions.META_PLATFORM, rs.getString("PLATFORM"));
			putIfNotEmpty(result, TagMatchDefinitions.META_RECEPTION, rs.getString("RECEPTION"));
			putIfNotEmpty(result, TagMatchDefinitions.META_CONTRACT_TYPE, rs.getString("CONTRACT_TYPE"));
			putIfNotEmpty(result, TagMatchDefinitions.META_RATECARDFLG, rs.getString("RATECARD_FLG"));
			//putIfNotEmpty(result, TagMatchDefinitions.IBAN, rs.getString("CUSTOMER_IBAN"));
			putIfNotEmpty(result, TagMatchDefinitions.BIC, rs.getString("CUSTOMER_BIC"));
			putIfNotEmpty(result, TagMatchDefinitions.SEPA_STATUS, rs.getString("MANDATE_STATUS"));
			putIfNotEmpty(result, TagMatchDefinitions.SEPA_MANDATE_NUMBER, rs.getString("MANDATE_REF_ID"));

			// SIT-13-09-060, Vertragsdatum
			Date contractDate = rs.getDate("CNTRSTARTDATE");
			if (contractDate != null) {
				putIfNotEmpty(result, TagMatchDefinitions.META_CONTRACT_DATE, contractDate.toString());

				Calendar contractDateC = Calendar.getInstance();
				contractDateC.setTime(contractDate);

				Calendar oneMonthAgoC = Calendar.getInstance();
				oneMonthAgoC.add(Calendar.DATE, -30);

				if (contractDateC.after(oneMonthAgoC)) {
					SkyLogger.getConnectorLogger().info("IF31_FuzzyDB_CustAttrCollector:" + rs.getString("CUSTOMER_ID") + ":" + rs.getString("CONTRACT_ID") + " MetaContractDate:" + contractDateC.toString() + ">" + oneMonthAgoC.toString() + ":NK");
					result.put(TagMatchDefinitions.META_SUBSCRIPTON_DAYS, "NK");
				} else {
					SkyLogger.getConnectorLogger().info("IF31_FuzzyDB_CustAttrCollector:" + rs.getString("CUSTOMER_ID") + ":" + rs.getString("CONTRACT_ID") + " MetaContractDate:" + contractDateC.toString() + "<" + oneMonthAgoC.toString() + ":BK");
					result.put(TagMatchDefinitions.META_SUBSCRIPTON_DAYS, "BK");
				}
			}
		}
		return result;
	}

	private static final String SQL_ENRICH_USING_ASSETTABLE = "SELECT nc.CUSTOMER_ID, nc.CONTRACT_ID, nc.OPERATOR, na.SERIAL_NUMBER, na.STATUS, na.OPERATION_DATE FROM  newdb_asset na, newdb_contract nc " + "WHERE na.CUSTOMER_ID = ? AND na.CONTRACT_ID = nc.CONTRACT_ID AND na.CUSTOMER_ID = nc.CUSTOMER_ID %s ORDER BY na.STATUS, na.OPERATION_DATE DESC";

	private Map<String, String> enrichFromAssetTable(Connection con, String documentid, String customerNumber, Set<String> contractids, Set<String> serials) throws Exception {
		Map<String, String> result = new TreeMap<>();
		String sqlextension = "";
		if (serials != null && !serials.isEmpty()) {
			sqlextension += " AND na.SERIAL_NUMBER in ('" + StringUtils.join(serials, "','") + "') ";
		}
		if (contractids != null && !contractids.isEmpty()) {
			sqlextension += " AND nc.CONTRACT_ID in ('" + StringUtils.join(contractids, "','") + "') ";
		}

		String esql = String.format(SQL_ENRICH_USING_ASSETTABLE, sqlextension);
		PreparedStatement assetStatement = con.prepareStatement(esql);
		assetStatement.setString(1, customerNumber);
		ResultSet rs = assetStatement.executeQuery();

		List<String> rtoids = new LinkedList<>();
		List<String> rserials = new LinkedList<>();

		try {
			while (rs.next()) {
				//putIfNotEmpty(result, TagMatchDefinitions.TAGMATCH_CONTRACT_NUMBER, rs.getString("CONTRACT_ID"));
				String serial = rs.getString("SERIAL_NUMBER");
				if (isNotEmpty(serial)) {
					rserials.add(rs.getString("SERIAL_NUMBER"));
					if (rs.getString("OPERATOR") != null && rs.getString("OPERATOR").equalsIgnoreCase("telekom")) {
						rtoids.add(serial);
					}
				}
			}
			putIfNotEmpty(result, TagMatchDefinitions.TAGMATCH_SMARTCARD_NUMBER, StringUtils.join(rserials, ","));
			putIfNotEmpty(result, TagMatchDefinitions.META_TOIDS, StringUtils.join(rtoids, ","));
		} finally {
			if (rs != null)
				rs.close();
		}

		return result;
	}

	private static final String SQL_ENRICH_USING_CAMPAIGN = "select campaign_type from newdb_campaign_conf ccf where end_date > sysdate and start_date < sysdate and status='AKTIV' and exists (select customer_id from newdb_campaign ca where ca.campaign_id=ccf.campaign_id and ca.customer_id=? ) group by campaign_type ";

	private Map<String, String> enrichFromCampaignTable(Connection con, String documentid, String customerNumber) throws Exception {
		Map<String, String> result = new TreeMap<>();
		ResultSet rs = null;
		List<String> campaigns = new LinkedList<>();
		try {
			PreparedStatement contractStatement = con.prepareStatement(SQL_ENRICH_USING_CAMPAIGN);
			contractStatement.setString(1, customerNumber);
			rs = contractStatement.executeQuery();
			while (rs.next()) {
				String campaign = rs.getString("campaign_type");
				if (isNotEmpty(campaign)) {
					campaigns.add(campaign);
				}
			}
			putIfNotEmpty(result, TagMatchDefinitions.META_CAMPAIGN_TYPE, StringUtils.join(campaigns, ","));
		} finally {
			if (rs != null) {
				rs.close();
			}
		}
		return result;
	}

	//private static final String SQL_ACTIVE_CONTRACTS = "select CONTRACT_ID from newdb_contract where CUSTOMER_ID=? and STATUS='AKTIV'";
	private static final String SQL_ACTIVE_CONTRACTS = "select CONTRACT_ID from newdb_contract where CUSTOMER_ID=? and Status in ('AKTIV','ZU AKTIVIEREN', 'UNTERZEICHNET', 'UNTERSCHRIEBEN', 'GESPERRT', 'INTERESSENT', 'IN DER SCHWEBE')";
	private static final String SQL_ALL_CONTRACTS = "select CONTRACT_ID from newdb_contract where CUSTOMER_ID=? order by CNTRSTARTDATE desc";

	@Override
	public Set<String> getContractsForCustomerId(String documentid, String customerid) throws Exception {
		Connection connection = null;
		Set<String> contracts = new HashSet<>();

		try {
			connection = ContexDbConnector.getAutoCommitConnection();
			if (connection == null || connection.isClosed()) {
				SkyLogger.getItyxLogger().warn("Not valid connection from Connectionpool");
				connection = ContexDbConnector.getAutoCommitConnection();
			}
			if (isNotEmpty(customerid)) {
				PreparedStatement contractStatement = connection.prepareStatement(SQL_ACTIVE_CONTRACTS);
				contractStatement.setString(1, customerid);
				ResultSet contract = contractStatement.executeQuery();
				while (contract.next()) {
					contracts.add(contract.getString("CONTRACT_ID"));
				}
				contract.close();
				if (contracts.isEmpty()) {
					PreparedStatement allcontractStatement = connection.prepareStatement(SQL_ALL_CONTRACTS);
					allcontractStatement.setString(1, customerid);
					contract = allcontractStatement.executeQuery();
					while (contract.next()) {
						contracts.add(contract.getString("CONTRACT_ID"));
					}
					contract.close();
					allcontractStatement.close();
				}
			}
		} catch (SQLException e) {
			SkyLogger.getItyxLogger().error("DB-Problem during CollectingSerials using IF3.1:" + e.getMessage(), e);
		} finally {
			if (connection != null) {
				ContexDbConnector.releaseConnection(connection);
			}
		}
		return contracts;
	}

	private static final String SQL_SERIALS = "select SERIAL_NUMBER from newdb_asset where CUSTOMER_ID=? and CONTRACT_ID=? and STATUS not in ('FALSCHE ZUWEISUNG')";
	private static final String SQL_SERIALS_GEN = "select SERIAL_NUMBER from newdb_asset where CUSTOMER_ID=? and STATUS not in ('FALSCHE ZUWEISUNG')";

	@Override
	public Set<String> getSerialsForContractsId(String documentid, String customerid, Set<String> contractids) throws Exception {
		Connection connection = null;
		Set<String> serials = new HashSet<>();

		try {
			connection = ContexDbConnector.getAutoCommitConnection();
			if (connection == null || connection.isClosed()) {
				SkyLogger.getItyxLogger().warn("Not valid connection from Connectionpool");
				connection = ContexDbConnector.getAutoCommitConnection();
			}

			if (isNotEmpty(customerid)) {
				PreparedStatement serialStatement = connection.prepareStatement(SQL_SERIALS);
				serialStatement.setString(1, customerid);
				for (String contractid : contractids) {
					serialStatement.setString(2, contractid);
					ResultSet contract = serialStatement.executeQuery();
					while (contract.next()) {
						serials.add(contract.getString("SERIAL_NUMBER"));
					}
					contract.close();
				}
				serialStatement.close();

				if (serials.isEmpty()) {
					serialStatement = connection.prepareStatement(SQL_SERIALS_GEN);
					serialStatement.setString(1, customerid);
					ResultSet snRs = serialStatement.executeQuery();
					while (snRs.next()) {
						serials.add(snRs.getString("SERIAL_NUMBER"));
					}
					snRs.close();
					serialStatement.close();

				}
			}
		} catch (SQLException e) {
			SkyLogger.getItyxLogger().error("DB-Problem during CollectingSerials using IF3.1:" + e.getMessage(), e);
		} finally {
			if (connection != null) {
				ContexDbConnector.releaseConnection(connection);
			}
		}
		return serials;
	}


	//private static final String SQL_CONTRACTSBYSERIAL = "select CONTRACT_ID from newdb_asset where CUSTOMER_ID=? and SERIAL_NUMBER=? and STATUS='AKTIV'";
	private static final String SQL_CONTRACTSBYSERIAL = "select CONTRACT_ID from newdb_asset where CUSTOMER_ID=? and SERIAL_NUMBER=? and Status in ('AKTIV','ZU AKTIVIEREN', 'UNTERZEICHNET', 'UNTERSCHRIEBEN', 'GESPERRT', 'INTERESSENT', 'IN DER SCHWEBE')";
	private static final String SQL_CONTRACTSBYSERIAL_ALL = "select CONTRACT_ID from newdb_asset where CUSTOMER_ID=? and SERIAL_NUMBER=?";

	@Override
	public Set<String> getContractsForCustomerSerialId(String documentid, String customerid, Set<String> serials) throws Exception {
		Connection connection = null;
		Set<String> contracts = new HashSet<>();

		try {
			connection = ContexDbConnector.getAutoCommitConnection();
			if (connection == null || connection.isClosed()) {
				SkyLogger.getItyxLogger().warn("Not valid connection from Connectionpool");
				connection = ContexDbConnector.getAutoCommitConnection();
			}

			if (isNotEmpty(customerid)) {
				PreparedStatement contractStatement = connection.prepareStatement(SQL_CONTRACTSBYSERIAL);
				contractStatement.setString(1, customerid);

				for (String serial : serials) {
					contractStatement.setString(2, serial);
					ResultSet contract = contractStatement.executeQuery();
					while (contract.next()) {
						contracts.add(contract.getString("CONTRACT_ID"));
					}
					contract.close();
				}
				contractStatement.close();

				if (contracts.isEmpty()) {
					contractStatement = connection.prepareStatement(SQL_CONTRACTSBYSERIAL_ALL);
					contractStatement.setString(1, customerid);

					for (String serial : serials) {
						contractStatement.setString(2, serial);
						ResultSet contract = contractStatement.executeQuery();
						while (contract.next()) {
							contracts.add(contract.getString("CONTRACT_ID"));
						}
						contract.close();
					}
					contractStatement.close();
				}

			}
		} catch (SQLException e) {
			SkyLogger.getItyxLogger().error("DB-Problem during CollectingContracts using IF3.1:" + e.getMessage(), e);
		} finally {
			if (connection != null) {
				ContexDbConnector.releaseConnection(connection);
			}
		}
		return contracts;
	}

	private static final String SQL_CCHECK_CUSTSERIAL = "select CONTRACT_ID from newdb_asset where CUSTOMER_ID=? and SERIAL_NUMBER in ('%s') group by CONTRACT_ID";
	private static final String SQL_CCHECK_CUSTONLY = "select CONTRACT_ID from newdb_contract where CUSTOMER_ID=? group by CONTRACT_ID";

	@Override
	public void consistencyCheck(String documentid, String customerid, Set<String> contracts, Set<String> serials, Set<String> mandates) throws Exception {
		Connection connection = null;

		try {
			connection = ContexDbConnector.getAutoCommitConnection();
			if (connection == null || connection.isClosed()) {
				SkyLogger.getItyxLogger().warn("Not valid connection from Connectionpool");
				connection = ContexDbConnector.getAutoCommitConnection();
			}
			boolean throwExIfCustomerNotIdentified=false;
			//cccheck1 serial+customer
			if (!(serials == null || serials.isEmpty()) && isNotEmpty(customerid)) {
				String esql = String.format(SQL_CCHECK_CUSTSERIAL, StringUtils.join(serials, "','"));
				PreparedStatement contractStatement = connection.prepareStatement(esql);
				contractStatement.setString(1, customerid);

				ResultSet contract = contractStatement.executeQuery();

				int i = 0;
				while (contract.next()) {
					contracts.add(contract.getString("CONTRACT_ID"));
				}
				if (contracts.size() == 0){
					SkyLogger.getItyxLogger().info("Contract not found by search using custid:" + customerid + " sn:" + StringUtils.join(serials, "','") + " s:" + contracts.size());
					throwExIfCustomerNotIdentified=true;
					serials=null;
				} else if (contracts.size() > 1) {
					SkyLogger.getItyxLogger().error("Contract is not unique by search using custid:" + customerid + " sn:" + StringUtils.join(serials, "','") + " s:" + contracts.size());
					throw new Exception("Contract is not unique by search using custid:" + customerid + " sn:" + StringUtils.join(serials, "','") + " s:" + contracts.size());
				}
				contract.close();
			}
			//cccheck2 cutomerid only
			if ((contracts == null || contracts.isEmpty()) && (serials == null || serials.isEmpty())) {

				PreparedStatement contractStatement = connection.prepareStatement(SQL_CCHECK_CUSTONLY);
				contractStatement.setString(1, customerid);
				ResultSet contract = contractStatement.executeQuery();

				int i = 0;
				if (contracts==null){
					contracts=new HashSet<>();
				}
				while (contract.next()) {
					if (contract.getString("CONTRACT_ID") != null) {
						contracts.add(contract.getString("CONTRACT_ID"));
					}
				}
				if (contracts.size() == 0){
					SkyLogger.getItyxLogger().info("Contract not found by search using custid:" + customerid + " sn:" + StringUtils.join(serials, "','") + " s:" + contracts.size());
					throwExIfCustomerNotIdentified=true;
					serials=null;
				} else if (contracts.size() > 1) {
					throw new Exception("Contract is not unique by search using custid:" + customerid + " s:" + contracts.size());
				}
				contract.close();
			}

			if (throwExIfCustomerNotIdentified){
				throw new Exception("Contract not found by search using custid:" + customerid + " sn:" + StringUtils.join(contracts, "','") + " s:" + contracts.size());
			}

		} catch (SQLException e) {
			SkyLogger.getItyxLogger().error("DB-Problem during CollectingContracts using IF3.1:" + e.getMessage(), e);
			throw e;
		} finally {
			if (connection != null) {
				ContexDbConnector.releaseConnection(connection);
			}
		}
	}
}
