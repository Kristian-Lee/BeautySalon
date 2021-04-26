package com.example.beautysalon.dao;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * @author Lee
 * @date 2021.4.5  16:22
 * @description
 */
@Data
public class BalanceDao implements Serializable {
    int id;
    float payments;
    int type;
    Date createDate;
    String description;
    int userId;
}
