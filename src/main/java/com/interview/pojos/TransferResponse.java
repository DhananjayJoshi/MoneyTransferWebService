package com.interview.pojos;

/*
Response class for balance transfer request.
 */
public class TransferResponse {
    private String message;
    private Boolean isErrorResponse;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getErrorResponse() {
        return isErrorResponse;
    }

    public void setErrorResponse(Boolean errorResponse) {
        isErrorResponse = errorResponse;
    }
}
