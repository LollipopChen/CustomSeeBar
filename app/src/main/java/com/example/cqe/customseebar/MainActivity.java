package com.example.cqe.customseebar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.levelView)
    HSeeBarLevelView hSeeBarLevelView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind( this ); //使用框架绑定控件
        //设置值：开始值，总值，当前值
        //TODO 注意：在列表中使用时，布局外需要裹一层父布局，否则不显示
        hSeeBarLevelView.resetLevelProgress(0f, 1000, 500);
    }
}
