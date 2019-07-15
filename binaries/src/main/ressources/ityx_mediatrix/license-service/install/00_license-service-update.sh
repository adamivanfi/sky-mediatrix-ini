#!/bin/bash

if [ ! "$ITYX_HOME" ]; then export ITYX_HOME=../; fi

if [ ! "$ITYX_CONF" ]; then export ITYX_CONF=$ITYX_HOME/conf; fi

#classpath
export CLASSPATH="../lib/*:../lib/customlib/*:"$ITYX_HOME"/libs/db/*"

java -cp "$CLASSPATH" -Dityx.home=$ITYX_HOME -Dityx.conf.dir=$ITYX_CONF de.ityx.cxlicense.service.LicenseServiceUpdate
