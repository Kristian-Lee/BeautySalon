package com.example.beautysalon.ui.adapter;

import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beautysalon.R;
import com.example.beautysalon.databinding.ActivityTopUpBinding;

import java.util.List;

/**
 * @author Lee
 * @date 2021.4.3  00:17
 * @description
 */
public class TopUpAdapter extends RecyclerView.Adapter<TopUpAdapter.MyBaseViewHolder> {
    private ActivityTopUpBinding mBinding;
    private final static int TYPE_TEXTVIEW = 101;
    private final static int TYPE_EDITTEXT = 102;
    private final List<Integer> mList;
    private final Context mContext;
    private EditText mEditText;
    boolean focus = false;
    private int mLastPressLocation = 0;

    public TopUpAdapter(Context context, ActivityTopUpBinding binding, List<Integer> list) {
        this.mContext = context;
        this.mList = list;
        this.mBinding = binding;
    }

    @NonNull
    @Override
    public TopUpAdapter.MyBaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_TEXTVIEW) {
            return new TextViewHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_top_up_textview_item, parent,
                    false));
        }
        return new EditViewHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_top_up_edittext_item, parent,
                false));
    }

    @Override
    public void onBindViewHolder(@NonNull TopUpAdapter.MyBaseViewHolder holder, int position) {
        System.out.println("bindViewHolder" + position);
        if (position < mList.size()) {
            holder.setData(mList.get(position));

        } else {
            holder.setData(null);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mList.size()) {
            return TYPE_EDITTEXT;
        }
        return TYPE_TEXTVIEW;
    }

    public class MyBaseViewHolder extends RecyclerView.ViewHolder {

        public MyBaseViewHolder(View itemView) {
            super(itemView);
        }

        void setData(Object data) {
        }
    }

    class TextViewHolder extends MyBaseViewHolder {
        TextView textView;

        public TextViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.textView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (mLastPressLocation == position) {
                        mLastPressLocation = -1;
                        mBinding.num.setText("0");
                    } else {
                        mLastPressLocation = position;
                        mBinding.num.setText(String.valueOf(mList.get(mLastPressLocation)));
                    }
                    mEditText.clearFocus();
                    mEditText.setSelected(false);
                    notifyDataSetChanged();
                }
            });
        }

        void setData(Object data) {
            if (data != null) {
                int text = (int) data;
                textView.setText(String.valueOf(text));
                if (getAdapterPosition() == mLastPressLocation) {
                    textView.setSelected(true);
                    textView.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.white));

                } else {
                    textView.setSelected(false);
                    textView.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.colorAccent));
                }

            }
        }
    }
    class EditViewHolder extends MyBaseViewHolder {

        public EditViewHolder(View view) {
            super(view);

            mEditText = view.findViewById(R.id.editText);

            mEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        if (mLastPressLocation != 6) {
                            focus = true;
                            mLastPressLocation = 6;
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    // 刷新操作
                                    notifyDataSetChanged();
                                }
                            });
                        }
                    }
                }
            });
            mEditText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            mEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    mBinding.num.setText(s);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });


        }
        void setData(Object data) {
            if (mLastPressLocation == 6) {
                if (!mEditText.getText().toString().equals("")) {
                    mBinding.num.setText(mEditText.getText().toString());
                } else {
                    mBinding.num.setText("0");
                }
                mEditText.requestFocus();
            }
        }
    }
}
