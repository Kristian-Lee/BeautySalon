package com.example.beautysalon.ui.adapter;

import android.annotation.SuppressLint;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.beautysalon.R;
import com.example.beautysalon.dao.ReserveDao;
import com.example.beautysalon.dao.UserDao;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author Lee
 * @date 2021.4.14  01:30
 * @description
 */
public class StylistReservationAdapter extends BaseQuickAdapter<ReserveDao, BaseViewHolder> implements LoadMoreModule {
    private List<UserDao> mUserDaoList;
    public StylistReservationAdapter(List<ReserveDao> reserveDaoList, List<UserDao> userDaoList) {
        super(R.layout.layout_stylist_reservation_item, reserveDaoList);
        this.mUserDaoList = userDaoList;
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void convert(@NotNull BaseViewHolder holder, ReserveDao reserveDao) {
        holder.setText(R.id.reservationId, String.valueOf(reserveDao.getId()));
        holder.setText(R.id.userName, mUserDaoList.get(getItemPosition(reserveDao)).getUserName());
        holder.setText(R.id.services, reserveDao.getServices());
        holder.setText(R.id.hobby, mUserDaoList.get(getItemPosition(reserveDao)).getHobby());
        holder.setText(R.id.serveDate, new SimpleDateFormat("dd日 HH:mm").format(reserveDao.getServeDate()));
    }
}
