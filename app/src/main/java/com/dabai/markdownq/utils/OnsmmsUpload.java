package com.dabai.markdownq.utils;


public abstract class OnsmmsUpload
{
	public abstract void onsuccess(SmmsRequestModel request);
	
	public abstract void onerror(Exception e,int code,String str);
	
	//public abstract void ondebug(String s);
}
