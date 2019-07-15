package com.nttdata.de.sky.ityx.mediatrix.businessrules.server;

import com.nttdata.de.sky.ityx.mediatrix.businessrules.server.sky.SkyServerObjectsControl;
import de.ityx.mediatrix.api.interfaces.businessrules.server.IServerObjectsControl;
import de.ityx.mediatrix.data.*;
import de.ityx.mediatrix.data.base.Lock;
import de.ityx.mediatrix.data.base.LockResult;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

public class ServerObjectsControl implements IServerObjectsControl {

	private IServerObjectsControl skydel = new SkyServerObjectsControl();

	@Override
	public HashMap<String, Object> preQuestionStore(Connection connection, Question question, HashMap<String, Object> hashMap) {
		return skydel.preQuestionStore(connection, question, hashMap);
	}

	@Override
	public HashMap<String, Object> postQuestionStore(Connection connection, Question question, HashMap<String, Object> hashMap) {
		return skydel.postQuestionStore(connection, question, hashMap);
	}

	@Override
	public HashMap<String, Object> preQuestionLoad(Connection connection, int i, HashMap<String, Object> hashMap) {
		return skydel.preQuestionLoad(connection, i, hashMap);
	}

	@Override
	public HashMap<String, Object> postQuestionLoad(Connection connection, Question question, HashMap<String, Object> hashMap) {
		return skydel.postQuestionLoad(connection, question, hashMap);
	}

	@Override
	public HashMap<String, Object> preQuestionDelete(Connection connection, Question question, HashMap<String, Object> hashMap) {
		return skydel.preQuestionDelete(connection, question, hashMap);
	}

	@Override
	public HashMap<String, Object> postQuestionDelete(Connection connection, Question question, HashMap<String, Object> hashMap) {
		return skydel.postQuestionDelete(connection, question, hashMap);
	}

	@Override
	public HashMap<String, Object> preAnswerStore(Connection connection, Answer answer, HashMap<String, Object> hashMap) {
		return skydel.preAnswerStore(connection, answer, hashMap);
	}

	@Override
	public HashMap<String, Object> postAnswerStore(Connection connection, Answer answer, HashMap<String, Object> hashMap) {
		return skydel.postAnswerStore(connection, answer, hashMap);
	}

	@Override
	public HashMap<String, Object> preAnswerLoad(Connection connection, int i, HashMap<String, Object> hashMap) {
		return skydel.preAnswerLoad(connection, i, hashMap);
	}

	@Override
	public HashMap<String, Object> postAnswerLoad(Connection connection, Answer answer, HashMap<String, Object> hashMap) {
		return skydel.postAnswerLoad(connection, answer, hashMap);
	}

	@Override
	public HashMap<String, Object> preAnswerLoadByEmailid(Connection connection, int i, HashMap<String, Object> hashMap) {
		return skydel.preAnswerLoadByEmailid(connection, i, hashMap);
	}

	@Override
	public HashMap<String, Object> postAnswerLoadByEmailid(Connection connection, Answer answer, HashMap<String, Object> hashMap) {
		return skydel.postAnswerLoadByEmailid(connection, answer, hashMap);
	}

	@Override
	public HashMap<String, Object> preAnswerDelete(Connection connection, Answer answer, HashMap<String, Object> hashMap) {
		return skydel.preAnswerDelete(connection, answer, hashMap);
	}

	@Override
	public HashMap<String, Object> postAnswerDelete(Connection connection, Answer answer, HashMap<String, Object> hashMap) {
		return skydel.postAnswerDelete(connection, answer, hashMap);
	}

	@Override
	public HashMap<String, Object> preCustomerStore(Connection connection, Customer customer, HashMap<String, Object> hashMap) {
		return skydel.preCustomerStore(connection, customer, hashMap);
	}

	@Override
	public HashMap<String, Object> postCustomerStore(Connection connection, Customer customer, HashMap<String, Object> hashMap) {
		return skydel.postCustomerStore(connection, customer, hashMap);
	}

	@Override
	public HashMap<String, Object> preCustomerLoad(Connection connection, int i, HashMap<String, Object> hashMap) {
		return skydel.preCustomerLoad(connection, i, hashMap);
	}

	@Override
	public HashMap<String, Object> postCustomerLoad(Connection connection, Customer customer, HashMap<String, Object> hashMap) {
		return skydel.postCustomerLoad(connection, customer, hashMap);
	}

	@Override
	public HashMap<String, Object> preCustomerDelete(Connection connection, List<Integer> list, HashMap<String, Object> hashMap) {
		return skydel.preCustomerDelete(connection, list, hashMap);
	}

	@Override
	public HashMap<String, Object> postCustomerDelete(Connection connection, List<Integer> list, HashMap<String, Object> hashMap) {
		return skydel.postCustomerDelete(connection, list, hashMap);
	}

	@Override
	public HashMap<String, Object> preOperatorStore(Connection connection, Operator operator, HashMap<String, Object> hashMap) {
		return skydel.preOperatorStore(connection, operator, hashMap);
	}

	@Override
	public HashMap<String, Object> postOperatorStore(Connection connection, Operator operator, HashMap<String, Object> hashMap) {
		return skydel.postOperatorStore(connection, operator, hashMap);
	}

	@Override
	public HashMap<String, Object> preOperatorLoad(Connection connection, int i, HashMap<String, Object> hashMap) {
		return skydel.preOperatorLoad(connection, i, hashMap);
	}

	@Override
	public HashMap<String, Object> postOperatorLoad(Connection connection, Operator operator, HashMap<String, Object> hashMap) {
		return skydel.postOperatorLoad(connection, operator, hashMap);
	}

	@Override
	public HashMap<String, Object> preProfileStore(Connection connection, Profile profile, HashMap<String, Object> hashMap) {
		return skydel.preProfileStore(connection, profile, hashMap);
	}

	@Override
	public HashMap<String, Object> postProfileStore(Connection connection, Profile profile, HashMap<String, Object> hashMap) {
		return skydel.postProfileStore(connection, profile, hashMap);
	}

	@Override
	public HashMap<String, Object> preProfileLoad(Connection connection, int i, HashMap<String, Object> hashMap) {
		return skydel.preProfileLoad(connection, i, hashMap);
	}

	@Override
	public HashMap<String, Object> postProfileLoad(Connection connection, Profile profile, HashMap<String, Object> hashMap) {
		return skydel.postProfileLoad(connection, profile, hashMap);
	}

	@Override
	public HashMap<String, Object> preProfileDelete(Connection connection, Profile profile, HashMap<String, Object> hashMap) {
		return skydel.preProfileDelete(connection, profile, hashMap);
	}

	@Override
	public HashMap<String, Object> postProfileDelete(Connection connection, Profile profile, HashMap<String, Object> hashMap) {
		return skydel.postProfileDelete(connection, profile, hashMap);
	}

	@Override
	public HashMap<String, Object> preProjectStore(Connection connection, Project project, HashMap<String, Object> hashMap) {
		return skydel.preProjectStore(connection, project, hashMap);
	}

	@Override
	public HashMap<String, Object> postProjectStore(Connection connection, Project project, HashMap<String, Object> hashMap) {
		return skydel.postProjectStore(connection, project, hashMap);
	}

	@Override
	public HashMap<String, Object> preProjectLoad(Connection connection, int i, HashMap<String, Object> hashMap) {
		return skydel.preProjectLoad(connection, i, hashMap);
	}

	@Override
	public HashMap<String, Object> postProjectLoad(Connection connection, Project project, HashMap<String, Object> hashMap) {
		return skydel.postProjectLoad(connection, project, hashMap);
	}

	@Override
	public HashMap<String, Object> preProjectDelete(Connection connection, Project project, HashMap<String, Object> hashMap) {
		return skydel.preProjectDelete(connection, project, hashMap);
	}

	@Override
	public HashMap<String, Object> postProjectDelete(Connection connection, Project project, HashMap<String, Object> hashMap) {
		return skydel.postProjectDelete(connection, project, hashMap);
	}

	@Override
	public HashMap<String, Object> preSubprojectStore(Connection connection, Subproject subproject, HashMap<String, Object> hashMap) {
		return skydel.preSubprojectStore(connection, subproject, hashMap);
	}

	@Override
	public HashMap<String, Object> postSubprojectStore(Connection connection, Subproject subproject, HashMap<String, Object> hashMap) {
		return skydel.postSubprojectStore(connection, subproject, hashMap);
	}

	@Override
	public HashMap<String, Object> preSubprojectLoad(Connection connection, int i, HashMap<String, Object> hashMap) {
		return skydel.preSubprojectLoad(connection, i, hashMap);
	}

	@Override
	public HashMap<String, Object> postSubprojectLoad(Connection connection, Subproject subproject, HashMap<String, Object> hashMap) {
		return skydel.postSubprojectLoad(connection, subproject, hashMap);
	}

	@Override
	public HashMap<String, Object> preSubprojectDelete(Connection connection, Subproject subproject, HashMap<String, Object> hashMap) {
		return skydel.preSubprojectDelete(connection, subproject, hashMap);
	}

	@Override
	public HashMap<String, Object> postSubprojectDelete(Connection connection, Subproject subproject, HashMap<String, Object> hashMap) {
		return skydel.postSubprojectDelete(connection, subproject, hashMap);
	}

	@Override
	public HashMap<String, Object> preCaseStore(Connection connection, Case aCase, HashMap<String, Object> hashMap) {
		return skydel.preCaseStore(connection, aCase, hashMap);
	}

	@Override
	public HashMap<String, Object> postCaseStore(Connection connection, Case aCase, HashMap<String, Object> hashMap) {
		return skydel.postCaseStore(connection, aCase, hashMap);
	}

	@Override
	public HashMap<String, Object> preCaseLoad(Connection connection, int i, HashMap<String, Object> hashMap) {
		return skydel.preCaseLoad(connection, i, hashMap);
	}

	@Override
	public HashMap<String, Object> postCaseLoad(Connection connection, Case aCase, HashMap<String, Object> hashMap) {
		return skydel.postCaseLoad(connection, aCase, hashMap);
	}

	@Override
	public HashMap<String, Object> preCaseDelete(Connection connection, List<Integer> list, HashMap<String, Object> hashMap) {
		return skydel.preCaseDelete(connection, list, hashMap);
	}

	@Override
	public void postCaseDelete(Connection connection, List<Integer> list, HashMap<String, Object> hashMap) {
		skydel.postCaseDelete(connection, list, hashMap);
	}

	@Override
	public HashMap<String, Object> preAttachmentStore(Connection connection, Attachment attachment, HashMap<String, Object> hashMap) {
		return skydel.preAttachmentStore(connection, attachment, hashMap);
	}

	@Override
	public HashMap<String, Object> postAttachmentStore(Connection connection, Attachment attachment, HashMap<String, Object> hashMap) {
		return skydel.postAttachmentStore(connection, attachment, hashMap);
	}

	@Override
	public HashMap<String, Object> preAttachmentLoad(Connection connection, Attachment attachment, HashMap<String, Object> hashMap) {
		return skydel.preAttachmentLoad(connection, attachment, hashMap);
	}

	@Override
	public HashMap<String, Object> preAttachmentLoad(Connection connection, int i, HashMap<String, Object> hashMap) {
		return skydel.preAttachmentLoad(connection, i, hashMap);
	}

	@Override
	public HashMap<String, Object> postAttachmentLoad(Connection connection, Attachment attachment, HashMap<String, Object> hashMap) {
		return skydel.postAttachmentLoad(connection, attachment, hashMap);
	}

	@Override
	public HashMap<String, Object> preAttachmentDelete(Connection connection, Attachment attachment, HashMap<String, Object> hashMap) {
		return skydel.preAttachmentDelete(connection, attachment, hashMap);
	}

	@Override
	public HashMap<String, Object> postAttachmentDelete(Connection connection, Attachment attachment, HashMap<String, Object> hashMap) {
		return skydel.postAttachmentDelete(connection, attachment, hashMap);
	}

	@Override
	public HashMap<String, Object> preQuestionCreate(Connection connection, Question question, HashMap<String, Object> hashMap) {
		return skydel.preQuestionCreate(connection, question, hashMap);
	}

	@Override
	public HashMap<String, Object> postQuestionCreate(Connection connection, Question question, HashMap<String, Object> hashMap) {
		return skydel.postQuestionCreate(connection, question, hashMap);
	}

	@Override
	public HashMap<String, Object> preAnswerCreate(Connection connection, Answer answer, HashMap<String, Object> hashMap) {
		return skydel.preAnswerCreate(connection, answer, hashMap);
	}

	@Override
	public HashMap<String, Object> postAnswerCreate(Connection connection, Answer answer, HashMap<String, Object> hashMap) {
		return skydel.postAnswerCreate(connection, answer, hashMap);
	}

	@Override
	public HashMap<String, Object> preCustomerCreate(Connection connection, Customer customer, HashMap<String, Object> hashMap) {
		return skydel.preCustomerCreate(connection, customer, hashMap);
	}

	@Override
	public HashMap<String, Object> postCustomerCreate(Connection connection, Customer customer, HashMap<String, Object> hashMap) {
		return skydel.postCustomerCreate(connection, customer, hashMap);
	}

	@Override
	public HashMap<String, Object> preOpertorCreate(Connection connection, Operator operator, HashMap<String, Object> hashMap) {
		return skydel.preOpertorCreate(connection, operator, hashMap);
	}

	@Override
	public HashMap<String, Object> postOpertorCreate(Connection connection, Operator operator, HashMap<String, Object> hashMap) {
		return skydel.postOpertorCreate(connection, operator, hashMap);
	}

	@Override
	public HashMap<String, Object> preProjectCreate(Connection connection, Project project, HashMap<String, Object> hashMap) {
		return skydel.preProjectCreate(connection, project, hashMap);
	}

	@Override
	public HashMap<String, Object> postProjectCreate(Connection connection, Project project, HashMap<String, Object> hashMap) {
		return skydel.postProjectCreate(connection, project, hashMap);
	}

	@Override
	public HashMap<String, Object> preSubprojectCreate(Connection connection, Subproject subproject, HashMap<String, Object> hashMap) {
		return skydel.preSubprojectCreate(connection, subproject, hashMap);
	}

	@Override
	public HashMap<String, Object> postSubprojectCreate(Connection connection, Subproject subproject, HashMap<String, Object> hashMap) {
		return skydel.postSubprojectCreate(connection, subproject, hashMap);
	}

	@Override
	public HashMap<String, Object> preCaseCreate(Connection connection, Case aCase, HashMap<String, Object> hashMap) {
		return skydel.preCaseCreate(connection, aCase, hashMap);
	}

	@Override
	public HashMap<String, Object> postCaseCreate(Connection connection, Case aCase, HashMap<String, Object> hashMap) {
		return skydel.postCaseCreate(connection, aCase, hashMap);
	}

	@Override
	public HashMap<String, Object> preAttachmentCreate(Connection connection, Attachment attachment, HashMap<String, Object> hashMap) {
		return skydel.preAttachmentCreate(connection, attachment, hashMap);
	}

	@Override
	public HashMap<String, Object> postAttachmentCreate(Connection connection, Attachment attachment, HashMap<String, Object> hashMap) {
		return skydel.postAttachmentCreate(connection, attachment, hashMap);
	}

	@Override
	public HashMap<String, Object> preSetLock(Connection connection, Lock lock, int i, String s, Long aLong, LockResult lockResult, boolean b, HashMap<String, Object> hashMap) {
		return skydel.preSetLock(connection, lock, i, s, aLong, lockResult, b, hashMap);
	}

	@Override
	public HashMap<String, Object> postSetLock(Connection connection, Lock lock, int i, String s, Long aLong, LockResult lockResult, boolean b, HashMap<String, Object> hashMap) {
		return skydel.postSetLock(connection, lock, i, s, aLong, lockResult, b, hashMap);
	}

	@Override

	public HashMap<String, Object> preReleaseLock(Connection connection, Lock lock, int i, String s, Long aLong, HashMap<String, Object> hashMap) {
		return skydel.preReleaseLock(connection, lock, i, s, aLong, hashMap);
	}

	@Override
	public HashMap<String, Object> postReleaseLock(Connection connection, Lock lock, int i, String s, Long aLong, HashMap<String, Object> hashMap) {
		return skydel.postReleaseLock(connection, lock, i, s, aLong, hashMap);
	}

	@Override
	public void preReportStore(Connection connection, ScheduledReport scheduledReport) {
		skydel.preReportStore(connection, scheduledReport);
	}

	@Override
	public void postReportStore(Connection connection, ScheduledReport scheduledReport) {
		skydel.postReportStore(connection, scheduledReport);
	}

	@Override
	public void preReportLoad(Connection connection, int i) {
		skydel.preReportLoad(connection, i);
	}

	@Override
	public void postReportLoad(Connection connection, ScheduledReport scheduledReport) {
		skydel.postReportLoad(connection, scheduledReport);
	}

}