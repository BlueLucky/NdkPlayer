#ifndef NDKPLAYER_VIDEOCHANNEL_H
#define NDKPLAYER_VIDEOCHANNEL_H
#include "BaseChannel.h"

extern "C"{
#include <libavcodec/avcodec.h>
#include <libavutil/imgutils.h>
#include <libswscale/swscale.h>
};
typedef void(*RenderCallBack)(uint8_t *,int,int,int) ;

class VideoChannel: public BaseChannel {
private:
    pthread_t  pid_video_decode;
    pthread_t  pid_video_play;
    RenderCallBack  renderCallBack;

public:
    VideoChannel(int stream_index,AVCodecContext *avCodecContext);
    ~VideoChannel();
    void stop();
    void start();
    void video_decode();
    void video_play();
    void  setRendCallBack(RenderCallBack renderCallBack);
};

#endif //NDKPLAYER_VIDEOCHANNEL_H
