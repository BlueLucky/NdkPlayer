#include "AudioChannel.h"


AudioChannel::~AudioChannel() {

}

AudioChannel::AudioChannel(int stream_index, AVCodecContext *avCodecContext)
: BaseChannel(stream_index, avCodecContext)
{

}

void AudioChannel::stop() {

}

void AudioChannel::start() {

}
