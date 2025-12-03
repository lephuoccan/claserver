package com.claserver.netty;

import com.claserver.utils.ConfigLoader;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;

import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.stream.ChunkedWriteHandler;

import javax.net.ssl.KeyManagerFactory;
import java.io.InputStream;
import java.security.KeyStore;

public class HttpsServerInitializer extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel ch) throws Exception {

        String keystorePath = ConfigLoader.get("ssl.keystore.path");
        String keystorePass = ConfigLoader.get("ssl.keystore.password");
        String keyPass = ConfigLoader.get("ssl.key.password");

        // Read keystore from classpath
        InputStream ksInput = getClass().getClassLoader().getResourceAsStream(keystorePath);
        if (ksInput == null) {
            throw new RuntimeException("Keystore file not found in resources: " + keystorePath);
        }

        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(ksInput, keystorePass.toCharArray());

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, keyPass.toCharArray());

        SslContext sslCtx = SslContextBuilder.forServer(kmf).build();

        ChannelPipeline p = ch.pipeline();

        // SSL handler
        p.addLast(sslCtx.newHandler(ch.alloc()));

        // HTTP codecs
        p.addLast(new HttpServerCodec());
        p.addLast(new HttpObjectAggregator(64 * 1024));
        p.addLast(new ChunkedWriteHandler());

        // Main handler
        p.addLast(new HttpHandler());
    }
}
