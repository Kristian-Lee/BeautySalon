package com.example.beautysalon.ui.adapter;

import android.annotation.SuppressLint;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.beautysalon.R;
import com.example.beautysalon.dao.EvaluateDao;
import com.example.beautysalon.dao.ReserveDao;
import com.example.beautysalon.dao.StylistDao;
import com.willy.ratingbar.ScaleRatingBar;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author Lee
 * @date 2021.4.13  10:48
 * @description
 */
public class MyCommentAdapter extends BaseQuickAdapter<EvaluateDao, BaseViewHolder> implements LoadMoreModule {

    private List<StylistDao> mStylistDaoList;
    private List<ReserveDao> mReserveDaoList;

    public MyCommentAdapter(List<EvaluateDao> evaluateDaoList, List<StylistDao> stylistDaoList, List<ReserveDao> reserveDaoList) {
        super(R.layout.layout_my_comment_item, evaluateDaoList);
        this.mStylistDaoList = stylistDaoList;
        this.mReserveDaoList = reserveDaoList;
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void convert(@NotNull BaseViewHolder holder, EvaluateDao evaluateDao) {
        holder.setText(R.id.reservationId, String.valueOf(mReserveDaoList.get(getItemPosition(evaluateDao)).getId()));
        holder.setText(R.id.stylistRealName, mStylistDaoList.get(getItemPosition(evaluateDao)).getRealName());
        holder.setText(R.id.services, mReserveDaoList.get(getItemPosition(evaluateDao)).getServices());
        holder.setText(R.id.contact, evaluateDao.getContact());
        holder.setText(R.id.date, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(evaluateDao.getCreateDate()));
        ScaleRatingBar rate = holder.getView(R.id.rate);
        rate.setRating(evaluateDao.getRate());
    }
}
