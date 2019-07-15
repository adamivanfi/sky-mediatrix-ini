package com.nttdata.de.ityx.cx.sky.connector.siebel;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;
import com.nttdata.de.lib.logging.SkyLogger;
import de.ityx.contex.interfaces.designer.IFlowObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ExtractFromFileBean extends AbstractWflBean {

	private static final long serialVersionUID = -1611432020723970778L;

	public void execute(IFlowObject flowObject) {
		String uri = (String)flowObject.get("txt.uri");
			
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(uri));
			
			String line = in.readLine();
			while (line != null) {
				String[] strs = line.split("=");
				if (strs.length == 2) {
					flowObject.put("callback_"+strs[0], strs[1]);
				}
				
				line = in.readLine();
			}
			
		} catch (IOException e) {
			SkyLogger.getConnectorLogger().error("IF4.1cb: " + e.getMessage(), e);
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					SkyLogger.getConnectorLogger().error("IF4.1cb: " + e.getMessage(), e);
				}
		}
	}


}
