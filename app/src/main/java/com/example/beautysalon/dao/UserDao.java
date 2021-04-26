package com.example.beautysalon.dao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Lee
 * @date 2021.3.15  00:28
 * @description 用户表
 */

@Data
public class UserDao implements Serializable {
    private int userId;
    private String userName;
    private String password;
    private int role;
    private String avatar;
    private Date birthday;
    private String phone;
    private String hobby;
    private int points;
    private float money;
    public UserDao(String userName, String password, int role, String phone,
                   String hobby) {
        this.userName = userName;
        this.password = password;
        this.role = role;
        avatar = "";
        birthday = null;
        this.phone = phone;
        this.hobby = hobby;
        points = 0;
    }
    public UserDao(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }
    public UserDao() {}
}
