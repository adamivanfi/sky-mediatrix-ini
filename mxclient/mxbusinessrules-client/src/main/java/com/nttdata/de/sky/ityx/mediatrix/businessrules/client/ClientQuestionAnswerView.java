package com.nttdata.de.sky.ityx.mediatrix.businessrules.client;

import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.archive.ClientUtils;
import com.nttdata.de.sky.archive.Constant;
import com.nttdata.de.sky.ityx.common.Actions;
import com.nttdata.de.sky.ityx.common.DocIDClient;
import com.nttdata.de.sky.ityx.common.SiebelClientIntegration;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import com.nttdata.de.sky.ityx.mediatrix.businessrules.BusinessRule;
import com.nttdata.de.sky.ityx.mediatrix.businessrules.archiving.ArchiveTool;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.interfaces.document.CDocument;
import de.ityx.contex.interfaces.extag.TagMatch;
import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.api.client.ICOperator;
import de.ityx.mediatrix.api.interfaces.IAttachmentPanel;
import de.ityx.mediatrix.api.interfaces.IEditorContainer;
import de.ityx.mediatrix.api.interfaces.businessrules.client.IClientQuestionAnswerView;
import de.ityx.mediatrix.api.interfaces.textobjects.ITextDocument;
import de.ityx.mediatrix.api.interfaces.textobjects.ITextObject;
import de.ityx.mediatrix.client.dialog.objects.answer.AnswerEmail;
import de.ityx.mediatrix.client.dialog.objects.answer.AnswerHeader;
import de.ityx.mediatrix.client.dialog.operate.QuestionAnswer;
import de.ityx.mediatrix.client.dialog.util.BigIconComboBox;
import de.ityx.mediatrix.client.util.ActionCodes;
import de.ityx.mediatrix.client.util.Repository;
import de.ityx.mediatrix.client.util.ToolBarPanel;
import de.ityx.mediatrix.data.*;
import de.ityx.mediatrix.data.metainformation.MetaInformationDocumentContainer;
import de.ityx.mediatrix.data.metainformation.MetaInformationInt;
import de.ityx.mediatrix.modules.businessrules.data.BRSession;
import de.ityx.mediatrix.modules.businessrules.tools.BRSessionPoolFactory;
import de.ityx.mediatrix.util.JTextField;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.*;
import java.util.List;


public class ClientQuestionAnswerView extends ClientOutboundRule implements IClientQuestionAnswerView {

	private static final String SWE_PASSWORD = "SWEPassword";
	private static final String SWE_USER_NAME = "SWEUserName";
	private static final String SWE_ROW_ID = "SWERowId0";
	public static List EMAILS_PROHIBITED_TO_SEND;
	private static final String SBL_URL = System.getProperty("siebel.url", "http://sbltest.premiere.de/ecommunications_enu/start.swe?SWEApplet0=SKYDE+Customer+Portal+Form+Applet+Details+OCP+SKY&SWEBU=1&SWECmd=ExecuteLogin&SWEView=Account+Popup+View+SKY&SWEAC=SWECmd%3dGotoView&SWEHo=sbltest.premiere.de&SWERF=FALSE");


	private IClientQuestionAnswerView outbound_delegate = new de.ityx.sky.outbound.client.ClientQuestionAnswerView();
	private IClientQuestionAnswerView agenturdel = null;
	private final String aclazz = "de.ityx.agentursteuerung.ClientQAView";


	public ClientQuestionAnswerView() {
		String logPrefix = "ClientQuestionAnswerView # Constructor ";
		try {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " initalization");
			Class aclass=Class.forName(aclazz);
			if (aclass!=null){
				Constructor aconstr=aclass.getConstructor(null);
				if (aconstr!=null){
					agenturdel = (IClientQuestionAnswerView) aconstr.newInstance(null);
				}
			}SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " initalized");
		} catch (NullPointerException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
			SkyLogger.getBRSLogger().warn(logPrefix + aclazz + " cannot be instantiated." + e.getMessage());
		}
	}

	@Override
	public boolean preAttachmentInsert(String loginname, boolean isOperatorMode, Question question, Answer answer, Attachment attachment) {

		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		boolean ret = true;
		ret = ret && outbound_delegate.preAttachmentInsert(loginname, isOperatorMode, question, answer, attachment);

		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret = ret && agenturdel.preAttachmentInsert(loginname, isOperatorMode, question, answer, attachment);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return ret;
	}

	@Override
	public List<javax.swing.AbstractButton> getExtButtonList(final Question question) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");


		// final List<ServiceCenterAttribute> map = API.getClientAPI().getServiceCenterAPI().getServiceCenter().getAttributes();
		ArrayList<AbstractButton> buttons = new ArrayList<>();
		if (TagMatchDefinitions.isNotSbsProject(question)) {
			final JButton siebelButton = new JButton(new AbstractAction("Siebel", new ImageIcon(ClientQuestionAnswerView.class.getClassLoader().getResource("Siebel.png"))) {

				@Override
				public void actionPerformed(ActionEvent e) {
					URI uri = null;
					try {
						String customerId = TagMatchDefinitions.extractXTMHeader(question.getHeaders(), TagMatchDefinitions.getHeaderTagName(TagMatchDefinitions.SIEBEL_CUSTOMER_ID));

                   /* boolean disableSiebelClientIntegration = "1".equals(System.getProperty("ClientQuestionAnswerView.DisableSiebelClientIntegration", "0"));

                    if (disableSiebelClientIntegration) {
                        String url = SBL_URL + "&" + SWE_USER_NAME + "=" + "JDB_ATOS" + "&" + SWE_PASSWORD + "=" + "JDB_ATOS" + "&" + SWE_ROW_ID + "=" + customerId;
                        SkyLogger.getBRSLogger().info("Using SBL_URL: " + url);
                        uri = java.net.URI.create(url);
                        java.awt.Desktop.getDesktop().browse(uri);
                    } else {
                        SkyLogger.getBRSLogger().debug("Using SiebelClientIntegration.changeView(" + customerId + ")");

                        if (customerId == null || "".equals(customerId)) {
                            SkyLogger.getBRSLogger().warn("Skipping invocation of Siebel interface. Not a valid row number: " + customerId);
                            JOptionPane.showMessageDialog(null, "Es kann kein Kontakt in Siebel zugeordnet werden. Das Dokument enthält keine Kundennummer.");
                        } else {
                            boolean success = SiebelClientIntegration.changeViewWithCom(customerId);
                            if (!success) {
                                SkyLogger.getBRSLogger().warn("Falling back to script based integration");
                                SiebelClientIntegration.changeViewWithScript(customerId);
                            }
                        }

                    }*/
						String env = "PROD";

                    /* //Funktioniert nicht am Client
					String ityx_environment_type = System.getProperty("ityx_environment_type");
                    if (ityx_environment_type != null && ityx_environment_type.equalsIgnoreCase("integration")) {
                        env="TEST";
                    } else if (ityx_environment_type != null && ityx_environment_type.equalsIgnoreCase("production")) {
                        env="PROD";
                    }*/

						String mxservlet = API.getServletUrl();
						if (mxservlet != null && (mxservlet.contains("int.mediatrix.sky.de") ||mxservlet.contains("int.mediatrix-test.sky.de") || mxservlet.contains("10.145.36.4") || mxservlet.contains("10.145.32.4") || mxservlet.contains("90.211.178.98"))) {
							env = "TEST";
						} else {
							env = "PROD";
						}


						SiebelClientIntegration.changeViewWithScript(customerId, env);
					} catch (java.io.IOException ex) {
						ex.printStackTrace();
						JOptionPane.showMessageDialog(null, uri);
					}
				}
			});
			siebelButton.setToolTipText("Siebel");
			buttons.add(siebelButton);

			buttons.add(getIndexingButton(question));

			// SIT-17-05-099:
			/*
			 *   Dectivated so long no functionality put to the button.
			 *
			 */
			//buttons.add(getAddTableButton(question));

			if (CANCELLATION_SUBPROJECT.contains(question.getSubprojectId())){
				buttons.add(getCancellationQAButton(question));
			}
			
			if (question.getSubprojectId() == MANDATE_SUBPROJECT) {
				buttons.add(getMandateIndexingButton(question));
			}
		} else {
			buttons.add(getAddessButton(null));
		}
		boolean notSbsProject = TagMatchDefinitions.isNotSbsProject(question);
		SkyLogger.getBRSLogger().debug("notSbs? " + notSbsProject + " qid:" + question.getId() + " pid:" + question.getProjectId());

		ImageIcon  imageIcon  = new ImageIcon(ClientQuestionAnswerView.class.getClassLoader().getResource("Forward.png"));
		ForwardingAction  forwardingAction = new ForwardingAction((notSbsProject ? "SBS" : "Sky"), imageIcon, question);
		final JButton forwardToProjectButton = new JButton(forwardingAction);

		forwardToProjectButton.setToolTipText("Weiterleiten an " + (notSbsProject ? "SBS" : "SCS"));
		buttons.add(forwardToProjectButton);

		List<AbstractButton> loq=outbound_delegate.getExtButtonList(question);
		if (loq!=null && !loq.isEmpty()) {
			buttons.addAll(loq);
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			
			List<AbstractButton> aoq=agenturdel.getExtButtonList(question);
			if (aoq!=null && !aoq.isEmpty()) {
				buttons.addAll(aoq);
			}
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return buttons;


	}

	protected JButton createTOButton(final String loginname) {
		final JButton toButton = new JButton(new AbstractAction("Template", new ImageIcon(ClientQuestionAnswerView.class.getClassLoader().getResource("Template_Button.png"))) {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String logPrefix = getClass().getName() + "#" + new Object() {
					}.getClass().getEnclosingMethod().getName() + ": ";
					final QuestionAnswer qa = (QuestionAnswer) Repository.getObject(Repository.OPERATORMODE);
					final Question question = qa.getQuestion();
					final Answer answer = qa.getAnswer();
					String customerCountry = TagMatchDefinitions.extractXTMHeader(question.getHeaders(), TagMatchDefinitions.getHeaderTagName(TagMatchDefinitions.CUSTOMER_COUNTRY));
					String countryString = null;
					if (customerCountry == null) {
						List<String> buttonTitles = new ArrayList<>();
						String deOption = "Deutschland";
						String atOption = "Österreich";
						buttonTitles.add(deOption);
						buttonTitles.add(atOption);
						Map<String, JRadioButton> radios = selectRadioButton(buttonTitles, 0, "Welches Land soll fuer die Vorlagenauswahl verwendet werden?");
						countryString = radios.get(atOption).isSelected() ? "at" : "de";
					} else {
						countryString = BusinessRule.AT.equals(customerCountry) ? "at" : "de";
					}
					final Integer subprojectId = question.getSubprojectId();
					String typ = qa.getAnswerHeaderPanel().getBigIconComboBox().getSelectedIconValue() == Email.TYPE_EMAIL ? "mail" : "brief";
					List<Object> parameter = new ArrayList<>();
					parameter.add((question.getProjectId() > 0) ? question.getProjectId() : 110);
					parameter.add(subprojectId + "");
					parameter.add(loginname);
					parameter.add(typ);
					parameter.add(countryString);
					parameter.add("Systemvorlage,PersonalSignature,PersonalSignatureHeader,PersonalSignatureFooter");
					int i = 0;
					for (Object par : parameter) {
						SkyLogger.getBRSLogger().debug(logPrefix + question.getId() + " Params: " + i + " par> " + par);
						i++;
					}
					List<Object> result = (List<Object>) API.getClientAPI().getConnectionAPI().sendServerEvent(Actions.ACTION_LOAD_TEXTOBJECT.name(), parameter);
					List<ITextObject> list = (List<ITextObject>) result.get(0);

					if (list != null && !list.isEmpty()) {
						IAttachmentPanel attachmentPanel = qa.getAnswerAttachmentPanel();
						if (SkyLogger.getBRSLogger().isDebugEnabled()) {
							String tlist = "";
							for (ITextObject template : list) {
								if (template != null) {
									tlist += template.getShortDescription() + ",";
								} else {
									tlist += "EMPTY TEMPLATE";
								}
							}
							SkyLogger.getBRSLogger().debug(logPrefix + question.getId() + " Loaded projecttemplates:" + list.size() + " list:" + tlist + " for parameters:" + subprojectId + ":" + typ + ":" + countryString);
						}

						ITextObject systemTemplate = selectTemplate(list, "Systemvorlage", subprojectId, null, typ, countryString, false);
						if (systemTemplate == null) {
							systemTemplate = selectTemplate(list, "Systemvorlage", "default", null, typ, countryString, false);
						}
						SkyLogger.getBRSLogger().debug(logPrefix + question.getId() + " Systemvorlage:" + (systemTemplate == null ? "not found" : systemTemplate.getShortDescription()));

						String templateText = "";
						if (systemTemplate != null) {

							templateText += storeTemplateAttachements(systemTemplate, attachmentPanel, answer);

						}

						Boolean usePersonalSignature = false;
						Boolean useGroupSignature = false;
						ITextObject personalSignature = selectTemplate(list, "PersonalSignature", subprojectId, loginname, typ, countryString, true);
						ITextObject groupSignature = selectTemplate(list, "PersonalSignature", subprojectId, null, typ, countryString, true);

						SkyLogger.getBRSLogger().debug(logPrefix + question.getId() + " PersonalSignature:" + (personalSignature != null ? personalSignature.getShortDescription() + "" : "notFound"));
						SkyLogger.getBRSLogger().debug(logPrefix + question.getId() + " GroupSignature:" + (groupSignature != null ? groupSignature.getShortDescription() + "" : "notFound"));

						if (personalSignature != null || groupSignature != null) {
							ICOperator operatorAPI = API.getClientAPI().getOperatorAPI();
							Properties userProfile = operatorAPI.loadUserProfile(loginname);

							if (userProfile != null) {
								String personalProperty = (String) userProfile.get("sky.signature.personal." + subprojectId);
								usePersonalSignature = personalProperty != null && personalProperty.equals("1");

								personalProperty = (String) userProfile.get("sky.signature.group." + subprojectId);
								useGroupSignature = personalProperty != null && personalProperty.equals("1");
							}

							if (!usePersonalSignature && !useGroupSignature) {
								List<String> buttonTitles = new ArrayList<>();
								String personalOption = "Personalisierte Signatur";
								String groupOption = "Gruppensignatur";
								String persistentPersonalOption = "In diesem Teilprojekt immer personalisierte Signatur verwenden";
								String persistentGroupOption = "In diesem Teilprojekt immer Gruppensignatur verwenden";
								if (personalSignature != null) {
									buttonTitles.add(personalOption);
								}
								if (groupSignature != null) {
									buttonTitles.add(groupOption);
								}
								if (personalSignature != null) {
									buttonTitles.add(persistentPersonalOption);
								}
								if (groupSignature != null) {
									buttonTitles.add(persistentGroupOption);
								}
								if (buttonTitles.size() > 0) {
									Map<String, JRadioButton> radios = selectRadioButton(buttonTitles, 0, "Wählen Sie bitte die Signatur aus");

									if (personalSignature != null && radios.get(personalOption).isSelected()) {
										usePersonalSignature = true;
										useGroupSignature = false;
									} else if (groupSignature != null && radios.get(groupOption).isSelected()) {
										usePersonalSignature = false;
										useGroupSignature = true;
									} else if (personalSignature != null && radios.get(persistentPersonalOption).isSelected()) {
										if (userProfile != null) {
											userProfile.put("sky.signature.personal." + subprojectId, "1");
											userProfile.put("sky.signature.group." + subprojectId, "0");
											operatorAPI.storeUserProfile(loginname, null, userProfile);
										} else {
											SkyLogger.getBRSLogger().warn("Cannot store PersistentPersonalSignature - no userprofilefound: " + loginname);
										}
										usePersonalSignature = true;
										useGroupSignature = false;
									} else if (groupSignature != null && radios.get(persistentGroupOption).isSelected()) {
										if (userProfile != null) {
											userProfile.put("sky.signature.personal." + subprojectId, "0");
											userProfile.put("sky.signature.group." + subprojectId, "1");
											operatorAPI.storeUserProfile(loginname, null, userProfile);
										} else {
											SkyLogger.getBRSLogger().warn("Cannot store PersistentPersonalSignature - no userprofilefound: " + loginname);
										}
										usePersonalSignature = false;
										useGroupSignature = true;
									}
								}
							}

							//SkyLogger.getBRSLogger().debug("PersonalSignature personal: " + usePersonalSignature + " group:" + useGroupSignature);

							if ((usePersonalSignature && personalSignature != null) || (useGroupSignature && groupSignature != null)) {
								ITextObject personalSignatureHeader = selectTemplate(list, "PersonalSignatureHeader", subprojectId, null, typ, countryString, false);
								if (personalSignatureHeader != null) {

									templateText += storeTemplateAttachements(personalSignatureHeader, attachmentPanel, answer);

								}
							}
							if (usePersonalSignature && personalSignature != null) {

								templateText += storeTemplateAttachements(personalSignature, attachmentPanel, answer);

							}
							if (useGroupSignature && groupSignature != null) {

								templateText += storeTemplateAttachements(groupSignature, attachmentPanel, answer);

							}

							if ((usePersonalSignature && personalSignature != null) || (useGroupSignature && groupSignature != null)) {
								ITextObject personalSignatureFooter = selectTemplate(list, "PersonalSignatureFooter", subprojectId, null, typ, countryString, false);
								if (personalSignatureFooter != null) {

									templateText += storeTemplateAttachements(personalSignatureFooter, attachmentPanel, answer);

								}
							}
						}
						if (notEmpty(templateText)) {
							AnswerEmail answerEmailPanel = qa.getAnswerEmailPanel();
							//SkyLogger.getBRSLogger().debug("panelText:"+answerEmailPanel.getText());
							String cleanPanelText = cleanText(answerEmailPanel.getText());
							//SkyLogger.getBRSLogger().debug("ctemplateText:"+templateText);
							//SkyLogger.getBRSLogger().debug("cpanelText:"+cleanPanelText);

							answerEmailPanel.setText("<html><head></head><body>" + templateText + "\n<br/>" + cleanPanelText + "</body></html>");
							ActionCodes.resolveVariables(qa.getAnswerEmailPanel());
						}
					} // else { //no suitable templates found

				} catch (Exception ex) {
					SkyLogger.getBRSLogger().error("Problems with Systemvorlage or PersonalSignature:" + ex.getMessage(), ex);

				}
			}

			public String cleanText(String txt) {
				if (txt == null)
					return "";
				txt = txt.replaceAll("(?i)<(head)*(.*)(</\\1>)", "");
				txt = txt.replaceAll("(?i)</?html>", "");
				txt = txt.replaceAll("(?i)</?body>", "");
				return txt;
			}

			public Map<String, JRadioButton> selectRadioButton(List<String> buttonTitles, Integer defaultIndex, String dialogTitle) {
				Map<String, JRadioButton> radios = new HashMap<>();
				ButtonGroup bg = new ButtonGroup();
				JPanel p = new JPanel(new GridLayout(3, 1));
				for (String title : buttonTitles) {
					JRadioButton radioButton = new JRadioButton(title);
					radios.put(title, radioButton);
					bg.add(radioButton);
					p.add(radioButton);
				}
				bg.setSelected(radios.get(buttonTitles.get(defaultIndex)).getModel(), true);
				JOptionPane.showConfirmDialog(null, p, dialogTitle, JOptionPane.DEFAULT_OPTION);
				return radios;
			}

			private String storeTemplateAttachements(ITextObject signature, IAttachmentPanel attachmentPanel, Answer answer) {
				String logPrefix = getClass().getName() + "#" + new Object() {
				}.getClass().getEnclosingMethod().getName() + ": ";
				//SkyLogger.getBRSLogger().debug(logPrefix + "StoringAttachments for Template:id:" + signature.getId() + " desc:" + signature.getShortDescription() + " att:" + (signature.getDocuments() != null ? signature.getDocuments().size() : 0));

				// Workaround um an die Attachments zu kommen, bei Serverseitigen Aufrufen liefert tdreloaded.getDocuments() null
				ITextObject tdreloaded = API.getClientAPI().getTextObjectAPI().loadDirty(signature.getId());
				if (tdreloaded != null) {
					//  SkyLogger.getBRSLogger().debug(logPrefix + "Reloaded Template for Template:id:" + tdreloaded.getId() + " desc:" + tdreloaded.getShortDescription() + " att:" + (tdreloaded.getDocuments() != null ? tdreloaded.getDocuments().size() : 0));
					//  SkyLogger.getBRSLogger().debug(logPrefix + "Reloaded Template for Template:id:" + tdreloaded.getId()+" lt:"+ tdreloaded.getLoadTime()+" v:"+tdreloaded.getVersion() );
					for (ITextDocument td : tdreloaded.getDocuments()) {
						try {
							SkyLogger.getBRSLogger().debug(logPrefix + " >st_att: path:" + td.getPath() + " name:" + td.getName() + " v:" + td.getVersionNumber() + " class:" + td.getClass() + " bempty?" + (td.getBuffer() != null ? "blenght:" + td.getBuffer().length : "bufferempty"));
							if (!td.isLoaded()) {
								//            SkyLogger.getBRSLogger().debug(logPrefix + " >st_att: ReLoad Doc ");
								// Workaround, da sonnst der Buffer leer und wir haben nichts zum speichern
								API.getClientAPI().getDocumentAPI().loadDocument(td, Document.DOCUMENT_TYPE_TEXTOBJECT, signature.getId());
							}
							if (td.getBuffer() != null) {
								File mdir = new File(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "mediatrix");
								if (!(mdir.exists() && mdir.isDirectory())) {
									SkyLogger.getBRSLogger().debug(logPrefix + "Created Directory:" + mdir.getPath() + " name:" + mdir.getName());
									mdir.mkdir();
								}
								File file = new File(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "mediatrix", td.getName());
								if (!file.exists()) {
									FileOutputStream fo = new FileOutputStream(file);
									fo.write(td.getBuffer());
									fo.close();
									SkyLogger.getBRSLogger().debug(logPrefix + "File saved as: path:" + file.getPath() + " name:" + file.getName() + " tmpDir: " + System.getProperty("java.io.tmpdir"));
								} //else {
								//              SkyLogger.getBRSLogger().debug(logPrefix + "File exists: path:" + file.getPath() + " name:" + file.getName() + " tmpDir: " + System.getProperty("java.io.tmpdir"));
								//}
								attachmentPanel.addAttachment(file);
							}
							//Attachment attachment = new Attachment(answer.getId(), td.getName(), "image/jpg", "binary", "attachment; filename=\"" + td.getName() + "\"");
							//answer.addAttachment(attachment);
							//API.getClientAPI().getAttachmentAPI().store(attachment);
							//attachmentPanel.refresh(answer);
							SkyLogger.getBRSLogger().debug(logPrefix + " >ok_att: org:" + td.getName() + " " + td.getPath());
						} catch (IOException e) {
							SkyLogger.getBRSLogger().error(logPrefix + " > problems in saving attachment:" + td.getName() + " " + td.getPath() + " e:" + e.getMessage());
						}
					}
				} else {
					return cleanText(signature.getLongDescription());
				}
				return cleanText(signature.getLongDescription());
			}

			private boolean notEmpty(String s) {
				return (s != null && !s.trim().isEmpty() && !s.equals("0"));
			}

			public ITextObject selectTemplate(List<ITextObject> list, String preafix, int subprojectId, String login, String typ, String countryString, boolean projectless) {
				return selectTemplate(list, preafix, subprojectId + "", login, typ, countryString, projectless);

			}

			public ITextObject selectTemplate(List<ITextObject> list, String preafix, String subprojectId, String login, String typ, String countryString, boolean projectless) {
				long currenttime = System.currentTimeMillis();

				ITextObject template_sproj_login_typ_country = null;
				ITextObject template_sproj_login_typ = null;
				ITextObject template_sproj_login_country = null;
				ITextObject template_sproj_login = null;

				ITextObject template_login_typ_country = null;
				ITextObject template_login_typ = null;
				ITextObject template_login_country = null;
				ITextObject template_login = null;
				//SkyLogger.getBRSLogger().debug("Searching Template:>" + preafix +"_"+ subprojectId + (login == null ? "" : ("_" + login)) + "_" + typ + "_" + countryString + "<");
				//SkyLogger.getBRSLogger().debug("Searching Template:>" + preafix + (projectless ? "" : ("_" + subprojectId)) + (login == null ? "" : ("_" + login)) + "_" + typ + "_" + countryString + "<");

				for (ITextObject itemplate : list) {
					if (itemplate.isEmailText() && currenttime >= itemplate.getValidFrom() && currenttime <= itemplate.getValidTo() && itemplate.getShortDescription().contains(preafix)) {
						String templatename = itemplate.getShortDescription().trim();
						//SkyLogger.getBRSLogger().debug("Checking Template:>" + templatename+"<");
						if (notEmpty(login)) {
							//SkyLogger.getBRSLogger().debug(">Checking Template:>"+templatename+"< using >" + preafix + "_"+ subprojectId+"_" + login+"<");
							if (notEmpty(subprojectId) && notEmpty(typ) && notEmpty(countryString) && templatename.equalsIgnoreCase(preafix + "_" + subprojectId + "_" + login + "_" + typ + "_" + countryString)) {
								template_sproj_login_typ_country = itemplate;
							} else if (notEmpty(subprojectId) && notEmpty(typ) && templatename.equalsIgnoreCase(preafix + "_" + subprojectId + "_" + login + "_" + typ)) {
								template_sproj_login_typ = itemplate;
							} else if (notEmpty(subprojectId) && notEmpty(countryString) && templatename.equalsIgnoreCase(preafix + "_" + subprojectId + "_" + login + "_" + countryString)) {
								template_sproj_login_country = itemplate;
							} else if (notEmpty(subprojectId) && templatename.equalsIgnoreCase(preafix + "_" + subprojectId + "_" + login)) {
								template_sproj_login = itemplate;
							}//else {
							//  SkyLogger.getBRSLogger().debug(">NOK Template:>"+templatename+"< using >" + preafix + "_"+ subprojectId+"_" + login+"<");
							//}
						} else { //login is empty
							//SkyLogger.getBRSLogger().debug(">Checking Template:>"+templatename+"< using >" + preafix + "_"+ subprojectId+"<");
							if (notEmpty(subprojectId) && notEmpty(typ) && notEmpty(countryString) && templatename.equalsIgnoreCase(preafix + "_" + subprojectId + "_" + typ + "_" + countryString)) {
								template_sproj_login_typ_country = itemplate;
							} else if (notEmpty(subprojectId) && notEmpty(typ) && templatename.equalsIgnoreCase(preafix + "_" + subprojectId + "_" + typ)) {
								template_sproj_login_typ = itemplate;
							} else if (notEmpty(subprojectId) && notEmpty(countryString) && templatename.equalsIgnoreCase(preafix + "_" + subprojectId + "_" + countryString)) {
								template_sproj_login_country = itemplate;
							} else if (notEmpty(subprojectId) && templatename.equalsIgnoreCase(preafix + "_" + subprojectId)) {
								template_sproj_login = itemplate;
							}//else{
							//  SkyLogger.getBRSLogger().debug(">NOK Template:>"+templatename+"< using >" + preafix + "_"+ subprojectId+"<");
							//}

						}
						if (projectless) {
							//SkyLogger.getBRSLogger().debug(">Checking Template:>"+templatename+"< using >" + preafix + "_"+ login+"<");

							if (notEmpty(login)) {
								if (notEmpty(typ) && notEmpty(countryString) && templatename.equalsIgnoreCase(preafix + "_" + login + "_" + typ + "_" + countryString)) {
									template_login_typ_country = itemplate;
								} else if (notEmpty(typ) && templatename.equalsIgnoreCase(preafix + "_" + login + "_" + typ)) {
									template_login_typ = itemplate;
								} else if (notEmpty(countryString) && templatename.equalsIgnoreCase(preafix + "_" + login + "_" + countryString)) {
									template_login_country = itemplate;
								} else if (templatename.equalsIgnoreCase(preafix + "_" + login)) {
									template_login = itemplate;
								}//else{
								//      SkyLogger.getBRSLogger().debug(">NOK Template:>"+templatename+"< using >" + preafix + "_"+ login+"<");
								//}
							} else {
								//SkyLogger.getBRSLogger().debug(">Checking Template:>"+templatename+"< using >" + preafix +"<");

								if (notEmpty(typ) && notEmpty(countryString) && templatename.equalsIgnoreCase(preafix + "_" + typ + "_" + countryString)) {
									template_login_typ_country = itemplate;
								} else if (notEmpty(typ) && templatename.equalsIgnoreCase(preafix + "_" + typ)) {
									template_login_typ = itemplate;
								} else if (notEmpty(countryString) && templatename.equalsIgnoreCase(preafix + "_" + countryString)) {
									template_login_country = itemplate;
								} else if (templatename.equalsIgnoreCase(preafix)) {
									template_login = itemplate;
								}//else{
								//  SkyLogger.getBRSLogger().debug(">NOK Template:>"+templatename+"< using >" + preafix+ "<");
								//}
							}
						}
					} //else {
					// SkyLogger.getBRSLogger().info("WrongType or notValidTemplate " + itemplate.getId() + ":"
					//         + itemplate.getShortDescription());

					//SkyLogger.getBRSLogger().info("t: " + itemplate.isEmailText() );
					//SkyLogger.getBRSLogger().info("z: "+currenttime+":" + itemplate.getValidFrom()+":"+ itemplate.getValidTo()+"/"+(currenttime >= itemplate.getValidFrom() && currenttime <= itemplate.getValidTo()));
					//SkyLogger.getBRSLogger().info("e: " + itemplate.getShortDescription().contains(preafix) );

					//}
				}
				if (template_sproj_login_typ_country != null) {
					return template_sproj_login_typ_country;
				} else if (template_sproj_login_typ != null) {
					return template_sproj_login_typ;
				} else if (template_sproj_login_country != null) {
					return template_sproj_login_country;
				} else if (template_sproj_login != null) {
					return template_sproj_login;

				} else if (template_login_typ_country != null) {
					return template_login_typ_country;
				} else if (template_login_typ != null) {
					return template_login_typ;
				} else if (template_login_country != null) {
					return template_login_country;
				} else if (template_login != null) {
					return template_login;
				} else {
					return null;
				}

			}

		});
		toButton.setToolTipText("Systemvorlage");

		return toButton;
	}

	@Override
	public void postQuestionForward(String loginname, boolean isOperatorMode, Question question, Answer answer, Subproject subproject, String address, int type) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + ": ##ClientQuestionAnswerView.postQuestionForward## enter:" + loginname + " op?" + isOperatorMode + " FType:" + type);
		if (answer != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + ": astime: " + answer.getSendTime() + ": astatus: " + answer.getStatus() + " is Inermediate?" + answer.getStatus().equals(Answer.S_INTERMEDIATE));
		}

		if (address != null && type == 0) {
			SkyLogger.getBRSLogger().info(logPrefix + ": archiving");
			ArchiveTool.archiveCaseWithAnswers(question);
			logCompleted(loginname, question);
		}
		outbound_delegate.postQuestionForward(loginname, isOperatorMode, question, answer, subproject, address, type);
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			agenturdel.postQuestionForward(loginname, isOperatorMode, question, answer, subproject, address, type);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
	}

	@Override
	public boolean preAnswerSend(String loginname, boolean isOperatorModus, Question question, Answer answer) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		SkyLogger.getBRSLogger().debug(logPrefix + " answer:"+answer.getId()+" at:"+answer.getType());
		//String contactid = TagMatchDefinitions.extractXTMHeader(question.getHeaders(), TagMatchDefinitions.getHeaderTagName(TagMatchDefinitions.CONTACT_ID));
		boolean ret = false;

		// Sky-DMS 420
		final QuestionAnswer fa = (QuestionAnswer) Repository.getObject(Repository.OPERATORMODE);
		final IEditorContainer editorContainer = fa.getAnswerEmailPanel().getEditorContainer();

		// SIT System mail addresses cannot be used as recipients, it means that no e-mails can be sent to that list.
		EMAILS_PROHIBITED_TO_SEND = new LinkedList();
		for (String s : StringUtils.split(System.getProperty("sky_emails.prohibited_to_send", "-1"), ",")) {
			if (s != null && !s.isEmpty()) {
				EMAILS_PROHIBITED_TO_SEND.add(s.trim());
				SkyLogger.getClientLogger().info(getClass().getName() + "not send to #" +s.trim());
			} else {
				SkyLogger.getBRSLogger().debug("Cannot Read sky_emails.prohibited_to_send-Value:" + s);
			}
		}
		if (answer.getType() == Email.TYPE_EMAIL) {
			if (answer.getTo() == null || answer.getTo().trim().length() == 0) {
				answer.setTo(JOptionPane.showInputDialog("Bitte eine Empfängeradresse eintragen!"));
			} else if (EMAILS_PROHIBITED_TO_SEND.contains(answer.getTo().toLowerCase())) {
				// SIT System mail addresses cannot be used as recipients, it means that no e-mails can be sent to that list.
				JOptionPane.showMessageDialog(null,
						"An die E-Mail-Adresse \"" + answer.getTo() + "\" kann nicht versendet werden.",
						"Fehler",
						JOptionPane.ERROR_MESSAGE);
				SkyLogger.getClientLogger().info("An die E-Mail-Adresse: " + answer.getTo() + " kann nicht versendet werden.");
				answer.setTo("");
				return false;
			}
		}

		final String text = editorContainer.getDocumentText();
		boolean notSbsProject = TagMatchDefinitions.isNotSbsProject(question);
		if (checkMXTags(text)) {
			if (editorContainer.checked()) {
				editorContainer.spellClick();
			}
			//INCTASK0018947---------------------------------------------------------
			//den Text nach der Rechtschreibprüfung wieder in die Antwort setzten
			//2018.07.03 Ivanfi
			answer.setBody(editorContainer.getText());
			//-----------------------------------------------------------------------
			if (selectPreSend(loginname, question, answer)) {
				if (outbound_delegate.preAnswerSend(loginname, isOperatorModus, question, answer)) {
					if (answer.getStatus().equals(Answer.S_MONITORED)) {
						logCompleted(loginname, question);
					}
					boolean check = true;
					if ((answer.getType() == Email.TYPE_LETTER || answer.getType() == Email.TYPE_FAX)) {
						check = checkFirstName(question);
					}
					SkyLogger.getBRSLogger().info(logPrefix + ": check -> " + check);
					// Letter must have address in SBS
					if (notSbsProject || check) {
						ret = true;
					}
				}
			}
		}

		// Question must be closeable in SBS
		/*
		SkyLogger.getBRSLogger().debug(logPrefix + " answer:"+answer.getId()+" at:"+answer.getType());
		if (answer.isManipulated() || answer.isChanged() || answer.isDirty()) {
			API.getClientAPI().getAnswerAPI().store(answer);
			SkyLogger.getClientLogger().debug(" StoringAnswer aid:" + answer.getId() + " storing Answer:"+answer.getType());
		}
		*/
		ret = ret && (notSbsProject || !isQuestionNotReadyForClose(loginname, question));
		SkyLogger.getBRSLogger().debug(logPrefix + " answer:"+answer.getId()+" at:"+answer.getType());
		
		SkyLogger.getBRSLogger().info(logPrefix + ": exit -> " + ret);
		//	ret = ret && outbound_delegate.preQuestionRequeue(arg0, arg1, arg2, arg3, arg4, arg5);

		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret = ret && agenturdel.preAnswerSend(loginname, isOperatorModus, question, answer);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish:"+ret);
		return ret;
	}

	@Override
	public boolean preQuestionStore(String loginname, boolean isOperatorModus, Question question, Answer answer) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().info(logPrefix + " start");

      /*  if (SEPAindexAction.QUESTION != null) {
			question.setHeaders(SEPAindexAction.QUESTION.getHeaders());
            final MetaInformationInt metaDoc = question.getMetaInformation(MetaInformationDocumentContainer.TYPENAME);
            if (metaDoc != null) {
                CDocumentContainer<CDocument> cont = (CDocumentContainer<CDocument>) metaDoc.getContent();
                List<TagMatch> tags = cont.getPage0Tags();
                CDocument doc = cont.getDocument(0);
                List<TagMatch> tagList = doc.getTags();
                updateTagmatches(question, doc, metaDoc, cont, tags, tagList);
            } else {
                SkyLogger.getBRSLogger().warn("No DocumentContainer for Question " + question.getId());
            }
            SEPAindexAction.QUESTION = null;
        } else {
        */
		//logQuestion(question, logPrefix);
		String headers = question.getHeaders();

		final MetaInformationInt metaDoc = question.getMetaInformation(MetaInformationDocumentContainer.TYPENAME);
		if (metaDoc != null) {
			CDocumentContainer<CDocument> cont = (CDocumentContainer<CDocument>) metaDoc.getContent();
			CDocument doc = cont.getDocument(0);
			question.setExtra4(doc.getFormtype());
			List<TagMatch> tags = cont.getPage0Tags();
			for (String tagName : ClientOutboundRule.CHANGING_FIELDS) {
				for (TagMatch tagMatch: tags){
					if (tagMatch.getIdentifier().equals(tagName) && tagMatch.getTagValue()!=null){
						headers = TagMatchDefinitions.addOrReplaceXTMHeader(headers, tagName, tagMatch.getTagValue());
                        SkyLogger.getBRSLogger().info(logPrefix + "Page Tag Name: " + tagName+"; Identifier: "+tagMatch.getIdentifier() +"; Value: " + tagMatch.getTagValue() + ";");
					}
				}
			}
			for (String tagName : ClientOutboundRule.CHANGING_FORM_TYPES) {
				for (TagMatch tagMatch: tags){
					if (tagMatch.getIdentifier().equals(tagName) && tagMatch.getTagValue()!=null){
						headers = TagMatchDefinitions.addOrReplaceXTMHeader(headers, tagName, doc.getFormtype());
						tagMatch.setTagValue(doc.getFormtype());
						SkyLogger.getBRSLogger().info(logPrefix + "Form Type Name: " + tagName+"; Identifier: "+tagMatch.getIdentifier() +"; Value: " + tagMatch.getTagValue() + ";");
					}
				}
			}

		}

		TagMatch tags = question.getTagMatches();
		for (String tagName : ClientOutboundRule.CHANGING_FIELDS) {
			String tagValue = tags.getTagValue(tagName);
			if (tagName != null && tagValue != null) {
				headers = TagMatchDefinitions.addOrReplaceXTMHeader(headers, tagName, tagValue);
                SkyLogger.getBRSLogger().info(logPrefix + "Tag: <" + tagName + ", " + tagValue + ">");
			}
		}
		for (String tagName : ClientOutboundRule.CHANGING_FORM_TYPES) {
			String tagValue = tags.getTagValue(tagName);
			if (tagName != null && tagValue != null) {
				headers = TagMatchDefinitions.addOrReplaceXTMHeader(headers, tagName, tagValue);
				SkyLogger.getBRSLogger().info(logPrefix + "Tag: <" + tagName + ", " + tagValue + ">");
			}
		}
		question.setHeaders(headers);
		//}
		SkyLogger.getBRSLogger().info(logPrefix + ": exit");

		boolean ret = true;
		ret = ret && outbound_delegate.preQuestionStore(loginname, isOperatorModus, question, answer);

		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret = ret && agenturdel.preQuestionStore(loginname, isOperatorModus, question, answer);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
        logQuestion(question, logPrefix);
		SkyLogger.getBRSLogger().info(logPrefix + " finish");
		return ret;
	}

	@Override
	public boolean preCreateMultiTopicMail(String loginname, boolean isOperatorModus, Question sourceQuestion, Question destinationQuestion) {

		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");

		String docid = TagMatchDefinitions.extractXTMHeader(sourceQuestion.getHeaders(), TagMatchDefinitions.X_TAGMATCH_DOCUMENT_ID);
		TagMatchDefinitions.Channel channel = TagMatchDefinitions.getChannel(TagMatchDefinitions.extractXTMHeader(sourceQuestion.getHeaders(), TagMatchDefinitions.CHANNEL));
		String newDocId = DocIDClient.getOrGenerateDocId(destinationQuestion, TagMatchDefinitions.DocumentDirection.MULTICASE, channel, docid);

		SkyLogger.getBRSLogger().debug("PreCreateMultitopicMail for Questions:" + sourceQuestion.getId() + " dest:" + destinationQuestion.getId() + "  docid:" + newDocId);
		boolean ret = true;
		ret = ret && outbound_delegate.preCreateMultiTopicMail(loginname, isOperatorModus, sourceQuestion, destinationQuestion);

		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret = ret && agenturdel.preCreateMultiTopicMail(loginname, isOperatorModus, sourceQuestion, destinationQuestion);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return ret;
	}

	@Override
	public List<JComponent> getTabList(Question question, Answer answer) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");

		List<JComponent> ret = new LinkedList<JComponent>();
		List<JComponent> od=outbound_delegate.getTabList(question,answer);
		if (od!=null){
			ret.addAll(od);
		}
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			List<JComponent> ag = agenturdel.getTabList(question,answer);
			if (ag!=null) {
				ret.addAll(ag);
				SkyLogger.getBRSLogger().info(logPrefix + aclazz + " agenturdel.getTabList returns size:"+ag.size());
			} else {
				SkyLogger.getBRSLogger().info(logPrefix + aclazz + " agenturdel.getTabList returns null!");
			}
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return ret;
	}


	@Override
	public void postAnswerSend(String loginname, boolean isOperatorMode, Question question, Answer answer) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		logPrefix += " q.id:" + question.getId() + " a.id:" + answer.getId() + " ";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");

       /* List<Object> aparameters=new LinkedList<>();
        aparameters.add(new Integer(110));
        aparameters.add(new Integer(123));
        aparameters.add(new Integer(123));
        API.getClientAPI().getConnectionAPI().sendServerEvent(Actions.ACTION_GETPROJECTINFO.name(), aparameters);
        */
		SkyLogger.getBRSLogger().debug(logPrefix + ": ##ClientQuestionAnswerView.postAnswerSend## enter:" + loginname + " op?" + isOperatorMode + ": astime: " + answer.getSendTime() + ": astatus: " + answer.getStatus() + " is Inermediate?" + answer.getStatus().equals(Answer.S_INTERMEDIATE));


		//cost: 2s Performance - needed here?
		//Operator operator = API.getClientAPI().getOperatorAPI().loadByLoginName(loginname);
		//SkyLogger.getBRSLogger().debug(logPrefix + "opid:"+ operator.getId()+ " opl:"+operator.getLogin()+ " maxMon:"+operator.getMaxMonitorDuration());


		// We need to add a check if answering the document implicitly
		// closes the document. How to extract this information?
		// Currently Zwischenantworten do not trigger archiving

		//if (!answer.getStatus().equals(Answer.S_INTERMEDIATE_ANSWER)) {
		//    SkyLogger.getBRSLogger().debug(logPrefix + ": archiving case number: " + question.getCaseId());
		//    ArchiveTool.writeCase(question, logPrefix);
		// logCompleted(loginname, question);
		// }

		outbound_delegate.postAnswerSend(loginname, isOperatorMode, question, answer);
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			agenturdel.postAnswerSend(loginname, isOperatorMode, question, answer);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
	}

	@Override
	public boolean postAttachmentDelete(String loginname, boolean isOperatorMode, Question question, Answer answer, Attachment attachment) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");

		boolean ret = true;
		ret = ret && outbound_delegate.postAttachmentDelete(loginname,  isOperatorMode,  question,  answer,  attachment) ;

		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret = ret && agenturdel.postAttachmentDelete(loginname,  isOperatorMode,  question,  answer,  attachment) ;
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return ret;
	}

	@Override
	public boolean postAttachmentInsert(String loginname, boolean isOperatorMode, Question question, Answer answer, Attachment attachment) {

		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		boolean ret = true;
		ret = ret && outbound_delegate.postAttachmentInsert(loginname,  isOperatorMode,  question,  answer,  attachment);

		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret = ret && agenturdel.postAttachmentInsert(loginname,  isOperatorMode,  question,  answer,  attachment);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return ret;
	}

	@Override
	public boolean postAttachmentView(String loginname, boolean isOperatorMode, Question question, Answer answer, Attachment attachment) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		boolean ret = true;
		ret = ret && outbound_delegate.postAttachmentView(loginname,  isOperatorMode,  question,  answer,  attachment);

		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret = ret && agenturdel.postAttachmentView(loginname,  isOperatorMode,  question,  answer,  attachment);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return ret;
	}

	@Override
	public void postCancel(String loginname, boolean isOperatorMode, Question question, Answer answer) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");

		BRSession session = BRSessionPoolFactory.getBRSessionPool().getBRSession("SkyReindex");
		session.put("OperatorMode", TagMatchDefinitions.FALSE);
		SkyLogger.getBRSLogger().info(logPrefix + ": exit");
		outbound_delegate.postCancel(loginname,  isOperatorMode,  question,  answer) ;
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			agenturdel.postCancel(loginname,  isOperatorMode,  question,  answer) ;
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
	}

	@Override
	public boolean postHistoryInsert(String loginname, boolean isOperatorMode, Question question, Answer answer, Attachment attachment) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		boolean ret = true;
		ret = ret && outbound_delegate.postHistoryInsert(loginname,  isOperatorMode,  question,  answer,  attachment) ;

		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret = ret && agenturdel.postHistoryInsert(loginname,  isOperatorMode,  question,  answer,  attachment) ;
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return ret;
	}

	@Override
	public void postKeywordDelete(String loginname, boolean isOperatorMode, Question question, Answer answer, Keyword keyword) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");

		throw new UnsupportedOperationException(Messages.getString("ClientQuestionAnswerView.54"));
	}

	@Override
	public void postKeywordInsert(String loginname, boolean isOperatorMode, Question question, Answer answer, Keyword keyword) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		outbound_delegate.postKeywordInsert(loginname,  isOperatorMode,  question,  answer,  keyword) ;
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			agenturdel.postKeywordInsert(loginname,  isOperatorMode,  question,  answer,  keyword) ;
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");

	}

	@Override
	public void postMetaInformationView(String login, boolean isOperatorMode, Question question, Answer answer, MetaInformationInt metainfo) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().info(logPrefix + " start");

		//logQuestion(question, logPrefix);
        //logMetaInformation(metainfo,logPrefix);

		outbound_delegate.postMetaInformationView(login,  isOperatorMode,  question,  answer,  metainfo);
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			agenturdel.postMetaInformationView(login,  isOperatorMode,  question,  answer,  metainfo);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().info(logPrefix + " finish");

	}

	@Override
	public void postQuestionComplete(String loginname, boolean isOperatorMode, Question question, Answer answer) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");

        /*List<Object> aparameters=new LinkedList<>();
        aparameters.add(new Integer(110));
        aparameters.add(new Integer(123));
        aparameters.add(new Integer(123));
        API.getClientAPI().getConnectionAPI().sendServerEvent(Actions.ACTION_GETPROJECTINFO.name(), aparameters);
        */

		API.getClientAPI().getQuestionAPI().store(question);

		ArchiveTool.archiveCase(question);

		if (CANCELLATION_SUBPROJECT.contains(question.getSubprojectId())) {
			List<Object> parameter = new ArrayList<Object>();
			parameter.add((question.getProjectId() > 0) ? question.getProjectId() : 110);
			parameter.add(question.getId());
			boolean notclosedaction = false;

			List<Object> result = (List<Object>) API.getClientAPI().getConnectionAPI().sendServerEvent(Actions.ACTION_OPENCANCELLATIONACTION.name(), parameter);
			if (result != null && result.size() > 1) {
				Object sucessO = result.get(0);
				notclosedaction = sucessO instanceof Boolean ? ((Boolean) sucessO) : false;
			}

			if (notclosedaction) {
				//suspended
				question.setStatus(Question.S_MONITORED);
				SkyLogger.getBRSLogger().debug("Setting state for Question:" + question.getDocId() + " " + question.getId() + " to Monitored state");
				API.getClientAPI().getQuestionAPI().store(question);
			} else {
				logCompleted(loginname, question);
			}
		} else {
			logCompleted(loginname, question);
		}
		outbound_delegate.postQuestionComplete(loginname, isOperatorMode, question, answer);
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			agenturdel.postQuestionComplete(loginname, isOperatorMode, question, answer);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
	}

	@Override
	public void postTextObjectInsert(String loginname, boolean isOperatorMode, Question question, Answer answer, ITextObject tb) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		outbound_delegate.postTextObjectInsert(loginname,  isOperatorMode,  question,  answer,  tb) ;
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			agenturdel.postTextObjectInsert(loginname,  isOperatorMode,  question,  answer,  tb) ;
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
	}

	@Override
	public boolean preAttachmentDelete(String loginname, boolean isOperatorMode, Question question, Answer answer, Attachment attachment) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		boolean ret = true;
		ret = ret && outbound_delegate.preAttachmentDelete(loginname,  isOperatorMode,  question,  answer,  attachment) ;
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret = ret && agenturdel.preAttachmentDelete(loginname,  isOperatorMode,  question,  answer,  attachment) ;
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return ret;
	}


	@Override
	public boolean preAttachmentView(String loginname, boolean isOperatorMode, Question question, Answer answer, Attachment attachment) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");

		boolean ret = true;
		ret = ret && outbound_delegate.preAttachmentView(loginname,  isOperatorMode,  question,  answer,  attachment) ;

		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret = ret && agenturdel.preAttachmentView(loginname,  isOperatorMode,  question,  answer,  attachment) ;
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return ret;
	}

	@Override
	public boolean preCancel(String loginname, boolean isOperatorMode, Question question, Answer answer) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		boolean ret = true;
		ret = ret && outbound_delegate.preCancel(loginname,  isOperatorMode,  question,  answer);

		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret = ret && agenturdel.preCancel(loginname,  isOperatorMode,  question,  answer);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return ret;
	}

	@Override
	public boolean preHistoryInsert(String loginname, boolean isOperatorMode, Question question, Answer answer, Attachment attachment) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		boolean ret = true;
		ret = ret && outbound_delegate.preHistoryInsert(loginname,  isOperatorMode,  question,  answer,  attachment);

		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret = ret && agenturdel.preHistoryInsert(loginname,  isOperatorMode,  question,  answer,  attachment);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return ret;
	}

	@Override
	public boolean preKeywordDelete(String loginname, boolean isOperatorMode, Question question, Answer answer, Keyword keyword) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		boolean ret = true;
		ret = ret && outbound_delegate.preKeywordDelete(loginname,  isOperatorMode,  question,  answer,  keyword);

		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret = ret && agenturdel.preKeywordDelete(loginname,  isOperatorMode,  question,  answer,  keyword);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return ret;
	}

	@Override
	public boolean preKeywordInsert(String loginname, boolean isOperatorMode, Question question, Answer answer, Keyword keyword) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		boolean ret = true;
		ret = ret && outbound_delegate.preKeywordInsert(loginname,  isOperatorMode,  question,  answer,  keyword);

		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret = ret && agenturdel.preKeywordInsert(loginname,  isOperatorMode,  question,  answer,  keyword);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return ret;
	}

	@Override
	public boolean preMetaInformationView(String loginname, boolean isOperatorMode, Question question, Answer answer, MetaInformationInt metainfo) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().info(logPrefix + " start");

        //logQuestion(question, logPrefix);
        //logMetaInformation(metainfo,logPrefix);

		boolean ret = true;
		ret = ret && outbound_delegate.preMetaInformationView(loginname,  isOperatorMode,  question,  answer,  metainfo);

		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret = ret && agenturdel.preMetaInformationView(loginname,  isOperatorMode,  question,  answer,  metainfo);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().info(logPrefix + " finish");
		return ret;
	}

	@Override
	public boolean preQuestionAnswerView(String loginname, boolean isOperatorMode, Question question, Answer answer) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		if (isOperatorMode) {
			BRSession session = BRSessionPoolFactory.getBRSessionPool().getBRSession("SkyReindex");
			session.put("OperatorMode", TagMatchDefinitions.TRUE);
		}

		SkyLogger.getBRSLogger().info(logPrefix + ": exit");
		boolean ret = true;
		ret = ret && outbound_delegate.preQuestionAnswerView( loginname,  isOperatorMode,  question,  answer);

		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret = ret && agenturdel.preQuestionAnswerView(loginname,  isOperatorMode,  question,  answer);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return ret;
	}

	@Override
	public void postQuestionAnswerView(String loginname, boolean isOperatorMode, Question question, Answer answer) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		outbound_delegate.postQuestionAnswerView(loginname,  isOperatorMode,  question,  answer);
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			agenturdel.postQuestionAnswerView(loginname,  isOperatorMode,  question,  answer);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		API.getClientAPI().getProcessingAPI().setOperatorModeStart(isOperatorMode);

		// outboundrule.postQuestionAnswerView(loginname, isOperatorMode,
		// question, answer);
		final QuestionAnswer qa = (QuestionAnswer) Repository.getObject(Repository.OPERATORMODE);

		final ToolBarPanel toolBarPanel = qa.getToolBarPanel();
		toolBarPanel.addButton(createTOButton(loginname));
		// SBS
		if (TagMatchDefinitions.isSbsProject(question)) {
			SkyLogger.getBRSLogger().info(logPrefix + ": SBS .");
			String headers = question.getHeaders();
			SkyLogger.getBRSLogger().info(logPrefix + ": headers " + headers);
			String customerId = TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.CUSTOMER_ID);
			final JTextField customerField = new JTextField((customerId == null) ? "" : customerId);
			customerField.setColumns(10);
			customerField.setToolTipText("CustomerID");
			final JPanel jPanelMain = toolBarPanel.getJPanelMain();
			toolBarPanel.addSeparator();
			jPanelMain.add(customerField);
			jPanelMain.add(createCustomerSaveButton(customerField));
			toolBarPanel.addSeparator();
		}

		final IEditorContainer editorContainer = qa.getAnswerEmailPanel().getEditorContainer();
		editorContainer.checkWords(true);

		final BigIconComboBox iconComboBoxType = qa.getAnswerHeaderPanel().getBigIconComboBox();
		ItemListener[] listeners = iconComboBoxType.getItemListeners();
		/*for (ItemListener listener : listeners) {
			iconComboBoxType.removeItemListener(listener);
		}
		*/
		iconComboBoxType.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				Class clazz = getClass();
				String name = new Object() {
				}.getClass().getEnclosingMethod().getName();
				String logPrefix = clazz.getName() + "#" + name;
				SkyLogger.getBRSLogger().info(logPrefix + ": enter");
				itemChanged(e, iconComboBoxType);
				SkyLogger.getBRSLogger().info(logPrefix + ": exit");
			}
		});
		//default value
		String currChannel=TagMatchDefinitions.extractXTMHeader(answer.getHeaders(), TagMatchDefinitions.CHANNEL);
		SkyLogger.getBRSLogger().info(logPrefix + ": current Channel:"+currChannel+" getType:"+answer.getType());
		if ((currChannel==null || currChannel.isEmpty()) &&  question.getType() == Email.TYPE_DOCUMENT || question.getType() == Email.TYPE_FAX) {
			SkyLogger.getBRSLogger().info(logPrefix + ": setting default Channel for Fax to Letter");
			iconComboBoxType.setSelectedIconValue(Email.TYPE_LETTER);
		}else if (currChannel!=null && !currChannel.isEmpty()){
			if (currChannel.equals(TagMatchDefinitions.Channel.BRIEF.toString())){
				SkyLogger.getBRSLogger().info(logPrefix + ": restore Channel to Letter");
				answer.setType(Email.TYPE_LETTER);
				//answer.setHeaders(TagMatchDefinitions.addOrReplaceXTMHeader(answer.getHeaders(), TagMatchDefinitions.CHANNEL, TagMatchDefinitions.Channel.BRIEF.toString()));
				iconComboBoxType.setSelectedIconValue(Email.TYPE_LETTER);

			}else if (currChannel.equals(TagMatchDefinitions.Channel.EMAIL.toString())){
				SkyLogger.getBRSLogger().info(logPrefix + ": restore Channel to Email");
				answer.setType(Email.TYPE_EMAIL);
				//answer.setHeaders(TagMatchDefinitions.addOrReplaceXTMHeader(answer.getHeaders(), TagMatchDefinitions.CHANNEL, TagMatchDefinitions.Channel.EMAIL.toString()));
				iconComboBoxType.setSelectedIconValue(Email.TYPE_EMAIL);
			}
		}
		
		JButton jbPreview = (JButton) qa.getButton("preview");
		jbPreview.setVisible(true);
		jbPreview.setEnabled(true);
		previewAnswer(qa.getAnswerEmailPanel(), answer, question, jbPreview);

		JButton jbSend = (JButton) qa.getButton(QuestionAnswer.SENDEN_BUTTON);
		jbSend.setVisible(ClientUtils.canSend(question, answer));
		jbSend.setEnabled(ClientUtils.canSend(question, answer));

		
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
	}

	protected JButton createCustomerSaveButton(final JTextField customerField) {
		final JButton customerIdButton = new JButton(new AbstractAction("Save", new ImageIcon(ClientQuestionAnswerView.class.getClassLoader().getResource("Save.png"))) {

			@Override
			public void actionPerformed(ActionEvent e) {
				int questionid = 0;
				final String text = customerField.getText();
				if (text.matches("\\d{10}|0")) {
					try {
						final QuestionAnswer qa = (QuestionAnswer) Repository.getObject(Repository.OPERATORMODE);
						Question question = qa.getQuestion();
						assert question != null;
						questionid = question.getId();
						sbsIndex(null, customerField.getText(), true);
						ClientUtils.reloadQuestion(API.getClientAPI().getProcessingAPI().isOperatorModeStart(), questionid, null);
						refreshTable();
					} catch (Exception ex) {
						SkyLogger.getBRSLogger().error("Problem during ReindexAction qid:" + questionid + (ex.getMessage() != null ? " ex:" + ex.getMessage() : ""), ex);
						ex.printStackTrace();
						JOptionPane.showMessageDialog(null, "Fehler bei der Indizierung des Kunden.");
					}
				} else {
					JOptionPane.showMessageDialog(null, "\"" + text + "\" ist keine gültige Kundennummer.");
				}
			}
		});
		customerIdButton.setToolTipText("Save");

		return customerIdButton;
	}

	@Override
	public boolean preTextObjectInsert(String loginname, boolean isOperatorMode, Question question, Answer answer, ITextObject tb) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		boolean ret = true;
		ret = ret && outbound_delegate.preTextObjectInsert(loginname,  isOperatorMode,  question,  answer,tb);

		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret = ret && agenturdel.preTextObjectInsert(loginname,  isOperatorMode,  question,  answer,tb);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return ret;
	}

	@Override
	public void postQuestionStore(String loginname, boolean isOperatorMode, Question question, Answer answer) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().info(logPrefix + " start");

     /* List<Object> aparameters=new LinkedList<>();
        aparameters.add(new Integer(110));
        aparameters.add(new Integer(123));
        aparameters.add(new Integer(123));
        API.getClientAPI().getConnectionAPI().sendServerEvent(Actions.ACTION_GETPROJECTINFO.name(), aparameters);
        */
        //logQuestion(question, logPrefix);
		outbound_delegate.postQuestionStore(loginname,  isOperatorMode,  question,  answer);
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			agenturdel.postQuestionStore(loginname,  isOperatorMode,  question,  answer);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
        //logQuestion(question, logPrefix);
		SkyLogger.getBRSLogger().info(logPrefix + " finish");
	}

	@Override
	public void postQuestionRequeue(String arg0, boolean arg1, Question arg2, Answer arg3, Subproject arg4, int arg5, long arg6) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		outbound_delegate.postQuestionRequeue(arg0, arg1, arg2, arg3, arg4, arg5, arg6);
		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			agenturdel.postQuestionRequeue(arg0, arg1, arg2, arg3, arg4, arg5, arg6);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
	}

	@Override
	public boolean preQuestionRequeue(String arg0, boolean arg1, Question arg2, Answer arg3, Subproject arg4, int arg5, long arg6) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");

		boolean ret = true;
		ret = ret && outbound_delegate.preQuestionRequeue(arg0, arg1, arg2, arg3, arg4, arg5, arg6);

		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret = ret && agenturdel.preQuestionRequeue(arg0, arg1, arg2, arg3, arg4, arg5, arg6);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return ret;
	}

	private void itemChanged(ItemEvent e,  BigIconComboBox iconComboBoxType) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":"+e.getStateChange();
		QuestionAnswer qa = (QuestionAnswer) Repository.getObject(Repository.OPERATORMODE);
		Answer answer = qa.getAnswer();
		if (answer!=null) {
			SkyLogger.getBRSLogger().debug(logPrefix + " start:" + answer.getType() + " a:" + answer.getId()+ " at:" + answer.getType());
		}
		if (e.getStateChange() == ItemEvent.SELECTED) {
			if (iconComboBoxType.getSelectedIconValue() == Email.TYPE_LETTER || iconComboBoxType.getSelectedIconValue() == Email.TYPE_FAX) {
				answer.setType(Email.TYPE_LETTER);
				answer.setHeaders(TagMatchDefinitions.addOrReplaceXTMHeader(answer.getHeaders(), TagMatchDefinitions.CHANNEL, TagMatchDefinitions.Channel.BRIEF.toString()));
				SkyLogger.getBRSLogger().debug(logPrefix +" a:" + answer.getId()+ " at:" + answer.getType() );
				qa.getAnswerEmailPanel().getAnswerHeader().setComponentVisible(AnswerHeader.COMPONENTS.CC.toString(), false);
				qa.getAnswerEmailPanel().getAnswerHeader().setComponentVisible(AnswerHeader.COMPONENTS.BCC.toString(), false);
				qa.getAnswerEmailPanel().getAnswerHeader().setComponentVisible(AnswerHeader.COMPONENTS.SUBJECT.toString(), false);
				qa.getAnswerEmailPanel().getAnswerHeader().setComponentVisible(AnswerHeader.COMPONENTS.EXPAND.toString(), false);
				JButton jbPreview = (JButton) qa.getButton(QuestionAnswer.PREVIEW_BUTTON);
				previewAnswer(qa.getAnswerEmailPanel(), answer, qa.getQuestion(), jbPreview);
				// ActionListener[] listeners = jbPreview.getActionListeners();
				// for (int i = 0; i < listeners.length; i++) {
				// jbPreview.removeActionListener(listeners[i]);
				// }
				// jbPreview.addActionListener(new ActionListener() {
				// @Override
				// public void actionPerformed(ActionEvent e) {
				// try {
				// if (iconComboBoxType.getSelectedIconValue() ==
				// Email.TYPE_LETTER) {
				// byte[] pdf = ClientTemplateExtension
				// .getInstance()
				// .createPdf(
				// new PdfFile(
				// qa.getAnswer()
				// .getBody(),
				// qa.getQuestion()
				// .getSubprojectId(),
				// qa.getQuestion()
				// .getLanguage(),
				// ClientUtils
				// .getParameter(qa
				// .getAnswer())),
				// null);
				// ClientUtils.preview(pdf);
				// } else {
				// qa.getAnswerEmailPanel().preview();
				// }
				// } catch (Exception ex) {
				// JOptionPane.showMessageDialog(Start.getInstance(),
				// ClientUtils.exception(ex).toString());
				// }
				// }
				// });
				qa.getAnswerEmailPanel().setSubject(Constant.LETTER_SUBJECT);
			} else {
				answer.setType(Email.TYPE_EMAIL);
				answer.setHeaders(TagMatchDefinitions.addOrReplaceXTMHeader(answer.getHeaders(), TagMatchDefinitions.CHANNEL, TagMatchDefinitions.Channel.EMAIL.toString()));
				SkyLogger.getBRSLogger().debug(logPrefix + " setAnswerType:" + answer.getType() + " a:" + answer.getId());
				qa.getAnswerEmailPanel().getAnswerHeader().setAllVisible();
				qa.getAnswerEmailPanel().getAnswerHeader().setComponentVisible(AnswerHeader.COMPONENTS.CC.toString(), false);
				qa.getAnswerEmailPanel().getAnswerHeader().setComponentVisible(AnswerHeader.COMPONENTS.BCC.toString(), false);
			}
			if (answer!=null) {
				SkyLogger.getBRSLogger().debug(logPrefix + " start:" + answer.getType() + " a:" + answer.getId()+ " at:" + answer.getType());
			}
			
		}
	}

	@Override
	public boolean preQuestionComplete(String loginname, boolean isOperatorModus,  Question question, Answer answer) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");

		boolean ret = true;
		/*SkyLogger.getBRSLogger().debug(logPrefix + " answer:"+answer.getId()+" at:"+answer.getType());
		if (answer.isManipulated() || answer.isChanged() || answer.isDirty()) {
			API.getClientAPI().getAnswerAPI().store(answer);
			SkyLogger.getClientLogger().debug(" StoringAnswer aid:" + answer.getId() + " storing Answer:"+answer.getType());
		}*/
		if (isQuestionNotReadyForClose(loginname, question)) {
			SkyLogger.getBRSLogger().debug(logPrefix + " answer:"+answer.getId()+" at:"+answer.getType());
			return false;
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " answer:"+answer.getId()+" at:"+answer.getType());
		
		ret=ret& askCompletionParametersAndExecuteAction(loginname, question, answer, ClientOutboundRule.REASON_COMPLETE, null, true);


		ret = ret && outbound_delegate.preQuestionComplete(loginname,  isOperatorModus,   question,  answer);

		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret = ret && agenturdel.preQuestionComplete(loginname,  isOperatorModus,   question,  answer);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return ret;
	}

	@Override
	public boolean preQuestionForward(String loginname, boolean isOperatorMode, Question question, Answer answer, Subproject subproject, String address, int type) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		// Checks if forwarding is permitted.
		boolean ret = checkForwarding(loginname, question, subproject, type);

		ret = ret && outbound_delegate.preQuestionForward(loginname, isOperatorMode, question, answer, subproject, address, type);

		if (agenturdel != null) {
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " start call");
			ret = ret && agenturdel.preQuestionForward(loginname, isOperatorMode, question, answer, subproject, address, type);
			SkyLogger.getBRSLogger().debug(logPrefix + aclazz + " finished call");
		}
		SkyLogger.getBRSLogger().debug(logPrefix + " finish");
		return ret;
	}

	public JButton getIndexingButton(final Question question) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");

		final JButton reindexButton = new JButton(new ReindexAction("Reindex", new ImageIcon(ClientOutboundRule.class.getClassLoader().getResource("Reindex.png"))));
		reindexButton.setToolTipText("Reindex");
		return reindexButton;
	}

	public JButton getAddTableButton(final Question question) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");

		final JButton addTableButton = new JButton(new AddTableAction("Add table", new ImageIcon(ClientOutboundRule.class.getClassLoader().getResource("AddTable.png"))));
		addTableButton.setToolTipText("Insert table");
		return addTableButton;
	}

	public JButton getCancellationQAButton(final Question question) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");
		
		final JButton cancellationButton = new JButton(new CancellationSRAction("Cancellation", new ImageIcon(ClientOutboundRule.class.getClassLoader().getResource("Cancel2.png"))));
		cancellationButton.setToolTipText("Cancellation QuickAction");
		
		return cancellationButton;
	}

	public JButton getMandateIndexingButton(final Question question) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");

		final JButton reindexButton = new JButton(new SEPAindexAction("SEPAindex", new ImageIcon(ClientOutboundRule.class.getClassLoader().getResource("CheckValid.png"))));
		reindexButton.setToolTipText("SEPAindex");
		return reindexButton;
	}

	public JButton getSBSIndexingButton(final Question question) {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":";
		SkyLogger.getBRSLogger().debug(logPrefix + " start");

		final JButton reindexButton = new JButton(new SBSindexAction("SBSindex", new ImageIcon(ClientOutboundRule.class.getClassLoader().getResource("CheckValid.png"))));
		reindexButton.setToolTipText("SBSindex");
		return reindexButton;
	}

	/* TEst Gregory */
	private void logQuestion(Question question, String logPrefix){
        final MetaInformationInt metaDoc = question.getMetaInformation(MetaInformationDocumentContainer.TYPENAME);
        logMetaInformation(metaDoc, logPrefix);
    }

    private void logMetaInformation(MetaInformationInt metaDoc, String logPrefix){
        if (metaDoc != null) {
            CDocumentContainer<CDocument> cont = (CDocumentContainer<CDocument>) metaDoc.getContent();
            if (cont != null) {
                List<CDocument> cdocs = cont.getDocuments();
                SkyLogger.getBRSLogger().info(logPrefix + " Document count: "+cdocs.size());
                for (CDocument cdoc : cdocs) {
                    SkyLogger.getBRSLogger().info(logPrefix + " Formtype: " + cdoc.getFormtype() + "; Title: " + cdoc.getTitle() +
                            "; Content Type: " + cdoc.getContenttype() + "; Document Type: " + cdoc.getDocumentType());
                }
                List<TagMatch> tags = cont.getPage0Tags();
                SkyLogger.getBRSLogger().info(logPrefix + " TagMatch count: "+tags.size());
                for (TagMatch tag : tags) {
                    SkyLogger.getBRSLogger().info(logPrefix + " TagMatch - Identifier: " + tag.getIdentifier() + "; Value: " +
                            tag.getTagValue() + "; Caption: " + tag.getCaption());
                }
            } else {
                SkyLogger.getBRSLogger().info(logPrefix +" Content is null!");
            }
        } else {
            SkyLogger.getBRSLogger().info(logPrefix +" MetaInformation is null!");
        }

    }


}
