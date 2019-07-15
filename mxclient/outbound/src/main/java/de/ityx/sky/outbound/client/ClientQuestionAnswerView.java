package de.ityx.sky.outbound.client;

import com.nttdata.de.sky.archive.ClientUtils;
import com.nttdata.de.sky.archive.Constant;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import com.nttdata.de.sky.pdf.ClientTemplateExtension;
import com.nttdata.de.sky.pdf.PdfFile;
import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.client.dialog.Start;
import de.ityx.mediatrix.client.dialog.objects.answer.AnswerHeader;
import de.ityx.mediatrix.client.dialog.operate.QuestionAnswer;
import de.ityx.mediatrix.client.dialog.util.BigIconComboBox;
import de.ityx.mediatrix.client.util.Repository;
import de.ityx.mediatrix.data.Answer;
import de.ityx.mediatrix.data.Email;
import de.ityx.mediatrix.data.Question;
import de.ityx.mediatrix.data.Subproject;
import de.ityx.mediatrix.modules.businessrules.tools.BRSessionPoolFactory;
import de.ityx.sky.outbound.client.base.ClientBaseQuestionAnswerView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.List;

//import de.ityx.sky.mx.clientevents.ws.ClientEventServiceImpl;

/**
 * to override the default behaviour of the preview button.
 */
public class ClientQuestionAnswerView extends ClientBaseQuestionAnswerView {

    @Override
    public void postQuestionAnswerView(String loginname,
            boolean isOperatorMode, Question question, Answer answer) {
        QuestionAnswer qa = (QuestionAnswer) Repository
                .getObject(Repository.OPERATORMODE);
        final BigIconComboBox iconComboBoxType = qa.getAnswerHeaderPanel()
                .getBigIconComboBox();
        if (answer.getType() == Email.TYPE_DOCUMENT) {
            iconComboBoxType.setSelectedIconValue(Email.TYPE_LETTER);
        }
        initIconComboBox();
        initButtonActions(question, answer);
    }

    @Override
    public boolean preQuestionForward(String loginname, boolean isOperatorMode,
            Question question, Answer answer, Subproject teilprojekt,
            String adresse, int typ) {
        teilprojekt = teilprojekt != null ? API.getClientAPI()
                .getSubprojectAPI().load(teilprojekt.getId())
                : new Subproject();
        if (!teilprojekt.getExternalEmail().trim().equals("")) {
            ClientUtils.addAttachments(question, question.getAttachments());
        }
        return true;
    }

    @Override
    public List<AbstractButton> getExtButtonList(final Question question) {
        return null;
    }

    private void initIconComboBox() {
        QuestionAnswer qa = (QuestionAnswer) Repository
                .getObject(Repository.OPERATORMODE);
        final BigIconComboBox iconComboBoxType = qa.getAnswerHeaderPanel()
                .getBigIconComboBox();
        ItemListener[] listeners = iconComboBoxType.getItemListeners();
        for (int i = 0; i < listeners.length; i++) {
            iconComboBoxType.removeItemListener(listeners[i]);
        }
        iconComboBoxType.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                itemChanged(e, iconComboBoxType);
            }
        });
    }

    private void itemChanged(ItemEvent e, final BigIconComboBox iconComboBoxType) {
        final QuestionAnswer qa = (QuestionAnswer) Repository
                .getObject(Repository.OPERATORMODE);
        if (e.getStateChange() == ItemEvent.SELECTED) {
            if (iconComboBoxType.getSelectedIconValue() == Email.TYPE_LETTER || iconComboBoxType.getSelectedIconValue() == Email.TYPE_FAX) {
                qa.getAnswerEmailPanel()
                        .getAnswerHeader()
                        .setComponentVisible(
                                AnswerHeader.COMPONENTS.CC.toString(), false);
                qa.getAnswerEmailPanel()
                        .getAnswerHeader()
                        .setComponentVisible(
                                AnswerHeader.COMPONENTS.BCC.toString(), false);
                qa.getAnswerEmailPanel()
                        .getAnswerHeader()
                        .setComponentVisible(
                                AnswerHeader.COMPONENTS.SUBJECT.toString(),
                                false);
                qa.getAnswerEmailPanel()
                        .getAnswerHeader()
                        .setComponentVisible(
                                AnswerHeader.COMPONENTS.EXPAND.toString(),
                                false);
                JButton jbPreview = (JButton) qa
                        .getButton(QuestionAnswer.PREVIEW_BUTTON);
                ActionListener[] listeners = jbPreview.getActionListeners();
                for (int i = 0; i < listeners.length; i++) {
                    jbPreview.removeActionListener(listeners[i]);
                }
                jbPreview.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            if (iconComboBoxType.getSelectedIconValue() == Email.TYPE_LETTER || iconComboBoxType.getSelectedIconValue() == Email.TYPE_FAX) {
                                byte[] pdf = ClientTemplateExtension
                                        .getInstance()
                                        .createPdf(
                                                new PdfFile(
                                                        qa.getAnswer()
                                                                .getBody(),
                                                        qa.getQuestion()
                                                                .getSubprojectId(),
                                                        qa.getQuestion()
                                                                .getLanguage(),
                                                        !TagMatchDefinitions.isSbsProject(qa.getQuestion()),
                                                        ClientUtils
                                                                .getParameter(qa
                                                                        .getAnswer(), true)),
                                                null);
                                ClientUtils.preview(pdf);
                            }
                            else {
                                qa.getAnswerEmailPanel().preview();
                            }
                        }
                        catch(Exception ex) {
                            JOptionPane.showMessageDialog(Start.getInstance(),
                                    ClientUtils.exception(ex).toString());
                        }
                    }
                });
                qa.getAnswerEmailPanel().setSubject(Constant.LETTER_SUBJECT);
            }
            else {
                qa.getAnswerEmailPanel().getAnswerHeader().setAllVisible();
                qa.getAnswerEmailPanel()
                        .getAnswerHeader()
                        .setComponentVisible(
                                AnswerHeader.COMPONENTS.CC.toString(), false);
                qa.getAnswerEmailPanel()
                        .getAnswerHeader()
                        .setComponentVisible(
                                AnswerHeader.COMPONENTS.BCC.toString(), false);
            }
        }
    }

    private void initButtonActions(Question question, Answer answer) {
        final QuestionAnswer qa = (QuestionAnswer) Repository
                .getObject(Repository.OPERATORMODE);

        JButton jbPreview = (JButton) qa.getButton("preview");
        jbPreview.setVisible(true);
        jbPreview.setEnabled(true);
        ActionListener[] plisteners = jbPreview.getActionListeners();
        for (int i = 0; i < plisteners.length; i++) {
            jbPreview.removeActionListener(plisteners[i]);
        }
        jbPreview.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent paramActionEvent) {
                try {
                    Answer answer = qa.getAnswer();
                    if (answer.getType() == Email.TYPE_LETTER||answer.getType() == Email.TYPE_FAX) {
                        final HashMap<String, Object> parameter = ClientUtils
                                .getParameter(answer, false);
                        parameter.put("loginname", API.getClientAPI().getConnectionAPI().getCurrentOperatorShort().getName());
                        byte[] pdf = ClientTemplateExtension.getInstance()
                                .createPdf(
                                        new PdfFile(answer.getBody(), qa
                                                .getQuestion()
                                                .getSubprojectId(), qa
                                                .getQuestion().getLanguage(),
                                                !TagMatchDefinitions.isSbsProject(qa.getQuestion()),
                                                parameter),
                                        null);
                        ClientUtils.preview(pdf);
                    }
                    else {
                        qa.getAnswerEmailPanel().preview();
                    }
                }
                catch(Exception e) {
                    JOptionPane.showMessageDialog(Start.getInstance(),
                            ClientUtils.exception(e).toString());
                    return;
                }
            }
        });

        JButton jbSend = (JButton) qa.getButton(QuestionAnswer.SENDEN_BUTTON);
        jbSend.setVisible(ClientUtils.canSend(question, answer));
        jbSend.setEnabled(ClientUtils.canSend(question, answer));
    }

    @Override
    public boolean preAnswerSend(String loginname, boolean isOperatorMode,
            Question question, Answer answer) {
        BRSessionPoolFactory.getBRSessionPool().getBRSession("SkyLetter").put("loginname" + answer.getEmailId(), loginname);
        String value = getValueFromExtraColumn(
                question,
                8
                /*API.getClientAPI()
                        .getServiceCenterAPI()
                        .getServiceCenter()
                        .getIntValue(
                                ClientEventServiceImpl.SKY_CONTACTID_EXTRA, 8)
                                */
        );
        System.err.println("RequestParameter: " + value);
        if (value.length() > 0) {
            try {
                String activityid = null;
                String[] parameters = value.split(";");
                for (String parameter : parameters) {
                    final String[] entry = parameter.split("=");
                    if (entry[0].equals("activityid")) {
                        activityid = entry[1];
                    }
                }
                if (activityid != null) {
                    answer.setExtra8(activityid);
                }
            }
            catch(Exception e) {
                e.printStackTrace();
                // JOptionPane.showMessageDialog(Start.getInstance(),
                // ClientUtils.exception(e).toString());
                // return false;
            }
        }

        if (answer.getType() == Email.TYPE_LETTER || answer.getType() == Email.TYPE_FAX) {
            try {
                byte[] pdf = ClientTemplateExtension.getInstance().createPdf(
                        new PdfFile(answer.getBody(),
                                question.getSubprojectId(),
                                question.getLanguage(),
                                !TagMatchDefinitions.isSbsProject(question),
                                ClientUtils.getParameter(answer, false)), null);
                //                ClientUtils.preview(pdf);
                if (!answer.getStatus().equals(Question.S_MONITORED)
                        && ClientUtils.isNotMonitored(question)) {
                    ClientUtils.insertAttachments(answer, pdf);
                }
            }
            catch(Exception e) {
                JOptionPane.showMessageDialog(Start.getInstance(), ClientUtils
                        .exception(e).toString());
                return false;
            }
            API.getClientAPI().getAnswerAPI().store(answer);
        }
        return true;
    }

    public static String getValueFromExtraColumn(Question question, int column) {
        String result = "";
        switch (column) {
            case 1:
                result = question.getExtra1();
                break;

            case 2:
                result = question.getExtra2();
                break;

            case 3:
                result = question.getExtra3();
                break;

            case 4:
                result = question.getExtra4();
                break;

            case 5:
                result = question.getExtra5();
                break;

            case 6:
                result = question.getExtra6();
                break;

            case 7:
                result = question.getExtra7();
                break;

            case 8:
                result = question.getExtra8();
                break;

            case 9:
                result = question.getExtra9();
                break;

            case 10:
                result = question.getExtra10();
                break;

            case 11:
                result = question.getExtra11();
                break;

            default:
                break;
        }
        return result;
    }
}
