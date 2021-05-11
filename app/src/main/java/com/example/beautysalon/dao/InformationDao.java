package com.example.beautysalon.dao;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * @author Lee
 * @date 2021.5.11  22:49
 * @description
 */
@Data
public class InformationDao implements Serializable {
    Integer id;
    Integer adminId;
    String title;
    String content;
    String webView;
    Date createDate;
}
