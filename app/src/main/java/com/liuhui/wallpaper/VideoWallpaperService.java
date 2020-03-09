package com.liuhui.wallpaper;

import android.media.MediaPlayer;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;


public class VideoWallpaperService extends WallpaperService {

    private MediaPlayer mp;
    private int progress = 0;
    private String TAG = "VideoWallpaper";

    @Override
    public Engine onCreateEngine() {
        return new VideoEngine();
    }

    class VideoEngine extends Engine {
        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            setTouchEventsEnabled(true);
            Log.e(TAG, "onCreate: ");
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            if (mp != null && mp.isPlaying())
                return;
            mp = MediaPlayer.create(getApplicationContext(), R.raw.girl);
            mp.setSurface(holder.getSurface());
            //mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
            mp.setLooping(true);
            mp.start();
        }


        @Override
        public void onVisibilityChanged(boolean visible) {
            if (visible) {
                if (mp != null)
                    return;
//                if(Application.getImageId()!=-1)
                mp = MediaPlayer.create(getApplicationContext(), R.raw.girl);
//                else
//                    mp= MediaPlayer.create(getApplicationContext(), Uri.parse(Application.getImageDir()));
                mp.setSurface(getSurfaceHolder().getSurface());
                mp.setLooping(true);
                mp.seekTo(progress);
                mp.start();
            } else {
                if (mp != null && mp.isPlaying()) {
                    progress = mp.getCurrentPosition();
                    mp.stop();
                    mp.release();
                    mp = null;
                }
            }

        }

        @Override
        public void onDestroy() {
            if (mp != null) {
                mp.stop();
                mp.release();
            }
            super.onDestroy();
        }
    }


}
