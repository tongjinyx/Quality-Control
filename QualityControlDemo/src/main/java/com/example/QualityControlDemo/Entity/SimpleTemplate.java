package com.example.QualityControlDemo.Entity;

public class SimpleTemplate {
    //部位名称
    public String name;
    //实性肿块接触程度
    public String SolidMassContactDegree;
    //实性肿块边界模糊/接触面呈条状
    public String SolidMassBoundariesBlurred;
    //局部血管狭窄或轮廓不规则
    public Boolean isArteryBloodVesselStenosis;
    //侵犯腹腔动脉干
    public Boolean isCeliacArteryInvaded;
    //侵犯肝动脉分叉部或肝左右动脉
    public Boolean isHepaticArteryInvaded;
    //侵犯肠系膜上静脉空肠引流支
    public Boolean isMesenteryVeinInvaded;
    //侵犯空肠动脉
    public Boolean isArteryIntestinalInvaded;
    //解剖变异
    public String arteryVariation;
    //变异血管接触
    public Boolean isVariableVascularContact;
    public SimpleTemplate() {

    }

    public void setArteryBloodVesselStenosis(Boolean arteryBloodVesselStenosis) {
        isArteryBloodVesselStenosis = arteryBloodVesselStenosis;
    }

    public void setArteryIntestinalInvaded(Boolean arteryIntestinalInvaded) {
        isArteryIntestinalInvaded = arteryIntestinalInvaded;
    }

    public void setArteryVariation(String arteryVariation) {
        arteryVariation = arteryVariation;
    }

    public void setMesenteryVeinInvaded(Boolean mesenteryVeinInvaded) {
        isMesenteryVeinInvaded = mesenteryVeinInvaded;
    }

    public void setCeliacArteryInvaded(Boolean celiacArteryInvaded) {
        isCeliacArteryInvaded = celiacArteryInvaded;
    }

    public void setHepaticArteryInvaded(Boolean hepaticArteryInvaded) {
        isHepaticArteryInvaded = hepaticArteryInvaded;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSolidMassBoundariesBlurred(String solidMassBoundariesBlurred) {
        SolidMassBoundariesBlurred = solidMassBoundariesBlurred;
    }

    public void setSolidMassContactDegree(String solidMassContactDegree) {
        SolidMassContactDegree = solidMassContactDegree;
    }

    public void setVariableVascularContact(Boolean variableVascularContact) {
        isVariableVascularContact = variableVascularContact;
    }


}
