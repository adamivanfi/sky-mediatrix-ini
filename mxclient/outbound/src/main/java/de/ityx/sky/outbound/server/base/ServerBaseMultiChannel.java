package de.ityx.sky.outbound.server.base;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import de.ityx.mediatrix.api.interfaces.businessrules.server.IServerMultiChannel;
import de.ityx.mediatrix.data.Account;
import de.ityx.mediatrix.data.Customer;
import de.ityx.mediatrix.data.Email;
import de.ityx.mediatrix.data.Project;

public abstract class ServerBaseMultiChannel implements IServerMultiChannel {

    @Override
    public HashMap prepareForFilter(Connection con, Email email, Account account, Project project) throws SQLException {
        return null;
    }

    @Override
    public Customer findCustomerForChannel(Connection con, Customer tempCustomer, Email email, Project project) throws SQLException {
        return null;
    }

    @Override
    public void assignLanguage(Connection con, Email newQuestion) throws SQLException {
    }

}
