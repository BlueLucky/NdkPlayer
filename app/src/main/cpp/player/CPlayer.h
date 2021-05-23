#ifndef NDKPLAYER_CPLAYER_H
#define NDKPLAYER_CPLAYER_H

#include <cstring>
#include <pthread.h>
#include "../channel/AudioChannel.h"
#include "../channel/VideoChannel.h"
#include "../callback/JNICallbackHelper.h"

#include <android/log.h>

#define TAG "Lucky_log"
// ... 我都不知道传入什么  借助JNI里面的宏来自动帮我填充
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG,__VA_ARGS__)

extern "C"{
#include <libavformat/avformat.h>
};

typedef void(*RenderCallBack)(uint8_t *,int,int,int) ;


class CPlayer {
private:
    char *dataSource=0;
    AVFormatContext *formatContext=0;
    pthread_t pid_prepare = 0;
    pthread_t pid_start = 0;
    AudioChannel *audio_channel=0;
    VideoChannel *video_channel=0;
    JNICallbackHelper *helper =0;
    void onError(int,int,char *);
    RenderCallBack  renderCallBack;

public:
    CPlayer(const char *dataSource, JNICallbackHelper *pHelper);
    ~CPlayer();
    void prepare();
    void prepare_();

    void start();

    void start_();
    bool isPlaying;
    void  setRendCallBack(RenderCallBack renderCallBack);
};

#endif //NDKPLAYER_CPLAYER_H
