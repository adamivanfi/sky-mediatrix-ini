package com.nttdata.de.ityx.cx.sky.outbound;

import com.jcraft.jsch.*;
import com.nttdata.de.lib.logging.SkyLogger;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

public class SFTPSession {

	private final String host;
	private final int port;
	private final String user;
	private final String password;

	private JSch jsch;
	private Session sftpSession;
	private static SFTPSession instance = null;

	public static synchronized SFTPSession getInstance(String host, int port,
			String user, String password) throws Exception {
		if (instance == null) {
			instance = new SFTPSession(host, port, user, password);
		}

		return instance;
	}

	public static void resetInstance(String host) {
		instance = null;
	}

	private SFTPSession(String host, int port, String user, String password)
			throws JSchException, SftpException {
		this.host = host;
		this.port = port;
		this.user = user;
		this.password = password;

		jsch = new JSch();
		connect();
	}

	private void connect() throws JSchException, SftpException {
		sftpSession = jsch.getSession(user, host, port);

		Hashtable<String, String> config = new Hashtable<>();
		config.put("StrictHostKeyChecking", "no");
		sftpSession.setConfig(config);
		sftpSession.setPassword(password);

		SkyLogger.getConnectorLogger().debug(
				"IF6.1: FTP Connection to " + host + ":" + port + "...");
		sftpSession.connect();
	}

	public ChannelSftp openChannel() {
		ChannelSftp sftpChannel = null;
		try {
			if (sftpSession == null || !sftpSession.isConnected()) {
				connect();
			}
			SkyLogger.getConnectorLogger().debug("IF6.1: FTP Setting up SFTP channel...");
			sftpChannel = (ChannelSftp) sftpSession.openChannel("sftp");
			sftpChannel.connect();

			if (sftpChannel.isConnected()) {
				SkyLogger.getConnectorLogger().debug(
						"IF6.1: FTP SFTP connection established: " + sftpChannel.pwd());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sftpChannel;
	}

	private void forceDir(String dir, ChannelSftp sftpChannel) throws Exception {
		String[] levels = dir.split("/");
		try {
			for (String level : levels) {
				try {
					sftpChannel.cd(level);
				} catch (Exception ex) {
					sftpChannel.mkdir(level);
					sftpChannel.cd(level);
				}
			}
		} catch (SftpException e) {
			throw new Exception("IF6.1: FTP Unable to create directory: " + dir, e);
		}
	}

	public void changeDir(String dir, boolean create, ChannelSftp sftpChannel)
			throws Exception {
		if (create) {
			forceDir(dir, sftpChannel);
		} else {
			try {
				sftpChannel.cd(dir);
			} catch (SftpException e) {
				throw new Exception("IF6.1: FTP Unable to change directory", e);
			}
		}
	}

	public void uploadFile(FileInputStream input, String remoteFile,
			ChannelSftp sftpChannel) throws Exception {
		FileInputStream fis = null;

		String rfile = remoteFile;
		if (remoteFile.indexOf("/") > 0) {
			String dir = remoteFile.substring(0, remoteFile.lastIndexOf("/"));
			rfile = remoteFile.substring(dir.length() + 1, remoteFile.length());
			try {
				changeDir(dir, true, sftpChannel);
			} catch (IOException e) {
				String msg = "IF6.1: FTP Unable to change to directory " + dir;
				SkyLogger.getConnectorLogger().error(msg, e);
				throw new Exception(msg, e);
			}
		}

		try {
			sftpChannel.put(input, rfile);
		} catch (SftpException e) {
			throw new Exception("IF6.1: FTP Unable to upload file:"+remoteFile, e);
		}
	}

	public void downloadFile(FileOutputStream output, String remoteFile,
			ChannelSftp sftpChannel) throws Exception {
		FileInputStream fos = null;

		String rfile = remoteFile;
		if (remoteFile.indexOf("/") > 0) {
			String dir = remoteFile.substring(0, remoteFile.lastIndexOf("/"));
			rfile = remoteFile.substring(dir.length() + 1, remoteFile.length());
			try {
				changeDir(dir, true, sftpChannel);
			} catch (IOException e) {
				String msg = "IF6.1: FTP Unable to change to directory " + dir;
				SkyLogger.getConnectorLogger().error(msg, e);
				throw new Exception(msg, e);
			}
		}

		try {
			sftpChannel.get(rfile, output);
		} catch (SftpException e) {
			throw new Exception("IF6.1: FTP Unable to download file", e);
		}
	}

	public Vector listDir(String path, ChannelSftp sftpChannel)
			throws Exception {
		Vector fileList = null;
		try {
			fileList = sftpChannel.ls(path);
		} catch (SftpException e) {
			throw new Exception("IF6.1: FTP Unable to list directory", e);
		}
		return fileList;
	}

	public String toString() {
		return "SFTPSession{" + user + ":" + password + "@" + host + ":" + port
				+ "}";
	}

}
