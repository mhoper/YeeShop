package com.legendleo.yeeshop;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.legendleo.yeekoor.R;
import com.legendleo.yeeshop.adapter.ProductDetailFragmentPagerAdapter;
import com.legendleo.yeeshop.fragment.ProductDetailFragmentA;
import com.legendleo.yeeshop.fragment.ProductDetailFragmentB;
import com.legendleo.yeeshop.fragment.ProductDetailFragmentC;

public class ProductDetailsFragmentActivity extends FragmentActivity {

	//header
	private TextView headerText;
	//tabs
	private ViewPager mViewPager;
	private TextView tab1Text, tab2Text, tab3Text;
	private View tabIndicator;
	//indicator移动偏移量
	private int offset;
	
	private int currentTab = 0;
	//保存fragment
	private List<Fragment> fragmentList;
	private ProductDetailFragmentA fragmentA;
	private ProductDetailFragmentB fragmentB;
	private ProductDetailFragmentC fragmentC;
	//fragmentPagerAdapter
	private ProductDetailFragmentPagerAdapter mFragmentPagerAdapter;
	
	//传递的数据
	private int pid;
	private String proDetail;
	private String proTitle = ""; //赋空值以免为null，由于用于setText
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.product_details_fragment_activity);
		
		Intent intent = getIntent();
		//获取当前Tab位置
		currentTab = intent.getIntExtra("currentTab", 0);
		
		pid = intent.getIntExtra("pid", 0);
		proDetail = intent.getStringExtra("proDetail");
		proTitle = intent.getStringExtra("proTitle");

		initText();
		initIndicator();
		initViewPager();
		
	}
	
	
	private void initText() {
		headerText = (TextView) findViewById(R.id.headerText);
		headerText.setText(proTitle);
		
		tab1Text = (TextView) findViewById(R.id.tab1Text);
		tab2Text = (TextView) findViewById(R.id.tab2Text);
		tab3Text = (TextView) findViewById(R.id.tab3Text);
		
		tab1Text.setOnClickListener(new tabTextListener(0));
		tab2Text.setOnClickListener(new tabTextListener(1));
		tab3Text.setOnClickListener(new tabTextListener(2));
		
		//设置默认tab文字颜色
		setTabTextColor(currentTab);
	}
	
	class tabTextListener implements View.OnClickListener {
		int index = 0;
		public tabTextListener(int i){
			index = i;
		}
		@Override
		public void onClick(View v) {
			mViewPager.setCurrentItem(index);
		}
		
	}
	
	private void setTabTextColor(int index){
		//全部清空，默认黑色
		tab1Text.setTextColor(Color.BLACK);
		tab2Text.setTextColor(Color.BLACK);
		tab3Text.setTextColor(Color.BLACK);
		
		switch (index) {
		case 0:
			tab1Text.setTextColor(this.getResources().getColor(R.color.red));
			break;
			
		case 1:
			tab2Text.setTextColor(this.getResources().getColor(R.color.red));
			break;
			
		case 2:
			tab3Text.setTextColor(this.getResources().getColor(R.color.red));
			break;
		}
	}
	
	private void initIndicator() {
		tabIndicator = findViewById(R.id.tabIndicator);
		//获取屏幕宽
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		//计算indicator宽度
		int tabIndicatorWidth = dm.widthPixels / 3;
		//设置tab滑动时偏移量
		offset = tabIndicatorWidth;
		
		//设置indicator宽度保持与tabText一致
		ViewGroup.LayoutParams lp = tabIndicator.getLayoutParams();
		lp.width = tabIndicatorWidth;
		tabIndicator.setLayoutParams(lp);
		
		//初始化时，若当前tab不是第一个，则设置偏移
		if(currentTab != 0){
			Animation animation = new TranslateAnimation(0, tabIndicatorWidth * currentTab, 0, 0);
			animation.setFillAfter(true);
			animation.setDuration(0);
			tabIndicator.startAnimation(animation);
		}
		
	}
	
	private void initViewPager(){
		mViewPager = (ViewPager) findViewById(R.id.tabViewPager);
		//Set the number of pages that should be retained to either side of the current page
		//in the view hierarchy in an idle state.
		mViewPager.setOffscreenPageLimit(2);
		
		fragmentList = new ArrayList<Fragment>();
		
		fragmentA = new ProductDetailFragmentA();
		fragmentB = new ProductDetailFragmentB();
		fragmentC = new ProductDetailFragmentC();
		fragmentList.add(fragmentA);
		fragmentList.add(fragmentB);
		fragmentList.add(fragmentC);
		
		//将商品详情传值给fragmentA
		Bundle bundleA = new Bundle();
		bundleA.putInt("pid", pid);
		bundleA.putString("proDetail", proDetail);
		fragmentA.setArguments(bundleA);
		
		//将pid传值给fragmentB和C
		Bundle bundleBC = new Bundle();
		bundleBC.putInt("pid", pid);
		fragmentB.setArguments(bundleBC);
		fragmentC.setArguments(bundleBC);
		
		mFragmentPagerAdapter = new ProductDetailFragmentPagerAdapter(getSupportFragmentManager(), fragmentList);
		mViewPager.setAdapter(mFragmentPagerAdapter);
		mViewPager.setCurrentItem(currentTab);
		
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				//直接切换，没有滑动效果
//				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(tabIndicator.getLayoutParams().width, tabIndicator.getLayoutParams().height);
//				lp.setMargins(offset * position, 0, 0, 0);
//				tabIndicator.setLayoutParams(lp);
				
				//设置tab文字颜色
				setTabTextColor(position);
				
				
				//设置滑动效果,currentTab默认值为0
				Animation animation = new TranslateAnimation(offset*currentTab, offset*position, 0, 0);
				currentTab = position; //将当前位置赋给currentTab，下一次滑动时便成为上一次的位置了
				animation.setFillAfter(true);
				animation.setDuration(200);
				tabIndicator.startAnimation(animation);
				
			}
			
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				
			}
			
			@Override
			public void onPageScrollStateChanged(int state) {
				
			}
		});
		
	}

	//header返回按钮
	public void back(View view){
		finish();
	}
}
