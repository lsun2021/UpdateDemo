package com.demo.mymobilephonedemo;

import android.app.Application;
import android.content.Context;
import android.util.Log;

/**
 * Created by ${momoThree} on 2017/10/23.
 * Title: 自定义Applictaion
 * @author asus
 */

public class MyApplication extends Application {

    public  static  MyApplication  app;


    public  static  MyApplication  getInstance(){
        return  app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;

    }

}
