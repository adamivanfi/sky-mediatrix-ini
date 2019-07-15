package com.nttdata.de.sky.ityx.common;

import com.nttdata.de.lib.logging.SkyLogger;
import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.client.dialog.Start;
import de.ityx.mediatrix.client.dialog.operate.WaitQueue;
import de.ityx.mediatrix.client.dialog.util.newmail.NewMailFrame;
import de.ityx.mediatrix.client.util.QuestionActions;
import de.ityx.mediatrix.client.util.Repository;
import de.ityx.mediatrix.data.ProjectInfo;
import de.ityx.mediatrix.data.Question;

import javax.swing.*;
import javax.websocket.*;
import java.awt.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import de.ityx.mediatrix.client.dialog.util.newmail.NewMailFrame;

/**
 * Created by meinusch on 03.08.15.
 */

@ClientEndpoint(encoders = PushToUserEncoder.class, decoders = PushToUserDecoder.class)
public class PushToUserClient {
	private Session userSession = null;
	URI endpointURI = null;
	private String login;

	public PushToUserClient(String login) {
		this.login = login;
		try {
			String servletUrl = (API.getServletUrl() != null && !API.getServletUrl().isEmpty()) ? API.getServletUrl() : null;

			if (servletUrl == null || servletUrl.isEmpty()) {
				String ityx_environment_type = System.getProperty("ityx_environment_type");
				if (ityx_environment_type != null && ityx_environment_type.equalsIgnoreCase("integration")) {
					servletUrl = "http://int.mediatrix.sky.de:8080/";
				} else {
					servletUrl = "http://mediatrix.sky.de:8080/";
				}
			}

			Matcher matcher = Pattern.compile("http(s?:\\/\\/[0-9a-zA-Z\\.\\-]*(:[0-9]*)?)*?\\/").matcher(servletUrl);
			if (matcher.find()) {
				if (API.isClient() && API.getClientAPI().getConnectionAPI().getHttpSession() != null) {
					endpointURI = new URI("ws" + matcher.group(1) + "/contex-ws/pushToUser/" + login + ";jsessionid=" + API.getClientAPI().getConnectionAPI().getHttpSession());
					SkyLogger.getClientLogger().info("PushToUserClient found cURL:" + endpointURI.toString());
				} else {
					endpointURI = new URI("ws" + matcher.group(1) + "/contex-ws/pushToUser/" + login);
					SkyLogger.getClientLogger().debug("PushToUserClient found sURL:" + endpointURI.toString());
				}
			} else {
				endpointURI = new URI("ws://mediatrix.sky.de:8080/contex-ws/pushToUser/" + login);
				SkyLogger.getClientLogger().debug("PushToUserClient found fURL:" + endpointURI.toString());
			}
			SkyLogger.getClientLogger().info("PushToUserClient Connectingto:" + endpointURI.toString());
			ContainerProvider.getWebSocketContainer().connectToServer(this, endpointURI);
			SkyLogger.getClientLogger().debug("PushToUserClient Connected to:" + endpointURI.toString());
		} catch (Exception e) {
			SkyLogger.getClientLogger().error("PushToUserClient Error:" + e.getMessage(), e);
		}
	}

	public PushToUserClient() {
		this((API.isClient() && API.getClientAPI().getConnectionAPI().getCurrentOperator() != null) ? API.getClientAPI().getConnectionAPI().getCurrentOperator().getLogin() : "server");
	}

	@OnOpen
	public void onOpen(Session session, EndpointConfig endpointConfig) {
		this.userSession = session;
		//userSession.getUserProperties().put("username", username);
		SkyLogger.getClientLogger().info("PushToUserClient session:" + session.getId() + " openend for l:" + login); // and bound to user: " + username);
	}

	@OnClose
	public void onClose(final Session userSession, final CloseReason reason) {
		SkyLogger.getClientLogger().info("PushToUserClient session:" + userSession.getId() + " l:" + login + " close:" + reason.getReasonPhrase());
		if (userSession.isOpen()) {
			SkyLogger.getClientLogger().info("PushToUserClient onClose:" + reason.getCloseCode() + " msg:" + reason.getReasonPhrase());
		}
	}

	@OnMessage
	public void onMessage(final PushToUserMessage message) throws InvocationTargetException, InterruptedException {
		SkyLogger.getClientLogger().info("PushToUserClient " + login + " MsgReceived: u:" + message.getUser() + " c:" + message.getType());

		if (API.isClient()) {
			if (message.getType().equals(PushToUserMessageCrmPopup.MSG_TYPE)) {
				PushToUserMessageCrmPopup messageCrm = (PushToUserMessageCrmPopup) message;
				/*Runnable act = new Runnable() {
				@Override
				public void run() {
					Start.getInstance().openNewMailFrame();
					SkyLogger.getClientLogger().info("PushToUserClient "+login+" run new email:u:" + message.getUser() + " c:" + message.getCustomerid() + " a:" + message.getActivityid() + " sc:" + message.getSblcontactid());
				}
			};*/
				try {
					SkyLogger.getClientLogger().info("PushToUserClient " + login + " MsgReceived: u:" + message.getUser() + " c:" + messageCrm.getCustomerid() + " a:" + messageCrm.getActivityid() + " sc:" + messageCrm.getSblcontactid());
					SkyLogger.getClientLogger().info("PushToUserClient " + login + " start: u:" + message.getUser() + " c:" + messageCrm.getCustomerid() + " a:" + messageCrm.getActivityid() + " sc:" + messageCrm.getSblcontactid());
					//SwingUtilities.invokeAndWait(act);
					List<JFrame> frames = null;
				/*if (Repository.hasObject(Repository.NEWMAILFRAME)) {
					SkyLogger.getClientLogger().info("PushToUserClient "+login+" existing frame found: u:" + message.getUser() + " c:" + message.getCustomerid() + " a:" + message.getActivityid() + " sc:" + message.getSblcontactid());
					frames = (List<JFrame>) Repository.getObject(Repository.NEWMAILFRAME);
				}*/


					NewMailFrame newMail;
					Question question = new Question();
					question = setMetaData(question, messageCrm);

					//if (frames == null || frames.isEmpty()) {
						newMail = new ExtendedNewMailFrame(question);
						SkyLogger.getClientLogger().info("PushToUserClient " + login + " newFrame: u:" + message.getUser() + " c:" + messageCrm.getCustomerid() + " a:" + messageCrm.getActivityid() + " sc:" + messageCrm.getSblcontactid());

				/*if (ExtendedNewMailEvent.this.id > 0) {
					newMail.setselectedTeilprojekt(ExtendedNewMailEvent.this.id);
				}*/
					/*} else {
						SkyLogger.getClientLogger().info("PushToUserClient " + login + " existingFrame:frameFound: u:" + message.getUser() + " c:" + messageCrm.getCustomerid() + " a:" + messageCrm.getActivityid() + " sc:" + messageCrm.getSblcontactid());
						newMail = (NewMailFrame) frames.get(0);
						//newMail.setState(java.awt.Frame.ICONIFIED);
						//newMail.setState(java.awt.Frame.NORMAL);
						SkyLogger.getClientLogger().info("PushToUserClient " + login + " headersSet: q" + question.getId() + " u:" + message.getUser() + " c:" + messageCrm.getCustomerid() + " a:" + messageCrm.getActivityid() + " sc:" + messageCrm.getSblcontactid());
					}*/

					int projectId = 0;
					SkyLogger.getClientLogger().info("PushToUserClient " + login + " newFragme: u:" + message.getUser() + " c:" + messageCrm.getCustomerid() + " a:" + messageCrm.getActivityid() + " sc:" + messageCrm.getSblcontactid());
					List<ProjectInfo> projectList = API.getClientAPI().getProjectAPI().loadProjectInfoList();
					for (ProjectInfo project : projectList) {
						if (project.getName().toLowerCase().indexOf("sky") > 0) {
							projectId = project.getId();
						}
					}

					newMail.setProjektID(projectId);

					newMail.setVisible(true);
					newMail.setDefaultFocus();
					//newMail.setState(java.awt.Frame.ICONIFIED);
					//newMail.setState(java.awt.Frame.NORMAL);
					newMail.getJMenuBar().setVisible(false);
					newMail.setTitle("Neues Anschreiben erstellen");
					newMail.toFront();
					newMail.repaint();
					newMail.setLocationRelativeTo((Component) Repository.getObject(Repository.MAINWINDOW));

					if (question.getProjectId() == 0) {
						newMail.initProjektID();
					}
					SkyLogger.getClientLogger().info("PushToUserClient " + login + " finish: u:" + message.getUser() + " c:" + messageCrm.getCustomerid() + " a:" + messageCrm.getActivityid() + " sc:" + messageCrm.getSblcontactid());
				} catch (Exception e) {
					SkyLogger.getClientLogger().error("PushToUserClient " + login + " exception:" + e.getMessage(), e);
					throw e;
				}
			}else if (message.getType().equals(PushToUserMessageScm.MSG_TYPE)) {
				PushToUserMessageScm messageScm = (PushToUserMessageScm) message;

				SkyLogger.getClientLogger().info("PushToUserClient SCM_Msg OperatorStart:" + login + ": u:" + message.getUser() + " q:" + messageScm.getQuestionid() + " c:" + messageScm.getCustomerid());

				Question frage = API.getClientAPI().getQuestionAPI().load(messageScm.getQuestionid());
				SkyLogger.getClientLogger().info("PushToUserClient SCM_Msg QuestionLoaded:"+ frage.getId()+" l:" +login + ": u:" + message.getUser() + " q:" + messageScm.getQuestionid() + " c:" + messageScm.getCustomerid());

				if(Repository.hasObject(Repository.OPERATORMODEWAITLOOP)) {
					SkyLogger.getClientLogger().info("PushToUserClient SCM_Msg Operator" + login + ": u:" + message.getUser() + " q:" + messageScm.getQuestionid() + " c:" + messageScm.getCustomerid());

					WaitQueue ws = (WaitQueue) Repository.getObject(Repository.OPERATORMODEWAITLOOP);
					if (ws == null) {
						ws = new WaitQueue();
					}
					ws.setProcessQuestion(true);
					if (!Repository.hasObject(Repository.QUESTIONANSWER)) {
						SkyLogger.getClientLogger().info("PushToUserClient WaitQueueShowQuestion" + login + ": u:" + message.getUser() + " q:" + messageScm.getQuestionid() + " c:" + messageScm.getCustomerid());
						WaitQueue.showFrage(frage, ws, null);
					}
					SkyLogger.getClientLogger().info("PushToUserClient finished" + login + ": u:" + message.getUser() + " q:" + messageScm.getQuestionid() + " c:" + messageScm.getCustomerid());

				}else { //Inbox
					SkyLogger.getClientLogger().info("PushToUserClient InboxStart" + login + ": u:" + message.getUser() + " q:" + messageScm.getQuestionid() + " c:" + messageScm.getCustomerid());

					Start start = Start.getInstance();
					QuestionActions.openMailInbox(start, start, frage);
					SkyLogger.getClientLogger().info("PushToUserClient InboxFinish" + login + ": u:" + message.getUser() + " q:" + messageScm.getQuestionid() + " c:" + messageScm.getCustomerid());
				}

			} else {
				SkyLogger.getClientLogger().error("PushToUserClient Unknown MSGType: l:" + login + " exception:" + message.getType());
			}
		} else {
			SkyLogger.getClientLogger().error("PushToUserClient " + login + " not possible to open NewMail-Popup without Client u:" + message.getUser() + " t:" + message.getType());
		}
	}

	@OnError
	public void onError(Session session, Throwable thr) {
		SkyLogger.getCommonLogger().error("PushToUserServer onError:" + session.getId() + " msg:" + thr.getMessage(), thr);
	}

	public Question setMetaData(Question question, PushToUserMessageCrmPopup message) {
		question.setExtra12(message.getActivityid());
		question.setExtra3(message.getCustomerid());
		question.setExtra8(message.getSblcontactid());

		String documentid = DocIDClient.getOrGenerateDocId(question, TagMatchDefinitions.DocumentDirection.INDIVIDUALCORRESPONDENCE, TagMatchDefinitions.Channel.EMAIL, null);
		String headers = question.getHeaders();
		headers = TagMatchDefinitions.addOrReplaceXTMHeader(headers, TagMatchDefinitions.ACTIVITY_ID, message.getActivityid());
		headers = TagMatchDefinitions.addOrReplaceXTMHeader(headers, TagMatchDefinitions.CUSTOMER_ID, message.getCustomerid());
		headers = TagMatchDefinitions.addOrReplaceXTMHeader(headers, TagMatchDefinitions.X_TAGMATCH_CUSTOMER_ID, message.getCustomerid());
		headers = TagMatchDefinitions.addOrReplaceXTMHeader(headers, TagMatchDefinitions.CONTACT_ID, message.getSblcontactid());
		headers = TagMatchDefinitions.addOrReplaceXTMHeader(headers, TagMatchDefinitions.TAGMATCH_CONTRACT_NUMBER, message.getContractid());
		headers = TagMatchDefinitions.addOrReplaceXTMHeader(headers, TagMatchDefinitions.DOCUMENT_ID, documentid);
		question.setHeaders(headers);
		return question;
	}


	/*public void sendMessage(String user, String customerid, String contractid, String activityid, String sblcontactid, String received) throws IOException, EncodeException {
		PushToUserMessage ptumsg = new PushToUserMessage(user, customerid, contractid, activityid, sblcontactid, received);
		sendMessage(ptumsg);
	}*/
	public void sendMessage(final PushToUserMessage message) throws IOException, EncodeException, IllegalArgumentException {
		SkyLogger.getClientLogger().info("PushToUserClient " + login + " sendMsg:S: u:" + message.getUser() + " t:" + message.getType());
		userSession.getBasicRemote().sendObject(message);
		SkyLogger.getClientLogger().info("PushToUserClient " + login + " sendMsg:F: u:" + message.getUser() + " t:" + message.getType());
	}

	public void registerForNewMessage() {
		SkyLogger.getClientLogger().info("PushToUserClient register:" + login);
	}

	public boolean isReady() {
		return (userSession != null && userSession.isOpen());
	}

	public void logout() throws IOException {
		if (isReady()) {
			userSession.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "Logout:" + login));
		}
	}
}
