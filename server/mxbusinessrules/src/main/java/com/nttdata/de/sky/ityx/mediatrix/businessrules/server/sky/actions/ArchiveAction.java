package com.nttdata.de.sky.ityx.mediatrix.businessrules.server.sky.actions;

import com.nttdata.de.ityx.sharedservices.archiveMetadata.ArchiveMetaDataUtils;
import com.nttdata.de.ityx.sharedservices.image.PrintService;
import com.nttdata.de.lib.exception.ExtendedMXException;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.archive.AbstractArchiveMetaData;
import com.nttdata.de.sky.ityx.common.Actions;
import com.nttdata.de.sky.ityx.mediatrix.businessrules.server.sky.OutboundRule;
import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.data.Answer;
import de.ityx.mediatrix.data.Question;

import java.sql.Connection;
import java.util.List;

/**
 * Created by meinusch on 13.04.15.
 */
public class ArchiveAction extends AServerEventAction {

	private OutboundRule or=new OutboundRule();

	@Override
	public String[] getActionNames() {
		return new String[]{Actions.ACTION_QUESTIONARCHIVE_WRITE.name(),
				Actions.ACTION_ARCHIVE_WRITE.name(),
				Actions.ACTION_ARCHIVE_NEEDED.name(),
				Actions.ACTION_ARCHIVE_POSSIBLE.name(),
				Actions.QUESTIONSEND.name(),
				Actions.ANSWERSEND.name()
		};
	}

	@Override
	public List actionPerformed(Connection con, String actionname, List parameters) throws Exception {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + " ";

		Actions t = null;
		try {
			t = Actions.valueOf(actionname);
		} catch (IllegalArgumentException e) {
			SkyLogger.getMediatrixLogger().error(logPrefix + "Problem during processing of Action:"+actionname+" notFound:"+e.getMessage(),e);
			throw new ExtendedMXException(e.getMessage(), e);
		}
		switch (t) {
	case ACTION_QUESTIONARCHIVE_WRITE:
				Integer aqaw_questionId = (Integer) parameters.get(0);
				parameters.clear();
				try {
					//String message = questionArchive(con, aaw_questionId, true);
					or.archiveQuestion(con, aqaw_questionId);
					parameters.add(true);
					parameters.add("");
				} catch (Exception e) {
					SkyLogger.getMediatrixLogger().error(logPrefix + "Problem during processing of Action:"+t.name()+" q:" + aqaw_questionId+" msg:"+e.getMessage(),e);
					parameters.add(false);
					parameters.add(e.getMessage());
				}
				break;
			case ACTION_ARCHIVE_WRITE:
				Integer aaw_questionId = (Integer) parameters.get(0);
				parameters.clear();
				try {
					//String message = questionArchive(con, aaw_questionId, true);
					or.archiveQuestionWithAnswers(con, aaw_questionId);
					parameters.add(true);
					parameters.add("");
				} catch (Exception e) {
					SkyLogger.getMediatrixLogger().error(logPrefix + "Problem during processing of Action:"+t.name()+" q:" + aaw_questionId+" msg:" +e.getMessage(),e);
					parameters.add(false);
					parameters.add(e.getMessage());
				}
				break;
			case ACTION_ARCHIVE_NEEDED:
				Integer aan_questionId = (Integer) parameters.get(0);
				parameters.clear();
				try {
					parameters.add(or.questionArchivingNeeded(con, aan_questionId));
				} catch (Exception e) {
					SkyLogger.getMediatrixLogger().error(logPrefix + "Problem during processing of Action:"+t.name()+" q:" + aan_questionId+" msg:" +e.getMessage(),e);
					parameters.clear();
					parameters.add(e.getMessage());
				}
				break;
			case ACTION_ARCHIVE_POSSIBLE:
				Integer aap_questionId = (Integer) parameters.get(0);
				parameters.clear();
				try {
					parameters.add(or.questionArchivingPossible(con, aap_questionId));
				} catch (Exception e) {
					SkyLogger.getMediatrixLogger().error(logPrefix + "Problem during processing of Action:"+t.name()+" q:" + aap_questionId+" msg:" +e.getMessage(),e);
					parameters.clear();
					parameters.add(e.getMessage());
				}
				break;
			case QUESTIONSEND:
				Integer qs_questionId = (Integer) parameters.get(0);
				parameters.clear();
				try {
					or.archiveQuestionWithAnswers(con, qs_questionId);
					parameters.add("");
				} catch (Exception e) {
					SkyLogger.getMediatrixLogger().error(logPrefix + "Problem during processing of Action:"+t.name()+" q:" + qs_questionId+" msg:" +e.getMessage(),e);
					parameters.add(e.getMessage());
				}
				break;
			case ANSWERSEND:
				int answerId = (Integer) parameters.get(0);
				try {
					SkyLogger.getMediatrixLogger().warn(" UNKNOWN CALLER ");

					Answer answer = API.getServerAPI().getAnswerAPI().load(con, answerId, true);
					Question question = API.getServerAPI().getQuestionAPI().load(con, answer.getQuestionId(), true);
					AbstractArchiveMetaData archiveMetaData = ArchiveMetaDataUtils.getArchiveMetaData(con, question);
					PrintService.executePrintserviceTransfer(con, question, answer,archiveMetaData, true);
					or.archiveQuestion(con, question);
					parameters.clear();
					parameters.add("");
				} catch (Exception e) {
					SkyLogger.getMediatrixLogger().error(logPrefix + "Problem during processing of Action:"+t.name()+" a:" + answerId+" msg:" +e.getMessage(),e);
					parameters.clear();
					parameters.add(e.getMessage());
				}
				break;
		}

		return parameters;
	}

}
