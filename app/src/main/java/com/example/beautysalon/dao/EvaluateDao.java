package com.example.beautysalon.dao;

import java.util.Date;

import lombok.Data;

/**
 * @author Lee
 * @date 2021.4.13  02:18
 * @description
 */
@Data
public class EvaluateDao {
    int Id;
    int orderId;
    int stylistId;
    int userId;
    String contact;
    Date createDate;
    float rate;
}
