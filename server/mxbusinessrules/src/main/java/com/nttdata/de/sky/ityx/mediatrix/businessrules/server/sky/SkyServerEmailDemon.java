package com.nttdata.de.sky.ityx.mediatrix.businessrules.server.sky;

import com.nttdata.de.ityx.sharedservices.archiveMetadata.ArchiveMetaDataUtils;
import com.nttdata.de.ityx.sharedservices.utils.ShedulerUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.lib.utils.MitarbeiterlogWriter;
import com.nttdata.de.sky.archive.AbstractArchiveMetaData;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import com.nttdata.de.sky.ityx.mediatrix.businessrules.server.sky.actions.QuickSRAction;
import de.ityx.licensing.exception.LicensingException;
import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.api.interfaces.IEmail;
import de.ityx.mediatrix.api.interfaces.businessrules.server.IServerEmailDemon;
import de.ityx.mediatrix.data.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import static com.nttdata.de.sky.ityx.common.TagMatchDefinitions.SEPA_MANDATE;
import static com.nttdata.de.sky.ityx.mediatrix.businessrules.server.sky.SkyServerObjectsControl.SEPA_MANDAT_AUTOMAT_VERARBEITUNG;

public class SkyServerEmailDemon extends InboundRule implements IServerEmailDemon {

	public static final int SBS_FORWARD_SUBPROJECT = Integer.parseInt(System.getProperty("sbs_forward.subproject", "58102"));
	public static final int L1_RET_SERV_ORDENTLICHE_KUENDIGUNG = Integer.parseInt(System.getProperty("sky_ordentliche_kuendigung.subproject", "941"));
	
	@Override
	public Vector foundFilter(Connection con, Filter filter, Question question) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":  q.id:" + question.getId() + "; q.status:" + question.getStatus() + "; f.id:" + filter.getId() + " ";
		
		boolean autoarchive = false;
		boolean monitoredAutoarchive = false;
		boolean questionStore = false;
		boolean exceptionBreakingAutoProcessing=false;
		boolean exceptionCancellation=false;


		if (question != null ){
			SkyLogger.getMediatrixLogger().debug(logPrefix +
					"; q.prjId: "+question.getProjectId()+
					"; q.subprjId: "+question.getSubprojectId());
		}
		if (filter != null){
			SkyLogger.getMediatrixLogger().debug(logPrefix +
					"; f.subprjId: " +filter.getSubprojectId()+
					"; f.title: "+( filter.getTitle()!=null ? filter.getTitle().toString(): "null" ));
		}

		if (question.getProjectId() == SBS_PROJECT_ID || filter.getProjectId() == SBS_PROJECT_ID) {
			//Aufruf von SkyServerObjectsControl ist zwecklos
			SkyLogger.getMediatrixLogger().debug(logPrefix + " SBS-Check start:");
			question = correctionOfSBSMail(con, question);
			questionStore = true;
			SkyLogger.getMediatrixLogger().debug(logPrefix + " SBS-Check finished");
		} else {
			SkyLogger.getMediatrixLogger().debug(logPrefix + " SBS-Check not needed, projektid:" + question.getProjectId());
		}
		
		if (filter.getStatus().equals(Question.S_COMPLETED)) {
			String customerid = TagMatchDefinitions.extractXTMHeader(question.getHeaders(), TagMatchDefinitions.CUSTOMER_ID);
			if (customerid == null || customerid.isEmpty() || customerid.trim().equals("0")) {
				SkyLogger.getMediatrixLogger().debug(logPrefix + ": skiping autoclose because of empty customerid:q:" + question.getId());
			} else {
				filter.setStatus(Question.S_NEW);
				autoarchive = true;
			}
		}
		
		String formtype = TagMatchDefinitions.extractXTMHeader(question.getHeaders(), TagMatchDefinitions.X_TAGMATCH_FORM_TYPE);
		String filtername = isEmpty(filter.getName()) ? "unnamedFilter" : filter.getName();
		SkyLogger.getMediatrixLogger().debug(logPrefix + " Start: ServerEmailDemon.foundFilter formtype:" + formtype + " q.extra3:" + question.getExtra3());
		
		if (filtername.contains("[FORWARD_TO_SBS]") && formtype != null && formtype.toLowerCase().contains("sbs_")) {
			SkyLogger.getMediatrixLogger().debug(logPrefix + "ForwardToSBS: checkingformtype:" + formtype + " checkingFiltername:" + filter.getName());
			try {
                API.getServerAPI().getQuestionAPI().forward(con, question, SBS_FORWARD_SUBPROJECT);
				question.setSubprojectId(SBS_FORWARD_SUBPROJECT);
			} catch (SQLException e) {
				SkyLogger.getMediatrixLogger().error(e.getMessage(),e);
				exceptionBreakingAutoProcessing=true;
			}
		}
		if (exceptionBreakingAutoProcessing){
				question.setSubprojectId(SBS_FORWARD_SUBPROJECT);
				SkyLogger.getMediatrixLogger().debug(logPrefix + "Forward SBSTeilProject:  " + question.getId());
				//API.getServerAPI().getQuestionAPI().forward(con, question, SBS_FORWARD_SUBPROJECT);
		}

		if (filtername.contains("[SBLSR_QA_CANCELLATION]") && formtype != null && formtype.toLowerCase().contains("kuendigung")) {
			try {
				SkyLogger.getMediatrixLogger().debug(logPrefix + "Kuendigung: checkingformtype:" + formtype + " checkingFiltername:" + filter.getName());
				String customerid = TagMatchDefinitions.extractXTMHeader(question.getHeaders(), TagMatchDefinitions.CUSTOMER_ID);
				if (customerid == null || customerid.isEmpty() || customerid.trim().equals("0")) {
					SkyLogger.getMediatrixLogger().debug(logPrefix + ": skiping cancellation because of empty customerid:q:" + question.getId() + " con:" + con.isClosed());
					autoarchive = false;
				} else {
					boolean check = new QuickSRAction().triggerCancellationProcess(con, question, false, null);
					if (check) {
						autoarchive = true;
						monitoredAutoarchive = true;
						SkyLogger.getMediatrixLogger().debug(logPrefix + "Kuendigung: cancellation ok");
					} else {
						SkyLogger.getMediatrixLogger().debug(logPrefix + "Kuendigung: cancellation nok");
					}
					SkyLogger.getMediatrixLogger().debug(logPrefix + "Kuendigung: Processing finished:" + check);
				}
			} catch (Exception ex) {
				MitarbeiterlogWriter.writeMitarbeiterlog(0, question.getId(), 0, 19, "Automatically processed cancellation" + ":BusinessCheck-Error:" + ex.getMessage(), System.currentTimeMillis(), true);
				SkyLogger.getMediatrixLogger().error(logPrefix + "Exception at ServerEmailDemon.foundFilter:msg:" + ex.getMessage(), ex);
				exceptionBreakingAutoProcessing=true;
				exceptionCancellation=true;
			}
		}

		//Change due Ticket #273718
		//The Question cannot be closed, Status need to be New and Subproject need to be moved forward into Backup L1_RET_SERV_ORDENTLICHE_KUENDIGUNG
		//By RothJ
		if (exceptionCancellation){
			question.setSubprojectId(L1_RET_SERV_ORDENTLICHE_KUENDIGUNG);
			MitarbeiterlogWriter.writeMitarbeiterlog(0, question.getId(), 0, 19, "Forward Frage" + question.getId() + " to TeilProjekt: " + L1_RET_SERV_ORDENTLICHE_KUENDIGUNG, System.currentTimeMillis(), true);
			SkyLogger.getMediatrixLogger().debug(logPrefix + "Forward TeilProject: cancellation nok " + question.getId() + " to TeilProjekt: " + L1_RET_SERV_ORDENTLICHE_KUENDIGUNG);
			filter.setSubprojectId(L1_RET_SERV_ORDENTLICHE_KUENDIGUNG);
		}

		String archiveFlag = TagMatchDefinitions.extractXTMHeader(question.getHeaders(), TagMatchDefinitions.ARCHIVE_FLAG);
		if (!exceptionCancellation && (autoarchive || (filter.getName() != null && filter.getName().contains("[AUTO_CLOSE]")) || (archiveFlag!=null && archiveFlag.equalsIgnoreCase("true")))) {
			String customerid = TagMatchDefinitions.extractXTMHeader(question.getHeaders(), TagMatchDefinitions.CUSTOMER_ID);
			if (customerid == null || customerid.isEmpty() || customerid.trim().equals("0")) {
				SkyLogger.getMediatrixLogger().debug(logPrefix + ": skiping autoclose because of empty customerid:q:" + question.getId() );
				autoarchive = false;
				question.setHeaders(TagMatchDefinitions.addOrReplaceXTMHeader(question.getHeaders(), TagMatchDefinitions.ARCHIVE_FLAG, "false"));
				questionStore = true;
				
			} else if (archiveFlag == null || !archiveFlag.equalsIgnoreCase("true")) {
				SkyLogger.getMediatrixLogger().debug(logPrefix + "ServerEmailDemon.foundFilter mark Question for autoclosing");
				if (monitoredAutoarchive) {
					//autoarchive initiated from trigger
					question.setHeaders(TagMatchDefinitions.addOrReplaceXTMHeader(question.getHeaders(), TagMatchDefinitions.ARCHIVE_FLAG, Question.S_MONITORED));
				} else {
					question.setHeaders(TagMatchDefinitions.addOrReplaceXTMHeader(question.getHeaders(), TagMatchDefinitions.ARCHIVE_FLAG, "true"));
				}
				questionStore = true;
			} else {
				autoarchive=true;
				SkyLogger.getMediatrixLogger().debug(logPrefix + "ServerEmailDemon.foundFilter Question already marked for autoclosing");
			}
			
			if (!exceptionBreakingAutoProcessing && autoarchive && !monitoredAutoarchive){
				try {
					AbstractArchiveMetaData archiveMetaData = ArchiveMetaDataUtils.getArchiveMetaData(con, question);
					Map<String, String> metaMap = archiveMetaData.collectMetadata(con, question);
					executeArchiving(con, metaMap, question, archiveMetaData, monitoredAutoarchive?Question.S_MONITORED:SkyRule.AUTOARCHIVE_INITIALIZED);
				}catch (Exception e){
					SkyLogger.getMediatrixLogger().error(logPrefix + "ServerEmailDemon.foundFilter Problem within Autoarchiving: "+e.getMessage(),e);
				}
			}
		}
        SkyLogger.getMediatrixLogger().debug(logPrefix + "; q.LockedBy: " +question.getLockedBy()+"; q.LockedAt: "+question.getLockedAt());
		if (question.getLockedBy() ==2 &&
				question.getSubject() != null && question.getSubject().contains(SEPA_MANDAT_AUTOMAT_VERARBEITUNG) &&
				formtype!=null && formtype.equalsIgnoreCase(SEPA_MANDATE)) {
            updateFilterMitarbeiterLog(con, question);
        }
		if (questionStore) {
			try {
				ShedulerUtils.checkAuth();
				SkyLogger.getCommonLogger().debug("SSED.QStore1 Generated docid:" + question.getDocId() + " frage:" + question.getId()+" status:"+question.getStatus());
				String statusbak=question.getStatus();
				question.setStatus(Question.S_BLOCKED);
				SkyLogger.getCommonLogger().debug("SSED.QStore1 Generated docid:" + question.getDocId() + "Before frage:" + question.getId()+ " Project:" + question.getProjectId() + " Teilprojekt:"+question.getSubprojectId());
				boolean questionstoreok = API.getServerAPI().getQuestionAPI().store(con, question);
				SkyLogger.getCommonLogger().debug("SSED.QStore1 Generated docid:" + question.getDocId() + "After frage:" + question.getId()+ " Project:" + question.getProjectId() + " Teilprojekt:"+question.getSubprojectId());
				question.setStatus(statusbak);
				SkyLogger.getCommonLogger().debug("SSED.QStore2 Generated docid:" + question.getDocId() + " frage:" + question.getId()+" sok:"+questionstoreok);
				
				SkyLogger.getMediatrixLogger().debug(logPrefix + "ServerEmailDemon.foundFilter Question marked for autoclosing");
			} catch (SQLException | LicensingException e) {
				ShedulerUtils.resetAuth(logPrefix);
				try {
					SkyLogger.getCommonLogger().debug("SSED.QStore1b Generated docid:" + question.getDocId() + " frage:" + question.getId()+" status:"+question.getStatus());
					String statusbak=question.getStatus();
					question.setStatus(Question.S_BLOCKED);
					boolean questionstoreok = API.getServerAPI().getQuestionAPI().store(con, question);
					question.setStatus(statusbak);
					SkyLogger.getCommonLogger().debug("SSED.QStore2b Generated docid:" + question.getDocId() + " frage:" + question.getId()+" sok:"+questionstoreok);
					SkyLogger.getMediatrixLogger().debug(logPrefix + "ServerEmailDemon.foundFilter Question marked for autoclosing");
				} catch (SQLException | LicensingException ee) {
					SkyLogger.getMediatrixLogger().warn(logPrefix + " ServerEmailDemon.Setting Archive-Flaq not possible: msg:" + ee.getMessage());
				}
			}
		}
		SkyLogger.getMediatrixLogger().debug(logPrefix + " Finish: ServerEmailDemon.foundFilter formtype:" + formtype + " q.extra3:" + question.getExtra3());
		
		return new Vector();
	}
	
	
	@Override
	public HashMap postImportExternalClassificator(Connection con, Project arg1, List<Question> list, Map<String, Object> arg3) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName();
		
		for (Question question : list) {
			try {
				question.setProjectId(arg1.getId());
				try {
					SkyLogger.getMediatrixLogger().debug(logPrefix + ": start initialisation: q:" + question.getId() + " docid:" + question.getDocId());
					initializeQuestion(con, question, true);
					SkyLogger.getMediatrixLogger().debug(logPrefix + ": interim initialisation: q:" + question.getId() + " docid:" + question.getDocId());
					//logValidationResult(con, question); This call exists in initializeQuestion
					question.setSmtpDate(System.currentTimeMillis());
				} catch (Exception e) {
					SkyLogger.getMediatrixLogger().error(logPrefix + " error initialisation: q:" + question.getId() + " " + e.getMessage(), e);
				}
				
				String archiveFlag = TagMatchDefinitions.extractXTMHeader(question.getHeaders(), TagMatchDefinitions.ARCHIVE_FLAG);
				SkyLogger.getMediatrixLogger().debug(logPrefix + ": checking archive: q:" + question.getId() + " af:" + archiveFlag + " docid:" + question.getDocId());
				
				if (archiveFlag != null && (archiveFlag.equalsIgnoreCase("true") || archiveFlag.equalsIgnoreCase(Question.S_MONITORED)|| archiveFlag.equalsIgnoreCase(SkyRule.AUTOARCHIVE_INITIALIZED))) {
					String customerid = TagMatchDefinitions.extractXTMHeader(question.getHeaders(), TagMatchDefinitions.CUSTOMER_ID);
					if (customerid == null || customerid.isEmpty() || customerid.trim().equals("0")) {
						SkyLogger.getMediatrixLogger().debug(logPrefix + ": skiping autoclose because of empty customerid:q:" + question.getId() + " docid:" + question.getDocId());
						try {
							MitarbeiterlogWriter.writeMitarbeiterlog(question, "Not possible to close question automatically: Empty CustomerID", 19, 0);
						} catch (Exception e) {
							ShedulerUtils.resetAuth("IR" + question.getId());
							MitarbeiterlogWriter.writeMitarbeiterlog(question, "Not possible to close question automatically: Empty CustomerID", 19, 0);
						}
					} else if (archiveFlag.equalsIgnoreCase(Question.S_MONITORED)) {
						autoClose(con, question, Question.S_MONITORED);
					} else {
						autoClose(con, question, Question.S_COMPLETED);
					}
					SkyLogger.getMediatrixLogger().debug(logPrefix + ": autoclose done: q:" + question.getId() + " docid:" + question.getDocId());
				} else {
					SkyLogger.getMediatrixLogger().debug(logPrefix + ": no autoclose: q:" + question.getId() + " docid:" + question.getDocId());
				}
				int popSubproject = question.getSubprojectId();
				question.setSubprojectId(Math.abs(popSubproject)); // sonnst probleme mit Cache-Mgr und dann mit den Lizenzen - da negative SubprojectID's gezogen werden
				boolean questionstoreok = false;
				try {
					ShedulerUtils.quickCheckLicence(question.getDocId());
					SkyLogger.getCommonLogger().debug("SSED.QStore1c generated q.id:" + question.getId() + "; f.docId:" + question.getDocId()+"; f.status:"+question.getStatus()+""+ question.getSubject());
					String statusbak=question.getStatus();
					if (question.getStatus().equals(Question.S_BLOCKED)){
						question.setStatus(Question.S_NEW);
						SkyLogger.getCommonLogger().debug("SSED.QStore1ca Generated docid:" + question.getDocId() + " frage:" + question.getId()+" remove blocked status:"+question.getStatus());
					}
					questionstoreok = API.getServerAPI().getQuestionAPI().store(con, question);
					question.setStatus(statusbak);
					SkyLogger.getCommonLogger().debug("SSED.QStore2c Generated q.docId:" + question.getDocId() + "; f.id:" + question.getId()+"; q.status:"+question.getStatus());
					
				} catch (SQLException e) {
					SkyLogger.getMediatrixLogger().error(logPrefix + "SSED.QStore1 Exception in StoreQuestion:" + question.getId() + " " + e.getMessage(), e);
				} catch (Exception e) {
					SkyLogger.getMediatrixLogger().error(logPrefix + "SSED.QStore1 Exception in StoreQuestion:" + question.getId() + " from:" + question.getFrom() + " to:" + question.getTo() + " return:" + question.getReturnPath() + " cc:" + question.getCC() + " bcc:" + question.getBCC() + " msg:" + e.getMessage(), e);
				}
				question.setSubprojectId(popSubproject);
				SkyLogger.getMediatrixLogger().debug(logPrefix + ": postImportExternalClassificator FINISH: q:" + question.getId() + " docid:" + question.getDocId() + "  cid:" + question.getExtra3() + " qstore:" + questionstoreok);
			} catch (Exception e) {
				SkyLogger.getMediatrixLogger().error(logPrefix + ": postImportExternalClassificator ERROR: q:" + question.getId() + " msg:" + e.getMessage(), e);
			}
		}
		try {
			if (!con.getAutoCommit() && !con.isClosed()) {
				con.commit();
				SkyLogger.getMediatrixLogger().debug(logPrefix + " Commit by leaving:");
			}
		} catch (SQLException e) {
			SkyLogger.getMediatrixLogger().error(logPrefix + " e:" + e.getMessage(), e);
		}
		return new HashMap();
	}
	
	
	@Override
	public HashMap foundServiceCenterReply(Connection arg0, Question question, Customer arg2, HashMap arg3) {
		SkyLogger.getMediatrixLogger().debug(getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ": q:" + (question != null ? question.getId() : "empty"));
		return new HashMap();
	}
	
	@Override
	public Vector messageChange(Connection arg0, Vector arg1, String arg2) {
		SkyLogger.getMediatrixLogger().debug(getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ": a2:" + arg2);
		return new Vector();
	}
	
	@Override
	public void postAutoReplySend(Connection arg0, Question question, Question arg2, Customer arg3, Subproject arg4, HashMap arg5) {
		//	SkyLogger.getMediatrixLogger().debug(getClass().getName() + "#" + new Object() {}.getClass().getEnclosingMethod().getName() + ": enter: q:" + question.getId());
	}
	
	@Override
	public HashMap postExportExternalClassificator(Connection arg0, Question question, Map arg2) {
		//String logPrefix = getClass().getName() + "#" + new Object() {}.getClass().getEnclosingMethod().getName();
		//SkyLogger.getMediatrixLogger().debug(logPrefix + ": enter");
		return new HashMap();
	}
	
	@Override
	public void postExternalSend(Connection arg0, Question question, Customer arg2, Subproject arg3, HashMap arg4) {
		//SkyLogger.getMediatrixLogger().debug(getClass().getName() + "#" + new Object() {}.getClass().getEnclosingMethod().getName() + ": enter: q:" + question.getId());
	}
	
	
	@Override
	public HashMap postProcessing(Connection con, Question question, Subproject arg2, HashMap arg3) {
		SkyLogger.getMediatrixLogger().debug(getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ": q:" + (question != null ? question.getId() : "empty"));
		return new HashMap();
	}
	
	@Override
	public HashMap preAutoReplySend(Connection arg0, Question question, Question arg2, Customer arg3, Subproject arg4, HashMap arg5) {
		SkyLogger.getMediatrixLogger().debug(getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ": q:" + (question != null ? question.getId() : "empty"));
		return new HashMap();
	}
	
	@Override
	public boolean preExportExternalClassificator(Connection arg0, Question question, Map arg2) {
		SkyLogger.getMediatrixLogger().debug(getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ": q:" + (question != null ? question.getId() : "empty"));
		return true;
	}
	
	@Override
	public HashMap preExternalSend(Connection arg0, Question arg1, Customer arg2, Subproject arg3, HashMap arg4) {
		//		SkyLogger.getMediatrixLogger().debug(getClass().getName() + "#" + new Object() {}.getClass().getEnclosingMethod().getName() + ": q:" +  (question!=null?question.getId():"empty"));
		return new HashMap();
	}
	
	@Override
	public List preFilterQuestion(Connection arg0, Question question, Project arg2, Account arg3, Map arg4) {
		SkyLogger.getMediatrixLogger().debug(getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ": q:" + (question != null ? question.getId() : "empty"));
		return new ArrayList();
	}
	
	@Override
	public boolean preImportExternalClassificator(Connection arg0, Project arg1, Map arg2) {
		return true;
	}
	
	@Override
	public String getExternalId(Connection con, IEmail email) {
		//	SkyLogger.getMediatrixLogger().debug(getClass().getName() + "#" + new Object() {}.getClass().getEnclosingMethod().getName() + ": q:" +  (question!=null?question.getId():"empty"));
		return "";
	}
	
	@Override
	public boolean checkInformationMessages(Connection arg0, IEmail arg1, Question question, Answer arg3) {
		SkyLogger.getMediatrixLogger().debug(getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ": q:" + (question != null ? question.getId() : "empty"));
		return true;
	}
	
	@Override
	public void externalReplyReceived(Connection connection, Question question, Question question1, Answer answer) {
		SkyLogger.getMediatrixLogger().debug(getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ": q:" + (question != null ? question.getId() : "empty"));
	}
}
