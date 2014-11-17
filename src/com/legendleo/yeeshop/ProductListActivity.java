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
	//���ظ���
	private View loadMoreView;
	private ProgressBar loadMoreProgressBar;
	private TextView loadMoreText;
	//���ɼ���Ŀ����
	private int lastVisibleIndex;
	
	private TextView newText, priceText, salesText, commentsText, emptyText;
	private LinearLayout priceLinearLayout;
	//�������־
	private boolean priceToggleFlag = true;
	
	private ProductBiz biz;
	private List<ProductList> productList;
	
	private ProductListAdapter mAdapter;
	private ProgressBar mProgressBar;
	//private ArrayAdapter<String> arrAdapter;
	//private List<String> arrList;
	
	//���ݹ�����cateid
	private int cateid;
	//Ĭ��ҳΪ1
	private int currentPage = 1;
	//Ĭ��������Ʒ
	private int sortType = Constants.SORT_TYPE_NEW;
	//Ĭ�������ؼ���Ϊ��
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
		//��������fragment������
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
		
		//�޸�searchView��ʾ�����ɫ������û�ж�Ӧ���Կ����ã�ͨ�����ַ�ʽ��dirty hacks?��
		int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
		TextView tv = (TextView) findViewById(id);
		tv.setHintTextColor(Color.LTGRAY); //������ʾ�ı���ɫ
		tv.setTextColor(Color.WHITE); //���������ı���ɫ
		searchView.setOnQueryTextListener(new OnQueryTextListener() {
			
			@Override
			public boolean onQueryTextSubmit(String query) {
				// TODO Auto-generated method stub
				searchKey = query;
				cateid = 0; //�������һ����������
				
				hideSoftInput(); //û�����ã�����û������

				//���ý������ɼ�
				mProgressBar.setVisibility(View.VISIBLE);
				//��ȡ��������
				new GetProductListDataTask().execute(LOAD_REFRESH);
				return true;
			}
			
			@Override
			public boolean onQueryTextChange(String newText) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		
		//listview�Ĺ�������
		productListView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// �����ײ����Զ�����
				System.out.println("onScrollStateChanged ----------------->>>>mAdapter.getCount():" + mAdapter.getCount());
				System.out.println("onScrollStateChanged ----------------->>>>scrollState:" + scrollState);
				if(scrollState == OnScrollListener.SCROLL_STATE_IDLE && lastVisibleIndex == mAdapter.getCount()){
					//���ü��ظ���View�ɼ������ֻ����loadMoreView����Ϊgone��û������progressBar��text�Ļ���ҳ��ռ仹�ǻᱻռ�ݣ�
					loadMoreProgressBar.setVisibility(View.VISIBLE);
					loadMoreText.setVisibility(View.VISIBLE);
					loadMoreData();
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// ���ɼ���Ŀ������
				lastVisibleIndex = firstVisibleItem + visibleItemCount - 1;
				System.out.println("onScroll ----------------->>>>lastVisibleIndex:" + lastVisibleIndex);
			}
		});
		
		productListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//��ȡ��Ʒ��pid
				int pid = productList.get(position).getPid();
				
				Intent pidIntent = new Intent(ProductListActivity.this, ProductInfoActivity.class);
				pidIntent.putExtra("pid", pid);
				startActivity(pidIntent);
			}
		});
		
		System.out.println("ProductListActivity------------>onCreate end");
		setSoftInputModeHidden();
		//Ĭ����Ϊ��Ʒ
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
				//�۸�����
				sortType = Constants.SORT_TYPE_PRICE_ASC;
				//��ʾ����ͼ��
				Drawable priceSortImg = getResources().getDrawable(R.drawable.sort_price_up);
				priceSortImg.setBounds(0, 0, priceSortImg.getIntrinsicWidth(), priceSortImg.getIntrinsicHeight());
				priceText.setCompoundDrawables(null, null, priceSortImg, null);
			}else{
				//�۸���
				sortType = Constants.SORT_TYPE_PRICE_DEC;
				//��ʾ����ͼ��
				Drawable priceSortImg = getResources().getDrawable(R.drawable.sort_price_down);
				priceSortImg.setBounds(0, 0, priceSortImg.getIntrinsicWidth(), priceSortImg.getIntrinsicHeight());
				priceText.setCompoundDrawables(null, null, priceSortImg, null);
			}
			//��תflagֵ
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
		//���ý������ɼ�
		mProgressBar.setVisibility(View.VISIBLE);
		//��ȡ��������
		new GetProductListDataTask().execute(LOAD_REFRESH);
	}
	
	//�������ѡ�е���ɫ
	private void clearSelections() {
		newText.setTextColor(Color.BLACK);
		priceText.setTextColor(Color.BLACK);
		salesText.setTextColor(Color.BLACK);
		commentsText.setTextColor(Color.BLACK);
	}

	
	//�첽��ȡ��������
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
					//�ж���������Ƿ�Ϊ��
					if(productList == null){
						//����listviewΪ��ʱ��ʾemptyText��ʾ
						productListView.setEmptyView(emptyText);
						
						//productList = nullʱ������add��mAdapter�У�����ᱨ��ָ���쳣
						//��ʱʹ��new��һ����ֵ���ٵ���notifyDataSetChanged��listview��ˢ��Ϊ�գ���ʱsetEmptyView������������
						productList = new ArrayList<ProductList>();
					}
					//����������
					mAdapter.setList(productList);
					mAdapter.notifyDataSetChanged();
				}else if(result == LOAD_MORE){
					//û�и�������ʱ�򲻼���
					if(productList != null){
						//ֱ�����
						mAdapter.addList(productList);
						mAdapter.notifyDataSetChanged();
					}else{
						currentPage = 1; // ����loadMoreData������currentPage���1,�ʴ˴��轫��ǰҳ������Ϊ1������������ʱ���õ�
						Toast.makeText(ProductListActivity.this, "û�и����Ʒ����", Toast.LENGTH_SHORT).show();
					}
				}
				
			}else{
				Toast.makeText(ProductListActivity.this, "û���������ӣ�", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	//���ظ�������
	private void loadMoreData(){
		currentPage += 1; //ȡ��һҳ����
		
		System.out.println("loadMoreData------------------------------->>>>>>>>currentPage:" + currentPage);
		new GetProductListDataTask().execute(LOAD_MORE);
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
//			imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0); //��Ч���������������һ��
			imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
		}
	}
	
	//���ذ�ť
	public void back(View view){
		finish();
	}
}
