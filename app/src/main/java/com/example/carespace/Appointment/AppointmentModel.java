package com.example.carespace.Appointment;

import java.io.Serializable;

public class AppointmentModel implements Serializable {

    private String AppointmentID, patientID, name,contactNum,email,bornDate,description,doctorID,doctorDepartment,price,AppointmentDate,timeSlot, doctorImgUrl,hospitalID,docName;

    public AppointmentModel() {
    }

    public AppointmentModel(String appointmentID, String patientID, String name, String contactNum, String email, String bornDate, String description, String doctorID, String doctorDepartment, String price, String appointmentDate, String timeSlot, String doctorImgUrl, String hospitalID, String docName) {
        AppointmentID = appointmentID;
        this.patientID = patientID;
        this.name = name;
        this.contactNum = contactNum;
        this.email = email;
        this.bornDate = bornDate;
        this.description = description;
        this.doctorID = doctorID;
        this.doctorDepartment = doctorDepartment;
        this.price = price;
        AppointmentDate = appointmentDate;
        this.timeSlot = timeSlot;
        this.doctorImgUrl = doctorImgUrl;
        this.hospitalID = hospitalID;
        this.docName = docName;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getHospitalID() {
        return hospitalID;
    }

    public void setHospitalID(String hospitalID) {
        this.hospitalID = hospitalID;
    }

    public String getDoctorImgUrl() {
        return doctorImgUrl;
    }

    public void setDoctorImgUrl(String doctorImgUrl) {
        this.doctorImgUrl = doctorImgUrl;
    }

    public String getAppointmentID() {
        return AppointmentID;
    }

    public void setAppointmentID(String appointmentID) {
        AppointmentID = appointmentID;
    }

    public String getPatientID() {
        return patientID;
    }

    public void setPatientID(String patientID) {
        this.patientID = patientID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactNum() {
        return contactNum;
    }

    public void setContactNum(String contactNum) {
        this.contactNum = contactNum;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBornDate() {
        return bornDate;
    }

    public void setBornDate(String bornDate) {
        this.bornDate = bornDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDoctorID() {
        return doctorID;
    }

    public void setDoctorID(String doctorID) {
        this.doctorID = doctorID;
    }

    public String getDoctorDepartment() {
        return doctorDepartment;
    }

    public void setDoctorDepartment(String doctorDepartment) {
        this.doctorDepartment = doctorDepartment;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAppointmentDate() {
        return AppointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        AppointmentDate = appointmentDate;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }
}
