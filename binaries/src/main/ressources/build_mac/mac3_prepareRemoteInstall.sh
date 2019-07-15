#!/bin/bash
if [ -e mac0_Config.sh ]; then
    . mac0_Config.sh
else
    . binaries/src/main/ressources/build_mac/mac0_Config.sh
fi


LOGFILE="${BUILD_PATH}/libs/VersionInfo_${BUILD_TS}.txt"
mkdir -p "${BUILD_PATH}/libs" "${BUILD_PATH}/logs" "${BUILD_PATH}/tomcat/webapps" "${BUILD_PATH}/license-service/lib" "${BUILD_PATH}/license-service/nativelib/" "${BUILD_PATH}/activemq/lib/" "${BUILD_PATH}/es/lib/"
rsync -av --delete --exclude */aspose* --exclude */axis-wsdl4j* --exclude */itextpdf* --exclude db/* --exclude */mediatrix-twitter* --exclude */*facebook* --exclude mediatrix/mediatrix-contex-* --exclude mediatrix/mediatrix-phonecontact-* --exclude mediatrix/lib-opennlp-models* --exclude mediatrix/languages-2*  ${MTRIX_CORE_PATH}/libs/ ${BUILD_PATH}/libs/

cp -Rp ${MX_EXTLIB_PATH}/ext_common/ ${BUILD_PATH}/libs/
cp -Rp ${MX_EXTLIB_ASPOSE_PATH}/ ${BUILD_PATH}/libs/
rsync -av --delete ${MX_EXTLIB_PATH}/ext_database/ ${BUILD_PATH}/libs/db/

cp -Rp ${MX_EXTLIB_CLIENTEVENTS_PATH}/*.jar ${BUILD_PATH}/libs/customlibs/
rm ${BUILD_PATH}/libs/customlibs/customer-mx-extension-clientevents*

cp -Rp ${NTT_LIB_SIGNED_PATH}/ntt-*-${NTT_LIB_VER}.jar ${BUILD_PATH}/libs/clientlibs/
rm ${BUILD_PATH}/libs/clientlibs/ntt-skydms-mxclient-extlibs-*

rm ${BUILD_PATH}/libs/clientlibs/ntt-skydms-mxclient-ityx-clientevents-extensions-*

#Extensions
cp -Rp ${MX_EXTLIB_TEAMMGMT_PATH}/mediatrix-agentursteuerung-${MX_EXTLIB_TEAMMGMT_VER}.jar ${BUILD_PATH}/libs/clientlibs/
cp -Rp ${MX_EXTLIB_TEAMMGMT_PATH}/discover-erms-processors-${MX_EXTLIB_DISCOVERY_VER}.jar ${BUILD_PATH}/libs/customlibs/
cp -Rp ${MX_EXTLIB_TEAMMGMT_PATH}/discover-server-${MX_EXTLIB_DISCOVERY_VER}-jar-with-dependencies.jar ${BUILD_PATH}/libs/customlibs/
cp -Rp ${MX_EXTLIB_TEAMMGMT_PATH}/discover-erms-messages-${MX_EXTLIB_DISCOVERY_VER}.jar ${BUILD_PATH}/libs/common/

#cp -Rp ${MX_EXTLIB_INBOXSORT_PATH}/*-${MX_EXTLIB_INBOXSORT_VER}.jar ${BUILD_PATH}/libs/clientlibs/

rsync -av --delete ${MTRIX_CORE_PATH}/license-service/lib/ ${BUILD_PATH}/license-service/lib/
rsync -av --delete ${MTRIX_CORE_PATH}/license-service/nativelib/ ${BUILD_PATH}/license-service/nativelib/
rsync -av --delete ${MTRIX_CORE_PATH}/es/lib/ ${BUILD_PATH}/es/lib/
rsync -av --delete  ${MTRIX_CORE_PATH}/activemq/lib/ ${BUILD_PATH}/activemq/lib/

cp -Rp ${WORKSPACE_PATH}/server/cxworkflow/target/*.jar ${BUILD_PATH}/libs/customlibs/
cp -Rp ${WORKSPACE_PATH}/server/connector/target/*.jar ${BUILD_PATH}/libs/customlibs/
cp -Rp ${WORKSPACE_PATH}/server/mxbusinessrules/target/*.jar ${BUILD_PATH}/libs/customlibs/
cp -Rp ${WORKSPACE_PATH}/server/contex-ws/target/*.jar  ${BUILD_PATH}/libs/customlibs/
cp -Rp ${WORKSPACE_PATH}/server/sharedservices/target/*.jar  ${BUILD_PATH}/libs/customlibs/
cp -Rp ${WORKSPACE_PATH}/server/contex-ws/target/*.war  ${BUILD_PATH}/tomcat/webapps/contex-ws.war
rsync -av --delete  ${WORKSPACE_PATH}/server/contex-ws/target/ntt-skydms-server-contex-ws-${NTT_LIB_VER}/  ${BUILD_PATH}/tomcat/webapps/contex-ws/

LibExistCheck "Mediatrix ${Mtrix_Ver}"                       ${BUILD_PATH}/libs/mediatrix/mediatrix-kernel-${MX_CORE_KERNEL_VER}.jar
LibExistCheck "NTTLibS/cxworkflow ${NTTLib_Ver}"             ${BUILD_PATH}/libs/customlibs/ntt-skydms-server-cxworkflow-${NTT_LIB_VER}.jar
LibExistCheck "NTTLibS/connector ${NTTLib_Ver}"              ${BUILD_PATH}/libs/customlibs/ntt-skydms-server-connector-${NTT_LIB_VER}.jar
LibExistCheck "NTTLibS/mxbusinessrules ${NTTLib_Ver}"        ${BUILD_PATH}/libs/customlibs/ntt-skydms-server-mxbusinessrules-${NTT_LIB_VER}.jar
LibExistCheck "NTTLibC/common ${NTTLib_Ver}"                 ${BUILD_PATH}/libs/clientlibs/ntt-skydms-mxclient-common-${NTT_LIB_VER}.jar
LibExistCheck "NTTLibC/businessrules-client ${NTTLib_Ver}"   ${BUILD_PATH}/libs/clientlibs/ntt-skydms-mxclient-mxbusinessrules-client-${NTT_LIB_VER}.jar
LibExistCheck "NTTLibC/businessrules-delegate ${NTTLib_Ver}" ${BUILD_PATH}/libs/clientlibs/ntt-skydms-mxclient-mxbusinessrules-delegate-${NTT_LIB_VER}.jar
LibExistCheck "NTTLibC/languages        ${NTTLib_Ver}"       ${BUILD_PATH}/libs/clientlibs/ntt-skydms-mxclient-languages-${NTT_LIB_VER}.jar
LibExistCheck "NTTLibC/outbound ${NTTLib_Ver}"               ${BUILD_PATH}/libs/clientlibs/ntt-skydms-mxclient-outbound-${NTT_LIB_VER}.jar
LibExistCheck "NTTLibS/contex-ws/MX ${NTTLib_Ver}"           ${BUILD_PATH}/tomcat/webapps/contex-ws.war
LibExistCheck "NTTLibS/contex-ws/CX ${NTTLib_Ver}"           ${BUILD_PATH}/libs/customlibs/ntt-skydms-server-contex-ws-${NTT_LIB_VER}.jar
#LibExistCheck "MX_CoreExt_MailInboxSorter ${MX_EXTLIB_INBOXSORT_VER}" ${BUILD_PATH}/libs/clientlibs/extension-mailinboxsorter-${MX_EXTLIB_INBOXSORT_VER}.jar
LibExistCheck "MX_CoreExt_Agency ${MX_EXTLIB_TEAMMGMT_PATH}" ${BUILD_PATH}/libs/clientlibs/mediatrix-agentursteuerung-${MX_EXTLIB_TEAMMGMT_VER}.jar

echo
LogWrite $(echo getBuildEnv)
echo "Bulid ${BUILD_TS} finished on `date`"
