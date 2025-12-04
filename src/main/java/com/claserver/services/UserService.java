package com.claserver.services;

import com.claserver.db.UserDAO;
import com.claserver.models.User;
import java.security.MessageDigest;
import java.util.HexFormat;

public class UserService {

    private final UserDAO userDAO = new UserDAO();

    private String hashPassword(String password) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(password.getBytes("UTF-8"));
        return HexFormat.of().formatHex(hash);
    }

    public User register(String username, String email, String password) throws Exception {
        if (userDAO.exists(username, email)) throw new Exception("Username/email already exists");
        String hash = hashPassword(password);
        return userDAO.createUser(username, email, hash);
    }

    public User login(String loginId, String password) throws Exception {
        String hash = hashPassword(password);
        return userDAO.login(loginId, hash);
    }
}
