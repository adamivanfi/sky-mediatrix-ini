package com.nttdata.de.ityx.mediatrix.businessrules.factories.sky;

import com.nttdata.de.ityx.mediatrix.businessrules.factories.AbstractNttDataBusinessruleFactory;
import com.nttdata.de.ityx.mediatrix.businessrules.factories.INttDataBusinessruleFactory;
import com.nttdata.de.ityx.mediatrix.businessrules.interfaces.IMXActions_Sky;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.mediatrix.businessrules.client.*;
import de.ityx.mediatrix.api.interfaces.businessrules.client.*;
import de.ityx.mediatrix.api.interfaces.businessrules.server.*;

/**
 * Contains a factory that returns the concrete implementations of business
 * rules.
 * 
 * @author DHIFLM
 * 
 */
public class NTTDataBusinessruleFactory_Sky extends AbstractNttDataBusinessruleFactory
		implements INttDataBusinessruleFactory {

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
	public IServerEventPerformer getServerEventPerformer() {
		String logPrefix =  getClass().getName() + "#" + new Object() {}.getClass().getEnclosingMethod().getName()+" ";
		try {
			return (IServerEventPerformer) Class.forName("com.nttdata.de.sky.ityx.mediatrix.businessrules.server.ServerEventPerformer").newInstance();
		} catch (Exception e) {
			SkyLogger.getMediatrixLogger().error(logPrefix + "Error instantiating: com.nttdata.de.sky.ityx.mediatrix.businessrules.server.ServerObjectsControl " + e.getMessage(), e);
			return super.getServerEventPerformer();
		}
	}

	@Override
	public IServerEmailDemon getServerEmailDemon() {
		String logPrefix =  getClass().getName() + "#" + new Object() {}.getClass().getEnclosingMethod().getName()+" ";
		try {
			return (IServerEmailDemon) Class.forName("com.nttdata.de.sky.ityx.mediatrix.businessrules.server.ServerEmailDemon").newInstance();
		} catch (Exception e) {
			SkyLogger.getMediatrixLogger().error(logPrefix + "Error instantiating: com.nttdata.de.sky.ityx.mediatrix.businessrules.server.ServerObjectsControl " + e.getMessage(), e);
			return super.getServerEmailDemon();
		}
	}

	@Override
	public IServerObjectsControl getServerObjectsControl() {
		String logPrefix =  getClass().getName() + "#" + new Object() {}.getClass().getEnclosingMethod().getName()+" ";
		try {
			return  (IServerObjectsControl) Class.forName("com.nttdata.de.sky.ityx.mediatrix.businessrules.server.ServerObjectsControl").newInstance();
		} catch (Exception e) {
			SkyLogger.getMediatrixLogger().error(logPrefix + "Error instantiating: com.nttdata.de.sky.ityx.mediatrix.businessrules.server.ServerObjectsControl " + e.getMessage(), e);
			return super.getServerObjectsControl();
		}
	}

	@Override
	public IServerMultiChannel getServerMultiChannel() {
		String logPrefix =  getClass().getName() + "#" + new Object() {}.getClass().getEnclosingMethod().getName()+" ";
		try {
			SkyLogger.getMediatrixLogger().info(logPrefix);
			return (IServerMultiChannel) Class.forName("com.nttdata.de.sky.ityx.mediatrix.businessrules.server.ServerMultiChannel").newInstance();
		} catch (Exception e) {
			SkyLogger.getMediatrixLogger().error(logPrefix + "Error instantiating: com.nttdata.de.sky.ityx.mediatrix.businessrules.server.ServerMultiChannel "+e.getMessage(),e );
			return super.getServerMultiChannel();
		}
	}

	@Override
	public IClientAnswerEdit getClientAnswerEdit() {
		return new ClientAnswerEdit();
	}

	@Override
	public IClientAnswerView getClientAnswerView() {
		return new ClientAnswerView();
	}

	@Override
	public IClientLogin getClientLogin() {
		return new ClientLogin();
	}

	@Override
	public IServerSystem getServerSystem() {
		String logPrefix =  getClass().getName() + "#" + new Object() {}.getClass().getEnclosingMethod().getName()+" ";
		try {
			return (IServerSystem) Class.forName("com.nttdata.de.sky.ityx.mediatrix.businessrules.server.ServerSystem").newInstance();
		} catch (Exception e) {
			SkyLogger.getMediatrixLogger().error(logPrefix + "Error instantiating: com.nttdata.de.sky.ityx.mediatrix.businessrules.server.ServerSystem " + e.getMessage(), e);
			return super.getServerSystem();
		}
	}

	@Override
	public IClientSystem getClientSystem() {
		return new ClientSystem();
	}

	public IServerContexDemon getServerContexDemon() {
		String logPrefix =  getClass().getName() + "#" + new Object() {}.getClass().getEnclosingMethod().getName()+" ";
		try {
			return (IServerContexDemon) Class.forName("com.nttdata.de.sky.ityx.mediatrix.businessrules.server.ServerContexDemon").newInstance();
		} catch (Exception e) {
			SkyLogger.getMediatrixLogger().error(logPrefix + "Error instantiating: com.nttdata.de.sky.ityx.mediatrix.businessrules.server.ServerContexDemon " + e.getMessage(), e);
			return super.getServerContexDemon();
		}
	}

	@Override
	public IClientForwardDialog getClientForwardDialog() {
		return new ClientForwardDialog();
	}

	public IClientClassificationValidation getClientClassificationValidation() {
		SkyLogger.getClientLogger().info(getClass().getName()+"#"+(new Object() {}.getClass().getEnclosingMethod().getName())+ " SKY Factory Request");
		return new ClientClassificationValidation();
	}

	@Override
	public IClientRunner getClientRunner() {
		return new ClientRunner();
	}

	@Override
	public IMXActions_Sky getClientMXAction() {
		return new ClientMXAction();
	}

	@Override
	public IServerMonitor getServerMonitor() {
		String logPrefix =  getClass().getName() + "#" + new Object() {}.getClass().getEnclosingMethod().getName()+" ";
		try {
			return  (IServerMonitor) Class.forName("com.nttdata.de.sky.ityx.mediatrix.businessrules.server.ServerMonitor").newInstance();
		} catch (Exception e) {
			SkyLogger.getMediatrixLogger().error(logPrefix + "Error instantiating: com.nttdata.de.sky.ityx.mediatrix.businessrules.server.ServerMonitor " + e.getMessage(), e);
			return super.getServerMonitor();
		}

	}
}
