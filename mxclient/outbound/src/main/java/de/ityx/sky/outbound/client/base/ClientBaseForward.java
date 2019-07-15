package de.ityx.sky.outbound.client.base;

import java.util.List;

import de.ityx.mediatrix.api.interfaces.businessrules.client.IClientForwardDialog;
import de.ityx.mediatrix.data.Attachment;
import de.ityx.mediatrix.data.Case;
import de.ityx.mediatrix.data.Customer;
import de.ityx.mediatrix.data.Email;
import de.ityx.mediatrix.data.Question;
import de.ityx.mediatrix.data.TextObject;

public class ClientBaseForward implements IClientForwardDialog {

    @Override
    public boolean onAddressBookBCCSelected(Question arg0, String arg1) {
        // TODO Auto-generated method stub
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
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean onAddressBookToSelected(Question arg0, String arg1) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean onAttachmentSelected(Question arg0, Attachment arg1) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean onCustomerBCCSelected(Question arg0, String arg1) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean onCustomerCCSelected(Question arg0, String arg1) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean onCustomerHistorySelected(Question arg0, Customer arg1, List<Case> arg2, List<Email> arg3) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean onCustomerToSelected(Question arg0, String arg1) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean onTypeChangedCancel(Question arg0, int arg1) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public void postAddAtachment(Question arg0, Attachment arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void postAddCustomerHistory(Question arg0, Customer arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void postAddTextObject(Question arg0, TextObject arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void postCancel(Question arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void postCiteQuestion(Question arg0, Question arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean preAddAtachment(Question arg0) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean preAddCustomerHistory(Question arg0, Customer arg1) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean preAddTextObject(Question arg0) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean preCancel(Question arg0) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean preCiteQuestion(Question arg0, StringBuilder arg1, List<Attachment> arg2) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean preSearchAddressBookBCC(Question arg0) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean preSearchAddressBookCC(Question arg0) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean preSearchAddressBookTo(Question arg0) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean preSearchCustomerBCC(Question arg0) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean preSearchCustomerCC(Question arg0) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean preSearchCustomerTo(Question arg0) {
        // TODO Auto-generated method stub
        return true;
    }

}
