package example.com.jianyiweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018\3\11 0011.
 */

public class Update {

        @SerializedName("loc")
        public String aqi;
        @SerializedName("utc")
        public String pm;

}
