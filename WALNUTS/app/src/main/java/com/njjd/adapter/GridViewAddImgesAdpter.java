package com.njjd.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.njjd.utils.GlideImageLoder;
import com.njjd.walnuts.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * com.bm.falvzixun.adapter.GridViewAddImgAdpter
 *
 * @author yuandl on 2015/12/24.
 *         添加上传图片适配器
 */
public class GridViewAddImgesAdpter extends BaseAdapter {
    private List<Map<String, Object>> datas;
    private Context context;
    private LayoutInflater inflater;
    private int type=1;
    /**
     * 可以动态设置最多上传几张，之后就不显示+号了，用户也无法上传了
     * 默认9张
     */
    private int maxImages = 9;

    public GridViewAddImgesAdpter(List<Map<String, Object>> datas, Context context,int type) {
        this.datas = datas;
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.type=type;
    }
    public int getDatasSize(){
        return datas==null?0:datas.size();
    }
    /**
     * 获取最大上传张数
     *
     * @return
     */
    public int getMaxImages() {
        return maxImages;
    }

    /**
     * 设置最大上传张数
     *
     * @param maxImages
     */
    public void setMaxImages(int maxImages) {
        this.maxImages = maxImages;
    }

    /**
     * 让GridView中的数据数目加1最后一个显示+号
     *
     * @return 返回GridView中的数量
     */
    @Override
    public int getCount() {
        int count = datas == null ? 1 : datas.size() + 1;
        if (count >= maxImages) {
            return datas.size();
        } else {
            return count;
        }
    }
    public List<String> getImgs(){
        List<String> imgs=new ArrayList<>();
        for(int i=0;i<datas.size();i++){
            imgs.add(datas.get(i).get("path").toString());
        }
        return imgs;
    }
    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void notifyDataSetChanged(List<Map<String, Object>> datas) {
        this.datas = datas;
        this.notifyDataSetChanged();

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_published_grida, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (datas != null && position < datas.size()) {
            GlideImageLoder.getInstance().displayImage(context,datas.get(position).get("path").toString(),viewHolder.ivimage);
            if(type==2){
                viewHolder.btdel.setVisibility(View.INVISIBLE);
            }else{
                viewHolder.btdel.setVisibility(View.VISIBLE);
            }
            viewHolder.btdel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final File file =  new File(datas.get(position).get("path").toString());
                    if (file.exists()) {
                        file.delete();
                    }
                    datas.remove(position);
                    notifyDataSetChanged();
                }
            });
        } else {
            Glide.with(context)
                    .load(R.drawable.image_add)
                    .priority(Priority.HIGH)
                    .centerCrop()
                    .into(viewHolder.ivimage);
            viewHolder.ivimage.setScaleType(ImageView.ScaleType.FIT_XY);
            viewHolder.btdel.setVisibility(View.GONE);
        }

        return convertView;

    }

    public class ViewHolder {
        public final ImageView ivimage;
        public final Button btdel;
        public final View root;

        public ViewHolder(View root) {
            ivimage = (ImageView) root.findViewById(R.id.iv_image);
            btdel = (Button) root.findViewById(R.id.bt_del);
            this.root = root;
        }
    }
}
