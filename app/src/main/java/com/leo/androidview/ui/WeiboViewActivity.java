package com.leo.androidview.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import com.leo.androidview.R;
import com.leo.weibotext.WeiboEditText;
import com.leo.weibotext.WeiboTextView;
import com.leo.weibotext.callback.IWBClickImpl;
import com.leo.weibotext.mode.WBClickMode;

/**
 * Created by LEO
 * On 2019/6/22
 * Description:仿微博@#控件
 */
public class WeiboViewActivity extends BaseActivity implements View.OnClickListener {
    private static final String CONTENT = "我就是随便说一下@测试 @测试2号 就这样@测试3号 巴啦啦#来个话题吧#@莫名其妙 不知道说啥的" +
            "@斗破苍穹 @武动乾坤 @大主宰 @元尊 这些年随便看一看不知道为啥就这样谁知道和http://www.baidu.com韩国社社格和韩国和供货商巴拉巴拉不知道写#什么好哟#" +
            "http://www.baidu.com";
    private Button mTopicBtn;
    private Button mAtBtn;
    private WeiboTextView mWbTextView;
    private WeiboEditText mWbEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weiboview);
        initView();
    }

    @Override
    protected void initActionBar() {
        super.initActionBar();
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("仿微博@#控件");
        }
    }

    private void initView() {
        mTopicBtn = findViewById(R.id.topicBtn);
        mTopicBtn.setOnClickListener(this);
        mAtBtn = findViewById(R.id.atBtn);
        mAtBtn.setOnClickListener(this);

        mWbTextView = findViewById(R.id.wbTextView);
        mWbTextView.setiWBClickImpl(new IWBClickImpl.IWBClickImplAdapter() {
            @Override
            public void onContentClick(int wbMode, String str) {
                super.onContentClick(wbMode, str);
                switch (wbMode) {
                    case WBClickMode.CALL:
                        showToast("at:" + str);
                        break;
                    case WBClickMode.TOPIC:
                        showToast("topic:" + str);
                        break;
                    case WBClickMode.HTML:
                        showToast("html:" + str);
                        break;
                    default:
                        break;
                }
            }
        });
        mWbEditText = findViewById(R.id.wbEditText);
        mWbEditText.setWBClickImpl(new IWBClickImpl.IWBClickImplAdapter() {
            @Override
            public void onContentClick(int wbMode, String str) {
                super.onContentClick(wbMode, str);
                switch (wbMode) {
                    case WBClickMode.CALL:
                        showToast("at:" + str);
                        break;
                    case WBClickMode.TOPIC:
                        showToast("topic:" + str);
                        break;
                    default:
                        break;
                }
            }
        });
        mWbTextView.setWbContent(CONTENT);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.topicBtn:
                mWbEditText.appendTopic("#随机话题#");
                break;
            case R.id.atBtn:
                mWbEditText.appendCall("@随机at ");
                break;
        }
    }

    private synchronized void showToast(String str) {
        Toast toast = Toast.makeText(WeiboViewActivity.this, str, Toast.LENGTH_SHORT);
        toast.show();
    }
}
