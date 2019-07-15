package com.nttdata.de.lib.utils;

import com.nttdata.de.lib.logging.SkyLogger;
import de.ityx.base.Global;
import de.ityx.base.dbpooling.DBConnectionPoolFactory;
import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.api.interfaces.IConnectionPool;
import de.ityx.mediatrix.data.OperatorLogRecord;
import de.ityx.mediatrix.data.Question;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by meinusch on 30.09.15.
 */
public class MitarbeiterlogWriter {

	private static final String LOGCHECKSQL = "select PARAMETER, mitarbeiterid, aktion, zeit from mitarbeiterlog where frageid=? and aktion=? and zeit=? and mitarbeiterid=?";
	private static final String LOGCHECKSQLPARAM = "select PARAMETER, mitarbeiterid, aktion, zeit from mitarbeiterlog where frageid=? and aktion=? and mitarbeiterid=? and PARAMETER=?";
	private static final String INSERTLOGSQL = "insert into MITARBEITERLOG(mitarbeiterid, frageid, antwortid, aktion, parameter, zeit, oper) values (?,?,?,?,?,?,?)";
	private static final String DELETELOGSQL = "delete from mitarbeiterlog where mitarbeiterid=? and frageid=? and aktion=? and zeit=?";
	private static final String UPDATELOGSQL = "update mitarbeiterlog set parameter=? where mitarbeiterid=? and frageid=? and aktion=? and zeit=?";


	public static boolean writeMitarbeiterlog(int operator, Question question, int antwortid, int aktion, String parameter, long time, boolean operatorB) {
		int questionid = (question != null ? question.getId() : 0);
		return writeMitarbeiterlog(operator, questionid, antwortid, aktion, parameter, time, operatorB);
	}
	public static boolean writeMitarbeiterlogIfNeeded(Question question, String parameter, int aktion, int operator) {
		return writeMitarbeiterlog(operator, question.getId(), 0, aktion, parameter, System.currentTimeMillis(), (operator > 0), true);
	}
 	public static boolean writeMitarbeiterlog(Question question, String parameter, int aktion, int operator) {
 		return writeMitarbeiterlog(operator, question.getId(), 0, aktion, parameter, System.currentTimeMillis(), (operator > 0));
 	}
	public static boolean writeMitarbeiterlog(int operator, int questionid, int antwortid, int aktion, String parameter, boolean operatorB) {
		return writeMitarbeiterlog(operator, questionid, antwortid, aktion, parameter, System.currentTimeMillis(), operatorB);
	}
	public static boolean writeMitarbeiterlog(int operator, int questionid, int antwortid, int aktion, String parameter, long time, boolean operatorB) {
		return writeMitarbeiterlog(operator,  questionid,  antwortid,  aktion,  parameter,  time,  operatorB, false);
	}
	public static boolean writeMitarbeiterlog(int operator, int questionid, int antwortid, int aktion, String parameter, long time, boolean operatorB, boolean checkIfOptional) {
		String logpreafix = new Object() {
		}.getClass().getEnclosingMethod().getName() + " q:" + questionid + " ";

		IConnectionPool cpool = DBConnectionPoolFactory.getPool();
		Connection con2 = null;
		try {
			con2 = cpool.getCon();
			if (questionid < 1) {
				SkyLogger.getMediatrixLogger().error(logpreafix + " Unable to write log entry for Question with empty ID:" + operator + ", " + questionid + ",a: " + antwortid + ", ak:" + aktion + ", param" + parameter + ", " + time + ", " + operatorB);
				return false;
			}
			if(checkIfOptional) {
				try {
					boolean logItemalreadypresent = MitarbeiterlogWriter.verifyLogEntry( con2, operator,  questionid,  antwortid,  aktion,  parameter, 0);
					if (logItemalreadypresent){
						return true;
					}
				} catch (SQLException e) {
					SkyLogger.getMediatrixLogger().debug(logpreafix + "item :"+questionid+" a:"+aktion+" p:"+parameter+ " could not be checked");
				}
				
			}
			if (Global.getOperatorLogLength() < 20) {
				SkyLogger.getMediatrixLogger().debug(logpreafix + "GlobalOpL:" + Global.getOperatorLogLength());
				synchronized (LOGCHECKSQL) {
					Global.initializeConfigurationForUnitIfNotDoneYet("DEFAULT");
					Global.setOperatorLogLength(2040);
				}
			}
			boolean verified = false;
			//Standard API Way
			try {
				if (API.getServerAPI().getCommonAPI().writeLog(con2, operator, questionid, antwortid, aktion, parameter, time, operatorB)) {
					verified = verifyLogEntry(con2, operator, questionid, antwortid, aktion, parameter, time);
				}
			} catch (SQLException e) {
				SkyLogger.getMediatrixLogger().error(logpreafix + " Unable to write log entry for Question:" + operator + ", " + questionid + ",a: " + antwortid + ", ak:" + aktion + ", param" + parameter + ", " + time + ", " + (operator > 0) + e.getMessage(), e);
			}
			if (verified) {
				SkyLogger.getMediatrixLogger().debug(logpreafix + " Write log 1 entry OK for Question:" + operator + ", " + questionid + ",a: " + antwortid + ", ak:" + aktion + ", param" + parameter + ", " + time + ", " + (operator > 0));
			} else {
				try {
					verified = writeLogDirectly(con2, operator, questionid, antwortid, aktion, parameter, time);
				} catch (SQLException e) {
					SkyLogger.getMediatrixLogger().error(logpreafix + " Unable to write log entry for Question:" + operator + ", " + questionid + ",a: " + antwortid + ", ak:" + aktion + ", param" + parameter + ", " + time + ", " + (operator > 0) + e.getMessage(), e);
				}
			}

			//Giving up?
			if (!verified) {
				SkyLogger.getMediatrixLogger().error(logpreafix + " WriteLog:NOK:GIVING UP: op:" + operator + " q:" + questionid + " time:" + time + " aktion:" + aktion + " parameter:" + parameter);
				return false;
			} else {
				SkyLogger.getMediatrixLogger().debug(logpreafix + " WriteLog:OK: op:" + operator + " q:" + questionid + " time:" + time + " aktion:" + aktion + " parameter:" + parameter);
				return true;
			}
		} finally {
			if (con2 != null) {
				try {
					if (!con2.getAutoCommit()) {
						con2.commit();
					}
				} catch (SQLException e) {
					SkyLogger.getMediatrixLogger().warn(logpreafix + " Unable to commit connection during writing log entry for Question:" + operator + ", " + questionid + ",a: " + antwortid + ", ak:" + aktion + ", param" + parameter + ", " + time + ", " + (operator > 0) + e.getMessage(), e);
				}
				cpool.releaseCon(con2);
			}
		}
	}

	public static boolean deleteLogDirectly(Connection con, int operator, int questionid, int aktion, long time) throws SQLException {
		String logpreafix = new Object() {
		}.getClass().getEnclosingMethod().getName() + " q:" + questionid + " ";
		boolean ret = false;
		PreparedStatement pst = null;
		try {
			pst = con.prepareStatement(DELETELOGSQL);
			pst.setInt(1, operator);
			pst.setInt(2, questionid);
			pst.setInt(3, aktion);
			pst.setLong(4, time);
			ResultSet rs = null;
			try {
				rs = pst.executeQuery();
				if (!con.getAutoCommit()) {
					con.commit();
				}
			} finally {
				if (rs != null) {
					rs.close();
				}
			}
			ret = true;
		} catch (SQLException e) {
			SkyLogger.getMediatrixLogger().error(logpreafix + " Unable to delete log entry via JDBC" + e.getMessage(), e);
		} finally {
			if (pst != null) {
				pst.close();
			}
		}
		return ret;
	}

	public static boolean updateLogDirectly(Connection con, int operator, int questionid, int aktion, long time, String parameter) throws SQLException {
		String logpreafix = new Object() {
		}.getClass().getEnclosingMethod().getName() + " q:" + questionid + " ";
		boolean ret = false;
		PreparedStatement pst = null;
		try {
			pst = con.prepareStatement(UPDATELOGSQL);
			pst.setString(1, parameter);
			pst.setInt(2, operator);
			pst.setInt(3, questionid);
			pst.setInt(4, aktion);
			pst.setLong(5, time);
			ResultSet rs = null;
			try {
				rs = pst.executeQuery();
				if (!con.getAutoCommit()) {
					con.commit();
				}
			} finally {
				if (rs != null) {
					rs.close();
				}
			}
			ret = true;
		} catch (SQLException e) {
			SkyLogger.getMediatrixLogger().error(logpreafix + " Unable to delete log entry via JDBC" + e.getMessage(), e);
		} finally {
			if (pst != null) {
				pst.close();
			}
		}
		return ret;
	}

	private static boolean writeLogDirectly(Connection con, int operator, int questionid, int antwortid, int aktion, String parameter, long time) throws SQLException {
		String logpreafix = new Object() {
		}.getClass().getEnclosingMethod().getName() + " q:" + questionid + " ";
		PreparedStatement pst = null;
		try {
			pst = con.prepareStatement(INSERTLOGSQL);
			pst.setInt(1, operator);
			pst.setInt(2, questionid);
			pst.setInt(3, antwortid);
			pst.setInt(4, aktion);
			pst.setString(5, parameter);
			pst.setLong(6, time);
			pst.setInt(7, 0);
			ResultSet rs = null;
			try {
				rs = pst.executeQuery();
				if (!con.getAutoCommit()) {
					con.commit();
				}
			} finally {
				if (rs != null) {
					rs.close();
				}
			}
		} catch (SQLException e) {
			SkyLogger.getMediatrixLogger().error(logpreafix + " Unable to insert log entry via JDBC" + e.getMessage(), e);
		} finally {
			if (pst != null) {
				pst.close();
			}
		}
		return verifyLogEntry(con, operator, questionid, antwortid, aktion, parameter, time);
	}

	private static boolean verifyLogEntry(Connection con, int operator, int questionid, int antwortid, int aktion, String parameter, long time) throws SQLException {
		String logpreafix = new Object() {
		}.getClass().getEnclosingMethod().getName() + " q:" + questionid + " ";
		// Checks if entry was written.
		if (parameter != null && !parameter.isEmpty()) {
			PreparedStatement pst = null;
			try {
				if (time>1) {
					pst = con.prepareStatement(LOGCHECKSQL);
					pst.setInt(1, questionid);
					pst.setInt(2, aktion);
					pst.setLong(3, time);
					pst.setInt(4, operator);
				}else{
					pst = con.prepareStatement(LOGCHECKSQLPARAM);
					pst.setInt(1, questionid);
					pst.setInt(2, aktion);
					pst.setInt(3, operator);
					pst.setString(4,parameter);
				}

				ResultSet rs = null;
				try {
					rs = pst.executeQuery();
					boolean foundCorrectEntry = false;
					while (rs.next()) {
						String checkparameter = rs.getString("PARAMETER");
						if (checkparameter != null && !checkparameter.isEmpty() && checkparameter.equalsIgnoreCase(parameter)) {
							foundCorrectEntry = true;
						} else {
							SkyLogger.getMediatrixLogger().debug(logpreafix + " API-Found Searching Entry with right parameter, this entry doesn't match:" + checkparameter);
						}
					}
					if (foundCorrectEntry) {
						return true;
					} else {
						SkyLogger.getMediatrixLogger().info(logpreafix + " Unable to write log entry via API-call, trying JDBC");
					}
				} finally {
					if (rs != null) {
						rs.close();
					}
				}
			} catch (SQLException e) {
				SkyLogger.getMediatrixLogger().error(logpreafix + " Unable to check log entry via JDBC" + e.getMessage(), e);
			} finally {
				if (pst != null) {
					pst.close();
				}
			}
		}
		return false;
	}

}
