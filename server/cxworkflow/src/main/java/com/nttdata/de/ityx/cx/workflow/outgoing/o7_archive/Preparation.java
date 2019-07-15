/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nttdata.de.ityx.cx.workflow.outgoing.o7_archive;


import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;
import com.nttdata.de.ityx.sharedservices.archiveMetadata.ArchiveMetaDataUtils;
import com.nttdata.de.ityx.sharedservices.image.ArchiveUtils;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.archive.AbstractArchiveMetaData;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.base.dbpooling.DBConnectionPoolFactory;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.document.CDocument;
import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.api.server.ISAnswer;
import de.ityx.mediatrix.api.server.ISQuestion;
import de.ityx.mediatrix.data.Answer;
import de.ityx.mediatrix.data.Question;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class Preparation extends AbstractWflBean {

	@Override
	public void execute(IFlowObject flowObject) throws Exception {
		CDocument doc = DocContainerUtils.getDoc(flowObject);
		if (doc != null) {

			String documentid = DocContainerUtils.getDocID(doc);
			flowObject.put("MoveFileToArchive_Direction", doc.getNote("Direction"));
			flowObject.put(TagMatchDefinitions.DOCUMENT_ID, documentid);

			int frageid = getINoteByName(doc, "FrageID");
			int antwortid = getINoteByName(doc, "AnswerID");

			if (frageid > 0) {
				createArchivingFiles(frageid, antwortid, documentid);
			}
		}
	}

	public void createArchivingFiles(int questionId, int answerId, String documentid) throws Exception {

		Connection con = DBConnectionPoolFactory.getPool().getCon();
		try {
			ISQuestion qapi = API.getServerAPI().getQuestionAPI();
			ISAnswer aapi = API.getServerAPI().getAnswerAPI();
			Question question = qapi.load(con, questionId, true);

			String formtype = TagMatchDefinitions.extractXTMHeader(question.getHeaders(), TagMatchDefinitions.X_TAGMATCH_FORM_TYPE);
			if (formtype == null || formtype.isEmpty()) {
				formtype = ArchiveMetaDataUtils.getFormtype(con, question);
			}
			AbstractArchiveMetaData metaData = ArchiveMetaDataUtils.getArchiveMetaData(formtype);
			Map<String, String> metaMap = new HashMap<>();
			metaMap.put(TagMatchDefinitions.DOCUMENT_ID, documentid);
			metaMap = metaData.deepCollectMetadata(con, question, metaMap);

			if (answerId > 0) {
				Answer archiveAnswer = aapi.load(con, answerId, true);
				metaMap = metaData.collectMetadata(con, archiveAnswer, metaMap);
				ArchiveUtils.createArchivingFiles(metaMap, archiveAnswer);
			} else {
				ArchiveUtils.createArchivingFiles(metaMap, question);
			}
		} finally {
			DBConnectionPoolFactory.getPool().releaseCon(con);
		}
	}

	private int getINoteByName(CDocument cdoc, String noteName) {
		String idString = null;
		try {
			idString = (String) cdoc.getNote(noteName);
			if (idString != null && !idString.isEmpty() && !idString.equals("0") && !idString.equals("SERVICE_OFF")) {
				return Integer.parseInt(idString);
			}

		} catch (Exception e) {
			SkyLogger.getItyxLogger().warn("Problem accessing Note:" + noteName + " for wfl_810_preparation:" + e.getMessage());
		}
		return 0;
	}
}
