package com.example.eventure.model;

public class CurrentUser {
    private String id;
    private String email;
    private String password;

    public CurrentUser() {
    }

    public CurrentUser(String email, String password) {
        this.id = "1";
        this.email = email;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
