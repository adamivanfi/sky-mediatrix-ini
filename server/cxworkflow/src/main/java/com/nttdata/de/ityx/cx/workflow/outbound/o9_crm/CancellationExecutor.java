package com.nttdata.de.ityx.cx.workflow.outbound.o9_crm;

import com.nttdata.de.ityx.cx.workflow.outbound.CallbackPooler;
import com.nttdata.de.ityx.cx.workflow.outgoing.mxpostprocessing.MxDbSingleton;
import com.nttdata.de.ityx.sharedservices.configuration.BeanConfig;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.connector.ConnectorFactory;
import com.nttdata.de.sky.connector.siebel.SiebelConnectorImpl;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.contex.interfaces.designer.IExflowState;
import de.ityx.contex.interfaces.designer.IFlowObject;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * Sky 202210 Cancellation Optimization
 */
public class CancellationExecutor extends CallbackPooler {

	private String serviceUrl;

	@Override
	public String getProcessname() {
		return "840_Cancellation";
	}
	@Override
	public String getMaster() {
		return "sky";
	}



	private static final String MXOUTBOUND_READ_CANCELPAR = "SELECT CANCELLATION_DATE, CANCELLATION_REASONCODE, CANCELLATION_REASON_FREETEXT, CANCELLATION_POSSIBLE_DATE from NTT_MX_OUTBOUND_CANCELPAR where OQ_ID = ?";

	public CancellationExecutor() {
	}

	@Override
	public void itemProcessor(IFlowObject flowObject, IExflowState exflowState, Map<String, Object> docMeta, String docid, long logid) throws Exception {
		synchronized (MXOUTBOUND_READ_CANCELPAR) {
			if (serviceUrl == null) {
				serviceUrl = BeanConfig.getReqString("QuickAction_WSDL");
				System.setProperty(SiebelConnectorImpl.SysProperty_SiebelService_Endpoint, serviceUrl);
			}
		}
		String customerid = (String) docMeta.get(TagMatchDefinitions.CUSTOMER_ID);
		String contactid = (String) docMeta.get(TagMatchDefinitions.CONTACT_ID);
		String contractId = (String) docMeta.get(TagMatchDefinitions.TAGMATCH_CONTRACT_NUMBER);
		String srId = (String) docMeta.get(TagMatchDefinitions.ACTIVITY_ID);

		Connection con = null;
		try {
			con = MxDbSingleton.getMxConnection(flowObject);

			PreparedStatement pstmt = con.prepareStatement(MXOUTBOUND_READ_CANCELPAR);
			pstmt.setLong(1, logid);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				Date cancellationDate = rs.getDate(1);
				String cancellationReason = rs.getString(2);
				String cancellationReasonFreetext = rs.getString(3);
				if (cancellationReasonFreetext == null)
					cancellationReasonFreetext = "";
				Date possibleCancellationDate = rs.getDate(4);

				// Changed by Ivanfi NTT-Data, I-211451:
				SkyLogger.getWflLogger().info("Calling IF4.6 with parameters:" + "docid=<" + docid + ">; customerid=<" + customerid + ">; contractId=<" + contractId + ">; contactid=<" + contactid + ">; srId=<" + srId + ">; cancellationDate=<" + getSblFormattedDate(cancellationDate)+">; possibleCancellationDate=<" +possibleCancellationDate + ">; cancellationReason=<" + cancellationReason + ">; cancellationReasonFreetext=<" + cancellationReasonFreetext + ">");
				//SkyLogger.getWflLogger().info("Calling IF4.6 with parameters:\n" + "docid:" + docid + "\n" + "customerid=<" + customerid + ">\n" + "contractId=<" + contractId + ">\n" + "contactid=<" + contactid + ">\n" + "srId=<" + srId + ">\n" + "cancellationDate=<" + getFormattedDate(cancellationDate) + ">\n" + "cancellationReason=<" + cancellationReason + ">\n" + "cancellationReasonFreetext=<" + cancellationReasonFreetext + ">\n");

				// Call IF 4.6
				ConnectorFactory.getSiebelInstance().triggerSRQuickAction_Cancellation(docid, customerid, contractId, contactid, srId, cancellationDate, possibleCancellationDate ,cancellationReason, cancellationReasonFreetext);
			} else {
				// Changed by Ivanfi NTT-Data, I-211451:
				SkyLogger.getWflLogger().error("Error due Calling IF4.6, Extended Attributes not readable. Parameters: docid=<" + docid + ">; customerid=<" + customerid + ">; contractId=<" + contractId + ">; contactid=<" + contactid + ">; srId=<" + srId + ">");
				//SkyLogger.getWflLogger().error("Error due Calling IF4.6, Extended Attributes not readable. Parameters:\n" + "docid:" + docid + "\n" + "customerid=<" + customerid + ">\n" + "contractId=<" + contractId + ">\n" + "contactid=<" + contactid + ">\n" + "srId=<" + srId + ">");
				throw new Exception("IF4.6 Missing Cancellation Data, mx2cxId: " + logid + " docid:" + docid);
			}
		} finally {
			MxDbSingleton.closeConnection(con);
		}
	}


	protected static final String sblDateFormatPattern = "MM/dd/yyyy"; //05/08/2015

	private static synchronized String getSblFormattedDate(java.util.Date tsdate) {
		return (new SimpleDateFormat(sblDateFormatPattern)).format(tsdate);
	}


	protected static final String mxDateFormatPattern = "yyyy.MM.dd"; //05/08/2015
	private static synchronized java.util.Date parseMXFormatedDate(String date){
		try {
			return	(new SimpleDateFormat(mxDateFormatPattern)).parse(date);
		} catch (ParseException e) {
			SkyLogger.getItyxLogger().error("Problem during Parsing ArchiveDate: " + date, e);
			return null;
		}
	}

}
