package com.nttdata.de.ityx.cx.sky.reporting;

import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

public class InsertReportingEntryParameter {
	private int		currentdocpool;
	private String		processname;
	private String		processversion;
	private String		step;
	private long		duration;
	private String		stepdetail;
	private long		ctx_docpoolid;
	private long		ctx_prevdocpoolid;
	private long		mtx_orgmailid;
	private String		documentsource;
	private String		documentid;
	private Date	incomingts;
	private TagMatchDefinitions.Channel channel;
	private String		crm_contactid;
	private String		formtype;
	private long		customerid;
	private String		master;
        private long	Mtx_frageid;
        private long	Mtx_vorgangid;
        private long Mtx_mailid;
        private String	Mtx_TPName;
	private String LHostname;

	public InsertReportingEntryParameter(int currentdocpool, String processname, String processversion, String step, long duration, String stepdetail, long ctx_docpoolid, long ctx_prevdocpoolid, long mtx_orgmailid,
			String documentsource, String documentid, Date incomingts, TagMatchDefinitions.Channel channel, String crm_contactid, String formtype, long customerid, String master) {
		this.currentdocpool = currentdocpool;
		this.processname = processname;
		this.processversion = processversion;
		this.step = step;
		this.duration = duration;
		this.stepdetail = stepdetail;
		this.ctx_docpoolid = ctx_docpoolid;
		this.ctx_prevdocpoolid = ctx_prevdocpoolid;
		this.mtx_orgmailid = mtx_orgmailid;
		this.documentsource = documentsource;
		this.documentid = documentid;
		this.incomingts = incomingts;
		this.channel = channel;
		this.crm_contactid = crm_contactid;
		this.formtype = formtype;
		this.customerid = customerid;
		this.master = master;
	}


	/**
	 *	Default constructor 
	 */
	public InsertReportingEntryParameter() {
	}

	public int getCurrentdocpool() {
		return currentdocpool;
	}

	public void setCurrentdocpool(int currentdocpool) {
		this.currentdocpool = currentdocpool;
	}

	public String getProcessname() {
		return processname;
	}

	public void setProcessname(String processname) {
		this.processname = processname;
	}

	public String getProcessversion() {
		return processversion;
	}

	public void setProcessversion(String processversion) {
		this.processversion = processversion;
	}

	public String getStep() {
		return step;
	}

	public void setStep(String step) {
		this.step = step;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public String getStepdetail() {
		return stepdetail;
	}

	public void setStepdetail(String stepdetail) {
		this.stepdetail = stepdetail;
	}

	public long getCtx_docpoolid() {
		return ctx_docpoolid;
	}

	public void setCtx_docpoolid(long ctx_docpoolid) {
		this.ctx_docpoolid = ctx_docpoolid;
	}

	public long getCtx_prevdocpoolid() {
		return ctx_prevdocpoolid;
	}

	public void setCtx_prevdocpoolid(long ctx_prevdocpoolid) {
		this.ctx_prevdocpoolid = ctx_prevdocpoolid;
	}

	public long getMtx_orgmailid() {
		return mtx_orgmailid;
	}

	public void setMtx_orgmailid(long mtx_orgmailid) {
		this.mtx_orgmailid = mtx_orgmailid;
	}

	public String getDocumentsource() {
		return documentsource;
	}

	public void setDocumentsource(String documentsource) {
		this.documentsource = documentsource;
	}

	public String getDocumentid() {
		return documentid;
	}

	public void setDocumentid(String documentid) {
		this.documentid = documentid;
	}

	public Date getIncomingdate() {
		return incomingts;
	}

	public void setIncomingdate(Date incomingts) {
		this.incomingts = incomingts;
	}

	public TagMatchDefinitions.Channel getChannel() {
		return channel;
	}

	public void setChannel(TagMatchDefinitions.Channel doctype) {
		this.channel = doctype;
	}

	public String getCrm_contactid() {
		return crm_contactid;
	}

	public void setCrm_contactid(String crm_contactid) {
		this.crm_contactid = crm_contactid;
	}

	public String getFormtype() {
		return formtype;
	}

	public void setFormtype(String formtype) {
		this.formtype = formtype;
	}

	public long getCustomerid() {
		return customerid;
	}

	public void setCustomerid(long customerid) {
		this.customerid = customerid;
	}

	public String getMaster() {
		return master;
	}

	public void setMaster(String master) {
		this.master = master;
	}

    /**
     * @return the Mtx_frageid
     */
    public long getMtx_frageid() {
        return Mtx_frageid;
    }

    /**
     * @param Mtx_frageid the Mtx_frageid to set
     */
    public void setMtx_frageid(long Mtx_frageid) {
        this.Mtx_frageid = Mtx_frageid;
    }

    /**
     * @return the Mtx_vorgangid
     */
    public long getMtx_vorgangid() {
        return Mtx_vorgangid;
    }

    /**
     * @param Mtx_vorgangid the Mtx_vorgangid to set
     */
    public void setMtx_vorgangid(long Mtx_vorgangid) {
        this.Mtx_vorgangid = Mtx_vorgangid;
    }

    /**
     * @return the Mtx_mailid
     */
    public long getMtx_mailid() {
        return Mtx_mailid;
    }

    /**
     * @param Mtx_mailid the Mtx_mailid to set
     */
    public void setMtx_mailid(long Mtx_mailid) {
        this.Mtx_mailid = Mtx_mailid;
    }

    /**
     * @return the Mtx_TPName
     */
    public String getMtx_TPName() {
        return Mtx_TPName;
    }

    /**
     * @param Mtx_TPName the Mtx_TPName to set
     */
    public void setMtx_TPName(String Mtx_TPName) {
        this.Mtx_TPName = Mtx_TPName;
    }


	public synchronized String getLHostname() {
		if (LHostname==null) {
			try {
				LHostname=InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException e) {
				LHostname="localhost";
			}
		}
		return LHostname;
	}
}