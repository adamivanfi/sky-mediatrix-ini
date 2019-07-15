package com.nttdata.de.sky.archive;

import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.DocIdGenerator;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.data.Email;
import de.ityx.mediatrix.data.Question;
import org.w3c.dom.Document;

import javax.xml.XMLConstants;
import javax.xml.bind.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Handles archive metadata.
 *
 * @author DHIFLM
 */
// TODO: Implement interface?
public abstract class AbstractArchiveMetaData {
	public static final String TIMESTAMP_CREATED = "TimestampCreated";
	public static final String TIMESTAMP_TRANSFERRED = "TimestampTransferred";



	protected static final String X_TAGMATCH = "X-Tagmatch";
	protected final int x_tagmatch_length;

	public final List<String> requiredTagmatchesWhitePaper = new ArrayList<String>(Arrays.asList(TagMatchDefinitions.CUSTOMER_STREET, TagMatchDefinitions.CUSTOMER_ZIP_CODE, TagMatchDefinitions.CUSTOMER_CITY));

	public final List<String> requiredTagmatches = new ArrayList<String>(Arrays.asList(TagMatchDefinitions.DOCUMENT_ID, TagMatchDefinitions.CUSTOMER_ID, TagMatchDefinitions.DOCUMENT_TYPE, TagMatchDefinitions.CHANNEL, TagMatchDefinitions.MX_DIRECTION, TIMESTAMP_CREATED));
	public final List<String> requiredTagmatchesSKY = new ArrayList<String>(Arrays.asList(TagMatchDefinitions.CUSTOMER_LAST_NAME));

	protected final List<String> optionalArchiveTagnames = new ArrayList<String>(Arrays.asList(TagMatchDefinitions.CUSTOMER_FIRST_NAME, TIMESTAMP_TRANSFERRED, TagMatchDefinitions.MX_QUESTIONID, TagMatchDefinitions.MX_ANSWERID, TagMatchDefinitions.MX_EMAILID));
	protected final List<String> optionalArchiveTagnamesSKY = new ArrayList<String>(Arrays.asList(TagMatchDefinitions.CONTACT_ID, TagMatchDefinitions.ACTIVITY_ID, TagMatchDefinitions.TAGMATCH_CONTRACT_NUMBER, TagMatchDefinitions.SEPA_MANDATE_NUMBER, TagMatchDefinitions.BIC, TagMatchDefinitions.SEPA_SIGNATURE_FLAG, TagMatchDefinitions.SEPA_SIGNATURE_DATE, TagMatchDefinitions.CUSTOMER_COUNTRY));

	protected Map<String, String> tag4archiveMap;
	protected List<String> archiveAttributes;
	protected List<String> archiveAttributesSBS;

	protected DocumentBuilderFactory factory;
	protected DocumentBuilder builder;
	protected ObjectFactory archiveFactory;
	protected JAXBContext archiveContext;

	protected static final String dateFormatPattern = "yyyyMMddHHmmss";

	protected AbstractArchiveMetaData() throws Exception {
		tag4archiveMap = new TreeMap<String, String>();
		tag4archiveMap.put(TagMatchDefinitions.FORM_TYPE_CATEGORY, TagMatchDefinitions.DOCUMENT_TYPE);
		tag4archiveMap.put(TagMatchDefinitions.X_TAGMATCH_FORM_TYPE, TagMatchDefinitions.DOCUMENT_TYPE);
		//tag4archiveMap.put(TagMatchDefinitions.INCOMINGDATE, TIMESTAMP_CREATED);

		archiveAttributes = new LinkedList<String>();
		archiveAttributes.addAll(requiredTagmatches);
		archiveAttributes.addAll(requiredTagmatchesSKY);
		archiveAttributes.addAll(optionalArchiveTagnames);
		archiveAttributes.addAll(requiredTagmatchesWhitePaper);
		archiveAttributes.addAll(optionalArchiveTagnamesSKY);

		archiveAttributesSBS = new LinkedList<String>();
		archiveAttributesSBS.addAll(requiredTagmatches);
		archiveAttributesSBS.addAll(optionalArchiveTagnames);
		archiveAttributesSBS.addAll(requiredTagmatchesWhitePaper);

		x_tagmatch_length = X_TAGMATCH.length();
		factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		builder = factory.newDocumentBuilder();
		archiveFactory = new ObjectFactory();
		archiveContext = JAXBContext.newInstance("com.nttdata.de.sky.archive");
	}

	/**
	 * @throws JAXBException
	 * @throws PropertyException
	 */
	public Marshaller initMarshaller() throws JAXBException {
		Marshaller archiveMarshaller = archiveContext.createMarshaller();
		archiveMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		archiveMarshaller.setProperty(Marshaller.JAXB_ENCODING, "windows-1252");
		// Gregor, 11.11.2014 -  Lettershop versteht nicht UTF-8 und man darf hier das Encoding nicht ändern.
		// basiert auf aussage von Robert Koch der bei Lettershop nachgefragt hat
		return archiveMarshaller;
	}

	public Unmarshaller initUnmarshaller() throws JAXBException {
		return archiveContext.createUnmarshaller();
	}

	/**
	 * Checks if the XML contains a valid archive metadata instance wrto
	 * archive.xsd.
	 *
	 * @param xmlStream This XML contains the metadata.
	 * @return Is the metadata valid XML?
	 */
	public boolean validateArchiveXML(InputStream xmlStream) {
		boolean valid = false;
		try {
			Document doc = builder.parse(xmlStream);
			SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Source schemaFile = new StreamSource(getClass().getResourceAsStream("/com/nttdata/de/sky/archive.xsd"));
			Schema schema = schemaFactory.newSchema(schemaFile);
			Validator validator = schema.newValidator();
			validator.validate(new DOMSource(doc));
			valid = true;
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return valid;
	}

	public Map<String, String> deepCollectMetadata(Connection con, Object source, Map<String, String> srcMap) throws ClassCastException, NoSuchMethodException {
		return collectMetadata(con, source, srcMap);
	}

	public Map<String, String> collectMetadata(Connection con, Object source) throws ClassCastException, NoSuchMethodException {
		return collectMetadata(con, source, null);
	}

	public Map<String, String> collectMetadata(Connection con, Object source, Map<String, String> srcMap) throws ClassCastException, NoSuchMethodException {
		Map<String, String> metaMap = new TreeMap<String, String>();

		for (Map.Entry<String, String> mentry : srcMap.entrySet()) {
			if (mentry.getKey().contains("X-Tagmatch:")) {
				metaMap.put(mentry.getKey().replace("X-Tagmatch:", ""), mentry.getValue());
			} else {
				metaMap.put(mentry.getKey(), mentry.getValue());
			}
		}
		for (String orgkey : tag4archiveMap.keySet()) {
			final String value = metaMap.get(orgkey);
			String mappedkey = tag4archiveMap.get(orgkey);
			if (value != null && (metaMap.get(mappedkey)==null || metaMap.get(mappedkey).isEmpty())) {
				metaMap.put(mappedkey, value);
			}
		}
		if (metaMap.get(TIMESTAMP_CREATED) == null || metaMap.get(TIMESTAMP_CREATED).isEmpty()) {
			String idate = metaMap.get(TagMatchDefinitions.INCOMINGDATE);
			if (idate != null && !idate.isEmpty()) {
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
				} else {
					metaMap.put(TIMESTAMP_CREATED, getFormattedTimestamp(new Date()));
				}
			}
		}
		String documentid = metaMap.get(TagMatchDefinitions.DOCUMENT_ID);
		
		if ((documentid == null || documentid.isEmpty()) && con != null) {
			String directionS = metaMap.get(TagMatchDefinitions.MX_DIRECTION);
			String channelS = metaMap.get(TagMatchDefinitions.CHANNEL);
			if (directionS != null && !directionS.isEmpty() && channelS != null && !channelS.isEmpty()) {
				TagMatchDefinitions.DocumentDirection doctype = TagMatchDefinitions.getDocumentDirection(directionS);
				TagMatchDefinitions.Channel channel = TagMatchDefinitions.getChannel(channelS);

				java.util.Date incommingDate = getDate(metaMap.get(TIMESTAMP_CREATED));
				documentid = DocIdGenerator.createUniqueDocumentId(con, doctype, channel, incommingDate);

				metaMap.put(TagMatchDefinitions.X_TAGMATCH_DOCUMENT_ID, documentid);
				metaMap.put(TagMatchDefinitions.DOCUMENT_ID, documentid);

				if (Question.class.isAssignableFrom(source.getClass())) {
					Question question = (Question) source;
					question.setDocId(documentid);
					question.setHeaders(TagMatchDefinitions.addOrReplaceXTMHeader(question.getHeaders(), TagMatchDefinitions.DOCUMENT_ID, documentid));

					try {
						SkyLogger.getCommonLogger().info("AAMD.QStore1 Generated docid:" + question.getDocId() + " frage:" + question.getId()+" status:"+question.getStatus());
						String statusbak=question.getStatus();
						question.setStatus(Question.S_BLOCKED);
						boolean questionstoreok = API.getServerAPI().getQuestionAPI().store(con, question);
						question.setStatus(statusbak);
						SkyLogger.getCommonLogger().info("AAMD.QStore2 Generated docid:" + question.getDocId() + " frage:" + question.getId()+" sok:"+questionstoreok);
						SkyLogger.getMediatrixLogger().debug("AAMD.O Generated docid:" + documentid + " frage:" + question.getId());

					} catch (java.sql.SQLException ex) {
						SkyLogger.getMediatrixLogger().warn(" Can't store generated docid:" + documentid + " dir:" + directionS + " channel:" + channelS, ex);
					}
				}
				if (Email.class.isAssignableFrom(source.getClass())) {
					Email email = (Email) source;

					//email.setHeaders(TagMatchDefinitions.addHeader(email.getHeaders(), TagMatchDefinitions.X_TAGMATCH_DOCUMENT_ID, documentid));
					email.setHeaders(TagMatchDefinitions.addOrReplaceXTMHeader(email.getHeaders(), TagMatchDefinitions.DOCUMENT_ID, documentid));
					try {
						de.ityx.mediatrix.api.API.getServerAPI().getEmailAPI().store(con, email);
					} catch (java.sql.SQLException ex) {
						SkyLogger.getMediatrixLogger().warn(" Can't store generated docid:" + documentid + " dir:" + directionS + " channel:" + channelS, ex);
					}
				}
			}
		}
		String firstName = metaMap.get(TagMatchDefinitions.CUSTOMER_FIRST_NAME);
		if (firstName == null || firstName.isEmpty()) {
			metaMap.put(TagMatchDefinitions.CUSTOMER_FIRST_NAME, "");
		}
		return metaMap;

	}
	protected Map<String, String> filterArchiveEntries(Map<String, String> metaMap, List<String> larchiveAttributes) {
		Map<String, String> result = new TreeMap<String, String>();
		for (String key : larchiveAttributes) {
			final String mapped = tag4archiveMap.get(key);
			final String tagName = mapped == null ? key : mapped;
			final String value = metaMap.get(tagName);
			if (value != null) {
				result.put(key, value);
			}
		}
		return result;
	}

	public boolean shouldBeArchived(Map<String, String> metaMap) {
		if (metaMap == null || metaMap.isEmpty()) {
			SkyLogger.getMediatrixLogger().info("BusinessTest failed: EmptyMetaMap");
			return false;
		}
		String custId = metaMap.get(TagMatchDefinitions.CUSTOMER_ID);
		if (custId != null && !custId.equals("0") && custId.length() > 1) {
			return true;
		} else {
			String msg = metaMap.get(TagMatchDefinitions.DOCUMENT_ID) != null ? " docId:" + metaMap.get(TagMatchDefinitions.DOCUMENT_ID) : "";
			msg += metaMap.get(TagMatchDefinitions.MX_QUESTIONID) != null ? " question:" + metaMap.get(TagMatchDefinitions.MX_QUESTIONID) : "";
			msg += metaMap.get(TagMatchDefinitions.MX_ANSWERID) != null ? " answer:" + metaMap.get(TagMatchDefinitions.MX_ANSWERID) : "";
			SkyLogger.getMediatrixLogger().info("BusinessTest failed: Document doesn't contains CustomerID." + msg);
			return false;
		}
	}

	public boolean isMetadataComplete(Map<String, String> metaMap, int questionid) {
		return  isMetadataComplete(metaMap, questionid + "");
	}

	public boolean isMetadataComplete(Map<String, String> metaMap, String questionid) {
		StringBuilder archiveMetaOk = new StringBuilder();
		StringBuilder archiveMetaNok = new StringBuilder();
		boolean allRequiredParamsFound = true;

		if (!shouldBeArchived(metaMap)) {
			return false;
		}

		if ((metaMap.get(TagMatchDefinitions.CUSTOMER_LAST_NAME) == null || metaMap.get(TagMatchDefinitions.CUSTOMER_LAST_NAME).isEmpty()) 
				&& (metaMap.get(TagMatchDefinitions.CUSTOMER_FIRST_NAME) != null && !metaMap.get(TagMatchDefinitions.CUSTOMER_FIRST_NAME).isEmpty())) {


			metaMap.put(TagMatchDefinitions.CUSTOMER_LAST_NAME, metaMap.get(TagMatchDefinitions.CUSTOMER_FIRST_NAME));

		}
		if ((metaMap.get(TagMatchDefinitions.CUSTOMER_FIRST_NAME) == null || metaMap.get(TagMatchDefinitions.CUSTOMER_FIRST_NAME).isEmpty()) 
				&& (metaMap.get(TagMatchDefinitions.CUSTOMER_LAST_NAME) != null && !metaMap.get(TagMatchDefinitions.CUSTOMER_LAST_NAME).isEmpty())) {

			metaMap.put(TagMatchDefinitions.CUSTOMER_FIRST_NAME, metaMap.get(TagMatchDefinitions.CUSTOMER_LAST_NAME));


		}
		if (metaMap.get(TagMatchDefinitions.CUSTOMER_ZIP_CODE) != null && !metaMap.get(TagMatchDefinitions.CUSTOMER_ZIP_CODE).isEmpty() 
				&& metaMap.get(TagMatchDefinitions.CUSTOMER_ZIP_CODE).length() > 9) {
			metaMap.put(TagMatchDefinitions.CUSTOMER_ZIP_CODE, metaMap.get(TagMatchDefinitions.CUSTOMER_ZIP_CODE).substring(0, 8));
		}

		for (String key : requiredTagmatches) {
			final String value = metaMap.get(key);
			if (value != null) {
				archiveMetaOk.append(key).append(":").append(value).append(", ");
			} else {
				allRequiredParamsFound = false;
				archiveMetaNok.append(key).append(", ");
			}
		}

		String directionS = metaMap.get(TagMatchDefinitions.MX_DIRECTION);
		String channelS = metaMap.get(TagMatchDefinitions.CHANNEL);
		if (directionS != null && !directionS.isEmpty() && channelS != null && !channelS.isEmpty()) {
			TagMatchDefinitions.Channel channel = TagMatchDefinitions.getChannel(channelS);
			TagMatchDefinitions.DocumentDirection direction = TagMatchDefinitions.getDocumentDirection(directionS);

			if ((direction == TagMatchDefinitions.DocumentDirection.OUTBOUND) && (channel == TagMatchDefinitions.Channel.BRIEF || channel == TagMatchDefinitions.Channel.DOCUMENT || channel == TagMatchDefinitions.Channel.FAX)) {
				for (String key : requiredTagmatchesWhitePaper) {
					final String value = metaMap.get(key);
					if (value != null) {
						archiveMetaOk.append(key).append(":").append(value).append(", ");
					} else {
						allRequiredParamsFound = false;
						archiveMetaNok.append(key).append(", ");
					}
				}
			}
		}
		if (!allRequiredParamsFound) {
			SkyLogger.getMediatrixLogger().warn("Problem during collection of ArchiveMetadata for Question:" + questionid + ": missing params: " + archiveMetaNok.toString() + "; foundparams:" + archiveMetaOk.toString());
			if (SkyLogger.getMediatrixLogger().isDebugEnabled()) {
				String stacktrace = "";
				for (StackTraceElement e : Thread.currentThread().getStackTrace()) {
					stacktrace += " => " + e.getClassName() + ">" + e.getMethodName() + ":" + e.getLineNumber() + "\r\n";
				}
				SkyLogger.getMediatrixLogger().debug("ArchiveMetadata Problem durign collection of params: stacktrace:" + stacktrace);
			}
			return false;
		} else {
			return true;
		}

	}

	/**
	 * @param timestamp time in ms
	 * @return timestamp formatted as yyyyMMddHHmmss
	 */
	public static synchronized String getFormattedTimestamp(Date timestamp) {
		return (new SimpleDateFormat(dateFormatPattern)).format(timestamp);
	}

	public static synchronized Date getDate(String formattedTimestamp) {
		Date ret = null;
		try {
			ret = (new SimpleDateFormat(dateFormatPattern)).parse(formattedTimestamp);
		} catch (ParseException e) {
			SkyLogger.getItyxLogger().error("Problem during Parsing ArchiveDate: " + formattedTimestamp, e);
			//e.printStackTrace();
		}
		if (ret == null) {
			ret = new Date();
		}
		return ret;
	}

	public Archive buildMetaDataXML(Map<String, String> map, boolean isSbsProject, int filesToArchive) {
		Archive archive = createDefaultArchive();
		archive.setDocumentcount(new BigInteger(""+filesToArchive));
		Map<String, String> archiveMap;
		if(isSbsProject) {
			archive.setDatadictionary(TagMatchDefinitions.DMSB_2_ARCHIVE_META);
			archiveMap = filterArchiveEntries(map,archiveAttributesSBS);
		}else{
			archiveMap = filterArchiveEntries(map,archiveAttributes);
		}
		final String documentid = archiveMap.get(TagMatchDefinitions.DOCUMENT_ID);
		archive.setDocumentid(documentid);

		Meta meta = archiveFactory.createMeta();
		archive.setMeta(meta);
		meta.setCount(new BigInteger("1"));
		Metarecord metarecord = archiveFactory.createMetarecord();
		meta.getMetarecord().add(metarecord);

		archiveMap.put(TIMESTAMP_TRANSFERRED, getFormattedTimestamp(new Date()));

		for (Map.Entry<String, String> me : archiveMap.entrySet()) {
			String value = me.getValue();
			if (value != null && value.trim().length() > 0) {
				Metavalue metavalue = archiveFactory.createMetavalue();
				metavalue.setKey(me.getKey());
				if (TagMatchDefinitions.CUSTOMER_ZIP_CODE.equalsIgnoreCase(me.getKey())){
					metavalue.setContent(value.length() > 8 ? value.substring(0, 7) : value);
				}else {
					metavalue.setContent(value.length() > 50 ? value.substring(0, 49) : value);
				}
				metarecord.getMetavalue().add(metavalue);
			}
		}
		return archive;
	}

	public String marshallArchive(Archive archive) {
		String ret = null;
		try {
			OutputStream os = new ByteArrayOutputStream();
			initMarshaller().marshal(archive, os);
			os.close();
			ret = os.toString();
		} catch (Exception e) {
			SkyLogger.getItyxLogger().error("Problem during marshalling archive: " + archive != null ? archive.getDocumentid() : "unkown", e);
			//e.printStackTrace();
		}
		return ret;
	}

	public Archive unmarshallArchive(File file) {
		Archive ret = null;
		try {
			ret = (Archive) initUnmarshaller().unmarshal(file);
		} catch (Exception e) {
			SkyLogger.getItyxLogger().error("Problem during unmarshalling file: " + file != null ? file.getName() : "emptyFile", e);
		}
		return ret;
	}

	public void marshallArchive(Archive archive, String filePrefix) {
		try {
			String fileName = filePrefix + ".skyarc";
			//System.err.println("Writing archive file: " + fileName);
			SkyLogger.getItyxLogger().info("Writing archive file::" + fileName);
			File archiveFile = new File(fileName);
			initMarshaller().marshal(archive, archiveFile);
		} catch (JAXBException e) {
			SkyLogger.getItyxLogger().warn("Problem during marshaling Aachive:" + filePrefix, e);
		}
	}

	public File marshallArchiveF(Archive archive, String fileName) throws JAXBException {
		try {
			SkyLogger.getItyxLogger().info("Writing archive file::" + fileName);
			File archiveFile = new File(fileName);
			initMarshaller().marshal(archive, archiveFile);
			return archiveFile;
		} catch (JAXBException e) {
			SkyLogger.getItyxLogger().warn("Problem during marshaling Aachive:" + fileName, e);
			throw e;
		}
	}

	protected Archive createDefaultArchive() {
		Archive archive = archiveFactory.createArchive();
		archive.setArchivecommand("CREATE");
		archive.setDatadictionary(TagMatchDefinitions.DMS_ARCHIVE_META);
		archive.setDocumentcount(new BigInteger("1"));
		archive.setReadablesource("ITyX Mediatrix");
		archive.setIsCampaign("0");
		return archive;
	}

	// public static void main(String[] args) {
	// System.out.println(new
	// Long(System.currentTimeMillis()).toString().length());
	// String documentid="ITYX"+System.currentTimeMillis()+"-";
	// System.out.println(documentid.substring(4,17)+"\n"+documentid);
	// }
	// public static void main(String[] args) {
	// String documentid="ITYX20131220113927816-32739478";
	// System.out.println(documentid.substring(4,18));
	// }

}
