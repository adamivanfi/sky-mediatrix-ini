package com.nttdata.de.sky.connector.contex;

import java.util.Map;

public interface TestContexWsListener {
	public void methodInvoked(String master, String processName, Map<String, String> input, Map<String, String> output);
}
