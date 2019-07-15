package com.nttdata.de.ityx.mediatrix.businessrules.impl.client;

import de.ityx.mediatrix.api.interfaces.businessrules.client.IClientForwardDialog;
import de.ityx.mediatrix.data.*;

import java.util.List;

public class ClientForwardDialog implements IClientForwardDialog {

	@Override
	public boolean onAddressBookBCCSelected(Question arg0, String arg1) {
		return true;
	}

	@Override
	public boolean onSearchAddressBookTO(Question question, String s) {
		return true;
	}

	@Override
	public boolean onSearchAddressBookCC(Question question, String s) {
		return true;
	}

	@Override
	public boolean onSearchAddressBookBCC(Question question, String s) {
		return true;
	}

	@Override
	public boolean onSearchCustomerTO(Question question, String s) {
		return true;
	}

	@Override
	public boolean onSearchCustomerCC(Question question, String s) {
		return true;
	}

	@Override
	public boolean onSearchCustomerBCC(Question question, String s) {
		return true;
	}

	@Override
	public boolean onAddressBookCCSelected(Question arg0, String arg1) {
		return true;
	}

	@Override
	public boolean onAddressBookToSelected(Question arg0, String arg1) {
		return true;
	}

	@Override
	public boolean onAttachmentSelected(Question arg0, Attachment arg1) {
		return true;
	}

	@Override
	public boolean onCustomerBCCSelected(Question arg0, String arg1) {
		return true;
	}

	@Override
	public boolean onCustomerCCSelected(Question arg0, String arg1) {
		return true;
	}

	@Override
	public boolean onCustomerHistorySelected(Question arg0, Customer arg1, List<Case> arg2, List<Email> arg3) {
		return true;
	}

	@Override
	public boolean onCustomerToSelected(Question arg0, String arg1) {
		return true;
	}

	@Override
	public boolean onTypeChangedCancel(Question arg0, int arg1) {
		return true;
	}

	@Override
	public void postAddAtachment(Question arg0, Attachment arg1) {
	}

	@Override
	public void postAddCustomerHistory(Question arg0, Customer arg1) {
	}

	@Override
	public void postAddTextObject(Question arg0, TextObject arg1) {
	}

	@Override
	public void postCancel(Question arg0) {
	}

	@Override
	public void postCiteQuestion(Question arg0, Question arg1) {
	}

	@Override
	public boolean preAddAtachment(Question arg0) {
		return true;
	}

	@Override
	public boolean preAddCustomerHistory(Question arg0, Customer arg1) {
		return true;
	}

	@Override
	public boolean preAddTextObject(Question arg0) {
		return true;
	}

	@Override
	public boolean preCancel(Question arg0) {
		return true;
	}

	@Override
	public boolean preCiteQuestion(Question arg0, StringBuilder arg1, List<Attachment> arg2) {
		return true;
	}

	@Override
	public boolean preSearchAddressBookBCC(Question arg0) {
		return true;
	}

	@Override
	public boolean preSearchAddressBookCC(Question arg0) {
		return true;
	}

	@Override
	public boolean preSearchAddressBookTo(Question arg0) {
		return true;
	}

	@Override
	public boolean preSearchCustomerBCC(Question arg0) {
		return true;
	}

	@Override
	public boolean preSearchCustomerCC(Question arg0) {
		return true;
	}

	@Override
	public boolean preSearchCustomerTo(Question arg0) {
		return true;
	}

}
