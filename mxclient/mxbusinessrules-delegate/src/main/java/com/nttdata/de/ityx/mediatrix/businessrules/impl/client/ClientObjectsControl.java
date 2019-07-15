package com.nttdata.de.ityx.mediatrix.businessrules.impl.client;

import de.ityx.mediatrix.api.interfaces.businessrules.client.IClientObjectsControl;
import de.ityx.mediatrix.data.*;

import java.util.HashMap;

public class ClientObjectsControl implements IClientObjectsControl {

	/*
	 * Will be called after an attachment has been deleted
	 */
	public HashMap postAttachmentDelete(
			de.ityx.mediatrix.data.Attachment attachment, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Will be called after an attachment has been stored
	 */
	public HashMap postAttachmentStore(
			de.ityx.mediatrix.data.Attachment attachment, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Is called before an attachment will be deleted
	 */
	public HashMap preAttachmentDelete(
			de.ityx.mediatrix.data.Attachment attachment, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Is called before an attachment will be loaded by id
	 */
	public HashMap preAttachmentLoad(int attachmentId, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Is called before an attachment will be loaded
	 */
	public HashMap preAttachmentLoad(
			de.ityx.mediatrix.data.Attachment attachment, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Is called before an attachment will be stored
	 */
	public HashMap preAttachmentStore(
			de.ityx.mediatrix.data.Attachment attachment, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Will be called after a question has been stored
	 */
	public HashMap postQuestionStore(Question question, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Is called before a question will be stored.
	 */
	public HashMap preQuestionStore(Question question, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Will be called after a case has been stored
	 */
	public HashMap postCaseStore(Case ccase, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Is called before a case will be stored
	 */
	public HashMap preCaseStore(Case ccase, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Will be called after an answer has been deleted
	 */
	public HashMap postAnswerDelete(Answer answer, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Will be called after an answer has been loaded
	 */
	public HashMap postAnswerLoad(Answer answer, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Will be called after an answer has been loaded
	 */
	public HashMap postAnswerLoadByEmailid(Answer answer, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Will be called after an answer has been stored
	 */
	public HashMap postAnswerStore(Answer answer, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Will be called after an attachment has been loaded
	 */
	public HashMap postAttachmentLoad(
			de.ityx.mediatrix.data.Attachment attachment, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Will be called after a case has been deleted
	 */
	public HashMap postCaseDelete(Case ccase, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Will be called after a case has been loaded
	 */
	public HashMap postCaseLoad(Case ccase, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Will be called after a customer has been deleted
	 */
	public HashMap postCustomerDelete(Customer customer, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Will be called after a customer has been loaded
	 */
	public HashMap postCustomerLoad(Customer customer, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Will be called after a customer has been stored
	 */
	public HashMap postCustomerStore(Customer customer, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Will be called after an operator has been loaded
	 */
	public HashMap postOperatorLoad(Operator operator, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Will be called after an operator has been stored
	 */
	public HashMap postOperatorStore(Operator operator, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Will be called after a profile has been deleted
	 */
	public HashMap postProfileDelete(Profile profile, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Will be called after a profile has been loaded
	 */
	public HashMap postProfileLoad(Profile profile, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Will be called after a profile has been stored
	 */
	public HashMap postProfileStore(Profile profile, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Will be called after a project has been deleted
	 */
	public HashMap postProjectDelete(Project project, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Will be called after a project has been loaded
	 */
	public HashMap postProjectLoad(Project project, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Will be called after a project has been stored
	 */
	public HashMap postProjectStore(Project project, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Will be called after a question has been deleted
	 */
	public HashMap postQuestionDelete(Question question, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Will be called after a question has been loaded
	 */
	public HashMap postQuestionLoad(Question question, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Will be called after a lock has been released
	 */
	public HashMap postReleaseLock(de.ityx.mediatrix.data.base.Lock lock,
			String result, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Will be called after a lock has been set
	 */
	public HashMap postSetLock(de.ityx.mediatrix.data.base.Lock lock,
			de.ityx.mediatrix.data.base.LockResult result, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Will be called after a subproject has been deleted
	 */
	public HashMap postSubprojectDelete(Subproject subproject, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Will be called after a subproject has been loaded
	 */
	public HashMap postSubprojectLoad(Subproject subproject, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Will be stored after a subproject has been stored
	 */
	public HashMap postSubprojectStore(Subproject subproject, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Is called before an answer will be deleted
	 */
	public HashMap preAnswerDelete(Answer answer, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Is Called before an answer will be loaded by id of answer
	 */
	public HashMap preAnswerLoad(int answerId, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Is called before an answer will be loaded by the id of an email
	 */
	public HashMap preAnswerLoadByEmailid(int emailId, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Is called before an answer will be stored
	 */
	public HashMap preAnswerStore(Answer answer, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Is called before a case will be deleted
	 */
	public HashMap preCaseDelete(Case ccase, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Is called before a case will be loaded by id
	 */
	public HashMap preCaseLoad(int caseId, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Is called before a customer will be deleted
	 */
	public HashMap preCustomerDelete(Customer customer, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Is called before a customer will be loaded by id of a customer
	 */
	public HashMap preCustomerLoad(int kundeId, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Is called before a custober will be stored
	 */
	public HashMap preCustomerStore(Customer customer, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Is called before an operator will be loaded by id
	 */
	public HashMap preOperatorLoad(int operatorId, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Is called before an operator will be stored
	 */
	public HashMap preOperatorStore(Operator operator, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Is called before a profile will be deleted
	 */
	public HashMap preProfileDelete(Profile profile, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Is called before a profile will be loaded by id
	 */
	public HashMap preProfileLoad(int profilId, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Is called before a profile will be stored
	 */
	public HashMap preProfileStore(Profile profile, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Is called before a project will be deleted
	 */
	public HashMap preProjectDelete(Project project, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Is called before a project will be loaded by id
	 */
	public HashMap preProjectLoad(int projektId, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Is called before a project will be stored
	 */
	public HashMap preProjectStore(Project project, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Is called before a question will be deleted
	 */
	public HashMap preQuestionDelete(Question question, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Is called before a question will be loaded
	 */
	public HashMap preQuestionLoad(int frageId, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Is called before a lock will be released
	 */
	public HashMap preReleaseLock(de.ityx.mediatrix.data.base.Lock lock,
			HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Is called before the lock will be set
	 */
	public HashMap preSetLock(de.ityx.mediatrix.data.base.Lock lock, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Is called before a subproject will be deleted
	 */
	public HashMap preSubprojectDelete(Subproject subproject, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Is called before a subproject will be loaded by id
	 */
	public HashMap preSubprojectLoad(int subprojectId, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}

	/*
	 * Is called before a subproject will be stored
	 */
	public HashMap preSubprojectStore(Subproject subproject, HashMap hm) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return new HashMap();
	}
}
