package com.example.rain.gson;

import java.util.List;

/**
 * Created by Administrator on 2017/10/10.
 */

public class Weather {
    public Aqi aqi;
    public Basic basic;
    public List<DailyForecast> daily_forecast;
    public List<HourlyForecast> hourly_forecast;
    public Now now;
    public String status;
    public Suggestion suggestion;
}
