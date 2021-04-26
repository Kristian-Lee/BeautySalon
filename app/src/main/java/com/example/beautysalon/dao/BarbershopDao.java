package com.example.beautysalon.dao;

import java.io.Serializable;

import lombok.Data;

/**
 * @author Lee
 * @date 2021.3.28  21:11
 * @description
 */
@Data
public class BarbershopDao implements Serializable {
    int barbershopId;
    String barbershopName;
    String address;
    int OwnerId;
    String picture;
}
