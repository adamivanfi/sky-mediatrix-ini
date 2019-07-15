package com.nttdata.de.ityx.mediatrix.businessrules.delegate.client;

import de.ityx.mediatrix.api.interfaces.businessrules.client.IClientObjectsControl;
import de.ityx.mediatrix.data.*;

import java.util.HashMap;

public class ClientObjectsControl_delegate implements IClientObjectsControl {

	IClientObjectsControl delegate = com.nttdata.de.ityx.mediatrix.businessrules.factories.NTTDataBusinessruleFactoryGenerator.createFactory(false).getClientObjectsControlRule();

	public HashMap postAnswerDelete(Answer answer, HashMap hm) {
		return delegate.postAnswerDelete(answer, hm);
	}

	public HashMap postAnswerLoadByEmailid(Answer answer, HashMap hm) {
		return delegate.postAnswerLoadByEmailid(answer, hm);
	}

	public HashMap postAnswerStore(Answer answer, HashMap hm) {
		return delegate.postAnswerStore(answer, hm);
	}

	public HashMap postAnswerLoad(Answer answer, HashMap hm) {
		return delegate.postAnswerLoad(answer, hm);
	}

	public HashMap postAttachmentDelete(Attachment attachment, HashMap hm) {
		return delegate.postAttachmentDelete(attachment, hm);
	}

	public HashMap postAttachmentLoad(Attachment attachment, HashMap hm) {
		return delegate.postAttachmentLoad(attachment, hm);
	}

	public HashMap postAttachmentStore(Attachment attachment, HashMap hm) {
		return delegate.postAttachmentStore(attachment, hm);
	}

	public HashMap postCaseDelete(Case ccase, HashMap hm) {
		return delegate.postCaseDelete(ccase, hm);
	}

	public HashMap postCaseLoad(Case ccase, HashMap hm) {
		return delegate.postCaseLoad(ccase, hm);
	}

	public HashMap postCaseStore(Case ccase, HashMap hm) {
		return delegate.postCaseStore(ccase, hm);
	}

	public HashMap postCustomerDelete(Customer customer, HashMap hm) {
		return delegate.postCustomerDelete(customer, hm);
	}

	public HashMap postCustomerLoad(Customer customer, HashMap hm) {
		return delegate.postCustomerLoad(customer, hm);
	}

	public HashMap postCustomerStore(Customer customer, HashMap hm) {
		return delegate.postCustomerStore(customer, hm);
	}

	public HashMap postOperatorLoad(Operator operator, HashMap hm) {
		return delegate.postOperatorLoad(operator, hm);
	}

	public HashMap postOperatorStore(Operator operator, HashMap hm) {
		return delegate.postOperatorStore(operator, hm);
	}

	public HashMap postProfileDelete(Profile profile, HashMap hm) {
		return delegate.postProfileDelete(profile, hm);
	}

	public HashMap postProfileLoad(Profile profile, HashMap hm) {
		return delegate.postProfileLoad(profile, hm);
	}

	public HashMap postProfileStore(Profile profile, HashMap hm) {
		return delegate.postProfileStore(profile, hm);
	}

	public HashMap postProjectDelete(Project project, HashMap hm) {
		return delegate.postProjectDelete(project, hm);
	}

	public HashMap postProjectLoad(Project project, HashMap hm) {
		return delegate.postProjectLoad(project, hm);
	}

	public HashMap postProjectStore(Project project, HashMap hm) {
		return delegate.postProjectStore(project, hm);
	}

	public HashMap postQuestionDelete(Question question, HashMap hm) {
		return delegate.postQuestionDelete(question, hm);
	}

	public HashMap postQuestionLoad(Question question, HashMap hm) {
		return delegate.postQuestionLoad(question, hm);
	}

	public HashMap postQuestionStore(Question question, HashMap hm) {
		return delegate.postQuestionStore(question, hm);
	}

	public HashMap postReleaseLock(de.ityx.mediatrix.data.base.Lock lock, String result, HashMap hm) {
		return delegate.postReleaseLock(lock, result, hm);
	}

	public HashMap postSetLock(de.ityx.mediatrix.data.base.Lock lock, de.ityx.mediatrix.data.base.LockResult result, HashMap hm) {
		return delegate.postSetLock(lock, result, hm);
	}

	public HashMap postSubprojectDelete(Subproject subproject, HashMap hm) {
		return delegate.postSubprojectDelete(subproject, hm);
	}

	public HashMap postSubprojectLoad(Subproject subproject, HashMap hm) {
		return delegate.postSubprojectLoad(subproject, hm);
	}

	public HashMap postSubprojectStore(Subproject subproject, HashMap hm) {
		return delegate.postSubprojectStore(subproject, hm);
	}

	public HashMap preAnswerDelete(Answer answer, HashMap hm) {
		return delegate.preAnswerDelete(answer, hm);
	}

	public HashMap preAnswerLoad(int answerId, HashMap hm) {
		return delegate.preAnswerLoad(answerId, hm);
	}

	public HashMap preAnswerLoadByEmailid(int emailId, HashMap hm) {
		return delegate.preAnswerLoadByEmailid(emailId, hm);
	}

	public HashMap preAnswerStore(Answer answer, HashMap hm) {
		return delegate.preAnswerStore(answer, hm);
	}

	public HashMap preAttachmentDelete(Attachment attachment, HashMap hm) {
		return delegate.preAttachmentDelete(attachment, hm);
	}

	public HashMap preAttachmentLoad(Attachment attachment, HashMap hm) {
		return delegate.preAttachmentLoad(attachment, hm);
	}

	public HashMap preAttachmentLoad(int attachmentId, HashMap hm) {
		return delegate.preAttachmentLoad(attachmentId, hm);
	}

	public HashMap preAttachmentStore(Attachment attachment, HashMap hm) {
		return delegate.preAttachmentStore(attachment, hm);
	}

	public HashMap preCaseDelete(Case ccase, HashMap hm) {
		return delegate.preCaseDelete(ccase, hm);
	}

	public HashMap preCaseLoad(int caseId, HashMap hm) {
		return delegate.preCaseLoad(caseId, hm);
	}

	public HashMap preCaseStore(Case ccase, HashMap hm) {
		return delegate.preCaseStore(ccase, hm);
	}

	public HashMap preCustomerDelete(Customer customer, HashMap hm) {
		return delegate.preCustomerDelete(customer, hm);
	}

	public HashMap preCustomerLoad(int kundeId, HashMap hm) {
		return delegate.preCustomerLoad(kundeId, hm);
	}

	public HashMap preCustomerStore(Customer customer, HashMap hm) {
		return delegate.preCustomerStore(customer, hm);
	}

	public HashMap preOperatorLoad(int operatorId, HashMap hm) {
		return delegate.preOperatorLoad(operatorId, hm);
	}

	public HashMap preOperatorStore(Operator operator, HashMap hm) {
		return delegate.preOperatorStore(operator, hm);
	}

	public HashMap preProfileDelete(Profile profile, HashMap hm) {
		return delegate.preProfileDelete(profile, hm);
	}

	public HashMap preProfileLoad(int profilId, HashMap hm) {
		return delegate.preProfileLoad(profilId, hm);
	}

	public HashMap preProfileStore(Profile profile, HashMap hm) {
		return delegate.preProfileStore(profile, hm);
	}

	public HashMap preProjectDelete(Project project, HashMap hm) {
		return delegate.preProjectDelete(project, hm);
	}

	public HashMap preProjectLoad(int projektId, HashMap hm) {
		return delegate.preProjectLoad(projektId, hm);
	}

	public HashMap preProjectStore(Project project, HashMap hm) {
		return delegate.preProjectStore(project, hm);
	}

	public HashMap preQuestionDelete(Question question, HashMap hm) {
		return delegate.preQuestionDelete(question, hm);
	}

	public HashMap preQuestionLoad(int frageId, HashMap hm) {
		return delegate.preQuestionLoad(frageId, hm);
	}

	public HashMap preQuestionStore(Question question, HashMap hm) {
		return delegate.preQuestionStore(question, hm);
	}

	public HashMap preReleaseLock(de.ityx.mediatrix.data.base.Lock lock, HashMap hm) {
		return delegate.preReleaseLock(lock, hm);
	}

	public HashMap preSetLock(de.ityx.mediatrix.data.base.Lock lock, HashMap hm) {
		return delegate.preSetLock(lock, hm);
	}

	public HashMap preSubprojectDelete(Subproject subproject, HashMap hm) {
		return delegate.preSubprojectDelete(subproject, hm);
	}

	public HashMap preSubprojectLoad(int subprojectId, HashMap hm) {
		return delegate.preSubprojectLoad(subprojectId, hm);
	}

	public HashMap preSubprojectStore(Subproject subproject, HashMap hm) {
		return delegate.preSubprojectStore(subproject, hm);
	}
	
}
