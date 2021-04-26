package com.example.beautysalon.ui.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.beautysalon.R;
import com.example.beautysalon.dao.BusinessHoursDao;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author Lee
 * @date 2021.4.8  13:39
 * @description
 */
public class RecommendTimeAdapter extends BaseQuickAdapter<BusinessHoursDao, BaseViewHolder> {

    public RecommendTimeAdapter(@Nullable List<BusinessHoursDao> data) {
        super(R.layout.layout_dialog_textview_item, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, BusinessHoursDao businessHoursDao) {
        String DateFrom = new SimpleDateFormat("HH:mm").format(businessHoursDao.getDateFrom());
        String DateTo = new SimpleDateFormat("HH:mm").format(businessHoursDao.getDateTo());
        holder.setText(R.id.textView, DateFrom + " - " + DateTo);
    }
}
