package com.nttdata.de.ityx.cx.workflow.incoming.i1_import;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;
import com.nttdata.de.lib.logging.SkyLogger;
import de.ityx.contex.impl.designer.state.KeyConfiguration;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.impl.document.text.string.StringDocument;
import de.ityx.contex.interfaces.designer.IFlowObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class PrepareOpenZip extends AbstractWflBean {

	protected String	importDir	= "importScanPB";

	@Override
	public void execute(IFlowObject flowObject) throws Exception {


		String error = "0";
		String importFolder = (String) flowObject.get(importDir);
/*		
 		CDocumentContainer<CDocument> cont = (CDocumentContainer<CDocument>) flowObject
				.get(TagMatchDefinitions.DOC);
		CDocument doc = cont.getDocument(0);
		final String triggerName = doc.getUri();
*/
		final String triggerName = (String) flowObject.get("src");
		final String dataName = triggerName.substring(triggerName.lastIndexOf(File.separator),triggerName.lastIndexOf(".trigger"));
		final String fileName = dataName+ ".zip";
		try {
			List<String> entryNames = new ArrayList<>();
			final ZipFile zipFile = new ZipFile(importFolder+File.separator+fileName);
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				final ZipEntry entry = entries.nextElement();
				final String name = entry.getName();
				entryNames.add(name);
			}
			flowObject.put("zipFile", zipFile);
			flowObject.put("entryNames", entryNames);
			flowObject.put("entrySize", entryNames.size());
			flowObject.put("dataName", dataName);
		} catch (Exception e) {
			error = fileName + ": " + e.getMessage();
			SkyLogger.getWflLogger().error("Error while handling " + error);
			//CDocumentContainer doc = createSimplifiedTextDocument(flowObject, triggerName);
			CDocumentContainer out = new CDocumentContainer(StringDocument.getInstance(triggerName + ":" + error));
			flowObject.put("src", out);
			out.setNote("Error", error);
		}
		flowObject.put("Error", error);
	}

@Override
public KeyConfiguration[] getKeys(){
	return new KeyConfiguration[]{
			new KeyConfiguration("Error", String.class),
			new KeyConfiguration("zipFile", ZipFile.class),
			new KeyConfiguration("entryNames",String.class),
			new KeyConfiguration("entrySize",Integer.class),
			new KeyConfiguration("dataName",String.class),

	};
}
	
}
