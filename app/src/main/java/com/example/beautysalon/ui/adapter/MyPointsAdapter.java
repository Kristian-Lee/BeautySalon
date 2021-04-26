package com.example.beautysalon.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beautysalon.R;
import com.example.beautysalon.dao.PointsDao;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author Lee
 * @date 2021.4.1  01:40
 * @description
 */
public class MyPointsAdapter extends RecyclerView.Adapter<MyPointsAdapter.MyViewHolder> {

    private final Context mContext;
    private final List<PointsDao> mPointsDaoList;

    public MyPointsAdapter(Context context, List<PointsDao> pointsDaoList) {
        this.mContext = context;
        this.mPointsDaoList = pointsDaoList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_points_item, parent,
                false));
    }

    @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        switch (mPointsDaoList.get(position).getType()) {
            case 0:
                holder.type.setText("签到");
                holder.value.setText("+" + mPointsDaoList.get(position).getValue());
                holder.value.setTextColor(Color.parseColor("#fb9250"));
                break;
            case 1:
                holder.type.setText("订单奖励");
                holder.value.setText("+" + mPointsDaoList.get(position).getValue());
                holder.value.setTextColor(Color.parseColor("#fb9250"));
                break;
            case 2:
                holder.type.setText("订单支付抵扣");
                holder.value.setText(String.valueOf(mPointsDaoList.get(position).getValue()));
                break;
            case 3:
                holder.type.setText("预约支付积分退还");
                holder.value.setText("+" + mPointsDaoList.get(position).getValue());
                holder.value.setTextColor(Color.parseColor("#fb9250"));
                break;
        }
        holder.date.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(mPointsDaoList.get(position).getCreateDate()));
    }

    @Override
    public int getItemCount() {
        return mPointsDaoList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView value, type, date;
        CardView cardView;
        public MyViewHolder(View view) {
            super(view);
            value = view.findViewById(R.id.value);
            type = view.findViewById(R.id.type);
            date = view.findViewById(R.id.date);
            cardView = view.findViewById(R.id.card);
        }
    }
}
