package com.nttdata.de.ityx.cx.workflow.outgoing.o7_archive;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;
import com.nttdata.de.ityx.cx.workflow.outgoing.mxpostprocessing.MxDbSingleton;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.outbound.MxOutboundIntegration;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.document.CDocument;

import java.sql.Connection;

public class LogCXProcessed extends AbstractWflBean {

    @Override
    public void execute(IFlowObject flowObject) throws Exception {
        String logPrefix = getClass().getName() + "#" + new Object() {
        }.getClass().getEnclosingMethod().getName() + ":";
        
        CDocument doc= DocContainerUtils.getDoc(flowObject);
        String docid= DocContainerUtils.getDocID(doc);
        if (doc.getNote("mx2cxId")!=null){
                
                Object mx2cxIdO = doc.getNote("mx2cxId");
                long mx2cxId=0;
                if (mx2cxIdO!=null){
                    if (mx2cxIdO instanceof Long){
                        mx2cxId = (Long) doc.getNote("mx2cxId"); 
                    } else{
                        String mx2cxIdS = (String) doc.getNote("mx2cxId");
                        mx2cxId=Long.parseLong(mx2cxIdS);
                    }
                }       
                if ( mx2cxId>0) {
                    Connection con=null;
                    try {
                        con = MxDbSingleton.getMxConnection(flowObject);
                        MxOutboundIntegration.updateContexProcessedAttribute(con, mx2cxId, MxOutboundIntegration.MXOUT_STATUS.WAITFORCB);
                    }finally {
                        MxDbSingleton.closeConnection(con);
                    }
                } else {
                    SkyLogger.getWflLogger().error(logPrefix + " ERROR_STATUS: NTT_MX_OutboundID: " + docid + " logid:" + mx2cxId);
                }
        }else {
               SkyLogger.getWflLogger().error(logPrefix + " ERROR_STATUS: NTT_MX_OutboundID: " + docid + " nologid");
        }
    }

}
