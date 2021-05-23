#ifndef NDKPLAYER_BASECHANNEL_H
#define NDKPLAYER_BASECHANNEL_H

extern "C" {
#include <libavcodec/avcodec.h>
};

#include "../utils/safe_queue.h"

class BaseChannel{
public:
    int stream_index;
    SafeQueue<AVPacket*> packets; //压缩包
    SafeQueue<AVFrame*> frames;//解压包
    bool isPlaying;
    AVCodecContext *avCodecContext=0;//解码器

    BaseChannel(int stream_index,AVCodecContext* avCodecContext)
    :stream_index(stream_index),avCodecContext(avCodecContext)
    {
        packets.setReleaseCallback(releaseAvPacket);
        frames.setReleaseCallback(releaseAvFrame);
    }
    //父类一定要用 virtual
    virtual ~BaseChannel(){
        packets.clear();
        frames.clear();
    }

    /**
     * 释放 队列中 所有的 AVPacket *
     * @param packet
     */
    static void releaseAvPacket(AVPacket ** pAvPacket){
        if (pAvPacket) {
            av_packet_free(pAvPacket); // 释放队列里面的 T == AVPacket
            *pAvPacket = 0;
        }
    }

    /**
     * 释放 队列中 所有的 AVFrame *
     * @param packet
     */
    // typedef void (*ReleaseCallback)(T *);
    static void releaseAvFrame(AVFrame ** avFrame){
        if(avFrame){
            av_frame_free(avFrame);
            *avFrame = 0;
        }
    }
};
#endif //NDKPLAYER_BASECHANNEL_H
