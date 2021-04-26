package com.example.beautysalon.ui.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.beautysalon.R;
import com.example.beautysalon.dao.BalanceDao;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author Lee
 * @date 2021.4.5  18:40
 * @description
 */
public class MyBalanceAdapter extends BaseQuickAdapter<BalanceDao, BaseViewHolder> implements LoadMoreModule {
    
    public MyBalanceAdapter(List<BalanceDao> balanceDaoList) {
        super(R.layout.layout_balance_item, balanceDaoList);
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, BalanceDao balanceDao) {
        switch (balanceDao.getType()) {
            case 1:
                baseViewHolder.setText(R.id.value, balanceDao.getPayments() + "");
                baseViewHolder.setTextColor(R.id.value, Color.parseColor("#757575"));
                break;
            default:
                baseViewHolder.setText(R.id.value, "+" + balanceDao.getPayments());
                baseViewHolder.setTextColor(R.id.value, Color.parseColor("#fb9250"));
                break;
        }
        baseViewHolder.setText(R.id.type, balanceDao.getDescription());
        baseViewHolder.setText(R.id.date, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(balanceDao.getCreateDate()));
    }
}
