package example.com.jianyiweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018\3\11 0011.
 */

public class Forecast {
    public String date;
    public Cond cond;
    public Tmp tmp;

    public class Cond{
        @SerializedName("txt_d")
        public String txt;
    }

    public class Tmp{
        public String max;
        public String min;
    }
}
