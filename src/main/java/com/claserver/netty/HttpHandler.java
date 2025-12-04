package com.claserver.netty;

import com.claserver.services.UserService;
import com.claserver.utils.InstantAdapter;
import com.claserver.services.DashboardService;
import com.claserver.services.DeviceService;
import com.claserver.services.PinService;

import com.google.gson.*;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.time.Instant;

import static io.netty.handler.codec.http.HttpResponseStatus.*;

public class HttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final UserService userService = new UserService();
    private final DashboardService dashboardService = new DashboardService();
    private final DeviceService deviceService = new DeviceService();
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
            JsonObject json = body.isEmpty() ? new JsonObject() : JsonParser.parseString(body).getAsJsonObject();

            switch (uri) {

                // -------------------- USER REGISTER --------------------
                case "/api/register":
                    if (!method.equals("POST")) { response = error("Method Not Allowed"); break; }

                    response = gson.toJson(
                        userService.register(
                            json.get("username").getAsString(),
                            json.get("email").getAsString(),
                            json.get("password").getAsString()
                        )
                    );
                    break;

                // -------------------- USER LOGIN --------------------
                case "/api/login":
                    if (!method.equals("POST")) { response = error("Method Not Allowed"); break; }

                    response = gson.toJson(
                        userService.login(
                            json.get("loginId").getAsString(),
                            json.get("password").getAsString()
                        )
                    );
                    break;

                // -------------------- DASHBOARD CREATE --------------------
                case "/api/dashboard/create":
                    if (!method.equals("POST")) { response = error("Method Not Allowed"); break; }

                    response = gson.toJson(
                        dashboardService.createDashboard(
                            json.get("user_id").getAsInt(),
                            json.get("name").getAsString()
                        )
                    );
                    break;

                // -------------------- DEVICE CREATE --------------------
                case "/api/device/create":
                    if (!method.equals("POST")) { response = error("Method Not Allowed"); break; }

                    response = gson.toJson(
                        deviceService.createDevice(
                            json.get("dashboard_id").getAsInt(),
                            json.get("name").getAsString()
                        )
                    );
                    break;

                // -------------------- DEVICE HEARTBEAT --------------------
                case "/api/device/heartbeat":
                    if (!method.equals("POST")) { response = error("Method Not Allowed"); break; }

                    response = gson.toJson(
                        deviceService.heartbeat(
                            json.get("token").getAsString()
                        )
                    );
                    break;

                // -------------------- PIN WRITE --------------------
                case "/api/pin/write":
                    if (!method.equals("POST")) { response = error("Method Not Allowed"); break; }

                    response = pinService.writePin(
                        json.get("token").getAsString(),
                        json.get("pin").getAsInt(),
                        json.get("value").getAsString()
                    );
                    break;

                // -------------------- PIN READ --------------------
                case "/api/pin/read":
                    if (!method.equals("POST")) { response = error("Method Not Allowed"); break; }

                    response = pinService.readPin(
                        json.get("token").getAsString(),
                        json.get("pin").getAsInt()
                    );
                    break;

                default:
                    response = error("Not Found");
            }

        } catch (Exception e) {
            response = error(e.getMessage());
        }

        FullHttpResponse res = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                OK,
                Unpooled.copiedBuffer(response, CharsetUtil.UTF_8)
        );

        res.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
        res.headers().set(HttpHeaderNames.CONTENT_LENGTH, res.content().readableBytes());

        ctx.writeAndFlush(res).addListener(ChannelFutureListener.CLOSE);
    }

    private String error(String msg) {
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
