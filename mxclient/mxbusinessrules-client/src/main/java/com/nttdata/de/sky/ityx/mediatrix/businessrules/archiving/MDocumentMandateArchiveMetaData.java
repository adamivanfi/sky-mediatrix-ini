package com.nttdata.de.sky.ityx.mediatrix.businessrules.archiving;


import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;

/**
 * Handles archive metadata of SEPA mandates.
 * 
 * @author DHIFLM
 * 
 */
public class MDocumentMandateArchiveMetaData extends MDocumentArchiveMetaData {

	public MDocumentMandateArchiveMetaData() throws Exception {
           super();
           optionalArchiveTagnames.add(TagMatchDefinitions.SEPA_MANDATE_NUMBER);
           //optionalArchiveTagnames.add(TagMatchDefinitions.IBAN);
           optionalArchiveTagnames.add(TagMatchDefinitions.BIC);
           optionalArchiveTagnames.add(TagMatchDefinitions.SEPA_SIGNATURE_FLAG);
           optionalArchiveTagnames.add(TagMatchDefinitions.SEPA_SIGNATURE_DATE);
           archiveAttributes.addAll(optionalArchiveTagnames);
	}
}
