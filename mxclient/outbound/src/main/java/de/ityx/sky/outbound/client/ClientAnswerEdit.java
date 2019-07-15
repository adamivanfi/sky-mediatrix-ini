package de.ityx.sky.outbound.client;

import com.nttdata.de.sky.archive.ClientUtils;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import com.nttdata.de.sky.pdf.ClientTemplateExtension;
import com.nttdata.de.sky.pdf.PdfFile;
import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.api.interfaces.gui.BaseAnswerEditorPanel;
import de.ityx.mediatrix.client.dialog.operate.QuestionAnswer;
import de.ityx.mediatrix.data.Answer;
import de.ityx.mediatrix.data.Email;
import de.ityx.mediatrix.data.Question;
import de.ityx.sky.outbound.client.base.ClientBaseAnswerEdit;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * to override the default behaviour of the preview button.
 */
public class ClientAnswerEdit extends ClientBaseAnswerEdit {
    @Override
    public void postAnswerEdit(final BaseAnswerEditorPanel editor, final Answer answer) {
        previewAnswer(editor, answer);
    }

	/**
	 * @param editor
	 * @param answer
	 */
	protected void previewAnswer(final BaseAnswerEditorPanel editor,
			final Answer answer) {
		JButton jbPreview = (JButton) editor.getButton(QuestionAnswer.PREVIEW_BUTTON);
        ActionListener[] listeners = jbPreview.getActionListeners();
        for (int i = 0; i < listeners.length; i++) {
            jbPreview.removeActionListener(listeners[i]);
        }
        jbPreview.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (answer.getType() == Email.TYPE_LETTER || answer.getType() == Email.TYPE_FAX) {
                        Question question = API.getClientAPI().getQuestionAPI().load(answer.getQuestionId());
                        byte[] pdf = ClientTemplateExtension.getInstance().createPdf(new PdfFile(editor.getAnswer().getBody(), question.getSubprojectId(), question.getLanguage(), !TagMatchDefinitions.isSbsProject(question), ClientUtils.getParameter(answer, false)), null);
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
	}
}
