package de.ityx.sky.outbound.common;

import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.archive.BaseUtils;
import com.nttdata.de.sky.archive.Constant;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import com.nttdata.de.sky.pdf.PdfFile;
import de.ityx.base.Global;
import de.ityx.contex.security.util.ContexSecurityTool;
import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.data.*;
import de.ityx.mediatrix.modules.tools.logger.Log;
import de.ityx.sky.outbound.extensions.template.ServerTemplateExtension;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ServerUtils {

	public static void createPdf(Connection con, Email email) throws Exception {
		if (email.getType() == Email.TYPE_LETTER || email.getType() == Email.TYPE_FAX) {
			if (email.getSubject().trim().length() == 0) {
				email.setSubject(Constant.LETTER_SUBJECT);
			}
			if (email instanceof Answer) {
				Answer answer = (Answer) email;
				Question question = API.getServerAPI().getQuestionAPI().load(con, answer.getQuestionId(), false);
				byte[] pdf = ServerTemplateExtension.getInstance().createPdf(new PdfFile(answer.getBody(), question.getSubprojectId(), question.getLanguage(), !TagMatchDefinitions.isSbsProject(question), getParameter(con, answer)), con);
				insertServerAttachments(con, answer, pdf);
				API.getServerAPI().getAnswerAPI().store(con, answer);

			} else if (email instanceof Question) {
				Question question = (Question) email;
				byte[] pdf = ServerTemplateExtension.getInstance().createPdf(new PdfFile(question.getBody(), question.getSubprojectId(), question.getLanguage(), !TagMatchDefinitions.isSbsProject(question) , getParameter(con, question)), con);
				insertServerAttachments(con, question, pdf);
				SkyLogger.getCommonLogger().info("SU.QStore1 Generated docid:" + question.getDocId() + " frage:" + question.getId());
				API.getServerAPI().getQuestionAPI().store(con, question);
				SkyLogger.getCommonLogger().info("SU.QStore2 Generated docid:" + question.getDocId() + " frage:" + question.getId());

			}
		}
	}

	private static final Long serialVersionUID=7853348339962583772L;

	/**
	 * Completes & stores the mail.
	 * Sets the send time by the answer.
	 * Protocols the send action in the operator log.
	 *
	 * @param con     the current connection
	 * @param email   the current mail
	 * @param sendlog true if the sent action is to be inserted into the operator log, false otherwise
	 * @throws SQLException
	 */
	public static void complete(Connection con, Email email, boolean sendlog) throws SQLException {
		Question question = null;
		Answer answer = null;
		if (email instanceof Answer) {
			answer = (Answer) email;
			question = API.getServerAPI().getQuestionAPI().load(con, answer.getQuestionId(), false);
			answer.setStatus(Question.S_COMPLETED);
			answer.setSendTime(System.currentTimeMillis()); // Used to protocol the send time. The answer time is viewed in the history.
			API.getServerAPI().getAnswerAPI().store(con, answer);
			Log.message("monitoring answer: " + answer.getId());
		} else if (email instanceof Question) {
			question = (Question) email;
			question.setStatus(Question.S_COMPLETED);
			Log.message("monitoring question: " + question.getId());
		}
		if (Global.getOperatorLogLength() < 20) {
			synchronized (serialVersionUID) {
				Global.initializeConfigurationForUnitIfNotDoneYet("DEFAULT");
				Global.setOperatorLogLength(2040);
			}
		}
		try {
			if (question != null && question.getId() > 0) {
				SkyLogger.getCommonLogger().info("SU.QStore1b Generated docid:" + question.getDocId() + " frage:" + question.getId());
				API.getServerAPI().getQuestionAPI().store(con, question);
				SkyLogger.getCommonLogger().info("SU.QStore2b Generated docid:" + question.getDocId() + " frage:" + question.getId());
				if (sendlog) {
					API.getServerAPI().getQuestionAPI().writeLog(con, email.getOperatorId(), question.getId(), (answer != null ? answer.getId() : 0), 1, "", System.currentTimeMillis(), false);
				}
			}
		} catch (SQLException e) {
			if (question != null && question.getId() > 0) {
				ContexSecurityTool.authenticateAsSystemUser();
				SkyLogger.getCommonLogger().info("SU.QStore1a Generated docid:" + question.getDocId() + " frage:" + question.getId());
				API.getServerAPI().getQuestionAPI().store(con, question);
				SkyLogger.getCommonLogger().info("SU.QStore2a Generated docid:" + question.getDocId() + " frage:" + question.getId());

				if (sendlog) {
					API.getServerAPI().getQuestionAPI().writeLog(con, email.getOperatorId(), question.getId(), (answer != null ? answer.getId() : 0), 1, "", System.currentTimeMillis(), false);
				}
			}
		}
	}

	public static void requeue(Connection con, Email email) throws SQLException {
		Question question = null;
		if (email instanceof Answer) {
			Answer answer = (Answer) email;
			question = API.getServerAPI().getQuestionAPI().load(con, answer.getQuestionId(), false);

		} else if (email instanceof Question) {
			question = (Question) email;
		}
		if (question != null) {
			question.setReserved(true);
			SkyLogger.getCommonLogger().info("SU.QStore1 Generated docid:" + question.getDocId() + " frage:" + question.getId());
			API.getServerAPI().getQuestionAPI().store(con, question);
			SkyLogger.getCommonLogger().info("SU.QStore2 Generated docid:" + question.getDocId() + " frage:" + question.getId());

		}
	}

	private static void insertServerAttachments(Connection con, Email email, byte[] pdf) throws SQLException {
		Attachment att = new Attachment();
		att.setClientFilename(Constant.PDF_FEEDBACK);
		att.setFilename(Constant.PDF_FEEDBACK);
		att.setContentType(Constant.PDF_CONTENTTYPE);
		att.setBuffer(pdf);
		att.setEmailId(email.getEmailId());
		List<Attachment> atti = new ArrayList<Attachment>();
		for (Attachment a : email.getAttachments()) {
			if (a.getClientFilename().equalsIgnoreCase(Constant.PDF_FEEDBACK) || a.getFilename().equalsIgnoreCase(Constant.PDF_FEEDBACK)) {
				API.getServerAPI().getAttachmentAPI().delete(con, a);
			} else {
				atti.add(a);
			}
		}
		API.getServerAPI().getAttachmentAPI().store(con, att);
		email.setAttachments(atti);
		email.addAttachment(att);
	}

	public static void archive(Connection con, Email email) {
		if (email instanceof Answer) {
			Log.message("answer sent: " + ((Answer) email).getId());
		} else if (email instanceof Question) {
			Log.message("question sent: " + ((Question) email).getId());
		}
	}

	public static HashMap<String, Object> getParameter(Connection con, Question question) throws Exception {
		Customer customer = API.getServerAPI().getCustomerAPI().load(con, API.getServerAPI().getCaseAPI().load(con, question.getCaseId()).getCustomerId());
		HashMap<String, Object> parameter = BaseUtils.getParameter(question.getId(), customer);
		parameter.put("embedded_att", question.getAttachments());
		return parameter;
	}

	public static HashMap<String, Object> getParameter(Connection con, Answer answer) throws Exception {
		Question question = API.getServerAPI().getQuestionAPI().load(con, answer.getQuestionId(), true);
		Customer customer = API.getServerAPI().getCustomerAPI().load(con, API.getServerAPI().getCaseAPI().load(con, question.getCaseId()).getCustomerId());
		HashMap<String, Object> parameter = BaseUtils.getParameter(question.getId(), customer);
		parameter.put("embedded_att", answer.getAttachments());
		return parameter;
	}

}
