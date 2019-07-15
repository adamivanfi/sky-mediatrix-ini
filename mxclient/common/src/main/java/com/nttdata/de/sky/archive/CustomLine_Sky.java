/**
 *
 */
package com.nttdata.de.sky.archive;

import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.mediatrix.data.Attachment;
import de.ityx.mediatrix.data.Email;

import java.util.HashMap;
import java.util.Map;
/**
 * @author DHIFLM
 *
 */
public class CustomLine_Sky extends CustomLine {

	private final static String CONST_FIRST = "$";

	@Override
	public String toString() {
		return CONST_FIRST + getGenJob() + CONST_FIRST + getLayout4()
				+ CONST_FIRST + getVariation() + CONST_FIRST
				+ getVariationInfo() + CONST_FIRST + getVariationCount()
				+ CONST_FIRST + getCountryCode() + CONST_FIRST
				+ getCustomerId() + CONST_FIRST + getPageNumber() + CONST_FIRST
				+ getTotalPages() + CONST_FIRST + getKsPorto() + CONST_FIRST
				+ getForm() + CONST_FIRST + getAttachment1() + CONST_FIRST
				+ getAttachment2() + CONST_FIRST + getAttachment3()
				+ CONST_FIRST + getAttachment4() + "$" + getZipCode() + "$" + getCompany() + "$";
	}

	public static HashMap<String, Object> getParameter(Email email,
													   Map<String, String> metaMap, String documentId, boolean preview)
			throws Exception {
		HashMap<String, Object> result = new HashMap<String, Object>();
		CustomLine_Sky cl = new CustomLine_Sky();
		cl.setGenJob(documentId);
		cl.setCustomerId(metaMap.get(TagMatchDefinitions.CUSTOMER_ID));
		cl.setZipCode(metaMap.get(TagMatchDefinitions.CUSTOMER_ZIP_CODE));
		cl.setCompany(metaMap.get(TagMatchDefinitions.SBS_COMPANY));
		cl.setCountryCode(getCountry(metaMap
				.get(TagMatchDefinitions.CUSTOMER_COUNTRY)));
		cl.setCountryCode(CustomLine.LANGUAGE.DE);
		result.put(CustomLine.class.getName(), cl);
		for (String key : metaMap.keySet()) {
			result.put(key, metaMap.get(key));
		}
		if(SkyLogger.getClientLogger().isDebugEnabled()){
			SkyLogger.getClientLogger().debug("AttachmentDebugger:Body:"+email.getBody());
			for (Attachment att: email.getAttachments()){
				SkyLogger.getClientLogger().debug("AttachmentDebuggerATT:"+ att.getFilename()+" cd:"+att.getContentDisposition()+" ct:"+
						att.getContentType()+" cte:"+att.getContentTransferEncoding()+att.getId());
			}
		}

		ClientUtils.addEmbeddedAttachments(email, preview, result);
		return result;
	}

	private static CustomLine.LANGUAGE getCountry(
			String country) {
		if (country != null && country.toLowerCase().equals("österreich")) {
			return CustomLine.LANGUAGE.AT;
		} else {
			return CustomLine.LANGUAGE.DE;
		}
	}

}
