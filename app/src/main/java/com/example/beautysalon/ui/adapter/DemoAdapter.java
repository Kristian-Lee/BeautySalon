package com.example.beautysalon.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.beautysalon.R;
import com.example.beautysalon.dao.EvaluationDao;
import com.example.beautysalon.dao.StylistDao;
import com.example.beautysalon.ui.activity.StylistDetailActivity;
import com.willy.ratingbar.ScaleRatingBar;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import cn.gavinliu.android.lib.shapedimageview.ShapedImageView;

/**
 * @author Lee
 * @date 2021.4.6  02:25
 * @description
 */
public class DemoAdapter extends BaseQuickAdapter<StylistDao, BaseViewHolder> implements LoadMoreModule {

    private Context mContext;
    private List<EvaluationDao> mEvaluationDaoList;
    private Integer mUserId;

    public DemoAdapter(Context context, List<StylistDao> stylistDaoList, List<EvaluationDao> evaluationDaoList, Integer userId) {
        super(R.layout.layout_stylist_item, stylistDaoList);
        this.mContext = context;
        this.mEvaluationDaoList = evaluationDaoList;
        this.mUserId = userId;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, StylistDao stylistDao) {
        holder.setText(R.id.real_name, stylistDao.getRealName());
        holder.setText(R.id.speciality, stylistDao.getSpeciality());
        ((ScaleRatingBar)holder.getView(R.id.rate)).setRating(
                mEvaluationDaoList.get(getItemPosition(stylistDao)).getRate());
        ShapedImageView avatar = holder.getView(R.id.avatar);
        Glide.with(getContext())
                .load(stylistDao.getAvatar())
                .centerCrop()
                .placeholder(R.drawable.dog)
                .into(avatar);
        holder.getView(R.id.card).setOnClickListener(v -> {
            Intent intent = new Intent(mContext, StylistDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("stylist", stylistDao);
            bundle.putSerializable("evaluation", mEvaluationDaoList.get(getItemPosition(stylistDao)));
            bundle.putInt("userId", mUserId);
            intent.putExtras(bundle);
            mContext.startActivity(intent);
        });
    }
}
