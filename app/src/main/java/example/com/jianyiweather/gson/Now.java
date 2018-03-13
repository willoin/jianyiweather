package example.com.jianyiweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018\3\11 0011.
 */

public class Now {
    @SerializedName("tmp")
    public String tmp;

    @SerializedName("cond_txt")
    public String txt;
}
