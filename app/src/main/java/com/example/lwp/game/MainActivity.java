package com.example.lwp.game;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashMap;
import java.util.Vector;

import static android.R.attr.pathPattern;
import static android.R.attr.scaleWidth;


public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        //设置游戏场景Screen
        setContentView(new Screen(this));

    }


    @Override
    public void onBackPressed() {//按返回键可以直接返回主菜单
        super.onBackPressed();

        Intent intent_Menu = new Intent(MainActivity.this, Menu.class); //新建Intent对象
        MainActivity.this.startActivity(intent_Menu);                    //回到主菜单

        MainActivity.this.finish();
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

//玩家类
class Player
{
    public int score;    //得分
    public float x, y;   //当前坐标
    public Bullet bullets[] = new Bullet[50];    //玩家子弹
    float w, h;           //玩家的宽、高
    private int bullet_size;     //子弹数量
    Bitmap bitmap;         //玩家图片
    int b_w, b_h;          //子弹宽、高
    Player(float a, float b, Bitmap bitmap, int b_w, int b_h)
    {
        score = 0;
        x = a;
        y = b;
        bullet_size = 50;
        this.bitmap = bitmap;
        w = bitmap.getWidth();
        h = bitmap.getHeight();
        this.b_w = b_w;
        this.b_h = b_h;
        for(int i = 0; i < bullet_size; ++i)    //创建所有子弹
        {
            bullets[i] = new Bullet((float)(x + bitmap.getWidth() * 0.5 - b_w * 0.5), y, b_w, b_h);
        }
    }
    public void shoot()   //玩家射击子弹
    {
        for(int i = 0; i < bullet_size; ++i)   //遍历子弹数组，找到一颗不在屏幕上显示的子弹
        {
            if(!bullets[i].getisDraw())
            {
                bullets[i].setDraw();           //设置子弹的可见属性，在下一次刷新画在屏幕上
                bullets[i].newBullet((float)(x + bitmap.getWidth() * 0.5 - (float)(b_w) * 0.5), y);  //初始化子弹发射位置
                break;
            }
        }
    }


    public int getBullet_size()
    {
        return bullet_size;
    }   //获取子弹总数量
}
//玩家子弹类
class Bullet
{
    public float x, y;   //子弹位置
    public int w, h;     //子弹宽、高
    private boolean isdraw;  //子弹的可见性，根据这个属性判断子弹是否要画在屏幕上

    Bullet(float a, float b, int w, int h)
    {
        x = a;
        y = b;
        this.w = w;
        this.h = h;
        isdraw = false;

    }

    //子弹更想
    public void update()
    {
        y = y - 9;
    }
    //画出子弹
    public void draw(Canvas canvas, Paint paint, Bitmap bitmap)
    {
        canvas.drawBitmap(bitmap, x, y, paint);
    }
    //设置子弹isdraw属性为true，画出子弹
    public void setDraw()
    {
        this.isdraw = true;
    }
    //设置子弹isdraw属性为false，子弹消失
    public void setnotDraw()
    {
        this.isdraw = false;
    }
    //获取子弹isdraw属性
    public boolean getisDraw()
    {
        return isdraw;
    }
    //创建子弹
    public void newBullet(float a, float b)
    {
        x = a;
        y = b;
    }
}
//敌人（小型、中型）
class Enemy
{
    public int x, y;      //敌人位置
    int w, h, level;      //宽、高、level 0是小型，1是中型
    private int speed;   //敌人移动速度
    private boolean isdraw;  //是否要画出来
    private Bitmap bitmap;    //敌人图片
    Enemy(int width, Bitmap bitmap)
    {
        x = (int)(Math.random() * width * 0.7) + 30;   //随机出现的位置
        y = 1;                                         //在屏幕顶部出现
        speed = (int)(Math.random() * 5) + 7;         //速度随机
        this.w = bitmap.getWidth();
        this.h = bitmap.getHeight();
        isdraw = false;
        level = 0;
        this.bitmap = bitmap;
    }
    //画出敌人
    public void draw(Canvas canvas, Paint paint)
    {
        canvas.drawBitmap(bitmap, x, y, paint);
    }
    //更新敌人位置
    public void update()
    {
            y = y + speed;
    }
    //设置敌人是否要画出来
    public void setDraw()
    {
        this.isdraw = true;
    }
    public void setnotDraw()
    {
        this.isdraw = false;
    }

    //获取isdraw属性
    public boolean getisDraw()
    {
        return isdraw;
    }

    //初始化敌人的出现位置
    public void randomEnemy(int width)
    {
        x = (int)(Math.random() * width * 0.7) + 30;
        y = 1;
        speed = (int)(Math.random() * 4) + 5;
    }
}
//背景类
class Background{
    public float x, y;     //背景位置
    public Bitmap bitmap;  //背景图片
    int speed;             //背景滚动速度
    Background(Bitmap bm, int width, int height)   //width、height是手机屏幕的宽、高
    {
        int ow = bm.getWidth(), oh = bm.getHeight();       //背景图片原始大小
        //算出缩放比例，主要是宽度能填满屏幕，用3张图片实现背景的循环滚动
        float scalewidth = ((float)width) / ow, scaleheight = ((float)height) / oh;
        Matrix matrix = new Matrix();
        matrix.postScale(scalewidth, scalewidth);
        bitmap = Bitmap.createBitmap(bm,0,0,ow,oh,matrix,false);   //得到缩放后的图片
        speed = 2;
    }
    //画出背景
    public void draw(Canvas canvas, Paint paint)
    {
        canvas.drawBitmap(bitmap, x, y, paint);
    }
    //背景滚动
    public void update()
    {
        y = y + speed;
    }

}
//Boss类
class Boss
{
    public int x, y;//位置
    public Bitmap bitmap;//图片
    private int speed, time, xdirection, ydirection;//速度，向左或右某个方向移动的时间，xdirection：0向左移动，1向右移动，ydirection：1向下移动，-1向上移动
    public int hp;//Boss血量
    float width;  //屏幕宽
    //boolean left;

    Boss(Bitmap bitmap, float width)
    {
        x = (int)(Math.random() * width * 0.5);//出现位置
        y = -bitmap.getHeight();
        time = (int)(Math.random() * 10) + 30;  //向一个方向移动一段随机的时间
        speed = 4; //速度

        //Boss放大一点
        Matrix matrix = new Matrix();
        matrix.postScale((float)1.3, (float)1.3);
        this.bitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,false);
        //this.bitmap = bitmap;


        hp = 20;
        this.width = width;
        xdirection  = (int) (Math.random() * 2); //随机向左或右移动
        ydirection = 1;                          //一开始先向下移动

    }
    //画出Boss
    public void draw(Canvas canvas, Paint paint)
    {
        canvas.drawBitmap(bitmap, x, y, paint);
    }
    //Boss更新
    public void update() {
        speed = (int) (Math.random() * 5) + 5;  //随机速度
        y = y + (int)(Math.random() * 3) * ydirection;  //更新y轴位置。ydirection：1向下移动，-1向上移动
        if(y < 0)      //到屏幕顶部就换方向
        {
            ydirection = 1;
        }
        if(y > bitmap.getHeight() + 10)    //将Boss的y轴上的移动限制在一定的范围
        {
            ydirection = -1;  //转向
        }
        if (time == 0)                       //想某个方向移动的时间结束
        {
            xdirection  = (int) (Math.random() * 2);    //随机生成新的移动方向（左或右）
            time = (int)(Math.random() * 10) + 30;       //随机生成新的时间
        }

        if(xdirection == 0)                //向左移动
        {
            if(x - speed > 0)             //判断是否到了最左边
            {
                x -= speed;
            }
            else                          //到达屏幕最左边就马上改变方向，避免Boss一直停在边上
            {
                xdirection = 1;

            }
        }
        else                             //向右移动同上
        {
            if(x + speed < width - bitmap.getWidth())
            {
                x += speed;
            }
            else
            {
                xdirection = 0;

            }
        }
        --time;   //记录移动的时间，时间到了就再随机移动方向和速度
    }
}
//Boss子弹
class BossBullet
{
    public int x, y, speed;  //位置，速度
    public Bitmap bitmap;    //图片
    public boolean isdraw;  //是否可画
    BossBullet(Bitmap bitmap, int x, int y)
    {
        this.bitmap = bitmap;
        isdraw = false;
        speed = 15;
        this.x = x;
        this.y = y;
    }
    //更新位置
    public void update()
    {
        y += speed;
    }
    //画出子弹
    public void draw(Canvas canvas, Paint paint)
    {
        canvas.drawBitmap(bitmap, x, y, paint);
    }
}