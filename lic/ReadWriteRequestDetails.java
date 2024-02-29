package com.dam.lic;

public class ReadWriteRequestDetails {
    public String sender;
    public String receiver;

    public ReadWriteRequestDetails(String sender, String receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    @Override
    public String toString() {
        return "ReadWriteRequestDetails{" +
                "sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                '}';
    }
}
