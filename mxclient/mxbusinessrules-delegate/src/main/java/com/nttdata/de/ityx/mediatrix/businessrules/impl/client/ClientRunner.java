package com.nttdata.de.ityx.mediatrix.businessrules.impl.client;

import de.ityx.mediatrix.api.interfaces.IViewFilter;
import de.ityx.mediatrix.api.interfaces.businessrules.client.IClientRunner;
import de.ityx.mediatrix.data.Question;
import de.ityx.mediatrix.data.SingleMode;

import javax.swing.*;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

public class ClientRunner implements IClientRunner {

	@Override
	public HashMap checkViewFilter(JPanel arg0, IViewFilter arg1, boolean arg2,
			HashMap arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void forwardServlet(String arg0, boolean arg1, Question arg2,
			HashMap arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public HashMap getParameter(HashMap arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, Object> getSystemLogin(HashMap arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, Object> matchFoundFilter(SingleMode arg0,
			List<IViewFilter> arg1, HashMap arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, Object> matchNotFoundFilter(SingleMode arg0,
			List<IViewFilter> arg1, HashMap arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onShutDown() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStartup() {
		// TODO Auto-generated method stub

	}

	@Override
	public void postPingSend(Hashtable arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean prePingSend(HashMap arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public HashMap setParameter(HashMap arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap setViewFilterComponent(JPanel arg0, HashMap arg1) {
		// TODO Auto-generated method stub
		return null;
	}

}
