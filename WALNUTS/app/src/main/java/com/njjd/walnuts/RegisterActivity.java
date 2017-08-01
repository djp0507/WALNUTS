package com.njjd.walnuts;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.retrofit.entity.SubjectPost;
import com.example.retrofit.listener.HttpOnNextListener;
import com.example.retrofit.subscribers.ProgressSubscriber;
import com.example.retrofit.util.StringUtil;
import com.njjd.http.HttpManager;
import com.njjd.utils.BasePopupWindow;
import com.njjd.utils.ImmersedStatusbarUtils;
import com.njjd.utils.LogUtils;
import com.njjd.utils.SPUtils;
import com.njjd.utils.TimeCountDown;
import com.njjd.utils.ToastUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by mrwim on 17/7/12.
 */

public class RegisterActivity extends BaseActivity implements TimeCountDown.OnTimerCountDownListener{
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.et_phone_code)
    EditText etPhoneCode;
    @BindView(R.id.btn_next)
    Button btnNext;
    @BindView(R.id.img_back)
    LinearLayout imgBack;
    @BindView(R.id.txt_tip)
    TextView txtTip;
    @BindView(R.id.btn_get_code)
    TimeCountDown btnGetCode;
    private EditText etVerify;
    private ImageView imgCode;
    View lvImgcode;
    BasePopupWindow popupWindow;
    LayoutInflater inflater;
    private String code;
    String temp;

    @Override
    public int bindLayout() {
        return R.layout.activity_register;
    }

    @Override
    public void initView(View view) {
        inflater = LayoutInflater.from(this);
        lvImgcode = inflater.inflate(R.layout.lay_code, null);
        popupWindow = new BasePopupWindow(this);
        popupWindow.setContentView(lvImgcode);
        ImmersedStatusbarUtils.initAfterSetContentView(this, imgBack);
        etVerify = (EditText) lvImgcode.findViewById(R.id.et_verify);
        imgCode = (ImageView) lvImgcode.findViewById(R.id.img_code);
        imgCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Glide.with(RegisterActivity.this)
                        .load(HttpManager.BASE_URL + "getVerify?num=" + (int) (Math.random() * 10000) + "&phone=" + etPhone.getText().toString().trim())
                        .centerCrop()
                        .thumbnail(0.5f)
                        .dontAnimate()
                        .skipMemoryCache(true)
                        .into(imgCode);
            }
        });
        etVerify.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 4) {
                    code = etVerify.getText().toString().trim();
                    popupWindow.dismiss();
                    getPhoneCode();
                    etVerify.setText("");
                }
            }
        });
//        ok.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            }
//        });
        btnGetCode.setOnTimerCountDownListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @OnClick({R.id.btn_next, R.id.img_back, R.id.btn_get_code})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_next:
                if (!StringUtil.isPhoneNumber(etPhone.getText().toString().trim())) {
                    ToastUtils.showShortToast(this, "请输入正确的手机号");
                    return;
                }
                if(etPhoneCode.getText().toString().trim().equals("")){
                    ToastUtils.showShortToast(this, "请输入短信验证码");
                    return;
                }
                verifyPhone();
//                btnGetCode.initTimer();
                break;
            case R.id.btn_get_code:
                if (!StringUtil.isPhoneNumber(etPhone.getText().toString().trim())) {
                    ToastUtils.showShortToast(this, "请输入正确的手机号");
                    return;
                }
                if("".equals(code)) {
                    popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
                    Glide.with(this)
                            .load(HttpManager.BASE_URL + "getVerify?num=" + (int) (Math.random() * 10000) + "&phone=" + etPhone.getText().toString().trim())
                            .centerCrop()
                            .thumbnail(0.5f)
                            .dontAnimate()
                            .skipMemoryCache(true)
                            .into(imgCode);
                }else{
                    getPhoneCode();
                }
                break;
            case R.id.img_back:
                finish();
                break;
        }
    }

    private void verifyPhone() {
        Map<String, String> map = new HashMap<>();
        map.put("phone", etPhone.getText().toString().trim());
        map.put("imgcode", etPhoneCode.getText().toString().trim());
        SubjectPost postEntity = new SubjectPost(new ProgressSubscriber(verifyPhoneListener, this, false, false), map);
        HttpManager.getInstance().verifyPhone(postEntity);
    }

    HttpOnNextListener verifyPhoneListener = new HttpOnNextListener() {
        @Override
        public void onNext(Object o) {
            if (o != null)
                LogUtils.d(o.toString());
            SPUtils.put(RegisterActivity.this, "phoneNumber", etPhone.getText().toString().trim());
            Intent intent = null;
            if (getIntent().getIntExtra("isFind", 0) == 0) {
                intent = new Intent(RegisterActivity.this, SetPasswordActivity.class);
                intent.putExtra("bind", getIntent().getIntExtra("bind", 0));
            } else {
                intent = new Intent(RegisterActivity.this, ChangePwdActivity.class);
            }
            startActivity(intent);
            finish();
        }
    };

    private void getPhoneCode() {
        Map<String, String> map = new HashMap<>();
        map.put("phone", etPhone.getText().toString().trim());
        map.put("imgcode", code);
        LogUtils.d(map.toString());
        SubjectPost postEntity = new SubjectPost(new ProgressSubscriber(this, this, true, false), map);
        HttpManager.getInstance().phoneCode(postEntity);
    }

    @Override
    public void onNext(Object o) {
        super.onNext(o);
        btnGetCode.initTimer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        btnGetCode.cancel();
    }

    @Override
    public void onCountDownStart() {
        temp = getResources().getString(R.string.code);
        temp = String.format(temp, "15295532262");
        txtTip.setText(temp);
        txtTip.setVisibility(View.VISIBLE);
        btnGetCode.setEnabled(false);
        btnGetCode.setBackground(getResources().getDrawable(R.drawable.background_button_grey));
    }

    @Override
    public void onCountDownLoading(int currentCount) {

    }

    @Override
    public void onCountDownError() {

    }

    @Override
    public void onCountDownFinish() {
        txtTip.setVisibility(View.GONE);
        btnGetCode.setEnabled(true);
        btnGetCode.setBackground(getResources().getDrawable(R.drawable.background_button_div));
    }
}