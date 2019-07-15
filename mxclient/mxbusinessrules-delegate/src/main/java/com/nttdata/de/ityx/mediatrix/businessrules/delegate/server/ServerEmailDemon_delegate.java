package com.nttdata.de.ityx.mediatrix.businessrules.delegate.server;

import de.ityx.mediatrix.api.interfaces.IEmail;
import de.ityx.mediatrix.api.interfaces.businessrules.server.IServerEmailDemon;
import de.ityx.mediatrix.data.*;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class ServerEmailDemon_delegate implements IServerEmailDemon {

	private IServerEmailDemon delegate = com.nttdata.de.ityx.mediatrix.businessrules.factories.NTTDataBusinessruleFactoryGenerator.createFactory(true).getServerEmailDemon();

	@Override
	public boolean checkInformationMessages(Connection arg0, IEmail arg1, Question arg2, Answer arg3) {
		return delegate.checkInformationMessages(arg0, arg1, arg2, arg3);
	}

	@Override
	public Vector foundFilter(Connection con, Filter arg1, Question question) {
		return delegate.foundFilter(con, arg1, question);
	}

	@Override
	public HashMap<String, Object> foundServiceCenterReply(Connection arg0, Question arg1, Customer arg2, HashMap<String, Object> arg3) {
		return delegate.foundServiceCenterReply(arg0, arg1, arg2, arg3);
	}

	@Override
	public Vector messageChange(Connection arg0, Vector arg1, String arg2) {
		return delegate.messageChange(arg0, arg1, arg2);
	}

	@Override
	public void postAutoReplySend(Connection arg0, Question arg1, Question arg2, Customer arg3, Subproject arg4, HashMap<String, Object> arg5) {
		delegate.postAutoReplySend(arg0, arg1, arg2, arg3, arg4, arg5);
	}


	@Override
	public HashMap<String, Object> postExportExternalClassificator(Connection arg0, Question arg1, Map<String, Object> arg2) {
		return delegate.postExportExternalClassificator(arg0, arg1, arg2);
	}

	@Override
	public void postExternalSend(Connection arg0, Question arg1, Customer arg2, Subproject arg3, HashMap<String, Object> arg4) {
		delegate.postExternalSend(arg0, arg1, arg2, arg3, arg4);
	}

	@Override
	public HashMap<String, Object> postImportExternalClassificator(Connection arg0, Project arg1, List<Question> arg2, Map<String, Object> arg3) {
		return delegate.postImportExternalClassificator(arg0, arg1, arg2, arg3);
	}

	@Override
	public HashMap<String, Object> postProcessing(Connection arg0, Question arg1, Subproject arg2, HashMap<String, Object> arg3) {
		return delegate.postProcessing(arg0, arg1, arg2, arg3);
	}

	@Override
	public HashMap<String, Object> preAutoReplySend(Connection arg0, Question arg1, Question arg2, Customer arg3, Subproject arg4, HashMap<String, Object> arg5) {
		return delegate.preAutoReplySend(arg0, arg1, arg2, arg3, arg4, arg5);
	}

	@Override
	public boolean preExportExternalClassificator(Connection arg0, Question arg1, Map<String, Object> arg2) {
		return delegate.preExportExternalClassificator(arg0, arg1, arg2);
	}

	@Override
	public HashMap<String, Object> preExternalSend(Connection arg0, Question arg1, Customer arg2, Subproject arg3, HashMap<String, Object> arg4) {
		return delegate.preExternalSend(arg0, arg1, arg2, arg3, arg4);
	}

	@Override
	public List<Object> preFilterQuestion(Connection arg0, Question arg1, Project arg2, Account arg3, Map<String, Object> arg4) {
		return delegate.preFilterQuestion(arg0, arg1, arg2, arg3, arg4);
	}

	@Override
	public boolean preImportExternalClassificator(Connection arg0, Project arg1, Map<String, Object> arg2) {
		return delegate.preImportExternalClassificator(arg0, arg1, arg2);
	}

	@Override
	public String getExternalId(Connection arg0, IEmail arg1) {
		return delegate.getExternalId(arg0, arg1);
	}

	@Override
	public void externalReplyReceived(java.sql.Connection connection,de.ityx.mediatrix.data.Question question,de.ityx.mediatrix.data.Question question1,de.ityx.mediatrix.data.Answer answer) {
		delegate.externalReplyReceived(connection, question, question1, answer);
	}

}
