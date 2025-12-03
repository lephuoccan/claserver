package com.claserver;

import java.io.*;
import java.net.*;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class TestApi {

    private static final String BASE_URL = "https://localhost:9443";

    // Bỏ qua SSL self-signed (dùng cho test)
    private static void trustAllCerts() throws Exception {
        TrustManager[] trustAll = new TrustManager[] {
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
                public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
            }
        };
        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, trustAll, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
    }

    public static void main(String[] args) throws Exception {
        trustAllCerts();

        // 1️⃣ Register
        String regResponse = sendPost("/api/register", "{\"email\":\"user1@test.com\",\"password\":\"123456\"}");
        System.out.println("Register Response: " + regResponse);

        // 2️⃣ Login
        String loginResponse = sendPost("/api/login", "{\"email\":\"user1@test.com\",\"password\":\"123456\"}");
        System.out.println("Login Response: " + loginResponse);

        // Lấy token từ response (đơn giản, parse chuỗi)
        String token = loginResponse.split("\"")[3];

        // 3️⃣ Write Virtual Pin
        String writeResponse = sendPost("/api/pin/write", "{\"token\":\"" + token + "\",\"pin\":1,\"value\":\"100\"}");
        System.out.println("Write Pin Response: " + writeResponse);

        // 4️⃣ Read Virtual Pin
        String readResponse = sendGet("/api/pin/read?token=" + token + "&pin=1");
        System.out.println("Read Pin Response: " + readResponse);
    }

    // Gửi POST
    public static String sendPost(String path, String json) throws Exception {
        URL url = new URL(BASE_URL + path);
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/json");

        try (OutputStream os = con.getOutputStream()) {
            os.write(json.getBytes());
            os.flush();
        }

        return readResponse(con);
    }

    // Gửi GET
    public static String sendGet(String path) throws Exception {
        URL url = new URL(BASE_URL + path);
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        return readResponse(con);
    }

    private static String readResponse(HttpsURLConnection con) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder resp = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            resp.append(inputLine);
        }
        in.close();
        return resp.toString();
    }
}
