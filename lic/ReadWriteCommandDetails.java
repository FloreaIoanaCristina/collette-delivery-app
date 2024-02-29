package com.dam.lic;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ReadWriteCommandDetails {
    private  String recipientLat;
    private  String recipientLng;
    private  String senderLat;
    private  String senderLng;
    private  int noPackages;
    private String height;
    private String length;
    private String width;
    private  String weight;
    private boolean fragile;
    private String senderName,senderPhone, senderCounty, senderLoc, senderAddress,
            recipientName,recipientPhone, recipientCounty, recipientLoc, recipientAddress,
            sender, recipient, courier,date, end;
    private boolean cashPayment;
    private Stare state;
    private  float price;

    public ReadWriteCommandDetails(int nrColete, String inaltime, String lungime, String latime, String greutate, boolean fragil, String numeExpeditor,String telefonExpeditor, String judetExpeditor, String orasExpeditor, String adresaExpeditor, String numeDestinatar,String telefonDestinatar, String judetDestinatar, String orasDestinatar, String adresaDestinatar, String expeditor, String destinatar, String curier, boolean plataCash,Stare stare, String data,String rLat, String rLng, String sLat, String sLng, float price) {
        this.noPackages = nrColete;
        this.height = inaltime;
        this.length = lungime;
        this.width = latime;
        this.weight = greutate;
        this.fragile = fragil;
        this.senderName = numeExpeditor;
        this.senderPhone = telefonExpeditor;
        this.senderCounty = judetExpeditor;
        senderLoc = orasExpeditor;
        senderAddress = adresaExpeditor;
        this.recipientName = numeDestinatar;
        this.recipientPhone=telefonDestinatar;
        this.recipientCounty = judetDestinatar;
        recipientLoc = orasDestinatar;
        recipientAddress = adresaDestinatar;
        this.sender = expeditor;
        this.recipient = destinatar;
        this.courier = curier;
        this.cashPayment = plataCash;
        this.state = stare;
        this.date=data;
        this.recipientLat = rLat;
        this.recipientLng =rLng;
        this.senderLat = sLat;
        this.senderLng = sLng;
        this.price = price;
    }

    @Override
    public String toString() {
        return "Comanda:\n" +
                "\t Data: " + date + "\n"+
                "\t Pret: "+ price+"\n"+
                "\t Numar colete: " + noPackages + "\n"+
                "\t Inaltime: " + height + "\n"+
                "\t Lungime: " + length + "\n"+
                "\t Latime: " + width + "\n"+
                "\t Fragil: " + fragile + "\n"+
                "\t Telefon expeditor: " + senderPhone + "\n"+
                "\t Judet expeditor: " + senderCounty + "\n"+
                "\t Oras expeditor: " + senderLoc + "\n"+
                "\t Adresa expeditor: " + senderAddress + "\n"+
                "\t Telefon destinatar: " + recipientPhone + "\n"+
                "\t Judet destinatar: " + recipientCounty + "\n"+
                "\t Oras destinatar: " + recipientLoc + "\n"+
                "\t Adresa destinatar: " + recipientAddress + "\n"+
                "\t Plata cash la expediere: " + cashPayment +"\n"+
                "\t Stare comanda: " + state +"\n";

    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getRecipientLat() {
        return recipientLat;
    }

    public void setRecipientLat(String recipientLat) {
        this.recipientLat = recipientLat;
    }

    public String getRecipientLng() {
        return recipientLng;
    }

    public void setRecipientLng(String recipientLng) {
        this.recipientLng = recipientLng;
    }

    public String getSenderLat() {
        return senderLat;
    }

    public void setSenderLat(String senderLat) {
        this.senderLat = senderLat;
    }

    public String getSenderLng() {
        return senderLng;
    }

    public void setSenderLng(String senderLng) {
        this.senderLng = senderLng;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSenderPhone() {
        return senderPhone;
    }

    public void setSenderPhone(String senderPhone) {
        this.senderPhone = senderPhone;
    }

    public String getRecipientPhone() {
        return recipientPhone;
    }

    public void setRecipientPhone(String recipientPhone) {
        this.recipientPhone = recipientPhone;
    }

    public String toStringCourier(){
        return "EXPEDITOR: " + senderName +"\n"+"DESTINATAR: " + recipientName +"\n" + this;
    }

    public int getNoPackages() {
        return noPackages;
    }

    public void setNoPackages(int noPackages) {
        this.noPackages = noPackages;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public boolean isFragile() {
        return fragile;
    }

    public void setFragile(boolean fragile) {
        this.fragile = fragile;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderCounty() {
        return senderCounty;
    }

    public void setSenderCounty(String senderCounty) {
        this.senderCounty = senderCounty;
    }

    public String getSenderLoc() {
        return senderLoc;
    }

    public void setSenderLoc(String senderLoc) {
        this.senderLoc = senderLoc;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getRecipientCounty() {
        return recipientCounty;
    }

    public void setRecipientCounty(String recipientCounty) {
        this.recipientCounty = recipientCounty;
    }

    public String getRecipientLoc() {
        return recipientLoc;
    }

    public void setRecipientLoc(String recipientLoc) {
        this.recipientLoc = recipientLoc;
    }

    public String getRecipientAddress() {
        return recipientAddress;
    }

    public void setRecipientAddress(String recipientAddress) {
        this.recipientAddress = recipientAddress;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getCourier() {
        return courier;
    }

    public void setCourier(String courier) {
        this.courier = courier;
    }

    public boolean isCashPayment() {
        return cashPayment;
    }

    public void setCashPayment(boolean cashPayment) {
        this.cashPayment = cashPayment;
    }

    public Stare getState() {
        return state;
    }

    public void setState(Stare state) {
        this.state = state;
    }
}
