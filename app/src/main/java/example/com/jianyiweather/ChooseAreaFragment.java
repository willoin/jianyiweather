package example.com.jianyiweather;

import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import example.com.jianyiweather.R;
import example.com.jianyiweather.db.City;
import example.com.jianyiweather.db.County;
import example.com.jianyiweather.db.Province;
import example.com.jianyiweather.gson.Weather;
import example.com.jianyiweather.util.HttpUtil;
import example.com.jianyiweather.util.Utility;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;
/**
 * Created by Administrator on 2018\3\11 0011.
 */

public class ChooseAreaFragment extends Fragment{

    private Button btn_back;
    private TextView title;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();

    //省，市，县,为了显示相应的数据
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private int currentLevel;//当前的级别

    //进度对话框，显示加载的时候用
    private ProgressDialog progressDialog;

    //选中省，市，县
    private Province selectedProvince;
    private City selectedCity;
    private County selectedCounty;

    //省，市，县列表
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;

    public static final String ADDRESS = "http://guolin.tech/api/china";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_area, container, false);
        btn_back = (Button) view.findViewById(R.id.btn_back);
        title = (TextView) view.findViewById(R.id.text_title);
        listView = (ListView) view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<String>(getContext(), R.layout.listview_item, dataList);
        listView.setAdapter(adapter);
        return view;
    }

    //默认显示的是Province的内容
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //点击listView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);//获取当前点击的省份信息
                    queryCities();//查询省份对应的城市
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCounties();
                }/* else if (currentLevel == LEVEL_COUNTY) {//判断是在MainAcitvity中还是WeatherActivity中
                    String weatherId = countyList.get(position).getWeatherId();
                    if (getActivity() instanceof MainActivity) {
                        Intent intent = new Intent(getActivity(), WeatherActivity.class);
                        intent.putExtra("weather_id", weatherId);
                        startActivity(intent);
                        getActivity().finish();
                    } else if (getActivity() instanceof WeatherActivity) {
                        WeatherActivity activity = (WeatherActivity) getActivity();
                        activity.drawerLayout.closeDrawers();
                        activity.refrshLayout.setRefreshing(true);
                        activity.requestWeather(weatherId);
                    }
                }*/
            }
        });

        //点击返回Button
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_CITY) {
                    queryProvinces();
                } else if (currentLevel == LEVEL_COUNTY) {
                    queryCities();
                }
            }
        });
        queryProvinces();//默认查询省份
    }

    //查询的时候，先在数据库中查找，没找到再到服务器中找。初始时都是到服务器中找，通过服务器把数据存储到数据库

    private void queryProvinces() {
        title.setText("中国");
        btn_back.setVisibility(View.GONE);//不显示返回按钮
        provinceList = DataSupport.findAll(Province.class);//查找数据库中所有的内容
        if (provinceList.size() > 0) {
            dataList.clear();//清除列表中内容

            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        } else {
            String address = ADDRESS;
            queryFromServer(address, "province");//从服务器中寻找数据
        }


    }


    //查询城市
    private void queryCities() {
        title.setText(selectedProvince.getProvinceName());
        btn_back.setVisibility(View.VISIBLE);
        //寻找对应的城市信息
        cityList = DataSupport.where("provinceid=?", String.valueOf(selectedProvince.getId())).find(City.class);
        if (cityList.size() > 0) {
            dataList.clear();

            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            String address = ADDRESS + "/" + provinceCode;

            queryFromServer(address, "city");
        }

    }

    //查询县
    private void queryCounties() {
        title.setText(selectedCity.getCityName());
        btn_back.setVisibility(View.VISIBLE);

        countyList = DataSupport.where("cityid = ?", String.valueOf(selectedCity.getId())).find(County.class);
        if (countyList.size() > 0) {
            dataList.clear();

            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = ADDRESS + "/" + provinceCode + "/" + cityCode;
            queryFromServer(address, "county");
        }


    }


    /**
     * 根据传入的地址和类型从服务器上查询省市县数据。
     */

    private void queryFromServer(String address, final String type) {
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.handleProvinceResponse(responseText);
                } else if ("city".equals(type)) {
                    result = Utility.handleCityResponse(responseText, selectedProvince.getId());
                } else if ("county".equals(type)) {
                    result = Utility.handleCountyResponse(responseText, selectedCity.getId());
                }
                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if ("county".equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                // 通过runOnUiThread()方法回到主线程处理逻辑
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        makeText(getContext(), "加载失败", LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    //显示进度对话框
    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());//dialog必须要在Activity中显示
            progressDialog.setMessage("加载中...");
            progressDialog.setCanceledOnTouchOutside(false);//不允许按返回键退出
        }
        progressDialog.show();//显示
    }

    //关闭对话框
    public void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

}
