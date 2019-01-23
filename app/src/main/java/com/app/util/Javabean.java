package com.app.util;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

public class Javabean<T> implements Serializable {
    private String msg;
    private String realName;
    private String loginid;
    private String reqURL;
    private int status;
    private String token;
    private List<T> data;
    private List<T> outletlist;
    private JSONArray jsonArray;
    private JSONObject jsonObject;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getLoginid() {
        return loginid;
    }

    public void setLoginid(String loginid) {
        this.loginid = loginid;
    }

    public String getReqURL() {
        return reqURL;
    }

    public void setReqURL(String reqURL) {
        this.reqURL = reqURL;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public List<T> getOutletlist() {
        return outletlist;
    }

    public void setOutletlist(List<T> outletlist) {
        this.outletlist = outletlist;
    }

    public JSONArray getJsonArray() {
        return jsonArray;
    }

    public void setJsonArray(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    @Override
    public String toString() {
        return "Javabean{" +
                "msg='" + msg + '\'' +
                ", realName='" + realName + '\'' +
                ", loginid='" + loginid + '\'' +
                ", reqURL='" + reqURL + '\'' +
                ", status=" + status +
                ", token='" + token + '\'' +
                ", data=" + data +
                ", outletlist=" + outletlist +
                ", jsonArray=" + jsonArray +
                ", jsonObject=" + jsonObject +
                '}';
    }
}
