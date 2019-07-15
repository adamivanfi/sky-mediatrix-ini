#!/bin/bash

set WORK_DIR=%~dp0

if [ ! "$ITYX_CONF" ]; then export ITYX_CONF='../conf'; fi

if [ ! "$ITYX_LIB" ]; then export ITYX_LIB=$HOME'/.ityx'; fi

#classpath
export CLASSPATH="../lib/*:../customlib/*:"$ITYX_HOME"/libs/db/*"

java -cp "$CLASSPATH" -Djava.library.path=../nativelib -Dityx.conf=$ITYX_CONF de.ityx.cxlicense.service.LicenseService
