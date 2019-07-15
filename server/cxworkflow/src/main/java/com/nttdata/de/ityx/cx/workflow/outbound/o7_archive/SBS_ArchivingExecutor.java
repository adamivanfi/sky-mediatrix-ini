package com.nttdata.de.ityx.cx.workflow.outbound.o7_archive;

/**
 * Created by meinusch on 16.07.15.
 */
public class SBS_ArchivingExecutor extends ArchivingExecutor {

	@Override
	public String getMaster() {
		return "sbs";
	}

	@Override
	public String getProcessname() {
		return "SBS_811_Archiv";
	}

}
