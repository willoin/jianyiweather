package example.com.jianyiweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018\3\11 0011.
 */

public class Basic {
    @SerializedName("city")
    public String cityName;
    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }
}
