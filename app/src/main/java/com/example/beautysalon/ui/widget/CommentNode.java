package com.example.beautysalon.ui.widget;

import com.chad.library.adapter.base.entity.node.BaseExpandNode;
import com.chad.library.adapter.base.entity.node.BaseNode;
import com.example.beautysalon.dao.CommentDao;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Lee
 * @date 2021.4.14  18:10
 * @description
 */
public class CommentNode extends BaseExpandNode {
    private CommentDao mCommentDao;

    public CommentNode (CommentDao commentDao) {
        this.mCommentDao = commentDao;
        setExpanded(false);
    }

    public CommentDao getComment() {
        return mCommentDao;
    }

    @Nullable
    @Override
    public List<BaseNode> getChildNode() {
        return null;
    }
}
