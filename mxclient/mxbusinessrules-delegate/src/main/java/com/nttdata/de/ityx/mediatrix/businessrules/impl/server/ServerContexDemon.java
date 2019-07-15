package com.nttdata.de.ityx.mediatrix.businessrules.impl.server;

import de.ityx.contex.data.icat.Category;
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

public class ServerContexDemon implements IServerContexDemon {
	@Override
	public boolean add(java.sql.Connection p0, Question p1, String p2, HashMap p3) {
		return true;
	}

	@Override
	public boolean delete(java.sql.Connection p0, Question p1, String p2, HashMap p3) {
		return true;
	}

	@Override
	public boolean update(java.sql.Connection p0, Question p1, String p2, HashMap p3) {
		return true;
	}

	@Override
	public boolean addArchive(java.sql.Connection p0, Question p1, String p2, HashMap p3) {
		return true;
	}

	@Override
	public void preCategorize(Connection connection, CDocument cDocument, long l, Filter filter, Email email, Project project) {

	}

	/*
		 * is called after a email has been categorized
		 */
	@Override
	public void postCategorize(java.sql.Connection con, Category[] cats, long master, Filter filter, Email question, Project project) {

	}

	/*
	 * is called after a email has been extracted
	 */
	@Override
	public void postExtraction(java.sql.Connection con, ArrayList<de.ityx.contex.interfaces.extag.TagMatch> tagmatches, long master, Filter filter, Email question, Project project) {

	}

	@Override
	public void preMcategorize(Connection connection, CDocument cDocument, MCatFlowObject mCatFlowObject, long l, Filter filter, Email email, Project project) {

	}

	/*
	 * is called after a email has been categorized
	 */
	@Override
	public void postMcategorize(java.sql.Connection con, Category[] cats, MCatFlowObject flow, long master, Filter filter, Email question, Project project) {

	}

	/*
	 * is called after a process has been called
	 */
	@Override
	public void postProcess(java.sql.Connection con, de.ityx.contex.interfaces.designer.IParameterMap map, Filter filter, Email question, Project project) {

	}


	/*
	 * is called before a email will be extracted
	 */
	@Override
	public void preExtraction(java.sql.Connection con, StringDocument doc, long master, Filter filter, Email question, Project project) {

	}


	/*
	 * is called before a process will be called
	 */
	@Override
	public void preProcess(java.sql.Connection con, de.ityx.contex.impl.document.CDocumentContainer<de.ityx.contex.interfaces.document.CDocument> container, Filter filter, Email question, Project project) {
	}

}
