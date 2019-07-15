package com.nttdata.de.sky.ityx.mediatrix.businessrules.server.sky.actions;

import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.Actions;
import com.nttdata.de.sky.ityx.common.DocIdGenerator;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;

import java.sql.Connection;
import java.util.List;

/**
 * Created by meinusch on 13.04.15.
 */
public class DocIDGeneratorAction extends AServerEventAction {
	@Override
	public List actionPerformed(Connection con, String actionname, List parameters) throws Exception {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ": ";
		TagMatchDefinitions.DocumentDirection doctype = (TagMatchDefinitions.DocumentDirection) parameters.get(0);
		TagMatchDefinitions.Channel channel = (TagMatchDefinitions.Channel) parameters.get(1);
		String parentDocID = null;
		if (parameters.size() > 2) {
			Object param = parameters.get(2);
			if (param.getClass().equals(String.class))
				parentDocID = (String) param;
		}
		SkyLogger.getMediatrixLogger().info(logPrefix + "generate DocID for:" + doctype.name() + ":" + channel.name() + ":" + parentDocID);

		parameters.clear();
		String uniqueDocId = null;
		try {
			 uniqueDocId = DocIdGenerator.createUniqueDocumentId(con, doctype, channel, parentDocID);
		} catch (Exception e){
			SkyLogger.getMediatrixLogger().error(logPrefix +"Error during generation of DocID for:"+parentDocID+ " for:"+doctype.name()+ ":"+channel.name()+":"+parentDocID+" msg:"+e.getMessage(),e);
			throw e;
		}
		parameters.add(uniqueDocId);
		SkyLogger.getMediatrixLogger().info(logPrefix +"generated "+uniqueDocId+ " for:"+doctype.name()+ ":"+channel.name()+":"+parentDocID);
		return parameters;
	}

	@Override
	public String[] getActionNames() {
		return new String[]{Actions.ACTION_CREATE_DOCUMENT_ID.name()};
	}
}
