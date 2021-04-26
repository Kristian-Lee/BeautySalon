package com.example.beautysalon.dao;

import java.io.Serializable;

import lombok.Data;

/**
 * @author Lee
 * @date 2021.3.26  22:17
 * @description 评分表
 */
@Data
public class EvaluationDao implements Serializable {
    int id;
    int stylistId;
    int quantities;
    float rate;
    float popularity;
    int positive;
}
