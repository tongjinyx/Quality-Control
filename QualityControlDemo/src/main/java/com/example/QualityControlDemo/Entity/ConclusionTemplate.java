package com.example.QualityControlDemo.Entity;


import javax.annotation.processing.Generated;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.List;
import java.util.Map;

public class ConclusionTemplate {
    public String diagnose;
    public String examinationName;
    public List<Map<Integer,String>> shortcut;
    public Double score;
    public ConclusionTemplate(){

    }

    public ConclusionTemplate(String diagnose,String examinationName) {
        this.diagnose=diagnose;

        this.examinationName=examinationName;

    }

    public String getDiagnose() {
        return diagnose;
    }



    public String getExaminationName() {
        return examinationName;
    }


    public void setDiagnose(String diagnose) {
        this.diagnose = diagnose;
    }



    public void setExaminationName(String examinationName) {
        this.examinationName = examinationName;
    }

    public void setShortcut(List<Map<Integer, String>> shortcut) {
        this.shortcut = shortcut;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
