package com.njjd.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.retrofit.entity.SubjectPost;
import com.example.retrofit.listener.HttpOnNextListener;
import com.example.retrofit.subscribers.ProgressSubscriber;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.njjd.adapter.FindAnswerAdapter;
import com.njjd.application.ConstantsVal;
import com.njjd.domain.ColumnEntity;
import com.njjd.domain.SelectedAnswerEntity;
import com.njjd.http.HttpManager;
import com.njjd.utils.ImmersedStatusbarUtils;
import com.njjd.utils.LogUtils;
import com.njjd.utils.MyXRecyclerView;
import com.njjd.utils.SPUtils;
import com.njjd.utils.ToastUtils;
import com.njjd.walnuts.LoginActivity;
import com.njjd.walnuts.R;
import com.njjd.walnuts.SelectAnswerDetailActivity;
import com.njjd.walnuts.WelcomeActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mrwim on 17/9/14.
 */

public class FindFragment2 extends BaseFragment implements HttpOnNextListener{
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.list_find)
    MyXRecyclerView listFind;
    @BindView(R.id.back)
    TextView back;
    @BindView(R.id.top)
    LinearLayout top;
    private Context context;
    private FindAnswerAdapter adapter;
    private List<ColumnEntity> columnEntities=new ArrayList<>();
    private List<SelectedAnswerEntity> selectedAnswerEntities=new ArrayList<>();
    private SelectedAnswerEntity entity;
    private MyReceiver receiver;
    private boolean loadmoe=true;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getContext();
        View view = inflater.inflate(R.layout.fragment_find2, container, false);
        ButterKnife.bind(this, view);
        ImmersedStatusbarUtils.initAfterSetContentView(getActivity(),top);
        return view;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConstantsVal.REFRESH_FIND);
        context.registerReceiver(receiver, filter);
        back.setVisibility(View.GONE);
        txtTitle.setText("精选");
        columnEntities.add(new ColumnEntity("1","http://p.3761.com/pic/231432169575.jpg","核桃小编","超级大美女1","我被客户说服了怎么办？","http://up.qqjia.com/z/16/tu17317_45.png"));
        columnEntities.add(new ColumnEntity("2","http://p.3761.com/pic/231432169575.jpg","核桃小编","超级大美女2","我被客户说服了怎么办？","http://up.qqjia.com/z/16/tu17317_45.png"));
        adapter=new FindAnswerAdapter(context,selectedAnswerEntities,columnEntities);
        listFind.setLayoutManager(new LinearLayoutManager(context));
        listFind.setAdapter(adapter);
        listFind.setEmptyView(view.findViewById(R.id.empty));
        listFind.setLoadingMoreProgressStyle(ProgressStyle.SquareSpin);
        listFind.setRefreshProgressStyle(ProgressStyle.BallPulse);
        adapter.setOnItemClickListener(new FindAnswerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent;
                entity=selectedAnswerEntities.get(position);
                intent=new Intent(context, SelectAnswerDetailActivity.class);
                intent.putExtra("questionId",entity.getArticle_id());
                intent.putExtra("questionTitle",entity.getTitle());
                intent.putExtra("answer_id",entity.getAnswer_id());
                startActivity(intent);
            }
        });
        getSelectedAnswerList();
        listFind.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                adapter.setCurrentPage(1);
                getSelectedAnswerList();
            }

            @Override
            public void onLoadMore() {
                if(!loadmoe){
                    ToastUtils.showShortToast(context,"已加载全部数据啦");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            listFind.loadMoreComplete();
                        }
                    }, 500);
                    return;
                }
                adapter.setCurrentPage(adapter.getCurrentPage()+1);
                getSelectedAnswerList();
            }
        });
    }
    private void getSelectedAnswerList(){
        Map<String, Object> map = new HashMap<>();
        map.put("uid", SPUtils.get(context, "userId", "").toString());
        map.put("token", SPUtils.get(context, "token", "").toString());
        map.put("page", adapter.getCurrentPage());
        SubjectPost postEntity = new SubjectPost(new ProgressSubscriber(this, context, false, false), map);
        HttpManager.getInstance().getHotComment(postEntity);
    }

    @Override
    public void onNext(Object o) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls(); //重点
        Gson gson = gsonBuilder.create();
        JSONObject object = null;
        JSONArray array = null;
        SelectedAnswerEntity answerEntity;
        try {
            object=new JSONObject(gson.toJson(o));
            array=object.getJSONArray("comment");
            if(adapter.getCurrentPage()==1){
                columnEntities.clear();
                selectedAnswerEntities.clear();
                columnEntities.add(new ColumnEntity("1","http://p.3761.com/pic/231432169575.jpg","核桃小编","超级大美女1","我被客户说服了怎么办？","http://up.qqjia.com/z/16/tu17317_45.png"));
                columnEntities.add(new ColumnEntity("2","http://p.3761.com/pic/231432169575.jpg","核桃小编","超级大美女2","我被客户说服了怎么办？","http://up.qqjia.com/z/16/tu17317_45.png"));
                adapter.setColumnEntities(columnEntities);
                listFind.refreshComplete();
            }else{
                listFind.loadMoreComplete();
            }
            if(array.length()<20){
                loadmoe=false;
            }else{
                loadmoe=true;
            }
            for(int i=0;i<array.length();i++){
                object=array.getJSONObject(i);
                answerEntity=new SelectedAnswerEntity(object);
                selectedAnswerEntities.add(answerEntity);
            }
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class MyReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
                listFind.smoothScrollToPosition(0);
                listFind.setPullRefreshEnabled(true);
            adapter.setCurrentPage(1);
                listFind.refresh();
//            getSelectedAnswerList();
        }
    }
    @Override
    public void lazyInitData() {

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        context.unregisterReceiver(receiver);
    }
}
