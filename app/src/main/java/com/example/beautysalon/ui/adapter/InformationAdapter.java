package com.example.beautysalon.ui.adapter;

import android.annotation.SuppressLint;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.beautysalon.R;
import com.example.beautysalon.dao.InformationDao;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author Lee
 * @date 2021.5.12  02:21
 * @description
 */
public class InformationAdapter extends BaseQuickAdapter<InformationDao, BaseViewHolder> implements LoadMoreModule {
    public InformationAdapter(List<InformationDao> informationDaoList) {
        super(R.layout.layout_information_item, informationDaoList);
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void convert(@NotNull BaseViewHolder holder, InformationDao informationDao) {
        holder.setText(R.id.title, informationDao.getTitle());
        holder.setText(R.id.content, informationDao.getContent());
        holder.setText(R.id.date, new SimpleDateFormat("yyyy-MM-dd").format(informationDao.getCreateDate()));
    }
}
