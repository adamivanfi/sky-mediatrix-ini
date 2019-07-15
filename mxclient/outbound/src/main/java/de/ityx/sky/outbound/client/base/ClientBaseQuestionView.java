package de.ityx.sky.outbound.client.base;

import java.util.HashMap;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JComponent;

import de.ityx.mediatrix.api.interfaces.businessrules.client.IClientQuestionView;
import de.ityx.mediatrix.data.Answer;
import de.ityx.mediatrix.data.Attachment;
import de.ityx.mediatrix.data.Case;
import de.ityx.mediatrix.data.Keyword;
import de.ityx.mediatrix.data.Question;
import de.ityx.mediatrix.data.Subproject;
import de.ityx.mediatrix.data.metainformation.MetaInformationInt;

public class ClientBaseQuestionView implements IClientQuestionView {

    @Override
    public List<AbstractButton> getExtButtonList(Question arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<JComponent> getTabList(Question arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean postAttachmentDelete(String arg0, boolean arg1, Question arg2, Attachment arg3) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean postAttachmentInsert(String arg0, boolean arg1, Question arg2, Attachment arg3) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean postAttachmentView(String arg0, boolean arg1, Question arg2, Attachment arg3) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public void postKeywordDelete(String arg0, boolean arg1, Question arg2, Keyword arg3) {
        // TODO Auto-generated method stub

    }

    @Override
    public void postKeywordInsert(String arg0, boolean arg1, Question arg2, Answer arg3, Keyword arg4) {
        // TODO Auto-generated method stub

    }

    @Override
    public Object postMetaInformationView(String arg0, boolean arg1, Question arg2, MetaInformationInt arg3) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void postQuestionComplete(String arg0, boolean arg1, Question arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void postQuestionForward(String arg0, boolean arg1, Question arg2, Subproject arg3, String arg4, int arg5) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean postQuestionMerge(Question arg0, Question arg1, boolean arg2, HashMap arg3) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public void postQuestionProcessing(String arg0, boolean arg1, Question arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void postQuestionRequeue(String arg0, boolean arg1, Question arg2, Subproject arg3, int arg4, long arg5) {
        // TODO Auto-generated method stub

    }

    @Override
    public void postQuestionStore(String arg0, boolean arg1, Question arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void postQuestionView(String arg0, Question arg1, Case arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean preAttachmentDelete(String arg0, boolean arg1, Question arg2, Attachment arg3) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean preAttachmentInsert(String arg0, boolean arg1, Question arg2, Attachment arg3) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean preAttachmentView(String arg0, boolean arg1, Question arg2, Attachment arg3) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean preKeywordDelete(String arg0, boolean arg1, Question arg2, Keyword arg3) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean preKeywordInsert(String arg0, boolean arg1, Question arg2, Answer arg3, Keyword arg4) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean preMetaInformationView(String arg0, boolean arg1, Question arg2, MetaInformationInt arg3) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean preQuestionComplete(String arg0, boolean arg1, Question arg2) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean preQuestionForward(String arg0, boolean arg1, Question arg2, Subproject arg3, String arg4, int arg5) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean preQuestionMerge(Question arg0, Question arg1, boolean arg2, HashMap arg3) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean preQuestionProcessing(String arg0, boolean arg1, Question arg2) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean preQuestionRequeue(String arg0, boolean arg1, Question arg2, Subproject arg3, int arg4, long arg5) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean preQuestionStore(String arg0, boolean arg1, Question arg2) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean preQuestionView(String arg0, Question arg1, Case arg2) {
        // TODO Auto-generated method stub
        return true;
    }

}
