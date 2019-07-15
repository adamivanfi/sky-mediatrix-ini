package com.nttdata.de.sky.archive;

import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.impl.document.image.ImageDocument;
import de.ityx.contex.interfaces.document.CDocument;
import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.api.client.ICQuestion;
import de.ityx.mediatrix.client.dialog.operate.QuestionAnswer;
import de.ityx.mediatrix.client.dialog.operate.WaitQueue;
import de.ityx.mediatrix.client.util.Repository;
import de.ityx.mediatrix.data.*;
import de.ityx.mediatrix.modules.tools.logger.Log;
import de.ityx.mediatrix.util.EmbeddedImageManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * contains common functions.
 */
public class ClientUtils {

    public static void preview(byte[] pdf) throws Exception {
        File file = File.createTempFile("Mediatrix", ".pdf");
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(pdf);
        fos.close();

        String externViewer = System.getProperty("de.ityx.extern.pdfviewer", "acroread");

        try {
            if (externViewer.trim().length() > 0) {
                Log.message("PDF-Aufruf>" + externViewer + ' ' + file + "<");
                Runtime.getRuntime().exec(externViewer + ' ' + file);
            }
        }
        catch(Exception e) {
            String msg = " You have to install the programm : acroread  ortherwise \nopen the generated pdf file manualy from your /temp or C:\temp directory according to your System";
            Log.debug(msg);
            Log.exception(e);
        }
        finally {
            file.deleteOnExit();
        }

    }

    public static StringWriter exception(Exception e) {
        Log.exception(e);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw;
    }

    public static void clearCache() {
        API.getClientAPI().getCaseAPI().hardResetCache();
    }

    public static HashMap<String, Object> getParameter(Answer answer, boolean preview) throws Exception {
        Customer customer = API.getClientAPI().getCustomerAPI().loadCustomerFromAnswer(answer);
        HashMap<String, Object> parameter = BaseUtils.getParameter(answer.getId(), customer);
        addEmbeddedAttachments(answer, preview, parameter);
        return parameter;
    }

    public static HashMap<String, Object> getParameter(Question question, boolean preview) throws Exception {
        HashMap<String, Object> parameter;
        if(!TagMatchDefinitions.isSbsProject(question)) {
            Customer customer = API.getClientAPI().getCustomerAPI().loadCustomerFromQuestion(question);
            parameter = BaseUtils.getParameter(question.getId(), customer);
            addEmbeddedAttachments(question, preview, parameter);
        }
        else {
            parameter = BaseUtils.getParameter(question.getId(), question.getHeaders());
        }
        return parameter;
    }

    /**
     * @param preview
     * @param parameter
     */
    public static void addEmbeddedAttachments(Email email, boolean preview,
            HashMap<String, Object> parameter) {
        if (preview) {
            parameter.put("embedded_att", getEmbeddedAttachments(email.getBody(), email.getAttachments()));
        }
        else {
            List<Integer> attIds = getEmbeddedAttachmentIds(email.getBody(), email.getAttachments());
            parameter.put("embedded_att_ids", attIds);
        }
    }

    private static List<Integer> getEmbeddedAttachmentIds(String body, List<Attachment> attList) {
        List<Integer> attIds = new ArrayList<Integer>();

        for (Attachment att : attList) {
            if (att.getFilename().startsWith(
                    EmbeddedImageManager.PREFIX) && (EmbeddedImageManager.checkBodyForPath(body, att.getFilename(), new StringBuilder()))) {
                if (att.getId() == 0) {
                    API.getClientAPI().getAttachmentAPI().store(att);
                }
                attIds.add(att.getId());
            }
        }
        return attIds;
    }

    private static List<Attachment> getEmbeddedAttachments(String body, List<Attachment> attList) {
        List<Attachment> embeddedAttList = new ArrayList<Attachment>();

        for (Attachment att : attList) {
            String attfile = att.getFilename();
            System.err.println("ATT LETTER ### "+attfile);
            System.out.println("ATT LETTER ### "+attfile);
			if (EmbeddedImageManager.PREFIX.startsWith(
                    EmbeddedImageManager.PREFIX) && body.contains(attfile)) {
                embeddedAttList.add(att);

            }
        }
        return embeddedAttList;
    }

    public static void insertAttachments(Email email, byte[] pdf) {
        Attachment att = new Attachment();
        att.setClientFilename(Constant.PDF_FEEDBACK);
        att.setFilename(Constant.PDF_FEEDBACK);
        att.setContentType(Constant.PDF_CONTENTTYPE);
        att.setBuffer(pdf);
        att.setEmailId(email.getEmailId());
        List<Attachment> atti = new ArrayList<Attachment>();
        for (Attachment a : email.getAttachments()) {
            if (a.getClientFilename().equalsIgnoreCase(Constant.PDF_FEEDBACK)
                    || a.getFilename().equalsIgnoreCase(Constant.PDF_FEEDBACK)) {
                API.getClientAPI().getAttachmentAPI().delete(a);
            }
            else {
                atti.add(a);
            }
        }
        API.getClientAPI().getAttachmentAPI().store(att);
        email.setAttachments(atti);
        email.addAttachment(att);
    }



    public static void preview(Email email) {
        if (email.getType() == Email.TYPE_LETTER || email.getType() == Email.TYPE_FAX) {
            for (Attachment att : email.getAttachments()) {
                if (att.getClientFilename().equals(Constant.PDF_FEEDBACK)) {
                    try {
                        if (!att.hasFullBuffer()) {
                            API.getClientAPI().getAttachmentAPI().load(att);
                        }
                        ClientUtils.preview(att.getBuffer());
                    }
                    catch(Exception e) {
                        Log.exception(e);
                    }
                }
            }
        }
    }

    public static boolean isNotMonitored(Question question) {
        return !API.getClientAPI().getConnectionAPI().getCurrentOperator().isMonitored() && !API.getClientAPI().getSubprojectAPI().load(question.getSubprojectId()).isMonitored();
    }

    public static boolean canSend(Question question, Answer answer) {
        Subproject subproject = API.getClientAPI().getSubprojectAPI().load(question.getSubprojectId());
        boolean unSend = subproject.isNotAllowedToComplete();
        boolean supervisor = API.getClientAPI().getConnectionAPI().getCurrentOperator().hasPermission(new Permission("supervisor", true, question.getSubprojectId()));
        boolean isadmin = API.getClientAPI().getConnectionAPI().getCurrentOperator().hasPermission(new Permission("administrator", true, 0));
        boolean isProjektAdmin = API.getClientAPI().getConnectionAPI().getCurrentOperator().isProjectAdmin(subproject);
        boolean monitored = answer.getStatus().equals(Answer.S_MONITORED);
        
        if (supervisor || isadmin || isProjektAdmin) {
            monitored = false;
        }
        return !unSend && !monitored && (isadmin || isProjektAdmin || API.getClientAPI().getConnectionAPI().getCurrentOperator().hasPermission(new Permission(Permission.P_SB_EMAIL_SEND, true, question.getSubprojectId())));
    }

    /**
     * Inserts a document container content as  attachments into the question
     * @param question the question
     * @param attachments attachments of the question
     */
    public static void addAttachments(Question question, List<Attachment> attachments) {
        if ((question.getType() == Email.TYPE_DOCUMENT || question.getType() == Email.TYPE_FAX)) {
            CDocumentContainer<CDocument> documentContainer = question.getDocumentContainer();
            if (documentContainer != null) {
                for (CDocument doc : documentContainer) {
                    if (doc instanceof ImageDocument) {
                        Attachment att = new Attachment();
                        att.setClientFilename(doc.getTitle());
                        att.setBuffer(doc.getPayload());
                        att.setContentType(doc.getContenttype());
                        att.setLength(att.getBuffer() != null ? att.getBuffer().length : att.getLength());

                        if (!haveAttachmentInQuestion(att, question.getAttachments())) {
                            attachments.add(att);
                        }
                    }
                }
            }
        }
    }

    /**
     * Checks if a list of attachments have a current attachment
     * @param att current attachment
     * @param attachments a list of attachment
     * @return
     */
    private static boolean haveAttachmentInQuestion(Attachment att, List<Attachment> attachments) {

        for (Attachment attachment : attachments) {
            if ((attachment.getLength() == att.getBuffer().length) && (attachment.getClientFilename().trim().equals(att.getClientFilename().trim()))) {
                return true;
            }
        }
        return false;
    }

    public static Question reloadQuestion(Boolean opMode, int questionid, Object ref) {
        ICQuestion questionAPI = API.getClientAPI().getQuestionAPI();
        Question newQuestion = questionAPI.load(questionid, false);
        if (opMode) {
            WaitQueue waitQueue = (WaitQueue) Repository.getObject(Repository.OPERATORMODEWAITLOOP);
            waitQueue.bearbeitefrage(true);
            WaitQueue.showFrage(newQuestion, waitQueue, (QuestionAnswer) Repository.getObject(Repository.QUESTIONANSWER));
        } else {
            Object qaO=Repository.getObject(Repository.OPERATORMODE);
            if (qaO instanceof QuestionAnswer) {
                final QuestionAnswer qa = (QuestionAnswer) Repository.getObject(Repository.OPERATORMODE);
                qa.reload(newQuestion);
                qa.refreshData();
                //qa.refreshAnswer(newAnswer);
                //questionAPI.refresh(newQuestion);
                //qa.refreshQuestion(newQuestion);
                //QuestionActions.openMailInbox(null, ref, newQuestion, true);
            }else{
                SkyLogger.getCommonLogger().debug("reload of Question: "+questionid +" not needed in bulk operations");
            }
        }
        return newQuestion;
    }

}
