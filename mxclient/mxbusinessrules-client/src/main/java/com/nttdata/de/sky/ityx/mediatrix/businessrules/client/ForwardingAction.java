package com.nttdata.de.sky.ityx.mediatrix.businessrules.client;

import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.Actions;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.api.client.ICQuestion;
import de.ityx.mediatrix.client.dialog.operate.QuestionAnswer;
import de.ityx.mediatrix.client.dialog.operate.WaitQueue;
import de.ityx.mediatrix.client.dialog.singlemode.QuestionTablePanel;
import de.ityx.mediatrix.client.util.QuestionActions;
import de.ityx.mediatrix.client.util.Repository;
import de.ityx.mediatrix.data.OperatorLogRecord;
import de.ityx.mediatrix.data.Question;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.net.URI;
import java.util.*;

public class ForwardingAction extends AbstractAction {

    public static final int SBS_FORWARD_SUBPROJECT = Integer.parseInt(System.getProperty("sbs_forward.subproject", "58102"));
    public static final int SKY_FORWARD_SUBPROJECT = Integer.parseInt(System.getProperty("sky_forward.subproject", "903"));

    private final Question question;

    public ForwardingAction(String name, Icon icon, Question question) {
        super(name, icon);
        this.question = question;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        URI uri = null;
        int confirm = JOptionPane.showConfirmDialog(null, "Möchten Sie wirklich, dass die Frage an " + (TagMatchDefinitions.isNotSbsProject(question) ? "SBS" : "SCS") + " weitergeleitet wird?", "Tenant Forwarding", JOptionPane.OK_CANCEL_OPTION);
        if (confirm == JOptionPane.OK_OPTION) {
            try {
                final QuestionAnswer qa = (QuestionAnswer) Repository.getObject(Repository.OPERATORMODE);
                boolean done = forwardToTenant(question, true);
                if(done) {
                    boolean opMode = API.getClientAPI().getProcessingAPI().isOperatorModeStart();
                    if ((Boolean) opMode) {
                        SkyLogger.getClientLogger().debug("forwardToProjectButton: OperatorMode");
                        qa.returnToInboxOrOperatorModusorWelcomePanel(null);
                    } else {
                        SkyLogger.getClientLogger().debug("forwardToProjectButton cancel after forwarding");
                        if(qa != null) {
                            qa.cancel();
                        }
                    }
                    API.getClientAPI().getQuestionAPI().releaseLock(question);
                }
                final String message = done ? "Die Frage " + question.getId() + " konnte erfolgreich weitergeleitet werden." : "Fehler bei der Weiterleitung der Frage.";
                JOptionPane.showMessageDialog(null, message);

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, uri);
            }
        }
    }

    public static boolean forwardToTenant(Question question, boolean reload) {
        boolean notSbsProject = TagMatchDefinitions.isNotSbsProject(question);
        String formtype = notSbsProject ? TagMatchDefinitions.SBS_FORMTYPE_DEFAULT : TagMatchDefinitions.DEFAULT_FORMTYPE;
        String headers = question.getHeaders();
        headers = TagMatchDefinitions.addOrReplaceXTMHeader(headers, TagMatchDefinitions.FORM_TYPE_CATEGORY, formtype);
        headers = TagMatchDefinitions.addOrReplaceXTMHeader(headers, TagMatchDefinitions.CONTACT_ID, "");
        headers = TagMatchDefinitions.addOrReplaceXTMHeader(headers, TagMatchDefinitions.ACTIVITY_ID, "");
        for (String header : TagMatchDefinitions.CUSTOMER_DATA) {
            headers = TagMatchDefinitions.addOrReplaceOrgHeader(headers, header, "");
        }
        question.setHeaders(headers);
        question.setExtra3("0");
        question.setExtra8("0");
        question.setExtra12("0");
        String customerID = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.CUSTOMER_ID);
        List<Object> parameter = new ArrayList<Object>();
        if (notSbsProject && customerID != null && !customerID.isEmpty() && !customerID.equals("0")) {
            parameter.add((question.getProjectId() > 0) ? question.getProjectId() : 110);
            parameter.add(question.getId());
            parameter.add("0");
            parameter.add("0");
            parameter.add(API.getClientAPI().getConnectionAPI().getCurrentOperatorShort().getId());
            parameter.add(new Boolean(false));
            // no initial contact
            parameter.add(false);
            // Needed for new contact
            String channel = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.getHeaderTagName(TagMatchDefinitions.CHANNEL));
            if (channel == null || channel.length() == 0 || channel.equals("0")) {
                channel = TagMatchDefinitions.Channel.EMAIL.toString();
            }
            parameter.add(channel);

            parameter.add(formtype);
            String direction = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.getHeaderTagName(TagMatchDefinitions.MX_DIRECTION));
            if (direction == null || direction.length() == 0 || direction.equals("0")) {
                direction = "INBOUND";
            }
            parameter.add(direction);
            SkyLogger.getClientLogger().debug("Starting ReindexAction with parameters:p:" + question.getProjectId() + " fid:" + question.getId() + " cid:" + " op:" + API.getClientAPI().getConnectionAPI().getCurrentOperatorShort().getId() + " ch:" + channel + " formtype" + formtype);
            List<Object> result = (List<Object>) API.getClientAPI().getConnectionAPI().sendServerEvent(Actions.ACTION_REINDEX.name(), parameter);
            if (result == null || result.size() < 1) {
                SkyLogger.getClientLogger().debug("Kunde nicht gefunden in FuzzyDB:p:" + question.getProjectId() + " fid:" + question.getId() + " cid:" + " op:" + API.getClientAPI().getConnectionAPI().getCurrentOperatorShort().getId() + " ch:" + channel + " formtype" + formtype);

            } else {
                Object firstres = result.get(0);
                if (firstres instanceof Boolean) {
                } else {
                    String problem = "";
                    for (Object r : result) {
                        problem += r.toString();
                    }
                    SkyLogger.getClientLogger().warn("Problem with Reindex: " + question.getId() + " problem:" + problem);
                }

            }
        }
        final ICQuestion questionAPI = API.getClientAPI().getQuestionAPI();

        SkyLogger.getClientLogger().info("questionAPI.forward -> notSbsProject: "+notSbsProject+
                "; SBS_FORWARD_SUBPROJECT: "+SBS_FORWARD_SUBPROJECT+"; SKY_FORWARD_SUBPROJECT: "+SKY_FORWARD_SUBPROJECT+
                "; q.id: "+question.getId()+"; q.prjId: "+question.getProjectId()+
                "; q.subprjId: " + question.getSubprojectId()+"; q.subject: "+question.getSubject());
        boolean done = questionAPI.forward(question, null, notSbsProject ? SBS_FORWARD_SUBPROJECT : SKY_FORWARD_SUBPROJECT, new HashMap());
        SkyLogger.getClientLogger().info("forwardToProjectButton: " + done);

        if (done) {
            List<OperatorLogRecord> logRecords = questionAPI.loadQuestionLog(question.getId());
            OperatorLogRecord lastOperatorLogRecord = logRecords.get(logRecords.size() - 1);
            String text = lastOperatorLogRecord.getParameter();
            text = text.replaceFirst((notSbsProject ? "To:sky" : "To:sbs"), (notSbsProject ? "To:sbs" : "To:sky"));
            try {
                parameter.clear();
                parameter.add((question.getProjectId() > 0) ? question.getProjectId() : 110);
                parameter.add(lastOperatorLogRecord.getOperatorId());
                parameter.add(lastOperatorLogRecord.getQuestionId());
                parameter.add(lastOperatorLogRecord.getAction());
                parameter.add(lastOperatorLogRecord.getTime());
                parameter.add(text);
                parameter = (List<Object>) API.getClientAPI().getConnectionAPI().sendServerEvent(Actions.ACTION_UPDATELOG.name(), parameter);
            }
            catch (Exception e) {
                SkyLogger.getClientLogger().error(e.getMessage());

            }
            Object ret = parameter.get(0);
            SkyLogger.getClientLogger().warn("ACTION_UPDATELOG: " + question.getId() + " result:" + ret);
            if(!ret.equals(Boolean.TRUE)) {
                lastOperatorLogRecord.setParameter(text);
                lastOperatorLogRecord.setTime(lastOperatorLogRecord.getTime()+1);
                questionAPI.storeQuestionLog(logRecords);
            }
            ClientOutboundRule.sbsIndex((reload ? null : question), "0", false);
            ClientOutboundRule.refreshTable();
        }
        return done;
    }
}
