package com.shanzhu.book.utils;

import com.shanzhu.book.model.User;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class TokenProcessor {
    private TokenProcessor() {}
    private static final TokenProcessor instance = new TokenProcessor();
    private static final Map<String, User> tokenMap = new ConcurrentHashMap<>();

    public static TokenProcessor getInstance() { return instance; }

    public String generateToken() {
        String token = (System.currentTimeMillis() + new Random().nextInt(999999999)) + "";
        try {
            MessageDigest md = MessageDigest.getInstance("md5");
            byte[] md5 = md.digest(token.getBytes());
            return Base64.getEncoder().encodeToString(md5);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveUser(String token, User user) {
        if (token != null && user != null) tokenMap.put(token, user);
    }

    public User getUser(String token) {
        return token == null ? null : tokenMap.get(token);
    }

    public void removeUser(String token) {
        if (token != null) tokenMap.remove(token);
    }
}