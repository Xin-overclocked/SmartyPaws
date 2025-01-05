package com.example.smartypaws;

public class UserProfile {
    private final String name;
    private String email;
    private final String location;
    private final String about;

    public UserProfile(String displayName, String location, String about) {
        this.name = displayName;
        this.location = location;
        this.about = about;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getLocation() {
        return location;
    }

    public String getAbout() {
        return about;
    }
}
