package com.nttdata.de.ityx.sharedservices.image;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.archive.AbstractArchiveMetaData;
import com.nttdata.de.sky.archive.Archive;
import com.nttdata.de.sky.archive.CustomLine_Sky;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import com.nttdata.de.sky.outbound.MxOutboundIntegration;
import com.nttdata.de.sky.pdf.PdfFile;

import de.ityx.base.Global;
import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.api.IServerAPI;
import de.ityx.mediatrix.data.Answer;
import de.ityx.mediatrix.data.Email;
import de.ityx.mediatrix.data.Operator;
import de.ityx.mediatrix.data.OperatorLogRecord;
import de.ityx.mediatrix.data.Question;
import de.ityx.sky.outbound.extensions.template.ServerTemplateExtension;

/**
 * Created by meinusch on 20.07.15.
 */
public class PrintService {
	public static final int SKY_PROJECT_ID = 110;
	public static final int SBS_PROJECT_ID = 120;
	public static final String OUTBOUND_PROCESS = "820_Outbound";
	public static final String SBS_OUTBOUND_PROCESS = "SBS_820_Outbound";
	public static final String SKY_MASTER = "sky";
	public static final String SBS_MASTER = "sbs";

	protected static ServerTemplateExtension srvTmplExtension;
	protected static final String TMP_DIR = System.getProperty("java.io.tmpdir");

	protected final Long fuzzy_master_id = 1L;
	protected final static String fuzzy_namespace = System.getProperty("contex.fuzzy.namespace", "fuzzy_newdb");

	public PrintService() {
		synchronized (fuzzy_master_id) {
			srvTmplExtension = ServerTemplateExtension.getInstance();
		}
	}

	protected static void executePrintserviceTransferByEmail(Connection con, Email email,AbstractArchiveMetaData archiveMetaData,  boolean writeable) throws Exception {
		if (Question.class.isAssignableFrom(email.getClass())) {
			//AbstractArchiveMetaData archiveMetaData = ArchiveMetaDataUtils.getArchiveMetaData(con, (Question) email);
			executePrintserviceTransfer(con, (Question) email, null,archiveMetaData, writeable);
		} else if (Answer.class.isAssignableFrom(email.getClass())) {
			Answer answer = (Answer) email;
			Question question = API.getServerAPI().getQuestionAPI().load(con, answer.getQuestionId(), true);
			//AbstractArchiveMetaData archiveMetaData = ArchiveMetaDataUtils.getArchiveMetaData(con, question);
			executePrintserviceTransfer(con, question, answer, archiveMetaData,writeable);
		} else {
			String logPrefix = "PrintService#" + new Object() {
			}.getClass().getEnclosingMethod().getName() + ": ";
			SkyLogger.getMediatrixLogger().error(logPrefix + " notLetter Unknown emailClass:" + email.getClass() + " for Emailid:" + email.getEmailId());
		}
	}


	public static void executePrintserviceTransferByAnswerID(Connection con, int answerId , AbstractArchiveMetaData archiveMetaData, boolean writeable) throws Exception {
		Answer answer = API.getServerAPI().getAnswerAPI().load(con, answerId, true);
		Question question = API.getServerAPI().getQuestionAPI().load(con, answer.getQuestionId(), true);
		executePrintserviceTransfer(con, question, answer,archiveMetaData, writeable);
	}

	public static void executePrintserviceTransferByQuestionID(Connection con, int questionId,  AbstractArchiveMetaData archiveMetaData, boolean writeable) throws Exception {
		Question question = API.getServerAPI().getQuestionAPI().load(con, questionId, true);
		executePrintserviceTransfer(con, question, null,archiveMetaData, writeable);
	}

	public static void executePrintserviceTransfer(Connection con, Question question, Answer answer, AbstractArchiveMetaData archiveMetaData, boolean writeable) throws Exception {
		String logPrefix =  "PrintService#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ": ";

		//EntwicklungOnly
		int questionId = question.getId();
		Email email = question;
		Map<String, String> metaMap = archiveMetaData.collectMetadata(con, question);
		if (answer != null) {
			metaMap = archiveMetaData.collectMetadata(con, answer, metaMap);
			email = answer;
		}
		logStacktrace(logPrefix + " ##executePrintserviceTransfer## ", metaMap);
		if (!archiveMetaData.shouldBeArchived(metaMap)) {
			SkyLogger.getMediatrixLogger().info(logPrefix + "BusinessTest failed: Question shouldn't be archved: " + question.getId());
			return;
		}
		final boolean isSbsProject = question.getProjectId() == SBS_PROJECT_ID;
		if (archiveMetaData.isMetadataComplete(metaMap, questionId)) {
			String documentid = metaMap.get(TagMatchDefinitions.DOCUMENT_ID);

			// answer or ind.corr. question channel:Letter
			SkyLogger.getMediatrixLogger().debug("##executePrintserviceTransfer## Generate output Letter: qid:" + questionId + " DocumentID:" + documentid);
			metaMap.put(TagMatchDefinitions.CHANNEL, TagMatchDefinitions.Channel.BRIEF.toString());
			String exportPrefix = Global.getProperty("sky.outbound", TMP_DIR) + File.separator + documentid;
			long acurrentTimeMillis = System.currentTimeMillis();
			Archive archive = archiveMetaData.buildMetaDataXML(metaMap, isSbsProject,1);
			archiveMetaData.marshallArchive(archive, exportPrefix);
			SkyLogger.getMediatrixLogger().debug("OutboundRule: Funktion executePrintserviceTransfer_createXML took " + (System.currentTimeMillis() - acurrentTimeMillis) + " " + documentid);

			final HashMap<String, Object> parameter = CustomLine_Sky.getParameter(email, metaMap, documentid, false);
			final String key = "loginname";

			logStacktrace(logPrefix + "executePrintserviceTransfer: Stacktrace for Quesion:" + documentid, metaMap);

			if (parameter.get(key) == null) {
				IServerAPI serverAPI = API.getServerAPI();
				List<OperatorLogRecord> questionLog = serverAPI.getCommonAPI().loadQuestionLog(con, questionId);
				int operatorId = -1;
				for (OperatorLogRecord log : questionLog) {
					int action = log.getAction();
					if (action == OperatorLogRecord.ACTION_INFO) {
						operatorId = log.getOperatorId();
					}
				}
				String loginname = null;
				if (operatorId > 0) {
					final Operator operator = (Operator) serverAPI.getOperatorAPI().load(con, operatorId);
					if (operator != null) {
						loginname = operator.getUserId();
					} else {
						SkyLogger.getMediatrixLogger().warn("executePrintserviceTransfer: Could not load operator with id " + operatorId);
					}
				}
				parameter.put(key, loginname != null ? loginname : "");
				SkyLogger.getMediatrixLogger().debug("executePrintserviceTransfer: " + key + ", " + loginname);
			}
			acurrentTimeMillis = System.currentTimeMillis();

			String body=email.getBody();
			int tpID=question.getSubprojectId();//metaMap.get(TagMatchDefinitions.MX_TP_ID);
			int language=question.getLanguage(); //metaMap.get(TagMatchDefinitions.MX_LANGUAGE);

			PdfFile pdffile = new PdfFile(body, tpID, language, !isSbsProject, parameter);

			if (srvTmplExtension==null){
				synchronized (fuzzy_namespace) {
					srvTmplExtension = ServerTemplateExtension.getInstance();
				}
			}

			byte[] pdf = srvTmplExtension.createPdf(pdffile, con);
			SkyLogger.getMediatrixLogger().debug("OutboundRule: Funktion executePrintserviceTransfer_createPdf q:"+question.getId()+" tp:"+Integer.parseInt(metaMap.get(TagMatchDefinitions.MX_TP_ID))+" l:"+Integer.parseInt(metaMap.get(TagMatchDefinitions.MX_LANGUAGE))+" took " + (System.currentTimeMillis() - acurrentTimeMillis) + " " + documentid);
			acurrentTimeMillis = System.currentTimeMillis();
			File file = new File(exportPrefix + ".pdf");
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(pdf);
			fos.close();
			SkyLogger.getMediatrixLogger().debug("OutboundRule: Funktion executePrintserviceTransfer_writePdf took " + (System.currentTimeMillis() - acurrentTimeMillis) + " " + documentid);
			metaMap.put(MxOutboundIntegration.DOCPOOL_PARAMETER, TagMatchDefinitions.isNotSbsProject(question) ? OUTBOUND_PROCESS : SBS_OUTBOUND_PROCESS);
			MxOutboundIntegration.addCxOutboundDocumentProcessToDBQueue(con, metaMap, TagMatchDefinitions.isNotSbsProject(question) ? SKY_MASTER : SBS_MASTER);

			if (writeable && answer != null && answer.getSendTime() == 0) {
				long lastActivityTime = getLastActivityTime(con, questionId);
				if (lastActivityTime > 0) {
					answer.setSendTime(lastActivityTime);
					metaMap.put(AbstractArchiveMetaData.TIMESTAMP_CREATED, AbstractArchiveMetaData.getFormattedTimestamp(new Date(lastActivityTime)));
				}
				answer.setStatus(Answer.S_COMPLETE);
				API.getServerAPI().getAnswerAPI().store(con, answer);
			}
		} else {
			//logStacktrace(logPrefix + " ##executePrintserviceTransfer##<<NOARCHIVE>>", metaMap);
			SkyLogger.getMediatrixLogger().error(logPrefix + "executePrintserviceTransfer##<<NOARCHIVE>>: Can not send Answer. Missing required archive metadata for question: " + questionId);
			metaMap.put(MxOutboundIntegration.PROCSTATUS, MxOutboundIntegration.MXOUT_STATUS.METAERR.name());
			metaMap.put(MxOutboundIntegration.DOCPOOL_PARAMETER, TagMatchDefinitions.isNotSbsProject(question) ? OUTBOUND_PROCESS : SBS_OUTBOUND_PROCESS);
			long mxcxid= MxOutboundIntegration.addCxOutboundDocumentProcessToDBQueue(con, metaMap, TagMatchDefinitions.isNotSbsProject(question) ? SKY_MASTER : SBS_MASTER);
			MxOutboundIntegration.updateDbStatusOfCxOutboundDocumentQueue(con, mxcxid, MxOutboundIntegration.MXOUT_STATUS.METAERR,"Metadata not complete");
			throw new java.lang.Exception("SendingLetter for Question id:" + question.getId() + " not possible. Metadata not complete");
		}
	}

	protected static void logStacktrace(String description, Map<String, String> metaMap) {
		if (SkyLogger.getMediatrixLogger().isDebugEnabled()) {
			String message = description;
			if (metaMap != null) {
				String questionId = metaMap.get(TagMatchDefinitions.MX_QUESTIONID);
				String answerId = metaMap.get(TagMatchDefinitions.MX_ANSWERID);

				String questionDocId = metaMap.get(TagMatchDefinitions.MX_QUESTIONDOCUMENTID);
				String docId = metaMap.get(TagMatchDefinitions.DOCUMENT_ID);
				String questionIdM = questionId != null ? " Quesion: " + questionId : "";
				String answerIdM = answerId != null ? " Answer: " + answerId : "";
				String direction = metaMap.get(TagMatchDefinitions.MX_DIRECTION);
				String directionM = direction != null ? " Direction: " + direction : "";

				String questionDocIdM = questionDocId != null ? " QuesionDocID: " + questionDocId : "";

				message = docId + " " + questionIdM + questionDocIdM + answerIdM + directionM + " " + message;
			}

			String stacktrace = "PrintService# " + message + " Stacktrace: \r\n";
			for (StackTraceElement e : Thread.currentThread().getStackTrace()) {
				if (e.getClassName().contains("nttdata")) {
					stacktrace += " => " + e.getClassName() + ">" + e.getMethodName() + ":" + e.getLineNumber() + "\r\n";
				}
			}
			SkyLogger.getMediatrixLogger().debug(stacktrace);
		}
	}



	public static long getLastActivityTime(Connection con, int questionid) {
		String logPrefix = "OutboundRule#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ": ";
		ResultSet rs = null;
		try {
			rs = con.createStatement().executeQuery("select max(zeit) zeit from mitarbeiterlog where frageid=" + questionid + " ");
			if (rs.next() && rs.getLong("zeit") > 0) {
				return rs.getLong("zeit");
			}
		} catch (SQLException e) {
			SkyLogger.getMediatrixLogger().error(logPrefix + " ##isQuesionForwarded## Problem accessing mitarbeiterlog for: " + questionid, e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					SkyLogger.getMediatrixLogger().debug(logPrefix + " ##isQuesionForwarded## Problem closing mitarbeiterlog rs: " + questionid, e);
				}
			}
		}
		return 0;
	}
}
