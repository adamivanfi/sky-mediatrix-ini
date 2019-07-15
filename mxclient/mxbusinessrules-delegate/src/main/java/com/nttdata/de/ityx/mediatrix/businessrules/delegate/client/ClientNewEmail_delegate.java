package com.nttdata.de.ityx.mediatrix.businessrules.delegate.client;

import de.ityx.mediatrix.api.interfaces.businessrules.client.IClientNewEmail;
import de.ityx.mediatrix.api.interfaces.textobjects.ITextObject;
import de.ityx.mediatrix.data.Attachment;
import de.ityx.mediatrix.data.Operator;
import de.ityx.mediatrix.data.Question;
import de.ityx.mediatrix.data.Subproject;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientNewEmail_delegate implements IClientNewEmail {

	IClientNewEmail delegate = com.nttdata.de.ityx.mediatrix.businessrules.factories.NTTDataBusinessruleFactoryGenerator.createFactory(false).getClientNewEmailRule();

	public Map getIdentityExtension(Operator operator, Question frage, int projektId, Map localRepository) {
		return delegate.getIdentityExtension(operator, frage, projektId, localRepository);
	}

	public List<JMenu> getMenuButtons(Operator operator, Question question, int projectId, Map localRepository) {
		return delegate.getMenuButtons(operator, question, projectId, localRepository);
	}

	public Map<Object, Object> getReceiverExtension(Operator operator, Question question, int projectId, Map localRepository) {
		return delegate.getReceiverExtension(operator, question, projectId, localRepository);
	}

	public Map<Object, Object> getSubjectExtension(Operator operator, Question question, int projectId, Map localRepository) {
		return delegate.getSubjectExtension(operator, question, projectId, localRepository);
	}

	public List<JComponent> getTabList(Question question, HashMap localRepository) {
		return delegate.getTabList(question, localRepository);
	}

	public List<JButton> getToolbarButtons(Operator operator, Question question, int projectId, Map localRepository) {
		return delegate.getToolbarButtons(operator, question, projectId, localRepository);
	}

	public boolean postAttachmentInsert(String loginname, boolean isOperatorMode, Question question, Attachment attachment, HashMap localRepository) {
		return delegate.postAttachmentInsert(loginname, isOperatorMode, question, attachment, localRepository);
	}

	public void postQuestionSend(String loginname, boolean isOperatorMode, Question question, HashMap localRepository) {
		delegate.postQuestionSend(loginname, isOperatorMode, question, localRepository);
	}

	public void postTextObjectInsert(String loginname, boolean isOperatorMode, Question question, ITextObject tb, HashMap localRepository) {
		delegate.postTextObjectInsert(loginname, isOperatorMode, question, tb, localRepository);
	}

	public boolean preAttachmentInsert(String loginname, boolean isOperatorMode, Question question, Attachment attachment, HashMap localRepository) {
		return delegate.preAttachmentInsert(loginname, isOperatorMode, question, attachment, localRepository);
	}

	public boolean preQuestionSend(String loginname, boolean oeratorMode, Question question, HashMap localRepository) {
		return delegate.preQuestionSend(loginname, oeratorMode, question, localRepository);
	}

	public boolean preTextObjectInsert(String loginname, boolean isOperatorMode, Question question, ITextObject tb, HashMap localRepository) {
		return delegate.preTextObjectInsert(loginname, isOperatorMode, question, tb, localRepository);
	}

	public void subprojectChanged(Subproject teilprojekt, HashMap arg1) {
		delegate.subprojectChanged(teilprojekt, arg1);
	}

}
