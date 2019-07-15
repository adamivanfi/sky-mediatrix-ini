package com.nttdata.de.lib.logging;

import de.ityx.base.tools.logger.RemoteLogger;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class SmartLogger extends RemoteLogger {
	private static Map<Integer, SmartLogger> loggerpool= Collections.synchronizedMap(new LinkedHashMap<Integer, SmartLogger>());

	public static synchronized SmartLogger getInstance(int newPortNumber) throws IOException {
		if (loggerpool.get(newPortNumber)!=null){
			return loggerpool.get(newPortNumber);
		}else{
			SmartLogger sl=new SmartLogger(newPortNumber);
			loggerpool.put(newPortNumber,sl);
			return sl;
		}
	}

	public SmartLogger(int newPortNumber) throws IOException {
		super(newPortNumber);
	}

	public SmartLogger(int newPortNumber, String logName) throws IOException {
		super(newPortNumber, logName);
	}

	public SmartLogger(int newPortNumber, File logPath, String logName) throws IOException {
		super(newPortNumber, logPath, logName);
	}

	@Override
	public void println(Object o) {
		// send to /dev/null
	}
}
