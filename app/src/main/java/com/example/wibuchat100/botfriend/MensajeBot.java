package com.example.wibuchat100.botfriend;

public class MensajeBot {
    public String mensaje;
    public boolean esHumano;

    public MensajeBot() {
    }

    public MensajeBot(String mensaje, boolean esHumano) {
        this.mensaje = mensaje;
        this.esHumano = esHumano;
    }


    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public boolean isEsHumano() {
        return esHumano;
    }

    public void setEsHumano(boolean esHumano) {
        this.esHumano = esHumano;
    }
}
