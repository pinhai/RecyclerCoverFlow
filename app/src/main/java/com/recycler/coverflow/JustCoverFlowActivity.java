package com.recycler.coverflow;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.hai.floatinglayer.FloatingCover;
import com.hai.floatinglayer.FloatingManager;

import recycler.coverflow.CoverFlowLayoutManger;
import recycler.coverflow.RecyclerCoverFlow;

public class JustCoverFlowActivity extends AppCompatActivity implements Adapter.onItemClick {

    private RecyclerCoverFlow mList;
    private int currentPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_just_coverflow);
        initList();
    }

    private void initList() {
        mList = (RecyclerCoverFlow) findViewById(R.id.list);
//        mList.setFlatFlow(true); //平面滚动
//        mList.setGreyItem(true); //设置灰度渐变
//        mList.setAlphaItem(true); //设置半透渐变
        mList.setAdapter(new Adapter(this, this));
        mList.setOnItemSelectedListener(new CoverFlowLayoutManger.OnSelected() {
            @Override
            public void onItemSelected(int position) {
                ((TextView)findViewById(R.id.index)).setText((position+1)+"/"+mList.getLayoutManager().getItemCount());
                currentPos = position;
            }
        });

        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
        FloatingCover floatingCover = (FloatingCover) findViewById(R.id.floating_container);
        FloatingManager.getInstance().init(this, scrollView, floatingCover, new FloatingManager.OnPositionListener() {
            @Override
            public int getCurrentPos() {
                return currentPos;
            }
        });
    }

    @Override
    public void clickItem(int pos) {
        mList.smoothScrollToPosition(pos);
    }

//    @Override
//    public void imgLongClickItem(int pos) {
//        Toast.makeText(this, "长按："+pos, Toast.LENGTH_SHORT).show();
//    }
}
