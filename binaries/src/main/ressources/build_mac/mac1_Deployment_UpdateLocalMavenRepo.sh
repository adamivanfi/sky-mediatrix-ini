#!/bin/bash
if [ -e mac0_Config.sh ]; then
    . mac0_Config.sh
else
    . binaries/src/main/ressources/build_mac/mac0_Config.sh
fi
#echo "#contexplain${MX_CORE_CX_VER}\.jar#"
InstallMavenIntoRepo "de.ityx"      "contexplain"                       "${MX_CORE_CX_VER}"                 "${MTRIX_CORE_LIBS_MX_PATH}/contexplain-${MX_CORE_CX_VER}.jar"
InstallMavenIntoRepo "de.ityx"      "contex-proguard"                   "${MX_CORE_CX_VER}"                 "${MTRIX_CORE_LIBS_MX_PATH}/contex-proguard-${MX_CORE_CX_VER}.jar"
InstallMavenIntoRepo "de.ityx"      "contex-service"                    "${MX_CORE_CX_SERVICE_VER}"         "${MTRIX_CORE_LIBS_MX_PATH}/contex-service-${MX_CORE_CX_SERVICE_VER}.jar"
InstallMavenIntoRepo "de.ityx"      "contex-base"                    "${MX_CORE_CX_SERVICE_VER}"         "${MTRIX_CORE_LIBS_MX_PATH}/contex-base-${MX_CORE_CX_SERVICE_VER}.jar"

InstallMavenIntoRepo "de.ityx"      "contex-security-core"                    "${MX_CORE_CX_SECURITY_VER}"         "${MTRIX_CORE_LIBS_MX_PATH}/contex-security-core-${MX_CORE_CX_SECURITY_VER}.jar"
InstallMavenIntoRepo "de.ityx"      "license-api"                    "${MX_EXTLIB_LICAPI_VER}"         "${MTRIX_CORE_LIBS_MX_PATH}/license-api-${MX_EXTLIB_LICAPI_VER}.jar"
InstallMavenIntoRepo "de.ityx"      "license-client"                    "${MX_EXTLIB_LICCLIENT_VER}"         "${MTRIX_CORE_LIBS_MX_PATH}/license-client-${MX_EXTLIB_LICCLIENT_VER}.jar"

InstallMavenIntoRepo "de.ityx"      "lingua"                    "${MX_EXTLIB_LINGUA_VER}"         "${MTRIX_CORE_LIBS_MX_PATH}/lingua-${MX_EXTLIB_LINGUA_VER}.jar"


InstallMavenIntoRepo "de.ityx"      "essentials"                        "${MX_EXTLIB_ESSENTIALS_VER}"       "${MTRIX_CORE_LIBS_MX_PATH}/essentials-${MX_EXTLIB_ESSENTIALS_VER}.jar"
InstallMavenIntoRepo "de.ityx"      "exceptionhandler"                  "${MX_EXTLIB_EXCEPTIONHANDLER_VER}" "${MTRIX_CORE_LIBS_MX_PATH}/exceptionhandler-${MX_EXTLIB_EXCEPTIONHANDLER_VER}.jar"
InstallMavenIntoRepo "de.ityx"      "layout-resources"                  "${MX_EXTLIB_LAYOUTRES_VER}"        "${MTRIX_CORE_LIBS_MX_PATH}/layout-resources-${MX_EXTLIB_LAYOUTRES_VER}.jar "
InstallMavenIntoRepo "de.ityx"      "layout-api"                        "${MX_EXTLIB_LAYOUTAPI_VER}"        "${MTRIX_CORE_LIBS_MX_PATH}/layout-api-${MX_EXTLIB_LAYOUTAPI_VER}.jar"
InstallMavenIntoRepo "de.ityx"      "mediatrix-kernel"                  "${MX_CORE_KERNEL_VER}"             "${MTRIX_CORE_LIBS_MX_PATH}/mediatrix-kernel-${MX_CORE_KERNEL_VER}.jar"
InstallMavenIntoRepo "de.ityx"      "mediatrix-server"                  "${MX_CORE_KERNEL_VER}"             "${MTRIX_CORE_LIBS_MX_PATH}/mediatrix-server-${MX_CORE_KERNEL_VER}.jar"
InstallMavenIntoRepo "de.ityx"      "mediatrix-services"                "${MX_CORE_KERNEL_VER}"             "${MTRIX_CORE_LIBS_MX_PATH}/mediatrix-services-${MX_CORE_KERNEL_VER}.jar"

InstallMavenIntoRepo "de.ityx"      "ntlangdetect"                      "${MX_EXTLIB_NTLANGDETECT_VER}"     "${MTRIX_CORE_LIBS_MX_PATH}/ntlangdetect-${MX_EXTLIB_NTLANGDETECT_VER}.jar"
InstallMavenIntoRepo "de.ityx"      "license-api"                      "${MX_EXTLIB_LICAPI_VER}"     "${MTRIX_CORE_LIBS_MX_PATH}/license-api-${MX_EXTLIB_LICAPI_VER}.jar"


InstallMavenIntoRepo "de.ityx"      "mediatrix-agentursteuerung"        "${MX_EXTLIB_TEAMMGMT_VER}"         "${MX_EXTLIB_TEAMMGMT_PATH}/mediatrix-agentursteuerung-${MX_EXTLIB_TEAMMGMT_VER}.jar"
InstallMavenIntoRepo "de.ityx.discover"      "discover-erms-messages"        "${MX_EXTLIB_DISCOVERY_VER}"         "${MX_EXTLIB_TEAMMGMT_PATH}/discover-erms-messages-${MX_EXTLIB_DISCOVERY_VER}.jar"
InstallMavenIntoRepo "de.ityx.discover"      "discover-erms-processors"        "${MX_EXTLIB_DISCOVERY_VER}"         "${MX_EXTLIB_TEAMMGMT_PATH}/discover-erms-processors-${MX_EXTLIB_DISCOVERY_VER}.jar"
InstallMavenIntoRepo "de.ityx.discover"      "discover-server"        "${MX_EXTLIB_DISCOVERY_VER}"         "${MX_EXTLIB_TEAMMGMT_PATH}/discover-server-${MX_EXTLIB_DISCOVERY_VER}-jar-with-dependencies.jar"

#InstallMavenIntoRepo "de.ityx"      "customer-mx-extension-clientevents"  "${MX_EXTLIB_CLIENTEVENTS_VER}"     "${MX_EXTLIB_CLIENTEVENTS_PATH}/customer-mx-extension-clientevents-${MX_EXTLIB_CLIENTEVENTS_VER}.jar"
InstallMavenIntoRepo "de.ityx"      "extension-mailinboxsorter"         "${MX_EXTLIB_INBOXSORT_VER}"        "${MX_EXTLIB_INBOXSORT_PATH}/extension-mailinboxsorter-${MX_EXTLIB_INBOXSORT_VER}.jar"
InstallMavenIntoRepo "de.ityx.customer.commons" "aspose-itext-pdf-convertor" "${MX_EXTLIB_ASPOSEITEX_VER}"  "${MX_EXTLIB_ASPOSE_PATH}/customlibs/aspose-itext-pdf-convertor-${MX_EXTLIB_ASPOSEITEX_VER}.jar"
InstallMavenIntoRepo "com.itextpdf" "itext-parent"                      "${MX_EXTLIB_ITEXTPDF_VER}"         "${MX_EXTLIB_ASPOSE_PATH}/customlibs/itextpdf-${MX_EXTLIB_ITEXTPDF_VER}.jar"
InstallMavenIntoRepo "com.itextpdf.tool" "xmlworker"                      "${MX_EXTLIB_ITEXTPDF_VER}"         "${MX_EXTLIB_ASPOSE_PATH}/customlibs/xmlworker-${MX_EXTLIB_ITEXTXMLW_VER}.jar"


#InstallMavenIntoRepo "com.aspose"   "aspose-cells"                      "${MX_EXTLIB_ASPOSECELLS}"         "${MX_EXTLIB_ASPOSE_PATH}/customlibs/aspose-cells-${MX_EXTLIB_ASPOSECELLS}.jar"
InstallMavenIntoRepo "com.aspose"   "aspose-imaging"                    "${MX_EXTLIB_ASPOSEIMG}"           "${MX_EXTLIB_ASPOSE_PATH}/customlibs/aspose-imaging-${MX_EXTLIB_ASPOSEIMG}-jdk16.jar"
InstallMavenIntoRepo "com.aspose"   "aspose-words"                      "${MX_EXTLIB_ASPOSWORDS}"          "${MX_EXTLIB_ASPOSE_PATH}/customlibs/aspose-words-${MX_EXTLIB_ASPOSWORDS}.jar"
InstallMavenIntoRepo "com.aspose"   "aspose-pdf"                        "${MX_EXTLIB_ASPOSEPDF}"           "${MX_EXTLIB_ASPOSE_PATH}/customlibs/aspose-pdf-${MX_EXTLIB_ASPOSEPDF}.jar"


InstallMavenIntoRepo "org.elasticsearch" "elasticsearch"    "${EXTLIB_ELASTICSEARCH_VER}"           "${MTRIX_CORE_PATH}/libs/common/elasticsearch-${EXTLIB_ELASTICSEARCH_VER}.jar"

InstallMavenIntoRepo "org.hibernate" "hibernate-commons-annotations"    "${EXTLIB_HIBERNATE_VER}"           "${MTRIX_CORE_PATH}/libs/common/hibernate-commons-annotations-${EXTLIB_HIBERNATE_VER}.jar"
InstallMavenIntoRepo "com.ibm"       "bridge2java"                      "${EXTLIB_BRIGE2JAVA_VER}"          "${MTRIX_CORE_PATH}/libs/common/bridge2java-${EXTLIB_BRIGE2JAVA_VER}.jar"
InstallMavenIntoRepo "com.jacob"     "jacob"                            "${EXTLIB_JACOB_VER}"               "${NTT_LIB_COMMONS_PATH}/clientlibs/jacob-${EXTLIB_JACOB_VER}.jar"
#bei ITyX nur jacob 12 deplyot
InstallMavenIntoRepo "com.jacob"     "jacob"                            "1.12"               "${NTT_LIB_COMMONS_PATH}/clientlibs/jacob-${EXTLIB_JACOB_VER}.jar"

InstallMavenIntoRepo "javassist"     "javassist"                        "${EXTLIB_JAVASSISTS_VER}"          "${MTRIX_CORE_PATH}/libs/common/javassist-${EXTLIB_JAVASSISTS_VER}.jar"
InstallMavenIntoRepo "javax.persistence" "persistence-api"              "${EXTLIB_PERSISTENCEAPI_VER}"      "${MTRIX_CORE_PATH}/libs/common/jpa-api-${EXTLIB_PERSISTENCEAPI_VER}.jar"
InstallMavenIntoRepo "geronimo-spec" "geronimo-spec-jta"                "${EXTLIB_GERONIMOJTA_VER}"         "${MTRIX_CORE_PATH}/libs/common/geronimo-jta_${EXTLIB_GERONIMOJTA_VER}.jar"

InstallMavenIntoRepo "com.oracle"    "ojdbc7"                           "${EXTLIB_JDBC_ORACLE_VER}"         "${MX_EXTLIB_PATH}/ext_database/ojdbc7-${EXTLIB_JDBC_ORACLE_VER}.jar"
InstallMavenIntoRepo "net.sourceforge.jchardet" "jchardet"              "${EXTLIB_JCHARDET_VER}"            "${NTT_LIB_COMMONS_PATH}/customlibs/chardet.jar"
#commons-imaging-1.0-20150224.194352-60.jar
InstallMavenIntoRepo "org.apache.commons" "commons-imaging"             "${EXTLIB_COMMONSIMG_VER}"          "${NTT_LIB_COMMONS_PATH}/customlibs/commons-imaging-${EXTLIB_COMMONSIMG_VER}.jar"
InstallMavenIntoRepo "com.sun.media" "jai_imageio"             "${EXTLIB_IMAGEIO_VER}"          "${MTRIX_CORE_PATH}/libs/common/jai_imageio-${EXTLIB_IMAGEIO_VER}.jar"

#InstallMavenIntoRepo "de.ityx" "aspose_converter_service" "2.0.3-SNAPSHOT"  "${MX_EXTLIB_ASPOSE_PATH}/customlibs/aspose_converter_service-2.0.3.jar"

if [ -n "$FAILED_TASKS" ] ; then
    echo "Failed Tasks: $FAILED_TASKS"
else
    echo "Build Completed"
fi
