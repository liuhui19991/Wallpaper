package com.liuhui.wallpaper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.Build;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.WindowManager;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * Created by Tyhj on 2017/5/23.
 */

public class GifWallpaperService extends WallpaperService {

    private final Handler mHandler = new Handler();
    private Movie movie;
    private float scaleWidth, scaleHeight;
    private String TAG = "GifWallpaperService";


    @Override
    public void onCreate() {
        initGif();
        super.onCreate();
    }

    public static int SCREEN_WIDTH = 0;
    public static int SCREEN_HEIGHT = 0;

    private void initGif() {
        InputStream stream = null;
        try {

            /*if (Application.getGifPath() == null)
                stream = getAssets().open("flower.gif");
            else{
                File file=new File(Application.getGifPath());
                if(file.exists())
                    stream=new FileInputStream(file);
                else
                    stream = getAssets().open("flower.gif");
            }*/
            stream = getAssets().open("flower.gif");
        } catch (IOException e) {
            e.printStackTrace();
        }
        movie = Movie.decodeStream(stream);
        //获取gif的宽高
        int width = movie.width();
        int height = movie.height();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        SCREEN_WIDTH = metrics.widthPixels;//获取到的是px，像素，绝对像素，需要转化为dpi
        SCREEN_HEIGHT = getRealHeight(this);
        // 设置想要的大小
        int newWidth = SCREEN_WIDTH;
        int newHeight = SCREEN_HEIGHT;

        Log.e("宽度：", newWidth + "");
        Log.e("长度：", newHeight + "");

        // 计算缩放比例
        scaleWidth = ((float) newWidth) / width;
        scaleHeight = ((float) newHeight) / height;

        scaleWidth = (scaleWidth > scaleHeight) ? scaleWidth : scaleHeight;
    }

    public static int getRealHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int screenHeight = 0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics dm = new DisplayMetrics();
            display.getRealMetrics(dm);
            screenHeight = dm.heightPixels;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            try {
                screenHeight = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
            } catch (Exception e) {
                DisplayMetrics dm = new DisplayMetrics();
                display.getMetrics(dm);
                screenHeight = dm.heightPixels;
            }
        }
        return screenHeight;
    }

    @Override
    public Engine onCreateEngine() {
        return new Mngine();
    }


    //Engine是WallpaperService中的一个内部类，实现了壁纸窗口的创建以及Surface的维护工作
    class Mngine extends Engine {

        //线程
        private Runnable runnable = new Runnable() {
            @Override
            public void run() {
                drawFrame();
            }
        };

        private void drawFrame() {
            if (movie == null) {
                initGif();
            }
            Canvas canvas = null;
            canvas = getSurfaceHolder().lockCanvas();
            canvas.scale(scaleWidth, scaleWidth);
            canvas.save();
            //绘制此gif的某一帧，并刷新本身
            movie.draw(canvas, 0, 0);
            movie.setTime((int) (System.currentTimeMillis() % movie.duration()));
            canvas.restore();
            //结束锁定画图，并提交改变,画画完成(解锁)
            getSurfaceHolder().unlockCanvasAndPost(canvas);
            mHandler.postDelayed(runnable, 50);   //50ms表示每50ms绘制一帧
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            setTouchEventsEnabled(true);
            Log.e(TAG, "onCreate: ");
        }

        public Mngine() {

        }


        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            if (movie == null) {
                initGif();
            }
            drawFrame();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            mHandler.removeCallbacks(runnable);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            initGif();
            /*下面这个判断好玩，就是说，如果屏幕壁纸状态转为显式时重新绘制壁纸，否则黑屏幕，隐藏就可以*/
            if (visible) {
                drawFrame();
            } else {
                mHandler.removeCallbacks(runnable);
            }
        }

        @Override
        public void onTouchEvent(MotionEvent event) {

        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            drawFrame();
            super.onSurfaceChanged(holder, format, width, height);
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            mHandler.removeCallbacks(runnable);
        }


    }


}
