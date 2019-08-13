package com.dabai.markdownq.utils;

import android.content.*;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.*;
import okhttp3.*;

import org.json.*;
import java.util.*;

/**
 *图片上传类
 *created by zs-river
 */

public class SmmsPostModel
{

	private Context context;

	private File updatepic;

	private boolean isssl;

	private Map<String, String> header;
	
	public SmmsPostModel(Context context){
		this.context=context;
	}
	
	public void setfile(File pic){
		this.updatepic=pic;
	}
	
	public void setssl(boolean usessl){
		this.isssl=usessl;
	}
	
	public void post(final OnsmmsUpload request){
		
		header=new HashMap<String, String>();
		header.put("User-Agent",SmmsUrl.postua);
		
		OkHttpUtils.post()
		.url(SmmsUrl.posturl)
		.headers(header)
		.addFile("smfile",updatepic.getName(),updatepic)
		.addParams("ssl",isssl?"true":"false")
		.build()
			.execute(new StringCallback(){

				@Override
				public void onError(Call p1, Exception p2, int p3)
				{
					// TODO: Implement this method
					request.onerror(p2,p3,"网络错误，请检查网络");
				}

				@Override
				public void onResponse(String p1, int p2)
				{
					// TODO: Implement this method
					try
					{
						SmmsRequestModel srm=new SmmsRequestModel(p1);
						if(srm.issuccess()){
							request.onsuccess(srm);
						}
						else{
							request.onerror(null,2001,ErrortranslateModel.replace(srm.geterrormsg()));
						}
					}
					catch (JSONException e)
					{
						request.onerror(e,1001,"json解析错误");
					}
				}
				
			
		});
	}
	

}
