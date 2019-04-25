package com.app.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.app.myapp.R;

import java.util.ArrayList;
import java.util.List;

public class GridViewAdapterXs extends BaseAdapter {

	private Context mContext;
	private List<Bitmap> list = new ArrayList<Bitmap>();

	public GridViewAdapterXs() {
		super();
	}
/**
 * 获取列表数据
 * @param list
 */
	public void setList(List<Bitmap> list){
		this.list = list;
		this.notifyDataSetChanged();
		Log.e(" 3333 ", this.list.size()+"");
	}

	public GridViewAdapterXs(Context mContext, List<Bitmap> list) {
		super();
		this.mContext = mContext;
		this.list = list;
		Log.e(" 2222 ", list.size()+"");
	}

	@Override
	public int getCount() {
		Log.e("  ", list.size()+"");
		return list == null ? 0 : list.size();
	}

	@Override
	public Object getItem(int position) {
		return list == null ? null : list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		GridViewAdapterXs.ViewHolder holder = null;
		if(convertView==null){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_published_grida_show, null);
			holder = new GridViewAdapterXs.ViewHolder();
			holder.item_grida_image = (ImageView) convertView.findViewById(R.id.item_grida_image_show);
			convertView.setTag(holder);
		}else{
			holder = (GridViewAdapterXs.ViewHolder) convertView.getTag();
		}

		if (isShowAddItem(position))
		{
//			holder.item_grida_image.setImageResource(R.drawable.btn_add_pic);
//			holder.item_grida_image.setBackgroundResource(R.color.bg_gray);

		}
		else
		{
			holder.item_grida_image.setImageBitmap(list.get(position));
			holder.item_grida_image.setBackgroundResource(R.color.bg_gray);
		}
		return convertView;
	}
	/**
	 * 判断当前下标是否是最大值
	 * @param position  当前下标
	 * @return
	 */
	private boolean isShowAddItem(int position)
	{
		int size = list == null ? 0 : list.size();
		return position == size;
	}

	class ViewHolder{
		ImageView item_grida_image;
	}

}
