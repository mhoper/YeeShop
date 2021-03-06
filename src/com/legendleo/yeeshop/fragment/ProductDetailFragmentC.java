package com.legendleo.yeeshop.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.legendleo.yeekoor.R;
import com.yeekoor.net.URLUtil;

public class ProductDetailFragmentC extends Fragment {
	//��ƷID
	private int pid;
	private WebView webView;
	private String url;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.product_detail_fragment_c, container, false);

		webView = (WebView) view.findViewById(R.id.CWebView);
		WebSettings webs = webView.getSettings();
		webs.setJavaScriptEnabled(true);
		webs.setUseWideViewPort(true);
		webs.setLoadWithOverviewMode(true);
		webs.setBuiltInZoomControls(true);
		webs.setDisplayZoomControls(false);
		
		webView.loadUrl(url);
		System.out.println("fragment onCreateView----------------->>>>>>>>>>>C");
		
		return view;
	}
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		
		if(url == null){
			Bundle bundle = getArguments();
			pid = bundle.getInt("pid", 0);
			url = URLUtil.generateProductConsultUrl(pid);
		}
		System.out.println("fragment setUserVisibleHint----------------->>>>>>>>>>>C load data");
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		System.out.println("fragment onDestroyView----------------->>>>>>>>>>>C");
	}
}
