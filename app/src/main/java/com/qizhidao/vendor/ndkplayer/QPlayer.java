package com.qizhidao.vendor.ndkplayer;


import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

public class QPlayer implements SurfaceHolder.Callback {
    static {
        System.loadLibrary("native-lib");
    }

    private OnPreparedListener preparedListener;
    private OnErrorListener errorListener;

    private String dataSource;

    private SurfaceHolder mSurfaceHolder;

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public void prepare() {
        prepareNative(dataSource);
    }

    public void start() {
        startNative();
    }

    public void stop() {
        stopNative();
    }

    public void release() {
        releaseNative();
    }


    public void setSurfaceView(SurfaceView surfaceView){
        if(mSurfaceHolder!=null){
            mSurfaceHolder.removeCallback(this);
        }
        mSurfaceHolder = surfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
    }

    public void setOnPreparedListener(OnPreparedListener preparedListener) {
        this.preparedListener = preparedListener;
    }

    public void setErrorListener(OnErrorListener errorListener) {
        this.errorListener = errorListener;
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
            setSurfaceNative(holder.getSurface());
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

    }

    interface OnPreparedListener {
        void onPrepared();
    }

    interface OnErrorListener{
        void onError(int errorCode,String errorMessage);
    }

    private native void prepareNative(String dataSource);
    private native void startNative();
    private native void stopNative();
    private native void releaseNative();
    private native void setSurfaceNative(Surface surface);

    /**
     * 仅给jni调用
     */
    public void onPrepared() {
        if (preparedListener != null) {
            preparedListener.onPrepared();
        }
    }
    public void onError(int errorCode,String errorMessage){
        if(errorListener!=null){
            errorListener.onError(errorCode,errorMessage);
        }
    }
}
