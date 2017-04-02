package com.example.onotes.bean;

import cn.bmob.v3.BmobObject;


/**
 * Created by cwj Mar.30.2017 6:26 PM
 */

public class Person extends BmobObject {
    private String username;
    private String password;
    private String email;
    private Integer phone;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public int getPhone() {
        return phone;
    }
}
