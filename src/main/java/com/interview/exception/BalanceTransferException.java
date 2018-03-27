package com.interview.exception;

/*
User defined exception class.
 */
public class BalanceTransferException extends Exception {

    private String errorMessage;

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public BalanceTransferException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
    }
}
