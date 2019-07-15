package com.nttdata.de.ityx.mediatrix.businessrules.impl.client;

import de.ityx.mediatrix.api.interfaces.businessrules.client.IClientQuestionView;
import de.ityx.mediatrix.data.*;
import de.ityx.mediatrix.data.metainformation.MetaInformationInt;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;

public class ClientQuestionView implements IClientQuestionView {

	@Override
	public List<AbstractButton> getExtButtonList(Question arg0) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return null;
	}

	@Override
	public List<JComponent> getTabList(Question arg0) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return null;
	}

	@Override
	public boolean postAttachmentDelete(String arg0, boolean arg1,
			Question arg2, Attachment arg3) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return false;
	}

	@Override
	public boolean postAttachmentInsert(String arg0, boolean arg1,
			Question arg2, Attachment arg3) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return false;
	}

	@Override
	public boolean postAttachmentView(String arg0, boolean arg1, Question arg2,
			Attachment arg3) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return false;
	}

	@Override
	public void postKeywordDelete(String arg0, boolean arg1, Question arg2,
			Keyword arg3) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);

	}

	@Override
	public void postKeywordInsert(String arg0, boolean arg1, Question arg2,
			Answer arg3, Keyword arg4) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);

	}

	@Override
	public Object postMetaInformationView(String arg0, boolean arg1,
			Question arg2, MetaInformationInt arg3) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return null;
	}

	@Override
	public void postQuestionComplete(String arg0, boolean arg1, Question arg2) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);

	}

	@Override
	public void postQuestionForward(String arg0, boolean arg1, Question arg2,
			Subproject arg3, String arg4, int arg5) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);

	}

	@Override
	public boolean postQuestionMerge(Question arg0, Question arg1,
			boolean arg2, HashMap arg3) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return false;
	}

	@Override
	public void postQuestionProcessing(String arg0, boolean arg1, Question arg2) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);

	}

	@Override
	public void postQuestionRequeue(String arg0, boolean arg1, Question arg2,
			Subproject arg3, int arg4, long arg5) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);

	}

	@Override
	public void postQuestionStore(String arg0, boolean arg1, Question arg2) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);

	}

	@Override
	public void postQuestionView(String arg0, Question arg1, Case arg2) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);

	}

	@Override
	public boolean preAttachmentDelete(String arg0, boolean arg1,
			Question arg2, Attachment arg3) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return false;
	}

	@Override
	public boolean preAttachmentInsert(String arg0, boolean arg1,
			Question arg2, Attachment arg3) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return false;
	}

	@Override
	public boolean preAttachmentView(String arg0, boolean arg1, Question arg2,
			Attachment arg3) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return false;
	}

	@Override
	public boolean preKeywordDelete(String arg0, boolean arg1, Question arg2,
			Keyword arg3) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return false;
	}

	@Override
	public boolean preKeywordInsert(String arg0, boolean arg1, Question arg2,
			Answer arg3, Keyword arg4) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return false;
	}

	@Override
	public boolean preMetaInformationView(String arg0, boolean arg1,
			Question arg2, MetaInformationInt arg3) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return false;
	}

	@Override
	public boolean preQuestionComplete(String arg0, boolean arg1, Question arg2) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return false;
	}

	@Override
	public boolean preQuestionForward(String arg0, boolean arg1, Question arg2,
			Subproject arg3, String arg4, int arg5) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return false;
	}

	@Override
	public boolean preQuestionMerge(Question arg0, Question arg1, boolean arg2,
			HashMap arg3) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return false;
	}

	@Override
	public boolean preQuestionProcessing(String arg0, boolean arg1,
			Question arg2) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return false;
	}

	@Override
	public boolean preQuestionRequeue(String arg0, boolean arg1, Question arg2,
			Subproject arg3, int arg4, long arg5) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return false;
	}

	@Override
	public boolean preQuestionStore(String arg0, boolean arg1, Question arg2) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return false;
	}

	@Override
	public boolean preQuestionView(String arg0, Question arg1, Case arg2) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return false;
	}

}
