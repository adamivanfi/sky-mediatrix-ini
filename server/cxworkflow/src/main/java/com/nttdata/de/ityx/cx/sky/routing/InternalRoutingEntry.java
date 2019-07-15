/**
 * 
 */
package com.nttdata.de.ityx.cx.sky.routing;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import java.io.Serializable;

/**
 * @author DHIFLM
 *
 */
@Entity
@Table(name = "routing_entry")
public class InternalRoutingEntry implements Comparable<InternalRoutingEntry>, Serializable {

	public enum Kundendaten {VALIDATE,EXTRACT,NONE}
	
	private Integer priority;
	
	private Kundendaten customerData;
	
	private String category;
	
	private String address;
	
	private String subject;

	public InternalRoutingEntry() {
		// empty
	}
	
	public InternalRoutingEntry(Integer priority, Kundendaten customerData, String category, String address, String subject) {
		setPriority(priority);
		setCustomerData(customerData);
		setCategory(category);
		setAddress(address);
		setSubject(subject);
	}
	
	@Override
	public int compareTo(InternalRoutingEntry o) {
		return this.getPriority().compareTo(o.getPriority());
	}

	/**
	 * @return the priority
	 */
	@Id
	public Integer getPriority() {
		return priority;
	}

	/**
	 * @param priority the priority to set
	 */
	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	/**
	 * @return the customerData
	 */
	@Enumerated(EnumType.ORDINAL)
	public Kundendaten getCustomerData() {
		return customerData;
	}

	/**
	 * @param customerData the customerData to set
	 */
	public void setCustomerData(Kundendaten customerData) {
		this.customerData = customerData;
	}

	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

}
