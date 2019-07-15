package com.nttdata.de.sky.ityx.mediatrix.businessrules.archiving;

import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.archive.AbstractArchiveMetaData;
import com.nttdata.de.sky.ityx.common.DocIdGenerator;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.interfaces.document.CDocument;
import de.ityx.contex.interfaces.extag.TagMatch;
import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.data.Answer;
import de.ityx.mediatrix.data.Email;
import de.ityx.mediatrix.data.Question;
import de.ityx.mediatrix.data.Subproject;
import de.ityx.mediatrix.data.metainformation.MetaInformationDocumentContainer;
import de.ityx.mediatrix.data.metainformation.MetaInformationInt;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MDocumentArchiveMetaData extends AbstractArchiveMetaData {

	public MDocumentArchiveMetaData() throws Exception {
		super();
		requiredTagmatches.add(TagMatchDefinitions.MX_QUESTIONID);
	}

	//wird wegen Fehlerbehandlung fuer Nacharchivierung benoetigt
	@Override
	public Map<String, String> deepCollectMetadata(Connection con, Object source, Map<String, String> srcMap) throws ClassCastException, NoSuchMethodException {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName();

		if (source==null){
			return null;
		}

		if (!Email.class.isAssignableFrom(source.getClass())) {
			SkyLogger.getMediatrixLogger().error(logPrefix + "Incompatible parameter class: " + source.getClass().getName() + " cannot be an instance of de.ityx.mediatrix.data.Email");
			throw new ClassCastException(logPrefix + "Incompatible parameter class: " + source.getClass().getName() + " cannot be an instance of de.ityx.mediatrix.data.Email");
		}
		Email email = (Email) source;
		Map<String, String> metaMap = new TreeMap<String, String>();
		if (srcMap != null) {
			metaMap.putAll(srcMap);
		}
		try {
			for (MetaInformationInt meta : API.getServerAPI().getMetaInformationAPI().loadByEmailId(con, email.getEmailId())) {
				if (meta.getType().equals(MetaInformationDocumentContainer.TYPENAME)) {
					CDocumentContainer<CDocument> doc = (CDocumentContainer<CDocument>) meta.getContent();
					if (doc != null) {
						List<TagMatch> tags = doc.getTags();
						for (TagMatch tm : tags) {
							String val = tm.getTagValue();
							if (isNotEmpty(val)) {
								metaMap.put(tm.getIdentifier(), val);
							}
						}
					}
					break;
				}
			}
		} catch (SQLException e) {
			SkyLogger.getMediatrixLogger().warn(logPrefix + ": Problem reading Container-MetaInfo for Mail:" + email.getEmailId());
		}
		return collectMetadata(con, source, metaMap);
	}


	@Override
	public Map<String, String> collectMetadata(Connection con, Object source, Map<String, String> srcMap) throws ClassCastException, NoSuchMethodException {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName();

		if (!Email.class.isAssignableFrom(source.getClass())) {
			SkyLogger.getMediatrixLogger().error(logPrefix + "Incompatible parameter class: " + source.getClass().getName() + " cannot be an instance of de.ityx.mediatrix.data.Email");
			throw new ClassCastException(logPrefix + "Incompatible parameter class: " + source.getClass().getName() + " cannot be an instance of de.ityx.mediatrix.data.Email");
		}
		Email email = (Email) source;
		Map<String, String> metaMap = new TreeMap<String, String>();
		if (srcMap != null) {
			metaMap.putAll(srcMap);
		}

		String[] headers_split = email.getHeaders().split("\n");
		for (String line : headers_split) {
			if (line.contains("=")) {
				String[] entry = line.split("=");
				if (entry.length == 2 && entry[0].startsWith(X_TAGMATCH)) {
					String val = entry[1];
					if (isNotEmpty(val)) {
						metaMap.put(entry[0].substring(x_tagmatch_length + 1).trim(), val.trim());
					}
				}
			}
		}
		//umgekehrt nicht notwendig, da in Tag4ArchiveMap die umwandlung stattfindet
		String tmdocid = metaMap.get(TagMatchDefinitions.DOCUMENT_ID);
		if (tmdocid != null && !tmdocid.isEmpty()) {
			metaMap.put(TagMatchDefinitions.X_TAGMATCH_DOCUMENT_ID, tmdocid);
		}
		metaMap.put(TagMatchDefinitions.MX_EMAILID, email.getEmailId() + "");

		String channelOrg = metaMap.get(TagMatchDefinitions.CHANNEL);
		String channelCur = null;
		if (email.getType() == Email.TYPE_EMAIL) {
			channelCur = TagMatchDefinitions.Channel.EMAIL.toString();
		} else if (email.getType() == Email.TYPE_LETTER) {
			channelCur = TagMatchDefinitions.Channel.BRIEF.toString();
		} else if (email.getType() == Email.TYPE_FAX) {
			channelCur = TagMatchDefinitions.Channel.FAX.toString();
		} else if (email.getType() == Email.TYPE_DOCUMENT) {
			//SkyLogger.getMediatrixLogger().warn(logPrefix + ": Channel can not be recognized by emailtype (DOCUMENT) for email:" + email.getEmailId());
		} else {
			SkyLogger.getMediatrixLogger().warn(logPrefix + ": Channel can not be recognized for email:" + email.getEmailId() + " type:" + email.getType());
		}
		if (channelCur != null) {
			metaMap.put("EMAIL_TYPE", channelCur);
		}
		if (channelOrg == null || channelOrg.isEmpty()) {
			if (channelCur != null && !channelCur.isEmpty()) {
				metaMap.put(TagMatchDefinitions.CHANNEL, channelCur);
			} else if (email.getType() == Email.TYPE_DOCUMENT) {
				channelCur = TagMatchDefinitions.Channel.BRIEF.toString();
				metaMap.put(TagMatchDefinitions.CHANNEL, channelCur);
			}
		}
		if (channelOrg != null && channelCur != null && !channelOrg.equalsIgnoreCase(channelCur)) {
			//SkyLogger.getMediatrixLogger().debug(logPrefix + ": Channel conflict for emailId:" + email.getEmailId()+" TagMatch:"+channelOrg+" EmailType:"+channelCur);
			metaMap.put(TagMatchDefinitions.CHANNEL, channelCur);
		}

		if (metaMap.get(TIMESTAMP_CREATED) == null || metaMap.get(TIMESTAMP_CREATED).isEmpty()) {
			String idate = metaMap.get(TagMatchDefinitions.INCOMINGDATE);
			if (Answer.class.isAssignableFrom(source.getClass())) {
				Answer answer = (Answer) source;
				long lastActivityTime = answer.getSendTime();
				if (lastActivityTime < 1) {
					lastActivityTime = answer.getEmailDate();
				}
				metaMap.put(AbstractArchiveMetaData.TIMESTAMP_CREATED, getFormattedTimestamp(new Date(lastActivityTime)));
			} else if (idate != null && !idate.isEmpty()) {
				metaMap.put(TIMESTAMP_CREATED, getFormattedTimestamp(getDate(idate)));
			} else {
				final String documentid = metaMap.get(TagMatchDefinitions.DOCUMENT_ID);
				if (documentid != null && !documentid.isEmpty() && documentid.length() >= 18 && documentid.substring(4, 6).equals("20")) {
					String createdTimestamp = documentid.substring(4, 18);
					metaMap.put(TIMESTAMP_CREATED, getFormattedTimestamp(getDate(createdTimestamp)));
				} else if (documentid != null && !documentid.isEmpty() && documentid.length() >= 17 && documentid.substring(4, 6).equals("13")) {
					String createdTimestampS = documentid.substring(4, 17);
					long createdTimestampL = Long.parseLong(createdTimestampS);
					metaMap.put(TIMESTAMP_CREATED, getFormattedTimestamp(new Date(createdTimestampL)));
				} else if (Question.class.isAssignableFrom(source.getClass())) {
					Question question = (Question) source;
					long lastActivityTime = question.getEmailDate();
					metaMap.put(AbstractArchiveMetaData.TIMESTAMP_CREATED, getFormattedTimestamp(new Date(lastActivityTime)));
				} else {
					metaMap.put(TIMESTAMP_CREATED, getFormattedTimestamp(new Date()));
				}
			}
		}

		if (Question.class.isAssignableFrom(source.getClass())) {
			Question question = (Question) source;
			metaMap.put(TagMatchDefinitions.MX_QUESTIONID, question.getId() + "");
			if (question.isServicecenter()) {
				metaMap.put(TagMatchDefinitions.MX_DIRECTION, TagMatchDefinitions.Direction.OUTBOUND.toString());
				if ((metaMap.get(TagMatchDefinitions.ACTIVITY_ID) == null || metaMap.get(TagMatchDefinitions.ACTIVITY_ID).isEmpty()) && (email.getExtra12() != null && !email.getExtra12().isEmpty())) {
					metaMap.put(TagMatchDefinitions.ACTIVITY_ID, email.getExtra12());
				}
				if ((metaMap.get(TagMatchDefinitions.CONTACT_ID) == null || metaMap.get(TagMatchDefinitions.CONTACT_ID).isEmpty()) && (email.getExtra8() != null && !email.getExtra8().isEmpty())) {
					metaMap.put(TagMatchDefinitions.CONTACT_ID, email.getExtra8());
				}
				if ((metaMap.get(TagMatchDefinitions.X_TAGMATCH_DOCUMENT_ID) == null || metaMap.get(TagMatchDefinitions.X_TAGMATCH_DOCUMENT_ID).isEmpty()) && (question.getDocId() != null && !question.getDocId().isEmpty())) {
					metaMap.put(TagMatchDefinitions.X_TAGMATCH_DOCUMENT_ID, question.getDocId());
				}
				if ((metaMap.get(TagMatchDefinitions.DOCUMENT_ID) == null || metaMap.get(TagMatchDefinitions.DOCUMENT_ID).isEmpty()) && (question.getDocId() != null && !question.getDocId().isEmpty())) {
					metaMap.put(TagMatchDefinitions.DOCUMENT_ID, question.getDocId());
				}
				if ((metaMap.get(TagMatchDefinitions.CUSTOMER_ID) == null || metaMap.get(TagMatchDefinitions.CUSTOMER_ID).isEmpty()) && (email.getExtra3() != null && !email.getExtra3().isEmpty())) {
					metaMap.put(TagMatchDefinitions.CUSTOMER_ID, email.getExtra3());
				}
			} else {
				if ((metaMap.get(TagMatchDefinitions.MX_DIRECTION) == null || metaMap.get(TagMatchDefinitions.MX_DIRECTION).isEmpty())) {
					metaMap.put(TagMatchDefinitions.MX_DIRECTION, TagMatchDefinitions.Direction.INBOUND.toString());
				}
				if ((metaMap.get(TagMatchDefinitions.CONTACT_ID) == null || metaMap.get(TagMatchDefinitions.CONTACT_ID).isEmpty()) && (email.getExtra8() != null && !email.getExtra8().isEmpty())) {
					metaMap.put(TagMatchDefinitions.CONTACT_ID, email.getExtra8());
				}
				//if ((metaMap.get(TagMatchDefinitions.CUSTOMER_ID) == null || metaMap.get(TagMatchDefinitions.CUSTOMER_ID).isEmpty()) && (email.getExtra3() != null && !email.getExtra3().isEmpty())) {
				//    metaMap.put(TagMatchDefinitions.CUSTOMER_ID, email.getExtra3());
				//}
				if ((metaMap.get(TagMatchDefinitions.CUSTOMER_COUNTRY) == null || metaMap.get(TagMatchDefinitions.CUSTOMER_COUNTRY).isEmpty()) && (email.getExtra9() != null && !email.getExtra9().isEmpty())) {
					metaMap.put(TagMatchDefinitions.CUSTOMER_COUNTRY, email.getExtra9());
				}
				if ((metaMap.get(TagMatchDefinitions.X_TAGMATCH_DOCUMENT_ID) == null || metaMap.get(TagMatchDefinitions.X_TAGMATCH_DOCUMENT_ID).isEmpty()) && (question.getDocId() != null && !question.getDocId().isEmpty())) {
					metaMap.put(TagMatchDefinitions.X_TAGMATCH_DOCUMENT_ID, question.getDocId());
				}
				if ((metaMap.get(TagMatchDefinitions.DOCUMENT_ID) == null || metaMap.get(TagMatchDefinitions.DOCUMENT_ID).isEmpty()) && (question.getDocId() != null && !question.getDocId().isEmpty())) {
					metaMap.put(TagMatchDefinitions.DOCUMENT_ID, question.getDocId());
				}
			}

			metaMap.put(TagMatchDefinitions.MX_TP_ID, question.getSubprojectId() + "");
			if (con != null) {
				try {
					final Subproject subproject = API.getServerAPI().getSubprojectAPI().load(con, question.getSubprojectId());
					if (subproject != null) {
						metaMap.put(TagMatchDefinitions.MX_TP_NAME, subproject.getName());
					}
				} catch (SQLException e) {
					SkyLogger.getMediatrixLogger().warn(logPrefix + ": Can load Teilprojektname for Question:" + question.getId(), e);
				}
			}
			String lang = question.getLanguage() + "";
			if (lang != null) {
				metaMap.put(TagMatchDefinitions.MX_LANGUAGE, lang);
			}
			String documentid =question.getDocId();
			
			if (documentid == null || documentid.isEmpty()) {
				documentid=metaMap.get(TagMatchDefinitions.X_TAGMATCH_DOCUMENT_ID);
			}
			boolean saveDocID = false;
			if (documentid == null || documentid.isEmpty()) {
				String directionS = metaMap.get(TagMatchDefinitions.MX_DIRECTION);
				String channelS = metaMap.get(TagMatchDefinitions.CHANNEL);
				if (con != null && directionS != null && !directionS.isEmpty() && channelS != null && !channelS.isEmpty()) {
					TagMatchDefinitions.DocumentDirection dirtype = TagMatchDefinitions.getDocumentDirection(directionS);
					TagMatchDefinitions.Channel channel = TagMatchDefinitions.getChannel(channelS);
					java.util.Date incommingDate = getDate(metaMap.get(TIMESTAMP_CREATED));
					documentid = DocIdGenerator.createUniqueDocumentId(con, dirtype, channel, incommingDate);
					saveDocID = true;
				} else {
					SkyLogger.getMediatrixLogger().warn(logPrefix + ": Can't generate DocID for Answer:" + question.getId() + ". Channel:" + channelS + " Docutype:" + directionS);
				}
			}

			if (documentid != null && !documentid.isEmpty()) {
				metaMap.put(TagMatchDefinitions.MX_QUESTIONDOCUMENTID, documentid);
				metaMap.put(TagMatchDefinitions.DOCUMENT_ID, documentid);
				metaMap.put(TagMatchDefinitions.X_TAGMATCH_DOCUMENT_ID, documentid);

				if (saveDocID || question.getDocId() == null || question.getDocId().isEmpty() || !question.getDocId().equalsIgnoreCase(documentid)) {
					question.setDocId(documentid);
					question.setHeaders(TagMatchDefinitions.addOrReplaceXTMHeader(question.getHeaders(), TagMatchDefinitions.DOCUMENT_ID, documentid));
					email.setHeaders(TagMatchDefinitions.addOrReplaceXTMHeader(email.getHeaders(), TagMatchDefinitions.DOCUMENT_ID, documentid));

					try {
						SkyLogger.getCommonLogger().info("MDAMD.QStore1 Generated docid:" + question.getDocId() + " frage:" + question.getId());
						de.ityx.mediatrix.api.API.getServerAPI().getEmailAPI().store(con, email);
						SkyLogger.getCommonLogger().info("MDAMD.QStore1a Generated docid:" + question.getDocId() + " frage:" + question.getId());
						de.ityx.mediatrix.api.API.getServerAPI().getQuestionAPI().store(con, question);
						SkyLogger.getCommonLogger().info("MDAMD.QStore2 Generated docid:" + question.getDocId() + " frage:" + question.getId());

						SkyLogger.getMediatrixLogger().debug("MDAMD.QD Generated docid:" + documentid + " frage:" + question.getId());

					} catch (java.sql.SQLException ex) {
						SkyLogger.getMediatrixLogger().warn(" MDAMD.QD Can't store generated docid:" + documentid + " f:" + question.getId(), ex);
					}
				}
			}
			String created = metaMap.get(TagMatchDefinitions.INCOMINGDATE);
			String ts_created = metaMap.get(AbstractArchiveMetaData.TIMESTAMP_CREATED);
			if ((ts_created == null || ts_created.isEmpty()) && (created != null && !created.isEmpty())) {
				metaMap.put(AbstractArchiveMetaData.TIMESTAMP_CREATED, created);
			}
		} else if (Answer.class.isAssignableFrom(source.getClass())) {
			Answer answer = (Answer) source;
			if (channelCur != null) {
				SkyLogger.getMediatrixLogger().debug(logPrefix + ": Setting Channel for Answer emailId:" + email.getEmailId()+" TagMatch:"+channelOrg+" EmailType:"+channelCur);
				metaMap.put(TagMatchDefinitions.CHANNEL, channelCur);
			}
			metaMap.put(TagMatchDefinitions.MX_ANSWERID, answer.getId() + "");
			metaMap.put(TagMatchDefinitions.MX_DIRECTION, TagMatchDefinitions.Direction.OUTBOUND.toString());
			if ((metaMap.get(TagMatchDefinitions.ACTIVITY_ID) == null || metaMap.get(TagMatchDefinitions.ACTIVITY_ID).isEmpty()) && (email.getExtra8() != null && !email.getExtra8().isEmpty())) {
				metaMap.put(TagMatchDefinitions.ACTIVITY_ID, email.getExtra8());
			}
			if ((metaMap.get(TagMatchDefinitions.CONTACT_ID) == null || metaMap.get(TagMatchDefinitions.CONTACT_ID).isEmpty()) && (email.getExtra9() != null && !email.getExtra9().isEmpty())) {
				metaMap.put(TagMatchDefinitions.CONTACT_ID, email.getExtra9());
			}
			String documentid = metaMap.get(TagMatchDefinitions.DOCUMENT_ID);
			String frageDocID = metaMap.get(TagMatchDefinitions.MX_QUESTIONDOCUMENTID);
			String answerDocID = metaMap.get(TagMatchDefinitions.MX_ANSWERDOCUMENTID);

			boolean answerchanged = false;
			if (answerDocID != null && !answerDocID.isEmpty() && (frageDocID == null || frageDocID.isEmpty() || !answerDocID.equalsIgnoreCase(frageDocID))) {
				documentid = answerDocID;
				metaMap.put(TagMatchDefinitions.DOCUMENT_ID, answerDocID);
				metaMap.put(TagMatchDefinitions.X_TAGMATCH_DOCUMENT_ID, answerDocID);
				email.setHeaders(TagMatchDefinitions.addOrReplaceXTMHeader(email.getHeaders(), TagMatchDefinitions.DOCUMENT_ID, documentid));
				email.setHeaders(TagMatchDefinitions.addOrReplaceXTMHeader(email.getHeaders(), TagMatchDefinitions.MX_ANSWERDOCUMENTID, documentid));
				//email.setHeaders(TagMatchDefinitions.addHeader(email.getHeaders(), TagMatchDefinitions.DOCUMENT_ID, documentid));
				answerchanged = true;
			}

			if (documentid == null || documentid.isEmpty() || documentid.equalsIgnoreCase(frageDocID)) {
				String directionS = metaMap.get(TagMatchDefinitions.MX_DIRECTION);
				String channelS = metaMap.get(TagMatchDefinitions.CHANNEL);
				if (con != null && directionS != null && !directionS.isEmpty() && channelS != null && !channelS.isEmpty() && frageDocID != null && !frageDocID.isEmpty()) {
					TagMatchDefinitions.DocumentDirection dirtype = TagMatchDefinitions.getDocumentDirection(directionS);
					TagMatchDefinitions.Channel channel = TagMatchDefinitions.getChannel(channelS);
					java.util.Date incommingDate = getDate(metaMap.get(TIMESTAMP_CREATED));
					documentid = DocIdGenerator.createUniqueDocumentId(con, dirtype, channel, frageDocID, incommingDate);
					metaMap.put(TagMatchDefinitions.X_TAGMATCH_DOCUMENT_ID, documentid);
					metaMap.put(TagMatchDefinitions.DOCUMENT_ID, documentid);
					metaMap.put(TagMatchDefinitions.MX_ANSWERDOCUMENTID, documentid);
					email.setHeaders(TagMatchDefinitions.addOrReplaceXTMHeader(email.getHeaders(), TagMatchDefinitions.DOCUMENT_ID, documentid));
					email.setHeaders(TagMatchDefinitions.addOrReplaceXTMHeader(email.getHeaders(), TagMatchDefinitions.MX_ANSWERDOCUMENTID, documentid));
					//email.setHeaders(TagMatchDefinitions.addHeader(email.getHeaders(), TagMatchDefinitions.DOCUMENT_ID, documentid));
					answerchanged=true;
					SkyLogger.getMediatrixLogger().debug("MDAMD.A Generated docid:" + documentid + " email:" + answer.getId());
				} else {
					SkyLogger.getMediatrixLogger().warn(logPrefix + ": Can't generate DocID for Answer:" + answer.getId() + ". Channel:" + channelS + " Docutype:" + directionS + " QuestionDocID:" + frageDocID);
				}
			}
			if (answer.getSendTime() > 0) {
				String created = getFormattedTimestamp(new Date(answer.getSendTime()));
				metaMap.put(AbstractArchiveMetaData.TIMESTAMP_CREATED, created);
			}

			if (answerchanged) {
				try {
					API.getServerAPI().getEmailAPI().store(con, email);
				} catch (SQLException ex) {
					SkyLogger.getMediatrixLogger().warn(logPrefix + " Can't store answer docid:" + documentid + " for answer:" + answer.getId() + " frageDocID:" + frageDocID, ex);
				}
			}
		} else {
			SkyLogger.getMediatrixLogger().warn(logPrefix + " Unknown soruce class:" + source.getClass() + " Metadata may be not complete.");
		}
		if (metaMap.get(TagMatchDefinitions.DOCUMENT_TYPE) == null || metaMap.get(TagMatchDefinitions.DOCUMENT_TYPE).isEmpty()) {

			if (metaMap.get(TagMatchDefinitions.FORM_TYPE_CATEGORY) != null && !metaMap.get(TagMatchDefinitions.FORM_TYPE_CATEGORY).isEmpty()) {
				metaMap.put(TagMatchDefinitions.DOCUMENT_TYPE, metaMap.get(TagMatchDefinitions.FORM_TYPE_CATEGORY));
			} else if (metaMap.get(TagMatchDefinitions.X_TAGMATCH_FORM_TYPE) != null && !metaMap.get(TagMatchDefinitions.X_TAGMATCH_FORM_TYPE).isEmpty()) {
				metaMap.put(TagMatchDefinitions.DOCUMENT_TYPE, metaMap.get(TagMatchDefinitions.X_TAGMATCH_FORM_TYPE));
			} else if (email.getExtra4() != null && !email.getExtra4().isEmpty()) {
				metaMap.put(TagMatchDefinitions.DOCUMENT_TYPE, email.getExtra4());
			} else if (metaMap.get(TagMatchDefinitions.MX_TP_NAME) != null && !metaMap.get(TagMatchDefinitions.MX_TP_NAME).isEmpty()) {
				metaMap.put(TagMatchDefinitions.DOCUMENT_TYPE, metaMap.get(TagMatchDefinitions.MX_TP_NAME));
			}
		}
		if (metaMap.get(TagMatchDefinitions.FORM_TYPE_CATEGORY) == null || metaMap.get(TagMatchDefinitions.FORM_TYPE_CATEGORY).isEmpty()) {
			if (metaMap.get(TagMatchDefinitions.DOCUMENT_TYPE) != null || !metaMap.get(TagMatchDefinitions.DOCUMENT_TYPE).isEmpty()) {
				metaMap.put(TagMatchDefinitions.FORM_TYPE_CATEGORY, metaMap.get(TagMatchDefinitions.DOCUMENT_TYPE));
			} else {
				metaMap.put(TagMatchDefinitions.FORM_TYPE_CATEGORY, "systemdefault");
			}
		}
		//translates special Tagmatches and computes creationtime
		metaMap = super.collectMetadata(con, source, metaMap);
		return metaMap;
	}

	private boolean isNotEmpty(String val) {
		return (val != null && !val.isEmpty() && !val.trim().equalsIgnoreCase("0") && !val.trim().equalsIgnoreCase("-"));
	}
}
