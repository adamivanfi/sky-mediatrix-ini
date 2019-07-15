package com.nttdata.de.sky.ityx.mediatrix.businessrules.client;

import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.Actions;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.client.dialog.operate.QuestionAnswer;
import de.ityx.mediatrix.client.util.Repository;
import de.ityx.mediatrix.data.Question;
import de.ityx.mediatrix.modules.businessrules.data.BRSession;
import de.ityx.mediatrix.modules.businessrules.tools.BRSessionPoolFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SEPAindexAction extends AbstractAction {

    private final String calendarDateFormatPattern = "dd.MM.yyyy";

    SEPAindexAction(String name, Icon icon) {
        super(name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int questionid = 0;
        try {
            GridBagConstraints c = new GridBagConstraints();
            JPanel inputPanel = new JPanel(new GridBagLayout());
            c.gridx = 0;
            c.gridy = 0;
            JLabel mandateLabel = new JLabel("Mandatsnummer: ");
            inputPanel.add(mandateLabel, c);
            c.gridy = 1;
            JLabel signatureLabel = new JLabel("Unterschriftsdatum: ");
            inputPanel.add(signatureLabel, c);
            c.gridx = 1;
            c.gridy = 0;
            javax.swing.JTextField mandateField = new javax.swing.JTextField(10);
            mandateField.setEditable(true);
            final QuestionAnswer qa = (QuestionAnswer) Repository.getObject(Repository.OPERATORMODE);
            Question question = qa.getQuestion();
            assert question != null;
            questionid = question.getId();
            String headers = question.getHeaders();
//			String channel = TagMatchDefinitions.extractHeader(headers, TagMatchDefinitions.getHeaderTagName(TagMatchDefinitions.CHANNEL));
//			if (channel == null || !channel.equals(TagMatchDefinitions.Channel.BRIEF.toString())) {
//				JOptionPane.showMessageDialog(null, "Nur Briefe können als Mandat indiziert werden.");
//				return;
//			}
            String mandateRefId = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.getHeaderTagName(TagMatchDefinitions.SEPA_MANDATE_NUMBER));
            if (headers != null) {
                mandateField.setText(mandateRefId);
            }
            inputPanel.add(mandateField, c);
            Date mailDate = new Date(question.getEmailDate());
            final JSpinner dateSpin = new JSpinner(new SpinnerDateModel(mailDate, null, mailDate, Calendar.DATE));
            String calendarDateFormatPattern = "dd.MM.yyyy";
            dateSpin.setEditor(new JSpinner.DateEditor(dateSpin, calendarDateFormatPattern));
            dateSpin.setEnabled(true);
            c.gridy = 1;
            inputPanel.add(dateSpin, c);
            javax.swing.JCheckBox sigCheckBox = new javax.swing.JCheckBox("unterschrieben?");
            c.gridy = 2;
            inputPanel.add(sigCheckBox, c);
            int dialog = JOptionPane.showConfirmDialog(null, inputPanel, "Mandatsinformation", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (dialog == JOptionPane.OK_OPTION) {
                if (qa.getQuestion().isManipulated() || qa.getQuestion().isManipulated()) {
                    API.getClientAPI().getQuestionAPI().store(qa.getQuestion());
                    SkyLogger.getClientLogger().debug(" SEPAReindexAction fid:" + qa.getQuestion().getId() + " storing question");
                }
                if (qa.getAnswer().isManipulated() || qa.getAnswer().isChanged() || qa.getAnswer().isDirty()) {
                    API.getClientAPI().getAnswerAPI().store(qa.getAnswer());
                    SkyLogger.getClientLogger().debug(" SEPAReindexAction aid:" + qa.getAnswer().getId() + " storing Answer");
                }

                Boolean trying = true;
                BRSession session = BRSessionPoolFactory.getBRSessionPool().getBRSession("SkyReindex");
                //String operatorMode = (String) session.is getParameter("OperatorMode");
                //Boolean opMode = operatorMode != null && operatorMode.equals(TagMatchDefinitions.TRUE);


                Boolean opMode = API.getClientAPI().getProcessingAPI().isOperatorModeStart();
                String customerID = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.X_TAGMATCH_CUSTOMER_ID);
                final String customerNumber = customerID.replaceAll("\\.", "");
                String contractID = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.getHeaderTagName(TagMatchDefinitions.TAGMATCH_CONTRACT_NUMBER));
                String mandateId = mandateField.getText();
                // Checks if indexing without mandate number.
                if (mandateId == null || mandateId.length() == 0 || mandateId.equals("0")) {
                    trying = false;
                    JOptionPane.showMessageDialog(null, "Ein Dokument ohne Mandatsreferenz kann nicht als Mandat indiziert werden.");
                }
                Boolean signed = sigCheckBox.isSelected();
                if (signed == null) {
                    signed = false;
                }
                String signatureDate = new SimpleDateFormat(calendarDateFormatPattern).format(dateSpin.getValue());
                // Checks if indexing without customer.
                if (customerID == null || customerID.length() == 0 || customerID.equals("0")) {
                    trying = false;
                    JOptionPane.showMessageDialog(null, "Ein Dokument ohne Kundenkontakt kann nicht als Mandat indiziert werden.");
                }

                if (trying) {
                    List<Object> parameter = new ArrayList<Object>();
                    parameter.add((question.getProjectId() > 0) ? question.getProjectId() : 110);
                    parameter.add(question.getId());
                    parameter.add(customerNumber);
                    parameter.add(contractID);
                    parameter.add(API.getClientAPI().getConnectionAPI().getCurrentOperatorShort().getId());
                    parameter.add(opMode);
                    // no initial contact
                    parameter.add(false);
                    // Needed for new contact
                    String channel = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.getHeaderTagName(TagMatchDefinitions.CHANNEL));
                    if (channel == null || channel.length() == 0 || channel.equals("0")) {
                        channel = TagMatchDefinitions.Channel.EMAIL.toString();
                    }
                    parameter.add(channel);
                    parameter.add(TagMatchDefinitions.SEPA_MANDATE);
                    parameter.add("INBOUND");
                    parameter.add(mandateId);
                    if (signatureDate != null && signatureDate.length() > 0) {
                        parameter.add(signatureDate);
                        SkyLogger.getClientLogger().info("Signaturedate qid:" + questionid + ":" + signatureDate);
                    } else {
                        SkyLogger.getClientLogger().info("Signaturedate is empty qid:" + questionid);

                    }

                    parameter.add(signed);

                    List<Object> result = (List<Object>) API.getClientAPI().getConnectionAPI().sendServerEvent(Actions.ACTION_REINDEX.name(), parameter);
                    trying = (Boolean) result.get(0);

                    if (!trying) {
                        JOptionPane.showMessageDialog(null, "Der (Kunde/Vertrag/Mandat) kann nicht indiziert werden.");
                    } else {
                        Boolean success = (Boolean) result.get(1);
                        if (success) {
                            //QUESTION = (Question) result.get(2);
                            ClientOutboundRule.reloadQuestion(opMode, question.getId(), this);
                        }
                        final String message = success ? "Mandat konnte erfolgreich indiziert werden." : "Fehler bei der SEPA-Indizierung.";
                        JOptionPane.showMessageDialog(null, message);
                    }
                }
            }
        } catch (Exception ex) {
            SkyLogger.getClientLogger().error("Problem during ReindexAction qid:" + questionid + (ex.getMessage() != null ? " ex:" + ex.getMessage() : ""), ex);
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Fehler bei der Indizierung des Kunden.");
        }
    }

}
