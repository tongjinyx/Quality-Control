package com.example.QualityControlDemo.Entity;

//胰周
public class Peripancreatic {
    //胰周改变
    public Boolean change;
    //脂肪间隙
    public String fatGap;
    //是否有积液
    public Boolean isEffusion;

    public Peripancreatic() {

    }

    public void setChange(Boolean change) {
        this.change = change;
    }

    public void setEffusion(Boolean effusion) {
        isEffusion = effusion;
    }

    public void setFatGap(String fatGap) {
        this.fatGap = fatGap;
    }
}
