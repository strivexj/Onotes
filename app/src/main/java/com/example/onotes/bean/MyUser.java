package com.example.onotes.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;

/**
 * Created by cwj Apr.03.2017 11:08 AM
 */

public class MyUser extends BmobUser {

    private Boolean sex; //0为女 1为男  /斜眼笑
    private String nickname;
    private String location;
    private String birthday;
    private String personalizeSignature;
    private String avatarUrl;
    private String wrok;

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    private  String introduction;


    public Boolean getSex() {
        return sex;
    }

    public void setSex(Boolean sex) {
        this.sex = sex;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getPersonalizeSignature() {
        return personalizeSignature;
    }

    public void setPersonalizeSignature(String personalizeSignature) {
        this.personalizeSignature = personalizeSignature;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getWrok() {
        return wrok;
    }

    public void setWrok(String wrok) {
        this.wrok = wrok;
    }
}
