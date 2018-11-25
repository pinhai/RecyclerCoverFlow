package com.hai.floatinglayer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ScrollView;

public class FloatingImage extends android.support.v7.widget.AppCompatImageView implements View.OnLongClickListener {

    public interface ITouchListener {
        void onClickEvent();

        void onLongClickEvent();

        void onDown();

        void onMove(float curX, float curY);

        void onUp();
    }

    private ITouchListener touchListener;

    public FloatingImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOnLongClickListener(this);
    }

    float currentX, currentY, firstX, firstY, dX, dY;
    long timeMillis;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 如果未初始化 DropManager，则默认任何事件都不处理
        if (!FloatingManager.getInstance().isEnable()) {
            return super.onTouchEvent(event);
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (FloatingManager.getInstance().isEnable()) {
                    if (touchListener != null) {
                        touchListener.onDown();
                        currentX = firstX = event.getX();
                        currentY = firstY = event.getY();
                        timeMillis = System.currentTimeMillis();
                    }
//                    return true; // eat
                }
//                return false;
            case MotionEvent.ACTION_MOVE:
                if (touchListener != null) {
                    dX = event.getX() - currentX;
                    dY = event.getY() - currentY;
                    currentX = event.getX();
                    currentY = event.getY();
                    touchListener.onMove(dX, dY);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (touchListener != null) {
                    // 将控制权还给父控件
                    disallowInterceptTouchEvent(false);
                    touchListener.onUp();
                    if(System.currentTimeMillis()-timeMillis < 500){
                        float dx = event.getX() - firstX;
                        float dy = event.getY() - firstY;
                        if(dx<2 && dy<2){
                            touchListener.onClickEvent();
                        }
                    }
                }
                break;
            default:
                break;
        }

        return super.onTouchEvent(event);
    }

    public void setTouchListener(ITouchListener listener) {
        touchListener = listener;
    }

    @Override
    public boolean onLongClick(View view) {
        if(touchListener != null){
            // 不允许父控件处理TouchEvent，当父控件为ListView这种本身可滑动的控件时必须要控制
            disallowInterceptTouchEvent(true);
            touchListener.onLongClickEvent();
            return true;
        }
        return false;
    }

    private void disallowInterceptTouchEvent(boolean disable) {
        ViewGroup parent = (ViewGroup) getParent();
        parent.requestDisallowInterceptTouchEvent(disable);

        while (true) {
            if (parent == null) {
                return;
            }

            if (parent instanceof RecyclerView || parent instanceof ListView || parent instanceof GridView ||
                    parent instanceof ScrollView) {
                parent.requestDisallowInterceptTouchEvent(disable);
                return;
            }

            ViewParent vp = parent.getParent();
            if (vp instanceof ViewGroup) {
                parent = (ViewGroup) parent.getParent();
            } else {
                return; // DecorView
            }
        }
    }

}
