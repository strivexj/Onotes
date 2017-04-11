package com.example.onotes.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by cwj Apr.10.2017 3:41 PM
 */

public class QqUser extends BmobObject {

    /**
     * ret : 0
     * msg :
     * is_lost : 0
     * nickname : No pains,no gains.
     * gender : 男
     * province : 广东
     * city : 佛山
     * figureurl : http://qzapp.qlogo.cn/qzapp/1106087728/FCCC25B9B646840DC1C408F0C6E989C7/30
     * figureurl_1 : http://qzapp.qlogo.cn/qzapp/1106087728/FCCC25B9B646840DC1C408F0C6E989C7/50
     * figureurl_2 : http://qzapp.qlogo.cn/qzapp/1106087728/FCCC25B9B646840DC1C408F0C6E989C7/100
     * figureurl_qq_1 : http://q.qlogo.cn/qqapp/1106087728/FCCC25B9B646840DC1C408F0C6E989C7/40
     * figureurl_qq_2 : http://q.qlogo.cn/qqapp/1106087728/FCCC25B9B646840DC1C408F0C6E989C7/100
     * is_yellow_vip : 0
     * vip : 0
     * yellow_vip_level : 0
     * level : 0
     * is_yellow_year_vip : 0
     *  String openID = obj.getString("openid");
     *  String accessToken = obj.getString("access_token");
     *  String expires = obj.getString("expires_in");
     */

    private String nickname;
    private String gender;
    private String province;
    private String city;
    private String figureurl;
    private String figureurl_1;
    private String figureurl_2;
    private String figureurl_qq_1;
    private String figureurl_qq_2;
    private String openID;
    private String accessToken;
    private String expires;

    public String getOpenID() {
        return openID;
    }

    public void setOpenID(String openID) {
        this.openID = openID;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getExpires() {
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getFigureurl() {
        return figureurl;
    }

    public void setFigureurl(String figureurl) {
        this.figureurl = figureurl;
    }

    public String getFigureurl_1() {
        return figureurl_1;
    }

    public void setFigureurl_1(String figureurl_1) {
        this.figureurl_1 = figureurl_1;
    }

    public String getFigureurl_2() {
        return figureurl_2;
    }

    public void setFigureurl_2(String figureurl_2) {
        this.figureurl_2 = figureurl_2;
    }

    public String getFigureurl_qq_1() {
        return figureurl_qq_1;
    }

    public void setFigureurl_qq_1(String figureurl_qq_1) {
        this.figureurl_qq_1 = figureurl_qq_1;
    }

    public String getFigureurl_qq_2() {
        return figureurl_qq_2;
    }

    public void setFigureurl_qq_2(String figureurl_qq_2) {
        this.figureurl_qq_2 = figureurl_qq_2;
    }
}
