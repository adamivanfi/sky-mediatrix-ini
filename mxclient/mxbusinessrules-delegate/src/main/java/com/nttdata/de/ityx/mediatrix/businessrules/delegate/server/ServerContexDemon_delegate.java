package com.nttdata.de.ityx.mediatrix.businessrules.delegate.server;

import de.ityx.contex.impl.document.text.string.StringDocument;
import de.ityx.contex.impl.mcat.MCatFlowObject;
import de.ityx.contex.interfaces.document.CDocument;
import de.ityx.mediatrix.api.interfaces.businessrules.server.IServerContexDemon;
import de.ityx.mediatrix.data.Email;
import de.ityx.mediatrix.data.Filter;
import de.ityx.mediatrix.data.Project;
import de.ityx.mediatrix.data.Question;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

public class ServerContexDemon_delegate implements IServerContexDemon {

	IServerContexDemon delegate = com.nttdata.de.ityx.mediatrix.businessrules.factories.NTTDataBusinessruleFactoryGenerator.createFactory(true).getServerContexDemon();

	@Override
	public boolean add(java.sql.Connection p0, Question p1, String p2, HashMap p3) {
		return delegate.add(p0, p1, p2, p3);
	}

	@Override
	public boolean delete(java.sql.Connection p0, Question p1, String p2, HashMap p3) {
		return delegate.delete(p0, p1, p2, p3);
	}

	@Override
	public boolean update(java.sql.Connection p0, Question p1, String p2, HashMap p3) {
		return delegate.update(p0, p1, p2, p3);
	}

	@Override
	public boolean addArchive(java.sql.Connection p0, Question p1, String p2, HashMap p3) {
		return delegate.addArchive(p0, p1, p2, p3);
	}

	@Override
	public void preCategorize(Connection connection, CDocument cDocument, long l, Filter filter, Email email, Project project) {
		delegate.preCategorize(connection, cDocument, l, filter, email, project);
	}

	/*
		 * is called after a email has been categorized
		 */
	@Override
	public void postCategorize(java.sql.Connection con, de.ityx.contex.data.icat.Category[] cats, long master, Filter filter, Email question, Project project) {
		delegate.postCategorize(con, cats, master, filter, question, project);
	}

	/*
	 * is called after a email has been extracted
	 */
	@Override
	public void postExtraction(java.sql.Connection con, ArrayList<de.ityx.contex.interfaces.extag.TagMatch> tagmatches, long master, Filter filter, Email question, Project project) {
		delegate.postExtraction(con, tagmatches, master, filter, question, project);
	}

	@Override
	public void preMcategorize(Connection connection, CDocument cDocument, MCatFlowObject mCatFlowObject, long l, Filter filter, Email email, Project project) {
		delegate.preMcategorize(connection, cDocument, mCatFlowObject, l, filter, email, project);
	}

	/*
	 * is called after a email has been categorized
	 */
	@Override
	public void postMcategorize(java.sql.Connection con, de.ityx.contex.data.icat.Category[] cats, de.ityx.contex.impl.mcat.MCatFlowObject flow, long master, Filter filter, Email question, Project project) {
		delegate.postMcategorize(con, cats, flow, master, filter, question, project);
	}

	/*
	 * is called after a process has been called
	 */
	@Override
	public void postProcess(java.sql.Connection con, de.ityx.contex.interfaces.designer.IParameterMap map, Filter filter, Email question, Project project) {
		delegate.postProcess(con, map, filter, question, project);
	}


	/*
	 * is called before a email will be extracted
	 */
	@Override
	public void preExtraction(java.sql.Connection con, StringDocument doc, long master, Filter filter, Email question, Project project) {
		delegate.preExtraction(con, doc, master, filter, question, project);
	}

	/*
	 * is called before a process will be called
	 */
	@Override
	public void preProcess(java.sql.Connection con, de.ityx.contex.impl.document.CDocumentContainer<de.ityx.contex.interfaces.document.CDocument> container, Filter filter, Email question, Project project) {
		delegate.preProcess(con, container, filter, question, project);
	}
}
