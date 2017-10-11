package com.example.rain.gson;

/**
 * Created by Administrator on 2017/10/10.
 */

public class Basic {
    public String city;
    private String cnty;
    public String weatgerId;
    public String lat;
    public String lon;

    public Update update;
    public class Update
    {
        public String loc;
        public String utc;
    }
}