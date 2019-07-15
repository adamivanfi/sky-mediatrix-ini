package com.nttdata.de.ityx.mediatrix.businessrules.delegate.client;

import de.ityx.mediatrix.api.interfaces.businessrules.client.IClientQuestionAnswerView;
import de.ityx.mediatrix.api.interfaces.textobjects.ITextObject;
import de.ityx.mediatrix.data.*;
import de.ityx.mediatrix.data.metainformation.MetaInformationInt;

import javax.swing.*;
import java.util.List;

public class ClientQuestionAnswerView_delegate implements IClientQuestionAnswerView {

	IClientQuestionAnswerView delegate = com.nttdata.de.ityx.mediatrix.businessrules.factories.NTTDataBusinessruleFactoryGenerator.createFactory(false).getClientQuestionAnswerViewRule();

	public List<AbstractButton> getExtButtonList(Question question) {
		return delegate.getExtButtonList(question);
	}

	public void postAnswerSend(String loginname, boolean isOperatorMode, Question question, Answer answer) {
		delegate.postAnswerSend(loginname, isOperatorMode, question, answer);
	}

	public boolean postAttachmentDelete(String loginname, boolean isOperatorMode, Question question, Answer answer, Attachment attachment) {
		return delegate.postAttachmentDelete(loginname, isOperatorMode, question, answer, attachment);
	}

	public List<JComponent> getTabList(Question question, Answer answer) {
		return delegate.getTabList(question, answer);
	}

	public boolean postAttachmentInsert(String loginname, boolean isOperatorMode, Question question, Answer answer, Attachment attachment) {
		return delegate.postAttachmentInsert(loginname, isOperatorMode, question, answer, attachment);
	}

	public boolean postAttachmentView(String loginname, boolean isOperatorMode, Question question, Answer answer, Attachment attachment) {
		return delegate.postAttachmentView(loginname, isOperatorMode, question, answer, attachment);
	}

	public void postCancel(String loginname, boolean isOperatorMode, Question question, Answer answer) {
		delegate.postCancel(loginname, isOperatorMode, question, answer);
	}

	public boolean postHistoryInsert(String loginname, boolean isOperatorMode, Question question, Answer answer, Attachment attachment) {
		return delegate.postHistoryInsert(loginname, isOperatorMode, question, answer, attachment);
	}

	public void postKeywordDelete(String loginname, boolean isOperatorMode, Question question, Answer answer, Keyword keyword) {
		delegate.postKeywordDelete(loginname, isOperatorMode, question, answer, keyword);
	}

	public void postKeywordInsert(String loginname, boolean isOperatorMode, Question question, Answer answer, Keyword keyword) {
		delegate.postKeywordInsert(loginname, isOperatorMode, question, answer, keyword);
	}

	public void postMetaInformationView(String login, boolean isOperatorMode, Question question, Answer answer, MetaInformationInt metainfo) {
		delegate.postMetaInformationView(login, isOperatorMode, question, answer, metainfo);
	}

	public void postQuestionAnswerView(String loginname, boolean isOperatorMode, Question question, Answer answer) {
		delegate.postQuestionAnswerView(loginname, isOperatorMode, question, answer);
	}

	public void postQuestionComplete(String loginname, boolean isOperatorMode, Question question, Answer answer) {
		delegate.postQuestionComplete(loginname, isOperatorMode, question, answer);
	}

	public void postQuestionForward(String loginname, boolean isOperatorMode, Question question, Answer answer, Subproject subproject, String address, int type) {
		delegate.postQuestionForward(loginname, isOperatorMode, question, answer, subproject, address, type);
	}

	public void postQuestionRequeue(String arg0, boolean arg1, Question arg2, Answer arg3, Subproject arg4, int arg5, long arg6) {
		delegate.postQuestionRequeue(arg0, arg1, arg2, arg3, arg4, arg5, arg6);
	}

	public void postQuestionStore(String loginname, boolean isOperatorMode, Question question, Answer answer) {
		delegate.postQuestionStore(loginname, isOperatorMode, question, answer);
	}

	public void postTextObjectInsert(String loginname, boolean isOperatorMode, Question question, Answer answer, ITextObject tb) {
		delegate.postTextObjectInsert(loginname, isOperatorMode, question, answer, tb);
	}

	public boolean preAnswerSend(String loginname, boolean isOperatorModus, Question question, Answer answer) {
		return delegate.preAnswerSend(loginname, isOperatorModus, question, answer);
	}

	public boolean preAttachmentDelete(String loginname, boolean isOperatorMode, Question question, Answer answer, Attachment attachment) {
		return delegate.preAttachmentDelete(loginname, isOperatorMode, question, answer, attachment);
	}

	public boolean preAttachmentInsert(String loginname, boolean isOperatorMode, Question question, Answer answer, Attachment attachment) {
		return delegate.preAttachmentInsert(loginname, isOperatorMode, question, answer, attachment);
	}

	public boolean preAttachmentView(String loginname, boolean isOperatorMode, Question question, Answer answer, Attachment attachment) {
		return delegate.preAttachmentView(loginname, isOperatorMode, question, answer, attachment);
	}

	public boolean preCancel(String loginname, boolean isOperatorMode, Question question, Answer answer) {
		return delegate.preCancel(loginname, isOperatorMode, question, answer);
	}

	public boolean preCreateMultiTopicMail(String loginname, boolean isOperatorModus, Question sourceQuestion, Question destinationQuestion) {
		return delegate.preCreateMultiTopicMail(loginname, isOperatorModus, sourceQuestion, destinationQuestion);
	}

	public boolean preHistoryInsert(String loginname, boolean isOperatorMode, Question question, Answer answer, Attachment attachment) {
		return delegate.preHistoryInsert(loginname, isOperatorMode, question, answer, attachment);
	}

	public boolean preKeywordDelete(String loginname, boolean isOperatorMode, Question question, Answer answer, Keyword keyword) {
		return delegate.preKeywordDelete(loginname, isOperatorMode, question, answer, keyword);
	}

	public boolean preKeywordInsert(String loginname, boolean isOperatorMode, Question question, Answer answer, Keyword keyword) {
		return delegate.preKeywordInsert(loginname, isOperatorMode, question, answer, keyword);
	}

	public boolean preMetaInformationView(String login, boolean operatorMode, Question question, Answer answer, MetaInformationInt metainfo) {
		return delegate.preMetaInformationView(login, operatorMode, question, answer, metainfo);
	}

	public boolean preQuestionAnswerView(String loginname, boolean isOperatorMode, Question question, Answer answer) {
		return delegate.preQuestionAnswerView(loginname, isOperatorMode, question, answer);
	}

	public boolean preQuestionComplete(String loginname, boolean isOperatorModus, Question question, Answer answer) {
		return delegate.preQuestionComplete(loginname, isOperatorModus, question, answer);
	}

	public boolean preQuestionForward(String loginname, boolean isOperatorMode, Question question, Answer answer, Subproject subproject, String address, int type) {
		return delegate.preQuestionForward(loginname, isOperatorMode, question, answer, subproject, address, type);
	}

	public boolean preQuestionRequeue(String arg0, boolean arg1, Question arg2, Answer arg3, Subproject arg4, int arg5, long arg6) {
		return delegate.preQuestionRequeue(arg0, arg1, arg2, arg3, arg4, arg5, arg6);
	}

	public boolean preQuestionStore(String loginname, boolean isOperatorModus, Question frage, Answer antwort) {
		return delegate.preQuestionStore(loginname, isOperatorModus, frage, antwort);
	}

	public boolean preTextObjectInsert(String loginname, boolean isOperatorMode, Question question, Answer answer, ITextObject tb) {
		return delegate.preTextObjectInsert(loginname, isOperatorMode, question, answer, tb);
	}

}
