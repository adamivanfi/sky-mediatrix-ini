package com.nttdata.de.ityx.cx.sky;

import com.nttdata.de.lib.logging.SkyLogger;

import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GroupGenerator {
	
	public static void main(String[] args) {
		
		System.out.println("start thread");
		new Thread() {
			public void run() {
				GroupGenerator.prepareInstance("TEST", new Parameters(20, 3));
				for (int h1=0; h1 < 1000; h1++) {
					for (int h2=0; h2 < 2-1; h2++) {
						GroupGenerator.getInstance("TEST").incGrouping();
					}
					String[] groups = GroupGenerator.getInstance("TEST").incGrouping();
					assert groups.length==3;
					System.out.println("A"+h1 +"     "+groups[0] + ":::" + groups[1] + "/" + groups[2]);
				}
			}
		}.start();
		
		System.out.println("start thread");
		new Thread() {
			public void run() {
				GroupGenerator.prepareInstance("TEST", new Parameters(2, 3));
				for (int h1=0; h1 < 1000; h1++) {
					for (int h2=0; h2 < 2-1; h2++) {
						GroupGenerator.getInstance("TEST").incGrouping();
					}
					String[] groups = GroupGenerator.getInstance("TEST").incGrouping();
					assert groups.length==3;
					System.out.println("B"+h1 +"     "+groups[0] + ":::" + groups[1] + "/" + groups[2]);
				}
			}
		}.start();
		
	}

	public static class Parameters {
		
		public Parameters() {
			this.docMax = 1000;
			this.grpMax = 1000;
			this.dateFormat = "yy-MM-dd";
			this.groupFormat = "%date-%starttime";
		}
		
		public Parameters(int docMax, int groupMax) {
			this.docMax = docMax;
			this.grpMax = groupMax;
			this.dateFormat = "yy-MM-dd";
			this.groupFormat = "%date-%starttime";
		}
		
		public Parameters(int docMax, int groupMax, String dateFormat, String groupFormat) {
			this.docMax = docMax;
			this.grpMax = groupMax;
			this.dateFormat = dateFormat;
			this.groupFormat = groupFormat;
		}

		private int docMax;
		private int grpMax;
		private String dateFormat;
		private String groupFormat;
		
		public int getGroupingMax() {
			return grpMax;
		}

		public int getDocumentsMax() {
			return docMax;
		}

		public String getDateFormat() {
			return dateFormat;
		}

		public String getBaseGroupingFormat() {
			return groupFormat;
		}		
	}
	
	public static Map<String,GroupGenerator> instanceMap = new HashMap<>();
	
	public static boolean prepareInstance(String generatorName, Parameters params) {
		if (instanceMap.containsKey(generatorName))
			return false;
		
		instanceMap.put(generatorName, new GroupGenerator(generatorName, params));
		return true;
	}
	
	public static GroupGenerator getInstance(String generatorName) {
		GroupGenerator result = instanceMap.get(generatorName);
		if (result == null) {
			result = new GroupGenerator(generatorName);
			instanceMap.put(generatorName, result);
		}
		return result;
	}
	
	public GroupGenerator(String name) {
		this.name = name;
		this.params = new Parameters();
	}
	
	public GroupGenerator(String name, Parameters params) {
		this.name = name;
		this.params = params;
	}
	
	public synchronized String[] incGrouping() {
		
		SkyLogger.getItyxLogger().debug("Create new group values");
		
		String[] result = new String[3];
		
		Date now = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat(params.getDateFormat());
		String actualDate = params.getBaseGroupingFormat();
		actualDate = actualDate.replaceAll("%date", formatter.format(now));
		actualDate = actualDate.replaceAll("%name", name);
		actualDate = actualDate.replaceAll("%starttime", ""+ManagementFactory.getRuntimeMXBean().getStartTime());
		if (currentDate == null || !currentDate.equals(actualDate)) {
			/*
			 * Reset grouping if not configured yet or the date has changed
			 */
			currentDate = actualDate;
			currentDocuments = 0;
		
			currentGrouping = new int[2];
			currentGrouping[0] = 0;
			currentGrouping[1] = 1;
		}
		int count = currentDocuments;
		result[0] = currentDate;
		
		if (count >= params.getDocumentsMax()) {
			if (currentGrouping[1] >= params.getGroupingMax()) {
				currentGrouping[0]++;
				currentGrouping[1] = 1;
			} else {
				currentGrouping[1]++;
			}
			count = 0;
		}
		result[1] = ""+currentGrouping[0];
		result[2] = ""+currentGrouping[1];
		count++;
		
		currentDocuments = count;
		
		SkyLogger.getItyxLogger().info("New group values generated: "+result[0]+", "+result[1]+", "+result[2]);		
		return result;
	}
	
	private Parameters params;
	private String name;
	private String currentDate = null;
	private int[] currentGrouping = null;
	private int currentDocuments = 0;

}
