package de.ityx.sky.outbound.client.base;

import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JComponent;

import de.ityx.mediatrix.api.interfaces.businessrules.client.IClientAnswerView;
import de.ityx.mediatrix.api.interfaces.textobjects.ITextObject;
import de.ityx.mediatrix.data.Answer;
import de.ityx.mediatrix.data.Attachment;
import de.ityx.mediatrix.data.Question;
import de.ityx.mediatrix.data.metainformation.MetaInformationInt;

public abstract class ClientBaseAnswerView implements IClientAnswerView {

    @Override
    public void postAnswerSend(String loginname, boolean isOperatorMode, Question question, Answer answer) {
    }

    @Override
    public List<AbstractButton> getExtButtonList(Answer answer) {

        return null;
    }

    @Override
    public List<JComponent> getTabList(Question question, Answer answer) {

        return null;
    }

    @Override
    public boolean preAttachmentView(String loginname, boolean isOperatorMode, Question question, Answer answer, Attachment attachment) {

        return true;
    }

    @Override
    public boolean postAttachmentView(String loginname, boolean isOperatorMode, Question question, Answer answer, Attachment attachment) {

        return true;
    }

    @Override
    public boolean preAttachmentInsert(String loginname, boolean isOperatorMode, Question question, Answer answer, Attachment attachment) {

        return true;
    }

    @Override
    public boolean postAttachmentInsert(String loginname, boolean isOperatorMode, Question question, Answer answer, Attachment attachment) {

        return true;
    }

    @Override
    public boolean preHistoryInsert(String loginname, boolean isOperatorMode, Question question, Answer answer, Attachment attachment) {

        return true;
    }

    @Override
    public boolean postHistoryInsert(String loginname, boolean isOperatorMode, Question question, Answer answer, Attachment attachment) {

        return true;
    }

    @Override
    public boolean preAttachmentDelete(String loginname, boolean isOperatorMode, Question question, Answer answer, Attachment attachment) {

        return true;
    }

    @Override
    public boolean postAttachmentDelete(String loginname, boolean isOperatorMode, Question question, Answer answer, Attachment attachment) {

        return true;
    }

    @Override
    public boolean preTextObjectInsert(String loginname, boolean isOperatorMode, Question question, Answer answer, ITextObject tb) {

        return true;
    }

    @Override
    public void postTextObjectInsert(String loginname, boolean isOperatorMode, Question question, Answer answer, ITextObject tb) {

    }

    @Override
    public void postMetaInformationView(String login, boolean operatorMode, Answer answer, MetaInformationInt metaInfo) {

    }

    @Override
    public boolean preMetaInformationView(String login, boolean operatorMode, Answer answer, MetaInformationInt metaInfo) {

        return true;
    }

}
