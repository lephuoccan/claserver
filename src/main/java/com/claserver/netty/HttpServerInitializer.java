package com.claserver.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedWriteHandler;

public class HttpServerInitializer extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();

        // HTTP codec
        p.addLast(new HttpServerCodec());
        p.addLast(new HttpObjectAggregator(64 * 1024));
        p.addLast(new ChunkedWriteHandler());

        // Main handler
        p.addLast(new HttpHandler());
    }
}
