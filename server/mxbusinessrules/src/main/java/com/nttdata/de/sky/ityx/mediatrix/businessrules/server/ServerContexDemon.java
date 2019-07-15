package com.nttdata.de.sky.ityx.mediatrix.businessrules.server;

import com.nttdata.de.sky.ityx.mediatrix.businessrules.server.sky.SkyServerContexDemon;
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

	IServerContexDemon skydel = new SkyServerContexDemon();

	@Override
	public boolean add(Connection con, Question question, String doctype, HashMap<String, String> hm) {
		return skydel.add(con, question, doctype, hm);
	}
	
	@Override
	public boolean delete(Connection con, Question question, String doctype, HashMap<String, Object> hm) {
		return skydel.delete(con, question, doctype, hm);
	}

	@Override
	public boolean update(Connection con, Question question, String doctype, HashMap<String, Object> hm) {
		return skydel.update(con, question, doctype, hm);
	}

	@Override
	public boolean addArchive(Connection con, Question question, String doctype, HashMap<String, Object> hm) {
		return skydel.addArchive(con, question, doctype, hm);
	}

	@Override
	public void postCategorize(java.sql.Connection con, Category[] cats, long master, Filter filter, Email question, Project project) {
		skydel.postCategorize(con, cats, master, filter, question, project);
	}

	@Override
	public void postExtraction(java.sql.Connection con, ArrayList<de.ityx.contex.interfaces.extag.TagMatch> tagmatches, long master, Filter filter, Email question, Project project) {
		skydel.postExtraction(con, tagmatches, master, filter, question, project);
	}

	@Override
	public void postMcategorize(java.sql.Connection con, Category[] cats, MCatFlowObject flow, long master, Filter filter, Email question, Project project) {
		skydel.postMcategorize(con, cats, flow, master, filter, question, project);
	}

	@Override
	public void postProcess(java.sql.Connection con, de.ityx.contex.interfaces.designer.IParameterMap map, Filter filter, Email question, Project project) {
		skydel.postProcess(con, map, filter, question, project);
	}

	@Override
	public void preCategorize(java.sql.Connection con, CDocument doc, long master, Filter filter, Email question, Project project) {
		skydel.preCategorize(con, doc, master, filter, question, project);
	}

	@Override
	public void preExtraction(java.sql.Connection con, StringDocument doc, long master, Filter filter, Email question, Project project) {
		skydel.preExtraction(con, doc, master, filter, question, project);
	}

	@Override
	public void preMcategorize(java.sql.Connection con, CDocument doc, MCatFlowObject flow, long master, Filter filter, Email question, Project project) {
		skydel.preMcategorize(con, doc, flow, master, filter, question, project);
	}
	@Override
	public void preProcess(java.sql.Connection con, de.ityx.contex.impl.document.CDocumentContainer<de.ityx.contex.interfaces.document.CDocument> container, Filter filter, Email question, Project project) {
		skydel.preProcess(con, container, filter, question, project);
	}
}
