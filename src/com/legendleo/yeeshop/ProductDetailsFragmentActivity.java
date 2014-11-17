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
	//indicator�ƶ�ƫ����
	private int offset;
	
	private int currentTab = 0;
	//����fragment
	private List<Fragment> fragmentList;
	private ProductDetailFragmentA fragmentA;
	private ProductDetailFragmentB fragmentB;
	private ProductDetailFragmentC fragmentC;
	//fragmentPagerAdapter
	private ProductDetailFragmentPagerAdapter mFragmentPagerAdapter;
	
	//���ݵ�����
	private int pid;
	private String proDetail;
	private String proTitle = ""; //����ֵ����Ϊnull����������setText
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.product_details_fragment_activity);
		
		Intent intent = getIntent();
		//��ȡ��ǰTabλ��
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
		
		//����Ĭ��tab������ɫ
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
		//ȫ����գ�Ĭ�Ϻ�ɫ
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
		//��ȡ��Ļ��
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		//����indicator���
		int tabIndicatorWidth = dm.widthPixels / 3;
		//����tab����ʱƫ����
		offset = tabIndicatorWidth;
		
		//����indicator��ȱ�����tabTextһ��
		ViewGroup.LayoutParams lp = tabIndicator.getLayoutParams();
		lp.width = tabIndicatorWidth;
		tabIndicator.setLayoutParams(lp);
		
		//��ʼ��ʱ������ǰtab���ǵ�һ����������ƫ��
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
		
		//����Ʒ���鴫ֵ��fragmentA
		Bundle bundleA = new Bundle();
		bundleA.putInt("pid", pid);
		bundleA.putString("proDetail", proDetail);
		fragmentA.setArguments(bundleA);
		
		//��pid��ֵ��fragmentB��C
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
				//ֱ���л���û�л���Ч��
//				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(tabIndicator.getLayoutParams().width, tabIndicator.getLayoutParams().height);
//				lp.setMargins(offset * position, 0, 0, 0);
//				tabIndicator.setLayoutParams(lp);
				
				//����tab������ɫ
				setTabTextColor(position);
				
				
				//���û���Ч��,currentTabĬ��ֵΪ0
				Animation animation = new TranslateAnimation(offset*currentTab, offset*position, 0, 0);
				currentTab = position; //����ǰλ�ø���currentTab����һ�λ���ʱ���Ϊ��һ�ε�λ����
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

	//header���ذ�ť
	public void back(View view){
		finish();
	}
}
