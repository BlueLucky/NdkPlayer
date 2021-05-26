#include <jni.h>
#include <string>
#include "player/CPlayer.h"
#include "callback/JNICallbackHelper.h"
#include <android/native_window_jni.h>

#define TAG "Lucky_log"
// ... 我都不知道传入什么  借助JNI里面的宏来自动帮我填充
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG,__VA_ARGS__)

JavaVM * jvm = 0;
CPlayer * player = 0;
ANativeWindow *aNativeWindow=0;

pthread_mutex_t mutex = PTHREAD_COND_INITIALIZER;

jint JNI_OnLoad(JavaVM * vm, void * args) {
    ::jvm = vm;
    return JNI_VERSION_1_6;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_qizhidao_vendor_ndkplayer_QPlayer_releaseNative(JNIEnv *env, jobject thiz) {

}

extern "C"
JNIEXPORT void JNICALL
Java_com_qizhidao_vendor_ndkplayer_QPlayer_stopNative(JNIEnv *env, jobject thiz) {

}

void renderFrame(uint8_t * dataSource,int width,int height,int lineSize){
    pthread_mutex_lock(&mutex);
    if(!aNativeWindow){
        pthread_mutex_unlock(&mutex);
    }
    //设置窗口大小
    ANativeWindow_setBuffersGeometry(aNativeWindow
            ,width,height,WINDOW_FORMAT_RGBA_8888);
    //ANative buffer
    ANativeWindow_Buffer windowBuffer;
    //如果在被渲染时，被锁住，无法渲染，需要释放
    if(ANativeWindow_lock(aNativeWindow,&windowBuffer,0)){
        ANativeWindow_release(aNativeWindow);
        aNativeWindow=0;
        pthread_mutex_unlock(&mutex); //解锁
        return;
    }
    //开始渲染
    auto * dst_data = static_cast<uint8_t *>(windowBuffer.bits);
    int dst_lineSize = windowBuffer.stride*4;

    for (int i = 0; i < windowBuffer.height; ++i) {//图：一行一行显示
        //ANativeWindow_buffer 64字节对齐算法。
        //memcpy(dst_data+i*1792,dataSource+i*1704,1792);
        memcpy(dst_data+i*dst_lineSize,dataSource+i*lineSize,dst_lineSize);
    }
    //数据刷新
    ANativeWindow_unlockAndPost(aNativeWindow);
    pthread_mutex_unlock(&mutex);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_qizhidao_vendor_ndkplayer_QPlayer_prepareNative(JNIEnv *env, jobject job,jstring dataSource) {
    const char* _datesoure = (char *) env->GetStringUTFChars(dataSource, 0);
    LOGD("_danseuse:%s",_datesoure);
    auto * helper = new JNICallbackHelper(jvm,env,job);
    player = new CPlayer(_datesoure, helper);
    player->setRendCallBack(renderFrame);
    player->prepare();
    env->ReleaseStringUTFChars(dataSource,_datesoure);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_qizhidao_vendor_ndkplayer_QPlayer_startNative(JNIEnv *env, jobject thiz) {
    if(player){
        player->start();
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_qizhidao_vendor_ndkplayer_QPlayer_setSurfaceNative(JNIEnv *env, jobject thiz,
                                                            jobject surface) {
    pthread_mutex_lock(&mutex);
    if(aNativeWindow){
         ANativeWindow_release(aNativeWindow);
         aNativeWindow = 0;
    }
    aNativeWindow = ANativeWindow_fromSurface(env,surface);
    pthread_mutex_unlock(&mutex);
}