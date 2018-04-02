package edu.cuit.hxf.likedu.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import edu.cuit.hxf.likedu.MyLeanCloudApp;
import edu.cuit.hxf.likedu.R;
import edu.cuit.hxf.likedu.utils.DemoUtils;
import edu.cuit.hxf.likedu.utils.EditTextClearTools;
import edu.cuit.hxf.likedu.utils.StringUtils;
import edu.cuit.hxf.likedu.utils.ToastUtils;


import android.app.TabActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVMobilePhoneVerifyCallback;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.RequestMobileCodeCallback;
import com.avos.avoscloud.SignUpCallback;
import com.avos.avoscloud.UpdatePasswordCallback;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends TabActivity implements OnClickListener {
    // 声明用到的页面控件
    private TabHost tabHost;
    private TabSpec tab1;
    private TabSpec tab2;
    //手机注册控件
    private EditText etRegisterName;
    private EditText etCode;
    private EditText etPassword;
    private Button btCode;
    private Button btRegister;
    private TextView tvUserProtocol;
    private CheckBox protocolCheckbox;
    //邮箱注册控件
    private EditText etRegisterEmailNum;
    private EditText etPasswordEmail;
    private Button btRegisterByEmail;
    private TextView tvUserProtocolEmail;
    private CheckBox protocolCheckboxEmail;

    // 定义变量
    private String userName;
    private String passWord;
    private String valicationCode;
    public boolean isChange = false;
    private boolean tag = true;
    private int i = 60;
    private Thread thread=null;
    /**
     * 注册时所带的参数
     */
    private Map<String, Object> registerParams = new HashMap<String, Object>();
    /**
     * 注册是否成功
     */
    private AVUser user;
    private String tempPsd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        initView();
        ToastUtils.getInstanc(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ToastUtils.getInstanc(this).destory();
    }

    public void initView() {
        // 初始化控件
        tabHost = this.getTabHost();
        tab1 = tabHost.newTabSpec("手机号码注册").setIndicator("手机号码注册").setContent(R.id.reg_phone);
        tabHost.addTab(tab1);
        tab2 = tabHost.newTabSpec("邮箱注册").setIndicator("邮箱注册").setContent(R.id.reg_mail);
        tabHost.addTab(tab2);
        //手机注册控件
        etRegisterName = (EditText) findViewById(R.id.et_register_username_id);
        etCode = (EditText) findViewById(R.id.et_register_code_id);
        etPassword = (EditText) findViewById(R.id.et_register_password_id);
        btCode = (Button) findViewById(R.id.bt_getcode_id);
        btRegister = (Button) findViewById(R.id.bt_register_id);
        btRegister.setClickable(false);
        tvUserProtocol = (TextView) findViewById(R.id.user_protocol);
        protocolCheckbox=(CheckBox) findViewById(R.id.protocol_checkbox);
        //邮箱注册控件
        etRegisterEmailNum = (EditText) findViewById(R.id.et_register_emailnum_id);
        etPasswordEmail = (EditText) findViewById(R.id.et_register_password_email);
        btRegisterByEmail=(Button)findViewById(R.id.bt_register_by_email_id);
        btRegisterByEmail.setClickable(false);
        tvUserProtocolEmail = (TextView) findViewById(R.id.user_protocol_email);
        protocolCheckboxEmail =(CheckBox) findViewById(R.id.protocol_checkbox_email);

        // 初始化监听事件
        btCode.setOnClickListener(this);
        btRegister.setOnClickListener(this);
        btRegisterByEmail.setOnClickListener(this);
        tvUserProtocol.setOnClickListener(this);
        protocolCheckbox.setOnClickListener(this);
    }

    /**
     * 获取验证码前的验证
     *
     * @return
     */
    private boolean isvalidate() {
        // 获取控件输入的值
        userName = etRegisterName.getText().toString().trim();
        passWord = etPassword.getText().toString().trim();
        if (!StringUtils.isEmpty(userName)) {
            ToastUtils.getInstanc(this).showToast("手机号不能为空");
            return false;
        }
        if (!StringUtils.isPhoneNumberValid(userName)) {
            ToastUtils.getInstanc(this).showToast("手机号有误");
            return false;
        }
        return true;
    }

    /**
     * 邮箱注册前的验证
     * @return
     */
    public boolean isValidByemail(){
        userName = etRegisterEmailNum.getText().toString().trim();
        passWord = etPasswordEmail.getText().toString().trim();
        if (userName.equals("")) {
            ToastUtils.getInstanc(this).showToast("邮箱号不能为空!");
            return false;
        }
        if(!StringUtils.isEmailValid(userName)){
            ToastUtils.getInstanc(this).showToast("邮箱号无效");
            return false;
        }
        if (passWord.equals("")) {
            ToastUtils.getInstanc(this).showToast("密码不能为空!");
            return false;
        } else if (passWord.length() < 6) {
            ToastUtils.getInstanc(this).showToast("密码至少6位!");
            return false;
        }
        registerParams.put("username",userName);
        registerParams.put("psd",passWord);
        return true;
    }

    /**
     * 手机注册前的验证
     *
     * @return
     */
    public boolean isValid() {
        userName = etRegisterName.getText().toString().trim();
        valicationCode = etCode.getText().toString().trim();
        passWord = etPassword.getText().toString().trim();
        if (userName.equals("")) {
            ToastUtils.getInstanc(this).showToast("手机号不能为空!");
            return false;
        }
        if (valicationCode.equals("")) {
            ToastUtils.getInstanc(this).showToast("验证码不能为空!");
            return false;
        }
        if (passWord.equals("")) {
            ToastUtils.getInstanc(this).showToast("密码不能为空!");
            return false;
        } else if (passWord.length() < 6) {
            ToastUtils.getInstanc(this).showToast("密码至少6位!");
            return false;
        }
        registerParams.put("username", userName);
        registerParams.put("psd", passWord);
        registerParams.put("code", valicationCode);
        return true;
    }

    //获取验证码
    public boolean getValidateCode() {
        userName = etRegisterName.getText().toString().trim();
        registerParams.put("username", userName);
        registerParams.put("psd", passWord);

        Thread codeThread = new Thread(codeRunnable);
        codeThread.start();
        return true;
    }

    /**
     * 验证码按钮设置
     */
    private void changeBtnGetCode() {
        thread = new Thread() {
            @Override
            public void run() {
                if (tag) {
                    while (i > 0) {
                        i--;
                        if (RegisterActivity.this == null) {
                            break;
                        }
                        RegisterActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btCode.setText("获取验证码(" + i + ")");
                                btCode.setClickable(false);
                            }
                        });
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    tag = false;
                }
                i = 60;
                tag = true;
                if (RegisterActivity.this != null) {
                    RegisterActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btCode.setText("获取验证码");
                            btCode.setClickable(true);
                        }
                    });
                }
            }

            ;
        };
        thread.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_getcode_id:
                if (!isvalidate())
                    break;
                btCode.setText("获取验证码");
                btCode.setClickable(true);
                isChange = true;
                changeBtnGetCode();
                getValidateCode();
                break;
            case R.id.bt_register_id:
                register();
                break;
            case R.id.user_protocol:
                // Intent intentUserProtocol = new Intent(this,UserProtocolActivity.class);
                //startActivity(intentUserProtocol);
                break;
            // case R.id.register_back_login:
            // this.finish();
            // break;
            case R.id.protocol_checkbox:
                if(protocolCheckbox.isChecked()){
                    btRegister.setClickable(true);
                }else{
                    btRegister.setClickable(false);
                }
                break;
            case R.id.bt_register_by_email_id:
                registerByemail();
                break;
            case R.id.protocol_checkbox_email:
                if(protocolCheckboxEmail.isChecked()){
                    btRegisterByEmail.setClickable(true);
                }else{
                    btRegisterByEmail.setClickable(false);
                }
                break;
            default:
                break;
        }
    }

    private class HandlerByEmail extends Handler{
        private final WeakReference<RegisterActivity> mActivity;

        public HandlerByEmail(RegisterActivity activity) {
            mActivity = new WeakReference<RegisterActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            // 获取上下文对象
            RegisterActivity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case 1:
                        Toast.makeText(activity, "邮件已发送，请注意及时验证!", Toast.LENGTH_SHORT).show();
                        activity.finish();
                        break;
                    case -1:
                        Toast.makeText(activity, "注册失败!", Toast.LENGTH_SHORT).show();
                        break;
                    case -2:
                        Toast.makeText(activity, "该邮箱号已经注册!", Toast.LENGTH_SHORT).show();
                        break;
                    case 0:
                        Toast.makeText(activity, "哎呀,出错啦..", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        }
    }
    private final HandlerByEmail emailHandler = new HandlerByEmail(this);

    /**
     * 邮箱注册线程
     */
    private Runnable emailRunnable = new Runnable() {
        @Override
        public void run() {
            final Message msg = new Message();
            user = new AVUser();
            user.setUsername((String) registerParams.get("username"));
            user.setPassword((String) registerParams.get("psd"));
            user.setEmail((String) registerParams.get("username"));
            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(AVException e) {
                    if (e==null) {
                        msg.what = 1;
                    }else{
                        msg.what = 0;
                    }
                }
            });
            mHandler.sendMessage(msg);
        }
    };

    private void registerByemail() {
        if(isValidByemail()){
            Thread emailThread=new Thread(emailRunnable);
            emailThread.start();
        }
    }

    /**
     * 自定义一个静态的具有弱引用的Handler，解决内存泄漏的问题，注册使用
     */
    private class MyHandler extends Handler {
        // 持有对本外部类的弱引用
        private final WeakReference<RegisterActivity> mActivity;

        public MyHandler(RegisterActivity activity) {
            mActivity = new WeakReference<RegisterActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            // 获取上下文对象
            RegisterActivity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case 1:
                        Toast.makeText(activity, "注册成功!", Toast.LENGTH_SHORT).show();
                        activity.finish();
                        break;
                    case -1:
                        Toast.makeText(activity, "注册失败!", Toast.LENGTH_SHORT).show();
                        break;
                    case -2:
                        Toast.makeText(activity, "该号已经注册!", Toast.LENGTH_SHORT).show();
                        break;
                    case 0:
                        Toast.makeText(activity, "哎呀,出错啦..", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * 实例化自定义的handler
     */
    private final MyHandler mHandler = new MyHandler(this);

    /**
     * 验证手机验证码线程
     */
    private Runnable sRunnable = new Runnable() {
        @Override
        public void run() {
            final Message msg = new Message();
            AVUser.verifyMobilePhoneInBackground((String) registerParams.get("code"), new AVMobilePhoneVerifyCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null) {
                        msg.what = 1;
                    } else {
                        msg.what = 0;
                    }
                }
            });
            mHandler.sendMessage(msg);
        }
    };

    public void register() {
        if (isValid()) {
            Thread thread = new Thread(sRunnable);
            thread.start();
        }
    }

    /**
     * 自定义一个静态的具有弱引用的Handler，解决内存泄漏的问题,本handler用来获取验证码
     */
    private static class CodeHandler extends Handler {
        // 持有对本外部类的弱引用
        private final WeakReference<RegisterActivity> mActivity;

        public CodeHandler(RegisterActivity activity) {
            mActivity = new WeakReference<RegisterActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            // 获取上下文对象
            RegisterActivity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case 1:
                        Toast.makeText(activity, "验证短信已发送，请输入验证码", Toast.LENGTH_SHORT).show();
                        break;
                    case -1:
                        Toast.makeText(activity, "获取验证码失败!", Toast.LENGTH_SHORT).show();
                        break;
                    case 0:
                        Toast.makeText(activity, "哎呀,出错啦..", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * 实例化自定义的handler
     */
    private final CodeHandler codeHandler = new CodeHandler(this);
    /**
     * 定义获取验证码的子线程
     */
    private Runnable codeRunnable = new Runnable() {
        @Override
        public void run() {
            final Message msg = new Message();
            // 返回true则将消息的what值为1，为false则what为-1，异常为0
            user = new AVUser();
            user.setUsername((String) registerParams.get("username"));
            registerParams.put("tempPsd",DemoUtils.getRandomString(6));
            user.setPassword("123456");
            user.setMobilePhoneNumber((String) registerParams.get("username"));
            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null) {
                        msg.what = 1;
                    } else {
                        msg.what = 0;
                    }
                }
            });
            codeHandler.sendMessage(msg);
        }
    };
}
