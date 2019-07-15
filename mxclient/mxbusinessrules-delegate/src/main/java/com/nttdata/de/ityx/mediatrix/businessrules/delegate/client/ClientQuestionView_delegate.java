package com.nttdata.de.ityx.mediatrix.businessrules.delegate.client;

import de.ityx.mediatrix.api.interfaces.businessrules.client.IClientQuestionView;
import de.ityx.mediatrix.data.*;
import de.ityx.mediatrix.data.metainformation.MetaInformationInt;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;

public class ClientQuestionView_delegate implements IClientQuestionView {

	IClientQuestionView delegate = com.nttdata.de.ityx.mediatrix.businessrules.factories.NTTDataBusinessruleFactoryGenerator.createFactory(false).getClientQuestionViewRule();

	public List<AbstractButton> getExtButtonList(Question frage) {
		return delegate.getExtButtonList(frage);
	}

	public List<JComponent> getTabList(Question question) {
		return delegate.getTabList(question);
	}

	public boolean postAttachmentDelete(String loginname, boolean isOperatorMode, Question question, Attachment attachment) {
		return delegate.postAttachmentDelete(loginname, isOperatorMode, question, attachment);
	}

	public boolean postAttachmentInsert(String loginname, boolean isOperatorMode, Question question, Attachment attachment) {
		return delegate.postAttachmentInsert(loginname, isOperatorMode, question, attachment);
	}

	public boolean postAttachmentView(String loginname, boolean isOperatorMode, Question question, Attachment attachment) {
		return delegate.postAttachmentView(loginname, isOperatorMode, question, attachment);
	}

	public void postKeywordDelete(String loginname, boolean isOperatorMode, Question question, Keyword keyword) {
		delegate.postKeywordDelete(loginname, isOperatorMode, question, keyword);
	}

	public void postKeywordInsert(String loginname, boolean isOperatorMode, Question question, Answer answer, Keyword keyword) {
		delegate.postKeywordInsert(loginname, isOperatorMode, question, answer, keyword);
	}

	public Object postMetaInformationView(String login, boolean isOperatorMode, Question question, MetaInformationInt metaInfo) {
		return delegate.postMetaInformationView(login, isOperatorMode, question, metaInfo);
	}

	public void postQuestionComplete(String loginname, boolean isOperatorMode, Question question) {
		delegate.postQuestionComplete(loginname, isOperatorMode, question);
	}

	public void postQuestionForward(String loginname, boolean isOperatorModus, Question frage, Subproject teilprojekt, String adresse, int typ) {
		delegate.postQuestionForward(loginname, isOperatorModus, frage, teilprojekt, adresse, typ);
	}

	public boolean postQuestionMerge(Question question, Question tempQuestion, boolean mailinbox, HashMap hm) {
		return delegate.postQuestionMerge(question, tempQuestion, mailinbox, hm);
	}

	public void postQuestionProcessing(String loginname, boolean isOperatorMode, Question question) {
		delegate.postQuestionProcessing(loginname, isOperatorMode, question);
	}

	public void postQuestionRequeue(String arg0, boolean arg1, Question arg2, Subproject arg3, int arg4, long arg5) {
		delegate.postQuestionRequeue(arg0, arg1, arg2, arg3, arg4, arg5);
	}

	public void postQuestionStore(String loginname, boolean isOperatorMode, Question question) {
		delegate.postQuestionStore(loginname, isOperatorMode, question);
	}

	public void postQuestionView(String loginname, Question question, Case ccase) {
		delegate.postQuestionView(loginname, question, ccase);
	}

	public boolean preAttachmentDelete(String loginname, boolean isOperatorMode, Question question, Attachment attachment) {
		return delegate.preAttachmentDelete(loginname, isOperatorMode, question, attachment);
	}

	public boolean preAttachmentInsert(String loginname, boolean isOperatorMode, Question question, Attachment attachment) {
		return delegate.preAttachmentInsert(loginname, isOperatorMode, question, attachment);
	}

	public boolean preAttachmentView(String loginname, boolean isOperatorMode, Question question, Attachment attachment) {
		return delegate.preAttachmentView(loginname, isOperatorMode, question, attachment);
	}

	public boolean preKeywordDelete(String loginname, boolean isOperatorMode, Question question, Keyword keyword) {
		return delegate.preKeywordDelete(loginname, isOperatorMode, question, keyword);
	}

	public boolean preKeywordInsert(String loginname, boolean isOperatorMode, Question question, Answer answer, Keyword keyword) {
		return delegate.preKeywordInsert(loginname, isOperatorMode, question, answer, keyword);
	}

	public boolean preMetaInformationView(String login, boolean isOperatorMode, Question question, MetaInformationInt metaInfo) {
		return delegate.preMetaInformationView(login, isOperatorMode, question, metaInfo);
	}

	public boolean preQuestionComplete(String loginname, boolean oper, Question frage) {
		return delegate.preQuestionComplete(loginname, oper, frage);
	}

	public boolean preQuestionForward(String loginname, boolean isOperatorMode, Question question, Subproject subproject, String address, int type) {
		return delegate.preQuestionForward(loginname, isOperatorMode, question, subproject, address, type);
	}

	public boolean preQuestionMerge(Question frage, Question tempFrage, boolean mailinbox, HashMap hm) {
		return delegate.preQuestionMerge(frage, tempFrage, mailinbox, hm);
	}

	public boolean preQuestionProcessing(String loginname, boolean isOperatorMode, Question question) {
		return delegate.preQuestionProcessing(loginname, isOperatorMode, question);
	}

	public boolean preQuestionRequeue(String arg0, boolean arg1, Question arg2, Subproject arg3, int arg4, long arg5) {
		return delegate.preQuestionRequeue(arg0, arg1, arg2, arg3, arg4, arg5);
	}

	public boolean preQuestionStore(String loginname, boolean isOperatorModus, Question frage) {
		return delegate.preQuestionStore(loginname, isOperatorModus, frage);
	}

	public boolean preQuestionView(String loginname, Question frage, Case vorgang) {
		return delegate.preQuestionView(loginname, frage, vorgang);
	}

}
