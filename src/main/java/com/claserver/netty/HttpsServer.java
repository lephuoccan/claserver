package com.claserver.netty;

import com.claserver.utils.ConfigLoader;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class HttpsServer {

    private final int port;

    public HttpsServer() {
        this.port = Integer.parseInt(ConfigLoader.get("server.https.port"));
    }

    public void start() throws Exception {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();

        try {
            ServerBootstrap bs = new ServerBootstrap();
            bs.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new HttpsServerInitializer());

            Channel ch = bs.bind(port).sync().channel();
            System.out.println("HTTPS server running at https://localhost:" + port);
            ch.closeFuture().sync();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
