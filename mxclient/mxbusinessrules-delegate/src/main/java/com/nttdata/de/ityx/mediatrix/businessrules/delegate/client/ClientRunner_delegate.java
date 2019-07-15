package com.nttdata.de.ityx.mediatrix.businessrules.delegate.client;

import com.nttdata.de.ityx.mediatrix.businessrules.factories.NTTDataBusinessruleFactoryGenerator;
import de.ityx.mediatrix.api.interfaces.IViewFilter;
import de.ityx.mediatrix.api.interfaces.businessrules.client.IClientRunner;
import de.ityx.mediatrix.data.Question;
import de.ityx.mediatrix.data.SingleMode;

import javax.swing.*;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

public class ClientRunner_delegate implements IClientRunner {

	IClientRunner delegate = NTTDataBusinessruleFactoryGenerator.createFactory(false).getClientRunner();

	@Override
	public HashMap checkViewFilter(JPanel arg0, IViewFilter arg1, boolean arg2, HashMap arg3) {
		return delegate.checkViewFilter(arg0, arg1, arg2, arg3);
	}

	@Override
	public void forwardServlet(String arg0, boolean arg1, Question arg2, HashMap arg3) {
		delegate.forwardServlet(arg0, arg1, arg2, arg3);
	}

	@Override
	public HashMap getParameter(HashMap arg0) {
		return delegate.getParameter(arg0);
	}

	@Override
	public HashMap<String, Object> getSystemLogin(HashMap arg0) {
		return delegate.getSystemLogin(arg0);
	}

	@Override
	public HashMap<String, Object> matchFoundFilter(SingleMode arg0, List<IViewFilter> arg1, HashMap arg2) {
		return delegate.matchFoundFilter(arg0, arg1, arg2);
	}

	@Override
	public HashMap<String, Object> matchNotFoundFilter(SingleMode arg0, List<IViewFilter> arg1, HashMap arg2) {
		return delegate.matchNotFoundFilter(arg0, arg1, arg2);
	}

	@Override
	public void onShutDown() {
		delegate.onShutDown();
	}

	@Override
	public void onStartup() {
		delegate.onStartup();
	}

	@Override
	public void postPingSend(Hashtable arg0) {
		delegate.postPingSend(arg0);
	}

	@Override
	public boolean prePingSend(HashMap arg0) {
		return delegate.prePingSend(arg0);
	}

	@Override
	public HashMap setParameter(HashMap arg0) {
		return delegate.setParameter(arg0);
	}

	@Override
	public HashMap setViewFilterComponent(JPanel arg0, HashMap arg1) {
		return delegate.setViewFilterComponent(arg0, arg1);
	}

}
