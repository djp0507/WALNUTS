package com.njjd.walnuts;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.njjd.adapter.GridViewAddImgesAdpter;
import com.njjd.domain.QuestionEntity;
import com.njjd.utils.AndroidBug5497Workaround;
import com.njjd.utils.CommonUtils;
import com.njjd.utils.LogUtils;
import com.njjd.utils.PhotoUtil;
import com.njjd.utils.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.valuesfeng.picker.Picker;
import io.valuesfeng.picker.engine.GlideEngine;
import io.valuesfeng.picker.utils.PicturePickerUtils;

/**
 * Created by mrwim on 17/7/12.
 */

public class AskActivity extends BaseActivity {
    @BindView(R.id.back)
    TextView back;
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.top)
    LinearLayout top;
    @BindView(R.id.et_title)
    EditText etTitle;
    @BindView(R.id.et_content2)
    EditText etContent;
    private GridView gw;
    private List<Map<String, Object>> datas;
    private GridViewAddImgesAdpter gridViewAddImgesAdpter;
    private  ArrayList<String> images=new ArrayList<>();
    public static Activity activity;
    private QuestionEntity questionEntity=null;

    @Override
    public int bindLayout() {
        return R.layout.activity_ask;
    }

    @Override
    public void initView(View view) {
        back.setText("返回");
        gw = (GridView) findViewById(R.id.gw);
        datas = new ArrayList<>();
        if("2".equals(getIntent().getStringExtra("type"))){
            questionEntity=(QuestionEntity) getIntent().getBundleExtra("question").get("question");
            txtTitle.setText("编辑问题");
            etTitle.setText(questionEntity.getTitle());
            etTitle.setSelection(questionEntity.getTitle().length());
            etContent.setText(questionEntity.getContent());
            etContent.setSelection(questionEntity.getContent().length());
            Map<String, Object> map;
            if(!"".equals(questionEntity.getPhoto())){
                final String[] imgs = questionEntity.getPhoto().split(",");
                for(int i=0;i<imgs.length;i++){
                    map = new HashMap<>();
                    map.put("path",imgs[i].replace("\"",""));
                    datas.add(map);
                }
            }
            gridViewAddImgesAdpter = new GridViewAddImgesAdpter(datas, this,2);
        }else{
            txtTitle.setText("提问");
            gridViewAddImgesAdpter = new GridViewAddImgesAdpter(datas, this,1);
        }
        gw.setAdapter(gridViewAddImgesAdpter);
        gw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if(questionEntity!=null){
                    ToastUtils.showShortToast(AskActivity.this,"不可修改图片");
                    return;
                }
                Picker.from(AskActivity.this)
                        .count(10-gridViewAddImgesAdpter.getCount())
                        .enableCamera(true)
                        .setEngine(new GlideEngine())
                        .forResult(REQUEST_CODE);
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity=this;
    }
    @OnClick({R.id.back,R.id.btn_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.btn_submit:
                if(etTitle.getText().toString().equals("")){
                    ToastUtils.showShortToast(this,"请输入问题标题");
                    return;
                }
                if(etContent.getText().toString().equals("")){
                    ToastUtils.showShortToast(this,"请输入问题描述");
                    return;
                }
                if(etTitle.getText().toString().trim().length()<5||etTitle.getText().toString().trim().length()>=50){
                    ToastUtils.showShortToast(this,"标题至少5个字");
                    return;
                }
                for(int i=0;i<datas.size();i++){
                    images.add(datas.get(i).get("path").toString());
                }
                Intent intent=new Intent(this,AskKindActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("title",etTitle.getText().toString().trim());
                bundle.putString("content",etContent.getText().toString().trim());
                bundle.putStringArrayList("imgs",images);
                intent.putExtra("question",bundle);
                if(questionEntity!=null){
                    intent.putExtra("type","2");
                    intent.putExtra("ids",questionEntity.getTag_id());
                    intent.putExtra("article_id",questionEntity.getQuestionId());
                }else{
                    intent.putExtra("type","1");
                }
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            List<Uri> mSelected = PicturePickerUtils.obtainResult(data);
            for (int i = 0; i < mSelected.size(); i++) {
                Map<String, Object> map = new HashMap<>();
                String imgpath= PhotoUtil.saveMyBitmapWH(CommonUtils.getRealPathFromUri(this,mSelected.get(i)), 480,800);
                map.put("path",imgpath);
                datas.add(map);
            }
            gridViewAddImgesAdpter.setMaxImages(9);
            gridViewAddImgesAdpter.notifyDataSetChanged();
        }
    }
}
