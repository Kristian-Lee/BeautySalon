package com.example.beautysalon.ui.adapter.provider;

import android.view.View;

import com.chad.library.adapter.base.entity.node.BaseNode;
import com.chad.library.adapter.base.provider.BaseNodeProvider;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.beautysalon.R;
import com.example.beautysalon.dao.ReservationBean;
import com.example.beautysalon.ui.adapter.NodeTwoAdapter;
import com.example.beautysalon.ui.widget.ReservationNode;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;

/**
 * @author Lee
 * @date 2021.4.14  18:14
 * @description
 */
public class ReservationProvider extends BaseNodeProvider {
    @Override
    public int getItemViewType() {
        return 1;
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_stylist_reservation_item;
    }

    @Override
    public void convert(@NotNull BaseViewHolder holder, BaseNode baseNode) {
        ReservationBean reservation = ((ReservationNode) baseNode).getReservation();
        holder.setText(R.id.reservationId, String.valueOf(reservation.getReservationId()));
        holder.setText(R.id.serveDate, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(reservation.getServeDate()));
        holder.setText(R.id.userName, reservation.getUserName());
        holder.setText(R.id.services, reservation.getServices());
        holder.setText(R.id.hobby, reservation.getHobby());
//        setArrowSpin(holder, baseNode, false);
    }


//    @Override
//    public void convert(@NotNull BaseViewHolder helper, @NotNull BaseNode data, @NotNull List<?> payloads) {
//        for (Object payload : payloads) {
//            if (payload instanceof Integer && (int) payload == NodeTreeAdapter.EXPAND_COLLAPSE_PAYLOAD) {
//                // 增量刷新，使用动画变化箭头
//                setArrowSpin(helper, data, true);
//            }
//        }
//    }

//    private void setArrowSpin(BaseViewHolder helper, BaseNode data, boolean isAnimate) {
//        ReservationNode entity = (ReservationNode) data;
//
//        ImageView imageView = helper.getView(R.id.iv);
//
//        if (entity.isExpanded()) {
//            if (isAnimate) {
//                ViewCompat.animate(imageView).setDuration(200)
//                        .setInterpolator(new DecelerateInterpolator())
//                        .rotation(0f)
//                        .start();
//            } else {
//                imageView.setRotation(0f);
//            }
//        } else {
//            if (isAnimate) {
//                ViewCompat.animate(imageView).setDuration(200)
//                        .setInterpolator(new DecelerateInterpolator())
//                        .rotation(90f)
//                        .start();
//            } else {
//                imageView.setRotation(90f);
//            }
//        }
//    }

    @Override
    public void onClick(@NotNull BaseViewHolder helper, @NotNull View view, BaseNode data, int position) {
        // 这里使用payload进行增量刷新（避免整个item刷新导致的闪烁，不自然）
        getAdapter().expandOrCollapse(position, true, true, NodeTwoAdapter.EXPAND_COLLAPSE_PAYLOAD);
    }
}
