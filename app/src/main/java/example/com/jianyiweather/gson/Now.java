package example.com.jianyiweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018\3\11 0011.
 */

public class Now {
    @SerializedName("tmp")
    public String tmp;

    public Cond cond;

    public class Cond {
        @SerializedName("txt")
        public String txt;
    }
}
