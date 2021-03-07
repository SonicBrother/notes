package com.qf.bean;

import java.io.Serializable;

public class UserVO implements Serializable {

    @Override
    public String toString() {
        return "UserVO{" +
                "age=" + age +
                ", name='" + name + '\'' +
                '}';
    }

    private Integer age;
    private String name;

    public UserVO(Integer age, String name) {
        this.age = age;
        this.name = name;
    }

    public UserVO() {
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
