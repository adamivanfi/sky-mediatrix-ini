package com.nttdata.de.sky.ityx.mediatrix.businessrules.client;

import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.mediatrix.businessrules.archiving.ArchiveTool;
import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.api.interfaces.IViewFilter;
import de.ityx.mediatrix.api.interfaces.businessrules.client.IClientRunner;
import de.ityx.mediatrix.client.util.SingleModeTableModel;
import de.ityx.mediatrix.data.Question;
import de.ityx.mediatrix.data.SingleMode;

import javax.swing.*;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientRunner implements IClientRunner {

	@Override
	public HashMap checkViewFilter(JPanel arg0, IViewFilter arg1, boolean arg2,
			HashMap arg3) {
		// TODO Auto-generated method stub
		return arg3;
	}

	@Override
	public void forwardServlet(String loginname, boolean arg1, Question question, HashMap arg3) {
		//SIT-18-10-056 - Jardel Luis Roth + Adam Ivanfi.
		// "question" refers to "outbound master", on external forwarding additionally.
		// Its attachment questions are the unique attQuestion-s, see below... of the bundle without "leading inbound" question.
		// Leading inbound question is managed in ClientMailInbox.postQuestionForward(...).
		final String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + " - question-ID:" + (question != null ? question.getId() + " " : " ");
		// Has 'question' been forwarded to external processing, it has an e-mail address.
		// If it's been forwarded to another subproject, it's target e-mail address is empty.
		// Execution for questions being forwarded to another subproject ends here:
		if (question.getForwardedTo()==null || question.getForwardedTo().isEmpty()) {
			SkyLogger.getClientLogger().info(logPrefix + "Question (ID=" + question.getId() + ") has been forwarded to subproject (ID=" + question.getSubprojectForwardId() + ")");
			// Leave execution:
			return;
		}

		String attQuestionIDList = "";
		Integer attachmentCount = question.getAttachments().size();
		SkyLogger.getClientLogger().debug(logPrefix + " - forwarding 'question-ID' (master) for external processing with " + attachmentCount.toString() + "(+1) attachments.");
		for (int i=0; i<attachmentCount; i++) {
			String attFileName = question.getAttachments().get(i).getFilename();
            SkyLogger.getClientLogger().debug(logPrefix + "Attachment name: " + attFileName);
			Pattern qidPattern = Pattern.compile("\\(question_([0-9]*)?\\).msg");
			Matcher matcher = qidPattern.matcher(attFileName);
			// Check if questionID-related regex matches attachment file name:
			if (matcher.find()) {
				SkyLogger.getClientLogger().debug(logPrefix + " Pattern " + qidPattern.toString() + " matches attached file name '" + attFileName + "'");
				// String number = question.getAttachments().get(i).getFilename().substring(question.getAttachments().get(i).getFilename().indexOf("(question_") + 10, question.getAttachments().get(i).getFilename().indexOf(").msg"));
				final String questionid_str = matcher.group(1);
				final Integer questionid = Integer.parseInt(questionid_str);
				attQuestionIDList += (questionid_str+",");
				//
				Integer completedBy = question.getCompletedBy();
				Question attQuestion = API.getClientAPI().getQuestionAPI().load(questionid);
				attQuestion.setStatus(Question.S_COMPLETED);
				attQuestion.setCompletedBy(completedBy);
				attQuestion.setForwardedTo(question.getForwardedTo());
				API.getClientAPI().getQuestionAPI().store(attQuestion);
				SingleModeTableModel.getInstance().updateSingleMode(attQuestion);
				ArchiveTool.archiveCase(attQuestion);
				SkyLogger.getClientLogger().debug(logPrefix + " Attachment related inbound question (ID=" + questionid_str + ") has been set to 'erledigt', sent to archiving and forwarded to external processing to the e-mail address: '" + question.getForwardedTo() + "'");
            }
			else {
				SkyLogger.getClientLogger().info(logPrefix + " Pattern " + qidPattern.toString() + " doesn't match attached file name '" + attFileName + "'");
			}
        }
        if (0 < attachmentCount) {
			SkyLogger.getClientLogger().info(logPrefix + " - External forwarding for attachment bundle (question list:" + attQuestionIDList.substring(0, attQuestionIDList.length() - 1) + ") of 'question-ID' (master) completed, archived.");
		}
	}


	@Override
	public HashMap getParameter(HashMap arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public HashMap<String, Object> getSystemLogin(HashMap arg0) {
		SkyLogger.getClientLogger().info("Enter");
		String uname = System.getProperty("user.name");
		String login = uname.toLowerCase();
		SkyLogger.getClientLogger().info(
				"User: " + uname + " Login: " + login);
		arg0.put("username", login);
		SkyLogger.getClientLogger().info("exit");
		return arg0;
	}

	@Override
	public HashMap<String, Object> matchFoundFilter(SingleMode arg0,
			List<IViewFilter> arg1, HashMap arg2) {
		// TODO Auto-generated method stub
		return arg2;
	}

	@Override
	public HashMap<String, Object> matchNotFoundFilter(SingleMode arg0,
			List<IViewFilter> arg1, HashMap arg2) {
		// TODO Auto-generated method stub
		return arg2;
	}

	@Override
	public void onShutDown() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStartup() {
		// TODO Auto-generated method stub

	}

	@Override
	public void postPingSend(Hashtable arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean prePingSend(HashMap arg0) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public HashMap setParameter(HashMap arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public HashMap setViewFilterComponent(JPanel arg0, HashMap arg1) {
		// TODO Auto-generated method stub
		return arg1;
	}

}
