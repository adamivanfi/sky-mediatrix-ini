package com.nttdata.de.ityx.mediatrix.businessrules.impl.server;

import de.ityx.mediatrix.api.interfaces.businessrules.server.IServerObjectsControl;
import de.ityx.mediatrix.data.*;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

public class ServerObjectsControl implements IServerObjectsControl {

	/*
	 * Will be called after an attachment has been deleted
	 */
	@Override
	public HashMap postAttachmentDelete(java.sql.Connection con, de.ityx.mediatrix.data.Attachment attachment, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Will be called after an attachment has been stored
	 */
	@Override
	public HashMap postAttachmentStore(java.sql.Connection con, de.ityx.mediatrix.data.Attachment attachment, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Is called before an attachment will be deleted
	 */
	@Override
	public HashMap preAttachmentDelete(java.sql.Connection con, de.ityx.mediatrix.data.Attachment attachment, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Is called before an attachment will be loaded by id
	 */
	@Override
	public HashMap preAttachmentLoad(java.sql.Connection con, int attachmentId, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Is called before an attachment will be loaded
	 */
	@Override
	public HashMap preAttachmentLoad(java.sql.Connection con, de.ityx.mediatrix.data.Attachment attachment, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Is called before an attachment will be stored
	 */
	@Override
	public HashMap preAttachmentStore(java.sql.Connection con, de.ityx.mediatrix.data.Attachment attachment, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Will be called after a question has been stored
	 */
	@Override
	public HashMap postQuestionStore(java.sql.Connection con, Question question, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Is called before a question will be stored
	 */
	@Override
	public HashMap preQuestionStore(java.sql.Connection con, Question question, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Will be called after a case has been stored
	 */
	@Override
	public HashMap postCaseStore(java.sql.Connection con, Case ccase, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Is called before a case will be stored
	 */
	@Override
	public HashMap preCaseStore(java.sql.Connection con, Case ccase, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Will be called after an answer has been deleted
	 */
	@Override
	public HashMap postAnswerDelete(java.sql.Connection con, Answer answer, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Will be called after an answer has been loaded
	 */
	@Override
	public HashMap postAnswerLoad(java.sql.Connection con, Answer answer, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Will be called after an answer has been loaded
	 */
	@Override
	public HashMap postAnswerLoadByEmailid(java.sql.Connection con, Answer answer, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Will be called after an answer has been stored
	 */
	@Override
	public HashMap postAnswerStore(java.sql.Connection con, Answer answer, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Will be called after an attachment has been loaded
	 */
	@Override
	public HashMap postAttachmentLoad(java.sql.Connection con, de.ityx.mediatrix.data.Attachment attachment, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Will be called after a case has been deleted
	 */
	@Override
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
	@Override
	public HashMap postCaseLoad(java.sql.Connection con, Case ccase, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Will be called after a customer has been deleted
	 */
	@Override
	public HashMap postCustomerDelete(java.sql.Connection con, List<java.lang.Integer> customerIds, HashMap<String, Object> hm) {
		return new HashMap();
	}

	/*
	 * Will be called after a customer has been loaded
	 */
	@Override
	public HashMap postCustomerLoad(java.sql.Connection con, Customer customer, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Will be called after a customer has been stored
	 */
	@Override
	public HashMap postCustomerStore(java.sql.Connection con, Customer customer, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Will be called after an operator has been loaded
	 */
	@Override
	public HashMap postOperatorLoad(java.sql.Connection con, Operator operator, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Will be called after an operator has been stored
	 */
	@Override
	public HashMap postOperatorStore(java.sql.Connection con, Operator operator, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Will be called after a profile has been deleted
	 */
	@Override
	public HashMap postProfileDelete(java.sql.Connection con, Profile profile, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Will be called after a profile has been loaded
	 */
	@Override
	public HashMap postProfileLoad(java.sql.Connection con, Profile profile, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Is called before a profile will be stored
	 */
	@Override
	public HashMap postProfileStore(java.sql.Connection con, Profile profile, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Will be called after a project has been deleted
	 */
	@Override
	public HashMap postProjectDelete(java.sql.Connection con, Project project, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Will be called after a project has been loaded
	 */
	@Override
	public HashMap postProjectLoad(java.sql.Connection con, Project project, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Will be called after a project has been stored
	 */
	@Override
	public HashMap postProjectStore(java.sql.Connection con, Project project, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Will be called after a question has been deleted
	 */
	@Override
	public HashMap postQuestionDelete(java.sql.Connection con, Question question, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Will be called after a question has been loaded
	 */
	@Override
	public HashMap postQuestionLoad(java.sql.Connection con, Question question, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Will be called after a lock has been released
	 */
	@Override
	public HashMap postReleaseLock(java.sql.Connection con, de.ityx.mediatrix.data.base.Lock lock, int operatorId, String sessionid, Long currentTimeMillis, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Will be called after a lock has been set
	 */
	@Override
	public HashMap postSetLock(java.sql.Connection con, de.ityx.mediatrix.data.base.Lock lock, int operatorId, String sessionid, Long currentTime, de.ityx.mediatrix.data.base.LockResult lockResult, boolean opMode, HashMap hm) {
		return new HashMap();
	}


	@Override
	public HashMap postSubprojectDelete(java.sql.Connection con, Subproject subproject, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Will be called after a subproject has been loaded
	 */
	@Override
	public HashMap postSubprojectLoad(java.sql.Connection con, Subproject subproject, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Will be stored after a subproject has been stored
	 */
	@Override
	public HashMap postSubprojectStore(java.sql.Connection con, Subproject subproject, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Is called before an answer will be deleted
	 */
	@Override
	public HashMap preAnswerDelete(java.sql.Connection con, Answer answer, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Is Called before an answer will be loaded
	 */
	@Override
	public HashMap preAnswerLoad(java.sql.Connection con, int answerId, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Is called before an answer will be loaded by the id of an email
	 */
	@Override
	public HashMap preAnswerLoadByEmailid(java.sql.Connection con, int emailId, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Is called before an answer will be stored
	 */
	@Override
	public HashMap preAnswerStore(java.sql.Connection con, Answer answer, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Is called before a case will be deleted
	 */
	@Override
	public HashMap preCaseDelete(java.sql.Connection con, List<java.lang.Integer> customerIds, HashMap<String, Object> hm) {
		return new HashMap();
	}

	/*
	 * Is called before a case will be loaded by id
	 */
	@Override
	public HashMap preCaseLoad(java.sql.Connection con, int caseId, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Is called before a customer will be deleted
	 */
	@Override
	public HashMap preCustomerDelete(java.sql.Connection con, List<java.lang.Integer> customerIds, HashMap<String, Object> hm) {
		return new HashMap();
	}

	/*
	 * Is called before a customer will be loaded by id of a customer
	 */
	@Override
	public HashMap preCustomerLoad(java.sql.Connection con, int customerId, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Is called before a customer will be stored
	 */
	@Override
	public HashMap preCustomerStore(java.sql.Connection con, Customer customer, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Is called before an operator will be loaded by id
	 */
	@Override
	public HashMap preOperatorLoad(java.sql.Connection con, int operatorId, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Is called before an operator will be stored
	 */
	@Override
	public HashMap preOperatorStore(java.sql.Connection con, Operator operator, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Is called before a profile will be deleted
	 */
	@Override
	public HashMap preProfileDelete(java.sql.Connection con, Profile profile, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Is called before a profile will be loaded by id
	 */
	@Override
	public HashMap preProfileLoad(java.sql.Connection con, int profileId, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Is called before a profile will be stored
	 */
	@Override
	public HashMap preProfileStore(java.sql.Connection con, Profile profile, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Is called before a project will be deleted
	 */
	@Override
	public HashMap preProjectDelete(java.sql.Connection con, Project project, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Is called before a project will be loaded by id
	 */
	@Override
	public HashMap preProjectLoad(java.sql.Connection con, int projectId, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Is called before a project will be stored
	 */
	@Override
	public HashMap preProjectStore(java.sql.Connection con, Project project, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Is called before a question will be deleted
	 */
	@Override
	public HashMap preQuestionDelete(java.sql.Connection con, Question question, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Is called before a question will be loaded
	 */
	@Override
	public HashMap preQuestionLoad(java.sql.Connection con, int questionId, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Is called before a lock will be released
	 */
	@Override
	public HashMap preReleaseLock(java.sql.Connection con, de.ityx.mediatrix.data.base.Lock lock, int operatorId, String sessionid, Long currentTimeMillis, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Is called before the lock will be set
	 */
	@Override
	public HashMap preSetLock(java.sql.Connection con, de.ityx.mediatrix.data.base.Lock lock, int operatorId, String sessionid, Long currentTime, de.ityx.mediatrix.data.base.LockResult lockResult, boolean opMode, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Is called before a subproject will be deleted
	 */
	@Override
	public HashMap preSubprojectDelete(java.sql.Connection con, Subproject subproject, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Is called before a subproject will be loaded by id
	 */
	@Override
	public HashMap preSubprojectLoad(java.sql.Connection con, int subprojectId, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Is called before a subproject will be stored
	 */
	@Override
	public HashMap preSubprojectStore(java.sql.Connection con, Subproject subproject, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Will be called after an Answer has been created
	 */
	@Override
	public HashMap postAnswerCreate(java.sql.Connection con, Answer answer, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Will be called after an Attachment has been created
	 */
	@Override
	public HashMap postAttachmentCreate(java.sql.Connection con, de.ityx.mediatrix.data.Attachment attachment, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Will be called after a case has been created
	 */
	@Override
	public HashMap postCaseCreate(java.sql.Connection con, Case ccase, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Will be called after a customer has been created
	 */
	@Override
	public HashMap postCustomerCreate(java.sql.Connection con, Customer customer, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Will be called after an operator has been created
	 */
	@Override
	public HashMap postOpertorCreate(java.sql.Connection con, Operator operator, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Will be called after a project has been created
	 */
	@Override
	public HashMap postProjectCreate(java.sql.Connection con, Project project, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Will be called after a question has been created
	 */
	@Override
	public HashMap postQuestionCreate(java.sql.Connection con, Question question, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Will be called after a subproject has been created.
	 */
	@Override
	public HashMap postSubprojectCreate(java.sql.Connection con, Subproject subproject, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Is called before an answer will be created
	 */
	@Override
	public HashMap preAnswerCreate(java.sql.Connection con, Answer answer, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Is called before an attachment will be created
	 */
	@Override
	public HashMap preAttachmentCreate(java.sql.Connection con, de.ityx.mediatrix.data.Attachment attachment, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Is called before a case will be created
	 */
	@Override
	public HashMap preCaseCreate(java.sql.Connection con, Case ccase, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Is called before a customer will be created
	 */
	@Override
	public HashMap preCustomerCreate(java.sql.Connection con, Customer customer, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Is called before an operator will be created
	 */
	@Override
	public HashMap preOpertorCreate(java.sql.Connection con, Operator operator, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Is called before a project will be created
	 */
	@Override
	public HashMap preProjectCreate(java.sql.Connection con, Project project, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Is called before a question will be created
	 */
	@Override
	public HashMap preQuestionCreate(java.sql.Connection con, Question question, HashMap hm) {
		return new HashMap();
	}

	/*
	 * Is called before a subproject will be created
	 */
	@Override
	public HashMap preSubprojectCreate(java.sql.Connection con, Subproject subproject, HashMap hm) {
		return new HashMap();
	}

}
