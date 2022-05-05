package com.example.carespace.ClinicHospitalView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class ClinicHospitalModel implements Serializable {
    private String hospitalID,name,title,city,description,rating,address,contactNum,workingHours,speciality;
    private HashMap<String,String> pictures;

    public ClinicHospitalModel() { }

    public ClinicHospitalModel(String hospitalID, String name, String title, String city, String description, String rating, String address, String contactNum, String workingHours, String speciality, HashMap<String, String> pictures) {
        this.hospitalID = hospitalID;
        this.name = name;
        this.title = title;
        this.city = city;
        this.description = description;
        this.rating = rating;
        this.address = address;
        this.contactNum = contactNum;
        this.workingHours = workingHours;
        this.speciality = speciality;
        this.pictures = pictures;
    }

    public HashMap<String, String> getPictures() {
        return pictures;
    }

    public void setPictures(HashMap<String, String> pictures) {
        this.pictures = pictures;
    }

    public String getHospitalID() {
        return hospitalID;
    }

    public void setHospitalID(String hospitalID) {
        this.hospitalID = hospitalID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactNum() {
        return contactNum;
    }

    public void setContactNum(String contactNum) {
        this.contactNum = contactNum;
    }

    public String getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(String workingHours) {
        this.workingHours = workingHours;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }
}
