package com.example.QualityControlDemo.Entity;

import java.util.List;
import java.util.Map;

public class FindingTemplate {
   public Pancreas pancreas;
   public Peripancreatic peripancreatic;
   public Parenchyma parenchyma;
   public Artery artery;
   public Vein vein;
   public OtherTemplate other;
    public List<Map<Integer,String>> shortcut;
    public Double score;
   public FindingTemplate() {

   }
    public void setPancreas(Pancreas pancreas) {
        this.pancreas = pancreas;
    }

    public void setParenchyma(Parenchyma parenchyma) {
        this.parenchyma = parenchyma;
    }

    public void setPeripancreatic(Peripancreatic peripancreatic) {
        this.peripancreatic = peripancreatic;
    }

    public void setArtery(Artery artery) {
        this.artery = artery;
    }

    public void setOther(OtherTemplate other) {
        this.other = other;
    }

    public void setVein(Vein vein) {
        this.vein = vein;
    }

    public void setShortcut(List<Map<Integer, String>> shortcut) {
        this.shortcut = shortcut;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
