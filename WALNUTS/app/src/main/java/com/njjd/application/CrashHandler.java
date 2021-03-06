package com.njjd.application;

import android.content.Context;

import com.njjd.utils.LogUtils;
import com.njjd.utils.ToastUtils;
import com.njjd.walnuts.LoginActivity;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by mrwim on 17/7/19.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler{
    // 需求是 整个应用程序 只有一个 MyCrash-Handler
    private static CrashHandler INSTANCE ;
    private Context context;

    //1.私有化构造方法
    private CrashHandler(){

    }

    public static synchronized CrashHandler getInstance(){
        if (INSTANCE == null)
            INSTANCE = new CrashHandler();
        return INSTANCE;
    }

    public void init(Context context){
        this.context = context;
    }


    public void uncaughtException(Thread arg0, Throwable arg1) {
        if(arg1.toString().contains("SSLSocketFactory")){
           return;
        }
        MobclickAgent.reportError(context,arg1);
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
