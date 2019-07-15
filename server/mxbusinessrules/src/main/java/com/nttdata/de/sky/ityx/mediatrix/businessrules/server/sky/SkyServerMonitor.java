/**
 *
 */
package com.nttdata.de.sky.ityx.mediatrix.businessrules.server.sky;

import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.mediatrix.api.interfaces.businessrules.server.IServerMonitor;
import de.ityx.mediatrix.data.Email;
import de.ityx.mediatrix.data.Operator;
import de.ityx.mediatrix.data.Subproject;

import java.sql.Connection;
import java.util.HashMap;

/**
 * @author DHIFLM
 */
public class SkyServerMonitor extends OutboundRule implements IServerMonitor {


	@Override
	public HashMap preMonitorEnter(Connection arg0, Email arg1, Operator arg2, Subproject arg3) {
		String logPrefix = getClass().getName() + "#" + new Object() {}.getClass().getEnclosingMethod().getName();
		SkyLogger.getMediatrixLogger().info(logPrefix);
		return new HashMap();
	}
	@Override
	public HashMap preMonitorSend(Connection arg0, Email email, Operator arg2, Subproject arg3) {
		String logPrefix = getClass().getName() + "#" + new Object() {}.getClass().getEnclosingMethod().getName();
		SkyLogger.getMediatrixLogger().info(logPrefix +"ServerMonitor->preMonitorSend:START:EmailID:" + email.getEmailId());
		return new HashMap();
	}

	@Override
	public HashMap postMonitorEnter(Connection con, Email email, Operator op, Subproject arg3) {
		String logPrefix = getClass().getName() + "#" + new Object() {}.getClass().getEnclosingMethod().getName();
		SkyLogger.getMediatrixLogger().info(logPrefix + "ServerMonitor->postMonitorEnter:START:EmailID:" + email.getEmailId());

		HashMap<String, String> hm = new HashMap<>();
		hm.put("action", TagMatchDefinitions.TRUE);
		return hm;
	}

	@Override
	public HashMap postMonitorSend(Connection con, Email email, Operator arg2, Subproject arg3) {
		String logPrefix = getClass().getName() + "#" + new Object() {}.getClass().getEnclosingMethod().getName();
		SkyLogger.getMediatrixLogger().info(logPrefix +"ServerMonitor->PostmonitorSend:START:EmailID:" + email.getEmailId());
		return new HashMap();
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * this function makes archiving of outbound mail and will be call after supervise time.
	 */
	/*
	private void archiveOutboundMail(Connection con, Email email) {
		String uniqueId = null;
                Question question = null;
		String subprojectName = null;
		try {
			TreeMap<String, String> metaMap = new TreeMap<String, String>();
			Answer answer = (Answer) email;
			uniqueId = TagMatchDefinitions.extractHeader(
					answer.getHeaders(),
					TagMatchDefinitions.X_TAGMATCH_DOCUMENT_ID);
			SkyLogger.getMediatrixLogger().debug("archiveOutboundMail: " + uniqueId);
			if (uniqueId != null) {
				AbstractArchiveMetaData.putInfo(metaMap, true);
				AbstractArchiveMetaData questionArchiveMetaData = ArchiveMetaDataFactory
						.getInstance("MDocumentArchiveMetaData");
				question = API.getServerAPI().getQuestionAPI()
						.load(con, answer.getQuestionId(), false);
				metaMap.put(QUESTIONID, "" + question.getId());
				Boolean metaDataComplete = questionArchiveMetaData
						.collectMetadata(question, metaMap);
				metaMap.put(ANSWERID, "" + answer.getId());
				metaMap.put(EMAILID, "" + answer.getEmailId());
				final Subproject subproject = API.getServerAPI()
						.getSubprojectAPI()
						.load(con, question.getSubprojectId());
				if (subproject != null)
					subprojectName = subproject.getName();
				metaMap.put(TP_NAME, subprojectName);
				String headers = question.getHeaders();
				String contactId = TagMatchDefinitions
						.extractHeader(
								headers,
								TagMatchDefinitions
										.getHeaderTagName(TagMatchDefinitions.CONTACT_ID));
				if (contactId != null)
					metaMap.put(TagMatchDefinitions.CONTACT_ID, contactId);
				String contractId = TagMatchDefinitions
						.extractHeader(
								headers,
								TagMatchDefinitions
										.getHeaderTagName(TagMatchDefinitions.TAGMATCH_CONTRACT_NUMBER));
				SkyLogger.getMediatrixLogger().debug("ArchivingMail: DocId: " + uniqueId+" question: " + question.getId()+"email: " + answer.getEmailId()+" answer: " + answer.getId()+" metaDataComplete: " + metaDataComplete+" subproject: " + subproject.getName()+" contactId: " + contactId+ " contractId: " + contractId);
                                if (contractId != null)
					metaMap.put(TagMatchDefinitions.TAGMATCH_CONTRACT_NUMBER,
							contractId);

				Archive archive = questionArchiveMetaData.buildMetaDataXML(metaMap);
				String exportDir = Global.getProperty("sky.outbound", TMP_DIR);
				String exportPrefix = exportDir + File.separator + uniqueId;
				SkyLogger.getMediatrixLogger().debug("ArchivingMail: DocId: " + uniqueId+" question: " + question.getId()+"email: " + answer.getEmailId()+" answer: " + answer.getId()+" exportto:"+ exportDir+ " Using exportPrefix: " + exportPrefix);
                                metaMap.put(TagMatchDefinitions.DOCUMENT_ID, uniqueId);
				archive = questionArchiveMetaData.buildMetaDataXML(metaMap);
				archiveTIFF(email, questionArchiveMetaData, archive, metaMap,
						con);
			} else {
				SkyLogger.getMediatrixLogger().info(
						"Archiving of Answer/Email: "+ answer.getId()+ " not possible: uniqueId=null");
			}
		} catch (Exception e) {
			SkyLogger.getMediatrixLogger().error(
					"ServerMonitor#archiveOutboundMail: "
							+(uniqueId==null?" docID:unknown":uniqueId) +":"+ e.getLocalizedMessage(), e);
			e.printStackTrace();
		}
		//
	}
	*/



}
