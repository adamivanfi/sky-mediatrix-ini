package com.nttdata.de.ityx.cx.sky.importChannel;

import de.ityx.contex.interfaces.designer.IFlowObject;

public class FaxMetadataBeanBackup extends FaxMetadataBean {

	@Override
	public String getImportBase(IFlowObject flowObject){
		return (String) flowObject.get("importFaxBackup");
	}
}
