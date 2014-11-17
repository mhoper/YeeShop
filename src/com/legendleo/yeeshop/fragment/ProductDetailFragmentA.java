package com.legendleo.yeeshop.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.legendleo.yeekoor.R;
import com.yeekoor.bean.CommonException;
import com.yeekoor.bean.ProductInfo;
import com.yeekoor.biz.ProductBiz;

public class ProductDetailFragmentA extends Fragment {
	//webView
	private WebView webView;
	private int pid;
	private String proDetail;
	private Handler handler;
//	private ProgressDialog dialog;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.product_detail_fragment_a, container, false);
		

		webView = (WebView) view.findViewById(R.id.webView);

		//web���ؽ�������
		if(webView != null){
//			webView.setWebViewClient(new WebViewClient(){
//				@Override
//				public void onPageFinished(WebView view, String url) {
//					// TODO Auto-generated method stub
//					dialog.dismiss();
//				}
//			});
			
			WebSettings webs = webView.getSettings();
//			webs.setJavaScriptEnabled(true);
			webs.setUseWideViewPort(true); //����ԭʼ�ߴ�
			webs.setLoadWithOverviewMode(true);//����Ӧ��Ļ���
			webs.setBuiltInZoomControls(true); //�ɻ�������
			webs.setDisplayZoomControls(false);//�������Ű�ť
			//webs.setCacheMode(WebSettings.LOAD_DEFAULT); //���û���
			
			//������Ϣ����loadData
			handler = new Handler(){
				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					if(msg.what == 0x1){
						//����loadData
						webView.loadData(proDetail, "text/html", "UTF-8");
					}
				}
			};
			
			webView.loadData(proDetail, "text/html", "UTF-8");
			
			//dialog = ProgressDialog.show(getActivity(), null, "ҳ������У����Ժ�");
			//webView.reload();
		}
		
		System.out.println("fragment onCreateView----------------->>>>>>>>>>>A");
		return view;
	}
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		
		System.out.println("fragment setUserVisibleHint----------------->>>>>>>>>>>A load data");
		if(proDetail == null){
			
			Bundle bundle = getArguments();
			pid = bundle.getInt("pid", 0);
			proDetail = bundle.getString("proDetail");

			//������ݹ���������Ϊ������������
			if(proDetail == null){
				System.out.println("fragment setUserVisibleHint----------------->>>>>>>>>>>A proDetail= null");
				proDetail = ""; //�ȸ���ֵ������loadUrlʱҳ����ʾnull����
				//�첽��ȡ����
				new GetDataThread().start();
			}
		}
	}
	
	class GetDataThread extends Thread {
		@Override
		public void run() {

			try {
				ProductInfo productInfo = new ProductBiz().getProInfo(pid);
				proDetail = productInfo.getProDetails();
				//���ݻ�ȡ�������Ϣ��handler
				handler.sendEmptyMessage(0x1);
			} catch (CommonException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		System.out.println("fragment onDestroyView----------------->>>>>>>>>>>A");
	}
}
