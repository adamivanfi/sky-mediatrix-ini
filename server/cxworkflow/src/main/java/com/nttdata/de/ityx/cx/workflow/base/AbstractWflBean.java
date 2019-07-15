package com.nttdata.de.ityx.cx.workflow.base;

import com.nttdata.de.ityx.cx.workflow.outgoing.mxpostprocessing.MxDbSingleton;
import com.nttdata.de.ityx.sharedservices.utils.ContexDbConnector;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.contex.impl.designer.state.KeyConfiguration;
import de.ityx.contex.impl.designer.state.StateResult;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.impl.document.text.email.EmailDocument;
import de.ityx.contex.impl.document.text.string.StringDocument;
import de.ityx.contex.interfaces.designer.IBeanState;
import de.ityx.contex.interfaces.designer.IExflowState;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.document.CDocument;
import de.ityx.contex.interfaces.extag.TagMatch;
import org.apache.log4j.Logger;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractWflBean implements IBeanState {

    protected final String logPreafix = getClass().getName();
    // protected Logger log=SkyLogger.getWflLogger();
    public Map<Long, String> parameterMap = new ConcurrentHashMap<>();

    public static final String SEPA_MANDAT_AUTOMAT_VERARBEITUNG = "[SEPA-Mandat automat. Verarbeitung] ";
    public static final String SEPA_MANDAT_MANUELE_VERARBEITUNG = "[SEPA-Mandat manuelle Verarbeitung] ";
    public static final String SEPA_MANDAT_MULTI_CASE = "[Mandat mit zweitem Anliegen bitte bearbeiten] ";
    public static final String SEPA_MANDAT_FHV = "[Mandat mit Vertrag bitte bearbeiten] ";
    public static final String FHV_AUTO_MANDAT = "[Fachhandelsvertrag automat. Verarbeitung, Mandat verarbeitet] ";
    public static final String FHV_AUTO =   "[Fachhandelsvertrag automat. Verarbeitung] ";
    public static final String FHV_MANUEL = "[Fachhandelsvertrag manuelle Verarbeitung] ";


    public static final String MX_EMAIL_IDS_BY_MESSAGEID = "SELECT ID from EMAIL where MESSAGEID = ? and EMAILDATE > ?  and ID != ? ";
    public static final String CX_PROCESSED_BY_EMAIL_IDS = "SELECT COUNT(LOG_ID) count from NTT_CX_REPORT "+
            " where CREATED > ? and CTX_DOCUMENTID != ? and PROCESSVERSION like ? and STEP = ? and MTX_ORGMAILID in ";


	protected String docid;
	protected String processname ;
	protected String tenant ;
    protected String processVersion;

    @Override
    public StateResult execute(int arg0, IFlowObject flowObject, IExflowState arg2) throws Exception {
         docid = DocContainerUtils.getDocID(flowObject);
         processname = getProcessname(arg2);
		 tenant = getTenant(arg2);
        try {
            if (docid != null) {
                parameterMap.put(Thread.currentThread().getId(), processname + ": " + docid);
            }
            setWflChannel(flowObject);
            execute(flowObject);
        } catch (Exception e) {
            e.printStackTrace();
            SkyLogger.getWflLogger().error(logPreafix + ":ERROR: " + processname + ": " + docid + " Message:" + e.getMessage(), e);
            return StateResult.exception(e.getMessage());
        }
        return StateResult.STATEOK;
    }

    public abstract void execute(IFlowObject flowObject) throws Exception;

    @Override
    public void abortExecute() {
        long tid = Thread.currentThread().getId();
        String docid = parameterMap.get(tid);
        Logger log = SkyLogger.getWflLogger();
        log.error(logPreafix + " ABORT EXECUTE for:" + docid + " threadID:" + tid);
    }

    @Override
    public void rollbackExecute() {
        long tid = Thread.currentThread().getId();
        String docid = parameterMap.get(tid);
        Logger log = SkyLogger.getWflLogger();
        log.error(logPreafix + " ROLLBACK EXECUTE for:" + docid + " threadID:" + tid);

    }


	public void setWflChannel(IFlowObject flow) {
        CDocument cdoc = DocContainerUtils.getDoc(flow);
        if (cdoc != null && (flow.get("WFLDTS") == null || flow.get("WFLDTF") == null)) {
            if (cdoc.getNote(TagMatchDefinitions.CHANNEL) != null) {
                String doctype = (String) cdoc.getNote(TagMatchDefinitions.CHANNEL);
                // TODO: Set BRIEF instead of LETTER?
                flow.put("WFLDTF", doctype);

                String doctype_short = doctype;
                if (doctype_short.equalsIgnoreCase("LETTER") || doctype_short.equalsIgnoreCase("BRIEF") || doctype_short.equalsIgnoreCase("FAX")) {
                    doctype_short = "DOCUMENT";
                }
                flow.put("WFLDTS", doctype_short);
            } else {
                CDocumentContainer<CDocument> cont = DocContainerUtils.getDocContainer(flow);
                if (cont != null && cont.getNote("doctype") != null) {
                    int doctype = (Integer) cont.getNote("doctype");
                    // ECODE(docpooltype,1,'Letter', 3, 'Fax', 2,'Email')
                    if (doctype == 1) {
                        flow.put("WFLDTF", "BRIEF");
                        flow.put("WFLDTS", "DOCUMENT");
                        cdoc.setNote(TagMatchDefinitions.CHANNEL, "BRIEF");
                    } else if (doctype == 2) {
                        flow.put("WFLDTF", "EMAIL");
                        flow.put("WFLDTS", "EMAIL");
                        cdoc.setNote(TagMatchDefinitions.CHANNEL, "EMAIL");
                    } else if (doctype == 3) {
                        flow.put("WFLDTF", "FAX");
                        flow.put("WFLDTS", "DOCUMENT");
                        cdoc.setNote(TagMatchDefinitions.CHANNEL, "FAX");
                    }
                }
            }
        }
    }

    public String getProcessname(IExflowState exflowstate) {
        if (exflowstate != null && exflowstate.getProcessInfo() != null && exflowstate.getProcessInfo().getProcessTyp() != null) {
            return exflowstate.getProcessInfo().getProcessTyp();
        }
        return null;
    }

	public String getTenant(IExflowState exflowstate) {
		if (exflowstate != null && exflowstate.getProcessInfo() != null && exflowstate.getProcessInfo().getProjectName() != null) {
			return exflowstate.getProcessInfo().getProjectName();
		}
		return null;
	}

    @Override
    public void cleanState() {
        //useless
    }

    @Override
    public void prepareForCluster(String arg0) {
		//useless
		}

    @Override
    public void prepareForResumeFromCluster() {
			//useless
			}

    @Override
    public KeyConfiguration[] getKeys() {
		//useless here, can be overwritten by subclasses
        return null;
    }

	public KeyConfiguration[] getCommonKeys() {
		return getKeys();
	}

	@Deprecated
	public CDocumentContainer createSimplifiedTextDocument(IFlowObject flowObject, String text) {
		return createSimplifiedTextDocument(DocContainerUtils.getDoc(flowObject), DocContainerUtils.getDocContainer(flowObject), text);
    }

	@Deprecated
    public CDocumentContainer createSimplifiedTextDocument(CDocument srcDoc, CDocumentContainer srcCont, String text) {
         StringDocument sdoc =  StringDocument.getInstance(text);

        copyMetadata(srcDoc,sdoc);
        CDocumentContainer out = new CDocumentContainer(sdoc);
        if (srcCont!=null) {
            out.setTags(srcCont.getTags());

            if (srcCont.getNotes()!=null) {
                for (java.util.Map.Entry<String, Object> note : srcCont.getNotes().entrySet()) {
                    out.setNote(note.getKey(), note.getValue());
                }
            }
        }
        return out;
    }
    
	public static void copyMetadata(CDocument sourceDoc, CDocument destinationDoc) {
		if (sourceDoc==null) {
            SkyLogger.getWflLogger().error("Empty Document to copy");
            return ;
        }
        if (sourceDoc.headers() != null) {
			List l = new LinkedList<>();
			Iterator i = sourceDoc.headers();
			while (i.hasNext()) {
				l.add(i.next());
			}
			destinationDoc.setHeaders(l);
		}
		if (sourceDoc.getTitle() != null) {
			destinationDoc.setTitle(sourceDoc.getTitle());
		} else if (sourceDoc.getClass().equals(EmailDocument.class)) {
			EmailDocument edoc = ((EmailDocument) sourceDoc);
			String subject = edoc.getSubject();

			if (subject != null) {
				destinationDoc.setTitle(subject);
			}
		}
		if (sourceDoc.getAnnotations() != null) {
			destinationDoc.setAnnotations(sourceDoc.getAnnotations());
		}
		if (sourceDoc.getTags() != null) {
			destinationDoc.getTags().clear();
			destinationDoc.setTags(sourceDoc.getTags());
		}

		for (Map.Entry<String, Object> note : sourceDoc.getNotes().entrySet()) {
			destinationDoc.setNote(note.getKey(), note.getValue());
		}
		if (sourceDoc.getFormtype()!= null) {
			destinationDoc.setFormtype(sourceDoc.getFormtype());
		}
	}

	protected CDocumentContainer createContainerAndCopyMetadata(CDocumentContainer cont, CDocument newdoc, String docID) {

        CDocumentContainer out = new CDocumentContainer(newdoc);
         List<TagMatch> contTml= cont.getTags();
        List<TagMatch> newTml= new LinkedList<>();
        for (TagMatch contTm : contTml) {
            List<TagMatch> outTml=out.getTags();
            boolean found=false;
            for (TagMatch outTm : outTml) {
                if (contTm.getIdentifier().equalsIgnoreCase(outTm.getIdentifier())){
					outTm.setTagValue(contTm.getTagValue());
                    break;
                }
                if (!found){
                    newTml.add(contTm);
                }
            }
        }
        out.getTags().addAll(newTml);
        //out.getNotes().clear();
        if (cont.getNotes()!=null){
            for (java.util.Map.Entry<String, Object> note : cont.getNotes().entrySet()) {
                out.setNote(note.getKey(), note.getValue());
            }
        }
		DocContainerUtils.setDocID(out,newdoc, docID);
        return out;
    }
    public void setWflStepDetail(IFlowObject flowObject, String text){
        Object mobj = flowObject.get("reporting_map");
        if (mobj != null && !mobj.getClass().equals(String.class)) {
            Map<String, Object> map = (Map<String, Object>) mobj;
            map.put("STEPDETAIL", text);
        }
        SkyLogger.getWflLogger().debug(DocContainerUtils.getDocID(flowObject)+":"+text);

    }


    protected List<String> getEmailIdsByMessageId(IFlowObject flowObject, String messageId, long currentMtxEmailId, long emaildate){
        List<String> result = new ArrayList<String>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = MxDbSingleton.getMxConnection(flowObject);
            pstmt = con.prepareStatement(MX_EMAIL_IDS_BY_MESSAGEID);
            pstmt.setString(1, messageId);
            pstmt.setLong(2, emaildate);
            pstmt.setLong(3, currentMtxEmailId);
            rs = pstmt.executeQuery();
            if (rs!=null) {
                while (rs.next()) {
                    result.add(rs.getString("ID"));
                }
            }
            SkyLogger.getWflLogger().debug(logPreafix+".getEmailIdsByMessageId -> messageId: "+messageId+
                    "; emaildate: "+emaildate+"; currentMtxEmailId: "+currentMtxEmailId+"; result: "+result.toString());
        } catch (Exception e) {
            SkyLogger.getWflLogger().error(logPreafix+".getEmailIdsByMessageId -> messageId: "+messageId+
                    "; emaildate: "+emaildate+"; currentMtxEmailId: "+currentMtxEmailId+"; errMsg: "+e.getMessage(),e);
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


    protected boolean isExistsProcessedDublicatedEmails(List<String> emailIds, String docId,
                                                        Date createtimeStart, String processVersion, String step){
        boolean result = false;
        if (emailIds == null || emailIds.isEmpty()){
            return result;
        }

        String inClause = " (?";
        for(int i = 1;i < emailIds.size();i++) {
            inClause += ", ?";
        }
        inClause += ")";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Array arrIds = null;
        try {
            con	= ContexDbConnector.getConnection();
            pstmt = con.prepareStatement(CX_PROCESSED_BY_EMAIL_IDS + inClause);

            int x = 1;
            pstmt.setDate(x++, new java.sql.Date(createtimeStart.getTime()));
            pstmt.setString(x++, docId);
            pstmt.setString(x++, processVersion);
            pstmt.setString(x++, step);
            for(String emailId : emailIds) {
                pstmt.setString(x++, emailId);
            }
            try {
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    result = rs.getLong("count")>0;
                }
            } finally {
                if (rs != null) {
                    rs.close();
                }
            }
            SkyLogger.getWflLogger().debug(logPreafix+".isExistsProcessedDublicatedEmails -> emailIds: "+emailIds.toString()+
                    "; createtimeStart: "+createtimeStart.toString()+"; result: "+result);
        } catch (Exception e) {
            SkyLogger.getWflLogger().error(logPreafix+".isExistsProcessedDublicatedEmails -> emailIds: "+emailIds.toString()+
                    "; createtimeStart: "+createtimeStart.toString()+"; errMsg: "+e.getMessage(),e);
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
            ContexDbConnector.releaseConnection(con);
        }
        return result;
    }

}
