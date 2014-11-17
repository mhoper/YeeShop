package com.legendleo.yeeshop;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.legendleo.yeekoor.R;
import com.legendleo.yeeshop.fragment.CategoryFragment;
import com.legendleo.yeeshop.fragment.CenterFragment;
import com.legendleo.yeeshop.fragment.HomeFragment;
import com.legendleo.yeeshop.fragment.ShoppingcartFragment;

public class MainActivity extends Activity implements OnClickListener {

	private HomeFragment homeFragment;
	private CategoryFragment categoryFragment;
	private ShoppingcartFragment shoppingcartFragment;
	private CenterFragment centerFragment;
	
	private View homeLayout;
	private View categoryLayout;
	private View shoppingcartLayout;
	private View centerLayout;
	
	private TextView homeText;
	private TextView categoryText;
	private TextView shoppingcartText;
	private TextView centerText;
	private Drawable textImg;
	
	private FragmentManager fragmentManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		initView();
		
		fragmentManager = getFragmentManager();
		setTabSelection(0);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		System.out.println("*****************************onSaveInstanceState***********************");
		// 由于按home后程序可能被系统kill，重新返回应用程序后fragment会发生重叠
		// 此处注释掉暂时可解决此问题
		//super.onSaveInstanceState(outState);
		
		//可在此保存各fragment的状态（isAdded()/isHidden()）
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
	}

	//初始化
	private void initView() {
		homeLayout = findViewById(R.id.home_layout);
		categoryLayout = findViewById(R.id.category_layout);
		shoppingcartLayout = findViewById(R.id.shoppingcart_layout);
		centerLayout = findViewById(R.id.center_layout);
		
		homeText = (TextView) findViewById(R.id.home_text);
		categoryText = (TextView) findViewById(R.id.category_text);
		shoppingcartText = (TextView) findViewById(R.id.shoppingcart_text);
		centerText = (TextView) findViewById(R.id.center_text);
		
		homeLayout.setOnClickListener(this);
		categoryLayout.setOnClickListener(this);
		shoppingcartLayout.setOnClickListener(this);
		centerLayout.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.home_layout:
			setTabSelection(0);
			break;

		case R.id.category_layout:
			setTabSelection(1);
			break;
			
		case R.id.shoppingcart_layout:
			setTabSelection(2);
			break;
			
		case R.id.center_layout:
			setTabSelection(3);
			break;
			
		default:
			break;
		}
		
	}

	private void setTabSelection(int index) {
		//清除上一次的选择
		clearSelection();
		//开启一个Fragment事务
		FragmentTransaction transaction = fragmentManager.beginTransaction(); 
		//先隐掉所有Fragment
		hideFragments(transaction);
		
		switch (index) {
		case 0:
			//改变文字颜色和对应的图片
			homeText.setTextColor(Color.WHITE);
			//使用了Textview的drawableTop属性，这里要先调用setBounds方法，再调用setCompoundDrawables方法
			textImg = getResources().getDrawable(R.drawable.home_selected);
			textImg.setBounds(0, 0, textImg.getIntrinsicWidth(), textImg.getIntrinsicHeight());
			homeText.setCompoundDrawables(null, textImg, null, null);
			
			if(homeFragment == null){
				homeFragment = new HomeFragment();
				transaction.add(R.id.content, homeFragment);
			}else{
				transaction.show(homeFragment);
			}
			break;

		case 1:
			categoryText.setTextColor(Color.WHITE);
			textImg = getResources().getDrawable(R.drawable.category_selected);
			textImg.setBounds(0, 0, textImg.getIntrinsicWidth(), textImg.getIntrinsicHeight());
			categoryText.setCompoundDrawables(null, textImg, null, null);
			
			if(categoryFragment == null){
				categoryFragment = new CategoryFragment();
				transaction.add(R.id.content, categoryFragment);
			}else{
				transaction.show(categoryFragment);
			}
			break;
			
		case 2:
			shoppingcartText.setTextColor(Color.WHITE);
			textImg = getResources().getDrawable(R.drawable.shoppingcart_selected);
			textImg.setBounds(0, 0, textImg.getIntrinsicWidth(), textImg.getIntrinsicHeight());
			shoppingcartText.setCompoundDrawables(null, textImg, null, null);
			
			if(shoppingcartFragment == null){
				shoppingcartFragment = new ShoppingcartFragment();
				transaction.add(R.id.content, shoppingcartFragment);
			}else{
				transaction.show(shoppingcartFragment);
			}
			break;
			
		case 3:
			centerText.setTextColor(Color.WHITE);
			textImg = getResources().getDrawable(R.drawable.center_selected);
			textImg.setBounds(0, 0, textImg.getIntrinsicWidth(), textImg.getIntrinsicHeight());
			centerText.setCompoundDrawables(null, textImg, null, null);
			
			if(centerFragment == null){
				centerFragment = new CenterFragment();
				transaction.add(R.id.content, centerFragment);
			}else{
				transaction.show(centerFragment);
			}
			break;
			
		default:
			break;
		}
		transaction.commit();
		
	}

	//清除底部所有选择项
	private void clearSelection() {
		homeText.setTextColor(getResources().getColor(R.color.textColor));
		textImg = getResources().getDrawable(R.drawable.home_unselected);
		textImg.setBounds(0, 0, textImg.getIntrinsicWidth(), textImg.getIntrinsicHeight());
		homeText.setCompoundDrawables(null, textImg, null, null);
		
		categoryText.setTextColor(getResources().getColor(R.color.textColor));
		textImg = getResources().getDrawable(R.drawable.category_unselected);
		textImg.setBounds(0, 0, textImg.getIntrinsicWidth(), textImg.getIntrinsicHeight());
		categoryText.setCompoundDrawables(null, textImg, null, null);
		
		shoppingcartText.setTextColor(getResources().getColor(R.color.textColor));
		textImg = getResources().getDrawable(R.drawable.shoppingcart_unselected);
		textImg.setBounds(0, 0, textImg.getIntrinsicWidth(), textImg.getIntrinsicHeight());
		shoppingcartText.setCompoundDrawables(null, textImg, null, null);
		
		centerText.setTextColor(getResources().getColor(R.color.textColor));
		textImg = getResources().getDrawable(R.drawable.center_unselected);
		textImg.setBounds(0, 0, textImg.getIntrinsicWidth(), textImg.getIntrinsicHeight());
		centerText.setCompoundDrawables(null, textImg, null, null);
	}

	//隐藏所有Fragment
	private void hideFragments(FragmentTransaction transaction) {
		if(homeFragment != null){
			transaction.hide(homeFragment);
		}
		if(categoryFragment != null){
			transaction.hide(categoryFragment);
		}
		if(shoppingcartFragment != null){
			transaction.hide(shoppingcartFragment);
		}
		if(centerFragment != null){
			transaction.hide(centerFragment);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
