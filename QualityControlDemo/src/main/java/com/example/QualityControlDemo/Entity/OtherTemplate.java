package com.example.QualityControlDemo.Entity;

public class OtherTemplate {
    //肝脏病变
    public Boolean liverLesions;
    //腹膜
    public Boolean peritoneum;
    //腹水
    public Boolean isAscites;
    //胰周异常
    public Boolean isPancreasAbnormal;
    //可疑淋巴结
    public String lymph;
    public OtherTemplate() {

    }

    public void setAscites(Boolean ascites) {
        isAscites = ascites;
    }

    public void setPeritoneum(Boolean peritoneum) {
        this.peritoneum = peritoneum;
    }

    public void setLymph(String lymph) {
        this.lymph = lymph;
    }

    public void setPancreasAbnormal(Boolean pancreasAbnormal) {
        isPancreasAbnormal = pancreasAbnormal;
    }

    public void setLiverLesions(Boolean liverLesions) {
        this.liverLesions = liverLesions;
    }
}
