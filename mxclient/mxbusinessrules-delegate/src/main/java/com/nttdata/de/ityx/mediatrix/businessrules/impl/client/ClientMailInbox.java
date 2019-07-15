package com.nttdata.de.ityx.mediatrix.businessrules.impl.client;

import com.nttdata.de.lib.logging.SkyLogger;
import de.ityx.mediatrix.api.interfaces.businessrules.client.IClientMailInbox;
import de.ityx.mediatrix.data.*;
import de.ityx.mediatrix.data.businessrules.client.QuestionOpenVeto;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientMailInbox implements IClientMailInbox {

	@Override
	public List<JMenuItem> getExtendedMenuItems(List<SingleMode> arg0,
			Map<Object, Object> arg1) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		
		SkyLogger.getClientLogger().error(logPrefix+ "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));
		return null;
	}

	@Override
	public void postQuestionComplete(String arg0, Question arg1) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getClientLogger().error(logPrefix+ "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));
	}

	@Override
	public void postQuestionForward(String arg0, Question arg1,
			Subproject arg2, String arg3, int arg4) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getClientLogger().error(logPrefix+ "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));

	}

	@Override
	public boolean postQuestionMerge(Question arg0, Question arg1,
			boolean arg2, HashMap arg3) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getClientLogger().error(logPrefix+ "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));
		return false;
	}

	@Override
	public void postQuestionRequeue(String arg0, Question arg1,
			Subproject arg2, int arg3, long arg4) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getClientLogger().error(logPrefix+ "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));
	}

	@Override
	public boolean preQuestionComplete(String arg0, Question arg1) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getClientLogger().error(logPrefix+ "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));
		return false;
	}

	@Override
	public boolean preQuestionForward(String arg0, Question arg1,
			Subproject arg2, String arg3, int arg4) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getClientLogger().error(logPrefix+ "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));
		return false;
	}

	@Override
	public boolean preQuestionMailPreview(String arg0, Question arg1,
			Subproject arg2) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getClientLogger().error(logPrefix+ "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));
		return false;
	}

	@Override
	public boolean preQuestionMerge(Question arg0, Question arg1, boolean arg2,
			HashMap arg3) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getClientLogger().error(logPrefix+ "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));
		return false;
	}

	@Override
	public void preQuestionOpen(Question arg0, Answer arg1, Customer arg2)
			throws QuestionOpenVeto {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getClientLogger().error(logPrefix+ "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));

	}

	@Override
	public boolean preQuestionRequeue(String arg0, Question arg1,
			Subproject arg2, int arg3, long arg4) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getClientLogger().error(logPrefix+ "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));
		return false;
	}

}
