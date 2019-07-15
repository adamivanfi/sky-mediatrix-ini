package com.nttdata.de.ityx.mediatrix.businessrules.delegate.client;

import de.ityx.mediatrix.api.interfaces.businessrules.client.IClientAnswerEdit;
import de.ityx.mediatrix.api.interfaces.gui.BaseAnswerEditorPanel;
import de.ityx.mediatrix.api.interfaces.textobjects.ITextObject;
import de.ityx.mediatrix.data.Answer;
import de.ityx.mediatrix.data.Attachment;

import javax.swing.*;
import java.util.List;

public class ClientAnswerEdit_delegate implements IClientAnswerEdit {

	IClientAnswerEdit delegate = com.nttdata.de.ityx.mediatrix.businessrules.factories.NTTDataBusinessruleFactoryGenerator.createFactory(false).getClientAnswerEdit();
	
	@Override
	public List<AbstractButton> getExtButtonList(BaseAnswerEditorPanel editor, Answer answer) {
		return delegate.getExtButtonList(editor, answer);
	}

	@Override
	public List<JComponent> getTabList(BaseAnswerEditorPanel editor, Answer answer) {
		return delegate.getTabList(editor, answer);
	}

	@Override
	public boolean preAnswerEdit(BaseAnswerEditorPanel editor, Answer answer) {
		return delegate.preAnswerEdit(editor, answer);
	}

	@Override
	public boolean preHistoryInsert(BaseAnswerEditorPanel editor, Answer answer, Attachment attachment) {
		return delegate.preHistoryInsert(editor, answer, attachment);
	}

	@Override
	public boolean postHistoryInsert(BaseAnswerEditorPanel editor, Answer answer, Attachment attachment) {
		return delegate.postHistoryInsert(editor, answer, attachment);
	}

	@Override
	public boolean preAttachmentDelete(BaseAnswerEditorPanel editor, Answer answer, Attachment attachment) {
		return delegate.preAttachmentDelete(editor, answer, attachment);
	}

	@Override
	public boolean postAttachmentDelete(BaseAnswerEditorPanel editor, Answer answer, Attachment attachment) {
		return delegate.postAttachmentDelete(editor, answer, attachment);
	}

	@Override
	public boolean preAttachmentInsert(BaseAnswerEditorPanel editor, Answer answer, Attachment attachment) {
		return delegate.preAttachmentInsert(editor, answer, attachment);
	}

	@Override
	public boolean postAttachmentInsert(BaseAnswerEditorPanel editor, Answer answer, Attachment attachment) {
		return delegate.postAttachmentInsert(editor, answer, attachment);
	}

	@Override
	public boolean preAttachmentView(BaseAnswerEditorPanel editor, Answer answer, Attachment attachment) {
		return delegate.preAttachmentView(editor, answer, attachment);
	}

	@Override
	public boolean postAttachmentView(BaseAnswerEditorPanel editor, Answer answer, Attachment attachment) {
		return delegate.postAttachmentView(editor, answer, attachment);
	}

	@Override
	public boolean preTextObjectInsert(BaseAnswerEditorPanel editor, Answer answer, ITextObject tb) {
		return delegate.preTextObjectInsert(editor, answer, tb);
	}

	@Override
	public void postTextObjectInsert(BaseAnswerEditorPanel editor, Answer answer, ITextObject tb) {
		delegate.postTextObjectInsert(editor, answer, tb);
	}

	@Override
	public void postAnswerEdit(BaseAnswerEditorPanel arg0, Answer arg1) {
		delegate.postAnswerEdit(arg0, arg1);
	}

}
