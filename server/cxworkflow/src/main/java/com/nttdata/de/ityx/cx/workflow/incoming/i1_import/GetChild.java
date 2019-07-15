package com.nttdata.de.ityx.cx.workflow.incoming.i1_import;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import de.ityx.contex.impl.designer.state.KeyConfiguration;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.impl.document.CDocumentFactory;
import de.ityx.contex.impl.document.text.string.StringDocument;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.document.CDocument;

import java.io.InputStream;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class GetChild extends AbstractWflBean {

	@Override
	public void execute(IFlowObject flowObject) throws Exception {
		String error = "0";
		String name = null;
		try {
			final ZipFile zipFile = (ZipFile) flowObject.get("zipFile");
			final List<String> names = (List<String>) flowObject.get("entryNames");
			final Integer count = (Integer) flowObject.get("count");
			name = names.get(count);
			SkyLogger.getWflLogger().debug("i1_getting:name" + name);
			final ZipEntry entry = zipFile.getEntry(name);
			SkyLogger.getWflLogger().debug("i1_extracting:name" + name);
			final InputStream inputStream = zipFile.getInputStream(entry);
			SkyLogger.getWflLogger().info("i1_containerbuild:name:"+name);

			CDocument img = CDocumentFactory.createDocument(inputStream, "image/tiff", new URI(name));
			img.setNote("Channel", "BRIEF");
			img.setNote("zipFile", zipFile.getName());
			img.setNote("fileName", name);

			Date incommingdate = new java.util.Date();
			Date createdate = incommingdate;

			try {
				createdate = new Date(entry.getTime());
			} catch (Exception e) {
				SkyLogger.getWflLogger().warn("Problem parsing date:" + name + zipFile.getName() + ":" + entry.getTime() + e.getMessage());
			}
			CDocumentContainer<CDocument> cdocc = new CDocumentContainer<>(img);
			DocContainerUtils.setIncommingDate(cdocc, img, incommingdate, createdate);

			cdocc.setNote("Channel", "BRIEF");
			cdocc.setNote("zipFile", zipFile.getName());
			cdocc.setNote("fileName", name);

			flowObject.put(DocContainerUtils.DOC, cdocc);
			//

		} catch (Exception e) {
			error="i1_GetChild: Error while extracting " + name + ": " + e.getMessage();
			//System.err.println(error);
			SkyLogger.getWflLogger().error(error, e);
			CDocumentContainer < CDocument > cont = null;
			CDocument doc =null;

			Object src=flowObject.get("src");
			if (src==null || src instanceof String){
				doc=StringDocument.getInstance(error);
				cont = new CDocumentContainer(doc);
				flowObject.put("src", cont);
				cont.setNote("Error", error);
			}else {
				cont = (CDocumentContainer<CDocument>) src;
				doc = DocContainerUtils.getDoc(cont);
			}
			doc.setNote("Error", name + ": " + e.getMessage());
			cont.setNote("Error", name + ": " + e.getMessage());
			error = name + ": " + e.getMessage();
		}
		flowObject.put("Error", error);

	}

	@Override
	public KeyConfiguration[] getKeys() {
		return new KeyConfiguration[]{new KeyConfiguration(DocContainerUtils.DOC, CDocumentContainer.class)};
	}
}
