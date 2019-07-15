package com.nttdata.de.ityx.cx.workflow.outbound.o7_archive;

import com.nttdata.de.ityx.cx.workflow.outbound.OutboundPooler;
import com.nttdata.de.ityx.sharedservices.archiveMetadata.ArchiveMetaDataUtils;
import com.nttdata.de.ityx.sharedservices.configuration.BeanConfig;
import com.nttdata.de.ityx.sharedservices.image.ArchiveUtils;
import com.nttdata.de.ityx.sharedservices.utils.NttFileUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.archive.AbstractArchiveMetaData;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.base.dbpooling.DBConnectionPoolFactory;
import de.ityx.contex.interfaces.designer.IExflowState;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.api.server.ISAnswer;
import de.ityx.mediatrix.api.server.ISQuestion;
import de.ityx.mediatrix.data.Answer;
import de.ityx.mediatrix.data.Question;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLDataException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArchivingExecutor extends OutboundPooler {

	@Override
	public String getProcessname() {
		return "811_Archiv";
	}
	@Override
	public String getMaster() {
		return "sky";
	}


	@Override
	public void itemProcessor(IFlowObject flowObject, IExflowState exflowState, Map<String, Object> docMeta, String docid, long logid) throws Exception {


		String direction = (String) docMeta.get(TagMatchDefinitions.MX_DIRECTION);
		String formtype = (String) docMeta.get(TagMatchDefinitions.FORM_TYPE_CATEGORY);
		String base_path = BeanConfig.getReqString(ArchiveUtils.ARCHIVE_BASE_DIR);
		String base_dir = base_path + File.separator + direction + File.separator;


		Connection con =null ;
		try {
			con = DBConnectionPoolFactory.getPool().getCon();
			String questionIdS = (String) docMeta.get(TagMatchDefinitions.MX_QUESTIONID);
			int questionId = (questionIdS == null || questionIdS.isEmpty()) ? 0 : Integer.parseInt(questionIdS);
			if (questionIdS==null) {
				throw new SQLDataException("Provided empty QuestionID:" + questionId + " logid:"+logid);
			}
			String answerIdS = (String) docMeta.get(TagMatchDefinitions.MX_ANSWERID);
			int answerId = (answerIdS == null || answerIdS.isEmpty()) ? 0 : Integer.parseInt(answerIdS);

			ISQuestion qapi = API.getServerAPI().getQuestionAPI();
			ISAnswer aapi = API.getServerAPI().getAnswerAPI();
			Question question = qapi.load(con, questionId, true);

			if (question==null){
				throw new SQLDataException("Cannot load Question:"+questionId+" using API");
			}
			// Create Archiving Files
			//OutboundRule or = new OutboundRule();
			AbstractArchiveMetaData metaData = ArchiveMetaDataUtils.getArchiveMetaData(formtype);
			Map<String, String> metaMap = new HashMap<>();

			for (Map.Entry<String, Object> dm:docMeta.entrySet()){
				metaMap.put(dm.getKey(),dm.getValue()+"");
			}
			metaMap = metaData.deepCollectMetadata(con, question, metaMap);
			if (metaMap == null) {
				metaMap = new HashMap<>();
				
				for (Map.Entry<String, Object> dm:docMeta.entrySet()){
					metaMap.put(dm.getKey(),dm.getValue()+"");
				}
			}
			if (metaMap==null){
				throw new SQLDataException("Cannot load Metadata For Question:"+questionId+" using API");
			}
			if (docid!=null) {
				metaMap.put(TagMatchDefinitions.DOCUMENT_ID, docid);
			}
			List<File> filesToArchive;
			if (answerId > 0) {
				Answer archiveAnswer = aapi.load(con, answerId, true);
				if (archiveAnswer==null){
					throw new SQLDataException("Cannot load Answer:"+answerId+" using API");
				}
				metaMap = metaData.collectMetadata(con, archiveAnswer, metaMap);
				if (metaMap==null){
					throw new SQLDataException("Cannot load Metadata for Answer:"+answerId+" using API");
				}
				// Create Tif-File for each Item (Attachments, Inbound etc)
				filesToArchive = ArchiveUtils.createArchivingFiles(metaMap, archiveAnswer);
				SkyLogger.getWflLogger().info("810: Archive/CreateArchFiles: " + docid + " END ANSWER creating archfiles q:" + questionId + " a:" + answerId + " ft:" + formtype);

			} else {
				// Create Tif-File for each Item
				filesToArchive = ArchiveUtils.createArchivingFiles(metaMap, question);
				SkyLogger.getWflLogger().info("810: Archive/CreateArchFiles: " + docid + " END QUESTION creating archfiles q:" + questionId + " a:" + answerId + " ft:" + formtype);

			}
			boolean isSbs=isSBS(docMeta,question);
			
			//überschreiben von DocID mit dem wert aus Tabelle - kann genutzt werden um falsch archivierte Dateien mit einem Suffix z.B. "-R" erneut nacharchivieren.
			if (docid!=null && !docid.isEmpty()) {
				metaMap.put(TagMatchDefinitions.DOCUMENT_ID, docid);
			}
			
			if (metaMap.get(TagMatchDefinitions.CUSTOMER_ID)==null && docMeta.get(TagMatchDefinitions.CUSTOMER_ID)!=null) {
				metaMap.put(TagMatchDefinitions.CUSTOMER_ID, (String) docMeta.get(TagMatchDefinitions.CUSTOMER_ID));
			}
			// Create SkyArch-Meta-File
			File metaFile = ArchiveMetaDataUtils.createArchivingMetaFile(metaMap, metaData, base_dir + "src",isSbs, filesToArchive.size());

			// Merge Tifs to one PDF
			//File bigPDF = PdfUtils.createMergedPdf(filesToArchive, docid, base_dir + "pdf" + File.separator + docid + ".pdf");

			/*String customerId=(String) docMeta.get(TagMatchDefinitions.CUSTOMER_ID);
			String contractId = (String) docMeta.get(TagMatchDefinitions.TAGMATCH_CONTRACT_NUMBER);
			String tpName = (String) docMeta.get(TagMatchDefinitions.MX_TP_NAME);
			String contactId = (String) docMeta.get(TagMatchDefinitions.CONTACT_ID);
			*/

			TransferDocumentToArchive.getInstance().transferToArchiveSrv(filesToArchive, metaFile, docid, direction, formtype, isSbs);
			String dstDir = base_dir + File.separator + "dst" + File.separator;
			//NttFileUtils.moveFileToDir(bigPDF, dstDir);
			NttFileUtils.moveFilesToDir(filesToArchive, dstDir);
			NttFileUtils.moveFileToDir(metaFile, dstDir);
			//NttFileUtils.moveFilesToDir(filesToArchive, base_dir + File.separator + "interim" + File.separator);

		} finally {
			if (con!=null) {
				DBConnectionPoolFactory.getPool().releaseCon(con);
			}
		}

	}



}
