package com.example.wibuchat100.chats;

public class Mensaje {
    private String key;
    private String texto;
    private String emisorUid;
    private String emisorNombre;
    private long timestamp;
    private boolean leido;

    public Mensaje() {}

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public String getEmisorUid() { return emisorUid; }
    public void setEmisorUid(String emisorUid) { this.emisorUid = emisorUid; }

    public String getEmisorNombre() { return emisorNombre; }
    public void setEmisorNombre(String emisorNombre) { this.emisorNombre = emisorNombre; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public boolean isLeido() { return leido; }
    public void setLeido(boolean leido) { this.leido = leido; }
}