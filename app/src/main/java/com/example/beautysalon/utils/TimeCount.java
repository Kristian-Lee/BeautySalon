package com.example.beautysalon.utils;

import android.os.CountDownTimer;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Lee
 * @date 2021.4.10  11:51
 * @description
 */
public class TimeCount extends CountDownTimer {
    private Date mCreateDate;
    private TextView mTextView;
    private long mInterval;
    private CallBack mCallBack;

    public TimeCount(long millisInFuture, long countDownInterval, Date createDate, TextView textView, CallBack callBack) {
        super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
        this.mTextView = textView;
        this.mInterval = System.currentTimeMillis() - createDate.getTime();
        this.mCallBack = callBack;
    }
    @Override
    public void onFinish() {// 计时完毕时触发
        mCallBack.done();
    }
    @Override
    public void onTick(long millisUntilFinished) {// 计时过程显示
        Date date = new Date(millisUntilFinished - mInterval);


        String time = new SimpleDateFormat("mm:ss").format(date);
        mTextView.setText("请在"+time+"内完成付款");

    }
    public interface CallBack {
        void done();
    }
}
