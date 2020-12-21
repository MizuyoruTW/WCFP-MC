package com.example.wcfp_mc;


public class Category {
    private int CFPs = 0;
    private String name = "";
    private String url = "";
    private boolean fav = false;

    public Category() {

    }

    public void setCFPs(int CFPs) {
        this.CFPs = CFPs;
    }

    public int getCFPs() {
        return this.CFPs;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return this.url;
    }

    public boolean isFav() {
        return fav;
    }

    public void setFav(boolean fav) {
        this.fav = fav;
    }
}


