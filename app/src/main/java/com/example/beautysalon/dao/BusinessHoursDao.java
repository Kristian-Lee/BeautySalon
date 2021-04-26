package com.example.beautysalon.dao;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * @author Lee
 * @date 2021.4.9  16:09
 * @description
 */
@Data
public class BusinessHoursDao implements Serializable {
    int id;
    int stylistId;
    Date dateFrom;
    Date dateTo;
}
