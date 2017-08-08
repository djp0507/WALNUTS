package com.njjd.utils;

import android.content.Context;

import com.example.retrofit.entity.SubjectPost;
import com.example.retrofit.listener.HttpOnNextListener;
import com.example.retrofit.subscribers.ProgressSubscriber;
import com.example.retrofit.util.JSONUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.njjd.db.DBHelper;
import com.njjd.domain.CommonEntity;
import com.njjd.domain.IndexNavEntity;
import com.njjd.domain.TagEntity;
import com.njjd.http.HttpManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mrwim on 17/8/3.
 * 此类未公共类，启动项目时获取一些公共数据
 */

public class CommonUtils {
    private volatile static CommonUtils INSTANCE;
    private  static  Context mContext;
    //省份一级菜单
    private static List<String> provinces = new ArrayList<>();
    private static List<CommonEntity> provinceEntities = new ArrayList<>();
    //城市二级菜单
    private static List<List<CommonEntity>> cityList = new ArrayList<>();
    private static List<List<String>> cityEntities = new ArrayList<>();
    //行业一级菜单
    private static List<String> industrys1 = new ArrayList<>();
    private static List<CommonEntity> industryList1 = new ArrayList<>();
    //行业二级菜单
    private static List<List<String>> industrys2 = new ArrayList<>();
    private static List<List<CommonEntity>> industryList2 = new ArrayList<>();
    //销售模式一级菜单
    private static List<String> sales = new ArrayList<>();
    private static List<CommonEntity> saleList = new ArrayList<>();
    private static IndexNavEntity entity;
    private static List<TagEntity> tagsList=new ArrayList<>();
    private static List<IndexNavEntity> navList=new ArrayList<>();
    public static void init(Context context){
        mContext=context;
        //获取省份
        getProvinces();
        //获取行业
        getIndustry();
        //获取销售模式
        getSaleModel();
        //获取首页导航栏分类
        getNavInfo();
//        //获取标签分类
        getTagInfo();
    }
    //获取单例
    public static CommonUtils getInstance() {
        if (INSTANCE == null) {
            synchronized (CommonUtils.class) {
                if (INSTANCE == null) {
                    INSTANCE = new CommonUtils();
                }
            }
        }
        return INSTANCE;
    }
    private static void getProvinces() {
        Map<String, Object> map = new HashMap<>();
        SubjectPost postEntity = new SubjectPost(new ProgressSubscriber(new HttpOnNextListener<Object>() {
            @Override
            public void onNext(Object o) {
                JsonArray array = JSONUtils.getAsJsonArray(o);
                JsonObject object = null;
                JsonArray cityArray;
                JsonObject cityObject;
                CommonEntity entity;
                for (int i = 0; i < array.size(); i++) {
                    object = array.get(i).getAsJsonObject();
                    cityArray=object.get("citylist").getAsJsonArray();
                    List<CommonEntity> tempList=new ArrayList<>();
                    List<String> citys = new ArrayList<>();
                    for(int j=0;j<cityArray.size();j++){
                        cityObject=cityArray.get(j).getAsJsonObject();
                        entity = new CommonEntity(cityObject.get("id").getAsString(), cityObject.get("name").getAsString(), cityObject.get("code").getAsString());
                        citys.add(cityObject.get("name").getAsString());
                        tempList.add(entity);
                    }
                    cityList.add(tempList);
                    cityEntities.add(citys);
                    entity = new CommonEntity(object.get("id").getAsString(), object.get("name").getAsString(), object.get("code").getAsString());
                    provinceEntities.add(entity);
                    provinces.add(object.get("name").getAsString());
                }
            }
        }, mContext, false, false), map);
        HttpManager.getInstance().provinceList(postEntity);
    }
    private static void getSaleModel(){
        Map<String, Object> map = new HashMap<>();
        SubjectPost postEntity = new SubjectPost(new ProgressSubscriber(new HttpOnNextListener<Object>() {
            @Override
            public void onNext(Object o) {
                JsonArray array = JSONUtils.getAsJsonArray(o);
                JsonObject object = null;
                CommonEntity entity;
                for (int i = 0; i < array.size(); i++) {
                    object = array.get(i).getAsJsonObject();
                    entity = new CommonEntity(object.get("id").getAsString(), object.get("sales_name").getAsString(),"");
                    saleList.add(entity);
                    sales.add(object.get("sales_name").getAsString());
                }
            }
        }, mContext, false, false), map);
        HttpManager.getInstance().getSaleModel(postEntity);
    }
    private static void getIndustry() {
        Map<String, Object> map = new HashMap<>();
        SubjectPost postEntity = new SubjectPost(new ProgressSubscriber(new HttpOnNextListener<Object>() {
            @Override
            public void onNext(Object o) {
                LogUtils.d(o.toString());
                JsonArray array = JSONUtils.getAsJsonArray(o);
                JsonObject object = null;
                JsonArray cityArray;
                JsonObject cityObject;
                CommonEntity entity;
                for (int i = 0; i < array.size(); i++) {
                    object = array.get(i).getAsJsonObject();
                    cityArray=object.get("list").getAsJsonArray();
                    List<CommonEntity> tempList=new ArrayList<>();
                    List<String> temp = new ArrayList<>();
                    temp.clear();
                    tempList.clear();
                    for(int j=0;j<cityArray.size();j++){
                        cityObject=cityArray.get(j).getAsJsonObject();
                        entity = new CommonEntity(cityObject.get("id").getAsString(), cityObject.get("industry_name").getAsString(),"");
                        temp.add(cityObject.get("industry_name").getAsString());
                        tempList.add(entity);
                    }
                    industryList2.add(tempList);
                    industrys2.add(temp);
                    entity = new CommonEntity(object.get("id").getAsString(), object.get("industry_name").getAsString(),"");
                    industryList1.add(entity);
                    industrys1.add(object.get("industry_name").getAsString());
                }
            }
        }, mContext, false, false), map);
        HttpManager.getInstance().industryList(postEntity);
    }
    private static void getNavInfo(){
        Map<String,Object> map=new HashMap<>();
        SubjectPost postEntity = new SubjectPost(new ProgressSubscriber(new HttpOnNextListener<Object>() {
            @Override
            public void onNext(Object o) {
                LogUtils.d(o.toString());
                JsonArray array=JSONUtils.getAsJsonArray(o);
                JsonObject object;
                IndexNavEntity entity;
                for(int i=0;i<array.size();i++){
                    object=array.get(i).getAsJsonObject();
                    entity=new IndexNavEntity(object.get("id").getAsString(),object.get("cate_name").getAsString());
                    DBHelper.getInstance().getmDaoSession().getIndexNavEntityDao().insertOrReplace(entity);
                    navList.add(entity);
                }
            }
        }, mContext, false, false), map);
        HttpManager.getInstance().getNav(postEntity);
    }
    private static void getTagInfo(){
        Map<String,Object> map=new HashMap<>();
        SubjectPost postEntity = new SubjectPost(new ProgressSubscriber(new HttpOnNextListener<Object>() {
            @Override
            public void onNext(Object o) {
                LogUtils.d(o.toString());
                JsonArray array=JSONUtils.getAsJsonArray(o);
                JsonObject object;
                TagEntity entity;
                for(int i=0;i<array.size();i++){
                    object=array.get(i).getAsJsonObject();
                   entity=new TagEntity(object.get("id").getAsString(),object.get("label_name").getAsString(),object.get("level").getAsString());
                    tagsList.add(entity);
                    DBHelper.getInstance().getmDaoSession().getTagEntityDao().insertOrReplace(entity);
                }
            }
        }, mContext, false, false), map);
        HttpManager.getInstance().getTag(postEntity);
    }
    public static void initData(JSONObject json){
        try {
            SPUtils.put(mContext,"userId",json.isNull("uid")?"":json.getString("uid"));
            SPUtils.put(mContext, "phoneNumber", json.isNull("phone") ? "" : json.getString("phone"));
            SPUtils.put(mContext, "pwd", json.isNull("pwd") ? "" : json.getString("pwd"));
            SPUtils.put(mContext, "head", json.isNull("head") ? "" : json.getString("head"));
            SPUtils.put(mContext, "name", json.isNull("name") ? "" : json.getString("name"));
            SPUtils.put(mContext, "province", json.isNull("province_id") ? "" : json.getString("province_id"));
            SPUtils.put(mContext, "city", json.isNull("city_id") ? "" : json.getString("city_id"));
//              SPUtils.put(BindActivity.this, "company", json.isNull("company").getAsString());
            SPUtils.put(mContext, "position", json.isNull("position") ? "" : json.getString("position"));
            SPUtils.put(mContext, "industry", json.isNull("industry_id") ? "" : json.getString("industry_id"));
            SPUtils.put(mContext, "sales", json.isNull("sales_id") ? "" : json.getString("sales_id"));
            SPUtils.put(mContext, "token", json.getString("token"));
            SPUtils.put(mContext, "message", json.isNull("introduction") ? "" : json.getString("introduction"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public List<String> getProvincesList(){
        return provinces;
    }
    public List<List<String>> getCityEntities(){
        return cityEntities;
    }
    public List<CommonEntity> getProvinceEntities(){
        return provinceEntities;
    }
    public List<List<CommonEntity>> getCityList(){
        return cityList;
    }
    public List<String> getSales(){
        return sales;
    }
    public List<CommonEntity> getSaleList(){
        return saleList;
    }
    public List<String> getIndustrys1(){
        return industrys1;
    }
    public List<List<String>> getIndustrys2(){
        return industrys2;
    }
    public List<CommonEntity> getIndustryList1(){
        return industryList1;
    }
    public List<List<CommonEntity>> getIndustryList2(){
        return industryList2;
    }
    public List<TagEntity> getTagsList(){
        return DBHelper.getInstance().getmDaoSession().getTagEntityDao().loadAll();
    }
    public List<IndexNavEntity> getNavsList(){
        return DBHelper.getInstance().getmDaoSession().getIndexNavEntityDao().loadAll();
    }
}