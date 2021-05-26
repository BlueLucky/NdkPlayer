#ifndef NDKPLAYER_AUDIOCHANNEL_H
#define NDKPLAYER_AUDIOCHANNEL_H

#include "BaseChannel.h"
#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>

#include "../utils/log4j.h"

extern "C"{
#include <libswresample/swresample.h> //音频重采样
}


class AudioChannel: public BaseChannel {
private:
    //引擎
    SLObjectItf engineObject = 0;
    //引擎接口
    SLEngineItf engineInterface = 0;
    //混音器
    SLObjectItf outputMixObject = 0;
    //播放器
    SLObjectItf bqPlayerObject = 0;
    //播放器接口
    SLPlayItf bqPlayerPlay = 0;
    //播放器队列接口
    SLAndroidSimpleBufferQueueItf bqPlayerBufferQueue = 0;
    pthread_t pid_audio_decode;
    pthread_t pid_audio_play;

    int out_channels;
    int out_buffers_size;
    int out_simple_size;
    int out_simple_rate;
    SwrContext *swr_context;

public:
    AudioChannel(int stream_index,AVCodecContext* avCodecContext);
    ~AudioChannel();
    void stop();
    void start();
    void audio_decode();
    void audio_play();

    uint8_t *out_buffers=0;

    int getPCM();
};

#endif //NDKPLAYER_AUDIOCHANNEL_H
