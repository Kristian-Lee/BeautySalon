package com.example.beautysalon.ui.adapter;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.beautysalon.R;
import com.example.beautysalon.dao.ServicesDao;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lee
 * @date 2021.4.8  14:19
 * @description
 */
public class ServiceAdapter extends BaseQuickAdapter<ServicesDao, BaseViewHolder>{

    private List<ServicesDao> selectList = new ArrayList<>();
    private View mDialogView;

    public ServiceAdapter(@Nullable List<ServicesDao> data, View dialogView) {
        super(R.layout.layout_dialog_textview_item, data);
        this.mDialogView = dialogView;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, ServicesDao servicesDao) {
        TextView textView = holder.getView(R.id.textView);
        TextView takeUp = mDialogView.findViewById(R.id.takeUp);
        TextView value = mDialogView.findViewById(R.id.value);
        textView.setText(servicesDao.getName());
        if (selectList.contains(servicesDao)) {
            textView.setSelected(true);
            textView.setTextColor(ContextCompat.getColor(textView.getContext(), R.color.highlight));
        } else {
            textView.setSelected(false);
            textView.setTextColor(ContextCompat.getColor(textView.getContext(), R.color.default_textColor));
        }
        textView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                int originTime = Integer.parseInt(takeUp.getText().toString());
                int originValue = Integer.parseInt(value.getText().toString());
                if (selectList.contains(servicesDao)) {
                    selectList.remove(servicesDao);

                    takeUp.setText((originTime - servicesDao.getTime()) + "");
                    value.setText((originValue - servicesDao.getValue()) + "");
                } else {
                    selectList.add(servicesDao);
                    takeUp.setText((originTime + servicesDao.getTime()) + "");
                    value.setText((originValue + servicesDao.getValue() + ""));
                }
                notifyItemChanged(getItemPosition(servicesDao));
            }
        });
    }

    public List<ServicesDao> getSelectList() {
        return selectList;
    }
}
