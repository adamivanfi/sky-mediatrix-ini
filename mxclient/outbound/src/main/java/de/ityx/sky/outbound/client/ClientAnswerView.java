package de.ityx.sky.outbound.client;

import com.nttdata.de.sky.archive.ClientUtils;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import com.nttdata.de.sky.pdf.ClientTemplateExtension;
import com.nttdata.de.sky.pdf.PdfFile;
import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.client.dialog.Start;
import de.ityx.mediatrix.client.dialog.objects.AnswerViewer;
import de.ityx.mediatrix.client.dialog.operate.QuestionAnswer;
import de.ityx.mediatrix.client.util.Repository;
import de.ityx.mediatrix.data.Answer;
import de.ityx.mediatrix.data.Case;
import de.ityx.mediatrix.data.Email;
import de.ityx.mediatrix.data.Question;
import de.ityx.sky.outbound.client.base.ClientBaseAnswerView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * to override the default behaviour of the preview button.
 */
public class ClientAnswerView extends ClientBaseAnswerView {
    @Override
    public boolean preAnswerView(String loginname, Question question, Answer answer, Case ccase) {
        return true;
    }

    @Override
    public void postAnswerView(String loginname, Question question, Answer answer, Case ccase) {
        initButtons(question, answer);
    }

    private void initButtons(Question question, Answer answer) {
        final AnswerViewer editor = (AnswerViewer) Repository.getObject(Repository.ANSWERVIEW);
        JButton jbPreview = (JButton) editor.getButton(QuestionAnswer.PREVIEW_BUTTON);
        jbPreview.setVisible(true);
        jbPreview.setEnabled(true);
        
        ActionListener[] listeners = jbPreview.getActionListeners();
        for (int i = 0; i < listeners.length; i++) {
            jbPreview.removeActionListener(listeners[i]);
        }
        jbPreview.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Answer answer = editor.getAntwort();
                    if (answer.getType() == Email.TYPE_LETTER|| answer.getType() == Email.TYPE_FAX ) {
                        Question question = API.getClientAPI().getQuestionAPI().load(answer.getQuestionId());
                        byte[] pdf = ClientTemplateExtension.getInstance().createPdf(new PdfFile(answer.getBody(), question.getSubprojectId(), question.getLanguage(), !TagMatchDefinitions.isSbsProject(question), ClientUtils.getParameter(answer, false)), null);
                        ClientUtils.preview(pdf);
                    }
                    else {
                        editor.getAnswerEmailPanel().preview();
                    }
                }
                catch(Exception ex) {
                    JOptionPane.showMessageDialog(editor, ClientUtils.exception(ex).toString());
                }
            }
        });

        JButton jbSend = (JButton) editor.getButton(QuestionAnswer.AC_SEND);
        jbSend.setVisible(ClientUtils.canSend(question, answer));
        jbSend.setEnabled(ClientUtils.canSend(question, answer));
    }

    @Override
    public boolean preAnswerSend(String loginname, boolean isOperatorMode, Question question, Answer answer) {
        if (answer.getType() == Email.TYPE_LETTER || answer.getType() == Email.TYPE_FAX) {
            try {
                byte[] pdf = ClientTemplateExtension.getInstance().createPdf(new PdfFile(answer.getBody(), question.getSubprojectId(), question.getLanguage(), !TagMatchDefinitions.isSbsProject(question), ClientUtils.getParameter(answer, false)), null);
                ClientUtils.preview(pdf);
                if (!question.getStatus().equals(Question.S_MONITORED) && ClientUtils.isNotMonitored(question)) {
                    ClientUtils.insertAttachments(answer, pdf);
                }
            }
            catch(Exception e) {
                JOptionPane.showMessageDialog(Start.getInstance(), ClientUtils.exception(e).toString());
                return false;
            }
        }
        return true;
    }

}
