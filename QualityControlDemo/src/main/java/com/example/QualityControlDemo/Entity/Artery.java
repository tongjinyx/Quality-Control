package com.example.QualityControlDemo.Entity;


//动脉评价
public class Artery {
    //肠系膜上动脉
    public SimpleTemplate mesentery;
    //腹腔干动脉
    public SimpleTemplate celiac;
    //肝总动脉
    public SimpleTemplate liver;
    //动脉变异
    public SimpleTemplate variation;
    public Artery() {

    }
    public void setCeliac(SimpleTemplate celiac) {
        this.celiac = celiac;
    }

    public void setLiver(SimpleTemplate liver) {
        this.liver = liver;
    }

    public void setMesentery(SimpleTemplate mesentery) {
        this.mesentery = mesentery;
    }

    public void setVariation(SimpleTemplate variation) {
        this.variation = variation;
    }
}
