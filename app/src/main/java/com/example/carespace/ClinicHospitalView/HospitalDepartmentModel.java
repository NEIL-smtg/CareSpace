package com.example.carespace.ClinicHospitalView;

import com.example.carespace.R;

import java.io.Serializable;

public class HospitalDepartmentModel implements Serializable {

    private String[] Department = {
            "Cardiology",
            "Neurology",
            "Obstetrics",
            "Ophthalmology",
            "Optometry",
            "Orthopedics",
            "Pediatrics",
            "Psychiatry",
            "Radiology",
            "Stomatology",
            "Dermatology"
    };

    private int[] img = {
            R.drawable.cardio,
            R.drawable.neurology,
            R.drawable.obstetrics,
            R.drawable.ophthalmology,
            R.drawable.optometry,
            R.drawable.orthopedics,
            R.drawable.pediatrics,
            R.drawable.psychiatry,
            R.drawable.radiology,
            R.drawable.stomatology,
            R.drawable.dermatology
    };

    public HospitalDepartmentModel() { }

    public String[] getDepartment() {
        return Department;
    }

    public int[] getImg() {
        return img;
    }

}
