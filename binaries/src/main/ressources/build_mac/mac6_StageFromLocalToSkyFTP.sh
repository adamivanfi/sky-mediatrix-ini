#!/bin/bash
if [ -e mac0_Config.sh ]; then
    . mac0_Config.sh
else
    . binaries/src/main/ressources/build_mac/mac0_Config.sh
fi

function uploadToSky {
FTPHOST="sul.sky.de"
FTPUSER="SFTPdms_Mediatrix"
FTPPASSWD="kuChec3cut"
DATE=`date +%Y-%m-%d`
REMOTEDIR="deployments"
date
 if [ "$OSTYPE" == cygwin -o "$OSTYPE" == msys ] ; then
   echo "uploading using powershell $1"
   pushd "${WORKSPACE_PATH}/binaries/src/main/ressources/build_mac"
   powershell -file ./mac6_cygwinToSkySFTP.ps1 $1
   popd
else
sshpass -p $FTPPASSWD sftp -oBatchMode=no $FTPUSER@$FTPHOST <<END_SCRIPT
   cd $REMOTEDIR
   mput $1
   bye
END_SCRIPT
   say Upload complete
fi
}


pushd "${BUILD_PATH}"
if [ "${USER}" == "meinusch" ] ; then
    FILENAME="${BUILD_PATH}/../${USERNAME}_Mx4Sky.zip"
else
    if [ "${USER}" == "IVANFA" ] ; then
        FILENAME="../mediatrix_Mx4Sky.zip"
    else
        FILENAME="${BUILD_PATH}/../mediatrix_Mx4Sky.zip"
    fi
fi

if [ -e $FILENAME ]; then
    rm $FILENAME
fi
while getopts 'cfs' flag; do
  case "${flag}" in
    f) zip -r $FILENAME ./* ;
       uploadToSky $FILENAME;
       echo "fullBuild"
    ;;
    c) zip -r $FILENAME libs/*.txt libs/*/ntt* libs/clientlibs/mediatrix-agentursteuerung-*.jar tomcat/webapps/contex-ws/ --exclude libs/clientlibs/ntt-skydms-mxclient-languages*  --exclude tomcat/webapps/contex-ws/WEB-INF/lib/*;
       #zip -r $FILENAME tomcat/webapps/contex-ws/WEB-INF/lib/ntt* ;

       uploadToSky $FILENAME;
       echo "client build"
    ;;
    s) zip -r $FILENAME libs/*.txt libs/*/ntt-skydms-server*  tomcat/webapps/contex-ws/WEB-INF/classes tomcat/webapps/contex-ws/WEB-INF/lib/*SNAPSHOT.jar tomcat/webapps/contex-ws/WEB-INF/wsdl tomcat/webapps/contex-ws/WEB-INF/*.xml  ;
       #zip -r $FILENAME tomcat/webapps/contex-ws/WEB-INF/lib/ntt* ;
       uploadToSky $FILENAME;
       echo "server build"
    ;;
    *) error "Unexpected option ${flag}" ;;
  esac
done
  popd
