package com.example.beautysalon.dao;

import java.util.Date;

import lombok.Data;

/**
 * @author Lee
 * @date 2021.4.14  18:02
 * @description
 */
@Data
public class ReservationBean {
    int reservationId;
    Date serveDate;
    String userName;
    String services;
    String hobby;
}
