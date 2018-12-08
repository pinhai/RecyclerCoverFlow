package com.recycler.coverflow.poker.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.hai.floatinglayer.util.ScreenUtils;
import com.recycler.coverflow.R;

public class PokerView extends View {

    private Paint paint;

    private int[] pokers;
    private Bitmap[] pokerBmp;
    private Matrix[] pokerMatrix;

    //牌的数量对应侧面第一张牌的旋转角度，从0开始
    private int[] pokerCorner = new int[]{0, 0, 30, 30, 25, 20, 15, 10, 10};

    private int width, height;  //view的宽度和高度

    public PokerView(Context context) {
        this(context, null);
    }

    public PokerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PokerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        if(pokers != null && pokers.length > 0){
            Bitmap bitmap = getPokerBitmap(0);
            int w = bitmap.getWidth(), h = bitmap.getHeight();
            height = (int) (Math.sqrt(w*w + h*h) + getPaddingLeft() + getPaddingRight() + ScreenUtils.dp2px(getContext(), 20));
            width = 2*h + getPaddingLeft() + getPaddingRight();
            setMeasuredDimension(width, height);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(pokers == null) return;
        for(int i=0; i<pokers.length; i++){
            pokerBmp[i] = getPokerBitmap(i);
            pokerMatrix[i] = new Matrix();
            pokerMatrix[i].postRotate(getDegree(pokers.length, i), pokerBmp[i].getWidth()/2, pokerBmp[i].getHeight());
            pokerMatrix[i].postTranslate(width/2-pokerBmp[i].getWidth()/2, height/2-pokerBmp[i].getHeight()/2);

            canvas.drawBitmap(pokerBmp[i], pokerMatrix[i], paint);
        }
    }

    private Bitmap getPokerBitmap(int i) {
        Bitmap result;
        switch (pokers[i]){
            case 0:
                result = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.back);
                break;
            case 5:
                result = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.front);
                break;
            case 10:
                result = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.front);
                break;
            default:
                result = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.front);
        }
        return result;
    }

    public void setPokers(int[] pokers){
        this.pokers = pokers;
        pokerBmp = new Bitmap[pokers.length];
        pokerMatrix = new Matrix[pokers.length];
        requestLayout();
        invalidate();
    }

    private float getDegree(int total, int index) {
        int perD = total < pokerCorner.length ? pokerCorner[total] : pokerCorner[pokerCorner.length-1];
        return -(perD * (total - 1))/2 + index*perD;
    }
}
