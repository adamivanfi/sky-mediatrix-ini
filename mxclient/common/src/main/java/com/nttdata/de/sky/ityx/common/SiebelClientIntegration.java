package com.nttdata.de.sky.ityx.common;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.nttdata.de.lib.logging.SkyLogger;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;


public class SiebelClientIntegration {
	
	public static void main(String[] args) {
		SiebelClientIntegration.changeViewWithCom(args[0]);
	}
	
	public static boolean changeViewWithCom(String customerId) {
		
		if (customerId == null || "".equals(customerId)) {
			SkyLogger.getClientLogger().warn("Skipping invocation of Siebel interface. Not a valid row number: "+customerId);
			return false;
		}
		
		try {
			//
			// This code has been tested on Citrix (with fixed rowId 1-AYKG1)
			//
			SkyLogger.getClientLogger().debug("Getting COM object");
			ActiveXComponent siebApp = new ActiveXComponent("Siebel.Desktop_Integration_Application.1");

			// Input properties
			SkyLogger.getClientLogger().debug("Create and set input properties");
			Dispatch inProp = siebApp.invoke("NewPropertySet").toDispatch();
			Dispatch.call(inProp, "SetProperty", "CustomerId", customerId);

			// Output properties
			SkyLogger.getClientLogger().debug("Create output properties");
			Dispatch outProp = siebApp.invoke("NewPropertySet").toDispatch();

			// siebSvcs
			SkyLogger.getClientLogger().debug("GetService SKYDE DMS Integration BS");
			Dispatch siebSvcs = Dispatch.call(siebApp, "GetService", "SKYDE DMS Integration BS").toDispatch();

			SkyLogger.getClientLogger().info("Invoking BringToTop");
			Dispatch.call(siebSvcs, "InvokeMethod", "BringToTop", inProp, outProp);

			SkyLogger.getClientLogger().info("Invoking GotoKundenPortal for customer "+customerId);
			Dispatch.call(siebSvcs, "InvokeMethod", "GotoKundenPortal", inProp, outProp);		
			//
			// END :::: This code has been tested on Citrix
			//
		} catch (Throwable t) {
			SkyLogger.getClientLogger().error("There was an exception while invoking the Siebel COM interface", t);
			return false;
		}
		
		return true;
	}
	
	public static boolean changeViewWithScript(String customerId, String env) throws IOException {
		
		if (customerId == null || "".equals(customerId)) {
			SkyLogger.getClientLogger().warn("Skipping invocation of Siebel interface. Not a valid row number: "+customerId);
			return false;
		}
		
		String script = "siebel81.vbs";
		try {
			String tmpScript = SystemWrapper.getTempDir("ITyX-Connector", "siebel") + File.separator + script;

			SkyLogger.getClientLogger().info("Extract and execute script: "+tmpScript+" for customer "+customerId+" env:"+env);
			URL url=SiebelClientIntegration.class.getClassLoader().getResource(script);
			if (url!=null) {
				FileUtils.copyURLToFile(url, new File(tmpScript));
				ScriptRunner runner = new ScriptRunner();
				StringBuffer b = runner.execute(-1, true, tmpScript, customerId, env);

				SkyLogger.getClientLogger().debug("ExitValue: " + runner.getExitValue());
				if (b != null) {
					SkyLogger.getClientLogger().debug(b.toString());
				}
			}else {
				SkyLogger.getClientLogger().debug("Siebel Script not found");
				return false;
			}
		} catch (Exception e) {
			SkyLogger.getClientLogger().error("Unable to trigger siebel: "+e);
			return false;
		}
		return true;
	}
	
}
