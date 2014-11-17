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
	 * ��ƷID
	 */
	private int pid;
	/**
	 * header
	 */
	private TextView headerText;
	private ProgressBar headerProgressBar;
	
	//��Ʒ����������ʾ��ͼƬ
	private ImageView stubImg;
	
	private ViewPager viewPager;
	//viewpager��ҳ��
	private TextView pagerText1, pagerText2;
	//viewpager��ҳ�ǵ�ǰҳ
	private int currentPager = 0;
	//viewpager��ҳ����ҳ��
	private int totalPagers = 0;
	
	private ProductInfoPagerAdapter pagerAdapter;
	
	
	private TextView productTitle, productNumber, productPrice, productVIPPrice;
	private TextView productComments, productConsult, productSize, productMaterial;
	private EditText productBuyNums;
	private RatingBar productRating;
	private String productDetailStr;
	private String productTitleStr;
	
	//ͼƬ��ַ����Ʒ�ߴ硢��Ʒ��������
	private List<String> imgLinks = new ArrayList<String>();
	private String[] proSizes;
	private String[] proMaterials;
	
	private ProductBiz biz;
	private ProductInfo productInfo;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//ȥ������
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.product_info_activity);
		
		//����pid
		Intent intent = getIntent();
		pid = intent.getIntExtra("pid", 0);
		//pid = 196;
		
		biz = new ProductBiz();
		init();
		
		pagerAdapter = new ProductInfoPagerAdapter(this);
		viewPager.setAdapter(pagerAdapter);
		viewPager.setCurrentItem(0);
		
		//����viewpager��������
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				currentPager = position + 1;
				//����ʱ���õ�ǰҳ��
				pagerText1.setText(currentPager + "");
			}
			
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				
			}
			
			@Override
			public void onPageScrollStateChanged(int state) {
				
			}
		});
		
		//�����������
		setSoftInputModeHidden();
		//��ȡ��������
		new GetProductDataTask().execute();
	}
	
	//��ʼ��
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
		//��̬����viewPager�ߵ�����Ļ��
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		//�丸����ΪRelativeLayout���Դ˴�����RelativeLayout
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
					//viewpager��ҳ��
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
					//Ĭ��Ϊ��һ��ֵ
					productSize.setText(proSizes[0]);
				}
				if(productInfo.getProMaterial() != null){
					proMaterials = productInfo.getProMaterial();
					//Ĭ��Ϊ��һ��ֵ
					productMaterial.setText(proMaterials[0]);
				}
				//��Ʒ����
				productDetailStr = productInfo.getProDetails();
				productTitleStr = productInfo.getTitle();
				

				pagerAdapter.addList(imgLinks);
				pagerAdapter.notifyDataSetChanged();
				
				//���ݼ���������õ�ǰҳ��Ϊ1����������ҳ��
				pagerText1.setText("1");
				pagerText2.setText(totalPagers + "");
			}
		}
	}

	//���ذ�ť��xml�ж���
	public void back(View view) {
		// TODO Auto-generated method stub
		finish();
	}
	
	//����鿴��Ʒ����
	public void proDetailClick(View view){
		Intent intent = new Intent(this, ProductDetailsFragmentActivity.class);
		intent.putExtra("currentTab", 0); //��ǰtabֵ
		intent.putExtra("pid", pid); //��ƷID
		intent.putExtra("proTitle", productTitleStr);
		intent.putExtra("proDetail", productDetailStr); //��Ʒ����ҳ
		startActivity(intent);
	}
	
	//����鿴��Ʒ����
	public void proCommentsClick(View view){
		Intent intent = new Intent(this, ProductDetailsFragmentActivity.class);
		intent.putExtra("currentTab", 1); //��ǰtabֵ
		intent.putExtra("pid", pid); //��ƷID
		intent.putExtra("proTitle", productTitleStr);
		startActivity(intent);
		
	}
	
	//����鿴��Ʒ�ߴ�Ͳ���
	public void proSizesClick(View view){
		Toast.makeText(this, "sizes", Toast.LENGTH_SHORT).show();
		
	}

	//ȡ��������̣�����ҳ��ʱ������Ч
	private void setSoftInputModeHidden(){
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}
	
	//ȡ��������̣����������ťʱ������Ч
	private void hideSoftInput(){
		
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if(imm != null){
//			imm.hideSoftInputFromInputMethod(getWindow().getDecorView().getWindowToken(), 0); //��������
			imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0); //��Ч���������������һ��
//			imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
		}
	}
}
