package com.example.wibuchat100;

public class HelperClass {
    private String uid;
    private String username;
    private String email;

    //Esta variable que voy a añadir es el token, muy muy inportante
    private String fcmToken;

    public HelperClass() {} // Constructor vacío obligatorio para Firebase

    public HelperClass(String uid, String username, String email) {
        this.uid = uid;
        this.username = username;
        this.email = email;
    }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}