package com.example.QualityControlDemo.Entity;

public enum StandardEnum {
    DIAGNOSE_UNCLEAR(1,"未明确影响诊断结论"),
    EXAMINATION_UNRECORDED(2,"未标明检查名称"),
    PANCREAS_ERROR(3,""),
    ARTERY_ERROR(4,""),
    VEIN_ERROR(5,""),
    OTHER_ERROR(6,""),
    ;
    private Integer code;
    private String msg;
    StandardEnum(Integer code, String msg) {
        this.code=code;
        this.msg=msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
