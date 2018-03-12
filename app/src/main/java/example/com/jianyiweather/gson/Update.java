package example.com.jianyiweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018\3\11 0011.
 */

public class Aqi {

    public AqiCity city;

    public class AqiCity{
        @SerializedName("aqi")
        public String aqi;
        @SerializedName("pm25")
        public String pm;
    }
}
