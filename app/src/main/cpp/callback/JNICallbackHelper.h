#ifndef NDKPLAYER_JNICALLBACKHELPER_H
#define NDKPLAYER_JNICALLBACKHELPER_H
#include <jni.h>
#include "../utils/util.h"

class JNICallbackHelper {
private:
    JavaVM *javaVm=0;
    JNIEnv *env = 0;
    jobject job;
    jmethodID jmd_prepared;
    jmethodID jmd_error;

public:
    JNICallbackHelper(JavaVM *pVm, JNIEnv *pEnv, jobject pJobject);
    ~JNICallbackHelper();
     void onPrepared(int);
     void onError(int,int, char*);
};

#endif //NDKPLAYER_JNICALLBACKHELPER_H
