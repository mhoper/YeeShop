package com.legendleo.yeeshop;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.legendleo.yeekoor.R;
import com.legendleo.yeeshop.adapter.ProductInfoPagerAdapter;
import com.legendleo.yeeshop.util.NetUtil;
import com.yeekoor.bean.CommonException;
import com.yeekoor.bean.ProductInfo;
import com.yeekoor.biz.ProductBiz;
import com.yeekoor.net.URLUtil;

public class ProductInfoActivity extends Activity {
	/**
	 * 产品ID
	 */
	private int pid;
	/**
	 * header
	 */
	private TextView headerText;
	private ProgressBar headerProgressBar;
	
	//产品相册加载中显示的图片
	private ImageView stubImg;
	
	private ViewPager viewPager;
	//viewpager的页角
	private TextView pagerText1, pagerText2;
	//viewpager的页角当前页
	private int currentPager = 0;
	//viewpager的页角总页数
	private int totalPagers = 0;
	
	private ProductInfoPagerAdapter pagerAdapter;
	
	
	private TextView productTitle, productNumber, productPrice, productVIPPrice;
	private TextView productComments, productConsult, productSize, productMaterial;
	private EditText productBuyNums;
	private RatingBar productRating;
	private String productDetailStr;
	private String productTitleStr;
	
	//图片地址、产品尺寸、产品材质数组
	private List<String> imgLinks = new ArrayList<String>();
	private String[] proSizes;
	private String[] proMaterials;
	
	private ProductBiz biz;
	private ProductInfo productInfo;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//去掉标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.product_info_activity);
		
		//接收pid
		Intent intent = getIntent();
		pid = intent.getIntExtra("pid", 0);
		//pid = 196;
		
		biz = new ProductBiz();
		init();
		
		pagerAdapter = new ProductInfoPagerAdapter(this);
		viewPager.setAdapter(pagerAdapter);
		viewPager.setCurrentItem(0);
		
		//设置viewpager滑动监听
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				currentPager = position + 1;
				//滑动时设置当前页数
				pagerText1.setText(currentPager + "");
			}
			
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				
			}
			
			@Override
			public void onPageScrollStateChanged(int state) {
				
			}
		});
		
		//隐藏虚拟键盘
		setSoftInputModeHidden();
		//获取网络数据
		new GetProductDataTask().execute();
	}
	
	//初始化
	private void init(){
		headerText = (TextView) findViewById(R.id.headerText);
		headerProgressBar = (ProgressBar) findViewById(R.id.headerProgressBar);
		stubImg = (ImageView) findViewById(R.id.stubImg);
		
		productTitle = (TextView) findViewById(R.id.productTitle);
		productNumber = (TextView) findViewById(R.id.productNumber);
		productPrice = (TextView) findViewById(R.id.productPrice);
		productVIPPrice = (TextView) findViewById(R.id.productVIPPrice);
		productRating =  (RatingBar) findViewById(R.id.productRatingBar);
		productComments = (TextView) findViewById(R.id.productComments);
		productConsult = (TextView) findViewById(R.id.productConsult);
		productSize = (TextView) findViewById(R.id.productSize);
		productMaterial = (TextView) findViewById(R.id.productMaterial);
		productBuyNums = (EditText) findViewById(R.id.productNums);
		
		pagerText1 = (TextView) findViewById(R.id.pagerText1);
		pagerText2 = (TextView) findViewById(R.id.pagerText2);
		viewPager = (ViewPager) findViewById(R.id.viewPager);
		//动态设置viewPager高等于屏幕宽
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		//其父容器为RelativeLayout所以此处须用RelativeLayout
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(dm.widthPixels, dm.widthPixels);
		System.out.println("screenwidth:" + dm.widthPixels);
		viewPager.setLayoutParams(lp);		
	}
	
	
	class GetProductDataTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			headerProgressBar.setVisibility(View.VISIBLE);
		}
		@Override
		protected Void doInBackground(Void... params) {
			try {
				if(NetUtil.CheckNet(ProductInfoActivity.this)){
					System.out.println("getProInfo --------------------------->>>>>>>>>> start");
					long startTime = System.currentTimeMillis();
					String temp = URLUtil.generateProductInfoUrl(pid);
					System.out.println("getProInfo --------------------------->>>>>>>>>> URL:" + temp);
					productInfo = biz.getProInfo(pid);
					System.out.println("getProInfo --------------------------->>>>>>>>>>> cost time:" + (System.currentTimeMillis() - startTime) + "ms");

					String[] arrImg = productInfo.getImgLinks();
					//viewpager总页数
					totalPagers = arrImg.length;
					
					if(totalPagers > 0){
						for (int i = 0; i < totalPagers; i++) {
							imgLinks.add(arrImg[i]);
							System.out.println("ImgLinks------------>>>>>>>>" + arrImg[i]);
						}
					}
				}
				
			} catch (CommonException e) {
				e.printStackTrace();
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			headerProgressBar.setVisibility(View.GONE);
			stubImg.setVisibility(View.GONE);
			
			if(productInfo != null){
				headerText.setText(productInfo.getTitle());
				productTitle.setText(productInfo.getTitle());
				productNumber.setText(productInfo.getProNumber());
				productPrice.setText(productInfo.getPrice());
				productVIPPrice.setText(productInfo.getVipPrice());
				productRating.setRating(productInfo.getProRating());
				productComments.setText(productInfo.getComments());
				productConsult.setText(productInfo.getConsults());
				if(productInfo.getProSizes() != null){
					proSizes = productInfo.getProSizes();
					//默认为第一个值
					productSize.setText(proSizes[0]);
				}
				if(productInfo.getProMaterial() != null){
					proMaterials = productInfo.getProMaterial();
					//默认为第一个值
					productMaterial.setText(proMaterials[0]);
				}
				//产品详情
				productDetailStr = productInfo.getProDetails();
				productTitleStr = productInfo.getTitle();
				

				pagerAdapter.addList(imgLinks);
				pagerAdapter.notifyDataSetChanged();
				
				//数据加载完后，设置当前页数为1，并设置总页数
				pagerText1.setText("1");
				pagerText2.setText(totalPagers + "");
			}
		}
	}

	//返回按钮，xml中定义
	public void back(View view) {
		// TODO Auto-generated method stub
		finish();
	}
	
	//点击查看商品详情
	public void proDetailClick(View view){
		Intent intent = new Intent(this, ProductDetailsFragmentActivity.class);
		intent.putExtra("currentTab", 0); //当前tab值
		intent.putExtra("pid", pid); //产品ID
		intent.putExtra("proTitle", productTitleStr);
		intent.putExtra("proDetail", productDetailStr); //产品详情页
		startActivity(intent);
	}
	
	//点击查看商品评论
	public void proCommentsClick(View view){
		Intent intent = new Intent(this, ProductDetailsFragmentActivity.class);
		intent.putExtra("currentTab", 1); //当前tab值
		intent.putExtra("pid", pid); //产品ID
		intent.putExtra("proTitle", productTitleStr);
		startActivity(intent);
		
	}
	
	//点击查看商品尺寸和材质
	public void proSizesClick(View view){
		Toast.makeText(this, "sizes", Toast.LENGTH_SHORT).show();
		
	}

	//取消虚拟键盘：进入页面时调用有效
	private void setSoftInputModeHidden(){
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}
	
	//取消虚拟键盘：点击搜索按钮时调用有效
	private void hideSoftInput(){
		
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if(imm != null){
//			imm.hideSoftInputFromInputMethod(getWindow().getDecorView().getWindowToken(), 0); //不起作用
			imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0); //有效，与下文语句作用一样
//			imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
		}
	}
}
