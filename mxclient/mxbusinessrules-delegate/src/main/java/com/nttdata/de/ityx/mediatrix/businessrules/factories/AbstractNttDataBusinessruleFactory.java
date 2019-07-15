package com.nttdata.de.ityx.mediatrix.businessrules.factories;

import com.nttdata.de.ityx.mediatrix.businessrules.impl.client.*;
import com.nttdata.de.ityx.mediatrix.businessrules.impl.server.*;
import com.nttdata.de.ityx.mediatrix.businessrules.interfaces.IMXActions_Sky;
import de.ityx.mediatrix.api.interfaces.businessrules.client.*;
import de.ityx.mediatrix.api.interfaces.businessrules.server.*;

/**
 * Returns a default implementation of each business rule.
 *
 * @author DHIFLM
 */
public abstract class AbstractNttDataBusinessruleFactory implements INttDataBusinessruleFactory {
	@Override
	public IClientObjectsControl getClientObjectsControlRule() {
		return new ClientObjectsControl();
	}

	@Override
	public IClientMailInbox getClientMailInboxRule() {
		return new ClientMailInbox();
	}

	@Override
	public IClientNewEmail getClientNewEmailRule() {
		return new ClientNewEmail();
	}

	@Override
	public IClientQuestionAnswerView getClientQuestionAnswerViewRule() {
		return new ClientQuestionAnswerView();
	}

	@Override
	public IClientQuestionView getClientQuestionViewRule() {
		return new ClientQuestionView();
	}

	@Override
	public IClientAnswerEdit getClientAnswerEdit() {
		return new ClientAnswerEdit();
	}

	@Override
	public IClientLogin getClientLogin() {
		return new ClientLogin();
	}

	@Override
	public IClientAnswerView getClientAnswerView() {
		return new ClientAnswerView();
	}

	@Override
	public IServerEventPerformer getServerEventPerformer() {
		return new ServerEventPerformer();
	}

	@Override
	public IServerEmailDemon getServerEmailDemon() {
		return new ServerEmailDemon();
	}

	@Override
	public IServerObjectsControl getServerObjectsControl() {
		return new ServerObjectsControl();
	}

	@Override
	public IServerMonitor getServerMonitor() {
		return new ServerMonitor();
	}

	@Override
	public IServerMultiChannel getServerMultiChannel() {
		return new ServerMultiChannel();
	}

	@Override
	public IServerSystem getServerSystem() {
		return new ServerSystem();
	}

	@Override
	public IClientSystem getClientSystem() {
		return new ClientSystem();
	}

	@Override
	public IServerContexDemon getServerContexDemon() {
		return new ServerContexDemon();
	}

	@Override
	public IClientForwardDialog getClientForwardDialog() {
		return new ClientForwardDialog();
	}

	@Override
	public IClientClassificationValidation getClientClassificationValidation() {
		return new ClientClassificationValidation();
	}
	
	@Override
	public IClientRunner getClientRunner() {
		return new ClientRunner();
	}

	@Override
	public IMXActions_Sky getClientMXAction(){return new ClientMXAction(); };
}
