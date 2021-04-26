package com.example.beautysalon.dao;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * @author Lee
 * @date 2021.3.30  18:57
 * @description
 */
@Data
public class CouponDao implements Serializable {
    int id;
    int userId;
    int couponId;
    int value;
    int upTo;
    Date validDateFrom;
    Date validDateTo;
    Date receiveDate;
    Date usedDate;
    int isUsed;
    int type;
}
