package com.hai.floatinglayer;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.TransitionOptions;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.bumptech.glide.request.transition.ViewPropertyTransition;
import com.hai.floatinglayer.model.ImageItem;
import com.hai.floatinglayer.util.ScreenUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import recycler.coverflow.R;

public class FloatingCover extends RelativeLayout {

    private static final int MAX_HEIGHT = 10000;  //最大高度，确保上下滑动不会到边界
    private static final int CUSTOM_MARGIN = 50;

    private int MAX_WIDTH_RANDOM = 800, MIN_WIDTH_RANDOM = 300;  //随机生成图片宽度范围
    int imageCountTop = 5, imageCountDown = 5;
    List<ImageItem> imageItemsTop, imageItemsDown;

    ImageView centerImg;

    public FloatingCover(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        imageItemsTop = new ArrayList<>();
        imageItemsDown = new ArrayList<>();
        MAX_WIDTH_RANDOM = ScreenUtils.getScreenWidth()/2 - CUSTOM_MARGIN;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        initHeight();
    }

    private void initHeight() {
        ViewGroup.LayoutParams lp = getLayoutParams();
        lp.height = MAX_HEIGHT;
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        setLayoutParams(lp);
    }

    public void onLongClickEvent(){
        setVisible(true);
        randomImageSize(((BitmapDrawable)centerImg.getDrawable()).getBitmap());
        randomImageViewTop(centerImg, null, null, 0);
        randomImageViewDown(centerImg, null, null, 0);
    }

    public void onDown(FloatingImage image){
        removeAllViews();
        imageItemsTop.clear();
        imageItemsDown.clear();

        int[] location2 = new int[2] ;
        image.getLocationOnScreen(location2);//获取在整个屏幕内的绝对坐标

        centerImg = new ImageView(getContext());
        LayoutParams lp = new LayoutParams(image.getWidth(), image.getHeight());
        lp.addRule(CENTER_IN_PARENT);
        centerImg.setLayoutParams(lp);
        centerImg.setImageResource(R.drawable.item2);
        centerImg.setScaleType(ImageView.ScaleType.CENTER_CROP);
        addView(centerImg);

        getScrollView().scrollTo(0, (MAX_HEIGHT-lp.height)/2-
                (location2[1]-ScreenUtils.getStatusBarHeight(getContext())-ScreenUtils.dp2px(getContext(),55)));
    }

    public void onMove(float curX, float curY){
        getScrollView().scrollBy(0, (int) -curY);
    }

    public void onUp(){
        setVisible(false);
    }

    private void setVisible(boolean visible){
        getScrollView().setVisibility(visible ? VISIBLE : INVISIBLE);
    }

    private ScrollView getScrollView(){
        return FloatingManager.getInstance().getParent();
    }

    //随机生成图片宽和高
    private void randomImageSize(Bitmap bitmap) {
        Random random = new Random();
        for(int i=0; i<imageCountTop+imageCountDown; i++){
            int width = (int) (random.nextFloat()*(MAX_WIDTH_RANDOM-MIN_WIDTH_RANDOM) + MIN_WIDTH_RANDOM);
            int height = (int) (((float)width/bitmap.getWidth())*bitmap.getHeight());

            ImageItem item = new ImageItem();
            item.setWidth(width);
            item.setHeight(height);
            if(i<imageCountTop){
                imageItemsTop.add(item);
            }else {
                imageItemsDown.add(item);
            }
        }
    }

    //随机生成图片，点击图上面的图片
    private void randomImageViewTop(final ImageView bottom, final ImageView left, final ImageView right,final int position) {
        if(position >= imageItemsTop.size()) return;

        final int p = position + 1;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ImageItem imageSize = imageItemsTop.get(position);
                ImageView image = new ImageView(getContext());
                MarginLayoutParams lp = new MarginLayoutParams(imageSize.getWidth(), imageSize.getHeight());
                if(left == null && right == null){
                    //开始画除了点击的图片之外的第一张图片
                    float lOrR = new Random().nextFloat();
                    if(lp.width < (bottom.getLeft()-CUSTOM_MARGIN)){
                        //缩进到点击图的侧边
                        lp.topMargin = MAX_HEIGHT/2 - bottom.getHeight()/2 - lp.height + CUSTOM_MARGIN;
                        if(lOrR < 0.5){
                            //排左边
                            lp.leftMargin = (bottom.getLeft()-lp.width)/2;
                            addView(image, lp);
                            randomImageViewTop(bottom, image, null, p);
                        }else {
                            //排右边
                            lp.leftMargin = (bottom.getLeft()-lp.width)/2 + bottom.getRight();
                            addView(image, lp);
                            randomImageViewTop(bottom, null, image, p);
                        }
                    }else {
                        //排在点击图的上面
                        lp.topMargin = MAX_HEIGHT/2 - bottom.getHeight()/2 - lp.height - CUSTOM_MARGIN;
                        if(lOrR < 0.5){
                            //排左边
                            lp.leftMargin = (int) (CUSTOM_MARGIN * lOrR * 3);
                            addView(image, lp);
                            randomImageViewTop(bottom, image, null, p);
                        }else {
                            //排右边
                            lp.leftMargin = (int) (getScrollView().getWidth() - lp.width - (CUSTOM_MARGIN * lOrR * 2));
                            addView(image, lp);
                            randomImageViewTop(bottom, null, image, p);
                        }
                    }
                }else if(left != null && right == null){
                    if(lp.width > (getScrollView().getWidth()-left.getRight()-CUSTOM_MARGIN)){
                        lp.width = getScrollView().getWidth()-left.getRight()-CUSTOM_MARGIN;
                    }

                    int bottomRandom = (int) (new Random().nextFloat()*CUSTOM_MARGIN*3);
                    lp.topMargin = bottom.getTop() - lp.height - bottomRandom;
                    lp.leftMargin = left.getRight() + ((getScrollView().getWidth()-left.getRight()-lp.width)/2);
                    addView(image, lp);

                    if(left.getTop() > lp.topMargin){
                        randomImageViewTop(left, null, image, p);
                    }else {
                        randomImageViewTop(image, left, null, p);
                    }
                }else if(left == null && right != null){
                    if(lp.width > (right.getLeft()-CUSTOM_MARGIN)){
                        lp.width = right.getLeft()-CUSTOM_MARGIN;
                    }

                    int bottomRandom = (int) (new Random().nextFloat()*CUSTOM_MARGIN*3);
                    lp.topMargin = bottom.getTop() - lp.height - bottomRandom;
                    lp.leftMargin = (right.getLeft() - lp.width)/2;
                    addView(image, lp);
                    if(right.getTop() > lp.topMargin){
                        randomImageViewTop(right, image, null, p);
                    }else {
                        randomImageViewTop(image, null, right, p);
                    }
                }
            }
        }, 200);
    }

    //随机生成图片，点击图下面的图片
    private void randomImageViewDown(final ImageView top, final ImageView left, final ImageView right,final int position) {
        if(position >= imageItemsDown.size()) return;

        final int p = position + 1;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ImageItem imageSize = imageItemsDown.get(position);
                ImageView image = new ImageView(getContext());
                MarginLayoutParams lp = new MarginLayoutParams(imageSize.getWidth(), imageSize.getHeight());
                if(left == null && right == null){
                    //开始画除了点击的图片之外的第一张图片
                    float lOrR = new Random().nextFloat();
                    if(lp.width < (top.getLeft()-CUSTOM_MARGIN)){
                        //缩进到点击图的侧边
                        lp.topMargin = MAX_HEIGHT/2 + top.getHeight()/2 - CUSTOM_MARGIN;
                        if(lOrR < 0.5){
                            //排左边
                            lp.leftMargin = (top.getLeft()-lp.width)/2;
                            addView(image, lp);
                            randomImageViewDown(top, image, null, p);
                        }else {
                            //排右边
                            lp.leftMargin = (top.getLeft()-lp.width)/2 + top.getRight();
                            addView(image, lp);
                            randomImageViewDown(top, null, image, p);
                        }
                    }else {
                        //排在点击图的下面
                        lp.topMargin = MAX_HEIGHT/2 + top.getHeight()/2 + CUSTOM_MARGIN;
                        if(lOrR < 0.5){
                            //排左边
                            lp.leftMargin = (int) (CUSTOM_MARGIN * lOrR * 3);
                            addView(image, lp);
                            randomImageViewDown(top, image, null, p);
                        }else {
                            //排右边
                            lp.leftMargin = (int) (getScrollView().getWidth() - lp.width - (CUSTOM_MARGIN * lOrR * 2));
                            addView(image, lp);
                            randomImageViewDown(top, null, image, p);
                        }
                    }
                }else if(left != null && right == null){
                    if(lp.width > (getScrollView().getWidth()-left.getRight()-CUSTOM_MARGIN)){
                        lp.width = getScrollView().getWidth()-left.getRight()-CUSTOM_MARGIN;
                    }

                    int bottomRandom = (int) (new Random().nextFloat()*CUSTOM_MARGIN*3);
                    lp.topMargin = top.getBottom() + bottomRandom;
                    lp.leftMargin = left.getRight() + ((getScrollView().getWidth()-left.getRight()-lp.width)/2);
                    addView(image, lp);

                    if(left.getBottom() > (lp.topMargin+lp.height)){
                        randomImageViewDown(image, left, null, p);
                    }else {
                        randomImageViewDown(left, null, image, p);
                    }
                }else if(left == null && right != null){
                    if(lp.width > (right.getLeft()-CUSTOM_MARGIN)){
                        lp.width = right.getLeft()-CUSTOM_MARGIN;
                    }

                    int bottomRandom = (int) (new Random().nextFloat()*CUSTOM_MARGIN*3);
                    lp.topMargin = top.getBottom() + bottomRandom;
                    lp.leftMargin = (right.getLeft() - lp.width)/2;
                    addView(image, lp);
                    if(right.getBottom() > (lp.topMargin+lp.height)){
                        randomImageViewDown(image, null, right, p);
                    }else {
                        randomImageViewDown(right, image, null, p);
                    }
                }
            }
        }, 200);
    }

    private void addView(ImageView image, MarginLayoutParams lp){
        image.setLayoutParams(lp);
//        image.setImageResource(R.drawable.item2);
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        addView(image);
        Glide.with(getContext())
                .asBitmap()
                .transition(new BitmapTransitionOptions().crossFade(2500))
                .load(R.drawable.item2)
                .into(image);
    }

}
