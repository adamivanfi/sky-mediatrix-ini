package com.nttdata.de.ityx.mediatrix.businessrules.impl.client;

import de.ityx.mediatrix.api.interfaces.businessrules.client.IClientAnswerEdit;
import de.ityx.mediatrix.api.interfaces.gui.BaseAnswerEditorPanel;
import de.ityx.mediatrix.api.interfaces.textobjects.ITextObject;
import de.ityx.mediatrix.data.Answer;
import de.ityx.mediatrix.data.Attachment;

import javax.swing.*;
import java.util.List;

public class ClientAnswerEdit implements IClientAnswerEdit {

    @Override
    public List<AbstractButton> getExtButtonList(BaseAnswerEditorPanel editor, Answer answer) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
        return null;
    }

    @Override
    public List<JComponent> getTabList(BaseAnswerEditorPanel editor, Answer answer) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
        return null;
    }

    @Override
    public boolean preAnswerEdit(BaseAnswerEditorPanel editor, Answer answer) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
        return true;
    }

    @Override
    public boolean preHistoryInsert(BaseAnswerEditorPanel editor, Answer answer, Attachment attachment) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
        return true;
    }

    @Override
    public boolean postHistoryInsert(BaseAnswerEditorPanel editor, Answer answer, Attachment attachment) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
        return true;
    }

    @Override
    public boolean preAttachmentDelete(BaseAnswerEditorPanel editor, Answer answer, Attachment attachment) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
        return true;
    }

    @Override
    public boolean postAttachmentDelete(BaseAnswerEditorPanel editor, Answer answer, Attachment attachment) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
        return true;
    }

    @Override
    public boolean preAttachmentInsert(BaseAnswerEditorPanel editor, Answer answer, Attachment attachment) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
        return true;
    }

    @Override
    public boolean postAttachmentInsert(BaseAnswerEditorPanel editor, Answer answer, Attachment attachment) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
        return true;
    }

    @Override
    public boolean preAttachmentView(BaseAnswerEditorPanel editor, Answer answer, Attachment attachment) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
        return true;
    }

    @Override
    public boolean postAttachmentView(BaseAnswerEditorPanel editor, Answer answer, Attachment attachment) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
        return true;
    }

    @Override
    public boolean preTextObjectInsert(BaseAnswerEditorPanel editor, Answer answer, ITextObject tb) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
        return true;
    }

    @Override
    public void postTextObjectInsert(BaseAnswerEditorPanel editor, Answer answer, ITextObject tb) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
    }

	@Override
	public void postAnswerEdit(BaseAnswerEditorPanel arg0, Answer arg1) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		System.err.println(logPrefix);
	}

}
