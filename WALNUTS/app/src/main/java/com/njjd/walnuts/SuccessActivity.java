package com.njjd.walnuts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.example.retrofit.entity.SubjectPost;
import com.example.retrofit.listener.HttpOnNextListener;
import com.example.retrofit.subscribers.ProgressSubscriber;
import com.njjd.http.HttpManager;
import com.njjd.utils.GlideImageLoder;
import com.njjd.utils.ImmersedStatusbarUtils;
import com.njjd.utils.LogUtils;
import com.njjd.utils.SPUtils;
import com.njjd.utils.ToastUtils;
import com.yongchun.library.view.ImageSelectorActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by mrwim on 17/7/12.
 */

public class SuccessActivity extends BaseActivity {
    @BindView(R.id.img_head)
    CircleImageView imgHead;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.txt_province)
    TextView txtProvince;
    @BindView(R.id.txt_city)
    TextView txtCity;
    @BindView(R.id.txt_vocation)
    TextView txtVocation;
    @BindView(R.id.txt_sex)
    TextView txtSex;
    private List<String> provinces=new ArrayList<>();
    private List<String> citys=new ArrayList<>();
    private List<String> industrys1=new ArrayList<>();
    private List<List<String>> industrys2=new ArrayList<>();
    private OptionsPickerView<String> provincePickview,cityPickview,industryPickview,sexPickview;
    private int provinceId=0,cityId=0,industryId=0;
    @BindView(R.id.img_back)
    LinearLayout imgBack;
    private String path="";//头像地址
    private File file;
    @Override
    public int bindLayout() {
        return R.layout.activity_success;
    }
    @Override
    public void initView(View view) {
        ImmersedStatusbarUtils.initAfterSetContentView(this, imgBack);
        final ArrayList<String> sex = new ArrayList<>();
        sex.add("男");
        sex.add("女");
        sexPickview=new OptionsPickerView.Builder(SuccessActivity.this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                txtSex.setText(sex.get(options1));
            }
        }).build();
        sexPickview.setPicker(sex);
        if(getIntent().getIntExtra("bind",0)==1){
            //绑定账号情况，预先设置第三方的头像性别等
            GlideImageLoder.getInstance().displayImage(this,SPUtils.get(this,"thirdHead","").toString(),imgHead);
            etName.setText(SPUtils.get(this,"thirdName","").toString());
            txtSex.setText(SPUtils.get(this,"thirdSex","").toString());
            path=SPUtils.get(this,"thirdHead","").toString();
        }
        getProvinces();
//        getIndustry();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    private void getProvinces(){
        Map<String,String> map=new HashMap<>();
        SubjectPost postEntity = new SubjectPost(new ProgressSubscriber(new HttpOnNextListener<Object>(){
            @Override
            public void onNext(Object o) {
                provincePickview=new OptionsPickerView.Builder(SuccessActivity.this, new OptionsPickerView.OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int options1, int options2, int options3, View v) {
                        if(options1==0){
                            ToastUtils.showShortToast(SuccessActivity.this, "选择无效地址");
                            return;
                        }
                        provinceId=options1;
                        ToastUtils.showShortToast(SuccessActivity.this, provinces.get(options1));
                    }
                }).build();
                provincePickview.setPicker(provinces);
            }
        }, this,false, false), map);
        HttpManager.getInstance().provinceList(postEntity);
    }
    private void getIndustry(){
        Map<String,String> map=new HashMap<>();
        SubjectPost postEntity = new SubjectPost(new ProgressSubscriber(new HttpOnNextListener<Object>(){
            @Override
            public void onNext(Object o) {
                industryPickview=new OptionsPickerView.Builder(SuccessActivity.this, new OptionsPickerView.OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int options1, int options2, int options3, View v) {
                        ToastUtils.showShortToast(SuccessActivity.this, industrys1.get(options1)+"  "+industrys2.get(options1).get(options2));
                    }
                }).build();
                industryPickview.setPicker(industrys1,industrys2);
            }
        }, this,false, false), map);
        HttpManager.getInstance().industryList(postEntity);
    }
    private void getcitys(){
        Map<String,String> map=new HashMap<>();
        map.put("code",provinceId+"");
        SubjectPost postEntity = new SubjectPost(new ProgressSubscriber(new HttpOnNextListener<Object>(){
            @Override
            public void onNext(Object o) {
                cityPickview=new OptionsPickerView.Builder(SuccessActivity.this, new OptionsPickerView.OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int options1, int options2, int options3, View v) {
                        if(options1==0){
                            ToastUtils.showShortToast(SuccessActivity.this, "选择无效地址");
                            return;
                        }
                        ToastUtils.showShortToast(SuccessActivity.this, citys.get(options1));
                    }
                }).build();
                cityPickview.setPicker(citys);
                cityPickview.show();
            }
        }, this,false, false), map);
        HttpManager.getInstance().cityList(postEntity);
    }
    @OnClick({R.id.img_back,R.id.img_head, R.id.txt_province, R.id.txt_city, R.id.txt_vocation, R.id.txt_sex, R.id.txt_agreement, R.id.btn_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.img_head:
                //1多选 2 单选 单选才有裁剪功能
                ImageSelectorActivity.start(this,1,2, true, true, true);
                break;
            case R.id.txt_province:
//                provincePickview.show();
                break;
            case R.id.txt_city:
                if(provinceId==0){
                    ToastUtils.showShortToast(SuccessActivity.this, "请先选择有效省份");
                    return ;
                }
                citys.clear();
//                getcitys();
                break;
            case R.id.txt_vocation:
//                industryPickview.show();
                break;
            case R.id.txt_sex:
                sexPickview.show();
                break;
            case R.id.txt_agreement:
                Intent intent=new Intent(this,AgreementActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_submit:
                if(path.equals("")){
                    ToastUtils.showShortToast(this,"请先选择头像");
                    return;
                }
                if(provinceId==0||cityId==0){
                    ToastUtils.showShortToast(this,"请选择地区");
                    return;
                }
                if(industryId==0){
                    ToastUtils.showShortToast(this,"请选择行业");
                    return;
                }
//                completeInfo();
                break;
        }
    }
    private void completeInfo() {
        Map<String, String> map = new HashMap<>();
        map.put("uid", SPUtils.get(this,"userId","").toString());
        map.put("name",etName.getText().toString().trim());
        map.put("token",SPUtils.get(this,"token","").toString());
        map.put("province_id",provinceId+"");
        map.put("city_id", cityId+"");
        map.put("industry_id",industryId+"");
        map.put("sex",txtSex.getText().toString().equals("男")?"0":"1");
        map.put("message","");
        map.put("position","");
        if(file==null){
            map.put("headimg",path);
            SubjectPost postEntity = new SubjectPost(new ProgressSubscriber(this, this, false, false), map);
            HttpManager.getInstance().completeInfo(postEntity);
        }else {
            OkHttpClient client = new OkHttpClient();
            // form 表单形式上传
            MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
            if (file != null) {
                // MediaType.parse() 里面是上传的文件类型。
                RequestBody body = RequestBody.create(MediaType.parse("image/*"), file);
                String filename = file.getName();
                // 参数分别为， 请求key ，文件名称 ， RequestBody
                requestBody.addFormDataPart("headimg", file.getName(), body);
            }
            if (map != null) {
                for (Map.Entry entry : map.entrySet()) {
                    requestBody.addFormDataPart(entry.getKey().toString(),entry.getValue().toString());
                }
            }
            Request request = new Request.Builder().url(HttpManager.BASE_URL+"setUserInfo").post(requestBody.build()).tag(this).build();
            // readTimeout("请求超时时间" , 时间单位);
            client.newBuilder().readTimeout(5000, TimeUnit.MILLISECONDS).build().newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    LogUtils.d("lfq", "onFailure");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String str = response.body().string();
                        LogUtils.d("lfq", response.message() + " , body " + str);
                        ToastUtils.showShortToast(SuccessActivity.this,"完善成功");

                    } else {
                        LogUtils.d("lfq", response.message() + " error : body " + response.body().string());
                    }
                }
            });
        }
    }
    @Override
    public void onNext(Object o) {
        super.onNext(o);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == ImageSelectorActivity.REQUEST_IMAGE) {
            ArrayList<String> images = (ArrayList<String>) data.getSerializableExtra(ImageSelectorActivity.REQUEST_OUTPUT);
            file = new File(images.get(0));
            GlideImageLoder.getInstance().displayImage(this,images.get(0),imgHead);
        }
    }
//    private void uploadFile(){
//        SubjectPost postEntity = new SubjectPost(new ProgressSubscriber(upFileListener, this, true, false), file);
//        HttpManager.getInstance().uploadFile(postEntity, new MyUploadListener());
//    }
//    HttpOnNextListener upFileListener=new HttpOnNextListener() {
//        @Override
//        public void onNext(Object o) {
//            JsonObject object= JSONUtils.getAsJsonObject(o);
//            path=object.get("path").getAsString();
////            completeInfo();
//        }
//    };
//    class MyUploadListener implements ProgressListener {
//        @Override
//        public void onProgress(long progress, long total, boolean done) {
//        }
//    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
