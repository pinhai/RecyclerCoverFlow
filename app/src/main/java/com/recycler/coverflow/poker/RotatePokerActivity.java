package com.recycler.coverflow.poker;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.recycler.coverflow.R;

public class RotatePokerActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int duration = 500;

    private View button;
    private View frame;
    private View front;
    private View back;

    private Spinner spinner;

    private int[] backIds, frontIds;
    private ImageView[] backPokers, frontPokers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_rotate_poker);

        backIds = new int[]{R.id.backCenter, R.id.backLeft1, R.id.backRight1, R.id.backLeft2, R.id.backRight2, R.id.backLeft3,
                R.id.backRight3, R.id.backLeft4, R.id.backRight4, R.id.backLeft5, R.id.backRight5};
        frontIds = new int[]{ R.id.frontCenter, R.id.frontLeft1, R.id.frontRight1, R.id.frontLeft2, R.id.frontRight2, R.id.frontLeft3,
                R.id.frontRight3, R.id.frontLeft4, R.id.frontRight4, R.id.frontLeft5, R.id.frontRight5};
        backPokers = new ImageView[backIds.length];
        frontPokers = new ImageView[frontIds.length];
        for(int i=0; i<backIds.length; i++){
            backPokers[i] = findViewById(backIds[i]);
        }
        for(int i=0; i<frontIds.length; i++){
            frontPokers[i] = findViewById(frontIds[i]);
        }

        button = findViewById(R.id.button);
        frame = findViewById(R.id.frame);
        front = findViewById(R.id.front);
        back = findViewById(R.id.back);
        button.setOnClickListener(this);

        spinner = findViewById(R.id.spinner);
        String[] num = new String[11];
        for (int i=0; i<num.length; i++){
            num[i] = (i+1)+"";
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.view_text, num);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setVisibleNum(position+1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        setVisibleNum(1);
    }

    private void switchViewVisibility() {
        if (back.isShown()) {
            back.setVisibility(View.GONE);
            front.setVisibility(View.VISIBLE);
        } else {
            back.setVisibility(View.VISIBLE);
            front.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == button) {
            int direction = 1;
            if (back.isShown()) {
                direction = -1;
            }
            ViewAnimUtils.flip(frame, duration, direction);
            frame.postDelayed(new Runnable() {
                @Override
                public void run() {
                    switchViewVisibility();
                }
            }, duration);
        }
    }

    private void setVisibleNum(int num){
        for(int i=0; i<backPokers.length; i++){
            if(i < num){
                backPokers[i].setVisibility(View.VISIBLE);
                frontPokers[i].setVisibility(View.VISIBLE);
            }else {
                backPokers[i].setVisibility(View.GONE);
                frontPokers[i].setVisibility(View.GONE);
            }
        }
    }
}
