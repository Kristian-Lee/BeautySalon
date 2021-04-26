package com.example.beautysalon.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.beautysalon.R;
import com.example.beautysalon.dao.BarbershopDao;
import com.example.beautysalon.dao.CommentDao;
import com.example.beautysalon.dao.EvaluationDao;
import com.example.beautysalon.dao.StylistDao;
import com.willy.ratingbar.ScaleRatingBar;

import java.util.List;

import cn.gavinliu.android.lib.shapedimageview.ShapedImageView;

/**
 * @author Lee
 * @date 2021.3.28  16:51
 * @description
 */
public class StylistDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final static int STYLIST_DETAIL_TYPE = 100001;
    private final static int COMMENT_TYPE = 200001;
    private Context mContext;
    private View mInflater;
    private StylistDao mStylistDao;
    private EvaluationDao mEvaluationDao;
    private BarbershopDao mBarbershopDao;
    private List<CommentDao> mCommentDaoList;


    public StylistDetailAdapter(Context context, StylistDao stylistDao, EvaluationDao evaluationDao,
                                BarbershopDao barbershopDao, List<CommentDao> commentDaoList) {
        this.mContext = context;
        this.mStylistDao = stylistDao;
        this.mEvaluationDao = evaluationDao;
        this.mBarbershopDao = barbershopDao;
        this.mCommentDaoList = commentDaoList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == STYLIST_DETAIL_TYPE) {
            mInflater = LayoutInflater.from(mContext).inflate(R.layout.layout_stylist_detail_item, parent,
                    false);
            return new StylistDetailViewHolder(mInflater);
        } else {
            mInflater = LayoutInflater.from(mContext).inflate(R.layout.layout_comment_item, parent,
                    false);
            return new CommentViewHolder(mInflater);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position == 0) {
            StylistDetailViewHolder tempHolder = (StylistDetailViewHolder) holder;
            tempHolder.tvStylistName.setText(mStylistDao.getRealName());
            tempHolder.tvPhone.setText(mStylistDao.getPhone());
            tempHolder.tvSpeciality.setText(mStylistDao.getSpeciality());
            tempHolder.tvWorkingYears.setText(String.valueOf(mStylistDao.getWorkingYears()));
            tempHolder.srbRate.setRating(mEvaluationDao.getRate());
            tempHolder.tvBarbershopName.setText(mBarbershopDao.getBarbershopName());
            tempHolder.tvBarbershopAddress.setText(mBarbershopDao.getAddress());
            Glide.with(mContext)
                    .load(mStylistDao.getAvatar())
                    .centerCrop()
                    .placeholder(R.drawable.dog)
                    .into(tempHolder.siv_avatar);
            if (mCommentDaoList.size() == 0) {
                tempHolder.tvMessage.setText("暂无用户评论");
            }
        } else {
            CommentViewHolder tempHolder = (CommentViewHolder) holder;
            tempHolder.tvDate.setText(mCommentDaoList.get(position - 1).getCreateDate());
            tempHolder.tvComment.setText(mCommentDaoList.get(position - 1).getContact());
            tempHolder.srbRate.setRating(mCommentDaoList.get(position - 1).getRate());
            tempHolder.tvUsername.setText(mCommentDaoList.get(position - 1).getUserName());
            Glide.with(mContext)
                    .load(mCommentDaoList.get(position - 1).getUserAvatar())
                    .centerCrop()
                    .placeholder(R.drawable.dog)
                    .into(tempHolder.siv_avatar);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return STYLIST_DETAIL_TYPE;
        }
        return COMMENT_TYPE;
    }

    @Override
    public int getItemCount() {
        return mCommentDaoList.size() + 1;
    }
    static class CommentViewHolder extends RecyclerView.ViewHolder {
        ShapedImageView siv_avatar;
        TextView tvUsername, tvComment, tvDate;
        ScaleRatingBar srbRate;
        CardView card;
        public CommentViewHolder(View view) {
            super(view);
            siv_avatar = view.findViewById(R.id.avatar);
            tvUsername = view.findViewById(R.id.user_name);
            tvComment = view.findViewById(R.id.comment);
            tvDate = view.findViewById(R.id.date);
            srbRate = view.findViewById(R.id.rate);
            card = view.findViewById(R.id.card);
        }
    }
    static class StylistDetailViewHolder extends RecyclerView.ViewHolder {
        ShapedImageView siv_avatar;
        TextView tvStylistName, tvPhone, tvSpeciality, tvWorkingYears,
                tvBarbershopName, tvBarbershopAddress, tvMessage;
        ScaleRatingBar srbRate;
        CardView card;
        public StylistDetailViewHolder(View view) {
            super(view);
            siv_avatar = view.findViewById(R.id.avatar);
            tvMessage = view.findViewById(R.id.message);
            tvStylistName = view.findViewById(R.id.real_name);
            tvPhone = view.findViewById(R.id.phone);
            tvWorkingYears = view.findViewById(R.id.working_years);
            tvSpeciality = view.findViewById(R.id.speciality);
            tvBarbershopName = view.findViewById(R.id.barbershop_name);
            tvBarbershopAddress = view.findViewById(R.id.barbershop_address);
            srbRate = view.findViewById(R.id.rate);
            card = view.findViewById(R.id.card);
        }
    }
}
