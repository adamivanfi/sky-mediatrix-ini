package com.nttdata.de.ityx.cx.workflow.utils;

import de.ityx.contex.impl.designer.state.KeyConfiguration;
import de.ityx.contex.impl.designer.state.StateResult;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.impl.document.text.string.StringDocument;
import de.ityx.contex.interfaces.designer.IBeanState;
import de.ityx.contex.interfaces.designer.IExflowState;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.document.CDocument;

public class TestSessionBean implements IBeanState {
	
	private static final String SIZE = "size";

	private static final long serialVersionUID = -1678111008899618272L;
	
	private static final String DOC = "test";
    
	@Override
    public StateResult execute(int arg0, IFlowObject flowObject, IExflowState arg2) throws Exception {
		flowObject.put(DOC, new CDocumentContainer<CDocument>(StringDocument.getInstance("test")));
		flowObject.put(SIZE, 1);
    	return StateResult.STATEOK;
    }

    @Override
    public void abortExecute() {}

    @Override
    public void rollbackExecute() {}


	@Override
    public void cleanState() {
        //useless
    }

    @Override
    public void prepareForCluster(String arg0) {
		//useless
		}

    @Override
    public void prepareForResumeFromCluster() {
			//useless
			}

    @Override
    public KeyConfiguration[] getKeys() {
    	 return new KeyConfiguration[] {new KeyConfiguration(DOC,  CDocumentContainer.class), new KeyConfiguration(SIZE, Integer.class)};
    }


}
