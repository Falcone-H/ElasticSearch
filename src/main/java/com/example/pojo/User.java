package com.example.pojo;

import lombok.Data;

/**
 * @description: TODO
 * @author: Falcone
 * @date: 2021/9/28 22:34
 */

@Data
public class User {
    private String name;
    private String sex;
    private int age;

    public User(String name, String sex, int age) {
        this.name = name;
        this.sex = sex;
        this.age = age;
    }
}
