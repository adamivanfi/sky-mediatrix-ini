package de.ityx.sky.outbound.client.base;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JMenu;

import de.ityx.mediatrix.api.interfaces.businessrules.client.IClientNewEmail;
import de.ityx.mediatrix.api.interfaces.textobjects.ITextObject;
import de.ityx.mediatrix.data.Attachment;
import de.ityx.mediatrix.data.Operator;
import de.ityx.mediatrix.data.Question;
import de.ityx.mediatrix.data.Subproject;

public abstract class ClientBaseNewMailFrame implements IClientNewEmail {

    //    @Override
    //    public void postQuestionSend(String loginname, boolean isOperatorMode, Question question, HashMap localRepository) {
    //    }

    @Override
    public boolean preAttachmentInsert(String loginname, boolean isOperatorMode, Question question, Attachment attachment, HashMap localRepository) {

        return true;
    }

    @Override
    public boolean postAttachmentInsert(String loginname, boolean isOperatorMode, Question question, Attachment attachment, HashMap localRepository) {

        return true;
    }

    @Override
    public boolean preTextObjectInsert(String loginname, boolean isOperatorMode, Question question, ITextObject tb, HashMap localRepository) {

        return true;
    }

    @Override
    public void postTextObjectInsert(String loginname, boolean isOperatorMode, Question question, ITextObject tb, HashMap localRepository) {

    }

    @Override
    public Map getIdentityExtension(Operator operator, Question question, int projectId, Map localRepository) {

        return null;
    }

    @Override
    public Map getSubjectExtension(Operator operator, Question question, int projectId, Map localRepository) {

        return null;
    }

    @Override
    public Map getReceiverExtension(Operator operator, Question question, int projectId, Map localRepository) {

        return null;
    }

    @Override
    public List<JButton> getToolbarButtons(Operator operator, Question question, int projectId, Map localRepository) {

        return null;
    }

    @Override
    public List<JMenu> getMenuButtons(Operator operator, Question question, int projectId, Map localRepository) {

        return null;
    }

    @Override
    public void subprojectChanged(Subproject selectedTP, HashMap localRepository) {
    }

}
