package com.example.beautysalon.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Message;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.alibaba.fastjson.JSON;
import com.example.beautysalon.R;
import com.example.beautysalon.ResponseCode;

import org.bouncycastle.jcajce.provider.digest.Keccak;
import org.bouncycastle.util.encoders.Hex;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;

public class Utils {
    public static final MediaType MEDIA_TYPE = MediaType.parse("application/json;charset=utf-8");
    private static final Keccak.Digest512 digest512 = new Keccak.Digest512();

    public static String encrypt(String origin) {
        return new String(Hex.encode(digest512.digest(origin.getBytes(StandardCharsets.UTF_8))));
    }

    public static boolean saveLoginInfo(Context context, String userName, String password, boolean isLogin) {
        SharedPreferences.Editor editor = context.getSharedPreferences("data", Context.MODE_PRIVATE).edit();
        editor.putString("userName", userName);
        editor.putString("password", password);
        editor.putBoolean("isLogin", true);
        editor.apply();
        return true;
    }

    public static boolean saveToken(Context context, String token) {
        SharedPreferences.Editor editor = context.getSharedPreferences("data", Context.MODE_PRIVATE).edit();
        editor.putString("token", token);
        editor.apply();
        return true;
    }

    public static String getResponseMessage(int code) {
        String message = "";
        switch (code) {
            case ResponseCode.SIGN_IN_SUCCESS:
                message = "登录成功";
                break;
            case ResponseCode.SIGN_UP_SUCCESS:
                message = "注册成功";
                break;
            case ResponseCode.SIGN_IN_FAILED:
                message = "用户名或密码错误";
                break;
            case ResponseCode.SIGN_UP_FAILED:
                message = "注册失败";
                break;
            case ResponseCode.DELETE_FAILED:
                message = "删除失败";
                break;
            case ResponseCode.DELETE_SUCCESS:
                message = "删除成功,自动退出";
                break;
            case ResponseCode.UPDATE_USER_SUCCESS:
                message = "更新用户信息成功";
                break;
            case ResponseCode.UPDATE_USER_FAILED:
                message = "更新用户信息失败";
                break;
            case ResponseCode.PHONE_EXIST_ERROR:
                message = "该手机号已被注册！请更改手机号重试";
                break;
            case ResponseCode.EMPTY_RESPONSE:
                message = "响应体为空";
                break;
            case ResponseCode.SERVER_ERROR:
                message = "服务器错误";
                break;
            case ResponseCode.JSON_SERIALIZATION:
                message = "JSON序列化错误";
                break;
            case ResponseCode.EXIT_SUCCESS:
                message = "退出成功";
                break;
            case ResponseCode.REQUEST_FAILED:
                message = "请求发送失败";
                break;
            case ResponseCode.UNCHANGED_INFORMATION:
                message = "未修改信息";
                break;
            case ResponseCode.USER_EXIST_FAILED:
                message = "用户已存在";
                break;
            case ResponseCode.REQUEST_BARBERSHOP_DATA_FAILED:
                message = "获取美容美发商店数据失败";
                break;
            case ResponseCode.REQUEST_STYLIST_DATA_FAILED:
                message = "获取发型师数据失败";
                break;
            case ResponseCode.REQUEST_COMMENT_DATA_FAILED:
                message = "获取评论数据失败";
                break;
            case ResponseCode.REQUEST_COUPON_DISTRIBUTION_DATA_FAILED:
                message = "获取优惠券数据失败";
                break;
            case ResponseCode.RECEIVE_COUPON_SUCCESS:
                message = "优惠券领取成功";
                break;
            case ResponseCode.RECEIVE_COUPON_FAILED:
                message = "优惠券领取失败";
                break;
            case ResponseCode.REQUEST_COUPON_DATA_FAILED:
                message = "优惠券数据获取失败";
                break;
            case ResponseCode.RECEIVE_POINTS_FAILED:
                message = "签到失败";
                break;
            case ResponseCode.REQUEST_USER_DATA_FAILED:
                message = "获取用户数据失败";
                break;
            case ResponseCode.REQUEST_BALANCE_DATA_FAILED:
                message = "获取余额数据失败";
                break;
            case ResponseCode.REQUEST_BUSINESS_HOURS_DATA_FAILED:
                message = "获取预约参数失败";
                break;
            case ResponseCode.RESERVE_FAILED:
                message = "预约失败,预定时间已爆满或在非营业时间预约";
                break;
            case ResponseCode.RESERVE_SUCCESS:
                message = "预约成功，正前往支付";
                break;
            case ResponseCode.CANCEL_RESERVE_SUCCESS:
                message = "取消预约成功";
                break;
            case ResponseCode.CANCEL_RESERVE_FAILED:
                message = "取消预约失败";
                break;
            case ResponseCode.PAY_FAILED:
                message = "支付失败";
                break;
            case ResponseCode.PAY_SUCCESS:
                message = "支付成功";
                break;
            case ResponseCode.PAY_TWICE_ERROR:
                message = "您已支付成功！请勿重复支付";
                break;
            case ResponseCode.REQUEST_RESERVATION_DATA_FAILED:
                message = "获取预约数据失败";
                break;
            case ResponseCode.REQUEST_VALID_POINTS_COUPON_FAILED:
                message = "获取可用的优惠券、积分数据失败";
                break;
            case ResponseCode.EVALUATE_SUCCESS:
                message = "发表成功";
                break;
            case ResponseCode.EVALUATE_FAILED:
                message = "发表失败";
                break;
            case ResponseCode.REQUEST_USER_COMMENT_FAILED:
                message = "获取用户评论数据失败";
                break;
            case ResponseCode.UPDATE_AVATAR_FAILED:
                message = "更新头像失败";
                break;
            case ResponseCode.REQUEST_STYLIST_RESERVATION_FAILED:
                message = "获取预约数据失败";
                break;
            case ResponseCode.REQUEST_STYLIST_MAIN_DATA_FAILED:
                message = "获取发型师主页数据失败";
                break;
            case ResponseCode.UPDATE_STYLIST_FAILED:
                message = "更新发型师信息失败";
                break;
            case ResponseCode.UPDATE_STYLIST_SUCCESS:
                message = "更新发型师信息成功";
                break;
            case ResponseCode.REQUEST_USER_COMMENT_DATA_FAILED:
                message = "获取用户评价数据失败";
                break;
            case ResponseCode.REQUEST_RESERVATION_COMMENT_DATA_FAILED:
                message = "获取服务历史数据失败";
        }
        return message;
    }

    public static void showMessage(Context context, Message message) {
        Toast.makeText(context, getResponseMessage(message.what), Toast.LENGTH_SHORT).show();
    }

    public static void showAlert(Context context, String info, DialogInterface.OnDismissListener onDismiss) {
        new AlertDialog.Builder(context)
                .setMessage(info)
                .setPositiveButton(R.string.confirm, null)
                .show();
    }

    public static List jsonToList(Class className, List list) {
        List result = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            result.add(JSON.parseObject(list.get(i).toString(), className));
        }
        return result;
    }
}
