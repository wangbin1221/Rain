package com.example.rain;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.rain.gson.DailyForecast;
import com.example.rain.gson.HourlyForecast;
import com.example.rain.gson.Weather;
import com.example.rain.util.HttpUtil;
import com.example.rain.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    public DrawerLayout drawerLayout;

    public SwipeRefreshLayout swipeRefresh;

    private ScrollView weatherLayout;

    private TextView titleCity;

    private TextView titleUpdateTime;

    private TextView degreeText;

    private TextView weatherInfoText;

    private LinearLayout forecastLayout;

    private TextView aqiText;

    private TextView pm25Text;

    private TextView comfortText;

    private TextView carWashText;

    private TextView sportText;
    private TextView uvText;
    private LinearLayout forecast_daily_layout;

    private ImageView bingPicImg;

    private String mWeatherId;

    private Button navButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >=21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        forecast_daily_layout = (LinearLayout)findViewById(R.id.forcast_daily_layout);
        aqiText = (TextView) findViewById(R.id.aqi_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        comfortText = (TextView) findViewById(R.id.comfort_text);
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        sportText = (TextView) findViewById(R.id.sport_text);
        uvText = (TextView)findViewById(R.id.uv_text);
        bingPicImg = (ImageView)findViewById(R.id.bing_pic_img);
        navButton = (Button)findViewById(R.id.uv_button);

       swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
       // navButton = (Button) findViewById(R.id.nav_button);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        String bingPic = prefs.getString("bing_pic",null);
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        if (bingPic!=null){
            Glide.with(this).load(bingPic).into(bingPicImg);
        }
        else{
            loadBingPic();
        }
        if (weatherString != null) {
            // 有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            mWeatherId = weather.basic.weatgerId;
            showWeatherInfo(weather);
        } else {
            // 无缓存时去服务器查询天气
             mWeatherId = getIntent().getStringExtra("weather_id");
            Toast.makeText(WeatherActivity.this,mWeatherId,Toast.LENGTH_LONG).show();
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(mWeatherId);
        }
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
            }
        });
    }
//https://free-api.heweather.com/v5/weather?city=CN101190402&key=bc0418b57b2d4918819d3974ac1285d9
//https://free-api.heweather.com/x3/weather?city=CN101190401&key=bc0418b57b2d4918819d3974ac1285d9
    public void requestWeather(final String weatherId) {
        String weatherUrl = "https://free-api.heweather.com/v5/weather?city=" + weatherId + "&key=bc0418b57b2d4918819d3974ac1285d9";
            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "waiting...", Toast.LENGTH_SHORT).show();
                        }
                      swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "响应天气信息失败", Toast.LENGTH_SHORT).show();
                       swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
        loadBingPic();
    }
    //=======================================================================
    public void showWeatherInfo(Weather weather) {
       // Toast.makeText(WeatherActivity.this,"111",Toast.LENGTH_LONG).show();
        String cityName = weather.basic.city;
        String updateTime = weather.basic.update.loc.split(" ")[1];
        String degree = weather.now.tmp + "℃";
        String weatherInfo = weather.now.cond.txt;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        forecast_daily_layout.removeAllViews();
        for (DailyForecast forecast : weather.daily_forecast) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.cond.txt_d);
            maxText.setText(forecast.tmp.max+"℃");
            minText.setText(forecast.tmp.min+"℃");
            forecastLayout.addView(view);
        }
        for (HourlyForecast hourlyForecast:weather.hourly_forecast){
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_daily,forecastLayout,false);
            TextView date_hour = (TextView)view.findViewById(R.id.hour_text);
            TextView pop_text = (TextView)view.findViewById(R.id.pop_text);
            TextView temp_text = (TextView)view.findViewById(R.id.tmp_text);
            TextView wind_text = (TextView)view.findViewById(R.id.wind_text);
            date_hour.setText(hourlyForecast.date.split(" ")[1]);
            pop_text.setText(hourlyForecast.pop);
            temp_text.setText(hourlyForecast.tmp+"℃");
            wind_text.setText(hourlyForecast.wind.dir+hourlyForecast.wind.spd);
            forecast_daily_layout.addView(view);

        }
        if (weather.aqi != null) {
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        String comfort = "舒适度：" + weather.suggestion.comf.txt;
        String carWash = "洗车指数：" + weather.suggestion.cw.txt;
        String sport = "运行建议：" + weather.suggestion.sport.txt;
        String uv = "紫外线防护" + weather.suggestion.uv.txt;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        uvText.setText(uv);
        Toast.makeText(WeatherActivity.this,"111",Toast.LENGTH_LONG).show();
        weatherLayout.setVisibility(View.VISIBLE);
        /*Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);*/
    }
    //==================================每日一图============================
    private void loadBingPic(){
        String bingPicUrl = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(bingPicUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取每日一图失败",Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String responeText = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",responeText);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(responeText).into(bingPicImg);
                    }
                });


            }
        });
    }

}
