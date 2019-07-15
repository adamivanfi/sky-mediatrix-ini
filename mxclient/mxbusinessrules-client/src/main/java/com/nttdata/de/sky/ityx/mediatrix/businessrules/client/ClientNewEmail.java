package com.nttdata.de.sky.ityx.mediatrix.businessrules.client;

import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.archive.AbstractArchiveMetaData;
import com.nttdata.de.sky.archive.ArchiveMetaDataFactory;
import com.nttdata.de.sky.archive.ClientUtils;
import com.nttdata.de.sky.archive.CustomLine_Sky;
import com.nttdata.de.sky.ityx.common.Actions;
import com.nttdata.de.sky.ityx.common.DocIDClient;
import com.nttdata.de.sky.ityx.common.ExtendedNewMailFrame;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import com.nttdata.de.sky.pdf.ClientTemplateExtension;
import com.nttdata.de.sky.pdf.PdfFile;
import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.api.IClientAPI;
import de.ityx.mediatrix.api.client.ICQuestion;
import de.ityx.mediatrix.api.interfaces.IEditorContainer;
import de.ityx.mediatrix.api.interfaces.businessrules.client.IClientNewEmail;
import de.ityx.mediatrix.api.interfaces.textobjects.ITextObject;
import de.ityx.mediatrix.client.dialog.Start;
import de.ityx.mediatrix.client.dialog.operate.QuestionAnswer;
import de.ityx.mediatrix.client.dialog.util.newmail.NewMailFrame;
import de.ityx.mediatrix.client.util.Repository;
import de.ityx.mediatrix.data.*;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

//import de.ityx.mediatrix.data.GlobalVariable;

public class ClientNewEmail extends ClientOutboundRule implements IClientNewEmail {

	private static final String R_QUESTION = "rQuestion";
	private static final String IDENTITYBOX = "IDENTITYBOX_SKY";
	private de.ityx.sky.outbound.client.ClientNewMailFrame clNewMailFrame;
	public static List EMAILS_PROHIBITED_TO_SEND;
	private AbstractArchiveMetaData questionArchiveMetaData = ArchiveMetaDataFactory.getInstance("com.nttdata.de.sky.ityx.mediatrix.businessrules.archiving.MDocumentArchiveMetaData");

	public ClientNewEmail() {
		clNewMailFrame = new de.ityx.sky.outbound.client.ClientNewMailFrame();
	}

	@Override
	public boolean preQuestionSend(String loginname, boolean operatorMode, Question question, HashMap localRepository) {
		SkyLogger.getClientLogger().info(getClass().getName() + "#" + (new Object() {
		}.getClass().getEnclosingMethod().getName()) + " SKY BR START");


		// SIT System mail addresses cannot be used as recipients, it means that no e-mails can be sent to that list.
		EMAILS_PROHIBITED_TO_SEND = new LinkedList();
		for (String s : StringUtils.split(System.getProperty("sky_emails.prohibited_to_send", "-1"), ",")) {
			if (s != null && !s.isEmpty()) {
				EMAILS_PROHIBITED_TO_SEND.add(s.trim());
				SkyLogger.getClientLogger().info(getClass().getName() + "not send to #" +s.trim());
			} else {
				SkyLogger.getClientLogger().warn("Cannot Read sky_emails.prohibited_to_send-Value:" + s);
			}
		}

		// Sky-DMS 420
		final NewMailFrame frame = (NewMailFrame) localRepository.get("CURRENT_FRAME");
		final IEditorContainer editorContainer = frame.getEditorContainer();
		if (editorContainer.checked()) {
			editorContainer.spellClick();
		}
		//INCTASK0018947---------------------------------------------------------
		//den Text nach der Rechtschreibprüfung wieder in die Frage (question) setzten
		//2018.07.03 Ivanfi
		/*
		 *  ATTENTION!!!
		 *  	Code change as done in "ClientQuestionAnswerView" doesn't work as
		 *  property "spell.check" --> editorContainer.checked()
		 *  is set to 0 ("false") in Mediatrix core at "send" on GUI !!!
		 */
		//question.setBody(editorContainer.getText());
		//-----------------------------------------------------------------------

		JComboBox jc = (JComboBox) localRepository.get(IDENTITYBOX);
		question.setFrom((String) jc.getSelectedItem());
		TagMatchDefinitions.Channel channel = TagMatchDefinitions.Channel.BRIEF;
		String headers = question.getHeaders();
		SkyLogger.getClientLogger().info(getClass().getName() + "not send to #" + question.getType());
		if (question.getType() == Email.TYPE_EMAIL) {
			channel = TagMatchDefinitions.Channel.EMAIL;
			if (question.getTo() == null || question.getTo().trim().length() == 0 || question.getTo().equals("noreply@sky.de")) {
				question.setTo(JOptionPane.showInputDialog("Bitte eine Empfängeradresse eintragen!"));
			} else if (EMAILS_PROHIBITED_TO_SEND.contains(question.getTo().toLowerCase())) {
                // SIT System mail addresses cannot be used as recipients, it means that no e-mails can be sent to that list.
                JOptionPane.showMessageDialog(null,
                        "An die E-Mail-Adresse \"" + question.getTo() + "\" kann nicht versendet werden.",
                        "Fehler",
                        JOptionPane.ERROR_MESSAGE);
                SkyLogger.getClientLogger().info("An die E-Mail-Adresse: " + question.getTo() + " kann nicht versendet werden.");
                question.setTo("");
                return false;
            }
		} else {
			question.setTo("noreply@sky.de");
			// reindex with correct channel
			String customerNumber = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.getHeaderTagName(TagMatchDefinitions.CUSTOMER_ID));
			if(customerNumber!=null) {
				List<Object> parameter = new ArrayList<Object>();
				parameter.add((question.getProjectId() > 0) ? question.getProjectId() : 110);
				parameter.add(question.getId());
				parameter.add(customerNumber);
				if (TagMatchDefinitions.isNotSbsProject(question)) parameter.add("0");
				parameter.add(API.getClientAPI().getConnectionAPI().getCurrentOperatorShort().getId());
				// no operator mode
				if (TagMatchDefinitions.isNotSbsProject(question)) parameter.add(false);
				// initial contact
				parameter.add(true);
				// Needed for new contact
				parameter.add(channel.toString());
				parameter.add(TagMatchDefinitions.isNotSbsProject(question) ? TagMatchDefinitions.DEFAULT_FORMTYPE : TagMatchDefinitions.SBS_FORMTYPE_DEFAULT);
				parameter.add("OUTBOUND");
				// Siebel activity
				if (TagMatchDefinitions.isNotSbsProject(question)) {
					parameter.add(TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.getHeaderTagName(TagMatchDefinitions.DOCUMENT_ID)));
					parameter.add(TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.getHeaderTagName(TagMatchDefinitions.ACTIVITY_ID)));//  activityID
				}
				else parameter.add(true);

				API.getClientAPI().getQuestionAPI().store(question);
				Actions event = TagMatchDefinitions.isNotSbsProject(question) ? Actions.ACTION_REINDEX : Actions.ACTION_SBSINDEX;
				SkyLogger.getClientLogger().info("preQuestionSend: Event - " + event + " ProjectId - question.getProjectId()");
				SkyLogger.getClientLogger().debug("preQuestionSend: Parameter - " + parameter);
				List<Object> result = (List<Object>) API.getClientAPI().getConnectionAPI().sendServerEvent(event.name(), parameter);
				Boolean reindexed = TagMatchDefinitions.isNotSbsProject(question) ? (Boolean) result.get(0) : true;

				if (!reindexed) {
					return false;
				}
			}
			else {
				JOptionPane.showMessageDialog(null, "Es ist nicht möglich einen Brief ohne Kundennummer zu versenden!");
				return false;
			}
		}
		headers = TagMatchDefinitions.addOrReplaceXTMHeader(headers, TagMatchDefinitions.CHANNEL, channel.toString());
		headers = TagMatchDefinitions.addOrReplaceXTMHeader(headers, TagMatchDefinitions.FORM_TYPE_CATEGORY, TagMatchDefinitions.isNotSbsProject(question)?TagMatchDefinitions.DEFAULT_FORMTYPE:TagMatchDefinitions.SBS_FORMTYPE_DEFAULT);
		setEmailHeaders(localRepository, headers);
		question.setAttachments(frame.getAttachments());

		if (question.getType() == Email.TYPE_LETTER || question.getType() == Email.TYPE_FAX) {
			try {
				String to = question.getTo();
				//if (to==null ||to.isEmpty())
				//	question.setTo("noreply@sky.de");

				byte[] pdf = ClientTemplateExtension.getInstance().createPdf(new PdfFile(question.getBody(), question.getSubprojectId(), question.getLanguage(), TagMatchDefinitions.isNotSbsProject(question), ClientUtils.getParameter(question, true)), null);
				//                ClientUtils.preview(pdf);
				if (!question.getStatus().equals(Question.S_MONITORED) && ClientUtils.isNotMonitored(question)) {
					ClientUtils.insertAttachments(question, pdf);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				SkyLogger.getClientLogger().error(ex.getMessage(),ex);
			}
		}

		final boolean ret = (checkMXTags(editorContainer.getDocumentText()) && checkMetaData(loginname, operatorMode, question, localRepository));

		SkyLogger.getClientLogger().debug("Return value: " + ret);
		return ret;
	}

	/**
	 * @param loginname
	 * @param operatorMode
	 * @param question
	 * @param localRepository
	 * @return
	 * @see de.ityx.sky.outbound.client.ClientNewMailFrame#preQuestionSend
	 * <p/>
	 * Uses Sky custom line.
	 */
	protected boolean checkMetaData(String loginname, boolean operatorMode, Question question, HashMap localRepository) {
		Boolean metaDataComplete = true;
		if (question.getType() == Email.TYPE_LETTER || question.getType() == Email.TYPE_FAX) {
			try {
				if (!checkFirstName(question)) {
					return false;
				}
				Map<String, String> metaMap = questionArchiveMetaData.collectMetadata(null, question);
				metaDataComplete = questionArchiveMetaData.isMetadataComplete(metaMap, question.getId());

			} catch (Exception ex) {
				ex.printStackTrace();
				SkyLogger.getClientLogger().error(ex.getMessage(),ex);
			}
		}

		return metaDataComplete;
	}

	@Override
	public void subprojectChanged(Subproject teilprojekt, HashMap map) {

		String globalString = API.getClientAPI().getServiceCenterAPI().getGlobalString("alternative_rueckantwort." + teilprojekt.getId(), teilprojekt.getReplyAddress());
		String[] adressen = globalString.split(";");
		if (globalString.indexOf(teilprojekt.getReplyAddress()) < 0) {
			String[] tmp = new String[adressen.length + 1];
			System.arraycopy(adressen, 0, tmp, 1, adressen.length);
			tmp[0] = teilprojekt.getReplyAddress();
			adressen = tmp;
		}
		JComboBox jc = (JComboBox) map.get(IDENTITYBOX);
		jc.setModel(new DefaultComboBoxModel(adressen));
	}

	@Override
	public Map getIdentityExtension(Operator operator, Question question, int projektId, Map localRepository) {

		long startTime = System.currentTimeMillis();
		localRepository.put("NewEmailStartTime" + System.identityHashCode(question), startTime);
		question.setEmailDate(System.currentTimeMillis());
		final Map result = new HashMap();
		JPanel panel = new JPanel();
		JComboBox jc = new JComboBox();
		localRepository.put(IDENTITYBOX, jc);

		//result.put("component", jc);

		result.put("fill", GridBagConstraints.VERTICAL);
		result.put("weighty", 0.0);
		result.put("weightx", 0.0);
		result.put("anchor", GridBagConstraints.LINE_END);

		JLabel text = new JLabel(Messages.getString("ClientNewMail.1"));
		// result.put("component_leading", text);
		// result.put("weighty_leading", 0.0);

		panel.add(text);
		panel.add(jc);
		result.put("component",panel);

		SkyLogger.getClientLogger().debug("Return value: " + result);
		return result;

	}

	/**
	 * Tries to update a initial question with Siebel data if provided, else
	 * asks the user for input.
	 *
	 * @param question        The initial question
	 * @param localRepository The MX-Map containing the GUI-component that shows the
	 *                        question
	 * @return The updated headers
	 */
	protected String checkRequest(Question question, final Map localRepository) {
		final IClientAPI clientAPI = API.getClientAPI();
		final ServiceCenter serviceCenter = clientAPI.getServiceCenterAPI().getServiceCenter();
		String value = de.ityx.sky.outbound.client.ClientQuestionAnswerView.getValueFromExtraColumn(question, serviceCenter.getIntValue("sky.contactid.extra", 8));
		String headers = question.getHeaders();
		SkyLogger.getClientLogger().debug("RequestParameter: using <" + value + "> as contactid");
		if (value.trim().length() > 0) {
			try {
				String activityid = question.getExtra12();
				String contactid = question.getExtra8();
				String customerid = question.getExtra3();
				String contractid = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.TAGMATCH_CONTRACT_NUMBER);

				if (value.contains(";")) {
					String[] parameters = value.split(";");
					for (String parameter : parameters) {
						if (!parameter.startsWith("X-Tagmatch:")) {
							final String[] entry = parameter.split("=");
							if (entry[0].equals("activityid") && entry.length == 2) {
								activityid = entry[1];
							} else if (entry[0].equals("contactid") && entry.length == 2) {
								contactid = entry[1];
							} else if (entry[0].equals("customerid") && entry.length == 2) {
								customerid = entry[1];
							} else if (entry[0].equals("contractid") && entry.length == 2) {
								contractid = entry[1];
							}
						} else {
							question.setHeaders(parameter);
						}
					}
				}

				if (activityid==null || activityid.isEmpty()){
					activityid = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.ACTIVITY_ID);
				}
				if (contactid==null || contactid.isEmpty()){
					contactid = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.CONTACT_ID);
				}
				if (customerid==null || customerid.isEmpty()){
					customerid = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.CUSTOMER_ID);
				}

				if (activityid == null) {
					activityid = "";
				}
				question.setExtra12(activityid);

				boolean enrichcustomer = false;

				String lastname = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.CUSTOMER_LAST_NAME);


				if (customerid == null) {
					customerid = "";
				} else if (lastname==null || lastname.isEmpty()){
					enrichcustomer = true;
				}
				question.setExtra3(customerid);

				if (contactid == null) {
					contactid = "";
				}
				question.setExtra8(contactid);

				if (enrichcustomer) {
					List<Object> parameter = new ArrayList<Object>();
					List<Object> result;

					String documentid = DocIDClient.getOrGenerateDocId(question, TagMatchDefinitions.DocumentDirection.INDIVIDUALCORRESPONDENCE, TagMatchDefinitions.Channel.EMAIL, null);

					headers = TagMatchDefinitions.addOrReplaceXTMHeader(headers, TagMatchDefinitions.CONTACT_ID, contactid);
					headers = TagMatchDefinitions.addOrReplaceXTMHeader(headers, TagMatchDefinitions.ACTIVITY_ID, activityid);
					headers = TagMatchDefinitions.addOrReplaceXTMHeader(headers, TagMatchDefinitions.CUSTOMER_ID, customerid);
					headers = TagMatchDefinitions.addOrReplaceXTMHeader(headers, TagMatchDefinitions.TAGMATCH_CONTRACT_NUMBER, contractid);
					headers = TagMatchDefinitions.addOrReplaceXTMHeader(headers, TagMatchDefinitions.DOCUMENT_ID, documentid);

					question.setHeaders(headers);

					ICQuestion questionAPI = clientAPI.getQuestionAPI();
					questionAPI.store(question);

					parameter.clear();

					parameter.add((question.getProjectId()>0)?question.getProjectId():110);
					if (question.getId()<0){
						JOptionPane.showMessageDialog(null, "Die Frage noch nicht gespeichert.");
					}
					parameter.add(question.getId());
					parameter.add(customerid);
					// no contract
					if(TagMatchDefinitions.isNotSbsProject(question)) {
						if (contractid == null || contractid.isEmpty()) {
							contractid = "0";
						}
						parameter.add(contractid);
					}
					parameter.add(API.getClientAPI().getConnectionAPI().getCurrentOperatorShort().getId());
					// not in operator mode
					if(TagMatchDefinitions.isNotSbsProject(question)) parameter.add(false);
					// initial contact
					parameter.add(true);

					// adds dummy values
					parameter.add("EMAIL");
					parameter.add("indCorr");
					parameter.add("OUTBOUND");
					// Siebel activity
					if(TagMatchDefinitions.isNotSbsProject(question)) {
						parameter.add(documentid);
						parameter.add(activityid);
					}
					else parameter.add(true);

					Actions event = TagMatchDefinitions.isNotSbsProject(question) ? Actions.ACTION_REINDEX : Actions.ACTION_SBSINDEX;
					SkyLogger.getClientLogger().info("checkRequest: Event - "+ event + " ProjectId - question.getProjectId()");
					SkyLogger.getClientLogger().debug("checkRequest: Parameter - "+ parameter);
					result = (List<Object>) clientAPI.getConnectionAPI().sendServerEvent(event.name(), parameter);

					Boolean trying = TagMatchDefinitions.isNotSbsProject(question) ? (Boolean) result.get(0) : true;
					if (!trying) {
						JOptionPane.showMessageDialog(null, "Der Kunde konnte nicht in MX-FuzzyDB gefunden werden.");
					} else {
						Boolean success = TagMatchDefinitions.isNotSbsProject(question) ? (Boolean) result.get(1) :true;
						final String message = success ? "Der Kunde konnte erfolgreich indiziert werden." : "Fehler bei der Indizierung des Kunden.";
						JOptionPane.showMessageDialog(null, message);
						if (success) {
							question = questionAPI.load(question.getId(), false);
							headers = question.getHeaders();
							SkyLogger.getClientLogger().debug("Question " + question.getId() + ":\n" + headers);
							setReceiverFromCustomerData(question);
						}
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				SkyLogger.getClientLogger().error(ex.getMessage(),ex);
			}
		} else {
			headers = executeIndexing(question, localRepository);
		}
		return headers;
	}

	@Override
	public List<JMenu> getMenuButtons(Operator operator, Question question, int projectId, Map localRepository) {
		final List<JMenu> ret = Collections.EMPTY_LIST;
		SkyLogger.getClientLogger().debug("Return value: " + ret);
		return ret;
	}

	@Override
	public Map<Object, Object> getReceiverExtension(Operator operator, Question question, int projectId, Map localRepository) {
		final Map<Object, Object> ret = Collections.EMPTY_MAP;
		SkyLogger.getClientLogger().debug("Return value: " + ret);
		return ret;
	}

	@Override
	public Map<Object, Object> getSubjectExtension(Operator operator, Question question, int projectId, Map localRepository) {
		final Map<Object, Object> ret = Collections.EMPTY_MAP;
		SkyLogger.getClientLogger().debug("Return value: " + ret);
		return ret;
	}

	@Override
	public List<JComponent> getTabList(Question question, HashMap localRepository) {
		SkyLogger.getClientLogger().info(getClass().getName() + "#" + (new Object() {
		}.getClass().getEnclosingMethod().getName()) + " SKY BR START");
		System.out.println("getTabList");
		System.err.println("getTabList");
		SkyLogger.getClientLogger().debug("getTabList");

		List<JComponent> ret = Collections.EMPTY_LIST;
		if (TagMatchDefinitions.extractXTMHeader(question.getHeaders(), TagMatchDefinitions.CUSTOMER_ID) == null) {
			ret = clNewMailFrame.getTabList(question, localRepository);
			SkyLogger.getClientLogger().debug("Return value: " + ret);
		}
		return ret;
	}

	@Override
	public List<JButton> getToolbarButtons(Operator operator, Question question, int projectId, Map localRepository) {
		SkyLogger.getClientLogger().debug("enter: getToolbarButtons: q:" + question.getId());
		final NewMailFrame frame = (NewMailFrame) localRepository.get("CURRENT_FRAME");
		final String questionHeaders = checkRequest(question, localRepository);
		setEmailHeaders(localRepository, questionHeaders);
		final IEditorContainer editorContainer = frame.getEditorContainer();
		editorContainer.checkWords(true);
		final List<JButton> buttons = new ArrayList<JButton>();
		String customer = TagMatchDefinitions.extractXTMHeader(questionHeaders, TagMatchDefinitions.X_TAGMATCH_CUSTOMER_ID);
		if (customer != null) {
			List<Object> parameter = new ArrayList<Object>();
			parameter.add((question.getProjectId()>0)?question.getProjectId():110);
			parameter.add(customer);
			List<Object> result = (List<Object>) API.getClientAPI().getConnectionAPI().sendServerEvent(Actions.ACTION_LOAD_CUSTOMER.name(), parameter);
			if (result.size() > 0) {
				//		Customer mxCustomer = (Customer) result.get(0);
			}
		}
		buttons.add(getAddessButton(localRepository));
		frame.setTo(question.getTo());
		setPreviewListener(frame);
		Repository.putObject(Repository.THEQUESTION, question);
		SkyLogger.getClientLogger().debug("exit: getToolbarButtons");
		return buttons;
	}

	/**
	 * Changes the behaviour of the preview button.
	 *
	 * @param frame The GUI-component that contains the buttons logic
	 */
	protected void setPreviewListener(final NewMailFrame frame) {
		final ActionListener previewListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					List<Object> list = new ArrayList<Object>();
					list.add(frame.getProjectId());
					String customerid = TagMatchDefinitions.extractXTMHeader(frame.getQuestion().getHeaders(), TagMatchDefinitions.CUSTOMER_ID);
					if (customerid != null) {
						list.add(customerid);
						List<Object> result = (List<Object>) API.getClientAPI().getConnectionAPI().sendServerEvent(Actions.ACTION_LOAD_CUSTOMER.name(), list);
						if (result.size() > 0 && result.get(0).getClass().equals(Customer.class)) {
							Customer customer = (Customer) result.get(0);
							frame.setCustomer(customer);
						}
						Question question = ExtendedNewMailFrame.prepareQuestion(frame, false);
						checkFirstName(question);
						Map<String, String> metaMap = questionArchiveMetaData.collectMetadata(null, question);
						final String documentid = metaMap.get(TagMatchDefinitions.DOCUMENT_ID);
						HashMap<String, Object> parameter = CustomLine_Sky.getParameter(question, metaMap, documentid, true);
						parameter.put("loginname", API.getClientAPI().getConnectionAPI().getCurrentOperator().getUserId());
						//String params="";
						//for (Map.Entry<String, Object> p: parameter.entrySet()){
						//   params+= p.getKey()+":>"+p.getValue()+"< ";
						//}
						byte[] pdf = ClientTemplateExtension.getInstance().createPdf(new PdfFile(question.getBody(), question.getSubprojectId(), question.getLanguage(), TagMatchDefinitions.isNotSbsProject(question), parameter), null);
						SkyLogger.getClientLogger().info("PreviewLetter: q:" + question.getId() + " tp:" + question.getSubprojectId() + " l:" + question.getLanguage() + " u:" + API.getClientAPI().getConnectionAPI().getCurrentOperator().getUserId() + " p:"); //+params);
						ClientUtils.preview(pdf);
					} else {
						JOptionPane.showMessageDialog(null, "Es ist nicht möglich einen Brief ohne Kundennummer zu versenden!");
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					SkyLogger.getClientLogger().error(ex.getMessage(),ex);
				}
			}
		};
		if (frame.getClass().equals(ExtendedNewMailFrame.class)) {
			((ExtendedNewMailFrame) frame).setPreviewListener(previewListener);
		} else {
			JButton jbPreview = (JButton) frame.getButton(QuestionAnswer.PREVIEW_BUTTON);
			ActionListener[] listeners = jbPreview.getActionListeners();
			for (ActionListener listener : listeners) {
				jbPreview.removeActionListener(listener);
			}
			jbPreview.addActionListener(ExtendedNewMailFrame.getNewMailPreviewListener(previewListener, frame));
		}
	}

	/**
	 * @param localRepository
	 * @param headers
	 */
	public static void setEmailHeaders(Map localRepository, String headers) {
		SkyLogger.getClientLogger().debug("Setting Headers:\n" + headers);
		NewMailFrame frame = (NewMailFrame) localRepository.get("CURRENT_FRAME");
		frame.getEmail().setHeaders(headers);
	}

	@Override
	public void postQuestionSend(String loginname, boolean isOperatorMode, Question question, HashMap localRepository) {
		if (question.getType() != Email.TYPE_LETTER && question.getType() != Email.TYPE_FAX) {
			//	ArchiveTool.archiveCaseWithAnswers(question, logPrefix);
			logCompleted(loginname, question);
		}
	}

	@Override
	public void postTextObjectInsert(String loginname, boolean isOperatorMode, Question question, ITextObject tb, HashMap localRepository) {
	}

	@Override
	public boolean preTextObjectInsert(String loginname, boolean isOperatorMode, Question question, ITextObject tb, HashMap localRepository) {
		final boolean ret = true;
		SkyLogger.getClientLogger().debug("Return value: " + ret);
		return ret;
	}

	@Override
	public boolean postAttachmentInsert(String arg0, boolean arg1, Question arg2, de.ityx.mediatrix.data.Attachment arg3, HashMap arg4) {
		final boolean ret = true;
		SkyLogger.getClientLogger().debug("Return value: " + ret);
		return ret;
	}

	@Override
	public boolean preAttachmentInsert(String arg0, boolean arg1, Question arg2, de.ityx.mediatrix.data.Attachment arg3, HashMap arg4) {
		final boolean ret = true;
		SkyLogger.getClientLogger().debug("Return value: " + ret);
		return ret;
	}

	/**
	 * Sets receiver email from NewDB customer data.
	 *
	 * @param question The question to alter
	 */
	protected void setReceiverFromCustomerData(Question question) {
		String customerMailH = TagMatchDefinitions.extractXTMHeader(question.getHeaders(), TagMatchDefinitions.getHeaderTagName(TagMatchDefinitions.TAGMATCH_EMAIL));
		List<Object> parameter = new ArrayList<Object>();
		parameter.add((question.getProjectId()>0)?question.getProjectId():110);
		parameter.add(question.getId());
		parameter.add(API.getClientAPI().getConnectionAPI().getCurrentOperatorShort().getId());
		Boolean opMode = API.getClientAPI().getProcessingAPI().isOperatorModeStart();
		parameter.add(opMode);
		List<Object> result = (List<Object>) API.getClientAPI().getConnectionAPI().sendServerEvent(Actions.ACTION_MX_RESOLVE.name(), parameter);
		Map<String, String> customer = (Map<String, String>) result.get(0);
		String customerEmail = customer.get("EmailAddress");
		if (customerEmail == null || customerEmail.isEmpty()) {
			if (customerMailH != null && !customerMailH.isEmpty()) {
				customerEmail = customerMailH;
			} else {
				customerEmail = "noreply@sky.de";
			}
		}
		question.setTo(customerEmail);
	}


	/**
	 * Asks the user for a customer id and indexes the question with it.
	 *
	 * @param question        The initial question
	 * @param localRepository The MX-Map containing the GUI-component that shows the
	 *                        question
	 * @return The updated headers
	 */
	protected String executeIndexing(Question question, final Map localRepository) {
		String headers = question.getHeaders();
		try {
			NewMailFrame frame = (NewMailFrame) localRepository.get("CURRENT_FRAME");
			GridBagConstraints c = new GridBagConstraints();
			JPanel inputPanel = new JPanel(new GridBagLayout());
			c.gridx = 0;
			c.gridy = 0;
			c.anchor = GridBagConstraints.WEST;
			JLabel customerLabel = new JLabel("Kundennummer: ");
			inputPanel.add(customerLabel, c);
			c.gridx = 1;;
			c.anchor = GridBagConstraints.EAST;
			javax.swing.JTextField customerField = new javax.swing.JTextField(10);
			customerField.setEditable(true);
			inputPanel.add(customerField, c);
			javax.swing.JTextField contractField = new javax.swing.JTextField(10);
			if (TagMatchDefinitions.isNotSbsProject(question)) {
				contractField.setEditable(true);
				c.gridy = 1;
				inputPanel.add(contractField, c);
				c.gridx = 0;
				JLabel contractLabel = new JLabel("Vertragsnummer: ");
				inputPanel.add(contractLabel, c);
			}
			int dialog = JOptionPane.showConfirmDialog(null, inputPanel, "Neuzuordnung Kunden", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
			if (dialog == JOptionPane.OK_OPTION) {
				String customerID = customerField.getText();
				String customerNumber = customerID!=null?customerID.replaceAll("[\\.\\s]", ""):null;

				Boolean trying = true;
				if (customerNumber!=null && !customerNumber.matches("\\d{10}|0|P\\d*") && !customerNumber.isEmpty()){
					trying = false;
					JOptionPane.showMessageDialog(null, "Kundennummerformat entspricht nicht der Vorgaben (10-Stellig)");
					dialog = JOptionPane.showConfirmDialog(null, inputPanel, "Neuzuordnung Kunden", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
					customerID = customerField.getText();
					customerNumber = customerID!=null?customerID.replaceAll("[\\.\\s]", ""):null;
				}
				String contractID = "";
				if (TagMatchDefinitions.isNotSbsProject(question)) contractID = contractField.getText();
				contractID = contractID.replaceAll("[\\.\\s]", "");

				// Checks if reindexing from 0 to 0.
				if (customerNumber.equals("0")) {
					String oldId = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.getHeaderTagName(TagMatchDefinitions.CUSTOMER_ID));
					if (oldId == null || oldId.length() == 0 || oldId.equals("0")) {
						trying = false;
						JOptionPane.showMessageDialog(null, "Ein Dokument ohne Kundenkontakt kann nicht deindiziert werden.");
					}
				}
				String docId = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.X_TAGMATCH_DOCUMENT_ID);
				if (docId == null || docId.trim().length() == 0) {

					TagMatchDefinitions.Channel channel = TagMatchDefinitions.getChannel(TagMatchDefinitions.extractXTMHeader(question.getHeaders(), TagMatchDefinitions.CHANNEL));

					String documentid = DocIDClient.getOrGenerateDocId(question, TagMatchDefinitions.DocumentDirection.INDIVIDUALCORRESPONDENCE, channel, null);


					if (trying) {
						headers = TagMatchDefinitions.addOrReplaceXTMHeader(headers, TagMatchDefinitions.CONTACT_ID, "0");
						headers = TagMatchDefinitions.addOrReplaceXTMHeader(headers, TagMatchDefinitions.CHANNEL, "EMAIL");
						headers = TagMatchDefinitions.addOrReplaceXTMHeader(headers, TagMatchDefinitions.FORM_TYPE_CATEGORY,  TagMatchDefinitions.isNotSbsProject(question)?TagMatchDefinitions.DEFAULT_FORMTYPE:TagMatchDefinitions.SBS_FORMTYPE_DEFAULT);
						question.setHeaders(headers);
						question.setExtra8("0");
						question.setExtra12("0");
					}
					int subproject = 110;
					if (frame != null && frame.getParentSubproject() != null && frame.getParentSubproject().getId() > 0) {
						subproject = frame.getParentSubproject().getId();
					}
					question.setSubprojectId(subproject);

				}
				API.getClientAPI().getQuestionAPI().store(question);

				List<Object> parameter = new ArrayList<Object>();
				parameter.add((question.getProjectId() > 0) ? question.getProjectId() : 110);
				parameter.add(question.getId());
				parameter.add(customerNumber);
				if (TagMatchDefinitions.isNotSbsProject(question)) parameter.add(contractID);
				parameter.add(API.getClientAPI().getConnectionAPI().getCurrentOperatorShort().getId());
				// no operator mode
				if (TagMatchDefinitions.isNotSbsProject(question)) parameter.add(false);
				// initial contact
				parameter.add(true);
				// Needed for new contact
				parameter.add(TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.getHeaderTagName(TagMatchDefinitions.CHANNEL)));
				parameter.add(TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.getHeaderTagName(TagMatchDefinitions.FORM_TYPE_CATEGORY)));
				String direction = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.getHeaderTagName(TagMatchDefinitions.MX_DIRECTION));
				if (direction == null)
					direction = "OUTBOUND";
				parameter.add(direction);
				// Siebel activity
				if (TagMatchDefinitions.isNotSbsProject(question)) {
					parameter.add(docId);
					parameter.add("");// empty activityID
				}
				else parameter.add(true);

				Actions event = TagMatchDefinitions.isNotSbsProject(question) ? Actions.ACTION_REINDEX : Actions.ACTION_SBSINDEX;
				SkyLogger.getClientLogger().info("preQuestionSend: Event - " + event + " ProjectId - question.getProjectId()");
				SkyLogger.getClientLogger().debug("preQuestionSend: Parameter - " + parameter);
				List<Object> result = (List<Object>) API.getClientAPI().getConnectionAPI().sendServerEvent(event.name(), parameter);

				trying = TagMatchDefinitions.isNotSbsProject(question) ? (Boolean) result.get(0) : true;

				if (!trying) {
					JOptionPane.showMessageDialog(null, "Der (Kunde/Vertrag) kann nicht indiziert werden.");
				} else {
					Boolean success = TagMatchDefinitions.isNotSbsProject(question) ? (Boolean) result.get(1) : true;
					final String message = success ? "Der Kunde konnte erfolgreich indiziert werden." : "Fehler bei der Indizierung des Kunden.";
					JOptionPane.showMessageDialog(null, message);
					if (success) {
						ICQuestion questionAPI = API.getClientAPI().getQuestionAPI();
						question = questionAPI.load(question.getId(), false);

						setReceiverFromCustomerData(question);
						final String to = question.getTo();
						question.setHeaders(TagMatchDefinitions.addOrReplaceXTMHeader(question.getHeaders(), TagMatchDefinitions.TAGMATCH_EMAIL, to));
						if (frame != null && (frame.getTo() == null || frame.getTo().isEmpty())) {
							frame.setTo(to);
						}
						questionAPI.store(question);

						System.err.println("Indexed question " + question.getId() + " with email <" + to + "> and headers:\n" + question.getHeaders());

						Repository.putObject(Repository.THEQUESTION, question);
						if (frame != null) {
							frame.setQuestion(question);
						}
						headers = question.getHeaders();
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			SkyLogger.getClientLogger().error(ex.getMessage(),ex);
		}
		return headers;
	}

}