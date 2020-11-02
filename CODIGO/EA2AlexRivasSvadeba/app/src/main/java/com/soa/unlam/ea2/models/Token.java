package com.soa.unlam.ea2.models;

public class Token {
    private static Token mInstance= null;

    public String token, token_refresh;


    public String getToken() {
        return "token";
    }
}
