package com.soa.unlam.ea2.models;

public class Usuario {
    private Long dni, commission;
    private String env,name, lastname, email, password, token, token_refresh;
    public Usuario(String env,String name, String lastname, Long dni, String email, String password, Long commission, String token) {
        this.env = env;
        this.name = name;
        this.lastname = lastname;
        this.dni = dni;
        this.email = email;
        this.password = password;
        this.commission = commission;
        this.token = token;
    }

    public Long getDni() {
        return dni;
    }

    public Long getCommission() {
        return commission;
    }

    public String getEnv() {
        return env;
    }

    public String getName() {
        return name;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getToken() {
        return token;
    }

    public String getToken_refresh() {
        return token_refresh;
    }
}