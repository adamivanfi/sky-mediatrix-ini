/**
 * 
 */
package com.nttdata.de.ityx.cx.sky.connector.siebel;

import com.nttdata.de.ityx.cx.sky.enrichment.SetDocumentMetadata;
import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;
import com.nttdata.de.ityx.cx.workflow.base.FlowUtils;
import com.nttdata.de.ityx.cx.workflow.utils.WorkflowTextExtractionUtils;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.designer.IParameterMap;
import de.ityx.contex.interfaces.document.CDocument;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author DHIFLM
 * 
 */
public class CheckSiebelCallbackBean extends AbstractWflBean {

	protected final String	logPreafix	= getClass().getName();

	public void execute(IFlowObject flowObject) throws Exception {
		Map<String, String> map = new TreeMap<>();
		IParameterMap inputMap = flowObject.getInputMap("modelMap");
		// for (Iterator it = inputMap.names(); it.hasNext();)
		// SkyLogger.getItyxLogger().debug(": " + it.next().toString());
		String docId = inputMap.getParameter("documentid").getAsString();
		String contactid = inputMap.getParameter("contactid").getAsString();
		String errorCode = inputMap.getParameter("errorcode").getAsString();
		String errorMessage = inputMap.getParameter("errormessage").getAsString();
		String correlationId = inputMap.getParameter("correlationid").getAsString();
		String usecaseId = inputMap.getParameter("usecaseid").getAsString();

		SkyLogger.getConnectorLogger().info("IF4.1C: "+docId+" UsecaseId: " + usecaseId + " ContactID: " + contactid + " ECode: " + errorCode + " eMessage: " + errorMessage + " cor: " + correlationId);

		if (!errorCode.equals("0")) {
                        SkyLogger.getConnectorLogger().info("IF4.1C: "+docId+" Error:\n" + errorCode + " " + errorMessage + " " );
			throw new Exception("CreateSiebelContactError:\n" + errorCode + " " + errorMessage + " " + usecaseId + "\nDcoumentID: " + docId);
		}

		map.put("siebelReturnCallback", docId != null ? docId : "0");
		String conId = contactid != null ? contactid : "0";
		map.put("siebelContactId", conId);
		flowObject.put("map", map);

		flowObject.put("ContactID", conId);
		CDocument document = DocContainerUtils.getDoc(flowObject);
		document.setNote(TagMatchDefinitions.CONTACT_ID, conId);

		String parameter = FlowUtils.getRequiredString(flowObject, "docStateMediatrixWrite") ; //+ "_" + FlowUtils.getRequiredString(flowObject, "WFLDTS");

		if (document.getFormtype()!=null && (document.getFormtype().equals("fh_vertrag") || document.getFormtype().equals(TagMatchDefinitions.SEPA_MANDATE))) {
			parameter = FlowUtils.getRequiredString(flowObject, "FHV_docStateMediatrixWrite");
		}
		flowObject.put("parameter", parameter);
	}

        protected boolean checkLagerBarcodePages(CDocument document) throws Exception {
            return WorkflowTextExtractionUtils.documentBarcodeMatchPattern(document, SetDocumentMetadata.BARCODE_LAGER);
        }

}
