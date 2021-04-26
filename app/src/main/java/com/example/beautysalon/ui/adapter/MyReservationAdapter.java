package com.example.beautysalon.ui.adapter;

import android.annotation.SuppressLint;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.beautysalon.R;
import com.example.beautysalon.dao.ReserveDao;
import com.example.beautysalon.dao.StylistDao;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author Lee
 * @date 2021.4.12  19:23
 * @description
 */
public class MyReservationAdapter extends BaseQuickAdapter<ReserveDao, BaseViewHolder> implements LoadMoreModule {

    private List<StylistDao> mStylistDaoList;
    private List<ReserveDao> mReserveDaoList;
    private TextView mRemainTime;
    private TextView mStatus;
    public MyReservationAdapter(@Nullable List<ReserveDao> data, List<StylistDao> stylistDaoList) {
        super(R.layout.layout_reservation_simple_item, data);
        this.mStylistDaoList = stylistDaoList;
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void convert(@NotNull BaseViewHolder holder, ReserveDao reserveDao) {
        holder.setText(R.id.reservationId, String.valueOf(reserveDao.getId()));
        holder.setText(R.id.stylistName, mStylistDaoList.get(getItemPosition(reserveDao)).getRealName());
        holder.setText(R.id.serveDate, new SimpleDateFormat("yyyy-MM-dd HH:mm")
                .format(reserveDao.getServeDate()));
        holder.setText(R.id.services, reserveDao.getServices());
        holder.setText(R.id.createDate, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(reserveDao.getCreateDate()));
        holder.setText(R.id.total, String.valueOf(reserveDao.getTotal()));
        holder.setText(R.id.payLabel, "共计 ¥");
        if (reserveDao.getStatus() == 1) {
            if (reserveDao.getServeDate().compareTo(new Date()) > 0) {
                holder.setText(R.id.status, "待服务");
            } else {
                holder.setText(R.id.status, "待评价");
            }
        } else if (reserveDao.getStatus() == 3) {
            holder.setText(R.id.status, "已取消");
        }
    }
}
