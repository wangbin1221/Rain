package com.example.rain.gson;

/**
 * Created by Administrator on 2017/10/10.
 */

public class Aqi {
    public City city;
    public class City
    {
        public String aqi;
        private String co;
        private String no2;
        private String o3;
        private String pm10;
        public String pm25;
        private String qlty;
        private String so2;
    }
}