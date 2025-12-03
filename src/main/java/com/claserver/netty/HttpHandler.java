package com.claserver.netty;

import com.claserver.controllers.AuthController;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;

import java.nio.charset.StandardCharsets;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public class HttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final AuthController auth = new AuthController();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) {

        String uri = req.uri();
        String method = req.method().name();

        String response;

        try {
            if (uri.equals("/api/register") && method.equals("POST")) {
                response = auth.register(req);

            } else if (uri.equals("/api/login") && method.equals("POST")) {
                response = auth.login(req);

            } else {
                response = "{\"error\":\"Not Found\"}";
            }

        } catch (Exception e) {
            response = "{\"error\":\"" + e.getMessage() + "\"}";
        }

        FullHttpResponse res = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                OK,
                Unpooled.copiedBuffer(response, StandardCharsets.UTF_8)
        );

        res.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF-8");
        res.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, res.content().readableBytes());

        boolean keepAlive = HttpUtil.isKeepAlive(req);
        if (keepAlive) {
            res.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            ctx.writeAndFlush(res);
        } else {
            ctx.writeAndFlush(res).addListener(ChannelFutureListener.CLOSE);
        }
    }
}
