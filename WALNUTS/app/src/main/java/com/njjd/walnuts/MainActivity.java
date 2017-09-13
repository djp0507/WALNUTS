package com.njjd.walnuts;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;

import com.example.retrofit.entity.SubjectPost;
import com.example.retrofit.listener.HttpOnNextListener;
import com.example.retrofit.subscribers.ProgressSubscriber;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.njjd.application.AppAplication;
import com.njjd.application.ConstantsVal;
import com.njjd.http.HttpManager;
import com.njjd.utils.ImmersedStatusbarUtils;
import com.njjd.utils.LogUtils;
import com.njjd.utils.MyActivityManager;
import com.njjd.utils.NotificationUtils;
import com.njjd.utils.SPUtils;
import com.njjd.utils.TipButton;
import com.njjd.utils.ToastUtils;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author MrWim
 * @deprecated Created  on 17/7/12.
 */
public class MainActivity extends FragmentActivity {

    @BindView(R.id.radio4)
    TipButton radio4;
    private FragmentManager fm;
    private Fragment indexFragment, findFragment, messFragment, mineFragment, pubFragment;
    public static int temp = 0;
    public static Activity activity;
    private MyReceiver receiver;
    private LoginPassReceiver passReceiver;
    private JSONObject object;
    private String message="";
    private long exitTime=0;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            sendNotify();
        }
    };
    private Handler handler2=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            getUserInfoByOpenId(msg.getData().getString("uid"));
        }
    };
    private Handler handler3=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            radio4.setTipOn(true);
            radio4.invalidate();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        ButterKnife.bind(this);
        initView();
        MyActivityManager.getInstance().pushOneActivity(this);
        loginHuanxin();
    }

    private static void loginHuanxin() {
        EMClient.getInstance().login(SPUtils.get(activity, "userId", "").toString(), "Walnut2017", new EMCallBack() {
            @Override
            public void onSuccess() {
                LogUtils.d("环信即时登陆成功");
                /**
                 *  获取所有联系人
                 */
                EMClient.getInstance().chatManager().loadAllConversations();
                addListener();
            }

            @Override
            public void onError(int code, String error) {
                LogUtils.d("环信即时登陆失败");
            }

            @Override
            public void onProgress(int progress, String status) {

            }
        });
    }

    private static void addListener() {
        //注册一个监听连接状态的listener
        EMClient.getInstance().addConnectionListener(new MyConnectionListener());
    }

    private static class MyConnectionListener implements EMConnectionListener {
        @Override
        public void onConnected() {
            LogUtils.d("huanxin connect");
        }

        @Override
        public void onDisconnected(final int error) {
            new Runnable() {
                @Override
                public void run() {
                    if (error == EMError.USER_REMOVED) {
                        LogUtils.d("huanxin账号被移除");
                        // 显示帐号已经被移除
                    } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                        LogUtils.d("huanxin在另外一台设备登陆");
                        EMClient.getInstance().logout(true);
                        // 显示帐号在其他设备登录
                    }
                }
            };
        }
    }

    private void initView() {
        receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConstantsVal.NEW_INFORM);
        registerReceiver(receiver, filter);
        passReceiver = new LoginPassReceiver();
        IntentFilter filter1 = new IntentFilter();
        filter1.addAction(ConstantsVal.LOGIN_PASS);
        registerReceiver(passReceiver, filter1);
        fm = getSupportFragmentManager();
        indexFragment = fm.findFragmentById(R.id.index_fragment);
        findFragment = fm.findFragmentById(R.id.find_fragment);
        messFragment = fm.findFragmentById(R.id.mess_fragment);
        pubFragment = fm.findFragmentById(R.id.pub_fragment);
        mineFragment = fm.findFragmentById(R.id.my_fragment);
        fm.beginTransaction().hide(findFragment).hide(messFragment).hide(mineFragment).hide(pubFragment)
                .show(indexFragment).commit();
        initConversionLitener();
        if(EMClient.getInstance().chatManager().getUnreadMessageCount()>0){
            handler3.sendEmptyMessage(0);
        }
    }

    private void initConversionLitener() {
        EMClient.getInstance().chatManager().addMessageListener(new EMMessageListener() {

            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                if (AppAplication.isApplicationBroughtToBackground(AppAplication.getContext())) {
                    Message msg = new Message();
                    Bundle b = new Bundle();// 存放数据
                    b.putString("uid",messages.get(0).getFrom());
                    msg.setData(b);
                    handler2.sendMessage(msg); // 向Handler发送消息，更新UI
                    if (messages.get(0).getType() == EMMessage.Type.TXT) {
                        message=((EMTextMessageBody) messages.get(0).getBody()).getMessage();
                    } else if (messages.get(0).getType() == EMMessage.Type.IMAGE) {
                        message="[图片]";
                    } else if (messages.get(0).getType() == EMMessage.Type.VOICE) {
                        message="[语音]";
                    }
                    radio4.setTipOn(true);
                    radio4.invalidate();
                }
                EMClient.getInstance().chatManager().getConversation(messages.get(0).getFrom());
                EMClient.getInstance().chatManager().importMessages(messages);
                EMClient.getInstance().chatManager().loadAllConversations();
                Intent intent = new Intent();
                intent.setAction(ConstantsVal.MESSAGE_RECEIVE);
                sendBroadcast(intent);
                if(temp!=2){
                    handler3.sendEmptyMessage(0);
                }
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
                //收到透传消息
            }

            @Override
            public void onMessageRead(List<EMMessage> messages) {
                //收到已读回执
            }

            @Override
            public void onMessageDelivered(List<EMMessage> message) {
                //收到已送达回执
            }

            @Override
            public void onMessageChanged(EMMessage message, Object change) {
                //消息状态变动
            }
        });
    }
    private void getUserInfoByOpenId(final String uid) {
        Map<String, Object> map = new HashMap<>();
        map.put("uids", uid);
        map.put("uid", SPUtils.get(activity, "userId", ""));
        map.put("token", SPUtils.get(activity, "token", ""));
        SubjectPost postEntity = new SubjectPost(new ProgressSubscriber(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.serializeNulls(); //重点
                Gson gson = gsonBuilder.create();
                try {
                    JSONArray array = new JSONArray(gson.toJson(o));
                    object=array.getJSONObject(0);
                    handler.sendEmptyMessage(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, this, false, false), map);
        HttpManager.getInstance().getUserUids(postEntity);
    }
    private void sendNotify(){
        Intent intent = new Intent(AppAplication.getContext(), ChatActivity.class);
        try {
            intent.putExtra("openId",object.getString("uid"));
            intent.putExtra("name",object.isNull("uname") ? "未填写" : object.getString("uname"));
            intent.putExtra("avatar",object.isNull("headimg") ? "" : object.getString("headimg"));
            NotificationUtils.createNotif(AppAplication.getContext(), R.drawable.logo, "", object.isNull("uname") ? "未填写" :object.getString("uname"),message, intent, 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        unregisterReceiver(passReceiver);
        MyActivityManager.getInstance().popOneActivity(this);
    }

    @OnClick({R.id.radio1, R.id.radio2, R.id.radio3, R.id.radio4, R.id.radio5})
    public void onViewClicked(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.radio1:
                if(temp==0){
                    intent = new Intent();
                    intent.setAction(ConstantsVal.REFRESH);
                    sendBroadcast(intent);
                }else {
                    fm.beginTransaction().hide(findFragment).hide(messFragment).hide(mineFragment).hide(pubFragment)
                            .show(indexFragment)
                            .commitAllowingStateLoss();
                    temp = 0;
                }
                break;
            case R.id.radio2:
                fm.beginTransaction().hide(indexFragment).hide(messFragment).hide(mineFragment).hide(pubFragment)
                        .show(findFragment)
                        .commitAllowingStateLoss();
                temp = 1;
                break;
            case R.id.radio3:
                intent = new Intent(this, AskActivity.class);
                startActivity(intent);
                break;
            case R.id.radio4:
                fm.beginTransaction().hide(findFragment).hide(indexFragment).hide(mineFragment).hide(pubFragment)
                        .show(messFragment)
                        .commitAllowingStateLoss();
                temp = 2;
                radio4.setTipOn(false);
                radio4.invalidate();
                break;
            case R.id.radio5:
                fm.beginTransaction().hide(findFragment).hide(messFragment).hide(indexFragment).hide(pubFragment)
                        .show(mineFragment)
                        .commitAllowingStateLoss();
                temp = 3;
                break;
        }
        if (view.getId() == R.id.radio3) {
            switch (temp) {
                case 0:
                    ((RadioButton) findViewById(R.id.radio1)).setChecked(true);
                    break;
                case 1:
                    ((RadioButton) findViewById(R.id.radio2)).setChecked(true);
                    break;
                case 2:
                    ((RadioButton) findViewById(R.id.radio4)).setChecked(true);
                    break;
                case 3:
                    ((RadioButton) findViewById(R.id.radio5)).setChecked(true);
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.getSupportFragmentManager().findFragmentByTag("mine").onActivityResult(requestCode, resultCode, data);
    }

    private class MyReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            radio4.setTipOn(true);
            radio4.invalidate();
        }
    }
    private class LoginPassReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            MyActivityManager.getInstance().finishAllActivity();
            startActivity(new Intent(MainActivity.this,LoginActivity.class));
            ToastUtils.showShortToast(MainActivity.this,"用户登录已过期，请重新登录");
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
