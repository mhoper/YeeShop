package com.legendleo.yeeshop.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.legendleo.yeekoor.R;
import com.legendleo.yeeshop.ProductListActivity;
import com.legendleo.yeeshop.adapter.CategoryAdapter;
import com.legendleo.yeeshop.util.NetUtil;
import com.legendleo.yeeshop.view.CategorySlidingLayout;
import com.yeekoor.bean.CommonException;
import com.yeekoor.bean.ProCategoryFirst;
import com.yeekoor.bean.ProCategorySecond;
import com.yeekoor.biz.ProCategoryBiz;
import com.yeekoor.config.Constants;

public class CategoryFragment extends Fragment {

	private CategorySlidingLayout slidingLayout;
	private ListView leftListView, rightListView;
	
	private CategoryAdapter leftListAdapter;
	private ArrayAdapter<String> rightListAdapter;

	private ProCategoryBiz biz;
	private List<Map<String, Object>> listMap;
	private List<ProCategoryFirst> listsFirst;
	private List<ProCategorySecond> listsSecond;
	private List<String> listStr;
	private List<Integer> listCateid;
	
	private ProgressBar progressBar;
	private long startTime;
	
	private static CategoryFragment categoryFragment = null; 
	
	public CategoryFragment(){
		categoryFragment = this;
	}

	public static CategoryFragment getCategoryFragment(){
		return categoryFragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		System.out.println("onCreateView----------------------------------->>>>> start");

		View categoryView = inflater.inflate(R.layout.category_fragment,
				container, false);

		slidingLayout = (CategorySlidingLayout) categoryView
				.findViewById(R.id.slidingLayout);
		leftListView = (ListView) categoryView.findViewById(R.id.leftList);
		rightListView = (ListView) categoryView.findViewById(R.id.rightList);
		progressBar = (ProgressBar) categoryView.findViewById(R.id.progressBar);

		biz = new ProCategoryBiz();
		listStr = new ArrayList<String>();
		listCateid = new ArrayList<Integer>();
		
		leftListAdapter = new CategoryAdapter(getActivity());
		rightListAdapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, listStr);

		leftListView.setAdapter(leftListAdapter);
		rightListView.setAdapter(rightListAdapter);

		slidingLayout.setScrollEvent(leftListView);

		//点击leftListView的item
		leftListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				int cateid1 = listsFirst.get(position).getCateid();
				
				//二级分类根据一级分类显示
				listStr.clear();
				listCateid.clear();
				for (int i = 0; i < listsSecond.size(); i++) {
					if(listsSecond.get(i).getCateid1() == cateid1){
						listStr.add(listsSecond.get(i).getProCate2());
						listCateid.add(listsSecond.get(i).getCateid2());
					}
				}
				rightListAdapter.notifyDataSetChanged();
				
				if (slidingLayout.isRightLayoutVisible()) {
					//改变点击行背景色
					leftListAdapter.setClickPosition(position);
					leftListView.setAdapter(leftListAdapter);
					
				} else {
					
					if (slidingLayout.getRightLayout().getVisibility() != View.VISIBLE) {
						slidingLayout.getRightLayout().setVisibility(
								View.VISIBLE);
					}
					slidingLayout.scrollToRightLayout();
					
					//点击一级分类后，其文字内容居右显示，其二级分类暂时隐藏
					if(leftListAdapter.getTextGravity() == Gravity.LEFT){
						leftListAdapter.setTextGravity(Gravity.RIGHT);
						leftListAdapter.setText2Visibility(View.GONE);
						//改变点击行背景色
						leftListAdapter.setClickPosition(position);
						leftListView.setAdapter(leftListAdapter);
					}
					
				}
			}

		});
		
		//点击rightListView的item
		rightListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				int cateid = listCateid.get(position);
				//Toast.makeText(getActivity(), txt, Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(getActivity(), ProductListActivity.class);
				intent.putExtra("cateid", cateid);
				startActivity(intent);
			}
		});

		new GetCategoryDataTask().execute();

		System.out.println("onCreateView----------------------------------->>>>> end");
		return categoryView;
	}

	public CategoryAdapter getLeftListAdapter() {
		return leftListAdapter;
	}

	class GetCategoryDataTask extends AsyncTask<Void, Void, Void>{

		@SuppressWarnings("unchecked")
		@Override
		protected Void doInBackground(Void... params) {

			
			try {
				if(NetUtil.CheckNet(getActivity())){

					long startTime2 = System.currentTimeMillis();
					listMap = biz.getCategorys();
					System.out.println("doInBackground----------------------------->:getCategorys Cost Time:" + (System.currentTimeMillis() - startTime2));
					
					listsFirst = (List<ProCategoryFirst>) listMap.get(0).get(Constants.KEY_CATEGORY1);
					listsSecond = (List<ProCategorySecond>) listMap.get(1).get(Constants.KEY_CATEGORY2);
					
					System.out.println("listsFirstSize----------------------------->:start");
					//listsFirst = biz.getProCategory();
					System.out.println("listsFirstSize----------------------------->:" + listsFirst.size());

					System.out.println("listsSecondSize----------------------------->:start");
					//listsSecond = biz.getProCateSecond();
					
//					for (ProCategorySecond sec : listsSecond) {
//						listStr.add(sec.getProCate2());
//					}
					System.out.println("listStrSize----------------------------->:" + listStr.size());
				}
				
			} catch (CommonException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			progressBar.setVisibility(View.GONE);
			if(NetUtil.CheckNet(getActivity())){
				System.out.println("onPostExecute----------------------------->:start");
				if(listsFirst != null){
					System.out.println("onPostExecute----------------------------->:1111");
					leftListAdapter.addList(listsFirst);
					leftListAdapter.notifyDataSetChanged();
				}
				
				if(listsSecond != null){
					System.out.println("onPostExecute----------------------------->:2222");
					//rightListAdapter.addAll(listStr);
					rightListAdapter.notifyDataSetChanged();
				}
	
				System.out.println("onPostExecute----------------------------->:end Cost Time:" + (System.currentTimeMillis() - startTime));
			}else{
				Toast.makeText(getActivity(), "没有网络连接！", Toast.LENGTH_SHORT).show();
			}
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

			startTime = System.currentTimeMillis();
		}
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		System.out.println("onActivityCreated");
	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		System.out.println("onStart");
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		System.out.println("onResume");
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		System.out.println("onPause");
	}
	
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		System.out.println("onStop");
	}
	
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		System.out.println("onDestroyView");
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		System.out.println("onDestroy");
	}
}
