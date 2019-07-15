package com.nttdata.de.sky.ityx.common;

import com.nttdata.de.lib.logging.SkyLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DocIdGenerator {

    private static final String NEXTVAL_MULTICASE = "select SEQNTT_MX_MULTICASE.nextval from dual";
    private static final String NEXTVAL_OUTBOUND = "select SEQNTT_MX_OUTBOUND.nextval from dual";
    private static final String NEXTVAL_INBOUND = "select SEQNTT_MX_INBOUND.nextval from dual";

    public static synchronized String createUniqueDocumentId(Connection con, TagMatchDefinitions.DocumentDirection documenttype, TagMatchDefinitions.Channel channel, Date incommingDate) {
        return createUniqueDocumentId(con, documenttype, channel, null, incommingDate);
    }
     
    public static synchronized String createUniqueDocumentId(Connection con, TagMatchDefinitions.DocumentDirection documenttype, TagMatchDefinitions.Channel channel, String parentDocID) {
        return createUniqueDocumentId( con,  documenttype,  channel,  parentDocID, new Date(System.currentTimeMillis()));
    }
    
    public static synchronized String createUniqueDocumentId(Connection con, TagMatchDefinitions.DocumentDirection documenttype, TagMatchDefinitions.Channel channel, String parentDocID, Date incommingDate) {
        String docID = "ITYX";
        String systemcode = System.getProperty("com.nttdata.dms.serviceID", "Z");
        String docTypeCode = getDocumenttypeCode(documenttype);
        String channelCode = getChannelCode(channel);
        String hexString = getDocIdSequenceHex(con, documenttype);
        if (hexString == null) {
            String logPrefix = DocIdGenerator.class.getName() + "#" + new Object() {
            }.getClass().getEnclosingMethod().getName();
            SkyLogger.getCommonLogger().error(logPrefix + "Generation of DocID faied. No HexCode/Sequence aviable");
            return null;
        }else{
            hexString=hexString.toUpperCase();
        }
        if ((parentDocID == null || parentDocID.isEmpty() || parentDocID.length() < 17)
                && (documenttype == TagMatchDefinitions.DocumentDirection.INBOUND || documenttype == TagMatchDefinitions.DocumentDirection.SPLITTED)) {
                 docID +=  (new SimpleDateFormat("yyyyMMddHHmmssSSS").format(incommingDate));
        } else if (parentDocID != null && !parentDocID.isEmpty() && parentDocID.length()>=21 && parentDocID.substring(4, 6).equals("20")) {
            docID += parentDocID.substring(4, 21);
            if (parentDocID.length()>=30){
                docID += '-'+parentDocID.substring(22, parentDocID.indexOf('-',22)>0?parentDocID.indexOf('-',22):(parentDocID.length()>34?34:parentDocID.length()));
            }
        } else if (parentDocID != null && !parentDocID.isEmpty() && parentDocID.length()>=17 && parentDocID.substring(4, 6).equals("13")) {
            Long datum=Long.parseLong(parentDocID.substring(4, 17));
            docID +=  new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date(datum));
           }  else{
            docID +=  (new SimpleDateFormat("yyyyMMddHHmmssSSS").format(incommingDate));
        } 
        docID += "-"+docTypeCode+ systemcode + channelCode + hexString;
        return docID;
    }

    private static synchronized String getDocIdSequenceHex(Connection con, TagMatchDefinitions.DocumentDirection documenttype) {
        Long sequenceID = null;
        String sequence = null;
        switch (documenttype) {
            case INBOUND:
                sequence = NEXTVAL_INBOUND;
                break;
            case OUTBOUND:
                sequence = NEXTVAL_OUTBOUND;
                break;
            case MULTICASE:
                sequence = NEXTVAL_MULTICASE;
                break;
            case INDIVIDUALCORRESPONDENCE:
                sequence = NEXTVAL_MULTICASE;
                break;
            case SPLITTED:
                sequence = NEXTVAL_MULTICASE;
                break;
            case FORWARDED:
                sequence = NEXTVAL_MULTICASE;
                break;
            case DEFAULT:
                sequence = NEXTVAL_MULTICASE;
                break;
            default:
                sequence = NEXTVAL_MULTICASE;
        }
        PreparedStatement idStmt = null;
        ResultSet rs = null;
        try {

            idStmt = con.prepareStatement(sequence);
            rs = idStmt.executeQuery();
            if (rs.next()) {
                sequenceID = rs.getLong(1);
            }
        } catch (SQLException e) {
            String logPrefix = DocIdGenerator.class.getName() + "#" + new Object() {
            }.getClass().getEnclosingMethod().getName();
            SkyLogger.getCommonLogger().error(logPrefix + "Generation of DocID-NexVal faied. Problems accessing Sequence for " + documenttype.toString() + " : " + sequence, e);
            return null;

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                String logPrefix = DocIdGenerator.class.getName() + "#" + new Object() {
                }.getClass().getEnclosingMethod().getName();
                SkyLogger.getCommonLogger().warn(logPrefix + "Problem closing rs for Sequence " + documenttype.toString() + " : " + sequence, e);

            }
            try {
                if (idStmt != null) {
                    idStmt.close();
                }
            } catch (SQLException e) {
                String logPrefix = DocIdGenerator.class.getName() + "#" + new Object() {
                }.getClass().getEnclosingMethod().getName();
                SkyLogger.getCommonLogger().warn(logPrefix + "Problem closing stmt for Sequence " + documenttype.toString() + " : " + sequence, e);

            }
        }
        return getPaddedHexString(sequenceID, 5);
    }

    public static String getDocumenttypeCode(TagMatchDefinitions.DocumentDirection documenttype) {
        String documenttypeCode = "D";

        switch (documenttype) {
            case INBOUND:
                documenttypeCode = "I";
                break;
            case OUTBOUND:
                documenttypeCode = "O";
                break;
            case MULTICASE:
                documenttypeCode = "M";
                break;
            case INDIVIDUALCORRESPONDENCE:
                documenttypeCode = "C";
                break;
            case SPLITTED:
                documenttypeCode = "S";
                break;
            case FORWARDED:   
                documenttypeCode = "F";
                break;
            case DEFAULT:
                documenttypeCode = "D";
                break;
        }
        return documenttypeCode;
    }
   
    public static String getChannelCode(TagMatchDefinitions.Channel channel) {
        String channelcode = "D";
        switch (channel) {
            case EMAIL:
                channelcode = "E";
                break;
            case BRIEF:
                channelcode = "L";
                break;
            case FAX:
                channelcode = "F";
                break;
            case SOCIALMEDIA:
                channelcode = "S";
                break;
            case SOCIALMEDIACARINGCOMMUNITY:
                channelcode = "SC";
                break;
            case SOCIALMEDIAFACEBOOK:
                channelcode = "SF";
                break;
            case SOCIALMEDIATWITTER:
                channelcode = "ST";
                break;
            case SOCIALMEDIAGOOGLE:
                channelcode = "SG";
                break;
            case DOCUMENT:
                channelcode = "D";
                break;
        }
        return channelcode;
    }

    public static TagMatchDefinitions.DocumentDirection getDocType(TagMatchDefinitions.Direction documenttype) {
        TagMatchDefinitions.DocumentDirection result = null;
        switch (documenttype) {
            case INBOUND:
                result = TagMatchDefinitions.DocumentDirection.INBOUND;
                break;
            case OUTBOUND:
                result = TagMatchDefinitions.DocumentDirection.OUTBOUND;
                break;
        }
        return result;
    }

    public static String getPaddedHexString(Long id, Integer length) {
        String hexString = Long.toHexString(id);
        while (hexString.length() < length) {
            hexString = "0" + hexString;
        }
        return hexString;
    }
}
