package com.nttdata.de.ityx.cx.workflow.incoming.i6_mediatrix;


import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;

import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.ityx.sharedservices.utils.ShedulerUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import de.ityx.contex.impl.designer.state.StateResult;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.impl.document.text.email.EmailDocument;
import de.ityx.contex.interfaces.designer.IExflowState;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.document.CDocument;
import de.ityx.contex.interfaces.extag.TagMatch;
import com.nttdata.de.ityx.cx.workflow.outgoing.mxpostprocessing.MxDbSingleton;
import org.apache.log4j.Logger;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PrepareInjection extends AbstractWflBean {

	public static final String BARCODE = "barcode";
	private static final String MX_FRAGE_READ_BY_DOCID = "SELECT ID from FRAGE where DOCID = ? and EMAIL_DATE > ?  and UNIT != 'DEFAULT'";

	@Override
	public void execute(IFlowObject flowObject) throws Exception {
		Logger log = SkyLogger.getWflLogger();
		// Removes illegal barcodes and redundant documents.
		try {
			CDocumentContainer con = DocContainerUtils.getDocContainer(flowObject);
			for (int i = 1; i < con.size(); i++) {
				con.removeDocumentAt(i);
			}
			CDocument document = DocContainerUtils.getDoc(flowObject);
			String title = (String) flowObject.get(DocContainerUtils.DOC + ".title");

			if (con.getNote("INCOMINGTIMESTAMP") == null) {
				Object o = document.getNote("INCOMINGTIMESTAMP");
				if (o != null && o instanceof java.sql.Timestamp) {
					con.setNote("INCOMINGTIMESTAMP", o);
				}
			}

			final String barcode = (String) document.getNote(BARCODE);
			if (barcode != null && !barcode.matches("\\d{16}")) {
				document.setNote(BARCODE, "unbekannter Barcodetyp");
				//log.error("unknown barcode during 600 MXInjection Preparation<" + barcode + ">");
			}

			Object err = document.getNote("validationError");
			if (err != null) {
				List<TagMatch> list = con.getTags();
				list.add(new TagMatch("Error", "ValidationError, " + new Date()));
				con.setTags(list);
				title = "ValidationError: " + document.getTitle();
			}
			if (document.getClass().equals(EmailDocument.class)) {
				flowObject.put(DocContainerUtils.DOC + ".doctype", 2);
			} else {
				if (!title.toLowerCase().endsWith(".tif")) {
					title = title + ".tif";
					document.setTitle(title);
					con.setNote("titel", title);
					flowObject.put(DocContainerUtils.DOC + ".title", title);
				}
			}
			log.info("PrepareInjection: " + DocContainerUtils.getDocID(flowObject) + ";");
		} catch (Exception e) {
			log.error("PrepareInjection: " + DocContainerUtils.getDocID(flowObject) + " " + e.getMessage());
			throw e;
		}
	}

	@Override
	public StateResult execute(int arg0, IFlowObject flowObject, IExflowState arg2) throws Exception {
		StateResult result = StateResult.STATEOK;
		docid = DocContainerUtils.getDocID(flowObject);
		CDocumentContainer con = DocContainerUtils.getDocContainer(flowObject);
		CDocument doc = DocContainerUtils.getDoc(flowObject, con);
		Date incomDate = DocContainerUtils.getIncommingDate(con);
		processname = getProcessname(arg2);
		processVersion = processname.substring(0,10)+"%";
		tenant = getTenant(arg2);
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(incomDate);
			calendar.add(Calendar.DATE, -3);
			if (docid != null) {
				parameterMap.put(Thread.currentThread().getId(), processname + ": " + docid);
				if (ShedulerUtils.isExistsDocsByParameter(tenant,processVersion,docid,calendar.getTimeInMillis())){
                    /* Check Document in MX: */
					/*	|| isExistsQuestionByDocId(flowObject, docid, calendar.getTime().getTime())){*/
					result = StateResult.exception("The process \'"+ processname+"\'  with docId: "+docid+" was already executed !");
				}
				if (EmailDocument.class.isAssignableFrom(doc.getClass())) {
					EmailDocument edoc = (EmailDocument) doc;
					String messageId = DocContainerUtils.getMessageId(edoc);
					long mtxEmailId = DocContainerUtils.getMtxEmailId(doc);
					List<String>  emailIdList = getEmailIdsByMessageId(flowObject,messageId,mtxEmailId,calendar.getTimeInMillis());
					/* Rollback am 02.08.2017
					if(isExistsProcessedDublicatedEmails(emailIdList, docid, calendar.getTime(), processVersion, "START" )){
						result = StateResult.exception("Dublicated Emails "+ emailIdList.toString()+"  with messageId: "+messageId+" in process \'"+ processname+"\' with docId: \'" + docid +  "\' was already executed !");
					}
					*/
				}
			}
			SkyLogger.getWflLogger().debug(logPreafix + ".execute -> tenant: " + tenant + "; processname: " + processname +
					"; docId; " + docid + "; calendar: "+calendar.getTime().getTime()+"; result: "+result.toString()  );
			if (result.getResultType() == 0) {
				setWflChannel(flowObject);
				execute(flowObject);
			}
		} catch (Exception e) {
			SkyLogger.getWflLogger().error(logPreafix + ".execute ->  tenant: " + tenant + "; processname: " + processname +
					"; docId; " + docid + "; calendar: "+calendar.getTime().getTime()+ "; errMsg: " + e.getMessage(), e);
			return StateResult.exception(e.getMessage());
		}
		return result;
	}


	public boolean isExistsQuestionByDocId(IFlowObject flowObject, String docId, long createtimeStart){
		boolean result = false;
		Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
			con = MxDbSingleton.getMxConnection(flowObject);
			pstmt = con.prepareStatement(MX_FRAGE_READ_BY_DOCID);
			pstmt.setString(1, docId);
			pstmt.setLong(2, createtimeStart);
			rs = pstmt.executeQuery();
            if (rs!=null) {
                int i = 0;
                while (rs.next()) {
                    i++;
                }
                result = i > 0;
            }
			SkyLogger.getWflLogger().debug(logPreafix+".isExistsQuestionByDocId -> docId: "+docId+
					"; createtimeStart: "+createtimeStart+"; result: "+result);
		} catch (Exception e) {
			SkyLogger.getWflLogger().error(logPreafix+".isExistsQuestionByDocId -> docId: "+docId+
					"; createtimeStart: "+createtimeStart+"; errMsg: "+e.getMessage(),e);
		} finally {
            if (rs!=null){
                try {
                    rs.close();
                } catch (Exception e) {
                    SkyLogger.getWflLogger().error(e.getMessage(),e);
                }
            }
            if (pstmt!=null){
                try {
                    pstmt.close();
                } catch (Exception e) {
                    SkyLogger.getWflLogger().error(e.getMessage(),e);
                }
            }
            MxDbSingleton.closeConnection(con);
		}
		return result;
	}



}
