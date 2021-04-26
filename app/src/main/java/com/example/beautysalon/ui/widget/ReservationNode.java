package com.example.beautysalon.ui.widget;

import com.chad.library.adapter.base.entity.node.BaseExpandNode;
import com.chad.library.adapter.base.entity.node.BaseNode;
import com.example.beautysalon.dao.ReservationBean;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Lee
 * @date 2021.4.14  17:57
 * @description
 */
public class ReservationNode extends BaseExpandNode {

    private List<BaseNode> mChildNode;
    private ReservationBean mReservationBean;

    public ReservationNode(List<BaseNode> childNode, ReservationBean reservationBean) {
        this.mChildNode = childNode;
        this.mReservationBean = reservationBean;
        setExpanded(false);
    }

    public ReservationBean getReservation() {
        return mReservationBean;
    }

    @Nullable
    @Override
    public List<BaseNode> getChildNode() {
        return mChildNode;
    }
}
