package com.legendleo.yeeshop.view;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.legendleo.yeekoor.R;
import com.legendleo.yeeshop.adapter.CategoryAdapter;
import com.legendleo.yeeshop.fragment.CategoryFragment;

public class CategorySlidingLayout extends LinearLayout implements OnTouchListener {

	public static final String TAG = "slidingTag";
	//屏幕宽度
	private int screenWidth;
	
	//左右View
	private View leftLayout;
	private View rightLayout;
	private View mBindView;
	
	//左右View参数
	private MarginLayoutParams leftLayoutParams;
	private MarginLayoutParams rightLayoutParams;
	
	//边界距离
	private int rightLayoutPadding;
	//左布局左边界的x轴坐标范围
	private int minEdge;
	private int maxEdge = 0;
	
	//手指滑动的速度
	private VelocityTracker mVelocityTracker;
	
	//手指按下、移动、抬起时的相对屏幕的x坐标
	private float xDown;
	private float xMove;
	private float xUp;

	//手指按下、移动、抬起时的相对屏幕的y坐标
	private float yDown;
	private float yMove;
	
	//右侧布局是否显示，只有完全显示或隐藏时才改变此值，滑动过程中忽略
	private boolean isRightLayoutVisible;
	

	//是否正在滑动
	private boolean isSliding;
	
	//在被判定为滚动之前用户手指可以移动的最大值
	private int touchSlop;
	
	//手指滑动速度
	public static final int SNAP_VELOCITY = 200;
	
	//布局滚动速度
	public static final int SCROLL_VELOCITY = 30;

	private ListView leftListView;
	private CategoryAdapter leftListAdapter;
	
	public CategorySlidingLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 获取屏幕
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		Display display = wm.getDefaultDisplay();
		display.getMetrics(dm);
		screenWidth = dm.widthPixels;
		rightLayoutPadding = screenWidth / 3;
		touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		System.out.println("onLayout----------------------------------->>>>> start");
		if(changed){
			System.out.println("onLayout----------------------------------->>>>> changed start");
			//获取左侧布局
			leftLayout = getChildAt(0);
			leftLayoutParams = (MarginLayoutParams) leftLayout.getLayoutParams();
			leftLayoutParams.width = screenWidth;
			leftLayout.setLayoutParams(leftLayoutParams);
			
			//获取右侧布局
			rightLayout = getChildAt(1);
			rightLayoutParams = (MarginLayoutParams) rightLayout.getLayoutParams();
			rightLayoutParams.width = screenWidth - rightLayoutPadding;
			minEdge = -rightLayoutParams.width;
			rightLayout.setLayoutParams(rightLayoutParams);
			System.out.println("onLayout----------------------------------->>>>> changed end");
			
			leftListAdapter = CategoryFragment.getCategoryFragment().getLeftListAdapter();
			leftListView = (ListView) findViewById(R.id.leftList);
		}
		super.onLayout(changed, l, t, r, b);

		System.out.println("onLayout----------------------------------->>>>> end");
	}
	

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		Log.e(TAG, "Sliding onTouch --> start");
		createVelocityTracker(event);
		//由于xml中先隐藏了rightLayout
		if(rightLayout.getVisibility() != View.VISIBLE){
			rightLayout.setVisibility(View.VISIBLE);
		}
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			xDown = event.getRawX(); //相对屏幕的坐标
			yDown = event.getRawY();
			break;

		case MotionEvent.ACTION_MOVE:
			xMove = event.getRawX();
			yMove = event.getRawY();
			int distanceX = (int) (xMove - xDown);
			int distanceY = (int) (yMove - yDown);
			
			if(isRightLayoutVisible && distanceX >= touchSlop && (isSliding || Math.abs(distanceY) <= touchSlop )){ //右布局显示状态，此时只能向右滑动隐藏
				isSliding = true;
				leftLayoutParams.leftMargin = minEdge + distanceX;
				
				if(leftLayoutParams.leftMargin > maxEdge ){
					leftLayoutParams.leftMargin = maxEdge;
				}
				leftLayout.setLayoutParams(leftLayoutParams);
			}
			
//			if(!isRightLayoutVisible && -distanceX >= touchSlop && (isSliding || Math.abs(distanceY) <= touchSlop)){ //右布局隐藏状态，此时只能向左滑动显示
//				isSliding = true;
//				leftLayoutParams.leftMargin = distanceX;
//				
//				if(leftLayoutParams.leftMargin < minEdge){
//					leftLayoutParams.leftMargin = minEdge;
//				}
//				leftLayout.setLayoutParams(leftLayoutParams);
//			}
			break;
			
		case MotionEvent.ACTION_UP:
			xUp = event.getRawX();
			int upDistanceX = (int) (xUp - xDown);
			if(isSliding){
				if(wantToShowLeftLayout()){
					if(shouldShowLeftLayout()){
						scrollToLeftLayout();
					}else{
						scrollToRightLayout();
					}
				}
				//此外不需要手势滑动
//				else if(wantToShowRightLayout()){
//					if(shouldShowRightLayout()){
//						scrollToRightLayout();
//					}else{
//						scrollToLeftLayout();
//					}
//				}
			}else if(upDistanceX < touchSlop && isRightLayoutVisible){
				//不需要点击隐藏功能
				//scrollToLeftLayout();
			}
			recycleVelocityTracker();
			break;
		}

		unFocusBindView();
		if(v.isEnabled()){
			if(isSliding){
				return true;
			}
			if(isRightLayoutVisible){
				return false;
			}
			return false;
		}
		//v.performClick();
		return true;
	}

	private void unFocusBindView() {
		if(mBindView != null){
			mBindView.setPressed(false);
			mBindView.setFocusable(false);
			mBindView.setFocusableInTouchMode(false);
			mBindView.setSelected(false);
		}
	}
	
	public void setScrollEvent(View bindView){
		mBindView = bindView;
		mBindView.setOnTouchListener(this);
	}

	private boolean wantToShowLeftLayout() {
		return isRightLayoutVisible && xUp - xDown > 0;
	}

	private boolean wantToShowRightLayout() {
		return !isRightLayoutVisible && xUp - xDown < 0;
	}
	
	private boolean shouldShowLeftLayout() {
		return xUp - xDown > screenWidth / 2 || getScrollVelocity() > SNAP_VELOCITY;
	}

	private boolean shouldShowRightLayout() {
		return xDown - xUp > screenWidth / 2 || getScrollVelocity() > SNAP_VELOCITY ;
	}

	private void scrollToLeftLayout() { 
		//向右滑后一级分类其文字内容居左显示，二级分类文字显示
		if(leftListAdapter.getTextGravity() == Gravity.RIGHT){
			leftListAdapter.setTextGravity(Gravity.LEFT);
			leftListAdapter.setText2Visibility(View.VISIBLE);
			leftListAdapter.setClickPosition(-1);
			leftListView.setAdapter(leftListAdapter);
		}
		new ScrollTask().execute(SCROLL_VELOCITY);
	}

	public void scrollToRightLayout() { //向左滑
		new ScrollTask().execute(-SCROLL_VELOCITY);
	}
	
	private int getScrollVelocity() {
		mVelocityTracker.computeCurrentVelocity(1000);
		int velocity = (int) mVelocityTracker.getXVelocity();
		return Math.abs(velocity);
	}
	
	//将触摸事件加入到VelocityTracker中
	private void createVelocityTracker(MotionEvent event) {
		if(mVelocityTracker == null){
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);
	}
	
	//回收VelocityTracker
	private void recycleVelocityTracker(){
		if(mVelocityTracker != null){
			mVelocityTracker.recycle();
			mVelocityTracker = null;
		}
	}
	
	public boolean isRightLayoutVisible(){
		return isRightLayoutVisible;
	}

	class ScrollTask extends AsyncTask<Integer, Integer, Integer>{
		@Override
		protected Integer doInBackground(Integer... speed) {
			int leftMargin = leftLayoutParams.leftMargin;
			
			while(true){
				leftMargin += speed[0];
				if(leftMargin > maxEdge){
					leftMargin = maxEdge;
					break;
				}
				if(leftMargin < minEdge){
					leftMargin = minEdge;
					break;
				}
				publishProgress(leftMargin);
				sleep(20);
			}
			if(speed[0] > 0){
				isRightLayoutVisible = false;
			}else{
				isRightLayoutVisible = true;
			}
			isSliding = false;

			return leftMargin;
		}
		
		@Override
		protected void onProgressUpdate(Integer... leftMargin) {
			leftLayoutParams.leftMargin = leftMargin[0];
			leftLayout.setLayoutParams(leftLayoutParams);
			unFocusBindView();
		}
		
		@Override
		protected void onPostExecute(Integer leftMargin) {
			leftLayoutParams.leftMargin = leftMargin;
			leftLayout.setLayoutParams(leftLayoutParams);
		}
		
	}
	
	private void sleep(long millis){
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public View getRightLayout(){
		return rightLayout;
	}
}
