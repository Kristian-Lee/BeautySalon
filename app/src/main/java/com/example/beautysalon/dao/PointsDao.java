package com.example.beautysalon.dao;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * @author Lee
 * @date 2021.4.1  01:35
 * @description
 */
@Data
public class PointsDao implements Serializable {
    int id;
    int userId;
    int value;
    Date createDate;
    int type;
}
