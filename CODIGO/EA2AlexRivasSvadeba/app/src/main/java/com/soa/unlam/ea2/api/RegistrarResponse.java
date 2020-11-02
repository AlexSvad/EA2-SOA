package com.soa.unlam.ea2.api;

public class RegistrarResponse {
    private Boolean success;
    private String env;
    private String msg;
    private String token;
    private String token_refresh;

    public Boolean getSuccess() {
        return success;
    }

    public String getEnv() {
        return env;
    }

    public String getMsg() {
        return msg;
    }

    public String getToken() {
        return token;
    }

    public String getToken_refresh() {
        return token_refresh;
    }
}
