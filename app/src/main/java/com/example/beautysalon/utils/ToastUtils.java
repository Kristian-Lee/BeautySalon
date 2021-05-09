package com.example.beautysalon.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * @author Lee
 * @date 2021.5.9  16:23
 * @description
 */
public class ToastUtils {
    private static  ToastUtils mToastUtils;
    private static Toast mToast;

    private ToastUtils(Context context){
        if (null == mToast){
            mToast = Toast.makeText(context.getApplicationContext(),"",Toast.LENGTH_LONG);
        }
    }

    public static ToastUtils getInstance(Context context) {
        if (mToastUtils == null){
            mToastUtils = new ToastUtils(context.getApplicationContext());
        }
        return mToastUtils;
    }

    public void showShortToast(String mString){
        if (mToast == null){
            return;
        }
        mToast.setText(mString);
        mToast.setDuration(Toast.LENGTH_SHORT);
        // mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.show();
    }

    public void showLongToast(String mString){
        if (mToast == null){
            return;
        }
        mToast.setText(mString);
        mToast.setDuration(Toast.LENGTH_LONG);
        // mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.show();
    }

}
