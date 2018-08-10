package com.liuyi.adimageview;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * @author liuyi
 * @date 2018/8/10
 */
public class AdViewAdapter extends RecyclerView.Adapter<AdViewAdapter.RvViewHolder> {

    List<AdBean> mDatas;
    private Activity mContext;

    public AdViewAdapter(Activity context, List<AdBean> datas) {
        this.mContext = context;
        mDatas = datas;
    }

    @NonNull
    @Override
    public RvViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RvViewHolder(mContext.getLayoutInflater().inflate(R.layout.item_ad_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RvViewHolder holder, int position) {
        AdBean adBean = mDatas.get(position);
        holder.ivAd.setVisibility(adBean.isAd ? View.VISIBLE : View.GONE);
        holder.title.setVisibility(adBean.isAd ? View.GONE : View.VISIBLE);
        holder.content.setVisibility(adBean.isAd ? View.GONE : View.VISIBLE);
//        holder.ivAd.setImageDrawable(adBean.drawable);
        holder.content.setText(adBean.des);
        holder.title.setText(adBean.title + position);

    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    protected class RvViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView content;
        public AdImageView ivAd;

        public RvViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            content = (TextView) itemView.findViewById(R.id.content);
            ivAd = (AdImageView) itemView.findViewById(R.id.iv_ad);
        }
    }
}
