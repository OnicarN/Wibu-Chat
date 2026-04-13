package com.example.wibuchat100;

import java.util.List;

public class Grupo {
    private String key;
    private String nombre;
    private String creadoPor;
    private List<String> participantes;

    public Grupo() {}

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCreadoPor() { return creadoPor; }
    public void setCreadoPor(String creadoPor) { this.creadoPor = creadoPor; }

    public List<String> getParticipantes() { return participantes; }
    public void setParticipantes(List<String> participantes) { this.participantes = participantes; }
}