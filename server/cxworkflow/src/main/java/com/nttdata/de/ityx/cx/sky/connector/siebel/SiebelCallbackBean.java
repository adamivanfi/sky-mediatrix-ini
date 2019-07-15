package com.nttdata.de.ityx.cx.sky.connector.siebel;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.FlowObjectConstants;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.designer.IParameterMap;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author DHIFLM
 *
 */
public class SiebelCallbackBean extends AbstractWflBean {

    @Override
    public void execute(IFlowObject flowObject) throws Exception {
        Map<String, String> map = new TreeMap<>();
        IParameterMap inputMap = flowObject.getInputMap("modelMap");
        String docId = inputMap.getParameter("documentid").getAsString();
        String contactid = inputMap.getParameter("contactid").getAsString();
        String errorCode = inputMap.getParameter("errorcode").getAsString();
        String errorMessage = inputMap.getParameter("errormessage").getAsString();
        String correlationId = inputMap.getParameter("correlationid").getAsString();
        String usecaseId = inputMap.getParameter("usecaseid").getAsString();

        SkyLogger.getConnectorLogger().info("IF4.XC: "+docId+ " ContactID: " + contactid + " " + "errorCode: " + errorCode + " " + "errorMessage: " + errorMessage + " "
                + " cor-Parameter: " + correlationId + " usecaseId: " + usecaseId + " ");

        if (FlowObjectConstants.SiebelCallback.VALUE_CreateContact.equals(usecaseId)) {

            flowObject.put(FlowObjectConstants.SiebelCallback.KEY, FlowObjectConstants.SiebelCallback.VALUE_CreateContact);
            flowObject.put(FlowObjectConstants.SiebelCallbackError.KEY, Boolean.FALSE);

            if (!errorCode.equals("0")) {
                flowObject.put(FlowObjectConstants.SiebelCallbackError.KEY, Boolean.TRUE);
                map.put("siebelReturnCallback", docId != null ? docId : "0");
                SkyLogger.getConnectorLogger().error("IF4.1: "+docId+" UCID:"+usecaseId+ " ERROR:"+errorCode+"/"+errorMessage);
                throw new Exception("CreateSiebelContactError:\n" + errorCode + " " + errorMessage + " " + usecaseId + "\nDcoumentID: " + docId);
            } else {
                map.put("siebelReturnCallback", docId != null ? docId : "0");
                String conId = contactid != null ? contactid : "0";
                map.put("siebelContactId", conId);
                flowObject.put("map", map);
                flowObject.put("ContactID", conId);
                DocContainerUtils.getDoc(flowObject).setNote(TagMatchDefinitions.CONTACT_ID, conId);
                SkyLogger.getConnectorLogger().debug("IF4.1C: "+docId+" UCID:"+usecaseId+ " OK:"+conId);
            }
        } else if (FlowObjectConstants.SiebelCallback.VALUE_CreateTrackingSR.equals(usecaseId)) {
            // TODO: reserved for IF 4.2 callbacks
            flowObject.put(FlowObjectConstants.SiebelCallback.KEY, FlowObjectConstants.SiebelCallback.VALUE_CreateTrackingSR);
            flowObject.put(FlowObjectConstants.SiebelCallbackError.KEY, Boolean.FALSE);

            if (!errorCode.equals("0")) {
                flowObject.put(FlowObjectConstants.SiebelCallbackError.KEY, Boolean.TRUE);
                SkyLogger.getConnectorLogger().error("IF4.2: "+docId+" UCID:"+usecaseId+ " ERROR:"+errorCode+"/"+errorMessage);
            } else {
                 SkyLogger.getConnectorLogger().debug("IF4.2: "+docId+" UCID:"+usecaseId+ " OK:");
            }
        } else if (FlowObjectConstants.SiebelCallback.VALUE_AssociateActivity.equals(usecaseId)) {
            // TODO: reserved for IF 4.4 callbacks
            flowObject.put(FlowObjectConstants.SiebelCallback.KEY, FlowObjectConstants.SiebelCallback.VALUE_AssociateActivity);
            flowObject.put(FlowObjectConstants.SiebelCallbackError.KEY, Boolean.FALSE);

            if (!errorCode.equals("0")) {
                flowObject.put(FlowObjectConstants.SiebelCallbackError.KEY, Boolean.TRUE);
                SkyLogger.getConnectorLogger().error("IF4.4: "+docId+" UCID:"+usecaseId+ " ERROR:"+errorCode+"/"+errorMessage);
            } else {
                SkyLogger.getConnectorLogger().debug("IF4.4: "+docId+" UCID:"+usecaseId+ " OK:");
            }
        } else {
            SkyLogger.getConnectorLogger().error("IF4.XXXX: "+docId+" UCID:"+usecaseId+ " Unhandled usecase identifier for callback");
        }
    }
}
