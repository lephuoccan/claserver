package com.claserver.controllers;

import com.claserver.db.UserDAO;
import com.claserver.models.User;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.buffer.ByteBuf;
import java.nio.charset.StandardCharsets;

public class AuthController {

    private final UserDAO userDAO = new UserDAO();
    private final Gson gson = new Gson();

    public String register(FullHttpRequest req) {
        try {
            ByteBuf buf = req.content();
            String json = buf.toString(StandardCharsets.UTF_8);
            User user = gson.fromJson(json, User.class);

            if (user.getUsername() == null || user.getPassword() == null || user.getEmail() == null) {
                return gson.toJson(new ResponseError("All fields (username, password, email) are required"));
            }

            if (userDAO.existsByUsername(user.getUsername())) {
                return gson.toJson(new ResponseError("Username already exists"));
            }

            if (userDAO.existsByEmail(user.getEmail())) {
                return gson.toJson(new ResponseError("Email already exists"));
            }

            userDAO.register(user);
            return gson.toJson(new ResponseSuccess("User registered successfully"));

        } catch (JsonSyntaxException e) {
            return gson.toJson(new ResponseError("Malformed JSON"));
        } catch (Exception e) {
            return gson.toJson(new ResponseError(e.getMessage()));
        }
    }

    public String login(FullHttpRequest req) {
        try {
            ByteBuf buf = req.content();
            String json = buf.toString(StandardCharsets.UTF_8);
            User user = gson.fromJson(json, User.class);

            if (user.getUsername() == null && user.getEmail() == null || user.getPassword() == null) {
                return gson.toJson(new ResponseError("username/email and password are required"));
            }

            String loginField = user.getUsername() != null ? user.getUsername() : user.getEmail();
            boolean valid = userDAO.validateLogin(loginField, user.getPassword());

            if (valid) {
                return gson.toJson(new ResponseSuccess("Login successful"));
            } else {
                return gson.toJson(new ResponseError("Invalid username/email or password"));
            }

        } catch (JsonSyntaxException e) {
            return gson.toJson(new ResponseError("Malformed JSON"));
        } catch (Exception e) {
            return gson.toJson(new ResponseError(e.getMessage()));
        }
    }

    private static class ResponseError {
        String error;
        ResponseError(String message) { this.error = message; }
    }

    private static class ResponseSuccess {
        String message;
        ResponseSuccess(String message) { this.message = message; }
    }
}
