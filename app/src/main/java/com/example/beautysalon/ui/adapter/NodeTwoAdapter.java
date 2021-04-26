package com.example.beautysalon.ui.adapter;

import com.chad.library.adapter.base.BaseNodeAdapter;
import com.chad.library.adapter.base.entity.node.BaseNode;
import com.example.beautysalon.ui.adapter.provider.CommentProvider;
import com.example.beautysalon.ui.adapter.provider.ReservationProvider;
import com.example.beautysalon.ui.widget.CommentNode;
import com.example.beautysalon.ui.widget.ReservationNode;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Lee
 * @date 2021.4.14  18:36
 * @description
 */
public class NodeTwoAdapter extends BaseNodeAdapter {

    public NodeTwoAdapter() {
        super();
        addNodeProvider(new ReservationProvider());
        addNodeProvider(new CommentProvider());
    }

    @Override
    protected int getItemType(@NotNull List<? extends BaseNode> data, int position) {
        BaseNode node = data.get(position);
        if (node instanceof ReservationNode) {
            return 1;
        } else if (node instanceof CommentNode) {
            return 2;
        }
        return -1;
    }

    public static final int EXPAND_COLLAPSE_PAYLOAD = 110;
}
