package de.ityx.sky.outbound.client;

import com.nttdata.de.sky.archive.ClientUtils;
import de.ityx.mediatrix.data.Attachment;
import de.ityx.mediatrix.data.Question;
import de.ityx.sky.outbound.client.base.ClientBaseForward;

import java.util.List;

public class ClientForward extends ClientBaseForward {

    @Override
    public boolean preCiteQuestion(Question arg0, StringBuilder arg1, List<Attachment> arg2) {
        ClientUtils.addAttachments(arg0, arg2);
        return true;
    }

}
