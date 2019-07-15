package com.nttdata.de.ityx.mediatrix.businessrules.delegate.client;

import de.ityx.mediatrix.api.interfaces.businessrules.client.IClientLogin;

public class ClientLogin_delegate implements IClientLogin {

	IClientLogin delegate = com.nttdata.de.ityx.mediatrix.businessrules.factories.NTTDataBusinessruleFactoryGenerator.createFactory(false).getClientLogin();

	@Override
	public boolean preLogin(String operator) {
		return delegate.preLogin(operator);
	}

	@Override
	public int preLogout(String operator) {
		return delegate.preLogout(operator);
	}

	@Override
	public void postLogout(String operator) {
		delegate.postLogout(operator);
	}

	@Override
	public void postLogin(String arg0) {
		delegate.postLogin(arg0);
	}

}
