#include "AudioChannel.h"


AudioChannel::~AudioChannel() {

}

AudioChannel::AudioChannel(int stream_index, AVCodecContext *avCodecContext)
        : BaseChannel(stream_index, avCodecContext) {
        // 初始化 out_buffer out_buffer_size
        //音频三要素
        // 1. 采样率（44100Hz等）
        // 2.位声/采样格式大小 2字节（16位）
        // 3. 声道数 2

        //音频压缩包 AAC (32bit) 为支持压缩算法效率
        //手机音频 （16bit）
        //所以需要重采样
        out_channels = av_get_channel_layout_nb_channels(AV_CH_LAYOUT_STEREO);//STEREO 双声道
        out_simple_size = av_get_bytes_per_sample(AV_SAMPLE_FMT_S16);//
        out_simple_rate = 44100;

        out_buffers_size = out_simple_rate*out_channels*out_simple_size;
        out_buffers = static_cast<uint8_t *>(malloc(out_buffers_size));

        //FFmpeg 音频重采样
        swr_context = swr_alloc_set_opts(0,
                                         //输出
                                         AV_CH_LAYOUT_STEREO,
                                         AV_SAMPLE_FMT_S16,
                                         out_simple_rate,

                                         //输入
                                         avCodecContext->channel_layout,
                                         avCodecContext->sample_fmt,
                                         avCodecContext->sample_rate,
                                         0,0
                );
        swr_init(swr_context);
}

void AudioChannel::stop() {

}

void *task_decode(void *args) {
    auto *channel = static_cast<AudioChannel *>(args);
    channel->audio_decode();
    return 0;
}

void *task_player(void *args) {
    auto *channel = static_cast<AudioChannel *>(args);
    channel->audio_play();
    return 0;
}


void AudioChannel::start() {
    isPlaying = 1;
    //解码packet
    packets.setWork(1);
    frames.setWork(1);
    //队列继续编码
    pthread_create(&pid_audio_decode, 0, task_decode, this);
    //取队列中的原始数据播放
    pthread_create(&pid_audio_play, 0, task_player, this);
}

//压缩包解码解码成源音频包（PCM数据）
void AudioChannel::audio_decode() {
    AVPacket *pkt = 0;
    while (isPlaying) {
        int ret = packets.getQueueAndDel(pkt);
        if (!isPlaying) {
            break;
        }
        if (!ret) {
            continue;
        }
        ret = avcodec_send_packet(avCodecContext, pkt);
        releaseAvPacket(&pkt);
        if (ret) {
            break;
        }

        AVFrame *frame = av_frame_alloc();
        ret = avcodec_receive_frame(avCodecContext, frame);
        if (ret == AVERROR(EAGAIN)) {
            continue;
        } else if (ret != 0) {
            break;
        }
        //PCM数据
        frames.insertToQueue(frame);
    }
    releaseAvPacket(&pkt);
}

//音频播放回调函数
void bqPlayerCallback(SLAndroidSimpleBufferQueueItf bq, void *args){
    auto * audioChannel = static_cast<AudioChannel *>(args);
    int pcm_data_size = audioChannel->getPCM();
    //添加数据到缓冲区
    (*bq)->Enqueue(bq,
            audioChannel->out_buffers,//PCM 数据
                   pcm_data_size);//PCM 数据对应的大小
}

int AudioChannel::getPCM() {
    //重采样

    return 0;
}

void AudioChannel::audio_play() {
    //1. 创建引擎
    SLresult result;
    // 1.1 创建引擎对象：SLObjectItf engineObject
    result = slCreateEngine(&engineObject, 0, NULL, 0, NULL, NULL);
    if (SL_RESULT_SUCCESS != result) {
        LOGD("创建引擎 slCreateEngine error");
        return;
    }
    // 1.2 初始化引擎
    result = (*engineObject)->Realize(engineObject, SL_BOOLEAN_FALSE);
    if (SL_RESULT_SUCCESS != result) {
        LOGD("创建引擎 Realize error");
        return;
    }
    // 1.3 获取引擎接口 SLEngineItf engineInterface
    result = (*engineObject)->GetInterface(engineObject, SL_IID_ENGINE, &engineInterface);
    if (SL_RESULT_SUCCESS != result) {
        LOGD("创建引擎 GetInterface error");
        return;
    }
    //健壮性判断
    if (engineInterface) {
        LOGD("创建引擎接口 成功");
    } else {
        LOGD("创建引擎接口 失败");
        return;
    }
    // 2. 设置混音器
    result = (*engineInterface)->CreateOutputMix(engineInterface, &outputMixObject, 0, 0, 0);
    if (SL_RESULT_SUCCESS != result) {
        LOGD("创建混音器 CreateOutputMix error");
        return;
    }
    //
    result = (*outputMixObject)->Realize(outputMixObject, SL_BOOLEAN_FALSE);
    if (SL_RESULT_SUCCESS != result) {
        LOGD("初始化混音器 CreateOutputMix error");
        return;
    }

    //3.1 配置输入声音信息
    //创建buffer缓冲类型的队列 2个队列
    SLDataLocator_AndroidSimpleBufferQueue loc_bufq = {SL_DATALOCATOR_ANDROIDSIMPLEBUFFERQUEUE,
                                                       2};
    //pcm数据格式
    //SL_DATAFORMAT_PCM：数据格式为pcm格式
    //2：双声道
    //SL_SAMPLINGRATE_44_1：采样率为44100
    //SL_PCMSAMPLEFORMAT_FIXED_16：采样格式为16bit
    //SL_PCMSAMPLEFORMAT_FIXED_16：数据大小为16bit
    //SL_SPEAKER_FRONT_LEFT | SL_SPEAKER_FRONT_RIGHT：左右声道（双声道）
    //SL_BYTEORDER_LITTLEENDIAN：小端模式
    SLDataFormat_PCM format_pcm = {SL_DATAFORMAT_PCM,
                                   2,//声道数
                                   SL_SAMPLINGRATE_44_1,//采样率
                                   SL_PCMSAMPLEFORMAT_FIXED_16,
                                   SL_PCMSAMPLEFORMAT_FIXED_16,
                                   SL_SPEAKER_FRONT_LEFT | SL_SPEAKER_FRONT_RIGHT,
                                   SL_BYTEORDER_LITTLEENDIAN};// 字节序
    //数据源 将上述配置信息放到这个数据源中
    SLDataSource audioSrc = {&loc_bufq, &format_pcm};
    //3.2 配置音轨（输出）
    //设置混音器
    SLDataLocator_OutputMix loc_outmix = {SL_DATALOCATOR_OUTPUTMIX, outputMixObject};
    SLDataSink audioSnk = {&loc_outmix, NULL};
    //需要的接口 操作队列的接口
    const SLInterfaceID ids[1] = {SL_IID_BUFFERQUEUE};
    const SLboolean req[1] = {SL_BOOLEAN_TRUE};
    //3.3 创建播放器
    result = (*engineInterface)->CreateAudioPlayer(engineInterface,
            &bqPlayerObject,
            &audioSrc,
            &audioSnk,
            //打开队列 需要的设置
            1,
            ids,
            req);

    if (SL_RESULT_SUCCESS != result) {
        LOGD("播放器 CreateAudioPlayer error");
        return;
    }

    //3.4 初始化播放器：SLObjectItf bqPlayerObject
    result = (*bqPlayerObject)->Realize(bqPlayerObject, SL_BOOLEAN_FALSE);
    if (SL_RESULT_SUCCESS != result) {
        LOGD("播放器 Realize error");
        return;
    }
    //3.5 获取播放器接口：SLPlayItf bqPlayerPlay
    result = (*bqPlayerObject)->GetInterface(bqPlayerObject, SL_IID_PLAY, &bqPlayerPlay);
    if (SL_RESULT_SUCCESS != result) {
        LOGD("播放器 GetInterface error");
        return;
    }
    LOGD("播放器 创建成功");
    //4.1 获取播放器队列接口：SLAndroidSimpleBufferQueueItf bqPlayerBufferQueue
    result = (*bqPlayerObject)->GetInterface(bqPlayerObject, SL_IID_BUFFERQUEUE, &bqPlayerBufferQueue);
    if(SL_RESULT_SUCCESS != result){
        LOGD("获取队列");
        return;
    }
    //4.2 设置回调 void bqPlayerCallback(SLAndroidSimpleBufferQueueItf bq, void *context)
    (*bqPlayerBufferQueue)->RegisterCallback(bqPlayerBufferQueue, bqPlayerCallback, this);
    //设置播放状态
    (*bqPlayerPlay)->SetPlayState(bqPlayerPlay, SL_PLAYSTATE_PLAYING);
    //手动激活
    bqPlayerCallback(bqPlayerBufferQueue, this);
}




