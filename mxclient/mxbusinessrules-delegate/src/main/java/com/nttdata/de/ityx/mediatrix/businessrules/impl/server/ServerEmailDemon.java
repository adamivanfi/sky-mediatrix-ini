package com.nttdata.de.ityx.mediatrix.businessrules.impl.server;

import com.nttdata.de.lib.logging.SkyLogger;
import de.ityx.mediatrix.api.interfaces.IEmail;
import de.ityx.mediatrix.api.interfaces.businessrules.server.IServerEmailDemon;
import de.ityx.mediatrix.data.*;

import java.sql.Connection;
import java.util.*;

public class ServerEmailDemon implements IServerEmailDemon {

	@Override
	public boolean checkInformationMessages(Connection arg0, IEmail arg1, Question arg2, Answer arg3) {
		SkyLogger.getBRSLogger().debug(getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ": call");
		return false;
	}

	@Override
	public Vector foundFilter(Connection arg0, Filter arg1, Question arg2) {
		SkyLogger.getBRSLogger().debug(getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ": call");
		return new Vector();
	}

	@Override
	public HashMap foundServiceCenterReply(Connection arg0, Question arg1, Customer arg2, HashMap arg3) {
		SkyLogger.getBRSLogger().debug(getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ": call");
		return new HashMap();
	}

	@Override
	public Vector messageChange(Connection arg0, Vector arg1, String arg2) {
		SkyLogger.getBRSLogger().debug(getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ": call");
		return new Vector();
	}

	@Override
	public void postAutoReplySend(Connection arg0, Question arg1, Question arg2, Customer arg3, Subproject arg4, HashMap arg5) {
		SkyLogger.getBRSLogger().debug(getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ": call");
	}

	@Override
	public HashMap postExportExternalClassificator(Connection arg0, Question arg1, Map arg2) {
		SkyLogger.getBRSLogger().debug(getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ": call");
		return new HashMap();
	}

	@Override
	public void postExternalSend(Connection arg0, Question arg1, Customer arg2, Subproject arg3, HashMap arg4) {
		SkyLogger.getBRSLogger().debug(getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ": call");


	}

	@Override
	public HashMap postImportExternalClassificator(Connection arg0, Project arg1, List<Question> arg2, Map<String, Object> arg3) {
		SkyLogger.getBRSLogger().debug(getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ": call");

		return new HashMap();
	}

	@Override
	public HashMap postProcessing(Connection arg0, Question arg1, Subproject arg2, HashMap arg3) {
		SkyLogger.getBRSLogger().debug(getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ": call");

		return new HashMap();
	}

	@Override
	public HashMap preAutoReplySend(Connection arg0, Question arg1, Question arg2, Customer arg3, Subproject arg4, HashMap arg5) {
		SkyLogger.getBRSLogger().debug(getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ": call");

		return new HashMap();
	}

	@Override
	public boolean preExportExternalClassificator(Connection arg0, Question arg1, Map arg2) {
		SkyLogger.getBRSLogger().debug(getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ": call");

		return false;
	}

	@Override
	public HashMap preExternalSend(Connection arg0, Question arg1, Customer arg2, Subproject arg3, HashMap arg4) {
		SkyLogger.getBRSLogger().debug(getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ": call");

		return new HashMap();
	}

	@Override
	public List preFilterQuestion(Connection arg0, Question arg1, Project arg2, Account arg3, Map arg4) {
		SkyLogger.getBRSLogger().debug(getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ": call");
		return new ArrayList();
	}

	@Override
	public boolean preImportExternalClassificator(Connection arg0, Project arg1, Map arg2) {
		SkyLogger.getBRSLogger().debug(getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ": call");
		return false;
	}

	@Override
	public String getExternalId(Connection arg0, IEmail arg1) {
		//@ToDo: DocID-Generation?
		return "";
	}


	@Override
	public void externalReplyReceived(java.sql.Connection connection, de.ityx.mediatrix.data.Question question, de.ityx.mediatrix.data.Question question1, de.ityx.mediatrix.data.Answer answer) {

	}

}
