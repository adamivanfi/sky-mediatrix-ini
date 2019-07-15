package com.nttdata.de.sky.ityx.mediatrix.businessrules.server.sky.actions;

import com.nttdata.de.lib.exception.ExtendedMXException;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.Actions;
import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.data.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by meinusch on 13.04.15.
 */
public class CustomerLoadAction extends AServerEventAction {

	private static final String GETCUSTOMER_BYMAIL = "select id from kunde where email=? and rownum<=1";
	private static final String GETCUSTOMER_BYEXTID = "select id from kunde where externeid=? and rownum<=1";

	@Override
	public String[] getActionNames() {
		return new String[]{Actions.ACTION_LOAD_CUSTOMER.name(), Actions.ACTION_LOAD_CUSTOMER_BYEMAIL.name(), Actions.ACTION_GETPROJECTINFO.name()};
	}

	@Override
	public List actionPerformed(Connection con, String actionname, List parameters) throws Exception {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + " ";
		SkyLogger.getMediatrixLogger().debug(logPrefix + " Starting : " + actionname);

		Actions t = null;
		try {
			t = Actions.valueOf(actionname);
		} catch (IllegalArgumentException e) {
			throw new ExtendedMXException(e.getMessage(), e);
		}
		switch (t) {
			case ACTION_GETPROJECTINFO:
				// not used currently
				SkyLogger.getMediatrixLogger().info(logPrefix + " getProjectInfo");
				parameters.clear();
				parameters.add(Boolean.TRUE);
				return parameters;
			case ACTION_LOAD_CUSTOMER:
				String customerIdS = (String) parameters.get(0);
				parameters.clear();

				try {
					Customer customer = API.getServerAPI().getCustomerAPI().loadExtID(con, customerIdS);
					parameters.add(customer);
				} catch (Exception e) {
					SkyLogger.getMediatrixLogger().error(logPrefix + " " + e.getMessage(), e);
				}
			case ACTION_LOAD_CUSTOMER_BYEMAIL:
				logPrefix = logPrefix + " " + Actions.ACTION_LOAD_CUSTOMER_BYEMAIL.name() + ":S:";
				SkyLogger.getMediatrixLogger().debug(logPrefix + ":START:");

				if (SkyLogger.getMediatrixLogger().isDebugEnabled()) {
					String paramout = "";

					int i = 0;
					for (Object par : parameters) {
						if (par != null) {
							paramout += i + ":" + par.toString() + "/";
						}
						i++;
					}
					SkyLogger.getMediatrixLogger().debug(logPrefix + ":int1:" + paramout);
				}

				// ToDo parameters auslesen und berücksichtigen
				int customerid = 0;
				// classcast customer?
				String email = null;
				String extId = null;
				boolean returncustomer=false;
				List<Customer> outCustomer=new LinkedList<>();

				if (parameters.get(0) instanceof Customer) {
					returncustomer=true;
					Customer cust = (Customer) parameters.get(0);

					if (cust.getId() > 0) {
						customerid = cust.getId();
					}
					if (cust.getEmail() != null && !cust.getEmail().isEmpty()) {
						email = cust.getEmail();
					}

					if (cust.getExternalId() != null && !cust.getExternalId().isEmpty()) {
						extId = cust.getExternalId();
					}
				} else if (parameters.size()>=5 && parameters.get(4) instanceof Customer) {
					Customer cust = (Customer) parameters.get(0);

					if (cust.getId() > 0) {
						customerid = cust.getId();
					}
					if (cust.getEmail() != null && !cust.getEmail().isEmpty()) {
						email = cust.getEmail();
					}

					if (cust.getExternalId() != null && !cust.getExternalId().isEmpty()) {
						extId = cust.getExternalId();
					}

				} else if (parameters.get(0) instanceof Integer) {

					Integer id = (Integer) parameters.get(0);
					String oper = (String) parameters.get(1);
					Integer operatorId = (Integer) parameters.get(2);
					Integer integer = (Integer) parameters.get(3);
					email = (String) parameters.get(4);

					SkyLogger.getMediatrixLogger().debug(logPrefix + ":post:id:" + id + " oper:" + oper + " operid:" + operatorId + " integer:" + integer + " email:" + email + " custid:" + customerid);

				}

				parameters.clear();
				SkyLogger.getMediatrixLogger().debug(logPrefix + ":int2:" + parameters + " email:" + email);

				PreparedStatement idStmt = null;
				ResultSet rs = null;


				if (customerid == 0 && extId != null) {
					extId = extId.trim().toLowerCase();
					if (!extId.isEmpty() && extId.length() > 1 && !extId.contains("0")) {
						try {
							idStmt = con.prepareStatement(GETCUSTOMER_BYEXTID);
							idStmt.setString(1, extId);
							SkyLogger.getMediatrixLogger().debug(logPrefix + ":preexec1:" + parameters + " extId:" + extId);

							rs = idStmt.executeQuery();
							if (rs.next()) {
								customerid = rs.getInt(1);
							}

							SkyLogger.getMediatrixLogger().debug(logPrefix + ":postexec1:" + parameters + " customerid:" + customerid);

						} catch (SQLException e) {
							SkyLogger.getMediatrixLogger().warn(logPrefix + ":err1:" + parameters + " customerid:" + customerid + " extId:" + extId);
							return null;

						} finally {
							try {
								if (rs != null) {
									rs.close();
								}
							} catch (SQLException e) {
								SkyLogger.getMediatrixLogger().warn(logPrefix + "Problem closing rs for Getting Customer by ExtID:" + email, e);
							}
						}
					}
				}

				if (customerid == 0 && email != null) {
					email = email.trim().toLowerCase();
					if (!email.isEmpty() && email.length() > 5 && !email.contains("sky.de")) {
						try {
							idStmt = con.prepareStatement(GETCUSTOMER_BYMAIL);
							idStmt.setString(1, email);
							SkyLogger.getMediatrixLogger().debug(logPrefix + ":preexec:" + parameters + " email:" + email);

							rs = idStmt.executeQuery();
							if (rs.next()) {
								customerid = rs.getInt(1);
							}

							SkyLogger.getMediatrixLogger().debug(logPrefix + ":postexec:" + parameters + " customerid:" + customerid + " email:" + email);

						} catch (SQLException e) {
							SkyLogger.getMediatrixLogger().error(logPrefix + ":err1:" + parameters + " customerid:" + customerid + " email:" + email + " e:" + e.getMessage(), e);
							return null;

						} finally {
							try {
								if (rs != null) {
									rs.close();
								}
							} catch (SQLException e) {
								SkyLogger.getMediatrixLogger().error(logPrefix + "Problem closing rs for Getting Customer by Mail:" + email, e);
							}
						}

					} else {
						SkyLogger.getMediatrixLogger().debug(logPrefix + ":emptyOrSkyMail:" + parameters + " customerid:" + customerid);
					}
				} else {
					SkyLogger.getMediatrixLogger().debug(logPrefix + ":noapi3:" + parameters + " customerid:" + customerid);
				}

				if (customerid > 0) {
					SkyLogger.getMediatrixLogger().debug(logPrefix + ":preApi:" + parameters + " customerid:" + customerid);
					outCustomer = API.getServerAPI().getCustomerAPI().getCustomerById(con, customerid, customerid, 0);
					if (outCustomer!=null){
						parameters.addAll(outCustomer);
					}
					SkyLogger.getMediatrixLogger().debug(logPrefix + ":postApi:" + parameters + " psize:" + parameters.size());
				} else if (email!=null && !email.isEmpty() && email.length() > 1) {
					outCustomer = API.getServerAPI().getCustomerAPI().getCustomerById(con, 10, 10, 0);
					SkyLogger.getMediatrixLogger().debug(logPrefix + ":defaultApi:" + parameters + " psize:" + parameters.size());
					if (outCustomer!=null) {
						parameters.addAll(outCustomer);
					}

					SkyLogger.getMediatrixLogger().debug(logPrefix + ":defaultApi:" + parameters + " psize:" + parameters.size());
				} else {
					SkyLogger.getMediatrixLogger().debug(logPrefix + ":noapi1:" + parameters + " customerid:" + customerid);
					//parameters.add(null); // ggf. empty customer
				}

		}
		SkyLogger.getMediatrixLogger().debug(logPrefix + ": exit");

		return parameters;
	}

}
