package com.nttdata.de.ityx.cx.workflow.incoming.i6_index;

import com.nttdata.de.ityx.cx.workflow.incoming.SBS_DocpoolWrite;
import de.ityx.contex.interfaces.designer.IFlowObject;

/**
 * Created by DHIFLM on 03.03.2016.
 */
public class WriteToSBSInjection extends SBS_DocpoolWrite {

    public static final String SBS_INJECTION = "SBS_600_MXInjection";

    @Override
    public String getParameter(IFlowObject flow) {
        return SBS_INJECTION;
    }
}
