package com.nttdata.de.sky.ityx.contex.enrichment;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CustomerValidationBeanTest {

	@Test
	public void testValidateCustomer() {
		CustomerValidationBean bean = new CustomerValidationBean();
		
		assertEquals(0, bean.validateCustomer("1234567890"));
		assertEquals(1, bean.validateCustomer("1812345678"));
		assertEquals(2, bean.validateCustomer("5142345678"));
	}

}
