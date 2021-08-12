package com.example.QualityControlDemo.Entity;

//胰腺本身
public class Pancreas {
    //大小
    public String size;
    //形态
    public String shape;
    //增大
    public String increaseNature;
    //边缘
    public String margin;
    //密度
    public String density;
    //异常密度影
    public Boolean isDensityShadowUnusual;
    //异常强化
    public Boolean isStrengtheningStoveUnusual;

    public Pancreas(){

    }

    public void setDensity(String density) {
        this.density = density;
    }

    public void setIncreaseNature(String increaseNature) {
        this.increaseNature = increaseNature;
    }

    public void setIsDensityShadowUnusual(Boolean isDensityShadowUnusual) {
        this.isDensityShadowUnusual = isDensityShadowUnusual;
    }

    public void setIsStrengtheningStoveUnusual(Boolean isStrengtheningStoveUnusual) {
        this.isStrengtheningStoveUnusual = isStrengtheningStoveUnusual;
    }

    public void setMargin(String margin) {
        this.margin = margin;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setShape(String shape) {
        this.shape = shape;
    }


}
