package com.nttdata.de.ityx.mediatrix.businessrules.delegate.client;

import de.ityx.mediatrix.api.interfaces.businessrules.client.IClientAnswerView;
import de.ityx.mediatrix.api.interfaces.textobjects.ITextObject;
import de.ityx.mediatrix.data.Answer;
import de.ityx.mediatrix.data.Attachment;
import de.ityx.mediatrix.data.Case;
import de.ityx.mediatrix.data.Question;
import de.ityx.mediatrix.data.metainformation.MetaInformationInt;

import javax.swing.*;
import java.util.List;

public class ClientAnswerView_delegate implements IClientAnswerView {

	IClientAnswerView delegate = com.nttdata.de.ityx.mediatrix.businessrules.factories.NTTDataBusinessruleFactoryGenerator.createFactory(false).getClientAnswerView();
	
	@Override
	public void postAnswerSend(String loginname, boolean isOperatorMode, Question question, Answer answer) {
		delegate.postAnswerSend(loginname, isOperatorMode, question, answer);
	}

	@Override
	public List<AbstractButton> getExtButtonList(Answer answer) {
		return delegate.getExtButtonList(answer);
	}

	@Override
	public List<JComponent> getTabList(Question question, Answer answer) {
		return delegate.getTabList(question, answer);
	}

	@Override
	public boolean preAttachmentView(String loginname, boolean isOperatorMode, Question question, Answer answer, Attachment attachment) {
		return delegate.preAttachmentView(loginname, isOperatorMode, question, answer, attachment);
	}

	@Override
	public boolean postAttachmentView(String loginname, boolean isOperatorMode, Question question, Answer answer, Attachment attachment) {
		return delegate.postAttachmentView(loginname, isOperatorMode, question, answer, attachment);
	}

	@Override
	public boolean preAttachmentInsert(String loginname, boolean isOperatorMode, Question question, Answer answer, Attachment attachment) {
		return delegate.preAttachmentInsert(loginname, isOperatorMode, question, answer, attachment);
	}

	@Override
	public boolean postAttachmentInsert(String loginname, boolean isOperatorMode, Question question, Answer answer, Attachment attachment) {
		return delegate.postAttachmentInsert(loginname, isOperatorMode, question, answer, attachment);
	}

	@Override
	public boolean preHistoryInsert(String loginname, boolean isOperatorMode, Question question, Answer answer, Attachment attachment) {
		return delegate.preHistoryInsert(loginname, isOperatorMode, question, answer, attachment);
	}

	@Override
	public boolean postHistoryInsert(String loginname, boolean isOperatorMode, Question question, Answer answer, Attachment attachment) {
		return delegate.postHistoryInsert(loginname, isOperatorMode, question, answer, attachment);
	}

	@Override
	public boolean preAttachmentDelete(String loginname, boolean isOperatorMode, Question question, Answer answer, Attachment attachment) {
		return delegate.preAttachmentDelete(loginname, isOperatorMode, question, answer, attachment);
	}

	@Override
	public boolean postAttachmentDelete(String loginname, boolean isOperatorMode, Question question, Answer answer, Attachment attachment) {
		return delegate.postAttachmentDelete(loginname, isOperatorMode, question, answer, attachment);
	}

	@Override
	public boolean preTextObjectInsert(String loginname, boolean isOperatorMode, Question question, Answer answer, ITextObject tb) {
		return delegate.preTextObjectInsert(loginname, isOperatorMode, question, answer, tb);
	}

	@Override
	public void postTextObjectInsert(String loginname, boolean isOperatorMode, Question question, Answer answer, ITextObject tb) {
		delegate.postTextObjectInsert(loginname, isOperatorMode, question, answer, tb);
	}

	@Override
	public void postMetaInformationView(String login, boolean operatorMode, Answer answer, MetaInformationInt metaInfo) {
		delegate.postMetaInformationView(login, operatorMode, answer, metaInfo);
	}

	@Override
	public boolean preMetaInformationView(String login, boolean operatorMode, Answer answer, MetaInformationInt metaInfo) {
		return delegate.preMetaInformationView(login, operatorMode, answer, metaInfo);
	}

	@Override
	public void postAnswerView(String arg0, Question arg1, Answer arg2, Case arg3) {
		delegate.postAnswerView(arg0, arg1, arg2, arg3);
	}

	@Override
	public boolean preAnswerSend(String arg0, boolean arg1, Question arg2, Answer arg3) {
		return delegate.preAnswerSend(arg0, arg1, arg2, arg3);
	}

	@Override
	public boolean preAnswerView(String arg0, Question arg1, Answer arg2, Case arg3) {
		return delegate.preAnswerView(arg0, arg1, arg2, arg3);
	}

}
