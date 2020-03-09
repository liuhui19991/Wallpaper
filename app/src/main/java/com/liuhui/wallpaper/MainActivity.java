package com.liuhui.wallpaper;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.PermissionUtils;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_gif).setOnClickListener(this);
        findViewById(R.id.btn_video).setOnClickListener(this);
        findViewById(R.id.btn_cream).setOnClickListener(this);
    }

    private void changePaper(String name) {
        Intent chooseIntent;
        if (Build.VERSION.SDK_INT >= 16) {
            chooseIntent = new Intent();
            chooseIntent.setAction(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
            chooseIntent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(getPackageName(), name));
            startActivity(chooseIntent);
        } else {
            chooseIntent = new Intent(Intent.ACTION_SET_WALLPAPER);
            startActivity(Intent.createChooser(chooseIntent, "选择壁纸"));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_gif:
                changePaper(GifWallpaperService.class.getCanonicalName());
                break;
            case R.id.btn_video:
                changePaper(VideoWallpaperService.class.getCanonicalName());
                break;
            case R.id.btn_cream:
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 0);
                PermissionUtils.permission(PermissionConstants.CAMERA)
                        .rationale(new PermissionUtils.OnRationaleListener() {
                            @Override
                            public void rationale(ShouldRequest shouldRequest) {
                            }
                        })
                        .callback(new PermissionUtils.FullCallback() {
                            @Override
                            public void onGranted(List<String> permissionsGranted) {
                                changePaper(CameraLiveWallpaperService.class.getCanonicalName());
                            }

                            @Override
                            public void onDenied(List<String> permissionsDeniedForever, List<String> permissionsDenied) {
                            }
                        })
                        .request();
                break;
        }
    }
}
