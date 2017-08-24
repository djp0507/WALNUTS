package com.njjd.walnuts;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.retrofit.entity.SubjectPost;
import com.example.retrofit.listener.HttpOnNextListener;
import com.example.retrofit.subscribers.ProgressSubscriber;
import com.example.retrofit.util.JSONUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.njjd.adapter.FocusPeopleAdapter;
import com.njjd.adapter.FocusQuesAdapter;
import com.njjd.adapter.FocusTagAdapter;
import com.njjd.domain.FocusEntity;
import com.njjd.http.HttpManager;
import com.njjd.utils.ImmersedStatusbarUtils;
import com.njjd.utils.MyActivityManager;
import com.njjd.utils.SPUtils;
import com.njjd.utils.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by mrwim on 17/7/12.
 */

public class AttentionActivity extends BaseActivity {
    @BindView(R.id.back)
    TextView back;
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.list_mes)
    SwipeMenuListView listMes;
    @BindView(R.id.top)
    LinearLayout topView;
    private List<FocusEntity> userList = new ArrayList<>();
    private FocusPeopleAdapter peopleAdapter;
    @BindView(R.id.refresh)
    SwipeRefreshLayout refreshLayout;
    @Override
    public int bindLayout() {
        return R.layout.activity_attention;
    }

    @Override
    public void initView(View view) {
        ImmersedStatusbarUtils.initAfterSetContentView(this,topView);
        back.setText("关注");
        txtTitle.setText("我的粉丝");
        listMes.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        ToastUtils.showShortToast(AttentionActivity.this, "私信他");
                        break;
                    case 1:
                        ToastUtils.showShortToast(AttentionActivity.this, "关注他");
                        break;
                }
                return false;
            }
        });
        peopleAdapter = new FocusPeopleAdapter(userList, this);
        listMes.setAdapter(peopleAdapter);
        listMes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(AttentionActivity.this,PeopleInfoActivity.class);
                intent.putExtra("uid",userList.get(position).getId());
                startActivity(intent);
            }
        });
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                        FocusPeopleAdapter.CURRENT_PAGE=1;
                getFocusUser();
            }
        });
        listMes.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // 当不滚动时
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (view.getLastVisiblePosition() == view.getCount() - 1) {
                        FocusQuesAdapter.CURRENT_PAGE++;
                        getFocusUser();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                boolean enable = false;
                if(listMes != null && listMes.getChildCount() > 0){
                    boolean firstItemVisible = listMes.getFirstVisiblePosition() == 0;
                    boolean topOfFirstItemVisible = listMes.getChildAt(0).getTop() == 0;
                    enable = firstItemVisible && topOfFirstItemVisible;
                }
                refreshLayout.setEnabled(enable);
            }});
        getFocusUser();
    }
    private void getFocusUser() {
        Map<String, Object> map = new HashMap<>();
        map.put("uid", SPUtils.get(this, "userId", "").toString());
        map.put("token", SPUtils.get(this, "token", "").toString());
        map.put("page", FocusPeopleAdapter.CURRENT_PAGE);
        map.put("select","2");
        SubjectPost postEntity = new SubjectPost(new ProgressSubscriber(getFocusUserListener, this, false, false), map);
        HttpManager.getInstance().getFollowUser(postEntity);
    }

    HttpOnNextListener getFocusUserListener = new HttpOnNextListener() {
        @Override
        public void onNext(Object o) {
            refreshLayout.setRefreshing(false);
            if (!o.toString().equals("")) {
                JsonObject jsonObject = JSONUtils.getAsJsonObject(o);
                JsonArray array = JSONUtils.getAsJsonArray(jsonObject.get("user"));
                JsonObject object;
                if (array != null) {
                    if(FocusPeopleAdapter.CURRENT_PAGE==1){
                        userList.clear();
                    }
                    for (int i = 0; i < array.size(); i++) {
                        object = array.get(i).getAsJsonObject();
                        userList.add(new FocusEntity(object.get("uid").getAsString(), object.get("uname").getAsString(), object.get("headimgs").getAsString(),
                                object.get("add_time").getAsString(), object.get("introduction").getAsString()));
                    }
                    peopleAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @OnClick(R.id.back)
    public void onViewClicked() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
