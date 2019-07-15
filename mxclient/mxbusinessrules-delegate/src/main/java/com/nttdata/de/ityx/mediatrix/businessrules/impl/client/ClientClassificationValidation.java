package com.nttdata.de.ityx.mediatrix.businessrules.impl.client;

import com.nttdata.de.lib.logging.SkyLogger;
import de.ityx.contex.interfaces.document.CDocument;
import de.ityx.contex.interfaces.extag.TagMatch;
import de.ityx.mediatrix.api.interfaces.businessrules.client.IClientClassificationValidation;
import de.ityx.mediatrix.data.Question;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ClientClassificationValidation implements IClientClassificationValidation {

	/*
	 * Will be called after the user has pressed the cancel button and the
	 * document has been released
	 */
	public void postCancel(String loginName, boolean isOperator, de.ityx.contex.impl.document.CDocumentContainer<? extends de.ityx.contex.interfaces.document.CDocument> cdoc, Map<java.lang.Object, java.lang.Object> map) {
		SkyLogger.getClientLogger().error(getClass().getName() + "#" + (new Object() {
		}.getClass().getEnclosingMethod().getName()) + "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));
	}

	/*
	 * Will be called after the user has pressed the cancel button before the
	 * document will be released
	 */
	public boolean preCancel(String loginName, boolean isOperator, de.ityx.contex.impl.document.CDocumentContainer<? extends de.ityx.contex.interfaces.document.CDocument> cdoc, Map<java.lang.Object, java.lang.Object> map) {
		SkyLogger.getClientLogger().error(getClass().getName() + "#" + (new Object() {
		}.getClass().getEnclosingMethod().getName()) + "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));
		return true;
	}

	/*
	 * Returns a list of additional buttons to be added to the toolbar.
	 */
	public List<javax.swing.AbstractButton> getExtButtonList(de.ityx.contex.impl.document.CDocumentContainer<? extends de.ityx.contex.interfaces.document.CDocument> cdoc) {
		SkyLogger.getClientLogger().error(getClass().getName() + "#" + (new Object() {
		}.getClass().getEnclosingMethod().getName()) + "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));

		return Collections.emptyList();
	}

	/*
	 * Returns the qualified name of the class implementing the lower panel in
	 * the validationstation. null oder empty means using the default
	 * implementation.
	 */
	public String getFormtypePanelImplementor(int subprojectId) {
		SkyLogger.getClientLogger().error(getClass().getName() + "#" + (new Object() {
		}.getClass().getEnclosingMethod().getName()) + "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));

		return null;
	}

	/*
	 * Will be called after the type of a document has been changed
	 */
	public void postChangeTagmatch(CDocument formular, String formtype, Map<java.lang.Object, java.lang.Object> map) {
		SkyLogger.getClientLogger().error(getClass().getName() + "#" + (new Object() {
		}.getClass().getEnclosingMethod().getName()) + "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));

	}

	/*
	 * Will be called after a page is moved from a document to another
	 */
	public <T extends de.ityx.contex.interfaces.document.CDocument> void postPageMove(de.ityx.contex.impl.document.CDocumentContainer<T> cdoc, T oldFormular, de.ityx.contex.interfaces.document.CPage newFormular, Map<java.lang.Object, java.lang.Object> pageToMove) {
		SkyLogger.getClientLogger().error(getClass().getName() + "#" + (new Object() {
		}.getClass().getEnclosingMethod().getName()) + "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));

	}

	/*
	 * Will be called after a page is added to a document
	 */
	public void postPageToDocument(CDocument formular, de.ityx.contex.interfaces.document.CPage pageToMove, Map<java.lang.Object, java.lang.Object> mape) {
		SkyLogger.getClientLogger().error(getClass().getName() + "#" + (new Object() {
		}.getClass().getEnclosingMethod().getName()) + "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));

	}

	/*
	 * Will becalled after a document has been requeued
	 */
	public void postRequeue(String loginName, boolean isOperator, de.ityx.contex.impl.document.CDocumentContainer<? extends de.ityx.contex.interfaces.document.CDocument> cdoc, Map<java.lang.Object, java.lang.Object> map) {
		SkyLogger.getClientLogger().error(getClass().getName() + "#" + (new Object() {
		}.getClass().getEnclosingMethod().getName()) + "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));
	}

	/*
	 * Will be called after a document has been bound to a validationstation
	 */
	public void postSetDocument(de.ityx.contex.impl.document.CDocumentContainer<? extends de.ityx.contex.interfaces.document.CDocument> cdoc, Map<java.lang.Object, java.lang.Object> map) {
		SkyLogger.getClientLogger().error(getClass().getName() + "#" + (new Object() {
		}.getClass().getEnclosingMethod().getName()) + "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));
	}

	/*
	 * Will be called after a tag has been set in the documentcontainer
	 */
	public void postSetTagMatch(Object key, de.ityx.contex.impl.document.CDocumentContainer<? extends de.ityx.contex.interfaces.document.CDocument> cdoc, TagMatch tagmatch, Map<java.lang.Object, java.lang.Object> map) {
		SkyLogger.getClientLogger().error(getClass().getName() + "#" + (new Object() {
		}.getClass().getEnclosingMethod().getName()) + "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));

	}

	/*
	 * Will be called after the document has been stored
	 */
	public void postStore(String loginName, boolean isOperator, de.ityx.contex.impl.document.CDocumentContainer<? extends de.ityx.contex.interfaces.document.CDocument> cdoc, Map<java.lang.Object, java.lang.Object> map) {
		SkyLogger.getClientLogger().error(getClass().getName() + "#" + (new Object() {
		}.getClass().getEnclosingMethod().getName()) + "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));
	}

	/*
	 * Will be called when before the type of a document will be set
	 */
	public boolean preChangeTagmatch(CDocument formular, String oldFormtype, String newformType, Map<java.lang.Object, java.lang.Object> map) {
		SkyLogger.getClientLogger().error(getClass().getName() + "#" + (new Object() {
		}.getClass().getEnclosingMethod().getName()) + "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));
		return true;
	}

	/*
	 * Will be called before a page is moved from a document to another
	 */
	public <T extends de.ityx.contex.interfaces.document.CDocument> boolean prePageMove(de.ityx.contex.impl.document.CDocumentContainer<T> cdoc, T oldFormular, de.ityx.contex.interfaces.document.CPage newFormular, Map<java.lang.Object, java.lang.Object> pageToMove) {
		SkyLogger.getClientLogger().error(getClass().getName() + "#" + (new Object() {
		}.getClass().getEnclosingMethod().getName()) + "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));

		return true;
	}

	/*
	 * Will be called before a page is added to a document
	 */
	public boolean prePageToDocument(CDocument formular, de.ityx.contex.interfaces.document.CPage pageToMove, Map<java.lang.Object, java.lang.Object> mape) {
		SkyLogger.getClientLogger().error(getClass().getName() + "#" + (new Object() {
		}.getClass().getEnclosingMethod().getName()) + "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));

		return true;
	}

	/*
	 * Will be called after the user has pressed the requeue button
	 */
	public boolean preRequeue(String loginName, boolean isOperator, Question question, de.ityx.contex.impl.document.CDocumentContainer<? extends de.ityx.contex.interfaces.document.CDocument> cdoc, java.beans.Statement p4, java.beans.Statement p5, Map<java.lang.Object, java.lang.Object> map) {
		SkyLogger.getClientLogger().error(getClass().getName() + "#" + (new Object() {
		}.getClass().getEnclosingMethod().getName()) + "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));

		return true;
	}

	/*
	 * Will be called before a document will be bound to a validationstation
	 */
	public void preSetDocument(de.ityx.contex.impl.document.CDocumentContainer<? extends de.ityx.contex.interfaces.document.CDocument> cdoc) {
		SkyLogger.getClientLogger().error(getClass().getName() + "#" + (new Object() {
		}.getClass().getEnclosingMethod().getName()) + "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));

	}

	/*
	 * Will be called before a tag will be set in the documentcontainer
	 */
	public void preSetTagMatch(Object key, de.ityx.contex.impl.document.CDocumentContainer<? extends de.ityx.contex.interfaces.document.CDocument> cdoc, TagMatch tagmatch, Map<java.lang.Object, java.lang.Object> map) {
		SkyLogger.getClientLogger().error(getClass().getName() + "#" + (new Object() {
		}.getClass().getEnclosingMethod().getName()) + "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));

	}

	/*
	 * Will be called before the document will be stored.
	 */
	public boolean preStore(String loginName, boolean isOperator, de.ityx.contex.impl.document.CDocumentContainer<? extends de.ityx.contex.interfaces.document.CDocument> cdoc, Map<java.lang.Object, java.lang.Object> map) {
		SkyLogger.getClientLogger().error(getClass().getName() + "#" + (new Object() {
		}.getClass().getEnclosingMethod().getName()) + "!!! DETECTED USAGE OF GENERAL BR-FACTORY", new IllegalAccessException("!!! DETECTED USAGE OF GENERAL BR-FACTORY"));

		return true;
	}
}