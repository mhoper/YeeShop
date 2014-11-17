package com.legendleo.yeeshop.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.legendleo.yeekoor.R;
import com.yeekoor.bean.ProCategoryFirst;

public class CategoryAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	
	private List<ProCategoryFirst> lists = new ArrayList<ProCategoryFirst>();
	private Context mContext;
	
	//����һ������gravity
	private int textGravity;
	//�������TextView�Ƿ�ɼ�
	private int text2Visibility;
	//����ķ���һ
	private int clickPosition = -1;
	//����һ��LayoutParams
	LinearLayout.LayoutParams lp;
	Bitmap bp;
	
	public CategoryAdapter(Context context){
		mInflater = LayoutInflater.from(context);
		mContext = context;
		textGravity = Gravity.LEFT;
		text2Visibility = View.VISIBLE;
		
		lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		//�����ͷͼƬ�Ŀ��
		bp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.arrow_category);
		System.out.println("CategoryAdapter Context----------------------------->:");
	}
	
	public void addList(List<ProCategoryFirst> listsFirst){
		lists.addAll(listsFirst);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return lists.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return lists.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		System.out.println("Adapter listsSize----------------------------->:" + lists.size());
		ProCategoryFirst listFirst =lists.get(position);
		ViewHolder holder;
		
		if(null == convertView){
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.category_adapter, parent, false);
			holder.mTextView1 = (TextView) convertView.findViewById(R.id.category_text);
			holder.mTextView2 = (TextView) convertView.findViewById(R.id.category_text2);
			holder.arrowImageView = (ImageView) convertView.findViewById(R.id.arrow_image);
			convertView.setTag(holder);
			
		}else{
			
			holder = (ViewHolder) convertView.getTag();
		}
		
		if(listFirst != null){
			System.out.println("Adapter getProCate1----------------------------->:" + listFirst.getProCate1());
			System.out.println("Adapter getProCate1----------------------------->:" + listFirst.getProCate2());
			holder.mTextView1.setText(listFirst.getProCate1());
			//��������һ����ķ���
			holder.mTextView1.setGravity(textGravity);
			
			holder.mTextView2.setText(listFirst.getProCate2());
			//�������������ʾ����������
			holder.mTextView2.setVisibility(text2Visibility);
			
			//�Ҳ�������ʱ
			if(clickPosition == -1){
				holder.arrowImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.arrow_right));
				lp.setMargins(10, 0, 10, 0);
			}else{
				if(position == clickPosition){
					//convertView.setBackgroundColor(Color.parseColor("#F1F0EE"));
					holder.arrowImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.arrow_category));
					lp.setMargins(10, 16, 0, 16);
					
				}else{
					holder.arrowImageView.setImageDrawable(null);
					//������null����Ҫ��ȫԭͼƬ�Ŀ��
					lp.setMargins(10, bp.getHeight()/2 + 16, bp.getWidth(), bp.getHeight()/2 + 16);
				}
			}
			holder.arrowImageView.setLayoutParams(lp);
		}
		
		
		return convertView;
	}
	
	private final class ViewHolder{
		TextView mTextView1;
		TextView mTextView2;
		ImageView arrowImageView;
	}
	
	public void setTextGravity(int gravity){
		this.textGravity = gravity;
	}
	
	public int getTextGravity() {
		return textGravity;
	}

	public void setText2Visibility(int text2Visibility) {
		this.text2Visibility = text2Visibility;
	}
	
	public void setClickPosition(int clickPosition) {
		this.clickPosition = clickPosition;
	}
}
