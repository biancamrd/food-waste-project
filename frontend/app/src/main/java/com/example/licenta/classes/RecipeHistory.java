package com.example.licenta.classes;

import com.example.licenta.helpers.CustomDateSerializer;
import com.google.gson.annotations.JsonAdapter;

import java.util.Date;

public class RecipeHistory {
    private Long id;
    private User user;
    private String name;
    @JsonAdapter(CustomDateSerializer.class)
    private Date date;
    private Long apiId;
    public RecipeHistory() {
    }

    public RecipeHistory(Long id) {
        this.id = id;
    }

    public RecipeHistory(User user, String name, Date date, Long apiId) {
        this.user = user;
        this.name = name;
        this.date = date;
        this.apiId = apiId;
    }

    public RecipeHistory(Long id, User user, String name, Date date, Long apiId) {
        this.id = id;
        this.user = user;
        this.name = name;
        this.date = date;
        this.apiId = apiId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getApiId() {
        return apiId;
    }

    public void setApiId(Long apiId) {
        this.apiId = apiId;
    }

    @Override
    public String toString() {
        return "RecipeHistory{" +
                " Name='" + name + '\'' +
                ", Date=" + date +
                '}';
    }
}
