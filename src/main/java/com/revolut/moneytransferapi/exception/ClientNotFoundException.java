package com.revolut.moneytransferapi.exception;

public class ClientNotFoundException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -2095266404597455621L;

	public ClientNotFoundException(String message) {
        super(message);
    }

}
