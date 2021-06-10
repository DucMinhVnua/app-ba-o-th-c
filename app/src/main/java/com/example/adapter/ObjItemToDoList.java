package com.example.adapter;

public class ObjItemToDoList {
    private String congViec;
    private String date;
    private int id;

    public ObjItemToDoList(String congViec, String date, int id) {
        this.congViec = congViec;
        this.date = date;
        this.id = id;
    }

    public String getCongViec() {
        return congViec;
    }

    public void setCongViec(String congViec) {
        this.congViec = congViec;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
