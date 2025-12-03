package com.claserver.netty;

import com.claserver.services.UserService;
import com.claserver.services.PinService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import io.netty.util.CharsetUtil;

public class HttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final UserService userService = new UserService();
    private final PinService pinService = new PinService();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) {

        String uri = req.uri();
        String method = req.method().name();
        String body = req.content().toString(CharsetUtil.UTF_8);
        String response = "";

        try {
            if (uri.equals("/api/register") && method.equals("POST")) {
                JsonObject json = JsonParser.parseString(body).getAsJsonObject();
                response = userService.register(
                        json.get("username").getAsString(),
                        json.get("email").getAsString(),
                        json.get("password").getAsString()
                );

            } else if (uri.equals("/api/login") && method.equals("POST")) {
                JsonObject json = JsonParser.parseString(body).getAsJsonObject();
                response = userService.login(
                        json.get("loginId").getAsString(),
                        json.get("password").getAsString()
                );

            } else if (uri.equals("/api/pin/write") && method.equals("POST")) {
                JsonObject json = JsonParser.parseString(body).getAsJsonObject();
                response = pinService.writePin(
                        json.get("token").getAsString(),
                        json.get("pin").getAsInt(),
                        json.get("value").getAsString()
                );

            } else if (uri.equals("/api/pin/read") && method.equals("POST")) {
                JsonObject json = JsonParser.parseString(body).getAsJsonObject();
                response = pinService.readPin(
                        json.get("token").getAsString(),
                        json.get("pin").getAsInt()
                );

            } else {
                JsonObject r = new JsonObject();
                r.addProperty("status", "error");
                r.addProperty("msg", "Not Found");
                response = r.toString();
            }

        } catch (Exception ex) {
            JsonObject r = new JsonObject();
            r.addProperty("status", "error");
            r.addProperty("msg", ex.getMessage());
            response = r.toString();
        }

        FullHttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
                Unpooled.copiedBuffer(response, CharsetUtil.UTF_8));
        res.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
        ctx.writeAndFlush(res);
    }
}
