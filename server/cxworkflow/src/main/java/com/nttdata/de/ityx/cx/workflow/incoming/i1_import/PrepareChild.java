package com.nttdata.de.ityx.cx.workflow.incoming.i1_import;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflReportedBean;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.ityx.sharedservices.utils.ShedulerUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import de.ityx.contex.impl.designer.state.KeyConfiguration;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.document.CDocument;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PrepareChild extends AbstractWflReportedBean {

	private final String parameter = "220_PreprocessingOCR";
	private final Pattern pbFormat = Pattern.compile("[\\d]{4}_([\\d]{8})_[\\d]{2,3}_[\\d]{5}_.?\\.tif");
	private final SimpleDateFormat sformater = new SimpleDateFormat("ddMMyyyy");
	
	@Override
	public void execute(IFlowObject flowObject) throws Exception {
		Object object = flowObject.get("doc");
		if (object != null && !object.getClass().equals(String.class)) {
			CDocumentContainer doc = (CDocumentContainer) object;
			CDocument cdoc = doc.getDocument(0);
			String fname= (String) cdoc.getNote("fileName");
			doc.setNote("DOCUMENTSOURCE", cdoc.getNote("zipFile"));
			doc.setNote("DOCUMENTKEY", fname);
			Date incommingdate = new java.util.Date();
			Date createdate = incommingdate;
			try{
				for (Matcher m = pbFormat.matcher(fname); m.find();) {
					String dateStr = m.group(1);
					createdate= sformater.parse(dateStr);
				}
			}catch (Exception e) {
				SkyLogger.getWflLogger().error("Error ParsingDate from ZIP-Child container:" + flowObject.get("zipFile") + ":" + flowObject.get("count")+" msg:"+e.getMessage(), e);
				// default value should be used
			}
			Map<String, Object> map = DocContainerUtils.setIncommingDate(doc, cdoc, incommingdate, createdate);
			flowObject.put("map", map);
			try {
				ShedulerUtils.storeDocAndScheduleProcess(ShedulerUtils.getDefaultUnit(), ShedulerUtils.getDefaultMaster(), getParameter(), doc, cdoc);
			}catch (Exception e) {
				SkyLogger.getWflLogger().error("Error Processing ZIP-Child container:" + flowObject.get("zipFile") + ":" + flowObject.get("count")+" msg:"+e.getMessage(), e);
				throw e;
			}
		}else{
			SkyLogger.getWflLogger().warn("Unexpected doctype during processing ZIP-Child container:"+flowObject.get("zipFile")+":"+flowObject.get("count"));
		}
	}

	@Override
	public KeyConfiguration[] getKeys() {
		return new KeyConfiguration[]{new KeyConfiguration(DocContainerUtils.DOC, CDocumentContainer.class)};
	}

	public String getParameter() {
		return parameter;
	}


}
