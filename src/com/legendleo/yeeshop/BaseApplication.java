package com.legendleo.yeeshop;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.app.Application;

public class BaseApplication extends Application {

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		ImageLoaderConfiguration config = ImageLoaderConfiguration.createDefault(getApplicationContext());
		ImageLoader.getInstance().init(config);
	}
}
