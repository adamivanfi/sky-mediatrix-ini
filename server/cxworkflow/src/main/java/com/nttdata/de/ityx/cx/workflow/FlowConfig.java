package com.nttdata.de.ityx.cx.workflow;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;
import com.nttdata.de.lib.logging.SkyLogger;
import de.ityx.contex.interfaces.designer.IFlowObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * Helper to load all Configurationproperties used from Contex-Designer-Stepps with notation ###prop####
 *
 * Created by meinusch on 17.02.15.
 */
public class FlowConfig extends AbstractWflBean {


	
	private static ResourceBundle flowConfig ;
	private static final String wflconffile = System.getProperty("ityx.conf", "c:/mediatrix/conf") + "/" + "WflFlowConfig.properties";
	private static FlowConfig singleton;
	
	private FlowConfig(){
		String wflfile=System.getProperty("ityx.conf", "c:/mediatrix/conf") + "/" + "WflFlowConfig.properties";
		try {
			SkyLogger.getItyxLogger().debug("Loading WflFlowConfig.properties " + wflfile);
			InputStream is =new FileInputStream(new File(wflfile));
			SkyLogger.getItyxLogger().debug("Loaded WflFlowConfig.properties IS");
			flowConfig = new PropertyResourceBundle(is);
		} catch (IOException e) {
			SkyLogger.getWflLogger().error("Unable to load WflFlow.properties");
			flowConfig=null;
		}
	}
	
	public void execute(IFlowObject flowObject) throws Exception {
		if (singleton==null){
			singleton=new FlowConfig();
		}
		if (flowConfig == null){
			singleton=null;
			throw new Exception("Unable to read WflFlow.properties");
		}
		for (String entry : flowConfig.keySet()) {
			flowObject.put(entry, flowConfig.getString(entry));
		}
	}
}
