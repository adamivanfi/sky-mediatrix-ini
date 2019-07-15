package com.nttdata.de.lib.exception;
/**
 * Ein Exception-Manager behandelt unchecked-exceptions.
 * @version 1.0.0
 * 
 * @author <a href="mailto:michael.adams@cirquent.de">Michael Adams</a>
 */
public interface ExceptionManager {
	
	/**
	 * Behandelt eine Exception.
	 * 
	 * @param t
	 *     Die Exception, welche behandelt werden soll. 
	 */
	void handleException(Throwable t);

}
