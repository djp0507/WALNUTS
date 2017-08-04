package com.njjd.http;

import com.example.retrofit.entity.HttpResult;
import com.njjd.utils.SPUtils;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * service统一接口数据
 * Created by WMM on 2017/7/24.
 */
public interface HttpService {
    /**
     * 用户模块
     * 注册、登录、图形验证码、手机验证码、完善信息、第三方登录、找回密码、第三方绑定
     * @param params
     * @return
     */
    @POST("register")
    Observable<HttpResult<Object>> userRegister(@QueryMap Map<String, String> params);
    @POST("checkSmsCode")
    Observable<HttpResult<Object>> verifyPhone(@QueryMap Map<String, String> params);
    @POST("login")
    Observable<HttpResult<Object>> userLogin(@QueryMap Map<String, String> params);
    @POST("sendSms")
    Observable<HttpResult<Object>> phoneCode(@QueryMap Map<String, String> params);
    @POST("setUserInfo")
    Observable<HttpResult<Object>> completeInfo(@QueryMap Map<String, String> params);
    @POST("authlogin")
    Observable<HttpResult<Object>> thirdLogin(@QueryMap Map<String, String> params);
    @POST("authBind")
    Observable<HttpResult<Object>> authBind(@QueryMap Map<String, String> params);
    @POST("forgetPwd")
    Observable<HttpResult<Object>> setNewPwd(@QueryMap Map<String, String> params);
    /**
     * 社区模块
     * 首页 获取分类、获取问题列表、获取banner
     * 详情 获取回答、获取评论、获取回复、回答、回复、评论、收藏回答、关注问题
     * 提问 获取标签、提问
     */
    @POST("getNav")
    Observable<HttpResult<Object>> getNav(@QueryMap Map<String, String> params);
    @POST("getTag")
    Observable<HttpResult<Object>> getTag(@QueryMap Map<String, String> params);
    @POST("getQuestion")
    Observable<HttpResult<Object>> getQuestionList(@QueryMap Map<String, String> params);
    @POST("getAnswer")
    Observable<HttpResult<Object>> getAnswerList(@QueryMap Map<String, String> params);
    @POST("getComment")
    Observable<HttpResult<Object>> getCommentList(@QueryMap Map<String, String> params);
    @POST("pubQuestion")
    Observable<HttpResult<Object>> pubQuestion(@QueryMap Map<String, String> params);
    @POST("pubAnswer")
    Observable<HttpResult<Object>> pubAnswer(@QueryMap Map<String, String> params);
    @POST("pubComment")
    Observable<HttpResult<Object>> pubComment(@QueryMap Map<String, String> params);
    @POST("saveAnswer")
    Observable<HttpResult<Object>> saveAnswer(@QueryMap Map<String, String> params);
    @POST("focusQuestion")
    Observable<HttpResult<Object>> focusQuestion(@QueryMap Map<String, String> params);
    @POST("agreeAnswer")
    Observable<HttpResult<Object>> agreeAnswer(@QueryMap Map<String, String> params);
    @POST("getReply")
    Observable<HttpResult<Object>> getReplyList(@QueryMap Map<String, String> params);
    @POST("pubReply")
    Observable<HttpResult<Object>> pubReply(@QueryMap Map<String, String> params);
    /**
     * 通知消息模块
     */
    /**
     *  个人中心模块
     */
    /**
     * 公共模块 获取地址、销售模式、行业信息、上传图片
     * @return
     */
    @POST("getCity")
    Observable<HttpResult<Object>> provinceList(@QueryMap Map<String, String> params);
    @POST("getSalesModel")
    Observable<HttpResult<Object>> getSaleModel(@QueryMap Map<String, String> params);
    @POST("getIndustry")
    Observable<HttpResult<Object>> industryList(@QueryMap Map<String, String> params);
    @POST("fileUpload/uploadImage")
    @Multipart
    Observable<HttpResult<Object>> uploadFile(@PartMap Map<String, RequestBody> file);
    //当文件大时必须使用@streaming流
    @Streaming
    @GET
    Observable<ResponseBody> downloadFile(@Url String fileUrl);
}
