package com.hnyzcwc.internship;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Administrator on 2017/10/17.
 */

public class MainViewHolder extends RecyclerView.ViewHolder {
    public ImageView mImage;
    public MainViewHolder(View itemView) {
        super(itemView);
        mImage = (ImageView) itemView.findViewById(R.id.iv_image);
    }
}
