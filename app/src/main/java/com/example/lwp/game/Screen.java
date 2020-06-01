package com.example.lwp.game;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static java.lang.Math.max;

public class Screen extends View {

    public Paint p = new Paint();          //创建新画布
    public float touch_x, touch_y;       //手指按下的坐标
    public float x, y, tempx, tempy;     //玩家当前位置，玩家下一帧的位置
    int t = 0, enemy_size = 20, i;        //t是总帧数
    boolean start = true;                 //判断游戏是否要暂停
    DisplayMetrics displayMetrics = getResources().getDisplayMetrics();  //获取屏幕分辨率
    int width = displayMetrics.widthPixels;
    int height = displayMetrics.heightPixels;
    Activity MA;                   //MainActivity的this引用
    AlertDialog alertDialog, successDialog;     //游戏失败后的弹窗，击败Boss后的弹窗
    //各种bitmap资源
    Bitmap player_bitmap, bullets_bitmap, enemy0_bitmap, bg_bitmap, boss_bitmap, enemy1_bitmap, big_hurt_bitmap, boss_bullet;                       //各种图
    Player player;
    public Enemy enemys[] = null;                           //敌人数组
    Background bg[] = null;       //背景，需要3张背景实现背景的循环滚动
    /*RelativeLayout.LayoutParams pauseparams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);*/
    //创建爆炸View的参数
    RelativeLayout.LayoutParams explosionparams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);

    //爆炸声音
    public SoundPool soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
    int music;   //爆炸声音编号
    Boss boss;
    boolean isboss;  //判断Boss是否需要出现
    BossBullet bossBullet;
    public Screen(Activity ma)
    {
        super(ma);
        MA = ma;
        //获取各种bitmap资源
        player_bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.plane);
        bullets_bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.blue_bullet);
        enemy0_bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.small);
        enemy1_bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.middle);
        bg_bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.bg);
        boss_bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.big);
        big_hurt_bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.big_hurt);
        boss_bullet = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.yellow_bullet);
        //创建敌人数组
        enemys = new Enemy[enemy_size];
        //加载爆炸音效
        music = soundPool.load(MA, R.raw.boom, 1);
        //Boss
        boss = new Boss(boss_bitmap, width);
        isboss = false;
        bossBullet = new BossBullet(boss_bullet, (int)(boss.x + boss_bitmap.getWidth() * 0.5 - boss_bullet.getWidth() * 0.5),
                (int)(boss.y + boss_bitmap.getHeight()));

        //初始化玩家
        player = new Player((float)(width  * 0.5 - player_bitmap.getWidth() * 0.5),
                (float)(height - player_bitmap.getHeight()),
                player_bitmap,
                bullets_bitmap.getWidth(),
                bullets_bitmap.getHeight());
        //循环背景，使用3张图片
        bg = new Background[3];
        for(int i = 0; i < 3; ++i)
        {
            bg[i] = new Background(bg_bitmap, width, height);
            bg[i].x = 0;
            bg[i].y = (i - 1) * bg[i].bitmap.getHeight();//3张图片的y坐标拼接在一起
        }



        //System.out.println(bg.bitmap.getWidth() + "\n" + bg.bitmap.getHeight());
        for(i = 0; i < enemy_size; ++i)    //初始化敌人数组
        {
            if(i % 2 == 0)     //小型敌人
            {
                enemys[i] = new Enemy(width, enemy0_bitmap);
            }
            else               //中型敌人
            {
                enemys[i] = new Enemy(width, enemy1_bitmap);
            }
        }
        //创建游戏失败后的弹窗
        alertDialog = new AlertDialog.Builder(MA)
                .setTitle("GMAE OVER")
                //.setMessage("有多个按钮")
                //.setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("重新开始", new DialogInterface.OnClickListener() {//添加重新开始按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent_restart = new Intent(MA, MainActivity.class); //新建Intent对象

                        //获取当前时间
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// HH:mm:ss
                        Date date = new Date(System.currentTimeMillis());
                        //保存到数据库
                        save_score(player.score, simpleDateFormat.format(date).toString());
                        //System.out.println(player.score);
                        //销毁当前Activity
                        MA.finish();
                        MA.startActivity(intent_restart);                    //重新运行自己

                    }
                })
                .setNegativeButton("回到主菜单", new DialogInterface.OnClickListener() {//添加按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent_Menu = new Intent(MA, Menu.class); //新建Intent对象

                        //获取当前时间
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// HH:mm:ss
                        Date date = new Date(System.currentTimeMillis());
                        //保存到数据库
                        save_score(player.score, simpleDateFormat.format(date).toString());
                        //System.out.println(player.score);
                        //销毁当前Activity
                        MA.finish();
                        MA.startActivity(intent_Menu);                    //回到主菜单

                    }
                }).setCancelable(false)
                .create();

        /*pause = new Button(MA);
        //pause.setBackgroundResource(R.drawable.pause1);
        pause.setText("pause");
        pauseparams.rightMargin = pause.getWidth();
        pauseparams.topMargin = 0;
        pause.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                start = !start;
            }
        });*/
        //监听屏幕点击事件，实现手指滑动屏幕控制飞机移动
        setOnTouchListener(new OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent event)                  //获取点击事件
            {
                if (event.getAction() == MotionEvent.ACTION_DOWN)                 //按下
                {
                    touch_x = event.getX();            //获取按下的坐标
                    touch_y = event.getY();
                    x = player.x;                      //获取玩家当前位置
                    y = player.y;
                }
                if (event.getAction() == MotionEvent.ACTION_MOVE)                  //手指滑动
                {
                    tempx = x + event.getX() - touch_x;                            //计算玩家新坐标，跟随手指滑动
                    tempy = y + event.getY() - touch_y;
                    player.x = tempx;                                              //玩家更新坐标
                    player.y = tempy;
                    if(tempx < -player.w * 0.5 + 2)                                //4个if判断屏幕四条边，避免玩家跑出屏幕外
                    {
                        player.x = (float)(-player.w * 0.5 + 2);
                    }
                    if(tempy < 0)
                    {
                        player.y = 0;
                    }
                    if(tempx > width - player.w * 0.5 + 2)
                    {
                        player.x = (float)(width - player.w * 0.5 + 2);
                    }
                    if(tempy > height - player.h)
                    {
                        player.y = height - player.h;
                    }
                }
                return true;
            }
        });

        new Thread(new refresh()).start();                        //刷新屏幕线程

    }
    //线程刷新
    private class refresh implements Runnable
    {
        @Override
        public void run()
        {
            while(start)                //start变量控制游戏是否要暂停
            {
                try {
                    Thread.sleep(10);    //10ms刷新屏幕
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                postInvalidate();        //刷新
            }
        }
    }
    //画出游戏场景物体
    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        t++;
        //背景循环滚动
        bg[0].update();
        bg[1].update();
        bg[2].update();
        for(int i = 0; i < 3; ++i)
        {
            if(bg[i].y > height + 80)       //某张背景移动到屏幕底部后循环回到上方
            {
                //(i + 1)%3实现3张图片的循环，可以使最后的图片拼接到最上方的图片上面
                bg[i].y = bg[(i + 1) % 3].y - bg[i].bitmap.getHeight();
            }
            bg[i].draw(canvas, p);
        }

        //画出玩家
        canvas.drawBitmap(player_bitmap, player.x, player.y, p);
        if (t % 18 == 0)           //子弹射击频率
        {
            player.shoot();
        }
        if (t % 40 == 0)           //敌人出现频率
        {
            for(int i = 0; i < 20; ++i)
            {
                if(!enemys[i].getisDraw())    //找到一个不在屏幕上画出来的敌人
                {
                    enemys[i].setDraw();      //设置它要画出来
                    enemys[i].randomEnemy(width);  //随机出现的位置
                    break;
                }
            }
        }
        if(t == 1000)      //10s后Boss出现
        {
            isboss = true;
        }



        int j = 0;
        for(int i = 0; i < player.getBullet_size(); ++i) //判断敌人与子弹碰撞
        {
            if(!player.bullets[i].getisDraw())            //跳过没有在屏幕上画出来的子弹
            {
                continue;
            }
            Bullet b = player.bullets[i];
            for(j = 0; j < enemy_size; ++j)          //遍历敌人数组
            {
                if(!enemys[j].getisDraw())           //跳过没有在屏幕上画出来的敌人
                {
                    continue;

                }
                Enemy e = enemys[j];

                if (b.x >= e.x - b.w && b.x <= e.x + e.w && b.y <= e.y + e.h && b.y >= e.y - b.h)   //碰撞判断
                {
                    //发生碰撞
                    ++player.score;   //记录分数
                    enemys[j].setnotDraw();   //敌人消失
                    player.bullets[i].setnotDraw();  //子弹消失

                    //爆炸，创建一个爆炸的View
                    View explosionView = new Explosion(MA, (float)(enemys[j].x + (float)(enemys[j].w) * 0.5), (float)(enemys[j].y + (float)(enemys[j].h) * 0.5));
                    MA.addContentView(explosionView, explosionparams);
                    //声音
                    soundPool.play(music, 1, 1, 0, 0, 1);
                }
            }
        }
        for(int i = 0; i < enemy_size; i++)  //判断玩家与敌人碰撞
        {
            if(!enemys[i].getisDraw())      //跳过没有画出来的敌人
            {
                continue;
            }
            Enemy e = enemys[i];
            if (player.x >= e.x - player.w && player.x <= e.x + e.w && player.y >= e.y - player.h && player.y <= e.y + e.h)
            {
                //发生碰撞
                //System.exit(0);
                start = false;          //游戏结束，暂停画面

                //爆炸
                View explosionView = new Explosion(MA, player.x, player.y);

                MA.addContentView(explosionView, explosionparams);
                //声音
                soundPool.play(music, 1, 1, 0, 0, 1);

                alertDialog.show();     //弹窗


            }
        }


        //遍历敌人数组所有对象，并更新位置
        for(int i = 0; i < enemy_size; i++)
        {
            if(enemys[i].getisDraw())   //画在屏幕上的敌人更新位置
            {
                enemys[i].update();
                if (enemys[i].y >= height)          //移动到屏幕外删除
                {
                    enemys[i].setnotDraw();
                }
                else
                {
                    enemys[i].draw(canvas, p);//画出敌人
                }
            }
        }
        //更新玩家子弹位置
        for(int i = 0; i < player.getBullet_size(); i++)
        {
            if(player.bullets[i].getisDraw())  //只更新画在屏幕上的子弹
            {
                player.bullets[i].update();    //更新子弹
                if (player.bullets[i].y <= 0)  //飞到屏幕外，子弹消失
                {
                    player.bullets[i].setnotDraw();
                }
                else
                {//画出子弹
                    canvas.drawBitmap(bullets_bitmap, player.bullets[i].x, player.bullets[i].y, p);
                }
            }
        }
        if(isboss)//Boss是否出现
        {
            for(int i = 0; i < player.getBullet_size(); ++i)  //判断子弹与boss碰撞
            {
                if(player.bullets[i].getisDraw())
                {
                    Bullet b = player.bullets[i];
                    if(b.x > boss.x - b.w && b.x < boss.x + boss.bitmap.getWidth() && b.y > boss.y - b.h && b.y < boss.y + boss.bitmap.getHeight())
                    {//发生碰撞
                        boss.hp--;//Boss扣血
                        player.bullets[i].setnotDraw(); //玩家子弹消失
                        //爆炸
                        /*View explosionView = new Explosion(MA, player.bullets[i].x, player.bullets[i].y);

                        MA.addContentView(explosionView, explosionparams);
                        //声音
                        soundPool.play(music, 1, 1, 0, 0, 1);*/

                        boss.bitmap = big_hurt_bitmap; //Boss击中后变红（一帧）
                        if(boss.hp == 0)                //击败Boss
                        {
                            player.score += 10;         //得分加10
                            start = false;              //暂停游戏
                            //通关弹窗
                            successDialog = new AlertDialog.Builder(MA)
                                    .setTitle("恭喜你击败了BOSS")
                                    .setMessage("你的得分是：" + player.score)
                                    //.setIcon(R.mipmap.ic_launcher)
                                    .setPositiveButton("重新开始", new DialogInterface.OnClickListener() {//添加重新开始按钮
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent intent_restart = new Intent(MA, MainActivity.class); //新建Intent对象

                                            //获取当前时间
                                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// HH:mm:ss
                                            Date date = new Date(System.currentTimeMillis());
                                            //保存
                                            save_score(player.score, simpleDateFormat.format(date).toString());
                                            //System.out.println(player.score);
                                            MA.finish();
                                            MA.startActivity(intent_restart);                    //重新运行自己

                                        }
                                    })
                                    .setNegativeButton("回到主菜单", new DialogInterface.OnClickListener() {//添加按钮
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent intent_Menu = new Intent(MA, Menu.class); //新建Intent对象

                                            //获取当前时间
                                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// HH:mm:ss
                                            Date date = new Date(System.currentTimeMillis());
                                            //保存
                                            save_score(player.score, simpleDateFormat.format(date).toString());
                                            //System.out.println(player.score);
                                            MA.finish();
                                            MA.startActivity(intent_Menu);                    //回到主菜单

                                        }
                                    }).setCancelable(false)
                                    .create();
                            successDialog.show();
                        }
                    }
                }
            }
            //判断boss子弹与玩家碰撞
            if (player.x >= bossBullet.x - player.w
                    && player.x <= bossBullet.x + boss_bullet.getWidth()
                    && player.y >= bossBullet.y - player.h
                    && player.y <= bossBullet.y + boss_bullet.getHeight())
            {
                //System.exit(0);
                start = false;          //暂停

                //爆炸
                View explosionView = new Explosion(MA, player.x, player.y);

                MA.addContentView(explosionView, explosionparams);
                //声音
                soundPool.play(music, 1, 1, 0, 0, 1);

                alertDialog.show();     //弹窗
            }
            //更新
            boss.update();
            bossBullet.update();

            if(bossBullet.y > height)
            {
                bossBullet.x = (int)(boss.x + boss_bitmap.getWidth() * 0.5 - boss_bullet.getWidth() * 0.5);
                bossBullet.y = boss.y + boss_bitmap.getHeight();
            }

            //画
            bossBullet.draw(canvas, p);

            boss.draw(canvas, p);
            boss.bitmap = boss_bitmap;
        }


        p.setTextSize(50);//分数记录
        //Paint.FontMetrics fm = p.getFontMetrics();
        canvas.drawText("Score:" + Integer.toString(player.score), 10, 50, p);

    }
    //保存数据到数据库
    public void save_score(int score, String date)
    {
        /*SharedPreferences.Editor editor = MA.getSharedPreferences("spdata",MODE_PRIVATE).edit();
        //向其中添加数据，是什么数据类型就put什么，前面是键，后面是数据
        editor.putString("name","Tom");
        editor.putInt("age",20);
        editor.putBoolean("boy",true);
        //调用apply方法将添加的数据提交，从而完成存储的动作
        editor.apply();*/

        DBHelper dbHelper = new DBHelper(MA);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values= new ContentValues();
        values.put("score", score);
        values.put("date", date);
        long rowid = db.insert("Rank",null,values);
        db.close();
    }
}

