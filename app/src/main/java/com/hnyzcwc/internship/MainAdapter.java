package com.hnyzcwc.internship;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by Administrator on 2017/10/17.
 */

public class MainAdapter extends RecyclerView.Adapter<MainViewHolder> {
    private Context mContext;
    private List<String>mList;
    private LayoutInflater mInflater;
    public MainAdapter(Context context, List<String> list){
        mContext = context;
        mList = list;
        mInflater = LayoutInflater.from(mContext);
    }
    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.main_item,parent,false);
        return new MainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, int position) {
        Glide.with(mContext)
                .load(mList.get(position))
                .placeholder(R.drawable.loading)
                .error(R.drawable.failed_img)
                .fitCenter()
                .into(holder.mImage);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setDataList(List<String> list){
        mList = list;
        notifyDataSetChanged();
    }
    public List<String> getDataList(){
        return mList;
    }
    public void addDataItem(String item){
        mList.add(item);
        notifyItemInserted(mList.size()-1);
    }
    public void setDataItem(String item,int position) {
        mList.set(position, item);
        notifyItemChanged(position);
    }
}
