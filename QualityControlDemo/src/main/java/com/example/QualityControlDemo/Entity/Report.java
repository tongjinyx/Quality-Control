package com.example.QualityControlDemo.Entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String conclusion;
    public String finding;
    public Long doctorId;

    @DateTimeFormat(pattern = "yyyy-mm-dd")
    public Date date;
    public String state;
    public Double conclusionScore;
    public Double findingScore;
    public Report(){

    }

    public Report(String conclusion, String finding, Long doctorId, Date date,String state, Double conclusionScore, Double findingScore) {
        this.conclusion=conclusion;
        this.finding=finding;
        this.doctorId=doctorId;
        this.state=state;
        this.conclusionScore=conclusionScore;
        this.findingScore=findingScore;
        this.date=date;
    }


    public Long getId() {
        return id;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public Double getConclusionScore() {
        return conclusionScore;
    }

    public Double getFindingScore() {
        return findingScore;
    }

    public Long getDoctorId() {
        return doctorId;
    }



    public String getState() {
        return state;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }


    public void setState(String state) {
        this.state = state;
    }

    public String getConclusion() {
        return conclusion;
    }

    public String getFinding() {
        return finding;
    }

    public void setConclusion(String conclusion) {
        this.conclusion = conclusion;
    }

    public void setConclusionScore(Double conclusionScore) {
        this.conclusionScore = conclusionScore;
    }

    public void setFinding(String finding) {
        this.finding = finding;
    }

    public void setFindingScore(Double findingScore) {
        this.findingScore = findingScore;
    }
}
