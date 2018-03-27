package com.interview.pojos;

/*
Balance transfer request class
 */
public class TransferRequest {

    private String senderAccountId;
    private String receiverAccountId;
    private Double balanceToTransfer;

    public String getSenderAccountId() {
        return senderAccountId;
    }

    public void setSenderAccountId(String senderAccountId) {
        this.senderAccountId = senderAccountId;
    }

    public String getReceiverAccountId() {
        return receiverAccountId;
    }

    public void setReceiverAccountId(String receiverAccountId) {
        this.receiverAccountId = receiverAccountId;
    }

    public Double getBalanceToTransfer() {
        return balanceToTransfer;
    }

    public void setBalanceToTransfer(Double balanceToTransfer) {
        this.balanceToTransfer = balanceToTransfer;
    }

    @Override
    public String toString() {
        return "TransferRequest{" +
                "senderAccountId='" + senderAccountId + '\'' +
                ", receiverAccountId='" + receiverAccountId + '\'' +
                ", balanceToTransfer='" + balanceToTransfer + '\'' +
                '}';
    }
}
