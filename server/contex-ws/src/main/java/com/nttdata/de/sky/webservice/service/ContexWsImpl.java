package com.nttdata.de.sky.webservice.service;

import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.webservice.ProcessExecutor;
import de.ityx.contex.webservice.schema.ContexRequest;
import de.ityx.contex.webservice.schema.ContexResponse;
import de.ityx.contex.webservice.schema.Entry;
import de.ityx.contex.webservice.service.ContexErrorMessage;
import de.ityx.contex.webservice.service.ContexWs;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import java.util.List;

@WebService(name="ContexWs",
		serviceName = "ContexWsService",
		portName = "ContexWsPort",
		targetNamespace = "http://webservice.contex.ityx.de/service",
		wsdlLocation = "WEB-INF/wsdl/contex-ws.wsdl",
		endpointInterface = "de.ityx.contex.webservice.service.ContexWs")
public class ContexWsImpl implements ContexWs {

	@WebMethod
	@Override
	@WebResult(name = "ContexResponse", targetNamespace = "http://webservice.contex.ityx.de/schema", partName = "response")
	public ContexResponse runProcess( @WebParam(name = "ContexRequest", targetNamespace = "http://webservice.contex.ityx.de/schema", partName = "request") ContexRequest request) throws ContexErrorMessage {
		try {
			List<Entry> entries = request.getEntries();
			String reqEntries="";
			for (Entry e:entries){
				reqEntries+=e.getKey()+":"+e.getValue()+";";
			}

			SkyLogger.getConnectorLogger().info("IF_CXWS: START WS-Call Process:" + request.getMaster() + "." + request.getProcessName() + ":"+reqEntries );
			ContexResponse cr= ProcessExecutor.getInstance().runProcess(request.getMaster(), request.getProcessName(), request.getEntries());

			String retEntries="";
			List<Entry> lb=cr.getReturn();
			for (Entry e:lb){
				retEntries+=e.getKey()+":"+e.getValue()+";";
			}
			SkyLogger.getConnectorLogger().info("IF_CXWS: END WS-Call Process:" + request.getMaster() + "." + request.getProcessName()  + ":"+reqEntries +" >"+retEntries );
			return cr;
		} catch (ContexErrorMessage e) {
			SkyLogger.getConnectorLogger().error("IF_CXWS: Unable to run Process:" + request.getMaster() + "." + request.getProcessName() +">"+request+"<"+request.getEntries() +" msg:"+ e.getMessage());
			throw e;
		}
	}
}
