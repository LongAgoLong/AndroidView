package com.leo.androidview;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.leo.system.IntentUtil;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private Button mWeiboBtn;
    private Button mImageBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mWeiboBtn = findViewById(R.id.weiboBtn);
        mWeiboBtn.setOnClickListener(this);
        mImageBtn = findViewById(R.id.imageBtn);
        mImageBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.weiboBtn:
                IntentUtil.startActivity(this, WeiboViewActivity.class);
                break;
            case R.id.imageBtn:
                IntentUtil.startActivity(this, ImageActivity.class);
                break;
        }
    }
}
