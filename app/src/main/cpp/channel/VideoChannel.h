#ifndef NDKPLAYER_VIDEOCHANNEL_H
#define NDKPLAYER_VIDEOCHANNEL_H

extern "C"{
#include <libavcodec/avcodec.h>
#include <libavutil/imgutils.h>
};

class VideoChannel {
private:
    AVCodecContext * codeContext;
    AVFormatContext * avFormatContext;
    int stream_index;

public:
    VideoChannel(AVFormatContext * formatContext,AVCodecContext * codeContext,int stream_index);
    ~VideoChannel();
    void player_();
};

#endif //NDKPLAYER_VIDEOCHANNEL_H
