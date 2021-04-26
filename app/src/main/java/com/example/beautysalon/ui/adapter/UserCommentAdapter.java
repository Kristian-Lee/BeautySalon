package com.example.beautysalon.ui.adapter;

import android.annotation.SuppressLint;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.beautysalon.R;
import com.example.beautysalon.dao.EvaluateDao;
import com.example.beautysalon.dao.ReserveDao;
import com.example.beautysalon.dao.UserDao;
import com.willy.ratingbar.ScaleRatingBar;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.List;

import cn.gavinliu.android.lib.shapedimageview.ShapedImageView;

/**
 * @author Lee
 * @date 2021.4.14  14:52
 * @description
 */
public class UserCommentAdapter extends BaseQuickAdapter<EvaluateDao, BaseViewHolder> implements LoadMoreModule {
    private List<ReserveDao> mReserveDaoList;
    private List<UserDao> mUserDaoList;
    public UserCommentAdapter(List<EvaluateDao> evaluateDaoList, List<ReserveDao> reserveDaoList, List<UserDao> userDaoList) {
        super(R.layout.layout_user_comment_item, evaluateDaoList);
        this.mReserveDaoList = reserveDaoList;
        this.mUserDaoList = userDaoList;
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void convert(@NotNull BaseViewHolder holder, EvaluateDao evaluateDao) {

        int position = getItemPosition(evaluateDao);
        ScaleRatingBar rate = holder.getView(R.id.rate);
        ShapedImageView avatar = holder.getView(R.id.avatar);
        holder.setText(R.id.user_name, mUserDaoList.get(position).getUserName());
        rate.setRating(evaluateDao.getRate());
        holder.setText(R.id.serveDate, new SimpleDateFormat("yyyy-MM-dd").format(mReserveDaoList.get(position).getServeDate()));
        holder.setText(R.id.services, mReserveDaoList.get(position).getServices());
        holder.setText(R.id.contact, evaluateDao.getContact());
        holder.setText(R.id.reservationId, String.valueOf(mReserveDaoList.get(position).getId()));
        holder.setText(R.id.date, new SimpleDateFormat("yyyy-MM-dd").format(evaluateDao.getCreateDate()));
        Glide.with(getContext())
                .load(mUserDaoList.get(position).getAvatar())
                .centerCrop()
                .placeholder(R.drawable.dog)
                .into(avatar);
    }
}
