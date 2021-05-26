#ifndef NDKPLAYER_LOG4J_H
#define NDKPLAYER_LOG4J_H

#include <android/log.h>

#define TAG "Lucky_log"
// ... 我都不知道传入什么  借助JNI里面的宏来自动帮我填充
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG,__VA_ARGS__)
#define LOGE(...) __android_log_print((ANDROID_LOG_ERROR,TAG,__VA_ARGS__)
#define LOGI(...) __android_log_print((ANDROID_LOG_INFO,TAG,__VA_ARGS__)

#endif //NDKPLAYER_LOG4J_H
