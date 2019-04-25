package com.app.myapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.app.service.DataService;
import com.app.util.Data;
import com.app.util.Javabean;
import com.app.util.Outlet;
import com.app.util.Retrofit;
import com.app.util.SpinnerOption;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity implements AMap.OnMyLocationChangeListener,
        AMap.OnMapTouchListener{
    private Retrofit retrofit;
    private DataService service;
    private MapView mMapView = null;
    private Button takePicBtn = null;
    private Switch yhcpwk;
    private Switch whcpwk;
    private String token;
    private Map<String, Object> yhcOutletMap;
    private Map<String, Object> whcOutletMap;
    private AMap aMap = null;
    private List<Marker> yhcMarker;
    private List<Marker> whcMarker;
    private boolean isFirst = false;
    private LatLng locationLatLng;
    private Outlet firstWhcOutlet;
    private Outlet fisrtYhcOutlet;
    private Spinner hlmc;
    private boolean followMove = true;

    private TextView riverTv;//选择爱好
    /**爱好列表集合*/
    private ArrayList<SpinnerOption> riverList;
    private ArrayList<String> riverNameList;//用于选择器显示
    private OptionsPickerView riverPickerView;//选择器
    private String riverCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        retrofit = Retrofit.getRetrofit();
        service = retrofit.getService();

        Data app = (Data) this.getApplicationContext();
        token = app.getToken();

        init(savedInstanceState);

        riverTv = (TextView) findViewById(R.id.riverTv);
        initDatas();
        initEvents();

        //已核查排污口
        yhcpwk = (Switch) findViewById(R.id.yhcpwk);
        yhcpwk.setChecked(true);
        yhcpwk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isFirst){
                    if (isChecked){
                        yhcpwkMarker(yhcOutletMap);
                    }else if (yhcOutletMap.size() != 0){
                        for (Marker marker : yhcMarker){
                            marker.remove();
                        }
                    }
                }
            }
        });

        //未核查排污口
        whcpwk = (Switch) findViewById(R.id.whcpwk);
        whcpwk.setChecked(true);
        whcpwk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isFirst) {
                    if (isChecked) {
                        whcpwkMarker(whcOutletMap);
                    } else if (whcOutletMap.size() != 0){
                        for (Marker marker : whcMarker) {
                            marker.remove();
                        }
                    }
                }
            }
        });

        //发现并记录按钮
        takePicBtn = (Button) findViewById(R.id.buttonPicture);
        takePicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, XunjianActivity.class);
                startActivity(intent);
            }
        });
        //地图视角监听
        aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                followMove = false;
            }
            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {

            }
        });

    }

    /**
     * 初始化
     */
    private void init(Bundle savedInstanceState) {
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mMapView.getMap();
            MyLocationStyle myLocationStyle;
            myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类
            myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
            myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
            myLocationStyle.strokeColor(Color.TRANSPARENT);// 设置圆形的边框颜色
            myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色
            myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked);
            myLocationStyle.myLocationIcon(bitmap);
            aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
            aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
            aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        }
        //设置地图拖动监听
        aMap.setOnMapTouchListener(this);
        //设置SDK 自带定位消息监听
        aMap.setOnMyLocationChangeListener(this);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onMyLocationChange(Location location) {
        // 定位回调监听
        if(location != null) {
            locationLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            if (followMove) {
                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationLatLng, 8));
            }
            Bundle bundle = location.getExtras();
            if(bundle != null) {
                int errorCode = bundle.getInt(MyLocationStyle.ERROR_CODE);
                String errorInfo = bundle.getString(MyLocationStyle.ERROR_INFO);
                // 定位类型，可能为GPS WIFI等，具体可以参考官网的定位SDK介绍
                int locationType = bundle.getInt(MyLocationStyle.LOCATION_TYPE);
//                Log.e("amap", "定位信息， code: " + errorCode + " errorInfo: " + errorInfo + " locationType: " + locationType );

            } else {

            }

        } else {

        }
    }

    //地图拖动监听
    @Override
    public void onTouch(MotionEvent motionEvent) {
        if (followMove) {
            Log.i("amap","onTouch 关闭地图和小蓝点一起移动的模式");
            //用户拖动地图后，不再跟随移动，直到用户点击定位按钮
            followMove = false;
        }
    }

    //标记监听
    private class OnMarkerClickListener implements AMap.OnMarkerClickListener {
        @Override
        public boolean onMarkerClick(Marker marker) {
            String title = marker.getTitle();
            String id = marker.getSnippet();
            if (title.equals("已核查排污口")){
                Outlet hyOutlet = (Outlet)yhcOutletMap.get(id);
                Intent intent = new Intent(MainActivity.this, QlpwkActivity.class);
                intent.putExtra("data", new Gson().toJson(hyOutlet));
                startActivity(intent);
            }else if (title.equals("未核查排污口")){
                Outlet wyOutlet = (Outlet)whcOutletMap.get(id);
                Intent intent = new Intent(MainActivity.this, HeyanActivity.class);
                intent.putExtra("data", new Gson().toJson(wyOutlet));
                startActivity(intent);
            }
            return true;
        }

    }

    private void initDatas() {
        //========================================初始化爱好列表集合========================================
        riverList = new ArrayList<SpinnerOption>();
        riverNameList = new ArrayList<String>();
        //模拟获取数据集合
        try{
            Call<Javabean> call = service.getRiver(URLEncoder.encode(token, "UTF-8"));
            call.enqueue(new Callback<Javabean>() {
                @Override
                public void onResponse(Call<Javabean> call, Response<Javabean> response) {
                    if (response.isSuccessful()){
                        List<JSONArray> list = response.body().getData();
                        List<SpinnerOption> towns;
                        //准备好下拉框内容
                        if (list != null && !list.isEmpty()){
                            SpinnerOption c1 = new SpinnerOption("全部","");
                            riverList.add(c1);
                            for (int i = 0; i < list.size(); i++) {
                                Map<String, String> map = (Map<String, String>)list.get(i);
                                SpinnerOption c = new SpinnerOption(map.get("itemName"),map.get("itemCode"));
                                riverList.add(c);
                            }
                        }
                        for(SpinnerOption spinnerBean : riverList){
                            riverNameList.add(spinnerBean.getItemName());
                        }
                    }
                }

                @Override
                public void onFailure(Call<Javabean> call, Throwable t) {
                    System.out.println("连接失败");
                }
            });
        }catch (Exception e) {
            e.printStackTrace();
        }


        //============初始化选择器============
        initHobbyOptionPicker();
        //如果想要直接赋值的话，这样写
		/*if(riverNameList.size() > 0){
			riverTv.setText(riverNameList.get(0));//默认展现第一个
		}*/

    }

    //初始化爱好选择器
    private void initHobbyOptionPicker() {
        riverPickerView = new OptionsPickerBuilder(MainActivity.this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String tx = riverNameList.get(options1);
                riverCode = riverList.get(options1).getItemCode();
                riverTv.setText(tx);

                yhcpwk.setChecked(true);
                whcpwk.setChecked(true);
                System.out.println("河流：" + riverCode);
                if(!riverCode.equals("0")){
                    getOutlet(riverCode);
                    aMap.setOnMarkerClickListener(new OnMarkerClickListener());
                }

            }
        })
                .setDecorView((RelativeLayout)findViewById(R.id.activity_rootview))//必须是RelativeLayout，不设置setDecorView的话，底部虚拟导航栏会显示在弹出的选择器区域
                .setTitleText("选择河流")//标题文字
                .setTitleSize(20)//标题文字大小
                .setTitleColor(getResources().getColor(R.color.pickerview_title_text_color))//标题文字颜色
                .setCancelText("取消")//取消按钮文字
                .setCancelColor(getResources().getColor(R.color.pickerview_cancel_text_color))//取消按钮文字颜色
                .setSubmitText("确定")//确认按钮文字
                .setSubmitColor(getResources().getColor(R.color.pickerview_submit_text_color))//确定按钮文字颜色
                .setContentTextSize(20)//滚轮文字大小
                .setTextColorCenter(getResources().getColor(R.color.pickerview_center_text_color))//设置选中文本的颜色值
                .setLineSpacingMultiplier(1.8f)//行间距
                .setDividerColor(getResources().getColor(R.color.pickerview_divider_color))//设置分割线的颜色
                .setSelectOptions(0)//设置选择的值
                .build();

        riverPickerView.setPicker(riverNameList);//添加数据
    }

    private void initEvents() {
        //选择爱好的下拉菜单点击事件
        riverTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                riverPickerView.show();
            }
        });

    }

    //获取排污口
    public void getOutlet(String riverid){
        try {
            Call<Javabean> callGetOutlet = service.getOutlet(URLEncoder.encode(token, "UTF-8"), URLEncoder.encode(riverid, "UTF-8"));
            callGetOutlet.enqueue(new Callback<Javabean>() {
                //请求成功时回调
                @Override
                public void onResponse(Call<Javabean> callGetOutlet, Response<Javabean> response) {
                    //请求处理,输出结果
                    if (response.isSuccessful()) {
                        yhcOutletMap = new HashMap<String, Object>();//已核查排污口
                        whcOutletMap = new HashMap<String, Object>();//未核查排污口
                        Gson gson = new Gson();
                        List<Outlet> list = response.body().getOutletlist();
                        String str = gson.toJson(list);
                        //所有排污口
                        List<Outlet> outletList = gson.fromJson(str,
                                new TypeToken<List<Outlet>>() {
                                }.getType());
                        if (list != null && !list.isEmpty()){
                            for (int i = 0; i < list.size(); i++) {
                                Outlet outlet = outletList.get(i);
                                if (outlet.getOutletType() != null && outlet.getDeleteFlag() != null) {
                                    if (outlet.getOutletType().equals("1") && outlet.getDeleteFlag().equals("0")) {
                                        yhcOutletMap.put(outlet.getId(), outlet);
                                    } else if (outlet.getOutletType().equals("2") && outlet.getDeleteFlag().equals("0")) {
                                        yhcOutletMap.put(outlet.getId(), outlet);
                                    } else if (outlet.getOutletType().equals("3") && outlet.getDeleteFlag().equals("0")) {
                                        whcOutletMap.put(outlet.getId(), outlet);
                                    }
                                }
                            }
                        }
                        aMap.clear();
                        isFirst = true;
                        yhcpwkMarker(yhcOutletMap);
                        whcpwkMarker(whcOutletMap);
                    }
                }

                //请求失败时候的回调
                @Override
                public void onFailure(Call<Javabean> callGetOutlet, Throwable throwable) {
                    System.out.println("连接失败");
                    Toast.makeText(getApplicationContext(),
                            "连接失败,请重新选择！",Toast.LENGTH_LONG).show();
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    //已核查排污口标记
    public void yhcpwkMarker(Map<String, Object> yhcMap){
        if(yhcMap.size() != 0){
            yhcMarker = new ArrayList();//已核查排污口标记
            for (Map.Entry<String, Object> entry : yhcMap.entrySet()) {
                Outlet ol = (Outlet)entry.getValue();
                MarkerOptions markerOption = new MarkerOptions();
                LatLng latLng = new LatLng(ol.getOutletLatitude().doubleValue(), ol.getOutletLongitude().doubleValue());
                markerOption.position(latLng);
                markerOption.title("已核查排污口");
                markerOption.snippet(ol.getId());
                markerOption.draggable(true);//设置Marker可拖动
                markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(),R.mipmap.yhc)));
                // 将Marker设置为贴地显示，可以双指下拉地图查看效果
                markerOption.setFlat(true);//设置marker平贴地图效果
                Marker ymaker = aMap.addMarker(markerOption);
                yhcMarker.add(ymaker);
            }
        }
    }

    //未核查排污口标记
    public void whcpwkMarker(Map<String, Object> whcMap){
        if(whcMap.size() != 0) {
            whcMarker = new ArrayList();//未核查排污口标记
            for (Map.Entry<String, Object> entry : whcMap.entrySet()) {
                Outlet ol = (Outlet) entry.getValue();
                MarkerOptions markerOption = new MarkerOptions();
                LatLng latLng = new LatLng(ol.getOutletLatitude().doubleValue(), ol.getOutletLongitude().doubleValue());
                markerOption.position(latLng);
                markerOption.title("未核查排污口");
                markerOption.snippet(ol.getId());
                markerOption.draggable(true);//设置Marker可拖动
                markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(), R.mipmap.whc)));
                // 将Marker设置为贴地显示，可以双指下拉地图查看效果
                markerOption.setFlat(true);//设置marker平贴地图效果
                Marker wmaker = aMap.addMarker(markerOption);
                whcMarker.add(wmaker);
            }
        }
    }


    //地图缩放
//    public void sfMap(){
//        if (isFirst){
//            if (fisrtYhcOutlet != null){
//                LatLng firstYhcLatLng = new LatLng(fisrtYhcOutlet.getOutletLatitude().doubleValue(),fisrtYhcOutlet.getOutletLongitude().doubleValue());
//                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstYhcLatLng, 13));
//                isFirst = false;
//            } else if (firstWhcOutlet != null && fisrtYhcOutlet == null){
//                LatLng firstWhcLatLng = new LatLng(firstWhcOutlet.getOutletLatitude().doubleValue(),firstWhcOutlet.getOutletLongitude().doubleValue());
//                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstWhcLatLng, 13));
//                isFirst = false;
//            } else if (firstWhcOutlet == null && fisrtYhcOutlet == null){
//                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationLatLng, 13));
//                isFirst = false;
//            }
//        }
//    }

    //菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    //退出
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.quit) {
            showAlertDialog("提示", "确定退出当前登录吗？", "确定", "取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    quit();
                }
            }, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.out.println("取消");
                }
            });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 带点击事件的双按钮AlertDialog
     *
     * @param title
     *            弹框标题
     * @param message
     *            弹框消息内容
     * @param positiveButton
     *            弹框第一个按钮的文字
     * @param negativeButton
     *            弹框第二个按钮的文字
     * @param positiveClickListener
     *            弹框第一个按钮的单击事件
     * @param negativeClickListener
     *            弹框第二个按钮的单击事件
     */
    public void showAlertDialog(String title, String message,
                                String positiveButton, String negativeButton,
                                DialogInterface.OnClickListener positiveClickListener,
                                DialogInterface.OnClickListener negativeClickListener) {
        new AlertDialog.Builder(this).setCancelable(false).setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButton, positiveClickListener)
                .setNegativeButton(negativeButton, negativeClickListener)
                .show();

    }

    //设备返回键监听
    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK){
            moveTaskToBack(true);
            return true;//不执行父类点击事件
        }
        return super.onKeyDown(keyCode, event);//继续执行父类其他点击事件
    }


    //退出
    public void quit(){
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }


}