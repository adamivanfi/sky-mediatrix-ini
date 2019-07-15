package de.ityx.sky.outbound.server.base;

import java.sql.Connection;
import java.util.HashMap;

import de.ityx.mediatrix.api.interfaces.businessrules.server.IServerMonitor;
import de.ityx.mediatrix.data.Email;
import de.ityx.mediatrix.data.Operator;
import de.ityx.mediatrix.data.Subproject;

public abstract class ServerBaseMonitor implements IServerMonitor {

    @Override
    public HashMap preMonitorEnter(Connection con, Email email, Operator operator, Subproject subproject) {
        return null;
    }

    @Override
    public HashMap postMonitorEnter(Connection con, Email email, Operator operator, Subproject subproject) {
        return null;
    }

    @Override
    public HashMap postMonitorSend(Connection con, Email email, Operator operator, Subproject subproject) {
        return null;
    }

}
