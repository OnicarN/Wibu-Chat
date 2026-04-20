package com.example.wibuchat100.solicitudes;

public class SolicitudItem {
    private String key;
    private String emisorUid;
    private String emisorNombre;
    private String emisorEmail;

    public SolicitudItem() {}

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public String getEmisorUid() { return emisorUid; }
    public void setEmisorUid(String emisorUid) { this.emisorUid = emisorUid; }

    public String getEmisorNombre() { return emisorNombre; }
    public void setEmisorNombre(String emisorNombre) { this.emisorNombre = emisorNombre; }

    public String getEmisorEmail() { return emisorEmail; }
    public void setEmisorEmail(String emisorEmail) { this.emisorEmail = emisorEmail; }
}