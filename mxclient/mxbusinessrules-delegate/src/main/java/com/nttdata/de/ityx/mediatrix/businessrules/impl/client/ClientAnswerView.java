package com.nttdata.de.ityx.mediatrix.businessrules.impl.client;

import de.ityx.mediatrix.api.interfaces.businessrules.client.IClientAnswerView;
import de.ityx.mediatrix.api.interfaces.textobjects.ITextObject;
import de.ityx.mediatrix.data.Answer;
import de.ityx.mediatrix.data.Attachment;
import de.ityx.mediatrix.data.Case;
import de.ityx.mediatrix.data.Question;
import de.ityx.mediatrix.data.metainformation.MetaInformationInt;

import javax.swing.*;
import java.util.List;

public class ClientAnswerView implements IClientAnswerView {

    @Override
    public void postAnswerSend(String loginname, boolean isOperatorMode, Question question, Answer answer) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
    }

    @Override
    public List<AbstractButton> getExtButtonList(Answer answer) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
        return null;
    }

    @Override
    public List<JComponent> getTabList(Question question, Answer answer) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
        return null;
    }

    @Override
    public boolean preAttachmentView(String loginname, boolean isOperatorMode, Question question, Answer answer, Attachment attachment) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
        return true;
    }

    @Override
    public boolean postAttachmentView(String loginname, boolean isOperatorMode, Question question, Answer answer, Attachment attachment) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
        return true;
    }

    @Override
    public boolean preAttachmentInsert(String loginname, boolean isOperatorMode, Question question, Answer answer, Attachment attachment) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
        return true;
    }

    @Override
    public boolean postAttachmentInsert(String loginname, boolean isOperatorMode, Question question, Answer answer, Attachment attachment) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
        return true;
    }

    @Override
    public boolean preHistoryInsert(String loginname, boolean isOperatorMode, Question question, Answer answer, Attachment attachment) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
        return true;
    }

    @Override
    public boolean postHistoryInsert(String loginname, boolean isOperatorMode, Question question, Answer answer, Attachment attachment) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
        return true;
    }

    @Override
    public boolean preAttachmentDelete(String loginname, boolean isOperatorMode, Question question, Answer answer, Attachment attachment) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
        return true;
    }

    @Override
    public boolean postAttachmentDelete(String loginname, boolean isOperatorMode, Question question, Answer answer, Attachment attachment) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
        return true;
    }

    @Override
    public boolean preTextObjectInsert(String loginname, boolean isOperatorMode, Question question, Answer answer, ITextObject tb) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
        return true;
    }

    @Override
    public void postTextObjectInsert(String loginname, boolean isOperatorMode, Question question, Answer answer, ITextObject tb) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
    }

    @Override
    public void postMetaInformationView(String login, boolean operatorMode, Answer answer, MetaInformationInt metaInfo) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
    }

    @Override
    public boolean preMetaInformationView(String login, boolean operatorMode, Answer answer, MetaInformationInt metaInfo) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
        return true;
    }

	@Override
	public void postAnswerView(String arg0, Question arg1, Answer arg2,
			Case arg3) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
	}

	@Override
	public boolean preAnswerSend(String arg0, boolean arg1, Question arg2,
			Answer arg3) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return true;
	}

	@Override
	public boolean preAnswerView(String arg0, Question arg1, Answer arg2,
			Case arg3) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
		return true;
	}

}
