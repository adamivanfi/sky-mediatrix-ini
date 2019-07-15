package com.nttdata.de.lib.exception;

public class TunnelException extends RuntimeException {
    private Throwable exception;

    public TunnelException(Throwable exception) {
        this.exception = exception;
    }
    
    @Override
	public Throwable getCause() {
    	return this.exception;
	}
}
