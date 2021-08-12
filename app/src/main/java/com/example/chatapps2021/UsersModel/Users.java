package com.example.chatapps2021.UsersModel;

public class Users {

    private String id;
    private String name;
    private String imageUrl;
    private String status;
    private String search;

    Users(){

    }

    public Users(String id, String name, String imageUrl,String status,String search) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.status = status;
        this.search = search;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
