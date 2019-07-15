#!/bin/bash

function LogWrite {
	"${1}" >> "${LOGFILE}"
}


function getBuildEnv {
 cat  << EOF
**********BUILD****************
Build Date:    ${BUILD_DATE}
BuildBy:       ${USERNAME}
**********VERSION**************
Mtrix_Version: ${MX_CORE_VER}
NTTLib:        ${NTT_LIB_VER}
AgenturMon:    ${MX_EXTLIB_TEAMMGMT_VER}

**********BUILDENV*************
JAVA Version:  ${JAVA_VER}
JAVA BIN:      ${JAVA_EXEC}
MAVEN Version: ${MVN_VER}
MAVEN Bin:     ${MVN_EXEC}
**********SCMINFO**************
GIT-BRANCH:    ${GIT_Branch}
GIT-REV:       ${GIT_REV}
**********PATH*****************
BasePath:      ${BASE_PATH}
Mediatrix:     ${MTRIX_CORE_PATH}
ExtLibs:       ${MX_EXTLIB_PATH}
TeamMgmgt:     ${MX_EXTLIB_TEAMMGMT_PATH}
InboxSortMod:  ${MX_EXTLIB_INBOXSORT_PATH}
SignedNTTLib:  ${NTT_LIB_SIGNED_PATH}
************************
EOF
}

FAILED_TASKS=""
function valid {
  if [ $? -eq 0 ]; then
     echo -e "\033[32m Build $1 OK \033[0m"
    else
     echo -e "\033[31m Build $1 FAILED \033[0m"
     FAILED_TASKS="${FAILED_TASKS} \033[31m Build $1 FAILED \033[0m"
     exit -1
  fi
}

function LibExistCheck {
if [ -e $2 ]; then
    echo -e "\033[32m $1 check ok: $2 \033[0m"
else
    echo -e "\033[31m $1 check FAILED: $2 \033[0m"
    exit -1
fi
}


# MAVEN
function InstallMavenIntoRepo {
	echo "$MVN_EXEC  install:install-file -Dfile=$(PathByOSType $4) -DgroupId=$1 -DartifactId=$2 -Dversion=$3 -Dpackaging=jar -DgeneratePom=true"
  $MVN_EXEC  install:install-file -Dfile=$(PathByOSType $4) -DgroupId=$1 -DartifactId=$2 -Dversion=$3 -Dpackaging=jar -DgeneratePom=true
  valid "$1/$2/$3"
}

function BuildMaven {
  echo "# Building: $1"
  pushd $1
  $MVN_EXEC install
  valid $1
  popd
}

function PathByOSType {
	if [ "$OSTYPE" == cygwin -o "$OSTYPE" == msys ] ; then
		echo $(cygpath -w $1)
	else
		echo $1
	fi
}

function diffFiles {
	# Cygwin:
	if [ "$OSTYPE" == cygwin ] ; then
		/cygdrive/c/Program\ Files\ \(x86\)/WinMerge/WinMergeU.exe /r /e /wl /dl "ITYX" /dr "NTT" /f WinMergeJavaFilter . $1

	# GIT-Bash, ...:
	elif [ "$OSTYPE" == msys ] ; then
		/c/Program\ Files\ \(x86\)/WinMerge/WinMergeU.exe /r /e /wl /dl "ITYX" /dr "NTT" /f WinMergeJavaFilter . $1

	elif [[ "$OSTYPE" == darwin17 ]]; then
		opendiff . $1 -merge .
    fi
}

function cdls {
    if [ "$OSTYPE" == cygwin ] ; then
        cd /cygdrive$1 ; ls -la
    else
        cd $1 ; ls -la
    fi
}
