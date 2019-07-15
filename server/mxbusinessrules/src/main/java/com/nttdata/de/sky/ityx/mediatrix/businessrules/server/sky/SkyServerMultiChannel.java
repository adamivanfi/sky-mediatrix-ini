package com.nttdata.de.sky.ityx.mediatrix.businessrules.server.sky;

import com.nttdata.de.ityx.sharedservices.archiveMetadata.ArchiveMetaDataUtils;
import com.nttdata.de.ityx.sharedservices.image.PrintService;
import com.nttdata.de.ityx.sharedservices.utils.ShedulerUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.archive.AbstractArchiveMetaData;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.licensing.exception.LicensingException;
import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.api.interfaces.businessrules.server.IServerMultiChannel;
import de.ityx.mediatrix.data.*;
import de.ityx.mediatrix.modules.tools.logger.Log;
import de.ityx.sky.outbound.common.ServerUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

public class SkyServerMultiChannel extends OutboundRule implements IServerMultiChannel {

	public SkyServerMultiChannel() {
		SkyLogger.getMediatrixLogger().info("Initialization of NTTServerMultiChannel");
	}

	@Override
	public HashMap deliverToChannel(Connection con, Email email) throws SQLException {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName();
		SkyLogger.getMediatrixLogger().info(logPrefix + " MultichannelDelivery start, email:" + email.getEmailId());
		HashMap<String, String> result = new HashMap<>();
		result.put(IServerMultiChannel.PARAM_RESULT, TagMatchDefinitions.TRUE);
		
		if (email.getType() == Email.TYPE_LETTER || email.getType() == Email.TYPE_FAX) {
			if (email.getType() == Email.TYPE_FAX) {
				email.setType(Email.TYPE_LETTER);
			}
			try {
				if (email.getClass().equals(Answer.class)) {
					Answer answer = (Answer) email;
					Question question = API.getServerAPI().getQuestionAPI().load(con, answer.getQuestionId(), true);
					SkyLogger.getMediatrixLogger().info(logPrefix + " MultichannelDelivery exec Question:" + question.getId() + " Answer:" + answer.getId());
					AbstractArchiveMetaData questionArchiveMetaData = ArchiveMetaDataUtils.getArchiveMetaData(con, question);

					PrintService.executePrintserviceTransfer(con, question, answer, questionArchiveMetaData, true);
					// Archivierung in ServerSystem.postSendEmail
					// archiveQuestion(con, question);
					SkyLogger.getMediatrixLogger().info(logPrefix + " MultichannelDelivery done Question:" + question.getId() + " Answer:" + answer.getId());
				} else if (email.getClass().equals(Question.class)) {
					SkyLogger.getMediatrixLogger().info(logPrefix + " MultichannelDelivery exec Question (IK):" + ((Question) email).getId());
					AbstractArchiveMetaData questionArchiveMetaData = ArchiveMetaDataUtils.getArchiveMetaData(con, (Question) email);

					PrintService.executePrintserviceTransfer(con, ((Question) email), null, questionArchiveMetaData, true);
					// Archivierung in ServerSystem.postSendEmail
					//Question Archived by Lettershop
					// archiveQuestion(con, question);
					SkyLogger.getMediatrixLogger().info(logPrefix + " MultichannelDelivery done Question (IK):" + ((Question) email).getId());
				}

				try {
					ServerUtils.complete(con, email, true);
					result.put(IServerMultiChannel.PARAM_RESULT, TagMatchDefinitions.TRUE);
				} catch (LicensingException | SQLException e) {
					ShedulerUtils.resetAuth(" SSMC:"+email.getEmailId());
					ServerUtils.complete(con, email, true);
					result.put(IServerMultiChannel.PARAM_RESULT, TagMatchDefinitions.TRUE);
				}
				SkyLogger.getMediatrixLogger().info(logPrefix + " MultichannelDelivery finished");
			} catch (Exception e) {
				SkyLogger.getMediatrixLogger().error(logPrefix + " MultichannelDelivery FAILED Sending monitored answer " + email.getEmailId() + " failed with message: " + e.getMessage(), e);
				try {
					ServerUtils.requeue(con, email);
				} catch (SQLException e1) {
					Log.exception(e1);
				}
				Log.exception(e);
			}
		}
		return result;
	}

	@Override
	public void assignLanguage(Connection arg0, Email arg1) throws SQLException {
		//String logPrefix =  getClass().getName() + "#" + new Object() {}.getClass().getEnclosingMethod().getName();
		//SkyLogger.getMediatrixLogger().info(logPrefix);
	}

	@Override
	public boolean isValidateReceiver(Connection connection, String s) {
		return true;
	}


	@Override
	public Customer findCustomerForChannel(Connection arg0, Customer arg1, Email arg2, Project arg3) throws SQLException {
		//	String logPrefix =  getClass().getName() + "#" + new Object() {}.getClass().getEnclosingMethod().getName();
		//	SkyLogger.getMediatrixLogger().info(logPrefix);
		return null;
	}

	@Override
	public HashMap prepareForFilter(Connection arg0, Email arg1, Account arg2, Project arg3) throws SQLException {
		//	String logPrefix =  getClass().getName() + "#" + new Object() {}.getClass().getEnclosingMethod().getName();
		//	SkyLogger.getMediatrixLogger().info(logPrefix);
		return new HashMap();
	}

}
