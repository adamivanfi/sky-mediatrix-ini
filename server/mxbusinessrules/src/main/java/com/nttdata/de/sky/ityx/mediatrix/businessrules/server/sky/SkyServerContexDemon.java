package com.nttdata.de.sky.ityx.mediatrix.businessrules.server.sky;

import com.nttdata.de.ityx.sharedservices.utils.ShedulerUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.lib.utils.MitarbeiterlogWriter;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.contex.data.icat.Category;
import de.ityx.contex.impl.document.text.string.StringDocument;
import de.ityx.contex.impl.mcat.MCatFlowObject;
import de.ityx.contex.interfaces.document.CDocument;
import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.api.interfaces.businessrules.server.IServerContexDemon;
import de.ityx.mediatrix.data.Email;
import de.ityx.mediatrix.data.Filter;
import de.ityx.mediatrix.data.Project;
import de.ityx.mediatrix.data.Question;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class SkyServerContexDemon extends InboundRule implements IServerContexDemon {

	@Override
	public boolean add(Connection con, Question question, String doctype, HashMap<String, String> hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + " q:" + question.getId() + " d:" + question.getDocId() + " ";
		SkyLogger.getMediatrixLogger().info(logPrefix + " Start");



		final int subprojectId = question.getSubprojectId();
		try {
			question.setProjectId(API.getServerAPI().getSubprojectAPI().load(con, subprojectId, true).getProjectId());
			if ( question.getStatus().equals(Question.S_CLASSIFICATION)
					|| question.getStatus().equals(Question.S_CLASSIFICATION_REQUEUE)
					|| question.getStatus().equals(Question.S_CLASSIFICATION_FORWARD)
					|| question.getStatus().equals(Question.S_CLASSIFIED)
					|| question.getStatus().equals(Question.S_EXTRACTION)
					|| question.getStatus().equals(Question.S_EXTRACTION_REQUEUE)
					|| question.getStatus().equals(Question.S_EXTRACTION_FORWARD)
					|| question.getStatus().equals(Question.S_EXTRACTED)
					|| question.getStatus().equals(Question.S_HOLD)
					|| question.getStatus().equals(Question.S_HOLD_EXTRACTION)
					|| question.getStatus().equals(Question.S_HOLD_CLASSIFICATION)
					|| question.getStatus().equals(Question.S_HOLD_EXTRACTION_REQUEUE)
					|| question.getStatus().equals(Question.S_HOLD_CLASSIFICATION_REQUEUE)
					) {
				//	ShedulerUtils.checkRuntimeLicense("SkyServer:" + question.getDocId() + ":" + question.getId());
				MitarbeiterlogWriter.writeMitarbeiterlog(question.getOperatorId(), question.getId(), 0, 19, SkyServerObjectsControl.DWH + " TP-ID: " + subprojectId, System.currentTimeMillis(), false);
			}
		} catch (Exception e) {
			ShedulerUtils.resetAuth("SkyServer:" + question.getDocId() + ":" + question.getId());
			if (question.getStatus().equals(Question.S_CLASSIFICATION)
					|| question.getStatus().equals(Question.S_CLASSIFICATION_REQUEUE)
					|| question.getStatus().equals(Question.S_CLASSIFICATION_FORWARD)
					|| question.getStatus().equals(Question.S_CLASSIFIED)
					|| question.getStatus().equals(Question.S_EXTRACTION)
					|| question.getStatus().equals(Question.S_EXTRACTION_REQUEUE)
					|| question.getStatus().equals(Question.S_EXTRACTION_FORWARD)
					|| question.getStatus().equals(Question.S_EXTRACTED)
					|| question.getStatus().equals(Question.S_HOLD)
					|| question.getStatus().equals(Question.S_HOLD_EXTRACTION)
					|| question.getStatus().equals(Question.S_HOLD_CLASSIFICATION)
					|| question.getStatus().equals(Question.S_HOLD_EXTRACTION_REQUEUE)
					|| question.getStatus().equals(Question.S_HOLD_CLASSIFICATION_REQUEUE)
					) {
				//	ShedulerUtils.checkRuntimeLicense("SkyServer:" + question.getDocId() + ":" + question.getId());
				MitarbeiterlogWriter.writeMitarbeiterlog(question.getOperatorId(), question.getId(), 0, 19, SkyServerObjectsControl.DWH + " TP-ID: " + subprojectId, System.currentTimeMillis(), false);
			}
			SkyLogger.getMediatrixLogger().error(logPrefix + e.getMessage(), e);
		}
		try {
			initializeQuestion(con, question, true);
			//logValidationResult(con, question); This call already exists in initializeQuestion
			question.setSmtpDate(System.currentTimeMillis());
		} catch (Exception e) {
			SkyLogger.getMediatrixLogger().warn(logPrefix + " Problem in initializeQuestion or logValidationResult:" + e.getMessage(), e);
		}

		String archiveFlag = TagMatchDefinitions.extractXTMHeader(question.getHeaders(), TagMatchDefinitions.ARCHIVE_FLAG);
		if (archiveFlag != null && (archiveFlag.equalsIgnoreCase("true") || archiveFlag.equalsIgnoreCase(Question.S_MONITORED)|| archiveFlag.equalsIgnoreCase(SkyRule.AUTOARCHIVE_INITIALIZED))) {
			SkyLogger.getMediatrixLogger().warn(logPrefix + " AutoCloseQuestion");
			String customerid = TagMatchDefinitions.extractXTMHeader(question.getHeaders(), TagMatchDefinitions.CUSTOMER_ID);
			if (customerid == null || customerid.isEmpty() || customerid.trim().equals("0")) {
				SkyLogger.getMediatrixLogger().info(logPrefix + ": skiping autoclose because of empty customerid:q:" + question.getId() );
				try {
					MitarbeiterlogWriter.writeMitarbeiterlog(question, "Not possible to close question automatically: Empty CustomerID", 19, 0);
				} catch (Exception e) {
					ShedulerUtils.resetAuth("IR" + question.getId());
					MitarbeiterlogWriter.writeMitarbeiterlog(question, "Not possible to close question automatically: Empty CustomerID", 19, 0);
				}
			} else {
				if (archiveFlag.equalsIgnoreCase(Question.S_MONITORED)) {
					autoClose(con, question, Question.S_MONITORED);
					SkyLogger.getMediatrixLogger().warn(logPrefix + " AutoCloseQuestion:Monitored");
				} else {
					autoClose(con, question, Question.S_COMPLETED);
					SkyLogger.getMediatrixLogger().warn(logPrefix + " AutoCloseQuestion:Completed");
				}
			}
		}
		SkyLogger.getMediatrixLogger().info(logPrefix + " Finish");
		
		try {
				SkyLogger.getCommonLogger().info("Inbound.SCDStore1 Generated docid:" + question.getDocId() + " frage:" + question.getId()+" status:"+question.getStatus());
				String statusbak=question.getStatus();
				if (question.getStatus().equals(Question.S_BLOCKED)){
					question.setStatus(Question.S_NEW);
				}
				boolean questionstoreok = API.getServerAPI().getQuestionAPI().store(con, question);
				question.setStatus(statusbak);
				SkyLogger.getCommonLogger().info("Inbound.SCDStore2 Generated docid:" + question.getDocId() + " frage:" + question.getId()+" sok:"+questionstoreok);
		} catch (SQLException e) {
				SkyLogger.getCommonLogger().warn("Inbound.SCDStoreERR Generated docid:" + question.getDocId() + " frage:" + question.getId() + " err" + e.getMessage());
		}
		
		try {
			if (!con.getAutoCommit()) {
				con.commit();
				SkyLogger.getMediatrixLogger().info(logPrefix+ "CommitedReportingEntry");
			} else {
				SkyLogger.getMediatrixLogger().info(logPrefix + "Autocomnit" );
			}
		} catch (SQLException e) {
			SkyLogger.getMediatrixLogger().error(logPrefix+"Commit " + e.getMessage(), e);
		}

		return true;
	}

	@Override
	public boolean delete(Connection con, Question question, String doctype, HashMap<String, Object> hm) {
		return true;
	}

	@Override
	public boolean update(Connection con, Question question, String doctype, HashMap<String, Object> hm) {
		return true;
	}

	@Override
	public boolean addArchive(Connection con, Question question, String doctype, HashMap<String, Object> hm) {
		return true;
	}

	
	/*
		 * is called after a email has been categorized
		 */
	@Override
	public void postCategorize(java.sql.Connection con, Category[] cats, long master, Filter filter, Email question, Project project) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + " e:" + question.getEmailId() + " ";
		SkyLogger.getMediatrixLogger().debug(logPrefix + "PostCategorize");

	}

	/*
	 * is called after a email has been extracted
	 */
	@Override
	public void postExtraction(java.sql.Connection con, ArrayList<de.ityx.contex.interfaces.extag.TagMatch> tagmatches, long master, Filter filter, Email question, Project project) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + " e:" + question.getEmailId() + " ";
		SkyLogger.getMediatrixLogger().debug(logPrefix + "postExtraction: ");

	}


	/*
	 * is called after a email has been categorized
	 */
	@Override
	public void postMcategorize(java.sql.Connection con, Category[] cats, MCatFlowObject flow, long master, Filter filter, Email question, Project project) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + " e:" + question.getEmailId() + " ";
		SkyLogger.getMediatrixLogger().debug(logPrefix + "postMcategorize: ");

	}

	/*
	 * is called after a process has been called
	 */
	@Override
	public void postProcess(java.sql.Connection con, de.ityx.contex.interfaces.designer.IParameterMap map, Filter filter, Email question, Project project) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + " e:" + question.getEmailId() + " ";
		SkyLogger.getMediatrixLogger().debug(logPrefix + "postProcess: " + question.getEmailId());

	}

	/*
	 * is called before a email will be categorized
	 */
	@Override
	public void preCategorize(java.sql.Connection con, CDocument doc, long master, Filter filter, Email question, Project project) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + " e:" + question.getEmailId() + " ";
		SkyLogger.getMediatrixLogger().debug(logPrefix + "preCategorize: " + question.getEmailId());

	}

	/*
	 * is called before a email will be extracted
	 */
	@Override
	public void preExtraction(java.sql.Connection con, StringDocument doc, long master, Filter filter, Email question, Project project) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + " e:" + question.getEmailId() + " ";
		SkyLogger.getMediatrixLogger().debug(logPrefix + "preExtraction: " + question.getEmailId());

	}

	/*
	 * is called before a email will be categorized
	 */
	@Override
	public void preMcategorize(java.sql.Connection con, CDocument doc, MCatFlowObject flow, long master, Filter filter, Email question, Project project) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + " e:" + question.getEmailId() + " ";
		SkyLogger.getMediatrixLogger().debug(logPrefix + "preMcategorize: " + question.getEmailId());

	}

	/*
	 * is called before a process will be called
	 */
	@Override
	public void preProcess(java.sql.Connection con, de.ityx.contex.impl.document.CDocumentContainer<de.ityx.contex.interfaces.document.CDocument> container, Filter filter, Email question, Project project) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + " e:" + question.getEmailId() + " ";
		SkyLogger.getMediatrixLogger().debug(logPrefix + "preProcess: " + question.getEmailId());

	}


}
