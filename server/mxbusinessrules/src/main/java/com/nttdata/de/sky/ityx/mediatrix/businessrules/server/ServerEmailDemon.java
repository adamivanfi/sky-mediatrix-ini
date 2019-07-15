package com.nttdata.de.sky.ityx.mediatrix.businessrules.server;

import com.nttdata.de.sky.ityx.mediatrix.businessrules.server.sky.SkyServerEmailDemon;
import de.ityx.mediatrix.api.interfaces.IEmail;
import de.ityx.mediatrix.api.interfaces.businessrules.server.IServerEmailDemon;
import de.ityx.mediatrix.data.*;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;


public class ServerEmailDemon implements IServerEmailDemon {

	IServerEmailDemon skydel = new SkyServerEmailDemon();

	@Override
	public boolean checkInformationMessages(Connection arg0, IEmail arg1, Question arg2, Answer arg3) {
		return skydel.checkInformationMessages(arg0, arg1, arg2, arg3);
	}

	@Override
	public Vector foundFilter(Connection con, Filter arg1, Question question) {
		return skydel.foundFilter(con, arg1, question);
	}

	@Override
	public HashMap<String, Object> foundServiceCenterReply(Connection arg0, Question arg1, Customer arg2, HashMap<String, Object> arg3) {
		return skydel.foundServiceCenterReply(arg0, arg1, arg2, arg3);
	}

	@Override
	public Vector messageChange(Connection arg0, Vector arg1, String arg2) {
		return skydel.messageChange(arg0, arg1, arg2);
	}

	@Override
	public void postAutoReplySend(Connection arg0, Question arg1, Question arg2, Customer arg3, Subproject arg4, HashMap<String, Object> arg5) {
		skydel.postAutoReplySend(arg0, arg1, arg2, arg3, arg4, arg5);
	}


	@Override
	public HashMap<String, Object> postExportExternalClassificator(Connection arg0, Question arg1, Map<String, Object> arg2) {
		return skydel.postExportExternalClassificator(arg0, arg1, arg2);
	}

	@Override
	public void postExternalSend(Connection arg0, Question arg1, Customer arg2, Subproject arg3, HashMap<String, Object> arg4) {
		skydel.postExternalSend(arg0, arg1, arg2, arg3, arg4);
	}

	@Override
	public HashMap<String, Object> postImportExternalClassificator(Connection arg0, Project arg1, List<Question> arg2, Map<String, Object> arg3) {
		return skydel.postImportExternalClassificator(arg0, arg1, arg2, arg3);
	}

	@Override
	public HashMap<String, Object> postProcessing(Connection arg0, Question arg1, Subproject arg2, HashMap<String, Object> arg3) {
		return skydel.postProcessing(arg0, arg1, arg2, arg3);
	}

	@Override
	public HashMap<String, Object> preAutoReplySend(Connection arg0, Question arg1, Question arg2, Customer arg3, Subproject arg4, HashMap<String, Object> arg5) {
		return skydel.preAutoReplySend(arg0, arg1, arg2, arg3, arg4, arg5);
	}

	@Override
	public boolean preExportExternalClassificator(Connection arg0, Question arg1, Map<String, Object> arg2) {
		return skydel.preExportExternalClassificator(arg0, arg1, arg2);
	}

	@Override
	public HashMap<String, Object> preExternalSend(Connection arg0, Question arg1, Customer arg2, Subproject arg3, HashMap<String, Object> arg4) {
		return skydel.preExternalSend(arg0, arg1, arg2, arg3, arg4);
	}

	@Override
	public List<Object> preFilterQuestion(Connection arg0, Question arg1, Project arg2, Account arg3, Map<String, Object> arg4) {
		return skydel.preFilterQuestion(arg0, arg1, arg2, arg3, arg4);
	}

	@Override
	public boolean preImportExternalClassificator(Connection arg0, Project arg1, Map<String, Object> arg2) {
		return skydel.preImportExternalClassificator(arg0, arg1, arg2);
	}

	@Override
	public String getExternalId(Connection arg0, IEmail arg1) {
		return skydel.getExternalId(arg0, arg1);
	}

	@Override
	public void externalReplyReceived(java.sql.Connection connection, de.ityx.mediatrix.data.Question question, de.ityx.mediatrix.data.Question question1, de.ityx.mediatrix.data.Answer answer) {
		skydel.externalReplyReceived(connection, question, question1, answer);
	}
}