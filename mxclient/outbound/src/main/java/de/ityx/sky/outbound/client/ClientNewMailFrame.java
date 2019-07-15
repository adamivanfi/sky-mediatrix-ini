package de.ityx.sky.outbound.client;

import com.nttdata.de.sky.archive.ClientUtils;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import com.nttdata.de.sky.pdf.ClientTemplateExtension;
import com.nttdata.de.sky.pdf.PdfFile;
import de.ityx.mediatrix.client.dialog.Start;
import de.ityx.mediatrix.client.dialog.util.newmail.NewMailFrame;
import de.ityx.mediatrix.data.Email;
import de.ityx.mediatrix.data.Question;
import de.ityx.sky.outbound.client.base.ClientBaseNewMailFrame;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//import de.ityx.mediatrix.data.GlobalVariable;

public class ClientNewMailFrame extends ClientBaseNewMailFrame {

    @Override
    public List<JComponent> getTabList(Question question,
            HashMap localRepository) {
        List<JComponent> tabs = new ArrayList<JComponent>();
        tabs.add(new CustomerSearchPanel((NewMailFrame) localRepository.get("CURRENT_FRAME")));
        return tabs;
    }

    @Override
    public boolean preQuestionSend(String loginname, boolean isOperatorMode, Question question, HashMap localRepository) {
		System.out.println("preQuestionSend");
		System.err.println("preQuestionSend");
        //Zum Testen
        // System.out.println("Extra 1:" + question.getExtra1());
        // System.out.println("Extra 2:" + question.getExtra2());
        //  System.out.println("Extra 3:" + question.getExtra3());

        if (question.getType() == Email.TYPE_LETTER || question.getType() == Email.TYPE_FAX) {
            try {
        		String to = question.getTo();
        		if (to==null ||to.isEmpty())
        			question.setTo("noreply@sky.de");
        		
                byte[] pdf = ClientTemplateExtension.getInstance().createPdf(
                        new PdfFile(question.getBody(),
                                question.getSubprojectId(),
                                question.getLanguage(), !TagMatchDefinitions.isSbsProject(question), ClientUtils
                                        .getParameter(question, true)), null);
                //                ClientUtils.preview(pdf);
                if (!question.getStatus().equals(Question.S_MONITORED)
                        && ClientUtils.isNotMonitored(question)) {
                    ClientUtils.insertAttachments(question, pdf);
                }
            }
            catch(Exception e) {
                JOptionPane.showMessageDialog(Start.getInstance(), ClientUtils
                        .exception(e).toString());
            }
        }

        return true;
    }

    @Override
    public void postQuestionSend(String loginname, boolean isOperatorMode,
            Question question, HashMap localRepository) {

        //        if (question.getType() == Email.TYPE_LETTER) {
        //            try {
        //
        //                byte[] pdf = ClientTemplateExtension.getInstance().createPdf(
        //                        new PdfFile(question.getBody(),
        //                                question.getSubprojectId(),
        //                                question.getLanguage(), ClientUtils
        //                                        .getParameter(question, false)), null);
        //                ClientUtils.preview(pdf);
        //                if (!question.getStatus().equals(Question.S_MONITORED)
        //                        && !ClientUtils.isMonitored(question)) {
        //                    ClientUtils.insertAttachments(question, pdf);
        //                }
        //            }
        //            catch(Exception e) {
        //                JOptionPane.showMessageDialog(Start.getInstance(), ClientUtils
        //                        .exception(e).toString());
        //            }
        //        }
    }
}
