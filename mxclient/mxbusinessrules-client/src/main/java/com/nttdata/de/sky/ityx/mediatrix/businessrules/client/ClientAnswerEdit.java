package com.nttdata.de.sky.ityx.mediatrix.businessrules.client;

import com.nttdata.de.lib.logging.SkyLogger;
import de.ityx.mediatrix.api.interfaces.businessrules.client.IClientAnswerEdit;
import de.ityx.mediatrix.api.interfaces.gui.BaseAnswerEditorPanel;
import de.ityx.mediatrix.api.interfaces.textobjects.ITextObject;
import de.ityx.mediatrix.data.Answer;
import de.ityx.mediatrix.data.Attachment;

import javax.swing.*;
import java.util.List;

public class ClientAnswerEdit implements IClientAnswerEdit {

	IClientAnswerEdit outbound_delegate = new de.ityx.sky.outbound.client.ClientAnswerEdit();

	@Override
	public List<AbstractButton> getExtButtonList(BaseAnswerEditorPanel editor,
			Answer answer) {
		List<AbstractButton> ret = null;
		return outbound_delegate.getExtButtonList(editor,answer);
	}

	@Override
	public List<JComponent> getTabList(BaseAnswerEditorPanel editor,
			Answer answer) {
		final List<JComponent> ret = null;
		return outbound_delegate.getTabList(editor,answer);
	}

	@Override
	public final boolean preAnswerEdit(BaseAnswerEditorPanel editor, Answer answer) {
		final boolean ret = true;
		return outbound_delegate.preAnswerEdit(editor,answer);
	}

	@Override
	public final boolean preHistoryInsert(BaseAnswerEditorPanel editor,
			Answer answer, Attachment attachment) {
		final boolean ret = true;
		return outbound_delegate.preHistoryInsert(editor,answer,attachment);
	}

	@Override
	public final boolean postHistoryInsert(BaseAnswerEditorPanel editor,
			Answer answer, Attachment attachment) {
		final boolean ret = true;
		return outbound_delegate.postHistoryInsert(editor,answer,attachment);
	}

	@Override
	public final boolean preAttachmentDelete(BaseAnswerEditorPanel editor,
			Answer answer, Attachment attachment) {
		final boolean ret = true;
		return outbound_delegate.preAttachmentDelete(editor,answer,attachment);
	}

	@Override
	public final boolean postAttachmentDelete(BaseAnswerEditorPanel editor,
			Answer answer, Attachment attachment) {
		final boolean ret = true;
		return outbound_delegate.postAttachmentDelete(editor,answer,attachment);
	}

	@Override
	public final boolean preAttachmentInsert(BaseAnswerEditorPanel editor,
			Answer answer, Attachment attachment) {
		final boolean ret = true;
		return outbound_delegate.preAttachmentInsert(editor,answer,attachment);
	}

	@Override
	public final boolean postAttachmentInsert(BaseAnswerEditorPanel editor,
			Answer answer, Attachment attachment) {
		final boolean ret = true;
		return outbound_delegate.postAttachmentInsert(editor,answer,attachment);
	}

	@Override
	public final boolean preAttachmentView(BaseAnswerEditorPanel editor,
			Answer answer, Attachment attachment) {
		return outbound_delegate.preAttachmentView(editor,answer,attachment);
	}

	@Override
	public final boolean postAttachmentView(BaseAnswerEditorPanel editor,
			Answer answer, Attachment attachment) {
		return outbound_delegate.postAttachmentView(editor,answer,attachment);
	}

	@Override
	public final boolean preTextObjectInsert(BaseAnswerEditorPanel editor,
			Answer answer, ITextObject tb) {
		return outbound_delegate.preTextObjectInsert(editor, answer,tb);
	}

	@Override
	public void postTextObjectInsert(BaseAnswerEditorPanel editor,
			Answer answer, ITextObject tb) {
		outbound_delegate.postTextObjectInsert(editor,answer,tb);
	}

	@Override
	public void postAnswerEdit(BaseAnswerEditorPanel arg0, Answer arg1) {
		SkyLogger.getClientLogger().info("Call outbound delegate.");
		outbound_delegate.postAnswerEdit(arg0, arg1);
	}

}
