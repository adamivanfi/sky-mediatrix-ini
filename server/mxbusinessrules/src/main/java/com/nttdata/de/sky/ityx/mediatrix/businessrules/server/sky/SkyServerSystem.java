/**
 *
 */
package com.nttdata.de.sky.ityx.mediatrix.businessrules.server.sky;

import com.nttdata.de.ityx.mediatrix.routing.SkyChannelRoutingModel;
import com.nttdata.de.ityx.sharedservices.archiveMetadata.ArchiveMetaDataUtils;
import com.nttdata.de.ityx.sharedservices.utils.ShedulerUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.archive.AbstractArchiveMetaData;
import de.ityx.check.FunctionChecker;
import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.api.interfaces.businessrules.server.IServerSystem;
import de.ityx.mediatrix.api.interfaces.routing.RoutingModel;
import de.ityx.mediatrix.data.Answer;
import de.ityx.mediatrix.data.Email;
import de.ityx.mediatrix.data.Question;
import de.ityx.mediatrix.server.routing.RoutingModelRegistry;

import javax.mail.internet.MimeMessage;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author DHIFLM
 * 
 */
public class SkyServerSystem extends OutboundRule implements IServerSystem {


	public SkyServerSystem() {
		super();
		initRoutingModel();
	}

	public void OnShutDown() {

	}

	public void OnStartUp() {

	}

	/**
	 * 
	 */
	private void initRoutingModel() {
		RoutingModelRegistry routingModelRegistry = RoutingModelRegistry.getInstance();
		RoutingModel registeredModel = routingModelRegistry.getRegisteredModel(SkyChannelRoutingModel.ROUTINGID);
		if (registeredModel == null || !registeredModel.getName().equals(SkyChannelRoutingModel.CHANNEL_BASED_ROUTING)) {
			routingModelRegistry.registerModel(new SkyChannelRoutingModel());
			SkyLogger.getMediatrixLogger().debug("Registered " + SkyChannelRoutingModel.ROUTINGID + "|" + SkyChannelRoutingModel.CHANNEL_BASED_ROUTING);
		}
	}

	/*
	 * Returns a list of additional configuration parameters to be editable in the ServiceCenter.
	 */
	public List<PropertyKey> addPropertyKeys() {
		return new LinkedList<>();
	}

	public int checkFunction(Object p0, String p1, String p2, Object p3, boolean p4) {
		String logPrefix = getClass().getName()  + "#" + new Object() {		}.getClass().getEnclosingMethod().getName();
		try {
			FunctionChecker fsck = FunctionChecker.getChecker();
			String funcClass = de.ityx.base.Global.getProperty(de.ityx.base.Global.GP_BEAN_FUNCTION_CHECKER, "");
			if (fsck.getClass().equals(de.ityx.check.DefaultFunctionChecker.class) && funcClass.length() > 0) {
				SkyLogger.getBRSLogger().debug(logPrefix+"MDX:> FunctionChecker: " + funcClass);
				java.lang.reflect.Constructor<?> constructor = Class.forName(funcClass).getConstructor(de.ityx.check.DefaultFunctionChecker.class);
				fsck = (FunctionChecker) constructor.newInstance(fsck);
				FunctionChecker.setChecker("_", fsck);
			}
		} catch (Exception e) {
			SkyLogger.getBRSLogger().error(logPrefix + "MDX:> FunctionChecker: " + e.getMessage(), e);
			//de.ityx.mediatrix.modules.tools.logger.Log.exception(e);
		}

		de.ityx.check.SyncFactory synchronizer = de.ityx.check.SyncFactory.getSync();
		if (synchronizer instanceof de.ityx.check.DefaultSyncFactory) {
			try {
				String synchronizerName = de.ityx.base.Global.getProperty(de.ityx.base.Global.GP_BEAN_SYNC_FACTORY, "");
				de.ityx.check.SyncFactory syncer = de.ityx.check.SyncFactory.getSync();
				if (synchronizerName.length() > 0 && !syncer.getClass().getName().equals(synchronizerName)) {
					de.ityx.check.SyncFactory syncFactory = (de.ityx.check.SyncFactory) java.beans.Beans.instantiate(getClass().getClassLoader(), synchronizerName);
					de.ityx.check.SyncFactory.setSyncFactory("_", syncFactory);
				}
			} catch (Exception e) {
				SkyLogger.getBRSLogger().error(logPrefix + "MDX:> synchronizer: " + e.getMessage(), e);
				//de.ityx.mediatrix.modules.tools.logger.Log.exception(e);
			}
		}

		de.ityx.check.ITransactionHandler transactionHandler = de.ityx.check.TransactionHandlerFactory.getTransactionHandler();
		if (transactionHandler instanceof de.ityx.check.DefaultTransactionHandler) {
			try {
				String taHandlerName = de.ityx.base.Global.getProperty(de.ityx.base.Global.GP_BEAN_TRANSACTION_HANDLER, "");
				de.ityx.check.ITransactionHandler ta = de.ityx.check.TransactionHandlerFactory.getTransactionHandler();
				if (taHandlerName.length() > 0 && !ta.getClass().getName().equals(taHandlerName)) {
					de.ityx.check.TransactionHandlerFactory.setTransactionHandler("_", taHandlerName);
				}
			} catch (Exception e) {
				SkyLogger.getBRSLogger().error(logPrefix + "MDX:> transactionHandler: " + e.getMessage(),e);
			//	de.ityx.mediatrix.modules.tools.logger.Log.exception(e);
			//	e.printStackTrace();
			}
		}
		return 0;
	}

	public byte[] getEmailRfc822(java.sql.Connection con, Email email) {
		String logPrefix = getClass().getName() + "#" + new Object() {}.getClass().getEnclosingMethod().getName() + " m.id:" + email.getEmailId() + " t:" + email.getType() + " ";
		SkyLogger.getMediatrixLogger().debug(logPrefix + ": enter");
		return new byte[0];
	}

	public Map postExecuteInquiry(java.sql.Connection p0, Object p1, Object p2, Map p3) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + " ";
		// SkyLogger.getMediatrixLogger().debug(logPrefix + ": enter");
		return p3;
	}

	public void postSendEmail(java.sql.Connection con, MimeMessage p1, Email email) {

		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + " m.id:" + email.getEmailId() + " t:" + email.getType() + " ";
		final long startTime = System.currentTimeMillis();

		SkyLogger.getMediatrixLogger().debug(logPrefix + " enter");
		if (email.getType() != Email.TYPE_LETTER && email.getType() != Email.TYPE_FAX) {
			SkyLogger.getMediatrixLogger().debug(logPrefix + "!TYPE_LETTER");
			try {
				Question question = null;
				if (Question.class.isAssignableFrom(email.getClass())) {
					SkyLogger.getMediatrixLogger().debug(logPrefix + " notLetter ArchiveQuestionWithAnswers");
					question = (Question) email;
					AbstractArchiveMetaData archiveMetaData = ArchiveMetaDataUtils.getArchiveMetaData(con, question);
					ShedulerUtils.checkRuntimeLicense("complete qs:" + question.getId());
					Map<String, String> metaMap = archiveMetaData.collectMetadata(con, question);
					SkyLogger.getMediatrixLogger().info("ServerSystem: Funktion postSendEmail/QMail/datacollect " + (System.currentTimeMillis() - startTime) + " " + email.getEmailId());

					associateEmailWithSiebel(con, email, question, null, archiveMetaData, metaMap);
					SkyLogger.getMediatrixLogger().info("ServerSystem: Funktion postSendEmail/QMail/associated " + (System.currentTimeMillis() - startTime) + " " + email.getEmailId());
					// archiveQuestionWithAnswers(con, ((Question) email));
					archiveQuestionWithAnswers(con, question, archiveMetaData, metaMap);
					SkyLogger.getMediatrixLogger().info("ServerSystem: Funktion postSendEmail/QMail/archived " + (System.currentTimeMillis() - startTime) + " " + email.getEmailId());

				} else if (Answer.class.isAssignableFrom(email.getClass())) {
					SkyLogger.getMediatrixLogger().debug(logPrefix + " notLetter ArchiveAnswer");
					Answer answer = (Answer) email;
					question = API.getServerAPI().getQuestionAPI().load(con, answer.getQuestionId(), true);
					AbstractArchiveMetaData archiveMetaData = ArchiveMetaDataUtils.getArchiveMetaData(con, question);
					Map<String, String> metaMap = archiveMetaData.collectMetadata(con, question);
					SkyLogger.getMediatrixLogger().info("ServerSystem: Funktion postSendEmail/AWMail/datacollect " + (System.currentTimeMillis() - startTime) + " " + email.getEmailId());

					answer.setEmailDate(System.currentTimeMillis());
					// archiveQuestion(con, answer.getQuestionId());
					archiveQuestion(con, question, archiveMetaData, metaMap);
					SkyLogger.getMediatrixLogger().info("ServerSystem: Funktion postSendEmail/AQMail/archived " + (System.currentTimeMillis() - startTime) + " " + email.getEmailId());

					metaMap = archiveMetaData.collectMetadata(con, answer, metaMap);
					SkyLogger.getMediatrixLogger().info("ServerSystem: Funktion postSendEmail/AAMail/datacollect " + (System.currentTimeMillis() - startTime) + " " + email.getEmailId());

					associateEmailWithSiebel(con, email, question, answer, archiveMetaData, metaMap);
					SkyLogger.getMediatrixLogger().info("ServerSystem: Funktion postSendEmail/AMail/associated " + (System.currentTimeMillis() - startTime) + " " + email.getEmailId());

					// archiveAnswer(con, answer);
					archiveAnswer(con, question, answer, archiveMetaData, metaMap);
					SkyLogger.getMediatrixLogger().info("ServerSystem: Funktion postSendEmail/AMail/archived " + (System.currentTimeMillis() - startTime) + " " + email.getEmailId());
				} else 	if (Email.class.isAssignableFrom(email.getClass()) && email.getEmailId() == 0) {
					SkyLogger.getMediatrixLogger().info(logPrefix + ": Sending out Report." );

				} else {
					SkyLogger.getMediatrixLogger().error(logPrefix + ": Email can not be mapped: " + email.getEmailId() + " unknown emailClass:" + email.getClass());
					throw new Exception(logPrefix + ": Email can not be mapped: " + email.getEmailId() + " unknown emailClass:" + email.getClass());
				}
			} catch (Exception ex) {
				if (ex.getMessage() != null) {
					SkyLogger.getMediatrixLogger().error(ex.getMessage(), ex);
				} else {
					SkyLogger.getMediatrixLogger().error("Undefined Exception at ServerSystem.postSendMail");
					if (SkyLogger.getMediatrixLogger().isDebugEnabled()) {
						String stacktrace = "";
						for (StackTraceElement e : Thread.currentThread().getStackTrace()) {
							stacktrace += " => " + e.getClassName() + ">" + e.getMethodName() + ":" + e.getLineNumber() + "\r\n";
						}
						SkyLogger.getMediatrixLogger().debug("Undefined Exception at ServerSystem.postSendMail: stacktrace:" + stacktrace);
					}
				}
			}
		} else {
			SkyLogger.getMediatrixLogger().debug(logPrefix + "TYPE_LETTER");
			try {
				// printing in ServerMultiChannel.deliverToChannel
				// executePrintserviceTransferByEmail(con,email);
				if (Question.class.isAssignableFrom(email.getClass())) {
					SkyLogger.getMediatrixLogger().info(logPrefix + " Letter ArchiveQuestion e:"+email.getEmailId());
					// archived over Lettershop
					// archiveQuestion(con, ((Question) email));
				} else if (Answer.class.isAssignableFrom(email.getClass())) {
					SkyLogger.getMediatrixLogger().info(logPrefix + " Letter ArchiveQuestionUsingAnswerQID e:"+email.getEmailId());
					Answer answer = (Answer) email;
					// Workaround für Ausdruck-Datum
					answer.setEmailDate(System.currentTimeMillis());
					archiveQuestion(con, ((Answer) email).getQuestionId());
				} else {
					SkyLogger.getMediatrixLogger().error(logPrefix + " Letter Unknown emailClass:" + email.getClass() +" for emailid:"+email.getEmailId());
				}
				SkyLogger.getMediatrixLogger().info("ServerSystem: Funktion postSendEmail/letter took " + (System.currentTimeMillis() - startTime) + " " + email.getEmailId());

			} catch (Exception e) {
				SkyLogger.getMediatrixLogger().error(logPrefix + ": Can not complete answer for emailid:" + email.getEmailId() + " with cause:" + e.getMessage(), e);
			}

			/*
			 * Answer answer = null; int emailId = email.getEmailId();
			 * 
			 * SkyLogger.getMediatrixLogger().debug(logPrefix + ": Try completing answer with emailid: " + emailId); try { answer = API.getServerAPI().getAnswerAPI().loadByEmailId(con, emailId, false);
			 * SkyLogger.getMediatrixLogger().debug("answer: " + answer.getEmailId());
			 * 
			 * 
			 * 
			 * if (answer != null) { answer.setStatus(Answer.S_COMPLETE); API.getServerAPI().getAnswerAPI().store(con, answer); SkyLogger.getMediatrixLogger().debug(logPrefix + ": Answer " + answer.getId() + " stored with status: " +
			 * answer.getStatus()); } else { SkyLogger.getMediatrixLogger().debug(logPrefix + ": Answer is NULL for emailid: " + emailId); } } catch (Exception e) { SkyLogger.getMediatrixLogger().debug( logPrefix +
			 * ": Can not complete answer for emailid:" + emailId + " with cause:" + e.getMessage()); }
			 */
		}
		SkyLogger.getMediatrixLogger().info("ServerSystem: Funktion postSendEmail took " + (System.currentTimeMillis() - startTime) + " " + email.getEmailId());
		}

	public void postSendEmailExceptionCheck(java.sql.Connection p0, javax.mail.MessagingException p1, MimeMessage p2, Email p3) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + " ";
		SkyLogger.getMediatrixLogger().debug(logPrefix + ": enter");
	}

	public Map preExecuteInquiry(java.sql.Connection p0, String p1, int p2, Object p3, Object p4, Object p5, Map p6) {
		return p6;
	}

	public boolean preSendEmail(java.sql.Connection con, MimeMessage p1, Email email) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + " ";
		SkyLogger.getMediatrixLogger().debug(logPrefix + ": preSendEmail:"+email.getEmailId()+" t:"+email.getType()+" to:"+email.getTo()+" subj:"+ email.getSubject());
		return true;
	}

	public Map<java.lang.Object, java.lang.Object> updateSingleMode(java.sql.Connection con, List<de.ityx.mediatrix.data.SingleMode> data, Map<java.lang.String, java.lang.Object> hm) {
		return new LinkedHashMap<>();
	}
}
