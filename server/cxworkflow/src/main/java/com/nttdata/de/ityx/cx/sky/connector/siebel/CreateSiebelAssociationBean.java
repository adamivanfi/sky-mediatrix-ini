package com.nttdata.de.ityx.cx.sky.connector.siebel;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.sky.connector.ConnectorFactory;
import com.nttdata.de.sky.connector.ISiebel;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.contex.interfaces.designer.IFlowObject;

public class CreateSiebelAssociationBean extends AbstractWflBean {

    private static final long serialVersionUID = 1504024930907576827L;

    @Override
    public void execute(IFlowObject flowObject) throws Exception {
        String docid = (String) flowObject.get(TagMatchDefinitions.DOCUMENT_ID);
        String customerid = (String) flowObject.get(TagMatchDefinitions.CUSTOMER_ID);
        String contactid = (String) flowObject.get(TagMatchDefinitions.CONTACT_ID);
        String contractId = (String) flowObject.get(TagMatchDefinitions.TAGMATCH_CONTRACT_NUMBER);  
        String activityid = (String) flowObject.get(TagMatchDefinitions.ACTIVITY_ID);
        ISiebel.Channel channel = ISiebel.Channel.valueOf((String) flowObject.get(TagMatchDefinitions.CHANNEL));

        if (DocContainerUtils.isEmpty(activityid)) {
            ConnectorFactory.getSiebelInstance().associateDocumentIdWithoutActivityId(docid, customerid, contractId, contactid);
            flowObject.put(CreateSiebelAssociationBean.class.getName(), "OK");
        } else {
            ConnectorFactory.getSiebelInstance().associateDocumentIdToActivity(docid, activityid, channel, ISiebel.Direction.OUTBOUND);
        }
    }
}
