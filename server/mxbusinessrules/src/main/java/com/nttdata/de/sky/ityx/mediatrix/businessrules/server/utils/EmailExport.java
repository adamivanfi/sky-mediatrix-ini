/**
 *
 */
package com.nttdata.de.sky.ityx.mediatrix.businessrules.server.utils;

import de.ityx.base.dbpooling.DBConnectionPoolFactory;

import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;
import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

/**
 * @author DHIFLM
 */
public class EmailExport {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		Properties properties = new Properties();
		properties.put("mail.smtp.port", "25");
		Connection connection = DBConnectionPoolFactory.getPool().getCon();
		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("select EMAILSERVER_IN,EMAIL_OUT,PASSWORT_OUT from PROJEKT where ID=110");
			if (rs.next()) {
				final String host = rs.getString("EMAILSERVER_IN");
				properties.put("mail.smtp.host", host);
				System.out.println("Using SMTP host: " + host);
				final String login = rs.getString("EMAIL_OUT");
				System.out.println("Using SMTP user: " + login);
				final String password = rs.getString("PASSWORT_OUT");
				Authenticator auth = new Authenticator() {
					public PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(login, password);
					}
				};
				Session session = Session.getDefaultInstance(properties, auth);

				PreparedStatement statement = connection.prepareStatement("select * from EMAIL where MESSAGEID=?");
				BufferedReader in = new BufferedReader(new FileReader(args[0])); // "c:/tmp/1205-ctex-no-noreply.log"
				String line = null;
				while ((line = in.readLine()) != null) {
					int start = line.indexOf("id=");
					if (start > -1) {
						line = line.substring(start + 3).trim();
						System.out.println("Resending Message-ID: " + line);
						statement.setString(1, "<" + line + ">");
						ResultSet resultSet = statement.executeQuery();
						if (resultSet.next()) {
							sendmail(resultSet, session);
						}
						resultSet.close();
					}
				}
			}
			rs.close();
		} finally {
			DBConnectionPoolFactory.getPool().releaseCon(connection);
		}
	}

	public static void sendmail(ResultSet resultSet, Session session) throws Exception {
		String from = resultSet.getString("SENDER");
		String to = resultSet.getString("RECEIVER");
		String subject = resultSet.getString("SUBJECT");
		String text = resultSet.getString("BODY");
		String headers = resultSet.getString("HEADERS");
		System.out.println("Send mail \nfrom: " + from + "\nto: " + to + "\n subject: " + subject);
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
			message.setSubject(subject);
			message.setDataHandler(new DataHandler(new ByteArrayDataSource(text, "text/html")));
			Transport.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
}
