package com.nttdata.de.ityx.cx.workflow.incoming.i3_classify;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflReportedBean;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import de.ityx.contex.interfaces.designer.IFlowObject;

/**
 * Created by meinusch on 30.11.15.
 */
public class PreClassifierCancellationReason  extends AbstractWflReportedBean {

	public final static String TMP_Formtype="tmp_formtype";
	public final static String TMP_CATS="tmp_cats";
	public final static String TMP_CATARRAY="tmp_cataray";


	@Override
	public void execute(IFlowObject flowObject) throws Exception {

		String formtype=DocContainerUtils.getFormtype(flowObject);
		if (formtype!=null) {
			flowObject.set(TMP_Formtype, formtype);
		}
		Object cats=flowObject.get("cats");
		if (cats!=null) {
			flowObject.set(TMP_CATS, cats);
		}
		Object catA=flowObject.get("catarray");
		if (catA!=null) {
			flowObject.set(TMP_CATARRAY, catA);
		}

	}
}
