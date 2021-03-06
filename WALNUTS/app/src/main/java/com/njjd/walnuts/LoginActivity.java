package com.njjd.walnuts;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.retrofit.entity.SubjectPost;
import com.example.retrofit.listener.HttpOnNextListener;
import com.example.retrofit.subscribers.ProgressSubscriber;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.njjd.application.ConstantsVal;
import com.njjd.http.HttpManager;
import com.njjd.utils.CommonUtils;
import com.njjd.utils.LogUtils;
import com.njjd.utils.SPUtils;
import com.njjd.utils.ToastUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by mrwim on 17/7/12.
 */

public class LoginActivity extends BaseActivity {
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.et_pwd)
    EditText etPwd;
    @BindView(R.id.btn_login)
    Button btnLogin;
    private UMShareAPI umShareAPI;
    private UMAuthListener authListener;
    private Map<String, String> thirdMap;
    public static Activity activity;
    @Override
    public int bindLayout() {
        return R.layout.activity_login;
    }

    @Override
    public void initView(View view) {
        activity=this;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        }
        if(SPUtils.get(this,"isFirst","0").equals("0")){
            SPUtils.put(this,"isFirst","1");
            startActivity(new Intent(this,AppIntroActivity.class));
            finish();
        }
        umShareAPI = UMShareAPI.get(this);
        etPhone.setText(SPUtils.get(this,"phoneNumber","").toString());
        etPwd.setText(SPUtils.get(this,"pwd","").toString());
        etPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
        etPhone.setSelection(etPhone.length());
        authListener = new UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {
                ToastUtils.showShortToast(LoginActivity.this, "授权开始");
            }
            @Override
            public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                thirdMap = map;
                doThirdLogin((share_media == SHARE_MEDIA.QQ ? 1 : share_media == SHARE_MEDIA.WEIXIN ? 2 : 3), map);
            }
            @Override
            public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                ToastUtils.showShortToast(LoginActivity.this, "授权错误");
            }
            @Override
            public void onCancel(SHARE_MEDIA share_media, int i) {
                ToastUtils.showShortToast(LoginActivity.this, "授权取消");
            }
        };
        etPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    etPhone.setTag(etPhone.getHint().toString());
                    etPhone.setHint("");
                }else{
                    etPhone.setHint(etPhone.getTag().toString());
                }
            }
        });
        etPwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    etPwd.setTag(etPwd.getHint().toString());
                    etPwd.setHint("");
                }else{
                    etPwd.setHint(etPwd.getTag().toString());
                }
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick({R.id.txt_misspwd, R.id.txt_register, R.id.btn_login, R.id.btn_sina, R.id.btn_wx, R.id.btn_qq})
    public void onViewClicked(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.txt_misspwd:
                intent = new Intent(this, ForgetPwdActivity.class);
                startActivity(intent);
                break;
            case R.id.txt_register:
                MobclickAgent.onEvent(this, ConstantsVal.REGISTER);
                intent = new Intent(this, RegisterActivity.class);
                intent.putExtra("bind", 0);
                startActivity(intent);
                break;
            case R.id.btn_login:
                if(etPhone.getText().toString().equals("")||etPwd.getText().toString().equals("")){
                    ToastUtils.showShortToast(this,"请输入账号和密码");
                    return;
                }
                MobclickAgent.onEvent(this,ConstantsVal.LOGIN);
                doLogin();
                break;
            case R.id.btn_sina:
                umShareAPI.getPlatformInfo(LoginActivity.this, SHARE_MEDIA.SINA, authListener);
                break;
            case R.id.btn_wx:
                if(!umShareAPI.isInstall(this, SHARE_MEDIA.WEIXIN)){
                    ToastUtils.showShortToast(this,"请先安装微信客户端");
                }
                umShareAPI.getPlatformInfo(LoginActivity.this, SHARE_MEDIA.WEIXIN, authListener);
                break;
            case R.id.btn_qq:
                if(!umShareAPI.isInstall(this, SHARE_MEDIA.QQ)){
                    ToastUtils.showShortToast(this,"请先安装QQ客户端");
                }
                umShareAPI.getPlatformInfo(LoginActivity.this, SHARE_MEDIA.QQ, authListener);
                break;
        }
    }

    private void doLogin() {
        LogUtils.d(PushAgent.getInstance(this).getRegistrationId());
        SPUtils.put(this,"deviceToken",PushAgent.getInstance(this).getRegistrationId());
        Map<String, Object> map = new HashMap<>();
        map.put("phone", etPhone.getText().toString().trim());
        map.put("pwd", etPwd.getText().toString().trim());
        map.put("device_token", SPUtils.get(this, "deviceToken", "").toString());
        SubjectPost postEntity = new SubjectPost(new ProgressSubscriber(this, this, true, false), map);
        HttpManager.getInstance().userLogin(postEntity);
    }
    @Override
    public void onNext(Object o) {
        super.onNext(o);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls(); //重点
        Gson gson = gsonBuilder.create();
        try {
            CommonUtils.initData(new JSONObject(gson.toJson(o)));
            SPUtils.put(LoginActivity.this,ConstantsVal.AUTOLOGIN,"true");
            SPUtils.put(LoginActivity.this,ConstantsVal.LOGINTYPE,"0");
            MobclickAgent.onEvent(LoginActivity.this,ConstantsVal.MAINACITVITY);
            SPUtils.put(LoginActivity.this,"pwd",etPwd.getText().toString().trim());
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void doThirdLogin(int type, Map<String, String> result) {
        MobclickAgent.onEvent(this,ConstantsVal.THIRD_LOGIN);
        //1 qq 2 wechat  3 sina
        Map<String, Object> map = new HashMap<>();
        map.put("logintype", "" + type);
        if(type==1||type==2) {
            map.put("uuid", result.get("unionid"));
        }else{
            map.put("uuid", result.get("uid"));
        }
        map.put("device_token", SPUtils.get(this, "deviceToken", "").toString());
        map.put("authimg",result.get("iconurl"));
        SPUtils.put(this,"authimg",result.get("iconurl").toString());
        SubjectPost postEntity = new SubjectPost(new ProgressSubscriber(thirdLoginListener, this, true, false), map);
        HttpManager.getInstance().thirdLogin(postEntity);
    }

    HttpOnNextListener thirdLoginListener = new HttpOnNextListener() {
        @Override
        public void onNext(Object o) {
            Intent intent;
//            JsonObject json = JSONUtils.getAsJsonObject(o);
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.serializeNulls(); //重点
            Gson gson = gsonBuilder.create();
            JSONObject json = null;
            MobclickAgent.onEvent(LoginActivity.this,ConstantsVal.THIRD_MAINACITVITY);
            try {
                json = new JSONObject(gson.toJson(o));
                SPUtils.put(LoginActivity.this, "uuid", json.isNull("uuid") ? "" : json.getString("uuid"));
                SPUtils.put(LoginActivity.this, "logintype", json.isNull("logintype") ? "" : json.getString("logintype"));
                if (json.toString().contains("code")) {
                    //用户未绑定
                    SPUtils.put(LoginActivity.this, "thirdName", thirdMap.get("name"));
                    SPUtils.put(LoginActivity.this, "thirdSex", thirdMap.get("gender"));
                    SPUtils.put(LoginActivity.this, "thirdHead", thirdMap.get("iconurl"));
                    SPUtils.put(LoginActivity.this, "thirdCity", thirdMap.get("city"));
                    SPUtils.put(LoginActivity.this, "thirdProvince", thirdMap.get("province"));
                    intent = new Intent(LoginActivity.this, BindActivity.class);
                    intent.putExtra("bind", 1);
                    startActivity(intent);
                    ToastUtils.showShortToast(LoginActivity.this, "请先绑定手机号");
                } else {
                    CommonUtils.initData(new JSONObject(gson.toJson(o)));
                    SPUtils.put(LoginActivity.this,ConstantsVal.AUTOLOGIN,"true");
                    SPUtils.put(LoginActivity.this,ConstantsVal.LOGINTYPE,"1");
                    intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }
}
