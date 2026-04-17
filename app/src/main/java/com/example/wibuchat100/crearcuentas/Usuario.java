package com.example.wibuchat100.crearcuentas;

public class Usuario {
    private String uid;
    private String username;
    private String email;
    private String fcmToken;

    public Usuario() {} // Constructor vacío obligatorio para Firebase

    public Usuario(String uid, String username, String email) {
        this.uid = uid;
        this.username = username;
        this.email = email;
    }

    public String getUid() { return uid; }

    public void setUid(String uid) { this.uid = uid; }
    public String getUsername() { return username; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }
    public String getFcmToken() {
        return fcmToken;
    }
    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}