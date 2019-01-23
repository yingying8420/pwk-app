package com.app.myapp;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.FileProvider;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.AdapterView.OnItemClickListener;

import com.app.adapter.GridViewAdapter;
import com.app.service.DataService;
import com.app.util.Data;
import com.app.util.FileUtils;
import com.app.util.ImageTools;
import com.app.util.Javabean;
import com.app.util.Outlet;
import com.app.util.SpinnerOption;
import org.json.JSONArray;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.app.util.Retrofit;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;


public class XunjianActivity extends AppCompatActivity implements OnItemClickListener {

    private Retrofit retrofit;
    private DataService service;

    private Timestamp hcTime;
    private String token;
    private BigDecimal outletLongitude;
    private BigDecimal outletLatitude;
    private String outletLatLng;
    private String outletSize;

    private List<String> imgGroup;

    private EditText time1;
    private EditText pwkcc;
    private EditText latlng;

    private Button button;
    private Button latLngButton;

    //河流
    private TextView riverTv;
    private ArrayList<SpinnerOption> riverList;
    private ArrayList<String> riverNameList;//用于选择器显示
    private OptionsPickerView riverPickerView;//选择器
    private String riverCode = null;

    //巡检类型
    private TextView xjlxTv;
    private ArrayList<SpinnerOption> xjlxList;
    private ArrayList<String> xjlxNameList;//用于选择器显示
    private OptionsPickerView xjlxPickerView;//选择器
    private String xjlxCode = null;

    //是否浅没
    private TextView sfqmTv;
    private ArrayList<SpinnerOption> sfqmList;
    private ArrayList<String> sfqmNameList;//用于选择器显示
    private OptionsPickerView sfqmPickerView;//选择器
    private String sfqmCode = null;

    //入河方式
    private TextView rhfsTv;
    private ArrayList<SpinnerOption> rhfsList;
    private ArrayList<String> rhfsNameList;//用于选择器显示
    private OptionsPickerView rhfsPickerView;//选择器
    private String rhfsCode = null;

    //排放类型
    private TextView pflxTv;
    private ArrayList<SpinnerOption> pflxList;
    private ArrayList<String> pflxNameList;//用于选择器显示
    private OptionsPickerView pflxPickerView;//选择器
    private String pflxCode = null;

    //排污口性质
    private TextView pwkxzTv;
    private ArrayList<SpinnerOption> pwkxzList;
    private ArrayList<String> pwkxzNameList;//用于选择器显示
    private OptionsPickerView pwkxzPickerView;//选择器
    private String pwkxzCode = null;

    //排污口类型
    private TextView outletTypeTv;
    private ArrayList<SpinnerOption> outletTypeList;
    private ArrayList<String> outletTypeNameList;//用于选择器显示
    private OptionsPickerView outletTypePickerView;//选择器
    private String outletTypeCode = null;


    // 图片 九宫格
    private GridView gv;
    // 图片 九宫格适配器
    private GridViewAdapter gvAdapter;

    // 用于保存图片资源文件
    private List<Bitmap> lists = new ArrayList<Bitmap>();
    // 用于保存图片路径
    private List<String> list_path = new ArrayList<String>();

    // 拍照
    public static final int IMAGE_CAPTURE = 1;
    // 从相册选择
    public static final int IMAGE_SELECT = 2;
    // 照片缩小比例
    private static final int SCALE = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xunjian);

        Data app = (Data) this.getApplicationContext();
        token = app.getToken();

        retrofit = Retrofit.getRetrofit();
        service = retrofit.getService();

        init();

        //获取组件id
        time1 = (EditText) findViewById(R.id.tv_time1);//记录时间
        latlng = (EditText) findViewById(R.id.latlng);//排污口坐标
        pwkcc = (EditText) findViewById(R.id.pwkcc);//排污口尺寸
        //获取当前时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// HH:mm:ss
        Date date = new Date(System.currentTimeMillis());
        time1.setText(simpleDateFormat.format(date));
        hcTime = Timestamp.valueOf(simpleDateFormat.format(date));
        imgGroup = new ArrayList<String>();//排污口图片
        //河流名称
        riverTv = findViewById(R.id.riverTv);
        initRiverDatas();
        //巡检类型
        xjlxTv = findViewById(R.id.xjlxTv);
        xjlxList = new ArrayList<SpinnerOption>();
        xjlxNameList = new ArrayList<String>();
        initDatas(xjlxList, xjlxNameList, "xjlx" );
        initXjlxOptionPicker();
        //是否浅没
        sfqmTv = findViewById(R.id.sfqmTv);
        sfqmList = new ArrayList<SpinnerOption>();
        sfqmNameList = new ArrayList<String>();
        initDatas(sfqmList, sfqmNameList, "yesorno" );
        initSfqmOptionPicker();
        //入河方式
        rhfsTv = findViewById(R.id.rhfsTv);
        rhfsList = new ArrayList<SpinnerOption>();
        rhfsNameList = new ArrayList<String>();
        initDatas(rhfsList, rhfsNameList, "rhfs" );
        initRhfsOptionPicker();
        //排放类型
        pflxTv = findViewById(R.id.pflxTv);
        pflxList = new ArrayList<SpinnerOption>();
        pflxNameList = new ArrayList<String>();
        initDatas(pflxList, pflxNameList, "pflx" );
        initPflxOptionPicker();
        //排污口性质
        pwkxzTv = findViewById(R.id.pwkxzTv);
        pwkxzList = new ArrayList<SpinnerOption>();
        pwkxzNameList = new ArrayList<String>();
        initDatas(pwkxzList, pwkxzNameList, "rhpwkxz" );
        initPwkxzOptionPicker();

        initEvents();


        latLngButton = (Button) findViewById(R.id.getLatLng);
        latLngButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toPositionPicker();
            }
        });

        button = (Button) findViewById(R.id.done);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
    }

    private void showDialog(){
        outletSize = pwkcc.getText().toString();
        outletLatLng = latlng.getText().toString();
        if (outletLatLng == null || outletLatLng.isEmpty()){
            Toast.makeText(getApplicationContext(),
                    "请获取排污口坐标！",Toast.LENGTH_LONG).show();
            return;
        }else if(imgGroup.size() == 0){
            Toast.makeText(getApplicationContext(),
                    "请上传排污口照片！",Toast.LENGTH_LONG).show();
            return;
        }else if (outletSize == null || outletSize.isEmpty()){
            Toast.makeText(getApplicationContext(),
                    "请填写排污口尺寸！",Toast.LENGTH_LONG).show();
            return;
        }
        showAlertDialog("提示", "确定提交排污口信息吗？", "确定", "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Outlet outlet = new Outlet();
                outletSize = pwkcc.getText().toString();
                outletLatLng = latlng.getText().toString();
                //添加排污口信息
                outlet.setOutletType("1");
                outlet.setOutletLongitude(outletLongitude);
                outlet.setOutletLatitude(outletLatitude);
                outlet.setOutletSize(outletSize);
                outlet.setDeleteFlag("0");
                outlet.setSourceType("0");
                outlet.setHcTime(hcTime);//核查时间
                outlet.setRiverid(riverCode);//河流ID
                outlet.setRoutingType(xjlxCode);//巡检类型
                outlet.setOutletYesno(sfqmCode);//是否潜没
                outlet.setOutletRhfs(rhfsCode);//入河方式
                outlet.setOutletPwlx(pflxCode);//排放类型
                outlet.setOutletPwxz(pwkxzCode);//排污口性质
                outlet.setImgGroup(imgGroup);//排污口图片

                Call<Javabean> callAddOutlet = service.addOutlet(outlet, token);
                callAddOutlet.enqueue(new Callback<Javabean>() {
                    //请求成功时回调
                    @Override
                    public void onResponse(Call<Javabean> callAddOutlet, Response<Javabean> response) {
                        //请求处理,输出结果
                        if (response.isSuccessful()) {
                            isDone(response.body());
                        }
                    }

                    //请求失败时候的回调
                    @Override
                    public void onFailure(Call<Javabean> callAddOutlet, Throwable throwable) {
                        System.out.println("连接失败");
                    }
                });
            }
        }, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("取消");
            }
        });

    }

    //判断完成记录并跳转
    public  void  isDone(Javabean res){
        if(res.getStatus() == 200){
            Toast.makeText(XunjianActivity.this,"完成记录！",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }else{
            Toast.makeText(XunjianActivity.this,"记录失败！",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void initRiverDatas() {
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
        initRiverOptionPicker();

        //如果想要直接赋值的话，这样写
		/*if(riverNameList.size() > 0){
			riverTv.setText(riverNameList.get(0));//默认展现第一个
		}*/

    }


    private void initDatas(final ArrayList<SpinnerOption> arrList, final ArrayList<String> nameList, String dictCode) {
        //模拟获取数据集合
        try{
            Call<Javabean> call = service.getDictCode(URLEncoder.encode(token, "UTF-8"), URLEncoder.encode(dictCode, "UTF-8"));
            call.enqueue(new Callback<Javabean>() {
                @Override
                public void onResponse(Call<Javabean> call, Response<Javabean> response) {
                    if (response.isSuccessful()){
                        List<JSONArray> list = response.body().getData();
                        List<SpinnerOption> towns;
                        //准备好下拉框内容
                        if (list != null && !list.isEmpty()){
                            for (int i = 0; i < list.size(); i++) {
                                Map<String, String> map = (Map<String, String>)list.get(i);
                                SpinnerOption c = new SpinnerOption(map.get("itemName"),map.get("itemCode"));
                                arrList.add(c);
                            }
                        }
                        for(SpinnerOption spinnerBean : arrList){
                            nameList.add(spinnerBean.getItemName());
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

    }

    //初始化河流选择器
    private void initRiverOptionPicker() {
        riverPickerView = new OptionsPickerBuilder(XunjianActivity.this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String tx = riverNameList.get(options1);
                riverCode = riverList.get(options1).getItemCode();
                System.out.println(tx + riverCode);
                riverTv.setText(tx);

            }
        })
                .setDecorView((RelativeLayout)findViewById(R.id.formLayout))//必须是RelativeLayout，不设置setDecorView的话，底部虚拟导航栏会显示在弹出的选择器区域
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

    //初始化巡检类型选择器
    private void initXjlxOptionPicker() {
        xjlxPickerView = new OptionsPickerBuilder(XunjianActivity.this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String tx = xjlxNameList.get(options1);
                xjlxCode = xjlxList.get(options1).getItemCode();
                xjlxTv.setText(tx);
            }
        })
                .setDecorView((RelativeLayout)findViewById(R.id.formLayout))//必须是RelativeLayout，不设置setDecorView的话，底部虚拟导航栏会显示在弹出的选择器区域
                .setTitleText("巡检类型")//标题文字
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

        xjlxPickerView.setPicker(xjlxNameList);//添加数据
    }

    //初始化是否浅没选择器
    private void initSfqmOptionPicker() {
        sfqmPickerView = new OptionsPickerBuilder(XunjianActivity.this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String tx = sfqmNameList.get(options1);
                sfqmCode = sfqmList.get(options1).getItemCode();
                sfqmTv.setText(tx);
            }
        })
                .setDecorView((RelativeLayout)findViewById(R.id.formLayout))//必须是RelativeLayout，不设置setDecorView的话，底部虚拟导航栏会显示在弹出的选择器区域
                .setTitleText("是否浅没")//标题文字
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

        sfqmPickerView.setPicker(sfqmNameList);//添加数据
    }

    //初始化入河方式选择器
    private void initRhfsOptionPicker() {
        rhfsPickerView = new OptionsPickerBuilder(XunjianActivity.this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String tx = rhfsNameList.get(options1);
                rhfsCode = rhfsList.get(options1).getItemCode();
                rhfsTv.setText(tx);
            }
        })
                .setDecorView((RelativeLayout)findViewById(R.id.formLayout))//必须是RelativeLayout，不设置setDecorView的话，底部虚拟导航栏会显示在弹出的选择器区域
                .setTitleText("入河方式")//标题文字
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

        rhfsPickerView.setPicker(rhfsNameList);//添加数据
    }

    //初始化排放类型选择器
    private void initPflxOptionPicker() {
        pflxPickerView = new OptionsPickerBuilder(XunjianActivity.this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String tx = pflxNameList.get(options1);
                pflxCode = pflxList.get(options1).getItemCode();
                pflxTv.setText(tx);
            }
        })
                .setDecorView((RelativeLayout)findViewById(R.id.formLayout))//必须是RelativeLayout，不设置setDecorView的话，底部虚拟导航栏会显示在弹出的选择器区域
                .setTitleText("排放类型")//标题文字
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

        pflxPickerView.setPicker(pflxNameList);//添加数据
    }

    //初始化排污口性质选择器
    private void initPwkxzOptionPicker() {
        pwkxzPickerView = new OptionsPickerBuilder(XunjianActivity.this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String tx = pwkxzNameList.get(options1);
                pwkxzCode = pwkxzList.get(options1).getItemCode();
                pwkxzTv.setText(tx);
            }
        })
                .setDecorView((RelativeLayout)findViewById(R.id.formLayout))//必须是RelativeLayout，不设置setDecorView的话，底部虚拟导航栏会显示在弹出的选择器区域
                .setTitleText("排污口性质")//标题文字
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

        pwkxzPickerView.setPicker(pwkxzNameList);//添加数据
    }


    //下拉菜单点击事件
    private void initEvents() {
        riverTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                riverPickerView.show();
            }
        });
        xjlxTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                xjlxPickerView.show();
            }
        });
        sfqmTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sfqmPickerView.show();
            }
        });
        rhfsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rhfsPickerView.show();
            }
        });
        pflxTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pflxPickerView.show();
            }
        });
        pwkxzTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pwkxzPickerView.show();
            }
        });
    }



    /**
     * 初始化控件
     */
    private void init() {
        gv = (GridView) findViewById(R.id.noScrollgridview);
        gvAdapter = new GridViewAdapter(this, lists);
        gv.setOnItemClickListener(this);
        gv.setAdapter(gvAdapter);
        gvAdapter.setList(lists);
    }

    @Override
    protected void onDestroy() {
        //删除文件夹及文件
        FileUtils.deleteDir();
        super.onDestroy();
    }

    /**
     * 拍照
     *
     * @param path
     *            照片存放的路径
     */
    public void captureImage(String path) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStorageDirectory(), "image.jpg");
        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".fileProvider", file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, IMAGE_CAPTURE);
    }

    /**
     * 从图库中选取图片
     */
    public void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(intent, IMAGE_SELECT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && resultCode != RESULT_CANCELED) {
            String fileName = null;

            switch (requestCode) {
                case IMAGE_CAPTURE:// 拍照返回
                    // 将保存在本地的图片取出并缩小后显示在界面上
                    String newPath = Environment.getExternalStorageDirectory() + "/image.jpg";

                    Bitmap bitmap = BitmapFactory.decodeFile(newPath);
                    Bitmap newBitmap = ImageTools.zoomBitmap(bitmap,bitmap.getWidth() / SCALE, bitmap.getHeight() / SCALE);
                    // 由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
                    bitmap.recycle();
                    // 生成一个图片文件名
                    fileName = String.valueOf(System.currentTimeMillis());
                    // 将处理过的图片添加到缩略图列表并保存到本地
                    ImageTools.savePhotoToSDCard(newBitmap, FileUtils.SDPATH,fileName);
                    lists.add(newBitmap);
                    list_path.add(fileName+".jpg");
                    for (int i = 0; i < list_path.size(); i++) {
                        System.out.println("第"+i+"张照片的地址："+list_path.get(i));
                    }

                    // 更新GrideView
                    gvAdapter.setList(lists);
                    break;
                case IMAGE_SELECT:// 选择照片返回
                    ContentResolver resolver = getContentResolver();
                    // 照片的原始资源地址
                    Uri originalUri = data.getData();
                    try {
                        // 使用ContentProvider通过URI获取原始图片
                        Bitmap photo = MediaStore.Images.Media.getBitmap(resolver,originalUri);
                        if (photo != null) {
                            // 为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
                            Bitmap smallBitmap = ImageTools.zoomBitmap(photo,photo.getWidth() / SCALE, photo.getHeight()/ SCALE);
                            // 释放原始图片占用的内存，防止out of memory异常发生
                            photo.recycle();
                            // 生成一个图片文件名
                            fileName = String.valueOf(System.currentTimeMillis());
                            // 将处理过的图片添加到缩略图列表并保存到本地
                            ImageTools.savePhotoToSDCard(smallBitmap, FileUtils.SDPATH,fileName);
                            lists.add(smallBitmap);
                            list_path.add(fileName+".jpg");

                            for (int i = 0; i < list_path.size(); i++) {
                                System.out.println("第"+i+"照片的地址："+list_path.get(i));
                            }

                            // 更新GrideView
                            gvAdapter.setList(lists);
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
                default:
                    break;
            }
            if (fileName != null) {
                String base64Img = imageToBase64(FileUtils.SDPATH + fileName + ".jpg");
                imgGroup.add(base64Img);
            }
        }else if(data != null){
            if(requestCode == 1){
                outletLongitude = new BigDecimal(data.getStringExtra("longtitude"));
                outletLatitude = new BigDecimal(data.getStringExtra("latitude"));
                latlng.setText(outletLongitude + "," + outletLatitude );
            }
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        if (position == getDataSize()) {// 点击“+”号位置添加图片
            showAlertDialog(false, "提示", new String[] { "拍照", "从图库选择", "取消" },
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which + 1) {
                                case 1:// 拍照
                                    captureImage(FileUtils.SDPATH);
                                    break;
                                case 2:// 从图库选择
                                    selectImage();
                                    break;
                                case 3:// 取消
                                    break;

                                default:
                                    break;
                            }
                        }
                    });
        } else {// 点击图片删除
            showAlertDialog("提示", "是否删除此图片？", "确定", "取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    lists.remove(position);
                    FileUtils.delFile(list_path.get(position));
                    list_path.remove(position);
                    gvAdapter.setList(lists);
                }
            }, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.out.println("取消");
                }
            });
        }
    }

    private int getDataSize() {
        return lists == null ? 0 : lists.size();
    }

    /**
     * 将图片转换成Base64编码的字符串
     *
     * @return base64编码的字符串
     */
    public static String imageToBase64(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        InputStream is = null;
        byte[] data = null;
        String result = null;
        try {
            is = new FileInputStream(path);
            //创建一个字符流大小的数组。
            data = new byte[is.available()];
            //写入数组
            is.read(data);
            //用默认的编码格式进行编码
            result = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public void toPositionPicker(){
        Intent intent = new Intent(this, SelectPoint.class);
        startActivityForResult(intent, 1);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            moveTaskToBack(true);
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 单选列表类型的弹出框
     *
     * @param cancelable
     *            设置是否能让用户主动取消弹窗
     *
     * @param title
     *            弹窗标题
     * @param items
     *            弹窗的列表数据源
     * @param selectListener
     *            弹窗列表选择事件
     */
    public void showAlertDialog(boolean cancelable, String title,
                                String items[], DialogInterface.OnClickListener selectListener) {
        new android.app.AlertDialog.Builder(this)
                // 设置按系统返回键的时候按钮弹窗不取消
                .setCancelable(cancelable).setTitle(title)
                .setItems(items, selectListener).show();
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
}
