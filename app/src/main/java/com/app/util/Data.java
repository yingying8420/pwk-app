package com.app.util;


import android.app.Application;

public class Data extends Application {

  private String token;

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}