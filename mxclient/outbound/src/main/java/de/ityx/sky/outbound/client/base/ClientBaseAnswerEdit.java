package de.ityx.sky.outbound.client.base;

import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JComponent;

import de.ityx.mediatrix.api.interfaces.businessrules.client.IClientAnswerEdit;
import de.ityx.mediatrix.api.interfaces.gui.BaseAnswerEditorPanel;
import de.ityx.mediatrix.api.interfaces.textobjects.ITextObject;
import de.ityx.mediatrix.data.Answer;
import de.ityx.mediatrix.data.Attachment;

public abstract class ClientBaseAnswerEdit implements IClientAnswerEdit {

    @Override
    public List<AbstractButton> getExtButtonList(BaseAnswerEditorPanel editor, Answer answer) {

        return null;
    }

    @Override
    public List<JComponent> getTabList(BaseAnswerEditorPanel editor, Answer answer) {

        return null;
    }

    @Override
    public boolean preAnswerEdit(BaseAnswerEditorPanel editor, Answer answer) {
        return true;
    }

    @Override
    public boolean preHistoryInsert(BaseAnswerEditorPanel editor, Answer answer, Attachment attachment) {

        return true;
    }

    @Override
    public boolean postHistoryInsert(BaseAnswerEditorPanel editor, Answer answer, Attachment attachment) {

        return true;
    }

    @Override
    public boolean preAttachmentDelete(BaseAnswerEditorPanel editor, Answer answer, Attachment attachment) {

        return true;
    }

    @Override
    public boolean postAttachmentDelete(BaseAnswerEditorPanel editor, Answer answer, Attachment attachment) {

        return true;
    }

    @Override
    public boolean preAttachmentInsert(BaseAnswerEditorPanel editor, Answer answer, Attachment attachment) {

        return true;
    }

    @Override
    public boolean postAttachmentInsert(BaseAnswerEditorPanel editor, Answer answer, Attachment attachment) {

        return true;
    }

    @Override
    public boolean preAttachmentView(BaseAnswerEditorPanel editor, Answer answer, Attachment attachment) {

        return true;
    }

    @Override
    public boolean postAttachmentView(BaseAnswerEditorPanel editor, Answer answer, Attachment attachment) {

        return true;
    }

    @Override
    public boolean preTextObjectInsert(BaseAnswerEditorPanel editor, Answer answer, ITextObject tb) {

        return true;
    }

    @Override
    public void postTextObjectInsert(BaseAnswerEditorPanel editor, Answer answer, ITextObject tb) {

    }

}
