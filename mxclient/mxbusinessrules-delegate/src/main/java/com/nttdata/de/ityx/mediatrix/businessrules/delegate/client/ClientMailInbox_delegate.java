package com.nttdata.de.ityx.mediatrix.businessrules.delegate.client;

import de.ityx.mediatrix.api.interfaces.businessrules.client.IClientMailInbox;
import de.ityx.mediatrix.data.*;
import de.ityx.mediatrix.data.businessrules.client.QuestionOpenVeto;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientMailInbox_delegate implements IClientMailInbox {

	IClientMailInbox delegate = com.nttdata.de.ityx.mediatrix.businessrules.factories.NTTDataBusinessruleFactoryGenerator.createFactory(false).getClientMailInboxRule();

	public List<JMenuItem> getExtendedMenuItems(List<SingleMode> smodes, Map<Object, Object> map) {
		return delegate.getExtendedMenuItems(smodes, map);
	}

	public void postQuestionComplete(String loginname, Question question) {
		delegate.postQuestionComplete(loginname, question);
	}

	public void postQuestionForward(String loginname, Question question, Subproject subproject, String address, int type) {
		delegate.postQuestionForward(loginname, question, subproject, address, type);
	}

	public boolean postQuestionMerge(Question frage, Question tempFrage, boolean mailinbox, HashMap hm) {
		return delegate.postQuestionMerge(frage, tempFrage, mailinbox, hm);
	}

	public void postQuestionRequeue(String arg0, Question arg1, Subproject arg2, int arg3, long arg4) {
		delegate.postQuestionRequeue(arg0, arg1, arg2, arg3, arg4);
	}

	public boolean preQuestionComplete(String loginname, Question frage) {
		return delegate.preQuestionComplete(loginname, frage);
	}

	public boolean preQuestionForward(String loginname, Question question, Subproject subproject, String address, int type) {
		return delegate.preQuestionForward(loginname, question, subproject, address, type);
	}

	public boolean preQuestionMailPreview(String loginname, Question question, Subproject subproject) {
		return delegate.preQuestionMailPreview(loginname, question, subproject);
	}

	public boolean preQuestionMerge(Question frage, Question tempFrage, boolean mailinbox, HashMap hm) {
		return delegate.preQuestionMerge(frage, tempFrage, mailinbox, hm);
	}

	public void preQuestionOpen(Question question, Answer answer, Customer customer) throws QuestionOpenVeto {
		delegate.preQuestionOpen(question, answer, customer);
	}

	public boolean preQuestionRequeue(String arg0, Question arg1, Subproject arg2, int arg3, long arg4) {
		return delegate.preQuestionRequeue(arg0, arg1, arg2, arg3, arg4);
	}

}
