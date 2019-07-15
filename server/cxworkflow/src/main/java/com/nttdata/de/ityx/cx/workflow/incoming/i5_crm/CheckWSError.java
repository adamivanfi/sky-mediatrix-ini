package com.nttdata.de.ityx.cx.workflow.incoming.i5_crm;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;
import com.nttdata.de.lib.logging.SkyLogger;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.designer.IParameterMap;
import org.apache.log4j.Logger;

import java.util.Iterator;

public class CheckWSError extends AbstractWflBean {

	/**
	 *
	 */
	private static final long serialVersionUID = -8289727354926654496L;

	protected final String logPreafix = getClass().getName();

	public void execute(IFlowObject flowObject) throws Exception {
		IParameterMap inputMap = flowObject.getInputMap("modelMap");
		
		//TODO: schöner machen
		String allparams = "";
		for (Iterator it = inputMap.names(); it.hasNext(); ) {
			String key = it.next().toString();
			allparams += ": " + key + "=" + inputMap.getParameter(key).getAsString();
		}

		String docId = inputMap.getParameter("documentid").getAsString();
		String contactid = inputMap.getParameter("contactid").getAsString();
		String errorCode = inputMap.getParameter("errorcode").getAsString();
		String errorMessage = inputMap.getParameter("errormessage").getAsString();
		String correlationId = inputMap.getParameter("correlationid").getAsString();
		String usecaseId = inputMap.getParameter("usecaseid").getAsString();

		Logger log = SkyLogger.getConnectorLogger();
		if (errorCode.equals("0") || errorCode.equals("")) {
			log.info("IF4.1CB: " + docId + " WSOK: ContactID-Parameter: " + contactid + "  usecaseId-Parameter: " + usecaseId + "  errorCode-Parameter: " + errorCode + " errorMessage-Parameter: " + errorMessage + " cor-Parameter: " + correlationId + " allparams:" + allparams);
			flowObject.put("errorcode", "0");
			flowObject.put("in.errorcode", "0");
		} else {
			flowObject.put("ErrorBean_GenerateError", Boolean.TRUE);
			log.error("IF4.1CB: " + docId + " WSERROR: ContactID-Parameter: " + contactid + "  usecaseId-Parameter: " + usecaseId + " errorCode-Parameter: " + errorCode + " errorMessage-Parameter: " + errorMessage + " cor-Parameter: " + correlationId + " allparams:" + allparams);
		}
	}

	
}
