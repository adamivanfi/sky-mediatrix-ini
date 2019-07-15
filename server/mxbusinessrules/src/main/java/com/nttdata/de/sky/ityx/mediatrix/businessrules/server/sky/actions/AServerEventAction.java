package com.nttdata.de.sky.ityx.mediatrix.businessrules.server.sky.actions;

public abstract class AServerEventAction implements IServerEventAction {

	public boolean isResponsibleFor(String actionname){
		for (String itaction: getActionNames()){
			if (actionname!=null && actionname.equalsIgnoreCase(itaction)){
				return true;
			}
		}
		return false;
	}
}
