package example.com.jianyiweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2018\3\11 0011.
 */

public class Weather {
    public Basic  basic;
    public Update aqi;
    public String status;
    public Now now;

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;

    public Suggestion suggestion;


}
