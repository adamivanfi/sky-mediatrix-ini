package com.nttdata.de.sky.ityx.mediatrix.businessrules;

import com.nttdata.de.sky.archive.AbstractArchiveMetaData;
import com.nttdata.de.sky.archive.ArchiveMetaDataFactory;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.fail;

public class ArchiveMetaDataTest {
        @Ignore
	@Test
	public void testValidation() {
		AbstractArchiveMetaData instance;
		try {
			instance = ArchiveMetaDataFactory.getInstance("com.nttdata.de.sky.ityx.mediatrix.businessrules.archiving.MDocumentArchiveMetaData");
			assert instance.validateArchiveXML(getClass().getResourceAsStream("/archive.xml"));
			assert !instance.validateArchiveXML(getClass().getResourceAsStream("/noarchive.xml"));
		} catch (Exception e) {
			fail(e.getMessage());
		}

	}
}
