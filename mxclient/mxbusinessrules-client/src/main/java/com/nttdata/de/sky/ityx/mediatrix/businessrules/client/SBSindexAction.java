package com.nttdata.de.sky.ityx.mediatrix.businessrules.client;

import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.archive.ClientUtils;
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
import java.util.ArrayList;
import java.util.List;

public class SBSindexAction extends AbstractAction {

	SBSindexAction(String name, Icon icon) {
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
			c.anchor = GridBagConstraints.WEST;
			JLabel customerLabel = new JLabel("Kundennummer: ");
			inputPanel.add(customerLabel, c);
			c.gridx = 1;
			c.gridy = 0;
			c.anchor = GridBagConstraints.EAST;
			JTextField customerField = new JTextField(10);
			customerField.setEditable(true);
			inputPanel.add(customerField, c);
			int dialog = JOptionPane.showConfirmDialog(null, inputPanel, "Neuzuordnung Kunden", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
			
			final QuestionAnswer qa = (QuestionAnswer) Repository.getObject(Repository.OPERATORMODE);
			Question question = qa.getQuestion();
			assert question != null;
			questionid = question.getId();
			if (dialog == JOptionPane.OK_OPTION) {

				String customerID = customerField.getText();
				String customerNumber = customerID!=null?customerID.replaceAll("[\\.\\s]", ""):null;
				Boolean trying = true;
				if (dialog == JOptionPane.OK_OPTION && customerNumber!=null && !customerNumber.matches("\\d{10}|0|P\\d*") && !customerNumber.isEmpty()){
					trying = false;
					JOptionPane.showMessageDialog(null, "Kundennummerformat entspricht nicht der Vorgaben (10-Stellig)");
					dialog = JOptionPane.showConfirmDialog(null, inputPanel, "Neuzuordnung Kunden", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
					customerID = customerField.getText();
					customerNumber = customerID!=null?customerID.replaceAll("[\\.\\s]", ""):null;
				}

				if (qa.getQuestion().isManipulated() || qa.getQuestion().isManipulated() || question.getId()<1) {
					API.getClientAPI().getQuestionAPI().store(qa.getQuestion());
					SkyLogger.getClientLogger().debug(" ReindexAction fid:" + qa.getQuestion().getId() + " storing question");
				}
				if (qa.getAnswer().isManipulated() || qa.getAnswer().isChanged() || qa.getAnswer().isDirty()) {
					API.getClientAPI().getAnswerAPI().store(qa.getAnswer());
					SkyLogger.getClientLogger().debug(" ReindexAction aid:" + qa.getAnswer().getId() + " storing Answer");
				}

				//  SkyLogger.getClientLogger().debug("Before ReindexAction aid:"+qa.getAnswer().getId() + " dirty:"+qa.getAnswer().isDirty() + " isChanged:"+qa.getAnswer().isChanged()+
				//           " isManipulated:" +qa.getAnswer().isManipulated()+ " AdocE:"+qa.isDocEdited()+" Adirty"+qa.isDirty());


				BRSession session = BRSessionPoolFactory.getBRSessionPool().getBRSession("SkyReindex");
				//String operatorMode = (String) session.getParameter("OperatorMode");
				//Boolean opMode = operatorMode != null && operatorMode.equals(TagMatchDefinitions.TRUE);
				Boolean opMode = API.getClientAPI().getProcessingAPI().isOperatorModeStart();
				String headers = question.getHeaders();

				// Checks if reindexing from 0 to 0.
				if (customerID.equals("0")) {
					String oldId = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.getHeaderTagName(TagMatchDefinitions.CUSTOMER_ID));
					if (oldId == null || oldId.length() == 0 || oldId.equals("0")) {
						trying = false;
						JOptionPane.showMessageDialog(null, "Ein Dokument ohne Kundenkontakt kann nicht deindiziert werden.");
					}
				}

				if (trying) {
					List<Object> parameter = new ArrayList<Object>();
					parameter.add((question.getProjectId()>0)?question.getProjectId():110);
					parameter.add(question.getId());
					parameter.add(customerNumber);
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
					String formtype = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.getHeaderTagName(TagMatchDefinitions.FORM_TYPE_CATEGORY));
					if (formtype == null || formtype.length() == 0 || formtype.equals("0")) {
						formtype = "systemdefault";
					}
					parameter.add(formtype);
					String direction = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.getHeaderTagName(TagMatchDefinitions.MX_DIRECTION));
					if (direction == null || direction.length() == 0 || direction.equals("0")) {
						direction = "INBOUND";
					}
					parameter.add(direction);
					SkyLogger.getClientLogger().debug("Starting ReindexAction with parameters:p:" + question.getProjectId() + " fid:" + question.getId() + " cid:" + customerNumber + " op:" + API.getClientAPI().getConnectionAPI().getCurrentOperatorShort().getId() + " ch:" + channel + " formtype" + formtype);
					List<Object> result = (List<Object>) API.getClientAPI().getConnectionAPI().sendServerEvent(Actions.ACTION_REINDEX.name(), parameter);

					if (result == null || result.size() < 1) {
						trying = false;
						SkyLogger.getClientLogger().debug("Kunde nicht gefunden in FuzzyDB:p:" + question.getProjectId() + " fid:" + question.getId() + " cid:" + customerNumber + " op:" + API.getClientAPI().getConnectionAPI().getCurrentOperatorShort().getId() + " ch:" + channel + " formtype" + formtype);

					} else {
						Object firstres = result.get(0);
						if (firstres instanceof Boolean) {
							trying = (Boolean) firstres;
						} else {
							trying = false;
							String problem = "";
							for (Object r : result) {
								problem += r.toString();
							}
							SkyLogger.getClientLogger().warn("Problem with Reindex: " + question.getId() + " kunde:" + customerID + " problem:" + problem);
						}

					}
					if (!trying) {
						JOptionPane.showMessageDialog(null, "Der (Kunde/Vertrag) kann nicht indiziert werden.");
					} else {
						Boolean success = result.size() > 2 && (Boolean) result.get(1);
						Question returnquestion = null;
						if (success) {
							returnquestion = (Question) result.get(2);
							returnquestion.setExtra3(customerID);
							//                   SkyLogger.getClientLogger().info("AfterSA ReindexAction aid:"+qa.getAnswer().getId() + " dirty:"+qa.getAnswer().isDirty() + " isChanged:"+qa.getAnswer().isChanged()+       " isManipulated:" +qa.getAnswer().isManipulated()+ " AdocE:"+qa.isDocEdited()+" Adirty"+qa.isDirty());
							Question q=ClientUtils.reloadQuestion(opMode, returnquestion.getId(), this);
							q.setExtra3(customerID);
							returnquestion.setExtra3(customerID);
							//SkyLogger.getClientLogger().info("AfterAll ReindexAction aid:"+qa.getAnswer().getId() + " dirty:"+qa.getAnswer().isDirty() + " isChanged:"+qa.getAnswer().isChanged()+                                          " isManipulated:" +qa.getAnswer().isManipulated()+ " AdocE:"+qa.isDocEdited()+" Adirty"+qa.isDirty());
						} else {
							SkyLogger.getClientLogger().warn("Problem with Reindex: " + question.getId() + " kunde:" + customerID);
							String problem = "";
							int i = 0;
							for (Object r : result) {
								problem += i + ":" + r.toString();
								i++;
							}
							SkyLogger.getClientLogger().warn("Problem with Reindex:" + question.getId() + " error:" + problem);
						}
						final String message = success ? "Der Kunde" + (customerID.equals("0") ? " konnte erfolgreich de" : ": " + customerNumber + " konnte erfolgreich ") + "indiziert werden." : "Fehler bei der Indizierung des Kunden.";
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

	/*public void reloadQuestion(Boolean opMode, int questionid) {
		ICQuestion questionAPI = API.getClientAPI().getQuestionAPI();
		Question newQuestion = questionAPI.load(questionid, false);
		if (opMode) {
			WaitQueue waitQueue = (WaitQueue) Repository.getObject(Repository.OPERATORMODEWAITLOOP);
			waitQueue.bearbeitefrage(true);
			WaitQueue.showFrage(newQuestion, waitQueue, (QuestionAnswer) Repository.getObject(Repository.QUESTIONANSWER));
		} else {
			final QuestionAnswer qa = (QuestionAnswer) Repository.getObject(Repository.OPERATORMODE);
			qa.refreshData();
			//qa.refreshAnswer(newAnswer);
			questionAPI.refresh(newQuestion);
			qa.reload(newQuestion);
			qa.refreshQuestion(newQuestion);
			QuestionActions.openMailInbox(null, this, newQuestion, true);
		}
	}*/
}