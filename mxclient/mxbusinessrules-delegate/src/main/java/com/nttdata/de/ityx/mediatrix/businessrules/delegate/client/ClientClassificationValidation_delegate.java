package com.nttdata.de.ityx.mediatrix.businessrules.delegate.client;

import com.nttdata.de.lib.logging.SkyLogger;
import de.ityx.contex.interfaces.document.CDocument;
import de.ityx.contex.interfaces.extag.TagMatch;
import de.ityx.mediatrix.api.interfaces.businessrules.client.IClientClassificationValidation;
import de.ityx.mediatrix.data.Question;

import java.util.List;
import java.util.Map;

public class ClientClassificationValidation_delegate implements IClientClassificationValidation {

	IClientClassificationValidation delegate = com.nttdata.de.ityx.mediatrix.businessrules.factories.NTTDataBusinessruleFactoryGenerator.createFactory(false).getClientClassificationValidation();

	/*
	 * Will be called after the user has pressed the cancel button and the
	 * document has been released
	 */
	@Override
	public void postCancel(String loginName, boolean isOperator, de.ityx.contex.impl.document.CDocumentContainer<? extends de.ityx.contex.interfaces.document.CDocument> cdoc, Map<java.lang.Object, java.lang.Object> map) {
		SkyLogger.getClientLogger().info(getClass().getName() + "#" + (new Object() {
		}.getClass().getEnclosingMethod().getName()) + " DELEGATE START");

		delegate.postCancel(loginName, isOperator, cdoc, map);
	}

	/*
	 * Will be called after the user has pressed the cancel button before the
	 * document will be released
	 */
	@Override
	public boolean preCancel(String loginName, boolean isOperator, de.ityx.contex.impl.document.CDocumentContainer<? extends de.ityx.contex.interfaces.document.CDocument> cdoc, Map<java.lang.Object, java.lang.Object> map) {
		SkyLogger.getClientLogger().info(getClass().getName() + "#" + (new Object() {
		}.getClass().getEnclosingMethod().getName()) + " DELEGATE START");

		return delegate.preCancel(loginName, isOperator, cdoc, map);
	}

	/*
	 * Returns a list of additional buttons to be added to the toolbar.
	 */
	@Override
	public List<javax.swing.AbstractButton> getExtButtonList(de.ityx.contex.impl.document.CDocumentContainer<? extends de.ityx.contex.interfaces.document.CDocument> cdoc) {
		SkyLogger.getClientLogger().info(getClass().getName() + "#" + (new Object() {
		}.getClass().getEnclosingMethod().getName()) + " DELEGATE START");

		return delegate.getExtButtonList(cdoc);
	}

	/*
	 * Returns the qualified name of the class implementing the lower panel in
	 * the validationstation. null oder empty means using the default
	 * implementation.
	 */
	@Override
	public String getFormtypePanelImplementor(int subprojectId) {
		SkyLogger.getClientLogger().info(getClass().getName() + "#" + (new Object() {
		}.getClass().getEnclosingMethod().getName()) + " DELEGATE START");
		return delegate.getFormtypePanelImplementor(subprojectId);
	}

	/*
	 * Will be called after the type of a document has been changed
	 */
	@Override
	public void postChangeTagmatch(CDocument formular, String formtype, Map<java.lang.Object, java.lang.Object> map) {
		SkyLogger.getClientLogger().info(getClass().getName() + "#" + (new Object() {
		}.getClass().getEnclosingMethod().getName()) + " DELEGATE START");
		delegate.postChangeTagmatch(formular, formtype, map);
	}

	/*
	 * Will be called after a page is moved from a document to another
	 */
	@Override
	public <T extends de.ityx.contex.interfaces.document.CDocument> void postPageMove(de.ityx.contex.impl.document.CDocumentContainer<T> cdoc, T oldFormular, de.ityx.contex.interfaces.document.CPage newFormular, Map<java.lang.Object, java.lang.Object> pageToMove) {
		SkyLogger.getClientLogger().info(getClass().getName()+"#"+(new Object() {}.getClass().getEnclosingMethod().getName())+ " DELEGATE START");

		delegate.postPageMove(cdoc, oldFormular, newFormular, pageToMove);
	}

	/*
	 * Will be called after a page is added to a document
	 */
	@Override
	public void postPageToDocument(CDocument formular, de.ityx.contex.interfaces.document.CPage pageToMove, Map<java.lang.Object, java.lang.Object> mape) {
		SkyLogger.getClientLogger().info(getClass().getName()+"#"+(new Object() {}.getClass().getEnclosingMethod().getName())+ " DELEGATE START");
		delegate.postPageToDocument(formular, pageToMove, mape);
	}

	/*
	 * Will becalled after a document has been requeued
	 */
	public void postRequeue(String loginName, boolean isOperator, de.ityx.contex.impl.document.CDocumentContainer<? extends de.ityx.contex.interfaces.document.CDocument> cdoc, Map<java.lang.Object, java.lang.Object> map) {
		SkyLogger.getClientLogger().info(getClass().getName()+"#"+(new Object() {}.getClass().getEnclosingMethod().getName())+ " DELEGATE START");
		delegate.postRequeue(loginName, isOperator, cdoc, map);
	}

	/*
	 * Will be called after a document has been bound to a validationstation
	 */
	@Override
	public void postSetDocument(de.ityx.contex.impl.document.CDocumentContainer<? extends de.ityx.contex.interfaces.document.CDocument> cdoc, Map<java.lang.Object, java.lang.Object> map) {
		SkyLogger.getClientLogger().info(getClass().getName() + "#" + (new Object() {
		}.getClass().getEnclosingMethod().getName()) + " DELEGATE START");
		delegate.postSetDocument(cdoc, map);
	}

	/*
	 * Will be called after a tag has been set in the documentcontainer
	 */
	@Override
	public void postSetTagMatch(Object key, de.ityx.contex.impl.document.CDocumentContainer<? extends de.ityx.contex.interfaces.document.CDocument> cdoc, TagMatch tagmatch, Map<java.lang.Object, java.lang.Object> map) {
		SkyLogger.getClientLogger().info(getClass().getName()+"#"+(new Object() {}.getClass().getEnclosingMethod().getName())+ " DELEGATE START");

		delegate.postSetTagMatch(key, cdoc, tagmatch, map);
	}

	/*
	 * Will be called after the document has been stored
	 */
	@Override
	public void postStore(String loginName, boolean isOperator, de.ityx.contex.impl.document.CDocumentContainer<? extends de.ityx.contex.interfaces.document.CDocument> cdoc, Map<java.lang.Object, java.lang.Object> map) {
		SkyLogger.getClientLogger().info(getClass().getName()+"#"+(new Object() {}.getClass().getEnclosingMethod().getName())+ " DELEGATE START");
		delegate.postStore(loginName, isOperator, cdoc, map);
	}

	/*
	 * Will be called when before the type of a document will be set
	 */
	@Override
	public boolean preChangeTagmatch(CDocument formular, String oldFormtype, String newformType, Map<java.lang.Object, java.lang.Object> map) {
		SkyLogger.getClientLogger().info(getClass().getName()+"#"+(new Object() {}.getClass().getEnclosingMethod().getName())+ " DELEGATE START");
		return delegate.preChangeTagmatch(formular, oldFormtype, newformType, map);
	}

	/*
	 * Will be called before a page is moved from a document to another
	 */
	@Override
	public <T extends de.ityx.contex.interfaces.document.CDocument> boolean prePageMove(de.ityx.contex.impl.document.CDocumentContainer<T> cdoc, T oldFormular, de.ityx.contex.interfaces.document.CPage newFormular, Map<java.lang.Object, java.lang.Object> pageToMove) {
		SkyLogger.getClientLogger().info(getClass().getName()+"#"+(new Object() {}.getClass().getEnclosingMethod().getName())+ " DELEGATE START");
		return delegate.prePageMove(cdoc, oldFormular, newFormular, pageToMove);
	}

	/*
	 * Will be called before a page is added to a document
	 */
	@Override
	public boolean prePageToDocument(CDocument formular, de.ityx.contex.interfaces.document.CPage pageToMove, Map<java.lang.Object, java.lang.Object> mape) {
		SkyLogger.getClientLogger().info(getClass().getName()+"#"+(new Object() {}.getClass().getEnclosingMethod().getName())+ " DELEGATE START");
		return delegate.prePageToDocument(formular, pageToMove, mape);
	}

	/*
	 * Will be called after the user has pressed the requeue button
	 */
	@Override
	public boolean preRequeue(String loginName, boolean isOperator, Question question, de.ityx.contex.impl.document.CDocumentContainer<? extends de.ityx.contex.interfaces.document.CDocument> cdoc, java.beans.Statement p4, java.beans.Statement p5, Map<java.lang.Object, java.lang.Object> map) {
		SkyLogger.getClientLogger().info(getClass().getName()+"#"+(new Object() {}.getClass().getEnclosingMethod().getName())+ " DELEGATE START");
		return delegate.preRequeue(loginName, isOperator, question, cdoc, p4, p5, map);
	}

	/*
	 * Will be called before a document will be bound to a validationstation
	 */
	@Override
	public void preSetDocument(de.ityx.contex.impl.document.CDocumentContainer<? extends de.ityx.contex.interfaces.document.CDocument> cdoc) {
		SkyLogger.getClientLogger().info(getClass().getName()+"#"+(new Object() {}.getClass().getEnclosingMethod().getName())+ " DELEGATE START");
		delegate.preSetDocument(cdoc);
	}

	/*
	 * Will be called before a tag will be set in the documentcontainer
	 */
	@Override
	public void preSetTagMatch(Object key, de.ityx.contex.impl.document.CDocumentContainer<? extends de.ityx.contex.interfaces.document.CDocument> cdoc, TagMatch tagmatch, Map<java.lang.Object, java.lang.Object> map) {
		SkyLogger.getClientLogger().info(getClass().getName()+"#"+(new Object() {}.getClass().getEnclosingMethod().getName())+ " DELEGATE START");
		delegate.preSetTagMatch(key, cdoc, tagmatch, map);
	}

	/*
	 * Will be called before the document will be stored.
	 */
	@Override
	public boolean preStore(String loginName, boolean isOperator, de.ityx.contex.impl.document.CDocumentContainer<? extends de.ityx.contex.interfaces.document.CDocument> cdoc, Map<java.lang.Object, java.lang.Object> map) {
		SkyLogger.getClientLogger().info(getClass().getName()+"#"+(new Object() {}.getClass().getEnclosingMethod().getName())+ " DELEGATE START");
		return delegate.preStore(loginName, isOperator, cdoc, map);
	}
}