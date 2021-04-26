package com.example.beautysalon.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.beautysalon.R;
import com.example.beautysalon.dao.EvaluationDao;
import com.example.beautysalon.dao.StylistDao;
import com.example.beautysalon.ui.activity.StylistDetailActivity;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.willy.ratingbar.ScaleRatingBar;

import java.util.List;

import cn.gavinliu.android.lib.shapedimageview.ShapedImageView;

/**
 * @author Lee
 * @date 2021.3.26  18:55
 * @description
 */
public class StylistDataAdapter extends XRecyclerView.Adapter<StylistDataAdapter.MyViewHolder> {
    private Context mContext;
    private List<StylistDao> mStylistDaoList;
    private List<EvaluationDao> mEvaluationDaoList;
    private View mInflater;

    public StylistDataAdapter(Context context, List<StylistDao> stylistDaoList,
                              List<EvaluationDao> evaluationDaoList) {
        this.mContext = context;
        this.mStylistDaoList = stylistDaoList;
        this.mEvaluationDaoList = evaluationDaoList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mInflater = LayoutInflater.from(mContext).inflate(R.layout.layout_stylist_item, parent,
                false);
        return new MyViewHolder(mInflater);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tvRealName.setText(mStylistDaoList.get(position).getRealName());
        holder.tvSpeciality.setText(mStylistDaoList.get(position).getSpeciality());
        holder.srb_rate.setRating(mEvaluationDaoList.get(position).getRate());
        System.out.println(mStylistDaoList.get(position).getAvatar());
        Glide.with(mContext)
                .load(mStylistDaoList.get(position).getAvatar())
                .centerCrop()
                .placeholder(R.drawable.dog)
                .into(holder.siv_avatar);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, StylistDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("stylist", mStylistDaoList.get(position));
                bundle.putSerializable("evaluation", mEvaluationDaoList.get(position));
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mStylistDaoList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvRealName, tvSpeciality;
        ShapedImageView siv_avatar;
        ScaleRatingBar srb_rate;
        CardView cardView;
        public MyViewHolder(View view) {
            super(view);
            tvRealName = view.findViewById(R.id.real_name);
            tvSpeciality = view.findViewById(R.id.speciality);
            siv_avatar = view.findViewById(R.id.avatar);
            srb_rate = view.findViewById(R.id.rate);
            cardView = view.findViewById(R.id.card);
        }
    }
}
