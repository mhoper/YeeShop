package com.legendleo.yeeshop.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ProductDetailFragmentPagerAdapter extends FragmentPagerAdapter {
	
	private List<Fragment> list;

	public ProductDetailFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
		super(fm);
		list = fragmentList;
	}

	@Override
	public Fragment getItem(int position) {
		return list.get(position);
	}

	@Override
	public int getCount() {
		return list.size();
	}

}
