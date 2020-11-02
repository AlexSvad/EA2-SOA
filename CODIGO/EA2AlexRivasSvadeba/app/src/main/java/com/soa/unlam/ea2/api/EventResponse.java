package com.soa.unlam.ea2.api;

import com.soa.unlam.ea2.models.Evento;

public class EventResponse {
    private Boolean success;
    private String env;
    private Evento event;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public Evento getEvent() {
        return event;
    }

    public void setEvent(Evento event) {
        this.event = event;
    }
}