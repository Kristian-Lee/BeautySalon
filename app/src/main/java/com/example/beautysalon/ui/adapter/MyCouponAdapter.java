package com.example.beautysalon.ui.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.beautysalon.R;
import com.example.beautysalon.dao.CouponDao;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author Lee
 * @date 2021.4.6  01:56
 * @description
 */
public class MyCouponAdapter extends BaseQuickAdapter<CouponDao, BaseViewHolder> implements LoadMoreModule {


    public MyCouponAdapter(List<CouponDao> couponDaoList) {
        super(R.layout.layout_coupon_item, couponDaoList);
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void convert(@NotNull BaseViewHolder holder, CouponDao couponDao) {
        Date date = couponDao.getValidDateTo();
        Date realDate = new Date(date.getTime() - (60 * 60 * 24) * 1000);
        holder.setText(R.id.up_to, "无门槛");
        holder.setText(R.id.value, String.valueOf(couponDao.getValue()));
        holder.setText(R.id.valid, "有效期：" +
                new SimpleDateFormat("yyyy-MM-dd").format(couponDao.getValidDateFrom())
                + " 至 " + new SimpleDateFormat("yyyy-MM-dd").format(realDate));

        if (couponDao.getType() == 0) {
            holder.getView(R.id.textView2).setVisibility(View.INVISIBLE);
            holder.setText(R.id.type, "满减券");
            if (couponDao.getUpTo() != 0) {
                holder.setText(R.id.up_to, "满 " + couponDao.getUpTo() + " 可用");
            }
        } else {
            holder.getView(R.id.textView1).setVisibility(View.INVISIBLE);
            holder.setText(R.id.type, "折扣券");
        }

        if (couponDao.getIsUsed() == 0) {
            holder.setText(R.id.receive, "待使用");
        }

        if (couponDao.getIsUsed() == 1) {
            setUsed(holder);
        } else if (couponDao.getValidDateTo().compareTo(new Date()) < 0) {
            setInvalid(holder);
        }
    }

    public void setUsed(BaseViewHolder holder) {
        holder.setText(R.id.receive, "已使用");
        holder.setTextColor(R.id.receive, Color.parseColor("#fdbc94"));
        holder.setBackgroundResource(R.id.textView, R.color.coupon_received);
        holder.setBackgroundResource(R.id.type, R.drawable.coupon_type_shape_success);
    }

    public void setInvalid(BaseViewHolder holder) {
        holder.setText(R.id.receive, "已失效");
        holder.setTextColor(R.id.receive, Color.parseColor("#757575"));
        holder.setBackgroundResource(R.id.textView, R.color.dialog_text);
        holder.setBackgroundResource(R.id.type, R.drawable.coupon_type_shape_failed);
    }
}
