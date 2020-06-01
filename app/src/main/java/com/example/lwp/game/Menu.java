package com.example.lwp.game;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Menu extends Activity {

    Button button_start, button_rank, button_quit, button_introduction;
    //public SoundPool soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
    //int music;
    AlertDialog alertDialog;//退出游戏弹窗
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

       //music = soundPool.load(this, R.raw.boom, 1);
        //开始游戏按钮
        button_start = (Button) findViewById(R.id.button_start);   //开始按钮
        button_start.setOnClickListener(new onclick_start());       //监听点击事件
        //个人记录按钮
        button_rank = (Button)findViewById(R.id.button_rank);
        button_rank.setOnClickListener(new onclick_rank());
        //退出游戏按钮
        button_quit = (Button) findViewById(R.id.button_quit);
        button_quit.setOnClickListener(new onclick_quit());
        //说明页面按钮
        button_introduction = (Button) findViewById(R.id.button_introduction);
        button_introduction.setOnClickListener(new onclick_introduction());
        //创建退出游戏弹窗
        alertDialog = new AlertDialog.Builder(this)
                .setTitle("是否退出游戏")
                //.setMessage("有多个按钮")
                //.setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("是", new DialogInterface.OnClickListener() {//添加重新开始按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Menu.this.finish();
                        System.exit(0);
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {//添加按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialog.dismiss();
                    }
                }).setCancelable(false)
                .create();

    }


    class onclick_start implements View.OnClickListener {            //设置点击后跳转到游戏界面
        public void onClick(View v) {
            Intent intent_play = new Intent(); //新建Intent对象
            intent_play.setClass(Menu.this, MainActivity.class);
            startActivityForResult(intent_play, 0);
            Menu.this.finish();
        }
    }

    class onclick_rank implements  View.OnClickListener{
        public void onClick(View v){
            Intent intent_back = new Intent();
            intent_back.setClass(Menu.this, Rank.class);
            startActivityForResult(intent_back, 0);
            Menu.this.finish();
        }
    }

    class onclick_quit implements View.OnClickListener{
        public void onClick(View v){
            alertDialog.show();
        }
    }

    class onclick_introduction implements View.OnClickListener{
        public void onClick(View v){
            Intent intent_introduction = new Intent();
            intent_introduction.setClass(Menu.this, Introduction.class);
            startActivityForResult(intent_introduction, 0);
            Menu.this.finish();
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
