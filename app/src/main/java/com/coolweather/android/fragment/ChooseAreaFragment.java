package com.coolweather.android.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.android.R;
import com.coolweather.android.db.City;
import com.coolweather.android.db.County;
import com.coolweather.android.db.Province;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by User on 2017/2/20.
 *
 * 省市县碎片
 * 碎片是不能直接显示到界面上的,
 * 因此还需要把它添加到活动里才行
 */

public class ChooseAreaFragment extends Fragment {

    public static final int LEVEL_PROVINCE=0;

    public static final int LEVEL_CITY =1;

    public static final int LEVEL_COUNTY =2;

    private ProgressDialog mProgressDialog ;

    private TextView titleText;

    private Button backButton;

    private ListView mListView;

    private ArrayAdapter<String> mAdapter;
        //数据列表集合
    private List<String> dataList = new ArrayList<>();

    /**
     * 省列表
     */

    private List<Province> provincesList ;
    //市列表
    private List<City>  mCityList;
    //县列表
    private List<County> mCountyList;

    private Province selectedProvince ;//选中省份
    private City selectedCity; //选中城市
    private int currentLevel;//当前选中级别

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //创建加载数据初始化
        View view = inflater.inflate(R.layout.choose_area,container,false);
        titleText = (TextView) view.findViewById(R.id.title_text);
        backButton = (Button) view.findViewById(R.id.back_button);
        mListView = (ListView) view.findViewById(R.id.list_view);

        mAdapter= new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        mListView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (currentLevel ==LEVEL_PROVINCE){
                    selectedProvince = provincesList.get(i);
                    Log.d("logcat","selectedProvince---"+selectedProvince.toString());
                    // 数据点击 省时市就要查询出来
                    queryCities();

                }else if (currentLevel==LEVEL_CITY){
                    selectedCity = mCityList.get(i);
                    //数据点击 市时县就要查询出来
                    queryCounties();
                    Log.d("logcat","selectedCity---"+selectedCity.toString());
                }
            }
        });

        Log.d("logcat","00000---");
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentLevel == LEVEL_COUNTY){
                    //点击 往上一级走
                    Log.d("logcat","666---");
                    queryCities();
                }else if (currentLevel == LEVEL_CITY){
                    // 往上一级走 显示 上级数据
                    queryProvinces();
                    Log.d("logcat","7777---");
                }
            }
        });
        //未选择的时候 省数据也要查询出来
        Log.d("logcat","555---");
        queryProvinces();
    }

    //查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询
    private void queryProvinces(){
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        Log.d("logcat","1111111---");
        provincesList = DataSupport.findAll(Province.class);
        Log.d("logcat","22---");
        if (provincesList.size() > 0){
            dataList.clear();
            for (Province province :provincesList){
                dataList.add(province.getProvinceName());
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        }else {
            String address ="http://guolin.tech/api/china";
            //解析
            queryFromServer(address,"province");

        }

    }

    //查询选中省内所有的市，优先从数据库查询，如果没有则服务器

    private  void queryCities(){

        titleText.setText(selectedProvince.getProvinceName());//

        backButton.setVisibility(View.VISIBLE);
        //市区数据用数据库查询
        mCityList = DataSupport.where("provinceid = ?" ,String.valueOf(selectedProvince
        .getId())).find(City.class);

        if (mCityList.size()>0){

            dataList.clear();
            for (City city :mCityList){

                dataList.add(city.getCityName());
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            currentLevel=LEVEL_CITY;


        }else {
            int provinceCode = selectedCity.getCityCode();
            String address ="http://guolin.tech/api/china"+provinceCode;
            //解析
            queryFromServer(address,"city");
      }


    }

    private void queryCounties(){
        titleText.setText(selectedCity.getCityName());
        backButton.setText(View.VISIBLE);

        mCountyList = DataSupport.where("cityid =?",String.valueOf(selectedCity.getId()
        )).find(County.class);

        if (mCountyList.size()>0){
            dataList.clear();
            for (County county:mCountyList){

                dataList.add(county.getCountyName());

            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            currentLevel =LEVEL_COUNTY;

        }else {
            //因为县级需要省级和市级数据  所以两个编码都需要
            int provinceCode =selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address ="http://guolin.tech/api/china"+provinceCode+"/"+cityCode;
        //解析
        queryFromServer(address,"county");
        }


    }
    //根据传入的地址和类型从服务器上查询省市县数据

    private void queryFromServer(String address,final String type){
        //显示进度对话框
        showProgressDialog();

        //网络访问发送请求 数据解析
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            //回调失败
            @Override
            public void onFailure(Call call, IOException e) {
            //通过runOn。。方法回到主线程处理逻辑更新UI
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //关闭对话框

                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });

            }
        //回调成功
            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String responseText = response.body().string();
                boolean result =false;
                if ("province".equals(type)){
                    //解析和处理服务器返回的数据
                    result = Utility.handleProviceResponse(responseText);
                }else if ("city".equals(type)){
                    result =Utility.handleCityResponse(responseText,
                            selectedProvince.getId());
                }else if ("county".equals(type)){
                    result =Utility.handleCountyResponse(responseText,
                            selectedCity.getId());
                }

                if (result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //加载成功 关闭对话框
                            closeProgressDialog();
                            if ("province".equals(type)){
                                //查询省数据  显示
                                queryProvinces();
                            }else if ("city".equals(type)){
                                //查询市数据 显示
                                queryCities();
                            }else if ("".equals(type)){
                                //查询县数据 显示
                                queryCounties();
                            }

                        }
                    });
                }
            }
        });

    }

    //显示进度对话框
    private void showProgressDialog(){
        if (mProgressDialog ==null){
            mProgressDialog =new ProgressDialog(getActivity());
            mProgressDialog.setMessage("加载中...");
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
        mProgressDialog.show();
    }

    //关闭进度对话框
    private void closeProgressDialog(){

        if (mProgressDialog !=null){
            mProgressDialog.dismiss();
        }

    }
}




















