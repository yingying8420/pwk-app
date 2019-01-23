package com.app.myapp;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.app.adapter.GridViewAdapter;
import com.app.service.DataService;
import com.app.util.Data;
import com.app.util.FileUtils;
import com.app.util.ImageTools;
import com.app.util.Javabean;
import com.app.util.Outlet;
import com.app.util.Retrofit;
import com.app.util.SpinnerOption;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HeyanActivity extends AppCompatActivity {

    private Retrofit retrofit;
    private DataService service;

    private String token;
    private String outletSize;
    private EditText time1;
    private EditText pwkcc;
    private Button button = null;
    private Outlet o;
    private Timestamp hcTime;

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heyan);

        Data app = (Data) this.getApplicationContext();
        token = app.getToken();

        retrofit = Retrofit.getRetrofit();
        service = retrofit.getService();

        String json = getIntent().getStringExtra("data");
        o = new Gson().fromJson(json,Outlet.class);

        //获取组件id
        time1 = (EditText) findViewById(R.id.tv_time1);//核查时间
        pwkcc = (EditText) findViewById(R.id.pwkcc);//排污口尺寸

        //获取当前时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// HH:mm:ss
        Date date = new Date(System.currentTimeMillis());
        time1.setText(simpleDateFormat.format(date));
        hcTime = Timestamp.valueOf(simpleDateFormat.format(date));

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

        button = (Button) findViewById(R.id.update);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
    }


    private void showDialog(){
        showAlertDialog("提示", "确定提交排污口信息吗？", "确定", "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //修改排污口信息
                Outlet outlet = new Outlet();
                outlet.setId(o.getId());
                outlet.setRiverid(o.getRiverid());
                outlet.setOutletLatitude(o.getOutletLatitude());
                outlet.setOutletLongitude(o.getOutletLongitude());
                outlet.setOutletType("2");
                outlet.setDeleteFlag(o.getDeleteFlag());
                outlet.setOutletAddress(o.getOutletAddress());
                outlet.setOutletCode(o.getOutletCode());
                outlet.setOutletInfo(o.getOutletInfo());
                outlet.setCurrOrgId(o.getCurrOrgId());
                outlet.setGroupid(o.getGroupid());
                outlet.setPicId(o.getPicId());
                outlet.setSourceType(o.getSourceType());
                outlet.setOutletPicname(o.getOutletPicname());
                outlet.setDepname(o.getDepname());
                outlet.setOutletSize(pwkcc.getText().toString());
                outlet.setHcTime(hcTime);
                outlet.setRoutingType(xjlxCode);//巡检类型
                outlet.setOutletYesno(sfqmCode);//是否潜没
                outlet.setOutletRhfs(rhfsCode);//入河方式
                outlet.setOutletPwlx(pflxCode);//排放类型
                outlet.setOutletPwxz(pwkxzCode);//排污口性质

                Call<Javabean> callUpdateOutlet = service.addOutlet(outlet, token);
                callUpdateOutlet.enqueue(new Callback<Javabean>() {
                    //请求成功时回调
                    @Override
                    public void onResponse(Call<Javabean> callUpdateOutlet, Response<Javabean> response) {
                        //请求处理,输出结果
                        if (response.isSuccessful()) {
                            isUpdate(response.body());
                        }
                    }

                    //请求失败时候的回调
                    @Override
                    public void onFailure(Call<Javabean> callUpdateOutlet, Throwable throwable) {
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
    public  void  isUpdate(Javabean res){
        if(res.getStatus() == 200){
            Toast.makeText(HeyanActivity.this,"完成记录！",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }else{
            Toast.makeText(HeyanActivity.this,"记录失败！",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
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

    //初始化巡检类型选择器
    private void initXjlxOptionPicker() {
        xjlxPickerView = new OptionsPickerBuilder(HeyanActivity.this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String tx = xjlxNameList.get(options1);
                xjlxCode = xjlxList.get(options1).getItemCode();
                xjlxTv.setText(tx);
            }
        })
                .setDecorView((RelativeLayout)findViewById(R.id.heyanLayout))//必须是RelativeLayout，不设置setDecorView的话，底部虚拟导航栏会显示在弹出的选择器区域
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
        sfqmPickerView = new OptionsPickerBuilder(HeyanActivity.this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String tx = sfqmNameList.get(options1);
                sfqmCode = sfqmList.get(options1).getItemCode();
                sfqmTv.setText(tx);
            }
        })
                .setDecorView((RelativeLayout)findViewById(R.id.heyanLayout))//必须是RelativeLayout，不设置setDecorView的话，底部虚拟导航栏会显示在弹出的选择器区域
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
        rhfsPickerView = new OptionsPickerBuilder(HeyanActivity.this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String tx = rhfsNameList.get(options1);
                rhfsCode = rhfsList.get(options1).getItemCode();
                rhfsTv.setText(tx);
            }
        })
                .setDecorView((RelativeLayout)findViewById(R.id.heyanLayout))//必须是RelativeLayout，不设置setDecorView的话，底部虚拟导航栏会显示在弹出的选择器区域
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
        pflxPickerView = new OptionsPickerBuilder(HeyanActivity.this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String tx = pflxNameList.get(options1);
                pflxCode = pflxList.get(options1).getItemCode();
                pflxTv.setText(tx);
            }
        })
                .setDecorView((RelativeLayout)findViewById(R.id.heyanLayout))//必须是RelativeLayout，不设置setDecorView的话，底部虚拟导航栏会显示在弹出的选择器区域
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
        pwkxzPickerView = new OptionsPickerBuilder(HeyanActivity.this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String tx = pwkxzNameList.get(options1);
                pwkxzCode = pwkxzList.get(options1).getItemCode();
                pwkxzTv.setText(tx);
            }
        })
                .setDecorView((RelativeLayout)findViewById(R.id.heyanLayout))//必须是RelativeLayout，不设置setDecorView的话，底部虚拟导航栏会显示在弹出的选择器区域
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
