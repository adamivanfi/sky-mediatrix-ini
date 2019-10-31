/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

package com.nttdata.de.sky.connector.faamockup;

import customer.de.sky.faa.schemas.*;
import services.efbus.customerservicefaa.*;

import java.math.BigInteger;

/**
 * This class was generated by Apache CXF 2.5.1 2013-11-25T16:48:53.739+01:00 Generated source version: 2.5.1
 */

@javax.jws.WebService(serviceName = "CustomerServiceFAA",
        portName = "CustomerServiceFAA",
        targetNamespace = "http://efbus.services/CustomerServiceFAA",
        wsdlLocation = "CustomerServiceFAA_v4.0.wsdl",
        endpointInterface = "services.efbus.customerservicefaa.PortType")
public class MockFaaStaticPortTypeImpl implements services.efbus.customerservicefaa.PortType {

    /*
     * (non-Javadoc)
     *
     * @see services.efbus.customerservicefaa.PortType#getCustomerData(customer.de.sky.faa.schemas.CustomerDataRequestType customerDataRequest )*
     */
    public CustomerDataResponseType getCustomerData(CustomerDataRequestType req) throws MsgFault {
        CustomerDataResponseType _return = null;

        MultipleContactListType list = new MultipleContactListType();
        list.setContactInterval3D(new BigInteger("3"));
        list.setContactInterval7D(new BigInteger("7"));
        list.setContactInterval14D(new BigInteger("14"));
        list.setContactInterval21D(new BigInteger("21"));
        list.setContactInterval28D(new BigInteger("28"));

        _return = new CustomerDataResponseType();
        _return.setMultipleContactList(list);
        _return.setRateCardFlag(FlagType.N);
        _return.setRetCode("0");
        _return.setErrorDesc("Success");

        return _return;
    }

    @Override
    public CustomerDiagnosticDataResponseType getCustomerDiagnosticData(CustomerDiagnosticDataRequestType customerDiagnosticDataRequest) throws MsgFault {
        return new CustomerDiagnosticDataResponseType();
    }

}