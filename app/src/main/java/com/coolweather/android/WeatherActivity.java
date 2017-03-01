package com.coolweather.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.coolweather.android.gson.Forecast;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.service.AutoUpdateService;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 *
 * 显示天气情况
 * 自己申请的key
 *      http://console.heweather.com/my/service
 *      key:f49513d45c8a4f32a2071ba492cccd3a
 *
 */

public class WeatherActivity extends AppCompatActivity {

    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTiem;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private ImageView bingPicImg;//每日更新一张背景图片
    public SwipeRefreshLayout mSwipeRefreshLayout;//下拉自动刷新 加载天气数据
    public DrawerLayout mDrawerLayout;//滑动主屏幕中显示内容
    private Button navButton;//切换城市

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //因直接加入必应服务器图片不能 跟原来的状态融合到一起，所以。。这个功能是android5.0以上系统支持
        if (Build.VERSION.SDK_INT >=21){
            //获取当前活动DecorView窗口
            View decorView =getWindow().getDecorView();
            //设置表示活动的布局会显示在状态栏上
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
             | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            //设置透明
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        //初始化各控件

        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdateTiem = (TextView) findViewById(R.id.title_update_time);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        aqiText = (TextView) findViewById(R.id.aqi_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        comfortText = (TextView) findViewById(R.id.comfort_text);
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        sportText = (TextView) findViewById(R.id.sport_text);

        //第三阶段加入背景图
        bingPicImg = (ImageView) findViewById(R.id.bing_pic_img);
        //第四阶段 加入自动刷新
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        //设置下拉刷新时进度条的颜色  colorPrimary主题颜色
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        //第五阶段
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navButton = (Button) findViewById(R.id.nav_button);


        //缓存
        SharedPreferences prfs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString =prfs.getString("weather",null);
        final String weatherId;
        if (weatherString !=null){
            //有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);

           weatherId = weather.basic.weatherId;
                Log.d("logcat","------weatherId----1--"+weatherId.toString());

            //直接显示
            showWeatherInfo(weather);
        }else {
            //无缓存时直接解析天气数据
            weatherId =getIntent().getStringExtra("weather_id");
            Log.d("logcat","------weatherId----2--"+weatherId.toString());
            weatherLayout.setVisibility(View.VISIBLE);
            //获取服务器数据
            requestWeather(weatherId);
        }

        //第四阶段 手动更新  回调onRefresh更新时监听
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.
                OnRefreshListener(){
            @Override
            public void onRefresh() {
                //请求天气信息
                requestWeather(weatherId);
            }
        });

        //第五阶段  切换城市
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开滑动菜单
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
        //必应的图片使用
        String bingPic = prfs.getString("bing_pic",null);
        if (bingPicImg != null){
            //读取缓存图片
            //图片缓存开源框架    添加引用 build.gradle 中添加配置
            Log.d("logcat","------读取缓存图片------");
            Glide.with(this).load(bingPic).into(bingPicImg);
        }else {
            //如果没有缓存则 获取必应服务器背景图片
            Log.d("logcat","------读取缓存图片---bingPic---");
            loadBingPic();

        }

    }

    /**
     * 请求服务器数据
     * 根据天气id请求城市天气信息
     * 获取和风天气接口数据
     */
    public void requestWeather(final String weatherId){
      // String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=bc0418b57b2d4918819d3974ac1285d9";

          String weatherUrl ="http://guolin.tech/api/weather?cityid=" +
              weatherId+"&key=f49513d45c8a4f32a2071ba492cccd3a";
        Log.d("logcat","------weatherUrl-----"+weatherUrl.toString());
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取失败",Toast.LENGTH_SHORT).show();
                        //第四阶段 手动点击 结束时 false表示结束刷新并隐藏刷新进度条
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final  String responseText =response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                Log.d("logcat","-------weather-1----"+weather.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //服务器返回的状态码是ok 说明请求天气成功 将返回的数据缓存到SharedPreferences当中并调用方法显示
                        if (weather != null && "ok".equals(weather.status)){
                            SharedPreferences.Editor editor =PreferenceManager.
                                    getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            //解析成功后显示数据
                            showWeatherInfo(weather);

                        }else {
                            Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        }
                        //第四阶段 手动点击 结束时  false表示结束刷新并隐藏刷新进度条
                    mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
        //加载背景图片
        loadBingPic();
    }

    /**
     *处理并展示Weather实体类中的数据
     */
    private void showWeatherInfo(Weather weather){

        if (weather!=null&&"ok".equals(weather.status)){
    try {
    String cityName = weather.basic.cityName;
    String updateTime = weather.basic.update.updateTime.split(" ")[1];
    String degree = weather.now.temperature + "℃";
    String weatherInfo = weather.now.more.info;
    titleCity.setText(cityName);
    titleUpdateTiem.setText(updateTime);
    degreeText.setText(degree);
    weatherInfoText.setText(weatherInfo);
    forecastLayout.removeAllViews();
        }catch (Exception e){
        e.printStackTrace();
    }
//加载显示未来的几天的天气预报部分，并加载了布局
        for (Forecast forecast:weather.mForecasts){
            //加载布局 添加到父类布局控件
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,
                    forecastLayout,false);
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.mTemperature.max);
            minText.setText(forecast.mTemperature.min);
            forecastLayout.addView(view);

        }

        if (weather.aqi!=null){
            aqiText.setText(weather.aqi.mCity.aqi);
            pm25Text.setText(weather.aqi.mCity.pm25);
        }

        try {


        String comfort ="舒适度："+weather.suggestion.mComfort.info;
        String carWash ="洗车指数:"+weather.suggestion.mComWash.info;
        String sport ="运动建议:"+weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);
        }catch (Exception e){
            e.printStackTrace();
        }
            //启动自动更新
            Intent intent =new Intent(this, AutoUpdateService.class);
            startService(intent);
        }else {
            Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
        }


    }


    /**
     *      加载必应每日 一图片，接口由必应提供
     */
    private void loadBingPic(){
        String requstBingPic="http://guolin.tech/api/bing_pic";
        //获取链接
        HttpUtil.sendOkHttpRequest(requstBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic =response.body().string();
                //将链接缓存到sharedpreferences中
                SharedPreferences.Editor editor =PreferenceManager.
                        getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                //将当前线程切换到主线程
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //使用框架加载图片
                        Glide.with(WeatherActivity.this).load(bingPic)
                                .into(bingPicImg);
                    }
                });
            }
        });

    }




}
