#include "VideoChannel.h"

VideoChannel::~VideoChannel() {
}


VideoChannel::VideoChannel(int stream_index, AVCodecContext *avCodecContext)
:BaseChannel(stream_index,avCodecContext)
{

}

void VideoChannel::video_decode() {
    AVPacket *packet=0;
    while (isPlaying){
        int res = packets.getQueueAndDel(packet);
        if(!isPlaying){
            break;
        }
        if(!res){
            continue;//没有成功也需要继续取
        }
        //发送到缓存区
        int r = avcodec_send_packet(avCodecContext,packet);
        releaseAvPacket(&packet);
        if(r<0){
            break;
        }
        AVFrame *avFrame = av_frame_alloc();
        r = avcodec_receive_frame(avCodecContext,avFrame);
        if(r==AVERROR(EAGAIN)){
            continue;
        }else if(r!=0){
            break;
        }
        frames.insertToQueue(avFrame);
    }
    releaseAvPacket(&packet);
}

//正式屏幕渲染
void VideoChannel::video_play() {
    AVFrame *frame=0;
    SwsContext * swsContext = sws_getContext(
            //输入
            avCodecContext->width,
            avCodecContext->height,
            avCodecContext->pix_fmt,
            //输出格式
            avCodecContext->width,
            avCodecContext->height,
            AV_PIX_FMT_RGBA,
            SWS_BILINEAR,//转化算法
            nullptr,nullptr,nullptr);
    uint8_t* dst_data[4]; //RGBA
    int dstStride[4];//RGBA
    //给dst——data 申请内存
    av_image_alloc(dst_data,dstStride,
            avCodecContext->width,avCodecContext->height,
            AV_PIX_FMT_RGBA,1);

    while (isPlaying){
        int res = frames.getQueueAndDel(frame);
        if(!isPlaying){
            break;
        }
        if(!res){
            continue;//没有成功也需要继续取
        }
        //格式转换（YUV-RGBA）
        sws_scale(swsContext
                //YUV 输入数据
                ,frame->data,frame->linesize,0,
                avCodecContext->height,
                //RGBA输出
                dst_data,dstStride);
        //ANativeWindows
        // SurfaceView ----- ANatvieWindows
        //那不到surface 需要回调
        //
        renderCallBack(dst_data[0],avCodecContext->width,avCodecContext->height,dstStride[0]);
        //释放frame
        releaseAvFrame(&frame);
    }
    releaseAvFrame(&frame);
    isPlaying = 0;
    av_free(&dst_data);
    sws_freeContext(swsContext);
}

void VideoChannel::stop() {
}

void *task_decode(void * args){
    auto* channel = static_cast<VideoChannel *>(args);
    channel->video_decode();
    return 0;
}

void *task_player(void * args){
    auto* channel = static_cast<VideoChannel *>(args);
    channel->video_play();
    return 0;
}

void VideoChannel::start() {
    isPlaying = 1;
    packets.setWork(1);
    frames.setWork(1);
    //队列继续编码
    pthread_create(&pid_video_decode,0,task_decode, this);
    //取队列中的原始数据播放
    pthread_create(&pid_video_play,0,task_player, this);
}

void VideoChannel::setRendCallBack(RenderCallBack renderCallBack) {
    this->renderCallBack = renderCallBack;
}


