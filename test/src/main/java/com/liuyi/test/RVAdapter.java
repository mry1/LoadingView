package com.liuyi.test;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Random;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.RVViewHolder> {

    private final Context mContext;
    private final Random mRandom;
    private List<String> mData;

    public RVAdapter(Context context, List<String> data) {
        mContext = context;
        mRandom = new Random();
        mData = data;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int itemViewType = getItemViewType(position);
                switch (itemViewType) {
                    case 1:
                        return 6;
                    case 2:
                        return 3;
                    case 3:
                        return 2;
                    case 4:
                        return 1;
                }
                return 3;
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        if (position < 6) {
            return 1;
        }
        if (position < 10) {
            return 2;
        }
        if (position < 20) {
            return 3;
        }
        if (position < 30) {
            return 4;
        }
        return 3;
    }

    @NonNull
    @Override
    public RVViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_rv, parent, false);
        return new RVViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RVViewHolder holder, int position) {
        int ranColor = 0xff000000 | mRandom.nextInt(0x00ffffff);
        holder.mTv_item.setBackgroundColor(ranColor);
        holder.mTv_item.setText(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class RVViewHolder extends RecyclerView.ViewHolder {

        private TextView mTv_item;

        private RVViewHolder(View itemView) {
            super(itemView);
            mTv_item = itemView.findViewById(R.id.tv_item);
        }
    }

}
