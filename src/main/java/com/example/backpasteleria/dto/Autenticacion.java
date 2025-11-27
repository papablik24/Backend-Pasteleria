package com.example.backpasteleria.dto;

public class Autenticacion {
    private String token;
    private String nombre;
    private String rol;

    public Autenticacion(String token, String nombre, String rol) {
        this.token = token;
        this.nombre = nombre;
        this.rol = rol;
    }

    public String getToken() {
        return token;
    }

    public String getNombre() {
        return nombre;
    }

    public String getRol() {
        return rol;
    }
}