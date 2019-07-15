package de.ityx.sky.mx.clientevents.ws;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

@WebService(targetNamespace = "http://ws.sky.ityx.de")
@SOAPBinding(style = Style.DOCUMENT, parameterStyle = ParameterStyle.WRAPPED, use = Use.LITERAL)
public interface ClientEventService {

    @WebMethod(operationName = "pushClient")
    public MediatrixResponse pushClient(MediatrixRequest request);

}
