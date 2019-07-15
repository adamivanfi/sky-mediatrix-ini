package com.nttdata.de.ityx.sharedservices.utils;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.nttdata.de.ityx.sharedservices.configuration.BeanConfig;
import com.nttdata.de.lib.logging.SkyLogger;
import de.ityx.contex.interfaces.designer.IFlowObject;

import java.sql.*;

public class ContexDbConnector {

	private static final ComboPooledDataSource cpds = new ComboPooledDataSource(ContexDbConnector.class.getName());
	private static volatile boolean configured = false;

	public final static String DBHOST = "dbSKYHost";
	public final static String DBUSER = "dbSKYUser";
	public final static String DBPASSWD = "dbSKYPasswd";
	public final static String DBNAME = "dbSKYName";
	public final static String DBPORT = "dbSKYPort";
	public final static String DBTYPE = "dbSKYType";
	public final static String DBUNBUFFERED = "dbSKYjdbcQueryMod";

	// Pooling properties
	public final static String DB_MINPOOLSIZE = "dbSKYminpoolsize";
	public final static String DB_MAXPOOLSIZE = "dbSKYmaxpoolsize";
	public final static String DB_IDLECONNECTIONPERIOD = "dbSKYidleconperiod";
	public final static String DB_MAXIDLETIMEEXCESSCONNECTIONS = "dbSKYmaxidleconnections";
	public final static String DB_MAXSTATEMENTS = "dbSKYmaxstatements";
	public final static String DB_UNRETURNEDCONNECTIONTIMEOUT = "dbSKYunreturnedConnectionTimeout";
	public final static String DB_MAXIDLETIME = "dbSKYmaxidletime";
	public final static String DB_ACQUIRERETRYATTEMPTS = "dbSKYacquireRetryAttempts";
	
	// Pooling defaults
	private final static int DB_MINPOOLSIZE_DEFAULT = 3;
	private final static int DB_MAXPOOLSIZE_DEFAULT = 500;
	private final static int DB_IDLECONNECTIONPERIOD_DEFAULT = 60;
	private final static int DB_MAXIDLETIME_DEFAULT = 14400;
	private final static int DB_MAXSTATEMENTS_DEFAULT = 200;
	private final static int DB_MAXIDLETIMEEXCESSCONNECTIONS_DEFAULT = 200;
	private final static int DB_UNRETURNEDCONNECTIONTIMEOUT_DEFAULT = 240;
	private final static int DB_ACQUIRERETRYATTEMPTS_DEFAULT = 10;

	/**
	 * Create a new connection pool with the given parameters. If one already
	 * exists, then this step is being skipped.
	 *
	 * @param url
	 * @param user
	 * @param password
	 */
	public static synchronized void setUp(String url, String user, String password) {
		if (!configured) {
			cpds.setJdbcUrl(url);
			cpds.setUser(user);
			cpds.setPassword(password);
			
			int poolMaxPoolSize = BeanConfig.getInt(DB_MAXPOOLSIZE, DB_MAXPOOLSIZE_DEFAULT);
			int poolIdleConnectionTestPeriod = BeanConfig.getInt(DB_IDLECONNECTIONPERIOD, DB_IDLECONNECTIONPERIOD_DEFAULT);
			int poolMaxIdleTimeExcessConnections = BeanConfig.getInt(DB_MAXIDLETIMEEXCESSCONNECTIONS, DB_MAXIDLETIMEEXCESSCONNECTIONS_DEFAULT);
			int poolMaxIdleTime = BeanConfig.getInt(DB_MAXIDLETIME, DB_MAXIDLETIME_DEFAULT);
			int poolUnreturnedconnectiontimeout = BeanConfig.getInt(DB_UNRETURNEDCONNECTIONTIMEOUT, DB_UNRETURNEDCONNECTIONTIMEOUT_DEFAULT);
			int poolAcquireRetryAttempts = BeanConfig.getInt(DB_ACQUIRERETRYATTEMPTS, DB_ACQUIRERETRYATTEMPTS_DEFAULT);
			//			int poolMaxStatements = BeanConfig.getInt(DB_MAXSTATEMENTS,
			//					DB_MAXSTATEMENTS_DEFAULT);

			SkyLogger.getItyxLogger().info("Configuring a new cxdb connection pool:"+url+ " "+DB_MAXPOOLSIZE + poolMaxPoolSize
			+" "+DB_IDLECONNECTIONPERIOD + poolIdleConnectionTestPeriod
			+" "+DB_MAXIDLETIMEEXCESSCONNECTIONS + poolMaxIdleTimeExcessConnections
			+" "+DB_MAXIDLETIME + poolMaxIdleTime
			+" "+DB_UNRETURNEDCONNECTIONTIMEOUT + poolUnreturnedconnectiontimeout
			+" "+DB_ACQUIRERETRYATTEMPTS + poolAcquireRetryAttempts);


			cpds.setMinPoolSize(DB_MINPOOLSIZE_DEFAULT);
			cpds.setInitialPoolSize(DB_MINPOOLSIZE_DEFAULT);
			// should be close to the amount of running threads
			cpds.setMaxPoolSize(poolMaxPoolSize);
			cpds.setIdleConnectionTestPeriod(poolIdleConnectionTestPeriod);
			cpds.setMaxIdleTimeExcessConnections(poolMaxIdleTimeExcessConnections);
			cpds.setMaxIdleTime(poolMaxIdleTime);
			cpds.setAcquireRetryAttempts(poolAcquireRetryAttempts);
			cpds.setUnreturnedConnectionTimeout(poolUnreturnedconnectiontimeout);
			configured = true;
			
		}
	}

	/**
	 * Create a new connection pool with configuration parameters from the conf
	 * object.
	 *
	 * @param dsConf
	 */
	public static void setUp(ContexDbDataSourceModel dsConf) {
		setUp(dsConf.getUrl(), dsConf.getUser(), dsConf.getPassword());
	}

	/**
	 * Set up a pool from a given flowObject.
	 *
	 * @param flowObject
		 */
	public static void setUp(IFlowObject flowObject) {
		setUp(getConfigurationFromFlow(flowObject));
	}

	/**
	 * To be called after setUp.
	 *
	 * @return
	 * @throws SQLException
	 */
	public static synchronized Connection getConnection() throws SQLException {

		if (cpds.getNumBusyConnections()/cpds.getMaxPoolSize()>0.8) {
			SkyLogger.getItyxLogger().debug("num_connections: " + cpds.getNumConnectionsDefaultUser() +
					" num_busy_connections: " + cpds.getNumBusyConnectionsDefaultUser() +
					" num_idle_connections: " + cpds.getNumIdleConnectionsDefaultUser());
		}
		ContexDbConnector.setUp(ContexDbConnector.getConfigurationFromBeanConfig());
		Connection connection = null;
		try {
			connection = cpds.getConnection();
		} catch (Exception e) {
			SkyLogger.getItyxLogger().error("Could not acquire connection to Contex DB"+e.getMessage(),e);
		}
		if (connection == null) {
			SkyLogger.getItyxLogger().error("Could not acquire connection to Contex DB");
			throw new SQLException("Could not acquire connection to Contex DB");
		}
		return connection;
	}

	public static Connection getAutoCommitConnection() throws SQLException {
		Connection connection = getConnection();
		connection.setAutoCommit(true);
		return connection;
	}

	/**
	 * Tries to close a given connection - this is a pooled environment!
	 * Swallows and prints the error message if any.
	 *
	 * @param con
	 */
	public static void releaseConnection(Connection con) {
		if (SkyLogger.getItyxLogger().isDebugEnabled()) {
			SkyLogger.getItyxLogger().debug("Releasing db connection");
		}
		try {
			con.close();
		} catch (Exception e) {
			SkyLogger.getItyxLogger().error("Cannot release connection: "+e.getMessage(), e);
		}
	}

	/**
	 * Construct the db configuration object from a given flowObject containing
	 * all the parameters as variables.
	 *
	 * @param flowObject
	 * @return
	 */
	public static ContexDbDataSourceModel getConfigurationFromFlow(IFlowObject flowObject) {
		String dbHost = flowObject.get(DBHOST, String.class);
		String database = flowObject.get(DBNAME, String.class);
		String dbUser = flowObject.get(DBUSER, String.class);
		String dbPassword = flowObject.get(DBPASSWD, String.class);
		String dbPort = getValueWithDefault(flowObject.get(DBPORT, String.class), "3306");
		String dbType = getValueWithDefault(flowObject.get(DBTYPE, String.class), "mysql");
		String dbUnbuffered = getValueWithDefault(flowObject.get(DBUNBUFFERED, String.class), "false");

		// pool
		String poolMinPoolSize = getValueWithDefault(flowObject.get(DB_MINPOOLSIZE, String.class), "3");
		String poolMaxPoolSize = getValueWithDefault(flowObject.get(DB_MAXPOOLSIZE, String.class), "30");
		String poolIdleConnectionTestPeriod = getValueWithDefault(flowObject.get(DB_IDLECONNECTIONPERIOD, String.class), "300");
		String poolMaxIdleTimeExcessConnections = getValueWithDefault(flowObject.get(DB_MAXIDLETIMEEXCESSCONNECTIONS, String.class), "240");
		String poolMaxStatements = getValueWithDefault(flowObject.get(DB_MAXSTATEMENTS, String.class), "200");
		if (dbUnbuffered.equals("false")) {
			return ContexDbDataSourceModel.build("jdbc:" + dbType + "://" + dbHost + ":" + dbPort + "/" + database, dbUser, dbPassword);
		} else {
			return ContexDbDataSourceModel.build("jdbc:" + dbType + "://" + dbHost + ":" + dbPort + "/" + database + "?autoReconnect=true&useUnbufferedInput=true&useReadAheadInput=false", dbUser, dbPassword);
		}
	}

	public static ContexDbDataSourceModel getConfigurationFromBeanConfig() {
		try{
			String dbHost = BeanConfig.getReqString(DBHOST);
			String database = BeanConfig.getReqString(DBNAME);
			String dbUser = BeanConfig.getReqString(DBUSER);
			String dbPassword = BeanConfig.getReqString(DBPASSWD);
			String dbPort = BeanConfig.getString(DBPORT, "1525");
			String dbType = BeanConfig.getString(DBTYPE, "oracle");

			return ContexDbDataSourceModel.build("jdbc:" + dbType + ":thin:@" + dbHost + ":" + dbPort + ":" + database, dbUser, dbPassword);

		}catch (Exception e){
			SkyLogger.getItyxLogger().error("Cannot build ContexDbDataSourceModel! Please check ConnectionSettings:"+e.getMessage(), e);
			return null;
		}

	}

	/**
	 * Get a default value for a input parameter if it is null or empty.
	 *
	 * @param input
	 * @param defaultValue
	 * @return
	 */
	protected static String getValueWithDefault(String input, String defaultValue) {
		String output = null;
		if (input == null || input.isEmpty() || "".equals(input)) {
			output = defaultValue;
		} else {
			output = input;
		}
		return output;
	}

	/**
	 *  Close everything: resultset, statement, connection
	 * @param rs
	 * @param stmt
	 * @param con
	 */
	public static void closeEverything(ResultSet rs, PreparedStatement stmt, Connection con) {
		if (rs != null) {
			try {
				rs.close();
			} catch (Exception e) {
				SkyLogger.getItyxLogger().error("Cannot close resultset: "+e.getMessage(), e);
			}
		}
		if (stmt != null) {
			try {
				stmt.close();
			} catch (Exception e) {
				SkyLogger.getItyxLogger().error("Cannot close statement: "+ e.getMessage(), e);
			}
		}
		if (con!=null) {
			releaseConnection(con);
		}
	}

}
