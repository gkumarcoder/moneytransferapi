package com.revolut.moneytransferapi.exception;

public class InvalidAmountValueException extends IllegalArgumentException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 4628157765686597309L;

	public InvalidAmountValueException(String message) {
        super(message);
    }
}
