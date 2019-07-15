package de.ityx.sky.outbound.client.base;

import de.ityx.mediatrix.api.interfaces.businessrules.client.IClientLogin;

public abstract class ClientBaseLogin implements IClientLogin {

    public boolean preLogin(String operator) {
        return true;
    }

    public int preLogout(String operator) {
        return 0;
    }

    public void postLogout(String operator) {
    }

}
