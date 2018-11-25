package com.recycler.coverflow;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.hai.floatinglayer.FloatingImage;
import com.hai.floatinglayer.FloatingManager;

/**
 * Created by chenxiaoping on 2017/3/28.
 */

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private Context mContext;
    private int[] mColors = {R.mipmap.item1,R.mipmap.item2,R.mipmap.item3,R.mipmap.item4,
            R.mipmap.item5,R.mipmap.item6};

    private onItemClick clickCb;

    public Adapter(Context c) {
        mContext = c;
    }

    public Adapter(Context c, onItemClick cb) {
        mContext = c;
        clickCb = cb;
    }

    public void setOnClickLstn(onItemClick cb) {
        this.clickCb = cb;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.layout_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Glide.with(mContext).load(mColors[position % mColors.length])
                .into(holder.img);
        holder.img.setTouchListener(new FloatingImage.ITouchListener() {
            @Override
            public void onClickEvent() {
                if (clickCb != null) {
                    clickCb.clickItem(position);
                }
            }

            @Override
            public void onLongClickEvent() {
                FloatingManager.getInstance().onLongClickEvent(position);
            }

            @Override
            public void onDown() {
                FloatingManager.getInstance().onDown(holder.img);
            }

            @Override
            public void onMove(float curX, float curY) {
                FloatingManager.getInstance().onMove(curX, curY);
            }

            @Override
            public void onUp() {
                FloatingManager.getInstance().onUp();
            }
        });

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Toast.makeText(mContext, "点击了："+position, Toast.LENGTH_SHORT).show();
//                if (clickCb != null) {
//                    clickCb.clickItem(position);
//                }
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        FloatingImage img;
        public ViewHolder(View itemView) {
            super(itemView);
            img = (FloatingImage) itemView.findViewById(R.id.img);
        }
    }

    interface onItemClick {
        void clickItem(int pos);
//        void imgLongClickItem(int pos);
    }
}
