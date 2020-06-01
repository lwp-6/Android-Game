package com.example.lwp.game;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Rank extends Activity {

    Button button_back, button_clear;
    AlertDialog alertDialog;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);
        //返回按钮
        button_back = (Button) findViewById(R.id.button_back);
        //button_back.setClickable(true);
        button_back.setOnClickListener(new Rank.onclick_back());       //监听点击事件
        //清除信息按钮
        button_clear = (Button) findViewById(R.id.button_clear);
        button_clear.setOnClickListener(new Rank.onclick_clear());

        alertDialog = new AlertDialog.Builder(this)
                .setTitle("是否删除全部记录")
                //.setMessage("有多个按钮")
                //.setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("是", new DialogInterface.OnClickListener() {//添加重新开始按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //删除所有数据
                        DBHelper helper = new DBHelper(Rank.this);
                        SQLiteDatabase db = helper.getWritableDatabase();
                        db.execSQL("delete from Rank");
                        db.close();

                        //刷新，重新打开自己，需要销毁之前的Activity
                        Intent intent_restart = new Intent(Rank.this, Rank.class);
                        Rank.this.finish();
                        Rank.this.startActivity(intent_restart);
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {//添加按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialog.dismiss();
                    }
                })
                .create();

        //sharedpreferences
        //首先获取一个SharedPreferences对象
        /*SharedPreferences preferences = getSharedPreferences("spdata", Context.MODE_PRIVATE);
        //然后通过键的方式取出，后边是如果找不到的默认内容
        String name=preferences.getString("name","");
        int age=preferences.getInt("age",0);
        System.out.println(name + age);*/

        //创建DBhelper
        DBHelper helper = new DBHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from Rank", null);//获取Rank数据库所有数据



        //实现listview分两列
        List<Map<String, Object>> listItems = new ArrayList<Map<String,Object>>();
        //放数据
        while(cursor.moveToNext())
        {
            int score = cursor.getInt(cursor.getColumnIndex("score"));
            String date = cursor.getString(cursor.getColumnIndex("date"));
            Map<String, Object> listItem = new HashMap<String,Object>();

            listItem.put("score", score);
            listItem.put("date", date);

            listItems.add(listItem);
        }
        //适配器
        ListView listView1 = (ListView) findViewById(R.id.list_rank);
        SimpleAdapter simpleAdapter=new SimpleAdapter(
                this,                                  //上下文
                listItems,                             //数据
                R.layout.listview_item,              //列表item的布局
                new String[]{"score","date"},        // 数据索引
                new int[]{R.id.item_score, R.id.item_date});     //列表item id
        listView1.setAdapter(simpleAdapter);          //设置适配器

    }

    class onclick_back implements View.OnClickListener {            //设置点击后跳转到主菜单
        public void onClick(View v) {
            Intent intent_back = new Intent(); //新建Intent对象
            intent_back.setClass(Rank.this, Menu.class);
            startActivityForResult(intent_back, 0);   //返回前一页
            Rank.this.finish();
            //Rank.this.onDestroy();
        }
    }

    class onclick_clear implements  View.OnClickListener{
        public void onClick(View v)
        {
            alertDialog.show();
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

