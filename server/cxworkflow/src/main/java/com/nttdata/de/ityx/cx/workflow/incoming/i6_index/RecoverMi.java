package com.nttdata.de.ityx.cx.workflow.incoming.i6_index;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;
import com.nttdata.de.ityx.sharedservices.utils.ContexDbConnector;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.document.CDocument;
import de.ityx.contex.interfaces.extag.TagMatch;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecoverMi extends AbstractWflBean {

    @Override
    public void execute(IFlowObject flowObject) throws Exception {
        CDocumentContainer container = DocContainerUtils.getDocContainer(flowObject);
        CDocument doc= DocContainerUtils.getDoc(container);

        //Recovery of OrgEmailID for Wfl-Reporting
        doc.setNote("MTX-EmailId", container.getNote("MTX-EmailId"));

        Long docpoolid = (Long) container.getMetainformation("docpoolid");

        String comment = "";
        Connection con = ContexDbConnector.getAutoCommitConnection();
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("select comment_text from cxdsg_cdocpool where id=" + docpoolid);
        if (rs.next()) {
            comment = rs.getString(1);
        }
        rs.close();
        con.close();
        String formtype = comment.substring(comment.indexOf("Formtype=") + 9, comment.length());

        int custid_start = comment.indexOf("CustomerID=") + 11;
        int custid_stop = comment.indexOf(", ContractID=");
        int ctr_start = custid_stop + 13;
        int ctr_stop = comment.indexOf(", F");

        if (custid_stop < 2) {
            custid_stop = comment.indexOf(", ContractNumber=");
            ctr_start = custid_stop + 16;
        }
        if (custid_stop < 2) {
            custid_stop = comment.indexOf(", Formtype=");
            ctr_start = 0;
            ctr_stop = 0;
        }

        String cid = comment.substring(custid_start, custid_stop);
        String ctrid = comment.substring(ctr_start, ctr_stop);

        if (DocContainerUtils.isEmpty(cid)) cid=null;
        if (DocContainerUtils.isEmpty(ctrid)) ctrid=null;
        
        TagMatch topLevel = new TagMatch("ManualValidation");
        topLevel.add(new TagMatch(TagMatchDefinitions.MANUAL_FORMTYPE, formtype));
        topLevel.add(new TagMatch(TagMatchDefinitions.CUSTOMER_ID, cid));
        topLevel.add(new TagMatch(TagMatchDefinitions.TAGMATCH_CONTRACT_NUMBER, ctrid));

        List<TagMatch> tmList = new ArrayList<>();
        tmList.add(topLevel);
        container.setTags(tmList);

        CDocument cdoc = container.getDocument(0);
        cdoc.setFormtype(formtype);
        cdoc.setNote(TagMatchDefinitions.MANUAL_FORMTYPE, formtype);
        cdoc.setTags(tmList);
        
        String info = ("CustID:>" + cid + "< CtrID:>" + ctrid + "< Formtype:>" + formtype + "<");

		Object mobj = flowObject.get("reporting_map");
		if (mobj != null && !mobj.getClass().equals(String.class)) {
			Map<String, Object> map = (Map<String, Object>) mobj;
			map.put("STEPDETAIL", info);
		}
        
    }

   
    
}
