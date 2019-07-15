package com.nttdata.de.sky.ityx.mediatrix.businessrules.server.sky;

import com.nttdata.de.ityx.sharedservices.utils.ShedulerUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.lib.utils.MitarbeiterlogWriter;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.licensing.exception.LicensingException;
import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.api.interfaces.businessrules.server.IServerObjectsControl;
import de.ityx.mediatrix.data.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import static com.nttdata.de.sky.ityx.common.TagMatchDefinitions.SEPA_MANDATE;

public class SkyServerObjectsControl extends InboundRule implements IServerObjectsControl {

	public static final String DWH = "DWH";
    public static final String SEPA_MANDAT_AUTOMAT_VERARBEITUNG = "[SEPA-Mandat automat. Verarbeitung] ";

    /*
     * Will be called after an attachment has been deleted
     */
	public HashMap postAttachmentDelete(java.sql.Connection con, de.ityx.mediatrix.data.Attachment attachment, HashMap hm) {
		return hm;
	}

	/*
	 * Will be called after an attachment has been stored
	 */
	public HashMap postAttachmentStore(java.sql.Connection con, de.ityx.mediatrix.data.Attachment attachment, HashMap hm) {
		return hm;
	}

	/*
	 * Is called before an attachment will be deleted
	 */
	public HashMap preAttachmentDelete(java.sql.Connection con, de.ityx.mediatrix.data.Attachment attachment, HashMap hm) {
		return hm;
	}

	/*
	 * Is called before an attachment will be loaded by id
	 */
	public HashMap preAttachmentLoad(java.sql.Connection con, int attachmentId, HashMap hm) {
		return hm;
	}

	/*
	 * Is called before an attachment will be loaded
	 */
	public HashMap preAttachmentLoad(java.sql.Connection con, de.ityx.mediatrix.data.Attachment attachment, HashMap hm) {
		return hm;
	}

	/*
	 * Is called before an attachment will be stored
	 */
	public HashMap preAttachmentStore(java.sql.Connection con, de.ityx.mediatrix.data.Attachment attachment, HashMap hm) {
		return hm;
	}

	/*
	 * Will be called after a question has been stored
	 *
	 * Fill Extra Columns: micolumn.3=Kundennummer micolumn.4=Dokumentenart
	 * micolumn.5=SMC beiliegend micolumn.6=ToAddress
	 */
	public HashMap postQuestionStore(java.sql.Connection con, Question question, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getMediatrixLogger().debug(logPrefix + " call postQuestionStore:1: q.Id:" + (question != null ? question.getId() : ""));
		initializeQuestion(con, question, false);
		if (question != null) {
			SkyLogger.getMediatrixLogger().debug(logPrefix + " call postQuestionStore:2: q.Id:" + question.getId()+"; q.SubprojectId: "+question.getSubprojectId());
            String headers = question.getHeaders();
			final int questionId = question.getId();
			int operatorId = question.getLockedBy();
			final String status = question.getStatus();
            String formtype = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.X_TAGMATCH_FORM_TYPE);

			SkyLogger.getMediatrixLogger().debug(logPrefix +
					" call postQuestionStore:3: q.Id:" + question.getId()+"; q.SubprojectId: "+question.getSubprojectId() + "; q.status:" + question.getStatus()+
					"; q.LockedBy: " + question.getLockedBy() + "; q.LockedAt: " + question.getLockedAt()+"; ");
            if (status != null && status.equals(Question.S_NEW) && operatorId == 0 &&
                    question.getSubject() != null && question.getSubject().contains(SEPA_MANDAT_AUTOMAT_VERARBEITUNG) &&
                    formtype!=null && formtype.equalsIgnoreCase(SEPA_MANDATE)){
                try {
                    ShedulerUtils.checkAuth();
                    question.setLockedBy(2);
                    question.setLockedAt(System.currentTimeMillis());
                    API.getServerAPI().getQuestionAPI().store(con, question);
                } catch (SQLException | LicensingException ee) {
                    SkyLogger.getMediatrixLogger().error(logPrefix + " Setting Lock is not possible!  msg:" + ee.getMessage(),ee);
                }
            }

			if (status != null && status.equals(Question.S_COMPLETED)) {
				try {
					ResultSet rs = con.createStatement().executeQuery("select 1 from mitarbeiterlog where frageid=" + questionId + " and aktion=" + Integer.toString(OperatorLogRecord.ACTION_INFO) + " and parameter='" + DWH + "'");
					Boolean addLog = !rs.next();
					rs.close();
					if (addLog) {
						if (operatorId < 10) {
							rs = con.createStatement().executeQuery("select mitarbeiterid, aktion, zeit from mitarbeiterlog where frageid=" + questionId + " and aktion=" + Integer.toString(OperatorLogRecord.ACTION_INFO) + " and mitarbeiterid>2 order by zeit desc");
							if (rs.next()) {
								operatorId = rs.getInt("mitarbeiterid");
							}
							rs.close();
						}
						MitarbeiterlogWriter.writeMitarbeiterlog(operatorId, questionId, 0, OperatorLogRecord.ACTION_INFO, DWH, System.currentTimeMillis(), false);
					}
				} catch (Exception e) {
					SkyLogger.getMediatrixLogger().error(logPrefix + ": " + "Could not write log for completed question with id <" + questionId + "> with error: " + e.getMessage(), e);
				}
				if (operatorId == 2 &&
						question.getSubject() != null && question.getSubject().contains(SEPA_MANDAT_AUTOMAT_VERARBEITUNG) &&
						formtype!=null && formtype.equalsIgnoreCase(SEPA_MANDATE)) {
					try {
						ShedulerUtils.checkAuth();
						question.setLockedBy(0);
						question.setLockedAt(0);
						API.getServerAPI().getQuestionAPI().store(con, question);
					} catch (SQLException | LicensingException ee) {
						SkyLogger.getMediatrixLogger().error(logPrefix + " Setting Unlock is not possible!  msg:" + ee.getMessage(), ee);
					}
				}
			}

			for (Attachment att : question.getAttachments()) {
				if (att.getId() == 0) {
					att.setEmailId(question.getEmailId());
					try {
						API.getServerAPI().getAttachmentAPI().store(con, att);
					} catch (SQLException e) {
						SkyLogger.getMediatrixLogger().error(logPrefix + ": " + e.getMessage() + e.getCause(), e);
					}
				}
			}
			SkyLogger.getMediatrixLogger().debug(logPrefix +
						" call postQuestionStore:FINISH: q.Id:" + question.getId() + "; q.SubprojectId: "+question.getSubprojectId()  + "; q.status:" + question.getStatus()+
						"; q.LockedBy: " + question.getLockedBy() + "; q.LockedAt: " + question.getLockedAt());
		}
		return hm;
	}

	/*
	 * Is called before a question will be stored
	 */
	public HashMap preQuestionStore(java.sql.Connection con, Question question, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name + " q:" + (question != null ? question.getId() : "") + " ";
		SkyLogger.getMediatrixLogger().debug(logPrefix + " call preQuestionStore:1:");
		//ShedulerUtils.checkAuth();
		//ShedulerUtils.checkRuntimeLicense("preQS q:" + (question != null ? question.getId() : ""));
		ShedulerUtils.quickCheckLicence("pqs" + (question!=null?question.getId():"0") + ":" + question.getDocId());
		SkyLogger.getMediatrixLogger().debug(logPrefix + " call preQuestionStore:2:");
		/*if (question.getAccountName() == null || question.getAccountName().isEmpty() //|| question.getAccountName().equalsIgnoreCase("Externe Mailinbox")
				) {
			try {
				question.setAccountName(API.getServerAPI().getAccountAPI().load(con, 110).getName());
			} catch (SQLException e) {
				SkyLogger.getMediatrixLogger().error(logPrefix + e.getMessage(), e);
			}
			SkyLogger.getMediatrixLogger().info(logPrefix + " call preQuestionStore:3: DefaultKonto");
		}else{
			SkyLogger.getMediatrixLogger().info(logPrefix+" call preQuestionStore:3: Konto:"+question.getAccountName());
		}*/



		//SkyLogger.getMediatrixLogger().debug(logPrefix + " call preQuestionStore:3:" + (question != null ? question.getId() : ""));

		initializeQuestion(con, question, false);
		SkyLogger.getMediatrixLogger().debug(logPrefix + " call preQuestionStore:4:" + (question != null ? question.getId() : ""));


		return hm;
	}

	/*
	 * Will be called after a case has been stored
	 */

	public HashMap postCaseStore(java.sql.Connection con, Case ccase, HashMap hm) {
		return hm;
	}

	/*
	 * Is called before a case will be stored
	 */
	public HashMap preCaseStore(java.sql.Connection con, Case ccase, HashMap hm) {
		return hm;
	}

	/*
	 * Will be called after an answer has been deleted
	 */
	public HashMap postAnswerDelete(java.sql.Connection con, Answer answer, HashMap hm) {
		return hm;
	}

	/*
	 * Will be called after an answer has been loaded
	 */
	public HashMap postAnswerLoad(java.sql.Connection con, Answer answer, HashMap hm) {
		return hm;
	}

	/*
	 * Will be called after an answer has been loaded
	 */
	public HashMap postAnswerLoadByEmailid(java.sql.Connection con, Answer answer, HashMap hm) {
		return hm;
	}

	/*
	 * Will be called after an answer has been stored
	 */
	public HashMap postAnswerStore(java.sql.Connection con, Answer answer, HashMap hm) {
		return hm;
	}

	/*
	 * Will be called after an attachment has been loaded
	 */
	public HashMap postAttachmentLoad(java.sql.Connection con, de.ityx.mediatrix.data.Attachment attachment, HashMap hm) {
		return hm;
	}

	/*
	 * Will be called after a case has been deleted
	 */
	public void postCaseDelete(java.sql.Connection con, List<java.lang.Integer> customerIds, HashMap<String, Object> hm) {

	}

	@Override
	public void preReportStore(Connection connection, ScheduledReport scheduledReport) {

	}

	@Override
	public void postReportStore(Connection connection, ScheduledReport scheduledReport) {

	}

	@Override
	public void preReportLoad(Connection connection, int i) {

	}

	@Override
	public void postReportLoad(Connection connection, ScheduledReport scheduledReport) {

	}

	/*
	 * Will be called after a case has been loaded
	 */
	public HashMap postCaseLoad(java.sql.Connection con, Case ccase, HashMap hm) {
		return hm;
	}

	/*
	 * Will be called after a customer has been deleted
	 */
	public HashMap postCustomerDelete(java.sql.Connection con, List<java.lang.Integer> customerIds, HashMap<String, Object> hm) {
		return hm;
	}

	/*
	 * Will be called after a customer has been loaded
	 */
	public HashMap postCustomerLoad(java.sql.Connection con, Customer customer, HashMap hm) {
		return hm;
	}

	/*
	 * Will be called after a customer has been stored
	 */
	public HashMap postCustomerStore(java.sql.Connection con, Customer customer, HashMap hm) {
		return hm;
	}

	/*
	 * Will be called after an operator has been loaded
	 */
	public HashMap postOperatorLoad(java.sql.Connection con, Operator operator, HashMap hm) {
		return hm;
	}

	/*
	 * Will be called after an operator has been stored
	 */
	public HashMap postOperatorStore(java.sql.Connection con, Operator operator, HashMap hm) {
		return hm;
	}

	/*
	 * Will be called after a profile has been deleted
	 */
	public HashMap postProfileDelete(java.sql.Connection con, Profile profile, HashMap hm) {
		return hm;
	}

	/*
	 * Will be called after a profile has been loaded
	 */
	public HashMap postProfileLoad(java.sql.Connection con, Profile profile, HashMap hm) {
		return hm;
	}

	/*
	 * Is called before a profile will be stored
	 */
	public HashMap postProfileStore(java.sql.Connection con, Profile profile, HashMap hm) {
		return hm;
	}

	/*
	 * Will be called after a project has been deleted
	 */
	public HashMap postProjectDelete(java.sql.Connection con, Project project, HashMap hm) {
		return hm;
	}

	/*
	 * Will be called after a project has been loaded
	 */
	public HashMap postProjectLoad(java.sql.Connection con, Project project, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getMediatrixLogger().debug(logPrefix + " call:1:");
		return hm;
	}

	/*
	 * Will be called after a project has been stored
	 */
	public HashMap postProjectStore(java.sql.Connection con, Project project, HashMap hm) {
		return hm;
	}

	/*
	 * Will be called after a question has been deleted
	 */
	public HashMap postQuestionDelete(java.sql.Connection con, Question question, HashMap hm) {
		return hm;
	}

	/*
	 * Will be called after a question has been loaded
	 */
	public HashMap postQuestionLoad(java.sql.Connection con, Question question, HashMap hm) {
		/*Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		try {
			String logPrefix = clazz.getName() + "#" + name + " q:" + (question != null ? question.getId() : "") + " ";
			SkyLogger.getMediatrixLogger().debug(logPrefix + ": con " + con.isClosed() + ", question " + (question != null ? question.getId() : ""));
		} catch (SQLException e) {
			String logPrefix = clazz.getName() + "#" + name + " q:" + (question != null ? question.getId() : "") + " ";
			SkyLogger.getMediatrixLogger().error(logPrefix + " question " + (question != null ? question.getId() : "") + " m:" + e.getMessage(), e);
		}*/
		return hm;
	}

	/*
	 * Will be called after a lock has been released
	 */
	public HashMap postReleaseLock(java.sql.Connection con, de.ityx.mediatrix.data.base.Lock lock, int operatorId, String sessionid, Long currentTimeMillis, HashMap hm) {
		return hm;
	}

	/*
	 * Will be called after a lock has been set
	 */
	public HashMap postSetLock(java.sql.Connection con, de.ityx.mediatrix.data.base.Lock lock, int operatorId, String sessionid, Long currentTime, de.ityx.mediatrix.data.base.LockResult lockResult, boolean opMode, HashMap hm) {
		return hm;
	}

	/*

*/
	public HashMap postSubprojectDelete(java.sql.Connection con, Subproject subproject, HashMap hm) {
		return hm;
	}

	/*
	 * Will be called after a subproject has been loaded
	 */
	public HashMap postSubprojectLoad(java.sql.Connection con, Subproject subproject, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getMediatrixLogger().debug(logPrefix);
		return hm;
	}

	/*
	 * Will be stored after a subproject has been stored
	 */
	public HashMap postSubprojectStore(java.sql.Connection con, Subproject subproject, HashMap hm) {
		return hm;
	}

	/*
	 * Is called before an answer will be deleted
	 */
	public HashMap preAnswerDelete(java.sql.Connection con, Answer answer, HashMap hm) {
		return hm;
	}

	/*
	 * Is Called before an answer will be loaded
	 */
	public HashMap preAnswerLoad(java.sql.Connection con, int answerId, HashMap hm) {
		return hm;
	}

	/*
	 * Is called before an answer will be loaded by the id of an email
	 */
	public HashMap preAnswerLoadByEmailid(java.sql.Connection con, int emailId, HashMap hm) {
		return hm;
	}

	/*
	 * Is called before an answer will be stored
	 */
	public HashMap preAnswerStore(java.sql.Connection con, Answer answer, HashMap hm) {
		return hm;
	}

	/*
	 * Is called before a case will be deleted
	 */
	public HashMap preCaseDelete(java.sql.Connection con, List<java.lang.Integer> customerIds, HashMap<String, Object> hm) {
		return hm;
	}

	/*
	 * Is called before a case will be loaded by id
	 */
	public HashMap preCaseLoad(java.sql.Connection con, int caseId, HashMap hm) {
		return hm;
	}

	/*
	 * Is called before a customer will be deleted
	 */
	public HashMap preCustomerDelete(java.sql.Connection con, List<java.lang.Integer> customerIds, HashMap<String, Object> hm) {
		return hm;
	}

	/*
	 * Is called before a customer will be loaded by id of a customer
	 */
	public HashMap preCustomerLoad(java.sql.Connection con, int customerId, HashMap hm) {
		return hm;
	}

	/*
	 * Is called before a customer will be stored
	 */
	public HashMap preCustomerStore(java.sql.Connection con, Customer customer, HashMap hm) {
		return hm;
	}

	/*
	 * Is called before an operator will be loaded by id
	 */
	public HashMap preOperatorLoad(java.sql.Connection con, int operatorId, HashMap hm) {
		return hm;
	}

	/*
	 * Is called before an operator will be stored
	 */
	public HashMap preOperatorStore(java.sql.Connection con, Operator operator, HashMap hm) {
		return hm;
	}

	/*
	 * Is called before a profile will be deleted
	 */
	public HashMap preProfileDelete(java.sql.Connection con, Profile profile, HashMap hm) {
		return hm;
	}

	/*
	 * Is called before a profile will be loaded by id
	 */
	public HashMap preProfileLoad(java.sql.Connection con, int profileId, HashMap hm) {
		return hm;
	}

	/*
	 * Is called before a profile will be stored
	 */
	public HashMap preProfileStore(java.sql.Connection con, Profile profile, HashMap hm) {
		return hm;
	}

	/*
	 * Is called before a project will be deleted
	 */
	public HashMap preProjectDelete(java.sql.Connection con, Project project, HashMap hm) {
		return hm;
	}

	/*
	 * Is called before a project will be loaded by id
	 */
	public HashMap preProjectLoad(java.sql.Connection con, int projectId, HashMap hm) {
		return hm;
	}

	/*
	 * Is called before a project will be stored
	 */
	public HashMap preProjectStore(java.sql.Connection con, Project project, HashMap hm) {
		return hm;
	}

	/*
	 * Is called before a question will be deleted
	 */
	public HashMap preQuestionDelete(java.sql.Connection con, Question question, HashMap hm) {
		return hm;
	}

	/*
	 * Is called before a question will be loaded
	 */
	public HashMap preQuestionLoad(java.sql.Connection con, int questionId, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		try {
			SkyLogger.getMediatrixLogger().debug(logPrefix + " call preQuestionLoad:" + questionId + " closed:" + con.isClosed());
		} catch (SQLException e) {
			SkyLogger.getMediatrixLogger().error(logPrefix + " call preQuestionLoad::" + questionId + " e:" + e.getMessage(), e);
		}
		return hm;
	}

	/*
	 * Is called before a lock will be released
	 */
	public HashMap preReleaseLock(java.sql.Connection con, de.ityx.mediatrix.data.base.Lock lock, int operatorId, String sessionid, Long currentTimeMillis, HashMap hm) {
		return hm;
	}

	/*
	 * Is called before the lock will be set
	 */
	public HashMap preSetLock(java.sql.Connection con, de.ityx.mediatrix.data.base.Lock lock, int operatorId, String sessionid, Long currentTime, de.ityx.mediatrix.data.base.LockResult lockResult, boolean opMode, HashMap hm) {
		return hm;
	}

	/*
	 * Is called before a subproject will be deleted
	 */
	public HashMap preSubprojectDelete(java.sql.Connection con, Subproject subproject, HashMap hm) {
		return hm;
	}

	/*
	 * Is called before a subproject will be loaded by id
	 */
	public HashMap preSubprojectLoad(java.sql.Connection con, int subprojectId, HashMap hm) {
		return hm;
	}

	/*
	 * Is called before a subproject will be stored
	 */
	public HashMap preSubprojectStore(java.sql.Connection con, Subproject subproject, HashMap hm) {
		return hm;
	}

	/*
	 * Will be called after an Answer has been created
	 */
	public HashMap postAnswerCreate(java.sql.Connection con, Answer answer, HashMap hm) {
		return hm;
	}

	/*
	 * Will be called after an Attachment has been created
	 */
	public HashMap postAttachmentCreate(java.sql.Connection con, de.ityx.mediatrix.data.Attachment attachment, HashMap hm) {
		return hm;
	}

	/*
	 * Will be called after a case has been created
	 */
	public HashMap postCaseCreate(java.sql.Connection con, Case ccase, HashMap hm) {
		return hm;
	}

	/*
	 * Will be called after a customer has been created
	 */
	public HashMap postCustomerCreate(java.sql.Connection con, Customer customer, HashMap hm) {
		return hm;
	}

	/*
	 * Will be called after an operator has been created
	 */
	public HashMap postOpertorCreate(java.sql.Connection con, Operator operator, HashMap hm) {
		return hm;
	}

	/*
	 * Will be called after a project has been created
	 */
	public HashMap postProjectCreate(java.sql.Connection con, Project project, HashMap hm) {
		return hm;
	}

	/*
	 * Will be called after a question has been created
	 */
	public HashMap postQuestionCreate(java.sql.Connection con, Question question, HashMap hm) {
		return hm;
	}

	/*
	 * Will be called after a subproject has been created.
	 */
	public HashMap postSubprojectCreate(java.sql.Connection con, Subproject subproject, HashMap hm) {
		return hm;
	}

	/*
	 * Is called before an answer will be created
	 */
	public HashMap preAnswerCreate(java.sql.Connection con, Answer answer, HashMap hm) {
		return hm;
	}

	/*
	 * Is called before an attachment will be created
	 */
	public HashMap preAttachmentCreate(java.sql.Connection con, de.ityx.mediatrix.data.Attachment attachment, HashMap hm) {
		return hm;
	}

	/*
	 * Is called before a case will be created
	 */
	public HashMap preCaseCreate(java.sql.Connection con, Case ccase, HashMap hm) {
		return hm;
	}

	/*
	 * Is called before a customer will be created
	 */
	public HashMap preCustomerCreate(java.sql.Connection con, Customer customer, HashMap hm) {
		return hm;
	}

	/*
	 * Is called before an operator will be created
	 */
	public HashMap preOpertorCreate(java.sql.Connection con, Operator operator, HashMap hm) {
		return hm;
	}

	/*
	 * Is called before a project will be created
	 */
	public HashMap preProjectCreate(java.sql.Connection con, Project project, HashMap hm) {
		return hm;
	}

	/*
	 * Is called before a question will be created
	 */
	public HashMap preQuestionCreate(java.sql.Connection con, Question question, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name + " q:" + (question != null ? question.getId() : "") + " ";
		SkyLogger.getMediatrixLogger().debug(logPrefix + " call preQuestionCreate:1:");
		return hm;
	}

	/*
	 * Is called before a subproject will be created
	 */
	public HashMap preSubprojectCreate(java.sql.Connection con, Subproject subproject, HashMap hm) {
		return hm;
	}


}
