package com.tvm.doctorcube.home.model;


import com.tvm.doctorcube.university.model.University;

import java.io.Serializable;
import java.util.List;

public class Country implements Serializable {
    private String name;
    private List<University> universities;
    private float averageRating;
    private int flagResourceId;

    public Country(String name, List<University> universities, float averageRating, int flagResourceId) {
        this.name = name;
        this.universities = universities;
        this.averageRating = averageRating;
        this.flagResourceId = flagResourceId;
    }

    public String getName() {
        return name;
    }

    public List<University> getUniversities() {
        return universities;
    }

    public float getAverageRating() {
        return averageRating;
    }

    public int getFlagResourceId() {
        return flagResourceId;
    }
}