package com.example.beautysalon.ui.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.beautysalon.R;
import com.example.beautysalon.dao.CouponDao;
import com.example.beautysalon.databinding.ActivityPayBinding;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Lee
 * @date 2021.4.10  15:54
 * @description
 */
public class CouponPayAdapter extends BaseQuickAdapter<CouponDao, BaseViewHolder> {

    private int mLastPosition = -1;
    private List<CouponDao> mCouponDaoList;
    private ActivityPayBinding mBinding;

    public CouponPayAdapter(@Nullable List<CouponDao> data, ActivityPayBinding binding) {
        super(R.layout.layout_pay_coupon_item, data);
        this.mCouponDaoList = data;
        this.mBinding = binding;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void convert(@NotNull BaseViewHolder holder, CouponDao couponDao) {

        TextView textView = holder.getView(R.id.couponList);
        CheckBox checkBox = holder.getView(R.id.checkbox);
        if (couponDao.getType() == 0) {
            textView.setText("满" + couponDao.getUpTo() + "减" + couponDao.getValue());
        } else {
            textView.setText("通用" + couponDao.getValue() + "折券");
        }
        if (getItemPosition(couponDao) == mLastPosition) {
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }

        if (mLastPosition == -1) {
            mBinding.couponSymbol.setVisibility(View.INVISIBLE);
            mBinding.coupon.setText("有" + mCouponDaoList.size() + "张优惠券可用");
        }
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    float total = Float.parseFloat(mBinding.total.getText().toString());
                    float realPay = Float.parseFloat(mBinding.realPay.getText().toString());
                    float result;

                    if (mLastPosition != -1) {
                        CouponDao coupon = mCouponDaoList.get(mLastPosition);
                        if (coupon.getType() == 0) {
                            realPay += coupon.getValue();
                        } else {
                            realPay += total * (10 - coupon.getValue()) / 10;
                        }
                    }


                    if (couponDao.getType() == 0) {
                        result = realPay - couponDao.getValue();
                        mBinding.coupon.setText(String.valueOf(couponDao.getValue()));
                    } else {
                        result = realPay - total * (10 - couponDao.getValue()) / 10;
                        mBinding.coupon.setText(String.valueOf(total * (10 - couponDao.getValue()) / 10));

                    }
                    mBinding.couponSymbol.setVisibility(View.VISIBLE);
                    mBinding.coupon.setTextColor(Color.parseColor("#e53d3d"));
                    mBinding.couponSymbol.setTextColor(Color.parseColor("#e53d3d"));
                    mBinding.realPay.setText(String.valueOf(result));

                    mLastPosition = getItemPosition(couponDao);
                    notifyDataSetChanged();

                } else {
                    float total = Float.parseFloat(mBinding.total.getText().toString());
                    float realPay = Float.parseFloat(mBinding.realPay.getText().toString());
                    float result;

                    if (mLastPosition == getItemPosition(couponDao)) {
                        mLastPosition = -1;
                        if (couponDao.getType() == 0) {
                            result = realPay + couponDao.getValue();
                            mBinding.realPay.setText(String.valueOf(result));
                        } else {
                            result = realPay + total * (10 - couponDao.getValue()) / 10;
                            mBinding.realPay.setText(String.valueOf(result));
                        }

                        mBinding.couponSymbol.setVisibility(View.INVISIBLE);
                        mBinding.coupon.setText("有" + mCouponDaoList.size() + "张优惠券可用");

                        mBinding.coupon.setTextColor(Color.parseColor("#757575"));
                        mBinding.couponSymbol.setTextColor(Color.parseColor("#e53d3d"));
                    }
                }
            }
        });
    }

    public float getCouponValue() {
        if (mLastPosition == -1) {
            return 0;
        } else {
            CouponDao couponDao = mCouponDaoList.get(mLastPosition);
            if (couponDao.getType() == 0) {
                return couponDao.getValue();
            } else {
                float total = Float.parseFloat(mBinding.total.getText().toString());
                return total - total * (10 - couponDao.getValue()) / 10;
            }

        }
    }

    public int getLastPosition() {
        return mLastPosition;
    }
}
