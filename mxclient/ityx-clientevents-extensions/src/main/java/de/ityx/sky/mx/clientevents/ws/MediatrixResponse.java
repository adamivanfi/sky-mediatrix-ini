package de.ityx.sky.mx.clientevents.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.Map;

@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class MediatrixResponse {
		
	public static final String SUCCESS = "SUCCESS";
	public static final String ERROR = "ERROR";
	public static final String TIMEOUT = "TIMEOUT";
	public static final String MXC_NOT_ONLINE = "MX_NOT_ONLINE";
	public static final String MXC_NOT_READY = "MX_NOT_READY";
	
	public static final String KEY_STATUS="STATUS";

    private Map<String, String> entries;

    public Map<String, String> getEntries() {
        return entries;
    }

    public void setEntries(Map<String, String> entries) {
        this.entries = entries;
    }    
}
