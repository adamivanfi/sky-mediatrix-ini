package com.nttdata.de.ityx.mediatrix.businessrules.factories;

import com.nttdata.de.ityx.mediatrix.businessrules.interfaces.IMXActions_Sky;
import de.ityx.mediatrix.api.interfaces.businessrules.client.*;
import de.ityx.mediatrix.api.interfaces.businessrules.server.*;

/**
 * This methods return concrete implementations of mediatrx business rules.
 * 
 * @author DHIFLM
 *
 */
public interface INttDataBusinessruleFactory {
	public IClientMailInbox getClientMailInboxRule();
	public IClientNewEmail getClientNewEmailRule();
	public IClientQuestionAnswerView getClientQuestionAnswerViewRule();
	public IClientQuestionView getClientQuestionViewRule();
	public IClientObjectsControl getClientObjectsControlRule();
	public IServerEventPerformer getServerEventPerformer();
	public IServerEmailDemon getServerEmailDemon();
	public IServerObjectsControl getServerObjectsControl();
	public IServerMonitor getServerMonitor();
	public IServerMultiChannel getServerMultiChannel();
	public IClientAnswerEdit getClientAnswerEdit();
	public IClientLogin getClientLogin();
	public IClientAnswerView getClientAnswerView();
	public IServerSystem getServerSystem();
	public IClientSystem getClientSystem();
	public IServerContexDemon getServerContexDemon();
	public IClientForwardDialog getClientForwardDialog();
	public IClientClassificationValidation getClientClassificationValidation();
	public IClientRunner getClientRunner();
	public IMXActions_Sky getClientMXAction();
}
