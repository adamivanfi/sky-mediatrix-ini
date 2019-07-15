package com.nttdata.de.ityx.cx.sky.archiving;

import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;

public class CDocumentMandateArchiveMetaData extends CDocumentArchiveMetaData {
        public CDocumentMandateArchiveMetaData() throws Exception {
           super();
           optionalArchiveTagnames.add(TagMatchDefinitions.SEPA_MANDATE_NUMBER);
           //optionalArchiveTagnames.add(TagMatchDefinitions.IBAN);
           optionalArchiveTagnames.add(TagMatchDefinitions.BIC);
           optionalArchiveTagnames.add(TagMatchDefinitions.SEPA_SIGNATURE_FLAG);
           optionalArchiveTagnames.add(TagMatchDefinitions.SEPA_SIGNATURE_DATE);
           archiveAttributes.addAll(optionalArchiveTagnames);
	}
}
