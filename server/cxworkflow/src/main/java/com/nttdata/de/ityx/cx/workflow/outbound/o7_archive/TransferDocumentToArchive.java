package com.nttdata.de.ityx.cx.workflow.outbound.o7_archive;

import com.jcraft.jsch.ChannelSftp;
import com.nttdata.de.ityx.cx.sky.GroupGenerator;
import com.nttdata.de.ityx.cx.sky.GroupGenerator.Parameters;
import com.nttdata.de.ityx.cx.sky.outbound.SFTPSession;
import com.nttdata.de.ityx.sharedservices.configuration.BeanConfig;
import com.nttdata.de.ityx.sharedservices.image.ArchiveUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

/**
 * Moves a file to the SFTP Archive
 *
 * @author VOGTDA
 *
 */
public class TransferDocumentToArchive {


    /**
     *
     */
    private static final long serialVersionUID = 2804105661425035768L;
    private static final Long serialVersionUIDL = serialVersionUID;

    private static TransferDocumentToArchive archiveTransferer;

    public static final String FLOW_FTP_HOST = "MoveFileToArchive_FtpHost";
    public static final String FLOW_FTP_PORT = "MoveFileToArchive_FtpPort";
    public static final String FLOW_FTP_USER = "MoveFileToArchive_FtpUser";
    public static final String FLOW_FTP_PASSWORD = "MoveFileToArchive_FtpPassword";
    public static final String FLOW_FTP_ROOT = "MoveFileToArchive_FtpRoot";
    //public static final String FLOW_DIRECTION = "MoveFileToArchive_Direction";
    // public static final String FLOW_CHANNEL = "MoveFileToArchive_Channel";
    public static final String FLOW_GRP_DOCMAX = "Grouping_MaxDocuments";
    public static final String FLOW_GRP_GRPMAX = "Grouping_MaxGrouping";
    public static final String FLOW_GRP_DATEFORMAT = "Grouping_DateFormat";
    public static final String FLOW_GRP_FORMAT = "Grouping_GroupFormat";

    public static final String UPLOAD_ENABLED="MoveFileToArchive_UploadEnabled";

    private String host;
    private int port;
    private String user;
    private String password ;
    private String root;
    private int groupDocMax;
    private int groupGrpMax ;
    private String  groupDateFormat ;
    private String groupFormat ;
    private String baseDir ;


    public static synchronized TransferDocumentToArchive getInstance(){
        synchronized (serialVersionUIDL){
            if (archiveTransferer==null){
                archiveTransferer = new TransferDocumentToArchive();
            }
        }
        return archiveTransferer;

    }

    private TransferDocumentToArchive(){

        try {
            host =  BeanConfig.getReqString(FLOW_FTP_HOST);
            port =  BeanConfig.getInt(FLOW_FTP_PORT, 21);
            user = BeanConfig.getReqString(FLOW_FTP_USER);
            password = BeanConfig.getReqString(FLOW_FTP_PASSWORD);
            root = BeanConfig.getString(FLOW_FTP_ROOT, "/");

            baseDir = BeanConfig.getReqString(ArchiveUtils.ARCHIVE_BASE_DIR);
        } catch (Exception e) {
            SkyLogger.getConnectorLogger().error("IF6.1: Initalization of ArchiveFTP NotPossible: "+e.getMessage(),e);

        }

        groupDocMax = BeanConfig.getInt(FLOW_GRP_DOCMAX, 1000);
        groupGrpMax = BeanConfig.getInt(FLOW_GRP_GRPMAX, 1000);
        groupDateFormat = BeanConfig.getString(FLOW_GRP_DATEFORMAT, "yy-MM-dd");
        groupFormat = BeanConfig.getString(FLOW_GRP_FORMAT, "%date-%name-%starttime");
    }

    /**
     * Uploads a file via FTP to a directory as specified in section 4.12.1.6.1
     * See also GroupGenerator for creation of grouping and batch directory
     * names
     *
     * The bean does not delete the input files, this is left for the contex
     * process to do
     *
     * @throws Exception
     */
    public synchronized void  transferToArchiveSrv(List<File> transferFiles, File metaFile, String docid, String direction, String formtype, Boolean isSbsArchive)throws Exception {
        //   IFlowObject superflowObject, IExflowState exflowState, Map<String, Object> docMeta,  long logid) throws Exception {

        // Generates remote directory name.
        String groupGeneratorName = "ARC_" + direction;
        GroupGenerator.prepareInstance(groupGeneratorName, new Parameters(groupDocMax, groupGrpMax, groupDateFormat, groupFormat));
        String[] groups = GroupGenerator.getInstance(groupGeneratorName).incGrouping();
        assert groups.length == 3;

        /*
         * Identifies source files.
         */

        for (File pdfFile: transferFiles) {
            if (!pdfFile.exists()) {
                SkyLogger.getConnectorLogger().error("IF6.1: " + docid + " ArchivingNotPossible: missing PDF-File:" + pdfFile.getAbsolutePath());
                throw new Exception("IF6.1: " + docid + " ArchivingNotPossible: missing PDF-File:" + pdfFile.getAbsolutePath());
            }
        }
        if ( !metaFile.exists()) {
            SkyLogger.getConnectorLogger().error("IF6.1: "+docid+" ArchivingNotPossible: missing META-File:"+metaFile.getAbsolutePath());
            throw new Exception("IF6.1: "+docid+" ArchivingNotPossible: missing META-File:"+metaFile.getAbsolutePath());
        }

        String prefix = generatePrefix(formtype, isSbsArchive);

        /*
         * Upload files
         */
        SFTPSession session = null;
        ChannelSftp channel = null;

        //synchronized (serialVersionUIDL) {
        try {
            session = SFTPSession.getInstance(host, port, user, password);
            channel = session.openChannel();
            session.changeDir(root, false, channel);

            // Retries if failed.
        } catch (Throwable tr) {
            SkyLogger.getConnectorLogger().warn("Reseting FTP-Session. cause:" + tr.getMessage());
            wait(5000);

            try {
                if (channel != null && channel.isConnected()) {
                    channel.disconnect();
                }
                if (session != null) {
                    SFTPSession.resetInstance(host);
                }
                session = SFTPSession.getInstance(host, port, user, password);
                channel = session.openChannel();
                session.changeDir(root, false, channel);
            } catch (Throwable t) {
                wait(30000);
                if (channel != null && channel.isConnected()) {
                    channel.disconnect();
                }
                if (session != null) {
                    SFTPSession.resetInstance(host);
                }
                String msg = "IF6.1: "+docid+" Failed to transfer PDF-File ";
                SkyLogger.getConnectorLogger().error(msg, t);
                throw new Exception(msg, t);
            }
        }

        String remoteFilePreafix = prefix + direction + "/" + groups[0] + "-" + groups[1] + "/" + groups[2] + "/" + docid + "/" ;
        session.changeDir(remoteFilePreafix,true, channel);
        try {
            int i=0;
            long start = System.currentTimeMillis();
            FileInputStream inputMeta = new FileInputStream(metaFile);

            session.uploadFile(inputMeta, docid+ "-"+String.format("%04d", 1)+".skyarc", channel);
            SkyLogger.getConnectorLogger().info("IF6.1: " + docid + " File " + metaFile.getAbsolutePath() + " uploaded" );
            inputMeta.close();

            for (File pdfFile: transferFiles) {
                i++;
                String ext= FilenameUtils.getExtension(pdfFile.getAbsolutePath());
                String remoteFile=docid+ "-"+String.format("%04d", i)+"."+ext;

                FileInputStream input = new FileInputStream(pdfFile);

               session.uploadFile(input, remoteFile, channel);
                SkyLogger.getConnectorLogger().info("IF6.1: " + docid + " File " + pdfFile.getAbsolutePath() + " uploaded to " + remoteFile);
                input.close();
            }

            FileInputStream inputOK = new FileInputStream(metaFile);
            session.uploadFile(inputOK, docid + ".ok", channel);
            inputOK.close();
            SkyLogger.getConnectorLogger().info("IF6.1: "+docid+" Uploaded:"+remoteFilePreafix+" duration: "+ (System.currentTimeMillis() - start) + "ms");

        } catch (Exception e) {
            SFTPSession.resetInstance(host);
            String msg = "IF6.1: "+docid+" Failed to transfer file to archive: " + remoteFilePreafix;
            SkyLogger.getConnectorLogger().error(msg, e);
            throw new Exception(msg, e);
        } finally {
            if (channel != null && channel.isConnected()) {
                channel.disconnect();
            }
        }

    }

    private String generatePrefix(String formtype, Boolean sbsArchive) {
        String prefix = "ITYX/";
        if (sbsArchive) {
            prefix = "B2IT/";
        }

        if (formtype!=null && formtype.equalsIgnoreCase(TagMatchDefinitions.SEPA_MANDATE)) {
            prefix = "SEIT/";
        }

        return prefix;
    }
}