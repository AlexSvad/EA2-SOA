package com.soa.unlam.ea2.models;

public class Evento {
    private String type_events,description;
    private Long dni,id;

    public Evento(String type_events,Long dni,String description,Long id) {
        this.type_events = type_events;
        this.dni = dni;
        this.description = description;
        this.id = id;
    }

    public String getType_events() {
        return type_events;
    }

    public void setType_events(String type_events) {
        this.type_events = type_events;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getDni() {
        return dni;
    }

    public void setDni(Long dni) {
        this.dni = dni;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
