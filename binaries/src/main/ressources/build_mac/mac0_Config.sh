#!/bin/bash
USERNAME=`id -nu`
MACHINE=$HOSTNAME
BUILD_TS=`date '+%Y%m%d_%H%M'`
BUILD_DATE=`date '+%Y-%m-%d %H:%M'`
BASE_PATH=~/Development/Sky
WORKSPACE_PATH=${BASE_PATH}/sky_dms

   #cd  $(readlink "$WORKSPACE_PATH")
if [ "$OSTYPE" == cygwin -o "$OSTYPE" == msys ] ; then
   LOWER_CASE_USERNAME=${USERNAME,,}
   #WORKSPACE_PATH=/Users/${USERNAME}/Development/Sky/sky_dms
   WORKSPACE_PATH=$(readlink -f $WORKSPACE_PATH)
      #WORKSPACE_PATH="/c/Users/${LOWER_CASE_USERNAME}/Development/SKY/sky_dms"
   #export http_proxy="http://141.77.17.155:8080/"
   export http_proxy="https://cugit.ityx.de/ntt-data/customer-sky.git/"
   export https_proxy=$http_proxy
   export ftp_proxy=$http_proxy
   export rsync_proxy=$http_proxy
   export HTTP_PROXY=$http_proxy
   export HTTPS_PROXY=$http_proxy
   export FTP_PROXY=$http_proxy
   # Änderung wegen neue VPN-SSL-Verbindung bei NTT-Data:
   # export no_proxy="localhost,127.0.0.1,localaddress,.de.softlab.net,.ntt-data.com,.nttdata-emea.com"
   export no_proxy="*"
else
#mac
  export JAVA_HOME=`/usr/libexec/java_home -v 1.7`
  BASE_PATH=~/Development/sky/dms/swenv
  WORKSPACE_PATH=${BASE_PATH}/sky_dms
fi

if ! [ -e $WORKSPACE_PATH ]; then
    echo "WorkspacePath empty, setting to current dir."
    WORKSPACE_PATH=`pwd`
fi

cd $WORKSPACE_PATH
BUILD_PATH=${WORKSPACE_PATH}/binaries/target/main/CurrentBuild
SCRIPT_PATH=${WORKSPACE_PATH}/binaries/src/main/ressources/build_mac
#echo $WORKSPACE_PATH

if [ -e $WORKSPACE_PATH/skybuild.sh ]; then
    echo "Wpath:$WORKSPACE_PATH"
else
    echo "WorkspacePath empty, cannot continue:$WORKSPACE_PATH"
    exit 1
fi

source ${WORKSPACE_PATH}/mxclient/ClientLibVersions.properties
source ${WORKSPACE_PATH}/server/ServerLibVersions.properties
source ${SCRIPT_PATH}/mac9_Utils.sh

# INITIALIZATION
MX_EXTLIB_PATH="${WORKSPACE_PATH}/binaries/src/main/ressources"
MX_EXTLIB_TEAMMGMT_PATH="${MX_EXTLIB_PATH}/ityx_teammanagement"
MX_EXTLIB_INBOXSORT_PATH="${MX_EXTLIB_PATH}/ityx_inboxsorter"
MX_EXTLIB_ASPOSE_PATH="${MX_EXTLIB_PATH}/ityx_aspose"
NTT_LIB_COMMONS_PATH="${MX_EXTLIB_PATH}/ext_common"
NTT_LIB_SIGNED_PATH="${MX_EXTLIB_PATH}/ext_nttsigned"
MTRIX_CORE_PATH="${MX_EXTLIB_PATH}/ityx_mediatrix"
MTRIX_CORE_LIBS_MX_PATH="${MTRIX_CORE_PATH}/libs/mediatrix"
MX_EXTLIB_CLIENTEVENTS_PATH="${MX_EXTLIB_PATH}/ext_clientevents"

#Machine Specific Configuration
GIT_EXEC="git"
GIT_Branch=`$GIT_EXEC rev-parse --abbrev-ref HEAD`
GIT_REV=`$GIT_EXEC rev-parse HEAD`

MVN_EXEC="mvn"
MVN_VER=`$MVN_EXEC -v`

JAVA_EXEC="${JAVA_HOME}/bin/java"
JAVA_VER=`"$JAVA_EXEC" -version 2>&1| head -1 | cut -d '"' -f2`

export KEYSTORE_PATH=$(PathByOSType  ${WORKSPACE_PATH})
export KEYSTORE_NAME="ntt.jks"
export KEYSTORE_ALIAS="ntt"
export KEYSTORE_PASSWORD="nttdata"
#
getBuildEnv
