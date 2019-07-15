package com.nttdata.de.sky.ityx.mediatrix.businessrules.client;

import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import com.nttdata.de.sky.ityx.mediatrix.businessrules.archiving.ArchiveTool;
import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.api.interfaces.businessrules.client.IClientMailInbox;
import de.ityx.mediatrix.api.interfaces.businessrules.client.IClientQuestionView;
import de.ityx.mediatrix.data.*;
import de.ityx.mediatrix.data.metainformation.MetaInformationInt;

import javax.swing.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClientQuestionView extends ClientOutboundRule implements IClientQuestionView {

	IClientQuestionView outbound_delegate = new de.ityx.sky.outbound.client.ClientQuestionView();
	
	private IClientQuestionView agenturdel = null;
	private final String aclazz = "de.ityx.agentursteuerung.ClientQuestionView";

	public ClientQuestionView() {
		String logPrefix = "ClientQuestionView # Constructor ";
		try {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " initalization");
			Class aclass=Class.forName(aclazz);
			if (aclass!=null){
				Constructor aconstr=aclass.getConstructor(null);
				if (aconstr!=null){
					agenturdel = (IClientQuestionView) aconstr.newInstance(null);
				}
			}SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " initalized");
		} catch (NullPointerException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
			SkyLogger.getBRSLogger().warn(logPrefix + aclazz + " cannot be instantiated." + e.getMessage());
		}
	}

	@Override
	public List<AbstractButton> getExtButtonList(final Question frage) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");

		List<AbstractButton> ret = null;
		// Bugfix (java.lang.NullPointerException) - Ivanfi 28.11.2018.
		ret = new ArrayList<>();
		ret.addAll(outbound_delegate.getExtButtonList(frage));
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret.addAll(agenturdel.getExtButtonList(frage));
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return ret;
	}

	@Override
	public void postQuestionForward(String loginname, boolean isOperatorModus, Question question, Subproject teilprojekt, String adresse, int typ) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");

		outbound_delegate.postQuestionForward(loginname, isOperatorModus, question, teilprojekt, adresse, typ);
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			agenturdel.postQuestionForward(loginname, isOperatorModus, question, teilprojekt, adresse, typ);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		ArchiveTool.archiveCaseWithAnswers(question);
		logCompleted(loginname, question);
		SkyLogger.getClientLogger().info(logPrefix + ": exit");
	}

	@Override
	public boolean preQuestionView(String loginname, Question frage, Case vorgang) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");

		boolean ret = true;
		SkyLogger.getBRSLogger().debug(logPrefix + "preAttachmentView:");
		ret = ret && outbound_delegate.preQuestionView(loginname, frage, vorgang);

		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret = ret && agenturdel.preQuestionView(loginname, frage, vorgang);
			;
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return ret;
	}

	@Override
	public boolean preQuestionComplete(String loginname, boolean oper, Question question) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");

		boolean complete = outbound_delegate.preQuestionComplete(loginname, oper, question);

		if (complete) {
			if (ArchiveTool.checkIsArchivingNeeded(question)) {
				if (!TagMatchDefinitions.isNotSbsProject(question) || ArchiveTool.checkIsArchivingPossible(question)) {
					SkyLogger.getClientLogger().debug(logPrefix + "Archiving for Question:" + question.getId() + " needed and possible.");
				} else {
					SkyLogger.getClientLogger().warn(logPrefix + "Archiving for Question:" + question.getId() + " needed but not possible.");
					//TODO: need to be tested:

					String check = System.getProperty("metacompletecheckdisabled", "false");
					if (check != null && !check.isEmpty() && !check.equalsIgnoreCase("true")) {
						JOptionPane.showMessageDialog(null, "Es fehlen notwendige Meta-Daten um die Frage:" + question.getId() + " abzuschliessen. Bitte Kunden Re-indizieren.");
						complete = false;
					}
				}
			} else {
				SkyLogger.getClientLogger().debug(logPrefix + "Archiving not needed for Question:" + question.getId());
			}
			complete = askCompletionParametersAndExecuteAction(loginname, question, null, REASON_COMPLETE, null, true);
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			complete = complete && agenturdel.preQuestionComplete(loginname, oper, question);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return complete;
	}

	@Override
	public boolean preQuestionStore(String loginname, boolean isOperatorModus, Question frage) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");

		boolean ret = true;
		ret = ret && outbound_delegate.preQuestionStore(loginname, isOperatorModus, frage);

		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret = ret && agenturdel.preQuestionStore(loginname, isOperatorModus, frage);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return ret;
	}

	@Override
	public boolean preQuestionMerge(Question frage, Question tempFrage, boolean mailinbox, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		boolean ret = true;
		ret = ret && outbound_delegate.preQuestionMerge(frage, tempFrage, mailinbox, hm);

		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret = ret && agenturdel.preQuestionMerge(frage, tempFrage, mailinbox, hm);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return ret;
	}

	@Override
	public List<JComponent> getTabList(Question question) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");

		List<JComponent> ret = null;
		// Bugfix (java.lang.NullPointerException) - Ivanfi 28.11.2018.
		ret = new ArrayList<>();
		ret.addAll(outbound_delegate.getTabList(question));
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret.addAll(agenturdel.getTabList(question));
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return ret;
	}

	@Override
	public boolean postAttachmentDelete(String loginname, boolean isOperatorMode, Question question, Attachment attachment) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		boolean ret = true;
		ret = ret && outbound_delegate.postAttachmentDelete(loginname, isOperatorMode, question, attachment);

		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret = ret && agenturdel.postAttachmentDelete(loginname, isOperatorMode, question, attachment);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return ret;
	}

	@Override
	public boolean postAttachmentInsert(String loginname, boolean isOperatorMode, Question question, Attachment attachment) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		boolean ret = true;
		ret = ret && outbound_delegate.postAttachmentInsert(loginname, isOperatorMode, question, attachment);

		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret = ret && agenturdel.postAttachmentInsert(loginname, isOperatorMode, question, attachment);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return ret;
	}

	@Override
	public boolean postAttachmentView(String loginname, boolean isOperatorMode, Question question, Attachment attachment) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		boolean ret = true;
		ret = ret && outbound_delegate.postAttachmentView(loginname, isOperatorMode, question, attachment);

		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret = ret && agenturdel.postAttachmentView(loginname, isOperatorMode, question, attachment);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return ret;
	}

	@Override
	public void postKeywordDelete(String loginname, boolean isOperatorMode, Question question, Keyword keyword) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		outbound_delegate.postKeywordDelete(loginname, isOperatorMode, question, keyword);
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			agenturdel.postKeywordDelete(loginname, isOperatorMode, question, keyword);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
	}

	@Override
	public void postKeywordInsert(String loginname, boolean isOperatorMode, Question question, Answer answer, Keyword keyword) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		outbound_delegate.postKeywordInsert(loginname, isOperatorMode, question, answer, keyword);
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			agenturdel.postKeywordInsert(loginname, isOperatorMode, question, answer, keyword);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
	}

	@Override
	public Object postMetaInformationView(String login, boolean isOperatorMode, Question question, MetaInformationInt metaInfo) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		Object o= outbound_delegate.postMetaInformationView(login, isOperatorMode, question, metaInfo);
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			return agenturdel.postMetaInformationView(login, isOperatorMode, question, metaInfo);
			//SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return o;
	}

	@Override
	public void postQuestionComplete(String loginname, boolean isOperatorMode, Question question) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");

		//AgDisabled
		// agentur.postQuestionComplete(loginname, isOperatorMode, question);
		outbound_delegate.postQuestionComplete(loginname, isOperatorMode, question);
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			agenturdel.postQuestionComplete(loginname, isOperatorMode, question);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		API.getClientAPI().getQuestionAPI().store(question);
		
		ArchiveTool.archiveCase(question);
		logCompleted(loginname, question);
		SkyLogger.getClientLogger().info(logPrefix + ": exit");
	}

	@Override
	public boolean postQuestionMerge(Question question, Question tempQuestion, boolean mailinbox, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		boolean ret = true;
		ret = ret && outbound_delegate.postQuestionMerge(question, tempQuestion, mailinbox, hm);

		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret = ret && agenturdel.postQuestionMerge(question, tempQuestion, mailinbox, hm);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return ret;
	}

	@Override
	public void postQuestionProcessing(String loginname, boolean isOperatorMode, Question question) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		outbound_delegate.postQuestionProcessing(loginname, isOperatorMode, question);
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			agenturdel.postQuestionProcessing(loginname, isOperatorMode, question);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
	}

	@Override
	public void postQuestionStore(String loginname, boolean isOperatorMode, Question question) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		throw new UnsupportedOperationException(Messages.getString("ClientQuestionView.15"));


	}

	@Override
	public void postQuestionView(String loginname, Question question, Case ccase) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		outbound_delegate.postQuestionView(loginname, question, ccase);
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			agenturdel.postQuestionView(loginname, question, ccase);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
	}

	@Override
	public boolean preAttachmentDelete(String loginname, boolean isOperatorMode, Question question, Attachment attachment) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		boolean ret = true;
		ret = ret && outbound_delegate.preAttachmentDelete(loginname, isOperatorMode, question, attachment);

		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret = ret && agenturdel.preAttachmentDelete(loginname, isOperatorMode, question, attachment);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return ret;
	}

	@Override
	public boolean preAttachmentInsert(String loginname, boolean isOperatorMode, Question question, Attachment attachment) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		boolean ret = true;
		ret = ret && outbound_delegate.preAttachmentInsert(loginname, isOperatorMode, question, attachment);

		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret = ret && agenturdel.preAttachmentInsert(loginname, isOperatorMode, question, attachment);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return ret;
	}

	@Override
	public boolean preAttachmentView(String loginname, boolean isOperatorMode, Question question, Attachment attachment) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		boolean ret = true;
		ret = ret && outbound_delegate.preAttachmentView(loginname, isOperatorMode, question, attachment);

		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret = ret && agenturdel.preAttachmentView(loginname, isOperatorMode, question, attachment);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return ret;
	}

	@Override
	public boolean preKeywordDelete(String loginname, boolean isOperatorMode, Question question, Keyword keyword) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		boolean ret = true;
		ret = ret && outbound_delegate.preKeywordDelete(loginname, isOperatorMode, question, keyword);

		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret = ret && agenturdel.preKeywordDelete(loginname, isOperatorMode, question, keyword);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return ret;
	}

	@Override
	public boolean preKeywordInsert(String loginname, boolean isOperatorMode, Question question, Answer answer, Keyword keyword) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");

		boolean ret = true;
		ret = ret && outbound_delegate.preKeywordInsert(loginname, isOperatorMode, question, answer, keyword);

		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret = ret && agenturdel.preKeywordInsert(loginname, isOperatorMode, question, answer, keyword);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return ret;
	}

	@Override
	public boolean preMetaInformationView(String loginname, boolean isOperatorMode, Question question, MetaInformationInt metaInfo) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");

		boolean ret = true;
		ret = ret && outbound_delegate.preMetaInformationView(loginname, isOperatorMode, question, metaInfo);

		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret = ret && agenturdel.preMetaInformationView(loginname, isOperatorMode, question, metaInfo);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return ret;
	}


	@Override
	public boolean preQuestionForward(String loginname, boolean isOperatorMode, Question question, Subproject subproject, String address, int type) {
		//AgDisabled
		//return  agentur.preQuestionForward(loginname, isOperatorMode, question, subproject, address, type) && checkForwarding(loginname, question, subproject, type);
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		boolean ret = true;
		ret = ret && outbound_delegate.preQuestionForward(loginname, isOperatorMode, question, subproject, address, type) && checkForwarding(loginname, question, subproject, type);

		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret = ret && agenturdel.preQuestionForward(loginname, isOperatorMode, question, subproject, address, type) && checkForwarding(loginname, question, subproject, type);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return ret;
	}

	@Override
	public boolean preQuestionProcessing(String loginname, boolean isOperatorMode, Question question) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");

		boolean ret = true;
		ret = ret && outbound_delegate.preQuestionProcessing(loginname, isOperatorMode, question);

		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret = ret && agenturdel.preQuestionProcessing(loginname, isOperatorMode, question);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return ret;
	}

	@Override
	public void postQuestionRequeue(String arg0, boolean arg1, Question arg2, Subproject arg3, int arg4, long arg5) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		outbound_delegate.postQuestionRequeue(arg0, arg1, arg2, arg3, arg4, arg5);
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			agenturdel.postQuestionRequeue(arg0, arg1, arg2, arg3, arg4, arg5);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
	}

	@Override
	public boolean preQuestionRequeue(String arg0, boolean arg1, Question arg2, Subproject arg3, int arg4, long arg5) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		boolean ret = true;
		ret = ret && outbound_delegate.preQuestionRequeue(arg0, arg1, arg2, arg3, arg4, arg5);

		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret = ret && agenturdel.preQuestionRequeue(arg0, arg1, arg2, arg3, arg4, arg5);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return ret;
	}
}
