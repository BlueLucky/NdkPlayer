#include <jni.h>
#include <string>
#include "player/CPlayer.h"
#include "callback/JNICallbackHelper.h"

#define TAG "Lucky_log"
// ... 我都不知道传入什么  借助JNI里面的宏来自动帮我填充
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG,__VA_ARGS__)

JavaVM * jvm = 0;
CPlayer * player = 0;

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

extern "C"
JNIEXPORT void JNICALL
Java_com_qizhidao_vendor_ndkplayer_QPlayer_prepareNative(JNIEnv *env, jobject job,jstring dataSource) {
    const char* _datesoure = (char *) env->GetStringUTFChars(dataSource, 0);
    LOGD("_danseuse:%s",_datesoure);
    auto * helper = new JNICallbackHelper(jvm,env,job);
    player = new CPlayer(_datesoure, helper);
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