package com.nttdata.de.sky.ityx.mediatrix.businessrules.client;

import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.Actions;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.contex.interfaces.document.CDocument;
import de.ityx.contex.interfaces.extag.TagMatch;
import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.client.dialog.operate.QuestionAnswer;
import de.ityx.mediatrix.client.util.Repository;
import de.ityx.mediatrix.data.OperatorLogRecord;
import de.ityx.mediatrix.data.Question;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class CancellationSRAction extends AbstractAction {
	
	private static final String TITLE_CANCEL_REASON = "Kündigungsgrund";
	public static final String REASON_CANCEL = "cancel";
	
	
	CancellationSRAction(String name, Icon icon) {
		super(name, icon);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		final QuestionAnswer qa = (QuestionAnswer) Repository.getObject(Repository.OPERATORMODE);
		Question question = qa.getQuestion();
		assert question != null;
		int questionid = question.getId();
		
		
		boolean execCancelQuickAction = false;
		HashMap<String, Set<String>> reasoncodes = new HashMap<>();
		SkyLogger.getClientLogger().info(questionid+" trigger cancellation: " + question.getId());
		
		Date creationDateD = null;
		String creationDate = TagMatchDefinitions.extractOrgHeader(question.getHeaders(), TagMatchDefinitions.CREATEDATE);
		if (creationDate != null && !creationDate.isEmpty()) {
			try {
				creationDateD = (new SimpleDateFormat("yyyyMMddHHmmss")).parse(creationDate);
			} catch (ParseException ee) {
				SkyLogger.getCommonLogger().error(questionid+"Not able to parse:" + creationDate + " to Format: yyyyMMddHHmmss");
			}
		}
		
		if (creationDateD == null) {
			creationDateD = new Date(question.getEmailDate());
		}
		if (creationDateD.getTime() < (System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000)) {
			int dialogO = JOptionPane.showConfirmDialog(null, "Die Frage:" + questionid + " ist vor mehr als 2 Tagen erstellt. " + "\nBitte Prüfen Sie ob reguläre Kündigung möglich ist." + "\nJa - Reguläre Kündigung wird von Mediatrix angestoßen" + "\nNein - Nachträgliche Kündigung wurde manuell in Siebel eingetragen. Frage wird geschlossen." + "\nAbbrechen - Aktuelle Aktion wird abgebrochen ", "Frage länger als zwei Tage offen", JOptionPane.YES_NO_CANCEL_OPTION);
			if (dialogO == JOptionPane.CANCEL_OPTION) {
				return;
			}
			execCancelQuickAction = (dialogO == JOptionPane.OK_OPTION);
		} else {
			execCancelQuickAction = true;
		}
		if (execCancelQuickAction) {
			String cancelReason = selectCancelationReason(question);
			if (cancelReason != null && !cancelReason.isEmpty()) {
				Set<String> cset = new HashSet<>();
				cset.add(cancelReason);
				reasoncodes.put(REASON_CANCEL, cset);
			} else {
				JOptionPane.showMessageDialog(null, "Die Frage:" + questionid + " hat keinen gültigen Kündigungsgrund. \nAutomatische Kündigung kann nicht angestoßen werden.\nBearbeitung wird abgebrochen.", "Kündigunggrund fehlt", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			
			
			String formtype = TagMatchDefinitions.extractOrgHeader(question.getHeaders(), TagMatchDefinitions.X_TAGMATCH_FORM_TYPE);
			if (formtype == null || formtype.isEmpty() || !formtype.toLowerCase().contains("kuendigung")) {
				if (reasoncodes.containsKey("FORMTYPE")) {
					//cache für bulk
					formtype = reasoncodes.get("FORMTYPE").iterator().next();
				} else {
					int dialog = JOptionPane.showConfirmDialog(null, "Im aktuellen Teilprojekt dürfen nur Kündigungen abgeschlossen werden.\n" + "Wollen Sie das Formtype für die Frage:" + question.getId() + " ändern?", "Unpassender Formtype", JOptionPane.YES_NO_OPTION);
					boolean okOption = dialog == JOptionPane.OK_OPTION;
					if (okOption) {
						CDocument document = question.getDocumentContainer().getDocument(0);
						formtype = selectFormtype(question, document);
						if (formtype != null && !formtype.isEmpty() && formtype.toLowerCase().contains("kuendigung")) {
							Set<String> fset = new HashSet<>();
							fset.add(formtype);
							reasoncodes.put("FORMTYPE", fset);
						} else {
							return;
						}
					} else {
						return;
					}
				}
			}
			/*Set<String> completeReasons = ClientOutboundRule.selectCompletionReason(null, question);
			if (completeReasons != null && !completeReasons.isEmpty()) {
				reasoncodes.put(REASON_COMPLETE, completeReasons);
			}*/
		}
		
		
		String custid = TagMatchDefinitions.extractXTMHeader(question.getHeaders(), TagMatchDefinitions.CUSTOMER_ID);
		
		if (custid == null || custid.isEmpty() || custid.equals("0") || custid.equals("-1")) {
			SkyLogger.getClientLogger().warn(" BusinessTest failed: Question:" + question.getId() + " without CustomerData can not start CancellationProcess: " + question.getId() + " h:" + question.getHeaders());
			int dialog = JOptionPane.showConfirmDialog(null, "Die Frage:" + question.getId() + " ist nicht zur einem Kunden indiziert. \nWollen Sie wirklich fortfahren (Kündigungsprozess wird nicht angestoßen)?", "Kunde nicht indiziert", JOptionPane.YES_NO_OPTION);
			boolean okOption = dialog == JOptionPane.OK_OPTION;
			if (!okOption) {
				return;
			}
		}
		SkyLogger.getClientLogger().info(" Preparing CancellationProcess: CustomerID: " + custid + " q:" + question.getId());
		
		String loginname = (API.isClient() && API.getClientAPI().getConnectionAPI().getCurrentOperator() != null) ? API.getClientAPI().getConnectionAPI().getCurrentOperator().getLogin() : "server";
		
		Set<String> cancelReasonS = reasoncodes.get(REASON_CANCEL);
		String cancelReason = cancelReasonS.iterator().next();
		question.setHeaders(TagMatchDefinitions.addOrReplaceXTMHeader(question.getHeaders(), TagMatchDefinitions.CANCELLATION_REASON, cancelReason));
		API.getClientAPI().getQuestionAPI().protocolLog(question, loginname + ":" + cancelReason, OperatorLogRecord.ACTION_INFO);
		
		List<Object> parameter = new ArrayList<Object>();
		parameter.add((question.getProjectId() > 0) ? question.getProjectId() : 110);
		parameter.add(question.getId());
		parameter.add(cancelReason);
		parameter.add(loginname);

			// um Headers für CANCELLATION_REASON zu persistieren, bevor die Frage vom Server bearbeitet wird.
		qa.storeQuestionAnswer(false);
		API.getClientAPI().getQuestionAPI().store(question);
			List<Object> result = (List<Object>) API.getClientAPI().getConnectionAPI().sendServerEvent(Actions.ACTION_CANCELLATION.name(), parameter);
			if (result != null && result.size() >= 1) {
				Object sucessO = result.get(0);
				boolean sucess = sucessO instanceof Boolean ? ((Boolean) sucessO) : false;
				String msg="";
				if (result.size() > 1 && result.get(1) != null){
					Object reasonCodeO = result.get(1);
					msg="\n Problem:"+(String) reasonCodeO;
				}
				
				if (sucess ) { //&& reasonCodeO != null && !((String) reasonCodeO).isEmpty()
					SkyLogger.getClientLogger().debug(" Closing cancellation:\n" + "qid:" + question.getId() + "\n" + "DocumentID:" + question.getDocId() + "\n" + "Reason: " + cancelReason + "\n" + "Success: " + result.get(0) + " msg:" + msg);
					//question = ClientUtils.reloadQuestion(API.getClientAPI().getProcessingAPI().isOperatorModeStart(), question.getId(), this);
					
					//int dialogO = JOptionPane.showConfirmDialog(null, "Die Kündigung für die Frage:" + questionid + " wurde initialisiert." , "Automatisches Kündigungsprozess wurde gestartet", JOptionPane.OK_OPTION );
					SkyLogger.getClientLogger().warn(" Closing cancellation:\n" + "qid:" + question.getId() + "\n" + "DocumentID:" + question.getDocId() + "\n" + "Reason: " + cancelReason + "\n" + " WITHOUT Success: " + msg);
					
					if ((Boolean) API.getClientAPI().getProcessingAPI().isOperatorModeStart()) {
						//qa.cancel();
						qa.executeComplete();
						/*if (question.getLockedBy()>0) {
							//API.getClientAPI().getQuestionAPI().complete(question);
							API.getClientAPI().getQuestionAPI().releaseLock(question);
						}
						SkyLogger.getClientLogger().debug("close after cancellation");
						//API.getClientAPI().getQuestionAPI().complete(question);
						*/
						//qa.returnToInboxOrOperatorModusorWelcomePanel(null);
					} else {
						SkyLogger.getClientLogger().debug("close after cancellation");
						//qa.cancel();
						qa.executeComplete();
						/*if (question.getLockedBy()>0) {
							API.getClientAPI().getQuestionAPI().complete(question);
							API.getClientAPI().getQuestionAPI().releaseLock(question);
						}*/
					}
				}else {
						int dialogO = JOptionPane.showConfirmDialog(null, "Die Kündigung für die Frage:" + questionid + " konnte nicht automatisch durchgeführt werden." + "\nBitte Prüfen Sie ob reguläre Kündigung im Siebel möglich ist."+msg, "Automatisches Prozess konnte nicht gestartet werden", JOptionPane.WARNING_MESSAGE );
						SkyLogger.getClientLogger().warn(" Closing cancellation:\n" + "qid:" + question.getId() + "\n" + "DocumentID:" + question.getDocId() + "\n" + "Reason: " + cancelReason + "\n" + " WITHOUT Success: " + (msg != null ? msg : null));
					}
			} else {
				String fehler="\n";
				int dialogO = JOptionPane.showConfirmDialog(null, "Die Kündigung für die Frage:" + questionid + " konnte nicht automatisch durchgeführt werden." + "\nBitte Prüfen Sie im Siebel ob reguläre Kündigung möglich ist."+fehler, "Automatisches Prozess konnte nicht gestartet werden", JOptionPane.WARNING_MESSAGE );
				SkyLogger.getClientLogger().warn(" Closing cancellation:\n" + "qid:" + question.getId() + "\n" + "DocumentID:" + question.getDocId() + "\n" + "Reason: " + cancelReason + "\n" + " WITHOUT Success: " + (result != null && result.size()>1? result.get(1) : null));
			}
		//Question q = ClientUtils.reloadQuestion(opMode, questionid, this);
	}
	
	protected String selectCancelationReason(Question question) {
		List<String> possibleReasons = getReasons(REASON_CANCEL, question);
		return selectRadioOption(TITLE_CANCEL_REASON, possibleReasons);
	}
	
	private Map<String, List<String>> reasonscache = new LinkedHashMap<>();
	
	protected List<String> getReasons(String action, Question question) {
		if (reasonscache.get(action) != null) {
			return reasonscache.get(action);
		}
		List<Object> parameter = new ArrayList<Object>();
		parameter.add((question.getProjectId() > 0) ? question.getProjectId() : 110);
		parameter.add(action);
		
		Object result = (List<Object>) API.getClientAPI().getConnectionAPI().sendServerEvent(Actions.ACTION_GET_REASONS.name(), parameter);
		if (result == null || result instanceof String) {
			SkyLogger.getClientLogger().fatal("Problem getting Reasons: " + question.getId() + ":" + result);
			return new LinkedList<String>();
		} else {
			Object result2 = ((List<Objects>) result).get(0);
			if (result2 == null || result2 instanceof String) {
				SkyLogger.getClientLogger().fatal("Problem2 getting Reasons: " + question.getId() + ":" + result2);
				return new LinkedList<String>();
			} else if (result2 instanceof List) {
				reasonscache.put(action, (List<String>) result2);
				return (List<String>) result2;
			} else {
				SkyLogger.getClientLogger().fatal("Problem3 getting Reasons: " + question.getId() + ":" + result2);
				return new LinkedList<String>();
			}
		}
	}
	
	protected String selectRadioOption(String title, List<String> reasons) {
		String reason = null;
		GridBagConstraints c = new GridBagConstraints();
		JPanel inputPanel = new JPanel(new GridBagLayout());
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		ButtonGroup reasonGroup = new ButtonGroup();
		for (String reasonOption : reasons) {
			JRadioButton rButton = new JRadioButton(reasonOption);
			rButton.setActionCommand(reasonOption);
			reasonGroup.add(rButton);
			c.gridy++;
			inputPanel.add(rButton, c);
		}
		while (reason == null) {
			int dialog = JOptionPane.showConfirmDialog(null, inputPanel, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
			boolean okOption = dialog == JOptionPane.OK_OPTION;
			if (okOption) {
				ButtonModel selection = reasonGroup.getSelection();
				if (selection == null) {
					JOptionPane.showMessageDialog(null, "Bitte einen Grund auswählen.");
				} else {
					reason = selection.getActionCommand();
				}
			} else {
				break;
			}
		}
		return reason;
	}
	
	private String selectFormtype(Question question, CDocument document) {
		Vector<String> formtypes = new Vector<String>();
		formtypes.addAll(ClientOutboundRule.loadFormtypeKeywords((question.getProjectId() > 0) ? question.getProjectId() : 110));
		String newType = null;
		GridBagConstraints c = new GridBagConstraints();
		JPanel inputPanel = new JPanel(new GridBagLayout());
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		JComboBox catBox = new JComboBox(formtypes);
		String formtype = document.getFormtype();
		catBox.setSelectedItem(formtype);
		inputPanel.add(catBox, c);
		int dialog = JOptionPane.showConfirmDialog(null, inputPanel, "Auswahl Dokumententyp", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		boolean okOption = dialog == JOptionPane.OK_OPTION;
		if (okOption) {
			newType = (String) catBox.getSelectedItem();
			
			document.setFormtype(newType);
			question.setHeaders(TagMatchDefinitions.addOrReplaceXTMHeader(question.getHeaders(), TagMatchDefinitions.X_TAGMATCH_FORM_TYPE, newType));
			List<TagMatch> tags = document.getTags();
			for (TagMatch tag : tags) {
				String identifier = tag.getIdentifier();
				if (identifier.equals(TagMatchDefinitions.FORM_TYPE_CATEGORY) || identifier.equals(TagMatchDefinitions.DOCUMENT_TYPE)) {
					SkyLogger.getClientLogger().info(tag.getIdentifier() + " : " + tag.getTagValue());
					tag.setTagValue(newType);
					SkyLogger.getClientLogger().info(tag.getIdentifier() + " : " + tag.getTagValue());
				}
			}
		}
		return newType;
	}
	
}