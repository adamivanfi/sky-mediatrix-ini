package com.nttdata.de.ityx.cx.workflow.incoming.i2_preprocessing;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflReportedBean;
import com.nttdata.de.ityx.sharedservices.configuration.BeanConfig;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.ityx.sharedservices.utils.ShedulerUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import com.sun.mail.smtp.SMTPMessage;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.impl.document.text.email.EmailDocument;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.document.CDocument;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MailDateFormat;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class PreprocessingMail extends AbstractWflReportedBean {
	
	private static final long serialVersionUID = -1448434499666667616L;
	String parameter = "300_Classification";
	public static List EMAILS_PROHIBITED_TO_SEND;


	@Override
	public void execute(IFlowObject flowObject) throws Exception {
		Logger log = SkyLogger.getWflLogger();
		CDocumentContainer docContainer = DocContainerUtils.getDocContainer(flowObject);
		CDocument doc = DocContainerUtils.getDoc(flowObject, docContainer);
		docContainer.setNote("DOCUMENTSOURCE", "MailBox");
		docContainer.setNote(TagMatchDefinitions.CHANNEL, "EMAIL");
		doc.setNote("Channel", "EMAIL");
		String docid = DocContainerUtils.getDocID(flowObject);
		DocContainerUtils.setDocID(flowObject, docid);

		Date incommingdate = new java.util.Date();
		Date createdate = incommingdate;

		if (EmailDocument.class.isAssignableFrom(doc.getClass())) {
			EmailDocument edoc = (EmailDocument) doc;
			String messageId = DocContainerUtils.getMessageId(edoc);
			createdate = findEmailDate(edoc, incommingdate, messageId);
			int attachments_quantity=edoc.attachments.size();
			docContainer.setNote(TagMatchDefinitions.ATTACHMENTS_QUANTITY, attachments_quantity+"");
			doc.setNote(TagMatchDefinitions.ATTACHMENTS_QUANTITY, attachments_quantity+"");
			log.debug("210: ATTACHMENTS_QUANTITY:"+attachments_quantity);
		} else {
			log.error("210: Wrong MessagePayload:" + docid);
			throw new Exception("Expected CDoc with Email payload. Email-Payload not found." + docid);
		}
		Map<String, Object> map = DocContainerUtils.setIncommingDate(docContainer, doc, incommingdate, createdate);
		flowObject.put("map", map);
		log.info("210: DocIDSet:" + docid + " >" + (new SimpleDateFormat("yyyy.MM.dd HH:mm:ss")).format(createdate) + "<");

		//SIT-19-04-016  - Jardel Luis Roth
		final Boolean autoReply = BeanConfig.getBoolean("autoreply_send", false);
		log.info("210: AutoReply.Send " + autoReply.toString());
		if (autoReply) {
			EmailDocument edoc = (EmailDocument) doc;
			log.info("210: Email from " + edoc.getFrom());
			log.info("210: Email to " + edoc.getTo());
			String emailFrom = edoc.getFrom();

			// INCTASK0034424 - System mail addresses should not received auto reply from Mediatrix.
			EMAILS_PROHIBITED_TO_SEND = new LinkedList();
			for (String s : StringUtils.split(BeanConfig.getString("autoreply_skyemailnotsend", "-1"), ",")) {
				if (s != null && !s.isEmpty()) {
					EMAILS_PROHIBITED_TO_SEND.add(s.trim());
					log.info("210: SKY Email not reply to #" +s.trim());
				} else {
					log.info("210: Cannot read autoreply_skyemailnotsend: " + s);
				}
			}
			String toEmail = emailFrom;
			Integer i2 = toEmail.indexOf('<')+1;
			if (i2 > 0) {
				toEmail = toEmail.substring(toEmail.indexOf('<')+1, toEmail.indexOf('>'));
			}

			if(EMAILS_PROHIBITED_TO_SEND.contains(toEmail.toLowerCase())){
				log.info("210: Auto Reply not sent to " + toEmail);
			} else if (emailFrom.toUpperCase().contains("NOREPLY@SKY.DE")){
				log.info("210: Auto Reply not sent to " + emailFrom);
			} else if (emailFrom.toUpperCase().contains("NO-REPLY@SKY.DE")){
				log.info("210: Auto Reply not sent to " + emailFrom);
			} else if (emailFrom.toUpperCase().contains("NO-REPLY@SKY.AT")){
				log.info("210: Auto Reply not sent to " + emailFrom);
			} else if (emailFrom.toUpperCase().contains("NOREPLY@SKY.AT")){
				log.info("210: Auto Reply not sent to " + emailFrom);
			} else {
				String emailTo = edoc.getTo();
				log.info("210: Email to " + edoc.getTo());
				Integer i1 = emailTo.indexOf('<')+1;
				log.info("210: Index  vor sendMail: " + i1.toString());
				if (i1 > 0) {
					emailTo = emailTo.substring(emailTo.indexOf('<')+1, emailTo.indexOf('>'));
				}
				log.info("210: Email to " + edoc.getTo());
				sendMail("skydmsint@sky.de", edoc.getFrom(), "Ihre E-Mail an uns ", "Template", emailTo);
				log.info("210: Email Send End ");
			}
		}

		ShedulerUtils.storeDocAndScheduleProcess(ShedulerUtils.getDefaultUnit(), ShedulerUtils.getDefaultMaster(), parameter, docContainer, doc);
	}


	private Date findEmailDate(EmailDocument edoc, Date incommingdate, String messageId){
		Logger log = SkyLogger.getWflLogger();
		Date createdate = incommingdate;
		try {
			InputStream src = new ByteArrayInputStream(edoc.getPayload());
			Session mailSession = Session.getDefaultInstance(System.getProperties(), null);
			MimeMessage message = new MimeMessage(mailSession, src);
			if (message != null) {
				createdate = message.getSentDate();
			}
		} catch (Exception e) {
			log.warn("210: Problem parsing date from RawMessage:" + messageId + ":" + e.getMessage());
		}
		log.info("210: parsed date:" + createdate);

		if (createdate == null) {
			try {
				createdate = (new MailDateFormat()).parse(edoc.getEmailDate());
			} catch (Exception e) {
				try {
					createdate = (new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH)).parse(edoc.getEmailDate());
				} catch (Exception ee) {
					log.warn("210: Problem parsing date:" + messageId + ":" + edoc.getEmailDate() + ":" + ee.getMessage());
				}
			}
		}
		log.info("210: parsed date:" + createdate);
		return createdate;
	}

	private void sendMail(String from, String to, String subject, String message, String mailSky) {

		Properties props = new Properties();
		Logger log = SkyLogger.getWflLogger();
		log.info("210: Start Reply Email: " + to);
		try {
			final String mailHost = BeanConfig.getString("autoreply_mailHost", "-1");
			final String mailUser = BeanConfig.getString("autoreply_mailUser", "-1");
			final String mailPass = BeanConfig.getString("autoreply_mailPass", "-1");
			final String mailPort = BeanConfig.getString("autoreply_mailPort", "-1");
			final String mailProtocol = BeanConfig.getString("autoreply_mailProtocol", "-1");
			final String mailskyde = BeanConfig.getString("autoreply_emailskyde", "-1");
            final String mailskyat = BeanConfig.getString("autoreply_emailskyat", "-1");
            final String mailsbsde = BeanConfig.getString("autoreply_emailsbsde", "-1");
            final String mailsbsat = BeanConfig.getString("autoreply_emailsbsat", "-1");
            final String templateskyde = BeanConfig.getString("autoreply_templateskyde", "-1");
            final String templateskyat = BeanConfig.getString("autoreply_templateskyat", "-1");
            final String templatesbsde = BeanConfig.getString("autoreply_templatesbsde", "-1");
            final String templatesbsat = BeanConfig.getString("autoreply_templatesbsat", "-1");

			log.info("210: Email Server Host: " + mailHost);
			log.info("210: AutoReply.User " + mailUser);
			log.info("210: AutoReply.Pass " + mailPass);
			log.info("210: AutoReply.Port " + mailPort);
			log.info("210: AutoReply.Protocol " + mailProtocol);
            log.info("210: AutoReply.emailskyde " + mailskyde);
            log.info("210: AutoReply.emailskyat " + mailskyat);
            log.info("210: AutoReply.emailsbsde " + mailsbsde);
            log.info("210: AutoReply.emailskyat " + mailsbsat);
            log.info("210: AutoReply.templateskyde " + templateskyde);
            log.info("210: AutoReply.templateskyat " + templateskyat);
            log.info("210: AutoReply.templatesbsde " + templatesbsde);
            log.info("210: AutoReply.templatesbsat " + templatesbsat);

			props.put("mail.transport.protocol", mailProtocol);
			props.put("mail.smtp.host", mailHost);
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.port", mailPort);
			props.put("mail.smtp.from", " ");

			Authenticator auth = new Authenticator() {
				@Override
				public PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(mailUser,mailPass);
				}
			};

			Session session = Session.getInstance(props, auth);

			//Message msg = new MimeMessage(session);
			Message msg2 = new SMTPMessage(session);
			((SMTPMessage) msg2).setEnvelopeFrom(" ");
			Integer i2 = to.indexOf('<')+1;
            log.info("210: Index sendMail: " + i2.toString());
            String toEmail = to;
            if (i2 > 0) {
                toEmail = to.substring(to.indexOf('<')+1, to.indexOf('>'));
            }
			log.info("210: Sent to: " + to);
			log.info("210: Sent to: " + toEmail);
            msg2.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            msg2.setFrom(new InternetAddress(from));
            msg2.setSubject(subject);

			MimeMultipart multipart = new MimeMultipart();
			BodyPart messageBodyPart = new MimeBodyPart();

			//Set key values
			Map<String, String> input = new HashMap<String, String>();
				input.put("Author", "NTT Data");
				input.put("Topic", "HTML Template for Email");
				input.put("Content In", "English");

			String htmlText = "";
            boolean skyDe = true;
            if (mailSky.toUpperCase().contains(mailskyde.toUpperCase())){
                htmlText = readEmailFromHtml(templateskyde,input);
				log.info("210: readEmailFromHtml" + templateskyde);
                skyDe = true;
            } else if (mailSky.toUpperCase().contains(mailskyat.toUpperCase())) {
                htmlText = readEmailFromHtml(templateskyat,input);
				log.info("210: readEmailFromHtml" + templateskyat);
				skyDe=false;
            }

			messageBodyPart.setContent(htmlText, "text/html");
			multipart.addBodyPart(messageBodyPart);
			msg2.setContent(multipart);

			Transport tr;
			try {
				tr = session.getTransport(mailProtocol);
				log.info("210: Sent Message");
				tr.connect(mailHost, mailUser, mailPass);
				((SMTPMessage) msg2).setEnvelopeFrom(" ");
				msg2.saveChanges();
				if (skyDe) {
                    msg2.setFrom(new InternetAddress("noReply@sky.de"));
                } else {
                    msg2.setFrom(new InternetAddress("noReply@sky.at"));
                }
				tr.sendMessage(msg2, msg2.getAllRecipients());
				tr.close();
			} catch (Exception e) {
				log.info("210: Error: Message not sent " + e.getMessage());
				e.printStackTrace();
			}

		} catch(Exception e){
			log.info("210: Authentication Error." + e.getMessage());
			e.printStackTrace();
		}
	}

	protected String readEmailFromHtml(String filePath, Map<String, String> input)
	{
		String msg = readContentFromFile(filePath);
		try {
			Set<Entry<String, String>> entries = input.entrySet();
			for (Map.Entry<String, String> entry : entries) {
				msg = msg.replace(entry.getKey().trim(), entry.getValue().trim());
			}
		}catch(Exception exception){
			exception.printStackTrace();
		}
		return msg;
	}

	//Method to read HTML file as a String
	private String readContentFromFile(String fileName)
	{
		StringBuffer contents = new StringBuffer();
		try {
			//use buffering, reading one line at a time
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			try {
				String line = null;
				while (( line = reader.readLine()) != null){
					contents.append(line);
					contents.append(System.getProperty("line.separator"));
				}
			}
			finally {
				reader.close();
			}
		}
		catch (IOException ex){
			ex.printStackTrace();
		}
		return contents.toString();
	}
}


