package com.example.beautysalon.dao;

import lombok.Data;

/**
 * @author Lee
 * @date 2021.3.28  22:12
 * @description
 */
@Data
public class CommentDao {
    int commentId;
    int orderId;
    int stylistId;
    int userId;
    String contact;
    String createDate;
    String userName;
    String userAvatar;
    float rate;
}
