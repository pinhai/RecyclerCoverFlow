package com.recycler.coverflow.poker;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.recycler.coverflow.R;
import com.recycler.coverflow.poker.widget.PokerView;

public class RotatePokerActivity2 extends AppCompatActivity {

    private static final int duration = 500;

    private View frame;
    PokerView pokerViewBack;
    PokerView pokerView;

    private Spinner spinner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rotate_poker2);

        frame = findViewById(R.id.frame);
        pokerViewBack = findViewById(R.id.pokerViewBack);
//        pokerViewBack.setPokers(new int[]{0});
        pokerView = findViewById(R.id.pokerView);
//        pokerView.setPokers(new int[]{10});

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
                int[] p = new int[position+1];
                int[] pBack = new int[position+1];
                for(int i=0; i<p.length; i++){
                    p[i] = 10;
                    pBack[i] = 0;
                }
                pokerView.setPokers(p);
                pokerViewBack.setPokers(pBack);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void switchViewVisibility() {
        if (pokerViewBack.isShown()) {
            pokerViewBack.setVisibility(View.GONE);
            pokerView.setVisibility(View.VISIBLE);
        } else {
            pokerViewBack.setVisibility(View.VISIBLE);
            pokerView.setVisibility(View.GONE);
        }
    }

    public void onClick(View v) {
        if (v.getId() == R.id.button) {
            int direction = 1;
            if (pokerViewBack.isShown()) {
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
}
