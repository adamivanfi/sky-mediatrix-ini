package com.nttdata.de.sky.ityx.mediatrix.businessrules.server.sky;

import com.nttdata.de.ityx.sharedservices.archiveMetadata.ArchiveMetaDataUtils;
import com.nttdata.de.ityx.sharedservices.image.PrintService;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.archive.AbstractArchiveMetaData;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import com.nttdata.de.sky.outbound.MxOutboundIntegration;
import de.ityx.contex.dba.TransactionException;
import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.data.Answer;
import de.ityx.mediatrix.data.Email;
import de.ityx.mediatrix.data.Question;
import de.ityx.base.Global;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/* 
 * Bis 11/2014 hat mediatrix beim Abschluss der Frage die Archivierungs Tiff und XML-Dateien generiert - 
 * und dann an Contex übergeben der sie weiterverarbeitet hat. 
 * Derzeit hat die Klasse die Aufgabe 
 */
public class OutboundRule extends SkyRule {


    /**
     * Fachliche Prüfung ob Frage archiviert werden soll
     *
     * Callby:ServerMultiChannel.deliverToChannel scheint nur für den
     * EmailTyp.letter aufgerufen zu werden
     */
    public boolean questionArchivingPossible(Connection con, int questionId) throws Exception {
        String logPrefix = getClass().getName() + "#" + new Object() {
        }.getClass().getEnclosingMethod().getName() + ": ";

        final Question question = API.getServerAPI().getQuestionAPI().load(con, questionId, true);
        AbstractArchiveMetaData questionArchiveMetaData = ArchiveMetaDataUtils.getArchiveMetaData(con, question);
        Map<String, String> metaMap = questionArchiveMetaData.collectMetadata(con, question);
        return questionArchivingPossible(con, questionId, metaMap, questionArchiveMetaData);
    }

    public boolean questionArchivingPossible(Connection con, int questionId, Map<String, String> metaMap, AbstractArchiveMetaData questionArchiveMetaData) throws Exception {
        String logPrefix = getClass().getName() + "#" + new Object() {
        }.getClass().getEnclosingMethod().getName() + ": ";

        if (questionArchiveMetaData.shouldBeArchived(metaMap)) {
            boolean metaDataComplete = questionArchiveMetaData.isMetadataComplete(metaMap, questionId);
            if (!metaDataComplete) {
                SkyLogger.getMediatrixLogger().warn(logPrefix + "Question can NOT be archived: " + questionId);
                //EntwicklungOnly
                //logStacktrace(logPrefix + " ##archiveCheck## ", metaMap);
            } else {
                SkyLogger.getMediatrixLogger().debug(logPrefix + "Question can be archived: " + questionId);
            }
            return metaDataComplete;
        } else {
            SkyLogger.getMediatrixLogger().debug(logPrefix + "BusinessTest failed: Question shouldn't be archved: " + questionId);
            return false;
        }
    }

    /**
     * Fachliche Prüfung ob Frage archiviert werden soll
     *
     * Callby:ServerMultiChannel.deliverToChannel scheint nur für den
     * EmailTyp.letter aufgerufen zu werden
     */
    public boolean questionArchivingNeeded(Connection con, int questionId) throws Exception {
        final Question question = API.getServerAPI().getQuestionAPI().load(con, questionId, true);
        AbstractArchiveMetaData questionArchiveMetaData = ArchiveMetaDataUtils.getArchiveMetaData(con, question);
        Map<String, String> metaMap = questionArchiveMetaData.collectMetadata(con, question);

        return questionArchivingNeeded(con, questionId, metaMap, questionArchiveMetaData);
    }

    public boolean questionArchivingNeeded(Connection con, int questionId, Map<String, String> metaMap, AbstractArchiveMetaData questionArchiveMetaData) throws Exception {
        String logPrefix = getClass().getName() + "#" + new Object() {
        }.getClass().getEnclosingMethod().getName() + ": ";

        if (questionArchiveMetaData.shouldBeArchived(metaMap)) {
            SkyLogger.getMediatrixLogger().debug(logPrefix + "Question should be archived: " + questionId);
            return true;
        } else {
            SkyLogger.getMediatrixLogger().debug(logPrefix + "Question does not need to be archived: " + questionId);
            return false;
        }
    }

    /**
     * Calls the process that creates a Contex document with metadata. Sets
     * input parameters for Siebel.
     *
     * wird aufgerufen durch ServerSystem.postSendMail
     *
     * @param con
     * @param email
     * @throws RemoteException
     * @throws TransactionException
     * @throws SQLException
     */
    public boolean associateEmailWithSiebel(Connection con, final Email email) throws Exception {
        String logPrefix = getClass().getName() + "#" + new Object() {
        }.getClass().getEnclosingMethod().getName() + " ";
        SkyLogger.getMediatrixLogger().debug(logPrefix + " ##associateEmailWithSiebel## e.id:" + email.getEmailId() + " Start");

        Question question = null;
        Answer answer = null;
        if (Question.class.isAssignableFrom(email.getClass())) {
            question = (Question) email;
            if (isQuesionForwarded(con, question)) {
                return false;
            }
        } else if (Answer.class.isAssignableFrom(email.getClass())) {
            answer = (Answer) email;
            question = API.getServerAPI().getQuestionAPI().load(con, answer.getQuestionId(), true);
        } else {
            throw new Exception(logPrefix + ": Email can not be mapped: " + email.getEmailId());
        }
        AbstractArchiveMetaData archiveMetaData = ArchiveMetaDataUtils.getArchiveMetaData(con, question);
        Map<String, String> metaMap = archiveMetaData.collectMetadata(con, question);
        if (answer != null) {
            metaMap = archiveMetaData.collectMetadata(con, answer, metaMap);
        }
        return associateEmailWithSiebel(con, email, question, answer, archiveMetaData, metaMap);
    }

    public boolean associateEmailWithSiebel(Connection con, final Email email, Question question, Answer answer, AbstractArchiveMetaData archiveMetaData, Map<String, String> metaMap) throws Exception {
        String logPrefix = getClass().getName() + "#" + new Object() {
        }.getClass().getEnclosingMethod().getName() + " ";

        String documentid = metaMap.get(TagMatchDefinitions.DOCUMENT_ID);
        metaMap.put(MxOutboundIntegration.DOCPOOL_PARAMETER, "830_Associate");

        if (!archiveMetaData.shouldBeArchived(metaMap)) {
            SkyLogger.getMediatrixLogger().info(logPrefix + " BusinessTest failed: Question shouldn't be archved: " + question.getId() + " docId:" + documentid);
            //SkyLogger.getMediatrixLogger().info(logPrefix+" qid:"+ question.getId()+" Map:"+formatMap(metaMap));
            //SkyLogger.getMediatrixLogger().info(logPrefix+" qid:"+ question.getId()+" Headers:"+email.getHeaders());
            //logStacktrace(documentid, metaMap);
            return false;
        }
        int questionId = 0;
        if (question != null && question.getId() > 0) {
            questionId = question.getId();
        }
        boolean notSbsProject = TagMatchDefinitions.isNotSbsProject(question);
        if(notSbsProject) {
            if (archiveMetaData.isMetadataComplete(metaMap, questionId)) {
                metaMap.put(MxOutboundIntegration.PROCSTATUS, MxOutboundIntegration.MXOUT_STATUS.WAIT.name());
                MxOutboundIntegration.addCxOutboundDocumentProcessToDBQueue(con, metaMap, "sky");
                SkyLogger.getMediatrixLogger().debug(logPrefix + " ##associateEmailWithSiebel## e.id:" + email.getEmailId() + " FINSHED OK " + documentid);
                return true;
            } else {
                metaMap.put(MxOutboundIntegration.PROCSTATUS, MxOutboundIntegration.MXOUT_STATUS.METAERR.name());
                SkyLogger.getMediatrixLogger().error(logPrefix + " ##associateEmailWithSiebel## e.id:" + email.getEmailId() + " Metamap is not complete:" + questionId + " documentid:" + documentid + " pInput:" + formatMap(metaMap));
                MxOutboundIntegration.addCxOutboundDocumentProcessToDBQueue(con, metaMap, "sky");
                return false;
            }
        }
        else return false;
    }

    public void archiveQuestion(Connection con, Integer questionId) throws Exception {
        Question question = API.getServerAPI().getQuestionAPI().load(con, questionId, true);
        archiveQuestion(con, question);
    }

    public boolean archiveQuestion(Connection con, final Question question) throws Exception {
        String logPrefix = getClass().getName() + "#" + new Object() { }.getClass().getEnclosingMethod().getName() + ": "+question.getId()+" : ";
		AbstractArchiveMetaData archiveMetaData = ArchiveMetaDataUtils.getArchiveMetaData(con, question);
        long startT=System.currentTimeMillis();
        Map<String, String> metaMap = archiveMetaData.collectMetadata(con, question);
        SkyLogger.getMediatrixLogger().info(logPrefix + " collectMetadata took "+(System.currentTimeMillis()-startT));
        return archiveQuestion(con, question, archiveMetaData, metaMap);
    }

    public boolean archiveQuestion(Connection con, final Question question, AbstractArchiveMetaData archiveMetaData, Map<String, String> metaMap) throws Exception {
        String logPrefix = getClass().getName() + "#" + new Object() {
        }.getClass().getEnclosingMethod().getName() + ": "+question.getId()+" : ";
        long startT=System.currentTimeMillis();
        int questionId = question.getId();
        String documentid = metaMap.get(TagMatchDefinitions.DOCUMENT_ID);
        if (!archiveMetaData.shouldBeArchived(metaMap)) {
            SkyLogger.getMediatrixLogger().info(logPrefix + "BusinessTest failed: Question shouldn't be archved: " + question.getId() + " docId:" + documentid);
            return false;
        }
        if (archiveMetaData.isMetadataComplete(metaMap, questionId)) {
            SkyLogger.getMediatrixLogger().debug(logPrefix + " Archiving question: qid:" + questionId + " DocumentID:" + documentid);
            executeArchiving(con, metaMap, question, archiveMetaData);
            //EntwicklungOnly
            //logStacktrace(logPrefix + " ##archiveQuestion## ", metaMap);
            SkyLogger.getMediatrixLogger().info(logPrefix + " archiveQuestionT took "+(System.currentTimeMillis()-startT));
            return true;
        } else {
            SkyLogger.getMediatrixLogger().error(logPrefix + " Archiving question: qid:" + questionId + " DocumentID:" + documentid + " not possible. Metadata not complete");
            //EntwicklungOnly
            //logStacktrace(logPrefix + " ##archiveQuestion## ", metaMap);
            SkyLogger.getMediatrixLogger().info(logPrefix + " archiveQuestionF took "+(System.currentTimeMillis()-startT));
            return false;
            //throw new java.lang.IllegalArgumentException("Archiving of question: qid:" + questionId +" DocumentID:"+documentid+" not possible. Metadata not complete");
        }

    }

    public void archiveQuestionWithAnswers(Connection con, Integer questionId) throws Exception {
        Question question = API.getServerAPI().getQuestionAPI().load(con, questionId, true);
         archiveQuestionWithAnswers(con, question);
    }

    // Map<String, String> parentMetaMap,
    public void archiveQuestionWithAnswers(Connection con, final Question question) throws Exception {
        AbstractArchiveMetaData archiveMetaData = ArchiveMetaDataUtils.getArchiveMetaData(con, question);
        Map<String, String> metaMap = archiveMetaData.collectMetadata(con, question);
         archiveQuestionWithAnswers(con, question, archiveMetaData, metaMap);
    }

    /**
     *   Method tells if "question" in the argument is the additional question (outbound master) at external forwarding.
     * Outbound master question is created by Mediatrix on external forwarding (bundle), that has the questions in bundle
     * up to one: the leading inbound question - the question with the less VORGANGID in budle. Mediatrix shows this
     * leading inbound question to have the others as attachment, but indeed it's the master, that doesn't contain
     * the leading one as attachmewnt. It has to be managed separately - e.g. in ClientMailInbox.postQuestionForward(...)
     *
     * @param question
     * @return is "question" an outbound master?: true/false
     */
	private boolean isOutboundMaster (final Question question) {
        //SIT-18-10-056 - Adam Ivanfi
        // MX-External forwarding in bundle causes the creation of a new question that's referred as "outbound master".
        // See ClientMailInbox.postQuestionForward(...) and ClientRunner.forwardServlet(...) as well!
        final boolean isOriginal = (question.getOrginal()==0);
        /*
        final String emailSender = question.getFrom().toLowerCase();
		final String[] emailAddresses = Global.getProperty("external.forward.sender", "").toLowerCase().split(";");
        boolean addressForwardSender = false;

        for (String addr : emailAddresses) {
            if (emailSender.equals(addr)) {
				addressForwardSender=true;
                break;
            }
        }
        */
		return !isOriginal; // && addressForwardSender;
    }


    public void archiveQuestionWithAnswers(Connection con, final Question question, AbstractArchiveMetaData archiveMetaData, Map<String, String> questionMetaMap) throws Exception {
        String logPrefix = getClass().getName() + "#" + new Object() {
        }.getClass().getEnclosingMethod().getName() + " - Question-ID:"+question.getId()+" - ";

        int questionId = question.getId();
        String documentid = questionMetaMap.get(TagMatchDefinitions.DOCUMENT_ID);
        //SIT-18-10-056 - Jardel Luis Roth + Adam Ivanfi
        if (isOutboundMaster(question)) {
            SkyLogger.getMediatrixLogger().info(logPrefix + "Outbound master question shouldn't be archived. Doc-Id:" + documentid);
            return ;
        }
        //SIT-18-10-056 - end
        if (!archiveMetaData.shouldBeArchived(questionMetaMap)) {
            SkyLogger.getMediatrixLogger().info(logPrefix + "BusinessTest failed: Question (no outbound master) shouldn't be archived. Question-Id:" + question.getId() + ", doc-Id:" + documentid);
            return ;
        }

        long startT=System.currentTimeMillis();

        if (archiveMetaData.isMetadataComplete(questionMetaMap, questionId)) {
            SkyLogger.getMediatrixLogger().debug(logPrefix + " Archiving question: qid:" + questionId + " DocumentID:" + documentid);
            for (Answer archiveAnswer : API.getServerAPI().getQuestionAPI().loadAnswers(con, questionId, true)) {
                if (archiveAnswer!=null && archiveAnswer.getStatus()!=null ){
                    if (archiveAnswer.getStatus().equalsIgnoreCase("fragment") && (archiveAnswer.getType() == Email.TYPE_DOCUMENT || archiveAnswer.getType() == Email.TYPE_LETTER || archiveAnswer.getType() == Email.TYPE_FAX)){
                     SkyLogger.getMediatrixLogger().debug(logPrefix + " Skip not finished WhitePaper answer " + archiveAnswer.getId() + " (" + archiveAnswer.getStatus() + "/" + archiveAnswer.getSendTime() + ") for question: qid:" + questionId + " DocumentID:" + documentid);
                    }else{
                        archiveAnswer.setProjectId(question.getProjectId());
                        SkyLogger.getMediatrixLogger().debug(logPrefix + " Archiving answer " + archiveAnswer.getId() + " (" + archiveAnswer.getStatus() + "/" + archiveAnswer.getSendTime() + ") for question: qid:" + questionId + " DocumentID:" + documentid);
                        Map<String, String> answerMetaMap = new HashMap<>();
                        answerMetaMap.putAll(questionMetaMap);
                        answerMetaMap = archiveMetaData.collectMetadata(con, archiveAnswer, answerMetaMap);
                        archiveAnswer(con, question, archiveAnswer, archiveMetaData, answerMetaMap);
                    }
                }
            }
            executeArchiving(con, questionMetaMap, question, archiveMetaData);
            //EntwicklungOnly
            //logStacktrace(logPrefix + " ##archiveQuestionWithAnswers## ", questionMetaMap);
		} else {
            SkyLogger.getMediatrixLogger().error(" Archiving question: qid:" + questionId + " DocumentID:" + documentid + " not possible.Metadate not complete");
            //EntwicklungOnly
            //logStacktrace(logPrefix + " ##archiveQuestionWithAnswers## ", questionMetaMap);
            //throw new java.lang.IllegalArgumentException("Archiving of question: qid:" + questionId +" DocumentID:"+documentid+" not possible. Metadata not complete");
        }
        SkyLogger.getMediatrixLogger().info(logPrefix + " archiveQuestionWithAnswers took "+(System.currentTimeMillis()-startT) + " milli seconds.");

    }

    protected boolean archiveAnswer(Connection con, Answer answer) throws SQLException {
        Question question = API.getServerAPI().getQuestionAPI().load(con, answer.getQuestionId(), false);
        return archiveAnswer(con, question, answer);

    }

    protected boolean archiveAnswer(Connection con, Question question, Answer answer) {
        AbstractArchiveMetaData questionArchiveMetaData = ArchiveMetaDataUtils.getArchiveMetaData(con, question);
        Map<String, String> metaMap;
        long startT=System.currentTimeMillis();
        String logPrefix = getClass().getName() + "#" + new Object() {}.getClass().getEnclosingMethod().getName() + ": "+question.getId()+" : "+answer.getId()+" : ";
        try {
             metaMap = questionArchiveMetaData.collectMetadata(con, question);
            metaMap = questionArchiveMetaData.collectMetadata(con, answer, metaMap);
            SkyLogger.getMediatrixLogger().info(logPrefix + " archiveAnswerCM took "+(System.currentTimeMillis()-startT));

            return archiveAnswer(con, question, answer, questionArchiveMetaData, metaMap);
        } catch (ClassCastException ex) {
            SkyLogger.getMediatrixLogger().error(logPrefix + " ClassCastException:", ex);
            return false;
        } catch (NoSuchMethodException ex) {
             SkyLogger.getMediatrixLogger().error(logPrefix + " NoSuchMethodException:", ex);
            return false;
        }

    }

    protected boolean archiveAnswer(Connection con, Question question, Answer answer, AbstractArchiveMetaData questionArchiveMetaData, Map<String, String> metaMap) {

        String logPrefix = getClass().getName() + "#" + new Object() {
        }.getClass().getEnclosingMethod().getName() + ": archiveAnswer a.id:" + answer.getId() + " q.id:" + question.getId() + " ";

        long startT=System.currentTimeMillis();
        try {
            SkyLogger.getMediatrixLogger().debug(logPrefix + "start");
            //if (answer.getSendTime() == 0) {

            if (!questionArchiveMetaData.shouldBeArchived(metaMap)) {
                SkyLogger.getMediatrixLogger().info(logPrefix + "BusinessTest failed: Question shouldn't be archved: " + question.getId());
                return false;
            }
            //String customerId = TagMatchDefinitions.extractHeader(question.getHeaders(), TagMatchDefinitions.X_TAGMATCH_CUSTOMER_ID);
            if (questionArchiveMetaData.isMetadataComplete(metaMap, question.getId())) {
                SkyLogger.getMediatrixLogger().debug(logPrefix + "metadata complete - start archiving");
                //logStacktrace(logPrefix + " ##archiveQuestionWithAnswers## ", metaMap);

                if (answer.getSendTime() > 0) {
                    metaMap.put(AbstractArchiveMetaData.TIMESTAMP_CREATED, AbstractArchiveMetaData.getFormattedTimestamp(new Date(answer.getSendTime())));
                } else {
                    long lastActivityTime = PrintService.getLastActivityTime(con, question.getId());
                    if (lastActivityTime > 0) {
                        answer.setSendTime(lastActivityTime);
                        metaMap.put(AbstractArchiveMetaData.TIMESTAMP_CREATED, AbstractArchiveMetaData.getFormattedTimestamp(new Date(lastActivityTime)));
                    }
                }
                final long currentTimeMillis = System.currentTimeMillis();
                API.getServerAPI().getAnswerAPI().store(con, answer);
                SkyLogger.getMediatrixLogger().debug("OutboundRule: Funktion archiveAnswer_store took " + (System.currentTimeMillis() - currentTimeMillis) + " " + question.getDocId() + " " + question.getId());

                executeArchiving(con, metaMap, answer, questionArchiveMetaData);
                SkyLogger.getMediatrixLogger().info(logPrefix + " archiveAnswerMDC took "+(System.currentTimeMillis()-startT));

                return true;
            } else {
                SkyLogger.getMediatrixLogger().error(logPrefix + "metadata not complete:" + question.getId());
                //logStacktrace(logPrefix + " ##archiveQuestionWithAnswers## ", metaMap);
                //throw new java.lang.IllegalArgumentException("Archiving of answer: aid:" + question.getId() +" not possible. Metadata not complete");
                return false;
            }
            // } else {
            //     SkyLogger.getMediatrixLogger().debug(logPrefix+"answer already archived: " + answer.getSendTime());
            //     logStacktrace(logPrefix + " ##archiveQuestionWithAnswers## ", null);
            // }
        } catch (Exception e) {
            SkyLogger.getMediatrixLogger().error(logPrefix + "archiveAnswer " + e.getLocalizedMessage(), e);
            return false;
        }
    }


    private boolean isQuesionForwarded(Connection con, Question question) {
        // Todo: Optimierungspotenzial, besser wäre hier Status der Frage oder die Action abgreifen.
        // Do not associate email when forwarding question.

        String logPrefix = getClass().getName() + "#" + new Object() {
        }.getClass().getEnclosingMethod().getName() + ": ";

        SkyLogger.getMediatrixLogger().warn(logPrefix + " ##isQuesionForwarded## Question" + question.getId() + " s:" + question.getStatus() + "/" + question.getGlobalStatus());
        ResultSet rs = null;
        try {
            rs = con.createStatement().executeQuery("select aktion, zeit from mitarbeiterlog where frageid=" + question.getId() + " and aktion=3 and zeit> datetoityxtime(sysdate-interval '2' Minute) order by zeit desc");
            if (rs.next() && rs.getInt("aktion") == 3) {
                SkyLogger.getMediatrixLogger().warn(logPrefix + " ##isQuesionForwarded## Question" + question.getId() + " has been forwarded in the last 2 minutes, no archiving " + question.getId());
                //logStacktrace(logPrefix + " ##isQuesionForwarded## Question " + question.getId() + " has been forwarded in the last 2 minutes, no archiving ", null);
                return true;
            }
        } catch (SQLException e) {
            SkyLogger.getMediatrixLogger().error(logPrefix + " ##isQuesionForwarded## Problem accessing mitarbeiterlog for: " + question.getId(), e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    SkyLogger.getMediatrixLogger().debug(logPrefix + " ##isQuesionForwarded## Problem closing mitarbeiterlog rs: " + question.getId(), e);
                }
            }
        }
        return false;
    }


}
