package com.nttdata.de.sky.ityx.common;

import com.nttdata.de.sky.archive.ClientUtils;
import com.nttdata.de.sky.archive.Constant;
import com.nttdata.de.sky.pdf.ClientTemplateExtension;
import com.nttdata.de.sky.pdf.PdfFile;
import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.client.dialog.util.IconComboBox;
import de.ityx.mediatrix.client.dialog.util.newmail.NewMailFrame;
import de.ityx.mediatrix.data.Customer;
import de.ityx.mediatrix.data.Email;
import de.ityx.mediatrix.data.Question;
import de.ityx.mediatrix.data.SubprojectInfo;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.Field;
import java.util.HashMap;

//import de.ityx.sky.outbound.extensions.template.ClientTemplateExtension;
//import de.ityx.sky.outbound.pdf.PdfFile;

//import de.ityx.mediatrix.data.GlobalVariable;

/**
 * extends the new service center frame to send the letters.
 */
public class ExtendedNewMailFrame extends NewMailFrame {
	private static final long serialVersionUID = 4204097186598719937L;
	private ActionListener previewListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				Question question = prepareQuestion(ExtendedNewMailFrame.this, true);
				HashMap<String, Object> parameter = ClientUtils.getParameter(question, true);
				byte[] pdf = ClientTemplateExtension.getInstance().createPdf(new PdfFile(question.getBody(), question.getSubprojectId(), question.getLanguage(), !TagMatchDefinitions.isSbsProject(question), parameter), null);
				ClientUtils.preview(pdf);
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(ExtendedNewMailFrame.this, ClientUtils.exception(ex).toString());
			}
		}
	};

	public ExtendedNewMailFrame() {
		super();
		initIdentityComboBox();
		// commonTypes.add(Email.TYPE_LETTER);
		initIconComboBox();
	}

	public ExtendedNewMailFrame(Question question) {
		super(question);
		initIdentityComboBox();
		// commonTypes.add(Email.TYPE_LETTER);
		initIconComboBox();
	}

	/**
	 * Rules are only defined for Projects not for operator! Therefore the
	 * searchmask will be disabled if an operator is selected.
	 */
	private void initIdentityComboBox() {
		super.jComboBoxSubprojects.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Object obj = jComboBoxSubprojects.getSelectedItem();
				if (!(obj instanceof SubprojectInfo)) {
					removeRuleTab();
				}
			}
		});

	}

	private void initIconComboBox() {
		ItemListener[] listeners = iconComboBoxType.getItemListeners();
		for (int i = 0; i < listeners.length; i++) {
			iconComboBoxType.removeItemListener(listeners[i]);
		}
		this.iconComboBoxType.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				itemChanged(e);
			}
		});
	}

	// @Override
	@Override
	protected boolean isEmptyEmail(String email) {
		// if (super.iconComboBoxType.getSelectedIconValue() ==
		// Email.TYPE_EMAIL) {
		// return super.isEmptyEmail(email);
		// }
		// else {
		return false;
		// }
	}

	// @Override
	// protected boolean toNotValid() {
	// if (super.iconComboBoxType.getSelectedIconValue() == Email.TYPE_EMAIL) {
	// return toNotValid();
	// }
	// else {
	// return false;
	// }
	// }

	private void itemChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			if (iconComboBoxType.getSelectedIconValue() == Email.TYPE_LETTER || iconComboBoxType.getSelectedIconValue() == Email.TYPE_FAX) {
				setComponentVisible(NewMailFrame.COMPONENTS.TO.toString(), false);
				setComponentVisible(NewMailFrame.COMPONENTS.CC.toString(), false);
				setComponentVisible(NewMailFrame.COMPONENTS.BCC.toString(), false);
				setComponentVisible(NewMailFrame.COMPONENTS.SUBJECT.toString(), false);

				JButton jbPreview = (JButton) getButton(AC_PREVIEW);
				ActionListener[] listeners = jbPreview.getActionListeners();
				for (int i = 0; i < listeners.length; i++) {
					jbPreview.removeActionListener(listeners[i]);
				}
				jbPreview.addActionListener(getNewMailPreviewListener(previewListener, this));

				jtfSubject.setText(Constant.LETTER_SUBJECT);
			} else {
				setAllVisible();
			}
		}
	}

	/**
	 * @return
	 */
	public static ActionListener getNewMailPreviewListener(final ActionListener previewListener, final NewMailFrame frame) {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					System.err.println(frame.getQuestion().getType());
					Field field = NewMailFrame.class.getDeclaredField("iconComboBoxType");
					field.setAccessible(true);
					IconComboBox iconComboBox = (IconComboBox) field.get(frame);
					frame.getQuestion().setType(iconComboBox.getSelectedIconValue());
					if (iconComboBox.getSelectedIconValue() == Email.TYPE_LETTER && frame.getSelected() instanceof SubprojectInfo) {
						previewListener.actionPerformed(e);
					} else {
						frame.preview();
					}
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(frame, ClientUtils.exception(ex).toString());
				}
			}
		};
	}

	private static void initQuestion(SubprojectInfo obj, NewMailFrame frame) {
		frame.setSubproject(obj);
		Customer customer = frame.selectKunde(frame.getTo());
		if (customer.getId() == 0) {
			customer.setProjectId((frame.getQuestion().getProjectId() > 0) ? frame.getQuestion().getProjectId() : 110);
			customer.setEmail(frame.getTo());
			API.getClientAPI().getCustomerAPI().store(customer);
		}
		frame.setCustomer(customer);
		frame.getQuestion().setCaseId(customer.getDefaultCaseId());
	}

	/**
	 * @return
	 */
	public static Question prepareQuestion(NewMailFrame frame, boolean mediatrixCustomer) {
		if (mediatrixCustomer) {
			initQuestion((SubprojectInfo) frame.getSelected(), frame);
		}
		Question question = frame.getQuestion();
		question.setSubject(frame.getSubject());
		question.setBody(frame.getBody());
		question.setServicecenter(false);
		question.setEmailDate(System.currentTimeMillis());
		try {
			Field field = NewMailFrame.class.getDeclaredField("iconComboBoxType");
			field.setAccessible(true);
			IconComboBox iconComboBox = (IconComboBox) field.get(frame);
			question.setType(iconComboBox.getSelectedIconValue());
		} catch (Exception e) {
			e.printStackTrace();
		}
		question.setAttachments(frame.getAttachments());
		return question;
	}

	public ActionListener getPreviewListener() {
		return previewListener;
	}

	public void setPreviewListener(ActionListener previewListener) {
		this.previewListener = previewListener;
	}

}
