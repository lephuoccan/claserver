package com.claserver.netty;

import com.claserver.services.UserService;
import com.claserver.services.PinService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import static io.netty.handler.codec.http.HttpResponseStatus.*;

public class HttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final UserService userService = new UserService();
    private final PinService pinService = new PinService();
    private final Gson gson = new Gson();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) {

        String uri = req.uri();
        String method = req.method().name();
        String response;

        try {
            String body = req.content().toString(CharsetUtil.UTF_8);

            switch (uri) {
                case "/api/register":
                    if (method.equals("POST")) {
                        JsonObject obj = JsonParser.parseString(body).getAsJsonObject();
                        String username = obj.get("username").getAsString();
                        String email = obj.get("email").getAsString();
                        String password = obj.get("password").getAsString();
                        response = gson.toJson(userService.register(username, email, password));
                    } else response = errorJson("Method Not Allowed");
                    break;

                case "/api/login":
                    if (method.equals("POST")) {
                        JsonObject obj = JsonParser.parseString(body).getAsJsonObject();
                        String loginId = obj.get("loginId").getAsString();
                        String password = obj.get("password").getAsString();
                        response = gson.toJson(userService.login(loginId, password));
                    } else response = errorJson("Method Not Allowed");
                    break;

                case "/api/pin/write":
                    if (method.equals("POST")) {
                        JsonObject obj = JsonParser.parseString(body).getAsJsonObject();
                        String token = obj.get("token").getAsString();
                        int pin = obj.get("pin").getAsInt();
                        String value = obj.get("value").getAsString();
                        response = pinService.writePin(token, pin, value);
                    } else response = errorJson("Method Not Allowed");
                    break;

                case "/api/pin/read":
                    if (method.equals("POST")) {
                        JsonObject obj = JsonParser.parseString(body).getAsJsonObject();
                        String token = obj.get("token").getAsString();
                        int pin = obj.get("pin").getAsInt();
                        response = pinService.readPin(token, pin);
                    } else response = errorJson("Method Not Allowed");
                    break;

                default:
                    response = errorJson("Not Found");
            }

        } catch (Exception e) {
            response = errorJson(e.getMessage());
        }

        // Build FullHttpResponse
        FullHttpResponse res = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                OK,
                Unpooled.copiedBuffer(response, CharsetUtil.UTF_8)
        );

        res.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
        res.headers().set(HttpHeaderNames.CONTENT_LENGTH, res.content().readableBytes());

        // Flush response and close connection
        ctx.writeAndFlush(res).addListener(ChannelFutureListener.CLOSE);
    }

    private String errorJson(String msg) {
        JsonObject r = new JsonObject();
        r.addProperty("error", msg);
        return gson.toJson(r);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
