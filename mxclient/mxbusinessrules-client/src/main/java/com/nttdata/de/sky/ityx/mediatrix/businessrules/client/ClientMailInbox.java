package com.nttdata.de.sky.ityx.mediatrix.businessrules.client;

import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import com.nttdata.de.sky.ityx.mediatrix.businessrules.archiving.ArchiveTool;
import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.api.client.ICQuestion;
import de.ityx.mediatrix.api.interfaces.businessrules.client.IClientMailInbox;
import de.ityx.mediatrix.client.dialog.singlemode.QuestionTablePanel;
import de.ityx.mediatrix.client.util.Repository;
import de.ityx.mediatrix.client.util.SingleModeTableModel;
import de.ityx.mediatrix.data.*;
import de.ityx.mediatrix.data.businessrules.client.QuestionOpenVeto;
import de.ityx.mediatrix.modules.businessrules.data.BRSession;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class ClientMailInbox extends ClientOutboundRule implements IClientMailInbox {

	private IClientMailInbox agenturdel = null;
	private final String aclazz = "de.ityx.agentursteuerung.ClientMailInbox";
	private IClientMailInbox mailinboxextdel = null;
	private final String iclazz = "de.ityx.mailinboxsorter.businessrules.MailInboxExtender";


	public ClientMailInbox() {
		String logPrefix =  "ClientMailInbox # Constructor ";
		try {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " initalization");
			Class aclass=Class.forName(aclazz);
			if (aclass!=null){
				Constructor aconstr=aclass.getConstructor(null);
				if (aconstr!=null){
					agenturdel = (IClientMailInbox) aconstr.newInstance(null);
				}
			}
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " initalized");
		} catch (NullPointerException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
			SkyLogger.getBRSLogger().warn(logPrefix + aclazz + " cannot be instantiated." + e.getMessage());
		}
		try {
			SkyLogger.getBRSLogger().info(logPrefix + iclazz + " initalization");
			mailinboxextdel = (IClientMailInbox) Class.forName(iclazz).getConstructor(null).newInstance(null);
			Class iclass=Class.forName(aclazz);
			if (iclass!=null){
				Constructor iconstr=iclass.getConstructor(null);
				if (iconstr!=null){
					mailinboxextdel = (IClientMailInbox) iconstr.newInstance(null);
				}
			}
			SkyLogger.getBRSLogger().info(logPrefix + iclazz + " initalized");
		} catch (NullPointerException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
			SkyLogger.getBRSLogger().warn(logPrefix + iclazz + " cannot be instantiated." + e.getMessage());
		}
	}

	@Override
	public boolean preQuestionComplete(String loginname, Question question) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ": q:" + (question != null ? question.getId() : "") + " l:" + loginname + " ";
		SkyLogger.getBRSLogger().info(logPrefix + " start");

		if (isQuestionNotReadyForClose(loginname, question)) {
			SkyLogger.getClientLogger().warn(logPrefix + "Question Not Ready to Close");
			return false;
		}
		SkyLogger.getClientLogger().debug(logPrefix + "Question ready to close, loading");

		boolean complete = false;
		BRSession session = de.ityx.mediatrix.modules.businessrules.tools.BRSessionPoolFactory.getBRSessionPool().getBRSession("SkyBulk");
		Object persistent = session.getPersistent("Completing");
		SkyLogger.getClientLogger().debug(logPrefix + " Session read: " + persistent);

		HashMap<String, Set<String>> reasoncodes = null;
		if (persistent != null && Map.class.isAssignableFrom(persistent.getClass())) {
			reasoncodes = (HashMap<String, Set<String>>) persistent;
		} else {
			reasoncodes = new HashMap<String, Set<String>>();
		}

		SkyLogger.getClientLogger().debug(logPrefix + " Start CompletionAskAndExecution " + (question != null ? question.getId() : 0) + ", " + question.getCaseId());
		complete = askCompletionParametersAndExecuteAction(loginname, question, null, REASON_COMPLETE, reasoncodes, false);
		SkyLogger.getClientLogger().debug(logPrefix + " Finished CompletionAskAndExecution: " + complete);

		QuestionTablePanel a = (QuestionTablePanel) Repository.getObject(Repository.QUESTIONTABLEPANEL);
		JTable questionTable = a.getQuestionTable();
		int[] selectedRows = questionTable.getSelectedRows();
		if (selectedRows.length == 1 || !complete) {
			session.removeParamter("Completing");
			SkyLogger.getClientLogger().debug(logPrefix + " last bulk");
		} else {
			session.putPersistent("Completing", reasoncodes);
		}
		if (!complete && selectedRows.length > 1) {
			JOptionPane.showMessageDialog(null, "Der Schließvorgang wurde unterbrochen. \n" +
					"Problem trat bei Frage:" + question.getId() + "/Vorgang:" + question.getCaseId() + " auf.", "Abbruch", JOptionPane.INFORMATION_MESSAGE);
		}
		SkyLogger.getClientLogger().info(logPrefix + "Finished PreQuestionComplete:" + loginname + ":" + question.getId() + " return:" + complete);

		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			complete = complete && agenturdel.preQuestionComplete(loginname, question);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + ", <<agenturdel>> call finished");
		}
		if (mailinboxextdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + iclazz + " start call");
			complete = complete && mailinboxextdel.preQuestionComplete(loginname, question);
			SkyLogger.getBRSLogger().debug(logPrefix + iclazz + ", <<mailinboxextdel>> call finished");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return complete;
	}

	@Override
	public boolean postQuestionMerge(Question frage, Question tempFrage, boolean mailinbox, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":" + (frage != null ? frage.getId() + " " : " ");
		SkyLogger.getBRSLogger().info(logPrefix + " start");

		boolean ret = true;
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret = ret && agenturdel.postQuestionMerge(frage, tempFrage, mailinbox, hm);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		if (mailinboxextdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + iclazz + " start call");
			ret = ret && mailinboxextdel.postQuestionMerge(frage, tempFrage, mailinbox, hm);
			SkyLogger.getBRSLogger().debug(logPrefix + iclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return ret;
	}

	@Override
	public boolean preQuestionMerge(Question frage, Question tempFrage, boolean mailinbox, HashMap hm) {

		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":" + (frage != null ? frage.getId() + " " : " ");
		SkyLogger.getBRSLogger().info(logPrefix + " start");

		boolean ret = true;
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret = ret && agenturdel.preQuestionMerge(frage, tempFrage, mailinbox, hm);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		if (mailinboxextdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + iclazz + " start call");
			ret = ret && mailinboxextdel.preQuestionMerge(frage, tempFrage, mailinbox, hm);
			SkyLogger.getBRSLogger().debug(logPrefix + iclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return ret;
	}

	@Override
	public List<JMenuItem> getExtendedMenuItems(final List<SingleMode> smodes, Map<Object, Object> map) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().info(logPrefix + " start");

		SkyLogger.getClientLogger().info(logPrefix + "smodes has " + smodes.size() + " elements.");
		for (int i=0; i<smodes.size(); i++) {
			SkyLogger.getClientLogger().info(logPrefix + "smodes("+i+") has the value " + smodes.get(i).toString());
		}

		List<JMenuItem> ret = new LinkedList<>();
		try {

			if (smodes != null && !smodes.isEmpty()) {
				final int projectId = smodes.get(0).getProjectId();
				final boolean sbs = projectId == TagMatchDefinitions.SBS_PROJECT_ID;
				JMenuItem sbsForwarding = new JMenuItem(new AbstractAction("Weiterleiten an " + (sbs ? "SCS" : "SBS")) {
					@Override
					public void actionPerformed(ActionEvent e) {
						int size = smodes.size();
						if (size > 0) {
							int projectId = smodes.get(0).getProjectId();
							SkyLogger.getClientLogger().debug("smode project: " + projectId);
							int confirm = JOptionPane.showConfirmDialog(null, "Möchten Sie wirklich, dass die Frage" + (size > 1 ? "n" : "") + " an " + (sbs ? "SCS" : "SBS") + " weitergeleitet " + (size > 1 ? "werden" : "wird") + "?", "Tenant Forwarding", JOptionPane.OK_CANCEL_OPTION);
							if (confirm == JOptionPane.OK_OPTION) {
								for (SingleMode sNode : smodes) {
									ICQuestion questionAPI = API.getClientAPI().getQuestionAPI();
									Question question = questionAPI.load(sNode.getQuestionId());
									String message;
									if (question.getProjectId() == projectId) {
										SkyLogger.getClientLogger().debug("question project:" + question.getProjectId() + " locking:" + question.getLockedAt() + "," + question.getLockedBy());
										if (question.getLockedAt() == 0 && question.getLockedBy() == 0) {
											boolean done = ForwardingAction.forwardToTenant(question, false);
											questionAPI.releaseLock(question);
											message = done ? "Die Frage " + question.getId() + " konnte erfolgreich weitergeleitet werden." : "Fehler bei der Weiterleitung der Frage.";
										} else {
											message = "Die Frage " + question.getId() + " ist zur Zeit gesperrt. " + question.getProjectId() + " locking:" + question.getLockedAt() + "," + question.getLockedBy();
										}
									} else {
										message = "Die Frage " + question.getId() + " ist bereits im Zielprojekt.";
									}
									JOptionPane.showMessageDialog(null, message);
								}
							}
						}
					}
				});
				ret.add(sbsForwarding);
			}

		} catch (Exception e) {
			SkyLogger.getClientLogger().error("Exception during loading ExtendedAgencyMenuItems: ");
			if (map != null) {
				Set<Object> keys = map.keySet();
				for (Object singleKey : keys) {
					SkyLogger.getClientLogger().error("MAP:" + singleKey + ":" + map.get(singleKey));
				}
			}
			if (smodes != null) {
				for (SingleMode sNode : smodes) {
					if (smodes != null) {
						SkyLogger.getClientLogger().error("sNode:" + sNode.getQuestionId());
					}
				}
			}
		}
		SkyLogger.getClientLogger().debug("Return value: " + ret);
		/*List<JMenuItem> extItem = new MailInboxExtender().getExtendedItem();
		if (extItem!=null){
			ret.addAll(extItem);
		}*/

		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret.addAll(agenturdel.getExtendedMenuItems(smodes, map));
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		if (mailinboxextdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + iclazz + " start call");
			ret.addAll(mailinboxextdel.getExtendedMenuItems(smodes, map));
			SkyLogger.getBRSLogger().debug(logPrefix + iclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");

		return ret;
	}

	@Override
	public void postQuestionComplete(String loginname, Question question) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":" + (question != null ? question.getId() + " " : " ");
		SkyLogger.getBRSLogger().info(logPrefix + " start");

		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			agenturdel.postQuestionComplete(loginname, question);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		if (mailinboxextdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + iclazz + " start call");
			mailinboxextdel.postQuestionComplete(loginname, question);
			SkyLogger.getBRSLogger().debug(logPrefix + iclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " interim");
		API.getClientAPI().getQuestionAPI().store(question);
		ArchiveTool.archiveCase(question);
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
	}


	@Override
	public void postQuestionForward(String loginname, Question question, Subproject subproject, String address, int type) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":" + (question != null ? question.getId() + " " : " ");
		SkyLogger.getBRSLogger().info(logPrefix + " start");
        // SIT-18-10-056 - Jardel Luis Roth + Adam Ivanfi.
		// Has 'question' been forwarded to external processing, it has an e-mail address.
		// If it's been forwarded to another subproject, it's target e-mail address is empty.
		// For further details see ClientRunner.forwardServlet(...) too!
		// Next we only consider questions forwarded to external processing:
		if (question.getForwardedTo()!=null && !question.getForwardedTo().isEmpty()) {
			// External forwarding - "leading inbound" question management. Leading inbound question is
			// the one with the less VORGANGID in bundle. GUI shows it to have the other questions in bundle
			// as attachment, but that's the outbound master indeed (see ClientRunner.forwardServlet(...)).
			question.setStatus(Question.S_COMPLETED);
			question.setCompletedBy(question.getCompletedBy());
			API.getClientAPI().getQuestionAPI().store(question);
			SingleModeTableModel.getInstance().updateSingleMode(question);
			SkyLogger.getBRSLogger().debug(logPrefix + " Leading inbound question (ID=" + question.getId() + ") has been set to 'erledigt' and forwarded to external processing to the e-mail address '" + question.getForwardedTo() + "'");
		}
		else {
			SkyLogger.getBRSLogger().info(logPrefix + " Inbound question (ID=" + question.getId() + ") has been forwarded to the subproject (ID=" + question.getSubprojectForwardId() + ")");
		}

		if (address != null && type == 0 ) {
			ArchiveTool.archiveCase(question);
			logCompleted(loginname, question);
			SkyLogger.getBRSLogger().debug(logPrefix + "Finished with archiving: " + loginname + ":" + question.getId());
		} else {
			SkyLogger.getBRSLogger().info(logPrefix + "Finished without archiving: " + loginname + ":" + question.getId());
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			agenturdel.postQuestionForward(loginname, question, subproject, address, type);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		if (mailinboxextdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + iclazz + " start call");
			mailinboxextdel.postQuestionForward(loginname, question, subproject, address, type);
			SkyLogger.getBRSLogger().debug(logPrefix + iclazz + " finished call.");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish.");
	}

	@Override
	public boolean preQuestionForward(String loginname, Question question, Subproject subproject, String address, int type) {

		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":" + (question != null ? question.getId() + " " : " ");
		SkyLogger.getBRSLogger().info(logPrefix + " start");

		boolean forwarding = false;
		if (isValidSubprojectForwarding(subproject, question, type) && !isExternalForwarding(subproject)) {
			//AgDisabled
			/*SkyLogger.getClientLogger().info(logPrefix + "Call agentur delegate:" + loginname + ":" + question.getId());
			boolean ar_preQF_successfull = agenturRule.preQuestionForward(loginname, question, subproject, address, type);
			SkyLogger.getClientLogger().info(logPrefix + "Finished agentur delegate:" + loginname + ":" + question.getId() + " result:" + ar_preQF_successfull);

			if (ar_preQF_successfull) {
			 */
			String forwardingReason = null;
			BRSession session = de.ityx.mediatrix.modules.businessrules.tools.BRSessionPoolFactory.getBRSessionPool().getBRSession("SkyBulk");
			QuestionTablePanel a = (QuestionTablePanel) Repository.getObject(Repository.QUESTIONTABLEPANEL);
			JTable questionTable = a.getQuestionTable();
			int[] selectedRows = questionTable.getSelectedRows();
			SingleModeTableModel model = (SingleModeTableModel) questionTable.getModel();
			if (question.equals(model.getFrageMailInbox(selectedRows[0]))) {
				forwardingReason = selectForwardingReason(loginname, question);
				session.putPersistent("Forwarding", forwardingReason);
			} else {
				forwardingReason = (String) session.getPersistent("Forwarding");
			}
			SkyLogger.getClientLogger().debug(" forwarding reason: " + forwardingReason);
			forwarding = forwardingReason != null;
			if (forwarding) {
				addForwardingKeywordLog(loginname, question, forwardingReason);
			}
			/*} else{
					SkyLogger.getClientLogger().warn(logPrefix + "Not sucessfull AR_PRE_Forwarding " + loginname + ":" + question.getId() + " forwarding?:" + forwarding+ " subproject:"+subproject.getName());
		    //ToDo: hier muss eine Fehlermeldung eingebaut werden!!!!
			}*/
		} else if (isExternalForwarding(subproject)) {
			SkyLogger.getClientLogger().warn(logPrefix + "External Forwarding: " + loginname + ":" + question.getId() + " forwarding?:" + forwarding + " subproject:" + subproject.getName() + " type:" + type);
			forwarding=true;
		} else {
			SkyLogger.getClientLogger().warn(logPrefix + "Not Valid internal Forwarding: " + loginname + ":" + question.getId() + " forwarding?:" + forwarding + " subproject:" + subproject.getName() + " type:" + type);
		}

		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			forwarding=forwarding&agenturdel.preQuestionForward(loginname, question, subproject, address, type);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		if (mailinboxextdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + iclazz + " start call");
			forwarding=forwarding&mailinboxextdel.preQuestionForward(loginname, question, subproject, address, type);
			SkyLogger.getBRSLogger().debug(logPrefix + iclazz + " finished call");
		}
		SkyLogger.getClientLogger().info(logPrefix + "Finished forwarding:" + loginname + ":" + question.getId() + " result:" + forwarding + " subproject:" + subproject.getName());
		return forwarding;
	}

	private boolean isExternalForwarding(Subproject subproject) {
		return (subproject == null || subproject.getName() == null || subproject.getName().isEmpty());
	}

	@Override
	public boolean preQuestionMailPreview(String loginname, Question question, Subproject subproject) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":" + (question != null ? question.getId() + " " : " ");
		SkyLogger.getBRSLogger().info(logPrefix + " start");

		boolean ret = true;
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret=ret&agenturdel.preQuestionMailPreview(loginname, question, subproject);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		if (mailinboxextdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + iclazz + " start call");
			ret=ret&mailinboxextdel.preQuestionMailPreview(loginname, question, subproject);
			SkyLogger.getBRSLogger().debug(logPrefix + iclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish:"+ret);
		return ret;
	}

	@Override
	public void preQuestionOpen(Question question, Answer answer, Customer customer) throws QuestionOpenVeto {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":" + (question != null ? question.getId() + " " : " ");
		SkyLogger.getBRSLogger().debug(logPrefix + " start");

		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			agenturdel.preQuestionOpen( question, answer,  customer);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		if (mailinboxextdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + iclazz + " start call");
			mailinboxextdel.preQuestionOpen( question, answer,  customer);
			SkyLogger.getBRSLogger().debug(logPrefix + iclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
	}

	@Override
	public void postQuestionRequeue(String arg0, Question question, Subproject arg2, int arg3, long arg4) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":" + (question != null ? question.getId() + " " : " ");
		SkyLogger.getBRSLogger().info(logPrefix + " start");

		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			agenturdel.postQuestionRequeue(arg0, question, arg2, arg3, arg4);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		if (mailinboxextdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + iclazz + " start call");
			mailinboxextdel.postQuestionRequeue(arg0, question, arg2, arg3, arg4);
			SkyLogger.getBRSLogger().debug(logPrefix + iclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");

	}

	@Override
	public boolean preQuestionRequeue(String arg0, Question question, Subproject arg2, int arg3, long arg4) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":" + (question != null ? question.getId() + " " : " ");
		SkyLogger.getBRSLogger().info(logPrefix + " start");

		boolean ret = true;
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret=ret&agenturdel.preQuestionRequeue(arg0, question, arg2, arg3, arg4);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		if (mailinboxextdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + iclazz + " start call");
			ret=ret&mailinboxextdel.preQuestionRequeue(arg0, question, arg2, arg3, arg4);
			SkyLogger.getBRSLogger().debug(logPrefix + iclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish:"+ret);
		return ret;
	}

}