package example.com.jianyiweather.util;

import android.content.ContentValues;
import android.text.TextUtils;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import example.com.jianyiweather.db.City;
import example.com.jianyiweather.db.County;
import example.com.jianyiweather.db.Province;
import example.com.jianyiweather.gson.Weather;
/**
 * Created by Administrator on 2018\3\11 0011.
 */

public class Utility {

    //json={"id":1,"name":"北京"}
    public static boolean handleProvinceResponse(String response) {
        //判断服务器返回的数据是否为空
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    //创建记录
                    Province province = new Province();
                    JSONObject object = jsonArray.getJSONObject(i);
                    province.setProvinceCode(object.getInt("id"));
                    province.setProvinceName(object.getString("name"));
                    boolean save = province.save();//存储到数据库

                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    //城市
    public static boolean handleCityResponse(String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i=0;i<jsonArray.length();i++) {
                    City city = new City();
                    JSONObject object = jsonArray.getJSONObject(i);
                    city.setProvinceId(provinceId);
                    city.setCityCode(object.getInt("id"));
                    city.setCityName(object.getString("name"));
                    city.save();
                }

                return  true;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return  false;
    }

    //县 json={"id":937,"name":"苏州","weather_id":"CN10110401"}
    public static boolean handleCountyResponse(String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i=0;i<jsonArray.length();i++) {
                    County county = new County();
                    JSONObject object = jsonArray.getJSONObject(i);
                    county.setCityId(cityId);
                    county.setCountyName(object.getString("name"));
                    county.setWeatherId(object.getString("weather_id"));

                    county.save();

                }
                return true;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        return  false;
    }

    //将返回的Json数据解析成Weather实体
    public static Weather handleWeatherResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            //HeWeather data service 3.0
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return  new Gson().fromJson(weatherContent,Weather.class);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  null;
    }
}
