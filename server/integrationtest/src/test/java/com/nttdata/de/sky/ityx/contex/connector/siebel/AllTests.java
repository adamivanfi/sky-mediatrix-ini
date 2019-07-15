package com.nttdata.de.sky.ityx.contex.connector.siebel;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ CreateSiebelAssociationBeanTest.class,
		CreateSiebelContactBeanTest.class, ValidateExtractedDataBeanTest.class })
public class AllTests {

}
