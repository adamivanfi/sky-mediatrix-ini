package com.nttdata.de.sky.ityx.mediatrix.businessrules.client;

import com.nttdata.de.lib.logging.SkyLogger;
import de.ityx.mediatrix.api.interfaces.businessrules.client.IClientAnswerView;
import de.ityx.mediatrix.api.interfaces.textobjects.ITextObject;
import de.ityx.mediatrix.data.Answer;
import de.ityx.mediatrix.data.Attachment;
import de.ityx.mediatrix.data.Case;
import de.ityx.mediatrix.data.Question;
import de.ityx.mediatrix.data.metainformation.MetaInformationInt;

import javax.swing.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class ClientAnswerView extends ClientOutboundRule implements IClientAnswerView {
	
	IClientAnswerView outbound_delegate = new de.ityx.sky.outbound.client.ClientAnswerView();
	private IClientAnswerView agenturdel = null;
	private final String aclazz = "de.ityx.agentursteuerung.ClientAnswerViewer";
	
	
	public ClientAnswerView() {
		String logPrefix =  "ClientAnswerView # Constructor ";
		try {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " initalization");
			Class aclass=Class.forName(aclazz);
			if (aclass!=null){
				Constructor aconstr=aclass.getConstructor(null);
				if (aconstr!=null){
					agenturdel = (IClientAnswerView) aconstr.newInstance(null);
				}
			}SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " initalized");
		} catch (NullPointerException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
			SkyLogger.getBRSLogger().warn(logPrefix + aclazz + " cannot be instantiated." + e.getMessage());
		}
	}
	
	@Override
	public void postAnswerSend(String loginname, final boolean isOperatorMode, Question question, Answer answer) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + " " + loginname + ":" + (question != null ? question.getId() + " " : " ");
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		
		outbound_delegate.postAnswerSend(loginname, isOperatorMode, question, answer);
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			agenturdel.postAnswerSend(loginname, isOperatorMode, question, answer);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		
	}
	
	@Override
	public List<AbstractButton> getExtButtonList(Answer answer) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":" + (answer != null ? answer.getId() + " " : " ");
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		List<AbstractButton> ret = null;
		ret.addAll(outbound_delegate.getExtButtonList(answer));
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret.addAll(agenturdel.getExtButtonList(answer));
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return ret;
	}
	
	@Override
	public List<JComponent> getTabList(Question question, Answer answer) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":" + (question != null ? question.getId() + " a:" : " a:") + (answer != null ? answer.getId() + " " : " ");
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		
		List<JComponent> ret = null;
		SkyLogger.getBRSLogger().debug("Return value: " + ret);
		ret.addAll(outbound_delegate.getTabList(question, answer));
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret.addAll(agenturdel.getTabList(question, answer));
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return ret;
	}
	
	@Override
	public final boolean preAttachmentView(String loginname, final boolean isOperatorMode, Question question, Answer answer, Attachment attachment) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":" + (question != null ? question.getId() + " a:" : " a:") + (answer != null ? answer.getId() + " " : " ");
		SkyLogger.getBRSLogger().debug(logPrefix +  " start");
		
		boolean ret = true;
		SkyLogger.getBRSLogger().debug("preAttachmentView:" + question.getId());
		ret = ret && outbound_delegate.preAttachmentView(loginname, isOperatorMode, question, answer, attachment);
		
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret = ret && agenturdel.preAttachmentView(loginname, isOperatorMode, question, answer, attachment);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return ret;
	}
	
	@Override
	public final boolean postAttachmentView(String loginname, final boolean isOperatorMode, Question question, Answer answer, Attachment attachment) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":" + (question != null ? question.getId() + " a:" : " a:") + (answer != null ? answer.getId() + " " : " ");
		SkyLogger.getBRSLogger().debug(logPrefix +  " start");
		
		boolean ret = true;
		SkyLogger.getBRSLogger().debug("postAttachmentView:" + question.getId());
		ret = ret &&  outbound_delegate.postAttachmentView(loginname, isOperatorMode, question, answer, attachment);
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret = ret && agenturdel.postAttachmentView(loginname, isOperatorMode, question, answer, attachment);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return ret;
	}
	
	@Override
	public final boolean preAttachmentInsert(String loginname, final boolean isOperatorMode, Question question, Answer answer, Attachment attachment) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":" + (question != null ? question.getId() + " a:" : " a:") + (answer != null ? answer.getId() + " " : " ");
		SkyLogger.getBRSLogger().debug(logPrefix +  " start");
		
		boolean ret = true;
		ret = ret &&  outbound_delegate.preAttachmentInsert(loginname, isOperatorMode, question, answer, attachment);
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret = ret && agenturdel.preAttachmentInsert(loginname, isOperatorMode, question, answer, attachment);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return ret;
	}
	
	@Override
	public final boolean postAttachmentInsert(String loginname, final boolean isOperatorMode, Question question, Answer answer, Attachment attachment) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":" + (question != null ? question.getId() + " a:" : " a:") + (answer != null ? answer.getId() + " " : " ");
		SkyLogger.getBRSLogger().debug(logPrefix +  " start");

		boolean ret = true;
		ret = ret &&  outbound_delegate.postAttachmentInsert(loginname, isOperatorMode, question, answer, attachment);
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret = ret && agenturdel.postAttachmentInsert(loginname, isOperatorMode, question, answer, attachment);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return ret;
	}
	
	@Override
	public final boolean preHistoryInsert(String loginname, final boolean isOperatorMode, Question question, Answer answer, Attachment attachment) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":" + (question != null ? question.getId() + " a:" : " a:") + (answer != null ? answer.getId() + " " : " ");
		SkyLogger.getBRSLogger().debug(logPrefix +  " start");
		boolean ret = true;
		ret = ret &&  outbound_delegate.preHistoryInsert(loginname, isOperatorMode, question, answer, attachment);
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret = ret && agenturdel.preHistoryInsert(loginname, isOperatorMode, question, answer, attachment);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return ret;
	}
	
	@Override
	public final boolean postHistoryInsert(String loginname, final boolean isOperatorMode, Question question, Answer answer, Attachment attachment) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":" + (question != null ? question.getId() + " a:" : " a:") + (answer != null ? answer.getId() + " " : " ");
		SkyLogger.getBRSLogger().debug(logPrefix +  " start");
		boolean ret = true;
		ret = ret &&  outbound_delegate.postHistoryInsert(loginname, isOperatorMode, question, answer, attachment);
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret = ret && agenturdel.postHistoryInsert(loginname, isOperatorMode, question, answer, attachment);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return ret;
	}
	
	@Override
	public final boolean preAttachmentDelete(String loginname, final boolean isOperatorMode, Question question, Answer answer, Attachment attachment) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":" + (question != null ? question.getId() + " a:" : " a:") + (answer != null ? answer.getId() + " " : " ");
		SkyLogger.getBRSLogger().debug(logPrefix +  " start");
		boolean ret = true;
		ret = ret &&  outbound_delegate.preAttachmentDelete(loginname, isOperatorMode, question, answer, attachment);
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret = ret && agenturdel.preAttachmentDelete(loginname, isOperatorMode, question, answer, attachment);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return ret;
	}
	
	@Override
	public final boolean postAttachmentDelete(String loginname, final boolean isOperatorMode, Question question, Answer answer, Attachment attachment) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":" + (question != null ? question.getId() + " a:" : " a:") + (answer != null ? answer.getId() + " " : " ");
		SkyLogger.getBRSLogger().debug(logPrefix +  " start");
		
		boolean ret = true;
		ret = ret &&  outbound_delegate.postAttachmentDelete(loginname, isOperatorMode, question, answer, attachment);
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret = ret && agenturdel.postAttachmentDelete(loginname, isOperatorMode, question, answer, attachment);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return ret;
	}
	
	@Override
	public final boolean preTextObjectInsert(String loginname, final boolean isOperatorMode, Question question, Answer answer, ITextObject tb) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":" + (question != null ? question.getId() + " a:" : " a:") + (answer != null ? answer.getId() + " " : " ");
		SkyLogger.getBRSLogger().debug(logPrefix +  " start");

		boolean ret = true;
		ret = ret &&  outbound_delegate.preTextObjectInsert(loginname, isOperatorMode, question, answer, tb);
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret = ret && agenturdel.preTextObjectInsert(loginname, isOperatorMode, question, answer, tb);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return ret;
	}
	
	@Override
	public void postTextObjectInsert(String loginname, final boolean isOperatorMode, Question question, Answer answer, ITextObject tb) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":" + (question != null ? question.getId() + " a:" : " a:") + (answer != null ? answer.getId() + " " : " ");
		SkyLogger.getBRSLogger().debug(logPrefix +  " start");
		
		outbound_delegate.postTextObjectInsert(loginname, isOperatorMode, question, answer, tb);
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			agenturdel.postTextObjectInsert(loginname, isOperatorMode, question, answer, tb);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
	}
	
	@Override
	public void postMetaInformationView(String loginname, final boolean isOperatorMode, Answer answer, MetaInformationInt metaInfo) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":" + (answer != null ? answer.getId() + " " : " ");
		SkyLogger.getBRSLogger().debug(logPrefix +  " start");
		
		outbound_delegate.postMetaInformationView(loginname, isOperatorMode, answer, metaInfo);
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			agenturdel.postMetaInformationView(loginname, isOperatorMode, answer, metaInfo);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");

	}
	
	@Override
	public final boolean preMetaInformationView(String loginname, final boolean isOperatorMode, Answer answer, MetaInformationInt metaInfo) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":" + (answer != null ? answer.getId() + " " : " ");
		SkyLogger.getBRSLogger().debug(logPrefix +  " start");

		boolean ret = true;
		ret = ret &&  outbound_delegate.preMetaInformationView(loginname, isOperatorMode, answer, metaInfo);
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret = ret && agenturdel.preMetaInformationView(loginname, isOperatorMode, answer, metaInfo);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return ret;
	}
	
	@Override
	public void postAnswerView(String arg0, Question arg1, Answer answer, Case arg3) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":"  + (answer != null ? answer.getId() + " " : " ");
		SkyLogger.getBRSLogger().debug(logPrefix +  " start");

		outbound_delegate.postAnswerView(arg0, arg1, answer, arg3);
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			agenturdel.postAnswerView(arg0, arg1, answer, arg3);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
	}
	
	@Override
	public final boolean preAnswerSend(String loginname, final boolean isOperatorMode, Question question, Answer answer) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":" + (question != null ? question.getId() + " a:" : " a:") + (answer != null ? answer.getId() + " " : " ");
		SkyLogger.getBRSLogger().debug(logPrefix +  " start");

		boolean ret = false;
		if (checkMXTags(answer.getBody()) && selectPreSend(loginname, question, answer)) {
			SkyLogger.getBRSLogger().debug("preAnswerSend");
			if (outbound_delegate.preAnswerSend(loginname, isOperatorMode, question, answer)) {
				if (answer.getStatus().equals(Answer.S_MONITORED)) {
					logCompleted(loginname, question);
				}
				ret = true;
			}
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret = ret && agenturdel.preAnswerSend(loginname, isOperatorMode, question, answer);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return ret;
	}
	
	@Override
	public final boolean preAnswerView(String arg0, Question question, Answer answer, Case arg3) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":" + (question != null ? question.getId() + " a:" : " a:") + (answer != null ? answer.getId() + " " : " ");
		SkyLogger.getBRSLogger().debug(logPrefix +  " start");

		boolean ret = true;
		ret = ret &&  outbound_delegate.preAnswerView(arg0, question, answer, arg3);
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret = ret && agenturdel.preAnswerView(arg0, question, answer, arg3);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return ret;
	}
}
