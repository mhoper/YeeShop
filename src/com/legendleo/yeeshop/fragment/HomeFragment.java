package com.legendleo.yeeshop.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;

import com.legendleo.yeekoor.R;
import com.legendleo.yeeshop.ProductListActivity;

public class HomeFragment extends Fragment {

	private SearchView searchView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View homeView = inflater.inflate(R.layout.home_fragment, container, false);
		
		searchProduct(homeView);
		
		return homeView;
	}
	
	private void searchProduct(View view){
		searchView = (SearchView) view.findViewById(R.id.searchList);
		searchView.setSubmitButtonEnabled(true);
		
		//隐藏返回按钮
		View backIcon = view.findViewById(R.id.backIcon);
		backIcon.setVisibility(View.GONE);
		
		//修改searchView提示语的颜色，由于没有对应属性可设置，通过这种方式（dirty hacks?）
		int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
		TextView tv = (TextView) view.findViewById(id);
		tv.setHintTextColor(Color.LTGRAY); //设置提示文本颜色
		tv.setTextColor(Color.WHITE); //设置输入文本颜色
		
		searchView.setOnQueryTextListener(new OnQueryTextListener() {
			
			@Override
			public boolean onQueryTextSubmit(String query) {
				//跳转页面
				Intent intent = new Intent(getActivity(), ProductListActivity.class);
				intent.putExtra("searchKey", query);
				startActivity(intent);
				
				return true;
			}
			
			@Override
			public boolean onQueryTextChange(String newText) {
				// TODO Auto-generated method stub
				return false;
			}
		});
	}
}
