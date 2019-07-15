package com.nttdata.de.sky.ityx.mediatrix.businessrules.archiving;

import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.Actions;
import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.data.Question;

import java.util.ArrayList;
import java.util.List;

/**
 * Sends archiving events to the server.
 *
 * @author DHIFLM
 */
public class ArchiveTool {
	
	private static List<Object> callArchiveEvent(Integer projectId, Integer questionId, Actions action) {
		assert action == Actions.ACTION_ARCHIVE_NEEDED || action == Actions.ACTION_ARCHIVE_POSSIBLE || action == Actions.ACTION_ARCHIVE_WRITE;
		List<Object> parameter = new ArrayList<Object>();
		parameter.add(projectId>0? projectId : 110);
		parameter.add(questionId);
		return (List<Object>) API.getClientAPI().getConnectionAPI().sendServerEvent(action.toString(), parameter);
	}

	private static List<Object> callArchiveCheckNeededEvent(Question question) {
		return ArchiveTool.callArchiveEvent(question.getProjectId(), question.getId(), Actions.ACTION_ARCHIVE_NEEDED);
	}

	private static List<Object> callArchiveCheckPossibleEvent(Question question) {
		return ArchiveTool.callArchiveEvent(question.getProjectId(), question.getId(), Actions.ACTION_ARCHIVE_POSSIBLE);
	}

	private static List<Object> callArchiveWriteEvent(Question question) {
		return ArchiveTool.callArchiveEvent(question.getProjectId(), question.getId(), Actions.ACTION_ARCHIVE_WRITE);
	}

	private static List<Object> callQuestionArchiveWriteEvent(Question question) {
		return ArchiveTool.callArchiveEvent(question.getProjectId(), question.getId(), Actions.ACTION_QUESTIONARCHIVE_WRITE);
	}

	/**
	 * Checks if the question contains all necessary archiving information.
	 *
	 * @param question Contains the information.
	 * @return Information is complete?
	 */
	public static boolean checkIsArchivingNeeded(Question question) {
		String logPrefix = ArchiveTool.class.getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ": q.id:" + question.getId() + " ";
		boolean archivingNeeded = false;
		SkyLogger.getClientLogger().debug(logPrefix + ": CHECK IF ARCHIV");

		List<Object> result = callArchiveCheckNeededEvent(question);

		if (result==null || result.isEmpty()) {
			SkyLogger.getClientLogger().error(logPrefix + ": cannot check if archiving is needed. Msg:" + result);
		}else {
			Object o1 = result.get(0);
			if (o1.getClass().equals(Boolean.class)) {
				archivingNeeded = (Boolean) o1;
				if (!archivingNeeded && result.size() > 1) {
					SkyLogger.getClientLogger().error(logPrefix + ": Server-Exception=" + result.get(1));
				}
				SkyLogger.getClientLogger().debug(logPrefix + ": check=" + archivingNeeded);
			}
			SkyLogger.getClientLogger().debug(logPrefix + ": exit");
		}return archivingNeeded;
	}

	/**
	 * Checks if the question contains all necessary archiving information.
	 *
	 * @param question Contains the information.
	 * @return Information is complete?
	 */
	public static boolean checkIsArchivingPossible(Question question) {
		String logPrefix = ArchiveTool.class.getName() + "#" + new Object() {}.getClass().getEnclosingMethod().getName() + ": q.id:" + question.getId() + " ";
		boolean archivingPossible = false;
		SkyLogger.getClientLogger().debug(logPrefix + ": check if Archiving possible");
		List<Object> result = callArchiveCheckPossibleEvent(question);
		Object o1 = result.get(0);
		if (o1.getClass().equals(Boolean.class)) {
			archivingPossible = (Boolean) o1;
			if (!archivingPossible && result.size() > 1) {
				SkyLogger.getClientLogger().error(logPrefix + ": Server-Exception=" + result.get(1));
			}
			SkyLogger.getClientLogger().debug(logPrefix + ": check=" + archivingPossible);
		}
		SkyLogger.getClientLogger().debug(logPrefix + ": exit");
		return archivingPossible;
	}

	/**
	 * Writes the case withAnswers to the archive.
	 *
	 * @param question Specifies the case to archive.
	 */
	public static void archiveCaseWithAnswers(Question question) {
		String logPrefix = ArchiveTool.class.getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ": q.id:" + question.getId() + " ";
		SkyLogger.getClientLogger().info(logPrefix + ": WRITE CaseWithAnswers TO ARCHIV qid:" + question.getId());
		List<Object> result = callArchiveWriteEvent(question);
		Object o1 = result.get(0);
		if (o1.getClass().equals(Boolean.class)) {
			Boolean b = (Boolean) o1;
			if (!b && result.size() > 1) {
				SkyLogger.getClientLogger().error(logPrefix + " qid:" + question.getId() + ": Server-Exception=" + result.get(1));
			}
			SkyLogger.getClientLogger().debug(logPrefix + ": write=" + b);
		}
	}


	public static void archiveCase(Question question) {
		String logPrefix = ArchiveTool.class.getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ": q.id:" + question.getId() + " ";
		SkyLogger.getClientLogger().info(logPrefix + ": WRITE Case TO ARCHIV qid:" + question.getId());
		List<Object> result = callQuestionArchiveWriteEvent(question);
		Object o1 = result.get(0);
		if (o1.getClass().equals(Boolean.class)) {
			Boolean b = (Boolean) o1;
			if (!b && result.size() > 1) {
				SkyLogger.getClientLogger().error(logPrefix + " qid:" + question.getId() + ": Server-Exception=" + result.get(1));
			}
			SkyLogger.getClientLogger().debug(logPrefix + ": write=" + b);
		}
	}


}
