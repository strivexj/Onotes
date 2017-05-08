package com.example.onotes.bean;

/**
 * Created by cwj Apr.12.2017 10:30 PM
 */

public class Notes {
    private int id;
    private String cityid;
    private String cityEn;
    private String cityZh;
    private String lat;
    private String lon;
    private String date;
    private String time;
    private String title;
    private String picture;
    private String content;
    private String location;

    private float textsize;
    private float linespace;
    private int bgcolor;
    private int type;


    public boolean isCheckbox_delete() {
        return checkbox_delete;
    }

    public void setCheckbox_delete(boolean checkbox_delete) {
        this.checkbox_delete = checkbox_delete;
    }

    private boolean checkbox_delete;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }



    public int getBgcolor() {
        return bgcolor;
    }

    public void setBgcolor(int bgcolor) {
        this.bgcolor = bgcolor;
    }

    public float getTextsize() {
        return textsize;
    }

    public void setTextsize(float textsize) {
        this.textsize = textsize;
    }

    public float getLinespace() {
        return linespace;
    }

    public void setLinespace(float linespace) {
        this.linespace = linespace;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityid() {
        return cityid;
    }

    public void setCityid(String cityid) {
        this.cityid = cityid;
    }

    public String getCityEn() {
        return cityEn;
    }

    public void setCityEn(String cityEn) {
        this.cityEn = cityEn;
    }

    public String getCityZh() {
        return cityZh;
    }

    public void setCityZh(String cityZh) {
        this.cityZh = cityZh;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
