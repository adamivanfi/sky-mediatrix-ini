package com.nttdata.de.ityx.cx.workflow.incoming.i1_import;

import com.nttdata.de.ityx.cx.workflow.incoming.SBS_DocpoolWrite;
import de.ityx.contex.interfaces.designer.IFlowObject;

/**
 * Created by DHIFLM on 03.03.2016.
 */
public class SBS_WriteEmailToClassification extends SBS_DocpoolWrite {

    public static final String SBS_CLASSIFICATION = "SBS_Classification";

    @Override
    public String getParameter(IFlowObject flow) {
        return SBS_CLASSIFICATION;
    }
}
