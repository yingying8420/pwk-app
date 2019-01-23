package com.app.myapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.app.service.DataService;
import com.app.util.Data;
import com.app.util.Javabean;
import com.app.util.Outlet;
import com.app.util.Retrofit;
import com.app.util.SpinnerOption;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class QlpwkActivity extends AppCompatActivity {

    private Retrofit retrofit;
    private DataService service;

    private String token;
    private EditText time1;
    private Button button = null;
    private Outlet o;

    //清理原因
    private TextView qlyyTv;
    private ArrayList<SpinnerOption> qlyyList;
    private ArrayList<String> qlyyNameList;//用于选择器显示
    private OptionsPickerView qlyyPickerView;//选择器
    private String qlyyCode = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qlcpwk);

        Data app = (Data) this.getApplicationContext();
        token = app.getToken();

        retrofit = Retrofit.getRetrofit();
        service = retrofit.getService();

        String json = getIntent().getStringExtra("data");
        o = new Gson().fromJson(json,Outlet.class);

        //获取组件id
        time1 = (EditText) findViewById(R.id.tv_time1);//记录时间
        time1.setText(o.getCreateTime());

        //清理原因
        qlyyTv = findViewById(R.id.qlyyTv);
        qlyyList = new ArrayList<SpinnerOption>();
        qlyyNameList = new ArrayList<String>();
        initDatas(qlyyList, qlyyNameList, "deleteFlag" );
        initQlyyOptionPicker();

        initEvents();


        button = (Button) findViewById(R.id.delete);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
    }


    private void showDialog(){
        showAlertDialog("提示", "确定提交清理排污口吗？", "确定", "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //修改排污口信息
                Outlet outlet = new Outlet();
                outlet.setHcTime(o.getHcTime());
                outlet.setId(o.getId());
                outlet.setRiverid(o.getRiverid());
                outlet.setOutletLatitude(o.getOutletLatitude());
                outlet.setOutletLongitude(o.getOutletLongitude());
                outlet.setOutletAddress(o.getOutletAddress());
                outlet.setOutletCode(o.getOutletCode());
                outlet.setOutletInfo(o.getOutletInfo());
                outlet.setCurrOrgId(o.getCurrOrgId());
                outlet.setGroupid(o.getGroupid());
                outlet.setPicId(o.getPicId());
                outlet.setSourceType(o.getSourceType());
                outlet.setOutletPicname(o.getOutletPicname());
                outlet.setDepname(o.getDepname());
                outlet.setOutletType(o.getOutletType());
                outlet.setOutletSize(o.getOutletSize());
                outlet.setRoutingType(o.getRoutingType());//巡检类型
                outlet.setOutletYesno(o.getOutletYesno());//是否潜没
                outlet.setOutletRhfs(o.getOutletRhfs());//入河方式
                outlet.setOutletPwlx(o.getOutletPwlx());//排放类型
                outlet.setOutletPwxz(o.getOutletPwxz());//排污口性质
                outlet.setDeleteFlag(qlyyCode);

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
            Toast.makeText(QlpwkActivity.this,"清理成功！",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }else{
            Toast.makeText(QlpwkActivity.this,"清理失败！",Toast.LENGTH_LONG).show();
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
                                if (!c.getItemCode().equals("0")){
                                    arrList.add(c);
                                }
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
    private void initQlyyOptionPicker() {
        qlyyPickerView = new OptionsPickerBuilder(QlpwkActivity.this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String tx = qlyyNameList.get(options1);
                qlyyCode = qlyyList.get(options1).getItemCode();
                qlyyTv.setText(tx);
            }
        })
                .setDecorView((RelativeLayout)findViewById(R.id.qlpwkLayout))//必须是RelativeLayout，不设置setDecorView的话，底部虚拟导航栏会显示在弹出的选择器区域
                .setTitleText("清理原因")//标题文字
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

        qlyyPickerView.setPicker(qlyyNameList);//添加数据
    }

    //下拉菜单点击事件
    private void initEvents() {
        qlyyTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qlyyPickerView.show();
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
