package com.example.beautysalon.dao;

import java.io.Serializable;

import lombok.Data;

/**
 * @author Lee
 * @date 2021.4.9  16:10
 * @description
 */
@Data
public class ServicesDao implements Serializable {
    int id;
    int barbershopId;
    String name;
    int value;
    int time;
}
