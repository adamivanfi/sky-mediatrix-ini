package de.ityx.sky.outbound.client.base;

import java.util.List;

import javax.swing.JComponent;

import de.ityx.mediatrix.api.interfaces.businessrules.client.IClientQuestionAnswerView;
import de.ityx.mediatrix.api.interfaces.textobjects.ITextObject;
import de.ityx.mediatrix.data.Answer;
import de.ityx.mediatrix.data.Attachment;
import de.ityx.mediatrix.data.Keyword;
import de.ityx.mediatrix.data.Question;
import de.ityx.mediatrix.data.Subproject;
import de.ityx.mediatrix.data.metainformation.MetaInformationInt;

public abstract class ClientBaseQuestionAnswerView implements IClientQuestionAnswerView {

    @Override
    public void postAnswerSend(String loginname, boolean isOperatorMode, Question question, Answer answer) {
    }

    @Override
    public List<JComponent> getTabList(Question question, Answer answer) {

        return null;
    }

    @Override
    public boolean preQuestionAnswerView(String loginname, boolean isOperatorMode, Question question, Answer answer) {

        return true;
    }

    @Override
    public boolean preKeywordInsert(String loginname, boolean isOperatorMode, Question question, Answer answer, Keyword keyword) {

        return true;
    }

    @Override
    public void postKeywordInsert(String loginname, boolean isOperatorMode, Question question, Answer answer, Keyword keyword) {

    }

    @Override
    public boolean preKeywordDelete(String loginname, boolean isOperatorMode, Question question, Answer answer, Keyword keyword) {

        return true;
    }

    @Override
    public void postKeywordDelete(String loginname, boolean isOperatorMode, Question question, Answer answer, Keyword keyword) {

    }

    @Override
    public boolean preTextObjectInsert(String loginname, boolean isOperatorMode, Question question, Answer answer, ITextObject tb) {

        return true;
    }

    @Override
    public void postTextObjectInsert(String loginname, boolean isOperatorMode, Question question, Answer answer, ITextObject tb) {

    }

    @Override
    public boolean preQuestionComplete(String loginname, boolean isOperatorMode, Question question, Answer answer) {

        return true;
    }

    @Override
    public void postQuestionComplete(String loginname, boolean isOperatorMode, Question question, Answer answer) {

    }

    @Override
    public boolean preQuestionForward(String loginname, boolean isOperatorMode, Question question, Answer answer, Subproject teilprojekt, String adresse, int typ) {

        return true;
    }

    @Override
    public void postQuestionForward(String loginname, boolean isOperatorMode, Question question, Answer answer, Subproject subproject, String adresse, int typ) {

    }

    @Override
    public boolean preQuestionRequeue(String loginname, boolean isOperatorMode, Question question, Answer answer, Subproject subproject, int mitarbeiterId, long datum) {

        return true;
    }

    @Override
    public void postQuestionRequeue(String loginname, boolean isOperatorMode, Question question, Answer answer, Subproject subproject, int operatorId, long date) {

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
    public boolean preCancel(String loginname, boolean isOperatorMode, Question question, Answer answer) {

        return true;
    }

    @Override
    public void postCancel(String loginname, boolean isOperatorMode, Question question, Answer answer) {

    }

    @Override
    public boolean preQuestionStore(String loginname, boolean isOperatorMode, Question question, Answer answer) {

        return true;
    }

    @Override
    public void postQuestionStore(String loginname, boolean isOperatorMode, Question question, Answer answer) {

    }

    @Override
    public boolean preMetaInformationView(String login, boolean operatorMode, Question question, Answer answer, MetaInformationInt metainfo) {

        return true;
    }

    @Override
    public void postMetaInformationView(String login, boolean isOperatorMode, Question question, Answer answer, MetaInformationInt metainfo) {

    }

    @Override
    public boolean preCreateMultiTopicMail(String loginname, boolean isOperatorMode, Question sourceQuestion, Question destinationQuestion) {

        return true;
    }

}
