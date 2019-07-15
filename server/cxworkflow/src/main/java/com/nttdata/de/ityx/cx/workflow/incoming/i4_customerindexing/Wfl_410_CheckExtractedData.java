package com.nttdata.de.ityx.cx.workflow.incoming.i4_customerindexing;

import de.ityx.contex.impl.designer.state.StateResult;
import de.ityx.contex.interfaces.designer.IExflowState;
import de.ityx.contex.interfaces.designer.IFlowObject;

public class Wfl_410_CheckExtractedData extends CheckExtractedData {

    @Override
    public StateResult execute(int arg0, IFlowObject flowObject, IExflowState arg2) throws Exception {
        //SkyLogger.getWflLogger().debug("410 " + getDocID(flowObject) + " CheckStart");
        super.execute(arg0, flowObject, arg2);

        //SkyLogger.getWflLogger().debug("410 " + getDocID(flowObject) + " CheckFinished:" + flowObject.get("IndexedDocument"));

        return ((Boolean) flowObject.get("IndexedDocument")) ? StateResult.CONDITION_UNSATISFIED : StateResult.STATEOK;
    }

}
