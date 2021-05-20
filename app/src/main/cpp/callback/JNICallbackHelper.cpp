#include "JNICallbackHelper.h"

JNICallbackHelper::JNICallbackHelper(JavaVM *pVm, JNIEnv *pEnv, jobject job) {
    this->javaVm = pVm;
    this->env = pEnv;
    //job不能跨越线程，不能跨越函数，必须全局引用
    this->job = pEnv->NewGlobalRef(job);

    jclass clazz = env->GetObjectClass(job);
    jmd_prepared = env->GetMethodID(clazz, "onPrepared", "()V");

    jmd_error = env->GetMethodID(clazz,"onError","(ILjava/lang/String;)V");
}

JNICallbackHelper::~JNICallbackHelper() {
    javaVm = nullptr;
    env->DeleteGlobalRef(job);
    job = nullptr;
    env = nullptr;
}

void JNICallbackHelper::onPrepared(int threadMode) {
    //子线程
    if (threadMode == THREAD_CHILD) {
        JNIEnv *env_child;
        javaVm->AttachCurrentThread(&env_child, 0);
        env_child->CallVoidMethod(job,jmd_prepared);
        javaVm->DetachCurrentThread();
    } else {
        env->CallVoidMethod(job,jmd_prepared);
    }
}

void JNICallbackHelper::onError(int threadMode,int errorCode, char * errorMsg) {
//子线程
    if (threadMode == THREAD_CHILD) {
        JNIEnv *env_child;
        javaVm->AttachCurrentThread(&env_child, 0);
        env_child->CallVoidMethod(job,jmd_error,errorCode,env_child->NewStringUTF(errorMsg));
        javaVm->DetachCurrentThread();
    } else {
        env->CallVoidMethod(job,jmd_error,errorCode,env->NewStringUTF(errorMsg));
    }
}
