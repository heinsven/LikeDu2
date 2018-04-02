package edu.cuit.hxf.likedu.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;

import edu.cuit.hxf.likedu.R;
import edu.cuit.hxf.likedu.utils.EditTextClearTools;
import edu.cuit.hxf.likedu.utils.ToastUtils;


public class LogoActivity extends AppCompatActivity {

    private EditText userName;
    private EditText password;
    private ImageView unameClear;
    private ImageView pwdClear;
    private TextView registerTex;
    private Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_logo_activty);
        init();
        ToastUtils.getInstanc(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ToastUtils.getInstanc(this).destory();
    }

    private void init(){
        userName = (EditText) findViewById(R.id.et_userName);
        password = (EditText) findViewById(R.id.et_password);
        unameClear = (ImageView) findViewById(R.id.iv_unameClear);
        pwdClear = (ImageView) findViewById(R.id.iv_pwdClear);

        registerTex = (TextView)findViewById(R.id.tex_register);
        registerTex.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent=new Intent(LogoActivity.this,RegisterActivity.class);
                startActivity(registerIntent);
            }
        });

        loginBtn = (Button) findViewById(R.id.btn_login);
        loginBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AVUser.logOut();
                AVUser.logInInBackground(userName.getText().toString().trim(), password.getText().toString().trim(), new LogInCallback<AVUser>() {
                    @Override
                    public void done(AVUser avUser, AVException e) {
                        if (e == null) {
                            Intent mainIntent=new Intent(LogoActivity.this,MainActivity.class);
                            startActivity(mainIntent);
                        } else {
                            ToastUtils.getInstanc(LogoActivity.this).showToast("用户名或密码错误！");
                        }
                    }
                });
            }
        });

        EditTextClearTools.addClearListener(userName, unameClear);
        EditTextClearTools.addClearListener(password, pwdClear);
    }



}
