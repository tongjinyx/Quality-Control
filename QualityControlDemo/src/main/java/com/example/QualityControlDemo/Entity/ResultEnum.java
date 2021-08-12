package com.example.QualityControlDemo.Entity;

public enum ResultEnum {
    UNKNOWN_ERROR(-1,"未知错误"),
    SUCCESS(200,"成功"),
    TEXT_NOT_EXIST(301,"文本ID不存在"),
    TEXT_NULL(303,"文本为空"),
    TEXT_IS_EXIST(302,"文本已存在"),
    DOCTOR_NOT_EXISTS(401,"医生ID不存在"),
    DATE_FORMAT_ERROR(501,"日期格式错误"),
    INVALID_VALUE(601,"数值不合理"),;
    private Integer code;
    private String msg;
    ResultEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
