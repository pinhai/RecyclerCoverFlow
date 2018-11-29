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
import com.hai.floatinglayer.util.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenxiaoping on 2017/3/28.
 */

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private Context mContext;
//    private int[] mColors = {R.mipmap.item1,R.mipmap.item2,R.mipmap.item3,R.mipmap.item4,
//            R.mipmap.item5,R.mipmap.item6};
    List<String> imgUrls;

    private onItemClick clickCb;

    private int imgSize;

    public Adapter(Context c) {
        mContext = c;
    }

    public Adapter(Context c, onItemClick cb) {
        mContext = c;
        clickCb = cb;

        imgSize = ScreenUtils.getScreenWidth()/2;

        imgUrls = new ArrayList<>();
        imgUrls.add("https://media2016-10006037.image.myqcloud.com/0bfb02cb-b01e-4f78-a72e-a70d68ed42ad/maskarty");
        imgUrls.add("https://media2016-10006037.image.myqcloud.com/ee30e583-998e-4bf6-98f2-32c719ddb02f/maskarty");
        imgUrls.add("https://media2016-10006037.image.myqcloud.com/f79ec791-d0b2-4f61-835e-6dd2f26905a5/maskarty");
        imgUrls.add("https://media2016-10006037.image.myqcloud.com/2dcec907-fe23-4f59-a3c5-5c697481885d");
        imgUrls.add("https://media2016-10006037.image.myqcloud.com/21f4a129-e849-4d10-8f9d-3f8d1967b654");
        imgUrls.add("https://media2016-10006037.image.myqcloud.com/eea427e7-d830-4424-b928-a801e1802209/maskarty");
        imgUrls.add("https://media2016-10006037.image.myqcloud.com/2e397838-7daa-49fd-b954-920256f874f9");
        imgUrls.add("https://media2016-10006037.image.myqcloud.com/cb61dc06-c69a-4165-b2e1-359c4e65ed9e/maskarty");
        imgUrls.add("https://media2016-10006037.image.myqcloud.com/4f751c79-327f-4fb7-86af-fdcf1c6c8036");
        imgUrls.add("https://media2016-10006037.image.myqcloud.com/187951a7-a6ad-4fde-89a6-cf71262e0702");
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
        Glide.with(mContext).load(imgUrls.get(position%imgUrls.size()))
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
                FloatingManager.getInstance().onLongClickEvent(position, imgUrls);
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

            ViewGroup.LayoutParams lp = img.getLayoutParams();
            lp.width = lp.height = imgSize;
            img.setLayoutParams(lp);
        }
    }

    interface onItemClick {
        void clickItem(int pos);
//        void imgLongClickItem(int pos);
    }
}
