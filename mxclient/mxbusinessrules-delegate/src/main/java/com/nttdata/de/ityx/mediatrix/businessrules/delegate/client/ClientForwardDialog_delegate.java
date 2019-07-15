package com.nttdata.de.ityx.mediatrix.businessrules.delegate.client;

import de.ityx.mediatrix.api.interfaces.businessrules.client.IClientForwardDialog;
import de.ityx.mediatrix.data.*;

import java.util.List;

public class ClientForwardDialog_delegate implements IClientForwardDialog {

	private IClientForwardDialog delegate = com.nttdata.de.ityx.mediatrix.businessrules.factories.NTTDataBusinessruleFactoryGenerator.createFactory(false).getClientForwardDialog();

	@Override
	public boolean onAddressBookBCCSelected(Question arg0, String arg1) {
		return delegate.onAddressBookBCCSelected(arg0, arg1);
	}

	@Override
	public boolean onSearchAddressBookTO(Question question, String s) {
		return delegate.onSearchAddressBookTO(question, s);
	}

	@Override
	public boolean onSearchAddressBookCC(Question question, String s) {
		return delegate.onSearchAddressBookCC(question, s);
	}

	@Override
	public boolean onSearchAddressBookBCC(Question question, String s) {
		return delegate.onSearchAddressBookBCC(question, s);
	}

	@Override
	public boolean onSearchCustomerTO(Question question, String s) {
		return delegate.onSearchCustomerTO(question, s);
	}

	@Override
	public boolean onSearchCustomerCC(Question question, String s) {
		return delegate.onSearchCustomerCC(question, s);
	}

	@Override
	public boolean onSearchCustomerBCC(Question question, String s) {
		return delegate.onSearchCustomerBCC(question, s);
	}

	@Override
	public boolean onAddressBookCCSelected(Question arg0, String arg1) {
		return delegate.onAddressBookCCSelected(arg0, arg1);
	}

	@Override
	public boolean onAddressBookToSelected(Question arg0, String arg1) {
		return delegate.onAddressBookToSelected(arg0, arg1);
	}

	@Override
	public boolean onAttachmentSelected(Question arg0, Attachment arg1) {
		return delegate.onAttachmentSelected(arg0, arg1);
	}

	@Override
	public boolean onCustomerBCCSelected(Question arg0, String arg1) {
		return delegate.onCustomerBCCSelected(arg0, arg1);
	}

	@Override
	public boolean onCustomerCCSelected(Question arg0, String arg1) {
		return delegate.onCustomerCCSelected(arg0, arg1);
	}

	@Override
	public boolean onCustomerHistorySelected(Question arg0, Customer arg1, List<Case> arg2, List<Email> arg3) {
		return delegate.onCustomerHistorySelected(arg0, arg1, arg2, arg3);
	}

	@Override
	public boolean onCustomerToSelected(Question arg0, String arg1) {
		return delegate.onCustomerToSelected(arg0, arg1);
	}

	@Override
	public boolean onTypeChangedCancel(Question arg0, int arg1) {
		return delegate.onTypeChangedCancel(arg0, arg1);
	}

	@Override
	public void postAddAtachment(Question arg0, Attachment arg1) {
		delegate.postAddAtachment(arg0, arg1);
	}

	@Override
	public void postAddCustomerHistory(Question arg0, Customer arg1) {
		delegate.postAddCustomerHistory(arg0, arg1);
	}

	@Override
	public void postAddTextObject(Question arg0, TextObject arg1) {
		delegate.postAddTextObject(arg0, arg1);
	}

	@Override
	public void postCancel(Question arg0) {
		delegate.postCancel(arg0);
	}

	@Override
	public void postCiteQuestion(Question arg0, Question arg1) {
		delegate.postCiteQuestion(arg0, arg1);
	}

	@Override
	public boolean preAddAtachment(Question arg0) {
		return delegate.preAddAtachment(arg0);
	}

	@Override
	public boolean preAddCustomerHistory(Question arg0, Customer arg1) {
		return delegate.preAddCustomerHistory(arg0, arg1);
	}

	@Override
	public boolean preAddTextObject(Question arg0) {
		return delegate.preAddTextObject(arg0);
	}

	@Override
	public boolean preCancel(Question arg0) {
		return delegate.preCancel(arg0);
	}

	@Override
	public boolean preCiteQuestion(Question arg0, StringBuilder arg1, List<Attachment> arg2) {
		return delegate.preCiteQuestion(arg0, arg1, arg2);
	}

	@Override
	public boolean preSearchAddressBookBCC(Question arg0) {
		return delegate.preSearchAddressBookBCC(arg0);
	}

	@Override
	public boolean preSearchAddressBookCC(Question arg0) {
		return delegate.preSearchAddressBookCC(arg0);
	}

	@Override
	public boolean preSearchAddressBookTo(Question arg0) {
		return delegate.preSearchAddressBookTo(arg0);
	}

	@Override
	public boolean preSearchCustomerBCC(Question arg0) {
		return delegate.preSearchCustomerBCC(arg0);
	}

	@Override
	public boolean preSearchCustomerCC(Question arg0) {
		return delegate.preSearchCustomerCC(arg0);
	}

	@Override
	public boolean preSearchCustomerTo(Question arg0) {
		return delegate.preSearchCustomerTo(arg0);
	}
}
