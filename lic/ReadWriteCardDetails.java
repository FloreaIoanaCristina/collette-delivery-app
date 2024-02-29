package com.dam.lic;

public class ReadWriteCardDetails {
    private String nr;
    private int expirationMonth;
    private int expirationYear;
    private String name;
    private String cvc;

    public int getActiv() {
        return activ;
    }

    public void setActiv(int activ) {
        this.activ = activ;
    }

    private int activ;

    public ReadWriteCardDetails(String nr, int expirationMonth, int expirationYear, String name, String cvc,int activ) {
        this.nr = nr;
        this.expirationMonth = expirationMonth;
        this.expirationYear = expirationYear;
        this.name = name;
        this.cvc = cvc;
        this.activ = activ;

    }

    public String getNr() {
        return nr;
    }

    public void setNr(String nr) {
        this.nr = nr;
    }

    public int getExpirationMonth() {
        return expirationMonth;
    }

    public void setExpirationMonth(int expirationMonth) {
        this.expirationMonth = expirationMonth;
    }

    public int getExpirationYear() {
        return expirationYear;
    }

    public void setExpirationYear(int expirationYear) {
        this.expirationYear = expirationYear;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCvc() {
        return cvc;
    }

    public void setCvc(String cvc) {
        this.cvc = cvc;
    }

}
