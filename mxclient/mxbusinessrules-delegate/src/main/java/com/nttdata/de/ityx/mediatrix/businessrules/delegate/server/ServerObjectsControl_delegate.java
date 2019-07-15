package com.nttdata.de.ityx.mediatrix.businessrules.delegate.server;

import de.ityx.mediatrix.api.interfaces.businessrules.server.IServerObjectsControl;
import de.ityx.mediatrix.data.*;
import de.ityx.mediatrix.data.base.Lock;
import de.ityx.mediatrix.data.base.LockResult;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

public class ServerObjectsControl_delegate implements IServerObjectsControl {

	private IServerObjectsControl delegate = com.nttdata.de.ityx.mediatrix.businessrules.factories.NTTDataBusinessruleFactoryGenerator.createFactory(true).getServerObjectsControl();
	
	@Override
	public HashMap<String, Object> preQuestionStore(Connection connection, Question question, HashMap<String, Object> hashMap) {
		return delegate.preQuestionStore(connection, question, hashMap);
	}

	@Override
	public HashMap<String, Object> postQuestionStore(Connection connection, Question question, HashMap<String, Object> hashMap) {
		return delegate.postQuestionStore(connection, question, hashMap);
	}

	@Override
	public HashMap<String, Object> preQuestionLoad(Connection connection, int i, HashMap<String, Object> hashMap) {
		return delegate.preQuestionLoad(connection, i, hashMap);
	}

	@Override
	public HashMap<String, Object> postQuestionLoad(Connection connection, Question question, HashMap<String, Object> hashMap) {
		return delegate.postQuestionLoad(connection, question, hashMap);
	}

	@Override
	public HashMap<String, Object> preQuestionDelete(Connection connection, Question question, HashMap<String, Object> hashMap) {
		return delegate.preQuestionDelete(connection, question, hashMap);
	}

	@Override
	public HashMap<String, Object> postQuestionDelete(Connection connection, Question question, HashMap<String, Object> hashMap) {
		return delegate.postQuestionDelete(connection, question, hashMap);
	}

	@Override
	public HashMap<String, Object> preAnswerStore(Connection connection, Answer answer, HashMap<String, Object> hashMap) {
		return delegate.preAnswerStore(connection, answer, hashMap);
	}

	@Override
	public HashMap<String, Object> postAnswerStore(Connection connection, Answer answer, HashMap<String, Object> hashMap) {
		return delegate.postAnswerStore(connection, answer, hashMap);
	}

	@Override
	public HashMap<String, Object> preAnswerLoad(Connection connection, int i, HashMap<String, Object> hashMap) {
		return delegate.preAnswerLoad(connection, i, hashMap);
	}

	@Override
	public HashMap<String, Object> postAnswerLoad(Connection connection, Answer answer, HashMap<String, Object> hashMap) {
		return delegate.postAnswerLoad(connection, answer, hashMap);
	}

	@Override
	public HashMap<String, Object> preAnswerLoadByEmailid(Connection connection, int i, HashMap<String, Object> hashMap) {
		return delegate.preAnswerLoadByEmailid(connection, i, hashMap);
	}

	@Override
	public HashMap<String, Object> postAnswerLoadByEmailid(Connection connection, Answer answer, HashMap<String, Object> hashMap) {
		return delegate.postAnswerLoadByEmailid(connection, answer, hashMap);
	}

	@Override
	public HashMap<String, Object> preAnswerDelete(Connection connection, Answer answer, HashMap<String, Object> hashMap) {
		return delegate.preAnswerDelete(connection, answer, hashMap);
	}

	@Override
	public HashMap<String, Object> postAnswerDelete(Connection connection, Answer answer, HashMap<String, Object> hashMap) {
		return delegate.postAnswerDelete(connection, answer, hashMap);
	}

	@Override
	public HashMap<String, Object> preCustomerStore(Connection connection, Customer customer, HashMap<String, Object> hashMap) {
		return delegate.preCustomerStore(connection, customer, hashMap);
	}

	@Override
	public HashMap<String, Object> postCustomerStore(Connection connection, Customer customer, HashMap<String, Object> hashMap) {
		return delegate.postCustomerStore(connection, customer, hashMap);
	}

	@Override
	public HashMap<String, Object> preCustomerLoad(Connection connection, int i, HashMap<String, Object> hashMap) {
		return delegate.preCustomerLoad(connection, i, hashMap);
	}

	@Override
	public HashMap<String, Object> postCustomerLoad(Connection connection, Customer customer, HashMap<String, Object> hashMap) {
		return delegate.postCustomerLoad(connection, customer, hashMap);
	}

	@Override
	public HashMap<String, Object> preCustomerDelete(Connection connection, List<Integer> list, HashMap<String, Object> hashMap) {
		return delegate.preCustomerDelete(connection, list, hashMap);
	}

	@Override
	public HashMap<String, Object> postCustomerDelete(Connection connection, List<Integer> list, HashMap<String, Object> hashMap) {
		return delegate.postCustomerDelete(connection,list,hashMap);
	}

	@Override
	public HashMap<String, Object> preOperatorStore(Connection connection, Operator operator, HashMap<String, Object> hashMap) {
		return delegate.preOperatorStore(connection, operator, hashMap);
	}

	@Override
	public HashMap<String, Object> postOperatorStore(Connection connection, Operator operator, HashMap<String, Object> hashMap) {
		return delegate.postOperatorStore(connection, operator, hashMap);
	}

	@Override
	public HashMap<String, Object> preOperatorLoad(Connection connection, int i, HashMap<String, Object> hashMap) {
		return delegate.preOperatorLoad(connection, i, hashMap);
	}

	@Override
	public HashMap<String, Object> postOperatorLoad(Connection connection, Operator operator, HashMap<String, Object> hashMap) {
		return delegate.postOperatorLoad(connection, operator, hashMap);
	}

	@Override
	public HashMap<String, Object> preProfileStore(Connection connection, Profile profile, HashMap<String, Object> hashMap) {
		return delegate.preProfileStore(connection, profile, hashMap);
	}

	@Override
	public HashMap<String, Object> postProfileStore(Connection connection, Profile profile, HashMap<String, Object> hashMap) {
		return delegate.postProfileStore(connection, profile, hashMap);
	}

	@Override
	public HashMap<String, Object> preProfileLoad(Connection connection, int i, HashMap<String, Object> hashMap) {
		return delegate.preProfileLoad(connection, i, hashMap);
	}

	@Override
	public HashMap<String, Object> postProfileLoad(Connection connection, Profile profile, HashMap<String, Object> hashMap) {
		return delegate.postProfileLoad(connection, profile, hashMap);
	}

	@Override
	public HashMap<String, Object> preProfileDelete(Connection connection, Profile profile, HashMap<String, Object> hashMap) {
		return delegate.preProfileDelete(connection, profile, hashMap);
	}

	@Override
	public HashMap<String, Object> postProfileDelete(Connection connection, Profile profile, HashMap<String, Object> hashMap) {
		return delegate.postProfileDelete(connection, profile, hashMap);
	}

	@Override
	public HashMap<String, Object> preProjectStore(Connection connection, Project project, HashMap<String, Object> hashMap) {
		return delegate.preProjectStore(connection, project, hashMap);
	}

	@Override
	public HashMap<String, Object> postProjectStore(Connection connection, Project project, HashMap<String, Object> hashMap) {
		return delegate.postProjectStore(connection, project, hashMap);
	}

	@Override
	public HashMap<String, Object> preProjectLoad(Connection connection, int i, HashMap<String, Object> hashMap) {
		return delegate.preProjectLoad(connection, i, hashMap);
	}

	@Override
	public HashMap<String, Object> postProjectLoad(Connection connection, Project project, HashMap<String, Object> hashMap) {
		return delegate.postProjectLoad(connection, project, hashMap);
	}

	@Override
	public HashMap<String, Object> preProjectDelete(Connection connection, Project project, HashMap<String, Object> hashMap) {
		return delegate.preProjectDelete(connection, project, hashMap);
	}

	@Override
	public HashMap<String, Object> postProjectDelete(Connection connection, Project project, HashMap<String, Object> hashMap) {
		return delegate.postProjectDelete(connection, project, hashMap);
	}

	@Override
	public HashMap<String, Object> preSubprojectStore(Connection connection, Subproject subproject, HashMap<String, Object> hashMap) {
		return delegate.preSubprojectStore(connection, subproject, hashMap);
	}

	@Override
	public HashMap<String, Object> postSubprojectStore(Connection connection, Subproject subproject, HashMap<String, Object> hashMap) {
		return delegate.postSubprojectStore(connection, subproject, hashMap);
	}

	@Override
	public HashMap<String, Object> preSubprojectLoad(Connection connection, int i, HashMap<String, Object> hashMap) {
		return delegate.preSubprojectLoad(connection, i, hashMap);
	}

	@Override
	public HashMap<String, Object> postSubprojectLoad(Connection connection, Subproject subproject, HashMap<String, Object> hashMap) {
		return delegate.postSubprojectLoad(connection, subproject, hashMap);
	}

	@Override
	public HashMap<String, Object> preSubprojectDelete(Connection connection, Subproject subproject, HashMap<String, Object> hashMap) {
		return delegate.preSubprojectDelete(connection, subproject, hashMap);
	}

	@Override
	public HashMap<String, Object> postSubprojectDelete(Connection connection, Subproject subproject, HashMap<String, Object> hashMap) {
		return delegate.postSubprojectDelete(connection, subproject, hashMap);
	}

	@Override
	public HashMap<String, Object> preCaseStore(Connection connection, Case aCase, HashMap<String, Object> hashMap) {
		return delegate.preCaseStore(connection, aCase, hashMap);
	}

	@Override
	public HashMap<String, Object> postCaseStore(Connection connection, Case aCase, HashMap<String, Object> hashMap) {
		return delegate.postCaseStore(connection, aCase, hashMap);
	}

	@Override
	public HashMap<String, Object> preCaseLoad(Connection connection, int i, HashMap<String, Object> hashMap) {
		return delegate.preCaseLoad(connection, i, hashMap);
	}

	@Override
	public HashMap<String, Object> postCaseLoad(Connection connection, Case aCase, HashMap<String, Object> hashMap) {
		return delegate.postCaseLoad(connection, aCase, hashMap);
	}

	@Override
	public HashMap<String, Object> preCaseDelete(Connection connection, List<Integer> list, HashMap<String, Object> hashMap) {
		return delegate.preCaseDelete(connection, list, hashMap);
	}

	@Override
	public void postCaseDelete(Connection connection, List<Integer> list, HashMap<String, Object> hashMap) {
		delegate.postCaseDelete(connection, list, hashMap);
	}

	@Override
	public HashMap<String, Object> preAttachmentStore(Connection connection, Attachment attachment, HashMap<String, Object> hashMap) {
		return delegate.preAttachmentStore(connection, attachment, hashMap);
	}

	@Override
	public HashMap<String, Object> postAttachmentStore(Connection connection, Attachment attachment, HashMap<String, Object> hashMap) {
		return delegate.postAttachmentStore(connection, attachment, hashMap);
	}

	@Override
	public HashMap<String, Object> preAttachmentLoad(Connection connection, Attachment attachment, HashMap<String, Object> hashMap) {
		return delegate.preAttachmentLoad(connection, attachment, hashMap);
	}

	@Override
	public HashMap<String, Object> preAttachmentLoad(Connection connection, int i, HashMap<String, Object> hashMap) {
		return delegate.preAttachmentLoad(connection, i, hashMap);
	}

	@Override
	public HashMap<String, Object> postAttachmentLoad(Connection connection, Attachment attachment, HashMap<String, Object> hashMap) {
		return delegate.postAttachmentLoad(connection, attachment, hashMap);
	}

	@Override
	public HashMap<String, Object> preAttachmentDelete(Connection connection, Attachment attachment, HashMap<String, Object> hashMap) {
		return delegate.preAttachmentDelete(connection, attachment, hashMap);
	}

	@Override
	public HashMap<String, Object> postAttachmentDelete(Connection connection, Attachment attachment, HashMap<String, Object> hashMap) {
		return delegate.postAttachmentDelete(connection, attachment, hashMap);
	}

	@Override
	public HashMap<String, Object> preQuestionCreate(Connection connection, Question question, HashMap<String, Object> hashMap) {
		return delegate.preQuestionCreate(connection, question, hashMap);
	}

	@Override
	public HashMap<String, Object> postQuestionCreate(Connection connection, Question question, HashMap<String, Object> hashMap) {
		return delegate.postQuestionCreate(connection, question, hashMap);
	}

	@Override
	public HashMap<String, Object> preAnswerCreate(Connection connection, Answer answer, HashMap<String, Object> hashMap) {
		return delegate.preAnswerCreate(connection, answer, hashMap);
	}

	@Override
	public HashMap<String, Object> postAnswerCreate(Connection connection, Answer answer, HashMap<String, Object> hashMap) {
		return delegate.postAnswerCreate(connection, answer, hashMap);
	}

	@Override
	public HashMap<String, Object> preCustomerCreate(Connection connection, Customer customer, HashMap<String, Object> hashMap) {
		return delegate.preCustomerCreate(connection, customer, hashMap);
	}

	@Override
	public HashMap<String, Object> postCustomerCreate(Connection connection, Customer customer, HashMap<String, Object> hashMap) {
		return delegate.postCustomerCreate(connection, customer, hashMap);
	}

	@Override
	public HashMap<String, Object> preOpertorCreate(Connection connection, Operator operator, HashMap<String, Object> hashMap) {
		return delegate.preOpertorCreate(connection, operator, hashMap);
	}

	@Override
	public HashMap<String, Object> postOpertorCreate(Connection connection, Operator operator, HashMap<String, Object> hashMap) {
		return delegate.postOpertorCreate(connection, operator, hashMap);
	}

	@Override
	public HashMap<String, Object> preProjectCreate(Connection connection, Project project, HashMap<String, Object> hashMap) {
		return delegate.preProjectCreate(connection, project, hashMap);
	}

	@Override
	public HashMap<String, Object> postProjectCreate(Connection connection, Project project, HashMap<String, Object> hashMap) {
		return delegate.postProjectCreate(connection, project, hashMap);
	}

	@Override
	public HashMap<String, Object> preSubprojectCreate(Connection connection, Subproject subproject, HashMap<String, Object> hashMap) {
		return delegate.preSubprojectCreate(connection, subproject, hashMap);
	}

	@Override
	public HashMap<String, Object> postSubprojectCreate(Connection connection, Subproject subproject, HashMap<String, Object> hashMap) {
		return delegate.postSubprojectCreate(connection, subproject, hashMap);
	}

	@Override
	public HashMap<String, Object> preCaseCreate(Connection connection, Case aCase, HashMap<String, Object> hashMap) {
		return delegate.preCaseCreate(connection, aCase, hashMap);
	}

	@Override
	public HashMap<String, Object> postCaseCreate(Connection connection, Case aCase, HashMap<String, Object> hashMap) {
		return delegate.postCaseCreate(connection, aCase, hashMap);
	}

	@Override
	public HashMap<String, Object> preAttachmentCreate(Connection connection, Attachment attachment, HashMap<String, Object> hashMap) {
		return delegate.preAttachmentCreate(connection, attachment, hashMap);
	}

	@Override
	public HashMap<String, Object> postAttachmentCreate(Connection connection, Attachment attachment, HashMap<String, Object> hashMap) {
		return delegate.postAttachmentCreate(connection, attachment, hashMap);
	}

	@Override
	public HashMap<String, Object> preSetLock(Connection connection, Lock lock, int i, String s, Long aLong, LockResult lockResult, boolean b, HashMap<String, Object> hashMap) {
		return delegate.preSetLock(connection, lock, i, s, aLong, lockResult, b, hashMap);
	}

	@Override
	public HashMap<String, Object> postSetLock(Connection connection, Lock lock, int i, String s, Long aLong, LockResult lockResult, boolean b, HashMap<String, Object> hashMap) {
		return delegate.postSetLock(connection, lock, i, s, aLong, lockResult, b, hashMap);
	}

	@Override
	
	public HashMap<String, Object> preReleaseLock(Connection connection, Lock lock, int i, String s, Long aLong, HashMap<String, Object> hashMap) {
		return delegate.preReleaseLock(connection, lock, i, s, aLong, hashMap);
	}

	@Override
	public HashMap<String, Object> postReleaseLock(Connection connection, Lock lock, int i, String s, Long aLong, HashMap<String, Object> hashMap) {
		return delegate.postReleaseLock(connection, lock, i, s, aLong, hashMap);
	}

	@Override
	public void preReportStore(Connection connection, ScheduledReport scheduledReport) {
		delegate.preReportStore(connection, scheduledReport);
	}

	@Override
	public void postReportStore(Connection connection, ScheduledReport scheduledReport) {
		delegate.postReportStore(connection, scheduledReport);
	}

	@Override
	public void preReportLoad(Connection connection, int i) {
		delegate.preReportLoad(connection, i);
	}

	@Override
	public void postReportLoad(Connection connection, ScheduledReport scheduledReport) {
		delegate.postReportLoad(connection, scheduledReport);
	}
	
	
}
