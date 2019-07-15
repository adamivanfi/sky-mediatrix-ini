package com.nttdata.de.ityx.cx.workflow.incoming.i1_import;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.impl.document.text.string.StringDocument;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.document.CDocument;

import java.io.File;
import java.util.zip.ZipFile;

public class Cleanup extends AbstractWflBean {

	protected String	importDir	= "importScanPB";
	protected String	destDir		= "destScan";

	@Override
	public void execute(IFlowObject flowObject) throws Exception {
		String error = "0";

		String[] fileTypes = new String[] { "csv", "zip", };
		ZipFile zip = (ZipFile) flowObject.get("zipFile");
		String importScan = (String) flowObject.get(importDir);
		String destScan = (String) flowObject.get(destDir);
		String dataName = (String) flowObject.get("dataName");
		try {
			zip.close();
			for (String type : fileTypes) {
				final String fileName = dataName + "." + type;
				String srcFileS=importScan + File.separator + fileName;
				String dstFileS=destScan + File.separator + fileName;
				final File srcFile = new File(srcFileS);
				final File dstFile = new File(dstFileS);

				SkyLogger.getWflLogger().debug("i1_cleanup:s:"+srcFileS+" d:"+dstFileS);

				if (!srcFile.exists() && !type.equals("csv")) {
					throw new Exception(type + "-file does not exist.");
				}else {
					boolean moved = srcFile.renameTo(dstFile);
					if (!moved) {
						throw new Exception(type + "-file:>"+srcFileS+"< could not be moved to:>"+dstFileS+"<");
					}
				}
			}
		} catch (Exception e) {
			error=dataName + ": " + e.getMessage();

			SkyLogger.getWflLogger().error(error, e);
			CDocumentContainer < CDocument > cont = null;
			CDocument doc =null;

			Object src=flowObject.get("src");
			if (src==null || src instanceof String){
				doc= StringDocument.getInstance(error);
				cont = new CDocumentContainer(doc);
				flowObject.put("src", cont);
				cont.setNote("Error", error);
			}else {
				cont = (CDocumentContainer<CDocument>) src;
				doc = DocContainerUtils.getDoc(cont);
			}
			doc.setNote("Error", error);
			cont.setNote("Error", error);
			System.err.println(error);

		}
		flowObject.put("Error", error);

	}

}
