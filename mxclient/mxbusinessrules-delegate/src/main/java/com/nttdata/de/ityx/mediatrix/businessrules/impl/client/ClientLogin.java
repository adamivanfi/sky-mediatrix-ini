package com.nttdata.de.ityx.mediatrix.businessrules.impl.client;

import de.ityx.mediatrix.api.interfaces.businessrules.client.IClientLogin;

public class ClientLogin implements IClientLogin {
	@Override
	public boolean preLogin(String operator) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return true;
	}

	@Override
	public int preLogout(String operator) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return 0;
	}

	@Override
	public void postLogout(String operator) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
	}

	@Override
	public void postLogin(String arg0) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
	}

}
