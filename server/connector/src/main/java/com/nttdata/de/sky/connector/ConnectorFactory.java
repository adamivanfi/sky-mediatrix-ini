package com.nttdata.de.sky.connector;

import com.nttdata.de.lib.logging.SkyLogger;

public class ConnectorFactory {

	public final static int TIMEOUT = 10000;

	private static INewDB instanceNewDb;
	private static boolean failedNewDb = false;
	
	private static IFAA instanceFAA;
	private static boolean failedFAA = false;

	private static ISiebel instanceSiebel;
	private static boolean failedSiebel = false;

	public static synchronized INewDB getNewDBInstance() {
		if (instanceNewDb == null && !failedNewDb) {
			try {
				SkyLogger.getConnectorLogger().info("IF3.2 Creating singelton: NewDBConnectorImpl");
				instanceNewDb = (INewDB) Class.forName("com.nttdata.de.sky.connector.newdb.NewDBConnectorImpl").newInstance();
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				SkyLogger.getConnectorLogger().error("IF3.2 Failed to create newdb-singleton", e);
				failedNewDb = true;
			}
		}
		return instanceNewDb;
	}
	public static synchronized ISiebel getSiebelInstance() {
		if (instanceSiebel == null && !failedSiebel) {
			try {
				SkyLogger.getConnectorLogger().info("IF4.X Creating singelton: SiebelConnectorImpl");
				instanceSiebel = (ISiebel) Class.forName("com.nttdata.de.sky.connector.siebel.SiebelConnectorImpl").newInstance();
			} catch (InstantiationException | ClassNotFoundException | IllegalAccessException e) {
				SkyLogger.getConnectorLogger().error("IF4.X Failed to create siebel-singleton", e);
				failedSiebel = true;
			}
		}
		return instanceSiebel;
	}
	public static synchronized IFAA getFAAInstance() {
		if (instanceFAA == null && !failedFAA) {
			try {
				SkyLogger.getConnectorLogger().info("IF3.3 Creating singelton: FAAConnectorImpl");
				instanceFAA = (IFAA) Class.forName("com.nttdata.de.sky.connector.faa.FAAConnectorImpl").newInstance();
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				SkyLogger.getConnectorLogger().error("IF3.3 Failed to create FAA singleton", e);
				failedFAA = true;
			}
		}
		return instanceFAA;
	}
}
