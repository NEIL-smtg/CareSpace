package com.example.carespace.ClinicHospitalView;

import java.io.Serializable;

public class dbDepartmentModel implements Serializable {

    private String hospitalID, departmentName, description, workingHours, contactNum, address, imgUrl;

    public dbDepartmentModel(String hospitalID, String departmentName, String description, String workingHours, String contactNum, String address, String imgUrl) {
        this.hospitalID = hospitalID;
        this.departmentName = departmentName;
        this.description = description;
        this.workingHours = workingHours;
        this.contactNum = contactNum;
        this.address = address;
        this.imgUrl = imgUrl;
    }

    public String getHospitalID() {
        return hospitalID;
    }

    public void setHospitalID(String hospitalID) {
        this.hospitalID = hospitalID;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(String workingHours) {
        this.workingHours = workingHours;
    }

    public String getContactNum() {
        return contactNum;
    }

    public void setContactNum(String contactNum) {
        this.contactNum = contactNum;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
