package com.nttdata.de.ityx.mediatrix.businessrules.delegate.client;

import de.ityx.mediatrix.api.interfaces.businessrules.client.IClientSystem;
import de.ityx.mediatrix.data.CheckList;
import de.ityx.mediatrix.data.Question;

import java.util.HashMap;
import java.util.List;

public class ClientSystem_delegate implements IClientSystem {

	IClientSystem delegate = com.nttdata.de.ityx.mediatrix.businessrules.factories.NTTDataBusinessruleFactoryGenerator.createFactory(false).getClientSystem();
	
	public HashMap setKeywords(Question p0, List<de.ityx.mediatrix.data.Keyword> p1, int p2, HashMap<String, Object> p3) {
		return delegate.setKeywords(p0, p1, p2, p3);
	}

	public void OnShutDown() {
		delegate.OnShutDown();
	}

	public void OnStartUp() {
		delegate.OnStartUp();
	}

	public void actionCheckList(CheckList p0, Question p1, boolean p2, HashMap p3) {
		delegate.actionCheckList(p0, p1, p2, p3);
	}

	public void changeAddresses(Object p0) {
		delegate.changeAddresses(p0);
	}

	public Object postClientExchange(String p0, String p1, int p2, String p3, Object p4, Integer p5, Integer p6, Object p7) {
		return delegate.postClientExchange(p0, p1, p2, p3, p4, p5, p6, p7);
	}

	public Object preClientExchange(String category, String command, int id, String oper, Object parameter, Integer operatorId, Integer integer) {
		return delegate.preClientExchange(category, command, id, oper, parameter, operatorId, integer);
	}
}
