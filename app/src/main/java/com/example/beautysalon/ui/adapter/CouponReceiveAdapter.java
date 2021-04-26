package com.example.beautysalon.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.beautysalon.R;
import com.example.beautysalon.ResponseCode;
import com.example.beautysalon.RestResponse;
import com.example.beautysalon.dao.CouponDistributionDao;
import com.example.beautysalon.dao.UserDao;
import com.example.beautysalon.utils.NetClient;
import com.example.beautysalon.utils.NetworkSettings;
import com.example.beautysalon.utils.Utils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @author Lee
 * @date 2021.4.5  21:16
 * @description
 */
public class CouponReceiveAdapter extends BaseQuickAdapter<CouponDistributionDao, BaseViewHolder> implements LoadMoreModule {

    private Context mContext;
    private View mInflater;
    private List<Integer> mUserCoupon;
    private UserDao mUser;
    private HashMap<String, Object> mMap = new HashMap<>();
    private final Message mMessage = new Message();
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    public CouponReceiveAdapter(Context context, UserDao user, List<CouponDistributionDao> coupon, List<Integer> userCoupon) {
        super(R.layout.layout_coupon_item, coupon);
        this.mUser = user;
        this.mContext = context;
        this.mUserCoupon = userCoupon;
    }


    @SuppressLint("SimpleDateFormat")
    @Override
    protected void convert(@NotNull BaseViewHolder holder, CouponDistributionDao couponDistributionDao) {
        Date date = couponDistributionDao.getValidDateTo();
        Date realDate = new Date(date.getTime() - (60 * 60 * 24) * 1000);
        holder.setText(R.id.up_to, "无门槛");
        holder.setText(R.id.value, String.valueOf(couponDistributionDao.getValue()));
        holder.setText(R.id.valid, "有效期：" +
                new SimpleDateFormat("yyyy-MM-dd").format(couponDistributionDao.getValidDateFrom())
                + " 至 " + new SimpleDateFormat("yyyy-MM-dd").format(realDate));

        if (couponDistributionDao.getType() == 0) {
            holder.getView(R.id.textView2).setVisibility(View.INVISIBLE);
            holder.setText(R.id.type, "满减券");
            if (couponDistributionDao.getUpTo() != 0) {
                holder.setText(R.id.up_to, "满 " + couponDistributionDao.getUpTo() + " 可用");
            }
        } else {
            holder.getView(R.id.textView1).setVisibility(View.INVISIBLE);
            holder.setText(R.id.type, "折扣券");
        }

        if (couponDistributionDao.getNum() == 0) {
            setReceiveFailed(holder);
        }

        if (mUserCoupon.contains(couponDistributionDao.getId())) {
            setReceiveSuccess(holder);
        }

        if (((Button)holder.getView(R.id.receive)).getText().equals("领取")) {
            holder.getView(R.id.receive).setOnClickListener(v -> {
                mMap.put("userId", mUser.getUserId());
                mMap.put("coupon", JSON.toJSONString(couponDistributionDao));
                NetClient.getNetClient().callNet(NetworkSettings.RECEIVE_COUPON,
                        RequestBody.create(JSON.toJSONString(mMap), Utils.MEDIA_TYPE), new NetClient.MyCallBack() {
                            @Override
                            public void onFailure(int code) {
                                mMessage.what = ResponseCode.RECEIVE_COUPON_FAILED;
                                mHandler.post(()-> Utils.showMessage(mContext, mMessage));
                                System.out.println("请求数据失败，请检查网络后");
                            }

                            @Override
                            public void onResponse(Response response) throws IOException {
                                if (response.isSuccessful()) {
                                    ResponseBody responseBody = response.body();
                                    if (responseBody != null) {
                                        RestResponse restResponse = JSON.parseObject(responseBody.string(), RestResponse.class);
                                        mMessage.what = restResponse.getCode();
                                        if (mMessage.what == ResponseCode.RECEIVE_COUPON_SUCCESS) {
                                            mHandler.post(() -> {
                                                Utils.showMessage(mContext, mMessage);
                                                setReceiveSuccess(holder);
                                            });
                                        } else {
                                            mHandler.post(() -> {
                                                Utils.showMessage(mContext, mMessage);
                                                setReceiveFailed(holder);
                                            });
                                        }
                                    } else {
                                        mMessage.what = ResponseCode.EMPTY_RESPONSE;
                                        mHandler.post(()->Utils.showMessage(mContext, mMessage));
                                    }
                                } else {
                                    mMessage.what = ResponseCode.SERVER_ERROR;
                                    mHandler.post(()->Utils.showMessage(mContext, mMessage));
                                }
                            }
                        });
            });
        } else if (((Button)holder.getView(R.id.receive)).equals("已抢光")) {
            (holder.getView(R.id.receive)).setOnClickListener(v -> Toast.makeText(mContext, "该优惠券已被抢光，看看其他优惠券吧~", Toast.LENGTH_SHORT).show());
        } else if (((Button)holder.getView(R.id.receive)).getText().equals("已领取")) {
            (holder.getView(R.id.receive)).setOnClickListener(v -> Toast.makeText(mContext, "您已拥有该优惠券，请前往我的优惠券查看吧~", Toast.LENGTH_SHORT).show());
        }
    }

    public void setReceiveSuccess(BaseViewHolder holder) {
        holder.getView(R.id.receive).setOnClickListener(v -> Toast.makeText(mContext, "不可重复领取", Toast.LENGTH_SHORT).show());
        holder.setText(R.id.receive, "已领取");
        holder.setTextColor(R.id.receive, Color.parseColor("#fdbc94"));
        holder.setBackgroundResource(R.id.textView, R.color.coupon_received);
        holder.setBackgroundResource(R.id.type, R.drawable.coupon_type_shape_success);
    }

    public void setReceiveFailed(BaseViewHolder holder) {
        holder.setText(R.id.receive, "已抢光");
        holder.setTextColor(R.id.receive, Color.parseColor("#757575"));
        holder.setBackgroundResource(R.id.textView, R.color.dialog_text);
        holder.setBackgroundResource(R.id.type, R.drawable.coupon_type_shape_failed);
    }
}