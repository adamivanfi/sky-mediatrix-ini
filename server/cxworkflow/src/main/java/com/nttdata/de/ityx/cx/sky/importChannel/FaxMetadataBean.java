package com.nttdata.de.ityx.cx.sky.importChannel;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.document.CDocument;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class FaxMetadataBean extends AbstractWflBean {

	public static final String KEY_VALIDFILE = "ValidFile";
	public static final String KEY_FAX_RCVTIME = "Fax.RCVTIME";
	public static final String KEY_FAX_ORIGIN = "Fax.ORIGIN";
	/**
	 *
	 */
	private static final long serialVersionUID = 962521877474182958L;

	private String getIncLineValue(String line) {
		if (line == null)
			return "";

		return line.substring(line.indexOf("=") + 1, line.length()).trim();
	}

	private void checkIncLine(String line, IFlowObject flowObject) {
		if (line.startsWith("RCVTIME")) {
			flowObject.put(KEY_FAX_RCVTIME, getIncLineValue(line));
		} else if (line.startsWith("ORIGIN")) {
			flowObject.put(KEY_FAX_ORIGIN, getIncLineValue(line));
		}
	}

	private boolean checkIncFile(File incFile, IFlowObject flowObject) {

		if (!incFile.exists()) {
			return false;
		}

		BufferedReader in;
		try {
			in = new BufferedReader(new FileReader(incFile));
			String line = in.readLine();
			while (line != null) {
				checkIncLine(line, flowObject);
				line = in.readLine();
			}

		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		return false;
	}

	public String getImportBase(IFlowObject flowObject) {
		return (String) flowObject.get("importFax");
	}
	
	public void execute(IFlowObject flowObject) {
		String uri = (String) flowObject.get("doc.uri");
		String importBase = getImportBase(flowObject);

		try {
			File file = new File(uri);

			String incFile = file.getName().substring(0, file.getName().lastIndexOf(".")) + ".inc";
			if (file.getName().contains("_")) {
				incFile = file.getName().substring(0, file.getName().lastIndexOf("_")) + ".inc";
			}
			
			File incfile = new File(importBase + File.separator + incFile);

			if (incfile.exists()) {
				checkIncFile(incfile, flowObject);
				flowObject.put(KEY_VALIDFILE, Boolean.TRUE);
			} else {
				flowObject.put(KEY_VALIDFILE, Boolean.FALSE);
			}
		} catch (Exception e) {
			SkyLogger.getItyxLogger().warn("No metadata could read for FAX: " + uri + " in folder " + importBase + " msg:" + e.getMessage(), e);
		}

		CDocumentContainer con = (CDocumentContainer) flowObject.get("doc");
		if (con != null) {
			CDocument cdoc = con.getDocument(0);
			String fax = (String) flowObject.get(KEY_FAX_ORIGIN);
			String rcvtime = (String) flowObject.get(KEY_FAX_RCVTIME);
			cdoc.setNote("Channel", "FAX");
			if (fax != null) {
				cdoc.setNote("ReceivingFax", fax);
			}
			
			Date incommingdate = new java.util.Date();
			Date createdate = incommingdate;
			if (rcvtime != null) {
				try {
					createdate = (new SimpleDateFormat("yyyy.MM.dd HH:mm:ss")).parse(rcvtime);
				} catch (Exception e) {
					e.printStackTrace();
					SkyLogger.getItyxLogger().error("Problem parsing date:" + cdoc.getNote("DOCUMENTKEY") + ":" + rcvtime);
				}
			}
			con.setNote("DOCUMENTSOURCE", cdoc.getUri());
			con.setNote("DOCUMENTKEY", cdoc.getTitle());
			Map<String, Object> map = DocContainerUtils.setIncommingDate(con, cdoc, incommingdate, createdate);
			flowObject.put("map", map);
		}
	}
}