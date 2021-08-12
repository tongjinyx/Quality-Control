package com.example.QualityControlDemo.Entity;

public class Template {
    public ConclusionTemplate conclusionTemplate;
    public FindingTemplate findingTemplate;

    public Template(){

    }

    public void setConclusionTemplate(ConclusionTemplate conclusionTemplate) {
        this.conclusionTemplate = conclusionTemplate;
    }

    public void setFindingTemplate(FindingTemplate findingTemplate) {
        this.findingTemplate = findingTemplate;
    }
}
