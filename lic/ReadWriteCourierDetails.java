package com.dam.lic;

import java.util.Random;

public class ReadWriteCourierDetails {

    public  String user;
    public  String token;
    public  String phone;
    public  String email;
    public  String brand;
    public  String model;
    public  String license;
    public  String color;
    public  String img;
    public  int nrFeedback;
    public  float rating;
    public boolean curier = true;
    public String lng;
    public String lat;

    public ReadWriteCourierDetails(String user, String phone, String email, String brand, String model, String license, String color, String img, String token) {
        this.user = user;
        this.phone = phone;
        this.email = email;
        this.brand = brand;
        this.model = model;
        this.license = license;
        this.color = color;
        this.img = img;
        this.token = token;
        this.nrFeedback=0;
        this.rating=5;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    @Override
    public String toString() {
        return "ReadWriteCourierDetails{" +
                "user='" + user + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", license='" + license + '\'' +
                ", color='" + color + '\'' +
                ", img='" + img + '\'' +
                '}';
    }
}
