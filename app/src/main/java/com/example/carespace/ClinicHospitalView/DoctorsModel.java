package com.example.carespace.ClinicHospitalView;

import com.example.carespace.R;

import java.io.Serializable;

public class DoctorsModel implements Serializable {

    private String hospitalID,doctorID,imgUrl,name,department,rating,workingExperience,description,workingHours,price;

    public DoctorsModel() { }

    public DoctorsModel(String hospitalID, String doctorID, String imgUrl, String name, String department, String rating, String workingExperience, String description, String workingHours, String price) {
        this.hospitalID = hospitalID;
        this.doctorID = doctorID;
        this.imgUrl = imgUrl;
        this.name = name;
        this.department = department;
        this.rating = rating;
        this.workingExperience = workingExperience;
        this.description = description;
        this.workingHours = workingHours;
        this.price = price;
    }

    public String getHospitalID() {
        return hospitalID;
    }

    public void setHospitalID(String hospitalID) {
        this.hospitalID = hospitalID;
    }

    public String getDoctorID() {
        return doctorID;
    }

    public void setDoctorID(String doctorID) {
        this.doctorID = doctorID;
    }


    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getWorkingExperience() {
        return workingExperience;
    }

    public void setWorkingExperience(String workingExperience) {
        this.workingExperience = workingExperience;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

//        private String[] doctors ={
//            "Benedict Cumberbatch",
//            "Robert Downy Jr",
//            "Tom Cruise",
//            "Robert Pattinson",
//            "Willem Dafoe",
//            "Phil Foden",
//            "Brie Larson",
//            "Chris Hemsworth",
//            "Joaquin Phoenix",
//            "Will Smith",
//            "Anggu Chew"
//    };
//
//    private String[] ratings = {
//            "4.3",
//            "3.2",
//            "4.5",
//            "3.9",
//            "4.8",
//            "4.3",
//            "3.2",
//            "4.5",
//            "3.9",
//            "4.8"
//    };
//
//    private int[] doctor_img={
//            R.drawable.doctor1,
//            R.drawable.doctor2,
//            R.drawable.doctor3,
//            R.drawable.doctor4,
//            R.drawable.doctor5,
//            R.drawable.doctor6,
//            R.drawable.doctor7,
//            R.drawable.doctor8,
//            R.drawable.doctor9,
//            R.drawable.doctor10
//    };
//
//    private String[] Department = {
//            "Cardiology",
//            "Neurology",
//            "Obstetrics",
//            "Ophthalmology",
//            "Optometry",
//            "Orthopedics",
//            "Pediatrics",
//            "Psychiatry",
//            "Radiology",
//            "Stomatology",
//            "Dermatology"
//    };
//
//    public String[] getDoctors() {
//        return doctors;
//    }
//
//    public String[] getRatings() {
//        return ratings;
//    }
//
//    public int[] getDoctor_img() {
//        return doctor_img;
//    }
//
//    public String[] getDepartment2() {
//        return Department;
//    }
}
