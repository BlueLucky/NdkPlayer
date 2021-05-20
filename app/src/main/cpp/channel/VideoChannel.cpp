#include <libavformat/avformat.h>
#include "VideoChannel.h"

VideoChannel::VideoChannel(AVFormatContext *avFormatContext,AVCodecContext * codeContext,int stream_index) {
    this->avFormatContext = avFormatContext;
    this->codeContext = codeContext;
    this->stream_index = stream_index;
}

VideoChannel::~VideoChannel() {
}

void VideoChannel::player_() {
    //申请帧缓冲区
    int out_size = av_image_get_buffer_size(AV_PIX_FMT_RGBA, codeContext->width,codeContext->height, 1);
    auto *out_buffer = (uint8_t *) malloc(sizeof(uint8_t) * out_size);

    //申请解压缩后的frame
    AVFrame * rgbFrame = av_frame_alloc();
    //分配缓存区，并读取文件数据填充
    //    av_image_fill_arrays(rgbFrame->data,rgbFrame->linesize,out_buffer, AV_PIX_FMT_RGBA,
    //                         codeContext->width, codeContext->height,1);
    //解压包
    auto *pAVPacket = (AVPacket *) av_malloc(sizeof(AVPacket));
    //文件视频,读完的时候<0
    while(av_read_frame(avFormatContext,pAVPacket)>=0){//
        //视频steam index
        if(pAVPacket->stream_index==stream_index){
           int r = avcodec_send_packet(codeContext,pAVPacket);
           if(r<0){
               av_packet_free(&pAVPacket);
               return;
           }
           r = avcodec_receive_frame(codeContext,rgbFrame);
           if(r){
               av_packet_free(&pAVPacket);
               return;
           }
           //给ANativeWindow 渲染
        }
        av_packet_free(&pAVPacket);
    }
}
