#ifndef NDKPLAYER_AUDIOCHANNEL_H
#define NDKPLAYER_AUDIOCHANNEL_H

#include "BaseChannel.h"

class AudioChannel: public BaseChannel {

public:
    AudioChannel(int stream_index,AVCodecContext* avCodecContext);
    ~AudioChannel();
    void stop();
    void start();
};
#endif //NDKPLAYER_AUDIOCHANNEL_H
