#!/bin/bash

if [ ! "$ITYX_HOME" ]; then export ITYX_HOME=../; fi

if [ ! "$ITYX_CONF" ]; then export ITYX_CONF=../conf; fi

#classpath
export CLASSPATH="lib/*:customlib/*:"$ITYX_HOME"/libs/db/*"

java -cp "$CLASSPATH" -Dityx.home="$ITYX_HOME" -Dityx.conf="$ITYX_CONF" -Dcom.mchange.v2.c3p0.cfg.xml="$ITYX_CONF/c3p0-config.xml" de.ityx.cxlicense.service.LicenseService
