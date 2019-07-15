package com.nttdata.de.ityx.cx.sky.outbound;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;
import com.nttdata.de.ityx.cx.workflow.base.FlowUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.contex.interfaces.designer.IFlowObject;
import org.apache.commons.io.FileUtils;

import java.io.File;

public class TransferDocumentToLettershop extends AbstractWflBean {

    private static final Long serialVersionUID = -7855448879962583132L;
    public static final String FLOW_SRC_DIR = "MoveFileToLettershop_SrcDir";
    public static final String FLOW_DST_DIR = "MoveFileToLettershop_DstDir";
    public static final String FLOW_GRP_DOCMAX = "Grouping_MaxDocuments";
    public static final String FLOW_GRP_GRPMAX = "Grouping_MaxGrouping";
    public static final String FLOW_GRP_DATEFORMAT = "Grouping_DateFormat";
    public static final String FLOW_GRP_FORMAT = "Grouping_GroupFormat";


	/**
     * Moves files to lettershop share. Files in src directory are not deleted
     * thus it is left to the contex process to clean up the src upon successful
     * invocation of this bean.
     *
     * @param flowObject
     * @throws Exception
     */
    @Override
    public void execute(IFlowObject flowObject) throws Exception {
        /*
         * Collect parameters from flow
         */
        String arcId = FlowUtils.getRequiredString(flowObject, TagMatchDefinitions.DOCUMENT_ID);
        
        File srcDir = new File(FlowUtils.getRequiredString(flowObject, FLOW_SRC_DIR));
        if (!(srcDir.exists() && srcDir.isDirectory())) {
            SkyLogger.getConnectorLogger().error("IF8.1: "+arcId+" Parameter srcDir is not configured properly");
            throw new Exception("IF8.1: "+arcId+" Parameter srcDir is not configured properly");
        }
        String dstDirString = FlowUtils.getRequiredString(flowObject, FLOW_DST_DIR);

		synchronized (serialVersionUID) {
            File lockFile = new File(srcDir + File.separator + arcId + ".lck");
            if (!lockFile.exists()) {
                FileUtils.touch(lockFile);
            } else {
                SkyLogger.getConnectorLogger().error("IF8.1: "+arcId+" Copy to Lettershop: File transfer has already been initiated. (lck-FileExists)");
                throw new Exception("IF8.1: "+arcId+" Copy to Lettershop: File transfer has already been initiated for id: " + arcId);
            }
        }

        File dataFile = new File(srcDir + File.separator + arcId + ".pdf");
        if (!dataFile.exists()) {
            SkyLogger.getConnectorLogger().error("IF8.1: "+arcId+" Expected PDF file does not exist: " + dataFile.getName() + "  "+dataFile.getCanonicalPath());
            throw new Exception("IF8.1: "+arcId+" Expected PDF file does not exist: " + dataFile);
        }
        File metaFile = new File(srcDir + File.separator + arcId + ".skyarc");
        if (!metaFile.exists()) {
            SkyLogger.getConnectorLogger().error("IF8.1: "+arcId+" Expected skyarc file does not exist: " + metaFile.getName() + "  "+metaFile.getCanonicalPath());
            throw new Exception("IF8.1: "+arcId+" Expected skyarc file does not exist: " + metaFile);
        }
        File dstDir = new File(dstDirString);
        String okFile = dstDirString + File.separator + arcId + ".ok";

        FileUtils.copyFileToDirectory(dataFile, dstDir, true);
        FileUtils.copyFileToDirectory(metaFile, dstDir, true);
        SkyLogger.getConnectorLogger().debug("IF8.1: "+arcId+" Copied files " + metaFile+", "+dataFile + " to " + dstDirString);
        FileUtils.touch(new File(okFile));
        SkyLogger.getConnectorLogger().info("IF8.1: "+arcId+" Uploaded all files to archive for document " + arcId);
    }
}
