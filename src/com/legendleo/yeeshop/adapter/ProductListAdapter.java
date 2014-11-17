package com.legendleo.yeeshop.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.legendleo.yeekoor.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.yeekoor.bean.ProductList;

public class ProductListAdapter extends BaseAdapter {
	private List<ProductList> lists;
	private LayoutInflater inflater;
	
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	
	public ProductListAdapter(Context context){
		inflater = LayoutInflater.from(context);
		this.lists = new ArrayList<ProductList>();
		
		options = new DisplayImageOptions.Builder()
					  .showStubImage(R.drawable.image_default_big)
					  .showImageForEmptyUri(R.drawable.image_default_big)
					  .showImageOnFail(R.drawable.image_default_big)
					  .cacheInMemory()
					  .cacheOnDisc()
					  .imageScaleType(ImageScaleType.EXACTLY)
					  .bitmapConfig(Bitmap.Config.RGB_565)
					  .displayer(new FadeInBitmapDisplayer(300))
					  .build();
	}

	public void addList(List<ProductList> mData) {
		this.lists.addAll(mData);
	}
	
	public void setList(List<ProductList> mData) {
		this.lists.clear();
		this.lists.addAll(mData);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return lists.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return lists.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		System.out.println("Adapter listsSize----------------------------->:" + lists.size());
		ViewHolder holder = null;
		if(null == convertView){
			convertView = inflater.inflate(R.layout.product_list_adapter, parent, false);
			holder = new ViewHolder();
			
			holder.mImageView = (ImageView) convertView.findViewById(R.id.productImg);
			holder.mTVTitle = (TextView) convertView.findViewById(R.id.productTitle);
			holder.mTVPrice = (TextView) convertView.findViewById(R.id.productPrice);
			holder.mTVSales = (TextView) convertView.findViewById(R.id.productSales);
			holder.mTVComments = (TextView) convertView.findViewById(R.id.productComments);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		if(lists != null){
			imageLoader.displayImage(lists.get(position).getImgLink(), holder.mImageView, options);
			holder.mTVTitle.setText(lists.get(position).getTitle());
			holder.mTVPrice.setText(lists.get(position).getPrice());
			holder.mTVSales.setText(lists.get(position).getSales());
			holder.mTVComments.setText(lists.get(position).getComments());
		}
		return convertView;
	}

	static class ViewHolder{
		ImageView mImageView;
		TextView mTVTitle;
		TextView mTVPrice;
		TextView mTVSales;
		TextView mTVComments;
	}

}
