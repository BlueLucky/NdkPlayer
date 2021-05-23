package com.qizhidao.vendor.ndkplayer;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private QPlayer qPlayer;
    private SurfaceView mSurfaceView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tv = findViewById(R.id.sample_text);
        tv.setText("");

        mSurfaceView = findViewById(R.id.surfaceview);



        UtilPermission.requestPermission(this
                , new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, 10000, new UtilPermission.PermissionRequestListener() {
                    @Override
                    public void finallyRequestedPermission(int requestCode) {
                        initPlayer();
                    }

                    @Override
                    public void finallyDeniedPermission(int requestCode, String permission) {

                    }

                    @Override
                    public boolean forEverDeniedPermissionPermission(int requestCode, String permission) {
                        return false;
                    }
                });
    }

    private void initPlayer(){
        ///data/data/com.qizhidao.vendor.ndkplayer/files/[4K高清MV] A Pink-Hush.mp4
        String path = getFilesDir() + File.separator + "[4K高清MV] A Pink-Hush.mp4";
        Log.d("Lucky_log","path:"+path);
        qPlayer = new QPlayer();
        qPlayer.setDataSource(path);
        qPlayer.setSurfaceView(mSurfaceView);

        qPlayer.setErrorListener(new QPlayer.OnErrorListener() {
            @Override
            public void onError(final int errorCode, final String errorMessage) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,"errorCode:"+errorCode+" 错误信息:"+errorMessage,Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        qPlayer.setOnPreparedListener(new QPlayer.OnPreparedListener() {
            @Override
            public void onPrepared() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,"准备成功",Toast.LENGTH_LONG).show();
                    }
                });
                qPlayer.start();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        initPlayer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        qPlayer.prepare();
    }

    @Override
    protected void onStop() {
        super.onStop();
        qPlayer.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        qPlayer.release();
    }
}
