package com.nttdata.de.sky.ityx.common;

import com.nttdata.de.lib.logging.SkyLogger;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;


public class Prem2ClientIntegration {

	public static boolean changeViewWithScript(String customerId, String env) throws IOException {
		if (customerId == null || "".equals(customerId)) {
			SkyLogger.getClientLogger().warn("Skipping invocation of Siebel interface. Not a valid row number: "+customerId);
			return false;
		}

		String script = "prem2.vbs";
		try {
			String tmpScript = SystemWrapper.getTempDir("ITyX-Connector", "prem2") + File.separator + script;

			SkyLogger.getClientLogger().info("Extract and execute script: "+tmpScript+" for customer "+customerId+" env:"+env);
			URL url=Prem2ClientIntegration.class.getClassLoader().getResource(script);
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
