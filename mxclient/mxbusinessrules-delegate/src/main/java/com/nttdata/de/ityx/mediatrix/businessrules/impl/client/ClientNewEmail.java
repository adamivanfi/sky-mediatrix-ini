package com.nttdata.de.ityx.mediatrix.businessrules.impl.client;

import de.ityx.mediatrix.api.interfaces.businessrules.client.IClientNewEmail;
import de.ityx.mediatrix.api.interfaces.textobjects.ITextObject;
import de.ityx.mediatrix.data.Attachment;
import de.ityx.mediatrix.data.Operator;
import de.ityx.mediatrix.data.Question;
import de.ityx.mediatrix.data.Subproject;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientNewEmail implements IClientNewEmail {

	@Override
	public Map getIdentityExtension(Operator arg0, Question arg1, int arg2,
			Map arg3) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return null;
	}

	@Override
	public List<JMenu> getMenuButtons(Operator arg0, Question arg1, int arg2,
			Map arg3) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return null;
	}

	@Override
	public Map getReceiverExtension(Operator arg0, Question arg1, int arg2,
			Map arg3) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return null;
	}

	@Override
	public Map getSubjectExtension(Operator arg0, Question arg1, int arg2,
			Map arg3) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return null;
	}

	@Override
	public List<JComponent> getTabList(Question arg0, HashMap arg1) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return null;
	}

	@Override
	public List<JButton> getToolbarButtons(Operator arg0, Question arg1,
			int arg2, Map arg3) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return null;
	}

	@Override
	public boolean postAttachmentInsert(String arg0, boolean arg1,
			Question arg2, Attachment arg3, HashMap arg4) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return false;
	}

	@Override
	public void postQuestionSend(String arg0, boolean arg1, Question arg2,
			HashMap arg3) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);

	}

	@Override
	public void postTextObjectInsert(String arg0, boolean arg1, Question arg2,
			ITextObject arg3, HashMap arg4) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);

	}

	@Override
	public boolean preAttachmentInsert(String arg0, boolean arg1,
			Question arg2, Attachment arg3, HashMap arg4) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return false;
	}

	@Override
	public boolean preQuestionSend(String arg0, boolean arg1, Question arg2,
			HashMap arg3) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return false;
	}

	@Override
	public boolean preTextObjectInsert(String arg0, boolean arg1,
			Question arg2, ITextObject arg3, HashMap arg4) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return false;
	}

	@Override
	public void subprojectChanged(Subproject arg0, HashMap arg1) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);

	}

}
