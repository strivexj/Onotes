package com.example.onotes.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by cwj Apr.13.2017 10:11 AM
 */

public class Chat extends BmobObject{

    private String content;
    private int type;
    private String name;
    private String pictureurl;

    public String getPictureurl() {
        return pictureurl;
    }

    public void setPictureurl(String pictureurl) {
        this.pictureurl = pictureurl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }



}
