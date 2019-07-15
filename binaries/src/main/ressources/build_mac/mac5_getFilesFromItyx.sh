#!/bin/bash
if [ -e mac0_Config.sh ]; then
    . mac0_Config.sh
else
    . binaries/src/main/ressources/build_mac/mac0_Config.sh
fi

HOST='cirquent@ftp.ityx.de'	# change the ipaddress accordingly
USER='cirquent'	# username also change
PASSWD='Xq9oRcGAzZD3'	# password also change

DATE=`date +%Y-%m-%d`
REMOTEDIR="/data/deploy-$DATE"
#REMOTEDIR="/data/deploy-2018-06-07"
EXTRA=''

fflag=false
while getopts 'f' flag; do
  case "${flag}" in
    f) fflag=true ; EXTRA='mget ntt-skydms-mxclient-extlibs-${NTT_LIB_VER}.jar
                           mget ntt-skydms-mxclient-languages-${NTT_LIB_VER}.jar
                           mget ntt-skydms-mxclient-ityx-clientevents-extensions-${NTT_LIB_VER}.jar'
    ;;  ## make full deployment instead of ntt* libs
    *) error "Unexpected option ${flag}" ;;
  esac
done

if [ "$OSTYPE" == darwin17 ] ; then
#gregors mac
ftp -n $HOST <<END_SCRIPT
quote USER $USER
quote PASS $PASSWD
bin
prompt off
cd $REMOTEDIR
lcd $NTT_LIB_SIGNED_PATH
ls
mget ntt-skydms-mxclient-common-${NTT_LIB_VER}.jar
mget ntt-skydms-mxclient-mxbusinessrules-client-${NTT_LIB_VER}.jar
mget ntt-skydms-mxclient-mxbusinessrules-delegate-${NTT_LIB_VER}.jar
mget ntt-skydms-mxclient-outbound-${NTT_LIB_VER}.jar
${EXTRA}
ls
;

bye
END_SCRIPT

else
#windows
# ncftpget -f $WORKSPACE_PATH/binaries/src/main/ressources/build_mac/ityxftplogin  $NTT_LIB_SIGNED_PATH $REMOTEDIR/ntt-skydms-mxclient-common-${NTT_LIB_VER}.jar \
#  $REMOTEDIR/ntt-skydms-mxclient-mxbusinessrules-client-${NTT_LIB_VER}.jar \
#  $REMOTEDIR/ntt-skydms-mxclient-mxbusinessrules-delegate-${NTT_LIB_VER}.jar \
#  $REMOTEDIR/ntt-skydms-mxclient-outbound-${NTT_LIB_VER}.jar \
#  $REMOTEDIR/ntt-skydms-mxclient-mxclient-languages-${NTT_LIB_VER}.jar \
#  $REMOTEDIR/ntt-skydms-mxclient-mxclient-extlibs-${NTT_LIB_VER}.jar \
#  $REMOTEDIR/ntt-skydms-mxclient-mxclient-ityx-clientevents-extensions-${NTT_LIB_VER}.jar

sftp $HOST <<END_SCRIPT
#quote USER $USER
#quote PASS $PASSWD
bin
prompt off
cd $REMOTEDIR
lcd $NTT_LIB_SIGNED_PATH
ls
mget ntt-skydms-mxclient-common-${NTT_LIB_VER}.jar
mget ntt-skydms-mxclient-mxbusinessrules-client-${NTT_LIB_VER}.jar
mget ntt-skydms-mxclient-mxbusinessrules-delegate-${NTT_LIB_VER}.jar
mget ntt-skydms-mxclient-outbound-${NTT_LIB_VER}.jar
${EXTRA}
ls


bye
END_SCRIPT

fi
