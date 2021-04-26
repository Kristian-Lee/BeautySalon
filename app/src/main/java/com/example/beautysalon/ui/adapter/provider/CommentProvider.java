package com.example.beautysalon.ui.adapter.provider;

import android.view.View;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.entity.node.BaseNode;
import com.chad.library.adapter.base.provider.BaseNodeProvider;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.beautysalon.R;
import com.example.beautysalon.dao.CommentDao;
import com.example.beautysalon.ui.widget.CommentNode;
import com.willy.ratingbar.ScaleRatingBar;

import org.jetbrains.annotations.NotNull;

import cn.gavinliu.android.lib.shapedimageview.ShapedImageView;

/**
 * @author Lee
 * @date 2021.4.14  18:28
 * @description
 */
public class CommentProvider extends BaseNodeProvider {

    @Override
    public int getItemViewType() {
        return 2;
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_expand_comment_item;
    }

    @Override
    public void convert(@NotNull BaseViewHolder holder, BaseNode baseNode) {
        CommentDao commentDao = ((CommentNode) baseNode).getComment();
        holder.setText(R.id.user_name, commentDao.getUserName());
        holder.setText(R.id.comment, commentDao.getContact());
        holder.setText(R.id.date, commentDao.getCreateDate());
        ScaleRatingBar rate = holder.getView(R.id.rate);
        rate.setRating(commentDao.getRate());
        ShapedImageView avatar = holder.getView(R.id.avatar);
        Glide.with(getContext())
                .load(commentDao.getUserAvatar())
                .centerCrop()
                .placeholder(R.drawable.dog)
                .into(avatar);
    }

    @Override
    public void onClick(@NotNull BaseViewHolder helper, @NotNull View view, BaseNode data, int position) {
        CommentNode entity = (CommentNode) data;
        if (entity.isExpanded()) {
            getAdapter().collapse(position);
        } else {
            getAdapter().expandAndCollapseOther(position);
        }
    }
}
