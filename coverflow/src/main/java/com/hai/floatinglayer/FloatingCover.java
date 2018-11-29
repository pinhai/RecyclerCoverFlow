package com.hai.floatinglayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.hai.floatinglayer.model.ImageItem;
import com.hai.floatinglayer.util.ScreenUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import recycler.coverflow.R;

public class FloatingCover extends RelativeLayout {

    private static final int MAX_HEIGHT = 10000;  //最大高度，确保上下滑动不会到边界
    private static final int CUSTOM_MARGIN = ScreenUtils.dp2px(10);

    private int MAX_WIDTH_RANDOM = 800, MIN_WIDTH_RANDOM = 300;  //随机生成图片宽度范围
    int imageCountTop, imageCountDown;
    List<ImageItem> imageItemsTop, imageItemsDown;
    List<String> mUrls;  //图片地址

    ImageView centerImg;

    long lastMillis;
    static final float SPEED_THRESHOLD = ScreenUtils.dp2px(100);  //速度阈值，超过该值就按比率增加滑动速度，单位：像素/s

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

    public void onLongClickEvent(List<String> urls){
        mUrls = urls;
        imageCountDown = urls.size()/2;
        imageCountTop = urls.size() - imageCountDown;

        setVisible(true);
        randomImageSize(urls);
//        randomImageViewTop(centerImg, null, null, 0);
//        randomImageViewDown(centerImg, null, null, 0);
    }

    public void onDown(FloatingImage image){
        removeAllViews();
        imageItemsTop.clear();
        imageItemsDown.clear();

        lastMillis = System.currentTimeMillis();

        int[] location2 = new int[2] ;
        image.getLocationOnScreen(location2);//获取在整个屏幕内的绝对坐标

        centerImg = new ImageView(getContext());
        LayoutParams lp = new LayoutParams(image.getWidth(), image.getHeight());
        lp.addRule(CENTER_IN_PARENT);
        centerImg.setLayoutParams(lp);
        centerImg.setImageDrawable(image.getDrawable());
        centerImg.setScaleType(ImageView.ScaleType.CENTER_CROP);
        addView(centerImg);

        getScrollView().scrollTo(0, (MAX_HEIGHT-lp.height)/2-
                (location2[1]-ScreenUtils.getStatusBarHeight(getContext())-ScreenUtils.dp2px(getContext(),55)));
    }

    public void onMove(float curX, float curY){
        long curM = System.currentTimeMillis();
        float speed = Math.abs(curY)*1000/(curM-lastMillis);
        lastMillis = curM;
        if(speed > SPEED_THRESHOLD){
            float disY = curY*(speed/SPEED_THRESHOLD);
            getScrollView().scrollBy(0, (int) -disY);
        }else {
            getScrollView().scrollBy(0, (int) -curY);
        }
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
    private void randomImageSize(List<String> urls) {
        final Random random = new Random();
        for(int i=0; i<imageCountTop+imageCountDown; i++){
            final int iT = i;
            Glide.with(getContext())
                    .asBitmap()
                    .load(urls.get(i))
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                            int width = (int) (random.nextFloat()*(MAX_WIDTH_RANDOM-MIN_WIDTH_RANDOM) + MIN_WIDTH_RANDOM);
                            int height = (int) (((float)width/bitmap.getWidth())*bitmap.getHeight());

                            ImageItem item = new ImageItem();
                            item.setWidth(width);
                            item.setHeight(height);
                            if(iT<imageCountTop){
                                imageItemsTop.add(item);
                                if(imageItemsTop.size() == imageCountTop){
                                    randomImageViewTop(centerImg, null, null, 0);
                                }
                            }else {
                                imageItemsDown.add(item);
                                if(imageItemsDown.size() == imageCountDown){
                                    randomImageViewDown(centerImg, null, null, 0);
                                }
                            }
                        }
                    });
        }
    }

    //随机生成图片，点击图上面的图片
    private void randomImageViewTop(final ImageView bottom, final ImageView left, final ImageView right,final int position) {
        if(position >= imageItemsTop.size()) return;

        final int nextPos = position + 1;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(position >= imageItemsDown.size()) return;
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
                            addView(image, lp, position);
                            randomImageViewTop(bottom, image, null, nextPos);
                        }else {
                            //排右边
                            lp.leftMargin = (bottom.getLeft()-lp.width)/2 + bottom.getRight();
                            addView(image, lp, position);
                            randomImageViewTop(bottom, null, image, nextPos);
                        }
                    }else {
                        //排在点击图的上面
                        lp.topMargin = MAX_HEIGHT/2 - bottom.getHeight()/2 - lp.height - CUSTOM_MARGIN;
                        if(lOrR < 0.5){
                            //排左边
                            lp.leftMargin = (int) (CUSTOM_MARGIN * lOrR * 3);
                            addView(image, lp, position);
                            randomImageViewTop(bottom, image, null, nextPos);
                        }else {
                            //排右边
                            lp.leftMargin = (int) (getScrollView().getWidth() - lp.width - (CUSTOM_MARGIN * lOrR * 2));
                            addView(image, lp, position);
                            randomImageViewTop(bottom, null, image, nextPos);
                        }
                    }
                }else if(left != null && right == null){
                    if(lp.width > (getScrollView().getWidth()-left.getRight()-CUSTOM_MARGIN)){
                        lp.width = getScrollView().getWidth()-left.getRight()-CUSTOM_MARGIN;
                    }

                    int bottomRandom = (int) (new Random().nextFloat()*CUSTOM_MARGIN*2 + CUSTOM_MARGIN);
                    lp.topMargin = bottom.getTop() - lp.height - bottomRandom;
                    lp.leftMargin = left.getRight() + ((getScrollView().getWidth()-left.getRight()-lp.width)/2);
                    addView(image, lp, position);

                    if(left.getTop() > lp.topMargin){
                        randomImageViewTop(left, null, image, nextPos);
                    }else {
                        randomImageViewTop(image, left, null, nextPos);
                    }
                }else if(left == null && right != null){
                    if(lp.width > (right.getLeft()-CUSTOM_MARGIN)){
                        lp.width = right.getLeft()-CUSTOM_MARGIN;
                    }

                    int bottomRandom = (int) (new Random().nextFloat()*CUSTOM_MARGIN*2 + CUSTOM_MARGIN);
                    lp.topMargin = bottom.getTop() - lp.height - bottomRandom;
                    lp.leftMargin = (right.getLeft() - lp.width)/2;
                    addView(image, lp, position);
                    if(right.getTop() > lp.topMargin){
                        randomImageViewTop(right, image, null, nextPos);
                    }else {
                        randomImageViewTop(image, null, right, nextPos);
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
                if(position >= imageItemsDown.size()) return;
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
                            addView(image, lp, position+imageCountTop);
                            randomImageViewDown(top, image, null, p);
                        }else {
                            //排右边
                            lp.leftMargin = (top.getLeft()-lp.width)/2 + top.getRight();
                            addView(image, lp, position+imageCountTop);
                            randomImageViewDown(top, null, image, p);
                        }
                    }else {
                        //排在点击图的下面
                        lp.topMargin = MAX_HEIGHT/2 + top.getHeight()/2 + CUSTOM_MARGIN;
                        if(lOrR < 0.5){
                            //排左边
                            lp.leftMargin = (int) (CUSTOM_MARGIN * lOrR * 3);
                            addView(image, lp, position+imageCountTop);
                            randomImageViewDown(top, image, null, p);
                        }else {
                            //排右边
                            lp.leftMargin = (int) (getScrollView().getWidth() - lp.width - (CUSTOM_MARGIN * lOrR * 2));
                            addView(image, lp, position+imageCountTop);
                            randomImageViewDown(top, null, image, p);
                        }
                    }
                }else if(left != null && right == null){
                    if(lp.width > (getScrollView().getWidth()-left.getRight()-CUSTOM_MARGIN)){
                        lp.width = getScrollView().getWidth()-left.getRight()-CUSTOM_MARGIN;
                    }

                    int topRandom = (int) (new Random().nextFloat()*CUSTOM_MARGIN*2 + CUSTOM_MARGIN);
                    lp.topMargin = top.getBottom() + topRandom;
                    lp.leftMargin = left.getRight() + ((getScrollView().getWidth()-left.getRight()-lp.width)/2);
                    addView(image, lp, position+imageCountTop);

                    if(left.getBottom() > (lp.topMargin+lp.height)){
                        randomImageViewDown(image, left, null, p);
                    }else {
                        randomImageViewDown(left, null, image, p);
                    }
                }else if(left == null && right != null){
                    if(lp.width > (right.getLeft()-CUSTOM_MARGIN)){
                        lp.width = right.getLeft()-CUSTOM_MARGIN;
                    }

                    int topRandom = (int) (new Random().nextFloat()*CUSTOM_MARGIN*2 + CUSTOM_MARGIN);
                    lp.topMargin = top.getBottom() + topRandom;
                    lp.leftMargin = (right.getLeft() - lp.width)/2;
                    addView(image, lp, position+imageCountTop);
                    if(right.getBottom() > (lp.topMargin+lp.height)){
                        randomImageViewDown(image, null, right, p);
                    }else {
                        randomImageViewDown(right, image, null, p);
                    }
                }
            }
        }, 200);
    }

    private void addView(ImageView image, MarginLayoutParams lp, int position){
//        int[] ids = new int[]{R.drawable.item1, R.drawable.item2, R.drawable.item3, R.drawable.item4,
//                R.drawable.item5, R.drawable.item6, R.drawable.maskarty, R.drawable.maskarty2};
//        int id = ids[(int) (new Random().nextFloat()*ids.length)];
        image.setLayoutParams(lp);
//        image.setImageResource(R.drawable.item2);
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        addView(image);
        Glide.with(getContext())
                .asBitmap()
                .transition(new BitmapTransitionOptions().crossFade(2500))
                .load(mUrls.get(position))
                .into(image);
    }

}
