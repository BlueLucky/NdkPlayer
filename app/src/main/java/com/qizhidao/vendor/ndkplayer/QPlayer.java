package com.qizhidao.vendor.ndkplayer;


public class QPlayer {
    static {
        System.loadLibrary("native-lib");
    }

    private OnPreparedListener preparedListener;
    private OnErrorListener errorListener;

    private String dataSource;

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



    public void setOnPreparedListener(OnPreparedListener preparedListener) {
        this.preparedListener = preparedListener;
    }

    public void setErrorListener(OnErrorListener errorListener) {
        this.errorListener = errorListener;
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
