package com.nttdata.de.ityx.cx.workflow.outgoing.mxpostprocessing;

import com.nttdata.de.ityx.sharedservices.configuration.BeanConfig;
import com.nttdata.de.lib.logging.SkyLogger;
import de.ityx.base.dbpooling.DBConnectionPoolFactory;
import de.ityx.contex.interfaces.designer.IFlowObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MxDbSingleton {

    protected static String urlString;

    private static MxDbSingleton instance = null;
    private static final Object mutex = new Object();
    private static Connection con = null;
    private static int usages=0;

    private MxDbSingleton(String urlString) {
        MxDbSingleton.urlString = urlString;
    }

   /* public static Connection getMxConnection(String mxDbhost, String mxDbport,
            String mxDbname, String mxDbuser, String mxDbpass) {
        urlString = "jdbc:oracle:thin:" + mxDbuser + "/" + mxDbpass + "@" + mxDbhost + ":" + mxDbport + ":" + mxDbname;
        return MxDbSingleton.getMxConnection(urlString);
        // jdbc:oracle:thin:[USER/PASSWORD]@[HOST][:PORT]:SID
        // jdbc:oracle:thin:[USER/PASSWORD]@//[HOST][:PORT]/SERVICE
    }*/
   public static Connection getMxConnection() {
       return MxDbSingleton.getMxConnection(getMxDbUrlString());
   }
    public static Connection getMxConnection(IFlowObject flowObject) {
            return MxDbSingleton.getMxConnection(getMxDbUrlString(flowObject));
    }

    public static Connection getMxConnection(String urlString) {
        return DBConnectionPoolFactory.getPool().getCon();

        /*if (instance == null) {
            SkyLogger.getWflLogger().debug("instance is emtpy, creating MxDbSingleton: ");
            synchronized (mutex) {
                instance = new MxDbSingleton(urlString);
            }
        }
        try {
            usages++;
            if (con == null || !con.isValid(120) || usages>100) {
                if (con != null && con.isValid(120)) {
                    try {
                        con.close();
                    } catch (SQLException e) {
                        SkyLogger.getWflLogger().error("Unsucessful closing of invalid connection...");
                    }
                    con = null;
                    usages=0;
                }
                //SkyLogger.getWflLogger().debug("try to connect to MX DB...");
                try {
                    OracleConnectionPoolDataSource ds = new OracleConnectionPoolDataSource();
                    ds.setURL(urlString);
                    //SkyLogger.getWflLogger().debug("try to connect with DB");
                    con = ds.getConnection();
                } catch (SQLException e) {
                    SkyLogger.getWflLogger().error("MxDbSingleton.connect(): " + e, e);
                }
            }
        } catch (SQLException e) {
            SkyLogger.getWflLogger().error("MxDbSingleton.getInstance(): " + e, e);
        }
        return con;*/
    }

    public static void closeConnection(Connection con) {
       // SkyLogger.getWflLogger().debug("MxDbSingleton.closeConnection()...");
        try {
            if (con!=null) {
                DBConnectionPoolFactory.getPool().releaseCon(con);
            }
        } catch (Exception e) {
            SkyLogger.getWflLogger().error("Unsucessful closing of connection...", e);
        }
    }

    public static String getMxDbUrlString(IFlowObject flowObject) {
        return getMxDbUrlString();
    }
    
    public static String getMxDbUrlString() {

        String MX_DBURL = "dburl";
        String MX_DBHOST = "dbhost";
        String MX_DBPORT = "dbport";
        String MX_DBNAME = "dbname";
        String MX_DBSID = "dbsidname";
        String MX_DBSERVICE = "dbservicename";
        String MX_DBUSER = "dbuser";
        String MX_DBPASS = "dbpass";


        String dburl = BeanConfig.getString(MX_DBURL,"");
        if (dburl != null && !dburl.isEmpty()) {
            return dburl;
        }
        String dbhost = BeanConfig.getString(MX_DBHOST,"");
        String dbport = BeanConfig.getString(MX_DBPORT,"");
        String dbuser = BeanConfig.getString(MX_DBUSER,"");
        String dbpass = BeanConfig.getString(MX_DBPASS,"");
        
        String dbname = BeanConfig.getString(MX_DBNAME,"");
        String dbsidname = BeanConfig.getString(MX_DBSID,"");
        String dbservicename = BeanConfig.getString(MX_DBSERVICE,"");
        
        if (dbservicename!=null && !dbservicename.isEmpty()){
            // jdbc:oracle:thin:[USER/PASSWORD]@//[HOST][:PORT]/SERVICE
            return "jdbc:oracle:thin:" + dbuser + "/" + dbpass + "@" + dbhost + ":" + dbport + "/" + dbservicename;
        }else if (dbsidname!=null && !dbsidname.isEmpty()){
            // jdbc:oracle:thin:[USER/PASSWORD]@[HOST][:PORT]:SID
            return "jdbc:oracle:thin:" + dbuser + "/" + dbpass + "@" + dbhost + ":" + dbport + ":" + dbsidname;
        }else{
             // jdbc:oracle:thin:[USER/PASSWORD]@//[HOST][:PORT]/SERVICE
            return "jdbc:oracle:thin:" + dbuser + "/" + dbpass + "@" + dbhost + ":" + dbport + "/" + dbname;
        }
        
        
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
                stmt.setPoolable(true);
                stmt.close();
            } catch (Exception e) {
                SkyLogger.getItyxLogger().error("Cannot close statement: "+ e.getMessage(), e);
            }
        }
        closeConnection(con);
    }
}
