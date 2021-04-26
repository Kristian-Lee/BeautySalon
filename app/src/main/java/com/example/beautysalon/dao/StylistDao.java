package com.example.beautysalon.dao;

import java.io.Serializable;

import lombok.Data;

/**
 * @author Lee
 * @date 2021.3.24  11:30
 * @description 发型师表
 */
@Data
public class StylistDao implements Serializable {
    private int stylistId;
    private String stylistName;
    private String password;
    private int role;
    private String avatar;
    private String phone;
    private String realName;
    private int barbershopId;
    private String speciality;
    private int isPassed;
    private int workingYears;

    public StylistDao(String stylistName, String password) {
        this.stylistName = stylistName;
        this.password = password;
    }
    public StylistDao(String stylistName, String password, int role, String phone,
                      String speciality, int barbershopId, String realName, int workingYears) {
        this.stylistName = stylistName;
        this.password = password;
        this.role = role;
        avatar = "";
        this.phone = phone;
        this.speciality = speciality;
        this.barbershopId = barbershopId;
        this.realName = realName;
        isPassed = 1;
        this.workingYears = workingYears;
    }
    public StylistDao(){}
}
