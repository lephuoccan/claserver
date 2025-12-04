package com.claserver.netty;

import com.claserver.services.UserService;
import com.claserver.services.PinService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import com.claserver.utils.InstantAdapter; 

import static io.netty.handler.codec.http.HttpResponseStatus.*;

import java.time.Instant;

public class HttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final UserService userService = new UserService();
    private final PinService pinService = new PinService();
    private final Gson gson = new GsonBuilder()
                .registerTypeAdapter(Instant.class, new InstantAdapter())
                .create();

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

                        // --- Bắt đầu kiểm tra an toàn JSON ---
                        JsonElement userElement = obj.get("username");
                        JsonElement emailElement = obj.get("email");
                        JsonElement passElement = obj.get("password");

                        if (userElement == null || userElement.isJsonNull() ||
                            emailElement == null || emailElement.isJsonNull() ||
                            passElement == null || passElement.isJsonNull()) {
                            
                            response = errorJson("Missing 'username', 'email', or 'password' in request body.");
                            
                        } else {
                            // Nếu các trường tồn tại, lấy giá trị và tiến hành đăng ký
                            String username = userElement.getAsString();
                            String email = emailElement.getAsString();
                            String password = passElement.getAsString();
                            
                            // service.register trả về đối tượng User.
                            // Đảm bảo đối tượng GSON dùng ở đây đã đăng ký TypeAdapter cho Instant (như đã hướng dẫn)
                            response = gson.toJson(userService.register(username, email, password));
                        }
                        // --- Kết thúc kiểm tra an toàn JSON ---

                    } else {
                        response = errorJson("Method Not Allowed");
                    }
                    break;
                case "/api/login":
                    if (method.equals("POST")) {
                        JsonObject obj = JsonParser.parseString(body).getAsJsonObject();

                        // --- Bắt đầu kiểm tra an toàn JSON ---
                        JsonElement loginIdElement = obj.get("loginId");
                        JsonElement passElement = obj.get("password");

                        if (loginIdElement == null || loginIdElement.isJsonNull() ||
                            passElement == null || passElement.isJsonNull()) {
                            
                            response = errorJson("Missing 'username', 'email', or 'password' in request body.");
                        }
                        else {
                            // Nếu các trường tồn tại, lấy giá trị và tiến hành đăng ký
                            String loginId = loginIdElement.getAsString();
                            String password = passElement.getAsString();
                            
                            // service.register trả về đối tượng User.
                            // Đảm bảo đối tượng GSON dùng ở đây đã đăng ký TypeAdapter cho Instant (như đã hướng dẫn)
                            response = gson.toJson(userService.login(loginId, password));
                        }
                        // --- Kết thúc kiểm tra an toàn JSON ---

                    } else {
                        response = errorJson("Method Not Allowed");
                    }
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
