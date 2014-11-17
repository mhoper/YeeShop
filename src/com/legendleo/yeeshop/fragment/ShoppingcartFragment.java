package com.legendleo.yeeshop.fragment;

import com.legendleo.yeekoor.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ShoppingcartFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View shoppingcartView = inflater.inflate(R.layout.shoppingcart_fragment, container, false);
		return shoppingcartView;
	}
}
