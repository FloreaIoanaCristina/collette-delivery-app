package com.dam.lic;

public class ReadWriteUserDetails {

    public String user;
    public String token;
    public String phone;
    public String address;
    public String email;
    public String county;
    public String loc;
    public String postalCode;
    public String img;
    public boolean curier = false;


    public ReadWriteUserDetails(String user,String phone,String email,String county, String loc, String address, String postalCode, String img,String token) {
        this.user = user;
        this.phone= phone;
        this.address=address;
        this.loc=loc;
        this.county=county;
        this.postalCode=postalCode;
        this.email= email;
        this.img = img;
        this.token =token;



    }

    @Override
    public String toString() {
        return "ReadWriteUserDetails{" +
                "user='" + user + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", email='" + email + '\'' +
                ", county='" + county + '\'' +
                ", loc='" + loc + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", img='" + img + '\'' +
                '}';
    }
}
