package com.legendleo.yeeshop;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

import com.legendleo.yeekoor.R;
import com.legendleo.yeeshop.adapter.ProductListAdapter;
import com.legendleo.yeeshop.util.NetUtil;
import com.yeekoor.bean.CommonException;
import com.yeekoor.bean.ProductList;
import com.yeekoor.biz.ProductBiz;
import com.yeekoor.config.Constants;

public class ProductListActivity extends Activity implements OnClickListener {
	private SearchView searchView;
	private ListView productListView;
	//加载更多
	private View loadMoreView;
	private ProgressBar loadMoreProgressBar;
	private TextView loadMoreText;
	//最后可见条目索引
	private int lastVisibleIndex;
	
	private TextView newText, priceText, salesText, commentsText, emptyText;
	private LinearLayout priceLinearLayout;
	//升序降序标志
	private boolean priceToggleFlag = true;
	
	private ProductBiz biz;
	private List<ProductList> productList;
	
	private ProductListAdapter mAdapter;
	private ProgressBar mProgressBar;
	//private ArrayAdapter<String> arrAdapter;
	//private List<String> arrList;
	
	//传递过来的cateid
	private int cateid;
	//默认页为1
	private int currentPage = 1;
	//默认排序：新品
	private int sortType = Constants.SORT_TYPE_NEW;
	//默认搜索关键字为空
	private String searchKey = "";
	
	private static final int LOAD_REFRESH = 1;
	private static final int LOAD_MORE = 2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.product_list_activity);
		
		Intent intent = getIntent();
		cateid = intent.getIntExtra("cateid", 0);
		//来自其它fragment的搜索
		searchKey = intent.getStringExtra("searchKey");

		System.out.println("ProductListActivity------------>onCreate start cateid:" + cateid);
		init();
		
		biz = new ProductBiz();
		mAdapter = new ProductListAdapter(this);
		
		//arrList = new ArrayList<String>();
		//arrAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrList);
		productListView.addFooterView(loadMoreView);
		productListView.setAdapter(mAdapter);
		System.out.println("ProductListActivity------------>onCreate setAdapter end ");
		
		searchView.setSubmitButtonEnabled(true);
		
		//修改searchView提示语的颜色，由于没有对应属性可设置，通过这种方式（dirty hacks?）
		int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
		TextView tv = (TextView) findViewById(id);
		tv.setHintTextColor(Color.LTGRAY); //设置提示文本颜色
		tv.setTextColor(Color.WHITE); //设置输入文本颜色
		searchView.setOnQueryTextListener(new OnQueryTextListener() {
			
			@Override
			public boolean onQueryTextSubmit(String query) {
				// TODO Auto-generated method stub
				searchKey = query;
				cateid = 0; //针对所有一级分类搜索
				
				hideSoftInput(); //没起作用，键盘没有隐藏

				//设置进度条可见
				mProgressBar.setVisibility(View.VISIBLE);
				//获取网络数据
				new GetProductListDataTask().execute(LOAD_REFRESH);
				return true;
			}
			
			@Override
			public boolean onQueryTextChange(String newText) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		
		//listview的滚动监听
		productListView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// 滑到底部后自动加载
				System.out.println("onScrollStateChanged ----------------->>>>mAdapter.getCount():" + mAdapter.getCount());
				System.out.println("onScrollStateChanged ----------------->>>>scrollState:" + scrollState);
				if(scrollState == OnScrollListener.SCROLL_STATE_IDLE && lastVisibleIndex == mAdapter.getCount()){
					//设置加载更多View可见（如果只设置loadMoreView属性为gone而没有设置progressBar和text的话，页面空间还是会被占据）
					loadMoreProgressBar.setVisibility(View.VISIBLE);
					loadMoreText.setVisibility(View.VISIBLE);
					loadMoreData();
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// 最后可见条目的索引
				lastVisibleIndex = firstVisibleItem + visibleItemCount - 1;
				System.out.println("onScroll ----------------->>>>lastVisibleIndex:" + lastVisibleIndex);
			}
		});
		
		productListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//获取产品的pid
				int pid = productList.get(position).getPid();
				
				Intent pidIntent = new Intent(ProductListActivity.this, ProductInfoActivity.class);
				pidIntent.putExtra("pid", pid);
				startActivity(pidIntent);
			}
		});
		
		System.out.println("ProductListActivity------------>onCreate end");
		setSoftInputModeHidden();
		//默认项为新品
		setTabSelection(0);
	}
	
	
	private void init(){
		searchView = (SearchView) findViewById(R.id.searchList);
		productListView = (ListView) findViewById(R.id.productList);
		emptyText = (TextView) findViewById(R.id.emptyText);
		mProgressBar = (ProgressBar) findViewById(R.id.productListPBar);
		
		newText = (TextView) findViewById(R.id.newText);
		priceText = (TextView) findViewById(R.id.priceText);
		priceLinearLayout = (LinearLayout) findViewById(R.id.priceLinearLayout);
		salesText = (TextView) findViewById(R.id.salesText);
		commentsText = (TextView) findViewById(R.id.commentsText);
		
		loadMoreView = getLayoutInflater().inflate(R.layout.product_listview_moredata, productListView, false);
		loadMoreProgressBar = (ProgressBar) loadMoreView.findViewById(R.id.loadMoreProgressBar);
		loadMoreText = (TextView) loadMoreView.findViewById(R.id.loadMoreText);
		
		newText.setOnClickListener(this);
		priceLinearLayout.setOnClickListener(this);
		salesText.setOnClickListener(this);
		commentsText.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.newText:
			sortType = Constants.SORT_TYPE_NEW;
			setTabSelection(0);
			break;
			
		case R.id.priceLinearLayout:
			if(priceToggleFlag){
				//价格升序
				sortType = Constants.SORT_TYPE_PRICE_ASC;
				//显示升序图标
				Drawable priceSortImg = getResources().getDrawable(R.drawable.sort_price_up);
				priceSortImg.setBounds(0, 0, priceSortImg.getIntrinsicWidth(), priceSortImg.getIntrinsicHeight());
				priceText.setCompoundDrawables(null, null, priceSortImg, null);
			}else{
				//价格降序
				sortType = Constants.SORT_TYPE_PRICE_DEC;
				//显示降序图标
				Drawable priceSortImg = getResources().getDrawable(R.drawable.sort_price_down);
				priceSortImg.setBounds(0, 0, priceSortImg.getIntrinsicWidth(), priceSortImg.getIntrinsicHeight());
				priceText.setCompoundDrawables(null, null, priceSortImg, null);
			}
			//反转flag值
			priceToggleFlag = !priceToggleFlag;
			
			setTabSelection(1);
			break;
			
		case R.id.salesText:
			sortType = Constants.SORT_TYPE_SALES;
			setTabSelection(2);
			break;
			
		case R.id.commentsText:
			sortType = Constants.SORT_TYPE_COMMENTS;
			setTabSelection(3);
			break;

		default:
			break;
		}
	}
	
	
	private void setTabSelection(int index){
		clearSelections();
		switch (index) {
		case 0:
			newText.setTextColor(Color.RED);
			break;
			
		case 1:
			priceText.setTextColor(Color.RED);
			break;
			
		case 2:
			salesText.setTextColor(Color.RED);
			break;
			
		case 3:
			commentsText.setTextColor(Color.RED);
			break;
		}
		//设置进度条可见
		mProgressBar.setVisibility(View.VISIBLE);
		//获取网络数据
		new GetProductListDataTask().execute(LOAD_REFRESH);
	}
	
	//清除所有选中的颜色
	private void clearSelections() {
		newText.setTextColor(Color.BLACK);
		priceText.setTextColor(Color.BLACK);
		salesText.setTextColor(Color.BLACK);
		commentsText.setTextColor(Color.BLACK);
	}

	
	//异步获取网络数据
	class GetProductListDataTask extends AsyncTask<Integer, Void, Integer>{
		
		@Override
		protected Integer doInBackground(Integer... params) {
			try {
				if(NetUtil.CheckNet(ProductListActivity.this)){
					System.out.println("cateid/currentPage/sortType/searchKey:" + cateid + "/" + currentPage + "/" + sortType + "/" + searchKey);
					productList = biz.getProList(cateid, currentPage, sortType, searchKey);
				}
				
			} catch (CommonException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			if(sorts == 1){
//				arrList.clear();
//				for (int i =10; i < 20; i++) {
//					arrList.add(i + "");
//				}
//			}else if(sorts == 3){
//				arrList.clear();
//				for (int i =20; i < 30; i++) {
//					arrList.add(i + "");
//				}
//			}
			
			return params[0];
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			mProgressBar.setVisibility(View.GONE);
			loadMoreProgressBar.setVisibility(View.GONE);
			loadMoreText.setVisibility(View.GONE);
			
			if(NetUtil.CheckNet(ProductListActivity.this)){
				
				if(result == LOAD_REFRESH){
					//判断搜索结果是否为空
					if(productList == null){
						//设置listview为空时显示emptyText提示
						productListView.setEmptyView(emptyText);
						
						//productList = null时，不能add到mAdapter中，否则会报空指针异常
						//此时使用new赋一个空值，再调用notifyDataSetChanged，listview会刷新为空，此时setEmptyView方法会起作用
						productList = new ArrayList<ProductList>();
					}
					//先清除后添加
					mAdapter.setList(productList);
					mAdapter.notifyDataSetChanged();
				}else if(result == LOAD_MORE){
					//没有更多数据时则不加载
					if(productList != null){
						//直接添加
						mAdapter.addList(productList);
						mAdapter.notifyDataSetChanged();
					}else{
						currentPage = 1; // 调用loadMoreData方法后currentPage会加1,故此处需将当前页数重置为1，搜索和排序时会用到
						Toast.makeText(ProductListActivity.this, "没有更多产品啦！", Toast.LENGTH_SHORT).show();
					}
				}
				
			}else{
				Toast.makeText(ProductListActivity.this, "没有网络连接！", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	//加载更多数据
	private void loadMoreData(){
		currentPage += 1; //取下一页数据
		
		System.out.println("loadMoreData------------------------------->>>>>>>>currentPage:" + currentPage);
		new GetProductListDataTask().execute(LOAD_MORE);
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
//			imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0); //有效，与下文语句作用一样
			imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
		}
	}
	
	//返回按钮
	public void back(View view){
		finish();
	}
}
