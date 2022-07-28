package com.shu.model;

/**
 * @Author shu
 * @Version 1.0
 * @Date: 2022/07/28/ 10:10
 * @Description 选择项实体类
 **/
public class Select {
    private String name;
    private String value;

    public Select(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public Select() {

    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


}
