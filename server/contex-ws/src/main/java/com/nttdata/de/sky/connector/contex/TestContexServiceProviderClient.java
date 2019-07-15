package com.nttdata.de.sky.connector.contex;

import com.nttdata.de.lib.logging.SkyLogger;
import de.ityx.contex.webservice.schema.ContexRequest;
import de.ityx.contex.webservice.schema.ContexResponse;
import de.ityx.contex.webservice.schema.Entry;
import de.ityx.contex.webservice.service.ContexWs;
import de.ityx.contex.webservice.service.ContexWsService;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import java.net.URL;
import java.util.List;

public class TestContexServiceProviderClient {

	public static void main(String[] args) throws Exception {

		if (args.length < 3) {
			SkyLogger.getConnectorLogger().info("Expected parameters: <ProviderURL> <Master> <ProcessName> <key>=<value>");
			SkyLogger.getConnectorLogger().info("Example: http://localhost:8080/contex-ws/ContexWs?wsdl sky contex-ws-example inputparam=value");
		}
		URL wsdlURL = ClassLoader.getSystemResource("classpath:wsdl/contexws/contex-ws.wsdl");
		SkyLogger.getConnectorLogger().info("Using WSDL at " + wsdlURL);
		String endpoint = args[0];
		String master = args[1];
		String processName = args[2];

		SkyLogger.getConnectorLogger().info("Calling at master " + master + " process " + processName + ". Host: " + endpoint);

		ContexWsService service = new ContexWsService(wsdlURL, new QName("http://webservice.contex.ityx.de/service", "ContexWsService"));
		ContexWs port = service.getPort(ContexWs.class);

		SkyLogger.getConnectorLogger().info("Using endpoint: " + endpoint);
		BindingProvider provider = (BindingProvider) port;
		provider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);

		ContexRequest payload = new ContexRequest();
		payload.setMaster(master); //Mastername
		payload.setProcessName(processName); //Prozessname

		//Es können beliebig viele Entries übergeben werden
		Entry e;

		for (int h1 = 3; h1 < args.length; h1++) {
			String[] kv = args[h1].split("=");
			if (kv.length == 2) {
				e = new Entry();
				e.setKey(kv[0]);
				e.setValue(kv[1]);
				payload.getEntries().add(e);
			}
		}

		ContexResponse runProcess = port.runProcess(payload);
		//ContexResponse enthält eine Liste mit den Outputparametern
		List<Entry> list = runProcess.getReturn();
		SkyLogger.getConnectorLogger().info("Received result from Contex process!");
		for (Entry entry : list) {
			SkyLogger.getConnectorLogger().info("Output-Parameter: " + entry.getKey() + " : " + entry.getValue());
		}
	}

}
