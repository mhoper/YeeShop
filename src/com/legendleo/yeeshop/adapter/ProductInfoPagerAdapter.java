package com.legendleo.yeeshop.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.legendleo.yeekoor.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
/**
 * ≤Œ’’universalimageloaderπŸ∑Ωsample
 * @author legendleo
 *
 */
public class ProductInfoPagerAdapter extends PagerAdapter {

	private LayoutInflater inflater;
	private List<String> lists = new ArrayList<String>();

	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	
	public ProductInfoPagerAdapter(Context context){
		inflater = LayoutInflater.from(context);

		options = new DisplayImageOptions.Builder()
					 .showImageForEmptyUri(R.drawable.image_default_big)
					 .showImageOnFail(R.drawable.image_default_big)
					 .resetViewBeforeLoading()
					 .cacheInMemory()
					 .cacheOnDisc()
					 .imageScaleType(ImageScaleType.EXACTLY)
					 .bitmapConfig(Bitmap.Config.RGB_565)
					 .displayer(new FadeInBitmapDisplayer(300))
					 .build();
	}
	
	public void addList(List<String> mData){
		lists.addAll(mData);
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return lists.size();
	}
	
	
	@Override
	public Object instantiateItem(ViewGroup view, int position) {
		View imageLayout = inflater.inflate(R.layout.product_info_pager_image_adpater, view, false);
		assert imageLayout != null;
		ImageView imageView = (ImageView) imageLayout.findViewById(R.id.productGallery);
		final ProgressBar progressBar = (ProgressBar) imageLayout.findViewById(R.id.imgLoadingPBar);
		
		System.out.println("ProductInfoPagerAdapter ------------->>>>>>>>instantiateItem listsize:" + lists.size());
		System.out.println("ProductInfoPagerAdapter ------------->>>>>>>>instantiateItem imgLink:" + lists.get(position));
		imageLoader.displayImage(lists.get(position), imageView, options, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				progressBar.setVisibility(View.VISIBLE);
			}

			@Override
			public void onLoadingComplete(String imageUri, View view,
					Bitmap loadedImage) {
				progressBar.setVisibility(View.GONE);
			}
		});
		
		view.addView(imageLayout);
		return imageLayout;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view.equals(object);
	}
	
	@Override
	public void restoreState(Parcelable state, ClassLoader loader) {
	}
	
	@Override
	public Parcelable saveState() {
		return null;
	}
	

}
