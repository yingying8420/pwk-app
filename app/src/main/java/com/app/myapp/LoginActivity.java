package com.app.myapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.app.service.DataService;
import com.app.util.Data;
import com.app.util.Javabean;
import com.app.util.Retrofit;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {

    // UI references.
    private EditText Username;
    private EditText Password;
    private Retrofit retrofit;
    private DataService service;
    private View Login_form;
    private Button button = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        Username = (EditText) findViewById(R.id.username);
        Password = (EditText) findViewById(R.id.password);

        Username.setText("admin");
        Password.setText("admin");



        retrofit = Retrofit.getRetrofit();
        service = retrofit.getService();

        button = (Button) findViewById(R.id.sign_in_button);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = Username.getText().toString();
                String password = Password.getText().toString();
                if(username.isEmpty()){
                    Toast.makeText(getApplicationContext(),
                            "请输入用户名！",Toast.LENGTH_LONG).show();
                    return;
                }else if (password.isEmpty()){
                    Toast.makeText(getApplicationContext(),
                            "请输入密码！",Toast.LENGTH_LONG).show();
                    return;
                }

                Call<Javabean> call = service.login(username, password);
                call.enqueue(new Callback<Javabean>() {
                    //请求成功时回调
                    @Override
                    public void onResponse(Call<Javabean> call, Response<Javabean> response) {
                        //请求处理,输出结果
                        if (response.isSuccessful()) {
                            islogin(response.body());
                        }
                    }

                    //请求失败时候的回调
                    @Override
                    public void onFailure(Call<Javabean> call, Throwable throwable) {
                        System.out.println("连接失败");
                        Toast.makeText(LoginActivity.this,"连接失败",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
    //判断用户名密码是否正确 并跳转
    public  void  islogin(Javabean res){
        if(res.getStatus() == 200){
            Data app = (Data) this.getApplicationContext();
            app.setToken(res.getToken());
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }else{

        }
        Toast.makeText(LoginActivity.this,res.getMsg(),Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK){
            moveTaskToBack(true);
            return true;//不执行父类点击事件
        }
        return super.onKeyDown(keyCode, event);//继续执行父类其他点击事件
    }
}

