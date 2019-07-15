package de.ityx.sky.mx.clientevents.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.Map;

@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class MediatrixRequest implements Serializable{

    private String master;
    public String getMaster() {
        return master;
    }
    public void setMaster(String master) {
        this.master = master;
    }
    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }
    public Map<String, String> getEntries() {
        return entries;
    }
    public void setEntries(Map<String, String> entries) {
        this.entries = entries;
    }
    private String user;
    private Map<String, String> entries;
}
