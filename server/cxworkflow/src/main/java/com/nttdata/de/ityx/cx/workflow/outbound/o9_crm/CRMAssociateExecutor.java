package com.nttdata.de.ityx.cx.workflow.outbound.o9_crm;

import com.nttdata.de.ityx.cx.workflow.outbound.OutboundPooler;
import com.nttdata.de.ityx.sharedservices.configuration.BeanConfig;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.sky.connector.ConnectorFactory;
import com.nttdata.de.sky.connector.ISiebel;
import com.nttdata.de.sky.connector.siebel.SiebelConnectorImpl;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.contex.interfaces.designer.IExflowState;
import de.ityx.contex.interfaces.designer.IFlowObject;

import java.util.Map;

/**
 * Created by meinusch on 16.07.15.
 */
public class CRMAssociateExecutor  extends OutboundPooler {

	private String serviceUrl;
	private final String processname="830_Associate";

	@Override
	public String getMaster() {
		return "sky";
	}


	public CRMAssociateExecutor() {
		}

	@Override
	public void itemProcessor(IFlowObject flowObject, IExflowState exflowState, Map<String, Object> docMeta, String docid, long logid) throws Exception {
		synchronized (processname) {
			if (serviceUrl==null) {
				serviceUrl = BeanConfig.getReqString("Siebel_WSDL");
				System.setProperty(SiebelConnectorImpl.SysProperty_SiebelService_Endpoint, serviceUrl);
			}
		}

		String direction = (String) docMeta.get(TagMatchDefinitions.MX_DIRECTION);
		String formtype = (String) docMeta.get(TagMatchDefinitions.FORM_TYPE_CATEGORY);

		String customerid =  (String) docMeta.get(TagMatchDefinitions.CUSTOMER_ID);
		String contactid =  (String) docMeta.get(TagMatchDefinitions.CONTACT_ID);
		String contractId =  (String) docMeta.get(TagMatchDefinitions.TAGMATCH_CONTRACT_NUMBER);
		String activityid =  (String) docMeta.get(TagMatchDefinitions.ACTIVITY_ID);
		ISiebel.Channel channel = ISiebel.Channel.valueOf((String) docMeta.get(TagMatchDefinitions.CHANNEL));

		if (DocContainerUtils.isEmpty(activityid)) {
			ConnectorFactory.getSiebelInstance().associateDocumentIdWithoutActivityId(docid, customerid, contractId, contactid);
		} else {
			ConnectorFactory.getSiebelInstance().associateDocumentIdToActivity(docid, activityid, channel, ISiebel.Direction.OUTBOUND);
		}
	}

	@Override
	public String getProcessname() {
		return processname;
	}
}
