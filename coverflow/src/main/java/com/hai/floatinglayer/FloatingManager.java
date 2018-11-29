package com.hai.floatinglayer;

import android.content.Context;
import android.widget.ScrollView;

import com.hai.floatinglayer.util.ScreenUtils;

import java.util.List;

public class FloatingManager {

    public interface OnPositionListener{
        int getCurrentPos();
    }

    private boolean enable;
    private int statusBarHeight; // 状态栏(通知栏)高度
    private ScrollView parent;
    private FloatingCover floatingCover;
    private OnPositionListener positionListener;

    // field
    private boolean isTouchable;

    // single instance
    private static FloatingManager instance;

    public static synchronized FloatingManager getInstance() {
        if (instance == null) {
            instance = new FloatingManager();
        }
        return instance;
    }

    public void init(Context context, ScrollView parent, FloatingCover floatingCover, OnPositionListener positionListener){
        this.statusBarHeight = ScreenUtils.getStatusBarHeight(context);
        this.parent = parent;
        this.floatingCover = floatingCover;
        this.positionListener = positionListener;
        this.enable = true;
        this.isTouchable = true;
    }

    public void destroy() {
        this.isTouchable = false;
        this.enable = false;
    }

    public boolean isEnable() {
        return enable;
    }

    public boolean isTouchable() {
        if (!enable) {
            return true;
        }
        return isTouchable;
    }

    public void setTouchable(boolean isTouchable) {
        this.isTouchable = isTouchable;
    }

    public int getTop() {
        return statusBarHeight;
    }

    public ScrollView getParent(){
        return parent;
    }

    public void onLongClickEvent(int position, List<String > urls){
        if(parent == null || floatingCover == null || positionListener.getCurrentPos() != position){
            return;
        }

        floatingCover.onLongClickEvent(urls);
    }

    public void onDown(FloatingImage image){
        if(parent == null || floatingCover == null){
            return;
        }

        floatingCover.onDown(image);
    }

    public void onMove(float curX, float curY){
        if(parent == null || floatingCover == null){
            return;
        }

        floatingCover.onMove(curX, curY);
    }

    public void onUp(){
        if(parent == null || floatingCover == null){
            return;
        }

        floatingCover.onUp();
    }

}
