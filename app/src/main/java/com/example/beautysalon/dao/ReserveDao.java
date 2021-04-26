package com.example.beautysalon.dao;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * @author Lee
 * @date 2021.4.10  12:59
 * @description
 */
@Data
public class ReserveDao implements Serializable {
    int id;
    int userId;
    int stylistId;
    int status;
    Date createDate;
    Date payDate;
    Date serveDate;
    float total;
    int points;
    int coupon;
    float value;
    String services;
    int takeUp;
}
