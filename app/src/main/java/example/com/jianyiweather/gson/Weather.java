package example.com.jianyiweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2018\3\11 0011.
 */

public class Weather {
    public String status;
    public Basic  basic;
    public Aqi aqi;
    public Now now;
    public Suggestion suggestion;

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}
