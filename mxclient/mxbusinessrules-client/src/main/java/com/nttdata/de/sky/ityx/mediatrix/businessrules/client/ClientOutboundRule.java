package com.nttdata.de.sky.ityx.mediatrix.businessrules.client;

import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.archive.AbstractArchiveMetaData;
import com.nttdata.de.sky.archive.ArchiveMetaDataFactory;
import com.nttdata.de.sky.archive.ClientUtils;
import com.nttdata.de.sky.archive.CustomLine_Sky;
import com.nttdata.de.sky.ityx.common.Actions;
import com.nttdata.de.sky.ityx.common.DocIdGenerator;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import com.nttdata.de.sky.ityx.mediatrix.businessrules.BusinessRule;
import com.nttdata.de.sky.ityx.mediatrix.businessrules.archiving.ArchiveTool;
import com.nttdata.de.sky.pdf.ClientTemplateExtension;
import com.nttdata.de.sky.pdf.PdfFile;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.impl.document.CHeader;
import de.ityx.contex.interfaces.document.CDocument;
import de.ityx.contex.interfaces.extag.TagMatch;
import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.api.IClientAPI;
import de.ityx.mediatrix.api.client.ICQuestion;
import de.ityx.mediatrix.api.interfaces.gui.BaseAnswerEmail;
import de.ityx.mediatrix.client.dialog.Start;
import de.ityx.mediatrix.client.dialog.objects.answer.AnswerEmail;
import de.ityx.mediatrix.client.dialog.operate.QuestionAnswer;
import de.ityx.mediatrix.client.dialog.operate.WaitQueue;
import de.ityx.mediatrix.client.dialog.singlemode.QuestionTablePanel;
import de.ityx.mediatrix.client.dialog.util.newmail.NewMailFrame;
import de.ityx.mediatrix.client.util.QuestionActions;
import de.ityx.mediatrix.client.util.Repository;
import de.ityx.mediatrix.data.*;
import de.ityx.mediatrix.data.metainformation.MetaInformationInt;
import de.ityx.sky.outbound.data.Messages;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

public class ClientOutboundRule extends BusinessRule {

	protected static final String FALSCHER_DOKUMENTENTYP = "Falscher Dokumententyp";
	protected static final String DOKUMENTWEITERLEITUNG = "Dokumentweiterleitung";
	protected static final String DOKUMENTENTYP = "Dokumententyp: ";

	private static final String TITLE_FORWARDING_REASON = "Weiterleitungsgründe";
	private static final String TITLE_CANCEL_REASON = "Kündigungsgrund";
	private static final String TITLE_COMPLETION_REASON = "Detailangaben (mehrere Angaben möglich)";
	private static final Pattern MX_PATTERN = Pattern.compile("\\[mx:action:.+\\]");
	public static final String REASON_FORWARD = "forward";
	public static final String REASON_COMPLETE = "complete";
	public static final String REASON_SEND = "send";

	public static final String[] CHANGING_FIELDS = new String[]{TagMatchDefinitions.CUSTOMER_FIRST_NAME, TagMatchDefinitions.CUSTOMER_LAST_NAME,
			TagMatchDefinitions.CUSTOMER_ADDITIONAL_ADDRESS, TagMatchDefinitions.SBS_COMPANY, TagMatchDefinitions.CUSTOMER_STREET, TagMatchDefinitions.CUSTOMER_ZIP_CODE,
			TagMatchDefinitions.CUSTOMER_CITY, TagMatchDefinitions.CUSTOMER_COUNTRY, TagMatchDefinitions.CONTACT_ID, TagMatchDefinitions.SEPA_MANDATE_NUMBER,
			TagMatchDefinitions.SEPA_SIGNATURE_DATE, TagMatchDefinitions.SEPA_SIGNATURE_FLAG};
	public static final String[] CHANGING_FORM_TYPES = new String[]{
			TagMatchDefinitions.FORM_TYPE, TagMatchDefinitions.FORM_TYPE_CATEGORY};

	public static final int MANDATE_SUBPROJECT = Integer.parseInt(System.getProperty("sepa.subproject", "-1"));
	public static final int SBS_SPAM_SUBPROJECT = Integer.parseInt(System.getProperty("sbs_spam.subproject", "-1"));
	protected static final int SU_RA_KORR_SUBPROJECT = Integer.parseInt(Messages.getString("su_ra_korr.subproject"));

	public static List CANCELLATION_SUBPROJECT;
	public static List SUBPROJECT_FORWARD_PROHIBITED;


	private final AbstractArchiveMetaData questionArchiveMetaData = ArchiveMetaDataFactory.getInstance("com.nttdata.de.sky.ityx.mediatrix.businessrules.archiving.MDocumentArchiveMetaData");

	public ClientOutboundRule() {
		super();
		CANCELLATION_SUBPROJECT = new LinkedList();
		for (String s : StringUtils.split(System.getProperty("cancellation.subproject", "-1"), ",")) {
			if (s != null && !s.isEmpty() && StringUtils.isNumeric(s.trim())) {
				CANCELLATION_SUBPROJECT.add(Integer.parseInt(s.trim()));
			} else {
				SkyLogger.getClientLogger().warn(" CannotRead cancellation.subproject-Value:" + s);
			}
		}
		SUBPROJECT_FORWARD_PROHIBITED = new LinkedList();
		for (String s : StringUtils.split(System.getProperty("subproject.forward.prohibited", "-1"), ",")) {
			if (s != null && !s.isEmpty() && StringUtils.isNumeric(s.trim())) {
				SUBPROJECT_FORWARD_PROHIBITED.add(Integer.parseInt(s.trim()));
			} else {
				SkyLogger.getClientLogger().warn(" CannotRead subproject.forward.prohibited-Value:" + s);
			}
		}
	}

	@Override
	protected Logger getLogger(){
		return SkyLogger.getClientLogger();
	}

	public static void reloadQuestion(Boolean opMode, int questionid, Object ref) {
    ICQuestion questionAPI = API.getClientAPI().getQuestionAPI();
    Question newQuestion = questionAPI.load(questionid, false);
    if (opMode) {
        WaitQueue waitQueue = (WaitQueue) Repository.getObject(Repository.OPERATORMODEWAITLOOP);
        waitQueue.bearbeitefrage(true);
        WaitQueue.showFrage(newQuestion, waitQueue, (QuestionAnswer) Repository.getObject(Repository.QUESTIONANSWER));
    } else {
        final QuestionAnswer qa = (QuestionAnswer) Repository.getObject(Repository.OPERATORMODE);
        qa.refreshData();
                    //qa.refreshAnswer(newAnswer);
        qa.refreshQuestion(newQuestion);
                    qa.reloadFrage(newQuestion);
        questionAPI.refresh(newQuestion);
		QuestionActions.openMailInbox(null, ref, newQuestion, true);
    }
}

	public static void refreshTable() {
        QuestionTablePanel frtb;
        if (Repository.hasObject(Repository.QUESTIONTABLEPANEL)) {
            frtb = (QuestionTablePanel) Repository.getObject(Repository.QUESTIONTABLEPANEL);
            frtb.performRefresh();
        }
    }


	protected void previewAnswer(final BaseAnswerEmail answerEmailPanel, final Answer answer, final Question question, JButton jbPreview) {
		ActionListener[] listeners = jbPreview.getActionListeners();
		for (int i = 0; i < listeners.length; i++) {
			jbPreview.removeActionListener(listeners[i]);
		}
		jbPreview.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (answer.getType() == Email.TYPE_LETTER || answer.getType() == Email.TYPE_FAX) {
						previewLetter(((AnswerEmail) answerEmailPanel).getText(), ((AnswerEmail) answerEmailPanel).getAttachments(), question, answer);
					} else {
						answerEmailPanel.preview();
					}
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(Start.getInstance(), ClientUtils.exception(ex).toString());
				}
			}

		});
	}

	/**
	 * @param text
	 * @param question
	 * @param answer
	 * @throws Exception
	 */
	protected void previewLetter(final String text, List<Attachment> attachments, final Question question, Answer answer) throws Exception {
		checkFirstName(question);
		Map<String, String> metaMap = questionArchiveMetaData.collectMetadata(null, question);
		answer.setBody(text);
		final String documentid = metaMap.get(TagMatchDefinitions.DOCUMENT_ID);
		HashMap<String, Object> parameter = CustomLine_Sky.getParameter(answer, metaMap, documentid, true);
		parameter.put("loginname", API.getClientAPI().getConnectionAPI().getCurrentOperator().getUserId());
		byte[] pdf = ClientTemplateExtension.getInstance().createPdf(new PdfFile(text, question.getSubprojectId(), question.getLanguage(), TagMatchDefinitions.isNotSbsProject(question), parameter), null);
		SkyLogger.getClientLogger().info("PreviewLetter: q:" + question.getId() + " tp:" + Integer.parseInt(metaMap.get(TagMatchDefinitions.MX_TP_ID)) + " l:" + Integer.parseInt(metaMap.get(TagMatchDefinitions.MX_LANGUAGE)));

		// BRSessionPoolFactory.getBRSessionPool().getBRSession("SkyLetter")
		// .put(key, value);
		ClientUtils.preview(pdf);
	}

	
	protected boolean askCompletionParametersAndExecuteAction(String loginname, Question question, Answer answer, String action, Map<String, Set<String>> reasoncodes, boolean alwaysAskForReasons) {
	
		if (reasoncodes == null) {
			reasoncodes = new HashMap<String, Set<String>>();
		}


		boolean noSBS = TagMatchDefinitions.isNotSbsProject(question);
		switch (action) {
			case REASON_FORWARD:
				if (!reasoncodes.containsKey(REASON_FORWARD) || alwaysAskForReasons) {
					String freason = noSBS ? selectForwardingReason(loginname, question) : null;
					if (freason != null && freason.isEmpty()) {
						Set<String> fset = new HashSet<>();
						fset.add(freason);
						reasoncodes.put(REASON_FORWARD, fset);
					} else if (noSBS){
							return false;
					}
				}
				break;
			case REASON_COMPLETE:
				if (!reasoncodes.containsKey(REASON_COMPLETE) || alwaysAskForReasons) {
					Set<String> completeReasons = noSBS ? selectCompletionReason(loginname, question) : null;
					if (completeReasons != null && !completeReasons.isEmpty()) {
						reasoncodes.put(REASON_COMPLETE, completeReasons);
					} else if (noSBS) {
						return false;
					}
				}
				break;
			case REASON_SEND:
				if (!reasoncodes.containsKey(REASON_SEND) || alwaysAskForReasons) {
					Set<String> sendReasons = noSBS ? selectSendReason(loginname, question) : null;
					if (sendReasons != null && !sendReasons.isEmpty()) {
						reasoncodes.put(REASON_SEND, sendReasons);
					} else if (noSBS){
						return false;
					}
				}
				break;
		}
		return executeCompletionActions(loginname, question, answer, action, reasoncodes);
	}


	protected boolean executeCompletionActions(String loginname, Question question, Answer answer, String action, Map<String, Set<String>> reasoncodes) {
		int logAction = OperatorLogRecord.ACTION_INFO;
		if (answer != null && answer.getStatus().equals(Answer.S_INTERMEDIATE)) {
			logAction = OperatorLogRecord.ACTION_INTERMEDIATE_ANSWER;
		}
		SkyLogger.getClientLogger().debug("Action: "+action);
		SkyLogger.getClientLogger().debug("Reasoncodes: "+reasoncodes);
		Set<String> actioncodes = reasoncodes.get(action);
		if(actioncodes != null) {
			for (String reason : actioncodes) {
				logReason(question, loginname, reason, logAction);
			}
		}


		return true;
	}

	protected static Set<String>  selectCompletionReason(String loginname, Question question) {
		return selectMultipleReason(question, ClientOutboundRule.REASON_COMPLETE, TITLE_COMPLETION_REASON);
	}

	protected Set<String> selectSendReason(String loginname, Question question) {
		return selectMultipleReason(question, ClientOutboundRule.REASON_SEND, TITLE_COMPLETION_REASON);
	}

	
	protected static String selectForwardingReason(String loginname, Question question) {
		String reason = selectSingleReason(question, ClientOutboundRule.REASON_FORWARD, TITLE_FORWARDING_REASON);
		if (reason != null && !reason.isEmpty() && reason.startsWith(FALSCHER_DOKUMENTENTYP)) {
			CDocument document = question.getDocumentContainer().getDocument(0);
			String formtype = document.getFormtype();
			String newType = selectFormtype(question, document);
			reason = DOKUMENTENTYP + formtype + " -> " + newType;
		}
		return reason;
	}

	protected static String selectSingleReason(Question question, String action, String title) {
		List<String> possibleReasons = getReasons(action, question);
		return selectRadioOption(title, possibleReasons);
	}

	protected static Set<String> selectMultipleReason(Question question, String action, String title) {
		List<String> possibleReasons = getReasons(action, question);
		return selectOptions(title, possibleReasons, false);
	}

	private static String selectFormtype(Question question, CDocument document) {

		Vector<String> formtypes = new Vector<String>();
		formtypes.addAll(ClientOutboundRule.loadFormtypeKeywords((question.getProjectId() > 0) ? question.getProjectId() : 110));
		String newType = null;
		GridBagConstraints c = new GridBagConstraints();
		JPanel inputPanel = new JPanel(new GridBagLayout());
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		JComboBox catBox = new JComboBox(formtypes);
		String formtype = document.getFormtype();
		catBox.setSelectedItem(formtype);
		inputPanel.add(catBox, c);
		int dialog = JOptionPane.showConfirmDialog(null, inputPanel, "Auswahl Dokumententyp", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		boolean okOption = dialog == JOptionPane.OK_OPTION;
		if (okOption) {
			// TODO: Check Keywords and Tags.
			newType = (String) catBox.getSelectedItem();

			document.setFormtype(newType);
			question.setHeaders(TagMatchDefinitions.addOrReplaceXTMHeader(question.getHeaders(), TagMatchDefinitions.X_TAGMATCH_FORM_TYPE, newType));
			List<TagMatch> tags = document.getTags();
			for (TagMatch tag : tags) {
				String identifier = tag.getIdentifier();
				if (identifier.equals(TagMatchDefinitions.FORM_TYPE_CATEGORY) || identifier.equals(TagMatchDefinitions.DOCUMENT_TYPE)) {
					SkyLogger.getClientLogger().info(tag.getIdentifier() +
							" : " + tag.getTagValue());
					tag.setTagValue(newType);
					SkyLogger.getClientLogger().info(tag.getIdentifier() +
							" : " + tag.getTagValue());
				}
			}
			//					List<Keyword> keywords = question.getKeywords();
			//					for (Keyword key : keywords) {
			//						SkyLogger.getClientLogger().info(key);
			//						if (key.getNodeName().equals(formtype)) {
			//							key.setName(newType);
			//						}
			//						SkyLogger.getClientLogger().info(key);
			//					}
		}
		return newType;
	}

	protected boolean checkFirstName(Question question) {
		boolean ret = false;
		String headers = question.getHeaders();

		if (TagMatchDefinitions.isNotSbsProject(question)) {
			String firstName = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.X_TAGMATCH_CUSTOMER_FIRST_NAME);
			if (firstName == null || firstName.trim().length() == 0) {
				final String message = "Die Kundendaten enthalten keinen Vornamen.\nBitte geben Sie an, welcher Wert anstelle des Vornamens im Adressblock erscheinen soll.";
				final String title = "Fehlendes Adressfeld";
				String name = JOptionPane.showInputDialog(null, message, title, JOptionPane.QUESTION_MESSAGE);
				headers = TagMatchDefinitions.addOrReplaceXTMHeader(headers, TagMatchDefinitions.X_TAGMATCH_CUSTOMER_FIRST_NAME, name != null ? name : " ");
				question.setHeaders(headers);
				API.getClientAPI().getQuestionAPI().store(question);
			}
			ret =  true;
		} else {

			if (checkSbsAddress(headers)) {
				ret = true;
			}
			else {
				SkyLogger.getClientLogger().debug("Read address headers:\n" + headers);
				Map<String, String> addressFields = changeAddressFields(question);
				if (checkSbsAddress(addressFields)) {
					SkyLogger.getClientLogger().debug("Setting address headers:\n" + headers);
					setAddressFields(question, headers, addressFields);
					ret =  true;
				}
			}
		}
		SkyLogger.getClientLogger().debug("Return value: " + ret);
		return ret;
	}

	public static boolean checkSbsAddress(String headers) {
		boolean hasAddress = true;
		boolean hasName = true;
        int checkNameAddress=0;
		for (String addressField : ClientOutboundRule.CHANGING_FIELDS) {
            String value = TagMatchDefinitions.extractXTMHeader(headers, addressField);
            SkyLogger.getClientLogger().info("checkSbsAddress: <" + addressField + "," + value + "> hasAddress:" + hasAddress + " hasName:" + hasName);
            /*Ticket 244911*/
            hasName = hasName
                    && (value != null
                    && !value.isEmpty()
                    || !(addressField.equalsIgnoreCase(TagMatchDefinitions.CUSTOMER_FIRST_NAME)
                    || addressField.equalsIgnoreCase(TagMatchDefinitions.CUSTOMER_LAST_NAME)
                    || addressField.equalsIgnoreCase(TagMatchDefinitions.SBS_COMPANY)));
            if(!hasName) checkNameAddress++;
            /*old Code
            hasName = hasName
                    && (value != null
                    && !value.isEmpty()
                    || (addressField.equalsIgnoreCase(TagMatchDefinitions.CUSTOMER_FIRST_NAME)
                    || addressField.equalsIgnoreCase(TagMatchDefinitions.CUSTOMER_LAST_NAME)
                    || addressField.equalsIgnoreCase(TagMatchDefinitions.SBS_COMPANY)));
            */
            hasAddress = hasAddress
                    && (value != null
                    && !value.isEmpty()
                    || !(addressField.equalsIgnoreCase(TagMatchDefinitions.CUSTOMER_STREET)
                    || addressField.equalsIgnoreCase(TagMatchDefinitions.CUSTOMER_ZIP_CODE)
                    || addressField.equalsIgnoreCase(TagMatchDefinitions.CUSTOMER_CITY)));
            SkyLogger.getClientLogger().info("checkSbsAddress: <" + addressField + "," + value + "> hasAddress:" + hasAddress + " hasName:" + hasName + " checkNameAddress:" + checkNameAddress);

        }
        if (checkNameAddress>0) hasName=false;
		hasAddress = hasAddress && hasName;

		SkyLogger.getCommonLogger().info("checkSbsAddress: " + hasAddress + " " + hasName);
		return hasAddress;
	}

	public static boolean checkSbsAddress(Map<String, String> fields) {
		boolean hasAddress = true;
		boolean hasName = true;
		for (String addressField : ClientOutboundRule.CHANGING_FIELDS) {
			String value = fields.get(addressField);
			SkyLogger.getClientLogger().info("checkSbsAddress_2: <" + addressField + "," + value + "> hasAddress:" + hasAddress + " hasName:" + hasName);
            /*Ticket 244911*/
            hasName = hasName
                    && (value != null
                    && !value.isEmpty()
                    || !(addressField.equalsIgnoreCase(TagMatchDefinitions.CUSTOMER_FIRST_NAME)
                    || addressField.equalsIgnoreCase(TagMatchDefinitions.CUSTOMER_LAST_NAME)
                    || addressField.equalsIgnoreCase(TagMatchDefinitions.SBS_COMPANY)));
			/* old code
            hasName = hasName
					|| (value != null
					&& !value.isEmpty()
					&& (addressField.equalsIgnoreCase(TagMatchDefinitions.CUSTOMER_FIRST_NAME)
					|| addressField.equalsIgnoreCase(TagMatchDefinitions.CUSTOMER_LAST_NAME)
					|| addressField.equalsIgnoreCase(TagMatchDefinitions.SBS_COMPANY)));
			*/
			hasAddress = hasAddress
					&& (value != null
					&& !value.isEmpty()
					|| !(addressField.equalsIgnoreCase(TagMatchDefinitions.CUSTOMER_STREET)
					|| addressField.equalsIgnoreCase(TagMatchDefinitions.CUSTOMER_ZIP_CODE)
					|| addressField.equalsIgnoreCase(TagMatchDefinitions.CUSTOMER_CITY)));
		}
		hasAddress = hasAddress && hasName;
		SkyLogger.getClientLogger().info("checkSbsAddress_2: " +hasAddress+" "+hasName);
		return hasAddress;
	}

	/**
	 * @param question
	 * @param loginname
	 * @param reason
	 * @param action
	 */
	protected void logReason(Question question, String loginname, String reason, int action) {
		API.getClientAPI().getQuestionAPI().protocolLog(question, loginname + ":" + reason, action);
	}

	protected static Set<String> selectOptions(String title, List<String> reasons, boolean radio) {
		Set<String> selection = new TreeSet<String>();
		if ("1".equals(System.getProperty("de.ityx.stress"))) {
			if (System.getProperty("de.ityx.stress.delay") != null) {
				try {
					long delay = Long.parseLong(System.getProperty("de.ityx.stress.delay"));
					Thread.sleep(delay);
				} catch (Exception e) {
					e.printStackTrace();
				}
				selection.add("STRESS");
			}

		} else if (radio) {
			String option = selectRadioOption(title, reasons);
			if (option != null) {
				selection.add(option);
			}
		} else {
			selection.addAll(selectCheckOptions(title, reasons));
		}
		return selection;
	}

	protected static String selectRadioOption(String title, List<String> reasons) {

		String reason = null;
		GridBagConstraints c = new GridBagConstraints();
		JPanel inputPanel = new JPanel(new GridBagLayout());
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		ButtonGroup reasonGroup = new ButtonGroup();
		for (String reasonOption : reasons) {
			JRadioButton rButton = new JRadioButton(reasonOption);
			rButton.setActionCommand(reasonOption);
			reasonGroup.add(rButton);
			c.gridy++;
			inputPanel.add(rButton, c);
		}
		while (reason == null) {
			int dialog = JOptionPane.showConfirmDialog(null, inputPanel, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
			boolean okOption = dialog == JOptionPane.OK_OPTION;
			if (okOption) {
				ButtonModel selection = reasonGroup.getSelection();
				if (selection == null) {
					JOptionPane.showMessageDialog(null, "Bitte einen Grund auswählen.");
				} else {
					reason = selection.getActionCommand();
				}
			} else {
				break;
			}
		}
		return reason;
	}

	protected static Set<String> selectCheckOptions(String title, List<String> reasons) {
		Set<String> selection = new TreeSet<String>();

		GridBagConstraints c = new GridBagConstraints();
		JPanel inputPanel = new JPanel(new GridBagLayout());
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;

		Set<JCheckBox> boxSet = new HashSet<JCheckBox>();
		for (String reasonOption : reasons) {
			JCheckBox rBox = new JCheckBox(reasonOption);
			c.gridy++;
			inputPanel.add(rBox, c);
			boxSet.add(rBox);
		}
		int dialog = JOptionPane.showConfirmDialog(null, inputPanel, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		boolean okOption = dialog == JOptionPane.OK_OPTION;
		if (okOption) {
			for (JCheckBox box : boxSet) {
				if (box.isSelected()) {
					selection.add(box.getText());
				}
			}
		}
		return selection;
	}


	private static Map<String, List<String>> reasonscache = new LinkedHashMap<>();

	protected static List<String> getReasons(String action, Question question) {

		if (reasonscache.get(action) != null) {
			return reasonscache.get(action);
		}

		List<Object> parameter = new ArrayList<Object>();
		parameter.add((question.getProjectId() > 0) ? question.getProjectId() : 110);
		parameter.add(action);

		Object result = (List<Object>) API.getClientAPI().getConnectionAPI().sendServerEvent(Actions.ACTION_GET_REASONS.name(), parameter);
		if (result == null || result instanceof String) {
			SkyLogger.getClientLogger().fatal("Problem getting Reasons: " + question.getId() + ":" + result);
			return new LinkedList<String>();
		} else {
			Object result2 = ((List<Objects>) result).get(0);
			if (result2 == null || result2 instanceof String) {
				SkyLogger.getClientLogger().fatal("Problem2 getting Reasons: " + question.getId() + ":" + result2);
				return new LinkedList<String>();
			} else if (result2 instanceof List) {
				reasonscache.put(action, (List<String>) result2);
				return (List<String>) result2;
			} else {
				SkyLogger.getClientLogger().fatal("Problem3 getting Reasons: " + question.getId() + ":" + result2);
				return new LinkedList<String>();
			}
		}
	}

	/**
	 * @param question
	 * @return
	 */
	protected String selectReplyAdress(Question question) {
		String domain = null;
		String country = TagMatchDefinitions.extractXTMHeader(question.getHeaders(), TagMatchDefinitions.CUSTOMER_COUNTRY);
		if (country != null && country.length() > 1) {
			country = country.substring(1);
		}
		if (country != null && country.equalsIgnoreCase(BusinessRule.AT)) {
			domain = ".at";
		}
		if (country != null && country.equalsIgnoreCase(BusinessRule.DE)) {
			domain = ".de";
		}
		final int teilprojektId = question.getSubprojectId();
		final IClientAPI clientAPI = API.getClientAPI();
		final Subproject subproject = clientAPI.getSubprojectAPI().load(teilprojektId);
		final String originalReply = subproject.getReplyAddress();
		List<String> addressList = new ArrayList<String>();
		try {
			String globalString = clientAPI.getServiceCenterAPI().getGlobalString("alternative_rueckantwort." + teilprojektId, originalReply);
			if (globalString != null) {
				String[] adressen = globalString.split(";");
				for (String address : adressen) {
					if ((domain == null || address.endsWith(domain)) && !addressList.contains(address)) {
						addressList.add(address);
					}
				}
			}
			if ((domain == null || originalReply.endsWith(domain)) && !addressList.contains(originalReply)) {
				addressList.add(0, originalReply);
			}
		} catch (Throwable t) {
			t.printStackTrace();
			SkyLogger.getClientLogger().fatal("RuntimeException: " + t.getCause());
		}
		if (addressList.size() == 0) {
			addressList.add(originalReply);
		}
		String replyAdress = addressList.get(0);
		if (addressList.size() > 1) {
			final Set<String> selectedAddress = selectOptions("Absender auswählen", addressList, true);
			if (selectedAddress.size() > 0) {
				String tmpAdress = selectedAddress.iterator().next();
				if (!tmpAdress.equals("none")) {
					replyAdress = tmpAdress;
				}
			} else {
				replyAdress = null;
			}
		}
		SkyLogger.getClientLogger().debug("Selected ReplyAdress: " + replyAdress);
		return replyAdress;
	}

	protected Map<String, String> changeAddressFields(Question queEmail) {
		Map<String, String> addressFields = new TreeMap<String, String>();
		String headers =  queEmail.getHeaders();
		GridBagConstraints c = new GridBagConstraints();
		JPanel inputPanel = new JPanel(new GridBagLayout());
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		Set<JTextField> fieldSet = new HashSet<JTextField>();
		//inputPanel.setPreferredSize(new Dimension(400,300));
		for (String addressField : ClientOutboundRule.CHANGING_FIELDS) {
			if (!addressField.equals("ContactID")
					&& !addressField.equals(TagMatchDefinitions.SEPA_MANDATE_NUMBER)
					&& !addressField.equals(TagMatchDefinitions.SEPA_SIGNATURE_DATE)
					&& !addressField.equals(TagMatchDefinitions.SEPA_SIGNATURE_FLAG)
					&& !(TagMatchDefinitions.isNotSbsProject(queEmail) && addressField.equalsIgnoreCase(TagMatchDefinitions.SBS_COMPANY)) ) {
				String headerField = TagMatchDefinitions.extractXTMHeader(headers, addressField);
				headerField = headerField == null ? "" : headerField;
				JTextField textField = new JTextField(headerField, 20);
				textField.setName(addressField);
				c.gridy++;
				c.gridx = 0;
				c.fill = GridBagConstraints.NONE;

				if (addressField.equalsIgnoreCase("CustomerFirstName")) {
					inputPanel.add(new JLabel("Vorname"), c);
				} else if (addressField.equalsIgnoreCase("CustomerLastName")) {
					inputPanel.add(new JLabel("Nachname"), c);
				} else if (addressField.equalsIgnoreCase(TagMatchDefinitions.CUSTOMER_ADDITIONAL_ADDRESS)
							&& queEmail.getSubprojectId() == SU_RA_KORR_SUBPROJECT) {
					inputPanel.add(new JLabel("Additional adress line"), c);
				} else if (addressField.equalsIgnoreCase(TagMatchDefinitions.SBS_COMPANY)) {
					inputPanel.add(new JLabel("Firma"), c);
				} else if (addressField.equalsIgnoreCase("CustomerStreet")) {
					inputPanel.add(new JLabel("Straße"), c);
				} else if (addressField.equalsIgnoreCase("CustomerZipCode")) {
					inputPanel.add(new JLabel("PLZ"), c);
				} else if (addressField.equalsIgnoreCase("CustomerCity")) {
					inputPanel.add(new JLabel("Ort"), c);
				} else if (addressField.equalsIgnoreCase("CustomerCountry")) {
					inputPanel.add(new JLabel("Land"), c);
				} else {
					inputPanel.add(new JLabel(addressField.replaceFirst("Customer", "")), c);
				}
				c.gridx = 1;
				c.fill = GridBagConstraints.WEST;
				inputPanel.add(textField, c);
				fieldSet.add(textField);
			}
		}
		int dialog = JOptionPane.showConfirmDialog(null, inputPanel, "Adressdaten", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		boolean okOption = dialog == JOptionPane.OK_OPTION;
		if (okOption) {
			for (JTextField field : fieldSet) {
					addressFields.put(field.getName(), field.getText());
			}
		}
		return addressFields;
	}

	/**
	 * @param loginname
	 * @param question
	 */
	protected void logCompleted(String loginname, Question question) {
		logReason(question, loginname, "Abschluss", OperatorLogRecord.ACTION_INFO);
	}

	/**
	 * @param loginname
	 * @param question
	 * @param answer
	 * @return
	 */
	protected boolean selectPreSend(String loginname, Question question, Answer answer) {
		String selectedAdress = selectReplyAdress(question);
		if (selectedAdress != null) {
			answer.setFrom(selectedAdress);
			// Sets "Reply-To" header.
			answer.setReturnPath(selectedAdress);
			return askCompletionParametersAndExecuteAction(loginname, question, answer, ClientOutboundRule.REASON_SEND, null, false);

		}
		return false;
	}

	/**
	 * Loads the formtype keywords.
	 *
	 * @param projectId The project which defines the keywords
	 * @return A list containing the formtypes
	 */
	public static Vector<String> loadFormtypeKeywords(int projectId) {
		Project project = API.getClientAPI().getProjectAPI().load(projectId);
		int pkId = -1;
		for (AbstractKeywordCategory kc : project.getKeywordCategory()) {
			if (kc.getName().equals(TagMatchDefinitions.FORM_TYPE_CATEGORY)) {
				pkId = kc.getId();
				break;
			}
		}
		Vector<String> keywords = new Vector<String>();
		for (Keyword k : project.getKeywords()) {
			if (k.getParentId() == pkId && !k.getNodeName().equalsIgnoreCase("kuendigung_automatisch")) {
				keywords.add(k.getNodeName());
			}
		}
		if (!keywords.contains(TagMatchDefinitions.DEFAULT_FORMTYPE)) {
			keywords.add(TagMatchDefinitions.DEFAULT_FORMTYPE);
		}
		return keywords;
	}

	/**
	 * Loads the keyword of a category.
	 *
	 * @param projectId   The project which defines the keywords
	 * @param keywordName The name of the keyword
	 * @return The keyword
	 */
	protected Keyword loadCategoryKeyword(int projectId, String keywordCategory, String keywordName) {
		SkyLogger.getClientLogger().debug(" category:" + keywordCategory + " name:" + keywordName);
		Project project = API.getClientAPI().getProjectAPI().load(projectId);
		int pkId = -1;
		for (AbstractKeywordCategory kc : project.getKeywordCategory()) {
			if (kc.getName().equals(keywordCategory)) {
				pkId = kc.getId();
				break;
			}
		}
		SkyLogger.getClientLogger().debug(" category-id:" + pkId);
		for (Keyword k : project.getKeywords()) {
			SkyLogger.getClientLogger().debug(" test:" + k.getName());
			if (k.getParentId() == pkId && k.getNodeName().equals(keywordName)) {
				return k;
			}
		}
		return null;
	}

	/**
	 * Checks if all mx:action-tags have been substituted.
	 *
	 * @param text Contains the text to be checked.
	 */
	public static boolean checkMXTags(final String text) {
		boolean ret = true;
		if (ClientOutboundRule.MX_PATTERN.matcher(text).find()) {
			ret = false;
			JOptionPane.showMessageDialog(null, "Siebel-Parameter bitte manuell über den Button in der Bearbeitungsleiste auflösen!");
		}
		return ret;
	}

	/**
	 * @param loginname
	 * @param question
	 * @param subproject
	 * @return
	 */
	protected boolean checkForwarding(String loginname, Question question, Subproject subproject, int type) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ": " + question.getId() + " ";
		SkyLogger.getClientLogger().info(logPrefix + "CheckForwaring:START: type:" + type);

		boolean forwarding = false;
		boolean notSbs = TagMatchDefinitions.isNotSbsProject(question);
		String forwardingReason = null;
		if (notSbs && isValidSubprojectForwarding(subproject, question, type)) {
			forwardingReason = selectForwardingReason(loginname, question);
			if (forwardingReason != null) {
				addForwardingKeywordLog(loginname, question, forwardingReason);
			}
			forwarding = forwardingReason != null;
		}
		SkyLogger.getClientLogger().info(logPrefix + "CheckForwaring:END: " + forwardingReason + " ok?:" + forwarding +" noSBS?:"+notSbs);

		return forwarding || !notSbs;
	}

	/**
	 * @param subproject
	 * @return
	 */
	protected boolean isValidSubprojectForwarding(Subproject subproject, Question question, int type) {
		boolean result = false;
		if (subproject != null && subproject.getName() != null && !subproject.getName().isEmpty()) {
            // Check forwarding  from property file. Gregory Verbitsky
			if (SUBPROJECT_FORWARD_PROHIBITED.contains(subproject.getId())) {
				JOptionPane.showMessageDialog(null,
						"Eine Weiterleitung an dieses Teilprojekt \"" + subproject.getName() + "\" ist nicht möglich.",
						"Fehler",
						JOptionPane.ERROR_MESSAGE);
			} else {
				result=true;
			}
		} else {
			// TODO: check external forwarding
			String logPrefix = getClass().getName() + "#" + new Object() {
			}.getClass().getEnclosingMethod().getName();
			SkyLogger.getClientLogger().info(logPrefix + "ExternalForwarding:" + question.getId() + " type:" + type);
			result = true;
		}
		return result;
	}

	/**
	 * Returns true if mandate metadata is missing.
	 */
	protected boolean isQuestionNotReadyForClose(String loginname, final Question question) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ": " + question.getId() + " ";
		SkyLogger.getClientLogger().debug(logPrefix + ": enter ");

		if (question.getId() == 0) {
			SkyLogger.getClientLogger().debug(logPrefix + "Frage hat noch keine ID, und wird zwischengespeichert");
			API.getClientAPI().getQuestionAPI().store(question);
			logPrefix = getClass().getName() + "#" + new Object() {
			}.getClass().getEnclosingMethod().getName() + ": " + question.getId() + " ";
			SkyLogger.getClientLogger().debug(logPrefix + " Frage gespeichert");
		}

		if (TagMatchDefinitions.isSbsProject(question)) {
			SkyLogger.getClientLogger().debug(logPrefix + ": SBS checking " + question.getId());
			if(question.getSubprojectId() == SBS_SPAM_SUBPROJECT) {
				return false;
			}
			SkyLogger.getClientLogger().info(logPrefix + ": SBS .");
			String headers = question.getHeaders();
			SkyLogger.getClientLogger().info(logPrefix + ": headers " + headers);
			String customerId = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.CUSTOMER_ID);
			if(customerId == null || customerId.isEmpty() || customerId.equals("0")) {
				GridBagConstraints c = new GridBagConstraints();
				JPanel inputPanel = new JPanel(new GridBagLayout());
				c.gridx = 0;
				c.gridy = 0;
				c.anchor = GridBagConstraints.WEST;
				JLabel customerLabel = new JLabel("Kundennummer: ");
				inputPanel.add(customerLabel, c);
				c.gridx = 1;
				c.gridy = 0;
				c.anchor = GridBagConstraints.EAST;
				JTextField customerField = new JTextField(10);
				customerField.setEditable(true);
				inputPanel.add(customerField, c);
				int dialog = JOptionPane.showConfirmDialog(null, inputPanel, "Neuzuordnung Kunden", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
				String customerID = customerField.getText();
				String customerNumber = customerID!=null?customerID.replaceAll("[\\.\\s]", ""):null;

				if (dialog == JOptionPane.OK_OPTION && customerNumber!=null && !customerNumber.matches("\\d{10}|0|P\\d*") && !customerNumber.isEmpty()) {
					JOptionPane.showMessageDialog(null, "Kundennummerformat entspricht nicht der Vorgaben (10-Stellig)");
					dialog = JOptionPane.showConfirmDialog(null, inputPanel, "Neuzuordnung Kunden", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
					customerID = customerField.getText();
					customerNumber = customerID!=null?customerID.replaceAll("[\\.\\s]", ""):null;
				}
				if (dialog == JOptionPane.OK_OPTION && customerNumber!=null && customerNumber.matches("\\d{10}")) {
					sbsIndex(question, customerNumber, true);
				}else {
					JOptionPane.showMessageDialog(null, "Abschluss ohne Kundennummer nur im Spam-Teilprojekt möglich.");
					return true;
				}
			}
		}else{
			String formtype = getFormtype(null, question);
			if ((question.getSubprojectId() == MANDATE_SUBPROJECT) && !(formtype != null && formtype.equals(TagMatchDefinitions.SEPA_MANDATE))) {
				JOptionPane.showMessageDialog(null, "Ausschließlich SEPA Mandate können im Mandate-Teilprojekt erledigt werden. Falscher Dokumenttyp oder falsches Teilprojekt: <QuestionId=" + question.getId() + ">");
				return true;
			}
		}

		if (ArchiveTool.checkIsArchivingNeeded(question)) {
			if (ArchiveTool.checkIsArchivingPossible(question)) {
				SkyLogger.getClientLogger().debug(logPrefix + "Archiving for Question:" + question.getId() + " needed and possible.");
			}else if (TagMatchDefinitions.isSbsProject(question) ) {
				SkyLogger.getClientLogger().warn(logPrefix + "Archiving for Question: SBS: " + question.getId() + " not possible?");
			} else {
				SkyLogger.getClientLogger().warn(logPrefix + "Archiving for Question:" + question.getId() + " needed but not possible.");
				String check = System.getProperty("metacompletecheckdisabled", "false");
				if (check != null && !check.isEmpty() && !check.equalsIgnoreCase("true")) {
					JOptionPane.showMessageDialog(null, "Es fehlen notwendige Meta-Daten um die Frage:" + question.getId() + " abzuschliessen. Bitte Kunden Re-indizieren.");
					return true;
				}
			}
		} else {
			SkyLogger.getClientLogger().debug(logPrefix + "Archiving not needed for Question:" + question.getId());
		}
		return false;
	}

	protected String createSEPACopy(Connection dcon, CDocumentContainer<CDocument> out, MetaInformationInt metaDoc) throws CloneNotSupportedException {
		CDocumentContainer<CDocument> con = (CDocumentContainer<CDocument>) metaDoc.getContent();
		CDocument document = con.getDocument(0);

		CDocument newdoc = document.clone();
		//if (document.getUri() != null)
		//	newdoc.setUri(document.getUri());
		//if (document.getWhiteKey() != null)
		//	newdoc.setWhiteKey(document.getWhiteKey());
		if (document.headers() != null) {
			List<CHeader> l = new LinkedList<CHeader>();
			Iterator<CHeader> i = document.headers();
			while (i.hasNext()) {
				l.add(i.next());
			}
			newdoc.setHeaders(l);
		}
		if (document.getTitle() != null)
			newdoc.setTitle(document.getTitle());
		if (document.getAnnotations() != null)
			newdoc.setAnnotations(document.getAnnotations());
		if (document.getTags() != null)
			newdoc.setTags(document.getTags());

		for (Map.Entry<String, Object> note : document.getNotes().entrySet()) {
			newdoc.setNote(note.getKey(), note.getValue());
		}

		newdoc.setFormtype(TagMatchDefinitions.SEPA_MANDATE);
		newdoc.setNote(TagMatchDefinitions.EVAL_FORMTYPE, TagMatchDefinitions.SEPA_MANDATE);


		String docId = document.getNote(TagMatchDefinitions.DOCUMENT_ID) + "-SEPA";
		String newDocID = DocIdGenerator.createUniqueDocumentId(dcon, TagMatchDefinitions.DocumentDirection.SPLITTED, TagMatchDefinitions.Channel.BRIEF, docId);

		newdoc.setNote(TagMatchDefinitions.DOCUMENT_ID, newDocID);
		out.addDocument(newdoc);
		out.setExternalID(newDocID);
		out.setNote("DocumentID", newDocID);


		List<TagMatch> newTags = new ArrayList<TagMatch>();
		final List<TagMatch> tags = con.getTags();
		for (TagMatch tm : tags) {
			final String identifier = tm.getIdentifier();
			if (identifier.equals(TagMatchDefinitions.DOCUMENT_ID)) {
				newTags.add(new TagMatch(identifier, newDocID));
			} else {
				newTags.add(new TagMatch(identifier, tm.getTagValue()));
			}
		}
		out.setTags(newTags);

		if (con.getNotes() != null) {
			for (java.util.Map.Entry<String, Object> note1 : con.getNotes().entrySet()) {
				out.setNote(note1.getKey(), note1.getValue());
			}
		}
		return newDocID;
	}

	
        
/*
		protected String generateDocumentId(Question question, TagMatchDefinitions.DocumentDirection doctype, TagMatchDefinitions.Channel channel, String parentDocID) {
		List<Object> parameter = new ArrayList<Object>();
		parameter.add(question.getProjectId());
		parameter.add(doctype);
                parameter.add(channel);
                if (parentDocID != null)
			parameter.add(parentDocID);
		List<Object> result = (List<Object>) API.getClientAPI().getConnectionAPI().sendServerEvent(Actions.ACTION_CREATE_DOCUMENT_ID.name(), parameter);
		String documentid = (String) result.get(0);
		question.setDocId(documentid);
                question.setHeaders(TagMatchDefinitions.addOrReplaceXTMHeader(question.getHeaders(), TagMatchDefinitions.DOCUMENT_ID, documentid));
		return documentid;
	}
*/

	protected void addForwardingKeywordLog(String loginname, Question question, String forwardingReason) {

		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ": " + question.getId() + " ";
		SkyLogger.getClientLogger().info(logPrefix + "addForwardingKeywordLog:START: type:" + forwardingReason);

		logReason(question, loginname, forwardingReason, OperatorLogRecord.ACTION_FORWARD);

		SkyLogger.getClientLogger().info(logPrefix + "addForwardingKeywordLog:INT1: type:" + forwardingReason);

		if (forwardingReason.startsWith(DOKUMENTENTYP)) {
			forwardingReason = FALSCHER_DOKUMENTENTYP;
		}
		Keyword forwardingKeyword = loadCategoryKeyword((question.getProjectId() > 0) ? question.getProjectId() : 110, ClientOutboundRule.DOKUMENTWEITERLEITUNG, forwardingReason);
		SkyLogger.getClientLogger().debug("set keyword with forwarding reason: " + forwardingKeyword);
		if (forwardingKeyword != null) {
			SkyLogger.getClientLogger().info(logPrefix + "addForwardingKeywordLog:INT2: set:" + forwardingKeyword.getName());
			List<Keyword> keywords = question.getKeywords();
			if (!keywords.contains(forwardingKeyword))
				keywords.add(forwardingKeyword);
		}
		SkyLogger.getClientLogger().info(logPrefix + "addForwardingKeywordLog:END: type:" + forwardingReason);

	}

	public static Integer sbsIndex(Question question, final String customerID, boolean manualReindex) {
		int questionid = 0;
		try {
			if(question == null) {
				Object qao =  Repository.getObject(Repository.OPERATORMODE);
				QuestionAnswer qa;
				if (qao instanceof QuestionAnswer) {
					qa = (QuestionAnswer) qao;
					question = qa.getQuestion();

					assert question != null;
					questionid = question.getId();

					if (qa.getQuestion().isManipulated() || qa.getQuestion().isManipulated() || question.getId() < 1) {
						API.getClientAPI().getQuestionAPI().store(qa.getQuestion());
						SkyLogger.getClientLogger().debug(" ReindexAction fid:" + qa.getQuestion().getId() + " storing question");
					}
					if (qa.getAnswer().isManipulated() || qa.getAnswer().isChanged() || qa.getAnswer().isDirty()) {
						API.getClientAPI().getAnswerAPI().store(qa.getAnswer());
						SkyLogger.getClientLogger().debug(" ReindexAction aid:" + qa.getAnswer().getId() + " storing Answer:"+qa.getAnswer().getType());
					}
					SkyLogger.getClientLogger().debug("Before ReindexAction aid:"+qa.getAnswer().getId() + " dirty:"+qa.getAnswer().isDirty() + " isChanged:"+qa.getAnswer().isChanged()+
							" isManipulated:" +qa.getAnswer().isManipulated()+ " AdocE:"+qa.isDocEdited()+" Adirty"+qa.isDirty());
				}
			}

			Boolean trying = true;
			//BRSession session = BRSessionPoolFactory.getBRSessionPool().getBRSession("SkyReindex");
			//String operatorMode = (String) (session.getParameter("OperatorMode"));
			//Boolean opMode = operatorMode != null && operatorMode.equals(TagMatchDefinitions.TRUE);
			Boolean opMode = API.getClientAPI().getProcessingAPI().isOperatorModeStart();
			String headers = question.getHeaders();

			if (manualReindex && customerID.equals("0")) {
				String oldId = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.getHeaderTagName(TagMatchDefinitions.CUSTOMER_ID));
				if (oldId == null || oldId.length() == 0 || oldId.equals("0")) {
					trying = false;
					JOptionPane.showMessageDialog(null, "Ein Dokument ohne Kundenkontakt kann nicht deindiziert werden.");
				}
			}

			if (trying) {
				List<Object> parameter = new ArrayList<Object>();
				parameter.add((question.getProjectId() > 0) ? question.getProjectId() : 120);
				parameter.add(question.getId());
				parameter.add(customerID);
				parameter.add(API.getClientAPI().getConnectionAPI().getCurrentOperatorShort().getId());
				// no initial contact
				parameter.add(new Boolean(false));
				// Needed for new contact
				String channel = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.getHeaderTagName(TagMatchDefinitions.CHANNEL));
				if (channel == null || channel.length() == 0 || channel.equals("0")) {
                    channel = TagMatchDefinitions.Channel.EMAIL.toString();
                }
				parameter.add(channel);
				String formtype = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.getHeaderTagName(TagMatchDefinitions.FORM_TYPE_CATEGORY));
				if (formtype == null || formtype.length() == 0 || formtype.equals("0")) {
                    formtype = "systemdefault";
                }
				parameter.add(formtype);
				String direction = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.getHeaderTagName(TagMatchDefinitions.MX_DIRECTION));
				if (direction == null || direction.length() == 0 || direction.equals("0")) {
                    direction = "INBOUND";
                }
				parameter.add(direction);
				parameter.add(manualReindex);
				SkyLogger.getClientLogger().debug("Starting ReindexAction with parameters:p:" + question.getProjectId() + " fid:" + question.getId() + " cid:" + customerID + " op:" + API.getClientAPI().getConnectionAPI().getCurrentOperatorShort().getId() + " ch:" + channel + " formtype" + formtype+ "\n and wirh headers: "+question.getHeaders());
				List<Object> result = (List<Object>) API.getClientAPI().getConnectionAPI().sendServerEvent(Actions.ACTION_SBSINDEX.name(), parameter);

				Question returnquestion = (Question) result.get(0);
				returnquestion.setExtra3(customerID);
				
				Question q = ClientUtils.reloadQuestion( opMode, returnquestion.getId(), null);
				q.setExtra3(customerID);
				q.setHeaders(TagMatchDefinitions.addOrReplaceXTMHeader(question.getHeaders(),TagMatchDefinitions.getHeaderTagName(TagMatchDefinitions.CUSTOMER_ID), customerID));
				q.setExtra7("");
				q.setExtra9("");

				/*
				returnquestion.setExtra3(customerID);

				returnquestion.setExtra7("");
				returnquestion.setExtra9("");
				*/
			}
		}
		catch (Exception ex) {
			SkyLogger.getClientLogger().error("Problem during ReindexAction qid:" + questionid + (ex.getMessage() != null ? " ex:" + ex.getMessage() : ""), ex);
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Fehler bei der Indizierung des Kunden.");
		}
		return questionid;
	}

	/**
	 * Adds a button to change the customers address data.
	 *
	 * @param localRepository The MX-Map containing the GUI-component that shows the
	 *                        question
	 * @return The button with the customers address data
	 */
	protected JButton getAddessButton(final Map localRepository) {
		final JButton addressButton = new JButton(new AbstractAction("Adresse", new ImageIcon(ClientOutboundRule.class.getClassLoader().getResource("Address.png"))) {

			@Override
			public void actionPerformed(ActionEvent e) {
				Question question;
				if (localRepository != null) {
					NewMailFrame frame = (NewMailFrame) localRepository.get("CURRENT_FRAME");
					question = frame.getQuestion();
				} else {
					QuestionAnswer qa = (QuestionAnswer) Repository.getObject(Repository.OPERATORMODE);
					question = qa.getQuestion();
				}
				String headers = question.getHeaders();
				SkyLogger.getClientLogger().debug("Read address headers:\n" + headers);
				Map<String, String> addressFields = changeAddressFields(question);
				setAddressFields(question, headers, addressFields);
			}
		});
		addressButton.setToolTipText("Adresse ändern");
		return addressButton;
	}

	protected void setAddressFields(Question question, String headers, Map<String, String> addressFields) {
		if (addressFields != null) {
            for (Map.Entry<String, String> en : addressFields.entrySet()) {
                String value = en.getValue();
                if (en.getKey().equalsIgnoreCase("CustomerCountry")) {
                    if (value.equalsIgnoreCase(DE) || value.equalsIgnoreCase(AT)) {
                        value = value.toUpperCase();
                    } else if (value.equalsIgnoreCase("Austria") || value.equalsIgnoreCase("AT")) {
                        value = AT;
                    } else {
                        value = DE;
                    }
                }
                headers = TagMatchDefinitions.addOrReplaceXTMHeader(headers, en.getKey(), value);
            }
            SkyLogger.getClientLogger().debug("Setting address headers:\n" + headers);
            question.setHeaders(headers);
			API.getClientAPI().getQuestionAPI().store(question);
			Question q = ClientUtils.reloadQuestion(API.getClientAPI().getProcessingAPI().isOperatorModeStart(), question.getId(), this);
        }
	}

	// @Override
	// public String getFormtype(final Question question) {
	// String formtype = null;
	//
	// List<Object> parameter = new ArrayList<Object>();
	// parameter.add(question.getProjectId());
	// parameter.add(question.getId());
	// List<Object> result = (List<Object>)
	// API.getClientAPI().getConnectionAPI().sendServerEvent(Actions.ACTION_LOAD_KEYWORD.name(),
	// parameter);
	// List<Keyword> keywords = (List<Keyword>) result.get(0);
	//
	// for (Keyword keyword : keywords) {
	// SkyLogger.getClientLogger().info(keyword);
	// String key = keyword.getName();
	// if (key.startsWith(FORMTYPE_PREFIX)) {
	// formtype = key.substring(FORMTYPE_PREFIX.length(), key.length());
	// break;
	// }
	// }
	// SkyLogger.getClientLogger().info(formtype);
	// return formtype;
	// }
}