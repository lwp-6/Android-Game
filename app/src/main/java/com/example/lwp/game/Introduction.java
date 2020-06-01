package com.example.lwp.game;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by lwp on 2020/5/18.
 */

public class Introduction extends Activity {

    Button button_back;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction);

        button_back = (Button) findViewById(R.id.introduction_back);
        button_back.setOnClickListener(new Introduction.onclick_back());
    }

    class onclick_back implements View.OnClickListener{
        public void onClick(View v)
        {
            Intent intent = new Intent();
            intent.setClass(Introduction.this, Menu.class);
            startActivityForResult(intent, 0);   //返回前一页
            Introduction.this.finish();
        }
    }

    //隐藏下方虚拟按键
    @Override
    protected void onResume() {
        super.onResume();
        hideBottomMenu();
    }
    protected void hideBottomMenu() {

        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
}
