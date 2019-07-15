package de.ityx.sky.outbound.client;

import java.util.ArrayList;
import java.util.List;

import de.ityx.mediatrix.api.interfaces.businessrules.client.IClientServiceCenter;
import de.ityx.mediatrix.data.ServiceCenterAttribute;

/**
 *
 * @was ClientServiceCenter.java
 */
public class ClientServiceCenter implements IClientServiceCenter {

    @Override
    public List<ServiceCenterAttribute> getAttributes() {
        List<ServiceCenterAttribute> list = new ArrayList<ServiceCenterAttribute>();

        ServiceCenterAttribute archivedir = new ServiceCenterAttribute();
        archivedir.setAttribute("sky.template.path");
        archivedir.setValue("");
        archivedir.setType(ServiceCenterAttribute.TYPE_STRING);
        list.add(archivedir);

        return list;
    }
}