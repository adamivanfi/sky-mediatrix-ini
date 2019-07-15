package com.nttdata.de.sky.ityx.mediatrix.businessrules.client;

import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.interfaces.document.CDocument;
import de.ityx.contex.interfaces.extag.TagMatch;
import de.ityx.mediatrix.api.interfaces.businessrules.client.IClientMailInbox;
import de.ityx.mediatrix.api.interfaces.businessrules.client.IClientObjectsControl;
import de.ityx.mediatrix.data.*;
import de.ityx.mediatrix.data.metainformation.MetaInformationDocumentContainer;
import de.ityx.mediatrix.data.metainformation.MetaInformationInt;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;

public class ClientObjectsControl implements IClientObjectsControl {

	private IClientObjectsControl agenturdel = null;
	private final String aclazz = "de.ityx.agentursteuerung.ClientObjectControl";

	public ClientObjectsControl() {
		String logPrefix = "ClientObjectsControl # Constructor ";
		try {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " initalization");
			Class aclass=Class.forName(aclazz);
			if (aclass!=null){
				Constructor aconstr=aclass.getConstructor(null);
				if (aconstr!=null){
					agenturdel = (IClientObjectsControl) aconstr.newInstance(null);
				}
			}
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " initalized");
		} catch (NullPointerException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
			SkyLogger.getBRSLogger().warn(logPrefix + aclazz + " cannot be instantiated." + e.getMessage());
		}
	}

	/*
	 * Will be called after an attachment has been deleted
	 */
	@Override
	public HashMap postAttachmentDelete(de.ityx.mediatrix.data.Attachment attachment, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");

		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.postAttachmentDelete(attachment, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	/*
	 * Will be called after an attachment has been stored
	 */
	@Override
	public HashMap postAttachmentStore(de.ityx.mediatrix.data.Attachment attachment, HashMap hm) {

		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");

		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.postAttachmentStore(attachment, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	/*
	 * Is called before an attachment will be deleted
	 */
	@Override
	public HashMap preAttachmentDelete(de.ityx.mediatrix.data.Attachment attachment, HashMap hm) {

		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.preAttachmentDelete(attachment, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	/*
	 * Is called before an attachment will be loaded by id
	 */
	@Override
	public HashMap preAttachmentLoad(int attachmentId, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.preAttachmentLoad(attachmentId, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	/*
	 * Is called before an attachment will be loaded
	 */
	@Override
	public HashMap preAttachmentLoad(de.ityx.mediatrix.data.Attachment attachment, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");

		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.preAttachmentLoad(attachment, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	/*
	 * Is called before an attachment will be stored
	 */
	@Override
	public HashMap preAttachmentStore(de.ityx.mediatrix.data.Attachment attachment, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");

		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.preAttachmentStore(attachment, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	/*
	 * Will be called after a question has been stored
	 */
	@Override
	public HashMap postQuestionStore(Question question, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");

		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.postQuestionStore(question, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	/*
	 * Is called before a question will be stored.
	 */
	@Override
	public HashMap preQuestionStore(Question question, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");

		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.preQuestionStore(question, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	/*
	 * Will be called after a case has been stored
	 */
	@Override
	public HashMap postCaseStore(Case ccase, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.postCaseStore(ccase, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	/*
	 * Is called before a case will be stored
	 */
	@Override
	public HashMap preCaseStore(Case ccase, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.preCaseStore(ccase, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	/*
	 * Will be called after an answer has been deleted
	 */
	@Override
	public HashMap postAnswerDelete(Answer answer, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.postAnswerDelete(answer, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	/*
	 * Will be called after an answer has been loaded
	 */
	@Override
	public HashMap postAnswerLoad(Answer answer, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.postAnswerLoad(answer, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	/*
	 * Will be called after an answer has been loaded
	 */
	@Override
	public HashMap postAnswerLoadByEmailid(Answer answer, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.postAnswerLoadByEmailid(answer, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	/*
	 * Will be called after an answer has been stored
	 */
	@Override
	public HashMap postAnswerStore(Answer answer, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.postAnswerStore(answer, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	/*
	 * Will be called after an attachment has been loaded
	 */
	@Override
	public HashMap postAttachmentLoad(de.ityx.mediatrix.data.Attachment attachment, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.postAttachmentLoad(attachment, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	/*
	 * Will be called after a case has been deleted
	 */
	@Override
	public HashMap postCaseDelete(Case ccase, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.postCaseDelete(ccase, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	/*
	 * Will be called after a case has been loaded
	 */
	@Override
	public HashMap postCaseLoad(Case ccase, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");

		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.postCaseLoad(ccase, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	/*
	 * Will be called after a customer has been deleted
	 */
	@Override
	public HashMap postCustomerDelete(Customer customer, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");

		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.postCustomerDelete(customer, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	/*
	 * Will be called after a customer has been loaded
	 */
	@Override
	public HashMap postCustomerLoad(Customer customer, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");

		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.postCustomerLoad(customer, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	/*
	 * Will be called after a customer has been stored
	 */
	@Override
	public HashMap postCustomerStore(Customer customer, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");

		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.postCustomerStore(customer, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	/*
	 * Will be called after an operator has been loaded
	 */
	@Override
	public HashMap postOperatorLoad(Operator operator, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");

		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.postOperatorLoad(operator, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	/*
	 * Will be called after an operator has been stored
	 */
	@Override
	public HashMap postOperatorStore(Operator operator, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");

		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.postOperatorStore(operator, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	/*
	 * Will be called after a profile has been deleted
	 */
	@Override
	public HashMap postProfileDelete(Profile profile, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.postProfileDelete(profile, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	/*
	 * Will be called after a profile has been loaded
	 */
	@Override
	public HashMap postProfileLoad(Profile profile, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");

		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.postProfileLoad(profile, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	/*
	 * Will be called after a profile has been stored
	 */
	@Override
	public HashMap postProfileStore(Profile profile, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.postProfileStore(profile, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	/*
	 * Will be called after a project has been deleted
	 */
	@Override
	public HashMap postProjectDelete(Project project, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");

		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.postProjectDelete(project, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	/*
	 * Will be called after a project has been loaded
	 */
	@Override
	public HashMap postProjectLoad(Project project, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");

		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.postProjectLoad(project, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	/*
	 * Will be called after a project has been stored
	 */
	@Override
	public HashMap postProjectStore(Project project, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");

		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.postProjectStore(project, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	/*
	 * Will be called after a question has been deleted
	 */
	@Override
	public HashMap postQuestionDelete(Question question, HashMap hm) {

		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");

		//AgDisabled
		//SkyLogger.getClientLogger().debug("Call agentur delegate.");
		//final HashMap<String, Object> ret = agentur.postQuestionDelete(question, hm);
		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.postQuestionDelete(question, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	/*
	 * Will be called after a question has been loaded
	 */
	@Override
	public HashMap postQuestionLoad(Question question, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");

		if (question == null) {
			SkyLogger.getClientLogger().warn("EmptyQuestion provided to postQuestionLoad");
			//throw new IllegalArgumentException("EmptyQuestion provided to postQuestionLoad");
		} else {
			setTagCaptions(question);
		}
		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.postQuestionLoad(question, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	private void setTagCaptions(Question question) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");

		long acurrentTimeMillis = System.currentTimeMillis();
		try {
			final MetaInformationInt metaDoc = question.getMetaInformation(MetaInformationDocumentContainer.TYPENAME);
			if (metaDoc != null) {
				CDocumentContainer<CDocument> cont = (CDocumentContainer<CDocument>) metaDoc.getContent();
				List<TagMatch> tags = cont.getPage0Tags();
				boolean needsStore = false;
				for (TagMatch tag : tags) {
					final String identifier = tag.getIdentifier();
					String caption = TagMatchDefinitions.getCaption(identifier);
					SkyLogger.getClientLogger().debug("Translating TagMatch: " + identifier + " to: " + caption);
					if (!identifier.equals(caption)) {
						tag.setCaption(caption);
						needsStore = true;
					}
				}
				if (needsStore) {
					metaDoc.setContent(cont);
					question.setMetaInformation(metaDoc);
					//Dieses Store macht gelegentlich Probleme es ist nicht ersichtlich ob es in der V2.4 benötigt wird
					//ggf. sollte der Code von in die Inbound-Rule bewegt werden
					//API.getClientAPI().getQuestionAPI().store(question);
				}
			}
		} catch (Exception e) {
			SkyLogger.getClientLogger().warn("OutboundRule: " + e.getMessage(), e);
			e.printStackTrace();
		}
		if (SkyLogger.getClientLogger().isDebugEnabled()) {
			SkyLogger.getClientLogger().debug("OutboundRule: Funktion executeArchiving_WriteWhitePaperDoc took " + (System.currentTimeMillis() - acurrentTimeMillis) + " " + question.getId());
		}
	}

	/*
	 * Will be called after a lock has been released
	 */
	@Override
	public HashMap postReleaseLock(de.ityx.mediatrix.data.base.Lock lock, String result, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.postReleaseLock(lock,result, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	/*
	 * Will be called after a lock has been set
	 */
	@Override
	public HashMap postSetLock(de.ityx.mediatrix.data.base.Lock lock, de.ityx.mediatrix.data.base.LockResult result, HashMap hm) {
		Class clazz = getClass();
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.postSetLock(lock,result, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	/*
	 * Will be called after a subproject has been deleted
	 */
	@Override
	public HashMap postSubprojectDelete(Subproject subproject, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.postSubprojectDelete(subproject, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	/*
	 * Will be called after a subproject has been loaded
	 */
	@Override
	public HashMap postSubprojectLoad(Subproject subproject, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.postSubprojectLoad(subproject, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	/*
	 * Will be stored after a subproject has been stored
	 */
	@Override
	public HashMap postSubprojectStore(Subproject subproject, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.postSubprojectStore(subproject, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	/*
	 * Is called before an answer will be deleted
	 */
	@Override
	public HashMap preAnswerDelete(Answer answer, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.preAnswerDelete(answer, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	/*
	 * Is Called before an answer will be loaded by id of answer
	 */
	@Override
	public HashMap preAnswerLoad(int answerId, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.preAnswerLoad(answerId, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	/*
	 * Is called before an answer will be loaded by the id of an email
	 */
	@Override
	public HashMap preAnswerLoadByEmailid(int emailId, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.preAnswerLoadByEmailid(emailId, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	/*
	 * Is called before an answer will be stored
	 */
	@Override
	public HashMap preAnswerStore(Answer answer, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.preAnswerStore(answer, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	/*
	 * Is called before a case will be deleted
	 */
	@Override
	public HashMap preCaseDelete(Case ccase, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.preCaseDelete(ccase, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	/*
	 * Is called before a case will be loaded by id
	 */
	@Override
	public HashMap preCaseLoad(int caseId, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.preCaseLoad(caseId, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	/*
	 * Is called before a customer will be deleted
	 */
	@Override
	public HashMap preCustomerDelete(Customer customer, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.preCustomerDelete(customer, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	/*
	 * Is called before a customer will be loaded by id of a customer
	 */
	@Override
	public HashMap preCustomerLoad(int kundeId, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.preCustomerLoad(kundeId, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	/*
	 * Is called before a custober will be stored
	 */
	@Override
	public HashMap preCustomerStore(Customer customer, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.preCustomerStore(customer, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	/*
	 * Is called before an operator will be loaded by id
	 */
	@Override
	public HashMap preOperatorLoad(int operatorId, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.preOperatorLoad(operatorId, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	/*
	 * Is called before an operator will be stored
	 */
	@Override
	public HashMap preOperatorStore(Operator operator, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.preOperatorStore(operator, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	/*
	 * Is called before a profile will be deleted
	 */
	@Override
	public HashMap preProfileDelete(Profile profile, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.preProfileDelete(profile, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	/*
	 * Is called before a profile will be loaded by id
	 */
	@Override
	public HashMap preProfileLoad(int profilId, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.preProfileLoad(profilId, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	/*
	 * Is called before a profile will be stored
	 */
	@Override
	public HashMap preProfileStore(Profile profile, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.preProfileStore(profile, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	/*
	 * Is called before a project will be deleted
	 */
	@Override
	public HashMap preProjectDelete(Project project, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.preProjectDelete(project, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	/*
	 * Is called before a project will be loaded by id
	 */
	@Override
	public HashMap preProjectLoad(int projektId, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.preProjectLoad(projektId, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	/*
	 * Is called before a project will be stored
	 */
	@Override
	public HashMap preProjectStore(Project project, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.preProjectStore(project, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	/*
	 * Is called before a question will be deleted
	 */
	@Override
	public HashMap preQuestionDelete(Question question, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.preQuestionDelete(question, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	/*
	 * Is called before a question will be loaded
	 */
	@Override
	public HashMap preQuestionLoad(int frageId, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.preQuestionLoad(frageId, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	/*
	 * Is called before a lock will be released
	 */
	@Override
	public HashMap preReleaseLock(de.ityx.mediatrix.data.base.Lock lock, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.preReleaseLock(lock, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		return hm;
	}

	/*
	 * Is called before the lock will be set
	 */
	@Override
	public HashMap preSetLock(de.ityx.mediatrix.data.base.Lock lock, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.preSetLock(lock, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		return hm;
	}

	/*
	 * Is called before a subproject will be deleted
	 */
	@Override
	public HashMap preSubprojectDelete(Subproject subproject, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.preSubprojectDelete(subproject, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		return hm;
	}

	/*
	 * Is called before a subproject will be loaded by id
	 */
	@Override
	public HashMap preSubprojectLoad(int subprojectId, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.preSubprojectLoad(subprojectId, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		return hm;
	}

	/*
	 * Is called before a subproject will be stored
	 */
	@Override
	public HashMap preSubprojectStore(Subproject subproject, HashMap hm) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		if (hm==null) {
			hm = new HashMap();
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " start call");
			hm.putAll(agenturdel.preSubprojectStore(subproject, hm));
			SkyLogger.getBRSLogger().info(logPrefix + aclazz + " finish call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		return hm;
	}
}
