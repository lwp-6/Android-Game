package com.example.lwp.game;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


public class Explosion extends View{
    Activity MA;                       //View所在的Activity
    public Paint p = new Paint();
    Bitmap explosion_bitmap;         //爆炸图片
    float x, y;                       //爆炸位置
    public int e_width, e_height, e_fraps;             //爆炸图片的宽度、高度、帧数
    int t = 0;                        //爆炸到第几帧
    Bitmap e_bitmap[] = new Bitmap[14];    //爆炸由14帧组成
    public Explosion(Activity ma, float x, float y){
        super(ma);
        MA = ma;
        explosion_bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.explosion);
        new Thread(new Explosion.refresh()).start();   //启动线程
        this.x = x;
        this.y = y;
        e_fraps = 14;
        e_width = explosion_bitmap.getWidth() / e_fraps;
        e_height = explosion_bitmap.getHeight();


        for(int i = 0; i < e_fraps; ++i)  //explosion_bitmap是完整的14帧，需要进行切割成14张图片
        {
            e_bitmap[i] = Bitmap.createBitmap(explosion_bitmap, i * e_width, 0, e_width, e_height);
        }

    }
    private class refresh implements Runnable
    {
        @Override
        public void run()
        {
            while(true)
            {
                try {
                    Thread.sleep(60);    //60ms刷新屏幕
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                postInvalidate();        //刷新
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        //画出爆炸的一帧
        canvas.drawBitmap(e_bitmap[t % e_fraps], (float)(x - e_bitmap[0].getWidth() * 0.5), (float)(y - e_bitmap[0].getHeight() * 0.5), p);
        ++t;  //记录帧数
        if(t == 14)   //14帧时销毁View
        {
            //((ViewGroup)this.getParent()).removeView(this);
            final ViewGroup parent_view = ((ViewGroup)this.getParent());  //找到父View，即游戏场景
            final View child_view = this;
            parent_view.post(new Runnable(){
                public void run(){
                    try{
                        parent_view.removeView(child_view);  //从父View销毁爆炸View
                    }catch(Exception ignored){
                    }
                }
            });
        }
    }
}