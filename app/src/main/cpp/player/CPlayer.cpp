#include "CPlayer.h"

CPlayer::CPlayer(const char *dataSource, JNICallbackHelper *pHelper) {
    this->dataSource = new char[strlen(dataSource) + 1];
    strcpy(this->dataSource, dataSource);
    this->helper = pHelper;
}

//函数指针
void *task_prepare(void *args) {
    auto *player = static_cast<CPlayer *>(args);
    player->prepare_();
    return 0; // 必须返回
}

void CPlayer::prepare_() {
    //打开本地媒体
    formatContext = avformat_alloc_context();

    LOGD("path：%s",dataSource);

    AVDictionary *dictionary = 0;
    av_dict_set(&dictionary, "timeout", "5000000", 0);//// 单位微妙
    /**
     * 1，AVFormatContext *
     * 2，路径
     * 3，AVInputFormat *fmt  Mac、Windows 摄像头、麦克风， 我们目前安卓用不到
     * 4，各种设置：例如：Http 连接超时， 打开rtmp的超时  AVDictionary **options
     */
    int r = avformat_open_input(&formatContext, dataSource, 0, &dictionary);

    //是否字典
    av_dict_free(&dictionary);

    //0 on success, a negative AVERROR on failure.
    if (r < 0) {
        onError(THREAD_CHILD,r,"打开视频失败");
        LOGD("error：打开视频文件 %d",av_err2str(r));
        return;
    }
    //查找媒体中的音视频流的信息
    r = avformat_find_stream_info(formatContext,0);
    // >=0 if OK, AVERROR_xxx on error
    if(r<0){
        onError(THREAD_CHILD,r,"查找视频流信息");
        LOGD("error：视频流信息");
        return;
    }
    //根据视频信息 找到对应流的对应 编码器
    for (int i = 0; i < formatContext->nb_streams; ++i) {
        //获取媒体流
        AVStream *stream = formatContext->streams[i];
        //获取编解码器 参数
        AVCodecParameters *parameters = stream->codecpar;
        //根据codec_id 查找avcode
        AVCodec *avCodec = avcodec_find_decoder(parameters->codec_id);
        //获取媒体流参数
        AVCodecContext * codeContext = avcodec_alloc_context3(avCodec);
        if(!codeContext){
            onError(THREAD_CHILD,r,av_err2str(r));
            LOGD("error：分配上下文");
            return;
        }
        //设置编码器上的参数
        r = avcodec_parameters_to_context(codeContext,parameters);
        // >= 0 on success, a negative AVERROR code on failure.
        if(r<0){
            onError(THREAD_CHILD,r,av_err2str(r));
            LOGD("error：解码器参数拷贝");
            return;
        }
        //打开编码器
        r = avcodec_open2(codeContext,avCodec,0);
        if(r){
            onError(THREAD_CHILD,r,av_err2str(r));
            LOGD("error：打开解码器");
            return;
        }
        //音频流
        if(parameters->codec_type == AVMediaType::AVMEDIA_TYPE_AUDIO){
            audio_channel = new AudioChannel(i,codeContext);
        }
        //视频流
        else if(parameters->codec_type==AVMediaType::AVMEDIA_TYPE_VIDEO){
            video_channel = new VideoChannel(i,codeContext);
            video_channel->setRenderCallback(renderCallBack);
        }
    }
    //判断解包是否正常
    if(!video_channel&&!audio_channel){
        LOGD("error：音视频channel异常");
        onError(THREAD_CHILD,-1,av_err2str(r));
        return;
    }
    //回调prepared 成功
    if(helper){
        helper->onPrepared(THREAD_CHILD);
    }
}

//启动子线程开启format解析
void CPlayer::prepare() {
    pthread_create(&pid_prepare, 0, task_prepare, this);
}


//函数指针
void *task_start(void *args) {
    auto *player = static_cast<CPlayer *>(args);
    player->start_();
    return 0; // 必须返回
}

//播放视频
void CPlayer::start() {
    isPlaying = 1;
    //视频播放
    if(video_channel){
        video_channel->start();
    }
    //音视频播放
    if(audio_channel){
        audio_channel->start();
    }
    pthread_create(&pid_start, 0, task_start, this);
}

//开始播放视频
void CPlayer::start_() {
    //取出所以的压缩包
    while (isPlaying){
        //AVPacket
        AVPacket * packet = av_packet_alloc();
       int r =  av_read_frame(formatContext,packet);
       if(!r){//ret==0
           if(video_channel&&video_channel->stream_index==packet->stream_index){
               video_channel->packets.insertToQueue(packet);
           }else if(audio_channel&&audio_channel->stream_index==packet->stream_index){
               audio_channel->packets.insertToQueue(packet);
           }
       }else if(r==AVERROR_EOF){//文件结尾
           //todo 文件结尾
       }else{
           break;
       }
    }
    isPlaying = 0;
    //todo stop
    video_channel->stop();
    audio_channel->stop();
}

CPlayer::~CPlayer() {
    if (dataSource) {
        delete dataSource;
    }
    if(helper){
        delete helper;
    }
}

void CPlayer::onError(int threadModel,int errorCode, char * errorMsg) {
    if(helper){
        helper->onError(threadModel,errorCode,errorMsg);
    }
}

void CPlayer::setRendCallBack(RenderCallBack renderCallBack) {
    this->renderCallBack = renderCallBack;
}







