package com.nttdata.de.ityx.sharedservices.image;

import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by meinusch on 23.07.15.
 */
public class ArchivingAttachment {
	
	public ArchivingAttachment(byte[] bytes, String fileName, String contenttype) {
		this(bytes, fileName);
		this.contenttype = contenttype;
	}
	
	public ArchivingAttachment(byte[] bytes, String fileName, Map<Integer, String> ocrText) {
		this(bytes, fileName);
		this.ocrText = ocrText;
	}
	
	public ArchivingAttachment(byte[] bytes, String fileName) {
		this.bytes = bytes;
		this.fileName = fileName;
	}
	
	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	byte[] bytes;
	String fileName;
	String contenttype;
	Map<Integer, String> ocrText = new HashMap<>();
	
	public boolean isContentAtt() {
		return contentAtt;
	}
	
	public void setContentAtt(boolean contentAtt) {
		this.contentAtt = contentAtt;
	}
	
	boolean contentAtt = false;
	
	public byte[] getBytes() {
		return bytes;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public void setOcrText(int pageNo, String ocrtext) {
		ocrText.put(pageNo, ocrtext);
	}
	
	public String getOcrText(int pageNo) {
		return ocrText.get(pageNo);
	}
	
	public String getExtension() {
		String ext =null;
		if (fileName != null && !fileName.isEmpty()) {
			ext=FilenameUtils.getExtension(fileName).toLowerCase().replaceAll("[^\\d\\w]", "");
		}
		if (ext == null || ext.isEmpty() || ext.length() > 4 || ext.length() < 2) {
			try {
				MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();
				MimeType mtype = allTypes.forName(getContenttype());
				ext= mtype.getExtension();
			} catch (MimeTypeException e) {
				ext="bin";
			}
		}
		return ext;
	}
	
	public String getContenttype() {
		if (contenttype != null && !contenttype.isEmpty()) {
			return contenttype;
		}
		String lfilename = fileName;
		if (lfilename != null && !lfilename.isEmpty()) {
			lfilename = lfilename.replaceAll("[^\\d\\w]", "");
		}
		if (lfilename == null || lfilename.isEmpty()) {
			lfilename = "tmp";
		}
		String contenttype;
		try {
			Tika defaultTika = new Tika();
			contenttype = defaultTika.detect(lfilename);
		} catch (Exception e) {
			contenttype = null;
		}
		if (contenttype == null || contenttype.isEmpty()) {
			try {
				File temp = File.createTempFile(lfilename, "tmp");
				FileOutputStream fos = new FileOutputStream(temp);
				fos.write(getBytes());
				fos.flush();
				fos.close();
				try {
					Tika defaultTika = new Tika();
					contenttype = defaultTika.detect(temp.getName());
				}catch (Exception ee){
					//nothng to do
				}
				if (contenttype == null || contenttype.isEmpty()) {
					contenttype = java.nio.file.Files.probeContentType(temp.toPath());
				}
			} catch (Exception e) {
				//nothing to do
			}
		}
		return contenttype;
	}
}
