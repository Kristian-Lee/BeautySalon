package com.example.beautysalon.dao;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * @author Lee
 * @date 2021.3.30  00:44
 * @description
 */
@Data
public class CouponDistributionDao implements Serializable {
    int id;
    int adminId;
    int value;
    int upTo;
    Date validDateFrom;
    Date validDateTo;
    int type;
    int num;
    int total;
}
