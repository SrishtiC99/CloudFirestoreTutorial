package com.example.srishti.cloudfirestore;

import com.google.firebase.firestore.Exclude;

public class Note {
    private String id;
    private String title;
    private String description;
    public Note() {
        //public no-arg constructor needed
    }
    public Note(String title, String description) {
        this.title = title;
        this.description = description;
    }
    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
}
