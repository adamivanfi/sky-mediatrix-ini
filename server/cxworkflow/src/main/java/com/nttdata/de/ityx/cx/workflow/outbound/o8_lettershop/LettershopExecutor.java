package com.nttdata.de.ityx.cx.workflow.outbound.o8_lettershop;

import com.nttdata.de.ityx.cx.workflow.outbound.OutboundPooler;
import com.nttdata.de.ityx.sharedservices.configuration.BeanConfig;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.contex.interfaces.designer.IExflowState;
import de.ityx.contex.interfaces.designer.IFlowObject;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Map;

/**
 * Created by meinusch on 16.04.15.
 */
public class LettershopExecutor extends OutboundPooler {

	private static final Long serialVersionUID=7855448339962583772L;

	@Override
	public String getProcessname() {
		return "820_Outbound";
	}
	@Override
	public String getMaster() { return "sky"; }

	public String getTargetPath() throws Exception {
		return BeanConfig.getReqString("MoveFileToLettershop_DstDir");
	}

	@Override
	public void itemProcessor(IFlowObject flowObject, IExflowState exflowState, Map<String, Object> docMeta, String docid, long logid) throws Exception {
		String documentID = (String) docMeta.get(TagMatchDefinitions.DOCUMENT_ID);
		String projectName = (String) docMeta.get(TagMatchDefinitions.MX_MASTER);
		transferDocumentToArchive(documentID);
	}

	public static final String FLOW_SRC_DIR = "MoveFileToLettershop_SrcDir";

	public void transferDocumentToArchive(String documentID) throws Exception {
		File srcDir = new File(BeanConfig.getReqString(FLOW_SRC_DIR));

		if (!(srcDir.exists() && srcDir.isDirectory())) {
			SkyLogger.getConnectorLogger().error("IF8.1: "+documentID+" Parameter srcDir is not configured properly");
			throw new Exception("IF8.1: "+documentID+" Parameter srcDir is not configured properly");
		}

		synchronized (serialVersionUID) {
			File lockFile = new File(srcDir + File.separator + documentID + ".lck");
			if (!lockFile.exists()) {
				FileUtils.touch(lockFile);
			} else {
				SkyLogger.getConnectorLogger().error("IF8.1: "+documentID+" Copy to Lettershop: File transfer has already been initiated. (lck-FileExists)");
				throw new Exception("IF8.1: "+documentID+" Copy to Lettershop: File transfer has already been initiated for id: " + documentID);
			}
		}
		File dataFile = new File(srcDir + File.separator + documentID + ".pdf");
		if (!dataFile.exists()) {
			SkyLogger.getConnectorLogger().error("IF8.1: "+documentID+" Expected PDF file does not exist: " + dataFile.getName() + "  "+dataFile.getCanonicalPath());
			throw new Exception("IF8.1: "+documentID+" Expected PDF file does not exist: " + dataFile);
		}
		File metaFile = new File(srcDir + File.separator + documentID + ".skyarc");
		if (!metaFile.exists()) {
			SkyLogger.getConnectorLogger().error("IF8.1: "+documentID+" Expected skyarc file does not exist: " + metaFile.getName() + "  "+metaFile.getCanonicalPath());
			throw new Exception("IF8.1: "+documentID+" Expected skyarc file does not exist: " + metaFile);
		}
		String targetPath=getTargetPath();
		File dstDir = new File(targetPath);
		FileUtils.copyFileToDirectory(dataFile, dstDir, true);
		FileUtils.copyFileToDirectory(metaFile, dstDir, true);
		SkyLogger.getConnectorLogger().debug("IF8.1: "+documentID+" Copied files " + metaFile+", "+dataFile + " to " + targetPath);
		FileUtils.touch(new File(targetPath + File.separator + documentID + ".ok"));
		SkyLogger.getConnectorLogger().info("IF8.1: "+documentID+" Uploaded all files to archive for document " + documentID);
	}
}
