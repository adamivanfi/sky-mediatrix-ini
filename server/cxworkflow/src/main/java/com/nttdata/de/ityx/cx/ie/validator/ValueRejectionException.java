/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nttdata.de.ityx.cx.ie.validator;

/**
 *
 * @author MEINUG
 */
public class ValueRejectionException extends Exception{
	
     public ValueRejectionException(String message,String value) {
        super(message+" value:"+value);
    }
}
