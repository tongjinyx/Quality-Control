package com.example.QualityControlDemo.Entity;

//胰腺实质
public class Parenchyma {
    public String density;
    public String size;
    public String position;
    public Boolean symptom1;
    public Boolean symptom2;

    public Parenchyma() {

    }

    public void setDensity(String density) {
        this.density = density;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setSymptom1(Boolean symptom1) {
        this.symptom1 = symptom1;
    }

    public void setSymptom2(Boolean symptom2) {
        this.symptom2 = symptom2;
    }
}
