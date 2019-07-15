package de.ityx.sky.outbound.client;

import com.nttdata.de.sky.archive.ClientUtils;
import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.data.Question;
import de.ityx.mediatrix.data.Subproject;
import de.ityx.sky.outbound.client.base.ClientBaseQuestionView;

public class ClientQuestionView extends ClientBaseQuestionView {

    @Override
    public boolean preQuestionForward(String arg0, boolean arg1, Question arg2, Subproject arg3, String arg4, int arg5) {

        arg3 = arg3 != null ? API.getClientAPI().getSubprojectAPI().load(arg3.getId()) : new Subproject();
        if (!arg3.getExternalEmail().trim().equals("")) {
            ClientUtils.addAttachments(arg2, arg2.getAttachments());
        }

        return true;
    }

}
