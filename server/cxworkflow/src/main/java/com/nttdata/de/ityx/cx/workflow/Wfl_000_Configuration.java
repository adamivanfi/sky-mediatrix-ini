package com.nttdata.de.ityx.cx.workflow;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.designer.IParameterMap;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Wfl_000_Configuration extends AbstractWflBean {

	private static final long			serialVersionUID	= 5484305478439698383L;

	private static long					birth				= 0;
	private static Map<String, Object>	store				= new HashMap<>();

	private static String				logPreafix			= "Wfl_000_Configuration";
	// private static Logger log=SkyLogger.getWflLogger();

	static {
		readConfig();
	}

	private static synchronized Map<String, Object> readConfig() {
		Logger log = SkyLogger.getWflLogger();
		long maxAge = 1000 * 60 * 60;
		if ((System.currentTimeMillis() - birth > maxAge)) {
			log.info(logPreafix + ":WFL_000_Config: Start load of Wfl-Properties: birth:" + birth + " maxage:" + maxAge);
			birth = System.currentTimeMillis();

			String wflconffile = System.getProperty("ityx.conf", "c:/mediatrix/conf") + "/" + "wflconfiguration.properties";

			Properties prop = new Properties();
			InputStream propertiesFileIS = null;
			try {
				propertiesFileIS = new FileInputStream(new File(wflconffile));
			} catch (FileNotFoundException e2) {
				log.info(logPreafix + ":WFL_000_Config: not possible to load Wfl-Configuration from AbsolutePath: " + wflconffile);
			}

			if (propertiesFileIS == null) {
				propertiesFileIS = Wfl_000_Configuration.class.getClassLoader().getResourceAsStream(wflconffile);
				log.info(logPreafix + ":WFL_000_Config: Tying using Classloader AbsolutePath: " + wflconffile);
			}
			if (propertiesFileIS == null) {
				propertiesFileIS = Wfl_000_Configuration.class.getClassLoader().getResourceAsStream("/wflconfiguration.properties");
				log.info(logPreafix + ":WFL_000_Config: Tying using Classloader RelativePath: " + wflconffile);
			}

			if (propertiesFileIS == null) {
				propertiesFileIS = Wfl_000_Configuration.class.getClassLoader().getResourceAsStream("/wflconfiguration.properties");
				log.info(logPreafix + ":WFL_000_Config: Tying using Classloader RelativePath2: " + wflconffile);
			}

			if (propertiesFileIS != null) {
				try {
					log.info(logPreafix + ":WFL_000_Config: Start loading of Wfl-Configuration from " + wflconffile);
					prop.load(propertiesFileIS);

				} catch (FileNotFoundException e1) {
					log.warn(logPreafix + ":WFL_000_Config: " + wflconffile + " not found. Loading Default Config", e1);
					store = loadDefaultProperties();
				} catch (IOException e1) {
					log.warn(logPreafix + ":WFL_000_Config: IO Problems during load of " + wflconffile + " . Loading Default Config", e1);
					store = loadDefaultProperties();
				}

				Enumeration<String> e = (Enumeration<String>) prop.propertyNames();
				store = new HashMap<>();
				while (e.hasMoreElements()) {
					String key = e.nextElement();
					store.put(key, convert(prop.getProperty(key)));
					// SkyLogger.getWflLogger().debug("Load Wfl-Properties:" +
					// key + ":" + prop.getProperty(key));
				}
				log.info(logPreafix + ":WFL_000_Config: Loaded Wfl-Configuration from " + wflconffile);

			} else {
				log.warn(logPreafix + ":WFL_000_ConfigError: " + wflconffile + " not found (IS null). Loading Default Config");
				store = loadDefaultProperties();
			}

		}
		return store;

	}

	private static Object convert(String property) {
		Object result = property;
		if (property.trim().matches("true")) {
			result = Boolean.TRUE;
		}
		if (property.trim().matches(TagMatchDefinitions.FALSE)) {
			result = Boolean.FALSE;
		}
		if (property.trim().matches("\\d+")) {
			result = Integer.parseInt(property.trim());
		}
		return result;
	}
@Override
	public void execute(IFlowObject flowObject) throws Exception {
		Logger log = SkyLogger.getWflLogger();

		Enumeration en=flowObject.getInputMapKeys();
		while (en.hasMoreElements()){
			String key= (String) en.nextElement();
			//log.debug("IMK: "+key );

			IParameterMap im = flowObject.getInputMap(key);
			/*Iterator imi=im.names();
			while (imi.hasNext()){
				String imo=(String) imi.next();
				log.debug("IMK_IMO: "+key+":" +imo+":"+im.getParameter(key));
			}*/

		}
		/*
		for (Map.Entry e: flowObject.getFlowParameter().entrySet()){
			log.debug("IMK_IM_FP: "+e.getKey()+":" +e.getValue());
		}*/

		Map<String, Object> map = readConfig();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			flowObject.put(entry.getKey(), entry.getValue());
		}
	}

	private static Map<String, Object> loadDefaultProperties() {
		Map<String, Object> lprop = new HashMap<>();
		// Sets configuration.
		Logger log = SkyLogger.getWflLogger();

		String ityx_environment_type = System.getProperty("ityx_environment_type");
		if (ityx_environment_type != null && ityx_environment_type.equalsIgnoreCase("integration")) {

			log.warn(logPreafix + ":WFL_000_ConfigError: Loading DefaultConfiguration for Integration Environment.");

			lprop.put("importFax", "D:\\mediatrix_data\\DMSftp\\Fax");
			lprop.put("importScanPB", "\\\\s-ng-ocr1-b.pfad.biz\\DMSftp\\test_int_letters_scanner_pbms");
			lprop.put("importScan", "\\\\s-ng-ocr1-b.pfad.biz\\DMSftp\\test_int_letters_scanner_pbms");
			lprop.put("workScan", "\\\\s-ng-ocr1-b.pfad.biz\\DMSftp\\test_int_letters_scanner_pbms\\tmp");
			lprop.put("workFax", "D:\\mediatrix_data\\tmp\\Fax");
			lprop.put("errorScan", "\\\\s-ng-ocr1-b.pfad.biz\\DMSftp\\test_int_letters_scanner_pbms\\err");
			lprop.put("destScan", "\\s-ng-ocr1-b.pfad.biz\\DMSftp\\test_int_letters_scanner_pbms\\dst");

			lprop.put("callbackRootCreateContact", "D:\\mediatrix_data\\callback_CreateContact");
			lprop.put("siebel_trigger", "C:\\mediatrix_data\\callback_CreateContact");
			lprop.put("DynamicCustomerData_WSDL", "http://10.96.111.22:13100/BusinessService/CustomerDataService");
			lprop.put("Siebel_WSDL", "http://10.96.111.22:13200/BusinessService/SiebelService");

			lprop.put("dbhost", "s-ng-ddmsx1.premiere.de");
			lprop.put("dbport", "1525");
			lprop.put("dbname", "MEXTST02");
			lprop.put("dbuser", "ITYX_MX_USER");
			lprop.put("dbpass", "Skei13pw");
			lprop.put("reportingEnabled", "true");

			lprop.put("MoveFileToLettershop_SrcDir", "D:\\mediatrix_data\\outbound");
			lprop.put("MoveFileToLettershop_DstDir", "D:\\mediatrix_data\\outbound\\letters");
			lprop.put("MoveFileToArchive_SrcDir", "D:\\mediatrix_data\\archive");
			lprop.put("MoveFileToArchive_FtpHost", "10.232.28.19");
			lprop.put("MoveFileToArchive_FtpPort", "22000");
			lprop.put("MoveFileToArchive_FtpUser", "dmsftpext");
			lprop.put("MoveFileToArchive_FtpPassword", "sPfdSm81%");

		} else if (ityx_environment_type != null && ityx_environment_type.equalsIgnoreCase("production")) {

			log.warn(logPreafix + ":WFL_000_ConfigError: Loading DefaultConfiguration for Production Environment.");

			lprop.put("importFax", "\\\\KMS-HH-DMSFAX1.pfad.biz\\Faxe\\mtrix");
			lprop.put("importScan", "D:\\mediatrix_data\\DMSftp\\scanner1");
			lprop.put("importScanPB", "\\\\s-ng-ocr1-b.pfad.biz\\DMSftp\\letters_scanner_pbms");
			lprop.put("workScan", "D:\\mediatrix_data\\tmp\\DMSftp\\scanner1");
			lprop.put("workFax", "D:\\mediatrix_data\\tmp\\Fax");
			lprop.put("errorScan", "D:\\mediatrix_data\\err\\DMSftp\\scanner1");
			lprop.put("destScan", "D:\\mediatrix_data\\dst\\DMSftp\\scanner1");

			lprop.put("callbackRootCreateContact", "D:\\mediatrix_data\\callback_CreateContact");
			lprop.put("siebel_trigger", "C:\\mediatrix_data\\callback_CreateContact");
			lprop.put("DynamicCustomerData_WSDL", "http://10.96.53.10:13100/BusinessService/CustomerDataService");
			lprop.put("Siebel_WSDL", "http://10.96.53.10:13200/BusinessService/SiebelService");
			lprop.put("dbhost", "memexdbprd.premiere.de");
			lprop.put("dbname", "MEXPROD2");
			lprop.put("dbuser", "ITYX_MX_USER");
			lprop.put("dbpass", "Skei13pw");
			lprop.put("dbport", "1525");


			lprop.put("MoveFileToLettershop_SrcDir", "\\\\s-ng-ctex1-b\\ContexShare\\mediatrix_data\\outbound");
			lprop.put("MoveFileToLettershop_DstDir", "\\\\p-ng-ls1.adpw.de\\ITYX");
			lprop.put("MoveFileToArchive_SrcDir", "\\\\s-ng-ctex1-b\\ContexShare\\mediatrix_data\\archive");
			lprop.put("MoveFileToArchive_FtpHost", "10.232.28.19");
			lprop.put("MoveFileToArchive_FtpPort", "22000");
			lprop.put("MoveFileToArchive_FtpUser", "pdmsftpext");
			lprop.put("MoveFileToArchive_FtpPassword", "PsfdSm53&");
		} else {

			log.error(logPreafix + ":WFL_000_ConfigError: Not possible to identify the Environment, defaultconfiguration cannot be loaded.");

		}
		// Commons
		lprop.put("Master", "sky");
		lprop.put("docStateImported", "300_Classification");
		lprop.put("docStateWaitForContactId", "500_CRM_Activity");
		lprop.put("docStateWaitForCallback", "550_CRM_Callback");
		lprop.put("docStateMediatrixWrite", "600_MXInjection");
		lprop.put("docStateReturnEmail", "MXMailReturn");
		lprop.put("docStateWaitForOCR", "220_Preprocessing_OCR");
		lprop.put("docStateManualValidation", "661_MX_ManualIndexing");
		lprop.put("docStateManualValidationCRM", "670_MX_ManualIndexing_CRM_Data");
		lprop.put("docStateAssociate", "830_Associate");
		lprop.put("docStateOutbound", "820_Outbound");
		lprop.put("docStateArchiv", "810_Archiv");
		lprop.put("docStateExtraction", "400_CustomerIndexing");
		lprop.put("docStateImportedE", "ImportedE");

		lprop.put("customerFound", Boolean.FALSE);
		lprop.put("ADDRS_FM_MAXWORDS", "650");
		lprop.put("DynamicCustomerData_Enabled", Boolean.TRUE);
		lprop.put("Siebel_Enabled", Boolean.TRUE);
		lprop.put("reportingEnabled", "true");
		return lprop;
	}

}
