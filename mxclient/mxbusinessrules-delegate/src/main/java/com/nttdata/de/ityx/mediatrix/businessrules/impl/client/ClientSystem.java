package com.nttdata.de.ityx.mediatrix.businessrules.impl.client;

import de.ityx.mediatrix.api.interfaces.businessrules.client.IClientSystem;
import de.ityx.mediatrix.data.CheckList;
import de.ityx.mediatrix.data.Question;

import java.util.HashMap;
import java.util.List;

public class ClientSystem implements IClientSystem {

	public HashMap setKeywords(Question p0,
			List<de.ityx.mediatrix.data.Keyword> p1, int p2, HashMap<String,Object> p3) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix + ": enter");
		return new HashMap();
	}

	public void OnShutDown() {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix + ": enter");
	}

	public void OnStartUp() {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix + ": enter");
	}

	public void actionCheckList(CheckList p0, Question p1, boolean p2,
			HashMap p3) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix + ": enter");
	}

	public void changeAddresses(Object p0) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix + ": enter");
	}

	public Object postClientExchange(String p0, String p1, int p2, String p3,
			Object p4, Integer p5, Integer p6, Object p7) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix + ": enter");
		return null;
	}

	public Object preClientExchange(String category, String command, int id,
			String oper, Object parameter, Integer operatorId, Integer integer) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix + ": enter");
		return null;
	}
}
